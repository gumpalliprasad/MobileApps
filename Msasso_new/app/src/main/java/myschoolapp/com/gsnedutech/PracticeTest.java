package myschoolapp.com.gsnedutech;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.PracMFQObj;
import myschoolapp.com.gsnedutech.Models.PracQuestionObj;
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

public class PracticeTest extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + PracticeTest.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.wv_test)
    CustomWebview wvQuestion;

//    @BindView(R.id.ll_paragraph)
//    LinearLayout llParagraph;

    @BindView(R.id.ll_mcq)
    LinearLayout llMcq;
    @BindView(R.id.ll_tf)
    LinearLayout llTf;
    @BindView(R.id.ll_itq)
    LinearLayout llITQ;
    @BindView(R.id.ll_fib)
    ScrollView llFib;
    @BindView(R.id.layout_fibans)
    LinearLayout llFibans;
    @BindView(R.id.ll_option)
    LinearLayout llOption;

    @BindView(R.id.et_itq)
    EditText etItq;

    @BindView(R.id.tv_next)
    TextView tvNext;

    @BindView(R.id.tv_queNo)
    TextView tvQueNO;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.tv_practice_timer)
    TextView tvPracticeTimer;

    @BindView(R.id.chronometer)
    Chronometer chronometer;

    @BindView(R.id.tv_type_header)
    TextView tvTypeHeader;


    private Button[] btn;


    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    long test_timetaken = 0;
    private long startTime = 0L;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;


    String testType = "", courseId = "", classId = "", subjectId = "", chapterId = "", topicId = "";

    List<EditText[]> editTextLists = new ArrayList<>();
    List<String> fibAnswers = new ArrayList<>();


    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    private Button[] tfbtn = new Button[2];
    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};


    List<PracQuestionObj> testQueList = new ArrayList<>();
    ArrayList<ArrayList<PracQuestionObj>> arrayOfArrays = new ArrayList<>();
    List<PracMFQObj> testMFQQueList = new ArrayList<>();
    List<String> subList;

    int que_index = 0;

    Dialog dialog;

    String selectedOpt = "";


    private Handler customHandler = new Handler();

    private Runnable updateTimerThread = new Runnable() {

        public void run() {


            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
//          timerValue.setText("" + mins + ":"+ String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    private void startTestTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime() + test_timetaken);
        chronometer.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = ContextCompat.getDrawable(this, R.drawable.gradient_theme);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_practice_test);
        ButterKnife.bind(this);

        init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, PracticeTest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                if (subjectId != null && subjectId.equalsIgnoreCase("0")) {
                    getQuestions("CEDZ", "1", "" + getIntent().getStringExtra("contentType"), sObj.getStudentId(),
                            chapterId, classId, courseId, subjectId, topicId, testType);
                } else {
                    if (subjectId != null)
                    getQuestions("CEDZ", "0", "" + getIntent().getStringExtra("contentType"), sObj.getStudentId(),
                            chapterId, classId, courseId, subjectId, topicId, testType);
                }
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        testType = getIntent().getStringExtra("testCategoryId");
        courseId = getIntent().getStringExtra("courseId");
        classId = getIntent().getStringExtra("classId");
        subjectId = getIntent().getStringExtra("subjectId");
        chapterId = getIntent().getStringExtra(AppConst.ChapterCCMapId);
        topicId = getIntent().getStringExtra(AppConst.TopicCCMapId);
        if (getIntent().hasExtra("testType") && getIntent().getStringExtra("testType").equalsIgnoreCase("SUBJECT-WISE")) {
            tvTypeHeader.setText(getIntent().getStringExtra("subjectName"));
            chapterId = "0";
            topicId = "0";
        } else if (getIntent().hasExtra("testType") && getIntent().getStringExtra("testType").equalsIgnoreCase("CHAPTER-WISE")) {
            tvTypeHeader.setText(getIntent().getStringExtra("chapterName"));
            topicId = "0";
        } else if (getIntent().hasExtra("testType") && getIntent().getStringExtra("testType").equalsIgnoreCase("TOPIC-WISE")) {
            tvTypeHeader.setText(getIntent().getStringExtra("topicName"));
        } else if (getIntent().hasExtra("testType") && getIntent().getStringExtra("testType").equalsIgnoreCase("CLASS-WISE")) {
            tvTypeHeader.setText(getIntent().getStringExtra("className"));
            chapterId = "0";
            topicId = "0";
        } else {
            if(getIntent().hasExtra("testType"))
            tvTypeHeader.setText(getIntent().getStringExtra("testType"));
        }

        tvNext.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        initOptions();

    }

    private void showNoDataDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("No Data To Load")
                .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
    }


    private void getQuestions(String cedz, String s, String contentType, String studentId, String chapterId, String classId, String courseId, String subjectId, String topicId, String testType) {
        ApiClient apiClient = new ApiClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            "CEDZ", "" + testCategoryObj.getMultipleSubjects(), "" + courseObj.getClasses().get(0).getSubjects().get(0).getContentType(),
//                    sObj.getStudentId(), chapterId,classId,courseId,subjectId,topicId,testType

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("contentOwner", cedz);
            jsonObject.put("multipleSubjects", s);
            jsonObject.put("questionType", contentType);
            jsonObject.put("studentId", studentId);
            jsonObject.put("testChapter", chapterId);
            jsonObject.put("testClass", classId);
            jsonObject.put("testCourse", courseId);
            jsonObject.put("testSubject", subjectId);
            jsonObject.put("testTopic", topicId);
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

        RequestBody body = RequestBody.create(String.valueOf(jsonObject), JSON);

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
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesList responce - " + jsonResp);

                    runOnUiThread(() -> {
                        try {
                            JSONObject parentjObject = new JSONObject(jsonResp);
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


                                testMFQQueList.addAll(new Gson().fromJson(MFQJSONArray.toString(), new TypeToken<List<PracMFQObj>>() {
                                }.getType()));
                                testQueList.addAll(new Gson().fromJson(MFQFiltered.toString(), new TypeToken<List<PracQuestionObj>>() {
                                }.getType()));

                                if (testQueList.size() > 0) {

                                    for (int i = 0; i < testMFQQueList.size(); i++) {
                                        PracQuestionObj mqo = new PracQuestionObj();
                                        mqo.setQuestionId(testMFQQueList.get(i).getQuestionId());
                                        mqo.setQuestion("Match the Following");
                                        mqo.setQueOptions(testMFQQueList.get(i).getQuestion());
                                        mqo.setQuestionType(testMFQQueList.get(i).getQuestType());
                                        mqo.setExplanation(testMFQQueList.get(i).getExplanation());
                                        mqo.setOption3("NA");
                                        mqo.setOption4("NA");
                                        mqo.setOption1("NA");
                                        mqo.setOption2("NA");
                                        mqo.setCorrectAnswer(testMFQQueList.get(i).getCorrectAnswer());

                                        String cAns = "";
                                        for (int j = 0; j < testMFQQueList.get(i).getQuestion().size(); j++) {
                                            cAns = cAns + testMFQQueList.get(i).getQuestion().get(j).getCorrectAnswer();
                                        }


                                        utils.showLog(TAG, "QuesList MFQ - cAns - " + cAns);
                                        testQueList.add(mqo);
                                    }
                                    utils.showLog(TAG, "QuesList MFQ - " + testMFQQueList.size());
                                    testMFQQueList.clear();
                                    utils.showLog(TAG, "QuesList MFQ - " + testMFQQueList.size());
                                    utils.showLog(TAG, "QuesList MFQFilterd - " + testQueList.size());

//                            startTest();

                                    loadQuestion(0);

//                                startTestTimer();

                                } else {
                                    showNoDataDialogue();
                                }

                            }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                String message = parentjObject.getString(AppConst.MESSAGE);
                                runOnUiThread(() -> {
                                    utils.dismissDialog();
                                    MyUtils.forceLogoutUser(toEdit, PracticeTest.this, message, sh_Pref);
                                });
                            } else {
                                showNoDataDialogue();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });


                }
            }
        });
    }


    private void initOptions() {
        btn = new Button[4];
        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setVisibility(View.VISIBLE);
//            if (question.getQuestionTypeCode().equalsIgnoreCase("MAQ"))
//                btn[i].setTag("Unselected");
            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            btn[i].setOnClickListener(PracticeTest.this);
        }

        for (int i = 0; i < tfbtn.length; i++) {
            tfbtn[i] = findViewById(tfbtn_id[i]);
            tfbtn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            tfbtn[i].setOnClickListener(this);
        }
    }

    public void loadQuestion(int que_index) {
        Log.v(TAG, "queIndex - " + que_index);
        startTestTimer();
        selectedOpt = "";

//    que_index = que_index + 1;
        tvQueNO.setText(MessageFormat.format("Question {0}/{1}", (que_index + 1), testQueList.size()));

        setUnFocusAll();

//        btnCheckans.setText("Check Answer");

        timeSwapBuff = testQueList.get(que_index).getTimetaken();
        Log.v("TIME - ", "timeSwapBuff - " + timeSwapBuff);
//        startCountUpTimer();

        Log.v(TAG, "QueType - " + testQueList.get(que_index).getQuestionType());

        if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MCQ")) {
            llMcq.setVisibility(View.VISIBLE);
            llTf.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llFibans.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            wvQuestion.setVisibility(View.VISIBLE);


            for (int i = 0; i < btn.length; i++) {
                btn[i].setEnabled(true);
            }
            String mainString = "";
            if (testQueList.get(que_index).getOption1().contains("fieldset")) {
                mainString = "<!DOCTYPE html> <html>" + testQueList.get(que_index).getQuestion()
                        + testQueList.get(que_index).getOption1()
                        + testQueList.get(que_index).getOption2()
                        + testQueList.get(que_index).getOption3()
                        + testQueList.get(que_index).getOption4()
                        + "</html>";
            } else {
                mainString = "<!DOCTYPE html> <html>" + testQueList.get(que_index).getQuestion() +
                        "<fieldset><legend>A)</legend>" + testQueList.get(que_index).getOption1() + "</fieldset>"
                        + "<fieldset><legend>B)</legend>" + testQueList.get(que_index).getOption2() + "</fieldset>"
                        + "<fieldset><legend>C)</legend>" + testQueList.get(que_index).getOption3() + "</fieldset>"
                        + "<fieldset><legend>D)</legend>" + testQueList.get(que_index).getOption4() + "</fieldset>"
                        + "</html>";
            }

            mainString = utils.cleanWebString(mainString);
            wvQuestion.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            Log.v(TAG, "QueType - " + mainString);
            Log.v(TAG, "QueType exp- " + testQueList.get(que_index).getExplanation());


        } else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("ITQ")) {
            wvQuestion.setVisibility(View.VISIBLE);
            llITQ.setVisibility(View.VISIBLE);
            llTf.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            llFib.setVisibility(View.GONE);
            llOption.removeAllViews();
            etItq.setText("");

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(que_index).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQuestion.loadData(mainString, "text/html; charset=utf-8", "utf-8");

//            ansArray[que_index] = testQueList.get(que_index).getCorrectAnswer();
            wvQuestion.scrollTo(0, 0);

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


        } else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("TOF")) {
            llMcq.setVisibility(View.GONE);
            llTf.setVisibility(View.VISIBLE);
            llFib.setVisibility(View.GONE);
            llFibans.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);
            wvQuestion.setVisibility(View.VISIBLE);

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(que_index).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQuestion.loadData(mainString, "text/html; charset=utf-8", "utf-8");

        } else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("FIB")) {

            wvQuestion.setVisibility(View.VISIBLE);
            llFib.setVisibility(View.VISIBLE);
            llFibans.setVisibility(View.VISIBLE);
            llFibans.removeAllViews();
            editTextLists.clear();
            llMcq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llITQ.setVisibility(View.GONE);


            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(que_index).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQuestion.loadData(mainString, "text/html; charset=utf-8", "utf-8");

            setFIBLayout(testQueList.get(que_index).getCorrectAnswer(), testQueList.get(que_index).getSelectedAnswer());
        } else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MFQ")) {
            checkNextQuestion();
        }
    }

    void checkNextQuestion() {
        if (++que_index < testQueList.size()) {
            loadQuestion(que_index);
        } else {
            showExitDialog();
        }
    }

    private void setUnFocusAll() {
        for (Button aBtn : btn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setFocusable(false);
            if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ"))
                aBtn.setTag("Unselected");
            aBtn.setTextColor(Color.parseColor("#444444"));

        }

        for (Button aBtn : tfbtn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setFocusable(false);
            aBtn.setTextColor(Color.parseColor("#444444"));

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_a:
                if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[0]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[0]);
                    selectedOpt = getOption(1);
//                    if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("Paragraph")){
////                        selectedOption = question.getParagraphQuestions().get(que_index).getOptions().get(0);
//                    }
//                    else {
//                        selectedOption = question.getOptions().get(0);
//                    }

                }
                break;
            case R.id.btn_b:
                if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[1]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[1]);
                    selectedOpt = getOption(2);
