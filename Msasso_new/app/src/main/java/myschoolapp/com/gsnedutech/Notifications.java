package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.NotificationModel;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Notifications extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener{

    private static final String TAG = Notifications.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_notifications)
    RecyclerView rvNotifications;

    @BindView(R.id.tv_no_notifications)
    TextView tvNoNotifications;

    List<NotificationModel> notificationsList = new ArrayList<>();

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        rvNotifications.setLayoutManager(new LinearLayoutManager(Notifications.this));

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
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, Notifications.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                getNotifications();
            }
            isNetworkAvail = true;
        }
    }

    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

        @NonNull
        @Override
        public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(Notifications.this).inflate(R.layout.item_notification,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {

//            if (position%2==0){
//                holder.ivIcon.setImageResource(R.drawable.ic_maths_notification);
//            }else {
//                holder.ivIcon.setImageResource(R.drawable.ic_khub_notification);
//            }
            holder.tvTitle.setText(notificationsList.get(position).getNotificationTitle());
            holder.tvDesc.setText(notificationsList.get(position).getNotificationDesc());

        }

        @Override
        public int getItemCount() {
            return notificationsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivIcon;
            TextView tvDesc, tvTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tv_title);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvDesc = itemView.findViewById(R.id.tv_desc);
            }
        }
    }

    private void getNotifications() {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - Notifications - " + AppUrls.GetStuNotifications + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")+"&sectionId="+sObj.getClassCourseSectionId());

        Request request = new Request.Builder()
                .url(AppUrls.GetStuNotifications + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")+"&sectionId="+sObj.getClassCourseSectionId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        tvNoNotifications.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            tvNoNotifications.setVisibility(View.VISIBLE);
                            rvNotifications.setVisibility(View.GONE);
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("notificationArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<NotificationModel>>() {
                            }.getType();

                            notificationsList.clear();
                            notificationsList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "notifications - " + notificationsList.size());

                            runOnUiThread(() -> {
                                if (notificationsList.size()>0){
                                    rvNotifications.setAdapter(new NotificationAdapter());
                                    tvNoNotifications.setVisibility(View.GONE);
                                    rvNotifications.setVisibility(View.VISIBLE);
                                }
                                else {
                                    tvNoNotifications.setVisibility(View.VISIBLE);
                                    rvNotifications.setVisibility(View.GONE);
                                }
                            });


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}