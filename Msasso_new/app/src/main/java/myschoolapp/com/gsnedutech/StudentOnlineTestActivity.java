package myschoolapp.com.gsnedutech;/*
 * *
 *  * Created by SriRamaMurthy A on 3/10/19 7:01 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/10/19 7:01 PM
 *
 */


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.JEETestSectionDetail;
import myschoolapp.com.gsnedutech.Models.OnlineQues;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.UpdateObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyCustomProgressBar;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentOnlineTestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = StudentOnlineTestActivity.class.getName();
    MyUtils utils = new MyUtils();

    StudentOnlineTestObj liveExam;

    @BindView(R.id.test_title)
    TextView testTitle;
    @BindView(R.id.progressBarCircle)
    MyCustomProgressBar progressBarCircle;
    @BindView(R.id.tv_test_timer)
    TextView tvTestTimer;
    @BindView(R.id.spinner_sub)
    Spinner spinner;
    @BindView(R.id.tv_test_count)
    TextView tvTestCount;
    @BindView(R.id.btn_test_finish)
    Button btnTestFinish;
    @BindView(R.id.que_no)
    TextView tvQueNo;
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
    @BindView(R.id.ll_option)
    LinearLayout llOption;
    @BindView(R.id.ll_que)
    LinearLayout llQue;
    @BindView(R.id.btn_clear)
    Button btnClear;
    @BindView(R.id.btn_review_next)
    Button btnReviewNext;
    @BindView(R.id.btn_save_next)
    Button btnSaveNext;
    @BindView(R.id.rv_que_no_list)
    RecyclerView rvQueNoList;

    int quesNo = 0, testTime = 0, total_testTime = 0;


    private Button[] tfbtn = new Button[2];
    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    List<Spinner> spinner_list = new ArrayList<>();
    List<String> answers_list = new ArrayList<>();

    List<EditText[]> editTextLists = new ArrayList<>();


    private long startTime = 0L;
    private Handler customHandler = new Handler();

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;
    private long timeCountInMilliSeconds = 1 * 60000;

    List<String> subList;
    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionObj2> testQueDetailsList = new ArrayList<>();
    List<UpdateObj> updateObjList = new ArrayList<>();
    ArrayList<ArrayList<OnlineQuestionObj2>> arrayOfArrays = new ArrayList<>();

    private Button[] btn = new Button[4];
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    String testId, studentId, correctMarks, wrongMarks, testCategory;

    List<JEETestSectionDetail> listSectionDetails = new ArrayList<>();
    String selectedAnswer = "";

    File postFile;
    String key, _finalUrl;
    AmazonS3Client s3Client1;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    int currentSelectedItem = 0;

    Timer timer;

    Boolean S3Uplaod = true;
    String accessKey, secretkey,bucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_student_online_test);
        ButterKnife.bind(this);

        init();

        Log.v(TAG, "testId - " +liveExam.getTestId());
        Log.v(TAG, "testId - " + liveExam.getTestFilePath());
        Log.v(TAG, "testId - " + liveExam.getStudentTestFilePath());

        if (!liveExam.getStudentTestFilePath().equalsIgnoreCase("NA")) {
            toEdit.putString("studentTestFilePath", liveExam.getStudentTestFilePath());
            toEdit.commit();
        }

        if (NetworkConnectivity.isConnected(this)) {
            new GetOnlineTestQuestions().execute(testId, studentId);
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }


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
                    saveTimetaken();
                    Log.v(TAG, "sriramTime before - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setTimeTaken("" + TimeUnit.MILLISECONDS.toSeconds(timeSwapBuff));
                    Log.v(TAG, "sriramTime after - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());

                    Log.v(TAG, "que time new " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());

                    int position = recyclerView.getChildAdapterPosition(child);
                    quesNo = position;
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setVisitedflag(true);
                    if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                        arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                    }
                    loadQuestion(arrayOfArrays.get(spinner.getSelectedItemPosition()), quesNo);
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

        rvQueNoList.setLayoutManager(new LinearLayoutManager(StudentOnlineTestActivity.this,RecyclerView.HORIZONTAL,false));


    }

    @Override
    public void onBackPressed() {

        if (isPanelShown())
            slideUpDown();
        else {
            Toast.makeText(this, "Please Submit the Test to Exit.", Toast.LENGTH_SHORT).show();
//            super.onBackPressed();
        }

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {


        liveExam = (StudentOnlineTestObj) getIntent().getSerializableExtra("live");

        if (!liveExam.getMjeeSectionTemplateName().equalsIgnoreCase("NA")) {
            if (getIntent().getExtras().containsKey("sections"))
                listSectionDetails.addAll((List<JEETestSectionDetail>) getIntent().getSerializableExtra("sections"));
        }
//        testTitle.setText(getIntent().getStringExtra("testName"));
        testTitle.setText(liveExam.getTestName());
//        testTime = (Integer.parseInt(getIntent().getStringExtra("testTime")) * 60);
        testTime = (Integer.parseInt(liveExam.getTestDuration()) * 60);

//        testId = getIntent().getStringExtra("testId");
        testId = liveExam.getTestId();
//        studentId = getIntent().getStringExtra("studentId");
//        correctMarks = getIntent().getStringExtra("correctMarks");
        correctMarks = liveExam.getCorrectAnswerMarks();
//        wrongMarks = getIntent().getStringExtra("wrongMarks");
        wrongMarks = liveExam.getWrongAnswerMarks();
//        testCategory = getIntent().getStringExtra("testCategory");
        testCategory = liveExam.getTestCategory();


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

        progressBarCircle.setProgressBarWidth(getResources().getDimension(R.dimen.progressBarWidth));
        progressBarCircle.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.backgroundProgressBarWidth));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvQuelist.setLayoutManager(mLayoutManager);
        rvQuelist.setItemAnimator(new DefaultItemAnimator());

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        studentId = sObj.getStudentId();


        accessKey = sh_Pref.getString("akey","");
        secretkey = sh_Pref.getString("skey","");
        bucket = sh_Pref.getString("bucket","");

    }

    private void startTest() {
        //        Initializing Spinner

        Set<String> hs = new HashSet<>();
        for (int i = 0; i < testQueList.size(); i++) {
            hs.add(testQueList.get(i).getSubjectGroup());
            Log.v(TAG, "quesid - " + testQueList.get(i).getQuestionId() + "time - " + testQueList.get(i).getQuestion());
            Log.v(TAG, "quesid - " + testQueList.get(i).getQuestionId() + "update - " + testQueList.get(i).getUpdated());
        }

        if (hs.size() < 2) {
            spinner.setVisibility(View.INVISIBLE);
        }
        subList = new ArrayList<String>(hs);
        Collections.sort(subList);

        int num = 1;
        for (int i = 0; i < subList.size(); i++) {
            ArrayList<OnlineQuestionObj2> sub_quelist = new ArrayList<>();
            for (int j = 0; j < testQueList.size(); j++) {
                if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                    testQueList.get(j).setQuestion_number(num++);
                    sub_quelist.add(testQueList.get(j));
                }
            }
            arrayOfArrays.add(sub_quelist);
        }
        Log.v(TAG, "arrayoff arrays size - " + arrayOfArrays.size());

        timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
                // your code here...
