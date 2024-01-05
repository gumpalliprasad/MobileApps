package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddQuizQuestion;
import myschoolapp.com.gsnedutech.Arena.ArQuizApprovalActivity;
import myschoolapp.com.gsnedutech.Arena.Models.ArQuizMCQ;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ArQuizAllTypeQuestion extends Fragment  {

    View viewArQuizAllTypeQuestion;
    Unbinder unbinder;
    
    Activity mActivity;

    @BindView(R.id.rv_q_num)
    RecyclerView rVQNum;


    View toHide;
    View toDisplay;

    //mcq
    @BindView(R.id.et_mcq_ques)
    EditText etMcqQues;
    @BindView(R.id.ll_show_add_q_image_text)
    LinearLayout llShowAddQImageText;
    @BindView(R.id.iv_q_image)
    ImageView ivQImage;
    @BindView(R.id.et_option_text_a)
    EditText etOptionTextA;
    @BindView(R.id.et_option_text_b)
    EditText etOptionTextB;
    @BindView(R.id.et_option_text_c)
    EditText etOptionTextC;
    @BindView(R.id.et_option_text_d)
    EditText etOptionTextD;
    @BindView(R.id.tv_select_image_a)
    TextView tvSelectImageA;
    @BindView(R.id.tv_select_image_b)
    TextView tvSelectImageB;
    @BindView(R.id.tv_select_image_c)
    TextView tvSelectImageC;
    @BindView(R.id.tv_select_image_d)
    TextView tvSelectImageD;
    @BindView(R.id.iv_selected_image_a)
    ImageView ivSelectedImageA;
    @BindView(R.id.iv_selected_image_b)
    ImageView ivSelectedImageB;
    @BindView(R.id.iv_selected_image_c)
    ImageView ivSelectedImageC;
    @BindView(R.id.iv_selected_image_d)
    ImageView ivSelectedImageD;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.cv_a)
    CardView cvA;
    @BindView(R.id.cv_b)
    CardView cvB;
    @BindView(R.id.cv_c)
    CardView cvC;
    @BindView(R.id.cv_d)
    CardView cvD;
    @BindView(R.id.tv_a)
    TextView tvA;
    @BindView(R.id.tv_b)
    TextView tvB;
    @BindView(R.id.tv_c)
    TextView tvC;
    @BindView(R.id.tv_d)
    TextView tvD;




    //tf
    @BindView(R.id.et_tf_ques)
    EditText etTfQues;
    @BindView(R.id.ll_show_add_tf_q_image_text)
    LinearLayout llShowAddTfQImageText;
    @BindView(R.id.iv_tf_q_image)
    ImageView ivTfQImage;
    @BindView(R.id.tv_tf_time)
    TextView tvTfTime;
    @BindView(R.id.tv_true)
    TextView tvTrue;
    @BindView(R.id.tv_false)
    TextView tvFalse;

    List<ArQuizMCQ> listQuestions = new ArrayList<>();

    int currentPos = 0;

    int numQues=0;

    QuestionNumberAdapter adapter;

    Uri picUri = null;

    AmazonS3Client s3Client1;

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    MyUtils utils = new MyUtils();
    
    public ArQuizAllTypeQuestion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArQuizAllTypeQuestion = inflater.inflate(R.layout.fragment_ar_quiz_all_type_question, container, false);
        unbinder = ButterKnife.bind(this,viewArQuizAllTypeQuestion);



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

        if (mActivity instanceof ArQuizApprovalActivity){

        }else {

            ((ArAddQuizQuestion) mActivity).tvTitle.setText(((ArAddQuizQuestion) mActivity).quizObject.getQuizTitle());

        }
        return  viewArQuizAllTypeQuestion;
    }
    
    void init(){

         sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        }
        else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }

        if (mActivity instanceof ArQuizApprovalActivity){
            List<ArenaQuizQuestionFiles> listQs = ((ArQuizApprovalActivity)mActivity).listQuestions;
            int qNum = ((ArQuizApprovalActivity)mActivity).qNum;

            viewArQuizAllTypeQuestion.findViewById(R.id.ll_save).setVisibility(View.GONE);

            if (listQs.get(qNum).getQuestType().equalsIgnoreCase("MCQ") || listQs.get(qNum).getQuestType().equalsIgnoreCase("MAQ")){
                viewArQuizAllTypeQuestion.findViewById(R.id.ll_quiz).setVisibility(View.VISIBLE);
                viewArQuizAllTypeQuestion.findViewById(R.id.ll_tf).setVisibility(View.GONE);

                if (listQs.get(qNum).getQuestion().contains("~~")){
                    String[] title = listQs.get(qNum).getQuestion().split("~~");
                    etMcqQues.setText(title[0]);

                    for (String s:title){
                        if (s.contains("http")){
                            Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
                        }
                    }

                    ivQImage.setVisibility(View.VISIBLE);
                    llShowAddQImageText.setVisibility(View.GONE);

                }else {
                    etMcqQues.setText(listQs.get(qNum).getQuestion());
                    etMcqQues.setFocusable(false);
                    ivQImage.setVisibility(View.GONE);
                    llShowAddQImageText.setVisibility(View.GONE);
                }

                if (Integer.parseInt(listQs.get(qNum).getQuestTime())<60) {
                    tvTime.setText(listQs.get(qNum).getQuestTime() +"sec");
                }else {
                    tvTime.setText((Integer.parseInt(listQs.get(qNum).getQuestTime())/60) +"min");
                }

                tvSelectImageA.setVisibility(View.GONE);
                tvSelectImageB.setVisibility(View.GONE);
                tvSelectImageC.setVisibility(View.GONE);
                tvSelectImageD.setVisibility(View.GONE);

                if (listQs.get(qNum).getOption1().contains("~~")){
                    etOptionTextA.setVisibility(View.GONE);
                    ivSelectedImageA.setVisibility(View.VISIBLE);
                    viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);

                    String[] str =  listQs.get(qNum).getOption1().split("~~");
                    for (String s:str){
                        if (s.contains("http")){
                            Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageA);
                        }
                    }
                }
                else {
                    etOptionTextA.setVisibility(View.VISIBLE);
                    ivSelectedImageA.setVisibility(View.GONE);
                    etOptionTextA.setText(listQs.get(qNum).getOption1());
                    etOptionTextA.setFocusable(false);
                    viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                }

                if (listQs.get(qNum).getOption2().contains("~~")){
                    etOptionTextB.setVisibility(View.GONE);
                    ivSelectedImageB.setVisibility(View.VISIBLE);
                    viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                    String[] str =  listQs.get(qNum).getOption2().split("~~");
                    for (String s:str){
                        if (s.contains("http")){
                            Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageB);
                        }
                    }
                }
                else {
                    etOptionTextB.setVisibility(View.VISIBLE);
                    ivSelectedImageB.setVisibility(View.GONE);
                    etOptionTextB.setText(listQs.get(qNum).getOption2());
                    etOptionTextB.setFocusable(false);
                    viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                }

                if (listQs.get(qNum).getOption3().contains("~~")){
                    etOptionTextC.setVisibility(View.GONE);
                    ivSelectedImageC.setVisibility(View.VISIBLE);
                    viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                    String[] str =  listQs.get(qNum).getOption3().split("~~");
                    for (String s:str){
                        if (s.contains("http")){
                            Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageC);
                        }
                    }
                }
                else {
                    etOptionTextC.setVisibility(View.VISIBLE);
                    ivSelectedImageC.setVisibility(View.GONE);
                    etOptionTextC.setText(listQs.get(qNum).getOption3());
                    etOptionTextC.setFocusable(false);
                    viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                }

                if (listQs.get(qNum).getOption4().contains("~~")){
                    etOptionTextD.setVisibility(View.GONE);
                    ivSelectedImageD.setVisibility(View.VISIBLE);
                    viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                    String[] str =  listQs.get(qNum).getOption4().split("~~");
                    for (String s:str){
                        if (s.contains("http")){
                            Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageD);
                        }
                    }
                }
                else {
                    etOptionTextD.setVisibility(View.VISIBLE);
                    ivSelectedImageD.setVisibility(View.GONE);
                    viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    etOptionTextD.setText(listQs.get(qNum).getOption4());
                    etOptionTextD.setFocusable(false);
                }

                if(!listQs.get(qNum).getAnswer().contains(",")){
                    switch (listQs.get(qNum).getAnswer()){
                        case "A":
                            tvA.setBackgroundColor (Color.parseColor("#1FA655"));
                            tvA.setTextColor(Color.WHITE);
                            break;
                        case "B":
                            tvB.setBackgroundColor (Color.parseColor("#1FA655"));
                            tvB.setTextColor(Color.WHITE);
                            break;
                        case "C":
                            tvC.setBackgroundColor (Color.parseColor("#1FA655"));
                            tvC.setTextColor(Color.WHITE);
                            break;
                        case "D":
                            tvD.setBackgroundColor (Color.parseColor("#1FA655"));
                            tvD.setTextColor(Color.WHITE);
                            break;
                    }
                }else {
                    for (String s:listQs.get(qNum).getAnswer().split(",")){
                        switch (s){
                            case "A":
                                tvA.setBackgroundColor (Color.parseColor("#1FA655"));
                                tvA.setTextColor(Color.WHITE);
                                break;
                            case "B":
                                tvB.setBackgroundColor (Color.parseColor("#1FA655"));
                                tvB.setTextColor(Color.WHITE);
                                break;
                            case "C":
                                tvC.setBackgroundColor (Color.parseColor("#1FA655"));
                                tvC.setTextColor(Color.WHITE);
                                break;
                            case "D":
                                tvD.setBackgroundColor (Color.parseColor("#1FA655"));
                                tvD.setTextColor(Color.WHITE);
                                break;
                        }
                    }
                }

            }
            if (listQs.get(qNum).getQuestType().equalsIgnoreCase("TOF")){

                viewArQuizAllTypeQuestion.findViewById(R.id.ll_quiz).setVisibility(View.GONE);
                viewArQuizAllTypeQuestion.findViewById(R.id.ll_tf).setVisibility(View.VISIBLE);

                if (Integer.parseInt(listQs.get(qNum).getQuestTime())<60) {
                    tvTfTime.setText(listQs.get(qNum).getQuestTime() +"sec");
                }else {
                    tvTfTime.setText((Integer.parseInt(listQs.get(qNum).getQuestTime())/60) +"min");
                }

                if (listQs.get(qNum).getQuestion().contains("~~")){
                    String[] title = listQs.get(qNum).getQuestion().split("~~");
                    etTfQues.setText(title[0]);

                    for (String s:title){
                        if (s.contains("http")){
                            Picasso.with(mActivity).load(s).placeholder(R.drawable.ic_arena_img)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivTfQImage);
                        }
                    }

                    ivTfQImage.setVisibility(View.VISIBLE);
                    llShowAddTfQImageText.setVisibility(View.GONE);

                }
                else {
                    etTfQues.setText(listQs.get(qNum).getQuestion());
                    etTfQues.setFocusable(false);
                    ivTfQImage.setVisibility(View.GONE);
                    llShowAddTfQImageText.setVisibility(View.GONE);
                }

                switch (listQs.get(qNum).getAnswer().toLowerCase()){
                    case "true":
                        tvTrue.setBackgroundColor(Color.parseColor("#1FA655"));
                        tvTrue.setTextColor(Color.WHITE);
                        break;
                    case "false":
                        tvFalse.setBackgroundColor(Color.parseColor("#1FA655"));
                        tvFalse.setTextColor(Color.WHITE);
                        break;
                }

            }

        }
        else {

                numQues = ((ArAddQuizQuestion) mActivity).quizObject.getNumberOfQuestions();


            if (((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().size() > 0) {
                listQuestions.addAll(((ArAddQuizQuestion) mActivity).quizObject.getListMCQ());
            } else {
                listQuestions.add(new ArQuizMCQ());
                listQuestions.get(0).setQuestionType(((ArAddQuizQuestion) mActivity).typeFirst);
            }


            rVQNum.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
            adapter = new QuestionNumberAdapter(numQues);
            rVQNum.setAdapter(adapter);

            tvTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvTrue.setBackgroundColor(Color.parseColor("#1FA655"));
                    tvTrue.setTextColor(Color.WHITE);
                    tvFalse.setBackgroundColor(Color.WHITE);
                    tvFalse.setTextColor(Color.BLACK);
                    listQuestions.get(currentPos).setIsCorrect("true");
                }
            });
            tvFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvFalse.setBackgroundColor(Color.parseColor("#1FA655"));
                    tvFalse.setTextColor(Color.WHITE);
                    tvTrue.setBackgroundColor(Color.WHITE);
                    tvTrue.setTextColor(Color.BLACK);
                    listQuestions.get(currentPos).setIsCorrect("false");
                }
            });

            etOptionTextA.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else if (etOptionTextB.getText().toString().length() > 0 || etOptionTextC.getText().toString().length() > 0 || etOptionTextD.getText().toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.VISIBLE);
                    }
                }
            });
            etOptionTextB.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else if (etOptionTextA.getText().toString().length() > 0 || etOptionTextC.getText().toString().length() > 0 || etOptionTextD.getText().toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.VISIBLE);
                    }
                }
            });
            etOptionTextC.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else if (etOptionTextB.getText().toString().length() > 0 || etOptionTextA.getText().toString().length() > 0 || etOptionTextD.getText().toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.VISIBLE);
                    }
                }
            });
            etOptionTextD.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else if (etOptionTextB.getText().toString().length() > 0 || etOptionTextC.getText().toString().length() > 0 || etOptionTextA.getText().toString().length() > 0) {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.GONE);
                    } else {
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_a).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_b).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_c).setVisibility(View.VISIBLE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.fl_d).setVisibility(View.VISIBLE);
                    }
                }
            });

            loadQuestion();


            viewArQuizAllTypeQuestion.findViewById(R.id.ll_add_q_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    toHide = llShowAddQImageText;
                    toDisplay = ivQImage;

//                    final Dialog dialog = new Dialog(mActivity);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_imgselector);
//                    dialog.setCancelable(true);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int wwidth = metrics.widthPixels;
//                    dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    TextView tvCam = dialog.findViewById(R.id.tv_cam);
//                    tvCam.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
////                fromGallery = false;
//                            try {
//
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
//                                File imageFile = new File(imageFilePath);
//                                picUri = Uri.fromFile(imageFile); // convert path to Uri
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                                startActivityForResult(takePictureIntent, 2);
//
//                            } catch (ActivityNotFoundException anfe) {
//                                //display an error message
//                                String errorMessage = "Whoops - your device doesn't support capturing images!";
//                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//
//                    TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
//                    tvGallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                fromGallery = true;
//                            Intent intent_upload = new Intent();
//                            intent_upload.setType("image/*");
//                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent_upload, 1);
//                            dialog.dismiss();
//                        }
//                    });

//                    dialog.show();
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, 1);
                }
            });

            tvSelectImageA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toHide = tvSelectImageA;
                    toDisplay = ivSelectedImageA;

                      final Dialog dialog = new Dialog(mActivity);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_imgselector);
