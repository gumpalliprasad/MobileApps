package myschoolapp.com.gsnedutech.Arena.Trial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
public class QuizEditingFrag extends Fragment {

    private static final String TAG = QuizEditingFrag.class.getName();

    View viewArQuizEditingFrag;
    Unbinder unbinder;

    Activity mActivity;

    @BindView(R.id.ll_quiz)
    LinearLayout llQuiz;
    @BindView(R.id.ll_tf)
    LinearLayout llTf;

    @BindView(R.id.et_mcq_ques)
    EditText etMcqQues;
    @BindView(R.id.ll_show_add_q_image_text)
    LinearLayout llShowAddQImageText;
    @BindView(R.id.iv_q_image)
    ImageView ivQImage;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_true)
    TextView tvTrue;
    @BindView(R.id.tv_false)
    TextView tvFalse;

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
    @BindView(R.id.tl_a)
    LinearLayout tlA;
    @BindView(R.id.tl_b)
    LinearLayout tlB;
    @BindView(R.id.tl_c)
    LinearLayout tlC;
    @BindView(R.id.tl_d)
    LinearLayout tlD;
    @BindView(R.id.fl_a)
    FrameLayout flA;
    @BindView(R.id.fl_b)
    FrameLayout flB;
    @BindView(R.id.fl_c)
    FrameLayout flC;
    @BindView(R.id.fl_d)
    FrameLayout flD;

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    int qNum;
    List<ArenaQuizQuestionFiles> listQs = new ArrayList<>();

    Uri qUri = null;
    Uri aUri=null,bUri=null,cUri=null,dUri=null;

    String correct = "";

    View toHide,toShow;

    MyUtils utils = new MyUtils();

    AmazonS3Client s3Client1;
    HashMap<String,String> mapKeyNames = new HashMap<>();

    public QuizEditingFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArQuizEditingFrag = inflater.inflate(R.layout.fragment_ar_quiz_editing, container, false);
        unbinder = ButterKnife.bind(this,viewArQuizEditingFrag);

