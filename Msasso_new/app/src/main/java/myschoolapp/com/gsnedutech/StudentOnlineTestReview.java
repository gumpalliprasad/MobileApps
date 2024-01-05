/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.OnlineQues;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj2;
import myschoolapp.com.gsnedutech.Models.OnlineQuestionParagraphDetails;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StudentOnlineTestReview extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + StudentOnlineTestReview.class.getName();

    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_queno)
    TextView tvQueno;
    @BindView(R.id.tv_pshow)
    TextView tvPshow;
    @BindView(R.id.tv_sec)
    TextView tvSecName;
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
    //    @BindView(R.id.ll_mcqOptions)
//    LinearLayout llMcqOptions;
//    @BindView(R.id.ll_tf)
//    LinearLayout llTf;
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
    @BindView(R.id.tv_grace)
    TextView tvGrace;

    String testId, studentId;
    @BindView(R.id.img_prev)
    ImageView imgPrev;
    @BindView(R.id.img_next)
    ImageView imgNext;
    @BindView(R.id.rv_que_no_list)
    RecyclerView rvQueNoList;

    @BindView(R.id.tv_subName)
    TextView tvSubName;

    String fileCorrectMarks, fileWrongMarks;
    SharedPreferences sh_Pref;

    int queNo = 0;
    RecyclerView.Adapter adapter = null;
    List<OnlineQuestionObj2> testQueList = new ArrayList<>();
    List<OnlineQuestionParagraphDetails> paragraphDetailsList = new ArrayList<>();


//    private Button[] btn = new Button[4];
//    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};
//    private Button[] tfbtn = new Button[2];
//    private int[] tfbtn_id = {R.id.btn_true, R.id.btn_false};

    List<OnlineQuestionObj2> testQuePaperList = new ArrayList<>();
    StudentOnlineTestObj studentTestObj;

    String paragraphString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_online_test_review);
        ButterKnife.bind(this);

        init();

