/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.MockMFQQues;
import myschoolapp.com.gsnedutech.Models.MockQuestionObj;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class MockTestReview extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SriRam -" + MockTestReview.class.getName();
    MyUtils utils = new MyUtils();

    List<MockQuestionObj> testQueList = new ArrayList<>();
    @BindView(R.id.iv_back)
    ImageView imgBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_list)
    ImageView imgList;
    @BindView(R.id.tv_queno)
    TextView tvQueno;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.wv_que)
    WebView wvQue;
    @BindView(R.id.tv_queresult)
    TextView tvQueresult;
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
    @BindView(R.id.tv_explain)
    TextView tvExplain;
    @BindView(R.id.img_prev)
    ImageView imgPrev;
    @BindView(R.id.img_next)
    ImageView imgNext;
    @BindView(R.id.img_close)
    ImageView imgClose;
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

    @BindView(R.id.rv_que_no_list)
    RecyclerView rvQueNoList;

    @BindView(R.id.tv_subName)
    TextView tvSubName;

    private Button[] btn = new Button[4];
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    private Button[] tfbtn = new Button[2];
    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};

    int queNo = 0;

    RecyclerView.Adapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mock_test_review);
        ButterKnife.bind(this);

        init();

        getQuestion();

        wvQue.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(MockTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        rvQueNoList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                    utils.showLog("Clicked position", "Position - " + position + " Index - " + queNo);
                    loadQuestion(queNo);

//                    slideUpDown();


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

    private void init() {

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.img_list).setOnClickListener(this);
        findViewById(R.id.img_expclose).setOnClickListener(this);
        findViewById(R.id.img_close).setOnClickListener(this);
        findViewById(R.id.img_prev).setOnClickListener(this);
        findViewById(R.id.img_next).setOnClickListener(this);

        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setBackgroundResource(R.drawable.practice_btn_optsel);
            btn[i].setTextColor(Color.parseColor("#9b9b9b"));
        }

        for (int i = 0; i < tfbtn.length; i++) {
            tfbtn[i] = findViewById(tfbtn_id[i]);
            tfbtn[i].setBackgroundResource(R.drawable.practice_btn_optsel);
            tfbtn[i].setTextColor(Color.parseColor("#9b9b9b"));
        }
        tvExplain.setOnClickListener(this);

        tvTitle.setText(getIntent().getStringExtra("testTitle"));

    }

    private void getQuestion() {

        BufferedReader reader;
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                utils.showLog("sriram ", "QueArray - " + getExternalFilesDir(null)+ getString(R.string.path_testjson));
                reader = new BufferedReader(new FileReader(getExternalFilesDir(null) + getString(R.string.path_testjson)));
            }
            else {
                utils.showLog("sriram ", "QueArray - " + Environment.getExternalStorageDirectory() + getString(R.string.path_testjson));
                reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + getString(R.string.path_testjson)));
            }
            Type listType = new TypeToken<List<MockQuestionObj>>() {
            }.getType();

            testQueList = new Gson().fromJson(reader, listType);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        utils.showLog("sriram ", "testQueList - " + testQueList.size());

        if (testQueList.size() > 0) {
            adapter = new QuesionNoAdapter(this, testQueList);
            rvQueNoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
            rvQueNoList.setAdapter(adapter);
            loadQuestion(queNo);
        } else {
            Toast.makeText(this, "Unable to show the Review", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

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
        tvTime.setText("" + TimeUnit.MILLISECONDS.toSeconds(testQueList.get(queNo).getTimetaken()) + " Secs ");
        tvSubName.setText(testQueList.get(queNo).getSubjectName());

        if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MCQ")) {
            llMcqOptions.setVisibility(View.VISIBLE);
            wvQue.setVisibility(View.VISIBLE);

            llTf.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.GONE);

            markAnswer();

            StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() +
                    "<fieldset><legend>A)</legend>" + testQueList.get(queNo).getOption1() + "</fieldset>"
                    + "<fieldset><legend>B)</legend>" + testQueList.get(queNo).getOption2() + "</fieldset>"
                    + "<fieldset><legend>C)</legend>" + testQueList.get(queNo).getOption3() + "</fieldset>"
                    + "<fieldset><legend>D)</legend>" + testQueList.get(queNo).getOption4() + "</fieldset>"
                    + "</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            wvQue.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("TOF")) {
            llTf.setVisibility(View.VISIBLE);
            wvQue.setVisibility(View.VISIBLE);

            llMcqOptions.setVisibility(View.GONE);
            llMfq.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.GONE);

            markAnswer();

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            wvQue.loadData(utils.cleanWebString(mainString), "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {
            llMfq.setVisibility(View.VISIBLE);
            llMfqOptions.setVisibility(View.VISIBLE);
            llMFQQue.removeAllViews();

            llTf.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.GONE);
            wvQue.setVisibility(View.GONE);


            markAnswer();

            setMFQlayout(testQueList.get(queNo).getQueOptions());
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("FIB")) {
            llMfqOptions.setVisibility(View.VISIBLE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.GONE);
            markAnswer();

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            wvQue.loadData(utils.cleanWebString(mainString), "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        }else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("ITQ")){
            llMfqOptions.setVisibility(View.VISIBLE);
            llMfq.setVisibility(View.GONE);
            llTf.setVisibility(View.GONE);
            llMcqOptions.setVisibility(View.GONE);
            markAnswer();
            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            wvQue.loadData(utils.cleanWebString(mainString), "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        }
    }

    boolean allCharactersComma(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++)
            if (s.charAt(i) != ',')
                return false;

        return true;
    }

    boolean allCharactersSame(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++)
            if (s.charAt(i) != 'X')
                return false;

        return true;
    }

    private void markAnswer() {
        setUnFocusAll();

        utils.showLog(TAG, "markAnswer tvQueNo -  " + queNo);
        utils.showLog(TAG, "markAnswer ganswer -  " + testQueList.get(queNo).getSelectedAnswer());
        utils.showLog(TAG, "markAnswer answer -  " + testQueList.get(queNo).getCorrectAnswer());

        if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("blank") || allCharactersComma(testQueList.get(queNo).getSelectedAnswer()) || allCharactersSame(testQueList.get(queNo).getSelectedAnswer()) || testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("")) {
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
                btn[0].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option2")) {
                btn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
                btn[1].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option3")) {
                btn[2].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
                btn[2].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("option4")) {
                btn[3].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
                btn[3].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("true")) {
                tfbtn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
                tfbtn[0].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("false")) {
                tfbtn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_wrong);
                tfbtn[1].setTextColor(Color.parseColor("#ffffff"));
            }

            if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option1")) {
                btn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
                btn[0].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option2")) {
                btn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
                btn[1].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option3")) {
                btn[2].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
                btn[2].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("option4")) {
                btn[3].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
                btn[3].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("true")) {
                tfbtn[0].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
                tfbtn[0].setTextColor(Color.parseColor("#ffffff"));
            } else if (testQueList.get(queNo).getCorrectAnswer().equalsIgnoreCase("false")) {
                tfbtn[1].setBackgroundResource(R.drawable.btn_cust_test_opt_correct);
                tfbtn[1].setTextColor(Color.parseColor("#ffffff"));
            }

        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());

            String ans = "";
            for (int i = 0; i < testQueList.get(queNo).getQueOptions().size(); i++) {
                ans = ans + "" + testQueList.get(queNo).getQueOptions().get(i).getCorrectAnswer();
            }
            llMfqResult.setText(ans);

            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("blank"))
                llMfqResponce.setVisibility(View.INVISIBLE);
            else
                llMfqResponce.setVisibility(View.VISIBLE);

        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("FIB")) {
            utils.showLog(TAG, "Responce -" + testQueList.get(queNo).getSelectedAnswer());
            utils.showLog(TAG, "Responce -" + testQueList.get(queNo).getCorrectAnswer());
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("blank"))
                llMfqResponce.setVisibility(View.INVISIBLE);
            else
                llMfqResponce.setVisibility(View.VISIBLE);
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
        }else  if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("ITQ")) {
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("blank"))
            {
                llMfqResponce.setVisibility(View.INVISIBLE);
            }else{
                llMfqResponce.setVisibility(View.VISIBLE);
            }
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
        }
    }

    private void setUnFocusAll() {
        for (Button aBtn : btn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setTextColor(Color.parseColor("#9b9b9b"));

        }

        for (Button aBtn : tfbtn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setTextColor(Color.parseColor("#9b9b9b"));

        }
    }

    private void setMFQlayout(List<MockMFQQues> quesListObj) {

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
        utils.showLog(TAG, "number of rows " + num_of_rows);


    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 'A' - 1)) : null;
    }



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


    private void showExplanation() {
        if (!isExpPanelShown()) {
            // Show the panel
            utils.showLog(TAG, "Explanation - " + testQueList.get(queNo).getExplanation().length());
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

            if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {
                String ans = "";
                for (int i = 0; i < testQueList.get(queNo).getQueOptions().size(); i++) {
                    ans = ans + "" + testQueList.get(queNo).getQueOptions().get(i).getCorrectAnswer();
                }
                optionString = "<!DOCTYPE html> <html>" + ans + "</html>";
            }

            wvOption.loadData(utils.cleanWebString(optionString), "text/html; charset=utf-8", "utf-8");

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

    @Override
    public void onBackPressed() {
        if (explanationPanel.getVisibility() == View.VISIBLE) {
            showExplanation();
        } else if (hiddenPanel.getVisibility() == View.VISIBLE) {
            slideUpDown();
        } else {
            new AlertDialog.Builder(MockTestReview.this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.alert_finreview))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                            super.onBackPressed();

                            finish();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.img_list:
                slideUpDown();
                break;
            case R.id.img_close:
                slideUpDown();
                break;
            case R.id.img_expclose:
                showExplanation();
                break;
            case R.id.img_prev:
                if (--queNo < 0) {
                    queNo = queNo + 1;
                } else {
                    utils.showLog(TAG, "next ques_no - " + queNo);
                    utils.showLog(TAG, "next test_que_list - " + testQueList.size());
                    loadQuestion(queNo);
                }
                break;
            case R.id.tv_explain:
                showExplanation();
                break;
            case R.id.img_next:
                if (++queNo < testQueList.size()) {
                    utils.showLog(TAG, "next ques_no - " + queNo);
                    utils.showLog(TAG, "next test_que_list - " + testQueList.size());
                    loadQuestion(queNo);
                } else {
                    queNo = queNo - 1;

                }
                break;
        }

    }

    public class TestReviewListAdapter extends RecyclerView.Adapter<TestReviewListAdapter.ViewHolder> {
        private List<MockQuestionObj> queslist;
        private Context _context;
        private String test_type;


        public TestReviewListAdapter(Context _context, List<MockQuestionObj> queslist) {
            this.queslist = queslist;
            this._context = _context;
        }

        @Override
        public TestReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_test_explist, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TestReviewListAdapter.ViewHolder viewHolder, int i) {

            if (queslist.get(i).getSelectedAnswer().equalsIgnoreCase("blank")) {

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


    class QuesionNoAdapter extends RecyclerView.Adapter<QuesionNoAdapter.ViewHolder>{

        private List<MockQuestionObj> queslist;
        private Context _context;

        public QuesionNoAdapter(Context _context, List<MockQuestionObj> queslist) {
            this.queslist = queslist;
            this._context = _context;
        }

        @NonNull
        @Override
        public QuesionNoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuesionNoAdapter.ViewHolder(LayoutInflater.from(MockTestReview.this).inflate(R.layout.item_question_number,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull QuesionNoAdapter.ViewHolder holder, int position) {

            if (queslist.get(position).getSelectedAnswer().equalsIgnoreCase("blank") || allCharactersComma(queslist.get(position).getSelectedAnswer()) || allCharactersSame(queslist.get(position).getSelectedAnswer()) || queslist.get(position).getSelectedAnswer().equalsIgnoreCase("")) {
                holder.llItemview.setBackgroundResource(R.drawable.bg_grey_circle_border);
                holder.tvQueNo.setTextColor(Color.parseColor("#40000000"));
            }
            else {

                if (queslist.get(position).getSelectedAnswer().equalsIgnoreCase(queslist.get(position).getCorrectAnswer())) {
                    holder.llItemview.setBackgroundResource(R.drawable.bg_review_circle_correct);
                    holder.tvQueNo.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    holder.llItemview.setBackgroundResource(R.drawable.bg_review_circle_wrong);
                    holder.tvQueNo.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            holder.tvQueNo.setText(""+(position+1));

        }

        @Override
        public int getItemCount() {
            return queslist.size();
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

}