//                    dialog.setCancelable(true);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int wwidth = metrics.widthPixels;
//                    dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    TextView tvCam = dialog.findViewById(R.id.tv_cam);
//                    tvCam.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
////                fromGallery = false;
//                            try {
//
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
//                                File imageFile = new File(imageFilePath);
//                                picUri = Uri.fromFile(imageFile); // convert path to Uri
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                                startActivityForResult(takePictureIntent, 2);
//
//                            } catch (ActivityNotFoundException anfe) {
//                                //display an error message
//                                String errorMessage = "Whoops - your device doesn't support capturing images!";
//                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//
//                    TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
//                    tvGallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                fromGallery = true;
//                            Intent intent_upload = new Intent();
//                            intent_upload.setType("image/*");
//                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent_upload, 1);
//                            dialog.dismiss();
//                        }
//                    });

//                    dialog.show();
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, 1);
                }
            });

            tvSelectImageB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toHide = tvSelectImageB;
                    toDisplay = ivSelectedImageB;

                      final Dialog dialog = new Dialog(mActivity);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_imgselector);
//                    dialog.setCancelable(true);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int wwidth = metrics.widthPixels;
//                    dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    TextView tvCam = dialog.findViewById(R.id.tv_cam);
//                    tvCam.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
////                fromGallery = false;
//                            try {
//
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
//                                File imageFile = new File(imageFilePath);
//                                picUri = Uri.fromFile(imageFile); // convert path to Uri
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                                startActivityForResult(takePictureIntent, 2);
//
//                            } catch (ActivityNotFoundException anfe) {
//                                //display an error message
//                                String errorMessage = "Whoops - your device doesn't support capturing images!";
//                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//
//                    TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
//                    tvGallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                fromGallery = true;
//                            Intent intent_upload = new Intent();
//                            intent_upload.setType("image/*");
//                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent_upload, 1);
//                            dialog.dismiss();
//                        }
//                    });

