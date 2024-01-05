package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.CourseMockTestReports;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TopicMockTestInfo extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TopicMockTestInfo.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.tv_chapName)
    TextView tvChapName;

    @BindView(R.id.tv_takeTest)
    TextView tvTakeTest;

    @BindView(R.id.rv_reports)
    RecyclerView rvReports;

    @BindView(R.id.tv_no_records)
    TextView tvNoRecords;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    String topicId;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    List<CourseMockTestReports> listReports = new ArrayList<>();
    MyUtils utils = new MyUtils();
    String courseId = "", classId = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_mock_test);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(view -> onBackPressed());


        init();


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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TopicMockTestInfo.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getReports();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {

        topicId = getIntent().getStringExtra("topicId");
        courseId = getIntent().getStringExtra("courseId");
        classId = getIntent().getStringExtra("classId");

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        tvChapName.setText(getIntent().getStringExtra("topicName"));
        tvTakeTest.setOnClickListener(view -> startTest());

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        rvReports.setLayoutManager(new LinearLayoutManager(TopicMockTestInfo.this));
//        listReports.addAll((Collection<? extends CourseMockTestReports>) getIntent().getSerializableExtra("reports"));
//        if (listReports.size() > 0) {
//            rvReports.setAdapter(new ReportsAdapterClass(listReports));
//
//        } else {
//            rvReports.setVisibility(View.GONE);
//            tvNoRecords.setVisibility(View.VISIBLE);
//
//        }

    }



    private void getReports() {

        utils.showLoader(TopicMockTestInfo.this);

        ApiClient apiClient = new ApiClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("schemaName",sh_Pref.getString("schema",""));
            json.put("studentId", sObj.getStudentId());
            json.put("topicId", topicId);
            json.put("topicCCMapId", getIntent().getStringExtra(AppConst.TopicCCMapId));
            json.put("chapterId", 0);
            json.put("subjectId", 0);
            json.put("classId", 0);
            json.put("courseId", 0);
            json.put("testType", "1");
            json.put("testTypename", "TOPIC-WISE");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(),JSON);

        utils.showLog(TAG, "TestReport Url - " + new AppUrls().GetStudentMockTestReport);
        utils.showLog(TAG, "TestReport Obj - " + json);
        Request request = apiClient.postRequest(new AppUrls().GetStudentMockTestReport,body, sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
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
                if (response.body()!=null){
                    try {
                        String jsonResp = response.body().string();

                        utils.showLog(TAG, "Reports analysis- " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("MockTestReport");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CourseMockTestReports>>() {}.getType();
                            listReports.clear();
                            listReports.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog(TAG, "size - " + listReports.size());
//                            Collections.reverse(listReports);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listReports.size() > 0) {
                                        findViewById(R.id.ll_mock).setVisibility(View.VISIBLE);
                                        rvReports.setAdapter(new ReportsAdapterClass(listReports));
                                        rvReports.setVisibility(View.VISIBLE);
                                        tvNoRecords.setVisibility(View.GONE);
                                    } else {
                                        findViewById(R.id.ll_mock).setVisibility(View.GONE);
                                        rvReports.setVisibility(View.GONE);
                                        tvNoRecords.setVisibility(View.VISIBLE);

                                    }
                                }
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, TopicMockTestInfo.this, message, sh_Pref);
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvReports.setVisibility(View.GONE);
                                    tvNoRecords.setVisibility(View.VISIBLE);
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        },1500);
                    }
                });
            }
        });
    }


    class ReportsAdapterClass extends RecyclerView.Adapter<ReportsAdapterClass.ViewHolder> {

        List<CourseMockTestReports> list_repo;
        float[] yData;
        String[] xData;

        ReportsAdapterClass(List<CourseMockTestReports> list_repo) {
            this.list_repo = list_repo;
            utils.showLog(TAG, "size - " + list_repo.size());
        }


        @NonNull
        @Override
        public ReportsAdapterClass.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TopicMockTestInfo.this).inflate(R.layout.course_reports_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportsAdapterClass.ViewHolder holder, int position) {
            holder.tv_marks_obtained.setText(list_repo.get(position).getCorrectAnswers() + "");
            int total_marks = list_repo.get(position).getCorrectAnswers() + list_repo.get(position).getWrongAnswers() + list_repo.get(position).getSkippedAnswers();
            holder.tv_total_marks.setText("/" + total_marks + "");
            holder.tv_percentage.setText(list_repo.get(position).getPercentage() + "");

            String date = list_repo.get(position).getCreatedDate();
            String[] date_arr = date.split(" ");
            String time = date_arr[1];
            String[] time_arr = time.split(":");
            String time_final = "";

            if (Integer.parseInt(time_arr[0].trim()) < 12) {
                time_final = time_arr[0] + ":" + time_arr[1] + " AM";
            } else if (Integer.parseInt(time_arr[0].trim()) > 12) {
                int hours = Integer.parseInt(time_arr[0]) - 12;
                time_final = hours + ":" + time_arr[1] + " PM";
            } else if ((Integer.parseInt(time_arr[0].trim()) == 12)) {
                time_final = time_arr[0] + ":" + time_arr[1] + " PM";
            }


            holder.tv_date.setText("Date: "+date_arr[0] + "  " + time_final);
            holder.tv_time_taken.setText(list_repo.get(position).getTotalTimeSpent() + " Mins");

            int x = list_repo.get(position).getCorrectAnswers() + list_repo.get(position).getWrongAnswers();
            holder.tv_total_answered.setText("Total: "+x);
            makePieChart(holder.piechart_reports, list_repo.get(position).getSkippedAnswers(), list_repo.get(position).getCorrectAnswers(), list_repo.get(position).getWrongAnswers());
            holder.iv_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isVisible(holder.piechart_reports)) {
                        holder.piechart_reports.setVisibility(View.GONE);
                    } else {
                        holder.piechart_reports.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        boolean isVisible(PieChart view_chart) {
            return view_chart.getVisibility() == View.VISIBLE;
        }

        private void makePieChart(PieChart pieChart, int skippedAns, int wrongAns, int CorrectAns) {


            yData = new float[]{skippedAns, CorrectAns, wrongAns};
            xData = new String[]{"Skipped", "Correct", "Worng"};


            int totalGivenAnswer = skippedAns + wrongAns + CorrectAns;


            Description description = new Description();
            description.setText("");

            pieChart.setDescription(description);
            pieChart.setNoDataTextColor(Color.WHITE);
            pieChart.setRotationEnabled(true);
            //piechart.setUsePercentValues(true);
            pieChart.setHoleColor(Color.TRANSPARENT);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.setHoleRadius(60f);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.animateXY(3000, 3000);
            pieChart.setTouchEnabled(true);

            addDataSet(pieChart);

            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {

                }

                @Override
                public void onNothingSelected() {

                }
            });


        }

        private void addDataSet(PieChart pieChart) {

            ArrayList<PieEntry> yEntrys = new ArrayList<>();
            ArrayList<String> xEntrys = new ArrayList<>();

            for (int i = 0; i < yData.length; i++) {
                yEntrys.add(new PieEntry(yData[i], i));
            }

            for (int i = 0; i < xData.length; i++) {
                xEntrys.add(xData[i]);
            }

            //create the data set
            PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
            pieDataSet.setSliceSpace(2);
            pieDataSet.setValueTextSize(0);
            pieDataSet.setValueTextColor(Color.WHITE);

            //add colors to dataset
            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.rgb(241, 195, 15)); //uans
            colors.add(Color.rgb(231, 75, 59));//wans
            colors.add(Color.rgb(22, 160, 133)); //cans


            pieDataSet.setColors(colors);

            // add legend to chart
            Legend legend = pieChart.getLegend();


            legend.setForm(Legend.LegendForm.CIRCLE);