//                    if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("Paragraph")){
////                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(1);
//                    }
//                    else {
//                        selectedOption = question.getOptions().get(1);
//                    }
                }
                break;
            case R.id.btn_c:
                if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[2]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[2]);
                    selectedOpt = getOption(3);
//                    if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("Paragraph")){
////                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(2);
//                    }
//                    else {
//                        selectedOption = question.getOptions().get(2);
//                    }
                }
                break;
            case R.id.btn_d:
                if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[3]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[3]);
                    selectedOpt = getOption(4);
//                    if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("Paragraph")){
////                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(3);
//                    }
//                    else {
//                        selectedOption = question.getOptions().get(3);
//                    }
                }
                break;
            case R.id.btn_true:
                setUnFocusAll();
                setFocus(tfbtn[0]);
                testQueList.get(que_index).setSelectedAnswer("true");
                selectedOpt = "true";
                break;
            case R.id.btn_false:
                setUnFocusAll();
                setFocus(tfbtn[1]);
                testQueList.get(que_index).setSelectedAnswer("false");
                selectedOpt = "false";
                break;
            case R.id.tv_next:
                chronometer.stop();
                if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("FIB")) {

                    String str = "";
                    fibAnswers.clear();

                    Boolean notans = false;
                    Boolean ans = false;
                    String[] arrCorrectAns;

                    for (int i = 0; i < editTextLists.size(); i++) {
                        for (int j = 0; j < editTextLists.get(i).length; j++) {
                            if (editTextLists.get(i)[j].getText().toString().equalsIgnoreCase("")) {
                                str = str + "";
                            } else {
                                str = str + editTextLists.get(i)[j].getText().toString();
                            }
                        }
                        if (str.length() == 0)
                            notans = true;
                        fibAnswers.add(str);
                        Log.v(TAG, " FIB Answer " + str);
                        str = "";


                    }


                    if (notans) {
                        Toast.makeText(PracticeTest.this, "Please Answer the Question", Toast.LENGTH_SHORT).show();

                    } else {
                        showAnswerDialog();
                    }

                } else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("ITQ")) {
                    if (etItq.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "Please Enter some thing", Toast.LENGTH_SHORT).show();
                    } else {
                        showAnswerDialog();
                    }
                } else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ")) {
                    showAnswerDialog();

                } else {
                    if (selectedOpt.isEmpty()) {
                        Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
                    } else
                        showAnswerDialog();
                }
                break;
            case R.id.iv_back:
                showExitDialog();
                break;
        }
    }

    //showing finish dialog
    private void showExitDialog() {
        dialog = new Dialog(PracticeTest.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_answer_explanation);
        if (dialog.isShowing()) dialog.dismiss();
        dialog.show();
        LinearLayout llMain = dialog.findViewById(R.id.ll_main_dialog);
        llMain.setVisibility(View.GONE);
        LinearLayout llLast = dialog.findViewById(R.id.ll_last);
        llLast.setVisibility(View.VISIBLE);
        TextView tvText = dialog.findViewById(R.id.tv_text);
        tvText.setText("Do you really want \nto quit this Practice?");

        TextView tvYes = dialog.findViewById(R.id.tv_yes);
        TextView tvNo = dialog.findViewById(R.id.tv_no);


        tvYes.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        tvNo.setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

    //showing answer dialog
    private void showAnswerDialog() {
        dialog = new Dialog(PracticeTest.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_answer_explanation);
        if (dialog.isShowing()) dialog.dismiss();
        dialog.show();
        CustomWebview wvQuestion = dialog.findViewById(R.id.wv_question);
        CustomWebview wvCorrect = dialog.findViewById(R.id.wv_correct);
        CustomWebview wvWrong = dialog.findViewById(R.id.wv_wrong);
        CustomWebview wvExplanation = dialog.findViewById(R.id.wv_explanation);
        LinearLayout llExp = dialog.findViewById(R.id.ll_exp);
        LinearLayout llMain = dialog.findViewById(R.id.ll_main_dialog);
        LinearLayout llLast = dialog.findViewById(R.id.ll_last);
        LinearLayout llCorrect = dialog.findViewById(R.id.ll_correct);
        LinearLayout llWrong = dialog.findViewById(R.id.ll_wrong);
        TextView tvNoExplanation = dialog.findViewById(R.id.tv_no_explanation);
        TextView tvNext = dialog.findViewById(R.id.tv_next);
        TextView tvTimeTaken = dialog.findViewById(R.id.tv_time_taken);
        tvTimeTaken.setText(chronometer.getText());

        TextView tvYes = dialog.findViewById(R.id.tv_yes);
        TextView tvNo = dialog.findViewById(R.id.tv_no);


        wvQuestion.setText(testQueList.get(que_index).getQuestion());
        llExp.setVisibility(View.VISIBLE);
        wvExplanation.setVisibility(View.GONE);
        tvNoExplanation.setVisibility(View.VISIBLE);
        if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("FIB")) {
            String[] arrCorrectAns;
            Boolean notans = false;
            Boolean ans = false;
//            tvCans.setText("CorrectAnswer : " + testQueList.get(que_index).getCorrectAnswer());
            if (testQueList.get(que_index).getCorrectAnswer().contains(",")) {

                arrCorrectAns = testQueList.get(que_index).getCorrectAnswer().split(", ");
            } else {
                arrCorrectAns = new String[1];
                arrCorrectAns[0] = testQueList.get(que_index).getCorrectAnswer();
            }

            for (int i = 0; i < arrCorrectAns.length; i++) {
                ans = arrCorrectAns[i].trim().equalsIgnoreCase(fibAnswers.get(fibAnswers.size() - (i + 1)).trim());
                if (!ans) break;
            }

            if (ans) {
                wvCorrect.setText(testQueList.get(que_index).getCorrectAnswer());
                llWrong.setVisibility(View.GONE);
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "correctAnswer", "1");
            } else {
                StringBuilder wans = new StringBuilder();
                for (String s : fibAnswers) {
                    if (!wans.toString().isEmpty())
                        wans.append(",").append(s);
                    else wans.append(s);
                }
                wvWrong.setText(wans.toString());
                wvCorrect.setText(testQueList.get(que_index).getCorrectAnswer());
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "wrongAnswer", "1");
            }

        }
        else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("ITQ")) {
            if (Float.parseFloat(etItq.getText().toString().trim()) == Float.parseFloat(testQueList.get(que_index).getCorrectAnswer())) {
                wvCorrect.setText(etItq.getText().toString().trim());
                llWrong.setVisibility(View.GONE);
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "correctAnswer", "1");
                if (!testQueList.get(que_index).getExplanation().equalsIgnoreCase("")) {
                    wvExplanation.setVisibility(View.VISIBLE);
                    tvNoExplanation.setVisibility(View.GONE);
                    wvExplanation.setText(testQueList.get(que_index).getExplanation());
                }
            } else {
                wvWrong.setText(etItq.getText().toString().trim());
                wvCorrect.setText(testQueList.get(que_index).getCorrectAnswer());
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "wrongAnswer", "1");
            }
        }
        else if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("TOF")) {
            if (selectedOpt.equalsIgnoreCase(testQueList.get(que_index).getCorrectAnswer())) {
                wvCorrect.setText(selectedOpt);
                llWrong.setVisibility(View.GONE);
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "correctAnswer", "1");
                if (!testQueList.get(que_index).getExplanation().equalsIgnoreCase("")) {
                    wvExplanation.setVisibility(View.VISIBLE);
                    tvNoExplanation.setVisibility(View.GONE);
                    wvExplanation.setText(testQueList.get(que_index).getExplanation());
                }
            } else {
                wvWrong.setText(selectedOpt);
                wvCorrect.setText(testQueList.get(que_index).getCorrectAnswer());
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "wrongAnswer", "1");
            }
        }
        else {
            if (selectedOpt.equalsIgnoreCase(testQueList.get(que_index).getCorrectAnswer())) {
                wvCorrect.setText(getOriginalOption(selectedOpt));
                llWrong.setVisibility(View.GONE);
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "correctAnswer", "1");
                if (!testQueList.get(que_index).getExplanation().equalsIgnoreCase("")) {
                    wvExplanation.setVisibility(View.VISIBLE);
                    tvNoExplanation.setVisibility(View.GONE);
                    wvExplanation.setText(testQueList.get(que_index).getExplanation());
                }
            } else {
                wvWrong.setText(getOriginalOption(selectedOpt));
                wvCorrect.setText(getOriginalOption(testQueList.get(que_index).getCorrectAnswer()));
//                postQuestion("" + testQueList.get(que_index).getQuestionId(), "" + ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000), topicId, "wrongAnswer", "1");
            }
        }

        tvNext.setOnClickListener(view -> {
            que_index = que_index + 1;
            if (que_index < testQueList.size()) {
                loadQuestion(que_index);
                dialog.dismiss();
            } else {
                llMain.setVisibility(View.GONE);
                llLast.setVisibility(View.VISIBLE);
            }

        });

        tvYes.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });

        tvNo.setOnClickListener(view -> {
            llMain.setVisibility(View.VISIBLE);
            llLast.setVisibility(View.GONE);
        });


    }


    private String getOriginalOption(String selectedOpt) {
        String result = "";
        if (selectedOpt.equalsIgnoreCase("option1")) {
            result = testQueList.get(que_index).getOption1();
        } else if (selectedOpt.equalsIgnoreCase("option2")) {
            result = testQueList.get(que_index).getOption2();
        } else if (selectedOpt.equalsIgnoreCase("option3")) {
            result = testQueList.get(que_index).getOption3();
        } else if (selectedOpt.equalsIgnoreCase("option4")) {
            result = testQueList.get(que_index).getOption4();
        }
        return result;
    }

    private void setFocus(Button btn_focus) {
        if (testQueList.get(que_index).getQuestionType().equalsIgnoreCase("MAQ")) {
            if (btn_focus.getTag().equals("selected")) {
                btn_focus.setBackgroundResource(R.drawable.practice_btn_opt);
                btn_focus.setFocusable(true);
                btn_focus.setTextColor(Color.parseColor("#ffffff"));
            } else {
                btn_focus.setBackgroundResource(R.drawable.practice_btn_optsel);
                btn_focus.setFocusable(false);
                btn_focus.setTextColor(Color.parseColor("#444444"));
            }
        } else {
            btn_focus.setBackgroundResource(R.drawable.practice_btn_opt);
            btn_focus.setFocusable(true);
            btn_focus.setTextColor(Color.parseColor("#ffffff"));
        }
    }


    private String getOption(int i) {
        String option = "";
        switch (i) {
            case 1:
                option = "option1";
                break;
            case 2:
                option = "option2";
                break;
            case 3:
                option = "option3";
                break;
            case 4:
                option = "option4";
                break;

        }
        return option;
    }


    private void setFIBLayout(String cAnswer, String selAns) {
        String[] fibans_words;
        cAnswer = cAnswer.replaceAll("\\s", "");
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
        llFibans.addView(linear_answer);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}