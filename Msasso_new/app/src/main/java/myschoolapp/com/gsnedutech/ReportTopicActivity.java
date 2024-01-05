package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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
import myschoolapp.com.gsnedutech.Models.AnalysisTestChapter;
import myschoolapp.com.gsnedutech.Models.AnalysisTestTopic;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportTopicActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = ReportTopicActivity.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    @BindView(R.id.tv_chap_percentage)
    TextView tvChapPercentage;
    @BindView(R.id.tv_chap_time)
    TextView tvChapTime;
    @BindView(R.id.tv_chap_test_type)
    TextView tvChapTestType;
    @BindView(R.id.tv_chap_name)
    TextView tvChapName;


    AnalysisTestChapter chapObj;
    String studentId;
    @BindView(R.id.rv_topic_list)
    RecyclerView rvTopicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_topic);
        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
            utils.alertDialog(1, ReportTopicActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getTopicAnalysis();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    private void init() {
        chapObj = (AnalysisTestChapter) getIntent().getSerializableExtra("chapObj");
        studentId = getIntent().getStringExtra("studentId");

        tvChapName.setText(chapObj.getChapterName());
        tvChapPercentage.setText("" + new MyUtils().roundTwoDecimals(chapObj.getPercentage()) + "%");
        tvChapTime.setText(" - ");
        tvChapTestType.setText(getIntent().getStringExtra("testType"));
        Float percent = Float.parseFloat("" + chapObj.getPercentage());
    }


    private void getTopicAnalysis() {
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("chapterId", chapObj.getChapterId());
            jsonObject.put("studentId", studentId);
        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        utils.showLog(TAG, "URL " + AppUrls.GETStudentMockTestsAnalysisByTopic);
        utils.showLog(TAG, "Body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(AppUrls.GETStudentMockTestsAnalysisByTopic)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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

                String resp = response.body().string();

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisTopicArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AnalysisTestTopic>>() {
                            }.getType();

                            List<AnalysisTestTopic> listTopic = new ArrayList<>();


                            listTopic.clear();
                            listTopic.addAll(gson.fromJson(jsonArr.toString(), type));


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvTopicList.setLayoutManager(new LinearLayoutManager(ReportTopicActivity.this));
                                    rvTopicList.setAdapter(new TopicAdapter(listTopic));
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

    class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

        List<AnalysisTestTopic> listTopic;

        public TopicAdapter(List<AnalysisTestTopic> listTopic) {
            this.listTopic = listTopic;
        }

        @NonNull
        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReportTopicActivity.this).inflate(R.layout.item_course_overall_performance,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {
            int i = position + 1;
            holder.tvTopicName.setText(i + " " + listTopic.get(position).getTopicName());
            holder.tvAvgScore.setText(listTopic.get(position).getCorrectAnswerPercentage() + "%");
            holder.tvTimeSpent.setText(""+listTopic.get(position).getTotalTimeSpent());
            holder.tvTestTaken.setText(listTopic.get(position).getTestCount());
            holder.rb.setRating((Float.parseFloat(listTopic.get(position).getCorrectAnswerPercentage())*5)/100);
        }

        @Override
        public int getItemCount() {
            return listTopic.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTopicName;
            TextView tvTestTaken,tvTimeSpent,tvAvgScore;
            RatingBar rb;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTopicName = itemView.findViewById(R.id.tv_course_name);
                tvTestTaken = itemView.findViewById(R.id.tv_test_taken);
                tvAvgScore = itemView.findViewById(R.id.tv_avg_score);
                tvTimeSpent = itemView.findViewById(R.id.tv_avg_time_spent);
                rb = itemView.findViewById(R.id.rb);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}