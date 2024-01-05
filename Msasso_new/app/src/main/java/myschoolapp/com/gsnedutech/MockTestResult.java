/*
 * *
 *  * Created by SriRamaMurthy A on 16/9/19 2:15 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 16/9/19 12:35 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.MockQuestionObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.SubScoreCard;
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

public class MockTestResult extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + MockTestResult.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    List<MockQuestionObj> testQueList = new ArrayList<>();

    int tunans = 0, tcans = 0, twans = 0;
    double totPercent;
    private float[] yData = null;
    private String[] xData = null;

    @BindView(R.id.tv_totalmarks)
    TextView tvTotalmarks;
    @BindView(R.id.tv_totalpercent)
    TextView tvTotalpercent;
    @BindView(R.id.tv_rank)
    TextView tvRank;

    @BindView(R.id.tv_correct_marks)
    TextView tvCorrectMarks;
    @BindView(R.id.tv_wrong_marks)
    TextView tvWrongMarks;
    @BindView(R.id.tv_unanswered_marks)
    TextView tvUnansweredMarks;
    @BindView(R.id.ll_notclassresults)
    LinearLayout llNotclassresults;
    @BindView(R.id.idPieChart)
    PieChart piechart;

    @BindView(R.id.rv_subscores)
    RecyclerView rvSubScores;

    @BindView(R.id.ic_close)
    ImageView icClose;

    List<String> subList;

    ArrayList<SubScoreCard> subScoreCards = new ArrayList<>();
    ArrayList<MockQuestionObj> mockQuestionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mock_test_result);
        ButterKnife.bind(this);

        init();

        createsubjectlis();

        rvSubScores.setAdapter(new ScoreCardAdapter());

        calculatemarks();


        Intent testResultIntent = getIntent();
        int totalTimeSpent = (int) (Double.parseDouble(testResultIntent.getStringExtra("timeSpent")) / 60000);
        mockQuestionList.addAll((Collection<? extends MockQuestionObj>) getIntent().getSerializableExtra("questions"));
        postResult(testResultIntent.getStringExtra("testType"), testResultIntent.getStringExtra("courseId"),
                testResultIntent.getStringExtra("classId"), testResultIntent.getStringExtra("subjectId"),
                testResultIntent.getStringExtra("chapterId"), testResultIntent.getStringExtra("topicId"),
                testResultIntent.getStringExtra("totalQuestions"), "" + totalTimeSpent,
                testResultIntent.getStringExtra(AppConst.TopicCCMapId), testResultIntent.getStringExtra(AppConst.ChapterCCMapId),
                testResultIntent.getStringExtra("skipperQuestions"), mockQuestionList);
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
            utils.alertDialog(1, MockTestResult.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    private void createsubjectlis() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < testQueList.size(); i++) {
            hs.add(testQueList.get(i).getSubjectGroup());
//            utils.showLog(TAG, "quesid - " + testQueList.get(i).getQuestionId());
        }


        subList = new ArrayList<String>(hs);

        for (int i = 0; i < subList.size(); i++) {
            int corAns = 0, wrgAns = 0, unAns = 0;
            ArrayList<MockQuestionObj> sub_quelist = new ArrayList<>();
            for (int j = 0; j < testQueList.size(); j++) {
                if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                    sub_quelist.add(testQueList.get(j));
                }
            }
            for (int j = 0; j < sub_quelist.size(); j++) {
                if (sub_quelist.get(j).getSelectedAnswer().equalsIgnoreCase("blank") || allCharactersComma(sub_quelist.get(j).getSelectedAnswer()) || allCharactersSame(sub_quelist.get(j).getSelectedAnswer()) || sub_quelist.get(j).getSelectedAnswer().equalsIgnoreCase("")) {
                    unAns++;

                } else if (sub_quelist.get(j).getSelectedAnswer().equalsIgnoreCase(sub_quelist.get(j).getCorrectAnswer())) {
                    corAns++;

                } else {
                    wrgAns++;

                }
            }
            SubScoreCard subScoreCard = new SubScoreCard();
            subScoreCard.setSubName(subList.get(i));
            subScoreCard.setCorAns(corAns);
            subScoreCard.setWrgAns(wrgAns);
            subScoreCard.setUnAns(unAns);
            subScoreCards.add(subScoreCard);
        }
    }

    boolean allCharactersComma(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++)
            if (s.charAt(i) != ',')
                return false;

        return true;
    }

    boolean allCharactersSame(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++)
            if (s.charAt(i) != 'X')
                return false;

        return true;
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

//        testreResultIntent.putExtra("testType", testType);
//        testreResultIntent.putExtra("courseId", courseId);
//        testreResultIntent.putExtra("totalQuestions", ""+testQueList.size());
//        testreResultIntent.putExtra("timeSpent", ""+testQueList.size());


        rvSubScores.setLayoutManager(new LinearLayoutManager(MockTestResult.this));
        testQueList = getResultArray(getString(R.string.path_testjson));
        utils.showLog(TAG, "explanatin size - " + testQueList.size());
        icClose.setOnClickListener(view -> onBackPressed());

    }

    private void calculatemarks() {

        int correctMarks = 0,totalMarks = 0;

        for (int i = 0; i < testQueList.size(); i++) {

            totalMarks = totalMarks+Integer.parseInt(testQueList.get(i).getCorrectMarks());

            if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase("blank") || allCharactersComma(testQueList.get(i).getSelectedAnswer()) || allCharactersSame(testQueList.get(i).getSelectedAnswer()) || testQueList.get(i).getSelectedAnswer().equalsIgnoreCase("")) {
                tunans++;
            } else if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase(testQueList.get(i).getCorrectAnswer())) {
                tcans++;
                correctMarks = correctMarks + (1*Integer.parseInt(testQueList.get(i).getCorrectMarks()));
            } else {
                twans++;
                correctMarks = correctMarks - (1*Integer.parseInt(testQueList.get(i).getWrongMarks()));
            }
        }

        utils.showLog(TAG, " tcans - " + tcans + " tuans - " + tunans + " twans - " + twans);

        if (correctMarks<0){
            correctMarks = 0;
        }

        tvTotalmarks.setText("" + correctMarks);
        totPercent = roundTwoDecimals(100*(double) (correctMarks) / (double) (totalMarks));
        tvTotalpercent.setText("" + totPercent);

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

    private double roundTwoDecimals(double tqe) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(tqe));
    }

    private void makePieChart(int skippedAns, int wrongAns, int CorrectAns) {


        yData = new float[]{skippedAns, wrongAns, CorrectAns};
        xData = new String[]{"Skiped", "Correct", "Worng"};


        int totalGivenAnswer = skippedAns + wrongAns + CorrectAns;
        utils.showLog(TAG, "total ans - " + totalGivenAnswer);
        utils.showLog(TAG, "total ski - " + skippedAns);
        utils.showLog(TAG, "total wro - " + wrongAns);
        utils.showLog(TAG, "total corre - " + CorrectAns);


//        int percentage = (CorrectAns*4- * 100 / totalGivenAnswer);
//
//        utils.showLog(TAG, "total perce - " + percentage);

        Description description = new Description();
        description.setText("");

        piechart.setDescription(description);
        piechart.setNoDataTextColor(Color.WHITE);
        piechart.setRotationEnabled(true);
        //piechart.setUsePercentValues(true);
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
        utils.showLog(TAG, "addDataSet started");
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

        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);


        LegendEntry l3 = new LegendEntry("Skipped " + tunans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.skiped_ans,null));
        LegendEntry l2 = new LegendEntry("Wrong Answers " + twans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.wrong_ans,null));
        LegendEntry l1 = new LegendEntry("Correct Answers " + tcans, Legend.LegendForm.SQUARE, 10f, 2f, null, getResources().getColor(R.color.correct_ans,null));


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
        legend.setCustom(new LegendEntry[]{l1, l2, l3});

        //piechart.getLegend().setEnabled(false);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);
        piechart.setExtraOffsets(-70, 0, 0, 0);
        piechart.invalidate();
    }

    public List<MockQuestionObj> getResultArray(String Path) {
        List<MockQuestionObj> QueArray = new ArrayList<>();
        utils.showLog("sriram ", "QueArray - " + Path);


        BufferedReader reader;
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                reader = new BufferedReader(new FileReader(getExternalFilesDir(null) + Path));
            }
            else reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + Path));
//            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<MockQuestionObj>>() {
            }.getType();

            QueArray = new Gson().fromJson(reader, listType);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        utils.showLog("sriram ", "QueArray - " + QueArray.size());
        return QueArray;
    }

    public void onViewClicked(View view) {
        Intent reviewIntent = new Intent(this, MockTestReview.class);
        reviewIntent.putExtra("testTitle", getIntent().getStringExtra("testTitle"));
        startActivity(reviewIntent);
        finish();
    }

    private void postResult(String testType, String courseId, String classId, String subjectId, String chapterId, String topicId, String totalQuestions, String totalTimeSpent,
                            String topicCCMapTd, String chapterCCMapId, String skipperQuestions, ArrayList<MockQuestionObj> mockQuestionList) {
        ApiClient apiClient = new ApiClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            int wrongAnswers = mockQuestionList.size() - Integer.parseInt(skipperQuestions)- tcans;
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("studentId", sObj.getStudentId());
            jsonObject.put("testType", testType);
            jsonObject.put("courseId", courseId);
            jsonObject.put("totalQuestions", totalQuestions);
            jsonObject.put("totalTimeSpent", totalTimeSpent);
            jsonObject.put("totalCorrectAnswers", tcans);
            jsonObject.put("totalWrongAnswers", wrongAnswers);
            jsonObject.put("totalSkippedAnswers", skipperQuestions);

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("classId", classId);
            jsonObj.put("subjectId", subjectId);
            jsonObj.put("chapterName", "");
            jsonObj.put("chapterId", chapterId);
            jsonObj.put("topicId", topicId);
            jsonObj.put("chapterCCMapId", chapterCCMapId);
            jsonObj.put("topicCCMapId", topicCCMapTd);
            jsonObj.put("correctAnswers", tcans);
            jsonObj.put("wrongAnswers", wrongAnswers);
            jsonObj.put("skippedAnswers", skipperQuestions);
            jsonObj.put("timeSpent", totalTimeSpent);

            JSONArray jsArray = new JSONArray();

            jsArray.put(jsonObj);
            jsonObject.put("insertTestDetailRecords", jsArray);
            JSONArray jsArray1 = new JSONArray();
            for(int i = 0; i < mockQuestionList.size(); i ++) {
                int timeSpentEachQuestion = (int) (mockQuestionList.get(i).getTimetaken() / 60000);
                JSONObject jsonObj1 = new JSONObject();
                jsonObj1.put("classId", classId);
                jsonObj1.put("chapterCCMapId", chapterCCMapId);
                jsonObj1.put("topicCCMapId", topicCCMapTd);
                jsonObj1.put("questionCCMapId", mockQuestionList.get(i).getQuestionCCMapId());
                jsonObj1.put("timeSpent", timeSpentEachQuestion);
                jsonObj1.put("subjectId", subjectId);
                jsonObj1.put("result", 0);
                jsArray1.put(jsonObj1);
            }
            jsonObject.put("questions", jsArray1);

            utils.showLog(TAG, String.valueOf(jsonObject));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        utils.showLog(TAG, "" + new AppUrls().PostMockTestResult);
        utils.showLog(TAG, "" + jsonObject);

        Request request = apiClient.postRequest(new AppUrls().PostMockTestResult, body, sh_Pref);

        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();

                        utils.showLog(TAG, "Student Result Status responce - " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            utils.showLog(TAG, "Result Posted Sucessfully");

                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, MockTestResult.this, message, sh_Pref);
                            });
                        } else {
                            utils.showLog(TAG, "Not Sucessfull");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    class ScoreCardAdapter extends RecyclerView.Adapter<ScoreCardAdapter.ViewHolder> {

        @NonNull
        @Override
        public ScoreCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ScoreCardAdapter.ViewHolder(LayoutInflater.from(MockTestResult.this).inflate(R.layout.item_sub_scorecard, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ScoreCardAdapter.ViewHolder holder, int position) {
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