//                Log.v(TAG,"Log from the Timer ");
                PrepareResult(false);
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule(hourlyTask, 0l, 1000 * 1 * 60);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_test_item, subList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);


        startStop();
    }

    @Override
    public void onClick(View v) {
        setUnFocusAll();

        switch (v.getId()) {
            case R.id.btn_a:
                setFocus(btn[0]);
                selectedAnswer = "option1";
                Log.v(TAG, "SelectedAnswer - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer());
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
                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");
                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setReviewflag(false);
                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                setUnFocusAll();

                etItq.setText("");

                if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {

                    for (int i = 0; i < editTextLists.size(); i++) {
                        for (int j = 0; j < editTextLists.get(i).length; j++) {
                            editTextLists.get(i)[j].getText().clear();
                        }
                    }
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(etItq.getText().toString());
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {
                    for (int i = 0; i < spinner_list.size(); i++) {
                        spinner_list.get(i).setSelection(0);
                    }
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MCQ")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");
                    selectedAnswer = "";
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer("");
                    selectedAnswer = "";
                }

                break;
            case R.id.btn_review_next:
                if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {
                    String answers = "";
                    for (int i = 0; i < spinner_list.size(); i++) {
                        if (!(spinner_list.get(i).getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                            answers = answers + spinner_list.get(i).getSelectedItem().toString();
                        } else {
                            answers = answers + "X";
                        }
                    }
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {

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
                        arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(etItq.getText().toString());
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MCQ")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                }


                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setReviewflag(true);
                if (!arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase("")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("answered_marked_div");
                } else {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("markedrview_div");
                }
                saveTimetaken();
                Log.v(TAG, "sriramTime before - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());
                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setTimeTaken("" + TimeUnit.MILLISECONDS.toSeconds(timeSwapBuff));
                Log.v(TAG, "sriramTime after - " + TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken())));

                if (++quesNo < arrayOfArrays.get(spinner.getSelectedItemPosition()).size()) {
                    if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                        arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                    }
                    loadQuestion(arrayOfArrays.get(spinner.getSelectedItemPosition()), quesNo);
                } else {
                    quesNo = quesNo - 1;
                    if (spinner.getSelectedItemPosition() < subList.size() - 1) {
                        spinner.setSelection(spinner.getSelectedItemPosition() + 1);
                    } else {
                        finishalert();
                    }
                }
                break;
            case R.id.btn_save_next:
                Log.v(TAG, "type - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType());
                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {
                    String answers = "";
                    for (int i = 0; i < spinner_list.size(); i++) {
                        if (!(spinner_list.get(i).getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                            answers = answers + spinner_list.get(i).getSelectedItem().toString();
                        } else {
                            answers = answers + "X";
                        }
                    }
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {
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
                        arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(answers);
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(etItq.getText().toString());
                    etItq.setText("");
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("MCQ")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                } else if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setSelectedAnswer(selectedAnswer);
                    selectedAnswer = "";
                }
                if (!arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase("") && !(allCharactersSame(arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer())) && !arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getSelectedAnswer().equalsIgnoreCase(",")) {
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setReviewflag(false);
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("answered_div");
                    saveTimetaken();
                    Log.v(TAG, "sriramTime before - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());
                    arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setTimeTaken("" + TimeUnit.MILLISECONDS.toSeconds(timeSwapBuff));
                    Log.v(TAG, "sriramTime after - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());
                    if (++quesNo < arrayOfArrays.get(spinner.getSelectedItemPosition()).size()) {
                        if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                            arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
                        }
                        loadQuestion(arrayOfArrays.get(spinner.getSelectedItemPosition()), quesNo);
                    } else {
                        quesNo = quesNo - 1;
                        if (spinner.getSelectedItemPosition() < subList.size() - 1)
                            spinner.setSelection(spinner.getSelectedItemPosition() + 1);
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

    private void skipQuestion() {
        saveTimetaken();

        Log.v(TAG, "sriramTime before - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());
        arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setTimeTaken("" + TimeUnit.MILLISECONDS.toSeconds(timeSwapBuff));
        Log.v(TAG, "sriramTime after - " + arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getTimeTaken());

        if (++quesNo < arrayOfArrays.get(spinner.getSelectedItemPosition()).size()) {
            if (arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
                arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setStyle("not_answered_div");
            }
            loadQuestion(arrayOfArrays.get(spinner.getSelectedItemPosition()), quesNo);
        } else {
            quesNo = quesNo - 1;
            Log.v(TAG, "position - " + spinner.getSelectedItemPosition());
            if (spinner.getSelectedItemPosition() < subList.size() - 1)
                spinner.setSelection(spinner.getSelectedItemPosition() + 1);
            else
                finishalert();
        }
    }

    private void slideUpDown() {

        if (!isPanelShown()) {

            // Show the panel
            quelist.setText(spinner.getSelectedItem().toString());
            RecyclerView.Adapter adapter = new OnlineTestQueListAdapter(this, arrayOfArrays.get(spinner.getSelectedItemPosition()));
            rvQuelist.setAdapter(adapter);

            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            btnTestFinish.setEnabled(false);
            btnClear.setEnabled(false);
            btnSaveNext.setEnabled(false);
            btnReviewNext.setEnabled(false);

        } else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
            btnTestFinish.setEnabled(true);
            btnClear.setEnabled(true);
            btnSaveNext.setEnabled(true);
            btnReviewNext.setEnabled(true);
        }
    }

    private boolean isPanelShown() {

        return hiddenPanel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        quesNo = 0;
        if (arrayOfArrays.get(position).get(quesNo).getStyle().equalsIgnoreCase("not_visited_div")) {
            arrayOfArrays.get(position).get(quesNo).setStyle("not_answered_div");
        }
        rvQueNoList.setAdapter(new QueNoAdapter(arrayOfArrays.get(position),quesNo));

        loadQuestion(arrayOfArrays.get(position), quesNo);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadQuestion(ArrayList<OnlineQuestionObj2> questions, int quesNo) {

        rvQueNoList.getAdapter().notifyDataSetChanged();
        rvQueNoList.getLayoutManager().scrollToPosition(quesNo);

        tvQueNo.setText("Q no: " + questions.get(quesNo).getQuestion_number());
        selectedAnswer = questions.get(quesNo).getSelectedAnswer();
        unanswered_count();
        setUnFocusAll();

        Log.v(TAG, "que time Old " + questions.get(quesNo).getTimeTaken());

        timeSwapBuff = (Integer.parseInt(questions.get(quesNo).getTimeTaken()) * 1000);

        Log.v(TAG, "que time Old  " + questions.get(quesNo).getTimeTaken());

        Log.v("TIME - ", "timeSwapBuff - " + timeSwapBuff);
        startCountUpTimer();

        questions.get(quesNo).setVisitedflag(true);

        if (questions.get(quesNo).getQuestType().equalsIgnoreCase("MCQ")) {

            llMcq.setVisibility(View.VISIBLE);
            wvTest.setVisibility(View.VISIBLE);
            llFib.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            setUnFocusAll();
            Log.v(TAG, "SelectedAnswer - " + questions.get(quesNo).getSelectedAnswer());
            if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option1")) {
                setFocus(btn[0]);
            } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option2")) {
                setFocus(btn[1]);
            } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option3")) {
                setFocus(btn[2]);
            } else if (questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase("option4")) {
                setFocus(btn[3]);
            }

            StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() +
                    "" + questions.get(quesNo).getOption1() +
                    "" + questions.get(quesNo).getOption2() +
                    "" + questions.get(quesNo).getOption3() +
                    "" + questions.get(quesNo).getOption4() +
                    "</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            wvTest.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
            wvTest.scrollTo(0, 0);
        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("FIB")) {
            Log.v(TAG, "FIB Que - " + questions.get(quesNo).getQuestion());
            Log.v(TAG, "\n FIB Que - " + questions.get(quesNo).getCorrectAnswer());
            llFib.setVisibility(View.VISIBLE);
            layoutFibans.removeAllViews();
            editTextLists.clear();

            wvTest.setVisibility(View.VISIBLE);
            llMcq.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() + "</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            wvTest.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
            wvTest.scrollTo(0, 0);

            setFIBLayout(questions.get(quesNo).getCorrectAnswer(), questions.get(quesNo).getSelectedAnswer());

        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("ITQ")) {
            wvTest.setVisibility(View.VISIBLE);
            llITQ.setVisibility(View.VISIBLE);
            llTf.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            llOption.removeAllViews();

            if (!questions.get(quesNo).getSelectedAnswer().equalsIgnoreCase(""))
                etItq.setText("" + questions.get(quesNo).getSelectedAnswer());
            else
                etItq.setText("");


            StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() + "</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            wvTest.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");

            wvTest.scrollTo(0, 0);


        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("TOF")) {
            llTf.setVisibility(View.VISIBLE);
            wvTest.setVisibility(View.VISIBLE);
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

            StringBuilder mainString =new StringBuilder("<!DOCTYPE html> <html>" + questions.get(quesNo).getQuestion() + "</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            wvTest.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
            wvTest.scrollTo(0, 0);

        } else if (questions.get(quesNo).getQuestType().equalsIgnoreCase("MFQ")) {
            llMfq.setVisibility(View.VISIBLE);
            tlAnswer.setVisibility(View.VISIBLE);

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
        if (!selAns.equalsIgnoreCase("")) {
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

    private void setMFQlayout(List<OnlineQues> quesListObj, String selectedAnswer) {

        answers_list.clear();
        Log.v(TAG, "Col A " + quesListObj.get(0).getColumnA());

        answers_list.add("Select");
        for (int i = 0; i < quesListObj.size(); i++) {

            View view = LayoutInflater.from(this).inflate(R.layout.layout_mfq_rows, null, false);
            WebView webViewA = view.findViewById(R.id.colA);
            WebView webViewB = view.findViewById(R.id.colB);
            webViewA.loadData(utils.cleanWebString(quesListObj.get(i).getColumnA()), "text/html", "UTF-8");
            webViewB.loadData(utils.cleanWebString(quesListObj.get(i).getColumnB()), "text/html", "UTF-8");
            webViewA.getSettings().setDefaultFontSize(12);
            webViewB.getSettings().setDefaultFontSize(12);
            int q_num = i + 1;
            TextView tv_colA_index = view.findViewById(R.id.tv_colA_index);
            tv_colA_index.setText(q_num + ") ");
            TextView tv_colB_index = view.findViewById(R.id.tv_colB_index);
            tv_colB_index.setText(getCharForNumber(q_num) + ") ");
            llQue.addView(view);
            answers_list.add(getCharForNumber(q_num));
        }

        int num_of_rows = 0;
        int q_num = 1;

        if (quesListObj.size() % 3 == 0) {
            num_of_rows = quesListObj.size() / 3;
        } else {
            num_of_rows = (quesListObj.size() / 3) + 1;
        }
        Log.v(TAG, "number of rows " + num_of_rows);

        for (int x = 0; x < num_of_rows; x++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            for (int y = 0; y < 3; y++) {
                if (x == (num_of_rows - 1) && (quesListObj.size() - (x * 3) == y)) {
                    break;
                } else {
                    LinearLayout ll_column = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                    Spinner spinner = new Spinner(this);
                    TextView que_num = new TextView(this);
                    que_num.setText(q_num + ". ");
                    que_num.setTypeface(Typeface.DEFAULT_BOLD);
                    layoutParams.setMargins(0, 0, 15, 20);
                    ll_column.addView(que_num);
                    ll_column.addView(spinner, layoutParams);
                    row.addView(ll_column);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, answers_list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner_list.add(spinner);
                    q_num++;
                }
            }
            tlAnswer.addView(row, x);
        }

        Log.v(TAG, "mfqSpiiner - " + selectedAnswer);
        Log.v(TAG, "mfqSpiiner - " + spinner_list.size());
        if (!selectedAnswer.equalsIgnoreCase("")) {

            if (selectedAnswer.length() > spinner_list.size()) {
                String ans = "";
                String[] ops = selectedAnswer.split(",");
                for (int i = 0; i < ops.length; i++) {
                    if (ops[i].length() < 3) {
                        ans = ans + "X";
                    } else {
                        ans = ans + ops[i].charAt(2);
                    }
                    selectedAnswer = ans;
                }
            }


            for (int i = 0; i < spinner_list.size(); i++) {
                if (selectedAnswer.charAt(i) != 'X') {
                    spinner_list.get(i).setSelection(((ArrayAdapter<String>) spinner_list.get(i).getAdapter()).getPosition(String.valueOf(selectedAnswer.charAt(i))));
                }
            }
        }


    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 'A' - 1)) : null;
    }

    private void unanswered_count() {
        int count = 0;
        Log.v(TAG, "allquelist size - " + testQueList.size());
        for (int i = 0; i < testQueList.size(); i++) {
            if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase(""))
                count++;
            Log.v(TAG, "allquelist size - " + testQueList.get(i).getSelectedAnswer());

        }
        count = testQueList.size() - count;
        tvTestCount.setText("" + count + " / " + testQueList.size());
    }

    private void setFocus(Button btn_focus) {
        btn_focus.setBackgroundResource(R.drawable.practice_btn_opt);
        btn_focus.setFocusable(true);
        btn_focus.setTextColor(Color.parseColor("#FFFFFF"));
    }

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

    private void finishalert() {
        int uans = 0, markedreview = 0;
        String Message = "Are you sure you want to finish this Test?";

        for (int i = 0; i < arrayOfArrays.size(); i++) {
            for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
//                if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase(""))
                if (arrayOfArrays.get(i).get(j).getStyle().equalsIgnoreCase("not_visited_div")
                        || arrayOfArrays.get(i).get(j).getStyle().equalsIgnoreCase("not_answered_div"))
                    uans++;

                if (arrayOfArrays.get(i).get(j).getStyle().equalsIgnoreCase("answered_marked_div")
                        || arrayOfArrays.get(i).get(j).getStyle().equalsIgnoreCase("markedrview_div"))
                    markedreview++;
            }

        }
        Message = "Are you sure you want to finish this Test? \n\nThere are "
                + uans + " Unanswerd Questions. \n\nThere are " + markedreview + " Questions as marked for Review.";

        if (timerStatus != TimerStatus.STOPPED) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(Message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            final Dialog dialog2 = new Dialog(StudentOnlineTestActivity.this);
                            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog2.setCancelable(true);
                            dialog2.setContentView(R.layout.dialog_test_finish);
                            Window window = dialog2.getWindow();
                            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            RecyclerView rvSubs = dialog2.findViewById(R.id.rv_subs);
                            rvSubs.setLayoutManager(new LinearLayoutManager(StudentOnlineTestActivity.this));
                            rvSubs.setAdapter(new DialogAdapter(subList));

                            dialog2.show();

                            dialog2.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PrepareResult(true);
                                    dialog2.dismiss();
                                }
                            });
                            dialog2.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog2.dismiss();
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Time's UP")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PrepareResult(true);

                            dialog.dismiss();


                        }
                    })

                    .setCancelable(false)
                    .show();
        }
    }


    class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {
        List<String> subjectList;

        public DialogAdapter(List<String> subjectList) {
            this.subjectList = subjectList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(StudentOnlineTestActivity.this).inflate(R.layout.dialog_finish_sub_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvSubName.setText(subjectList.get(position));
            int ansCount = 0, totatlCount = 0;
            for (int x = 0; x < arrayOfArrays.size(); x++) {
                for (int y = 0; y < arrayOfArrays.get(x).size(); y++) {
                    if (arrayOfArrays.get(x).get(y).getSubjectGroup().equalsIgnoreCase(subjectList.get(position))) {
                        totatlCount++;
                        if (!arrayOfArrays.get(x).get(y).getSelectedAnswer().equalsIgnoreCase("") && !allCharactersSame(arrayOfArrays.get(x).get(y).getSelectedAnswer()) && !arrayOfArrays.get(x).get(y).getSelectedAnswer().equalsIgnoreCase("")) {
                            ansCount++;
                        }
                    }
                }
            }

            holder.tvCount.setText(ansCount + "/" + totatlCount);

        }

        @Override
        public int getItemCount() {
            return subjectList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName, tvCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvCount = itemView.findViewById(R.id.tv_ans_count);

            }
        }
    }


    private void PrepareResult(Boolean testFinal) {

        JSONArray insertRecords = new JSONArray();
        JSONArray finalinsertRecords = new JSONArray();

        for (int i = 0; i < arrayOfArrays.size(); i++) {
            for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
//                Check Here
//                arrayOfArrays.get(i).get(j).getTimeTaken()
                JSONObject values = prepareJsonObj(arrayOfArrays.get(i).get(j));
                insertRecords.put(values);

            }
        }
        Log.v(TAG, "InserRecordes` - " + insertRecords.length());


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
                jsonObject.put("testId", testId);
                jsonObject.put("studentId", studentId);
                jsonObject.put("correctMarks", correctMarks);
                jsonObject.put("wrongMarks", wrongMarks);
                jsonObject.put("testCategory", testCategory);
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
        new uploadtos3().execute();


        if (testFinal) {
            if (S3Uplaod) {
                if (NetworkConnectivity.isConnected(this)) {
                    new saveResulttoServer().execute(finalinsertRecords);
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("insertRecords", insertRecords);
                        jsonObject.put("testId", testId);
                        jsonObject.put("studentId", studentId);
                        jsonObject.put("correctMarks", correctMarks);
                        jsonObject.put("wrongMarks", wrongMarks);
                        jsonObject.put("testCategory", testCategory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.v(TAG, "WriteJson - " + jsonObject.toString());
                    QueJsonFile_write(jsonObject);
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("Your internet seems to be down at the moment.Please connect and try again to submit the results")
                            .setPositiveButton("TryAgain", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    PrepareResult(true);
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Seems to be a problem while Submitting, Please try again")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PrepareResult(true);
                            }
                        })
                        .setCancelable(false)
                        .show();
                ;
            }
        }

    }


    private void QueJsonFile_write(JSONObject test_que_list) {
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
            writer.write(test_que_list.toString());
            writer.close();
        } catch (Exception e) {
            Log.v(TAG, "error - " + e.getMessage());
        }

    }

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
            if (listSectionDetails.size() > 0) {
                for (int x = 0; x < listSectionDetails.size(); x++) {
                    if (question.getQuestType().equalsIgnoreCase(listSectionDetails.get(x).getQuestType())) {
                        values.put("correctMarks", listSectionDetails.get(x).getCorrectAnswerMarks());
                        values.put("wrongMarks", listSectionDetails.get(x).getWrongAnswerMarks());
                    }
                }
                values.put("question_number", question.getQuestion_number());
                values.put("questionId", question.getQuestionId());
                values.put("correctAnswer", question.getCorrectAnswer());
                values.put("selectedAnswer", ans);
                values.put("style", style);
                values.put("timeTaken", Integer.parseInt( question.getTimeTaken()));
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
                values.put("branchId", sObj.getBranchId());
            } else {
                values.put("question_number", question.getQuestion_number());
                values.put("questionId", question.getQuestionId());
                values.put("correctAnswer", question.getCorrectAnswer());
                values.put("selectedAnswer", ans);
                values.put("style", style);
                values.put("timeTaken", Integer.parseInt( question.getTimeTaken()));
                values.put("subjectGroup", question.getSubjectGroup());
                values.put("subjectName", question.getSubjectName());
                values.put("questType", question.getQuestType());
                values.put("correctMarks", correctMarks);
                values.put("wrongMarks", wrongMarks);
                values.put("testCategory", testCategory);
                values.put("updated", question.getUpdated());
                values.put("chapterName", question.getChapterName());
                values.put("topic_name", question.getTopic_name());
                values.put("chapterId", question.getChapterId());
                values.put("topicId", question.getTopicId());
                values.put("subjectId", question.getSubjectId());
                values.put("branchId", sObj.getBranchId());
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
        timeCountInMilliSeconds = testTime * 1000;
    }

    //    CountUp timer for each question
    private void startCountUpTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    private void saveTimetaken() {
        timeSwapBuff += timeInMilliseconds;
        Log.v(TAG, "time - " + timeSwapBuff);
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            if (currentSelectedItem != spinner.getSelectedItemPosition()) {
                quesNo = 0;
                currentSelectedItem = spinner.getSelectedItemPosition();
            }
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            arrayOfArrays.get(spinner.getSelectedItemPosition()).get(quesNo).setTimeTaken("" + TimeUnit.MILLISECONDS.toSeconds((timeSwapBuff + timeInMilliseconds)));

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
//            timerValue.setText("" + mins + ":"
//                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

        }

    };


