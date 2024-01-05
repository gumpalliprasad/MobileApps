/*
 * *
 *  * Created by SriRamaMurthy A on 16/9/19 2:15 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 16/9/19 12:35 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.Fragments.SubFrag;
import myschoolapp.com.gsnedutech.Models.AnalysisChapterArray;
import myschoolapp.com.gsnedutech.Models.AnalysisTestChapter;
import myschoolapp.com.gsnedutech.Models.AnalysisTestSubject;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.MyUtils;


public class AnalyticsSubChapPerformance extends AppCompatActivity {

    private static final String TAG = "SriRam -" + AnalyticsSubChapPerformance.class.getName();
    MyUtils utils = new MyUtils();

    List<AnalysisTestSubject> listSubPerformance = new ArrayList<>();
    List<AnalysisTestChapter> listChapPerformance = new ArrayList<>();

    String studentId, courseId, classId;
    int position;

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.rv_chap_list)
    RecyclerView rvChapList;

    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_sub_chap_performance);
        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listSubPerformance.clear();
        listSubPerformance.addAll((List<AnalysisTestSubject>) getIntent().getSerializableExtra("subjects"));

        init();

    }

    private void init() {
        studentId = getIntent().getStringExtra("studentId");
        courseId = getIntent().getStringExtra("courseId");
        classId = getIntent().getStringExtra("classId");
        position = getIntent().getIntExtra("pos", 0);

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(position);
//


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (NetworkConnectivity.isConnected(AnalyticsSubChapPerformance.this)) {
                    new GetChapterAnalysis().execute(studentId, courseId, classId, listSubPerformance.get(position).getSubjectId());
                } else {
                    new MyUtils().alertDialog(1, AnalyticsSubChapPerformance.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close),false);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.iv_move_left, R.id.iv_move_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_move_left:
                if (position > 0) {
                    pager.setCurrentItem(--position);
                }
                break;
            case R.id.iv_move_right:
                if (position < listSubPerformance.size() - 1) {
                    pager.setCurrentItem(++position);
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new SubFrag(listSubPerformance.get(position));
        }

        @Override
        public int getCount() {
            return listSubPerformance.size();
        }
    }

    private class GetChapterAnalysis extends AsyncTask<String, Void, String> {

        MyUtils utils = new MyUtils();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           utils.showLoader(AnalyticsSubChapPerformance.this);
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
                jsonObject.put("studentId", strings[0]);
                jsonObject.put("courseId", strings[1]);
                jsonObject.put("classId", strings[2]);
                jsonObject.put("subjectId", strings[3]);
            } catch (Exception e) {

            }

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            utils.showLog(TAG, "body " + jsonObject.toString());

            utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetStudentMockTestsAnalysisByChapter);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetStudentMockTestsAnalysisByChapter)
                    .post(body)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Chapter Test Analysis - " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisChapterArray");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AnalysisTestChapter>>() {
                        }.getType();

                        listChapPerformance.clear();
                        listChapPerformance.addAll(gson.fromJson(jsonArr.toString(), type));
                        rvChapList.setLayoutManager(new LinearLayoutManager(AnalyticsSubChapPerformance.this));
                        rvChapList.setAdapter(new ChapAdapter(listChapPerformance));

                    }


                } catch (Exception e) {
                    utils.showLog(TAG, e.getMessage());
                }

            }
            utils.dismissDialog();
        }

    }

    class ChapAdapter extends RecyclerView.Adapter<ChapAdapter.ViewHolder> {

        List<AnalysisTestChapter> listChap;

        ChapAdapter(List<AnalysisTestChapter> listChap) {
            this.listChap = listChap;
        }

        @NonNull
        @Override
        public ChapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AnalyticsSubChapPerformance.this).inflate(R.layout.cv_analysis_chap, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ChapAdapter.ViewHolder holder, int position) {
            int i = position + 1;
            holder.tvChapName.setText(i + ". " + listChap.get(position).getChapterName());
            holder.tvChapPercent.setText("" + new MyUtils().roundTwoDecimals(listChap.get(position).getPercentage()) + "%");

            List<AnalysisChapterArray> listChapAnalysis = new ArrayList<>();
            listChapAnalysis.addAll(listChap.get(position).getChapterAnalysisArray());
            String test_type = "";
            for (int n = 0; n < listChapAnalysis.size(); n++) {
                if (n == listChapAnalysis.size() - 1) {
                    if (listChapAnalysis.get(n).getTestCount() == "0") {
                        test_type = "No Tests Taken";
                    } else {
                        test_type = test_type + listChapAnalysis.get(n).getTestCount() + " " + listChapAnalysis.get(n).getTestTypeName();
                    }
                } else {
                    test_type = test_type + listChapAnalysis.get(n).getTestCount() + " " + listChapAnalysis.get(n).getTestTypeName() + " | ";
                }
            }
            holder.tvChapTestType.setText(test_type);
//            holder.tvChapTime.setText(listChap.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent());
            if (Integer.parseInt(listChap.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) != 0)
                holder.tvChapTime.setText("" + String.format("%02d:%02d:%02d", Integer.parseInt(listChap.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) / 3600, Integer.parseInt(listChap.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) / 60, Integer.parseInt(listChap.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) % 60));
            else
                holder.tvChapTime.setText(" - ");

            holder.rbPerf.setRating(Float.parseFloat((listChap.get(position).getPercentage() / 33) + ""));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AnalyticsSubChapPerformance.this, AnalyticsTopicPerformance.class);
//                    intent.putExtra("timeSpent",holder.tvChapTime.getText().toString());
//                    intent.putExtra("chapterName",holder.tvChapName.getText().toString());
//                    intent.putExtra("percentage",listChap.get(position).getPercentage()+"");
                    intent.putExtra("testType", holder.tvChapTestType.getText().toString());
//                    intent.putExtra("chapterId",listChap.get(position).getChapterId());

                    intent.putExtra("chapObj", listChap.get(position));
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listChap.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChapName, tvChapPercent, tvChapTime, tvChapTestType;
            RatingBar rbPerf;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvChapName = itemView.findViewById(R.id.tv_chap_name);
                tvChapPercent = itemView.findViewById(R.id.tv_chap_percentage);
                tvChapTime = itemView.findViewById(R.id.tv_chap_time);
                tvChapTestType = itemView.findViewById(R.id.tv_chap_test_type);
                rbPerf = itemView.findViewById(R.id.rating);
            }
        }
    }
}
