package myschoolapp.com.gsnedutech.JeeAdvanced.Files;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeParagraphQues;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeQueOptions;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeQuestion;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubmitAnswer;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityAdvJeeTestBinding;
import myschoolapp.com.gsnedutech.databinding.AdvjeeDialogAnswerExplanationBinding;
import myschoolapp.com.gsnedutech.databinding.DialogPartialMarkingBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.cloudedz.jeeadvanced.models.UserObj;

public class AdvJeeTest extends AppCompatActivity implements View.OnClickListener {

    private ActivityAdvJeeTestBinding binding;
    MyUtils utils = new MyUtils();
    Dialog dialog;
    AdvJeeQuestion questionObj;
    String selectedOpt = "";
    String subName, chapName;
    int drawable;
    ApiInterface apiInterface;
    AdvJeeQueOptions selectedOption;
    ArrayList<AdvJeeQueOptions> maqSelectOptions = new ArrayList<>();
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
//    UserObj userObj;
    private Button[] btn;
    private int[] btn_id = {R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn_d};
    int progress, qsize, qNo;
    boolean isAnswered = false;
    String select = "";
    int pos = 0;
    int que_index = 0, que_NO = 0;
    boolean isNext = false;
    AdvJeeSubmitAnswer submitAnswer;
    String maqans = "";
    String[] paraquestions;

    Dialog loading;
    private LottieAnimationView animationView;

    SharedPreferences shPref;
    StudentObj sObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdvJeeTestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.adv_sh_pref), MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        toEdit.apply();
        String ss = sh_Pref.getString("usrObj", "");
