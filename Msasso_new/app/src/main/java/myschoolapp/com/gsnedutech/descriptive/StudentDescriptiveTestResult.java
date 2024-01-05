package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.SubScoreCard;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.descriptive.fragments.StudentOTDescriptiveExamsFrag;
import myschoolapp.com.gsnedutech.descriptive.models.DescCategory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static myschoolapp.com.gsnedutech.Util.MyUtils.roundTwoDecimals;


public class StudentDescriptiveTestResult extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = StudentDescriptiveTestResult.class.getName();

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
    int descTestMarks;
    double totPercent;


    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();

    List<DescCategory> descriptiveQues = new ArrayList<>();
    List<DescriptiveSectionDetails.DescExam> exams = new ArrayList<>();

    DescriptiveAdapter adapter;


    StudentOTDescriptiveExamsFrag.LiveExam studentTestObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_descriptive_test_result);
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
            utils.alertDialog(1, StudentDescriptiveTestResult.this, getString(R.string.error_connect), getString(R.string.error_internet),
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
                    getOnlineTestQuePaper(studentTestObj.getePath());
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
            studentTestObj = (StudentOTDescriptiveExamsFrag.LiveExam) getIntent().getSerializableExtra("studentTest");
            tvTestName.setText("" + studentTestObj.geteName());
        }

        studentId = getIntent().getStringExtra("studentId");

        rvSubScores.setLayoutManager(new LinearLayoutManager(StudentDescriptiveTestResult.this));


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    /* Get Question Paper from Service */
    void getOnlineTestQuePaper(String... strings) {
        utils.showLoader(StudentDescriptiveTestResult.this);

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

                            JSONArray jsonArr = ParentjObject.getJSONArray("descExams");

                            exams.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<DescriptiveSectionDetails.DescExam>>() {
                            }.getType()));

                            utils.showLog(TAG, "exams - " + exams.size());

                            runOnUiThread(() -> {
                                if (exams.size()>0)
                                    diffDescriptiveAndObjective();
                                else showNoDataDialogue();
                            });

//                            if (ParentjObject.has("paragraphDetails")) {
//                                JSONArray jsonArr2 = ParentjObject.getJSONArray("paragraphDetails");
//                                paragraphDetailsList.addAll(new Gson().fromJson(jsonArr2.toString(), new TypeToken<List<OnlineQuestionParagraphDetails>>() {
//                                }.getType()));
//                                utils.showLog(TAG, "paragraphDetails - " + paragraphDetailsList.size());
//                            }


