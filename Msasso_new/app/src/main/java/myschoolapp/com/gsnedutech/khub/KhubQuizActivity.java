package myschoolapp.com.gsnedutech.khub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KhubOption;
import myschoolapp.com.gsnedutech.khub.models.KhubQuizQuestions;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KhubQuizActivity extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubQuizActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    List<KhubQuizQuestions> listQuestions = new ArrayList<>();
    int pos;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    @BindView(R.id.wv_q)
    WebView wvQ;
    @BindView(R.id.tv_qnum)
    TextView tvQnum;

    @BindView(R.id.wv_op_1)
    WebView wvOp1;
    @BindView(R.id.wv_op_2)
    WebView wvOp2;
    @BindView(R.id.wv_op_3)
    WebView wvOp3;
    @BindView(R.id.wv_op_4)
    WebView wvOp4;

    @BindView(R.id.rv_questions)
    RecyclerView rvQuestions;

    @BindView(R.id.cv_a)
    CardView cvA;
    @BindView(R.id.cv_b)
    CardView cvB;
    @BindView(R.id.cv_c)
    CardView cvC;
    @BindView(R.id.cv_d)
    CardView cvD;
    @BindView(R.id.ll_fib)
    LinearLayout llFib;
    @BindView(R.id.tv_check_answer)
    TextView tvCheckAnswer;
    @BindView(R.id.layout_fibans)
    LinearLayout layoutFibans;

    @BindView(R.id.ll_mcq)
    LinearLayout llMcq;

    @BindView(R.id.op_mcq)
    LinearLayout opMcq;

    @BindView(R.id.ll_mfqOptions)
    LinearLayout llMfqOptions;
    @BindView(R.id.ll_mfqResponce)
    LinearLayout llMfqResponce;
    @BindView(R.id.tv_mfqResponce)
    TextView tvMfqResponce;
    @BindView(R.id.tv_mfqResult)
    TextView llMfqResult;

    @BindView(R.id.ll_qa)
    LinearLayout llQA;
    @BindView(R.id.wv_ans)
    WebView wvAns;

    @BindView(R.id.ll_tof)
    LinearLayout llTof;

    @BindView(R.id.tv_question_type)
    TextView tvQuestionType;


    List<EditText[]> editTextLists = new ArrayList<>();

    private int[] btn_id = {R.id.tv_a, R.id.tv_b, R.id.tv_c, R.id.tv_d};
    private int[] torfIds = {R.id.tv_t, R.id.tv_f};
    private TextView[] btn;
    private TextView[] tfBtn;



    int selectedPos = 0;

    String maqans = "";


    QuizAdapter adapter;
    String contentType = "", moduleContentId = "", moduleId = "";

    MyUtils utils = new MyUtils();
    int selectedOpt;
    LinearLayoutManager linearLayoutManager;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_quiz);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_next).setOnClickListener(view -> {
            if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("Q&A")
                    || listQuestions.get(selectedPos).getSelectedOption()!=0
                    || listQuestions.get(selectedPos).getSelectedAnswer().length()>0 ){
                if (++selectedPos < listQuestions.size()) {
                    rvQuestions.smoothScrollToPosition(selectedPos);
                    adapter.notifyDataSetChanged();
                } else {
                    selectedPos = listQuestions.size() - 1;
                    showFinishAlert("finish");
                }
            }
            else {
                new AlertDialog.Builder(KhubQuizActivity.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Please select the option and check it.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        findViewById(R.id.iv_prev).setOnClickListener(view -> {
            if (--selectedPos >= 0) {
                rvQuestions.smoothScrollToPosition(selectedPos);
                adapter.notifyDataSetChanged();
            } else {
                selectedPos = 0;
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());

        findViewById(R.id.tv_finish).setOnClickListener(view -> showFinishAlert("Are you sure you want to finish the test?"));

        tvCheckAnswer.setOnClickListener(view -> {
            if (tvCheckAnswer.getText().toString().trim().contains("Check")) {
                if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ")) {
                    boolean isSelected = false;
                    String selectedAns = "";
                    for (int b = 0; b < btn.length; b++) {
                        if (btn[b].getTag().equals("selected")) {
                            isSelected = true;
                            if (selectedAns.isEmpty())
                                selectedAns = String.valueOf(b + 1);
                            else selectedAns = selectedAns + "," + (b + 1);

                        }
                    }
                    if (isSelected) {
                        listQuestions.get(selectedPos).setSelectedAnswer(selectedAns);
                        maqans = "";
                        for (int k = 0; k < listQuestions.get(selectedPos).getKhubOptions().size(); k++) {
                            if (listQuestions.get(selectedPos).getKhubOptions().get(k).getIsCorrect()) {
                                if (maqans.isEmpty()) maqans = "" + (k + 1);
                                else maqans = MessageFormat.format("{0},{1}", maqans, k + 1);
                                btn[k].setTextColor(Color.parseColor("#ffffff"));
                                btn[k].setBackgroundColor(Color.parseColor("#0fdea0"));
                            }
                        }
                        if (maqans.isEmpty()) {
                            Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            if (!listQuestions.get(selectedPos).getExplanation().isEmpty()){
                                tvCheckAnswer.setVisibility(View.VISIBLE);
                                tvCheckAnswer.setText("View Explanation");
                            }
                            boolean isCorrect = true;
                            for (int b = 0; b < btn.length; b++) {
                                if (btn[b].getTag().equals("selected")) {
                                    btn[b].setTextColor(Color.parseColor("#ffffff"));
                                    if (maqans.contains("" + (b + 1))) {
                                        btn[b].setBackgroundColor(Color.parseColor("#0fdea0"));
                                        if (isCorrect)
                                            isCorrect = true;
                                    } else {
                                        btn[b].setBackgroundColor(Color.parseColor("#f15353"));
                                        isCorrect = false;
                                    }
                                }
                                btn[b].setEnabled(false);
                            }
                            listQuestions.get(selectedPos).setCorrect(isCorrect);
                            if (listQuestions.get(selectedPos).isCorrect()){
                                Toast.makeText(this, "You selected Correct Answer", Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(this, "You selected Wrong Answer", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please select the options ", Toast.LENGTH_SHORT).show();
                    }
                } else if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("FIB")) {
                    if (editTextLists.size() > 0) {
                        StringBuilder answersBuilder = new StringBuilder();
                        for (int i = 0; i < editTextLists.size(); i++) {
                            if (i > 0) {
                                answersBuilder.append(",");
                            }
                            for (int j = 0; j < editTextLists.get(i).length; j++) {
                                answersBuilder.append(editTextLists.get(i)[j].getText().toString());
                                editTextLists.get(i)[j].setEnabled(false);
                            }

                            utils.showLog(TAG, " Answer " + answersBuilder);
                        }
                        String answers = answersBuilder.toString();
                        if (!answers.equalsIgnoreCase("")) {
                            listQuestions.get(selectedPos).setSelectedAnswer(answers);
                            String[] ans = answers.split(",");
                            llMfqOptions.setVisibility(View.VISIBLE);
                            llMcq.setVisibility(View.GONE);
                            opMcq.setVisibility(View.GONE);
                            boolean isCorrect = false;
                            for (int k = 0; k < listQuestions.get(selectedPos).getPossibleAnswers().size(); k++) {
                                if (!ans[k].equalsIgnoreCase(listQuestions.get(selectedPos).getPossibleAnswers().get(k))) {
//                                    Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                isCorrect = true;
                            }
                            if (listQuestions.get(selectedPos).getSelectedAnswer().equalsIgnoreCase(""))
                                llMfqResponce.setVisibility(View.INVISIBLE);
                            llMfqResult.setText(TextUtils.join(",", listQuestions.get(selectedPos).getPossibleAnswers()));
                            tvMfqResponce.setText(listQuestions.get(selectedPos).getSelectedAnswer());
                            listQuestions.get(selectedPos).setCorrect(isCorrect);
                            if (listQuestions.get(selectedPos).isCorrect()){
                                Toast.makeText(this, "You selected Correct Answer", Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(this, "You selected Wrong Answer", Toast.LENGTH_SHORT).show();
                            if (!listQuestions.get(selectedPos).getExplanation().isEmpty()){
                                tvCheckAnswer.setVisibility(View.VISIBLE);
                                tvCheckAnswer.setText("View Explanation");
                            }
                        } else {
                            Toast.makeText(this, "Please fill the blank", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            else {
                showAnswerDialog();
            }

        });

    }


    void init() {

        //get pos and modId

        pos = Integer.parseInt(getIntent().getStringExtra("position"));
        moduleId = getIntent().getStringExtra("moduleId");
        moduleContentId = getIntent().getStringExtra("moduleContentId");
        contentType = getIntent().getStringExtra("contentType");
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        linearLayoutManager = new LinearLayoutManager(KhubQuizActivity.this, RecyclerView.HORIZONTAL, false);



    }

    void loadQuestion(int num) {
        findViewById(R.id.iv_prev).setVisibility(View.VISIBLE);

        if(num == 0)
        {
            findViewById(R.id.iv_prev).setVisibility(View.INVISIBLE);
        }
        tvCheckAnswer.setVisibility(View.VISIBLE);
        tvCheckAnswer.setText("Check Answer ");
        if (listQuestions.get(num).getQType().equalsIgnoreCase("MCQ") ||
                listQuestions.get(num).getQType().equalsIgnoreCase("MAQ")){

            if (listQuestions.get(num).getQType().equalsIgnoreCase("MAQ")){
                tvQuestionType.setText("MAQ");
            }
            else {
                tvQuestionType.setText("MCQ");
                tvCheckAnswer.setVisibility(View.INVISIBLE);
            }

            llFib.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.GONE);
            llQA.setVisibility(View.GONE);
            llMcq.setVisibility(View.VISIBLE);
            opMcq.setVisibility(View.VISIBLE);
            llTof.setVisibility(View.GONE);


            btn = new TextView[Math.min(listQuestions.get(num).getKhubOptions().size(), 4)];
            for (int i = 0; i < btn.length; i++) {
                btn[i] = findViewById(btn_id[i]);
                if (listQuestions.get(num).getQType().equalsIgnoreCase("MAQ"))
                    btn[i].setTag("Unselected");
                btn[i].setOnClickListener(KhubQuizActivity.this);
                btn[i].setBackgroundColor(Color.parseColor("#ffffff"));
                btn[i].setTextColor(Color.parseColor("#000000"));
                btn[i].setEnabled(true);
            }

            List<KhubOption> listOptions = new ArrayList<>(listQuestions.get(num).getKhubOptions());

            StringBuilder mainString;
            if (listQuestions.get(num).getKhubOptions() != null) {
                mainString = new StringBuilder("<!DOCTYPE html> <html>" + "<fieldset><legend>A)</legend>" + listOptions.get(0).getOption() + "</fieldset>" + "</html>");
                wvOp1.loadData(utils.cleanWebString(String.valueOf(mainString)), "text/html; charset=utf-8", "utf-8");
                if (listOptions.size()>1) {
                    cvB.setVisibility(View.VISIBLE);
                    mainString = new StringBuilder("<!DOCTYPE html> <html>" + "<fieldset><legend>B)</legend>" + listOptions.get(1).getOption() + "</fieldset>" + "</html>");
                    wvOp2.loadData(utils.cleanWebString(String.valueOf(mainString)), "text/html; charset=utf-8", "utf-8");
                }
                else cvB.setVisibility(View.GONE);
                if (listOptions.size()>2) {
                    cvC.setVisibility(View.VISIBLE);
                    mainString = new StringBuilder("<!DOCTYPE html> <html>" + "<fieldset><legend>C)</legend>" + listOptions.get(2).getOption() + "</fieldset>" + "</html>");
                    wvOp3.loadData(utils.cleanWebString(String.valueOf(mainString)), "text/html; charset=utf-8", "utf-8");
                }else cvC.setVisibility(View.GONE);
                if (listOptions.size()>3) {
                    cvD.setVisibility(View.VISIBLE);
                    mainString = new StringBuilder("<!DOCTYPE html> <html>" + "<fieldset><legend>D)</legend>" + listOptions.get(3).getOption() + "</fieldset>" + "</html>");
                    wvOp4.loadData(utils.cleanWebString(String.valueOf(mainString)), "text/html; charset=utf-8", "utf-8");
                }else cvD.setVisibility(View.GONE);

                if (listQuestions.get(num).getSelectedOption()!=0){
                    if (!listQuestions.get(num).getExplanation().isEmpty()){
                        tvCheckAnswer.setVisibility(View.VISIBLE);
                        tvCheckAnswer.setText("View Explanation");
                    }

                    btn[listQuestions.get(num).getSelectedOption() - 1].setBackgroundColor(Color.parseColor("#f15353"));
                    for (int i = 0; i < listQuestions.get(num).getKhubOptions().size(); i++) {
                        btn[i].setTextColor(Color.parseColor("#40000000"));
                        if (listQuestions.get(num).getKhubOptions().get(i).getIsCorrect()) {
                            btn[i].setBackgroundColor(Color.parseColor("#0fdea0"));
                            btn[i].setTextColor(Color.parseColor("#ffffff"));
                        }
                        btn[i].setEnabled(false);
                    }
                    btn[listQuestions.get(num).getSelectedOption() - 1].setTextColor(Color.parseColor("#ffffff"));
                }
                if (listQuestions.get(num).getSelectedAnswer().length()>0){
                    if (!listQuestions.get(num).getExplanation().isEmpty()){
                        tvCheckAnswer.setVisibility(View.VISIBLE);
                        tvCheckAnswer.setText("View Explanation");
                    }
                    for (int k = 0; k < listQuestions.get(selectedPos).getKhubOptions().size(); k++) {
                        btn[k].setTextColor(Color.parseColor("#40000000"));
                        if (listQuestions.get(selectedPos).getKhubOptions().get(k).getIsCorrect()) {
                            btn[k].setTextColor(Color.parseColor("#ffffff"));
                            btn[k].setBackgroundColor(Color.parseColor("#0fdea0"));
                        }
                        if (listQuestions.get(num).getSelectedAnswer().contains(""+(k+1))){
                            if (!listQuestions.get(selectedPos).getKhubOptions().get(k).getIsCorrect()){
                                btn[k].setBackgroundColor(Color.parseColor("#f15353"));
                                btn[k].setTextColor(Color.parseColor("#ffffff"));
                            }
                        }
                        btn[k].setEnabled(false);
                    }
                    for (int b = 0; b < btn.length; b++) {

                    }
                }

            }
        } else if (listQuestions.get(num).getQType().equalsIgnoreCase("FIB")) {
            utils.showLog(TAG, "FIB Que - " + listQuestions.get(num).getQuestion());
            utils.showLog(TAG, "\n FIB Que - " + listQuestions.get(num).getPossibleAnswers().toString());
            llFib.setVisibility(View.VISIBLE);
            layoutFibans.removeAllViews();
            editTextLists.clear();

            llMcq.setVisibility(View.GONE);
            opMcq.setVisibility(View.GONE);
            llQA.setVisibility(View.GONE);
            llTof.setVisibility(View.GONE);

            tvQuestionType.setText("FIB");

            setFIBLayout(TextUtils.join(",", listQuestions.get(num).getPossibleAnswers()), listQuestions.get(num).getSelectedAnswer());
            if (listQuestions.get(num).getSelectedAnswer().length()>0){
                if (!listQuestions.get(num).getExplanation().isEmpty()){
                    tvCheckAnswer.setVisibility(View.VISIBLE);
                    tvCheckAnswer.setText("View Explanation");
                }
                llFib.setVisibility(View.GONE);
                llMfqOptions.setVisibility(View.VISIBLE);
                if (listQuestions.get(num).getSelectedAnswer().equalsIgnoreCase(""))
                    llMfqResponce.setVisibility(View.INVISIBLE);
                llMfqResult.setText(TextUtils.join(",",listQuestions.get(num).getPossibleAnswers()));
                tvMfqResponce.setText(listQuestions.get(num).getSelectedAnswer());
            }
            else {
                llMfqOptions.setVisibility(View.GONE);
            }
        } else if (listQuestions.get(num).getQType().equalsIgnoreCase("Q&A")){
            tvQuestionType.setText("Q&A");
            llFib.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            opMcq.setVisibility(View.GONE);
            llQA.setVisibility(View.VISIBLE);
            tvCheckAnswer.setVisibility(View.INVISIBLE);
            wvAns.loadData(utils.cleanWebString(listQuestions.get(num).getQuestionAnswer()), "text/html; charset=utf-8", "utf-8");
        }
        else if (listQuestions.get(num).getQType().equalsIgnoreCase("T&F")){

            tvCheckAnswer.setVisibility(View.INVISIBLE);
            tvQuestionType.setText("T&F");
            llFib.setVisibility(View.GONE);
            llMfqOptions.setVisibility(View.GONE);
            llQA.setVisibility(View.GONE);
            llMcq.setVisibility(View.GONE);
            opMcq.setVisibility(View.GONE);
            llTof.setVisibility(View.VISIBLE);
            llTof.setVisibility(View.VISIBLE);
            tfBtn = new TextView[2];
            for (int i = 0; i < tfBtn.length; i++) {
                tfBtn[i] = findViewById(torfIds[i]);
                tfBtn[i].setOnClickListener(KhubQuizActivity.this);
                tfBtn[i].setBackgroundColor(Color.parseColor("#ffffff"));
                tfBtn[i].setTextColor(Color.parseColor("#000000"));
                tfBtn[i].setEnabled(true);
            }
            if (listQuestions.get(num).getSelectedOption()!=0){
                if (!listQuestions.get(num).getExplanation().isEmpty()){
                    tvCheckAnswer.setVisibility(View.VISIBLE);
                    tvCheckAnswer.setText("View Explanation");
                }
                if (listQuestions.get(num).getSelectedOption() == 1){
                    setTofButtons(0,1,num);
                } else {
                    setTofButtons(1,0,num);
                }
                for (int i = 0; i < 2; i++) {
                    tfBtn[i].setEnabled(false);
                }
            }
        }

        wvQ.loadData(utils.cleanWebString(listQuestions.get(selectedPos).getQuestion()!=null?listQuestions.get(selectedPos).getQuestion():""), "text/html; charset=utf-8", "utf-8");
        wvQ.scrollTo(0, 0);
        utils.showLog(TAG,"size "+listQuestions.size());

        tvQnum.setText(MessageFormat.format("Question {0} of {1}", num + 1, listQuestions.size()));

    }

    void setTofButtons(int i, int j, int num){
        if (tfBtn[i].getText().toString().equalsIgnoreCase(listQuestions.get(num).getTrueOrFalse())) {
            tfBtn[i].setBackgroundColor(Color.parseColor("#0fdea0"));
            tfBtn[i].setTextColor(Color.parseColor("#ffffff"));
        }
        else {
            tfBtn[i].setBackgroundColor(Color.parseColor("#f15353"));
            tfBtn[j].setTextColor(Color.parseColor("#ffffff"));
            tfBtn[j].setTextColor(Color.parseColor("#ffffff"));
            tfBtn[i].setBackgroundColor(Color.parseColor("#0fdea0"));
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
            tvQNum.setText(MessageFormat.format("{0}. ", number));
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
                final int finalJ = j;
                editText.setOnTouchListener((v, event) -> {
                    editText.setSelection(editText.getText().length());
                    return false;
                });
                editText.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (editText.getText().toString().length() == 0) {
                            if (finalJ + 1 > 1) {
                                ed_array[finalJ - 1].requestFocus();
                            }
                        }
                    }
                    return false;
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
                                if (finalJ != ed_array.length - 1) {
                                    int count = 0;
                                    if (ed_array[finalJ + 1].isFocusable()) {
                                        if (finalJ != ed_array.length - 1) {
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
                                editText.setText(MessageFormat.format("{0}", str.charAt(1)));
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
                    editTextLists.get(i)[j].setText(MessageFormat.format("{0}", fibsel_words[i].charAt(j)));
                }
            }
        }
    }

    void getContent() {

        utils.showLoader(KhubQuizActivity.this);

        ApiClient client = new ApiClient();
        String URL = AppUrls.KHUB_BASE_URL+AppUrls.Content+"contentType="+contentType+"&moduleContentId="+moduleContentId+"&moduleId="+moduleId+"&studentId="+sObj.getStudentId()+"&schemaName="+sh_Pref.getString("schema","") ;

        Request get = client.getRequest(URL, sh_Pref);
        utils.showLog(TAG, "URL - " + URL);
        client.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> utils.dismissDialog());
                } else {
                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<KhubQuizQuestions>>() {
                            }.getType();
                            listQuestions.clear();
                            listQuestions.addAll(gson.fromJson(jsonArray.toString(), type));
                            if (listQuestions.size()>0){
                                runOnUiThread(() -> {
                                    try {
                                        if (object.has("activityData") && object.getJSONArray("activityData").length()>0){
                                            JSONArray arr = object.getJSONArray("activityData");
                                            JSONArray jsonArray1 = arr.getJSONObject(0).getJSONArray("activityData");
                                            if (listQuestions.get(0).getQType().equalsIgnoreCase("Q&A")){
                                                selectedPos = jsonArray1.getJSONObject(0).getInt("positionId");
                                            }
                                            else {
                                                for (int i = 0; i< jsonArray1.length();i++){
                                                    for (int j =0; j< listQuestions.size(); j++) {
                                                        if (listQuestions.get(j).getId().equalsIgnoreCase(jsonArray1.getJSONObject(i).getString("qId"))) {
                                                            listQuestions.get(j).setCorrect(jsonArray1.getJSONObject(i).getBoolean("isCorrect"));
                                                            if (listQuestions.get(j).getQType().equalsIgnoreCase("MCQ")) {
                                                                listQuestions.get(j).setSelectedOption(Integer.parseInt(jsonArray1.getJSONObject(i).getString("givenAnswer")));
                                                            } else if (listQuestions.get(j).getQType().equalsIgnoreCase("MAQ")) {
                                                                listQuestions.get(j).setSelectedAnswer(jsonArray1.getJSONObject(i).getString("givenAnswer"));
                                                            } else if (listQuestions.get(j).getQType().equalsIgnoreCase("FIB")) {
                                                                listQuestions.get(i).setSelectedAnswer(jsonArray1.getJSONObject(i).getString("givenAnswer"));
                                                            } else if (listQuestions.get(j).getQType().equalsIgnoreCase("T&F")) {
                                                                listQuestions.get(j).setSelectedOption(jsonArray1.getJSONObject(i).getString("givenAnswer").equalsIgnoreCase("false") ? 1 : 0);
                                                            }
                                                            selectedPos = j+1;
                                                            break;

                                                        }
                                                    }

                                                }

                                            }
                                            if (jsonArray1.length() == listQuestions.size()){
                                                showFinishAlert1();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    rvQuestions.setLayoutManager(linearLayoutManager);
                                    adapter = new QuizAdapter(listQuestions);
                                    rvQuestions.setAdapter(adapter);
                                    rvQuestions.scrollToPosition(selectedPos);
                                });
                            }
                            else {
                                runOnUiThread(() -> new AlertDialog.Builder(KhubQuizActivity.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage("No Data To Load")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show());
                            }


                            utils.showLog(TAG, "size " + listQuestions.size());


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(() -> utils.dismissDialog());

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_a:
                if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[0]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[0]);
                    selectedOpt = 1;
                    if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MCQ")) {
                        showMcqAnswer();
                    }
                }

                break;
            case R.id.tv_b:
                if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[1]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[1]);
                    selectedOpt = 2;
                    if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MCQ")) {
                        showMcqAnswer();
                    }
                }
                break;
            case R.id.tv_c:
                if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[2]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[2]);

                    selectedOpt = 3;
                    if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MCQ")) {
                        showMcqAnswer();
                    }
                }
                break;
            case R.id.tv_d:
                if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ")) {
                    if (view.getTag().equals("selected")) {
                        view.setTag("Unselectd");
                    } else {
                        view.setTag("selected");
                    }
                    setFocus(btn[3]);
                } else {
                    setUnFocusAll();
                    setFocus(btn[3]);

                    selectedOpt = 4;
                    if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MCQ")) {
                        showMcqAnswer();
                    }
//
                }
                break;
            case R.id.tv_t:
                setUnFocusAll();
                setFocus(tfBtn[0]);
                selectedOpt = 1;
                showTorFAnswer();
                break;
            case R.id.tv_f:
                setUnFocusAll();
                setFocus(tfBtn[1]);
                selectedOpt = 2;
                showTorFAnswer();
                break;
        }

    }

    private void showMcqAnswer() {
        if (selectedOpt!=0) {
            if (!listQuestions.get(selectedPos).getExplanation().isEmpty()){
                tvCheckAnswer.setVisibility(View.VISIBLE);
                tvCheckAnswer.setText("View Explanation");
            }
            listQuestions.get(selectedPos).setSelectedOption(selectedOpt);
            btn[listQuestions.get(selectedPos).getSelectedOption() - 1].setBackgroundColor(Color.parseColor("#f15353"));
            btn[listQuestions.get(selectedPos).getSelectedOption() - 1].setTextColor(Color.parseColor("#ffffff"));
            for (int i = 0; i < Math.min(listQuestions.get(selectedPos).getKhubOptions().size(),4); i++) {
                if (listQuestions.get(selectedPos).getKhubOptions().get(i).getIsCorrect()) {
                    btn[i].setBackgroundColor(Color.parseColor("#0fdea0"));
                    btn[i].setTextColor(Color.parseColor("#ffffff"));
                }
                btn[i].setEnabled(false);
            }

            listQuestions.get(selectedPos).setCorrect(listQuestions.get(selectedPos).getKhubOptions().get(listQuestions.get(selectedPos).getSelectedOption() - 1).getIsCorrect());
            if (listQuestions.get(selectedPos).isCorrect()){
                Toast.makeText(this, "You selected Correct Answer", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "You selected Wrong Answer", Toast.LENGTH_SHORT).show();
            selectedOpt = 0;
        }
    }

    private void showTorFAnswer() {
        if (selectedOpt!=0) {
            if (!listQuestions.get(selectedPos).getExplanation().isEmpty()){
                tvCheckAnswer.setVisibility(View.VISIBLE);
                tvCheckAnswer.setText("View Explanation");
            }
            listQuestions.get(selectedPos).setSelectedOption(selectedOpt);
            if (listQuestions.get(selectedPos).getSelectedOption() == 1){
                if (tfBtn[0].getText().toString().equalsIgnoreCase(listQuestions.get(selectedPos).getTrueOrFalse())) {
                    tfBtn[0].setBackgroundColor(Color.parseColor("#0fdea0"));
                    tfBtn[0].setTextColor(Color.parseColor("#ffffff"));
                    listQuestions.get(selectedPos).setCorrect(true);
                }
                else {
                    tfBtn[0].setBackgroundColor(Color.parseColor("#f15353"));
                    tfBtn[0].setTextColor(Color.parseColor("#ffffff"));
                    tfBtn[1].setTextColor(Color.parseColor("#ffffff"));
                    tfBtn[1].setBackgroundColor(Color.parseColor("#0fdea0"));
                    listQuestions.get(selectedPos).setCorrect(false);
                }
            } else {
                if (tfBtn[1].getText().toString().equalsIgnoreCase(listQuestions.get(selectedPos).getTrueOrFalse())) {
                    tfBtn[1].setBackgroundColor(Color.parseColor("#0fdea0"));
                    tfBtn[1].setTextColor(Color.parseColor("#ffffff"));
                    listQuestions.get(selectedPos).setCorrect(true);
                }
                else {
                    tfBtn[1].setBackgroundColor(Color.parseColor("#f15353"));
                    tfBtn[0].setTextColor(Color.parseColor("#ffffff"));
                    tfBtn[1].setTextColor(Color.parseColor("#ffffff"));
                    tfBtn[0].setBackgroundColor(Color.parseColor("#0fdea0"));
                    listQuestions.get(selectedPos).setCorrect(false);
                }
            }
            for (int i = 0; i < 2; i++) {
                tfBtn[i].setEnabled(false);
            }
            if (listQuestions.get(selectedPos).isCorrect()){
                Toast.makeText(this, "You selected Correct Answer", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "You selected Wrong Answer", Toast.LENGTH_SHORT).show();
            selectedOpt = 0;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubQuizActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (listQuestions.size() == 0)
                    getContent();
            }
            isNetworkAvail = true;
        }
    }

    class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

        List<KhubQuizQuestions> quesList;

        public QuizAdapter(List<KhubQuizQuestions> quesList) {

            this.quesList = quesList;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubQuizActivity.this).inflate(R.layout.item_khub_que_count, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvCount.setText(MessageFormat.format("{0}", position + 1));

            if (position == selectedPos) {
                holder.tvCount.setTextColor(Color.WHITE);
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_selected);
                loadQuestion(selectedPos);
            } else {
                holder.tvCount.setTextColor(Color.BLACK);
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_not_selected);
            }
            if (quesList.get(position).getSelectedOption()!=0 || quesList.get(position).getSelectedAnswer().length()>0) {
                if (position == selectedPos) {
                    holder.tvCount.setTextColor(Color.WHITE);
                    holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_selected);
                }
                else {
                    if (quesList.get(position).getQType().equalsIgnoreCase("MCQ")) {
                        if (quesList.get(position).getKhubOptions().get(quesList.get(position).getSelectedOption() - 1).getIsCorrect()) {
                            holder.tvCount.setTextColor(Color.WHITE);
                            holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_correct);
                        } else {
                            holder.tvCount.setTextColor(Color.WHITE);
                            holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_wrong);
                        }
                    }
                    else if (quesList.get(position).getQType().equalsIgnoreCase("MAQ") ||
                            quesList.get(position).getQType().equalsIgnoreCase("FIB") ||
                            quesList.get(position).getQType().equalsIgnoreCase("T&F")){
                        if (quesList.get(position).isCorrect()) {
                            holder.tvCount.setTextColor(Color.WHITE);
                            holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_correct);
                        } else {
                            holder.tvCount.setTextColor(Color.WHITE);
                            holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_wrong);
                        }
                    }
                }

            }

        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return quesList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCount = itemView.findViewById(R.id.tv_count);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        postContentActivity("");
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void postContentActivity(String refresh){
        utils.showLoader(this);
        ApiClient client  = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for (KhubQuizQuestions questions : listQuestions){
                if (questions.getQType().equalsIgnoreCase("Q&A")
                        || questions.getSelectedOption()!=0
                        || questions.getSelectedAnswer().length()>0){
                    JSONObject data = new JSONObject();
                    data.put("qId", questions.getId());
                    data.put("isCorrect", questions.isCorrect());
                    if(questions.getQType().equalsIgnoreCase("MCQ")){
//                        data.put("givenAnswer", questions.getKhubOptions().get(questions.getSelectedOption()-1).getOption());
                        data.put("givenAnswer", ""+questions.getSelectedOption());
                    }
                    else if (questions.getQType().equalsIgnoreCase("MAQ") || questions.getQType().equalsIgnoreCase("FIB")){
                        data.put("givenAnswer", questions.getSelectedAnswer());
                    }
                    else if (questions.getQType().equalsIgnoreCase("T&F")){
                        if (questions.getSelectedOption()==0)
                            data.put("givenAnswer","true");
                        else data.put("givenAnswer","false");
                    }
                    array.put(data);
                }

            }

            float pr;
            if (listQuestions.get(0).getQType().equalsIgnoreCase("Q&A")){
                array = new JSONArray();
                JSONObject data = new JSONObject();
                data.put("positionId", selectedPos-1);
                data.put("moduleDataId", listQuestions.get(selectedPos-1).getId());
                array.put(data);
                pr = (((float) (selectedPos+1)/ listQuestions.size())*100);
            }
            else pr = (((float) (array.length())/ listQuestions.size())*100);


            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", getIntent().getStringExtra("courseId"));
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleContentId", moduleContentId);
            jsonObject.put("contentType", contentType);
            if (refresh.isEmpty()){
                jsonObject.put("progress", pr);
                jsonObject.put("activityData",array );
            }
            else {
                jsonObject.put("progress", 0);
                jsonObject.put("activityData",new JSONArray() );
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = client.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity,body, sh_Pref);
        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity);
        utils.showLog(TAG, "body -"+ jsonObject.toString());
        client.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject ParentjObject = new JSONObject(res);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {

                            if (refresh.isEmpty())runOnUiThread(() -> finish());

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> utils.dismissDialog());

            }
        });
    }


    private void setFocus(TextView btn_focus) {
        if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ")) {
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


    private void setUnFocusAll() {
        if (btn!=null && btn.length>0) {
            for (TextView aBtn : btn) {
                aBtn.setBackgroundResource(0);
                aBtn.setBackgroundColor(Color.parseColor("#ffffff"));
                aBtn.setFocusable(false);
                if (listQuestions.get(selectedPos).getQType().equalsIgnoreCase("MAQ"))
                    aBtn.setTag("Unselected");
                aBtn.setTextColor(Color.parseColor("#000000"));

            }
        }
        if (tfBtn!=null && tfBtn.length>0) {
            for (TextView aBtn : tfBtn) {
                aBtn.setBackgroundResource(0);
                aBtn.setBackgroundColor(Color.parseColor("#ffffff"));
                aBtn.setFocusable(false);
                aBtn.setTextColor(Color.parseColor("#000000"));

            }
        }
    }

    void showFinishAlert(String message){
        Dialog dialog = new Dialog(KhubQuizActivity.this);
        dialog.setContentView(R.layout.dialog_khub_quiz_finish);
        dialog.setCancelable(false);
        if (dialog.isShowing()) dialog.dismiss();
        dialog.show();
        TextView tvCorrect = dialog.findViewById(R.id.tv_corrct_answer);
        TextView tvWrong = dialog.findViewById(R.id.tv_wrong_answer);
        TextView tvUnAnswered = dialog.findViewById(R.id.tv_unanswered);
        Button btnCompleted = dialog.findViewById(R.id.btn_completed);
        Button btnRetake = dialog.findViewById(R.id.btn_retake);
        if (message.equalsIgnoreCase("finish")){
            btnCompleted.setText("Finish");
        }
        else btnCompleted.setText("Pause");
        int cAnswered = 0, wAnswered=0, unAnswered=0;

        for (KhubQuizQuestions question: listQuestions) {


            if (question.getQType().equalsIgnoreCase("Q&A")
                    || question.getSelectedOption()!=0
                    || question.getSelectedAnswer().length()>0){
                if (question.isCorrect()){
                    cAnswered++;
                }
                else {
                    wAnswered++;
                }
            }
            else {
                unAnswered++;
            }
        }

        tvUnAnswered.setText(""+unAnswered);
        tvCorrect.setText(""+cAnswered);
        tvWrong.setText(""+wAnswered);

        btnCompleted.setOnClickListener(v -> {

            if (btnCompleted.getText().toString().equalsIgnoreCase("finish")){
                dialog.dismiss();
                onBackPressed();
            }
            else {
                dialog.dismiss();
                onBackPressed();
            }

        });

        btnRetake.setOnClickListener(v -> {
            recreate();
            postContentActivity("fresh");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }

    void showFinishAlert1(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(KhubQuizActivity.this);
        builder1.setMessage("You have finished this Activity. Do you want to start Again?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        recreate();
                        postContentActivity("refresh");
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //showing answer dialog
    private void showAnswerDialog() {
        Rect displayRectangle = new Rect();
        Window window = KhubQuizActivity.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog = new Dialog(KhubQuizActivity.this);
        dialog.setContentView(R.layout.dialog_khub_explanation);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog.isShowing()) dialog.dismiss();
        dialog.show();

        CustomWebview wvExplanation = dialog.findViewById(R.id.wv_explanation);

        TextView tvNext = dialog.findViewById(R.id.tv_next);

        wvExplanation.setText(listQuestions.get(selectedPos).getExplanation());

        tvNext.setOnClickListener(view -> {
            dialog.dismiss();
        });

    }

}