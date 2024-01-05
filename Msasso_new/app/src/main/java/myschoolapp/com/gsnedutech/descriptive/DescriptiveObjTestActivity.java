/*
 * *
 *  * Created by SriRamaMurthy A on 3/10/19 7:01 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/10/19 7:01 PM
 *
 */

package myschoolapp.com.gsnedutech.descriptive;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.JEETestSectionDetail;
import myschoolapp.com.gsnedutech.Models.OnlineQues;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionParagraphDetails;
import myschoolapp.com.gsnedutech.Models.TemplateSection;
import myschoolapp.com.gsnedutech.Models.UpdateObj;
import myschoolapp.com.gsnedutech.OnlIneTest.Config;
import myschoolapp.com.gsnedutech.OnlIneTest.OTLoginResult;
import myschoolapp.com.gsnedutech.OnlIneTest.OTStudentTestOnlineCompleted;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MultiSpinner;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DescriptiveObjTestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = DescriptiveObjTestActivity.class.getName();
    MyUtils utils = new MyUtils();

    @BindView(R.id.test_title)
    TextView testTitle;
    @BindView(R.id.tv_test_timer)
    TextView tvTestTimer;
    @BindView(R.id.spinner_sub)
    Spinner subSpinner;
    @BindView(R.id.tv_test_count)
    TextView tvTestCount;
    @BindView(R.id.btn_test_finish)
    Button btnTestFinish;
    @BindView(R.id.que_no)
    TextView tvQueNo;
    @BindView(R.id.tv_pshow)
    TextView tvPshow;
    @BindView(R.id.tv_sec)
    TextView tvSecName;
    @BindView(R.id.wv_test)
    WebView wvTest;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.quelist)
    TextView quelist;
    @BindView(R.id.rv_quelist)
    RecyclerView rvQuelist;
    @BindView(R.id.hidden_panel)
    RelativeLayout hiddenPanel;
    @BindView(R.id.ll_mfq)
    ScrollView llMfq;
    @BindView(R.id.tl_answer)
    TableLayout tlAnswer;
    @BindView(R.id.ll_fib)
    ScrollView llFib;
    @BindView(R.id.ll_mcq)
    LinearLayout llMcq;
    @BindView(R.id.ll_tf)
    LinearLayout llTf;
    @BindView(R.id.ll_itq)
    LinearLayout llITQ;
    @BindView(R.id.et_itq)
    EditText etItq;
    @BindView(R.id.layout_fibans)
    LinearLayout layoutFibans;
    @BindView(R.id.tv_itqInst)
    TextView tvItqInst;
    @BindView(R.id.ll_option)
    LinearLayout llOption;
    @BindView(R.id.ll_que)
    LinearLayout llQue;


    String paragraphString = "";
    String queType = "";

    int quesNo = 0, testTime = 0, total_testTime = 0;

    private Button[] tfbtn = new Button[2];
    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};

    List<MultiSpinner> spinner_list = new ArrayList<>();
    List<String> answers_list = new ArrayList<>();

    List<EditText[]> editTextLists = new ArrayList<>();


    private long startTime = 0L;
    private Handler customHandler = new Handler();

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;
    private long timeCountInMilliSeconds = 1 * 60000;

    List<Integer> itqListCount = new ArrayList<>();
    Boolean itqCount = false;
    int itqCountMax;

    List<String> subList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();
    List<OnlineQuestionParagraphDetails> paragraphDetailsList = new ArrayList<>();
    List<UpdateObj> updateObjList = new ArrayList<>();
    ArrayList<ArrayList<OnlineQuestionObj2>> arrayOfArrays = new ArrayList<>();

    private Button[] btn = new Button[4];
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    String testId,  wrongMarks, testCategory;
    int correctMarks = 0;
    String appVersion = "";

    int studentId;

    List<JEETestSectionDetail> listSectionDetails = new ArrayList<>();
    String selectedAnswer = "";

    File postFile;
    String key, _finalUrl, baseUrl;
    String examdetId = "";
    String eStautus = "";

    SharedPreferences sh_Pref;
    SharedPreferences.Editor editor;
    OTLoginResult sObj;
    String insertId;
    String testStartTime;
    Timer timer;

    Boolean subChanged = false;
    int subSpinnerPosition = 0;

    //config
    Config config;

    boolean isINPFirst = false;
    boolean isSUBFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_o_t_student_online_test);
        ButterKnife.bind(this);

        init();

        String ss = getIntent().getStringExtra("objList");
        testQueList = new Gson().fromJson(ss, new TypeToken<List<OnlineQuestionObj2>>() {
        }.getType());

        PrepareQuestionPaper();