//                    dialog.show();
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, 1);
                }
            });

            tvSelectImageC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toHide = tvSelectImageC;
                    toDisplay = ivSelectedImageC;

                      final Dialog dialog = new Dialog(mActivity);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_imgselector);
//                    dialog.setCancelable(true);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int wwidth = metrics.widthPixels;
//                    dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    TextView tvCam = dialog.findViewById(R.id.tv_cam);
//                    tvCam.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
////                fromGallery = false;
//                            try {
//
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
//                                File imageFile = new File(imageFilePath);
//                                picUri = Uri.fromFile(imageFile); // convert path to Uri
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                                startActivityForResult(takePictureIntent, 2);
//
//                            } catch (ActivityNotFoundException anfe) {
//                                //display an error message
//                                String errorMessage = "Whoops - your device doesn't support capturing images!";
//                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//
//                    TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
//                    tvGallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                fromGallery = true;
//                            Intent intent_upload = new Intent();
//                            intent_upload.setType("image/*");
//                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent_upload, 1);
//                            dialog.dismiss();
//                        }
//                    });

//                    dialog.show();
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, 1);
                }
            });

            tvSelectImageD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toHide = tvSelectImageD;
                    toDisplay = ivSelectedImageD;

                      final Dialog dialog = new Dialog(mActivity);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_imgselector);
