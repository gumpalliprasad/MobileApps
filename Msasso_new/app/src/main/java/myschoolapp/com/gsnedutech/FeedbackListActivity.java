package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.SurveyForm;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FeedbackListActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = "SriRam -" + FeedbackListActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_feed_back)
    RecyclerView rvFeedBack;
    MyUtils utils = new MyUtils();

    String date;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, FeedbackListActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getFeedBack();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    void getFeedBack(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG,"url "+AppUrls.GetStudentSurveyForms + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&date=" + date + "&classType=" + sObj.getClassType() + "&studentId=" + sObj.getClassCourseSectionId());

//        Request get = new Request.Builder()
//                .url(AppUrls.GetStudentSurveyForms + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&date=" + date + "&classType=" + sObj.getClassType() + "&studentId=" + sObj.getClassCourseSectionId())
//                .build();
        Request get = new Request.Builder()
                .url("http://13.232.73.168:9000/getStudentSurveyForms?schemaName=nar666&classType=1&date=2020-09-23&studentId=65537")
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvFeedBack.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {


                }else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = ParentjObject.getJSONArray("TopicVideos");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SurveyForm>>() {
                            }.getType();

                            List<SurveyForm> listForm = new ArrayList<>();

                            listForm.addAll(gson.fromJson(jsonArray.toString(), type));


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvFeedBack.setAdapter(new FeedBackAdapter(listForm));
                                }
                            });

                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }


    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        rvFeedBack.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder>{

        List<SurveyForm> listForm;

        public FeedBackAdapter(List<SurveyForm> listForm) {
            this.listForm = listForm;
        }

        @NonNull
        @Override
        public FeedBackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeedbackListActivity.this).inflate(R.layout.item_feed_back,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FeedBackAdapter.ViewHolder holder, int position) {

            holder.tvFormName.setText(listForm.get(position).getSurveyFormName());
            holder.tvFormDesc.setText(listForm.get(position).getSurveyFormDesc());
            try {
                holder.tvSubDate.setText(new SimpleDateFormat("dd MMM,yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listForm.get(position).getFormEndDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(FeedbackListActivity.this, FeedbackFormActivity.class);
                    intent.putExtra("surveyFormId",listForm.get(position).getSurveyFormId());
                    intent.putExtra("studentId",sObj.getStudentId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listForm.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFormName,tvFormDesc,tvSubDate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFormName = itemView.findViewById(R.id.tv_form_name);
                tvFormDesc = itemView.findViewById(R.id.tv_form_desc);
                tvSubDate = itemView.findViewById(R.id.tv_sub_date);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}