/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.OnlineQues;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class DescriptiveTestReview extends AppCompatActivity {

    private static final String TAG = "SriRam -" + DescriptiveTestReview.class.getName();

    MyUtils utils = new MyUtils();


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_queno)
    TextView tvQueno;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.wv_que)
    WebView wvQue;
    @BindView(R.id.tv_queresult)
    TextView tvQueresult;
    @BindView(R.id.rv_quelist)
    RecyclerView rvQuelist;
    @BindView(R.id.hidden_panel)
    RelativeLayout hiddenPanel;
    @BindView(R.id.wv_quereview)
    WebView wvQuereview;
    @BindView(R.id.wv_option)
    WebView wvOption;
    @BindView(R.id.wv_explanation)
    WebView wvExplanation;
    @BindView(R.id.explanation_panel)
    RelativeLayout explanationPanel;
    @BindView(R.id.ll_mcqOptions)
    LinearLayout llMcqOptions;
    @BindView(R.id.ll_tf)
    LinearLayout llTf;
    @BindView(R.id.ll_mfqOptions)
    LinearLayout llMfqOptions;
    @BindView(R.id.ll_mfqResponce)
    LinearLayout llMfqResponce;
    @BindView(R.id.ll_mfq)
    ScrollView llMfq;
    @BindView(R.id.tv_mfqResponce)
    TextView tvMfqResponce;
    @BindView(R.id.tv_mfqResult)
    TextView llMfqResult;
    @BindView(R.id.ll_que)
    LinearLayout llMFQQue;


    String testId;
    int studentId;
    @BindView(R.id.img_prev)
    ImageView imgPrev;
    @BindView(R.id.img_next)
    ImageView imgNext;
    int queNo = 0;
    RecyclerView.Adapter adapter = null;
    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    private Button[] btn = new Button[4];
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};
    private Button[] tfbtn = new Button[2];
    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};

    List<OnlineQuestionObj> testQuePaperList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ot_student_online_test_review);
        ButterKnife.bind(this);

        init();

        String ss = getIntent().getStringExtra("objList");
        testQueList = new Gson().fromJson(ss, new TypeToken<List<OnlineQuestionObj2>>() {
        }.getType());

        if (testQueList.size() > 0) {
            loadQuestion(queNo);
        } else
            showNoDataDialogue();

