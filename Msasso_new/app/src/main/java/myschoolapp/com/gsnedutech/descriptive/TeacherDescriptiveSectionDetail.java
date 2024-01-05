package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.Models.SubScoreCard;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.models.DescCategory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static myschoolapp.com.gsnedutech.Util.MyUtils.roundTwoDecimals;


public class TeacherDescriptiveSectionDetail extends AppCompatActivity {

    private static final String TAG = DescriptiveSectionDetails.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_desc_sec)
    RecyclerView rvDescSections;

    @BindView(R.id.tv_totalmarks)
    TextView tvTotalmarks;
    @BindView(R.id.tv_totalpercent)
    TextView tvTotalpercent;
    @BindView(R.id.tv_rank)
    TextView tvRank;

    DescriptiveAdapter adapter;

    int selectPos = -1;
    HashMap<Integer, Float> summary = new HashMap<>();

    boolean isObjectiveStart = false;
    List<DescriptiveSectionDetails.DescExam> exams = new ArrayList<>();

    List<DescCategory> descriptiveQues = new ArrayList<>();
    SharedPreferences sh_Pref;
    SharedPreferences.Editor editor;

    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();

    int tunans = 0, tcans = 0, twans = 0, tgans = 0, tcMarks = 0, twMarks = 0, testMarks = 0, tgMarks = 0;
    int pCMarks = 0, pWMarks = 0;
    int descTestMarks;
    double totPercent;

    String studentId, fileCorrectMarks, fileWrongMarks;

    List<String> subList;
    ArrayList<SubScoreCard> subScoreCards = new ArrayList<>();

    boolean isStudent = false;

    int prev = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_descriptive_section_detail);
        ButterKnife.bind(this);
        init();
        getOnlineTestQuePaper(getIntent().getStringExtra("testFilePath"));

        findViewById(R.id.btn_finish).setOnClickListener(v -> {
            showCommentDialog();
        });

        findViewById(R.id.iv_back).setOnClickListener(v -> onBackPressed());
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        editor = sh_Pref.edit();
        if (sh_Pref.getBoolean("student_loggedin", false)) {
            isStudent = true;
            findViewById(R.id.ll_marks).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_finish).setVisibility(View.GONE);
        }

    }

    /* Get Question Paper from Service */
    void getOnlineTestQuePaper(String... strings) {
        utils.showLoader(TeacherDescriptiveSectionDetail.this);

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
                                if (exams.size() > 0)
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

        for (int i = 0; i < exams.size(); i++) {

            if (isStudent) {
                if (exams.get(i).getIsObjective().equalsIgnoreCase("1")) {

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
            }

            if (exams.get(i).getIsObjective().equalsIgnoreCase("0")) {


                for (int j = 0; j < exams.get(i).getQuestions().size(); j++) {
                    DescriptiveSectionDetails.Question que = exams.get(i).getQuestions().get(j);
                    DescCategory descriptive = new DescCategory();
                    descriptive.setExamSectionId(que.getExamSectionId().intValue());
                    descriptive.setQuestionId(que.getDescriptiveQuestionId());
                    descriptive.setStudentAnswer(new ArrayList<>());
                    descriptive.setComments("");
                    descriptive.setMarks(que.getMarks());
                    descriptive.setQuestionNumber(Integer.valueOf(que.getQuestionDisplayOrder()));

                    descriptiveQues.add(descriptive);
                }
            } else {
                if (!isStudent)
                    exams.remove(i);
                else {

                }
            }
        }

        if (isStudent) {
            getresultFromDB();
        } else {
            if (getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("INPROGRESS")
                    || getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("SUBMITTED")) {
                getDBTestQuestions();
            } else
                prepareQuestionPaper();
        }


    }

    /* Get DB Question Answers and Merge Questions and Previous Answers based on Question Id  */
    void getDBTestQuestions() {
        ProgressDialog loading = new ProgressDialog(TeacherDescriptiveSectionDetail.this);
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

        Log.v(TAG, "StudentDBTestQuestions Url - " + AppUrls.DGETSTUDENTEXAMBYID + "?studentId=" + getIntent().getIntExtra("studentId", 0) + "&examId=" + getIntent().getIntExtra("testId", 0));

        Request request = new Request.Builder()
                .url(AppUrls.DGETSTUDENTEXAMBYID + "?studentId=" + getIntent().getIntExtra("studentId", 0) + "&examId=" + getIntent().getIntExtra("testId", 0))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                new AlertDialog.Builder(TeacherDescriptiveSectionDetail.this)
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
                            descriptiveQues.clear();
                            descriptiveQues.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<DescCategory>>() {
                            }.getType()));

                            if (isStudent) {
                                JSONArray jsonArr = jsonObject.getJSONArray("descTestCategories");
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

    /* Get DB Question Answers and Merge Questions and Previous Answers based on Question Id  */
    void getresultFromDB() {
        ProgressDialog loading = new ProgressDialog(TeacherDescriptiveSectionDetail.this);
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

        Log.v(TAG, "StudentDBResult Url - " + AppUrls.DGetExamResultByStudentId + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + getIntent().getIntExtra("studentId", 0) + "&testId=" + getIntent().getIntExtra("testId", 0));

        Request request = new Request.Builder()
                .url(AppUrls.DGetExamResultByStudentId + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + getIntent().getIntExtra("studentId", 0) + "&testId=" + getIntent().getIntExtra("testId", 0))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    loading.dismiss();
                    new AlertDialog.Builder(TeacherDescriptiveSectionDetail.this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("Your internet seems to be down at the moment.Please connect and try again.")
                            .setPositiveButton("ReTry", (dialog, which) -> getDBTestQuestions())
                            .setNegativeButton("CLose the Test", (dialog, which) -> {
                                // do nothing
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(loading::dismiss);
                if (response.body() != null) {
                    try {
                        String responce = response.body().string();
                        Log.v(TAG, "DBTestQuestions response - " + responce);
                        JSONObject ParentjObject = new JSONObject(responce);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = ParentjObject.getJSONObject("result");
                            JSONArray jsonArray = jsonObject.getJSONArray("descCategories");
                            descriptiveQues.clear();
                            descriptiveQues.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<DescCategory>>() {
                            }.getType()));

                            JSONArray summaryArr = jsonObject.getJSONArray("summary");
                            summary.clear();
                            List<Summary> summaryList = new ArrayList<>();
                            summaryList.addAll(new Gson().fromJson(summaryArr.toString(), new TypeToken<List<Summary>>() {
                            }.getType()));
                            for (int i = 0; i < summaryList.size(); i++) {
                                summary.put(summaryList.get(i).getExamSectionId(), summaryList.get(i).getExamSectionMarks());
                            }


                            if (isStudent) {
                                JSONArray jsonArr = jsonObject.getJSONArray("descTestCategories");
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

        if (isStudent) {

            float marks = 0;
            descTestMarks = 0;

            if (summary.size() > 0) {
                for (int i = 0; i < exams.size(); i++) {
                    if (summary.get(exams.get(i).getDescExamSectionId()) != null)
                        marks = marks + summary.get(exams.get(i).getDescExamSectionId());
                    descTestMarks = descTestMarks + Integer.parseInt(exams.get(i).getExamSectionMarks());
                    exams.get(i).setSecuredMarks(summary.get(exams.get(i).getDescExamSectionId()));
                }
            }
            tvTotalmarks.setText(marks + "/" + descTestMarks);

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
        }

        rvDescSections.setLayoutManager(new LinearLayoutManager(TeacherDescriptiveSectionDetail.this));
        adapter = new DescriptiveAdapter();
        rvDescSections.setAdapter(adapter);

    }

    private class DescriptiveAdapter extends RecyclerView.Adapter<DescriptiveAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherDescriptiveSectionDetail.this).inflate(R.layout.item_descriptive, parent, false));
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

            holder.tvObjective.setOnClickListener(v -> {
//                Intent onlineTestIntent;
//                if (liveExams.getJeeSectionTemplate()!=null && !liveExams.getJeeSectionTemplate().equalsIgnoreCase("NA")) {
//                    onlineTestIntent = new Intent(mActivity, JEETestMarksDivision.class);
//                } else {
//                    onlineTestIntent = new Intent(mActivity, OTStudentOnlineTestActivity.class);
//                }
//                onlineTestIntent.putExtra("live", (Serializable) liveExams);
                Intent onlineTestIntent = new Intent(TeacherDescriptiveSectionDetail.this, DescriptiveTestReview.class);
                onlineTestIntent.putExtra("studentId", studentId);
                onlineTestIntent.putExtra("testId", getIntent().getIntExtra("testId", 0));
                onlineTestIntent.putExtra("testName", getIntent().getStringExtra("testName"));
                onlineTestIntent.putExtra("examSTime", getIntent().getStringExtra("examSTime"));
                onlineTestIntent.putExtra("examETime", getIntent().getStringExtra("examETime"));
                onlineTestIntent.putExtra("examRTime", getIntent().getStringExtra("examRTime"));
                onlineTestIntent.putExtra("testTime", "4800000");
                onlineTestIntent.putExtra("eDuration", 120);
                onlineTestIntent.putExtra("correctMarks", getIntent().getIntExtra("correctMarks", 0));
                onlineTestIntent.putExtra("wrongMarks", getIntent().getIntExtra("wrongMarks", 0));
                onlineTestIntent.putExtra("testCategory", "3");
                onlineTestIntent.putExtra("jeeSectionTemplate", "NA");
                onlineTestIntent.putExtra("testFilePath", getIntent().getStringExtra("testFilePath"));
                onlineTestIntent.putExtra("objList", new Gson().toJson(testQueList));
                onlineTestIntent.putExtra("studentTestFilePath", "INPROGRESS");
                startActivity(onlineTestIntent);
            });

            holder.llSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPos = position;
                    if (exams.get(position).getIsObjective().equalsIgnoreCase("0")) {
                        if (exams.get(position).getQuestions().size() > 0) {
                            holder.rvSecDet.setAdapter(new SecDetAdapter(exams.get(position)));
                            if (holder.rvSecDet.getVisibility() == View.VISIBLE) {
                                holder.rvSecDet.setVisibility(View.GONE);
                            } else {
                                holder.rvSecDet.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (holder.tvNoQuestions.getVisibility() == View.VISIBLE) {
                                holder.tvNoQuestions.setVisibility(View.GONE);
                            } else {
                                holder.tvNoQuestions.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
//                        if (isObjectiveStart) {
//                            holder.tvObjective.setText("Resume Objective Test");
//                        }
                        if (holder.tvObjective.getVisibility() == View.VISIBLE) {
                            holder.tvObjective.setVisibility(View.GONE);
                        } else {
                            holder.tvObjective.setVisibility(View.VISIBLE);
                        }
                    }

                    if (prev != -1 && prev != position)
                        notifyItemChanged(prev);
                    prev = position;
                }
            });
            if (exams.get(position).getIsObjective().equalsIgnoreCase("0")) {
                holder.rvSecDet.setLayoutManager(new LinearLayoutManager(TeacherDescriptiveSectionDetail.this));
                holder.rvSecDet.setVisibility(View.GONE);
                holder.tvObjective.setVisibility(View.GONE);
                holder.tvNoQuestions.setVisibility(View.GONE);
                if (selectPos == position) {
                    holder.rvSecDet.setVisibility(View.VISIBLE);
                }
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
                if (isStudent)
                    holder.tvSecuredMarks.setText("Secured Marks : " + NumberFormat.getInstance().format(exams.get(position).getSecuredMarks()));
                else
                    holder.tvSecuredMarks.setText("Secured Marks : " + NumberFormat.getInstance().format(totalMarks));
//            Toast.makeText(TeacherDescriptiveSectionDetail.this, ""+totalMarks, Toast.LENGTH_SHORT).show();
            } else {
                holder.tvObjective.setText("Review Objective Test");
                if (isStudent)
                    holder.tvSecuredMarks.setText("Secured Marks : " + NumberFormat.getInstance().format(exams.get(position).getSecuredMarks()));
                else
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

    private class SecDetAdapter extends RecyclerView.Adapter<SecDetAdapter.ViewHolder> {

        List<DescriptiveSectionDetails.Question> secQuestionList = new ArrayList<>();
        DescriptiveSectionDetails.DescExam exam;

        public SecDetAdapter(DescriptiveSectionDetails.DescExam descExam) {
            this.exam = descExam;
            this.secQuestionList = exam.getQuestions();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherDescriptiveSectionDetail.this).inflate(R.layout.item_descr_sec_det, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.cwvQuestion.setText(secQuestionList.get(position).getDescriptiveQuestion());
//            holder.tvSubmit.setText("Review Answer");
            for (int i = 0; i < descriptiveQues.size(); i++) {
                if (secQuestionList.get(position).getDescriptiveQuestionId().equals(descriptiveQues.get(i).getQuestionId())) {
                    if (descriptiveQues.get(i).getStudentAnswer().size() > 0) {
                        holder.tvFilesAttached.setText(descriptiveQues.get(i).getStudentAnswer().size() + " files attached");
                    } else {
                        holder.tvFilesAttached.setText("0 files attached");
                    }
                }
            }

            holder.ivSubmit.setOnClickListener(v -> {
                getStudentAnswer(secQuestionList.get(position), exam);
//                Intent ii = new Intent(DescriptiveSectionDetails.this, DescriptiveQueSubmit.class);
//                ii.putExtra("question", secQuestionList.get(position).getDescriptiveQuestion());
//                ii.putExtra("object", );
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            });
        }

        @Override
        public int getItemCount() {
            return secQuestionList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            CustomWebview cwvQuestion;
            ImageView ivSubmit;
            TextView tvFilesAttached;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cwvQuestion = itemView.findViewById(R.id.cwv_question);
                ivSubmit = itemView.findViewById(R.id.iv_submit);
                tvFilesAttached = itemView.findViewById(R.id.tv_attached_files);

            }
        }
    }

    private void getStudentAnswer(DescriptiveSectionDetails.Question descriptiveQuestionId, DescriptiveSectionDetails.DescExam questionMarks) {

        DescCategory studentAnswers = new DescCategory();
        int pos = 0;
        for (int i = 0; i < descriptiveQues.size(); i++) {
            if (descriptiveQuestionId.getDescriptiveQuestionId().equals(descriptiveQues.get(i).getQuestionId())) {
                studentAnswers = descriptiveQues.get(i);
                pos = i;
            }
        }
        if (studentAnswers.getStudentAnswer().size() > 0) {

            Intent ii = new Intent(TeacherDescriptiveSectionDetail.this, TeacherDescriptiveQueSubmit.class);
            ii.putExtra("question", descriptiveQuestionId.getDescriptiveQuestion());
            ii.putExtra("questionObj", studentAnswers);
            ii.putExtra("examSectionId", descriptiveQuestionId.getExamSectionId().intValue());
            ii.putExtra("questionMarks", questionMarks);
            ii.putExtra("studentId", getIntent().getIntExtra("studentId", 0));
            ii.putExtra("examId", getIntent().getIntExtra("testId", 0));
            ii.putExtra("pos", pos);
            startActivityForResult(ii, 1234);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else {
            new AlertDialog.Builder(TeacherDescriptiveSectionDetail.this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage("Question not submitted by Student")
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == 4321) {
            int pos = data.getIntExtra("pos", 0);
            DescCategory ans = (DescCategory) data.getSerializableExtra("resultObj");
            descriptiveQues.set(pos, ans);
            if (data.hasExtra("questionId")) {
                updateDescriptiveQueId(data.getIntExtra("questionId", 0));
            } else adapter.notifyDataSetChanged();
        }
    }

    private void updateDescriptiveQueId(int questionId) {
        for (DescCategory cat : descriptiveQues) {
            if (cat.getQuestionId() == questionId) {
                cat.setResultMarks(0);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void updateMarks(String comments) {
        utils.showLoader(TeacherDescriptiveSectionDetail.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
            jsonObject.put("isCompleted", 1);
            jsonObject.put("comments", comments);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG, "_finalUrl Json " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.DUpdateMarks)
                .put(body)
                .build();


        utils.showLog(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonResp = response.body().string();
                    utils.showLog(TAG, "_finalUrl responseBody - " + jsonResp);
                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(TeacherDescriptiveSectionDetail.this, ParentjObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    void showCommentDialog() {

        Rect displayRectangle = new Rect();
        Window window = TeacherDescriptiveSectionDetail.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        Dialog dd = new Dialog(TeacherDescriptiveSectionDetail.this);
        dd.setCancelable(false);
        dd.setContentView(R.layout.dialog_comments);
        dd.show();
        dd.getWindow().setLayout((int) (displayRectangle.width() * 0.9f), ViewGroup.LayoutParams.WRAP_CONTENT);
        dd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText comments = dd.findViewById(R.id.et_comment);
        dd.findViewById(R.id.img_close).setOnClickListener(v -> {
            dd.dismiss();
        });

        dd.findViewById(R.id.tv_submit).setOnClickListener(v -> {
            if (comments.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Comments should not be empty", Toast.LENGTH_SHORT).show();
            } else {
                updateMarks(comments.getText().toString().trim());
            }
        });

//        new AlertDialog.Builder(this)
//                .setTitle(getResources().getString(R.string.app_name))
//                .setMessage("No Data")
//                .setView(input)
//                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//                    if (input.getText().toString().trim().isEmpty()){
//                        Toast.makeText(this, "Comments should not be empty", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        updateMarks(input.getText().toString().trim());
//                    }
//
//                })
//                .setCancelable(false)
//                .show();
    }

    private int calculateMarks() {
        int tot = 0;
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


    class Summary implements Serializable {
        @SerializedName("desc_exam_id")
        @Expose
        private Integer descExamId;
        @SerializedName("student_id")
        @Expose
        private Integer studentId;
        @SerializedName("total_marks")
        @Expose
        private Integer totalMarks;
        @SerializedName("exam_section_id")
        @Expose
        private Integer examSectionId;
        @SerializedName("exam_section_name")
        @Expose
        private String examSectionName;
        @SerializedName("exam_section_marks")
        @Expose
        private Float examSectionMarks;
        @SerializedName("is_objective")
        @Expose
        private Integer isObjective;

        public Integer getDescExamId() {
            return descExamId;
        }

        public void setDescExamId(Integer descExamId) {
            this.descExamId = descExamId;
        }

        public Integer getStudentId() {
            return studentId;
        }

        public void setStudentId(Integer studentId) {
            this.studentId = studentId;
        }

        public Integer getTotalMarks() {
            return totalMarks;
        }

        public void setTotalMarks(Integer totalMarks) {
            this.totalMarks = totalMarks;
        }

        public Integer getExamSectionId() {
            return examSectionId;
        }

        public void setExamSectionId(Integer examSectionId) {
            this.examSectionId = examSectionId;
        }

        public String getExamSectionName() {
            return examSectionName;
        }

        public void setExamSectionName(String examSectionName) {
            this.examSectionName = examSectionName;
        }

        public Float getExamSectionMarks() {
            return examSectionMarks;
        }

        public void setExamSectionMarks(Float examSectionMarks) {
            this.examSectionMarks = examSectionMarks;
        }

        public Integer getIsObjective() {
            return isObjective;
        }

        public void setIsObjective(Integer isObjective) {
            this.isObjective = isObjective;
        }
    }


}