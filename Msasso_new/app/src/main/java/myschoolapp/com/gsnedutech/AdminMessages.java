package myschoolapp.com.gsnedutech;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
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
import myschoolapp.com.gsnedutech.Fragments.AdminParentMessages;
import myschoolapp.com.gsnedutech.Fragments.AdminStaffMessages;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.MsgAdminUserDetails;
import myschoolapp.com.gsnedutech.Models.MsgDetails;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AdminMessages extends AppCompatActivity {
    private static final String TAG = AdminMessages.class.getName();
    MyUtils utils = new MyUtils();
    List<MsgDetails> listMessages = new ArrayList<>();

    SharedPreferences sh_Pref;
    AdminObj adminObj;
    @BindView(R.id.messages_tablayout)
    TabLayout messagesTabLayout;
    @BindView(R.id.messages_viewPager)
    ViewPager messagesViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tabbed_messages);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("adminObj", "");
        adminObj = gson.fromJson(json, AdminObj.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessages();
    }

    public void sendNewMessage(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_new_message_admin);
        Spinner spRole = dialog.findViewById(R.id.sp_role);
        String[] roles = {"Parent", "Student", "Staff"};

        ArrayAdapter adapterRole = new ArrayAdapter(this, android.R.layout.simple_spinner_item, roles);
        adapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapterRole);
        final String[] role = new String[1];

        spRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        role[0] = "P";
                        break;
                    case 1:
                        role[0] = "S";
                        break;
                    case 2:
                        role[0] = "E";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        EditText etName = dialog.findViewById(R.id.et_name_search);
        Button btnSearch = dialog.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(AdminMessages.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                } else {
                    searchUser(role[0], etName.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void  getMessages(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.v(TAG, "URL - " + AppUrls.GetAllMessages +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&userId=" + adminObj.getUserId());
        Request request = new Request.Builder()
                .url(AppUrls.GetAllMessages +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&userId=" + adminObj.getUserId())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();
                    Log.v("TAG", "response - " + jsonResp);
                    runOnUiThread(() -> {
                        try {
                            JSONObject ParentjObject = new JSONObject(jsonResp);
                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = ParentjObject.getJSONArray("Messages");
                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<MsgDetails>>() {
                                }.getType();
                                listMessages.clear();
                                listMessages.addAll(gson.fromJson(jsonArray.toString(), type));
                                Log.v("TAG", listMessages.size() + "");
                                Collections.reverse(listMessages);
                                MessagesTabAdapter adapter = new MessagesTabAdapter(getSupportFragmentManager());
                                adapter.addFragment(new AdminStaffMessages(), "Staff");
                                adapter.addFragment(new AdminParentMessages(), "Parent");
                                messagesViewPager.setAdapter(adapter);
                                messagesTabLayout.setupWithViewPager(messagesViewPager);
                            } else {
                            }
                        } catch (JSONException e) {
                            Log.v("TAG", "error " + e.getMessage());
                        }
                    });
                }
                runOnUiThread(() -> {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                    rvMsgList.setVisibility(View.VISIBLE);
//                    shimmerSkeleton.setVisibility(View.GONE);
//                    skeletonLayout.setVisibility(View.GONE);
                        }
                    }, 2000);
                });
            }
        });
    }

    void searchUser(String... strings){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//            Request request = new Request.Builder()
//                    .url("http://45.127.101.187:9000/searchMember?userTag="+strings[0]+"&schemaName=demo1&userName="+strings[1]+"&branchId=1&userId=1")
//                    .build();
        Log.v(TAG, "URL - " + AppUrls.SearchMember +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&userTag=" + strings[0] + "&userName=" + strings[1] + "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&userId=" + adminObj.getUserId());
        Request request = new Request.Builder()
                .url(AppUrls.SearchMember +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&userTag=" + strings[0] + "&userName=" + strings[1] + "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&userId=" + adminObj.getUserId())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();
                    Log.v("TAG", "response - " + jsonResp);
                    runOnUiThread(() -> {
                        try {
                            JSONObject ParentjObject = new JSONObject(jsonResp);
                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<MsgAdminUserDetails>>() {
                                }.getType();
                                JSONArray jsonArray = ParentjObject.getJSONArray("userDetails");
                                List<MsgAdminUserDetails> listUsers = new ArrayList<>();
                                listUsers.clear();
                                listUsers.addAll(gson.fromJson(jsonArray.toString(), type));
                                Intent intent = new Intent(AdminMessages.this, MessageChatActivity.class);
                                intent.putExtra("userName", listUsers.get(0).getUserName());
                                intent.putExtra("userId", listUsers.get(0).getUserId());
                                intent.putExtra("createdBy", "" + adminObj.getUserId());
                                intent.putExtra("newMessage", "1");
                                startActivity(intent);
                            } else {
                                Toast.makeText(AdminMessages.this, "No user Found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.v("TAG", "error " + e.getMessage());
                        }
                    });
                }
            }
        });
    }



    public List<MsgDetails> getMessageList() {
        return listMessages;
    }

    public class MessagesTabAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        MessagesTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
