/*
 * *
 *  * Created by SriRamaMurthy A on 1/10/19 4:53 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 30/9/19 6:18 PM
 *
 */

package myschoolapp.com.gsnedutech.OnlIneTest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OTStudentOnlineTestResult extends AppCompatActivity {

    private static final String TAG = OTStudentOnlineTestResult.class.getName();

    MyUtils utils = new MyUtils();


    @BindView(R.id.tv_totalmarks)
    TextView tvTotalmarks;
    @BindView(R.id.tv_totalpercent)
    TextView tvTotalpercent;
    @BindView(R.id.tv_rank)
    TextView tvRank;
    @BindView(R.id.idPieChart)
    PieChart piechart;
    @BindView(R.id.tv_correct_marks)
    TextView tvCorrectMarks;
    @BindView(R.id.tv_wrong_marks)
    TextView tvWrongMarks;
    @BindView(R.id.tv_unanswered_marks)
    TextView tvUnansweredMarks;

    @BindView(R.id.img_back)
    ImageView imgBack;

    int testId;
    int studentId;

    int tunans = 0, tcans = 0, twans = 0;
    double totPercent;
    private float[] yData = null;
    private String[] xData = null;

    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ot_student_online_test_result);
        ButterKnife.bind(this);

        init();
        imgBack.setOnClickListener(view -> onBackPressed());

        getOnlineTestQuePaper(getIntent().getStringExtra("testFilePath"));
    }

    /* Get Test Question Paper */
    void getOnlineTestQuePaper(String... strings){
        utils.showLoader(OTStudentOnlineTestResult.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "TestGetQueDetails Url - " + strings[0]);

        Request request = new Request.Builder()
                .url(strings[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesDetialsList responce - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                            testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            utils.showLog(TAG, "testQueList - " + testQueList.size());
                            runOnUiThread(() -> getDBTestQuestions());

                        } else {
//                        showNoDataDialogue();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }


    /* Get User Answers From Database */
    void getDBTestQuestions(){

        utils.showLoader(OTStudentOnlineTestResult.this);

        String jsonResp = "null";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "StudentDBTestQuestions Url - " + AppUrls.GETSTATUSBYID + "?examDocId=" +getIntent().getStringExtra("studentTestFilePath"));

        Request request = new Request.Builder()
                .url(AppUrls.GETSTATUSBYID + "?examDocId=" +getIntent().getStringExtra("studentTestFilePath"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.body()!=null){
                    try {
                        String responce = response.body().string();
                        Log.v(TAG, "DBTestQuestions response - " + responce);
                        JSONObject ParentjObject = new JSONObject(responce);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = ParentjObject.getJSONObject("result");
//                            insertId = jsonObject.getString("_id");
                            JSONArray jsonArr = jsonObject.getJSONArray("testCategories");
                            testQueDetailsList.clear();
                            testQueDetailsList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            Log.v(TAG, "StudentDBTestQuestions testQueDetailsList - " + testQueDetailsList.size());

                            if (testQueDetailsList.size() > 0) {
                                for (int i = 0; i < testQueDetailsList.size(); i++) {
                                    for (int j = 0; j < testQueList.size(); j++) {
                                        if (testQueDetailsList.get(i).getQuestionId().equals(testQueList.get(j).getQuestionId())) {
                                            testQueList.get(j).setCorrectAnswer(testQueDetailsList.get(i).getCorrectAnswer());
                                            testQueList.get(j).setSelectedAnswer(testQueDetailsList.get(i).getSelectedAnswer());
                                            testQueList.get(j).setStyle(testQueDetailsList.get(i).getStyle());
                                            testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());
                                        }
                                    }
                                }
                                runOnUiThread(() -> calculatemarks());

                            } else {
                                runOnUiThread(() -> calculatemarks());
                            }
                        } else {
                            runOnUiThread(() -> calculatemarks());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(() -> utils.dismissDialog());
                }
            }
        });



    }

    /* INitialization */
    private void init() {
        testId = getIntent().getIntExtra("testId",0);
        studentId = getIntent().getIntExtra("studentId",0);
    }

    @OnClick(R.id.btn_review)
    public void onViewClicked() {

        Intent intent = new Intent(this, OTStudentOnlineTestReview.class);
        intent.putExtra("testName", getIntent().getStringExtra("testName"));
        intent.putExtra("testType", getIntent().getStringExtra("testType"));
        intent.putExtra("testId", getIntent().getIntExtra("testId",0));
        intent.putExtra("studentId", studentId);
        intent.putExtra("studentTestFilePath", getIntent().getStringExtra("studentTestFilePath"));
        intent.putExtra("testName", getIntent().getStringExtra("testName"));
        intent.putExtra("testFilePath", getIntent().getStringExtra("testFilePath"));

        startActivity(intent);

    }

    /* Drawing Pie Chart based on Test Result */
    private void makePieChart(int skippedAns, int wrongAns, int CorrectAns) {


        yData = new float[]{skippedAns, wrongAns, CorrectAns};
        xData = new String[]{"Skiped", "Correct", "Worng"};


        int totalGivenAnswer = skippedAns + wrongAns + CorrectAns;
        Log.v(TAG, "total ans - " + totalGivenAnswer);
        Log.v(TAG, "total ski - " + skippedAns);
        Log.v(TAG, "total wro - " + wrongAns);
        Log.v(TAG, "total corre - " + CorrectAns);


        Description description = new Description();
        description.setText("");

        piechart.setDescription(description);
        piechart.setNoDataTextColor(Color.WHITE);
        piechart.setRotationEnabled(true);
        //piechart.setUsePercentValues(true);
        piechart.setHoleColor(Color.TRANSPARENT);
        piechart.setCenterTextColor(Color.WHITE);
        piechart.setHoleRadius(60f);
        piechart.setTransparentCircleAlpha(0);
        piechart.animateXY(3000, 3000);
        piechart.setTouchEnabled(true);

        addDataSet();

        piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });


    }

    /* Preparing data to draw Pie Chart */
    private void addDataSet() {
        Log.v(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], i));
        }

        for (int i = 1; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(0);
        pieDataSet.setValueTextColor(Color.WHITE);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.skiped_ans)); //uans
        colors.add(getResources().getColor(R.color.wrong_ans));//wans
        colors.add(getResources().getColor(R.color.correct_ans)); //cans


        pieDataSet.setColors(colors);

        // add legend to chart
        Legend legend = piechart.getLegend();


        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        LegendEntry l3 = new LegendEntry("Skipped " + tunans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.skiped_ans));
        LegendEntry l2 = new LegendEntry("Wrong Answers " + twans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.wrong_ans));
        LegendEntry l1 = new LegendEntry("Correct Answers " + tcans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.correct_ans));


        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(5f);

        //legend.setCustom(new int[]{R.color.skiped_ans, R.color.wrong_ans, R.color.correct_ans}, new String[] { "Skipped ", "Wrong", "Correct"});
        legend.setCustom(new LegendEntry[]{l1, l2, l3});

        //piechart.getLegend().setEnabled(false);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);
        piechart.setExtraOffsets(-70, 0, 0, 0);
        piechart.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* Calculating Marks and Percentages */
    private void calculatemarks() {
        for (int i = 0; i < testQueList.size(); i++) {
            if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase("")) {
                tunans++;

            } else if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase(testQueList.get(i).getCorrectAnswer())) {
                tcans++;

            } else {
                twans++;

            }
        }

        Log.v(TAG, " tcans - " + tcans + " tuans - " + tunans + " twans - " + twans);

        tvTotalmarks.setText("" + tcans);
        totPercent = roundTwoDecimals(((double) (tcans) / (double) (testQueList.size())) * 100);
        tvTotalpercent.setText("" + (int) Math.ceil(totPercent));

        if (totPercent < 24) {
            tvRank.setText("F");
        } else if (totPercent > 23 && totPercent < 41) {
            tvRank.setText("C");
        } else if (totPercent > 40 && totPercent < 61) {
            tvRank.setText("B");
        } else if (totPercent > 60 && totPercent < 71) {
            tvRank.setText("B+");
        } else if (totPercent > 70 && totPercent < 91) {
            tvRank.setText("A");
        } else if (totPercent > 90) {
            tvRank.setText("A+");
        }

        tvCorrectMarks.setText("" + tcans);
        tvWrongMarks.setText("" + twans);
        tvUnansweredMarks.setText("" + tunans);

        makePieChart(tunans, twans, tcans);

    }

    /* Rounding Two Decimals places for double values */
    private double roundTwoDecimals(double tqe) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(tqe));
    }
}
