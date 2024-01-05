package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.services.s3.AmazonS3Client;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.models.DescCategory;
import myschoolapp.com.gsnedutech.descriptive.models.StudentAnswer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherDescriptiveQueSubmit extends AppCompatActivity {

    private static final String TAG = TeacherDescriptiveQueSubmit.class.getName();

    MyUtils utils = new MyUtils();

    int removeQuestionId = -1;

    @BindView(R.id.ll_marks)
    LinearLayout llMarks;

    @BindView(R.id.ll_show_answer)
    LinearLayout llShowAnswer;

    @BindView(R.id.cwv_question)
    CustomWebview cwvQuestion;

    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    @BindView(R.id.rv_prevFiles)
    RecyclerView rvPrevFiles;

    @BindView(R.id.et_marks)
    EditText etMarks;

    @BindView(R.id.spinner_marks)
    Spinner spinnerMarks;

    @BindView(R.id.et_comment)
    EditText etComment;

    List<StudentAnswer> answers = new ArrayList<>();

    class FileUri{
        FileUri(Uri fileUri, int fileOrder){
            this.uri = fileUri;
            this.order = fileOrder;
        }
        Uri uri;
        Integer order;
    }

    List<FileUri> mImageUri = new ArrayList<>();
    AmazonS3Client s3Client1;
    List<String> fileName = new ArrayList<>();
    List <String> keyName = new ArrayList<>();
    DescCategory descQuestionObj = new DescCategory();
    DescriptiveSectionDetails.DescExam exam;

    String baseUrl = "";
    private Uri picUri;
    String schemaName = "";
    int ExamId = 0, studentId = 0, Questionid = 0;

    String[] decimals = {"0", "25", "50", "75"};
    String[] decimalsCheck = {"0", "25", "5", "75"};

    boolean isStudent = false;


    int questionMarks = 0;
    SharedPreferences sh_Pref;

    String regex = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_descriptive_que_submit);
        ButterKnife.bind(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();



        if (descQuestionObj.getStudentAnswer().size()>0){
            answers = descQuestionObj.getStudentAnswer();
            rvPrevFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
            rvPrevFiles.setAdapter(new PrevAdapter());
        }
        else {
            findViewById(R.id.tv_prev_head).setVisibility(View.GONE);
        }


        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());

        findViewById(R.id.tv_enable).setOnClickListener(v -> {
            showAlertDialog();
        });

        tvSubmit.setOnClickListener(v -> {
            if (etMarks.length()!=0 && etComment.getText().toString().trim().length()>0){
                updateMarks();
            }
            else if (etMarks.length()==0 && etComment.getText().toString().trim().length()==0) {
                Toast.makeText(this, "Please give the marks or comments for Question!", Toast.LENGTH_SHORT).show();
            }
            else {
                updateMarks();
            }

        });

        if (isStudent){
            tvSubmit.setVisibility(View.GONE);
            etComment.setEnabled(false);
            etMarks.setEnabled(false);
            spinnerMarks.setEnabled(false);
        }
    }

    private void updateMarks() {

        String marks = "";
        if(etMarks.getText().toString().trim().length()>0){
            marks= etMarks.getText().toString().trim()+"."+ spinnerMarks.getSelectedItem();
        }
        else marks ="0";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
            jsonObject.put("questionId", descQuestionObj.getQuestionId());
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("examId", ExamId);
            jsonObject.put("examSectionId", Integer.valueOf(descQuestionObj.getExamSectionId()));
            if (Float.parseFloat(marks)>0)
            jsonObject.put("marks",Float.parseFloat(marks));
            jsonObject.put("isCompleted", 0);
//            if (etComment.getText().toString().trim().length()>0)
            jsonObject.put("comments", etComment.getText().toString().trim());



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

        String finalMarks = marks;
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
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(TeacherDescriptiveQueSubmit.this, ParentjObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    setResulttoPrevious(finalMarks);
                                } catch (JSONException e) {
                                    e.printStackTrace();
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

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        schemaName = sh_Pref.getString("schema", "");
        if (sh_Pref.getBoolean("student_loggedin", false)){
            isStudent = true;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter(TeacherDescriptiveQueSubmit.this, R.layout.spinner_test_item, decimals);

        /* Drop down layout style - list view with radio button */
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* attaching data adapter to spinner */
        spinnerMarks.setAdapter(dataAdapter);


        exam = (DescriptiveSectionDetails.DescExam) getIntent().getSerializableExtra("questionMarks");


        questionMarks = Integer.parseInt(exam.getQuestionMarks());
        String marks = exam.getQuestionMarks();
        if (marks.length()==2){
            char start = marks.charAt(0) ;
            char end = marks.charAt(1);
            regex = "^-?\\d{0}[0-"+start+"]?[0-"+end+"]?$";
        }
        else if (marks.length() == 1){
            regex = "^-?\\d{0}[0-"+marks+"]?$";
        }
        descQuestionObj  = (DescCategory) getIntent().getSerializableExtra("questionObj");
        checkAnswers();
        ExamId = getIntent().getIntExtra("examId", 0);
        studentId = getIntent().getIntExtra("studentId", 0);
        Questionid = descQuestionObj.getQuestionId();
        baseUrl = "descriptive/"+schemaName+"/"+ExamId+"/"+studentId+"/"+Questionid+"/";
        if (descQuestionObj.getResultMarks()>0){
//            etMarks.setText(""+descQuestionObj.getResultMarks());
            String marksWithDec = String.valueOf(descQuestionObj.getResultMarks());
            String[] marksDec = marksWithDec.split("\\.");
            etMarks.setText(marksDec[0]);

            for(int i=0; i< decimalsCheck.length; i++){
                if (decimalsCheck[i].equalsIgnoreCase(marksDec[1])){
                    spinnerMarks.setSelection(i);
                }
            }
            if (Integer.parseInt(marksDec[0]) == descQuestionObj.getMarks()){
                spinnerMarks.setSelection(0);
                spinnerMarks.setEnabled(false);
            }
            else {
                spinnerMarks.setEnabled(true);
            }


        }
        if (!descQuestionObj.getComments().isEmpty()){
            etComment.setText(descQuestionObj.getComments());
        }

        cwvQuestion.setText(getIntent().getStringExtra("question"));

        /* ITQ Text Change Listner */
        etMarks.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();
                if (length>0 && Integer.parseInt(text) == descQuestionObj.getMarks()){
                    spinnerMarks.setSelection(0);
                    spinnerMarks.setEnabled(false);
                }
                else {
                    spinnerMarks.setEnabled(true);
                }
                if (length > 0 && !Pattern.matches(regex, text)) {
                    s.delete(length - 1, length);
                    Toast.makeText(TeacherDescriptiveQueSubmit.this, "Max marks is "+descQuestionObj.getMarks(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkAnswers() {
        int mandatory = 0;
        if (exam.getOptionalQuestions().isEmpty() || exam.getOptionalQuestions().equalsIgnoreCase("0")){
            mandatory = Integer.parseInt(exam.getNumQuestions());
        }
        else {
            if (exam.getOptionalQuestions()!=null) {
                mandatory = Integer.parseInt(exam.getNumQuestions()) - Integer.parseInt(exam.getOptionalQuestions());
            }
        }
        int answered = 0;

        for (int i=0; i< exam.getQuestions().size();i++){
            if (exam.getQuestions().get(i).getQuestioncorrectmark()>0){
                answered++;
            }
        }

        if (!isStudent) {
            if (mandatory == answered) {
                if (descQuestionObj.getResultMarks() > 0) {
                    llMarks.setVisibility(View.VISIBLE);
                    llShowAnswer.setVisibility(View.GONE);
                } else {
                    llMarks.setVisibility(View.GONE);
                    llShowAnswer.setVisibility(View.VISIBLE);
                }
            } else {
                llMarks.setVisibility(View.VISIBLE);
                llShowAnswer.setVisibility(View.GONE);
            }
        }
        else {
            llMarks.setVisibility(View.VISIBLE);
            llShowAnswer.setVisibility(View.GONE);
            spinnerMarks.setEnabled(false);
        }
    }


    private void setResulttoPrevious(String marks) {
        List<StudentAnswer> answers = descQuestionObj.getStudentAnswer();

        descQuestionObj.setComments(etComment.getText().toString().trim());
        descQuestionObj.setResultMarks(Float.parseFloat(marks));
        descQuestionObj.setStudentAnswer(answers);
        Intent intent = new Intent(TeacherDescriptiveQueSubmit.this, TeacherDescriptiveSectionDetail.class);
        intent.putExtra("resultObj", descQuestionObj);
        if (removeQuestionId!=-1) {
            intent.putExtra("questionId", removeQuestionId);
            removeQuestionId = -1;
        }
        intent.putExtra("pos", getIntent().getIntExtra("pos",0));
        setResult(4321, intent);
        onBackPressed();

//        Intent intent = new Intent(DescriptiveQueSubmit.this, DescriptiveSectionDetails.class);
//        intent.putExtra("resultObj", descQuestionObj);
//        intent.putExtra("pos", getIntent().getIntExtra("pos",0));
//        setResult(4321, intent);
//        onBackPressed();
    }



    @Override
    public void onBackPressed() {
        if (removeQuestionId==-1){
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
        else if (removeQuestionId>0){
            Intent intent = new Intent(TeacherDescriptiveQueSubmit.this, TeacherDescriptiveSectionDetail.class);
            intent.putExtra("resultObj", descQuestionObj);
            intent.putExtra("questionId", removeQuestionId);
            intent.putExtra("pos", getIntent().getIntExtra("pos",0));
            setResult(4321, intent);
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }


    }

    class PrevAdapter extends RecyclerView.Adapter<PrevAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherDescriptiveQueSubmit.this).inflate(R.layout.item_article_files,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvFileName.setText(URLUtil.guessFileName(answers.get(position).getPath(),null,null));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(TeacherDescriptiveQueSubmit.this, ImageDisp.class);
                intent.putExtra("path", answers.get(position).getPath());
                startActivity(intent);
            });
            holder.cvDel.setVisibility(View.GONE);
            holder.cvDel.setOnClickListener(v -> {
                answers.remove(position);
                if (answers.size()==0){
                    findViewById(R.id.tv_prev_head).setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return answers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFile;
            TextView tvFileName;
            CardView cvDel;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                cvDel = itemView.findViewById(R.id.cv_del);

            }
        }
    }


    Dialog dialog;
    void showAlertDialog(){
        Rect displayRectangle = new Rect();
        Window window = TeacherDescriptiveQueSubmit.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog = new Dialog(TeacherDescriptiveQueSubmit.this);
        dialog.setContentView(R.layout.dialog_desc_ans_error);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog.isShowing()) dialog.dismiss();
        dialog.show();

        RecyclerView rvQAns = dialog.findViewById(R.id.rv_qans);
        rvQAns.setLayoutManager(new LinearLayoutManager(this));
        rvQAns.setAdapter(new QAnsAdapter(exam.getQuestions()));

        TextView tvNext = dialog.findViewById(R.id.tv_next);

        tvNext.setOnClickListener(view -> {
            dialog.dismiss();
        });
    }

    private class QAnsAdapter extends RecyclerView.Adapter<QAnsAdapter.ViewHolder> {
        List<DescriptiveSectionDetails.Question> questions;
        public QAnsAdapter(List<DescriptiveSectionDetails.Question> questions) {
            this.questions = questions;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherDescriptiveQueSubmit.this).inflate(R.layout.item_qans, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.qNo.setText("("+(position+1)+")");
            holder.qMarks.setText(""+questions.get(position).getQuestioncorrectmark());
            if (questions.get(position).getQuestioncorrectmark()>0){
                holder.ansDelete.setVisibility(View.VISIBLE);
            }
            else {
                holder.ansDelete.setVisibility(View.GONE);
            }
            holder.ansDelete.setOnClickListener(v -> {
                AlertDialog dialog = new AlertDialog.Builder(TeacherDescriptiveQueSubmit.this)
                        .setTitle("Descriptive Exams")
                        .setMessage("Are you sure you want to remove marks for this Question")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // call api method
                                removeMarks(questions.get(position));

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView qNo, qMarks, ansDelete;

            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                qMarks = itemView.findViewById(R.id.tv_ans);
                ansDelete = itemView.findViewById(R.id.tv_del);
                qNo = itemView.findViewById(R.id.tv_qno);
            }
        }
    }


    private void removeMarks(DescriptiveSectionDetails.Question question) {

        utils.showLoader(TeacherDescriptiveQueSubmit.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
            jsonObject.put("questionId", question.getDescriptiveQuestionId());
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("examId", ExamId);
            jsonObject.put("examSectionId", question.getExamSectionId().intValue());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG, "_finalUrl Json " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.DRemoveMarks)
                .put(body)
                .build();


        utils.showLog(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
                if (response.body() != null) {
                    String jsonResp = response.body().string();
                    utils.showLog(TAG, "_finalUrl responseBody - " + jsonResp);
                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(TeacherDescriptiveQueSubmit.this, ParentjObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    removeQuestionId = question.getDescriptiveQuestionId();
                                    llMarks.setVisibility(View.VISIBLE);
                                    llShowAnswer.setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
}