//        new GetOnlineTestQuestions().execute();


        wvQue.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(StudentOnlineTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
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

        wvExplanation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(StudentOnlineTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        wvQuereview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(StudentOnlineTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        wvOption.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(StudentOnlineTestReview.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

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

        tvPshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(StudentOnlineTestReview.this);
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
                dialog.findViewById(R.id.tv_paraTitle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
            utils.alertDialog(1, StudentOnlineTestReview.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                if (getIntent().hasExtra("studentTest"))
                    getOnlineTestQuePaper(studentTestObj.getTestFilePath());
                else
                    getOnlineTestQuePaper(getIntent().getStringExtra("TestFilePath"));
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

//        for (int i = 0; i < btn.length; i++) {
//            btn[i] = findViewById(btn_id[i]);
//            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
//        }
//        for (int i = 0; i < tfbtn.length; i++) {
//            tfbtn[i] = findViewById(tfbtn_id[i]);
//            tfbtn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
//        }

        if (getIntent().hasExtra("studentTest")) {
            studentTestObj = (StudentOnlineTestObj) getIntent().getSerializableExtra("studentTest");
            tvTitle.setText(studentTestObj.getTestName());
            testId = studentTestObj.getTestId();
        } else
            tvTitle.setText(getIntent().getStringExtra("testName"));

        studentId = getIntent().getStringExtra("studentId");
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
        tvSecName.setText(testQueList.get(queNo).getTestSectionName() + "(" + testQueList.get(queNo).getQuestType() + ")");
        if (calculateMarks(testQueList.get(queNo)).equalsIgnoreCase("0"))
            tvTime.setText("" + testQueList.get(queNo).getTimeTaken() + " Secs ");
        else
            tvTime.setText("( " + calculateMarks(testQueList.get(queNo)) + " marks ) " + testQueList.get(queNo).getTimeTaken() + " Secs ");
        tvSubName.setText(testQueList.get(queNo).getSubjectName());

        if (testQueList.get(queNo).getIsGrace() != 0) {
            tvGrace.setVisibility(View.VISIBLE);

            if (testQueList.get(queNo).getIsGrace() == 1)
                tvGrace.setText("* Grace Marks were given for this Question");
            else if (testQueList.get(queNo).getIsGrace() == 2)
                tvGrace.setText("* Key has been changed for this Question.So you may find difference in Marks");
        } else {
            tvGrace.setVisibility(View.GONE);
        }

        if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MCQ") ||
                testQueList.get(queNo).getQuestType().equalsIgnoreCase("MAQ")
                || testQueList.get(queNo).getQuestType().equalsIgnoreCase("CPQ")) {

            if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("CPQ")) {
                for (int i = 0; i < paragraphDetailsList.size(); i++) {
                    if (testQueList.get(queNo).getParagraphId().equalsIgnoreCase(String.valueOf(paragraphDetailsList.get(i).getParagraphId()))) {
                        paragraphString = paragraphDetailsList.get(i).getParagraph();
                    }
                }
                tvPshow.setVisibility(View.VISIBLE);
            } else {
                tvPshow.setVisibility(View.GONE);
                paragraphString = "";
            }

            wvQue.setVisibility(View.VISIBLE);
            llMfq.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.VISIBLE);

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
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("TOF") ||
                testQueList.get(queNo).getQuestType().equalsIgnoreCase("FIB") ||
                testQueList.get(queNo).getQuestType().equalsIgnoreCase("ITQ")) {

            wvQue.setVisibility(View.VISIBLE);
            llMfq.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.VISIBLE);

            markAnswer();

            String mainString = "<!DOCTYPE html> <html>" + testQueList.get(queNo).getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            wvQue.loadData(mainString, "text/html; charset=utf-8", "utf-8");
            wvQue.scrollTo(0, 0);
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {

            wvQue.setVisibility(View.GONE);
            llMfq.setVisibility(View.VISIBLE);
            llMfqOptions.setVisibility(View.VISIBLE);

            llMFQQue.removeAllViews();


            markAnswer();

            setMFQlayout(testQueList.get(queNo).getQuestions());
        }
    }

    private String calculateMarks(OnlineQuestionObj2 onlineQuestionObj2) {
        String marks = "";

        if (onlineQuestionObj2.getSelectedAnswer().equalsIgnoreCase("")) {
            marks = "0";
        } else {
            if (onlineQuestionObj2.getQuestType().equalsIgnoreCase("MAQ")) {
                if (onlineQuestionObj2.getPartialMarksAvailable().equalsIgnoreCase("y")) {
                    if (onlineQuestionObj2.getSelectedAnswer().equalsIgnoreCase(onlineQuestionObj2.getCorrectAnswer())) {

                        if (onlineQuestionObj2.getCorrectMarks() > 0) {
                            marks = String.valueOf(onlineQuestionObj2.getCorrectMarks());
                        } else
                            marks = fileCorrectMarks;
                    } else {
                        int pCMarks = 0;
                        int pWMarks = 0;
                        String[] Sarray = onlineQuestionObj2.getSelectedAnswer().split(",");
                        String[] Carray = onlineQuestionObj2.getCorrectAnswer().split(",");

                        List<String> list1 = new ArrayList<String>();
                        Collections.addAll(list1, Carray);

                        for (int j = 0; j < Sarray.length; j++) {
                            if (list1.contains(Sarray[j])) {
                                pCMarks++;
                            } else {
                                pWMarks++;
                            }
                        }


                        if (onlineQuestionObj2.getIsOptionMarks().equalsIgnoreCase("0")) {
                            if (pWMarks > 0) {
                                marks = "-" + onlineQuestionObj2.getWrongMarks();
                            } else {
                                marks = "" + (pCMarks * Integer.parseInt(onlineQuestionObj2.getPartialCorrectMarks()));
                            }
                        } else {
                            marks = "" + ((pCMarks * Integer.parseInt(onlineQuestionObj2.getPartialCorrectMarks())) - (pWMarks * Integer.parseInt(onlineQuestionObj2.getPartialWrongMarks())));
                        }

                    }

                } else {
                    if (removeTrailingLeadingZeroes(onlineQuestionObj2.getSelectedAnswer())
                            .equalsIgnoreCase(removeTrailingLeadingZeroes(onlineQuestionObj2.getCorrectAnswer()))) {

                        if (onlineQuestionObj2.getCorrectMarks() > 0) {
                            marks = String.valueOf(onlineQuestionObj2.getCorrectMarks());
                        } else
                            marks = fileCorrectMarks;
                    } else {
                        if (!onlineQuestionObj2.getWrongMarks().equalsIgnoreCase(""))
                            marks = "-" + onlineQuestionObj2.getWrongMarks();
                        else
                            marks = "-" + fileWrongMarks;
                    }
                }
            } else if (onlineQuestionObj2.getQuestType().equalsIgnoreCase("MFQ")) {
                if (onlineQuestionObj2.getPartialMarksAvailable().equalsIgnoreCase("y")) {
                    if (onlineQuestionObj2.getSelectedAnswer().equalsIgnoreCase(onlineQuestionObj2.getCorrectAnswer())) {

                        if (onlineQuestionObj2.getCorrectMarks() > 0) {
                            marks = String.valueOf(onlineQuestionObj2.getCorrectMarks());
                            onlineQuestionObj2.setMarks(onlineQuestionObj2.getCorrectMarks());
                        } else
                            marks = fileCorrectMarks;
                    } else {
                        int pCMarks = 0;
                        int pWMarks = 0;
                        String[] Sarray = onlineQuestionObj2.getSelectedAnswer().split(",");
                        String[] Carray = onlineQuestionObj2.getCorrectAnswer().split(",");

                        for (int j = 0; j < Sarray.length; j++) {
                            if (Sarray[j].length() > 2) {
                                if (Carray[j].equalsIgnoreCase(Sarray[j])) {
                                    pCMarks++;
                                } else {
                                    pWMarks++;
                                }
                            }
                        }

                        if (onlineQuestionObj2.getIsOptionMarks().equalsIgnoreCase("0")) {
                            if (pWMarks > 0) {
                                marks = "-" + onlineQuestionObj2.getWrongMarks();
                            } else {
                                marks = "" + (pCMarks * Integer.parseInt(onlineQuestionObj2.getPartialCorrectMarks()));
                            }
                        } else {
                            marks = "" + ((pCMarks * Integer.parseInt(onlineQuestionObj2.getPartialCorrectMarks())) - (pWMarks * Integer.parseInt(onlineQuestionObj2.getPartialWrongMarks())));
                        }

                    }

                } else {
                    if (removeTrailingLeadingZeroes(onlineQuestionObj2.getSelectedAnswer())
                            .equalsIgnoreCase(removeTrailingLeadingZeroes(onlineQuestionObj2.getCorrectAnswer()))) {

                        if (onlineQuestionObj2.getCorrectMarks() > 0) {
                            marks = String.valueOf(onlineQuestionObj2.getCorrectMarks());
                        } else
                            marks = fileCorrectMarks;
                    } else {
                        if (!onlineQuestionObj2.getWrongMarks().equalsIgnoreCase(""))
                            marks = "-" + onlineQuestionObj2.getWrongMarks();
                        else
                            marks = "-" + fileWrongMarks;
                    }
                }
            } else if (onlineQuestionObj2.getQuestType().equalsIgnoreCase("MCQ")
                    || onlineQuestionObj2.getQuestType().equalsIgnoreCase("CPQ")) {
                if (onlineQuestionObj2.getCorrectAnswer()
                        .contains(onlineQuestionObj2.getSelectedAnswer())) {
                    if (onlineQuestionObj2.getCorrectMarks() > 0) {
                        marks = String.valueOf(onlineQuestionObj2.getCorrectMarks());
                    } else
                        marks = fileCorrectMarks;
                } else {
                    if (!onlineQuestionObj2.getWrongMarks().equalsIgnoreCase(""))
                        marks = "-" + onlineQuestionObj2.getWrongMarks();
                    else
                        marks = "-" + fileWrongMarks;
                }
            } else {
                if (removeTrailingLeadingZeroes(onlineQuestionObj2.getSelectedAnswer())
                        .equalsIgnoreCase(removeTrailingLeadingZeroes(onlineQuestionObj2.getCorrectAnswer()))) {

                    if (onlineQuestionObj2.getCorrectMarks() > 0) {
                        marks = String.valueOf(onlineQuestionObj2.getCorrectMarks());
                    } else
                        marks = fileCorrectMarks;
                } else {
                    if (!onlineQuestionObj2.getWrongMarks().equalsIgnoreCase(""))
                        marks = "-" + onlineQuestionObj2.getWrongMarks();
                    else
                        marks = "-" + fileWrongMarks;
                }
            }
        }

        if (marks.equalsIgnoreCase("-0"))
            marks = "0";
        return marks;
    }

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

    public static String removeTrailingLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        str = str.indexOf(".") < 0 ? str : str.replaceAll("0*$", "").replaceAll("\\.$", "");
        return str;
    }

    @SuppressLint("SetTextI18n")
    private void markAnswer() {

        Log.v(TAG, "markAnswer tvQueNo -  " + queNo);
        Log.v(TAG, "markAnswer ganswer -  " + testQueList.get(queNo).getSelectedAnswer());
        Log.v(TAG, "markAnswer answer -  " + testQueList.get(queNo).getCorrectAnswer());

        if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("")) {
            tvQueresult.setText(" UnAnswered");
            tvQueresult.setTextColor(getResources().getColor(R.color.skiped_ans));
        } else {
             if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MCQ")
                    || testQueList.get(queNo).getQuestType().equalsIgnoreCase("CPQ")) {
                 if (testQueList.get(queNo).getCorrectAnswer().contains(testQueList.get(queNo).getSelectedAnswer())) {
                     tvQueresult.setText("Correct Answered");
                     tvQueresult.setTextColor(getResources().getColor(R.color.correct_ans,null));
                 } else {
                     tvQueresult.setText("Wrong Answered");
                     tvQueresult.setTextColor(getResources().getColor(R.color.wrong_ans,null));
                 }
            }else{
                 if (removeTrailingLeadingZeroes(testQueList.get(queNo).getCorrectAnswer())
                         .equalsIgnoreCase(removeTrailingLeadingZeroes(testQueList.get(queNo).getSelectedAnswer()))) {
                     tvQueresult.setText("Correct Answered");
                     tvQueresult.setTextColor(getResources().getColor(R.color.correct_ans,null));
                 } else {
                     tvQueresult.setText("Wrong Answered");
                     tvQueresult.setTextColor(getResources().getColor(R.color.wrong_ans,null));
                 }
             }

        }

        if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MCQ")
                || testQueList.get(queNo).getQuestType().equalsIgnoreCase("CPQ")
                || testQueList.get(queNo).getQuestType().equalsIgnoreCase("TOF")
                || testQueList.get(queNo).getQuestType().equalsIgnoreCase("MAQ")) {

            llMfqResult.setText("" + testQueList.get(queNo).getCorrectAnswer().replace("option1", "A")
                    .replace("option2", "B")
                    .replace("option3", "C")
                    .replace("option4", "D"));
            tvMfqResponce.setText("" + testQueList.get(queNo).getSelectedAnswer().replace("option1", "A")
                    .replace("option2", "B")
                    .replace("option3", "C")
                    .replace("option4", "D"));
//            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
//            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());

        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("MFQ")) {
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase(""))
                llMfqResponce.setVisibility(View.INVISIBLE);
            else {
                tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
                llMfqResponce.setVisibility(View.VISIBLE);
            }

            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase(""))
                llMfqResponce.setVisibility(View.INVISIBLE);

        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("FIB")) {
            Log.v(TAG, "Responce -" + testQueList.get(queNo).getSelectedAnswer());
            Log.v(TAG, "Responce -" + testQueList.get(queNo).getCorrectAnswer());
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase(""))
                llMfqResponce.setVisibility(View.INVISIBLE);
            else
                llMfqResponce.setVisibility(View.VISIBLE);
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
        } else if (testQueList.get(queNo).getQuestType().equalsIgnoreCase("ITQ")) {
            if (testQueList.get(queNo).getSelectedAnswer().equalsIgnoreCase("")) {
                llMfqResponce.setVisibility(View.INVISIBLE);
            } else {
                llMfqResponce.setVisibility(View.VISIBLE);
            }
            llMfqResult.setText(testQueList.get(queNo).getCorrectAnswer());
            tvMfqResponce.setText(testQueList.get(queNo).getSelectedAnswer());
        }
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
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
            new AlertDialog.Builder(StudentOnlineTestReview.this)
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

    void getOnlineTestQuePaper(String... strings) {
        utils.showLoader(StudentOnlineTestReview.this);
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
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesPaperList responce - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                            testQuePaperList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                            }.getType()));

                            if (ParentjObject.has("paragraphDetails")) {
                                JSONArray jsonArr2 = ParentjObject.getJSONArray("paragraphDetails");
                                paragraphDetailsList.addAll(new Gson().fromJson(jsonArr2.toString(), new TypeToken<List<OnlineQuestionParagraphDetails>>() {
                                }.getType()));
                                utils.showLog(TAG, "paragraphDetails - " + paragraphDetailsList.size());
                            }

                            if (testQuePaperList.size() > 0) {
                                utils.showLog(TAG, "QuePaperList - " + testQuePaperList.size());
                                runOnUiThread(() -> utils.dismissDialog());
                                if (getIntent().hasExtra("studentTest"))

                                    if(studentTestObj.getStudentTestFilePath().equalsIgnoreCase("NA") ||
                                            studentTestObj.getStudentTestFilePath().equalsIgnoreCase("UNDEFINED") ||
                                            studentTestObj.getStudentTestFilePath().isEmpty()) {
                                        getOnlineTestQueDetailsDB();
                                    }else{
                                        getOnlineTestQuePaperDetails(studentTestObj.getStudentTestFilePath());
                                    }

                                else
                                    getOnlineTestQuePaperDetails(getIntent().getStringExtra("studentTestFilePath"));
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
                runOnUiThread(() -> utils.dismissDialog());
            }
        });

    }

    void getOnlineTestQuePaperDetails(String... strings) {
        utils.showLoader(StudentOnlineTestReview.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "TestGetQue Url - " + strings[0]);

        Request request = new Request.Builder()
                .url(strings[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (NetworkConnectivity.isConnected(StudentOnlineTestReview.this)) {
                    getOnlineTestQueDetailsDB();
                } else {
                    runOnUiThread(() -> utils.dismissDialog());
                }

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "QuesPaperList responce - " + jsonResp);

                    try {
                        JSONObject jsonObject = new JSONObject(jsonResp);
                        JSONArray jsonArr = jsonObject.getJSONArray("TestCategories");
                        testQueList.clear();
                        testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                        }.getType()));

                        fileCorrectMarks = jsonObject.getString("correctMarks");
                        fileWrongMarks = jsonObject.getString("wrongMarks");

                        utils.showLog(TAG, "testQuePaperdetailsList - " + testQueList.size());

                        runOnUiThread(() -> MergeQuestions());


                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (NetworkConnectivity.isConnected(StudentOnlineTestReview.this)) {
                            getOnlineTestQueDetailsDB();
                        } else {
                            runOnUiThread(() -> utils.dismissDialog());
                        }
                    }

                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });


    }

    void getOnlineTestQueDetailsDB() {
        utils.showLoader(StudentOnlineTestReview.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "TestGetQue Url testID - " + studentTestObj.getTestId());
        utils.showLog(TAG, "TestGetQue Url studentID - " + studentTestObj.getStudentId());
        utils.showLog(TAG, "TestGetQue Url schema - " + sh_Pref.getString("schema", ""));
        utils.showLog(TAG, "TestGetQue Url  - " + AppUrls.GetExamResultByStudentId + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentTestObj.getStudentId() + "&testId=" + studentTestObj.getTestId());

        Request request = new Request.Builder()
                .url(AppUrls.GetExamResultByStudentId + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentTestObj.getStudentId() + "&testId=" + studentTestObj.getTestId())
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

                    utils.showLog(TAG, "QuesPaperList responce - " + jsonResp);

                    try {
                        JSONObject jsonObject = new JSONObject(jsonResp);
                        JSONArray jsonArr = jsonObject.getJSONObject("result").getJSONArray("testCategories");
                        testQueList.clear();
                        testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
                        }.getType()));


                        utils.showLog(TAG, "testQuePaperdetailsList - " + testQueList.size());

                        runOnUiThread(() -> MergeQuestions());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });


    }


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
                        testQueList.get(j).setIsGrace(testQuePaperList.get(i).getIsGrace());
                        testQueList.get(j).setCorrectAnswer(testQuePaperList.get(i).getCorrectAnswer());
                        testQueList.get(j).setSubjectName(testQuePaperList.get(i).getSubjectName());
                        testQueList.get(j).setQuestions(testQuePaperList.get(i).getQuestions());
                    }
                }
            }
        }
        if (testQueList.size() > 0) {
            adapter = new QuesionNoAdapter(this, testQueList);
            rvQueNoList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            rvQueNoList.setAdapter(adapter);
            loadQuestion(queNo);
        } else
            showNoDataDialogue();
    }

