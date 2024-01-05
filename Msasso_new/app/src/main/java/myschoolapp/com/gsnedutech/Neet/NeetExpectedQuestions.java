package myschoolapp.com.gsnedutech.Neet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiClient;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiInterface;
import myschoolapp.com.gsnedutech.Neet.models.NeetQueOptions;
import myschoolapp.com.gsnedutech.Neet.models.NeetQuestion;
import myschoolapp.com.gsnedutech.Neet.models.NeetSubmitExpectedQuestion;
import myschoolapp.com.gsnedutech.Neet.models.UserObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityNeetExcepectedQuestionsBinding;
import myschoolapp.com.gsnedutech.databinding.NeetDialogExpectedAnswerBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NeetExpectedQuestions extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG =  NeetExpectedQuestions.class.getName();

    private ActivityNeetExcepectedQuestionsBinding binding;
    NeetApiInterface apiInterface;

    ArrayList<NeetQuestion> questions = new ArrayList<>();


    private Button[] btn;
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};

    SharedPreferences sh_Pref;
    UserObj uObj;
    String contentType, topicId;


    int que_index = 0, que_NO = 0;
    NeetQueOptions selectedOption;

    String selectedOpt="";
    String questionRefId="";
    String chapterId="", contentMatrixId="";
    String select = "";
    int pos = 0;

    Dialog loading;
    private LottieAnimationView animationView;

    String subName, chapName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNeetExcepectedQuestionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        apiInterface = NeetApiClient.getClient().create(NeetApiInterface.class);
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
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sri.niceguy@gmail.com"});
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
        Call<ArrayList<NeetQuestion>> call = apiInterface.getExpectedQuestions(id,true);
        call.enqueue(new Callback<ArrayList<NeetQuestion>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<NeetQuestion>> call, @NotNull Response<ArrayList<NeetQuestion>> response) {
                if (response.body()!=null && response.body().size()>0){
                    questions = response.body();
                    loadQuestion(que_index);
                    new Handler().postDelayed(() -> dismissDialog(),1000);
                }
                else {
                    binding.tvNoavailable.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(() -> dismissDialog(),1000);
                }

            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<NeetQuestion>> call, @NotNull Throwable t) {
                new Handler().postDelayed(() -> dismissDialog(),1000);
                binding.tvNoavailable.setVisibility(View.VISIBLE);
            }
        });
    }

    private void init() {

        Intent intent = getIntent();
        contentType = intent.getStringExtra("contentType");
        topicId = intent.getStringExtra("topicId");
        que_NO = intent.getIntExtra("que_NO", 0);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.neet_sh_pref), MODE_PRIVATE);


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
                    if (selectedOpt.isEmpty() || selectedOption == null) {
                        Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
                    } else
                        showResult();
                }

                break;
            case R.id.btn_next:
                que_index = que_index+1;
                loadQuestion(que_index);
                break;
        }
    }

    private void showResult() {
        for(int i=0; i<questions.get(que_index).getqOptions().size();i++){
            if(questions.get(que_index).getqOptions().get(i).isCorrect()){
                select = getOption(i+1);
                pos = i;
                break;
            }
        }

        NeetSubmitExpectedQuestion submitAnswer = new NeetSubmitExpectedQuestion();
        submitAnswer.setChapterId(chapterId);
        submitAnswer.setQuestionRefId(questionRefId);
        submitAnswer.setQuestionId(questions.get(que_index).get_id());
        if(select.equalsIgnoreCase(selectedOpt)){
            submitAnswer.setCorrect(true);
        }
        else
            submitAnswer.setCorrect(false);
        submitAnswer.setSelectedOption(selectedOption.getOpt());
        submitAnswer.setUid(uObj.getUid());
        submitAnswer.setContentMatrixId(contentMatrixId);
        submitAnswer.setUserId(uObj.getId());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(NeetExpectedQuestions.this);
        NeetDialogExpectedAnswerBinding dView = NeetDialogExpectedAnswerBinding.inflate(getLayoutInflater());
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
            dView.llImg.setBackground(getDrawable(R.drawable.jee_circle_green));
            dView.imgCorrectorwrong.setImageDrawable(getDrawable(R.drawable.jee_green));
            dView.tvWrongAnswer.setTextColor(Color.GREEN);
            dView.llWhy.setVisibility(View.GONE);
        }
        else {
            dView.tvWrongAnswer.setText("You got it wrong");
            dView.imgCorrectorwrong.setImageDrawable(getDrawable(R.drawable.ic_close));
            dView.tvWrongAnswer.setTextColor(Color.parseColor("#D90000"));
            dView.llWhy.setVisibility(View.VISIBLE);
        }

        if (selectedOption.getExplanation().equalsIgnoreCase(""))
            dView.llWhy.setVisibility(View.GONE);
        else {
            dView.wvWhy.setText(selectedOption.getExplanation());
        }

        if (questions.get(que_index).getqHint().equalsIgnoreCase(""))
            dView.llExplanation.setVisibility(View.GONE);
        else {
            dView.llExplanation.setVisibility(View.VISIBLE);
            dView.wvHint.setText(questions.get(que_index).getqHint());
        }

        if (questions.get(que_index).getqOptions().get(pos).getExplanation()!=null && !questions.get(que_index).getqOptions().get(pos).getExplanation().isEmpty() ){
            dView.llExp.setVisibility(View.VISIBLE);
            dView.wvExplanation.setText(questions.get(que_index).getqOptions().get(pos).getExplanation());
        }
        else {
            dView.llExp.setVisibility(View.GONE);
        }

        dView.imgClose.setOnClickListener(view -> {
            alertDialog.dismiss();
            changeUi();
        });



        dView.tvCorrctAnswer.setText("The correct answer is "+ select);
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
        for (Button aBtn : btn) {
            aBtn.setEnabled(false);
            binding.btnCheckans.setText("View Hint");
            if (aBtn.getCurrentTextColor()==Color.parseColor("#4a4a4a")){
                aBtn.setBackgroundResource(R.drawable.practice_btn_opt);
                aBtn.setFocusable(true);
                aBtn.setTextColor(Color.parseColor("#4a4a4a"));
            }
            else {
                aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
                aBtn.setFocusable(false);
                aBtn.setTextColor(Color.parseColor("#444444"));
            }
        }
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

        if(que_index<questions.size()) {
            Log.v(TAG, "queIndex - " + que_index);

            if(questions.size()>que_index+1){
                binding.btnNext.setVisibility(View.VISIBLE);
            }
            else binding.btnNext.setVisibility(View.GONE);

            select = "";
            pos = 0;
            binding.btnCheckans.setText("Check Answer");
            que_NO = que_index + 1;
            selectedOpt ="";
            selectedOption=null;

            checkbuttons();
            initOptions(questions.get(que_index));
            setUnFocusAll();

            binding.queNo.setText("Question "+que_NO+"/"+questions.size());
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
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(NeetExpectedQuestions.this);
            NeetDialogExpectedAnswerBinding dView = NeetDialogExpectedAnswerBinding.inflate(getLayoutInflater());
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

    private void initOptions(NeetQuestion question) {
        btn = new Button[question.getqOptions().size()];
        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setVisibility(View.GONE);
            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            btn[i].setOnClickListener(NeetExpectedQuestions.this);
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
}
