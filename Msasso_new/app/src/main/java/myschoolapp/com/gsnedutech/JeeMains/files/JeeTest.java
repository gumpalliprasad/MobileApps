package myschoolapp.com.gsnedutech.JeeMains.files;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.JeeMains.Utils.JeeApiClient;
import myschoolapp.com.gsnedutech.JeeMains.models.QueOptions;
import myschoolapp.com.gsnedutech.JeeMains.models.Question;
import myschoolapp.com.gsnedutech.JeeMains.models.SubmitAnswer;
import myschoolapp.com.gsnedutech.JeeMains.models.UserObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityJeeTestBinding;
import myschoolapp.com.gsnedutech.databinding.JeeDialogAnswerExplanationBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JeeTest extends AppCompatActivity implements View.OnClickListener {

    private ActivityJeeTestBinding binding;
    Dialog dialog;
    Question questionObj;
    String selectedOpt = "";
    String subName, chapName;
    int drawable;
    ApiInterface apiInterface;
    QueOptions selectedOption;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    UserObj userObj;
    private Button[] btn;
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};
    int progress, qsize, qNo;
    boolean isAnswered = false;
    String select = "";
    int pos = 0;

    boolean isNext = false;
    SubmitAnswer submitAnswer;

    Dialog loading;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
        binding = ActivityJeeTestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.jee_sh_pref), MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        String ss = sh_Pref.getString("usrObj", "");
        userObj = new Gson().fromJson(ss, UserObj.class);
        progress = getIntent().getIntExtra("percentage", 0);
        qsize = getIntent().getIntExtra("qsize", 0);
        qNo = getIntent().getIntExtra("qNo", 0);
        apiInterface = JeeApiClient.getClient().create(ApiInterface.class);
        subName = getIntent().getStringExtra("subname");
        chapName = getIntent().getStringExtra("chapname");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.imgSub.setImageDrawable(getDrawable(getIntent().getIntExtra("imgsub",0)));
        }
        else  binding.imgSub.setImageDrawable(getResources().getDrawable(getIntent().getIntExtra("imgsub",0)));
        drawable = getIntent().getIntExtra("drawable", R.drawable.jee_physics_gradient);
        binding.tvSubName.setText(subName);
        binding.tvChapName.setText(chapName);
        binding.tvChaterIndc.setText("" + chapName.charAt(0));
        if (subName.equalsIgnoreCase("physics")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#EB22AF"));
        } else if (subName.equalsIgnoreCase("chemistry")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#FB7900"));
        } else if (subName.equalsIgnoreCase("botany")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#41EB22"));
        } else if (subName.equalsIgnoreCase("zoology")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#FF0000"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.pbTest.setProgressDrawable(getDrawable(drawable));
        }
        binding.pbTest.setProgress(progress);
        binding.prog.setText(progress + "%");
        if (progress > 50) {
            binding.prog.setTextColor(Color.parseColor("#ffffff"));
        } else binding.prog.setTextColor(Color.parseColor("#000000"));
        questionObj = (Question) getIntent().getSerializableExtra("question");
        init();
        loadQuestion();
        binding.btnCheckans.setOnClickListener(view1 -> {
            if (binding.btnCheckans.getText().toString().equals("View Hint")){
                showAnswerDialog();
            }
            else {
                if (questionObj.getqTypeCode().equalsIgnoreCase("ITQ")){
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
            }
        });

        binding.imgBack.setOnClickListener(view1 -> {
            isNext = false;
            onBackPressed();
        });

        binding.imgEmail.setOnClickListener(view1 -> {
            View rootView = getWindow().getDecorView().findViewById(R.id.wv_question);
            sendEmail(rootView);
        });

    }

    private void showResult() {
        SubmitAnswer submitAnswer1 = new SubmitAnswer();
        submitAnswer1.setChapterId(questionObj.getChapterId());
        submitAnswer1.setQuestionRefId(questionObj.get_id());
        if (questionObj.getqTypeCode().equalsIgnoreCase("ITQ")){
            if (Float.parseFloat(binding.etItq.getText().toString())==questionObj.getCorrectAnswer().getAnswer()) {
                submitAnswer1.setCorrect(true);
            } else
                submitAnswer1.setCorrect(false);
            selectedOpt = String.valueOf(Float.parseFloat(binding.etItq.getText().toString()));
            select = String.valueOf((float) questionObj.getCorrectAnswer().getAnswer());
            submitAnswer1.setSelectedOption(binding.etItq.getText().toString());
        }
        else {
            for (int i = 0; i < questionObj.getqOptions().size(); i++) {
                if (questionObj.getqOptions().get(i).isCorrect()) {
                    select = getOption(i + 1);
                    pos = i;
                    break;
                }
            }
            if (select.equalsIgnoreCase(selectedOpt)) {
                submitAnswer1.setCorrect(true);
            } else
                submitAnswer1.setCorrect(false);
            submitAnswer1.setSelectedOption(selectedOption.getOpt());
        }


        submitAnswer1.setqType(questionObj.getqType());
        submitAnswer1.setUid(getIntent().getStringExtra("uid"));
        submitAnswer1.setContentMatrixId(questionObj.getContentMatrixId());
        submitAnswer1.setUserId(getIntent().getStringExtra("uid"));
        Call<JSONObject> call = apiInterface.submitAnswer(submitAnswer1);
        showProgress();
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                if (response.code() == 201) {
                    dismissDialog();
                    submitAnswer = submitAnswer1;
                    showAnswerDialog();
                } else {
                    Toast.makeText(JeeTest.this, "Please try again", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {
                Toast.makeText(JeeTest.this, "Please try again", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
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

    private void showAnswerDialog() {
        dialog = new Dialog(JeeTest.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        JeeDialogAnswerExplanationBinding dView = JeeDialogAnswerExplanationBinding.inflate(getLayoutInflater());
        dialog.setContentView(dView.getRoot());
        dView.tvSubName.setText(subName);

        if (select.equalsIgnoreCase(selectedOpt)) {
            dView.tvWrongAnswer.setText("You got it!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dView.llImg.setBackground(getDrawable(R.drawable.jee_circle_green));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dView.imgCorrectorwrong.setImageDrawable(getDrawable(R.drawable.jee_green));
            }
            dView.tvWrongAnswer.setTextColor(Color.GREEN);
            dView.llWhy.setVisibility(View.GONE);
        } else {
            dView.tvWrongAnswer.setText("You got it wrong");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dView.imgCorrectorwrong.setImageDrawable(getDrawable(R.drawable.ic_close));
            }
            dView.tvWrongAnswer.setTextColor(Color.parseColor("#D90000"));
            dView.llWhy.setVisibility(View.VISIBLE);
        }


        dView.wvWhy.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dView.wvExplanation.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dView.wvReference.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dView.wvOtherPossible.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        dView.wvExplanation.getSettings().setBuiltInZoomControls(true);
        dView.wvExplanation.getSettings().setDisplayZoomControls(false);
        dView.wvReference.getSettings().setBuiltInZoomControls(true);
        dView.wvReference.getSettings().setDisplayZoomControls(false);
        dView.wvOtherPossible.getSettings().setBuiltInZoomControls(true);
        dView.wvOtherPossible.getSettings().setDisplayZoomControls(false);
        dView.imgClose.setOnClickListener(view -> {
            isAnswered = true;
            dialog.dismiss();
            changeUi();
        });

        dView.imgEmail.setOnClickListener(view -> {
            View rootView = dialog.getWindow().getDecorView();
            sendEmail(rootView);
        });

        if (questionObj.getqTypeCode().equalsIgnoreCase("ITQ")){
            dView.tvCorrctAnswer.setText("The correct answer is " + questionObj.getCorrectAnswer().getAnswer());
            dView.llWhy.setVisibility(View.GONE);
            if (questionObj.getCorrectAnswer().getExplanation().equalsIgnoreCase(""))
                dView.llExp.setVisibility(View.GONE);
            else
                dView.wvExplanation.setText(questionObj.getCorrectAnswer().getExplanation());
        }
        else {
            dView.tvCorrctAnswer.setText("The correct answer is " + select);
            if (questionObj.getqOptions().get(pos).getExplanation().equalsIgnoreCase(""))
                dView.llExp.setVisibility(View.GONE);
            else
                dView.wvExplanation.setText(questionObj.getqOptions().get(pos).getExplanation());
            if (selectedOption.getExplanation().equalsIgnoreCase(""))
                dView.llWhy.setVisibility(View.GONE);
            else
                dView.wvWhy.setText(selectedOption.getExplanation());
        }




        if (questionObj.getAdditionalInfo().contains("UNDERLYING CONCEPT")){
            questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("UNDERLYING CONCEPT","<p style=\"color:red;\">Elemental Concept</p>"));
        }
        if (questionObj.getAdditionalInfo().contains("Underlying Concept")){
            questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("Underlying Concept","<p style=\"color:red;\">Elemental Concept</p>"));
        }
        if (questionObj.getAdditionalInfo().contains("Reference with NCERT")){
            questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("Reference with NCERT","<p style=\"color:red;\">NCERT Citation</p>"));
        }
        if (questionObj.getAdditionalInfo().contains("REFERENCE WITH NCERT")){
            questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("REFERENCE WITH NCERT","<p style=\"color:red;\">NCERT Citation</p>"));
        }
        dView.wvReference.setText(questionObj.getAdditionalInfo());
        dView.wvOtherPossible.setText(questionObj.getPossibleCasesContent());
        dView.llExpectedQue.setOnClickListener(view -> {
            Intent i = new Intent(JeeTest.this, JeeExpectedQuestions.class);
            i.putExtra("id", questionObj.get_id());
            i.putExtra("chapter", questionObj.getChapterId());
            i.putExtra("subName", subName);
            i.putExtra("chapName", chapName);
            i.putExtra("contentmatrix", questionObj.getContentMatrixId());
            startActivity(i);
        });

        if (qsize - 1 >= qNo + 1) {
            dView.llNextQue.setText("Next Question");
            dView.llNextQue.setVisibility(View.VISIBLE);
        } else {
            dView.llNextQue.setText("Finish");
        }

        dView.llNextQue.setOnClickListener(view -> {
            dialog.dismiss();
            isNext = true;
            onBackPressed();
        });
        dView.imgBack.setOnClickListener(view -> {
            isNext = false;
            onBackPressed();
        });
        if (dialog.isShowing()) dialog.dismiss();
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
                isNext = false;
                onBackPressed();
            }
        });
    }

    private void changeUi() {
        if(questionObj.getqTypeCode().equalsIgnoreCase("ITQ")){
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
        switch (i) {
            case 1:
                option = "1";
                break;
            case 2:
                option = "2";
                break;
            case 3:
                option = "3";
                break;
            case 4:
                option = "4";
                break;

        }
        return option;
    }

    private void init() {

//        String mainString = "<!DOCTYPE html> <html>" + questionObj.getqName() +
//                questionObj.getqOptions().get(0)
//                +  questionObj.getqOptions().get(1)
//                +  questionObj.getqOptions().get(2)
//                + questionObj.getqOptions().get(3)
//                + "</html>";
//        mainString = mainString.replaceAll("<span.*?>", "");
//        mainString = mainString.replaceAll("&#39;", "");
//        binding.wvTest.loadData(mainString, "text/html; charset=utf-8", "utf-8");
//        Log.v("rr", "QueType - " + mainString);

        if (questionObj.getqAppearace().size() > 0) {
            binding.tvQappearance.setVisibility(View.VISIBLE);
            binding.tvQappearance.setText(questionObj.getqAppearace().get(0));
        } else binding.tvQappearance.setVisibility(View.GONE);

//        binding.wvQuestion.setText(questionObj.getqName());

//            binding.wvOpt1.getSettings().setTextZoom(150);
//            binding.wvOpt2.getSettings().setTextZoom(150);
//            binding.wvOpt3.getSettings().setTextZoom(150);
//            binding.wvOpt4.getSettings().setTextZoom(150);

        btn =new Button[questionObj.getqOptions().size()];

        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
//            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            btn[i].setOnClickListener(JeeTest.this);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_a:
                setUnFocusAll();
                setFocus(btn[0]);
                selectedOpt = getOption(1);
                selectedOption = questionObj.getqOptions().get(0);
                break;
            case R.id.btn_b:
                setUnFocusAll();
                setFocus(btn[1]);
                selectedOpt = getOption(2);
                selectedOption = questionObj.getqOptions().get(1);
                break;
            case R.id.btn_c:
                setUnFocusAll();
                setFocus(btn[2]);
                selectedOpt = getOption(3);
                selectedOption = questionObj.getqOptions().get(2);
                break;
            case R.id.btn_d:
                setUnFocusAll();
                setFocus(btn[3]);
                selectedOpt = getOption(4);
                selectedOption = questionObj.getqOptions().get(3);
                break;
        }
    }

