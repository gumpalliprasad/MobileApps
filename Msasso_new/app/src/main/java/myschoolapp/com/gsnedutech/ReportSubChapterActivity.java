package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
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
import myschoolapp.com.gsnedutech.Fragments.SubFrag;
import myschoolapp.com.gsnedutech.Models.AnalysisChapterArray;
import myschoolapp.com.gsnedutech.Models.AnalysisTestChapter;
import myschoolapp.com.gsnedutech.Models.AnalysisTestSubject;
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

public class ReportSubChapterActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + ReportSubChapterActivity.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    List<AnalysisTestSubject> listSubPerformance = new ArrayList<>();

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
        setContentView(R.layout.activity_report_sub_chapter);
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
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, ReportSubChapterActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getChapterAnalysis(listSubPerformance.get(position).getSubjectId());
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    void init(){
        listSubPerformance.clear();
        listSubPerformance.addAll((List<AnalysisTestSubject>) getIntent().getSerializableExtra("subjects"));

        studentId = getIntent().getStringExtra("studentId");
        courseId = getIntent().getStringExtra("courseId");
        classId = getIntent().getStringExtra("classId");
        position = getIntent().getIntExtra("pos", 0);




        findViewById(R.id.iv_move_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0) {
                    pager.setCurrentItem(--position);
                }
            }
        });

        findViewById(R.id.iv_move_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < listSubPerformance.size() - 1) {
                    pager.setCurrentItem(++position);
                }
            }
        });

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(position);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (NetworkConnectivity.isConnected(ReportSubChapterActivity.this)) {
                    getChapterAnalysis(listSubPerformance.get(position).getSubjectId());
                } else {
                    new MyUtils().alertDialog(1, ReportSubChapterActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close),false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void getChapterAnalysis( String subjectId) {
        utils.showLoader(ReportSubChapterActivity.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("studentId", studentId);
            jsonObject.put("courseId", courseId);
            jsonObject.put("classId", classId);
            jsonObject.put("subjectId", subjectId);
        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        utils.showLog(TAG, "body " + jsonObject.toString());

        utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetStudentMockTestsAnalysisByChapter);

        Request request = new Request.Builder()
                .url(new AppUrls().GetStudentMockTestsAnalysisByChapter)
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
                            JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisChapterArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AnalysisTestChapter>>() {
                            }.getType();

                            List<AnalysisTestChapter> listChapPerformance = new ArrayList<>();


                            listChapPerformance.clear();
                            listChapPerformance.addAll(gson.fromJson(jsonArr.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvChapList.setLayoutManager(new LinearLayoutManager(ReportSubChapterActivity.this));
                                    rvChapList.setAdapter(new ChapAdapter(listChapPerformance));
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


    class ChapAdapter extends RecyclerView.Adapter<ChapAdapter.ViewHolder>{

        List<AnalysisTestChapter> listChapPerformance;

        public ChapAdapter(List<AnalysisTestChapter> listChapPerformance) {
            this.listChapPerformance = listChapPerformance;
        }

        @NonNull
        @Override
        public ChapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReportSubChapterActivity.this).inflate(R.layout.item_course_overall_performance,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull ChapAdapter.ViewHolder holder, int position) {
            holder.tvChapName.setText(listChapPerformance.get(position).getChapterName());

            List<AnalysisChapterArray> listChapAnalysis = new ArrayList<>();
            listChapAnalysis.addAll(listChapPerformance.get(position).getChapterAnalysisArray());

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

            holder.tvTestTaken.setText(test_type);

            holder.tvAvgScore.setText("" + new MyUtils().roundTwoDecimals(listChapPerformance.get(position).getPercentage()) + "%");


            if (Integer.parseInt(listChapPerformance.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) != 0)
                holder.tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", Integer.parseInt(listChapPerformance.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) / 3600, Integer.parseInt(listChapPerformance.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) / 60, Integer.parseInt(listChapPerformance.get(position).getChapterAnalysisArray().get(0).getTotalTimeSpent()) % 60));
            else
                holder.tvTimeSpent.setText("00:00:00");

            holder.rb.setRating((float)(listChapPerformance.get(position).getPercentage()*5) / 100);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReportSubChapterActivity.this, ReportTopicActivity.class);
                    intent.putExtra("testType", holder.tvTestTaken.getText().toString());
                    intent.putExtra("chapObj", listChapPerformance.get(position));
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listChapPerformance.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvChapName;
            TextView tvTestTaken,tvTimeSpent,tvAvgScore;
            RatingBar rb;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChapName = itemView.findViewById(R.id.tv_course_name);
                tvTestTaken = itemView.findViewById(R.id.tv_test_taken);
                tvAvgScore = itemView.findViewById(R.id.tv_avg_score);
                tvTimeSpent = itemView.findViewById(R.id.tv_avg_time_spent);
                rb = itemView.findViewById(R.id.rb);
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}