//                    dialog.setCancelable(true);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int wwidth = metrics.widthPixels;
//                    dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    TextView tvCam = dialog.findViewById(R.id.tv_cam);
//                    tvCam.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
////                fromGallery = false;
//                            try {
//
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
//                                File imageFile = new File(imageFilePath);
//                                picUri = Uri.fromFile(imageFile); // convert path to Uri
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                                startActivityForResult(takePictureIntent, 2);
//
//                            } catch (ActivityNotFoundException anfe) {
//                                //display an error message
//                                String errorMessage = "Whoops - your device doesn't support capturing images!";
//                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//
//                    TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
//                    tvGallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                fromGallery = true;
//                            Intent intent_upload = new Intent();
//                            intent_upload.setType("image/*");
//                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent_upload, 1);
//                            dialog.dismiss();
//                        }
//                    });

//                    dialog.show();
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, 1);
                }
            });


            tvTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(mActivity);
                    dialog.setContentView(R.layout.dialog_time_arena);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();


                    dialog.findViewById(R.id.tv_op_1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTime.setText("5 sec");
                            listQuestions.get(currentPos).setQuestionTime("5");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.tv_op_2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTime.setText("30 sec");
                            listQuestions.get(currentPos).setQuestionTime("30");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.tv_op_3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTime.setText("1 min");
                            listQuestions.get(currentPos).setQuestionTime("60");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.tv_op_4).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTime.setText("2 min");
                            listQuestions.get(currentPos).setQuestionTime("120");
                            dialog.dismiss();
                        }
                    });

                }
            });

            tvTfTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(mActivity);
                    dialog.setContentView(R.layout.dialog_time_arena);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();


                    dialog.findViewById(R.id.tv_op_1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTfTime.setText("5 sec");
                            listQuestions.get(currentPos).setQuestionTime("5");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.tv_op_2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTfTime.setText("30 sec");
                            listQuestions.get(currentPos).setQuestionTime("30");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.tv_op_3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTfTime.setText("1 min");
                            listQuestions.get(currentPos).setQuestionTime("60");
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.tv_op_4).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tvTfTime.setText("2 min");
                            listQuestions.get(currentPos).setQuestionTime("120");
                            dialog.dismiss();
                        }
                    });

                }
            });

            viewArQuizAllTypeQuestion.findViewById(R.id.ll_add_tf_q_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    toHide = llShowAddTfQImageText;
                    toDisplay = ivTfQImage;

                      final Dialog dialog = new Dialog(mActivity);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.dialog_imgselector);
//                    dialog.setCancelable(true);
//                    DisplayMetrics metrics = new DisplayMetrics();
//                    mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                    int wwidth = metrics.widthPixels;
//                    dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                    TextView tvCam = dialog.findViewById(R.id.tv_cam);
//                    tvCam.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
////                fromGallery = false;
//                            try {
//
//                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
//                                File imageFile = new File(imageFilePath);
//                                picUri = Uri.fromFile(imageFile); // convert path to Uri
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                                startActivityForResult(takePictureIntent, 2);
//
//                            } catch (ActivityNotFoundException anfe) {
//                                //display an error message
//                                String errorMessage = "Whoops - your device doesn't support capturing images!";
//                                Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//
//                    TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
//                    tvGallery.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                fromGallery = true;
//                            Intent intent_upload = new Intent();
//                            intent_upload.setType("image/*");
//                            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent_upload, 1);
//                            dialog.dismiss();
//                        }
//                    });

