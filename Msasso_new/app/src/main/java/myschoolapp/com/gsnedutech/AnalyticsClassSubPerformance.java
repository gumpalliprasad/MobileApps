/*
 * *
 *  * Created by SriRamaMurthy A on 1/10/19 4:53 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 30/9/19 6:08 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.Models.AnalysisTestClass;
import myschoolapp.com.gsnedutech.Models.AnalysisTestSubject;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.MyUtils;


public class AnalyticsClassSubPerformance extends AppCompatActivity {

    private static final String TAG = "SriRam -" + AnalyticsClassSubPerformance.class.getName();
    MyUtils utils = new MyUtils();

    AnalysisTestClass analysisTestClass;
    @BindView(R.id.tv_percentage)
    TextView tvPercentage;
    @BindView(R.id.tv_time_spent)
    TextView tvTimeSpent;
    @BindView(R.id.tv_test_taken)
    TextView tvTestTaken;
    @BindView(R.id.rv_sub_list)
    RecyclerView rvSubList;

    List<AnalysisTestSubject> listSubPerformance = new ArrayList<>();

    String studentId, courseId, classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_class_performance);
        ButterKnife.bind(this);

        analysisTestClass = (AnalysisTestClass) getIntent().getSerializableExtra("analysisTestClass");

        studentId = getIntent().getStringExtra("studentId");
        courseId = getIntent().getStringExtra("courseId");
        classId = "" + analysisTestClass.getClassId();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvPercentage.setText("" + new MyUtils().roundTwoDecimals((double) analysisTestClass.getPercentage()) + " %");
        tvTestTaken.setText("" + analysisTestClass.getTestCount());
//        tvTimeSpent.setText("" + analysisTestClass.getTotalTimeSpent() + " sec");

        tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", analysisTestClass.getTotalTimeSpent() / 3600, (analysisTestClass.getTotalTimeSpent() % 3600) / 60, analysisTestClass.getTotalTimeSpent() % 60));


        if (NetworkConnectivity.isConnected(AnalyticsClassSubPerformance.this)) {
            new GetSubAnalysis().execute(studentId, courseId, classId);
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class GetSubAnalysis extends AsyncTask<String, Void, String> {

        MyUtils utils = new MyUtils();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           utils.showLoader(AnalyticsClassSubPerformance.this);
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

            utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetSubjectAnalysisByclass +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&courseId=" + strings[1] + "&studentId=" + strings[0] + "&classId=" + strings[2]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetSubjectAnalysisByclass +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&courseId=" + strings[1] + "&studentId=" + strings[0] + "&classId=" + strings[2])
                    .build();

//            Request request = new Request.Builder()
//                    .url("http://45.127.101.187:9009/studentMockTestsSubjectAnalysisByclass?courseId=5&studentId=3&schemaName=abc5527&classId="+strings[0])
//                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Sub Test Analysis - " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisSubjectArray");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AnalysisTestSubject>>() {
                        }.getType();

                        listSubPerformance.addAll(gson.fromJson(jsonArr.toString(), type));
                        rvSubList.setLayoutManager(new LinearLayoutManager(AnalyticsClassSubPerformance.this));
                        rvSubList.setAdapter(new SubjectAdapter(listSubPerformance));
                    }


                } catch (Exception e) {
                    utils.showLog(TAG, e.getMessage());
                }

            }
            utils.dismissDialog();
        }

    }

    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

        List<AnalysisTestSubject> listSubPerf;

        public SubjectAdapter(List<AnalysisTestSubject> listSubPerf) {
            this.listSubPerf = listSubPerf;
        }

        @NonNull
        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AnalyticsClassSubPerformance.this).inflate(R.layout.cv_analysis_sub, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {
            holder.tvSubName.setText(listSubPerf.get(position).getSubjectName());
            holder.tvPercentage.setText("" + new MyUtils().roundTwoDecimals(listSubPerf.get(position).getPercentage()) + "%");
//            holder.tvTimeSpent.setText(listSubPerf.get(position).getTotalTimeSpent() + " sec");
            holder.tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", Integer.parseInt(listSubPerf.get(position).getTotalTimeSpent() ) / 3600, Integer.parseInt(listSubPerf.get(position).getTotalTimeSpent() )  / 60, Integer.parseInt(listSubPerf.get(position).getTotalTimeSpent() )  % 60));

            holder.tvTestTaken.setText(listSubPerf.get(position).getTestCount() + "");

            holder.ivSub.setBackgroundResource(R.drawable.bg_grad_tab_select);

            switch (listSubPerf.get(position).getSubjectGroup()) {

                case "Maths":
                    holder.ivSub.setImageResource(R.drawable.ic_maths_icon_white);
                    break;
                case "Science":
                    holder.ivSub.setImageResource(R.drawable.ic_science_white);
                    break;
                case "English":
                    holder.ivSub.setImageResource(R.drawable.ic_english_white);
                    break;
                case "GeneralKnowledge":
                    holder.ivSub.setImageResource(R.drawable.ic_gk_white);
                    break;
                case "SocialScience":
                case "Social Studies":
                    holder.ivSub.setImageResource(R.drawable.ic_social_white);
                    break;
                case "Hindi":
                    holder.ivSub.setImageResource(R.drawable.ic_hindi_white);
                    break;
                case "Telugu":
                    holder.ivSub.setImageResource(R.drawable.ic_telugu_white);
                    break;
                case "Physics":
                    holder.ivSub.setImageResource(R.drawable.ic_physics_white);
                    break;
                case "Chemistry":
                    holder.ivSub.setImageResource(R.drawable.ic_chemistry_white);
                    break;
                case "Biology":
                    holder.ivSub.setImageResource(R.drawable.ic_biology_white);
                    break;
                default:
                    holder.ivSub.setImageResource(R.drawable.ic_science_white);
                    break;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AnalyticsClassSubPerformance.this, AnalyticsSubChapPerformance.class);
                    intent.putExtra("subjects", (Serializable) listSubPerf);
                    intent.putExtra("pos", position);
                    intent.putExtra("classId", classId);
                    intent.putExtra("courseId", courseId);
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSubPerf.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName, tvPercentage, tvTimeSpent, tvTestTaken;
            ImageView ivSub;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvPercentage = itemView.findViewById(R.id.tv_score_sub);
                tvTimeSpent = itemView.findViewById(R.id.tv_time_spent_sub);
                tvTestTaken = itemView.findViewById(R.id.tv_test_taken_sub);
                ivSub = itemView.findViewById(R.id.iv_sub);
            }
        }
    }
}
