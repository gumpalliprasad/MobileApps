package myschoolapp.com.gsnedutech.Arena;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.ProgressIndicator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.PollOption;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherPollReview extends AppCompatActivity {

    private static final String TAG = TeacherPollReview.class.getName();

    @BindView(R.id.ll_op1)
    LinearLayout llOp1;
    @BindView(R.id.ll_op2)
    LinearLayout llOp2;
    @BindView(R.id.ll_op3)
    LinearLayout llOp3;
    @BindView(R.id.ll_op4)
    LinearLayout llOp4;

    @BindView(R.id.rb_1)
    RadioButton rb1;
    @BindView(R.id.rb_2)
    RadioButton rb2;
    @BindView(R.id.rb_3)
    RadioButton rb3;
    @BindView(R.id.rb_4)
    RadioButton rb4;

    @BindView(R.id.tv_percent_op1)
    TextView tvPercentOp1;
    @BindView(R.id.tv_percent_op2)
    TextView tvPercentOp2;
    @BindView(R.id.tv_percent_op3)
    TextView tvPercentOp3;
    @BindView(R.id.tv_percent_op4)
    TextView tvPercentOp4;

    @BindView(R.id.tv_op1)
    TextView tvOp1;
    @BindView(R.id.tv_op2)
    TextView tvOp2;
    @BindView(R.id.tv_op3)
    TextView tvOp3;
    @BindView(R.id.tv_op4)
    TextView tvOp4;

    @BindView(R.id.iv_op1)
    ImageView ivOp1;
    @BindView(R.id.iv_op2)
    ImageView ivOp2;
    @BindView(R.id.iv_op3)
    ImageView ivOp3;
    @BindView(R.id.iv_op4)
    ImageView ivOp4;

    @BindView(R.id.progress_op1)
    ProgressIndicator progressOp1;
    @BindView(R.id.progress_op2)
    ProgressIndicator progressOp2;
    @BindView(R.id.progress_op3)
    ProgressIndicator progressOp3;
    @BindView(R.id.progress_op4)
    ProgressIndicator progressOp4;

    @BindView(R.id.tv_ques)
    TextView tvQues;
    @BindView(R.id.iv_question)
    ImageView ivQues;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.btn_submit)
    TextView btnSubmit;
    @BindView(R.id.et_remarks)
    EditText etRemarks;


    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    ArenaRecord pollObj;

    List<PollOption> listPolls = new ArrayList<>();

    List<ArenaQuizQuestionFiles> listQs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_poll_review);
        ButterKnife.bind(this);

        init();
    }

    void init(){

        if (getIntent().hasExtra("reassign")) {
            findViewById(R.id.tv_reassign_students).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_reassign_students).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getNewSections();
                }
            });
        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
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

        pollObj = (ArenaRecord) getIntent().getSerializableExtra("item");

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>0){
                    btnSubmit.setText("Reject");
                }else {
                    btnSubmit.setText("Approve");
                }
            }
        });

        String str = pollObj.getArenaName();
        if (str.contains("~~")) {

            String[] s = str.split("~~");

            tvTitle.setText(s[0]);
        }else{
            tvTitle.setText(str);
        }

        ivOp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = listQs.get(0).getOption1();

                String[] s = str.split("~~");

                showImageDialog(s[(s.length - 1)]);

            }
        });
        ivOp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = listQs.get(0).getOption2();

                String[] s = str.split("~~");

                showImageDialog(s[(s.length - 1)]);
            }
        });
        ivOp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = listQs.get(0).getOption3();

                String[] s = str.split("~~");

                showImageDialog(s[(s.length - 1)]);
            }
        });
        ivOp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = listQs.get(0).getOption4();

                String[] s = str.split("~~");

                showImageDialog(s[(s.length - 1)]);
            }
        });



        getQuizDetails();
    }

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + pollObj.getArenaId() + "&userId="+tObj.getUserId()+"&arenaCategory=7";
        }else {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + pollObj.getArenaId()+"&arenaCategory=7";
        }

        Request get = new Request.Builder()
                .url(url)
                .build();

        utils.showLog(TAG, "url "+url);

        client.newCall(get).enqueue(new Callback() {
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

                String resp = response.body().string();

                Log.v(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
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
                            JSONArray pollArray = jsonObject.getJSONArray("pollOptions");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaQuizQuestionFiles>>() {
                            }.getType();

                            listQs.clear();
                            listQs.addAll(gson.fromJson(array.toString(),type));

                            gson = new Gson();
                            type = new TypeToken<List<PollOption>>() {
                            }.getType();

                            listPolls.clear();
                            listPolls.addAll(gson.fromJson(pollArray.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setOptionsLayout();
                                }
                            });



                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void showImageDialog(String link){
        final Dialog dialog = new Dialog(TeacherPollReview.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        ImageView iv =  dialog.findViewById(R.id.iv_dialog);

        Picasso.with(TeacherPollReview.this).load(link).placeholder(R.drawable.ic_arena_img)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(iv);
    }

    void setProgress(int op1,int op2,int op3,int op4){
        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        rb4.setVisibility(View.GONE);

        tvPercentOp1.setText(op1+"%");
        tvPercentOp2.setText(op2+"%");
        tvPercentOp3.setText(op3+"%");
        tvPercentOp4.setText(op4+"%");

        tvPercentOp1.setVisibility(View.VISIBLE);
        tvPercentOp2.setVisibility(View.VISIBLE);
        tvPercentOp3.setVisibility(View.VISIBLE);
        tvPercentOp4.setVisibility(View.VISIBLE);

        progressOp1.setProgress(op1);
        progressOp2.setProgress(op2);
        progressOp3.setProgress(op3);
        progressOp4.setProgress(op4);

        progressOp1.setVisibility(View.VISIBLE);
        progressOp2.setVisibility(View.VISIBLE);
        progressOp3.setVisibility(View.VISIBLE);
        progressOp4.setVisibility(View.VISIBLE);
    }

    void setOptionsLayout() {
        for (int i = 0; i < listQs.size(); i++) {

            String str = listQs.get(i).getQuestion();
            if (str.contains("~~")) {
                String[] s = str.split("~~");
                tvQues.setText(s[0]);

                Picasso.with(TeacherPollReview.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQues);
            } else {
                tvQues.setText(str);
                ivQues.setVisibility(View.GONE);
            }

            str = listQs.get(i).getOption1();

            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                llOp1.setVisibility(View.VISIBLE);

                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    tvOp1.setText(s[0]);

                    Picasso.with(TeacherPollReview.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp1);
                } else {
                    tvOp1.setText(str);
                    ivOp1.setVisibility(View.GONE);
                }

            }else{
                llOp1.setVisibility(View.GONE);
            }

            str = listQs.get(i).getOption2();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                llOp2.setVisibility(View.VISIBLE);

                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    tvOp2.setText(s[0]);

                    Picasso.with(TeacherPollReview.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp2);
                } else {
                    tvOp2.setText(str);
                    ivOp2.setVisibility(View.GONE);
                }

            }else {
                llOp2.setVisibility(View.GONE);
            }

            str = listQs.get(i).getOption3();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                llOp3.setVisibility(View.VISIBLE);

                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    tvOp3.setText(s[0]);

                    Picasso.with(TeacherPollReview.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp3);
                } else {
                    tvOp3.setText(str);
                    ivOp3.setVisibility(View.GONE);
                }

            }else{
                llOp3.setVisibility(View.GONE);
            }

            str = listQs.get(i).getOption4();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                llOp4.setVisibility(View.VISIBLE);

                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    tvOp4.setText(s[0]);

                    Picasso.with(TeacherPollReview.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp4);
                } else {
                    tvOp4.setText(str);
                    ivOp4.setVisibility(View.GONE);
                }

            }else{
                llOp4.setVisibility(View.GONE);
            }
        }

        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        rb4.setVisibility(View.GONE);

        if (getIntent().getStringExtra("status").equalsIgnoreCase("0")) {
            btnSubmit.setVisibility(View.VISIBLE);
            etRemarks.setVisibility(View.VISIBLE);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(etRemarks.getText().toString().trim().length()>0){
                        arenaReview("2",new JSONArray());
                    }else {
                        //do section call and add object
                        getSections();
                        //arenaReview("1");
                    }
                }
            });
        }else {
            btnSubmit.setVisibility(View.GONE);
            etRemarks.setVisibility(View.GONE);
            if (listPolls.size()>0){
                calculate();
            }
        }
    }


    void calculate(){
        HashMap<String, Integer> mapPercent = new HashMap<>();

        List<String> keys = new ArrayList<>();

        for (int i=0;i<listQs.size();i++){
            if (listQs.get(i).getOption1()!=null || !listQs.get(i).getOption1().equalsIgnoreCase("") || !listQs.get(i).getOption1().equalsIgnoreCase("NA")){
                keys.add("option1");
            }
            if (listQs.get(i).getOption2()!=null || !listQs.get(i).getOption2().equalsIgnoreCase("") || !listQs.get(i).getOption2().equalsIgnoreCase("NA")){
                keys.add("option2");
            }
            if (listQs.get(i).getOption3()!=null || !listQs.get(i).getOption3().equalsIgnoreCase("") || !listQs.get(i).getOption3().equalsIgnoreCase("NA")){
                keys.add("option3");
            }
            if (listQs.get(i).getOption4()!=null || !listQs.get(i).getOption4().equalsIgnoreCase("") || !listQs.get(i).getOption4().equalsIgnoreCase("NA")){
                keys.add("option4");
            }
        }

        float total= 0;
        for (int i = 0; i < listPolls.size(); i++) {
            total =total+ (float)listPolls.get(i).getPollOptionCount();
        }


        for (int i=0;i<keys.size();i++){
            int c=0,pos=0;
            for (int j=0;j<listPolls.size();j++){
                if (listPolls.get(j).getPollOption().equalsIgnoreCase(keys.get(i))){
                    c++;
                    pos=j;
                }
            }
            if (c>0){
                float val = (float)listPolls.get(pos).getPollOptionCount();

                float percent = (val/total)*100;

                mapPercent.put(keys.get(i), Math.round(percent));
            }else {
                mapPercent.put(keys.get(i), 0);
            }
        }




        switch (keys.size()) {
            case 2:
                setProgress(mapPercent.get("option1"), mapPercent.get("option2"), 0, 0, 0);
                break;
            case 3:
                setProgress(mapPercent.get("option1"), mapPercent.get("option2"), mapPercent.get("option3"), 0, 0);
                break;
            case 4:
                setProgress(mapPercent.get("option1"), mapPercent.get("option2"), mapPercent.get("option3"), mapPercent.get("option4"), 0);
                break;
        }
    }

    void setProgress(int op1,int op2,int op3,int op4,int choice){
        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        rb4.setVisibility(View.GONE);

        tvPercentOp1.setText(op1+"%");
        tvPercentOp2.setText(op2+"%");
        tvPercentOp3.setText(op3+"%");
        tvPercentOp4.setText(op4+"%");

        tvPercentOp1.setVisibility(View.VISIBLE);
        tvPercentOp2.setVisibility(View.VISIBLE);
        tvPercentOp3.setVisibility(View.VISIBLE);
        tvPercentOp4.setVisibility(View.VISIBLE);

        tvOp1.setTextColor(Color.WHITE);
        tvOp2.setTextColor(Color.WHITE);
        tvOp3.setTextColor(Color.WHITE);
        tvOp4.setTextColor(Color.WHITE);



        ObjectAnimator.ofInt(progressOp1, "progress", op1)
                .setDuration(1000)
                .start();
        ObjectAnimator.ofInt(progressOp2, "progress", op2)
                .setDuration(300)
                .start();
        ObjectAnimator.ofInt(progressOp3, "progress", op3)
                .setDuration(300)
                .start();
        ObjectAnimator.ofInt(progressOp4, "progress", op4)
                .setDuration(300)
                .start();



        progressOp1.setVisibility(View.VISIBLE);
        progressOp2.setVisibility(View.VISIBLE);
        progressOp3.setVisibility(View.VISIBLE);
        progressOp4.setVisibility(View.VISIBLE);


    }

    public void arenaReview(String s, JSONArray sections) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("arenaId",pollObj.getArenaId()+"");
            if (s.equalsIgnoreCase("1")){
                jsonObject.put("arenaStatus","1");
                jsonObject.put("sections",sections);
            }else {
                jsonObject.put("arenaStatus","2");
                jsonObject.put("teacherReview",etRemarks.getText().toString());
            }
            jsonObject.put("createdBy",pollObj.getStudentId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().ReviewArenaStatus);
        utils.showLog(TAG,"url obj"+jsonObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().ReviewArenaStatus)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        utils.dismissDialog();
                        new AlertDialog.Builder(TeacherPollReview.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Oops! Something went wrong.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(true)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.v(TAG,"response "+resp);
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(TeacherPollReview.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Oops! Something went wrong.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObj = new JSONObject(resp);
                        if (jsonObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (s.equalsIgnoreCase("1")){
                                        new AlertDialog.Builder(TeacherPollReview.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Approved successfully!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }else {
                                        new AlertDialog.Builder(TeacherPollReview.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Review successfully sent!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }
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
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"")
                .build();

        utils.showLog(TAG, AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(TeacherPollReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherPollReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);

                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){

                            JSONArray array = respObj.getJSONArray("collegesInfo");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollegeInfo>>() {
                            }.getType();

                            List<CollegeInfo> listBranches = new ArrayList<>();
                            listBranches.clear();
                            listBranches.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(TeacherPollReview.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherPollReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                        utils.dismissDialog();
                    }
                });
            }
        });

    }


    void getNewSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+pollObj.getArenaId())
                .build();

        utils.showLog(TAG, AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+pollObj.getArenaId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(TeacherPollReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherPollReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);

                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){

                            JSONArray array = respObj.getJSONArray("collegesInfo");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollegeInfo>>() {
                            }.getType();

                            List<CollegeInfo> listBranches = new ArrayList<>();
                            listBranches.clear();
                            listBranches.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<listBranches.size();i++){
                                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                            for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getAssignedId().equalsIgnoreCase("0")){
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(false);
                                                    }else {
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(true);
                                                        listBranches.get(i).setShow(true);
                                                        for (int a=0;a<listBranches.get(i).getCourses().size();a++) {
                                                            listBranches.get(i).getCourses().get(a).setShow(true);
                                                            if (listBranches.get(i).getCourses().get(a).getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())){
                                                                for (int b=0;b<listBranches.get(i).getCourses().get(a).getClasses().size();b++){
                                                                    listBranches.get(i).getCourses().get(a).getClasses().get(b).setShow(true);
                                                                    if (listBranches.get(i).getCourses().get(a).getClasses().get(b).getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                                                        for (int c=0;c<listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().size();c++){
                                                                            listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().get(c).setShow(true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(TeacherPollReview.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherPollReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}