//                    dialog.show();
                    Intent intent_upload = new Intent();
                    intent_upload.setType("image/*");
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent_upload, 1);
                }
            });


            tvA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MAQ")) {
                        if (listQuestions.get(currentPos).getIsCorrect().contains("A")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace("A", ""));
                            tvA.setTextColor(Color.BLACK);
                            tvA.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else if (listQuestions.get(currentPos).getIsCorrect().contains(",A")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace(",A", ""));
                            tvA.setTextColor(Color.BLACK);
                            tvA.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else {
                            if (listQuestions.get(currentPos).getIsCorrect().length() > 0) {
                                listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect() + ",A");
                                tvA.setTextColor(Color.WHITE);
                                tvA.setBackgroundColor (Color.parseColor("#1FA655"));
                            } else {
                                listQuestions.get(currentPos).setIsCorrect("A");
                                tvA.setTextColor(Color.WHITE);
                                tvA.setBackgroundColor(Color.parseColor("#1FA655"));
                            }
                        }

                    } else {
                        unselectAll();
                        tvA.setTextColor(Color.WHITE);
                        tvA.setBackgroundColor(Color.parseColor("#1FA655"));
                        listQuestions.get(currentPos).setIsCorrect("A");
                    }
                }
            });
            tvB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MAQ")) {
                        if (listQuestions.get(currentPos).getIsCorrect().contains("B")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace("B", ""));
                            tvB.setTextColor(Color.BLACK);
                            tvB.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else if (listQuestions.get(currentPos).getIsCorrect().contains(",B")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace(",B", ""));
                            tvB.setTextColor(Color.BLACK);
                            tvB.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else {
                            if (listQuestions.get(currentPos).getIsCorrect().length() > 0) {
                                listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect() + ",B");
                                tvB.setTextColor(Color.WHITE);
                                tvB.setBackgroundColor(Color.parseColor("#1FA655"));
                            } else {
                                listQuestions.get(currentPos).setIsCorrect("B");
                                tvB.setTextColor(Color.WHITE);
                                tvB.setBackgroundColor(Color.parseColor("#1FA655"));
                            }
                        }

                    } else {
                        unselectAll();
                        tvB.setTextColor(Color.WHITE);
                        tvB.setBackgroundColor(Color.parseColor("#1FA655"));
                        listQuestions.get(currentPos).setIsCorrect("B");
                    }
                }
            });
            tvC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MAQ")) {
                        if (listQuestions.get(currentPos).getIsCorrect().contains("C")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace("C", ""));
                            tvC.setTextColor(Color.BLACK);
                            tvC.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else if (listQuestions.get(currentPos).getIsCorrect().contains(",C")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace(",C", ""));
                            tvC.setTextColor(Color.BLACK);
                            tvC.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else {
                            if (listQuestions.get(currentPos).getIsCorrect().length() > 0) {
                                listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect() + ",C");
                                tvC.setTextColor(Color.WHITE);
                                tvC.setBackgroundColor(Color.parseColor("#1FA655"));
                            } else {
                                listQuestions.get(currentPos).setIsCorrect("C");
                                tvC.setTextColor(Color.WHITE);
                                tvC.setBackgroundColor(Color.parseColor("#1FA655"));
                            }
                        }

                    } else {
                        unselectAll();
                        tvC.setTextColor(Color.WHITE);
                        tvC.setBackgroundColor(Color.parseColor("#1FA655"));
                        listQuestions.get(currentPos).setIsCorrect("C");
                    }
                }
            });
            tvD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MAQ")) {
                        if (listQuestions.get(currentPos).getIsCorrect().contains("D")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace("D", ""));
                            tvD.setTextColor(Color.BLACK);
                            tvD.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else if (listQuestions.get(currentPos).getIsCorrect().contains(",D")) {
                            listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect().replace(",D", ""));
                            tvD.setTextColor(Color.BLACK);
                            tvD.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        } else {
                            if (listQuestions.get(currentPos).getIsCorrect().length() > 0) {
                                listQuestions.get(currentPos).setIsCorrect(listQuestions.get(currentPos).getIsCorrect() + ",D");
                                tvD.setTextColor(Color.WHITE);
                                tvD.setBackgroundColor(Color.parseColor("#1FA655"));
                            } else {
                                listQuestions.get(currentPos).setIsCorrect("D");
                                tvD.setTextColor(Color.WHITE);
                                tvD.setBackgroundColor(Color.parseColor("#1FA655"));
                            }
                        }

                    } else {
                        unselectAll();
                        tvD.setTextColor(Color.WHITE);
                        tvD.setBackgroundColor(Color.parseColor("#1FA655"));
                        listQuestions.get(currentPos).setIsCorrect("D");
                    }
                }
            });


            viewArQuizAllTypeQuestion.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MCQ") || listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MAQ")) {
                        if (etMcqQues.getText().toString().length() > 0) {
                            listQuestions.get(currentPos).setQuestion(etMcqQues.getText().toString());
                            if (viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).getVisibility() == View.VISIBLE) {
                                if (etOptionTextA.getText().toString().length() > 0 && etOptionTextB.getText().toString().length() > 0 && etOptionTextC.getText().toString().length() > 0 && etOptionTextD.getText().toString().length() > 0) {
                                    listQuestions.get(currentPos).setOptionA(etOptionTextA.getText().toString());
                                    listQuestions.get(currentPos).setOptionB(etOptionTextB.getText().toString());
                                    listQuestions.get(currentPos).setOptionC(etOptionTextC.getText().toString());
                                    listQuestions.get(currentPos).setOptionD(etOptionTextD.getText().toString());

                                    if (!listQuestions.get(currentPos).getIsCorrect().equalsIgnoreCase("")) {
                                        navigateNext();
                                    } else {
                                        showDialog("Please Enter the correct options!");
                                    }
                                } else {
                                    showDialog("Please Enter all the options!");
                                }
                            } else {
                                if (listQuestions.get(currentPos).getOptionAImage() != null && listQuestions.get(currentPos).getOptionBImage() != null && listQuestions.get(currentPos).getOptionCImage() != null && listQuestions.get(currentPos).getOptionDImage() != null) {
                                    if (!listQuestions.get(currentPos).getIsCorrect().equalsIgnoreCase("")) {
                                        navigateNext();
                                    } else {
                                        showDialog("Please Enter the correct options!");
                                    }
                                } else {
                                    showDialog("Please Enter all the options!");
                                }
                            }

                        } else {
                            showDialog("Please Enter the question!");
                        }
                    } else {
                        if (etTfQues.getText().length() > 0) {
                            listQuestions.get(currentPos).setQuestion(etTfQues.getText().toString());
                            if (!listQuestions.get(currentPos).getIsCorrect().equalsIgnoreCase("")) {
                                navigateNext();
                            } else {
                                showDialog("Please Enter the correct options!");
                            }
                        }
                    }
                }
            });

            viewArQuizAllTypeQuestion.findViewById(R.id.save_draft).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ((ArAddQuizQuestion)mActivity).quizObject.setListMCQ(listQuestions);