//        if (NetworkConnectivity.isConnected(DescriptiveObjTestActivity.this)) {
//            updatedDevice();
//            getOnlineTestQuePaper(getIntent().getStringExtra("testFilePath"));
//        } else {
//            new MyUtils().alertDialog(1, DescriptiveObjTestActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
//                    getString(R.string.action_settings), getString(R.string.action_close), false);
//        }

        /* loading question every list item clicks */
        rvQuelist.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null && gestureDetector.onTouchEvent(motionEvent)) {

//                    Log.v(TAG, "que time new " + arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());

                    int position = recyclerView.getChildAdapterPosition(child);
                    quesNo = position;
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setVisitedflag(true);
                    if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                    }
                    loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), quesNo);
                    slideUpDown();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        /* Showing Paragraph in a Dialog */
        tvPshow.setOnClickListener(v -> {
            Dialog dialog = new Dialog(DescriptiveObjTestActivity.this);
            dialog.setContentView(R.layout.dialog_paragraph);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();
            WebView wvParagraph = dialog.findViewById(R.id.wv_question_paragraph);
            paragraphString = "<!DOCTYPE html> <html>" + paragraphString + "</html>";
            paragraphString = utils.cleanWebString(paragraphString);
            Log.v(TAG, "paragraphString - " + paragraphString);
            wvParagraph.loadData(paragraphString, "text/html; charset=utf-8", "utf-8");
            wvParagraph.scrollTo(0, 0);
            dialog.findViewById(R.id.tv_paraTitle).setOnClickListener(v1 -> dialog.dismiss());
        });

        /* ITQ Text Change Listner */
        etItq.addTextChangedListener(new TextWatcher() {

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
                if (length > 0 && !Pattern.matches("^-?[0-9]{0,4}(\\.\\d{0,2}+)?$", text)) {
                    s.delete(length - 1, length);
                    Toast.makeText(DescriptiveObjTestActivity.this, "You can enter 4 digit before decimal and 2 digit after decimal. (eg:- 9999.99)", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (isPanelShown())
            slideUpDown();
        else {
            Toast.makeText(this, "Please Submit the Test to Exit.", Toast.LENGTH_SHORT).show();
        }

    }

    /* Update Device Details to Server */
    void updatedDevice() {
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
            jsonObject.put("dMake", Build.MANUFACTURER + " " + Build.MODEL);
            jsonObject.put("dType", "Mobile");
            jsonObject.put("osVersion", Build.VERSION.RELEASE);
            jsonObject.put("appVersion", appVersion);
            jsonObject.put("bVersion", "");
            jsonObject.put("ip", MyUtils.getIPAddress(true));
            jsonObject.put("IMEI", "");
            jsonObject.put("MACaddress", MyUtils.getMACAddress("wlan0"));
            jsonObject.put("geoLocation", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG, "TestGetQue Url - " + AppUrls.UPDATEDEVICE);
        utils.showLog(TAG, "TestGetQue URL LogingObj - " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.UPDATEDEVICE)
                .post(body)
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

        utils.showLog(TAG, "TestGetQue Url - " + AppUrls.UPDATELIVEEXAMSTATUS);
        utils.showLog(TAG, "TestGetQue URL LogingObj - " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.UPDATELIVEEXAMSTATUS)
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    /* Adapter for Showing JeeSection in Dialog */
    class JeeSectionsAdapter extends RecyclerView.Adapter<JeeSectionsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(DescriptiveObjTestActivity.this).inflate(R.layout.item_jee_section_details, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.tvSectionName.setText(listSectionDetails.get(i).getSectionName());
            viewHolder.tvQuestType.setText(listSectionDetails.get(i).getQuestType());
            viewHolder.tvPosMarks.setText(listSectionDetails.get(i).getCorrectAnswerMarks());
            viewHolder.tvNegMarks.setText(listSectionDetails.get(i).getWrongAnswerMarks());
            viewHolder.tvNumOfQuestions.setText(listSectionDetails.get(i).getNumQuestions());
            viewHolder.tvTotal.setText((Integer.parseInt(listSectionDetails.get(i).getNumQuestions()) * (Integer.parseInt(listSectionDetails.get(i).getCorrectAnswerMarks()))) + "");
            viewHolder.rvInstructions.setLayoutManager(new LinearLayoutManager(DescriptiveObjTestActivity.this));
            viewHolder.rvInstructions.setAdapter(new InstructionAdapter(listSectionDetails.get(i).getSubject(), listSectionDetails.get(i).getTemplateSection()));
        }

        @Override
        public int getItemCount() {
            return listSectionDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSectionName, tvQuestType, tvPosMarks, tvNegMarks, tvNumOfQuestions, tvTotal;
            RecyclerView rvInstructions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSectionName = itemView.findViewById(R.id.tv_jee_sec_name);
                tvQuestType = itemView.findViewById(R.id.tv_jee_q_type);
                tvPosMarks = itemView.findViewById(R.id.tv_jee_positive);
                tvNegMarks = itemView.findViewById(R.id.tv_jee_negative);
                tvNumOfQuestions = itemView.findViewById(R.id.tv_jee_questions);
                tvTotal = itemView.findViewById(R.id.tv_jee_total);
                rvInstructions = itemView.findViewById(R.id.rv_instructions);
            }
        }
    }

    /* Adapter for Showing Instructions in Dialog */
    class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.ViewHolder> {

        List<String> subject;
        List<TemplateSection> templateSection;

        public InstructionAdapter(List<String> subject, List<TemplateSection> templateSection) {
            this.subject = subject;
            this.templateSection = templateSection;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(DescriptiveObjTestActivity.this).inflate(R.layout.item_instructions, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSubName.setText(subject.get(position));
            holder.tvInstructions.setText(templateSection.get(position).getSectionsDesc());
        }

        @Override
        public int getItemCount() {
            return subject.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName, tvInstructions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvInstructions = itemView.findViewById(R.id.tv_instructions);
            }
        }
    }

    /* Initialization */
    private void init() {

        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!getIntent().getStringExtra("jeeSectionTemplate").equalsIgnoreCase("NA")) {
            if (getIntent().getExtras().containsKey("sections")) {
                listSectionDetails.addAll((List<JEETestSectionDetail>) getIntent().getSerializableExtra("sections"));

                if (listSectionDetails.get(0).getTemplateSection() != null && listSectionDetails.get(0).getTemplateSection().size() > 0) {
                    findViewById(R.id.tv_instructions).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_instructions).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialog dialog = new Dialog(DescriptiveObjTestActivity.this);
                            dialog.setContentView(R.layout.dialog_instructions);
                            dialog.setCancelable(true);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            dialog.show();

                            RecyclerView rvInstructions = dialog.findViewById(R.id.rv_instructions);
                            rvInstructions.setLayoutManager(new LinearLayoutManager(DescriptiveObjTestActivity.this));
                            rvInstructions.setAdapter(new JeeSectionsAdapter());

                            dialog.findViewById(R.id.tv_paraTitle).setOnClickListener(view1 -> dialog.dismiss());
                        }
                    });
                }

                for (int i = 0; i < listSectionDetails.size(); i++) {
                    if (Integer.parseInt(listSectionDetails.get(i).getQuestionLimitCount()) > 0) {
                        itqCount = true;
                        if (listSectionDetails.get(i).getQuestType().equalsIgnoreCase("ITQ")) {
                            itqCountMax = Integer.parseInt(listSectionDetails.get(i).getQuestionLimitCount());
                            tvItqInst.setText("You have already answered " + itqCountMax + " ITQ questions in this Subject.\n Please Clear any one of your choice to answer this Questions");
                        }
                    }
                }

            }
        }
        testTitle.setText(getIntent().getStringExtra("testName"));
        testTime = (Integer.parseInt(getIntent().getStringExtra("testTime")));
        testStartTime = getIntent().getStringExtra("startTime");

        examdetId = getIntent().getStringExtra("examdet_Id");
        eStautus = getIntent().getStringExtra("studentTestFilePath");
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

        testId = String.valueOf(getIntent().getIntExtra("testId", 0));
        studentId = getIntent().getIntExtra("studentId", 0);
        correctMarks = getIntent().getIntExtra("correctMarks", 0);
        wrongMarks = String.valueOf(getIntent().getIntExtra("wrongMarks", 0));
        testCategory = String.valueOf(getIntent().getIntExtra("testCategory", 0));


        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundResource(R.drawable.practice_btn_opt);
            btn[i].setOnClickListener(this);
        }

        for (int i = 0; i < tfbtn.length; i++) {
            tfbtn[i] = findViewById(tfbtn_id[i]);
            tfbtn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            tfbtn[i].setOnClickListener(this);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvQuelist.setLayoutManager(mLayoutManager);
        rvQuelist.setItemAnimator(new DefaultItemAnimator());

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        editor = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("otStudentObj", "");
        String confJson = sh_Pref.getString("config", "");
        sObj = gson.fromJson(json, OTLoginResult.class);
        config = gson.fromJson(confJson, Config.class);
        Log.v(TAG, "aKey - " + config.getS3Details().getaKey());
        Log.v(TAG, "sKey - " + config.getS3Details().getsKey());
        Log.v(TAG, "buc - " + config.getS3Details().getBucketName());

    }

    /* Start Test */
    private void startTest() {

        for (int i = 0; i < testQueList.size(); i++) {
            if (!subList.contains(testQueList.get(i).getSubjectGroup())) {
                subList.add(testQueList.get(i).getSubjectGroup());
                if (itqCount) {
                    itqListCount.add(0);
                }
            }
        }

        if (subList.size() < 2) {
            subSpinner.setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < subList.size(); i++) {
            ArrayList<OnlineQuestionObj2> sub_quelist = new ArrayList<>();
            for (int j = 0; j < testQueList.size(); j++) {
                if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                    sub_quelist.add(testQueList.get(j));
                    if (itqCount && testQueList.get(j).getQuestType().equalsIgnoreCase("ITQ")
                            && !testQueList.get(j).getSelectedAnswer().equalsIgnoreCase("")) {
                        itqListCount.set(i, (itqListCount.get(i) + 1));
                    }
                }
            }
//            TODO : sort the array with examquestion number
            arrayOfArrays.add(sub_quelist);
        }
        Log.v(TAG, "arrayoff arrays size - " + arrayOfArrays.size());


        timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                PrepareResult(false);
            }
        };

        /* schedule the task to run starting now and then every hour... */
        timer.schedule(hourlyTask, 0l, 1000 * config.getDbSync());

        /* Creating adapter for spinner */
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(DescriptiveObjTestActivity.this, R.layout.spinner_test_item, subList);

        /* Drop down layout style - list view with radio button */
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* attaching data adapter to spinner */
        subSpinner.setAdapter(dataAdapter);

        /* Spinner click listener */
        subSpinner.setOnItemSelectedListener(DescriptiveObjTestActivity.this);


        startStop();
    }

    @Override
    public void onClick(View v) {
        if (queType.equalsIgnoreCase("MAQ")) {
            switch (v.getId()) {
                case R.id.btn_a:
                    selectMultiple(btn[0], "option1");
                    break;
                case R.id.btn_b:
                    selectMultiple(btn[1], "option2");
                    break;
                case R.id.btn_c:
                    selectMultiple(btn[2], "option3");
                    break;
                case R.id.btn_d:
                    selectMultiple(btn[3], "option4");
                    break;
            }
        } else {
            setUnFocusAll();
            switch (v.getId()) {
                case R.id.btn_a:
                    setFocus(btn[0]);
                    selectedAnswer = "option1";
                    break;
                case R.id.btn_b:
                    setFocus(btn[1]);
                    selectedAnswer = "option2";
                    break;
                case R.id.btn_c:
                    setFocus(btn[2]);
                    selectedAnswer = "option3";
                    break;
                case R.id.btn_d:
                    setFocus(btn[3]);
                    selectedAnswer = "option4";
                    break;
                case R.id.btn_true:
                    setFocus(tfbtn[0]);
                    selectedAnswer = "true";
                    break;

                case R.id.btn_false:
                    setFocus(tfbtn[1]);
                    selectedAnswer = "false";
                    break;
            }
        }
    }

    /* Select Multiple options for MAQ */
    private void selectMultiple(Button button, String option) {
        if (button.getCurrentTextColor() == Color.parseColor("#FFFFFF")) {
            setUnFocus(button);
            selectedAnswer = selectedAnswer.replace("," + option, "");
            selectedAnswer = selectedAnswer.replace(option + ",", "");
        } else {
            setFocus(button);
            selectedAnswer = selectedAnswer + "," + option;
        }
    }

    /* Save&Next, MArkForReview, Clear and Submit Click Events  */
    @OnClick({R.id.img_test_qlist, R.id.btn_test_finish, R.id.btn_clear, R.id.btn_review_next, R.id.btn_save_next, R.id.img_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_test_qlist:
                slideUpDown();
                break;
            case R.id.btn_test_finish:
                finishalert();
                break;

            case R.id.btn_clear:
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setReviewflag(false);
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                setUnFocusAll();

                etItq.setText("");

                if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {

                    for (int i = 0; i < editTextLists.size(); i++) {
                        for (int j = 0; j < editTextLists.get(i).length; j++) {
                            editTextLists.get(i)[j].getText().clear();
                        }
                    }
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {

                    if (itqCount && !(arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase("")))
                        itqListCount.set(subSpinner.getSelectedItemPosition(), (itqListCount.get(subSpinner.getSelectedItemPosition()) - 1));


                    if (itqListCount.get(subSpinnerPosition) >= itqCountMax) {
                        etItq.setVisibility(View.GONE);
                        tvItqInst.setVisibility(View.VISIBLE);
                    } else {
                        etItq.setVisibility(View.VISIBLE);
                        tvItqInst.setVisibility(View.GONE);
                    }

                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(etItq.getText().toString());

                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {
                    for (int i = 0; i < spinner_list.size(); i++) {
                        spinner_list.get(i).setSelection("");
                    }
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");
                    selectedAnswer = "";
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MCQ")
                        || arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MAQ")) {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");
                    selectedAnswer = "";
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");
                    selectedAnswer = "";
                }

                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");

                break;
            case R.id.btn_review_next:
                if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {

                    int counter = 0;
                    String answers = "";
                    for (int i = 0; i < spinner_list.size(); i++) {
                        if (!(spinner_list.get(i).getSelection().equalsIgnoreCase(""))) {
                            counter++;
                            if (i == 0) {
                                answers = answers + (i + 1) + "-" + spinner_list.get(i).getSelection();
                            } else {
                                answers = answers + "," + (i + 1) + "-" + spinner_list.get(i).getSelection();
                            }

                        } else {
                            if (i == 0) {
                                answers = answers + (i + 1) + "-";
                            } else {
                                answers = answers + "," + (i + 1) + "-";

                            }

                        }
                    }
                    if (counter == 0)
                        answers = "";

                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {

                    String answers = "";
                    for (int i = 0; i < editTextLists.size(); i++) {
                        if (i > 0) {
                            answers = answers + ",";
                        }
                        for (int j = 0; j < editTextLists.get(i).length; j++) {
                            answers = answers + editTextLists.get(i)[j].getText().toString();
                        }
                        Log.v(TAG, " Answer " + answers);
                    }

                    if (!answers.equalsIgnoreCase(""))
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
                    if (itqCount && !etItq.getText().toString().equalsIgnoreCase("") &&
                            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase(""))
                        itqListCount.set(subSpinner.getSelectedItemPosition(), (itqListCount.get(subSpinner.getSelectedItemPosition()) + 1));
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(etItq.getText().toString());
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MCQ")
                        || arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MAQ")
                        || arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("CPQ")) {
                    if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MAQ"))
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(sortOptions(selectedAnswer));
                    else
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                }


                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setReviewflag(true);
                if (!arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase("")) {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("answered_marked_div");
                } else {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("markedrview_div");
                }

                if (++quesNo < arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size()) {
                    if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                    }
                    loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), quesNo);
                } else {
                    quesNo = quesNo - 1;
                    if (subSpinner.getSelectedItemPosition() < subList.size() - 1) {
                        subSpinner.setSelection(subSpinner.getSelectedItemPosition() + 1);
                    } else {
                        finishalert();
                    }
                }
                break;
            case R.id.btn_save_next:
                Log.v(TAG, "type - " + arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType());
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {

                    int counter = 0;

                    String answers = "";
                    for (int i = 0; i < spinner_list.size(); i++) {
                        if (!(spinner_list.get(i).getSelection().equalsIgnoreCase(""))) {
                            counter++;
                            if (i == 0) {
                                answers = answers + (i + 1) + "-" + spinner_list.get(i).getSelection();
                            } else {
                                answers = answers + "," + (i + 1) + "-" + spinner_list.get(i).getSelection();
                            }
                        } else {
                            if (i == 0) {
                                answers = answers + (i + 1) + "-";
                            } else {
                                answers = answers + "," + (i + 1) + "-";

                            }
                        }
                    }
                    if (counter == 0)
                        answers = "";

                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);

                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {
                    String answers = "";
                    for (int i = 0; i < editTextLists.size(); i++) {
                        if (i > 0) {
                            answers = answers + ",";
                        }
                        for (int j = 0; j < editTextLists.get(i).length; j++) {
                            answers = answers + editTextLists.get(i)[j].getText().toString();
                        }
                        Log.v(TAG, " Answer " + answers);
                    }
                    if (!answers.equalsIgnoreCase(""))
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
                    if (itqCount && !etItq.getText().toString().equalsIgnoreCase("") &&
                            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase(""))
                        itqListCount.set(subSpinner.getSelectedItemPosition(), (itqListCount.get(subSpinner.getSelectedItemPosition()) + 1));
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(etItq.getText().toString());
                    etItq.setText("");
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MCQ")
                        || arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MAQ")
                        || arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("CPQ")) {
                    if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MAQ"))
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(sortOptions(selectedAnswer));
                    else
                        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);

                    selectedAnswer = "";
                } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                }
                if (!arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase("") && !(allCharactersSame(arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer())) && !arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase(",")) {
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setReviewflag(false);
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("answered_div");
                    if (++quesNo < arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size()) {
                        if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                        }
                        loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), quesNo);
                    } else {
                        quesNo = quesNo - 1;
                        if (subSpinner.getSelectedItemPosition() < subList.size() - 1)
                            subSpinner.setSelection(subSpinner.getSelectedItemPosition() + 1);
                        else
                            finishalert();
                    }
                } else {
                    skipQuestion();
                }
                break;
            case R.id.img_close:
                slideUpDown();
                break;
        }
    }

    /* Sorting MAQ Options */
    private String sortOptions(String selectedAnswer) {
        String Sorted = "";
        if (selectedAnswer.contains("option1"))
            Sorted = "option1";

        if (selectedAnswer.contains("option2")) {
            if (Sorted.equalsIgnoreCase(""))
                Sorted = "option2";
            else
                Sorted = Sorted + ",option2";

        }
        if (selectedAnswer.contains("option3")) {
            if (Sorted.equalsIgnoreCase(""))
                Sorted = "option3";
            else
                Sorted = Sorted + ",option3";

        }
        if (selectedAnswer.contains("option4")) {
            if (Sorted.equalsIgnoreCase(""))
                Sorted = "option4";
            else
                Sorted = Sorted + ",option4";

        }

        return Sorted;
    }

    /* Skip Question */
    private void skipQuestion() {
        if (++quesNo < arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size()) {
            if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
            }
            loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), quesNo);
        } else {
            quesNo = quesNo - 1;
            Log.v(TAG, "position - " + subSpinner.getSelectedItemPosition());
            if (subSpinner.getSelectedItemPosition() < subList.size() - 1)
                subSpinner.setSelection(subSpinner.getSelectedItemPosition() + 1);
            else
                finishalert();
        }
    }

    /* Showing Questions List with Answers and questiondiv status */
    private void slideUpDown() {

        if (!isPanelShown()) {

            // Show the panel
            quelist.setText(subSpinner.getSelectedItem().toString());
            RecyclerView.Adapter adapter = new OnlineTestQueListAdapter(DescriptiveObjTestActivity.this, arrayOfArrays.get(subSpinner.getSelectedItemPosition()));
            rvQuelist.setAdapter(adapter);

            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
        } else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
        }
    }

    /* Checking whether the question list is showing in Panel or not */
    private boolean isPanelShown() {

        return hiddenPanel.getVisibility() == View.VISIBLE;
    }

    /* Loading Questions based on Subjects spinner item */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        subSpinnerPosition = position;
        quesNo = 0;
        if (arrayOfArrays.get(position).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
            arrayOfArrays.get(position).get(quesNo).setStyle("not_answered_div");
        }

        loadQuestion(arrayOfArrays.get(position), quesNo);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /* Loading question from Question List */
    private void loadQuestion(ArrayList<OnlineQuestionObj2> questions, int quesNo) {

        if (!questions.get(quesNo).getTestSectionName().equalsIgnoreCase("NA"))
            tvSecName.setText(questions.get(quesNo).getTestSectionName() + "(" + questions.get(quesNo).getQuestType() + ")");
        else
            tvSecName.setText("(" + questions.get(quesNo).getQuestType() + ")");
        tvQueNo.setText("Q no: " + questions.get(quesNo).getQuestion_number());
        selectedAnswer = questions.get(quesNo).getSelectedAnswer();
        unanswered_count();
        setUnFocusAll();

        Log.v(TAG, "que time Old  " + questions.get(quesNo).getTimeTaken());

        if (subChanged) {
            subChanged = false;
        }

        questions.get(quesNo).setVisitedflag(true);
        queType = questions.get(quesNo).getQuestType();


        if (questions.get(quesNo).getQuestType().equalsIgnoreCase("MCQ")
                || questions.get(quesNo).getQuestType().equalsIgnoreCase("CPQ")
                || questions.get(quesNo).getQuestType().equalsIgnoreCase("MAQ")) {

            if (questions.get(quesNo).getQuestType().equalsIgnoreCase("CPQ")) {
                for (int i = 0; i < paragraphDetailsList.size(); i++) {
                    if (questions.get(quesNo).getParagraphId().equalsIgnoreCase(String.valueOf(paragraphDetailsList.get(i).getParagraphId()))) {
                        paragraphString = paragraphDetailsList.get(i).getParagraph();
                    }
                }
                tvPshow.setVisibility(View.VISIBLE);
            } else {
                tvPshow.setVisibility(View.GONE);
                paragraphString = "";
            }


            llMcq.setVisibility(View.VISIBLE);
            wvTest.setVisibility(View.VISIBLE);
            llFib.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            setUnFocusAll();
            Log.v(TAG, "SelectedAnswer - " + questions.get(quesNo).getSelectedAnswer());

            if (questions.get(quesNo).getQuestType().equalsIgnoreCase("MAQ")) {
                if (questions.get(quesNo).getSelectedAnswer().contains("option1")) {
                    setFocus(btn[0]);
                }
                if (questions.get(quesNo).getSelectedAnswer().contains("option2")) {
                    setFocus(btn[1]);
                }
                if (questions.get(quesNo).getSelectedAnswer().contains("option3")) {
                    setFocus(btn[2]);
                }
                if (questions.get(quesNo).getSelectedAnswer().contains("option4")) {
                    setFocus(btn[3]);
                }
            } else {
                if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option1")) {
                    setFocus(btn[0]);
                } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option2")) {
                    setFocus(btn[1]);
                } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option3")) {
                    setFocus(btn[2]);
                } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option4")) {
                    setFocus(btn[3]);
                }
            }
            String mainString = "<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() +
                    "" + questions.get(quesNo).getOption1() +
                    "" + questions.get(quesNo).getOption2() +
                    "" + questions.get(quesNo).getOption3() +
                    "" + questions.get(quesNo).getOption4() +
                    "</html>";
            mainString = utils.cleanWebString(mainString);
            wvTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvTest.scrollTo(0, 0);
        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {
            Log.v(TAG, "FIB Que - " + questions.get(quesNo).getQuestion());
            Log.v(TAG, "\n FIB Que - " + questions.get(quesNo).getCorrectAnswer());
            llFib.setVisibility(View.VISIBLE);
            layoutFibans.removeAllViews();
            editTextLists.clear();

            wvTest.setVisibility(View.VISIBLE);
            tvPshow.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            String mainString = "<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvTest.scrollTo(0, 0);

            setFIBLayout(questions.get(quesNo).getCorrectAnswer(), questions.get(quesNo).getSelectedAnswer());

        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
            wvTest.setVisibility(View.VISIBLE);
            llITQ.setVisibility(View.VISIBLE);
            tvPshow.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            llOption.removeAllViews();


            if (!(questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase(""))
                    && !(questions.get(quesNo).getSelectedAnswer().isEmpty())) {
                etItq.setText("" + questions.get(quesNo).getSelectedAnswer());
                etItq.setVisibility(View.VISIBLE);
                tvItqInst.setVisibility(View.GONE);
            } else {
                etItq.setText("");
                if (itqCount) {
                    if (itqListCount.get(subSpinnerPosition) >= itqCountMax) {
                        etItq.setVisibility(View.GONE);
                        tvItqInst.setVisibility(View.VISIBLE);
                    } else {
                        etItq.setVisibility(View.VISIBLE);
                        tvItqInst.setVisibility(View.GONE);
                    }
                }
            }


            String mainString = "<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");

            wvTest.scrollTo(0, 0);


        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
            llTf.setVisibility(View.VISIBLE);
            wvTest.setVisibility(View.VISIBLE);
            tvPshow.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            setUnFocusAll();
            if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("true")) {
                setFocus(tfbtn[0]);
            } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("false")) {
                setFocus(tfbtn[1]);
            }

            String mainString = "<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvTest.scrollTo(0, 0);

        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {
            llMfq.setVisibility(View.VISIBLE);
            tlAnswer.setVisibility(View.VISIBLE);
            tvPshow.setVisibility(View.GONE);

            llQue.removeAllViews();
            tlAnswer.removeAllViews();
            spinner_list.clear();

            wvTest.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);

            setMFQlayout(questions.get(quesNo).getQuestions(), questions.get(quesNo).getSelectedAnswer());
        }


    }

    /* Setting Data Fill In the Blanks Question Dynamic Layout  */
    private void setFIBLayout(String cAnswer, String selAns) {
        String[] fibans_words;
//        cAnswer = cAnswer.replaceAll("\\s", "");
        cAnswer = cAnswer.toLowerCase();
        if (cAnswer.contains(",")) {
            fibans_words = cAnswer.split(",");
        } else {
            fibans_words = new String[1];
            fibans_words[0] = cAnswer;
        }
//        Adding the Blanks
        TableLayout linear_answer = LayoutInflater.from(this).inflate(R.layout.layout_linearblanks, null, false).findViewById(R.id.ll_blanks);
        for (int i = 0; i < fibans_words.length; i++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            row.setPadding(0, 30, 0, 30);
            final EditText[] ed_array = new EditText[fibans_words[i].length()];
            TextView tvQNum = LayoutInflater.from(this).inflate(R.layout.view_textqnum, null, false).findViewById(R.id.tv_qNum);
            int number = i + 1;
            tvQNum.setText(number + ". ");
            tvQNum.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            row.addView(tvQNum);
            for (int j = 0; j < fibans_words[i].length(); j++) {
                final EditText editText = LayoutInflater.from(this).inflate(R.layout.view_editblanks, null, false).findViewById(R.id.et_blank);
                if (j == 0) {
                    editText.setBackgroundResource(R.drawable.blank_left_bg);
                } else if (j == fibans_words[i].length() - 1) {
                    editText.setBackgroundResource(R.drawable.blank_right_bg);
                } else {
                    editText.setBackgroundResource(R.drawable.blank_bg);
                }
                row.addView(editText);
                ed_array[j] = editText;
                final int finalI = i;
                final int finalJ = j;
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        editText.setSelection(editText.getText().length());
                        return false;
                    }
                });
                editText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            if (editText.getText().toString().length() == 0) {
                                if (finalJ + 1 > 1) {
                                    ed_array[finalJ - 1].requestFocus();
                                }
                            }
                        }
                        return false;
                    }
                });
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals("\n")) {
                            String str = s.toString();
                            int n = str.length();
                            if (n == 1) {
                                if (finalJ == ed_array.length - 1) {
                                } else {
                                    int count = 0;
                                    if (ed_array[finalJ + 1].isFocusable()) {
                                        if (finalJ == ed_array.length - 1) {
                                        } else {
                                            ed_array[finalJ + 1].requestFocus();
                                        }
                                    } else {
                                        count = finalJ + 1;
                                        while (!(ed_array[count].isFocusable())) {
                                            count++;
                                        }
                                        if (count <= ed_array.length) {
                                            ed_array[count].requestFocus();
                                        }
                                    }
                                }
                            }
                            if (n > 1) {
                                editText.setText(str.charAt(1) + "");
                                editText.setSelection(editText.getText().length());
                            }
                        } else {
                            editText.setText("");
                        }
                    }
                });
            }
            editTextLists.add(ed_array);
            linear_answer.addView(row);
        }
        layoutFibans.addView(linear_answer);