//            legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);


            LegendEntry l3 = new LegendEntry("Total Skipped " + (int) (yData[0]), Legend.LegendForm.SQUARE, 10f, 2f, null, Color.rgb(241, 195, 15));
            LegendEntry l2 = new LegendEntry("Total Wrong Answers " + (int) (yData[1]), Legend.LegendForm.SQUARE, 10f, 2f, null, Color.rgb(231, 75, 59));
            LegendEntry l1 = new LegendEntry("Total Correct Answers " + (int) (yData[2]), Legend.LegendForm.SQUARE, 10f, 2f, null, Color.rgb(22, 160, 133));


            legend.setTextSize(12f);
            legend.setTextColor(Color.BLACK);
            legend.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            if (getString(R.string.screen).equalsIgnoreCase("Xlarge")){
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            }
            else legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setWordWrapEnabled(true);
            legend.setDrawInside(false);
            legend.setCustom(new LegendEntry[]{l1, l2, l3});

            //piechart.getLegend().setEnabled(false);

            //create pie data object
            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.setExtraOffsets(-70, 0, 0, 0);
            pieChart.invalidate();
        }

        @Override
        public int getItemCount() {
            return list_repo.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_marks_obtained, tv_total_marks, tv_percentage, tv_time_taken, tv_date, tv_total_answered;
            PieChart piechart_reports;
            ImageView iv_down;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_marks_obtained = itemView.findViewById(R.id.tv_marks_obtained);
                tv_total_marks = itemView.findViewById(R.id.tv_total_mark);
                tv_percentage = itemView.findViewById(R.id.tv_percentage);
                tv_time_taken = itemView.findViewById(R.id.tv_time_taken);
                piechart_reports = itemView.findViewById(R.id.idPieChart_reports);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_total_answered = itemView.findViewById(R.id.tv_total_answered);
                iv_down = itemView.findViewById(R.id.iv_down);
            }


        }
    }


    private void startTest() {
        Intent testmock_intent = new Intent(TopicMockTestInfo.this, MockTestNew.class);
        testmock_intent.putExtra("contentType", getIntent().getStringExtra("contentType"));
        testmock_intent.putExtra("courseId", courseId);
        testmock_intent.putExtra("classId", classId);
        testmock_intent.putExtra("subId", getIntent().getStringExtra("subId"));
        testmock_intent.putExtra("chapterId", getIntent().getStringExtra("chapterId"));
        testmock_intent.putExtra("topicId", getIntent().getStringExtra("topicId"));
        testmock_intent.putExtra(AppConst.TopicCCMapId, getIntent().getStringExtra(AppConst.TopicCCMapId));
        testmock_intent.putExtra(AppConst.ChapterCCMapId, getIntent().getStringExtra(AppConst.ChapterCCMapId));
        testmock_intent.putExtra("topicName", getIntent().getStringExtra("topicName"));


        startActivity(testmock_intent);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(2,new Intent(this, CourseTopicView.class));
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}