//    private void unselectAll() {
//        binding.llOpt1Sel.setBackground(getDrawable(R.drawable.circle_white));
//        binding.tvOpt1Sel.setTextColor(Color.BLACK);
//        binding.tvOpt1Sel.setBackground(null);
//        binding.llOpt2Sel.setBackground(getDrawable(R.drawable.circle_white));
//        binding.tvOpt2Sel.setTextColor(Color.BLACK);
//        binding.tvOpt2Sel.setBackground(null);
//        binding.llOpt3Sel.setBackground(getDrawable(R.drawable.circle_white));
//        binding.tvOpt3Sel.setTextColor(Color.BLACK);
//        binding.tvOpt3Sel.setBackground(null);
//        binding.llOpt4Sel.setBackground(getDrawable(R.drawable.circle_white));
//        binding.tvOpt4Sel.setTextColor(Color.BLACK);
//        binding.tvOpt4Sel.setBackground(null);
//    }
//
//    private void selected(int i) {
//        selectedOption = questionObj.getqOptions().get(i-1);
//        selectedOpt = getOption(i);
//        binding.llSubmit.setVisibility(View.VISIBLE);
//        switch (i){
//            case 1:
//                binding.llOpt1Sel.setBackground(getDrawable(R.drawable.circle_white_border));
//                binding.tvOpt1Sel.setTextColor(Color.WHITE);
//                binding.tvOpt1Sel.setBackground(getDrawable(R.drawable.circle_black));
//                break;
//            case 2:
//                binding.llOpt2Sel.setBackground(getDrawable(R.drawable.circle_white_border));
//                binding.tvOpt2Sel.setTextColor(Color.WHITE);
//                binding.tvOpt2Sel.setBackground(getDrawable(R.drawable.circle_black));
//                break;
//            case 3:
//                binding.llOpt3Sel.setBackground(getDrawable(R.drawable.circle_white_border));
//                binding.tvOpt3Sel.setTextColor(Color.WHITE);
//                binding.tvOpt3Sel.setBackground(getDrawable(R.drawable.circle_black));
//                break;
//            case 4:
//                binding.llOpt4Sel.setBackground(getDrawable(R.drawable.circle_white_border));
//                binding.tvOpt4Sel.setTextColor(Color.WHITE);
//                binding.tvOpt4Sel.setBackground(getDrawable(R.drawable.circle_black));
//                break;
//        }
//    }


    public void loadQuestion() {
//        Log.v(TAG, "queIndex - " + que_index);

//        que_NO = que_NO + 1;
        if (questionObj.getqTypeCode().equalsIgnoreCase("ITQ")){
            binding.llMcq.setVisibility(View.GONE);
            binding.llItq.setVisibility(View.VISIBLE);
            binding.wvQuestion.setVisibility(View.VISIBLE);
            binding.wvQuestion.getSettings().setBuiltInZoomControls(true);
            binding.wvQuestion.getSettings().setDisplayZoomControls(false);
            binding.llOption.removeAllViews();
            binding.etItq.setText("");


            String mainString = "<!DOCTYPE html> <html>" + questionObj.getqName()+ "</html>";
            mainString = utils.cleanWebString(mainString);
            binding.wvQuestion.loadData(mainString, "text/html; charset=utf-8", "utf-8");

            binding.wvQuestion.scrollTo(0, 0);
        }
        else {
            setUnFocusAll();

            binding.llMcq.setVisibility(View.VISIBLE);
            binding.llItq.setVisibility(View.GONE);
            binding.wvQuestion.setVisibility(View.VISIBLE);
            binding.wvQuestion.getSettings().setBuiltInZoomControls(true);
            binding.wvQuestion.getSettings().setDisplayZoomControls(false);

            StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + questionObj.getqName());

            for (int i = 0; i < btn.length; i++) {
                btn[i].setEnabled(true);
                btn[i].setVisibility(View.VISIBLE);
                mainString.append("<fieldset><legend>").append(i + 1).append(")</legend>").append(questionObj.getqOptions().get(i).getOpt()).append("</fieldset>");
            }
            mainString.append("</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            binding.wvQuestion.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
        }


//        Log.v(TAG, "QueType - " + mainString);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(JeeTest.this, JeeChapterQuestions.class);
        if (submitAnswer!=null &&!submitAnswer.getQuestionRefId().isEmpty()){
            i.putExtra("submit",submitAnswer);
        }
        i.putExtra("isNext", isNext);
        setResult(4321, i);
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

    }

    MyUtils utils = new MyUtils();
    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();
    }
}