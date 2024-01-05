package myschoolapp.com.gsnedutech.Arena;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Arena.Models.PollOption;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppConst;
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

public class ArPollDisplayActivity extends AppCompatActivity {

    private static final String TAG = ArPollDisplayActivity.class.getName();

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


    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    ArenaRecord pollObj;

    List<PollOption> listPolls = new ArrayList<>();

    List<ArenaQuizQuestionFiles> listQs = new ArrayList<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    int studentStatus=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_poll_display);

        ButterKnife.bind(this);

        init();
    }

    void init(){

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

        pollObj = (ArenaRecord) getIntent().getSerializableExtra("poll");

        if (getIntent().hasExtra("reassign")){
            findViewById(R.id.tv_reassign_students).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_reassign_students).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSectionsNew();
                }
            });
        }

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

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postOptionCalc("option1");
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postOptionCalc("option2");
            }
        });

        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postOptionCalc("option3");
            }
        });

        rb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postOptionCalc("option4");
            }
        });

        getQuizDetails();
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


    void postOptionCalc(String choice){

        if (listPolls.size()>0){

            int c=0,pos=0;

            for (int i=0;i<listPolls.size();i++){
                if (listPolls.get(i).getPollOption().equalsIgnoreCase(choice)){
                    c++;
                    pos=i;
                }
            }

            if (c>0){
                listPolls.get(pos).setPollOptionCount((listPolls.get(pos).getPollOptionCount()+1));
                calculate();
            }else {
                PollOption pollOption = new PollOption();
                pollOption.setPollOption(choice);
                pollOption.setPollOptionCount(1);
                listPolls.add(pollOption);
                calculate();
            }
        }else {
            PollOption pollOption = new PollOption();
            pollOption.setPollOption(choice);
            pollOption.setPollOptionCount(1);
            listPolls.add(pollOption);
            calculate();
        }

        postOption(choice);
    }

    private void postOption(String choice) {

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("pollOption",choice);
            postObject.put("studentId",sObj.getStudentId());
            postObject.put("arenaId",pollObj.getArenaId());
            postObject.put("arenaQuestionId",listQs.get(0).getArenaQuestionId());
            postObject.put("createdBy",pollObj.getUserId());
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.InsertStudentPoll)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ArPollDisplayActivity.this,"Failed to update!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArPollDisplayActivity.this,"Failed to update!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);

                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArPollDisplayActivity.this,"Successfully updated!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArPollDisplayActivity.this,"Failed to update!",Toast.LENGTH_SHORT).show();
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

    void showImageDialog(String link){
        final Dialog dialog = new Dialog(ArPollDisplayActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        ImageView iv =  dialog.findViewById(R.id.iv_dialog);

        Picasso.with(ArPollDisplayActivity.this).load(link).placeholder(R.drawable.ic_arena_img)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(iv);
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
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + pollObj.getArenaId()+"&arenaCategory=7&studentId="+sObj.getStudentId();
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

                            if (jsonObject.has("studentStatus")){
                                studentStatus = jsonObject.getInt("studentStatus");
                            }

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

    void setOptionsLayout() {
        for (int i = 0; i < listQs.size(); i++) {

            String str = listQs.get(i).getQuestion();
            if (str.contains("~~")) {
                String[] s = str.split("~~");
                tvQues.setText(s[0]);

                Picasso.with(ArPollDisplayActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
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

                    Picasso.with(ArPollDisplayActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
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

                    Picasso.with(ArPollDisplayActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
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

                    Picasso.with(ArPollDisplayActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
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

                    Picasso.with(ArPollDisplayActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp4);
                } else {
                    tvOp4.setText(str);
                    ivOp4.setVisibility(View.GONE);
                }

            }else{
                llOp4.setVisibility(View.GONE);
            }
        }

        if (getIntent().hasExtra("draft") || getIntent().hasExtra("self") || (pollObj.getUserId()!=null && sObj!=null && pollObj.getUserId().equalsIgnoreCase(sObj.getStudentId())) || tObj!=null || studentStatus!=0){
            rb1.setVisibility(View.GONE);
            rb2.setVisibility(View.GONE);
            rb3.setVisibility(View.GONE);
            rb4.setVisibility(View.GONE);

            if((pollObj.getUserId()!=null && sObj!=null && pollObj.getUserId().equalsIgnoreCase(sObj.getStudentId())) || tObj!=null || studentStatus!=0){

                if (listPolls!=null && listPolls.size()>0) {
                    calculate();
                }
            }

            if (getIntent().hasExtra("draft")) {
                btnSubmit.setVisibility(View.VISIBLE);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                            submitArena(pollObj.getArenaId() + "", "");
                            getSections();
                        } else {
                            getTeachers(pollObj.getArenaId() + "");
                        }
                    }
                });
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
                setProgress(mapPercent.get("option1"), mapPercent.get("option2"), 0, 0);
                break;
            case 3:
                setProgress(mapPercent.get("option1"), mapPercent.get("option2"), mapPercent.get("option3"), 0);
                break;
            case 4:
                setProgress(mapPercent.get("option1"), mapPercent.get("option2"), mapPercent.get("option3"), mapPercent.get("option4"));
                break;
        }
    }

    private void getTeachers(String arenaId) {

        utils.showLoader(ArPollDisplayActivity.this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("sectionId",sObj.getClassCourseSectionId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeachersForArena)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArPollDisplayActivity.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nPoll Article saved in drafts in my polls. Can submit for approval later.")

                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ArPollDisplayActivity.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nPoll Article saved in drafts in my polls. Can submit for approval later.")

                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = respObj.getJSONArray("arenaRecords");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaTeacherList>>(){}.getType();
                            listTeachers.clear();
                            listTeachers.addAll(gson.fromJson(array.toString(),type));

                            if (listTeachers.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog dialog = new Dialog(ArPollDisplayActivity.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(ArPollDisplayActivity.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Poll Article saved in drafts in my polls. Can submit for approval later.")

                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });

                                        RecyclerView rvTeachers = dialog.findViewById(R.id.rv_teachers);
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(ArPollDisplayActivity.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ArPollDisplayActivity.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nPoll cannot be sent for approval\nPlease contact your school for more information.")

                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(ArPollDisplayActivity.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nPoll Article saved in drafts in my polls. Can submit for approval later.")

                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
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

    class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder>{

        List<ArenaTeacherList> teacherList; String arenaId;

        public TeacherAdapter(List<ArenaTeacherList> teacherList, String arenaId) {
            this.teacherList = teacherList;
            this.arenaId = arenaId;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArPollDisplayActivity.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(listTeachers.get(position).getUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitArena(arenaId,listTeachers.get(position).getTeacherId()+"");
                }
            });

        }

        @Override
        public int getItemCount() {
            return teacherList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);

            }
        }
    }

    private void submitArena(String arenaId, String teacherId) {

        utils.showLoader(ArPollDisplayActivity.this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("arenaId",arenaId);

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("createdBy", tObj.getUserId());
                postObject.put("arenaStatus", "1");
            }else {
                postObject.put("userId", sObj.getStudentId());
                postObject.put("createdBy", sObj.getStudentId());
                postObject.put("teacherId", teacherId);
            }

            postObject.put("arenaDraftStatus","1");
            postObject.put("schemaName", sh_Pref.getString("schema",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = new AppUrls().SubmitTeacherArena;
        }else {
            url = new AppUrls().SubmitStudentArena;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArPollDisplayActivity.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("oops! something went wrong please try again later")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                })

                                .setCancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(ArPollDisplayActivity.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("oops! something went wrong please try again later")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        }
                                    })

                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArPollDisplayActivity.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArPollDisplayActivity.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("oops! something went wrong please try again later")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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
                        Toast.makeText(ArPollDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArPollDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(ArPollDisplayActivity.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArPollDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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

            jsonObject.put("arenaId",pollObj.getArenaId());

            jsonObject.put("arenaDraftStatus", "1");
            jsonObject.put("arenaStatus","1");
            jsonObject.put("sections",sections);
            jsonObject.put("createdBy",tObj.getUserId()+"");
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
                        new AlertDialog.Builder(ArPollDisplayActivity.this)
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
                            new AlertDialog.Builder(ArPollDisplayActivity.this)
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
                                        new AlertDialog.Builder(ArPollDisplayActivity.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Approved successfully!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }else {
                                        new AlertDialog.Builder(ArPollDisplayActivity.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Review successfully sent!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
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

    void getSectionsNew(){
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
                        Toast.makeText(ArPollDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArPollDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(ArPollDisplayActivity.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArPollDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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