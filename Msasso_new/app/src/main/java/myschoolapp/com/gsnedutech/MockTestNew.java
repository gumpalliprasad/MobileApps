/*
 * *
 *  * Created by SriRamaMurthy A on 1/10/19 4:53 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 1/10/19 3:38 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.MockMFQObj;
import myschoolapp.com.gsnedutech.Models.MockMFQQues;
import myschoolapp.com.gsnedutech.Models.MockQuestionObj;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MockTestNew extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "SriRam -" + MockTestNew.class.getName();
    MyUtils utils = new MyUtils();

    private int selectedPos = RecyclerView.NO_POSITION;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    List<MockQuestionObj> testQueList = new ArrayList<>();
    ArrayList<ArrayList<MockQuestionObj>> arrayOfArrays = new ArrayList<>();
    List<MockMFQObj> testMFQQueList = new ArrayList<>();
    List<String> subList;

    StdnTestDefClsCourseSub courseObj;
    StdnTestCategories testCategoryObj;

    int que_index = 0;
    int mfqQue_index = 0;
    int test_time = 0;

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private long startTime = 0L;
    private Handler customHandler = new Handler();

    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;
    private long timeCountInMilliSeconds = 1 * 60000;
    int total_testTime = 0;

    private Button[] btn = new Button[4];
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    private Button[] tfbtn = new Button[2];
    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};

    String selectedAnswer = "";
    String contentType = "";

    //    For MFQ
    List<String> answers_list = new ArrayList<>();
    List<Spinner> spinner_list = new ArrayList<>();
    //    For MFQ ENDED
    //    For FIB
    List<EditText[]> editTextLists = new ArrayList<>();
    //    For FIB ENDED

    int stepSize = 10;
    String[] ansArray;

    @BindView(R.id.spinner)
    Spinner subSpinner;
    @BindView(R.id.et_itq)
    EditText etItq;
    @BindView(R.id.que_no)
    TextView queNo;
    @BindView(R.id.wv_test)
    CustomWebview webViewTest;
    @BindView(R.id.tv_test_count)
    TextView tvTestCount;
    @BindView(R.id.tv_test_timer)
    TextView tvTestTimer;
    @BindView(R.id.test_title)
    TextView testTitle;
    @BindView(R.id.ll_mfq)
    ScrollView llMfq;
    @BindView(R.id.ll_fib)
    ScrollView llFib;
    @BindView(R.id.ll_mcq)
    LinearLayout llMcq;
    @BindView(R.id.ll_tf)
    LinearLayout llTf;
    @BindView(R.id.ll_itq)
    LinearLayout llITQ;
    @BindView(R.id.ll_option)
    LinearLayout llOption;
    @BindView(R.id.ll_next)
    LinearLayout llNext;
    @BindView(R.id.rv_quelist)
    RecyclerView rvQuelist;
    @BindView(R.id.hidden_panel)
    RelativeLayout hiddenPanel;
    @BindView(R.id.quelist)
    TextView quelist;
    @BindView(R.id.ll_que)
    LinearLayout llQue;
    @BindView(R.id.tl_answer)
    TableLayout tlAnswer;
    @BindView(R.id.layout_fibans)
    LinearLayout layoutFibans;

    @BindView(R.id.rv_que_no_list)
    RecyclerView rvQueNoList;

    QuesionNoAdapter adapter;

    String testType = "", courseId = "", classId = "", subjectId = "", chapterId = "", topicId = "";
    String chapterCCMapId = "", topicCCMapId = "";
    int skippedAnswers  = 0;

    int qno = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mock_test_new);
        ButterKnife.bind(this);

        init();

        rvQueNoList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                    int position = recyclerView.getChildAdapterPosition(child);
                    que_index = position;
                    queNo.setText("Q no: " + position);
                    arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setVisitedflag(true);
                    loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), que_index);
//                    slideUpDown();
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

        webViewTest.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(MockTestNew.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    private void init() {
        if (getIntent().hasExtra("contentType")) {
            contentType = getIntent().getStringExtra("contentType");
            utils.showLog(TAG, "Content type is ----> " + contentType);
        }

        rvQueNoList.setLayoutManager(new LinearLayoutManager(MockTestNew.this, RecyclerView.HORIZONTAL, false));

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        queNo.setText("Q no: 0");

//        progressBarCircle.setProgressBarWidth(getResources().getDimension(R.dimen.progressBarWidth));
//        progressBarCircle.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.backgroundProgressBarWidth));

        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            btn[i].setOnClickListener(MockTestNew.this);
        }

        for (int i = 0; i < tfbtn.length; i++) {
            tfbtn[i] = findViewById(tfbtn_id[i]);
            tfbtn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            tfbtn[i].setOnClickListener(MockTestNew.this);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvQuelist.setLayoutManager(mLayoutManager);
        rvQueNoList.setItemAnimator(new DefaultItemAnimator());

        classId = getIntent().getStringExtra("classId");
        subjectId = getIntent().getStringExtra("subjectId");
        chapterId = getIntent().getStringExtra("chapterId");
        topicId = getIntent().getStringExtra("topicId");

        chapterCCMapId = getIntent().getStringExtra(AppConst.ChapterCCMapId);
        topicCCMapId = getIntent().getStringExtra(AppConst.TopicCCMapId);
//        When Iam Navigating from StudentBottomFfragTests, this Condition is necessary
        if (getIntent().hasExtra("courseObj") && getIntent().hasExtra("testCategoryObj")) {
            courseObj = (StdnTestDefClsCourseSub) getIntent().getSerializableExtra("courseObj");
            testCategoryObj = (StdnTestCategories) getIntent().getSerializableExtra("testCategoryObj");

            testType = testCategoryObj.getCategoryId();
            courseId = courseObj.getCourseId();
            if (testCategoryObj.getCategoryId().equalsIgnoreCase("1") || testCategoryObj.getCategoryId().equalsIgnoreCase("2") ||
                    testCategoryObj.getCategoryId().equalsIgnoreCase("3") || testCategoryObj.getCategoryName().equalsIgnoreCase("CLASS-WISE")) {

                test_time = testCategoryObj.getDuration();
                testTitle.setText(getIntent().getStringExtra("testTitle"));
                if (NetworkConnectivity.isConnected(MockTestNew.this)) {
                    if(testCategoryObj.getCategoryName().equalsIgnoreCase("TOPIC-WISE")){
                        chapterCCMapId = getIntent().getStringExtra(AppConst.ChapterCCMapId);
                        topicCCMapId = getIntent().getStringExtra(AppConst.TopicCCMapId);
                    }else  if(testCategoryObj.getCategoryName().equalsIgnoreCase("CHAPTER-WISE")){
                        chapterCCMapId = getIntent().getStringExtra(AppConst.ChapterCCMapId);
                        topicCCMapId = "0";
                    }else  if(testCategoryObj.getCategoryName().equalsIgnoreCase("SUBJECT-WISE")){
                        chapterCCMapId = "0";
                        topicCCMapId = "0";
                    }
                    getQuestions("CEDZ", "" + testCategoryObj.getMultipleSubjects(), "" + courseObj.getClasses().get(0).getSubjects().get(0).getContentType(),
                            sObj.getStudentId(), chapterId, classId, courseId, subjectId, topicId, testType,chapterCCMapId, topicCCMapId );

                } else {
                    new MyUtils().alertDialog(1, MockTestNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close), false);
                }

            } else {
                test_time = testCategoryObj.getDuration();
                testTitle.setText(testCategoryObj.getCategoryName());
                classId = "" + sObj.getClassId();

                if (NetworkConnectivity.isConnected(MockTestNew.this)) {
                    getQuestions("CEDZ", "" + testCategoryObj.getMultipleSubjects(), "" + courseObj.getClasses().get(0).getSubjects().get(0).getContentType(),
                            sObj.getStudentId(), chapterId, classId, courseId, subjectId, topicId, testType, chapterCCMapId, topicCCMapId);

                } else {
                    new MyUtils().alertDialog(1, MockTestNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close), false);
                }

            }
        } else {
//            Calling this when navigating from Course Topic Test Page
            test_time = 15;
            testType = "1";
            courseId = getIntent().getStringExtra("courseId");
            classId = getIntent().getStringExtra("classId");
            subjectId = getIntent().getStringExtra("subId");
            chapterId = getIntent().getStringExtra("chapterId");
            topicId = getIntent().getStringExtra("topicId");

            testTitle.setText(getIntent().getStringExtra("topicName"));
            if (NetworkConnectivity.isConnected(MockTestNew.this)) {
                getQuestions("CEDZ", "0", "" + getIntent().getStringExtra("contentType"), sObj.getStudentId(),
                        chapterId, classId, courseId, subjectId, topicId, testType, chapterCCMapId, topicCCMapId);
            } else {
                new MyUtils().alertDialog(1, MockTestNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                        getString(R.string.action_settings), getString(R.string.action_close), false);
            }

        }


    }


    //    Starting the Test
    private void startTest() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < testQueList.size(); i++) {
            hs.add(testQueList.get(i).getSubjectGroup());
//            utils.showLog(TAG, "quesid - " + testQueList.get(i).getQuestionId());
        }

        if (hs.size() < 2) {
            subSpinner.setVisibility(View.INVISIBLE);
        }
        subList = new ArrayList<String>(hs);

        for (int i = 0; i < subList.size(); i++) {
            ArrayList<MockQuestionObj> sub_quelist = new ArrayList<>();
            for (int j = 0; j < testQueList.size(); j++) {
                if (subList.get(i).equalsIgnoreCase(testQueList.get(j).getSubjectGroup())) {
                    sub_quelist.add(testQueList.get(j));
                }
            }
            arrayOfArrays.add(sub_quelist);
        }
        utils.showLog(TAG, "arrayoff arrays size - " + arrayOfArrays.size());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(MockTestNew.this, R.layout.spinner_test_item, subList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        subSpinner.setAdapter(dataAdapter);

        // Spinner click listener
        subSpinner.setOnItemSelectedListener(MockTestNew.this);

        startStop();

    }

    //    Spinner OnItemSelected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        qno = calculateQno(position);
        adapter = new QuesionNoAdapter();
//                                    selectedPos = 0;
//                                    adapter.notifyItemChanged(selectedPos);
        rvQueNoList.setAdapter(adapter);

        que_index = 0;
        mfqQue_index = 0;
        loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), que_index);
    }

    private int calculateQno(int position) {
        int pos = 0;
        for (int i = 0; i < subSpinner.getCount(); i++) {
            if (i == position) {
                break;
            } else pos = pos + arrayOfArrays.get(i).size();
        }
        return pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //    Spinner OnItem Selected Ended

    private void loadQuestion(ArrayList<MockQuestionObj> questions, int ques_no) {


        queNo.setText("Q no: " + (qno + ques_no + 1));
        adapter.notifyItemChanged(selectedPos);
        selectedPos = ques_no;
        adapter.notifyItemChanged(selectedPos);
        selectedAnswer = questions.get(ques_no).getSelectedAnswer();
        unanswered_count();
        timeSwapBuff = questions.get(ques_no).getTimetaken();
        utils.showLog("TIME - ", "timeSwapBuff - " + timeSwapBuff);
        startCountUpTimer();
        questions.get(ques_no).setVisitedflag(true);

        if (questions.get(ques_no).getQuestType().equalsIgnoreCase("MCQ")) {

            llMcq.setVisibility(View.VISIBLE);
            webViewTest.setVisibility(View.VISIBLE);
            llFib.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            setUnFocusAll();
            if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("option1")) {
                setFocus(btn[0]);
            } else if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("option2")) {
                setFocus(btn[1]);
            } else if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("option3")) {
                setFocus(btn[2]);
            } else if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("option4")) {
                setFocus(btn[3]);
            }


//            String sample ="<p><img alt=\"\\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\] \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\] \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\] \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\]\" src=\"http://latex.codecogs.com/gif.latex?%5Cdpi%7B100%7D%20%5Cfn_jvn%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D\" /><br /> <img alt=\"\\large \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\] \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\] \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\] \\[ \\,\\frac{{Diplacement}} {{Time}}\\,\\, = \\,\\,\\left( {\\frac{{S + 2S}} {{\\frac{S} {V} + \\frac{{2S}} {{3V}}}}} \\right) \\]\" src=\"http://latex.codecogs.com/gif.latex?%5Cdpi%7B100%7D%20%5Cfn_jvn%20%5Clarge%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D%20%5C%5B%20%5C%2C%5Cfrac%7B%7BDiplacement%7D%7D%20%7B%7BTime%7D%7D%5C%2C%5C%2C%20%3D%20%5C%2C%5C%2C%5Cleft%28%20%7B%5Cfrac%7B%7BS%20&amp;plus;%202S%7D%7D%20%7B%7B%5Cfrac%7BS%7D%20%7BV%7D%20&amp;plus;%20%5Cfrac%7B%7B2S%7D%7D%20%7B%7B3V%7D%7D%7D%7D%7D%20%5Cright%29%20%5C%5D\" /></p> ";
//            String mainString = "<!DOCTYPE html> <html>" + sample +
//                    "<fieldset><legend>A)</legend>" + sample + "</fieldset>"
//                    + "<fieldset><legend>B)</legend>" + sample + "</fieldset>"
//                    + "<fieldset><legend>C)</legend>" + sample + "</fieldset>"
//                    + "<fieldset><legend>D)</legend>" + sample + "</fieldset>"
//                    + "</html>";
            String mainString = "<!DOCTYPE html> <html>"+ questions.get(ques_no).getQuestion() +
                    "<fieldset><legend>A)</legend>" + questions.get(ques_no).getOption1() + "</fieldset>"
                    + "<fieldset><legend>B)</legend>" + questions.get(ques_no).getOption2() + "</fieldset>"
                    + "<fieldset><legend>C)</legend>" + questions.get(ques_no).getOption3() + "</fieldset>"
                    + "<fieldset><legend>D)</legend>" + questions.get(ques_no).getOption4() + "</fieldset>"
                    + "</html>";


            mainString = utils.cleanWebString(mainString);
            webViewTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            webViewTest.scrollTo(0, 0);


        } else if (questions.get(ques_no).getQuestType().equalsIgnoreCase("ITQ")) {
            webViewTest.setVisibility(View.VISIBLE);
            llITQ.setVisibility(View.VISIBLE);
            llTf.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            llOption.removeAllViews();

            String mainString = "<!DOCTYPE html> <html>" + questions.get(ques_no).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            webViewTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");

//            ansArray[que_index] = testQueList.get(que_index).getCorrectAnswer();
            webViewTest.scrollTo(0, 0);

//            String options = questions.get(ques_no).getOption1().substring(3, questions.get(ques_no).getOption1().length() - 5);
//            utils.showLog(TAG,"options"+ options);
//            ansArray = options.split(",");
//
//            for (int i = 0; i < ansArray.length; i++) {
//                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//                TextView tv = new TextView(this);
//                tv.setGravity(Gravity.CENTER);
//                tv.setText(ansArray[i]);
//                llOption.addView(tv, param);
//            }
//
//            seek.setMax((ansArray.length - 1) * 10);
//
//            if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("blank")) {
//                seek.setProgress(0);
//            } else {
//                seek.setProgress((Arrays.asList(ansArray).indexOf(questions.get(ques_no).getSelectedAnswer())) * 10);
//            }


        } else if (questions.get(ques_no).getQuestType().equalsIgnoreCase("TOF")) {
            llTf.setVisibility(View.VISIBLE);
            webViewTest.setVisibility(View.VISIBLE);
            llITQ.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            setUnFocusAll();
            if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("true")) {
                setFocus(tfbtn[0]);
            } else if (questions.get(ques_no).getSelectedAnswer().equalsIgnoreCase("false")) {
                setFocus(tfbtn[1]);
            }

            String mainString = "<!DOCTYPE html> <html>" + questions.get(ques_no).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            webViewTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            webViewTest.scrollTo(0, 0);

        } else if (questions.get(ques_no).getQuestType().equalsIgnoreCase("FIB")) {
            utils.showLog(TAG, "FIB Que - " + questions.get(ques_no).getQuestion());
            utils.showLog(TAG, "\n FIB Que - " + questions.get(ques_no).getCorrectAnswer());
            llFib.setVisibility(View.VISIBLE);
            layoutFibans.removeAllViews();
            editTextLists.clear();

            webViewTest.setVisibility(View.VISIBLE);
            llMcq.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            tlAnswer.setVisibility(View.GONE);

            String mainString = "<!DOCTYPE html> <html>" + questions.get(ques_no).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            webViewTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            webViewTest.scrollTo(0, 0);

            setFIBLayout(questions.get(ques_no).getCorrectAnswer(), questions.get(ques_no).getSelectedAnswer());

        } else if (questions.get(ques_no).getQuestType().equalsIgnoreCase("MFQ")) {
            llMfq.setVisibility(View.VISIBLE);
            tlAnswer.setVisibility(View.VISIBLE);

            llQue.removeAllViews();
            tlAnswer.removeAllViews();
            spinner_list.clear();

            webViewTest.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);

            setMFQlayout(questions.get(ques_no).getQueOptions(), questions.get(ques_no).getSelectedAnswer());
        }
    }


    private void setFIBLayout(String cAnswer, String selAns) {
        String[] fibans_words;
//        cAnswer =  cAnswer.replaceAll("\\s", "");
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
            utils.showLog(TAG, "selAns - " + selAns);
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


    private void setMFQlayout(List<MockMFQQues> quesListObj, String selectedAnswer) {

        answers_list.clear();
        utils.showLog(TAG, "Col A " + quesListObj.get(0).getColumnA());

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
        utils.showLog(TAG, "number of rows " + num_of_rows);

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

        utils.showLog(TAG, "mfqSpiiner - " + selectedAnswer);
        utils.showLog(TAG, "mfqSpiiner - " + spinner_list.size());
        if (!selectedAnswer.equalsIgnoreCase("blank")) {
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


    @Override
    public void onClick(View v) {
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


    @OnClick(R.id.btn_clear)
    public void onBtnClearClicked() {
        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer("blank");
        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setReviewflag(false);
        setUnFocusAll();
        etItq.setText("");

        if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("FIB")) {

            for (int i = 0; i < editTextLists.size(); i++) {
                for (int j = 0; j < editTextLists.get(i).length; j++) {
                    editTextLists.get(i)[j].getText().clear();
                }
            }
        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("ITQ")) {


            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(ansArray[Integer.parseInt(etItq.getText().toString())]);
        }
        adapter.notifyItemChanged(que_index);
        selectedAnswer = "";

    }

    @OnClick(R.id.btn_review_next)
    public void onBtnReviewNextClicked() {

        if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("MFQ")) {
            String answers = "";
            for (int i = 0; i < spinner_list.size(); i++) {
                if (!(spinner_list.get(i).getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                    answers = answers + spinner_list.get(i).getSelectedItem().toString();
                } else {
                    answers = answers + "X";
                }
            }
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(answers);
        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("FIB")) {

            String answers = "";
            for (int i = 0; i < editTextLists.size(); i++) {
                if (i > 0) {
                    answers = answers + ",";
                }
                for (int j = 0; j < editTextLists.get(i).length; j++) {
                    answers = answers + editTextLists.get(i)[j].getText().toString();
                }
                utils.showLog(TAG, " Answer " + answers);
            }

            if (!answers.equalsIgnoreCase(""))
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(answers);
        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("ITQ")) {

            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(ansArray[Integer.parseInt(etItq.getText().toString())]);
//            Toast.makeText(this, "" + arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getSelectedAnswer(), Toast.LENGTH_LONG).show();

        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("MCQ")) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(selectedAnswer);
            selectedAnswer = "";
        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("TOF")) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(selectedAnswer);
            selectedAnswer = "";
        }

        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setReviewflag(true);
        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setProceed(true);
        saveTimetaken();
        arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setTimetaken(timeSwapBuff);

//        uploadtoServer(que_index);
        skippedAnswers += 1;
        if (++que_index < arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size()) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setVisitedflag(true);
            loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), que_index);
        } else {
            que_index = que_index - 1;
            if (subSpinner.getSelectedItemPosition() < subList.size() - 1) {
                subSpinner.setSelection(subSpinner.getSelectedItemPosition() + 1);
            } else {
//                Toast.makeText(this, "FInished the test", Toast.LENGTH_SHORT).show();
                finishalert();
            }
        }
    }

    @OnClick(R.id.btn_save_next)
    public void onBtnSaveNextClicked() {

        if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("MFQ")) {
            String answers = "";
            for (int i = 0; i < spinner_list.size(); i++) {
                if (!(spinner_list.get(i).getSelectedItem().toString().equalsIgnoreCase("Select"))) {
                    answers = answers + spinner_list.get(i).getSelectedItem().toString();
                } else {
                    answers = answers + "X";
                }
            }
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(answers);
        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("FIB")) {

            String answers = "";
            for (int i = 0; i < editTextLists.size(); i++) {
                if (i > 0) {
                    answers = answers + ",";
                }
                for (int j = 0; j < editTextLists.get(i).length; j++) {
                    answers = answers + editTextLists.get(i)[j].getText().toString();
                }
                utils.showLog(TAG, " Answer " + answers);
            }
            if (!answers.equalsIgnoreCase(""))
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(answers);

        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("ITQ")) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(etItq.getText().toString());
            etItq.setText("");

        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("MCQ")) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(selectedAnswer);
            selectedAnswer = "";
        } else if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getQuestType().equalsIgnoreCase("TOF")) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer(selectedAnswer);
            selectedAnswer = "";
        }


        if (!arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).getSelectedAnswer().equalsIgnoreCase("blank")) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setReviewflag(false);
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setProceed(true);
            saveTimetaken();
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setTimetaken(timeSwapBuff);

//            uploadtoServer(que_index);

            if (++que_index < arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size()) {
                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setVisitedflag(true);
                loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), que_index);
            } else {
                que_index = que_index - 1;
                if (subSpinner.getSelectedItemPosition() < subList.size() - 1)
                    subSpinner.setSelection(subSpinner.getSelectedItemPosition() + 1);
                else
//                    Toast.makeText(this, "Finished the test", Toast.LENGTH_SHORT).show();
                    finishalert();
            }
        } else {
            skipQuestion();
        }
    }

    @OnClick(R.id.img_test_qlist)
    public void onImgTestQlistClicked() {
        slideUpDown();
    }

    @OnClick(R.id.btn_test_finish)
    public void onBtnTestFinishClicked() {
        finishalert();
    }

    @OnClick(R.id.img_close)
    public void onImgCloseClicked() {
        slideUpDown();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (isPanelShown())
            slideUpDown();
        else
            Toast.makeText(this, "Please Submit the Test to Exit.", Toast.LENGTH_SHORT).show();

    }

    private void slideUpDown() {

        if (!isPanelShown()) {

            // Show the panel

//            if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).isProceed())
//                arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer("blank");
            quelist.setText(subSpinner.getSelectedItem().toString());
            RecyclerView.Adapter adapter = new EntranceTestReviewAdapter(MockTestNew.this, arrayOfArrays.get(subSpinner.getSelectedItemPosition()));
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

    private boolean isPanelShown() {

        return hiddenPanel.getVisibility() == View.VISIBLE;
    }


    private void unanswered_count() {
        utils.showLog(TAG, "allquelist size - " + testQueList.size());
        int count = 0;
        for (int i = 0; i < testQueList.size(); i++) {
            utils.showLog(TAG, "allquelist size - " + testQueList.get(i).getSelectedAnswer());
            if (testQueList.get(i).getSelectedAnswer().equalsIgnoreCase("blank"))
                count++;
        }
        count = testQueList.size() - count;

        tvTestCount.setText("Answered : " + count + " / " + testQueList.size());
    }

    private void skipQuestion() {
//        uploadtoServer(ques_no);

        if (!arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).isProceed())
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setSelectedAnswer("blank");

        if (++que_index < arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size()) {
            arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(que_index).setVisitedflag(true);
            loadQuestion(arrayOfArrays.get(subSpinner.getSelectedItemPosition()), que_index);
        } else {
            que_index = que_index - 1;
            utils.showLog(TAG, "position - " + subSpinner.getSelectedItemPosition());
            if (subSpinner.getSelectedItemPosition() < subList.size() - 1)
                subSpinner.setSelection(subSpinner.getSelectedItemPosition() + 1);
            else
//                Toast.makeText(this, "Finished the test", Toast.LENGTH_SHORT).show();
                finishalert();
        }
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


    //    Finish Alert
    private void finishalert() {
        int uans = 0, markedreview = 0, ans = 0;
        String Message = "Are you sure you want to finish this Test?";

        for (int i = 0; i < arrayOfArrays.size(); i++) {
            for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
                if (arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase("blank"))
                    uans++;

                if (arrayOfArrays.get(i).get(j).isReviewflag() && arrayOfArrays.get(i).get(j).getSelectedAnswer().equalsIgnoreCase("blank"))
                    markedreview++;

            }

        }
//        Message = "Are you sure you want to finish this Test? \n\nThere are "
//                + uans + " Unanswerd Questions. \n\n There are " + markedreview + " Questions as marked for Review.\n\n There are"
//                +ans + "Answered Questions.";


        if (timerStatus != TimerStatus.STOPPED) {

            Dialog dialog = new Dialog(MockTestNew.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_submit_alert);
            if (dialog.isShowing()) dialog.dismiss();
            dialog.show();
            TextView tvUnanswered = dialog.findViewById(R.id.tv_unanswered);
            TextView tvReviewed = dialog.findViewById(R.id.tv_reviewed);
            TextView tvContinue = dialog.findViewById(R.id.tv_continue);
            TextView tvSubmit = dialog.findViewById(R.id.tv_submit);
            LinearLayout llMain = dialog.findViewById(R.id.ll_main_dialog);
            LinearLayout llLast = dialog.findViewById(R.id.ll_last);
            LinearLayout llYes = dialog.findViewById(R.id.ll_yes);
            LinearLayout llNo = dialog.findViewById(R.id.ll_no);
            tvUnanswered.setText("" + uans);
            tvReviewed.setText("" + markedreview);
            tvContinue.setOnClickListener(view -> {
                dialog.dismiss();
            });
            tvSubmit.setOnClickListener(view -> {
                llLast.setVisibility(View.VISIBLE);
                llMain.setVisibility(View.GONE);
            });

            llNo.setOnClickListener(view -> {
                dialog.dismiss();
            });

            llYes.setOnClickListener(view -> {
                preparingResult();
//                new PreparingResult().execute();

                dialog.dismiss();
            });
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("Time's UP")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            preparingResult();
//                            new PreparingResult().execute();


                        }
                    })

                    .setCancelable(false)
                    .show();
        }
    }

    //    CountUp timer for each question
    private void startCountUpTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    private void saveTimetaken() {
        timeSwapBuff += timeInMilliseconds;
        utils.showLog(TAG, "time - " + timeSwapBuff);
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

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

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {

        timeCountInMilliSeconds = test_time * 60 * 1000;
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                total_testTime = total_testTime + 1000;

                tvTestTimer.setText(hmsTimeFormatter(millisUntilFinished));

                //progressBarCircle.setProgressMax(timeCountInMilliSeconds);
//                progressBarCircle.setProgressWithAnimation((int) (millisUntilFinished / 1000), 10);


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

//        progressBarCircle.setProgressMax((int) timeCountInMilliSeconds / 1000);
//        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);


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


    private void getQuestions(String cedz, String s, String s1, String studentId, String chapterId, String classId,
                              String courseId, String subjectId, String topicId, String testType,
                               String chapterCCMapId, String topicCCMapId) {
        ApiClient apiClient = new ApiClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            "CEDZ", "" + testCategoryObj.getMultipleSubjects(), "" + courseObj.getClasses().get(0).getSubjects().get(0).getContentType(),
//                    sObj.getStudentId(), chapterId,classId,courseId,subjectId,topicId,testType

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("contentOwner", cedz);
            jsonObject.put("multipleSubjects", s);
            jsonObject.put("questionType", s1);
            jsonObject.put("studentId", studentId);
            jsonObject.put("testChapter", chapterCCMapId);
            jsonObject.put("testClass", classId);
            jsonObject.put("testCourse", courseId);
            jsonObject.put("testSubject", subjectId);
            jsonObject.put("testTopic", topicCCMapId);
            jsonObject.put("testType", testType);

            JSONArray jsArray = new JSONArray();
            JSONObject repoObj = new JSONObject();
            repoObj.put("repo", "rankr");
            repoObj.put("noOfQue", 10);
            jsArray.put(repoObj);
            jsonObject.put("questionRepoObj", jsArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        utils.showLog(TAG, "TestGetQue Url - " + new AppUrls().GetMockTestQueestions);
        utils.showLog(TAG, "TestGetQue Obj - " + jsonObject);
        Request request = apiClient.postRequest(new AppUrls().GetMockTestQueestions, body, sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "QuesList responce - " + responce);

                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("TestQuestions");
                            JSONArray MFQFiltered = new JSONArray();
                            JSONArray MFQJSONArray = new JSONArray();

                            for (int j = 0; j < jsonArr.length(); j++) {
                                if (!jsonArr.getJSONObject(j).getString("questType").equalsIgnoreCase("MFQ"))
                                    MFQFiltered.put(jsonArr.getJSONObject(j));
                                else
                                    MFQJSONArray.put(jsonArr.getJSONObject(j));

                            }

                            utils.showLog(TAG, String.valueOf(MFQFiltered));
                            utils.showLog(TAG, String.valueOf(MFQJSONArray));

                            testMFQQueList.addAll(new Gson().fromJson(MFQJSONArray.toString(), new TypeToken<List<MockMFQObj>>() {
                            }.getType()));
                            testQueList.addAll(new Gson().fromJson(MFQFiltered.toString(), new TypeToken<List<MockQuestionObj>>() {
                            }.getType()));

                            if (testQueList.size() > 0) {

                                for (int i = 0; i < testMFQQueList.size(); i++) {
                                    MockQuestionObj mqo = new MockQuestionObj();
                                    mqo.setCorrectMarks(testMFQQueList.get(i).getCorrectMarks());
                                    mqo.setWrongMarks(testMFQQueList.get(i).getWrongMarks());
                                    mqo.setQuestionId(testMFQQueList.get(i).getQuestionId());
                                    mqo.setQuestion("Match the Following");
                                    mqo.setQueOptions(testMFQQueList.get(i).getQuestion());
                                    mqo.setSubjectGroup(testMFQQueList.get(i).getSubjectGroup());
                                    mqo.setQuestType(testMFQQueList.get(i).getQuestType());
                                    mqo.setChapterName(testMFQQueList.get(i).getChapterName());
                                    mqo.setExplanation(testMFQQueList.get(i).getExplanation());
                                    mqo.setSubjectId(testMFQQueList.get(i).getSubjectId());
                                    mqo.setDuration(testMFQQueList.get(i).getDuration());
                                    mqo.setTopicId(testMFQQueList.get(i).getTopicId());
                                    mqo.setChapterId(testMFQQueList.get(i).getChapterId());
                                    mqo.setOption3("NA");
                                    mqo.setOption4("NA");
                                    mqo.setOption1("NA");
                                    mqo.setOption2("NA");
                                    mqo.setTopicName(testMFQQueList.get(i).getTopicName());
                                    mqo.setCorrectAnswer(testMFQQueList.get(i).getCorrectAnswer());
                                    mqo.setSubjectName(testMFQQueList.get(i).getSubjectName());
                                    mqo.setChapterCCMapId(testMFQQueList.get(i).getChapterCCMapId());
                                    mqo.setTopicCCMapId(testMFQQueList.get(i).getTopicCCMapId());

                                    String cAns = "";
                                    for (int j = 0; j < testMFQQueList.get(i).getQuestion().size(); j++) {
                                        cAns = cAns + testMFQQueList.get(i).getQuestion().get(j).getCorrectAnswer();
                                    }

                                    mqo.setCorrectAnswer(cAns);

                                    utils.showLog(TAG, "QuesList MFQ - cAns - " + cAns);
                                    testQueList.add(mqo);
                                }
                                utils.showLog(TAG, "QuesList MFQ - " + testMFQQueList.size());
                                testMFQQueList.clear();
                                utils.showLog(TAG, "QuesList MFQ - " + testMFQQueList.size());
                                utils.showLog(TAG, "QuesList MFQFilterd - " + testQueList.size());

                                runOnUiThread(() -> {
                                    startTest();
                                });
                            }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                String message = parentjObject.getString(AppConst.MESSAGE);
                                runOnUiThread(() -> {
                                    utils.dismissDialog();
                                    MyUtils.forceLogoutUser(toEdit, MockTestNew.this, message, sh_Pref);
                                });
                            } else {
                                runOnUiThread(() -> showNoDataDialogue());
                            }
                        } else {
                            runOnUiThread(() -> showNoDataDialogue());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class QuesionNoAdapter extends RecyclerView.Adapter<QuesionNoAdapter.ViewHolder> {

        @NonNull
        @Override
        public QuesionNoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuesionNoAdapter.ViewHolder(LayoutInflater.from(MockTestNew.this).inflate(R.layout.item_question_number, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull QuesionNoAdapter.ViewHolder holder, int position) {
            holder.itemView.setSelected(selectedPos == position);
            if (holder.itemView.isSelected()) {
                holder.tvQueNo.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.black));
            } else {
                holder.tvQueNo.setTextColor(Color.parseColor("#40000000"));
            }
            if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(position).isReviewflag()) {
                holder.llItemview.setBackgroundResource(R.drawable.bg_review_circle_border);
            } else {
                holder.llItemview.setBackgroundResource(R.drawable.bg_grey_circle_border);
            }

            holder.tvQueNo.setText("" + (qno + position + 1));
            if (arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(position).getSelectedAnswer().equalsIgnoreCase("blank") || arrayOfArrays.get(subSpinner.getSelectedItemPosition()).get(position).getSelectedAnswer().equalsIgnoreCase("")) {
                holder.tvQueNo.setVisibility(View.VISIBLE);
                holder.ivQueState.setVisibility(View.GONE);
            } else {
                holder.tvQueNo.setVisibility(View.GONE);
                holder.ivQueState.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyItemChanged(selectedPos);
                    selectedPos = position;
                    notifyItemChanged(selectedPos);
                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayOfArrays.get(subSpinner.getSelectedItemPosition()).size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            TextView tvQueNo;
            ImageView ivQueState;
            LinearLayout llItemview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                llItemview = itemView.findViewById(R.id.ll_itemview);
                tvQueNo = itemView.findViewById(R.id.tv_queNo);
                ivQueState = itemView.findViewById(R.id.iv_que_state);
            }
        }
    }

    private void showNoDataDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("No Data To Load")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    void preparingResult() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                stopCountDownTimer();
                ArrayList<MockQuestionObj> test_que_list = new ArrayList<>();
                for (int i = 0; i < arrayOfArrays.size(); i++) {
                    test_que_list.addAll(arrayOfArrays.get(i));
                }
                QueJsonFile_write(test_que_list);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopCountDownTimer();

                        Intent testreResultIntent = new Intent(MockTestNew.this, MockTestResult.class);
                        testreResultIntent.putExtra("testTitle", testTitle.getText().toString());
                        testreResultIntent.putExtra("testType", testType);
                        testreResultIntent.putExtra("courseId", courseId);
                        testreResultIntent.putExtra("classId", classId);
                        testreResultIntent.putExtra("subjectId", subjectId);
                        testreResultIntent.putExtra("chapterId", chapterId);
                        testreResultIntent.putExtra("topicId", topicId);
                        testreResultIntent.putExtra(AppConst.TopicCCMapId, topicCCMapId);
                        testreResultIntent.putExtra(AppConst.ChapterCCMapId, chapterCCMapId);
                        testreResultIntent.putExtra("totalQuestions", "" + testQueList.size());
                        testreResultIntent.putExtra("timeSpent", "" + total_testTime);
                        testreResultIntent.putExtra("questions", (Serializable) testQueList);
                        testreResultIntent.putExtra("skipperQuestions", ""+skippedAnswers);


//                        testreResultIntent.putExtra("correctMarks",array)
                        startActivity(testreResultIntent);
                        finish();
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    }
                });
            }
        }).start();

    }

    private void QueJsonFile_write(ArrayList<MockQuestionObj> test_que_list) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (new File(getExternalFilesDir(null) + getString(R.string.path_testjson)).exists()) {
                new File(getExternalFilesDir(null) + getString(R.string.path_testjson)).delete();
            } else {
                if (!MyUtils.isFileDataAvailableOrNot(this, getString(R.string.path_base))) {
                    File folder = new File(getExternalFilesDir(null) + getString(R.string.path_base));
                    folder.mkdir();
                }
            }
            try {
                Writer writer = new FileWriter(getExternalFilesDir(null) + getString(R.string.path_testjson));
                Gson gson = new GsonBuilder().create();
                gson.toJson(test_que_list, writer);
                writer.close();
            } catch (Exception e) {
                utils.showLog(TAG, "error - " + e.getMessage());
            }
        } else {
            if (new File(Environment.getExternalStorageDirectory() + getString(R.string.path_testjson)).exists()) {
                new File(Environment.getExternalStorageDirectory() + getString(R.string.path_testjson)).delete();
            } else {
                if (!MyUtils.isFileDataAvailableOrNot(this, getString(R.string.path_base))) {
                    File folder = new File(Environment.getExternalStorageDirectory() + getString(R.string.path_base));
                    folder.mkdir();
                }
            }
            try {
                Writer writer = new FileWriter(Environment.getExternalStorageDirectory() + getString(R.string.path_testjson));
                Gson gson = new GsonBuilder().create();
                gson.toJson(test_que_list, writer);
                writer.close();
            } catch (Exception e) {
                utils.showLog(TAG, "error - " + e.getMessage());
            }
        }
    }

    //    Recycler View Adapter
    public class EntranceTestReviewAdapter extends RecyclerView.Adapter<EntranceTestReviewAdapter.ViewHolder> {
        private List<MockQuestionObj> queslist;
        private Context _context;


        public EntranceTestReviewAdapter(Context _context, List<MockQuestionObj> queslist) {
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

//        viewHolder.sno_tv.setText("Q " + (i + 1));

            if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option1"))
                viewHolder.sno_tv.setText("Q " + (i + 1) + " : A");
            else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option2"))
                viewHolder.sno_tv.setText("Q " + (i + 1) + " : B");

            else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option3"))
                viewHolder.sno_tv.setText("Q " + (i + 1) + " : C");

            else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("option4"))
                viewHolder.sno_tv.setText("Q " + (i + 1) + " : D");

            else if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("blank"))
                viewHolder.sno_tv.setText("Q " + (i + 1));
            else
                viewHolder.sno_tv.setText("Q " + (i + 1) + " : " + queslist.get(i).getSelectedAnswer());

            utils.showLog("sriee", "i - " + i);
            utils.showLog("sriee", queslist.get(i).getSelectedAnswer());

            if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("blank")
                    || allCharactersComma(queslist.get(i).getSelectedAnswer())) {
                viewHolder.gans_tv.setText("Not Visited");
//                viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.notvisited));
                viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.notvisited));

                if (queslist.get(i).isVisitedflag()) {
                    viewHolder.gans_tv.setText("Unanswered");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.unanswered));
                    if (queslist.get(i).isReviewflag()) {
                        viewHolder.gans_tv.setText("Marked for Review");
//                        viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview));
                        viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.markreview));
                    }
                }
            } else {
                if (queslist.get(i).isReviewflag()) {
                    viewHolder.gans_tv.setText("Answered & Marked for Review");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.ansmark));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.ansmark));

                } else {
                    viewHolder.gans_tv.setText("Answered");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.answered));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.answered));

                }

            }
            if (!queslist.get(i).getStyle().equalsIgnoreCase("not_visited_div")) {
                utils.showLog("stylesriran", "" + i + " -  " + queslist.get(i).getStyle());

                if (queslist.get(i).getStyle().equalsIgnoreCase("markedrview_div")) {
                    viewHolder.gans_tv.setText("Marked for Review");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.markreview));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.markreview));

                } else if (queslist.get(i).getStyle().equalsIgnoreCase("not_answered_div")) {
                    viewHolder.gans_tv.setText("Unanswered");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.unanswered));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.unanswered));

                } else if (queslist.get(i).getStyle().equalsIgnoreCase("answered_div")) {
                    viewHolder.gans_tv.setText("Answered");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.answered));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.answered));

                } else if (queslist.get(i).getStyle().equalsIgnoreCase("answered_marked_div")) {
                    viewHolder.gans_tv.setText("Answered & Marked for Review");
//                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.ansmark));
                    viewHolder.gans_tv.setTextColor(ContextCompat.getColor(MockTestNew.this, R.color.ansmark));

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
//            ans_tv = view.findViewById(R.id.ans_tv);
//            review_tv = view.findViewById(R.id.review_flag);
//            time_tv = view.findViewById(R.id.time);

            }
        }

    }

    boolean allCharactersComma(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++)
            if (s.charAt(i) != ',')
                return false;

        return true;
    }


}