//    private class GetOnlineTestQuestions extends AsyncTask<String, Void, String> {
//        ProgressDialog loading;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loading = new ProgressDialog(StudentOnlineTestReview.this);
//            loading.setMessage("Please wait...");
//            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            loading.setCancelable(false);
//            loading.show();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String jsonResp = "null";
//
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build();
//
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//            Log.v(TAG, "Log - " + new AppUrls().GetStudentTestQuestionAnalysis + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&studentId=" + studentId + "&testId=" + testId);
//
//
//            Request request = new Request.Builder()
//                    .url(new AppUrls().GetStudentTestQuestionAnalysis + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&studentId=" + studentId + "&testId=" + testId)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                jsonResp = response.body().string();
//
//                Log.v(TAG, "result responce - " + jsonResp);
//
//                // Do something with the response.
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return jsonResp;
//        }
//
//        @Override
//        protected void onPostExecute(String responce) {
//            super.onPostExecute(responce);
//            if (responce != null) {
//                try {
//                    JSONObject ParentjObject = new JSONObject(responce);
//                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//                        JSONArray jsonArr = ParentjObject.getJSONArray("TestQuestions");
//
//                        testQueList.addAll(new Gson().fromJson(jsonArr.toString(), new TypeToken<List<OnlineQuestionObj2>>() {
//                        }.getType()));
//
//                        Log.v(TAG, "testQueList  - " + testQueList.size());
//                        if (testQueList.size() > 0) {
//                            loadQuestion(queNo);
//                        } else
//                            showNoDataDialogue();
//
//                    } else
//                        showNoDataDialogue();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            loading.dismiss();
//
//        }
//    }

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


    class QuesionNoAdapter extends RecyclerView.Adapter<QuesionNoAdapter.ViewHolder> {

        private List<OnlineQuestionObj2> queslist;
        private Context _context;

        public QuesionNoAdapter(Context _context, List<OnlineQuestionObj2> queslist) {
            this.queslist = queslist;
            this._context = _context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(StudentOnlineTestReview.this).inflate(R.layout.item_question_number, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if (queslist.get(position).getSelectedAnswer().equalsIgnoreCase("blank")) {
                holder.llItemview.setBackgroundResource(R.drawable.bg_grey_circle_border);
                holder.tvQueNo.setTextColor(Color.parseColor("#40000000"));
            } else {

                if (queslist.get(position).getSelectedAnswer().equalsIgnoreCase(queslist.get(position).getCorrectAnswer())) {
                    holder.llItemview.setBackgroundResource(R.drawable.bg_review_circle_correct);
                    holder.tvQueNo.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    holder.llItemview.setBackgroundResource(R.drawable.bg_review_circle_wrong);
                    holder.tvQueNo.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            holder.tvQueNo.setText("" + (position + 1));

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
