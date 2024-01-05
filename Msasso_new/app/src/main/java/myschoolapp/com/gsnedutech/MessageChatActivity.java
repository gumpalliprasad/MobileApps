/*
 * *
 *  * Created by SriRamaMurthy A on 26/9/19 12:13 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 26/9/19 12:13 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.MessagesChatObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageChatActivity extends AppCompatActivity {

    private static final String TAG = MessageChatActivity.class.getName();

    @BindView(R.id.tv_profile_img)
    TextView tvProfileImg;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_list_convo)
    RecyclerView rvListConvo;
    @BindView(R.id.et_message)
    EditText etMessage;

    String threadId, userId, createdBy;

    List<MessagesChatObj> msgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

        tvUserName.setText(getIntent().getStringExtra("userName"));
        String name = getIntent().getStringExtra("userName").charAt(0) + "";
        tvProfileImg = findViewById(R.id.tv_profile_img);
        tvProfileImg.setText(name.toUpperCase());
        rvListConvo.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().getStringExtra("newMessage").equalsIgnoreCase("0")) {
            getChat();
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getChat();
                }
            }, 0, 5000);
        }

    }

    private void init() {
        threadId = getIntent().getStringExtra("threadId");
        userId = getIntent().getStringExtra("userId");
        createdBy = getIntent().getStringExtra("createdBy");
    }

    public void sendMessage(View view) {
        if (etMessage.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please type the message first", Toast.LENGTH_SHORT).show();
        } else {
            postMessage(etMessage.getText().toString());
            etMessage.setText("");

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void getChat(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetMessageDetails +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&threadId=" + threadId + "&userId=" + userId);

        Request request = new Request.Builder()
                .url(AppUrls.GetMessageDetails +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&threadId=" + threadId + "&userId=" + userId)
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
                                JSONArray jsonArray = ParentjObject.getJSONArray("messageDetails");
                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<MessagesChatObj>>() {
                                }.getType();

                                List<MessagesChatObj> list = new ArrayList<>();
                                list.addAll(gson.fromJson(jsonArray.toString(), type));

                                String x = list.get(0).getCreatedDate();
                                String[] dt = x.split(" ");
                                x = dt[0];
                                msgList.clear();
                                msgList.add(new MessagesChatObj());
                                msgList.get(0).setCreatedDate(dt[0]);
                                msgList.get(0).setMessage("");

                                List<MessagesChatObj> listDate = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getCreatedDate().split(" ")[0].equalsIgnoreCase(x)) {
                                        listDate.add(list.get(i));
                                        msgList.add(list.get(i));
                                    } else {

                                        String[] dta = list.get(i).getCreatedDate().split(" ");
                                        x = dta[0];
                                        msgList.add(new MessagesChatObj());
                                        msgList.get(msgList.size() - 1).setCreatedDate(x);
                                        msgList.get(msgList.size() - 1).setMessage("");
                                        listDate.clear();
                                        listDate.add(list.get(i));
                                        msgList.add(list.get(i));
                                    }

                                }


                                rvListConvo.setAdapter(new ChatAdapter(msgList));
                                rvListConvo.scrollToPosition(msgList.size() - 1);

                            }


                        } catch (JSONException e) {
                            Log.v("TAG", "error " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

        List<MessagesChatObj> msgList;


        public ChatAdapter(List<MessagesChatObj> msgList) {
            this.msgList = msgList;
        }

        @Override
        public int getItemViewType(int position) {
            if (msgList.get(position).getMessage().equalsIgnoreCase("")) {
                return 0;
            } else {
                return 1;
            }
        }

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new ViewHolder(LayoutInflater.from(MessageChatActivity.this).inflate(R.layout.item_msg_date_head, viewGroup, false));
            } else {
                return new ViewHolder(LayoutInflater.from(MessageChatActivity.this).inflate(R.layout.item_msg_message, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                viewHolder.tvDateHead.setText(msgList.get(i).getCreatedDate());
            } else {
                //viewHolder.tvMessage.setText(msgList.get(i).getMessage());
                if (!msgList.get(i).getMessageFrom().equalsIgnoreCase(userId)) {
                    viewHolder.llOther.setVisibility(View.GONE);
                    viewHolder.llUserMessages.setVisibility(View.VISIBLE);
                    viewHolder.tvUserMsg.setText(msgList.get(i).getMessage());


                    String Date = msgList.get(i).getCreatedDate();
                    String[] datetime = Date.split(" ");
                    String[] time = datetime[1].split(":");
                    String t = "";
                    if (Integer.parseInt(time[0]) < 12) {
                        t = time[0] + ":" + time[1] + "AM";
                    } else if (Integer.parseInt(time[0]) > 12) {
                        int x = Integer.parseInt(time[0]) - 12;
                        t = x + ":" + time[1] + "PM";
                    } else {
                        t = time[0] + ":" + time[1] + "PM";
                    }
                    viewHolder.tvUserTime.setText(t);

                } else {
                    viewHolder.llOther.setVisibility(View.VISIBLE);
                    viewHolder.llUserMessages.setVisibility(View.GONE);
                    viewHolder.tvMessage.setText(msgList.get(i).getMessage());

                    String Date = msgList.get(i).getCreatedDate();
                    String[] datetime = Date.split(" ");
                    String[] time = datetime[1].split(":");
                    String t = "";
                    if (Integer.parseInt(time[0]) < 12) {
                        t = time[0] + ":" + time[1] + "AM";
                    } else if (Integer.parseInt(time[0]) > 12) {
                        int x = Integer.parseInt(time[0]) - 12;
                        t = x + ":" + time[1] + "PM";
                    } else {
                        t = time[0] + ":" + time[1] + "PM";
                    }
                    viewHolder.tvTime.setText(t);
                }
            }
        }

        @Override
        public int getItemCount() {
            return msgList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDateHead;
            LinearLayout llUserMessages, llOther;
            TextView tvUserMsg, tvMessage, tvUserTime, tvTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvDateHead = itemView.findViewById(R.id.tv_date_head);
                llUserMessages = itemView.findViewById(R.id.ll_user);
                llOther = itemView.findViewById(R.id.ll_other);
                tvUserMsg = itemView.findViewById(R.id.tv_user_message);
                tvMessage = itemView.findViewById(R.id.tv_message);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvUserTime = itemView.findViewById(R.id.tv_user_time);
                //tvMessage = itemView.findViewById(R.id.message);
            }
        }
    }

    void postMessage(String... strings){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();

//            {“schemaName”:“demo1",“createdBy”:“1",“MessageTo”:3,“messageDetails”:“hLeoskf”}

        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("createdBy", createdBy);
            jsonObject.put("MessageTo", userId);
            jsonObject.put("messageDetails", strings[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "URL - " + AppUrls.PostCreateNewMessage);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(AppUrls.PostCreateNewMessage)
                .post(body)
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
                            JSONObject ParentObject = new JSONObject(jsonResp);
                            if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")) {

                                if (getIntent().getStringExtra("newMessage").equalsIgnoreCase("0")) {

                                } else {
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}