//        getOnlineTestQuePaper(getIntent().getStringExtra("testFilePath"));

        wvQue.setOnLongClickListener(v -> {
            // For final release of your app, comment the toast notification
            Toast.makeText(DescriptiveTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
            return true;
        });

        wvExplanation.setOnLongClickListener(v -> {
            // For final release of your app, comment the toast notification
            Toast.makeText(DescriptiveTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
            return true;
        });

        wvQuereview.setOnLongClickListener(v -> {
            // For final release of your app, comment the toast notification
            Toast.makeText(DescriptiveTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
            return true;
        });
        wvOption.setOnLongClickListener(v -> {
            // For final release of your app, comment the toast notification
            Toast.makeText(DescriptiveTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
            return true;
        });

        /* loading question on recyclerview item touch */
        rvQuelist.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {

                    int position = rv.getChildAdapterPosition(child);
                    queNo = position;
                    Log.v("Clicked position", "Position - " + position + " Index - " + queNo);
                    loadQuestion(queNo);

                    slideUpDown();


                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /* Initialization */
    private void init() {
        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
        }
        for (int i = 0; i < tfbtn.length; i++) {
            tfbtn[i] = findViewById(tfbtn_id[i]);
            tfbtn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
        }


        tvTitle.setText(getIntent().getStringExtra("testName"));


        testId = getIntent().getStringExtra("testId");
        studentId = getIntent().getIntExtra("studentId",0);
    }

    @OnClick({R.id.img_back, R.id.img_list, R.id.img_prev, R.id.tv_explain, R.id.img_next, R.id.img_close, R.id.img_expclose})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_list:
                slideUpDown();
                break;
            case R.id.img_prev:
                if (--queNo < 0) {
                    queNo = queNo + 1;
                } else {
                    Log.v(TAG, "next ques_no - " + queNo);
                    Log.v(TAG, "next test_que_list - " + testQueList.size());
                    loadQuestion(queNo);
                }
                break;
            case R.id.tv_explain:
                showExplanation();
                break;
            case R.id.img_next:
                if (++queNo < testQueList.size()) {
                    Log.v(TAG, "next ques_no - " + queNo);
                    Log.v(TAG, "next test_que_list - " + testQueList.size());
                    loadQuestion(queNo);
                } else {
                    queNo = queNo - 1;

                }
                break;
            case R.id.img_close:
                slideUpDown();
                break;
            case R.id.img_expclose:
                showExplanation();
                break;
        }
    }

    /* Loading Question using question number from testQueList  */
    private void loadQuestion(int queNo) {


        if (queNo == 0) {
            imgPrev.setVisibility(View.INVISIBLE);
            imgPrev.setEnabled(false);
        } else {
            imgPrev.setVisibility(View.VISIBLE);
            imgPrev.setEnabled(true);
        }

        if (queNo == testQueList.size() - 1) {
            imgNext.setVisibility(View.INVISIBLE);
            imgNext.setEnabled(false);
        } else {
            imgNext.setVisibility(View.VISIBLE);
            imgNext.setEnabled(true);
        }

        tvQueno.setText("Question " + (queNo + 1));
        tvTime.setText("" + testQueList.get(queNo).getTimeTaken() + " Secs ");

        if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MCQ")) {

            wvQue.setVisibility(View.VISIBLE);
            llTf.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.VISIBLE);

            markAnswer();

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion()
                    + testQueList.get(queNo).getOption1()
                    + testQueList.get(queNo).getOption2()
                    + testQueList.get(queNo).getOption3()
                    + testQueList.get(queNo).getOption4()
                    + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQue.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("TOF")) {
            llTf.setVisibility(View.VISIBLE);
            wvQue.setVisibility(View.VISIBLE);

            llMcqOptions.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.GONE);

            markAnswer();

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQue.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {
            llMfq.setVisibility(View.VISIBLE);
            llMfqOptions.setVisibility(View.VISIBLE);
            llMFQQue.removeAllViews();

            llTf.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.GONE);
            wvQue.setVisibility(View.GONE);


            markAnswer();

            setMFQlayout(testQueList.get(queNo).getQuestions());
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("FIB")) {
            llMfqOptions.setVisibility(View.VISIBLE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.GONE);
            markAnswer();

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQue.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("ITQ")) {
            llMfqOptions.setVisibility(View.VISIBLE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.GONE);
            markAnswer();
            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQue.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        }
    }

    /* Creating MFQ Layout dynamically */
    private void setMFQlayout(List<OnlineQues> quesListObj) {

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
            llMFQQue.addView(view);
        }

        int num_of_rows = 0;
        int q_num = 1;

        if (quesListObj.size() % 3 == 0) {
            num_of_rows = quesListObj.size() / 3;
        } else {
            num_of_rows = (quesListObj.size() / 3) + 1;
        }
        Log.v(TAG, "number of rows " + num_of_rows);


    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 'A' - 1)) : null;
    }

    /* Mark Answer Based on User Selected Answer */
    private void markAnswer() {
        setUnFocusAll();

        Log.v(TAG, "markAnswer tvQueNo -  " + queNo);
        Log.v(TAG, "markAnswer ganswer -  " + testQueList.get(queNo).getSelectedAnswer());
        Log.v(TAG, "markAnswer answer -  " + testQueList.get(queNo).getCorrectAnswer());

        if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("")) {
            tvQueresult.setText(" UnAnswered");
            tvQueresult.setTextColor(getResources().getColor(R.color.skiped_ans));
        } else {
            if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase(testQueList.get(queNo).getSelectedAnswer())) {
                tvQueresult.setText("Correct Answered");
                tvQueresult.setTextColor(getResources().getColor(R.color.correct_ans));
            } else {
                tvQueresult.setText("Wrong Answered");
                tvQueresult.setTextColor(getResources().getColor(R.color.wrong_ans));
            }
        }

        if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MCQ") || testQueList.get(queNo).getQuestType().equalsIgnoreCase("TOF")) {
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option1")) {
                btn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option2")) {
                btn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option3")) {
                btn[2].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option4")) {
                btn[3].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("true")) {
                tfbtn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("false")) {
                tfbtn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
            }

            if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option1")) {
                btn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option2")) {
                btn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option3")) {
                btn[2].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option4")) {
                btn[3].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("true")) {
                tfbtn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("false")) {
                tfbtn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
            }

        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());

            String ans = "";
            for (int i = 0; i < testQueList.get(queNo).getQuestions().size(); i++) {
                ans = ans + "" + testQueList.get(queNo).getQuestions().get(i).getCorrectAnswer();
            }
            llMfqResult.setText(ans);

            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase(""))
                llMfqResponce.setVisibility(View.INVISIBLE);

        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("FIB")) {
            Log.v(TAG, "Responce -" + testQueList.get(queNo).getSelectedAnswer());
            Log.v(TAG, "Responce -" + testQueList.get(queNo).getCorrectAnswer());
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("blank"))
                llMfqResponce.setVisibility(View.INVISIBLE);
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("ITQ")) {
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("blank")) {
                llMfqResponce.setVisibility(View.INVISIBLE);
            } else {
                llMfqResponce.setVisibility(View.VISIBLE);
            }
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
        }
    }

    /* UnSelecting All Buttons */
    private void setUnFocusAll() {
        for (Button aBtn : btn) {
            aBtn.setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
        }
        for (Button aBtn : tfbtn) {
            aBtn.setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);

        }
    }

    /* Sliding Questions List */
    private void slideUpDown() {
        if (!isPanelShown()) {
            // Show the panel
            adapter = new TestReviewListAdapter(this, testQueList);
            rvQuelist.setLayoutManager(new LinearLayoutManager(this));
            rvQuelist.setAdapter(adapter);
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);

            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            hiddenPanel.bringToFront();
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

    /* Showing Explanation View Panel */
    private void showExplanation() {
        if (!isExpPanelShown()) {
            // Show the panel
            Log.v(TAG, "Explanation - " + testQueList.get(queNo).getExplanation().length());
            wvQuereview.loadData(utils.cleanWebString(testQueList.get(queNo).getQuestion()), "text/html; charset=utf-8", "utf-8");

            String optionString = null;
            if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option1")) {

                optionString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getOption1() + "</html>";
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option2")) {

                optionString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getOption2() + "</html>";
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option3")) {

                optionString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getOption3() + "</html>";
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option4")) {

                optionString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getOption4() + "</html>";
            } else {
                optionString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getCorrectAnswer() + "</html>";
            }

            optionString = utils.cleanWebString(optionString);


            wvOption.loadData(optionString, "text/html; charset=utf-8", "utf-8");

            if (testQueList.get(queNo).getExplanation().length() > 0) {
                wvExplanation.loadData(utils.cleanWebString(testQueList.get(queNo).getExplanation()), "text/html; charset=utf-8", "utf-8");
            } else
                wvExplanation.loadData("No Explanation", "text/html; charset=utf-8", "utf-8");

            wvExplanation.scrollTo(0, 0);
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);

            explanationPanel.startAnimation(bottomUp);
            explanationPanel.setVisibility(View.VISIBLE);
            explanationPanel.bringToFront();
        } else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

            explanationPanel.startAnimation(bottomDown);
            explanationPanel.setVisibility(View.GONE);

        }
    }

    private boolean isExpPanelShown() {
        return explanationPanel.getVisibility() == View.VISIBLE;
    }


    private void showNoDataDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Unable To Load")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (explanationPanel.getVisibility() == View.VISIBLE) {
            showExplanation();
        } else if (hiddenPanel.getVisibility() == View.VISIBLE) {
            slideUpDown();
        } else {
            new AlertDialog.Builder(DescriptiveTestReview.this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.alert_finreview))
                    .setPositiveButton("Yes", (dialog, which) -> {
//                            super.onBackPressed();

                        finish();

                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // do nothing
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    /* Get Test Question Paper */
//    void getOnlineTestQuePaper(String... strings){
//        utils.showLoader(DescriptiveTestReview.this);
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//        utils.showLog(TAG, "TestGetQue Url - " + strings[0]);
//
//        Request request = new Request.Builder()
//                .url(strings[0])
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                runOnUiThread(() -> utils.dismissDialog());
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (response.body()!=null){
//                    String jsonResp = response.body().string();
//
//                    utils.showLog(TAG, "QuesPaperList responce - " + jsonResp);
//
//                    try {
//                        JSONObject ParentjObject = new JSONObject(jsonResp);
//                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//
//                            JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");
//
//                            testQuePaperList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj>>() {
//                            }.getType()));
//
//                            if (testQuePaperList.size() > 0) {
//                                utils.showLog(TAG, "QuePaperList - " + testQuePaperList.size());
//                                runOnUiThread(() -> {
//                                    utils.dismissDialog();
//                                    getDBTestQuestions();
//                                });
//
////                            new GetOnlineTestQuePaperDetails().execute(getIntent().getStringExtra("studentTestFilePath"));
//                            } else {
//                                runOnUiThread(() ->  showNoDataDialogue());
//                            }
//
//                        } else {
//                            runOnUiThread(() -> showNoDataDialogue() );
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                runOnUiThread(() -> utils.dismissDialog());
//            }
//        });
//    }



    /* Get Test Answers From Database  */
//    void getDBTestQuestions() {
//        utils.showLoader(DescriptiveTestReview.this);
//
//        String jsonResp = "null";
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//        Log.v(TAG, "StudentDBTestQuestions Url - " + AppUrls.GETSTATUSBYID + "?examDocId=" +getIntent().getStringExtra("studentTestFilePath"));
//
//        Request request = new Request.Builder()
//                .url(AppUrls.GETSTATUSBYID + "?examDocId=" +getIntent().getStringExtra("studentTestFilePath"))
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                runOnUiThread(() -> utils.dismissDialog());
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (response.body() != null) {
//                    try {
//                        String responce = response.body().string();
//                        Log.v(TAG, "DBTestQuestions response - " + responce);
//                        JSONObject ParentjObject = new JSONObject(responce);
//                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
//                            JSONObject jsonObject = ParentjObject.getJSONObject("result");
////                            insertId = jsonObject.getString("_id");
//                            JSONArray jsonArr = jsonObject.getJSONArray("testCategories");
//                            testQueList.clear();
//                            testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj>>() {
//                            }.getType()));
//
//                            Log.v(TAG, "testQuePaperdetailsList - " + testQueList.size());
//
//                            runOnUiThread(() -> MergeQuestions());
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                runOnUiThread(() -> utils.dismissDialog());
//            }
//        });
//
//
//    }

    /* Merging Questions and Answers based on Question Id */
    private void MergeQuestions() {
        if (testQuePaperList.size() > 0) {
            for (int i = 0; i < testQuePaperList.size(); i++) {
                for (int j = 0; j < testQueList.size(); j++) {
                    if (testQuePaperList.get(i).getQuestionId().equals(testQueList.get(j).getQuestionId())) {
                        testQueList.get(j).setQuestion(testQuePaperList.get(i).getQuestion());
                        testQueList.get(j).setSubjectGroup(testQuePaperList.get(i).getSubjectGroup());
                        testQueList.get(j).setQuestType(testQuePaperList.get(i).getQuestType());
                        testQueList.get(j).setSubjectId(testQuePaperList.get(i).getSubjectId());
                        testQueList.get(j).setOption1(testQuePaperList.get(i).getOption1());
                        testQueList.get(j).setOption2(testQuePaperList.get(i).getOption2());
                        testQueList.get(j).setOption3(testQuePaperList.get(i).getOption3());
                        testQueList.get(j).setOption4(testQuePaperList.get(i).getOption4());
                        testQueList.get(j).setCorrectAnswer(testQuePaperList.get(i).getCorrectAnswer());
                        testQueList.get(j).setSubjectName(testQuePaperList.get(i).getSubjectName());
                        testQueList.get(j).setExplanation(testQuePaperList.get(i).getExplanation());
                    }
                }
            }
        }
        if (testQueList.size() > 0) {
            loadQuestion(queNo);
        } else
            showNoDataDialogue();
    }

    /* Review List Adapter */
    public class TestReviewListAdapter extends RecyclerView.Adapter<TestReviewListAdapter.ViewHolder> {
        private List<OnlineQuestionObj2> queslist;
        private Context _context;


        public TestReviewListAdapter(Context _context, List<OnlineQuestionObj2> queslist) {
            this.queslist = queslist;
            this._context = _context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_test_explist, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("")) {

                viewHolder.gans_tv.setText("Skipped");
                viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.skiped_ans));

            } else {
                if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase(queslist.get(i).getCorrectAnswer())) {
                    viewHolder.gans_tv.setText("Correct Answer");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.correct_ans));
                } else {
                    viewHolder.gans_tv.setText("Wrong Answer");
                    viewHolder.gans_tv.setTextColor(_context.getResources().getColor(R.color.wrong_ans));
                }
            }


            viewHolder.sno_tv.setText("Question " + (i + 1));

        }

        @Override
        public int getItemCount() {
            return queslist.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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
}
