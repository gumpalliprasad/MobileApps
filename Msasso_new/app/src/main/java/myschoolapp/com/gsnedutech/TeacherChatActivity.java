package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.MsgDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherChatActivity extends AppCompatActivity {

    private static final String TAG = TeacherChatActivity.class.getName();

    List<MsgDetails> listMessages = new ArrayList<>();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj teacherObj;

    String userId,branchId;

    @BindView(R.id.rv_message_threads)
    RecyclerView rvMessages;

    MyUtils utils = new MyUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_chat);
        ButterKnife.bind(this);

        init();
    }

    void getAllMessages(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllMessages +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + branchId + "&userId=" + userId)
                .build();

        utils.showLog(TAG, AppUrls.GetAllMessages +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + branchId + "&userId=" + userId);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);

                        JSONArray jsonArray = ParentjObject.getJSONArray("Messages");
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<MsgDetails>>() {
                        }.getType();

                        listMessages.clear();
                        listMessages.addAll(gson.fromJson(jsonArray.toString(), type));
                        rvMessages.setAdapter(new ChatAdapter(listMessages));

                        if (listMessages.size()>0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

        userId = "" + teacherObj.getUserId();
        branchId = "" + teacherObj.getBranchId();

        if (NetworkConnectivity.isConnected(this)) {
            getAllMessages();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
        List<MsgDetails> listMessages;
        public ChatAdapter(List<MsgDetails> listMessages) {
            this.listMessages = listMessages;
        }

        @NonNull
        @Override
        public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatAdapter.ViewHolder(LayoutInflater.from(TeacherChatActivity.this).inflate(R.layout.item_chat,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

            if (position==0){
                holder.vOnline.setVisibility(View.GONE);
                holder.tvDate.setVisibility(View.VISIBLE);
                holder.tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return listMessages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            View vOnline;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                vOnline = itemView.findViewById(R.id.view_online);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}