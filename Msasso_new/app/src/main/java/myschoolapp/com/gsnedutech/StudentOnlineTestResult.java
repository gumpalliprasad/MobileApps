/*
 * *
 *  * Created by SriRamaMurthy A on 1/10/19 4:53 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 30/9/19 6:18 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.SubScoreCard;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static myschoolapp.com.gsnedutech.Util.MyUtils.roundTwoDecimals;


public class StudentOnlineTestResult extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = StudentOnlineTestResult.class.getName();

    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    @BindView(R.id.tv_testName)
    TextView tvTestName;
    @BindView(R.id.tv_stdDetails)
    TextView tvStdDetails;
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
    @BindView(R.id.tv_grace)
    TextView tvGrace;
    @BindView(R.id.rv_subscores)
    RecyclerView rvSubScores;

    String studentId, fileCorrectMarks, fileWrongMarks;

    List<String> subList;
    ArrayList<SubScoreCard> subScoreCards = new ArrayList<>();


    int tunans = 0, tcans = 0, twans = 0, tgans = 0, tcMarks = 0, twMarks = 0, testMarks = 0, tgMarks = 0;
    int pCMarks = 0, pWMarks = 0;
    double totPercent;

    private float[] yData = null;
    private String[] xData = null;

    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();

    StudentOnlineTestObj studentTestObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_online_test_result);
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
            utils.alertDialog(1, StudentOnlineTestResult.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                tunans = 0;
                tcans = 0;
                twans = 0;
                tgans = 0;
                tcMarks = 0;
                twMarks = 0;
                testMarks = 0;
                tgMarks = 0;
                pCMarks = 0;
                pWMarks = 0;
                totPercent = 0;
                if (getIntent().hasExtra("studentTest")) {
                    getOnlineTestQuePaper(studentTestObj.getTestFilePath());
//                    getOnlineTestQueDetails(studentTestObj.getStudentTestFilePath());
                } else {
                    getOnlineTestQuePaper(getIntent().getStringExtra("TestFilePath"));
//                    getOnlineTestQueDetails(getIntent().getStringExtra("studentTestFilePath"));
                }
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
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            tvStdDetails.setText("" + sObj.getStudentName() + "\u2022 " + sObj.getLoginId());
        }

        if (getIntent().hasExtra("studentTest")) {
            studentTestObj = (StudentOnlineTestObj) getIntent().getSerializableExtra("studentTest");
            tvTestName.setText("" + studentTestObj.getTestName());
        }

        studentId = getIntent().getStringExtra("studentId");

        rvSubScores.setLayoutManager(new LinearLayoutManager(StudentOnlineTestResult.this));


    }

    @OnClick(R.id.ll_review)
    public void onViewClicked() {

        Intent intent = new Intent(this, StudentOnlineTestReview.class);
        if (getIntent().hasExtra("studentTest"))
            intent.putExtra("studentTest", studentTestObj);
        else {
            intent.putExtra("testName", getIntent().getStringExtra("testName"));
            intent.putExtra("TestFilePath", getIntent().getStringExtra("TestFilePath"));
            intent.putExtra("studentTestFilePath", getIntent().getStringExtra("studentTestFilePath"));
        }
        intent.putExtra("studentId", studentId);

        startActivity(intent);

    }

    private void makePieChart(int skippedAns, int wrongAns, int CorrectAns, int GraceCount) {


        yData = new float[]{skippedAns, wrongAns, CorrectAns, GraceCount};
        xData = new String[]{"Skiped", "Correct", "Worng", "Grace"};


        int totalGivenAnswer = skippedAns + wrongAns + CorrectAns;
        Log.v(TAG, "total ans - " + totalGivenAnswer);
        Log.v(TAG, "total ski - " + skippedAns);
        Log.v(TAG, "total wro - " + wrongAns);
        Log.v(TAG, "total corre - " + CorrectAns);
        Log.v(TAG, "total grace - " + GraceCount);

        Description description = new Description();
        description.setText("");

        piechart.setDescription(description);
        piechart.setNoDataTextColor(Color.WHITE);
        piechart.setRotationEnabled(true);
        piechart.setUsePercentValues(true);
        piechart.setCenterTextColor(Color.WHITE);
        piechart.setHoleRadius(0f);
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
        colors.add(getResources().getColor(R.color.skiped_ans, null)); //uans
        colors.add(getResources().getColor(R.color.wrong_ans, null));//wans
        colors.add(getResources().getColor(R.color.correct_ans, null)); //cans
        colors.add(getResources().getColor(R.color.grace_ans, null)); //cans


        pieDataSet.setColors(colors);

        // add legend to chart
        Legend legend = piechart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);


        LegendEntry l4 = new LegendEntry("Grace " + tgans, Legend.LegendForm.DEFAULT, 10f, 2f, null, getResources().getColor(R.color.grace_ans));
        LegendEntry l3 = new LegendEntry("Skipped " + tunans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.skiped_ans));
        LegendEntry l2 = new LegendEntry("Wrong Answers " + twans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.wrong_ans));
        LegendEntry l1 = new LegendEntry("Correct Answers " + tcans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.correct_ans));


        legend.setTextSize(12f);
        legend.setTextColor(Color.WHITE);
        legend.setXEntrySpace(10f); // set the space between the legend entries on the x-axis
        legend.setYEntrySpace(10f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);

        //legend.setCustom(new int[]{R.color.skiped_ans, R.color.wrong_ans, R.color.correct_ans}, new String[] { "Skipped ", "Wrong", "Correct"});
        legend.setCustom(new LegendEntry[]{l1, l2, l3, l4});
//        legend.setCustom(new LegendEntry[]{l1, l2, l3});

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

    void getOnlineTestQuePaper(String... strings) {
        utils.showLoader(StudentOnlineTestResult.this);

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
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesDetialsList responce - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");
                            testQueList.clear();
                            testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            utils.showLog(TAG, "testQueList - " + testQueList.size());
                            if (getIntent().hasExtra("studentTest"))
                                if(studentTestObj.getStudentTestFilePath().equalsIgnoreCase("NA") ||
                                        studentTestObj.getStudentTestFilePath().equalsIgnoreCase("UNDEFINED") ||
                                        studentTestObj.getStudentTestFilePath().isEmpty()) {
                                    getOnlineTestQueDetailsDB(studentTestObj.getTestId(), studentTestObj.getStudentId());
                                }else{
                                    getOnlineTestQueDetails(studentTestObj.getStudentTestFilePath());
                                }
                            else
                                getOnlineTestQueDetails(getIntent().getStringExtra("studentTestFilePath"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }

    void getOnlineTestQueDetails(String... strings) {
        utils.showLoader(StudentOnlineTestResult.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "TestGetQue Url - " + strings[0]);

        Request request = new Request.Builder()
                .url(strings[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {


                if (NetworkConnectivity.isConnected(StudentOnlineTestResult.this)) {
                    getOnlineTestQueDetailsDB(studentTestObj.getTestId(), studentTestObj.getStudentId());
                } else {
                    runOnUiThread(() -> utils.dismissDialog());
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesPaperList responce - " + jsonResp);

                    try {

                        JSONObject jsonObject = new JSONObject(jsonResp);
                        JSONArray jsonArr = jsonObject.getJSONArray("TestCategories");

//                        testQueList.clear();
//                        testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {}.getType()));
                        testQueDetailsList.clear();
                        testQueDetailsList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                        }.getType()));

                        for (int i = 0; i < testQueDetailsList.size(); i++) {
                            for (int j = 0; j < testQueList.size(); j++) {
                                utils.showLog(TAG, "" + testQueDetailsList.get(i).getQuestionId());
                                utils.showLog(TAG, "" + testQueList.get(j).getQuestionId());
                                if (testQueDetailsList.get(i).getQuestType().equalsIgnoreCase("CPQ")) {
                                    utils.showLog(TAG, "" + testQueList.get(j).getQuestionMapId());
                                    if (testQueDetailsList.get(i).getQuestionMapId().equals(testQueList.get(j).getQuestionMapId())) {
                                        testQueList.get(j).setCorrectMarks(testQueDetailsList.get(i).getCorrectMarks());
                                        testQueList.get(j).setWrongMarks(testQueDetailsList.get(i).getWrongMarks());
                                        testQueList.get(j).setSelectedAnswer(testQueDetailsList.get(i).getSelectedAnswer());
                                        testQueList.get(j).setStyle(testQueDetailsList.get(i).getStyle());
                                        testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());

                                        testQueList.get(j).setParagraphId(testQueDetailsList.get(i).getParagraphId());
                                        testQueList.get(j).setPartialMarksAvailable(testQueDetailsList.get(i).getPartialMarksAvailable());
                                        testQueList.get(j).setPartialCorrectMarks(testQueDetailsList.get(i).getPartialCorrectMarks());
                                        testQueList.get(j).setPartialWrongMarks(testQueDetailsList.get(i).getPartialWrongMarks());
                                        testQueList.get(j).setIsOptionMarks(testQueDetailsList.get(i).getIsOptionMarks());
                                        testQueList.get(j).setMarksUnattempted(testQueDetailsList.get(i).getMarksUnattempted());
                                    }
                                } else {
                                    if (testQueDetailsList.get(i).getQuestionId().equals(testQueList.get(j).getQuestionId())) {
                                        testQueList.get(j).setCorrectMarks(testQueDetailsList.get(i).getCorrectMarks());
                                        testQueList.get(j).setWrongMarks(testQueDetailsList.get(i).getWrongMarks());
                                        testQueList.get(j).setSelectedAnswer(testQueDetailsList.get(i).getSelectedAnswer());
                                        testQueList.get(j).setStyle(testQueDetailsList.get(i).getStyle());
                                        testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());

                                        testQueList.get(j).setParagraphId(testQueDetailsList.get(i).getParagraphId());
                                        testQueList.get(j).setPartialMarksAvailable(testQueDetailsList.get(i).getPartialMarksAvailable());
                                        testQueList.get(j).setPartialCorrectMarks(testQueDetailsList.get(i).getPartialCorrectMarks());
                                        testQueList.get(j).setPartialWrongMarks(testQueDetailsList.get(i).getPartialWrongMarks());
                                        testQueList.get(j).setIsOptionMarks(testQueDetailsList.get(i).getIsOptionMarks());
                                        testQueList.get(j).setMarksUnattempted(testQueDetailsList.get(i).getMarksUnattempted());
                                    }
                                }

                            }
                        }

                        utils.showLog(TAG, "testQuePaperList - " + testQueList.size());
                        fileCorrectMarks = jsonObject.getString("correctMarks");
                        fileWrongMarks = jsonObject.getString("wrongMarks");
//                        runOnUiThread(() -> calculateQueListmarks());
                        runOnUiThread(() -> calculateMarks());


                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (NetworkConnectivity.isConnected(StudentOnlineTestResult.this)) {
                            getOnlineTestQueDetailsDB(studentTestObj.getTestId(), studentTestObj.getStudentId());
                        } else {
                            runOnUiThread(() -> utils.dismissDialog());
                        }
                    }
                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });

    }

    void getOnlineTestQueDetailsDB(String... strings) {
        utils.showLoader(StudentOnlineTestResult.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        utils.showLog(TAG, "TestGetQue Url testID - " + studentTestObj.getTestId());
        utils.showLog(TAG, "TestGetQue Url studentID - " + studentTestObj.getStudentId());
        utils.showLog(TAG, "TestGetQue Url schema - " + sh_Pref.getString("schema", ""));
        utils.showLog(TAG, "TestGetQue Url  - " + AppUrls.GetExamResultByStudentId + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentTestObj.getStudentId() + "&testId=" + studentTestObj.getTestId());

        Request request = new Request.Builder()
                .url(AppUrls.GetExamResultByStudentId + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentTestObj.getStudentId() + "&testId=" + studentTestObj.getTestId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesPaperList responce - " + jsonResp);

                    try {

                        JSONObject jsonObject = new JSONObject(jsonResp);
                        JSONArray jsonArr = jsonObject.getJSONObject("result").getJSONArray("testCategories");
                        testQueList.clear();
                        testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                        }.getType()));

                        utils.showLog(TAG, "testQuePaperList - " + testQueList.size());
//                        runOnUiThread(() -> calculateQueListmarks());
                        runOnUiThread(() -> calculateMarks());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });

    }

    public static String removeTrailingLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        str = str.indexOf(".") < 0 ? str : str.replaceAll("0*$", "").replaceAll("\\.$", "");
        if (str.equalsIgnoreCase(""))
            str = "0";
        return str;
    }

    private void calculateMarks() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < testQueList.size(); i++) {
            hs.add(testQueList.get(i).getSubjectGroup());
        }

        subList = new ArrayList<String>(hs);

        for (int i = 0; i < subList.size(); i++) {
            int corAns = 0, wrgAns = 0, unAns = 0, gAns = 0;
            int cMarks = 0, wMarks = 0, gMarks = 0;

            ArrayList<OnlineQuestionObj2> sub_quelist = new ArrayList<>();
            for (int j = 0; j < testQueList.size(); j++) {
                if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                    sub_quelist.add(testQueList.get(j));
                }
            }

            Log.v(TAG, "subList - " + subList.get(i) + " - " + sub_quelist.size());

            for (int k = 0; k < sub_quelist.size(); k++) {
                if (sub_quelist.get(k).getIsGrace() == 1) {
                    gAns = gAns + 1;
                    gMarks = gMarks + sub_quelist.get(k).getCorrectMarks();
                } else {
                    if (sub_quelist.get(k).getCorrectMarks() > 0)
                        testMarks = testMarks + sub_quelist.get(k).getCorrectMarks();
                    else
                        testMarks = testMarks + Integer.parseInt(fileCorrectMarks);

                    if (sub_quelist.get(k).getSelectedAnswer().equalsIgnoreCase("")) {
                        unAns++;
                    } else {
                        if (sub_quelist.get(k).getQuestType().equalsIgnoreCase("MAQ")) {
                            if (sub_quelist.get(k).getPartialMarksAvailable().equalsIgnoreCase("y")) {
                                if (sub_quelist.get(k).getSelectedAnswer().equalsIgnoreCase(sub_quelist.get(k).getCorrectAnswer())) {
                                    corAns++;
                                    if (sub_quelist.get(k).getCorrectMarks() > 0)
                                        cMarks = cMarks + sub_quelist.get(k).getCorrectMarks();
                                    else
                                        cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                                } else {
                                    pCMarks = 0;
                                    pWMarks = 0;
                                    String[] Sarray = sub_quelist.get(k).getSelectedAnswer().split(",");
                                    String[] Carray = sub_quelist.get(k).getCorrectAnswer().split(",");

                                    List<String> list1 = new ArrayList<String>();
                                    Collections.addAll(list1, Carray);

                                    for (int j = 0; j < Sarray.length; j++) {
                                        if (list1.contains(Sarray[j])) {
                                            pCMarks++;
                                        } else {
                                            pWMarks++;
                                        }
                                    }

                                    if (sub_quelist.get(k).getIsOptionMarks().equalsIgnoreCase("0")) {
                                        if (pWMarks > 0) {
                                            wMarks = wMarks + Integer.parseInt(sub_quelist.get(k).getWrongMarks());
                                        } else {
                                            cMarks = cMarks + (pCMarks * Integer.parseInt(sub_quelist.get(k).getPartialCorrectMarks()));
                                        }
                                    } else {
                                        cMarks = cMarks + (pCMarks * Integer.parseInt(sub_quelist.get(k).getPartialCorrectMarks()));
                                        wMarks = wMarks + (pWMarks * Integer.parseInt(sub_quelist.get(k).getPartialWrongMarks()));
                                    }

                                }

                            } else {
                                if (removeTrailingLeadingZeroes(sub_quelist.get(k).getSelectedAnswer())
                                        .equalsIgnoreCase(removeTrailingLeadingZeroes(sub_quelist.get(k).getCorrectAnswer()))) {
                                    corAns++;
                                    if (sub_quelist.get(k).getCorrectMarks() > 0)
                                        cMarks = cMarks + sub_quelist.get(k).getCorrectMarks();
                                    else
                                        cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                                } else {
                                    wrgAns++;
                                    if (!sub_quelist.get(k).getWrongMarks().equalsIgnoreCase(""))
                                        wMarks = wMarks + Integer.parseInt(sub_quelist.get(k).getWrongMarks());
                                    else
                                        wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                                }
                            }
                        } else if (sub_quelist.get(k).getQuestType().equalsIgnoreCase("MFQ")) {
                            if (sub_quelist.get(k).getPartialMarksAvailable().equalsIgnoreCase("y")) {
                                if (sub_quelist.get(k).getSelectedAnswer().equalsIgnoreCase(sub_quelist.get(k).getCorrectAnswer())) {
                                    corAns++;
                                    if (sub_quelist.get(k).getCorrectMarks() > 0)
                                        cMarks = cMarks + sub_quelist.get(k).getCorrectMarks();
                                    else
                                        cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                                } else {
                                    pCMarks = 0;
                                    pWMarks = 0;
                                    String[] Sarray = sub_quelist.get(k).getSelectedAnswer().split(",");
                                    String[] Carray = sub_quelist.get(k).getCorrectAnswer().split(",");

                                    for (int j = 0; j < Sarray.length; j++) {
                                        if (Sarray[j].length() > 2) {
                                            if (Carray[j].equalsIgnoreCase(Sarray[j])) {
                                                pCMarks++;
                                            } else {
                                                pWMarks++;
                                            }
                                        }
                                    }

                                    if (sub_quelist.get(k).getIsOptionMarks().equalsIgnoreCase("0")) {
                                        if (pWMarks > 0) {
                                            wMarks = wMarks + Integer.parseInt(sub_quelist.get(k).getWrongMarks());
                                        } else {
                                            cMarks = cMarks + (pCMarks * Integer.parseInt(sub_quelist.get(k).getPartialCorrectMarks()));
                                        }
                                    } else {
                                        cMarks = cMarks + (pCMarks * Integer.parseInt(sub_quelist.get(k).getPartialCorrectMarks()));
                                        wMarks = wMarks + (pWMarks * Integer.parseInt(sub_quelist.get(k).getPartialWrongMarks()));

                                    }

                                }

                            } else {
                                if (removeTrailingLeadingZeroes(sub_quelist.get(k).getSelectedAnswer())
                                        .equalsIgnoreCase(removeTrailingLeadingZeroes(sub_quelist.get(k).getCorrectAnswer()))) {
                                    corAns++;
                                    if (sub_quelist.get(k).getCorrectMarks() > 0)
                                        cMarks = cMarks + sub_quelist.get(k).getCorrectMarks();
                                    else
                                        cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                                } else {
                                    wrgAns++;
                                    if (!sub_quelist.get(k).getWrongMarks().equalsIgnoreCase(""))
                                        wMarks = wMarks + Integer.parseInt(sub_quelist.get(k).getWrongMarks());
                                    else
                                        wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                                }
                            }
                        } else if (sub_quelist.get(k).getQuestType().equalsIgnoreCase("MCQ")
                                || sub_quelist.get(k).getQuestType().equalsIgnoreCase("CPQ")) {
                            if (sub_quelist.get(k).getCorrectAnswer().contains(sub_quelist.get(k).getSelectedAnswer())) {
                                corAns++;
                                if (sub_quelist.get(k).getCorrectMarks() > 0)
                                    cMarks = cMarks + sub_quelist.get(k).getCorrectMarks();
                                else
                                    cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                            } else {
                                wrgAns++;
                                if (!sub_quelist.get(k).getWrongMarks().equalsIgnoreCase(""))
                                    wMarks = wMarks + Integer.parseInt(sub_quelist.get(k).getWrongMarks());
                                else
                                    wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                            }
                        } else {
                            if (removeTrailingLeadingZeroes(sub_quelist.get(k).getSelectedAnswer())
                                    .equalsIgnoreCase(removeTrailingLeadingZeroes(sub_quelist.get(k).getCorrectAnswer()))) {
                                corAns++;
                                if (sub_quelist.get(k).getCorrectMarks() > 0)
                                    cMarks = cMarks + sub_quelist.get(k).getCorrectMarks();
                                else
                                    cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                            } else {
                                wrgAns++;
                                if (!sub_quelist.get(k).getWrongMarks().equalsIgnoreCase(""))
                                    wMarks = wMarks + Integer.parseInt(sub_quelist.get(k).getWrongMarks());
                                else
                                    wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                            }
                        }
                    }
                }
                Log.v(TAG, "subList - " + (k + 1) + " - " + cMarks);
                Log.v(TAG, "subList - " + (k + 1) + " - " + +wMarks);

                Log.v(TAG, "-----------------");
            }


//                Log.v(TAG, "subList - " + subList.get(i) + " - " + cMarks);
//                Log.v(TAG, "subList - " + subList.get(i) + " - " + wMarks);

            SubScoreCard subScoreCard = new SubScoreCard();
            subScoreCard.setSubName(subList.get(i));
            subScoreCard.setCorAns(corAns);
            subScoreCard.setWrgAns(wrgAns);
            subScoreCard.setUnAns(unAns);
            subScoreCard.setgAns(gAns);

            subScoreCard.setcMarks(cMarks);
            subScoreCard.setwMarks(wMarks);
            subScoreCard.setgMarks(gMarks);
            subScoreCards.add(subScoreCard);
        }

        for (int i = 0; i < subScoreCards.size(); i++) {
            tcMarks = tcMarks + subScoreCards.get(i).getcMarks();
            twMarks = twMarks + subScoreCards.get(i).getwMarks();
            tgMarks = tgMarks + subScoreCards.get(i).getgMarks();

            tcans = tcans + subScoreCards.get(i).getCorAns();
            twans = twans + subScoreCards.get(i).getWrgAns();
            tunans = tunans + subScoreCards.get(i).getUnAns();
        }


        Log.v(TAG, " tcans - " + tcans + " tuans - " + tunans + " twans - " + twans + " tcMarks - " + tcMarks + " twMarks - " + twMarks + " testMarks - " + testMarks);

        tvTotalmarks.setText("" + ((tcMarks - twMarks) + tgMarks));
        totPercent = roundTwoDecimals(((double) (tcMarks - twMarks + tgMarks) / (double) (testMarks) * 100));
        if (totPercent > 0)
            tvTotalpercent.setText("" + (int) Math.ceil(totPercent));
        else
            tvTotalpercent.setText("0");

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
        tvGrace.setText("" + tgans);

        makePieChart(tunans, twans, tcans, tgans);
        rvSubScores.setAdapter(new ScoreCardAdapter());

    }

    private void calculateQueListmarks() {

        int corAns = 0, wrgAns = 0, unAns = 0, gAns = 0;
        int cMarks = 0, wMarks = 0, gMarks = 0;


        for (int i = 0; i < testQueList.size(); i++) {
            if (testQueList.get(i).getCorrectMarks() > 0) {
                testMarks = testMarks + testQueList.get(i).getCorrectMarks();
            } else
                testMarks = testMarks + Integer.parseInt(fileCorrectMarks);

            if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase("")) {
                unAns++;
            } else {
                if (testQueList.get(i).getQuestType().equalsIgnoreCase("MAQ")) {
                    if (testQueList.get(i).getPartialMarksAvailable().equalsIgnoreCase("y")) {
                        if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase(testQueList.get(i).getCorrectAnswer())) {
                            corAns++;
                            if (testQueList.get(i).getCorrectMarks() > 0) {
                                cMarks = cMarks + testQueList.get(i).getCorrectMarks();
                                testQueList.get(i).setMarks(testQueList.get(i).getCorrectMarks());
                            } else
                                cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                        } else {
                            pCMarks = 0;
                            pWMarks = 0;
                            String[] Sarray = testQueList.get(i).getSelectedAnswer().split(",");
                            String[] Carray = testQueList.get(i).getCorrectAnswer().split(",");

                            List<String> list1 = new ArrayList<String>();
                            Collections.addAll(list1, Carray);

                            for (int j = 0; j < Sarray.length; j++) {
                                if (list1.contains(Sarray[j])) {
                                    pCMarks++;
                                } else {
                                    pWMarks++;
                                }
                            }


                            if (testQueList.get(i).getIsOptionMarks().equalsIgnoreCase("0")) {
                                if (pWMarks > 0) {
                                    wMarks = wMarks + Integer.parseInt(testQueList.get(i).getWrongMarks());
                                } else {
                                    cMarks = cMarks + (pCMarks * Integer.parseInt(testQueList.get(i).getPartialCorrectMarks()));
                                    testQueList.get(i).setMarks(cMarks);
                                }
                            } else {
                                cMarks = cMarks + (pCMarks * Integer.parseInt(testQueList.get(i).getPartialCorrectMarks()));
                                wMarks = wMarks + (pWMarks * Integer.parseInt(testQueList.get(i).getPartialWrongMarks()));
                            }

                        }

                    } else {
                        if (removeTrailingLeadingZeroes(testQueList.get(i).getSelectedAnswer())
                                .equalsIgnoreCase(removeTrailingLeadingZeroes(testQueList.get(i).getCorrectAnswer()))) {
                            corAns++;
                            if (testQueList.get(i).getCorrectMarks() > 0) {
                                cMarks = cMarks + testQueList.get(i).getCorrectMarks();
                                testQueList.get(i).setMarks(testQueList.get(i).getCorrectMarks());
                            } else
                                cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                        } else {
                            wrgAns++;
                            if (!testQueList.get(i).getWrongMarks().equalsIgnoreCase(""))
                                wMarks = wMarks + Integer.parseInt(testQueList.get(i).getWrongMarks());
                            else
                                wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                        }
                    }
                } else if (testQueList.get(i).getQuestType().equalsIgnoreCase("MFQ")) {
                    if (testQueList.get(i).getPartialMarksAvailable().equalsIgnoreCase("y")) {
                        if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase(testQueList.get(i).getCorrectAnswer())) {
                            corAns++;
                            if (testQueList.get(i).getCorrectMarks() > 0) {
                                cMarks = cMarks + testQueList.get(i).getCorrectMarks();
                                testQueList.get(i).setMarks(testQueList.get(i).getCorrectMarks());
                            } else
                                cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                        } else {
                            pCMarks = 0;
                            pWMarks = 0;
                            String[] Sarray = testQueList.get(i).getSelectedAnswer().split(",");
                            String[] Carray = testQueList.get(i).getCorrectAnswer().split(",");

                            for (int j = 0; j < Sarray.length; j++) {
                                if (Sarray[j].length() > 2) {
                                    if (Carray[j].equalsIgnoreCase(Sarray[j])) {
                                        pCMarks++;
                                    } else {
                                        pWMarks++;
                                    }
                                }
                            }

                            if (testQueList.get(i).getIsOptionMarks().equalsIgnoreCase("0")) {
                                if (pWMarks > 0) {
                                    wMarks = wMarks + Integer.parseInt(testQueList.get(i).getWrongMarks());
                                } else {
                                    cMarks = cMarks + (pCMarks * Integer.parseInt(testQueList.get(i).getPartialCorrectMarks()));
                                    testQueList.get(i).setMarks(cMarks);
                                }
                            } else {
                                cMarks = cMarks + (pCMarks * Integer.parseInt(testQueList.get(i).getPartialCorrectMarks()));
                                wMarks = wMarks + (pWMarks * Integer.parseInt(testQueList.get(i).getPartialWrongMarks()));
                            }

                        }

                    } else {
                        if (removeTrailingLeadingZeroes(testQueList.get(i).getSelectedAnswer())
                                .equalsIgnoreCase(removeTrailingLeadingZeroes(testQueList.get(i).getCorrectAnswer()))) {
                            corAns++;
                            if (testQueList.get(i).getCorrectMarks() > 0) {
                                cMarks = cMarks + testQueList.get(i).getCorrectMarks();
                                testQueList.get(i).setMarks(testQueList.get(i).getCorrectMarks());
                            } else
                                cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                        } else {
                            wrgAns++;
                            if (!testQueList.get(i).getWrongMarks().equalsIgnoreCase(""))
                                wMarks = wMarks + Integer.parseInt(testQueList.get(i).getWrongMarks());
                            else
                                wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                        }
                    }
                } else {
                    if (removeTrailingLeadingZeroes(testQueList.get(i).getSelectedAnswer())
                            .equalsIgnoreCase(removeTrailingLeadingZeroes(testQueList.get(i).getCorrectAnswer()))) {
                        corAns++;
                        if (testQueList.get(i).getCorrectMarks() > 0) {
                            cMarks = cMarks + testQueList.get(i).getCorrectMarks();
                            testQueList.get(i).setMarks(testQueList.get(i).getCorrectMarks());
                        } else
                            cMarks = cMarks + Integer.parseInt(fileCorrectMarks);
                    } else {
                        wrgAns++;
                        if (!testQueList.get(i).getWrongMarks().equalsIgnoreCase(""))
                            wMarks = wMarks + Integer.parseInt(testQueList.get(i).getWrongMarks());
                        else
                            wMarks = wMarks + Integer.parseInt(fileWrongMarks);
                    }
                }
            }
        }

        tcMarks = cMarks;
        twMarks = wMarks;
        tgMarks = gMarks;

        tcans = corAns;
        twans = wrgAns;
        tunans = unAns;


        Log.v(TAG, " tcans - " + tcans + " tuans - " + tunans + " twans - " + twans + " tcMarks - " + tcMarks + " twMarks - " + twMarks + " testMarks - " + testMarks);

        tvTotalmarks.setText("" + ((tcMarks - twMarks) + tgMarks));
        totPercent =

                roundTwoDecimals(((double) (tcMarks - twMarks + tgMarks) / (double) (testMarks) * 100));
        if (totPercent > 0)
            tvTotalpercent.setText("" + (int) Math.ceil(totPercent));
        else
            tvTotalpercent.setText("0");

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
//        tvGrace.setText("" + tgans);

//        Gson gson = new Gson();
//        Type type = new TypeToken<ArrayList<OnlineQuestionObj2>>() {
//        }.getType();
//
//        String json = gson.toJson(testQueList, type);
//        Log.v(TAG, "json - " + json);

        makePieChart(tunans, twans, tcans, tgans);
        rvSubScores.setAdapter(new ScoreCardAdapter());
    }

    class ScoreCardAdapter extends RecyclerView.Adapter<ScoreCardAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(StudentOnlineTestResult.this).inflate(R.layout.item_sub_scorecard, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSubName.setText(subScoreCards.get(position).getSubName());
            holder.tvCorrectAns.setText("" + subScoreCards.get(position).getCorAns());
            holder.tvWrongAns.setText("" + subScoreCards.get(position).getWrgAns());
            holder.tvUnAns.setText("" + subScoreCards.get(position).getUnAns());
        }

        @Override
        public int getItemCount() {
            return subScoreCards.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            TextView tvSubName, tvCorrectAns, tvWrongAns, tvUnAns;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubName = itemView.findViewById(R.id.tv_subName);
                tvCorrectAns = itemView.findViewById(R.id.tv_correctanswer);
                tvWrongAns = itemView.findViewById(R.id.tv_wronganswer);
                tvUnAns = itemView.findViewById(R.id.tv_unanswered);
            }
        }
    }
}