//        if the Selected Answer is not Blank
        if (!selAns.equalsIgnoreCase("blank")) {
            Log.v(TAG, "selAns - " + selAns);
            String[] fibsel_words;
            if (selAns.contains(",")) {
                fibsel_words = new String[selAns.split(",").length];
                for (int x = 0; x < selAns.split(",").length; x++) {
                    fibsel_words[x] = selAns.split(",")[x];
                }
            } else {
                fibsel_words = new String[1];
                fibsel_words[0] = selAns;
            }
            for (int i = 0; i < fibsel_words.length; i++) {
                for (int j = 0; j < fibsel_words[i].length(); j++) {
                    editTextLists.get(i)[j].setText("" + fibsel_words[i].charAt(j));
                }
            }
        }
    }

    /* Setting Data Match the Following Question Dynamic Layout  */
    private void setMFQlayout(List<OnlineQues> quesListObj, String selectedAnswer) {

        answers_list.clear();
        Log.v(TAG, "Col A " + quesListObj.get(0).getColumnA());

        int rowCount = 0;

        for (int i = 0; i < quesListObj.size(); i++) {

            View view = LayoutInflater.from(this).inflate(R.layout.layout_mfq_rows, null, false);
            WebView webViewA = view.findViewById(R.id.colA);
            WebView webViewB = view.findViewById(R.id.colB);
            if (!quesListObj.get(i).getColumnA().equalsIgnoreCase("NA") && !quesListObj.get(i).getColumnB().equalsIgnoreCase("NA")) {
                rowCount++;
                int q_num = i + 1;
                if (!quesListObj.get(i).getColumnA().equalsIgnoreCase("NA")) {
                    webViewA.loadData(utils.cleanWebString(quesListObj.get(i).getColumnA()), "text/html", "UTF-8");
                    webViewA.getSettings().setDefaultFontSize(12);
                    TextView tv_colA_index = view.findViewById(R.id.tv_colA_index);
                    tv_colA_index.setText(q_num + ") ");
                }
                if (!quesListObj.get(i).getColumnB().equalsIgnoreCase("NA")) {
                    webViewB.loadData(utils.cleanWebString(quesListObj.get(i).getColumnB()), "text/html", "UTF-8");
                    webViewB.getSettings().setDefaultFontSize(12);
                    TextView tv_colB_index = view.findViewById(R.id.tv_colB_index);
                    tv_colB_index.setText(getCharForNumber(q_num) + ") ");
                }
                llQue.addView(view);
                answers_list.add(getCharForNumber(q_num));
            }
        }

        int num_of_rows = 0;
        int q_num = 1;
        if (rowCount % 3 == 0) {
            num_of_rows = rowCount / 3;
        } else {
            num_of_rows = (rowCount / 3) + 1;
        }
        Log.v(TAG, "number of rows Count " + rowCount);
        Log.v(TAG, "number of rows " + num_of_rows);

        for (int x = 0; x < num_of_rows; x++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            for (int y = 0; y < 3; y++) {
                if (x == (num_of_rows - 1) && (rowCount - (x * 3) == y)) {
                    break;
                } else {
                    LinearLayout ll_column = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                    MultiSpinner spinner = new MultiSpinner(this);
                    TextView que_num = new TextView(this);
                    que_num.setText(q_num + ". ");
                    que_num.setTypeface(Typeface.DEFAULT_BOLD);
                    layoutParams.setMargins(0, 0, 15, 20);
                    ll_column.addView(que_num);
                    ll_column.addView(spinner, layoutParams);
                    row.addView(ll_column);

                    spinner.setList(answers_list);
                    spinner_list.add(spinner);
                    q_num++;
                }
            }
            tlAnswer.addView(row, x);
        }

        Log.v(TAG, "mfqSpiiner - " + selectedAnswer);
        Log.v(TAG, "mfqSpiiner - " + spinner_list.size());
        if (!selectedAnswer.equalsIgnoreCase("")) {
            for (int i = 0; i < spinner_list.size(); i++) {
                String[] s = selectedAnswer.split(",");
                if (s[i].length() == 2) {
                    spinner_list.get(i).setSelection("");
                } else {
                    spinner_list.get(i).setSelection(s[i].split("-")[1]);
                }
            }
        }


    }

    /* Get Character For Given Number for MFQ  */
    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 'A' - 1)) : null;
    }

    /* Checking UnAnsweredCount while Loading Question  */
    private void unanswered_count() {
        int count = 0;
//        Log.v(TAG, "allquelist size - " + testQueList.size());
        for (int i = 0; i < testQueList.size(); i++) {
            if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase(""))
                count++;
//            Log.v(TAG, "allquelist size - " + testQueList.get(i).getSelectedAnswer());

        }
        count = testQueList.size() - count;
        tvTestCount.setText("Answered : " + count + " / " + testQueList.size());
    }

    /* Selecting Single Button  */
    private void setFocus(Button btn_focus) {
        btn_focus.setBackgroundResource(R.drawable.bg_grad_ot_button);
        btn_focus.setFocusable(true);
        btn_focus.setTextColor(Color.parseColor("#FFFFFF"));
    }

    /* UnSelecting Single Button  */
    private void setUnFocus(Button btn_focus) {
        btn_focus.setBackgroundResource(R.drawable.practice_btn_optsel);
        btn_focus.setFocusable(false);
        btn_focus.setTextColor(Color.parseColor("#444444"));
    }

    /* Unselecting All Buttons */
    private void setUnFocusAll() {
        for (Button aBtn : btn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setFocusable(false);
            aBtn.setTextColor(Color.parseColor("#444444"));

        }
        for (Button aBtn : tfbtn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setFocusable(false);
            aBtn.setTextColor(Color.parseColor("#444444"));

        }
    }

    /* Showing Finish Alert when User clicks on Submit or Time Up  */
    private void finishalert() {
        int uans = 0, markedreview = 0;
        String Message = "Are you sure you want to finish this Test?";

        for (int i = 0; i < arrayOfArrays.size(); i++) {
            for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
                if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase(""))
                    uans++;
                if (arrayOfArrays.get(i).get(j).isReviewflag() && arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase(""))
                    markedreview++;
            }

        }
        Message = "Are you sure you want to finish this Test? \n\nThere are "
                + uans + " Unanswerd Questions. \n\n There are " + markedreview + " Questions as marked for Review.";

        if (timerStatus != TimerStatus.STOPPED) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(Message)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                        PrepareResult(true);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Time's UP")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        PrepareResult(true);
                        dialog.dismiss();
                    })

                    .setCancelable(false)
                    .show();
        }
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
            insertRecords = new JSONArray(new GsonBuilder().create().toJson(insertList, new TypeToken<ArrayList<ResultObj>>() {
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

//        Log.v(TAG, "InserRecordes - " + insertRecords.toString());


        if (new File(Environment.getExternalStorageDirectory() + "/.rankrPlus/onlinetest.json").exists()) {
            new File(Environment.getExternalStorageDirectory() + "/.rankrPlus/onlinetest.json").delete();
        } else {
            if (!MyUtils.isFileDataAvailableOrNot(this, getString(R.string.path_base))) {
                File folder = new File(Environment.getExternalStorageDirectory() + getString(R.string.path_base));
                folder.mkdir();
            }
        }

        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("testId", getIntent().getIntExtra("testId", 0));
                jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
                jsonObject.put("correctMarks", correctMarks);
                jsonObject.put("wrongMarks", wrongMarks);
                jsonObject.put("testCategory", testCategory);
                jsonObject.put("branchId", sObj.getMBranchId());
                jsonObject.put("admissionNumber", sObj.getAdmissionNo());
                jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
                jsonObject.put("TestCategories", insertRecords);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Writer output = null;
            File file = new File(Environment.getExternalStorageDirectory() + "/.rankrPlus/onlinetest.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject.toString());
            output.close();
        } catch (Exception e) {
            Log.v(TAG, "error - " + e.getMessage());
        }

        postFile = new File(Environment.getExternalStorageDirectory() + "/.rankrPlus/onlinetest.json");
//        uploadToS3();
        sync(testFinal,insertRecords);

//        if (testFinal) {
//            if (NetworkConnectivity.isConnected(this)) {
//                saveResultstoServer(insertRecords);
//            } else {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("insertRecords", insertRecords);
//                    jsonObject.put("testId", getIntent().getIntExtra("testId", 0));
//                    jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
//                    jsonObject.put("correctMarks", correctMarks);
//                    jsonObject.put("wrongMarks", wrongMarks);
//                    jsonObject.put("testCategory", testCategory);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Log.v(TAG, "WriteJson - " + jsonObject.toString());
//                QueJsonFile_write(jsonObject);
//                new AlertDialog.Builder(DescriptiveObjTestActivity.this)
//                        .setTitle(getString(R.string.app_name))
//                        .setMessage("Your internet seems to be down at the moment.Please connect and try again to submit the results")
//                        .setPositiveButton("TryAgain", (dialog, which) -> {
////                            super.onBackPressed();
////                                finish();
////                            PrepareResult(true);
//                        })
//                        .setNegativeButton("No", (dialog, which) -> {
//                            // do nothing
//                            dialog.dismiss();
//                        })
//                        .setCancelable(false)
//                        .show();
//            }
//        }

    }

    private void sync(boolean finish,JSONArray insertRecords) {
//        updateQueDB(insertRecords);
        Intent ii = new Intent("activity-2-initialized");
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("array", insertRecords);
            ii.putExtra("testQueList", jObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ii.putExtra("resultObj", new Gson().toJson(arrayOfArrays));
        LocalBroadcastManager.getInstance(this).sendBroadcast(ii);
        if (finish){
            finish();
        }

    }

    /* Creating Json file in Local Storage to show the result after completing test  */
    private void QueJsonFile_write(JSONObject test_que_list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (new File(getExternalFilesDir(null) + getString(R.string.path_online_testjson)).exists()) {
                new File(getExternalFilesDir(null) + getString(R.string.path_online_testjson)).delete();
            } else {
                if (!MyUtils.isFileDataAvailableOrNot(this, getString(R.string.path_base))) {
                    File folder = new File(getExternalFilesDir(null) + getString(R.string.path_base));
                    folder.mkdir();
                }
            }
            try {
                Writer writer = new FileWriter(getExternalFilesDir(null) + getString(R.string.path_online_testjson));
                Gson gson = new GsonBuilder().create();
                gson.toJson(test_que_list, writer);
                writer.close();
            } catch (Exception e) {
                utils.showLog(TAG, "error - " + e.getMessage());
            }
        } else {
            if (new File(Environment.getExternalStorageDirectory() + getString(R.string.path_online_testjson)).exists()) {
                new File(Environment.getExternalStorageDirectory() + getString(R.string.path_online_testjson)).delete();
            } else {
                if (!MyUtils.isFileDataAvailableOrNot(this, getString(R.string.path_base))) {
                    File folder = new File(Environment.getExternalStorageDirectory() + getString(R.string.path_base));
                    folder.mkdir();
                }
            }
            try {
                Writer writer = new FileWriter(Environment.getExternalStorageDirectory() + getString(R.string.path_online_testjson));
                Gson gson = new GsonBuilder().create();
                gson.toJson(test_que_list, writer);
                writer.close();
            } catch (Exception e) {
                utils.showLog(TAG, "error - " + e.getMessage());
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
            if (listSectionDetails.size() > 0) {
                for (int x = 0; x < listSectionDetails.size(); x++) {
//                    Log.v(TAG, "sectionName - " + question.getTestSectionName());
//                    Log.v(TAG, "sectionName - " + listSectionDetails.get(x).getSectionName());
                    if (question.getTestSectionName().equalsIgnoreCase(listSectionDetails.get(x).getSectionName())) {
                        values.put("correctMarks", Integer.valueOf(listSectionDetails.get(x).getCorrectAnswerMarks()));
                        values.put("wrongMarks", listSectionDetails.get(x).getWrongAnswerMarks());
                        values.put("isOptionMarks", listSectionDetails.get(x).getIsOptionMarks());
                        values.put("partialMarksAvailable", listSectionDetails.get(x).getPartialMarksAvailable());
                        values.put("partialCorrectMarks", listSectionDetails.get(x).getPartialCorrectMarks());
                        values.put("partialWrongMarks", listSectionDetails.get(x).getPartialWrongMarks());
                        values.put("marksUnattempted", listSectionDetails.get(x).getMarksUnattempted());
                    }
                }
                if (question.getQuestType().equalsIgnoreCase("CPQ")) {
                    values.put("paragraphId", question.getParagraphId());
                    values.put("questionMapId", question.getQuestionMapId());
                }

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
                values.put("testCategory", testCategory);
                values.put("updated", question.getUpdated());
                values.put("chapterName", question.getChapterName());
                values.put("topic_name", question.getTopic_name());
                values.put("chapterId", question.getChapterId());
                values.put("topicId", question.getTopicId());
                values.put("subjectId", question.getSubjectId());
                values.put("branchId", sObj.getMBranchId());
                values.put("question", question.getQuestion());
                values.put("testSectionName", question.getTestSectionName());
                values.put("examSectionId", Integer.valueOf(question.getExamSectionId()));
                if(question.getCorrectMarks() > 0)
                values.put("correctMarks", Integer.valueOf(question.getCorrectMarks()));
            } else {
                values.put("question", question.getQuestion());
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
                if(question.getCorrectMarks() > 0)
                values.put("correctMarks", Integer.valueOf(question.getCorrectMarks()));
                values.put("wrongMarks", wrongMarks);
                values.put("testCategory", testCategory);
                values.put("updated", question.getUpdated());
                values.put("chapterName", question.getChapterName());
                values.put("topic_name", question.getTopic_name());
                values.put("chapterId", question.getChapterId());
                values.put("topicId", question.getTopicId());
                values.put("subjectId", question.getSubjectId());
                values.put("branchId", sObj.getMBranchId());
                values.put("testSectionName", question.getTestSectionName());
                values.put("examSectionId", Integer.valueOf(question.getExamSectionId()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
        timeCountInMilliSeconds = testTime;
    }


    /**
     * method to start and stop count down timer
     */

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    /* Start and Stop Timers and Countdown Timer */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
        }

    }

    boolean allCharactersSame(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++)
            if (s.charAt(i) != 'X')
                return false;

        return true;
    }

    /* Get Question Paper from Service */
    void getOnlineTestQuePaper(String... strings) {
        utils.showLoader(DescriptiveObjTestActivity.this);

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

                            testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            utils.showLog(TAG, "testQueList - " + testQueList.size());

                            if (ParentjObject.has("paragraphDetails")) {
                                JSONArray jsonArr2 = ParentjObject.getJSONArray("paragraphDetails");
                                paragraphDetailsList.addAll(new Gson().fromJson(jsonArr2.toString(), new TypeToken<List<OnlineQuestionParagraphDetails>>() {
                                }.getType()));
                                utils.showLog(TAG, "paragraphDetails - " + paragraphDetailsList.size());
                            }


                            runOnUiThread(() -> {
                                if (getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("INPROGRESS")
                                        || getIntent().getStringExtra("studentTestFilePath").equalsIgnoreCase("SUBMITTED")) {
                                    getDBTestQuestions();
                                } else
                                    PrepareQuestionPaper();
                            });

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

    /* Get DB Question Answers and Merge Questions and Previous Answers based on Question Id  */
    void getDBTestQuestions() {
        ProgressDialog loading = new ProgressDialog(DescriptiveObjTestActivity.this);
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

        Log.v(TAG, "StudentDBTestQuestions Url - " + AppUrls.GETSTATUSBYID + "?examDocId=" + examdetId);

        Request request = new Request.Builder()
                .url(AppUrls.GETSTATUSBYID + "?examDocId=" + examdetId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                new AlertDialog.Builder(DescriptiveObjTestActivity.this)
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
                            JSONArray jsonArr = jsonObject.getJSONArray("testCategories");
                            testQueDetailsList.clear();
                            testQueDetailsList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

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
                                runOnUiThread(() -> PrepareQuestionPaper());

                            } else {
                                runOnUiThread(() -> PrepareQuestionPaper());
                            }
                        } else {
                            runOnUiThread(() -> PrepareQuestionPaper());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    /* Starting CountDownTimer For Test */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                total_testTime = total_testTime + 1000;
                tvTestTimer.setText(hmsTimeFormatter(millisUntilFinished));

                Log.v(TAG, "subSpinnerPosition - " + subSpinnerPosition);
                Log.v(TAG, "subSpinnerPosition - quesNo - " + quesNo);
                arrayOfArrays.get(subSpinnerPosition).get(quesNo).setTimeTaken("" + (Integer.parseInt(arrayOfArrays.get(subSpinnerPosition).get(quesNo).getTimeTaken()) + 1));

            }

            @Override
            public void onFinish() {

                tvTestTimer.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                timerStatus = TimerStatus.STOPPED;
//                createAlertFinishTimeUp(this);
                finishalert();

            }

        }.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

//    Count Down Timer end

    /* Updating Records to Database */
    void updateQueDB(JSONArray params) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            if (insertId != null && !insertId.equalsIgnoreCase("")) {
                jsonObject.put("_id", insertId);
                jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
                jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
                jsonObject.put("eStatus", eStautus);
                jsonObject.put("testCategories", params);
                jsonObject.put("studentTestFilePath", _finalUrl);


            } else {
                jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
                jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
                jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
                eStautus = "INPROGRESS";
                jsonObject.put("eStatus", eStautus);
                jsonObject.put("ePath", getIntent().getStringExtra("testFilePath"));
                jsonObject.put("eName", getIntent().getStringExtra("testName"));
                jsonObject.put("examStartTime", getIntent().getStringExtra("examSTime"));
                jsonObject.put("examEndTime", getIntent().getStringExtra("examETime"));
                jsonObject.put("rDate", getIntent().getStringExtra("examRTime"));
                jsonObject.put("testCategories", params);
                jsonObject.put("studentTestFilePath", _finalUrl);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG, "_finalUrl Json " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request;
        if (insertId != null && !insertId.equalsIgnoreCase("")) {
            request = new Request.Builder()
                    .url(AppUrls.UPDATESTATUSBYID)
                    .put(body)
                    .build();
            isINPFirst = false;
        } else {
            request = new Request.Builder()
                    .url(AppUrls.UPDATESTUDENTEXAMSTATUS)
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

    /* Uploaing Records to Amazon S3 */
    AmazonS3Client s3Client1;

    void uploadToS3() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date expiration = new Date();
                    long msec = expiration.getTime();
                    msec += 1000 * 60 * 60; // 1 hour.
                    expiration.setTime(msec);

                    key = sh_Pref.getString("schema", "")+"/studentExamFiles/" + testId + "/inprogress/" + studentId + ".json";

                    s3Client1 = new AmazonS3Client(new BasicAWSCredentials(config.getS3Details().getaKey(), config.getS3Details().getsKey()),
                            Region.getRegion(Regions.AP_SOUTH_1));
                    PutObjectRequest por = new PutObjectRequest(config.getS3Details().getBucketName(), key, postFile);//key is  URL

                    //making the object Public
                    por.setCannedAcl(CannedAccessControlList.PublicRead);
                    s3Client1.putObject(por);

                    runOnUiThread(() -> {
                        _finalUrl = s3Client1.getResourceUrl(config.getS3Details().getBucketName(), key);
                        editor.putString("studentTestFilePath", _finalUrl);
                        editor.commit();
                        utils.showLog("TAG", "_finalUrl - " + _finalUrl);
                    });

                } catch (Exception e) {
                    // writing error to Log
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startStop();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /* Finally We are saving results to server  */
    void saveResultstoServer(JSONArray insertRecords) {
        ProgressDialog loading = new ProgressDialog(DescriptiveObjTestActivity.this);
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", insertId);
            jsonObject.put("examId", getIntent().getIntExtra("testId", 0));
            jsonObject.put("studentId", getIntent().getIntExtra("studentId", 0));
            if (eStautus.equalsIgnoreCase("SUBMITTED"))
                isSUBFirst = false;
            else isSUBFirst = true;
            eStautus = "SUBMITTED";
            jsonObject.put("eStatus", eStautus);
            jsonObject.put("testCategories", insertRecords);

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
                            stopCountDownTimer();
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            int tunans = 0, tcans = 0, twans = 0;
                            for (int i = 0; i < arrayOfArrays.size(); i++) {
                                for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
                                    if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase("")) {
                                        tunans++;

                                    } else if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase(arrayOfArrays.get(i).get(j).getCorrectAnswer())) {
                                        tcans++;

                                    } else {
                                        twans++;

                                    }
                                }
                            }

                            Intent intent = new Intent(DescriptiveObjTestActivity.this, OTStudentTestOnlineCompleted.class);
                            intent.putExtra("studentId", getIntent().getIntExtra("studentId", 0));
                            intent.putExtra("testId", getIntent().getIntExtra("testId", 0));
                            intent.putExtra("jeeSectionTemplate", getIntent().getStringExtra("jeeSectionTemplate"));
                            intent.putExtra("testFilePath", getIntent().getStringExtra("testFilePath"));
                            intent.putExtra("studentTestFilePath", _finalUrl);
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

    /* Checking TimeEvaluated for Test and  updating the testQueList Object */
    private void PrepareQuestionPaper() {
        if (testQueList.size() > 0) {

//            Log.v(TAG, "QuesList - " + testQueList.size());
            int allQueTime = 0;

            for (int i = 0; i < testQueList.size(); i++) {
                if (testQueList.get(i).getQuestionDisplayOrder() == 0)
                    testQueList.get(i).setQuestion_number((i + 1));
                else
                    testQueList.get(i).setQuestion_number(testQueList.get(i).getQuestionDisplayOrder());

                allQueTime = allQueTime + Integer.parseInt("" + testQueList.get(i).getTimeTaken());

                Log.v(TAG, "allQueTime - " + allQueTime);


                UpdateObj updateObj = new UpdateObj();
                updateObj.setQuestionId(testQueList.get(i).getQuestionId());
                if (testQueList.get(i).getStyle().equalsIgnoreCase("not_visited_div")) {
                    updateObj.setUpdated(false);
                    testQueList.get(i).setUpdated(false);
                } else {
                    updateObj.setUpdated(true);
                    testQueList.get(i).setUpdated(true);
                }

                updateObjList.add(updateObj);
            }

            Log.v(TAG, "updateObjList - size - " + updateObjList.size());

            for (int i = 0; i < updateObjList.size(); i++) {
                Log.v(TAG, "updateObjList - queId - " + updateObjList.get(i).getQuestionId());
                Log.v(TAG, "updateObjList - queId status- " + updateObjList.get(i).getUpdated());
            }

            if (allQueTime > 0) {
                Log.v(TAG, "allQueTime testtime- " + (testTime));
                Log.v(TAG, "allQueTime testtime- " + (testTime) / 60);
                if (testTime > allQueTime)
                    testTime = (testTime - allQueTime);
                Log.v(TAG, "allQueTime testtime- " + testTime);
                Log.v(TAG, "allQueTime testtime- " + (testTime / 60));
            }
            int totaleDuration = getIntent().getIntExtra("eDuration", 0);
            totaleDuration = (totaleDuration * 60 * 1000) - (allQueTime * 1000);

            if (testTime > totaleDuration) {
                testTime = totaleDuration;
            }

            startTest();
        } else {
            showNoDataDialogue();
        }
    }


    private void showNoDataDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Test Already Started/Submitted")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }


    /* Question List Adapter */
    public class OnlineTestQueListAdapter extends RecyclerView.Adapter<OnlineTestQueListAdapter.ViewHolder> {
        private List<OnlineQuestionObj2> queslist;
        private Context _context;


        public OnlineTestQueListAdapter(Context _context, List<OnlineQuestionObj2> queslist) {
            this.queslist = queslist;
            this._context = _context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test_anslist_cardview, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number());
            if (!allCharactersSame(queslist.get(i).getSelectedAnswer())) {
                viewHolder.sno_tv.setText("Q " + (i + 1) + " : " + queslist.get(i).getSelectedAnswer()
                        .replace("option1", "A")
                        .replace("option2", "B")
                        .replace("option3", "C")
                        .replace("option4", "D"));
            }
            Log.v("sriee", "i - " + i);
            Log.v("sriee", queslist.get(i).getSelectedAnswer());
            if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("")) {
                viewHolder.gans_tv.setText("Not Visited");
                viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.notvisited, null));
                if (queslist.get(i).isVisitedflag()) {
                    viewHolder.gans_tv.setText("Unanswered");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered, null));
                    if (queslist.get(i).isReviewflag()) {
                        viewHolder.gans_tv.setText("Marked for Review");
                        viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview, null));
                    }
                }
            } else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase(",")) {
                viewHolder.gans_tv.setText("Not Visited");
                viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.notvisited));
                if (queslist.get(i).isVisitedflag()) {
                    viewHolder.gans_tv.setText("Unanswered");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered));
                    if (queslist.get(i).isReviewflag()) {
                        viewHolder.gans_tv.setText("Marked for Review");
                        viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview));
                    }
                }
            } else if (allCharactersSame(queslist.get(i).getSelectedAnswer())) {
                viewHolder.gans_tv.setText("Not Visited");
                viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.notvisited));
                if (queslist.get(i).isVisitedflag()) {
                    viewHolder.gans_tv.setText("Unanswered");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered));
                    if (queslist.get(i).isReviewflag()) {
                        viewHolder.gans_tv.setText("Marked for Review");
                        viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview));
                    }
                }
            } else {
                if (queslist.get(i).isReviewflag()) {
                    viewHolder.gans_tv.setText("Answered & Marked for Review");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.ansmark));
                } else {
                    viewHolder.gans_tv.setText("Answered");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.answered));
                }
            }
            if (!queslist.get(i).getStyle().equalsIgnoreCase("not_visited_div")) {
                Log.v("stylesriran", "" + i + " -  " + queslist.get(i).getStyle());
                if (queslist.get(i).getStyle().equalsIgnoreCase("markedrview_div")) {
                    viewHolder.gans_tv.setText("Marked for Review");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview));
                } else if (queslist.get(i).getStyle().equalsIgnoreCase("not_answered_div")) {
                    viewHolder.gans_tv.setText("Unanswered");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered));
                } else if (queslist.get(i).getStyle().equalsIgnoreCase("answered_div")) {
                    viewHolder.gans_tv.setText("Answered");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.answered));
                } else if (queslist.get(i).getStyle().equalsIgnoreCase("answered_marked_div")) {
                    viewHolder.gans_tv.setText("Answered & Marked for Review");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.ansmark));
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return queslist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView sno_tv, gans_tv;

            public ViewHolder(View view) {
                super(view);

                sno_tv = view.findViewById(R.id.sno_tv);
                gans_tv = view.findViewById(R.id.gans_tv);

            }
        }

    }

    /* Result Object Model Class */
    public class ResultObj implements Serializable {

        @SerializedName("question_number")
        @Expose
        private int question_number;
        @SerializedName("marks")
        @Expose
        private Integer marks;
        @SerializedName("questionDisplayOrder")
        @Expose
        private Integer questionDisplayOrder = -1;
        @SerializedName("questionId")
        @Expose
        private Integer questionId;
        @SerializedName("questionMapId")
        @Expose
        private String questionMapId = "";
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("selectedAnswer")
        @Expose
        private String selectedAnswer = "";
        @SerializedName("subjectGroup")
        @Expose
        private String subjectGroup;
        @SerializedName("questType")
        @Expose
        private String questType = "MCQ";
        @SerializedName("subjectId")
        @Expose
        private String subjectId;
        @SerializedName("chapterId")
        @Expose
        private String chapterId;
        @SerializedName("topicId")
        @Expose
        private String topicId;
        @SerializedName("testSectionName")
        @Expose
        private String testSectionName = "NA";
        @SerializedName("timeTaken")
        @Expose
        private String timeTaken = "0";
        @SerializedName("style")
        @Expose
        private String style = "not_visited_div";
        @SerializedName("correctAnswer")
        @Expose
        private String correctAnswer;
        @SerializedName("subjectName")
        @Expose
        private String subjectName;
        @SerializedName("chapterName")
        @Expose
        private String chapterName;
        @SerializedName("topic_name")
        @Expose
        private String topic_name;
        @SerializedName("questions")
        @Expose
        private List<OnlineQues> questions = null;
        @SerializedName("updated")
        @Expose
        private Boolean updated;
        @SerializedName("correctMarks")
        @Expose
        private int correctMarks = 0;
        @SerializedName("wrongMarks")
        @Expose
        private String wrongMarks = "";
        @SerializedName("paragraphId")
        @Expose
        private String paragraphId = "";
        @SerializedName("partialMarksAvailable")
        @Expose
        private String partialMarksAvailable = "";
        @SerializedName("partialCorrectMarks")
        @Expose
        private String partialCorrectMarks = "";
        @SerializedName("partialWrongMarks")
        @Expose
        private String partialWrongMarks = "";
        @SerializedName("marksUnattempted")
        @Expose
        private String marksUnattempted = "";
        @SerializedName("isOptionMarks")
        @Expose
        private String isOptionMarks = "";
        @SerializedName("isGrace")
        @Expose
        private int isGrace = 0;
        @Expose
        @SerializedName("explanation")
        private String Explanation = "";
        @SerializedName("reviewflag")
        @Expose
        private boolean reviewflag = false;
        @SerializedName("visitedflag")
        @Expose
        private boolean visitedflag = false;
        @SerializedName("proceed")
        @Expose
        private boolean proceed = false;
        @SerializedName("chapterCCMapId")
        @Expose
        private String chapterCCMapId;

        public String getChapterCCMapId() {
            return chapterCCMapId;
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

        @SerializedName("questionCCMapId")
        @Expose
        private String questionCCMapId;
        public String getQuestionCCMapId() {
            return questionCCMapId;
        }

        public void setQuestionCCMapId(String questionCCMapId) {
            this.questionCCMapId = questionCCMapId;
        }

        public void setChapterCCMapId(String chapterCCMapId) {
            this.chapterCCMapId = chapterCCMapId;
        }
        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }

        @SerializedName("branchId")
        @Expose
        private String branchId;

        public Integer getMarks() {
            return marks;
        }

        public void setMarks(Integer marks) {
            this.marks = marks;
        }

        public Integer getQuestionDisplayOrder() {
            return questionDisplayOrder;
        }

        public void setQuestionDisplayOrder(Integer questionDisplayOrder) {
            this.questionDisplayOrder = questionDisplayOrder;
        }

        public String getQuestionMapId() {
            return questionMapId;
        }

        public void setQuestionMapId(String questionMapId) {
            this.questionMapId = questionMapId;
        }

        public String getParagraphId() {
            return paragraphId;
        }

        public void setParagraphId(String paragraphId) {
            this.paragraphId = paragraphId;
        }

        public String getPartialMarksAvailable() {
            return partialMarksAvailable;
        }

        public void setPartialMarksAvailable(String partialMarksAvailable) {
            this.partialMarksAvailable = partialMarksAvailable;
        }

        public String getPartialCorrectMarks() {
            return partialCorrectMarks;
        }

        public void setPartialCorrectMarks(String partialCorrectMarks) {
            this.partialCorrectMarks = partialCorrectMarks;
        }

        public String getPartialWrongMarks() {
            return partialWrongMarks;
        }

        public void setPartialWrongMarks(String partialWrongMarks) {
            this.partialWrongMarks = partialWrongMarks;
        }

        public String getMarksUnattempted() {
            return marksUnattempted;
        }

        public void setMarksUnattempted(String marksUnattempted) {
            this.marksUnattempted = marksUnattempted;
        }

        public String getIsOptionMarks() {
            return isOptionMarks;
        }

        public void setIsOptionMarks(String isOptionMarks) {
            this.isOptionMarks = isOptionMarks;
        }

        public int getIsGrace() {
            return isGrace;
        }

        public void setIsGrace(int isGrace) {
            this.isGrace = isGrace;
        }

        public int getCorrectMarks() {
            return correctMarks;
        }

        public void setCorrectMarks(int correctMarks) {
            this.correctMarks = correctMarks;
        }

        public String getWrongMarks() {
            return wrongMarks;
        }

        public void setWrongMarks(String wrongMarks) {
            this.wrongMarks = wrongMarks;
        }

        public Boolean getUpdated() {
            return updated;
        }

        public void setUpdated(Boolean updated) {
            this.updated = updated;
        }

        public int getQuestion_number() {
            return question_number;
        }

        public void setQuestion_number(int question_number) {
            this.question_number = question_number;
        }

        public boolean isReviewflag() {
            return reviewflag;
        }

        public void setReviewflag(boolean reviewflag) {
            this.reviewflag = reviewflag;
        }

        public boolean isVisitedflag() {
            return visitedflag;
        }

        public void setVisitedflag(boolean visitedflag) {
            this.visitedflag = visitedflag;
        }

        public boolean isProceed() {
            return proceed;
        }

        public void setProceed(boolean proceed) {
            this.proceed = proceed;
        }

        public String getExplanation() {
            return Explanation;
        }

        public void setExplanation(String Explanation) {
            this.Explanation = Explanation;
        }

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getSelectedAnswer() {
            return selectedAnswer;
        }

        public void setSelectedAnswer(String selectedAnswer) {
            this.selectedAnswer = selectedAnswer;
        }

        public String getSubjectGroup() {
            return subjectGroup;
        }

        public void setSubjectGroup(String subjectGroup) {
            this.subjectGroup = subjectGroup;
        }

        public String getQuestType() {
            return questType;
        }

        public void setQuestType(String questType) {
            this.questType = questType;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getChapterId() {
            return chapterId;
        }

        public void setChapterId(String chapterId) {
            this.chapterId = chapterId;
        }

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getTestSectionName() {
            return testSectionName;
        }

        public void setTestSectionName(String testSectionName) {
            this.testSectionName = testSectionName;
        }

        public String getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(String timeTaken) {
            this.timeTaken = timeTaken;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getTopic_name() {
            return topic_name;
        }

        public void setTopic_name(String topic_name) {
            this.topic_name = topic_name;
        }

        public List<OnlineQues> getQuestions() {
            return questions;
        }

        public void setQuestions(List<OnlineQues> questions) {
            this.questions = questions;
        }

    }

}


