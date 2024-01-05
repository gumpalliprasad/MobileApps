/*
 * *
 *  * Created by SriRamaMurthy A on 26/9/19 3:50 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 26/9/19 3:50 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.MsgDetails;
import myschoolapp.com.gsnedutech.Models.MsgUserDetails;
import myschoolapp.com.gsnedutech.Models.ParentObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserMessages extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = UserMessages.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();


    @BindView(R.id.rv_msg_list)
    RecyclerView rvMsgList;
    @BindView(R.id.tv_no_messages)
    TextView tvNoMessages;

    RecyclerView rvAdminList;
    ChatAdapter chatAdapter;

    List<MsgDetails> listMessages = new ArrayList<>();

    SharedPreferences sh_Pref;
    ParentObj pObj;
    StudentObj sObj;

    TeacherObj teacherObj;

    String userId, branchId;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_messages);
        ButterKnife.bind(this);


        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, UserMessages.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getMessages();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }


    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            String json = sh_Pref.getString("teacherObj", "");
            teacherObj = gson.fromJson(json, TeacherObj.class);

            userId = "" + teacherObj.getUserId();
            branchId = "" + teacherObj.getBranchId();

        } else if (sh_Pref.getBoolean("parent_loggedin", false)) {
            String json = sh_Pref.getString("parentObj", "");
            pObj = gson.fromJson(json, ParentObj.class);

            json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);

            userId = "" + pObj.getUserId();
            branchId = "" + sObj.getBranchId();
        }


        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvMsgList.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMsgList.getContext(), manager.getOrientation());
        rvMsgList.addItemDecoration(dividerItemDecoration);

    }




    public void sendNewMessage(View view) {
        searchUser(branchId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void searchUser(String... strings){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Log.v(TAG, "URL - " + AppUrls.GetAdminForParentMessages +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + strings[0]);


        Request request = new Request.Builder()
                .url(AppUrls.GetAdminForParentMessages +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + strings[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();
                    utils.showLog(TAG, "response - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {


                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<MsgUserDetails>>() {
                            }.getType();

                            JSONArray jsonArray = ParentjObject.getJSONArray("adminArray");
                            List<MsgUserDetails> listUsers = new ArrayList<>();
                            listUsers.clear();
                            listUsers.addAll(gson.fromJson(jsonArray.toString(), type));
                            runOnUiThread(() -> {
                                if (listUsers.size()>0){
                                    dialog = new Dialog(UserMessages.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(true);
                                    dialog.setContentView(R.layout.dialog_new_message_parent);

                                    rvAdminList = dialog.findViewById(R.id.rv_admin_list);
                                    rvAdminList.setLayoutManager(new LinearLayoutManager(UserMessages.this));
                                    rvAdminList.setAdapter(new AdminAdapter(listUsers));
                                    dialog.show();
                                }

                                rvAdminList.setAdapter(new AdminAdapter(listUsers));

                            });


                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(UserMessages.this, "No user Found", Toast.LENGTH_SHORT).show();
                            });

                        }


                    } catch (JSONException e) {
                        utils.showLog(TAG, "error " + e.getMessage());
                    }
                }
            }
        });

    }

    class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {

        List<MsgUserDetails> listUserDetails;

        AdminAdapter(List<MsgUserDetails> listUserDetails) {
            this.listUserDetails = listUserDetails;
        }

        @NonNull
        @Override
        public AdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(UserMessages.this).inflate(R.layout.item_msg_admin, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AdminAdapter.ViewHolder holder, int position) {
            holder.tvNameNm.setText(listUserDetails.get(position).getUserName());
            String nm = listUserDetails.get(position).getUserName().charAt(0) + "";
            holder.tvProfileNm.setText(nm.toUpperCase());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserMessages.this, MessageChatActivity.class);
                    intent.putExtra("userName", listUserDetails.get(position).getUserName());
                    intent.putExtra("userId", listUserDetails.get(position).getUserId());
                    intent.putExtra("createdBy", "" + userId);
                    intent.putExtra("newMessage", "1");
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listUserDetails.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvNameNm, tvProfileNm;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvNameNm = itemView.findViewById(R.id.tv_name_nm);
                tvProfileNm = itemView.findViewById(R.id.tv_profile_nm);
            }
        }
    }

    void getMessages(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Log.v(TAG, "URL - " + AppUrls.GetAllMessages +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + branchId + "&userId=" + userId);


        Request request = new Request.Builder()
                .url(AppUrls.GetAllMessages +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + branchId + "&userId=" + userId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "response - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("Messages");
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<MsgDetails>>() {
                            }.getType();

                            listMessages.clear();
                            listMessages.addAll(gson.fromJson(jsonArray.toString(), type));

                            utils.showLog(TAG, listMessages.size() + "");

                            Collections.reverse(listMessages);
                            chatAdapter = new ChatAdapter(listMessages);
                            runOnUiThread(() -> rvMsgList.setAdapter(chatAdapter));

                        } else {
                            runOnUiThread(() -> tvNoMessages.setVisibility(View.VISIBLE));
                        }


                    } catch (JSONException e) {
                        utils.showLog(TAG, "error " + e.getMessage());
                    }

                    runOnUiThread(() -> {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (listMessages.size()>0){

                                    rvMsgList.setVisibility(View.VISIBLE);
                                    tvNoMessages.setVisibility(View.GONE);
                                }
                                else{
                                    rvMsgList.setVisibility(View.GONE);
                                    tvNoMessages.setVisibility(View.VISIBLE);

                                }


                            }
                        }, 2000);
                    });
                }
            }
        });
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        List<MsgDetails> listMsgDetails;
        List<MsgDetails> list_filtered;

        ChatAdapter(List<MsgDetails> listMsgDetails) {
            this.listMsgDetails = listMsgDetails;
            this.list_filtered = listMsgDetails;

        }

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(UserMessages.this).inflate(R.layout.item_chat, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {

            viewHolder.tvName.setText(list_filtered.get(i).getUserName());
            viewHolder.tvMessgae.setText(list_filtered.get(i).getMessage());
            String user = list_filtered.get(i).getUserName().charAt(0) + "";
//            viewHolder.tvProfileImage.setText(user.toUpperCase());
            String Date = list_filtered.get(i).getCreatedDate();
            String[] datetime = Date.split(" ");
            String[] time = datetime[1].split(":");
            String t = "";
            if (Integer.parseInt(time[0]) < 12) {
                t = time[0] + ":" + time[1] + "AM";
            } else if (Integer.parseInt(time[0]) > 12) {
                int x = Integer.parseInt(time[0]) - 12;
                t = x + ":" + time[1] + "PM";
            } else {
                t = time[0] + time[1] + "PM";
            }
            viewHolder.tvDate.setText(datetime[0] + "\n" + t);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserMessages.this, MessageChatActivity.class);
                    intent.putExtra("userName", list_filtered.get(i).getUserName());
                    intent.putExtra("threadId", list_filtered.get(i).getThreadId() + "");
                    intent.putExtra("userId", list_filtered.get(i).getUserId() + "");
                    intent.putExtra("createdBy", "" + userId);
                    intent.putExtra("newMessage", "0");
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName, tvDate, tvMessgae;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);
                tvDate = itemView.findViewById(R.id.tv_date);
//                tvProfileImage = itemView.findViewById(R.id.tv_profile);
                tvMessgae = itemView.findViewById(R.id.tv_message);
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


}