//
//                    uploadtoS3();
                }
            });

        }

    }

    private void uploadtoS3() {
        utils.showLoader(mActivity);

        int c=0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 1000 * 60 * 60; // 1 hour.
                expiration.setTime(msec);

                String keyName ="";
                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                        Region.getRegion(Regions.AP_SOUTH_1));

                if (((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()!=null){
                    try {
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()).getName();
                        }
                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);
                        ((ArAddQuizQuestion)mActivity).quizObject.setCoverImagePath(keyName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for (int i=0;i<((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().size();i++){
                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImage()!=null){
                        try {
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getQuesImage()).getName();
                            }else {
                                keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getQuesImage()).getName();
                            }
                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    keyName,
                                    FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImage()));
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);
                            ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setQuesImageFilepath(keyName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionType().equalsIgnoreCase("True/False")){
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionAImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionAImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionAImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionBImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionBImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionBImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionCImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionCImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionCImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionDImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionDImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionDImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                postToServer();

            }
        }).start();
    }


    private void postToServer() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.dismissDialog();
            }
        });

        JSONArray filesArray = new JSONArray();
        for (int i=0;i<((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().size();i++){
            JSONObject fileObj = new JSONObject();
            if (!((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImageFilepath().equalsIgnoreCase("NA")){
                try {
                    fileObj.put("question",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestion()+"~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImageFilepath()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    fileObj.put("question",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestion());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            try {
                fileObj.put("answer",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getIsCorrect());
                fileObj.put("explanation","");
                fileObj.put("queTime",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionType().equalsIgnoreCase("True/False")){
                try {
                    fileObj.put("questType","TOF");
                    fileObj.put("option1","NA");
                    fileObj.put("option2","NA");
                    fileObj.put("option3","NA");
                    fileObj.put("option4","NA");

                    filesArray.put(fileObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {

                if(((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionType().equalsIgnoreCase("MCQ")){
                    try {
                        fileObj.put("questType","MCQ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        fileObj.put("questType","MAQ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option1",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionA());
                    }else {
                        fileObj.put("option1","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImagePath()));
                    }

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option2",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionB());
                    }else {
                        fileObj.put("option2","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImagePath()));
                    }

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option3",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionC());
                    }else {
                        fileObj.put("option3","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImagePath()));
                    }

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option4",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionD());
                    }else {
                        fileObj.put("option4","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImagePath()));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                filesArray.put(fileObj);

            }

        }


        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName",sh_Pref.getString("schema",""));
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("branchId",tObj.getBranchId());
                postObject.put("createdBy",tObj.getUserId());
                postObject.put("userRole","T");
            }else {
                postObject.put("userRole","S");
                postObject.put("userId", sObj.getStudentId());
                postObject.put("sectionId", sObj.getClassCourseSectionId());
                postObject.put("branchId",sObj.getBranchId());
                postObject.put("createdBy",sObj.getStudentId());
            }
            if(((ArAddQuizQuestion)mActivity).quizObject.getCoverImagePath().equalsIgnoreCase("NA")) {
                postObject.put("arenaName", ((ArAddQuizQuestion) mActivity).quizObject.getQuizTitle());
            }else {
                postObject.put("arenaName", ((ArAddQuizQuestion) mActivity).quizObject.getQuizTitle()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion) mActivity).quizObject.getCoverImagePath()));
            }
            postObject.put("arenaDesc",((ArAddQuizQuestion) mActivity).quizObject.getQuizDesc());
            postObject.put("arenaType","Quiz");
            postObject.put("arenaCategory","1");

            postObject.put("color","#FFFFFF");
            postObject.put("questionCount",((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().size()+"");
            postObject.put("filesArray",filesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("tag","obj "+postObject.toString());
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.InsertTeacherArenaRecord;
        }else {
            url = AppUrls.InsertArenaRecord;
        }

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();
                utils.showLog("tag","response "+resp);

                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.getString("StatusCode").equalsIgnoreCase("200")){
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                                Toast.makeText(mActivity, "Success!", Toast.LENGTH_SHORT).show();

                                new AlertDialog.Builder(mActivity)
                                        .setTitle(getResources().getString(R.string.app_name))
                                        .setMessage("Quiz saved in draft.")
                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                mActivity.finish();
                                            }
                                        })

                                        .setCancelable(false)
                                        .show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    private void navigateNext() {


        if ((currentPos+1)<numQues) {

            currentPos++;

            if (listQuestions.size() == numQues){
                loadQuestion();
                adapter.notifyDataSetChanged();
                if ((currentPos+1)==numQues) {
                    ((TextView)viewArQuizAllTypeQuestion.findViewById(R.id.tv_next)).setText("Preview");
                }
            }else {
                Dialog dialog = new Dialog(mActivity);
                dialog.setContentView(R.layout.layout_arena_quiz_option);
                dialog.setCancelable(true);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
                dialog.show();

                dialog.findViewById(R.id.tv_close_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        currentPos--;
                    }
                });
                dialog.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArQuizMCQ obj = new ArQuizMCQ();
                        obj.setQuestionType("MCQ");
                        listQuestions.add(obj);
                        loadQuestion();
                        adapter.notifyDataSetChanged();
                        if ((currentPos+1)==numQues) {
                            ((TextView)viewArQuizAllTypeQuestion.findViewById(R.id.tv_next)).setText("Preview");
                        }
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.cv_maq).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArQuizMCQ obj = new ArQuizMCQ();
                        obj.setQuestionType("MAQ");
                        listQuestions.add(obj);
                        loadQuestion();
                        adapter.notifyDataSetChanged();
                        if ((currentPos+1)==numQues) {
                            ((TextView)viewArQuizAllTypeQuestion.findViewById(R.id.tv_next)).setText("Preview");
                        }
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.cv_tf).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArQuizMCQ obj = new ArQuizMCQ();
                        obj.setQuestionType("True/False");
                        listQuestions.add(obj);
                        loadQuestion();
                        adapter.notifyDataSetChanged();
                        if ((currentPos+1)==numQues) {
                            ((TextView)viewArQuizAllTypeQuestion.findViewById(R.id.tv_next)).setText("Preview");
                        }
                        dialog.dismiss();

                    }
                });
            }
        }else{
            ((ArAddQuizQuestion)mActivity).stepNo = 4;
            ((ArAddQuizQuestion)mActivity).quizObject.setListMCQ(listQuestions);
            ((ArAddQuizQuestion)mActivity).init();
        }
    }

    void showDialog(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(text)
                .setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }

                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    
    void loadQuestion(){
        if (listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MCQ") || listQuestions.get(currentPos).getQuestionType().equalsIgnoreCase("MAQ")){
            viewArQuizAllTypeQuestion.findViewById(R.id.ll_quiz).setVisibility(View.VISIBLE);
            viewArQuizAllTypeQuestion.findViewById(R.id.ll_tf).setVisibility(View.GONE);

            etMcqQues.setText(listQuestions.get(currentPos).getQuestion());

            if (listQuestions.get(currentPos).getQuesImage()==null){
                ivQImage.setVisibility(View.GONE);
                llShowAddQImageText.setVisibility(View.VISIBLE);
            }else {
                ivQImage.setVisibility(View.VISIBLE);
                llShowAddQImageText.setVisibility(View.GONE);
                try {
                    ivQImage.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), listQuestions.get(currentPos).getQuesImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!listQuestions.get(currentPos).getIsCorrect().equalsIgnoreCase("NA")){
                unselectAll();
                switch (listQuestions.get(currentPos).getIsCorrect()){
                    case "A":
                        tvA.setTextColor(Color.WHITE);
                        tvA.setBackgroundColor(Color.parseColor("#1FA655"));
                        break;
                    case "B":
                        tvB.setTextColor(Color.WHITE);
                        tvB.setBackgroundColor(Color.parseColor("#1FA655"));
                        break;
                    case "C":
                        tvC.setTextColor(Color.WHITE);
                        tvC.setBackgroundColor(Color.parseColor("#1FA655"));
                        break;
                    case "D":
                        tvD.setTextColor(Color.WHITE);
                        tvD.setBackgroundColor(Color.parseColor("#1FA655"));
                        break;
                }
            }

            etOptionTextA.setText(listQuestions.get(currentPos).getOptionA());
            etOptionTextB.setText(listQuestions.get(currentPos).getOptionB());
            etOptionTextC.setText(listQuestions.get(currentPos).getOptionC());
            etOptionTextD.setText(listQuestions.get(currentPos).getOptionD());

            if (listQuestions.get(currentPos).getOptionAImage()==null){
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.VISIBLE);
                tvSelectImageA.setVisibility(View.VISIBLE);
                ivSelectedImageA.setVisibility(View.GONE);
            }else {
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                tvSelectImageA.setVisibility(View.GONE);
                ivSelectedImageA.setVisibility(View.VISIBLE);
                try {
                    ivSelectedImageA.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), listQuestions.get(currentPos).getOptionAImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (listQuestions.get(currentPos).getOptionBImage()==null){
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.VISIBLE);
                tvSelectImageB.setVisibility(View.VISIBLE);
                ivSelectedImageB.setVisibility(View.GONE);
            }else {
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                tvSelectImageB.setVisibility(View.GONE);
                ivSelectedImageB.setVisibility(View.VISIBLE);
                try {
                    ivSelectedImageB.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), listQuestions.get(currentPos).getOptionBImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (listQuestions.get(currentPos).getOptionCImage()==null){
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.VISIBLE);
                tvSelectImageC.setVisibility(View.VISIBLE);
                ivSelectedImageC.setVisibility(View.GONE);
            }else {
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                tvSelectImageC.setVisibility(View.GONE);
                ivSelectedImageC.setVisibility(View.VISIBLE);
                try {
                    ivSelectedImageC.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), listQuestions.get(currentPos).getOptionCImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (listQuestions.get(currentPos).getOptionDImage()==null){
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.VISIBLE);
                tvSelectImageD.setVisibility(View.VISIBLE);
                ivSelectedImageD.setVisibility(View.GONE);
            }else {
                viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                tvSelectImageD.setVisibility(View.GONE);
                ivSelectedImageD.setVisibility(View.VISIBLE);
                try {
                    ivSelectedImageD.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), listQuestions.get(currentPos).getOptionDImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (listQuestions.get(currentPos).getQuestionTime().length()>0){
                int val = Integer.parseInt(listQuestions.get(currentPos).getQuestionTime());
                if (val>=60){
                    tvTime.setText((val/60)+" min");
                    listQuestions.get(currentPos).setQuestionTime((val/60)+"");
                }else {
                    tvTime.setText(val +" sec");
                    listQuestions.get(currentPos).setQuestionTime(val+"");
                }
            }else {
                tvTime.setText("5 sec");
                listQuestions.get(currentPos).setQuestionTime("5");
            }


        }else{
            viewArQuizAllTypeQuestion.findViewById(R.id.ll_quiz).setVisibility(View.GONE);
            viewArQuizAllTypeQuestion.findViewById(R.id.ll_tf).setVisibility(View.VISIBLE);

            etTfQues.setText(listQuestions.get(currentPos).getQuestion());

            if (listQuestions.get(currentPos).getQuesImage()==null){
                ivTfQImage.setVisibility(View.GONE);
                llShowAddTfQImageText.setVisibility(View.VISIBLE);
            }else {
                try {
                    ivTfQImage.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), listQuestions.get(currentPos).getQuesImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ivTfQImage.setVisibility(View.VISIBLE);
                llShowAddTfQImageText.setVisibility(View.GONE);
            }

            if (listQuestions.get(currentPos).getQuestionTime().length()>0){
                int val = Integer.parseInt(listQuestions.get(currentPos).getQuestionTime());
                if (val>=60){
                    tvTfTime.setText((val/60)+" min");
                    listQuestions.get(currentPos).setQuestionTime((val/60)+"");
                }else {
                    tvTfTime.setText(val +" sec");
                    listQuestions.get(currentPos).setQuestionTime(val+"");
                }
            }else {
                tvTfTime.setText("5 sec");
                listQuestions.get(currentPos).setQuestionTime("5");
            }

            if (listQuestions.get(currentPos).getIsCorrect().equalsIgnoreCase("")){
                tvTrue.setBackgroundColor(Color.WHITE);
                tvTrue.setTextColor(Color.BLACK);
                tvFalse.setBackgroundColor(Color.WHITE);
                tvFalse.setTextColor(Color.BLACK);
            }else{
                switch (listQuestions.get(currentPos).getIsCorrect()){
                    case "true":
                        tvTrue.setBackgroundColor(Color.parseColor("#1FA655"));
                        tvTrue.setTextColor(Color.WHITE);
                        tvFalse.setBackgroundColor(Color.WHITE);
                        tvFalse.setTextColor(Color.BLACK);
                        break;
                    case "false":
                        tvFalse.setBackgroundColor(Color.parseColor("#1FA655"));
                        tvFalse.setTextColor(Color.WHITE);
                        tvTrue.setBackgroundColor(Color.WHITE);
                        tvTrue.setTextColor(Color.BLACK);
                        break;
                }
            }

        }
    }

    private void unselectAll() {
        tvA.setBackgroundColor(Color.WHITE);
        tvB.setBackgroundColor(Color.WHITE);
        tvC.setBackgroundColor(Color.WHITE);
        tvD.setBackgroundColor(Color.WHITE);

        tvA.setTextColor(Color.BLACK);
        tvB.setTextColor(Color.BLACK);
        tvC.setTextColor(Color.BLACK);
        tvD.setTextColor(Color.BLACK);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK){

            if (requestCode==1) {

                Uri uri = data.getData();
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                toHide.setVisibility(View.GONE);
//                ((ImageView) toDisplay).setImageBitmap(bitmap);
                ((ImageView) toDisplay).setImageURI(uri);
//                bitmap.recycle();
                toDisplay.setVisibility(View.VISIBLE);

                switch (toDisplay.getId()) {
                    case R.id.iv_q_image:
                    case R.id.iv_tf_q_image:
                        listQuestions.get(currentPos).setQuesImage(uri);
                        break;
                    case R.id.iv_selected_image_a:
                        listQuestions.get(currentPos).setOptionAImage(uri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                    case R.id.iv_selected_image_b:
                        listQuestions.get(currentPos).setOptionBImage(uri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                    case R.id.iv_selected_image_c:
                        listQuestions.get(currentPos).setOptionCImage(uri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                    case R.id.iv_selected_image_d:
                        listQuestions.get(currentPos).setOptionDImage(uri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                }
            }else {
//                Uri uri = data.getData();
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                toHide.setVisibility(View.GONE);
//                ((ImageView) toDisplay).setImageBitmap(bitmap);
                ((ImageView) toDisplay).setImageURI(picUri);
//                bitmap.recycle();
                toDisplay.setVisibility(View.VISIBLE);
                switch (toDisplay.getId()) {
                    case R.id.iv_q_image:
                    case R.id.iv_tf_q_image:
                        listQuestions.get(currentPos).setQuesImage(picUri);
                        break;
                    case R.id.iv_selected_image_a:
                        listQuestions.get(currentPos).setOptionAImage(picUri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                    case R.id.iv_selected_image_b:
                        listQuestions.get(currentPos).setOptionBImage(picUri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                    case R.id.iv_selected_image_c:
                        listQuestions.get(currentPos).setOptionCImage(picUri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                    case R.id.iv_selected_image_d:
                        listQuestions.get(currentPos).setOptionDImage(picUri);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_a).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_b).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_c).setVisibility(View.GONE);
                        viewArQuizAllTypeQuestion.findViewById(R.id.tl_d).setVisibility(View.GONE);
                        break;
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder>{

        int numberOfQuestions;

        public QuestionNumberAdapter(int numberOfQuestions) {
            this.numberOfQuestions = numberOfQuestions;
        }

        @NonNull
        @Override
        public QuestionNumberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionNumberAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_question_number,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionNumberAdapter.ViewHolder holder, int position) {
            holder.tvQNum.setText((position+1)+"");

            if (position==currentPos){
                holder.llItem.setBackgroundResource(R.drawable.bg_blue_circle);
                holder.tvQNum.setTextColor(Color.WHITE);
            }else{
                holder.llItem.setBackgroundResource(R.drawable.bg_grey_circle_border);
                if (position<currentPos){
                    holder.tvQNum.setTextColor(Color.parseColor("#000000"));
                }else {
                    holder.tvQNum.setTextColor(Color.parseColor("#40000000"));
                }
            }

        }

        @Override
        public int getItemCount() {
            return numberOfQuestions;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQNum;
            LinearLayout llItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQNum = itemView.findViewById(R.id.tv_queNo);
                llItem = itemView.findViewById(R.id.ll_itemview);
            }
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
    
}