//        init();

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


        getQuizDetails();

        return viewArQuizEditingFrag;

    }

    void init(){


        qNum = ((ArAddQuizNew) mActivity).qNum ;
//        listQs = ((ArAddQuizNew)mActivity).listQuestions;

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
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv_op_2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvTime.setText("30 sec");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv_op_3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvTime.setText("1 mins");
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.tv_op_4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvTime.setText("2 mins");
                        dialog.dismiss();
                    }
                });

            }
        });

        viewArQuizEditingFrag.findViewById(R.id.cv_remove_q_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivQImage.setVisibility(View.GONE);
                llShowAddQImageText.setVisibility(View.VISIBLE);
                qUri = null;
            }
        });

        llShowAddQImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHide = llShowAddQImageText;
                toShow = ivQImage;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        ivQImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHide = llShowAddQImageText;
                toShow = ivQImage;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        viewArQuizEditingFrag.findViewById(R.id.cv_remove_a_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aUri = null;
                ivSelectedImageA.setVisibility(View.GONE);
                tvSelectImageA.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        viewArQuizEditingFrag.findViewById(R.id.cv_remove_b_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bUri = null;
                ivSelectedImageB.setVisibility(View.GONE);
                tvSelectImageB.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        viewArQuizEditingFrag.findViewById(R.id.cv_remove_c_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cUri = null;
                ivSelectedImageC.setVisibility(View.GONE);
                tvSelectImageC.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        viewArQuizEditingFrag.findViewById(R.id.cv_remove_d_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dUri = null;
                ivSelectedImageD.setVisibility(View.GONE);
                tvSelectImageD.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        tvSelectImageA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageA;
                toHide = tvSelectImageA;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        ivSelectedImageA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageA;
                toHide = tvSelectImageA;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        tvSelectImageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageB;
                toHide = tvSelectImageB;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        ivSelectedImageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageB;
                toHide = tvSelectImageB;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        tvSelectImageC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageC;
                toHide = tvSelectImageC;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        ivSelectedImageC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageC;
                toHide = tvSelectImageC;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        tvSelectImageD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageD;
                toHide = tvSelectImageD;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        ivSelectedImageD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageD;
                toHide = tvSelectImageD;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
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
                String s = editable.toString().trim();
                if (s.length()>0 || etOptionTextB.getText().toString().trim().length()>0 || etOptionTextC.getText().toString().trim().length()>0 || etOptionTextD.getText().toString().trim().length()>0){
                    flA.setVisibility(View.GONE);
                    flB.setVisibility(View.GONE);
                    flC.setVisibility(View.GONE);
                    flD.setVisibility(View.GONE);
                }else {
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
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
                String s = editable.toString().trim();
                if (s.length()>0 || etOptionTextA.getText().toString().trim().length()>0 || etOptionTextC.getText().toString().trim().length()>0 || etOptionTextD.getText().toString().trim().length()>0){
                    flA.setVisibility(View.GONE);
                    flB.setVisibility(View.GONE);
                    flC.setVisibility(View.GONE);
                    flD.setVisibility(View.GONE);
                }else {
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
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
                String s = editable.toString().trim();
                if (s.length()>0 || etOptionTextB.getText().toString().trim().length()>0 || etOptionTextA.getText().toString().trim().length()>0 || etOptionTextD.getText().toString().trim().length()>0){
                    flA.setVisibility(View.GONE);
                    flB.setVisibility(View.GONE);
                    flC.setVisibility(View.GONE);
                    flD.setVisibility(View.GONE);
                }else {
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
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
                String s = editable.toString().trim();
                if (s.length()>0 || etOptionTextB.getText().toString().trim().length()>0 || etOptionTextC.getText().toString().trim().length()>0 || etOptionTextA.getText().toString().trim().length()>0){
                    flA.setVisibility(View.GONE);
                    flB.setVisibility(View.GONE);
                    flC.setVisibility(View.GONE);
                    flD.setVisibility(View.GONE);
                }else {
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                }
            }
        });

        correct = listQs.get(qNum).getAnswer();

        if (listQs.get(qNum).getQuestType().equalsIgnoreCase("TOF")){
            tvTrue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    correct = "true";
                    setCorrect(correct);
                }
            });
            tvFalse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    correct = "false";
                    setCorrect(correct);
                }
            });
        }
        else if(listQs.get(qNum).getQuestType().equalsIgnoreCase("MCQ")){
            tvA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    correct = "A";
                    setCorrect(correct);
                }
            });
            tvB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    correct = "B";
                    setCorrect(correct);
                }
            });
            tvC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    correct = "C";
                    setCorrect(correct);
                }
            });
            tvD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    correct = "D";
                    setCorrect(correct);
                }
            });
        }
        else {
            tvA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    String[] str = correct.split(",");
                    List <String> answers= new ArrayList<>();
                    for (int i=0;i<str.length;i++){
                        answers.add(str[i]);
                    }
                    if (answers.contains("A")){
                        answers.remove(answers.indexOf("A"));
                    }else {
                        answers.add("A");
                    }
                    correct = "";
                    for (int i=0;i<answers.size();i++){
                        setCorrect(answers.get(i));
                        if (i>0){
                            correct = correct+","+answers.get(i);
                        }else {
                            correct = correct+answers.get(i);
                        }
                    }

                }
            });

            tvB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    String[] str = correct.split(",");
                    List <String> answers= new ArrayList<>();
                    for (int i=0;i<str.length;i++){
                        answers.add(str[i]);
                    }
                    if (answers.contains("B")){
                        answers.remove(answers.indexOf("B"));
                    }else {
                        answers.add("B");
                    }

                    correct = "";
                    for (int i=0;i<answers.size();i++){
                        setCorrect(answers.get(i));
                        if (i>0){
                            correct = correct+","+answers.get(i);
                        }else {
                            correct = correct+answers.get(i);
                        }
                    }

                }
            });

            tvC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    String[] str = correct.split(",");
                    List <String> answers= new ArrayList<>();
                    for (int i=0;i<str.length;i++){
                        answers.add(str[i]);
                    }
                    if (answers.contains("C")){
                        answers.remove(answers.indexOf("C"));
                    }else {
                        answers.add("C");
                    }

                    correct = "";
                    for (int i=0;i<answers.size();i++){
                        setCorrect(answers.get(i));
                        if (i>0){
                            correct = correct+","+answers.get(i);
                        }else {
                            correct = correct+answers.get(i);
                        }
                    }

                }
            });

            tvD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unselectAll();
                    String[] str = correct.split(",");
                    List <String> answers= new ArrayList<>();
                    for (int i=0;i<str.length;i++){
                        answers.add(str[i]);
                    }
                    if (answers.contains("D")){
                        answers.remove(answers.indexOf("D"));
                    }else {
                        answers.add("D");
                    }

                    correct = "";
                    for (int i=0;i<answers.size();i++){
                        setCorrect(answers.get(i));
                        if (i>0){
                            correct = correct+","+answers.get(i);
                        }else {
                            correct = correct+answers.get(i);
                        }
                    }

                }
            });
        }

        viewArQuizEditingFrag.findViewById(R.id.save_draft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etMcqQues.getText().toString().trim().length()>0){
                    if (!correct.equalsIgnoreCase("")){
                        if (listQs.get(qNum).getQuestType().equalsIgnoreCase("TOF")){
                            if (qUri==null){
                                updateQuestion();
                            }else {
                                uploadToS3(0);
                            }
                        }else {
                            if (tlA.getVisibility()==View.VISIBLE){
                                if (etOptionTextA.getText().toString().trim().length()>0 && etOptionTextB.getText().toString().trim().length()>0 && etOptionTextC.getText().toString().trim().length()>0 && etOptionTextD.getText().toString().trim().length()>0){
                                    if (qUri!=null){
                                        uploadToS3(0);
                                    }else {
                                        updateQuestion();
                                    }
                                }else {
                                    Toast.makeText(mActivity,"Enter all the options!",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                if (ivSelectedImageA.getVisibility()==View.VISIBLE && ivSelectedImageB.getVisibility()==View.VISIBLE && ivSelectedImageC.getVisibility()==View.VISIBLE && ivSelectedImageD.getVisibility()==View.VISIBLE){
                                    if (aUri==null && bUri==null && cUri==null && dUri==null){
                                        updateQuestion();
                                    }else {
                                        uploadToS3(1);
                                    }
                                }else {
                                    Toast.makeText(mActivity,"Enter all the options!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }else {
                        Toast.makeText(mActivity,"Enter a correct option!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mActivity,"Enter a valid question!",Toast.LENGTH_SHORT).show();
                }


            }
        });


        loadQuestion();
    }

    void loadQuestion(){

        if (listQs.get(qNum).getQuestion().contains("~~")){
            String[] str = listQs.get(qNum).getQuestion().split("~~");
            etMcqQues.setText(str[0]);
            llShowAddQImageText.setVisibility(View.GONE);
            ivQImage.setVisibility(View.VISIBLE);
            Picasso.with(mActivity).load(str[(str.length-1)]).placeholder(R.drawable.ic_arena_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
        }else {
            etMcqQues.setText(listQs.get(qNum).getQuestion());
        }
        tvTime.setText(secondsToMinutes(Integer.parseInt(listQs.get(qNum).getQuestTime())));



        if (listQs.get(qNum).getQuestType().equalsIgnoreCase("TOF")){
            llTf.setVisibility(View.VISIBLE);
            llQuiz.setVisibility(View.GONE);

            unselectAll();
            if (listQs.get(qNum).getAnswer().equalsIgnoreCase("true")){
                tvTrue.setBackgroundColor(Color.parseColor("#1FA655"));
                tvTrue.setTextColor(Color.WHITE);
            }else {
                tvFalse.setBackgroundColor(Color.parseColor("#1FA655"));
                tvFalse.setTextColor(Color.WHITE);
            }

        }else {
            llQuiz.setVisibility(View.VISIBLE);
            llTf.setVisibility(View.GONE);

            if (listQs.get(qNum).getOption1().contains("~~")){
                tlA.setVisibility(View.GONE);
                tlB.setVisibility(View.GONE);
                tlC.setVisibility(View.GONE);
                tlD.setVisibility(View.GONE);
                tvSelectImageA.setVisibility(View.GONE);
                tvSelectImageB.setVisibility(View.GONE);
                tvSelectImageC.setVisibility(View.GONE);
                tvSelectImageD.setVisibility(View.GONE);
                ivSelectedImageA.setVisibility(View.VISIBLE);
                ivSelectedImageB.setVisibility(View.VISIBLE);
                ivSelectedImageC.setVisibility(View.VISIBLE);
                ivSelectedImageD.setVisibility(View.VISIBLE);

                String[] s1 = listQs.get(qNum).getOption1().split("~~");
                Picasso.with(mActivity).load(s1[(s1.length-1)]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageA);
                String[] s2 = listQs.get(qNum).getOption2().split("~~");
                Picasso.with(mActivity).load(s2[(s2.length-1)]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageB);
                String[] s3 = listQs.get(qNum).getOption3().split("~~");
                Picasso.with(mActivity).load(s3[(s3.length-1)]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageC);
                String[] s4 = listQs.get(qNum).getOption4().split("~~");
                Picasso.with(mActivity).load(s4[(s4.length-1)]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageD);


            }
            else {
                tlA.setVisibility(View.VISIBLE);
                tlB.setVisibility(View.VISIBLE);
                tlC.setVisibility(View.VISIBLE);
                tlD.setVisibility(View.VISIBLE);
                etOptionTextA.setVisibility(View.VISIBLE);
                etOptionTextB.setVisibility(View.VISIBLE);
                etOptionTextC.setVisibility(View.VISIBLE);
                etOptionTextD.setVisibility(View.VISIBLE);
                tvSelectImageA.setVisibility(View.GONE);
                tvSelectImageB.setVisibility(View.GONE);
                tvSelectImageC.setVisibility(View.GONE);
                tvSelectImageD.setVisibility(View.GONE);
                ivSelectedImageA.setVisibility(View.GONE);
                ivSelectedImageB.setVisibility(View.GONE);
                ivSelectedImageC.setVisibility(View.GONE);
                ivSelectedImageD.setVisibility(View.GONE);

                etOptionTextA.setText(listQs.get(qNum).getOption1());
                etOptionTextB.setText(listQs.get(qNum).getOption2());
                etOptionTextC.setText(listQs.get(qNum).getOption3());
                etOptionTextD.setText(listQs.get(qNum).getOption4());
            }

            if (listQs.get(qNum).getQuestType().equalsIgnoreCase("MCQ")){
                unselectAll();
                setCorrect(listQs.get(qNum).getAnswer());
            }else {
                unselectAll();
                String s[] = listQs.get(qNum).getAnswer().split(",");
                for (int i=0;i<s.length;i++){
                    setCorrect(s[i].trim());
                }
            }

        }

    }

    void setCorrect(String op){
        switch (op){
            case "A":
                tvA.setBackgroundColor(Color.parseColor("#1FA655"));
                tvA.setTextColor(Color.WHITE);
                break;
            case "B":
                tvB.setBackgroundColor(Color.parseColor("#1FA655"));
                tvB.setTextColor(Color.WHITE);
                break;
            case "C":
                tvC.setBackgroundColor(Color.parseColor("#1FA655"));
                tvC.setTextColor(Color.WHITE);
                break;
            case "D":
                tvD.setBackgroundColor(Color.parseColor("#1FA655"));
                tvD.setTextColor(Color.WHITE);
                break;
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

    void unselectAll(){
        tvA.setBackgroundColor(Color.WHITE);
        tvB.setBackgroundColor(Color.WHITE);
        tvC.setBackgroundColor(Color.WHITE);
        tvD.setBackgroundColor(Color.WHITE);
        tvTrue.setBackgroundColor(Color.WHITE);
        tvFalse.setBackgroundColor(Color.WHITE);

        tvA.setTextColor(Color.BLACK);
        tvB.setTextColor(Color.BLACK);
        tvC.setTextColor(Color.BLACK);
        tvD.setTextColor(Color.BLACK);
        tvTrue.setTextColor(Color.BLACK);
        tvFalse.setTextColor(Color.BLACK);
    }

    void uploadToS3(int choice){

        utils.showLoader(mActivity);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 1000 * 60 * 60; // 1 hour.
                expiration.setTime(msec);

                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                        Region.getRegion(Regions.AP_SOUTH_1));

                String keyName ="";
                try{

                    /*uploading question cover*/

                    if (qUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,qUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,qUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(mActivity,qUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("question",keyName);
                    }

                    /*uploading question cover*/


                    /*uploading options*/

                    if (choice>0){

                        /*uploading optionA*/
                        if (aUri!=null){
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,aUri).getName();
                            }else {
                                keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,aUri).getName();
                            }

                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    keyName,
                                    FileUtil.from(mActivity,aUri));
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            mapKeyNames.put("A",keyName);
                        }
                        /*uploading optionA*/


                        /*uploading optionB*/
                        if (bUri!=null){
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,bUri).getName();
                            }else {
                                keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,bUri).getName();
                            }

                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    keyName,
                                    FileUtil.from(mActivity,bUri));
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            mapKeyNames.put("B",keyName);
                        }
                        /*uploading optionB*/


                        /*uploading optionC*/
                        if (cUri!=null){
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,cUri).getName();
                            }else {
                                keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,cUri).getName();
                            }

                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    keyName,
                                    FileUtil.from(mActivity,cUri));
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            mapKeyNames.put("C",keyName);
                        }
                        /*uploading optionC*/


                        /*uploading optionD*/
                        if (dUri!=null){
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,dUri).getName();
                            }else {
                                keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,dUri).getName();
                            }

                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    keyName,
                                    FileUtil.from(mActivity,dUri));
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            mapKeyNames.put("D",keyName);
                        }
                        /*uploading optionB*/

                    }

                    /*uploading options*/

                    updateQuestion();

                }catch (IOException e){
                    e.printStackTrace();
                }



            }
        }).start();

    }

    void updateQuestion(){

        JSONObject postObj = new JSONObject();

        try {
            postObj.put("arenaQuestionId",listQs.get(qNum).getArenaQuestionId());

            if(mapKeyNames.containsKey("question")){
                postObj.put("question",etMcqQues.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("question")));
            }
            else {
                if (ivQImage.getVisibility()==View.VISIBLE && listQs.get(qNum).getQuestion().contains("~~")){
                    String[] s= listQs.get(qNum).getQuestion().split("~~");
                    postObj.put("question", etMcqQues.getText().toString()+"~~link~~"+s[(s.length-1)]);
                }else {
                    postObj.put("question", etMcqQues.getText().toString());
                }
            }

            if (mapKeyNames.containsKey("A")){
                postObj.put("option1","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("A")));
            }
            else {
                if (tlA.getVisibility()==View.VISIBLE){
                    postObj.put("option1",etOptionTextA.getText().toString());
                }else {
                    postObj.put("option1",listQs.get(qNum).getOption1());
                }
            }

            if (mapKeyNames.containsKey("B")){
                postObj.put("option2","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("B")));
            }
            else {
                if (tlB.getVisibility()==View.VISIBLE){
                    postObj.put("option2",etOptionTextB.getText().toString());
                }else {
                    postObj.put("option2",listQs.get(qNum).getOption2());
                }
            }

            if (mapKeyNames.containsKey("C")){
                postObj.put("option3","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("C")));
            }
            else {
                if (tlC.getVisibility()==View.VISIBLE){
                    postObj.put("option3",etOptionTextC.getText().toString());
                }else {
                    postObj.put("option3",listQs.get(qNum).getOption3());
                }
            }

            if (mapKeyNames.containsKey("D")){
                postObj.put("option4","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("D")));
            }
            else {
                if (tlD.getVisibility()==View.VISIBLE){
                    postObj.put("option4",etOptionTextD.getText().toString());
                }else {
                    postObj.put("option4",listQs.get(qNum).getOption4());
                }
            }

            postObj.put("answer",correct);

            if (tvTime.getText().toString().length()>0){
                String[] s = tvTime.getText().toString().trim().split(" ");
                if (s[(s.length-1)].equalsIgnoreCase("mins")){
                    postObj.put("queTime",(Integer.parseInt(s[0])*60)+"");
                }else {
                    postObj.put("queTime",s[0]+"");
                }
            }

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObj.put("updatedBy",tObj.getUserId());
            }else {
                postObj.put("updatedBy",sObj.getStudentId());
            }

            postObj.put("schemaName", sh_Pref.getString("schema",""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"post obj "+postObj);
        utils.showLog(TAG,"post url "+AppUrls.UpdateArenaQuestion);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, String.valueOf(postObj));

        Request request = new Request.Builder()
                .url(AppUrls.UpdateArenaQuestion)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        showErrorDialog("Oops! Failed to update question.");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("Oops! Failed to update question.");
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity,"Success!",Toast.LENGTH_SHORT).show();
                                    if (((ArAddQuizNew) mActivity).myFrag==null) {
                                        ((ArAddQuizNew) mActivity).stepNo = 3;
                                        ((ArAddQuizNew) mActivity).loadFragment();
                                    }else {
                                        ((ArAddQuizNew) mActivity).loadNewFrag();
                                    }
                                }
                            });
                        }else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorDialog("Oops! Failed to update question.");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            toHide.setVisibility(View.GONE);
            toShow.setVisibility(View.VISIBLE);
            ((ImageView)toShow).setImageURI(data.getData());
            if (toShow.getId()==R.id.iv_q_image){
                qUri = data.getData();
            }
            if (toShow.getId()==R.id.iv_selected_image_a){
                aUri = data.getData();
                tlA.setVisibility(View.GONE);
                tlB.setVisibility(View.GONE);
                tlC.setVisibility(View.GONE);
                tlD.setVisibility(View.GONE);
            }
            if (toShow.getId()==R.id.iv_selected_image_b){
                bUri = data.getData();
                tlA.setVisibility(View.GONE);
                tlB.setVisibility(View.GONE);
                tlC.setVisibility(View.GONE);
                tlD.setVisibility(View.GONE);
            }
            if (toShow.getId()==R.id.iv_selected_image_c){
                cUri = data.getData();
                tlA.setVisibility(View.GONE);
                tlB.setVisibility(View.GONE);
                tlC.setVisibility(View.GONE);
                tlD.setVisibility(View.GONE);
            }
            if (toShow.getId()==R.id.iv_selected_image_d){
                dUri = data.getData();
                tlA.setVisibility(View.GONE);
                tlB.setVisibility(View.GONE);
                tlC.setVisibility(View.GONE);
                tlD.setVisibility(View.GONE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    String secondsToMinutes(int s){
        int res = s/60;
        if (res>0){
            return res+" mins";
        }else {
            return s+" secs";
        }
    }

    void showErrorDialog(String message){
        new AlertDialog.Builder(mActivity)
                .setTitle(getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    void getQuizDetails(){

        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + ((ArAddQuizNew)mActivity).arenaId + "&userId="+tObj.getUserId();
        }else {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + ((ArAddQuizNew)mActivity).arenaId;
        }

        Request get = new Request.Builder()
                .url(url)
                .build();

        utils.showLog(TAG, "url "+url);

        client.newCall(get).enqueue(new Callback() {
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

                Log.v(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaQuizQuestionFiles>>() {
                            }.getType();

                            listQs.clear();
                            listQs.addAll(gson.fromJson(array.toString(),type));

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    init();
                                }
                            });



                        }else{
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}