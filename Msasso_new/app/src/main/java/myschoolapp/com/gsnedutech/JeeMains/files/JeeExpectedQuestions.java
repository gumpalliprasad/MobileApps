package myschoolapp.com.gsnedutech.JeeMains.files;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.JeeMains.Utils.JeeApiClient;
import myschoolapp.com.gsnedutech.JeeMains.models.QueOptions;
import myschoolapp.com.gsnedutech.JeeMains.models.Question;
import myschoolapp.com.gsnedutech.JeeMains.models.SubmitExpectedQuestion;
import myschoolapp.com.gsnedutech.JeeMains.models.UserObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityJeeExcepectedQuestionsBinding;
import myschoolapp.com.gsnedutech.databinding.JeeDialogExpectedAnswerBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JeeExpectedQuestions extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG =  JeeExpectedQuestions.class.getName();

    private ActivityJeeExcepectedQuestionsBinding binding;
    ApiInterface apiInterface;

    ArrayList<Question> questions = new ArrayList<>();


    private Button[] btn;
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    SharedPreferences sh_Pref;
    UserObj uObj;
    String contentType, topicId;


    int que_index = 0, que_NO = 0;
    QueOptions selectedOption;

    String selectedOpt="";
    String questionRefId="";
    String chapterId="", contentMatrixId="";
    String select = "";
    int pos = 0;



    String subName, chapName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJeeExcepectedQuestionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        apiInterface = JeeApiClient.getClient().create(ApiInterface.class);
        subName = getIntent().getStringExtra("subName");
        chapName = getIntent().getStringExtra("chapName");
        init();

        questionRefId = getIntent().getStringExtra("id");
        chapterId = getIntent().getStringExtra("chapter");
        contentMatrixId = getIntent().getStringExtra("contentmatrix");
        getExpectedQuestions(questionRefId);

        binding.imgBack.setOnClickListener(view1 -> onBackPressed());

        binding.imgEmail.setOnClickListener(view1 -> {
            View rootView = getWindow().getDecorView().findViewById(R.id.wv_test);
            sendEmail(rootView);
        });
    }

    private void sendEmail(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        File file = new File(getFilesDir(),"temp");
        if (!file.exists()) {
            file.mkdir();
        }
        File gpxfile = null;
        try {
            gpxfile = new File(file, "question.jpg");
            if(gpxfile.exists()){
                file.delete();
                gpxfile = new File(file, "question.jpg");
            }
            try {
                FileOutputStream fos = new FileOutputStream(gpxfile);
                rootView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {
                Log.e("GREC", e.getMessage(), e);
            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
            }
//            Toast.makeText(getActivity(), "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }
        Uri URI = null;
        if (gpxfile!=null){
            URI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", gpxfile);
        }

        String extraText = "Subject : "+subName+"\n";
        extraText = extraText + "Chapter : "+chapName+"\n";
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.jee_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_TEXT,extraText);

        if (URI!=null){
            intent.putExtra(Intent.EXTRA_STREAM,URI);
        }
        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(getPackageManager())!=null)
            startActivity(intent);
        else
            Toast.makeText(this,"Gmail App is not installed",Toast.LENGTH_SHORT).show();
    }

    private void getExpectedQuestions(String id) {
        showProgress();
        Call<ArrayList<Question>> call = apiInterface.getExpectedQuestions(id,true);
        call.enqueue(new Callback<ArrayList<Question>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Question>> call, @NotNull Response<ArrayList<Question>> response) {
                if (response.body()!=null && response.body().size()>0){
                    questions = response.body();
                    loadQuestion(que_index);
                    new Handler().postDelayed(() -> dismissDialog(),1000);
                }
                else {
                    binding.imgEmail.setVisibility(View.GONE);
                    binding.tvNoavailable.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> dismissDialog(),1000);
                }

            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<Question>> call, @NotNull Throwable t) {
                new Handler().postDelayed(() -> dismissDialog(),1000);
                binding.imgEmail.setVisibility(View.GONE);
                binding.tvNoavailable.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init() {

        Intent intent = getIntent();
        contentType = intent.getStringExtra("contentType");
        topicId = intent.getStringExtra("topicId");
        que_NO = intent.getIntExtra("que_NO", 0);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.jee_sh_pref), MODE_PRIVATE);


        Gson gson = new Gson();
        String json = sh_Pref.getString("usrObj", "");
        uObj = gson.fromJson(json, UserObj.class);


        binding.btnCheckans.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_a:
                setUnFocusAll();
                setFocus(btn[0]);
                selectedOpt = getOption(1);
                selectedOption = questions.get(que_index).getqOptions().get(0);
                break;
            case R.id.btn_b:
                setUnFocusAll();
                setFocus(btn[1]);
                selectedOpt = getOption(2);
                selectedOption = questions.get(que_index).getqOptions().get(1);
                break;
            case R.id.btn_c:
                setUnFocusAll();
                setFocus(btn[2]);
                selectedOpt = getOption(3);
                selectedOption = questions.get(que_index).getqOptions().get(2);
                break;
            case R.id.btn_d:
                setUnFocusAll();
                setFocus(btn[3]);
                selectedOpt = getOption(4);
                selectedOption = questions.get(que_index).getqOptions().get(3);
                break;
            case R.id.btn_checkans:
                if (binding.btnCheckans.getText().toString().equals("View Hint")){
                    showAnswerDialog();
                }
                else {
                    if (questions.get(que_index).getqTypeCode().equalsIgnoreCase("ITQ")){
                        if (binding.etItq.getText().toString().trim().isEmpty()){
                            Toast.makeText(this, "Please Enter some thing", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            showResult();
                        }
                    }
                    else {
                        if (selectedOpt.isEmpty()) {
                            Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
                        } else
                            showResult();
                    }
//                    if (selectedOpt.isEmpty() || selectedOption == null) {
//                        Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
//                    } else
//                        showResult();
                }

                break;
            case R.id.btn_next:
                que_index = que_index+1;
                loadQuestion(que_index);
                break;
        }
    }

    private void showResult() {
//        for(int i=0; i<questions.get(que_index).getqOptions().size();i++){
//            if(questions.get(que_index).getqOptions().get(i).isCorrect()){
//                select = getOption(i+1);
//                pos = i;
//                break;
//            }
//        }

        SubmitExpectedQuestion submitAnswer = new SubmitExpectedQuestion();
        submitAnswer.setChapterId(chapterId);
        submitAnswer.setQuestionRefId(questionRefId);

        if (questions.get(que_index).getqTypeCode().equalsIgnoreCase("ITQ")){
            if (Float.parseFloat(binding.etItq.getText().toString())==questions.get(que_index).getCorrectAnswer().getAnswer()) {
                submitAnswer.setCorrect(true);
            } else
                submitAnswer.setCorrect(false);
            selectedOpt = String.valueOf(Float.parseFloat(binding.etItq.getText().toString()));
            select = String.valueOf((float) questions.get(que_index).getCorrectAnswer().getAnswer());
            submitAnswer.setSelectedOption(binding.etItq.getText().toString());
        }
        else {
            for (int i = 0; i < questions.get(que_index).getqOptions().size(); i++) {
                if (questions.get(que_index).getqOptions().get(i).isCorrect()) {
                    select = getOption(i + 1);
                    pos = i;
                    break;
                }
            }
            if (select.equalsIgnoreCase(selectedOpt)) {
                submitAnswer.setCorrect(true);
            } else
                submitAnswer.setCorrect(false);
            submitAnswer.setSelectedOption(selectedOption.getOpt());
        }


        submitAnswer.setqType(questions.get(que_index).getqType());
        submitAnswer.setQuestionId(questions.get(que_index).get_id());
        submitAnswer.setUid(uObj.getUid());
        submitAnswer.setContentMatrixId(contentMatrixId);
        submitAnswer.setUserId(uObj.get_id());
        Call<JSONObject> call = apiInterface.submitExpectedQuestion(submitAnswer);
        int finalPos = pos;
        String finalSelect = select;
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                if(response.code()==201){
                    showAnswerDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {

            }
        });



    }

    private void showAnswerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(JeeExpectedQuestions.this);
        JeeDialogExpectedAnswerBinding dView = JeeDialogExpectedAnswerBinding.inflate(getLayoutInflater());
        builder.setView(dView.getRoot());
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        dView.wvExplanation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dView.wvHint.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        dView.wvWhy.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        dView.wvExplanation.getSettings().setBuiltInZoomControls(true);
        dView.wvExplanation.getSettings().setDisplayZoomControls(false);
        dView.wvHint.getSettings().setBuiltInZoomControls(true);
        dView.wvHint.getSettings().setDisplayZoomControls(false);
        dView.wvWhy.getSettings().setBuiltInZoomControls(true);
        dView.wvWhy.getSettings().setDisplayZoomControls(false);

        dView.imgEmail.setOnClickListener(view -> {
            View rootView = alertDialog.getWindow().getDecorView();
            sendEmail(rootView);
        });

        if(select.equalsIgnoreCase(selectedOpt)){
            dView.tvWrongAnswer.setText("You got it!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dView.llImg.setBackground(getDrawable(R.drawable.jee_circle_green));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dView.imgCorrectorwrong.setImageDrawable(getDrawable(R.drawable.jee_green));
            }
            dView.tvWrongAnswer.setTextColor(Color.GREEN);
            dView.llWhy.setVisibility(View.GONE);
        }
        else {
            dView.tvWrongAnswer.setText("You got it wrong");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dView.imgCorrectorwrong.setImageDrawable(getDrawable(R.drawable.ic_close));
            }
            dView.tvWrongAnswer.setTextColor(Color.parseColor("#D90000"));
            dView.llWhy.setVisibility(View.VISIBLE);
        }

        if (questions.get(que_index).getqTypeCode().equalsIgnoreCase("ITQ")){
            dView.llWhy.setVisibility(View.GONE);
            if (questions.get(que_index).getCorrectAnswer().getExplanation()!=null){
                dView.llExplanation.setVisibility(View.VISIBLE);
                dView.wvHint.setText(questions.get(que_index).getCorrectAnswer().getExplanation());
            }
            else {
                dView.llExplanation.setVisibility(View.GONE);
            }
            dView.tvCorrctAnswer.setText("The correct answer is "+ questions.get(que_index).getCorrectAnswer().getAnswer());
        }
        else {
            if (selectedOption.getExplanation()!=null && !(selectedOption.getExplanation().equalsIgnoreCase("")))
                dView.wvWhy.setText(selectedOption.getExplanation());
            else {
                dView.llWhy.setVisibility(View.GONE);
            }

            if (questions.get(que_index).getqHint() != null || !(questions.get(que_index).getqHint().equalsIgnoreCase(""))) {
                dView.llExplanation.setVisibility(View.VISIBLE);
                dView.wvHint.setText(questions.get(que_index).getqHint());
            }
            else {
                dView.llExplanation.setVisibility(View.GONE);
            }

            if (questions.get(que_index).getqOptions().get(pos).getExplanation()!=null && !questions.get(que_index).getqOptions().get(pos).getExplanation().isEmpty() ){
                dView.llExp.setVisibility(View.VISIBLE);
                dView.wvExplanation.setText(questions.get(que_index).getqOptions().get(pos).getExplanation());
            }
            else {
                dView.llExp.setVisibility(View.GONE);
            }
            dView.tvCorrctAnswer.setText("The correct answer is "+ select);
        }


        dView.imgClose.setOnClickListener(view -> {
            alertDialog.dismiss();
            changeUi();
        });




        dView.llNext.setOnClickListener(view -> {
            que_index = que_index+1;
            loadQuestion(que_index);
            alertDialog.dismiss();
        });


        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
    }

    private void changeUi() {
        if(questions.get(que_index).getqTypeCode().equalsIgnoreCase("ITQ")){
            binding.etItq.setEnabled(false);
        }
        else {
            for (Button aBtn : btn) {
                aBtn.setEnabled(false);
                if (aBtn.getCurrentTextColor() == Color.parseColor("#4a4a4a")) {
                    aBtn.setBackgroundResource(R.drawable.practice_btn_opt);
                    aBtn.setFocusable(true);
                    aBtn.setTextColor(Color.parseColor("#4a4a4a"));
                } else {
                    aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
                    aBtn.setFocusable(false);
                    aBtn.setTextColor(Color.parseColor("#444444"));
                }
            }
        }

        binding.btnCheckans.setText("View Hint");
    }


    private String getOption(int i) {
        String option = "";
        switch (i){
            case 1: option = "1";
                break;
            case 2: option = "2";
                break;
            case 3: option = "3";
                break;
            case 4: option = "4";
                break;

        }
        return option;
    }







    public void loadQuestion(int que_index) {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (que_index < questions.size()) {
            if (questions.get(que_index).getqTypeCode().equalsIgnoreCase("ITQ")){
                binding.llMcq.setVisibility(View.GONE);
                binding.llItq.setVisibility(View.VISIBLE);
                binding.wvTest.setVisibility(View.VISIBLE);
                binding.wvTest.getSettings().setBuiltInZoomControls(true);
                binding.wvTest.getSettings().setDisplayZoomControls(false);
                binding.llOption.removeAllViews();
                binding.etItq.setText("");
                binding.etItq.setEnabled(true);
                binding.btnCheckans.setText("Check Answer");

                if (questions.size() > que_index + 1) {
                    binding.btnNext.setVisibility(View.VISIBLE);
                } else binding.btnNext.setVisibility(View.GONE);


                String mainString = "<!DOCTYPE html> <html>" + questions.get(que_index).getqName()+ "</html>";
                mainString = utils.cleanWebString(mainString);
                binding.wvTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");

                binding.wvTest.scrollTo(0, 0);
            }
            else {
                binding.llMcq.setVisibility(View.VISIBLE);
                binding.llItq.setVisibility(View.GONE);
                binding.wvTest.setVisibility(View.VISIBLE);

                if (que_index < questions.size()) {
                    Log.v(TAG, "queIndex - " + que_index);

                    if (questions.size() > que_index + 1) {
                        binding.btnNext.setVisibility(View.VISIBLE);
                    } else binding.btnNext.setVisibility(View.GONE);

                    select = "";
                    pos = 0;
                    binding.btnCheckans.setText("Check Answer");
                    que_NO = que_index + 1;
                    selectedOpt = "";
                    selectedOption = null;

                    checkbuttons();
                    initOptions(questions.get(que_index));
                    setUnFocusAll();

                    binding.queNo.setText("Question " + que_NO + "/" + questions.size());
                    binding.llMcq.setVisibility(View.VISIBLE);
                    binding.wvTest.setVisibility(View.VISIBLE);
                    binding.wvTest.getSettings().setBuiltInZoomControls(true);
                    binding.wvTest.getSettings().setDisplayZoomControls(false);

                    StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + questions.get(que_index).getqName());
                    for (int i = 0; i < btn.length; i++) {
                        btn[i].setEnabled(true);
                        btn[i].setVisibility(View.VISIBLE);
                        mainString.append("<fieldset><legend>").append(i + 1).append(")</legend>").append(questions.get(que_index).getqOptions().get(i).getOpt()).append("</fieldset>");
                    }
                    mainString.append("</html>");
                    mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
                    binding.wvTest.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
                    Log.v(TAG, "QueType - " + mainString);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JeeExpectedQuestions.this);
                    JeeDialogExpectedAnswerBinding dView = JeeDialogExpectedAnswerBinding.inflate(getLayoutInflater());
                    builder.setView(dView.getRoot());
                    builder.setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);


                    alertDialog.show();

                    dView.llExplanation.setVisibility(View.GONE);
                    dView.tvWrongAnswer.setVisibility(View.GONE);
                    dView.imgClose.setVisibility(View.GONE);
                    dView.tvCorrctAnswer.setText("You have attempted all the expected questions");
                    dView.tvDone.setText("Done");
                    dView.llNext.setOnClickListener(view -> {
                        alertDialog.dismiss();
                        finish();
                    });


                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });
                }
            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(JeeExpectedQuestions.this);
            JeeDialogExpectedAnswerBinding dView = JeeDialogExpectedAnswerBinding.inflate(getLayoutInflater());
            builder.setView(dView.getRoot());
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);


            alertDialog.show();

            dView.llExplanation.setVisibility(View.GONE);
            dView.tvWrongAnswer.setVisibility(View.GONE);
            dView.imgClose.setVisibility(View.GONE);
            dView.tvCorrctAnswer.setText("You have attempted all the expected questions");
            dView.tvDone.setText("Done");
            dView.llNext.setOnClickListener(view -> {
                alertDialog.dismiss();
                finish();
            });


            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    finish();
                }
            });
        }



    }

    private void checkbuttons() {
        if (btn!=null) {
            for (Button aBtn : btn) {
                aBtn.setVisibility(View.GONE);
            }
        }
    }

    private void initOptions(Question question) {
        btn = new Button[question.getqOptions().size()];
        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setVisibility(View.GONE);
            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            btn[i].setOnClickListener(JeeExpectedQuestions.this);
        }
    }

    private void setFocus(Button btn_focus) {
        btn_focus.setBackgroundResource(R.drawable.practice_btn_opt);
        btn_focus.setFocusable(true);
        btn_focus.setTextColor(Color.parseColor("#4a4a4a"));
    }

    private void setUnFocusAll() {
        for (Button aBtn : btn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setFocusable(false);
            aBtn.setTextColor(Color.parseColor("#444444"));

        }
    }

    MyUtils utils = new MyUtils();
    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