//                            runOnUiThread(() -> {
//                                if (getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("INPROGRESS")
//                                        || getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("SUBMITTED")) {
//                                    getDBTestQuestions();
//                                } else
//                                    PrepareQuestionPaper();
//                            });

                        } else {

                            runOnUiThread(() -> {
                                showNoDataDialogue();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }


    private void showNoDataDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("No Data")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void diffDescriptiveAndObjective() {

        for (int i=0; i<exams.size(); i++){

            if (exams.get(i).getIsObjective().equalsIgnoreCase("1")){

                testQueList.addAll(new Gson().fromJson(new Gson().toJson(exams.get(i).getQuestions()), new TypeToken<List<OnlineQuestionObj2>>() {
                }.getType()));

                utils.showLog(TAG, "testQueList - " + testQueList.size());

//                if (ParentjObject.has("paragraphDetails")) {
//                    JSONArray jsonArr2 = ParentjObject.getJSONArray("paragraphDetails");
//                    paragraphDetailsList.addAll(new Gson().fromJson(jsonArr2.toString(), new TypeToken<List<OnlineQuestionParagraphDetails>>() {
//                    }.getType()));
//                    utils.showLog(TAG, "paragraphDetails - " + paragraphDetailsList.size());
//                }
            }

            if (exams.get(i).getIsObjective().equalsIgnoreCase("0")){
                for (int j =0; j<exams.get(i).getQuestions().size(); j++){
                    DescriptiveSectionDetails.Question que = exams.get(i).getQuestions().get(j);
                    DescCategory descriptive = new DescCategory();
                    descriptive.setExamSectionId(que.getExamSectionId().intValue()); //
                    descriptive.setQuestionId(que.getDescriptiveQuestionId());
                    descriptive.setStudentAnswer(new ArrayList<>());
                    descriptive.setComments("");
                    descriptive.setMarks(que.getMarks());
                    descriptive.setQuestionNumber(Integer.valueOf(que.getQuestionDisplayOrder()));

                    descriptiveQues.add(descriptive);
                }
            }
        }

        getDBTestQuestions();


    }

    /* Get DB Question Answers and Merge Questions and Previous Answers based on Question Id  */
    void getDBTestQuestions() {
        ProgressDialog loading = new ProgressDialog(StudentDescriptiveTestResult.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        String jsonResp = "null";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "StudentDBTestQuestions Url - " + AppUrls.DGETSTUDENTEXAMBYID + "?studentId="+getIntent().getIntExtra("studentId",0)+"&examId="+getIntent().getIntExtra("testId",0));

        Request request = new Request.Builder()
                .url(AppUrls.DGETSTUDENTEXAMBYID + "?studentId="+getIntent().getIntExtra("studentId",0)+"&examId="+getIntent().getIntExtra("testId",0))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                new AlertDialog.Builder(StudentDescriptiveTestResult.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Your internet seems to be down at the moment.Please connect and try again.")
                        .setPositiveButton("ReTry", (dialog, which) -> getDBTestQuestions())
                        .setNegativeButton("CLose the Test", (dialog, which) -> {
                            // do nothing
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {
                    try {
                        String responce = response.body().string();
                        Log.v(TAG, "DBTestQuestions response - " + responce);
                        JSONObject ParentjObject = new JSONObject(responce);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = ParentjObject.getJSONObject("result");
                            JSONArray jsonArray = jsonObject.getJSONArray("descCategories");
                            JSONArray jsonArr = jsonObject.getJSONArray("descTestCategories");
                            descriptiveQues.clear();
                            descriptiveQues.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<DescCategory>>() {
                            }.getType()));

                            testQueDetailsList.clear();
                            testQueDetailsList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            if (testQueDetailsList.size() > 0) {
                                for (int i = 0; i < testQueDetailsList.size(); i++) {
                                    for (int j = 0; j < testQueList.size(); j++) {
                                        if (testQueDetailsList.get(i).getQuestType().equalsIgnoreCase("CPQ")) {
                                            if (testQueDetailsList.get(i).getQuestionMapId().equals(testQueList.get(j).getQuestionMapId())) {
                                                testQueList.get(j).setCorrectAnswer(testQueDetailsList.get(i).getCorrectAnswer());
                                                testQueList.get(j).setSelectedAnswer(testQueDetailsList.get(i).getSelectedAnswer());
                                                testQueList.get(j).setStyle(testQueDetailsList.get(i).getStyle());
                                                testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());
                                            }
                                        } else {
                                            if (testQueDetailsList.get(i).getQuestionId().equals(testQueList.get(j).getQuestionId())) {
                                                testQueList.get(j).setCorrectAnswer(testQueDetailsList.get(i).getCorrectAnswer());
                                                testQueList.get(j).setSelectedAnswer(testQueDetailsList.get(i).getSelectedAnswer());
                                                testQueList.get(j).setStyle(testQueDetailsList.get(i).getStyle());
                                                testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());
                                            }
                                        }
                                    }
                                }
                            }


                            runOnUiThread(() ->
                                    prepareQuestionPaper());
                        } else {
                            runOnUiThread(() -> prepareQuestionPaper());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }



    private void prepareQuestionPaper() {

        rvSubScores.setLayoutManager(new LinearLayoutManager(StudentDescriptiveTestResult.this));
        adapter = new DescriptiveAdapter();
        rvSubScores.setAdapter(adapter);
        float marks = 0;
        for (DescCategory descCategory : descriptiveQues){
            if (descCategory.getResultMarks()>0){
               marks = marks + descCategory.getResultMarks();
            }
        }
        for (DescriptiveSectionDetails.DescExam exam: exams ){
            descTestMarks = descTestMarks + Integer.parseInt(exam.getExamSectionMarks());
        }
        marks = marks + calculateMarks();

        tvTotalmarks.setText("" + marks);

        totPercent = roundTwoDecimals(((double) marks / (double) (descTestMarks) * 100));
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


    }

    private class DescriptiveAdapter extends RecyclerView.Adapter<DescriptiveAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(StudentDescriptiveTestResult.this).inflate(R.layout.item_descriptive, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSecName.setText(exams.get(position).getExamSectionName());
            holder.tvSecDesc.setText(exams.get(position).getExamSectionDesc());
            if (exams.get(position).getOptionalQuestions().isEmpty() || exams.get(position).getOptionalQuestions().equalsIgnoreCase("0")) {
                holder.tvAnsAny.setText("Answer Any : " + exams.get(position).getNumQuestions() + "/" + exams.get(position).getNumQuestions());
            } else {
                if (exams.get(position).getOptionalQuestions() != null) {
                    int ansAny = Integer.parseInt(exams.get(position).getNumQuestions()) - Integer.parseInt(exams.get(position).getOptionalQuestions());
                    holder.tvAnsAny.setText("Answer Any : " + ansAny + "/" + exams.get(position).getNumQuestions());
                }
            }
            holder.tvSecMarks.setText("Total Marks : " + exams.get(position).getExamSectionMarks());
            if (exams.get(position).getIsObjective().equalsIgnoreCase("0")) {
                holder.rvSecDet.setLayoutManager(new LinearLayoutManager(StudentDescriptiveTestResult.this));
                holder.rvSecDet.setVisibility(View.GONE);
                holder.tvObjective.setVisibility(View.GONE);
                holder.tvNoQuestions.setVisibility(View.GONE);
                float totalMarks = 0;
                List<DescriptiveSectionDetails.Question> questions = exams.get(position).getQuestions();
                for (int i = 0; i < descriptiveQues.size(); i++) {
                    for (int j = 0; j < questions.size(); j++) {
                        if (questions.get(j).getDescriptiveQuestionId().equals(descriptiveQues.get(i).getQuestionId())) {
                            if (descriptiveQues.get(i).getResultMarks() > 0) {
                                totalMarks = totalMarks + descriptiveQues.get(i).getResultMarks();
                                exams.get(position).getQuestions().get(j).setQuestioncorrectmark(descriptiveQues.get(i).getResultMarks());
                            } else {
                                exams.get(position).getQuestions().get(j).setQuestioncorrectmark(0);
                            }
                        }
                    }

                }
                holder.tvSecuredMarks.setText("Secured Marks : " + NumberFormat.getInstance().format(totalMarks));
//            Toast.makeText(TeacherDescriptiveSectionDetail.this, ""+totalMarks, Toast.LENGTH_SHORT).show();
            }
            else {
                holder.tvSecuredMarks.setText("Secured Marks : " + NumberFormat.getInstance().format(calculateMarks()));
            }
        }

        @Override
        public int getItemCount() {
            return exams.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            RecyclerView rvSecDet;
            TextView tvSecName, tvSecDesc, tvNoQuestions, tvObjective, tvAnsAny, tvSecMarks, tvSecuredMarks;
            LinearLayout llSection;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSecMarks = itemView.findViewById(R.id.tv_sec_marks);
                tvAnsAny = itemView.findViewById(R.id.tv_ans_any);
                tvNoQuestions = itemView.findViewById(R.id.tv_no_questions);
                tvObjective = itemView.findViewById(R.id.tv_objective);
                tvSecName = itemView.findViewById(R.id.tv_secName);
                tvSecDesc = itemView.findViewById(R.id.tv_secDesc);
                rvSecDet = itemView.findViewById(R.id.rv_sec_det);
                llSection = itemView.findViewById(R.id.ll_section);
                tvSecuredMarks = itemView.findViewById(R.id.tv_secured_marks);
            }
        }
    }

    private int calculateMarks() {
        int tot=0;
        tcMarks = 0;
        twMarks = 0;
        tgMarks = 0;
        pCMarks = 0;
        pWMarks = 0;
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
        tot = (tcMarks - twMarks) + tgMarks;

//
//        makePieChart(tunans, twans, tcans, tgans);
//        rvSubScores.setAdapter(new StudentOnlineTestResult.ScoreCardAdapter());
        return tot;

    }

    public static String removeTrailingLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        str = str.indexOf(".") < 0 ? str : str.replaceAll("0*$", "").replaceAll("\\.$", "");
        if (str.equalsIgnoreCase(""))
            str = "0";
        return str;
    }



}

