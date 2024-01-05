/*
 * *
 *  * Created by SriRamaMurthy A on 16/9/19 2:15 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 16/9/19 12:35 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.Models.AnalysisTestChapter;
import myschoolapp.com.gsnedutech.Models.AnalysisTestTopic;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.MyUtils;


public class AnalyticsTopicPerformance extends AppCompatActivity {

    private static final String TAG = AnalyticsTopicPerformance.class.getName();
    MyUtils utils = new MyUtils();

    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.tv_chap_percentage)
    TextView tvChapPercentage;
    @BindView(R.id.tv_chap_time)
    TextView tvChapTime;
    @BindView(R.id.tv_chap_test_type)
    TextView tvChapTestType;


    List<AnalysisTestTopic> listTopic = new ArrayList<>();
    AnalysisTestChapter chapObj;
    String studentId;
    @BindView(R.id.rv_topic_list)
    RecyclerView rvTopicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_topic_performance);
        ButterKnife.bind(this);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();

        if (NetworkConnectivity.isConnected(AnalyticsTopicPerformance.this)) {

            new GetTopicAnalysis().execute(studentId, chapObj.getChapterId());
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }

    private void init() {
        chapObj = (AnalysisTestChapter) getIntent().getSerializableExtra("chapObj");
        studentId = getIntent().getStringExtra("studentId");

        tvChapPercentage.setText("" + new MyUtils().roundTwoDecimals(chapObj.getPercentage()) + "%");
        tvChapTime.setText(" - ");
        tvChapTestType.setText(getIntent().getStringExtra("testType"));
        Float percent = Float.parseFloat("" + chapObj.getPercentage());
        rating.setRating(Float.parseFloat((percent / 33) + ""));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class GetTopicAnalysis extends AsyncTask<String, Void, String> {

        MyUtils utils = new MyUtils();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utils.showLoader(AnalyticsTopicPerformance.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
                jsonObject.put("chapterId", strings[1]);
                jsonObject.put("studentId", strings[0]);
            } catch (Exception e) {

            }

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            utils.showLog(TAG, "URL " + AppUrls.GETStudentMockTestsAnalysisByTopic);
            utils.showLog(TAG, "Body " + jsonObject.toString());

            Request request = new Request.Builder()
                    .url(AppUrls.GETStudentMockTestsAnalysisByTopic)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Topic Test Analysis - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisTopicArray");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AnalysisTestTopic>>() {
                        }.getType();

                        listTopic.clear();
                        listTopic.addAll(gson.fromJson(jsonArr.toString(), type));
                        rvTopicList.setLayoutManager(new LinearLayoutManager(AnalyticsTopicPerformance.this));
                        rvTopicList.setAdapter(new TopicAdapter(listTopic));

                    }


                } catch (Exception e) {
                    utils.showLog(TAG, e.getMessage());
                }

            }
            utils.dismissDialog();
        }

    }

    class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

        List<AnalysisTestTopic> listTopicTests;

        TopicAdapter(List<AnalysisTestTopic> listTopicTests) {
            this.listTopicTests = listTopicTests;
        }

        @NonNull
        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AnalyticsTopicPerformance.this).inflate(R.layout.cv_analysis_chap, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {

            int i = position + 1;
            holder.tvTopicName.setText(i + " " + listTopicTests.get(position).getTopicName());
            holder.ivArrow.setVisibility(View.GONE);
            holder.tvTopicercent.setText(listTopicTests.get(position).getCorrectAnswerPercentage() + "%");
            holder.tvTopicTime.setText(""+listTopicTests.get(position).getTotalTimeSpent());
//            holder.tvTopicTime.setText("" + String.format("%02f:%02f:%02f", Float.parseFloat(listTopicTests.get(position).getTotalTimeSpent()) / 3600, (Float.parseFloat(listTopicTests.get(position).getTotalTimeSpent()) % 3600) / 60, Float.parseFloat(listTopicTests.get(position).getTotalTimeSpent()) % 60));

            holder.tvTopicTestType.setText(listTopicTests.get(position).getTestCount());
            holder.rating.setRating(Float.parseFloat(listTopicTests.get(position).getCorrectAnswerPercentage())/33);
        }

        @Override
        public int getItemCount() {
            return listTopicTests.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivArrow;
            TextView tvTopicName, tvTopicercent, tvTopicTime, tvTopicTestType;
            RatingBar rating;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivArrow = itemView.findViewById(R.id.arrow);
                tvTopicName = itemView.findViewById(R.id.tv_chap_name);
                tvTopicercent = itemView.findViewById(R.id.tv_chap_percentage);
                tvTopicTime = itemView.findViewById(R.id.tv_chap_time);
                tvTopicTestType = itemView.findViewById(R.id.tv_chap_test_type);
                rating = itemView.findViewById(R.id.rating);
            }
        }
    }
}