//        userObj = new Gson().fromJson(ss, UserObj.class);

        shPref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shPref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        progress = getIntent().getIntExtra("percentage", 0);
        qsize = getIntent().getIntExtra("qsize", 0);
        qNo = getIntent().getIntExtra("qNo", 0);
        apiInterface = ApiClient.getAdvJeeClient().create(ApiInterface.class);
        subName = getIntent().getStringExtra("subname");
        chapName = getIntent().getStringExtra("chapname");
        binding.imgSub.setImageDrawable(ContextCompat.getDrawable(AdvJeeTest.this, getIntent().getIntExtra("imgsub", 0)));
        drawable = getIntent().getIntExtra("drawable", R.drawable.jee_physics_gradient);
        binding.tvSubName.setText(subName);
        binding.tvChapName.setText(chapName);
        if (chapName != null && !chapName.equalsIgnoreCase(""))
            binding.tvChaterIndc.setText(MessageFormat.format("{0}", chapName.charAt(0)));
        if (subName.equalsIgnoreCase("physics")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#EB22AF"));
        } else if (subName.equalsIgnoreCase("chemistry")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#FB7900"));
        } else if (subName.equalsIgnoreCase("botany")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#41EB22"));
        } else if (subName.equalsIgnoreCase("zoology")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#FF0000"));
        }
        binding.pbTest.setProgressDrawable(ContextCompat.getDrawable(AdvJeeTest.this, drawable));
        binding.pbTest.setProgress(progress);
        binding.prog.setText(MessageFormat.format("{0}%", progress));
        if (progress > 50) {
            binding.prog.setTextColor(Color.parseColor("#ffffff"));
        } else binding.prog.setTextColor(Color.parseColor("#000000"));
        questionObj = AdvJeeSubjectsActivity.quesString;
        init();
        loadQuestion();
        binding.btnCheckans.setOnClickListener(view1 -> {
            if (binding.btnCheckans.getText().toString().equals("View Hint")) {
                showAnswerDialog();
            } else {
                if (questionObj.getQuestionTypeCode().equalsIgnoreCase("ITQ")) {
                    if (binding.etItq.getText().toString().trim().isEmpty()) {
                        Toast.makeText(this, "Please Enter some thing", Toast.LENGTH_SHORT).show();
                    } else {
                        showResult();
                    }
                } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
                    int selcnt = 0;
                    for (Button button : btn) {
                        if (button.getTag().equals("selected")) {
                            selcnt++;
                        }
                    }
                    if (selcnt > 0) {
                        showResult();
                    } else {
                        Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
                    }
                } else {
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


    private void init() {

        if (questionObj.isPartialmarking()) {
            binding.tvQappearance.setVisibility(View.VISIBLE);
        } else binding.tvQappearance.setVisibility(View.GONE);
        if (questionObj.isNegativeMarkingEnabled()) {
            binding.tvNegativemark.setText(questionObj.getNegativemarks() > 0 ? "-" + questionObj.getNegativemarks() : "0");
        } else {
            binding.tvNegativemark.setText("0");
        }
        binding.queType.setText(questionObj.getQuestionTypeCode());
        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
            binding.tvParaTotmarks.setVisibility(View.VISIBLE);
            binding.tvParaTotmarks.setText(MessageFormat.format("Total Marks : {0}", questionObj.getTotalQuestionMarks()));
            binding.tvTotalmarks.setText(MessageFormat.format("{0}", questionObj.getParagraphQuestions().get(que_index).getMarks()));
        } else {
            binding.tvTotalmarks.setText(MessageFormat.format("{0}", questionObj.getTotalQuestionMarks()));
        }

        binding.tvQappearance.setOnClickListener(view -> {
            if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ"))
                showPartialDialog();
        });

        btn = new Button[questionObj.getOptions().size()];

        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ"))
                btn[i].setTag("Unselected");
            btn[i].setOnClickListener(AdvJeeTest.this);
        }
    }

    private void showPartialDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AdvJeeTest.this);
        DialogPartialMarkingBinding dView = DialogPartialMarkingBinding.inflate(getLayoutInflater());
        builder.setView(dView.getRoot());
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
        dView.tvPartialMark.setText(MessageFormat.format("{0}", "Full Marks : +" + questionObj.getTotalQuestionMarks() + " If only the bubble(s) corresponding to all the correct option(s) is(are) darkened\n" +
                "\n" +
                "Partial Marks : +1 For darkening a bubble corresponding to each correct option, provided NO incorrect option is darkened\n\n" +
                "Zero Marks : 0 If none of the bubbles is darkened\n\n" +
                "Negative Marks : " + (questionObj.getNegativemarks() > 0 ? "-" + questionObj.getNegativemarks() : "0") + " In all other cases"));
        dView.llNext.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }


    private void showResult() {
        int marks = 0;
        AdvJeeSubmitAnswer submitAnswer1 = new AdvJeeSubmitAnswer();
        submitAnswer1.setUid(sObj.getStudentId());
        submitAnswer1.setUserId(sObj.getStudentId());
        submitAnswer1.setChapterId(questionObj.getChapterCode());
        submitAnswer1.setQuestionRefId(questionObj.get_id());
        submitAnswer1.setQuestionTypeId(questionObj.getQuestionTypeId());
        submitAnswer1.setYearId(questionObj.getYearId());
        submitAnswer1.setYear(questionObj.getYear());
        submitAnswer1.setPaperType(getIntent().getStringExtra("papertype"));
        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
            if (questionObj.getParagraphQuestions().get(que_index).getParagraphQuesId() != null &&
                    !questionObj.getParagraphQuestions().get(que_index).getParagraphQuesId().equalsIgnoreCase(""))
                submitAnswer1.setParagraphQuesId(questionObj.getParagraphQuestions().get(que_index).getParagraphQuesId());
            else submitAnswer1.setParagraphQuesId("1");
        }

        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("ITQ")) {
            String ans = utils.cleanWebString(questionObj.getITQCorrectAnswer().getAnswer().replaceAll("<p.*?>", "")
                    .replaceAll("</p.*?>", "").replaceAll("\n", "").trim());
            if (Float.parseFloat(binding.etItq.getText().toString()) == Float.parseFloat(ans)) {
                submitAnswer1.setCorrect(true);
                marks = questionObj.getTotalQuestionMarks();
            } else {
                if (questionObj.isNegativeMarkingEnabled())
                    marks = questionObj.getNegativemarks() > 0 ? -questionObj.getNegativemarks() : questionObj.getNegativemarks();
                submitAnswer1.setCorrect(false);
            }
            selectedOpt = String.valueOf(Float.parseFloat(binding.etItq.getText().toString()));
            select = String.valueOf(questionObj.getITQCorrectAnswer().getAnswer());
            ArrayList<String> sopt = new ArrayList<>();
            sopt.add(binding.etItq.getText().toString());
            submitAnswer1.setSelectedOption(sopt);
        } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {

            maqans = "";
            for (int k = 0; k < questionObj.getOptions().size(); k++) {
                if (questionObj.getOptions().get(k).isCorrect()) {
                    if (maqans.isEmpty()) maqans = "" + (k + 1);
                    else maqans = MessageFormat.format("{0},{1}", maqans, k + 1);
                }
            }
            if (maqans.isEmpty()) {
                Toast.makeText(this, "Please select option", Toast.LENGTH_SHORT).show();
                return;
            }
            int selectcount = 0;
            String[] ca = maqans.split(",");
            ArrayList<String> sopt = new ArrayList<>();

            for (int b = 0; b < btn.length; b++) {
                if (btn[b].getTag().equals("selected")) {
                    selectcount = selectcount + 1;
                    maqSelectOptions.add(questionObj.getOptions().get(b));
                    sopt.add(questionObj.getOptions().get(b).getOpt());
                    for (String s : ca) {
                        if (s.equalsIgnoreCase(btn[b].getText().toString())) {
                            marks = marks + 1;
                            break;
                        }
                    }
                }
            }
            if (selectcount == marks) {
                if (marks == ca.length) {
                    marks = questionObj.getTotalQuestionMarks();
                } else if (marks == 1 && ca.length == 2) {
                    marks = 2;
                }
                submitAnswer1.setCorrect(true);
            } else {
                marks = 0;
                submitAnswer1.setCorrect(false);
                if (questionObj.isNegativeMarkingEnabled())
                    marks = questionObj.getNegativemarks() > 0 ? -questionObj.getNegativemarks() : questionObj.getNegativemarks();
            }

            submitAnswer1.setSelectedOption(sopt);

        } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
            for (int i = 0; i < questionObj.getParagraphQuestions().get(que_index).getOptions().size(); i++) {
                if (questionObj.getParagraphQuestions().get(que_index).getOptions().get(i).isCorrect()) {
                    select = getOption(i + 1);
                    pos = i;
                    break;
                }
            }
            if (select.equalsIgnoreCase(selectedOpt)) {
                submitAnswer1.setCorrect(true);
                marks = Integer.parseInt(questionObj.getParagraphQuestions().get(que_index).getMarks());
            } else {
                marks = 0;
                submitAnswer1.setCorrect(false);
                if (questionObj.isNegativeMarkingEnabled())
                    marks = questionObj.getNegativemarks() > 0 ? -questionObj.getNegativemarks() : questionObj.getNegativemarks();
            }
            ArrayList<String> sopt = new ArrayList<>();
            sopt.add(selectedOption.getOpt());
            submitAnswer1.setSelectedOption(sopt);
        } else {
            for (int i = 0; i < questionObj.getOptions().size(); i++) {
                if (questionObj.getOptions().get(i).isCorrect()) {
                    select = getOption(i + 1);
                    pos = i;
                    break;
                }
            }
            if (select.equalsIgnoreCase(selectedOpt)) {
                submitAnswer1.setCorrect(true);
                marks = questionObj.getTotalQuestionMarks();
            } else {
                marks = 0;
                submitAnswer1.setCorrect(false);
                if (questionObj.isNegativeMarkingEnabled())
                    marks = questionObj.getNegativemarks() > 0 ? -questionObj.getNegativemarks() : questionObj.getNegativemarks();
            }
            ArrayList<String> sopt = new ArrayList<>();
            sopt.add(selectedOption.getOpt());
            submitAnswer1.setSelectedOption(sopt);
        }
        submitAnswer1.setMarks(marks);

        Call<JSONObject> call = apiInterface.advJeeSubmitAnswer(submitAnswer1);
        showProgress();
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NotNull Call<JSONObject> call, @NotNull Response<JSONObject> response) {
                if (response.code() == 201) {
                    dismissDialog();
                    submitAnswer = submitAnswer1;
                    showAnswerDialog();
                } else {
                    Toast.makeText(AdvJeeTest.this, "Please try again", Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<JSONObject> call, @NotNull Throwable t) {
                Toast.makeText(AdvJeeTest.this, "Please try again", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });

    }

    private void sendEmail(View rootView) {
        rootView.setDrawingCacheEnabled(true);
        File file = new File(getFilesDir(), "temp");
        if (!file.exists()) {
            boolean dir = file.mkdir();
            Log.v("test", String.valueOf(dir));
        }
        File gpxfile = null;
        try {
            gpxfile = new File(file, "question.jpg");
            if (gpxfile.exists()) {
                boolean dir = file.delete();
                Log.v("test", String.valueOf(dir));
                gpxfile = new File(file, "question.jpg");
            }
            try {
                FileOutputStream fos = new FileOutputStream(gpxfile);
                rootView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                Log.e("GREC", e.getMessage(), e);
            }
        } catch (Exception ignored) {
        }
        Uri URI = null;
        if (gpxfile != null) {
            URI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", gpxfile);
        }

        String extraText = "Subject : " + subName + "\n";
        extraText = extraText + "Chapter : " + chapName + "\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"@email"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_TEXT, extraText);

        if (URI != null) {
            intent.putExtra(Intent.EXTRA_STREAM, URI);
        }
        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, "Gmail App is not installed", Toast.LENGTH_SHORT).show();
    }

    private void showAnswerDialog() {
        dialog = new Dialog(AdvJeeTest.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        AdvjeeDialogAnswerExplanationBinding dView = AdvjeeDialogAnswerExplanationBinding.inflate(getLayoutInflater());
        dialog.setContentView(dView.getRoot());
        dView.tvSubName.setText(subName);

        if (submitAnswer.isCorrect()) {
            dView.tvWrongAnswer.setText(MessageFormat.format("{0}", "You got it!"));
            dView.llImg.setBackground(ContextCompat.getDrawable(AdvJeeTest.this, R.drawable.jee_circle_green));
            dView.imgCorrectorwrong.setImageDrawable(ContextCompat.getDrawable(AdvJeeTest.this, R.drawable.jee_green));
            dView.tvWrongAnswer.setTextColor(Color.GREEN);
            dView.llWhy.setVisibility(View.GONE);
        } else {
            dView.tvWrongAnswer.setText(MessageFormat.format("{0}", "You got it wrong"));
            dView.imgCorrectorwrong.setImageDrawable(ContextCompat.getDrawable(AdvJeeTest.this, R.drawable.ic_close));
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
            if (dialog.getWindow() != null) {
                View rootView = dialog.getWindow().getDecorView();
                sendEmail(rootView);
            }
        });

        dView.tvMarks.setText(MessageFormat.format("You have Secured {0} Marks", submitAnswer.getMarks()));

        dView.llNextParagraphQue.setOnClickListener(view -> {
            que_index = que_index + 1;
            loadParaQuestion(que_index);
            dialog.dismiss();
        });

        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("ITQ")) {
            String ans = questionObj.getITQCorrectAnswer().getAnswer().replaceAll("<p.*?>", "")
                    .replaceAll("</p.*?>", "").replaceAll("\n", "").trim();
            dView.tvCorrctAnswer.setText(MessageFormat.format("{0}", "The correct answer is " + ans));
            dView.llWhy.setVisibility(View.GONE);
            if (questionObj.getITQCorrectAnswer().getExplanation().equalsIgnoreCase(""))
                dView.llExp.setVisibility(View.GONE);
            else
                dView.wvExplanation.setText(questionObj.getITQCorrectAnswer().getExplanation());
        } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
            dView.tvCorrctAnswer.setText(MessageFormat.format("{0}", "The correct answer is " + select));
            if (questionObj.getParagraphQuestions().get(que_index).getOptions().get(pos).getExplanation().equalsIgnoreCase(""))
                dView.llExp.setVisibility(View.GONE);
            else
                dView.wvExplanation.setText(questionObj.getParagraphQuestions().get(que_index).getOptions().get(pos).getExplanation());
            if (selectedOption.getExplanation().equalsIgnoreCase(""))
                dView.llWhy.setVisibility(View.GONE);
            else
                dView.wvWhy.setText(selectedOption.getExplanation());
            if (questionObj.getParagraphQuestions().size() > que_index + 1) {
                dView.llNextQue.setVisibility(View.GONE);
                dView.llExpectedQue.setVisibility(View.GONE);
                dView.llNextParagraphQue.setVisibility(View.VISIBLE);
            } else {
                dView.llNextQue.setVisibility(View.VISIBLE);
                dView.llExpectedQue.setVisibility(View.VISIBLE);
                dView.llNextParagraphQue.setVisibility(View.GONE);
            }
        } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
            dView.tvCorrctAnswer.setText(MessageFormat.format("{0}", "The correct answer is " + maqans));
            AdvJeeQueOptions rightOption = null, wrongOption = null;
            for (int s = 0; s < maqSelectOptions.size(); s++) {
                if (maqSelectOptions.get(s).isCorrect()) {
                    rightOption = maqSelectOptions.get(s);
                } else {
                    wrongOption = maqSelectOptions.get(s);
                }
            }
            if (rightOption != null && wrongOption != null) {
                for (int l = 0; l < questionObj.getOptions().size(); l++) {
                    if (questionObj.getOptions().get(l).isCorrect()) {
                        rightOption = questionObj.getOptions().get(l);
                        if (rightOption.getExplanation() != null && !rightOption.getExplanation().equalsIgnoreCase(""))
                            break;
                    }
                }

                if (rightOption.getExplanation() == null || rightOption.getExplanation().equalsIgnoreCase("")) {
                    dView.llExp.setVisibility(View.GONE);
                } else dView.llExp.setVisibility(View.VISIBLE);
                dView.wvExplanation.setText(rightOption.getExplanation());
                if (wrongOption.getExplanation() == null || wrongOption.getExplanation().equalsIgnoreCase("")) {
                    dView.llWhy.setVisibility(View.GONE);
                } else dView.llWhy.setVisibility(View.VISIBLE);
                dView.wvWhy.setText(wrongOption.getExplanation());
            } else {
                if (rightOption == null) {
                    for (int l = 0; l < questionObj.getOptions().size(); l++) {
                        if (questionObj.getOptions().get(l).isCorrect()) {
                            rightOption = questionObj.getOptions().get(l);
                            if (rightOption.getExplanation() != null && !rightOption.getExplanation().equalsIgnoreCase(""))
                                break;
                        }
                    }
                }
                if (wrongOption == null || wrongOption.getExplanation() == null || wrongOption.getExplanation().equalsIgnoreCase("")) {
                    dView.llWhy.setVisibility(View.GONE);
                } else dView.wvWhy.setText(wrongOption.getExplanation());
                if (rightOption != null) {
                    if (rightOption.getExplanation().equalsIgnoreCase(""))
                        dView.llExp.setVisibility(View.GONE);
                    else
                        dView.wvExplanation.setText(rightOption.getExplanation());
                }

            }
        } else {
            dView.tvCorrctAnswer.setText(MessageFormat.format("{0}", "The correct answer is " + select));
            if (questionObj.getOptions().get(pos).getExplanation().equalsIgnoreCase(""))
                dView.llExp.setVisibility(View.GONE);
            else
                dView.wvExplanation.setText(questionObj.getOptions().get(pos).getExplanation());
            if (selectedOption.getExplanation().equalsIgnoreCase(""))
                dView.llWhy.setVisibility(View.GONE);
            else
                dView.wvWhy.setText(selectedOption.getExplanation());
        }

        if (questionObj.getChapterCode() != null && !questionObj.getChapterCode().equalsIgnoreCase("")) {
            dView.tvChapterName.setVisibility(View.VISIBLE);
            dView.tvChapterName.setText(MessageFormat.format("Chapter : {0}\n Topic : {1}", questionObj.getChapterCode(), questionObj.getTopicCode()));
        } else dView.tvChapterName.setVisibility(View.GONE);

        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
            if (questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo() != null && !questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().equalsIgnoreCase("")) {
                dView.llAdditional.setVisibility(View.VISIBLE);
                if (questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().contains("UNDERLYING CONCEPT")) {
                    questionObj.getParagraphQuestions().get(que_index).setAdditionalInfo(questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().replace("UNDERLYING CONCEPT", "<p style=\"color:red;\">Elemental Concept</p>"));
                }
                if (questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().contains("Underlying Concept")) {
                    questionObj.getParagraphQuestions().get(que_index).setAdditionalInfo(questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().replace("Underlying Concept", "<p style=\"color:red;\">Elemental Concept</p>"));
                }
                if (questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().contains("Reference with NCERT")) {
                    questionObj.getParagraphQuestions().get(que_index).setAdditionalInfo(questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().replace("Reference with NCERT", "<p style=\"color:red;\">NCERT Citation</p>"));
                }
                if (questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().contains("REFERENCE WITH NCERT")) {
                    questionObj.getParagraphQuestions().get(que_index).setAdditionalInfo(questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo().replace("REFERENCE WITH NCERT", "<p style=\"color:red;\">NCERT Citation</p>"));
                }
                dView.wvReference.setText(questionObj.getParagraphQuestions().get(que_index).getAdditionalInfo());
            } else {
                dView.llAdditional.setVisibility(View.GONE);
            }
        } else {
            if (questionObj.getAdditionalInfo() != null && !questionObj.getAdditionalInfo().equalsIgnoreCase("")) {
                dView.llAdditional.setVisibility(View.VISIBLE);
                if (questionObj.getAdditionalInfo().contains("UNDERLYING CONCEPT")) {
                    questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("UNDERLYING CONCEPT", "<p style=\"color:red;\">Elemental Concept</p>"));
                }
                if (questionObj.getAdditionalInfo().contains("Underlying Concept")) {
                    questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("Underlying Concept", "<p style=\"color:red;\">Elemental Concept</p>"));
                }
                if (questionObj.getAdditionalInfo().contains("Reference with NCERT")) {
                    questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("Reference with NCERT", "<p style=\"color:red;\">NCERT Citation</p>"));
                }
                if (questionObj.getAdditionalInfo().contains("REFERENCE WITH NCERT")) {
                    questionObj.setAdditionalInfo(questionObj.getAdditionalInfo().replace("REFERENCE WITH NCERT", "<p style=\"color:red;\">NCERT Citation</p>"));
                }
                dView.wvReference.setText(questionObj.getAdditionalInfo());
            } else {
                dView.llAdditional.setVisibility(View.GONE);
            }
        }


        if (questionObj.getPossibleCasesContent() != null && !questionObj.getPossibleCasesContent().equalsIgnoreCase("")) {
            dView.llOtherProbabilities.setVisibility(View.VISIBLE);
            dView.wvOtherPossible.setText(questionObj.getPossibleCasesContent());
        } else {
            dView.llOtherProbabilities.setVisibility(View.GONE);
        }
        dView.llExpectedQue.setOnClickListener(view -> {
//            Intent i = new Intent(com.cloudedz.jeeadvanced.AdvJeeTest.this, ExpectedQuestions.class);
//            i.putExtra("id", questionObj.get_id());
//            i.putExtra("chapter", questionObj.getChapterCode());
//            i.putExtra("subName", subName);
//            i.putExtra("chapName", chapName);
//            i.putExtra("contentmatrix", questionObj.getYearId());
//            startActivity(i);
        });

        if (qsize - 1 >= qNo + 1) {
            if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
                if (questionObj.getParagraphQuestions().size() > que_index + 1) {
                    dView.llNextQue.setVisibility(View.GONE);
                    dView.llExpectedQue.setVisibility(View.GONE);
                    dView.llNextParagraphQue.setVisibility(View.VISIBLE);
                } else {
                    dView.llNextQue.setVisibility(View.VISIBLE);
                    dView.llExpectedQue.setVisibility(View.VISIBLE);
                    dView.llNextParagraphQue.setVisibility(View.GONE);
                    dView.llNextQue.setText(MessageFormat.format("{0}", "Next AdvJeeQuestion"));
                }
            } else {

            }
        } else {
            dView.llNextQue.setText(MessageFormat.format("{0}", "Finish"));
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
        dialog.setOnCancelListener(dialogInterface -> {
            dialog.dismiss();
            isNext = false;
            onBackPressed();
        });
    }

    private void changeUi() {
        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("ITQ")) {
            binding.etItq.setEnabled(false);
        } else {
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

        binding.btnCheckans.setText(MessageFormat.format("{0}", "View Hint"));
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_a:
                if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
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
                    if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(0);
                    } else {
                        selectedOption = questionObj.getOptions().get(0);
                    }

                }
                break;
            case R.id.btn_b:
                if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
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
                    if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(1);
                    } else {
                        selectedOption = questionObj.getOptions().get(1);
                    }
                }
                break;
            case R.id.btn_c:
                if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
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
                    if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(2);
                    } else {
                        selectedOption = questionObj.getOptions().get(2);
                    }
                }
                break;
            case R.id.btn_d:
                if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
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
                    if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
                        selectedOption = questionObj.getParagraphQuestions().get(que_index).getOptions().get(3);
                    } else {
                        selectedOption = questionObj.getOptions().get(3);
                    }
                }
                break;
        }
    }


    public void loadQuestion() {
        if (!questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
            binding.tvQappearance.setVisibility(View.VISIBLE);
            binding.tvQappearance.setText("Marks");
            binding.tvQappearance.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MCQ") ||
                questionObj.getQuestionTypeCode().equalsIgnoreCase("Statement") ||
                questionObj.getQuestionTypeCode().equalsIgnoreCase("Matrix Matching") ||
                questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
            setUnFocusAll();
            binding.queNo.setText(MessageFormat.format(" AdvJeeQuestion {0} :", questionObj.getqDisplayOrder()));
            binding.llParagraph.setVisibility(View.GONE);
            binding.llMcq.setVisibility(View.VISIBLE);
            binding.llItq.setVisibility(View.GONE);
            binding.wvQuestion.setVisibility(View.VISIBLE);
            binding.wvQuestion.getSettings().setBuiltInZoomControls(true);
            binding.wvQuestion.getSettings().setDisplayZoomControls(false);

            StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + questionObj.getQuestion());

            for (int i = 0; i < btn.length; i++) {
                btn[i].setEnabled(true);
                btn[i].setVisibility(View.VISIBLE);
                mainString.append("<fieldset><legend>").append(i + 1).append(")</legend>").append(questionObj.getOptions().get(i).getOpt()).append("</fieldset>");
            }
            mainString.append("</html>");
            mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
            binding.wvQuestion.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
        } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("ITQ")) {
            binding.llParagraph.setVisibility(View.GONE);
            binding.llMcq.setVisibility(View.GONE);
            binding.llItq.setVisibility(View.VISIBLE);
            binding.wvQuestion.setVisibility(View.VISIBLE);
            binding.wvQuestion.getSettings().setBuiltInZoomControls(true);
            binding.wvQuestion.getSettings().setDisplayZoomControls(false);
            binding.llOption.removeAllViews();
            binding.etItq.setText("");
            binding.queNo.setText(MessageFormat.format(" AdvJeeQuestion {0} :", questionObj.getqDisplayOrder()));


            String mainString = "<!DOCTYPE html> <html>" + questionObj.getQuestion() + "</html>";
            mainString = utils.cleanWebString(mainString);
            binding.wvQuestion.loadData(mainString, "text/html; charset=utf-8", "utf-8");

            binding.wvQuestion.scrollTo(0, 0);
        } else if (questionObj.getQuestionTypeCode().equalsIgnoreCase("Paragraph")) {
            binding.llParagraph.setVisibility(View.VISIBLE);
            binding.wvQuestionParagraph.getSettings().setBuiltInZoomControls(true);
            binding.wvQuestionParagraph.getSettings().setDisplayZoomControls(false);
            paraquestions = questionObj.getqDisplayOrder().replaceAll("\\[", "").replaceAll("]", "").split(",");
            binding.wvQuestionParagraph.setVerticalScrollBarEnabled(true);
            binding.wvQuestionParagraph.setText(questionObj.getQuestion().replaceAll("<span.*?>", "")
                    .replaceAll("&#39;", "'"));
            loadParaQuestion(que_index);
        }

    }

    private void setFocus(Button btn_focus) {
        if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ")) {
            if (btn_focus.getTag().equals("selected")) {
                btn_focus.setBackgroundResource(R.drawable.practice_btn_opt);
                btn_focus.setFocusable(true);
                btn_focus.setTextColor(Color.parseColor("#4a4a4a"));
            } else {
                btn_focus.setBackgroundResource(R.drawable.practice_btn_optsel);
                btn_focus.setFocusable(false);
                btn_focus.setTextColor(Color.parseColor("#444444"));
            }
        } else {
            btn_focus.setBackgroundResource(R.drawable.practice_btn_opt);
            btn_focus.setFocusable(true);
            btn_focus.setTextColor(Color.parseColor("#4a4a4a"));
        }
    }

    private void setUnFocusAll() {
        for (Button aBtn : btn) {
            aBtn.setBackgroundResource(R.drawable.practice_btn_optsel);
            aBtn.setFocusable(false);
            if (questionObj.getQuestionTypeCode().equalsIgnoreCase("MAQ"))
                aBtn.setTag("Unselected");
            aBtn.setTextColor(Color.parseColor("#444444"));

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AdvJeeTest.this, AdvJeeSubjectsActivity.class);
        if (submitAnswer != null && !submitAnswer.getQuestionRefId().isEmpty()) {
            i.putExtra("submit", submitAnswer);
        }
        i.putExtra("isNext", isNext);
        setResult(4321, i);
        finish();

    }

    public void showProgress() {
        if (loading != null && loading.isShowing()) loading.dismiss();
        loading = new Dialog(AdvJeeTest.this);
        loading.setContentView(R.layout.dialog_loader);
        loading.setCancelable(false);
        if (loading.getWindow() != null)
            loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        animationView = loading.findViewById(R.id.animation_view);
//        animationView.playAnimation();
        loading.show();
    }

    public void dismissDialog() {
//        if (animationView != null && animationView.isAnimating())
//            animationView.cancelAnimation();
        if (loading != null && loading.isShowing())
            loading.dismiss();
    }

    public void loadParaQuestion(int que_index) {
        que_NO = que_index + 1;

        if (que_index == paraquestions.length)
            binding.tvParaqno.setText(MessageFormat.format(" AdvJeeQuestion {0} :", paraquestions[que_index - 1]));
        else
            binding.tvParaqno.setText(MessageFormat.format(" AdvJeeQuestion {0} :", paraquestions[que_index]));
        binding.queNo.setText(MessageFormat.format("{0}", "AdvJeeQuestion " + que_NO + "/" + questionObj.getParagraphQuestions().size()));
        binding.wvQuestion.getSettings().setBuiltInZoomControls(true);
        binding.wvQuestion.getSettings().setDisplayZoomControls(false);

        checkbuttons();
        initOptions(questionObj.getParagraphQuestions().get(que_index));
        setUnFocusAll();

        ArrayList<AdvJeeParagraphQues> pQuestions = questionObj.getParagraphQuestions();
        AdvJeeParagraphQues pq = pQuestions.get(que_index);
        StringBuilder mainString = new StringBuilder("<!DOCTYPE html> <html>" + pq.getQuestion());

        for (int i = 0; i < pq.getOptions().size(); i++) {
            btn[i].setEnabled(true);
            btn[i].setVisibility(View.VISIBLE);
            if (questionObj.getParagraphQuestions().get(que_index).getSelectOption() == i) {
                setFocus(btn[i]);
            }
            mainString.append("<fieldset><legend>").append(i + 1).append(")</legend>").append(pq.getOptions().get(i).getOpt()).append("</fieldset>");
        }
        mainString.append("</html>");
        mainString = new StringBuilder(utils.cleanWebString(mainString.toString()));
        binding.wvQuestion.loadData(mainString.toString(), "text/html; charset=utf-8", "utf-8");
    }

    private void checkbuttons() {
        if (btn != null) {
            for (Button aBtn : btn) {
                aBtn.setVisibility(View.GONE);
            }
        }
    }

    private void initOptions(AdvJeeParagraphQues question) {
        btn = new Button[question.getOptions().size()];
        for (int i = 0; i < btn.length; i++) {
            btn[i] = findViewById(btn_id[i]);
            btn[i].setVisibility(View.GONE);
            btn[i].setBackgroundResource(R.drawable.btn_cust_test_opt_unselected);
            btn[i].setOnClickListener(AdvJeeTest.this);
        }
    }
}