//    Countup timer  for each question end

//    Count Down Timer

    /**
     * method to start and stop count down timer
     */

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
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

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                total_testTime = total_testTime + 1000;

                tvTestTimer.setText(hmsTimeFormatter(millisUntilFinished));

                //progressBarCircle.setProgressMax(timeCountInMilliSeconds);
                progressBarCircle.setProgressWithAnimation((int) (millisUntilFinished / 1000), 10);
            }

            @Override
            public void onFinish() {

                tvTestTimer.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
//                createAlertFinishTimeUp(this);
                finishalert();

            }

        }.start();
//        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setProgressMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);


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

    public class uploadtos3 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responce = "responce";
            try {
                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 1000 * 60 * 60; // 1 hour.
                expiration.setTime(msec);

                key = testId + "/inprogress/";
                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretkey),
                        Region.getRegion(Regions.AP_SOUTH_1));
                PutObjectRequest por = new PutObjectRequest(bucket, key + studentId + ".json", postFile);//key is  URL

                //making the object Public
                por.setCannedAcl(CannedAccessControlList.PublicRead);
                s3Client1.putObject(por);

            } catch (Exception e) {
                // writing error to Log
                e.printStackTrace();
                responce = "exception";
                S3Uplaod = false;
            }

            return responce;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("responce")) {
                S3Uplaod = true;
                if (S3Uplaod) {
                    _finalUrl = s3Client1.getResourceUrl(sh_Pref.getString("bucket",""), key + studentId + ".json");
                    Log.v("TAG", "_finalUrl - " + _finalUrl);
                    if (liveExam.getStudentTestFilePath().equalsIgnoreCase("NA")
                            && sh_Pref.getString("studentTestFilePath", "NA").equalsIgnoreCase("NA")) {
                        toEdit.putString("studentTestFilePath", _finalUrl);
                        toEdit.commit();
                        new updateStudentProgress().execute();
                    }
                }
            }

        }
    }

    public class updateStudentProgress extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("path", _finalUrl);
                jsonObject.put("testId", testId);
                jsonObject.put("studentId", sObj.getStudentId());
                jsonObject.put("schemaName", sh_Pref.getString("schema", ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(TAG, jsonObject.toString());


            RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

            Request request = new Request.Builder()
                    .url(new AppUrls().BASE_URL + "updateStudentTestProgress")
                    .post(body)
                    .build();

            Log.v(TAG, request.body().toString());

            try {
                Response response = client.newCall(request).execute();

                jsonResp = response.body().string();
                Log.v(TAG, "responseBody - " + jsonResp);
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }


            return jsonResp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(result);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class saveResulttoServer extends AsyncTask<JSONArray, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(StudentOnlineTestActivity.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(JSONArray... params) {

            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("testId", liveExam.getTestId());
                jsonObject.put("studentId", studentId);
                jsonObject.put("correctMarks", correctMarks);
                jsonObject.put("wrongMarks", wrongMarks);
                jsonObject.put("testCategory", testCategory);
                jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
                jsonObject.put("updatedRecords", params[0]);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v(TAG, jsonObject.toString());


            RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

            Request request = new Request.Builder()
                    .url(AppUrls.SubmitStudentOnlineTest)
                    .post(body)
                    .build();

            Log.v(TAG, request.body().toString());

            try {
                Response response = client.newCall(request).execute();

                jsonResp = response.body().string();
                Log.v(TAG, "responseBody - " + jsonResp);
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }


            return jsonResp;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.dismiss();

            Log.v(TAG, "responseBody - result - " + result);

            if (result != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(result);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

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
                        timer.cancel();
                        Log.v(TAG, String.format("un is %d, cans is %d, wans is %d", tunans, tcans, twans));
                        Intent intent = new Intent(StudentOnlineTestActivity.this, StudentTestOnlineCompleted.class);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("testId", liveExam.getTestId());
                        intent.putExtra("jeeSectionTemplate", liveExam.getMjeeSectionTemplateName());
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
    }

    public class GetOnlineTestQuestions extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(StudentOnlineTestActivity.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            "CEDZ", "" + testCategoryObj.getMultipleSubjects(), "" + courseObj.getClasses().get(0).getSubjects().get(0).getContentType(),
//                    sObj.getStudentId(), chapterId,classId,courseId,subjectId,topicId,testType

            Log.v(TAG, "TestGetQue Url - " + new AppUrls().GetStudentOnlineTestQuestions + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&testId=" + strings[0] + "&studentId=" + strings[1]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetStudentOnlineTestQuestions + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&testId=" + strings[0] + "&studentId=" + strings[1])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "QuesList responce - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        new GetOnlineTestQuePaper().execute(liveExam.getTestFilePath());
                    } else {
//                        showNoDataDialogue();
                        showRetrivingErrorDialogue();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();

        }
    }


    public class GetOnlineTestQuePaper extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(StudentOnlineTestActivity.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "TestGetQueDetails Url - " + strings[0]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "QuesDetialsList responce - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                        JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                        testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                        }.getType()));

                        Log.v(TAG, "testQueList - " + testQueList.size());

                        if (liveExam.getStudentTestFilePath().equalsIgnoreCase("NA")
                                && sh_Pref.getString("studentTestFilePath", "NA").equalsIgnoreCase("NA"))
                            PrepareQuestionPaper();
                        else
                            new GetOnlineTestQueDetails().execute(sh_Pref.getString("studentTestFilePath", "NA"));
                    } else {
                        showNoDataDialogue();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();

        }
    }

    public class GetOnlineTestQueDetails extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(StudentOnlineTestActivity.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "TestGetQue Url - " + strings[0]);

            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "QuesPaperList responce - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("testId").equalsIgnoreCase(testId)) {

                        JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                        testQueDetailsList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                        }.getType()));

                        Log.v(TAG, "testQueDetailsList - " + testQueDetailsList.size());

                        if (testQueDetailsList.size() > 0) {
                            for (int i = 0; i < testQueDetailsList.size(); i++) {
                                for (int j = 0; j < testQueList.size(); j++) {
                                    if (testQueDetailsList.get(i).getQuestionId().equals(testQueList.get(j).getQuestionId())) {
                                        testQueList.get(j).setCorrectAnswer(testQueDetailsList.get(i).getCorrectAnswer());
                                        testQueList.get(j).setSelectedAnswer(testQueDetailsList.get(i).getSelectedAnswer());
                                        testQueList.get(j).setStyle(testQueDetailsList.get(i).getStyle());
                                        testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());
                                        testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());
                                        testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());
                                        testQueList.get(j).setTimeTaken(testQueDetailsList.get(i).getTimeTaken());

                                    }
                                }
                            }
                            PrepareQuestionPaper();
                        } else {
                            PrepareQuestionPaper();
                        }

                    } else {
                        PrepareQuestionPaper();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();

        }
    }

    private void PrepareQuestionPaper() {
        if (testQueList.size() > 0) {

            Log.v(TAG, "QuesList - " + testQueList.size());
            int allQueTime = 0;

            for (int i = 0; i < testQueList.size(); i++) {
//                testQueList.get(i).setQuestion_number((i + 1));
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

            startTest();
        } else {
            showNoDataDialogue();
        }
    }

    private void showRetrivingErrorDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Error While Retriving the Question Paper\n Please try again")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showNoDataDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Test Already Started/Submitted")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    //    List Adapter
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
                if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option1"))
                    viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number() + " : A");
                else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option2"))
                    viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number() + " : B");
                else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option3"))
                    viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number() + " : C");
                else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option4"))
                    viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number() + " : D");
                else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase(""))
                    viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number());
                else
                    viewHolder.sno_tv.setText("Q " + queslist.get(i).getQuestion_number() + " : " + queslist.get(i).getSelectedAnswer());
            } else {
//                if (queslist.get(i).isVisitedflag()) {
//                    viewHolder.gans_tv.setText("Unanswered");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered));
//                    if (queslist.get(i).isReviewflag()) {
//                        viewHolder.gans_tv.setText("Marked for Review");
//                        viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview));
//                    }
//                }
            }
            Log.v("sriee", "i - " + i);
            Log.v("sriee", queslist.get(i).getSelectedAnswer());
            if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("")) {
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


    class QueNoAdapter extends RecyclerView.Adapter<QueNoAdapter.ViewHolder> {

        ArrayList<OnlineQuestionObj2> questions; int qno;

        public QueNoAdapter(ArrayList<OnlineQuestionObj2> questions, int qno) {
            this.questions = questions;
            this.qno = qno;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(StudentOnlineTestActivity.this).inflate(R.layout.item_question_number,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            int index = arrayOfArrays.indexOf(questions);

            if (index>0){
                int extra = 0;
                for (int i=0;i<index;i++){
                    extra = extra+arrayOfArrays.get(i).size();
                }
                holder.tvQueNo.setText((extra+position+1)+"");

            }else{
                holder.tvQueNo.setText((position+1)+"");
            }

            if (quesNo==position){
                holder.tvQueNo.setTextColor(Color.BLACK);
            }else{
                holder.tvQueNo.setTextColor(Color.parseColor("#40000000"));
            }

            if (questions.get(position).getSelectedAnswer().equalsIgnoreCase("") || questions.get(position).getSelectedAnswer().equalsIgnoreCase("blank")){
                holder.imgTick.setVisibility(View.GONE);
                holder.tvQueNo.setVisibility(View.VISIBLE);

                if(questions.get(position).isReviewflag()){
                    holder.llItemView.setBackgroundResource(R.drawable.bg_review_circle_border);
                }else{
                    holder.llItemView.setBackgroundResource(R.drawable.bg_grey_circle_border);
                }
            }else{

                if(questions.get(position).isReviewflag()){
                    holder.llItemView.setBackgroundResource(R.drawable.bg_review_circle_border);
                }else{
                    holder.llItemView.setBackgroundResource(R.drawable.bg_grey_circle_border);
                }

                holder.tvQueNo.setVisibility(View.GONE);
                holder.imgTick.setVisibility(View.VISIBLE);
            }



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quesNo = position;
                    loadQuestion(questions,quesNo);
                }
            });


        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQueNo;
            ImageView imgTick;
            LinearLayout llItemView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQueNo = itemView.findViewById(R.id.tv_queNo);
                llItemView = itemView.findViewById(R.id.ll_itemview);
                imgTick = itemView.findViewById(R.id.iv_que_state);
            }
        }
    }
}
