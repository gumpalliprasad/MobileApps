package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.OnlIneTest.OTStudentTestOnlineCompleted;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.descriptive.models.DescCategory;
import myschoolapp.com.gsnedutech.descriptive.models.DescTestCategory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DescriptiveSectionDetails extends AppCompatActivity {

    private static final String TAG = DescriptiveSectionDetails.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_desc_sec)
    RecyclerView rvDescSections;

    DescriptiveAdapter adapter;

    int selectPos = -1;

    boolean isObjectiveStart = false;
    List<DescExam> exams = new ArrayList<>();

    List<DescTestCategory> objectiveQues = new ArrayList<>();
    List<DescCategory> descriptiveQues = new ArrayList<>();
    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();
    ArrayList<ArrayList<OnlineQuestionObj2>> arrayOfArrays = new ArrayList<>();
    List<String> subList = new ArrayList<>();

    int studentId;
    String insertId;
    String examdetId = "";
    String eStautus = "";

    boolean isINPFirst = false;
    boolean isSUBFirst = false;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor editor;

    int correctMarks = 0;
    String wrongMarks;


    int prev = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_section_details);
        ButterKnife.bind(this);

        init();
        getOnlineTestQuePaper(getIntent().getStringExtra("testFilePath"));
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.btn_syn).setOnClickListener(v -> {
            PrepareResult(false);
        });

        findViewById(R.id.btn_finish).setOnClickListener(v -> {
            PrepareResult(true);
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(act2InitReceiver, new IntentFilter("activity-2-initialized"));


    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        editor = sh_Pref.edit();

        examdetId = getIntent().getStringExtra("examdet_Id");
        eStautus = getIntent().getStringExtra("studentTestFilePath");
        studentId = getIntent().getIntExtra("studentId", 0);
        correctMarks = getIntent().getIntExtra("correctMarks", 0);
        wrongMarks = String.valueOf(getIntent().getIntExtra("wrongMarks", 0));
        if (eStautus.equalsIgnoreCase("NA")) {
            isSUBFirst = true;
            isINPFirst = true;
        } else if (eStautus.equalsIgnoreCase("INPROGRESS")) {
            isINPFirst = false;
            isSUBFirst = true;
        } else if (eStautus.equalsIgnoreCase("SUBMITTED")) {
            isSUBFirst = false;
            isINPFirst = false;
        }
    }

    private class DescriptiveAdapter extends RecyclerView.Adapter<DescriptiveAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(DescriptiveSectionDetails.this).inflate(R.layout.item_descriptive, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvSecName.setText(exams.get(position).getExamSectionName());
            holder.tvSecDesc.setText(exams.get(position).getExamSectionDesc());
            if (exams.get(position).getOptionalQuestions().isEmpty() || exams.get(position).getOptionalQuestions().equalsIgnoreCase("0")) {
                holder.tvAnsAny.setText("Answer : " + exams.get(position).getNumQuestions() + "/" + exams.get(position).getNumQuestions());
            } else {
                if (exams.get(position).getOptionalQuestions() != null) {
                    int ansAny = Integer.parseInt(exams.get(position).getNumQuestions()) - Integer.parseInt(exams.get(position).getOptionalQuestions());
                    holder.tvAnsAny.setText("Answer : " + ansAny + "/" + exams.get(position).getNumQuestions());
                }
            }
            holder.tvSecMarks.setText("Marks    : " + exams.get(position).getExamSectionMarks());
            holder.tvSecuredMarks.setText("Secured Marks : "+exams.get(position).getSecuredMarks());
            holder.rvSecDet.setLayoutManager(new LinearLayoutManager(DescriptiveSectionDetails.this));
            holder.rvSecDet.setVisibility(View.GONE);
            if (selectPos == position) {
                holder.rvSecDet.setVisibility(View.VISIBLE);
            }
            holder.tvObjective.setVisibility(View.GONE);
            holder.tvNoQuestions.setVisibility(View.GONE);
            holder.tvObjective.setOnClickListener(v -> {
//                Intent onlineTestIntent;
//                if (liveExams.getJeeSectionTemplate()!=null && !liveExams.getJeeSectionTemplate().equalsIgnoreCase("NA")) {
//                    onlineTestIntent = new Intent(mActivity, JEETestMarksDivision.class);
//                } else {
//                    onlineTestIntent = new Intent(mActivity, OTStudentOnlineTestActivity.class);
//                }
//                onlineTestIntent.putExtra("live", (Serializable) liveExams);
                Intent onlineTestIntent = new Intent(DescriptiveSectionDetails.this, DescriptiveObjTestActivity.class);
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
                            holder.rvSecDet.setAdapter(new SecDetAdapter(exams.get(position).getQuestions()));
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
                        if (isObjectiveStart) {
                            holder.tvObjective.setText("Resume Objective Test");
                        }
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

        List<Question> secQuestionList = new ArrayList<>();

        public SecDetAdapter(List<Question> questions) {
            this.secQuestionList = questions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(DescriptiveSectionDetails.this).inflate(R.layout.item_descr_sec_det, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.cwvQuestion.setText(secQuestionList.get(position).descriptiveQuestion);
//            holder.ivSubmit.setText("Upload Answer");
//            if (descCategories.get(position).getStudentAnswer().size()>0){
//                holder.tvFilesAttached.setText(descCategories.get(position).getStudentAnswer().size()+" files attached");
//            }
//            else {
//                holder.tvFilesAttached.setText("0 files attached");
//            }
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
                getStudentAnswer(secQuestionList.get(position));
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

    private void getStudentAnswer(Question descriptiveQuestionId) {

        DescCategory studentAnswers = new DescCategory();
        int pos = 0;
        for (int i = 0; i < descriptiveQues.size(); i++) {
            if (descriptiveQuestionId.getDescriptiveQuestionId().equals(descriptiveQues.get(i).getQuestionId())) {
                studentAnswers = descriptiveQues.get(i);
                pos = i;
            }
        }
        Intent ii = new Intent(DescriptiveSectionDetails.this, DescriptiveQueSubmit.class);
        ii.putExtra("question", descriptiveQuestionId.getDescriptiveQuestion());
        ii.putExtra("questionObj", studentAnswers);
        ii.putExtra("studentId", getIntent().getIntExtra("studentId", 0));
        ii.putExtra("examId", getIntent().getIntExtra("testId", 0));
        ii.putExtra("pos", pos);
        startActivityForResult(ii, 1234);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == 4321) {
            int pos = data.getIntExtra("pos", 0);
            DescCategory ans = (DescCategory) data.getSerializableExtra("resultObj");
            descriptiveQues.get(pos).setStudentAnswer(ans.getStudentAnswer());
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    public class DescExam implements Serializable {

        @SerializedName("descExamSectionId")
        @Expose
        private Integer descExamSectionId;
        @SerializedName("examSectionDesc")
        @Expose
        private String examSectionDesc;
        @SerializedName("examSectionMarks")
        @Expose
        private String examSectionMarks;
        @SerializedName("examSectionName")
        @Expose
        private String examSectionName;
        @SerializedName("isObjective")
        @Expose
        private String isObjective;
        @SerializedName("numQuestions")
        @Expose
        private String numQuestions;
        @SerializedName("optionalQuestions")
        @Expose
        private String optionalQuestions;
        @SerializedName("questionMarks")
        @Expose
        private String questionMarks;
        @SerializedName("questions")
        @Expose
        private List<Question> questions = null;

        public Integer getDescExamSectionId() {
            return descExamSectionId;
        }

        public void setDescExamSectionId(Integer descExamSectionId) {
            this.descExamSectionId = descExamSectionId;
        }

        public String getExamSectionDesc() {
            return examSectionDesc;
        }

        public void setExamSectionDesc(String examSectionDesc) {
            this.examSectionDesc = examSectionDesc;
        }

        public String getExamSectionMarks() {
            return examSectionMarks;
        }

        public void setExamSectionMarks(String examSectionMarks) {
            this.examSectionMarks = examSectionMarks;
        }

        public String getExamSectionName() {
            return examSectionName;
        }

        public void setExamSectionName(String examSectionName) {
            this.examSectionName = examSectionName;
        }

        public String getIsObjective() {
            return isObjective;
        }

        public void setIsObjective(String isObjective) {
            this.isObjective = isObjective;
        }

        public String getNumQuestions() {
            return numQuestions;
        }

        public void setNumQuestions(String numQuestions) {
            this.numQuestions = numQuestions;
        }

        public String getOptionalQuestions() {
            return optionalQuestions;
        }

        public void setOptionalQuestions(String optionalQuestions) {
            this.optionalQuestions = optionalQuestions;
        }

        public String getQuestionMarks() {
            return questionMarks;
        }

        public void setQuestionMarks(String questionMarks) {
            this.questionMarks = questionMarks;
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        Float securedMarks;

        public void setSecuredMarks(Float securedMarks) {
            this.securedMarks = securedMarks;
        }

        public Float getSecuredMarks() {
            return securedMarks;
        }
    }


    public class Question implements Serializable {

        @SerializedName("examSectionId")
        @Expose
        private Integer examSectionId;
        @SerializedName("descriptiveQuestionAnswer")
        @Expose
        private String descriptiveQuestionAnswer;
        @SerializedName("descriptiveQuestion")
        @Expose
        private String descriptiveQuestion;
        @SerializedName("questType")
        @Expose
        private String questType;
        @SerializedName("chapterName")
        @Expose
        private String chapterName;
        @SerializedName("marks")
        @Expose
        private Integer marks;
        @SerializedName("descExamId")
        @Expose
        private Integer descExamId;
        @SerializedName("subjectId")
        @Expose
        private Integer subjectId;
        @SerializedName("topicId")
        @Expose
        private Integer topicId;
        @SerializedName("chapterId")
        @Expose
        private Integer chapterId;
        @SerializedName("topicName")
        @Expose
        private String topicName;
        @SerializedName("questionDisplayOrder")
        @Expose
        private String questionDisplayOrder;
        @SerializedName("descriptiveQuestionId")
        @Expose
        private Integer descriptiveQuestionId;
        @SerializedName("subjectName")
        @Expose
        private String subjectName;
        @SerializedName("correctAnswer")
        @Expose
        private String correctAnswer;
        @SerializedName("correctMarks")
        @Expose
        private Integer correctMarks;
        @SerializedName("wrongMarks")
        @Expose
        private Integer wrongMarks;
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("questionId")
        @Expose
        private Integer questionId;
        @SerializedName("option1")
        @Expose
        private String option1;
        @SerializedName("option2")
        @Expose
        private String option2;
        @SerializedName("option3")
        @Expose
        private String option3;
        @SerializedName("option4")
        @Expose
        private String option4;
        @SerializedName("chapterCCMapId")
        @Expose
        private String chapterCCMapId;

        public String getChapterCCMapId() {
            return chapterCCMapId;
        }

        public void setChapterCCMapId(String chapterCCMapId) {
            this.chapterCCMapId = chapterCCMapId;
        }

        @SerializedName("topicCCMapId")
        @Expose
        private String topicCCMapId;
        public String getTopicCCMapId() {
            return topicCCMapId;
        }

        public void setTopicCCMapId(String topicCCMapId) {
            this.topicCCMapId = topicCCMapId;
        }

        public float getQuestioncorrectmark() {
            return questioncorrectmark;
        }

        public void setQuestioncorrectmark(float questioncorrectmark) {
            this.questioncorrectmark = questioncorrectmark;
        }

        private float questioncorrectmark;

        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }

        @SerializedName("branchId")
        @Expose
        private String branchId;
        @SerializedName("subjectGroup")
        @Expose
        private String subjectGroup;

        public Integer getExamSectionId() {
            return examSectionId;
        }

        public void setExamSectionId(Integer examSectionId) {
            this.examSectionId = examSectionId;
        }

        public String getDescriptiveQuestionAnswer() {
            return descriptiveQuestionAnswer;
        }

        public void setDescriptiveQuestionAnswer(String descriptiveQuestionAnswer) {
            this.descriptiveQuestionAnswer = descriptiveQuestionAnswer;
        }

        public String getDescriptiveQuestion() {
            return descriptiveQuestion;
        }

        public void setDescriptiveQuestion(String descriptiveQuestion) {
            this.descriptiveQuestion = descriptiveQuestion;
        }

        public String getQuestType() {
            return questType;
        }

        public void setQuestType(String questType) {
            this.questType = questType;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public Integer getMarks() {
            return marks;
        }

        public void setMarks(Integer marks) {
            this.marks = marks;
        }

        public Integer getDescExamId() {
            return descExamId;
        }

        public void setDescExamId(Integer descExamId) {
            this.descExamId = descExamId;
        }

        public Integer getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Integer subjectId) {
            this.subjectId = subjectId;
        }

        public Integer getTopicId() {
            return topicId;
        }

        public void setTopicId(Integer topicId) {
            this.topicId = topicId;
        }

        public Integer getChapterId() {
            return chapterId;
        }

        public void setChapterId(Integer chapterId) {
            this.chapterId = chapterId;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getQuestionDisplayOrder() {
            return questionDisplayOrder;
        }

        public void setQuestionDisplayOrder(String questionDisplayOrder) {
            this.questionDisplayOrder = questionDisplayOrder;
        }

        public Integer getDescriptiveQuestionId() {
            return descriptiveQuestionId;
        }

        public void setDescriptiveQuestionId(Integer descriptiveQuestionId) {
            this.descriptiveQuestionId = descriptiveQuestionId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public Integer getCorrectMarks() {
            return correctMarks;
        }

        public void setCorrectMarks(Integer correctMarks) {
            this.correctMarks = correctMarks;
        }

        public Integer getWrongMarks() {
            return wrongMarks;
        }

        public void setWrongMarks(Integer wrongMarks) {
            this.wrongMarks = wrongMarks;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

        public String getOption1() {
            return option1;
        }

        public void setOption1(String option1) {
            this.option1 = option1;
        }

        public String getOption2() {
            return option2;
        }

        public void setOption2(String option2) {
            this.option2 = option2;
        }

        public String getOption3() {
            return option3;
        }

        public void setOption3(String option3) {
            this.option3 = option3;
        }

        public String getOption4() {
            return option4;
        }

        public void setOption4(String option4) {
            this.option4 = option4;
        }

        public String getSubjectGroup() {
            return subjectGroup;
        }

        public void setSubjectGroup(String subjectGroup) {
            this.subjectGroup = subjectGroup;
        }

    }


    /* Get Question Paper from Service */
    void getOnlineTestQuePaper(String... strings) {
        utils.showLoader(DescriptiveSectionDetails.this);

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

                            exams.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<DescExam>>() {
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

    private void diffDescriptiveAndObjective() {


        for (int i = 0; i < exams.size(); i++) {

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
            } else {
                for (int j = 0; j < exams.get(i).getQuestions().size(); j++) {
                    Question que = exams.get(i).getQuestions().get(j);
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

        if (getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("INPROGRESS")
                || getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("SUBMITTED")) {
            getDBTestQuestions();
        } else
            prepareQuestionPaper();


    }

    private void prepareQuestionPaper() {

        rvDescSections.setLayoutManager(new LinearLayoutManager(DescriptiveSectionDetails.this));
        adapter = new DescriptiveAdapter();
        rvDescSections.setAdapter(adapter);
        for (int i = 0; i < testQueList.size(); i++) {
            if (!subList.contains(testQueList.get(i).getSubjectGroup())) {
                subList.add(testQueList.get(i).getSubjectGroup());
            }
        }

        for (int i = 0; i < subList.size(); i++) {
            ArrayList<OnlineQuestionObj2> sub_quelist = new ArrayList<>();
            for (int j = 0; j < testQueList.size(); j++) {
                if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                    sub_quelist.add(testQueList.get(j));
                }
            }
//            TODO : sort the array with examquestion number
            arrayOfArrays.add(sub_quelist);
        }
        PrepareResult(false);
//        updateQueDB();

    }

    BroadcastReceiver act2InitReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // do your listener event stuff

            String ss = intent.getStringExtra("resultObj");
            String ss1 = intent.getStringExtra("testQueList");
            try {
                JSONObject jsonObject = new JSONObject(ss1);
                JSONArray jsonArray = jsonObject.getJSONArray("array");
                List<OnlineQuestionObj2> updated = new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<OnlineQuestionObj2>>() {
                }.getType());
                for (int i = 0; i < updated.size(); i++) {
                    updated.get(i).setOption1(testQueList.get(i).getOption1());
                    updated.get(i).setOption2(testQueList.get(i).getOption2());
                    updated.get(i).setOption3(testQueList.get(i).getOption3());
                    updated.get(i).setOption4(testQueList.get(i).getOption4());
                }
                testQueList = updated;
                arrayOfArrays = new ArrayList<>();
                for (int i = 0; i < subList.size(); i++) {
                    ArrayList<OnlineQuestionObj2> sub_quelist = new ArrayList<>();
                    for (int j = 0; j < testQueList.size(); j++) {
                        if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                            sub_quelist.add(testQueList.get(j));
                        }
                    }
//            TODO : sort the array with examquestion number
                    arrayOfArrays.add(sub_quelist);
                }
                isObjectiveStart = true;
                PrepareResult(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            arrayOfArrays = new Gson().fromJson(ss, new TypeToken<ArrayList<ArrayList<OnlineQuestionObj2>>>() {
//            }.getType());

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(act2InitReceiver);
    }

    /* Updating Records to Database */
    public void updateQueDB(JSONArray params) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            Gson gson = new Gson();

            String obj = gson.toJson(
                    objectiveQues,
                    new TypeToken<ArrayList<DescTestCategory>>() {
                    }.getType());
            String descr = gson.toJson(
                    descriptiveQues,
                    new TypeToken<ArrayList<DescCategory>>() {
                    }.getType());

            if (insertId != null && !insertId.equalsIgnoreCase("")) {
                jsonObject.put("_id", insertId);
                jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
                jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
                jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
                jsonObject.put("eStatus", eStautus);
                jsonObject.put("isObjectStarted", isObjectiveStart);
                jsonObject.put("descTestCategories", params);
                jsonObject.put("descCategories", new JSONArray(descr));
//                jsonObject.put("studentTestFilePath", "NA");


            } else {
                jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
                jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
                jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
                eStautus = "INPROGRESS";
                jsonObject.put("eStatus", eStautus);
                jsonObject.put("isObjectStarted", isObjectiveStart);
                jsonObject.put("ePath", getIntent().getStringExtra("testFilePath"));
                jsonObject.put("eName", getIntent().getStringExtra("testName"));
                jsonObject.put("sDate", getIntent().getStringExtra("examSTime"));
                jsonObject.put("eDate", getIntent().getStringExtra("examETime"));
                jsonObject.put("rDate", getIntent().getStringExtra("examRTime"));
                jsonObject.put("descTestCategories", params);
                jsonObject.put("descCategories", new JSONArray(descr));
//                jsonObject.put("studentTestFilePath", _finalUrl);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG, "_finalUrl Json " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request;
        if (insertId != null && !insertId.equalsIgnoreCase("")) {
            request = new Request.Builder()
                    .url(AppUrls.DUPDATESTATUSBYID)
                    .put(body)
                    .build();
            isINPFirst = false;
        } else {
            request = new Request.Builder()
                    .url(AppUrls.DUPDATESTUDENTEXAMSTATUS)
                    .post(body)
                    .build();
            isINPFirst = true;
        }

        utils.showLog(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonResp = response.body().string();
                    utils.showLog(TAG, "_finalUrl responseBody - " + jsonResp);
                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = ParentjObject.getJSONObject("result");
                            insertId = jsonObject.getString("insertedId");
                            runOnUiThread(() -> {
                                Toast.makeText(DescriptiveSectionDetails.this, "Sync Completed", Toast.LENGTH_SHORT).show();
                                if (isINPFirst) {
                                    updateLiveExamStatus();
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /* Get DB Question Answers and Merge Questions and Previous Answers based on Question Id  */
    void getDBTestQuestions() {
        ProgressDialog loading = new ProgressDialog(DescriptiveSectionDetails.this);
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

        Log.v(TAG, "StudentDBTestQuestions Url - " + AppUrls.DGETSTATUSBYID + "?examDocId=" + examdetId + "&schemaName=" + sh_Pref.getString("schema", ""));

        Request request = new Request.Builder()
                .url(AppUrls.DGETSTATUSBYID + "?examDocId=" + examdetId + "&schemaName=" + sh_Pref.getString("schema", ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                new AlertDialog.Builder(DescriptiveSectionDetails.this)
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
                            insertId = jsonObject.getString("_id");
                            JSONArray jsonArr = jsonObject.getJSONArray("descTestCategories");
                            JSONArray jsonArray = jsonObject.getJSONArray("descCategories");
                            descriptiveQues.clear();
                            objectiveQues.clear();

                            descriptiveQues.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<DescCategory>>() {
                            }.getType()));
                            objectiveQues.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<DescTestCategory>>() {
                            }.getType()));

                            testQueDetailsList.clear();
                            testQueDetailsList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            isObjectiveStart = jsonObject.getBoolean("isObjectStarted");

                            Log.v(TAG, "StudentDBTestQuestions testQueDetailsList - " + testQueDetailsList.size());

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
                                runOnUiThread(() -> prepareQuestionPaper());

                            } else {
                                runOnUiThread(() -> prepareQuestionPaper());
                            }
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


    /*  Updating Test Status  */
    void updateLiveExamStatus() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("eStatus", eStautus);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG, "TestGetQue Url - " + AppUrls.DUPDATELIVEEXAMSTATUS);
        utils.showLog(TAG, "TestGetQue URL LogingObj - " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.DUPDATELIVEEXAMSTATUS)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });

    }


    /* Finally We are saving results to server  */
    void saveResultstoServer(JSONArray insertRecords) {
        ProgressDialog loading = new ProgressDialog(DescriptiveSectionDetails.this);
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
        String descr = new Gson().toJson(
                descriptiveQues,
                new TypeToken<ArrayList<DescCategory>>() {
                }.getType());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", insertId);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
            jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
            if (eStautus.equalsIgnoreCase("SUBMITTED"))
                isSUBFirst = false;
            else isSUBFirst = true;
            eStautus = "SUBMITTED";
            jsonObject.put("eStatus", eStautus);
            jsonObject.put("descTestCategories", insertRecords);
            jsonObject.put("descCategories", new JSONArray(descr));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.v(TAG, "submitted" + jsonObject.toString());


        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.UPDATESTATUSBYID)
                .put(body)
                .build();

        Log.v(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();
                        Log.v(TAG, "responseBody - " + jsonResp);
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            if (isSUBFirst) {
                                updateLiveExamStatus();
                            }
//                            stopCountDownTimer();
//                            if (timer != null) {
//                                timer.cancel();
//                                timer = null;
//                            }
//                            int tunans = 0, tcans = 0, twans = 0;
//                            for (int i = 0; i < arrayOfArrays.size(); i++) {
//                                for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
//                                    if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase("")) {
//                                        tunans++;
//
//                                    } else if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase(arrayOfArrays.get(i).get(j).getCorrectAnswer())) {
//                                        tcans++;
//
//                                    } else {
//                                        twans++;
//
//                                    }
//                                }
//                            }

                            Intent intent = new Intent(DescriptiveSectionDetails.this, OTStudentTestOnlineCompleted.class);
                            intent.putExtra("studentId", getIntent().getIntExtra("studentId", 0));
                            intent.putExtra("testId", getIntent().getIntExtra("testId", 0));
                            intent.putExtra("jeeSectionTemplate", getIntent().getStringExtra("jeeSectionTemplate"));
                            intent.putExtra("testFilePath", getIntent().getStringExtra("testFilePath"));
//                            intent.putExtra("studentTestFilePath", _finalUrl);
                            intent.putExtra("testName", getIntent().getStringExtra("testName"));
                            intent.putExtra("nav", true);
                            startActivity(intent);
                            finish();

                        } else {
//                        TODO : Save the values in DB
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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


    /* Preparing Result Object to Update Records in S3 & DataBase based on DB Syncing Time */
    private void PrepareResult(Boolean testFinal) {

        JSONArray oldinsertRecords = new JSONArray();

        for (int i = 0; i < arrayOfArrays.size(); i++) {
            for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
                JSONObject values = prepareJsonObj(arrayOfArrays.get(i).get(j));
                oldinsertRecords.put(values);
            }
        }

        ArrayList<OnlineQuestionObj2> insertList = new Gson().fromJson(oldinsertRecords.toString(),
                new TypeToken<List<OnlineQuestionObj2>>() {
                }.getType());

        Log.v(TAG, "insertList - " + insertList.size());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            insertList.sort((o1, o2) -> o1.getQuestionDisplayOrder().compareTo(o2.getQuestionDisplayOrder()));
        } else {
            Collections.sort(insertList, new Comparator<OnlineQuestionObj2>() {
                public int compare(OnlineQuestionObj2 o1, OnlineQuestionObj2 o2) {
                    return o1.getQuestionDisplayOrder().compareTo(o2.getQuestionDisplayOrder());
                }
            });
        }

        JSONArray insertRecords = null;
        try {
            insertRecords = new JSONArray(new GsonBuilder().create().toJson(insertList, new TypeToken<ArrayList<DescriptiveObjTestActivity.ResultObj>>() {
            }.getType()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < insertRecords.length(); i++) {
            try {
                if (insertRecords.getJSONObject(i).getString("option1").equalsIgnoreCase("NA")) {
                    insertRecords.getJSONObject(i).remove("option1");
                }
                if (insertRecords.getJSONObject(i).getString("option2").equalsIgnoreCase("NA")) {
                    insertRecords.getJSONObject(i).remove("option2");
                }
                if (insertRecords.getJSONObject(i).getString("option3").equalsIgnoreCase("NA")) {
                    insertRecords.getJSONObject(i).remove("option3");
                }
                if (insertRecords.getJSONObject(i).getString("option4").equalsIgnoreCase("NA")) {
                    insertRecords.getJSONObject(i).remove("option4");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        updateQueDB(insertRecords);

        if (testFinal) {
            if (NetworkConnectivity.isConnected(this)) {
                saveResultstoServer(insertRecords);
            } else {
                new AlertDialog.Builder(DescriptiveSectionDetails.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Your internet seems to be down at the moment.Please connect and try again to submit the results")
                        .setPositiveButton("TryAgain", (dialog, which) -> {
//                            super.onBackPressed();
//                                finish();
                            PrepareResult(true);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // do nothing
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            }
        }

    }


    /* Preparing JsonObject for Updating records */
    private JSONObject prepareJsonObj(OnlineQuestionObj2 question) {
        boolean is_answered_marked = false;
        boolean is_marked = false;
        boolean not_answered = false;
        String style = question.getStyle();
        ;
        String ans = "";

        if (question.getSelectedAnswer().equalsIgnoreCase("") && !question.isReviewflag()) {
            is_marked = false;
            not_answered = true;
            is_answered_marked = false;

        } else if (question.getSelectedAnswer().equalsIgnoreCase("") && question.isReviewflag()) {
            is_marked = true;
            not_answered = true;
            is_answered_marked = false;

        } else if (!question.getSelectedAnswer().equalsIgnoreCase("") && question.isReviewflag()) {
            is_marked = false;
            not_answered = false;
            is_answered_marked = true;
            ans = question.getSelectedAnswer();
        } else if (!question.getSelectedAnswer().equalsIgnoreCase("") && !question.isReviewflag()) {
            is_marked = false;
            not_answered = false;
            is_answered_marked = false;
            ans = question.getSelectedAnswer();
        }

        JSONObject values = new JSONObject();
        try {
            values.put("correctMarks", correctMarks);
            values.put("wrongMarks", wrongMarks);
//            if (listSectionDetails.size() > 0) {
//                for (int x = 0; x < listSectionDetails.size(); x++) {
////                    Log.v(TAG, "sectionName - " + question.getTestSectionName());
////                    Log.v(TAG, "sectionName - " + listSectionDetails.get(x).getSectionName());
//                    if (question.getTestSectionName().equalsIgnoreCase(listSectionDetails.get(x).getSectionName())) {
//                        values.put("correctMarks", listSectionDetails.get(x).getCorrectAnswerMarks());
//                        values.put("wrongMarks", listSectionDetails.get(x).getWrongAnswerMarks());
//                        values.put("isOptionMarks", listSectionDetails.get(x).getIsOptionMarks());
//                        values.put("partialMarksAvailable", listSectionDetails.get(x).getPartialMarksAvailable());
//                        values.put("partialCorrectMarks", listSectionDetails.get(x).getPartialCorrectMarks());
//                        values.put("partialWrongMarks", listSectionDetails.get(x).getPartialWrongMarks());
//                        values.put("marksUnattempted", listSectionDetails.get(x).getMarksUnattempted());
//                    }
//                }
//                if (question.getQuestType().equalsIgnoreCase("CPQ")) {
//                    values.put("paragraphId", question.getParagraphId());
//                    values.put("questionMapId", question.getQuestionMapId());
//                }
//
//                values.put("question_number", question.getQuestion_number());
//                values.put("questionDisplayOrder", question.getQuestionDisplayOrder());
//                values.put("questionId", question.getQuestionId());
//                values.put("correctAnswer", question.getCorrectAnswer());
//                values.put("selectedAnswer", ans);
//                values.put("style", style);
//                values.put("timeTaken", "" + question.getTimeTaken());
//                values.put("subjectGroup", question.getSubjectGroup());
//                values.put("subjectName", question.getSubjectName());
//                values.put("questType", question.getQuestType());
//                values.put("testCategory", testCategory);
//                values.put("updated", question.getUpdated());
//                values.put("chapterName", question.getChapterName());
//                values.put("topic_name", question.getTopic_name());
//                values.put("chapterId", question.getChapterId());
//                values.put("topicId", question.getTopicId());
//                values.put("subjectId", question.getSubjectId());
//                values.put("branchId", sObj.getMBranchId());
//                values.put("testSectionName", question.getTestSectionName());
//            } else {
            values.put("question_number", question.getQuestion_number());
            values.put("questionDisplayOrder", question.getQuestionDisplayOrder());
            values.put("questionId", question.getQuestionId());
            values.put("correctAnswer", question.getCorrectAnswer());
            values.put("selectedAnswer", ans);
            values.put("style", style);
            values.put("timeTaken", "" + question.getTimeTaken());
            values.put("subjectGroup", question.getSubjectGroup());
            values.put("subjectName", question.getSubjectName());
            values.put("questType", question.getQuestType());
            values.put("correctMarks", question.getCorrectMarks());
            values.put("wrongMarks", wrongMarks);
//                values.put("testCategory", testCategory);
            values.put("updated", question.getUpdated());
            values.put("chapterName", question.getChapterName());
            values.put("topic_name", question.getTopic_name());
            values.put("chapterId", question.getChapterId());
            values.put("topicId", question.getTopicId());
            values.put("subjectId", question.getSubjectId());
            values.put("examSectionId", Integer.valueOf(question.getExamSectionId()));
//                values.put("branchId", sObj.getMBranchId());
            values.put("testSectionName", question.getTestSectionName());
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;

    }


}