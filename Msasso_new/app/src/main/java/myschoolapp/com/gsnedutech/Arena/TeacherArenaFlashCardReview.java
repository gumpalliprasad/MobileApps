package myschoolapp.com.gsnedutech.Arena;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.FlashCardFilesArray;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherArenaFlashCardReview extends AppCompatActivity {

    private static final String TAG = TeacherArenaFlashCardReview.class.getName();

    TeacherObj tObj;
    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;

    ArenaRecord flashObj;

    int selectedPos = 0;

    List<FlashCardFilesArray> cardsList = new ArrayList<>();

    boolean front  = true;

    @BindView(R.id.iv_q_image)
    ImageView ivQ;
    @BindView(R.id.tv_ques)
    TextView tvQues;
    @BindView(R.id.iv_a_image)
    ImageView ivA;
    @BindView(R.id.tv_answer)
    TextView tvAnswer;

    @BindView(R.id.ll_front)
    LinearLayout llFront;
    @BindView(R.id.cv_falsh_card)
    CardView cvFlashCard;
    @BindView(R.id.et_remarks)
    EditText etRemarks;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.iv_speak)
    ImageView ivSpeak;
    @BindView(R.id.ll_color)
    LinearLayout llColor;

    String status = "";

    String question, answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_arena_flash_card_review);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    void init(){

        status = getIntent().getStringExtra("status");
        if (!status.equalsIgnoreCase("0"))
            etRemarks.setVisibility(View.INVISIBLE);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        flashObj = (ArenaRecord) getIntent().getSerializableExtra("item");

        if (flashObj.getColor()!= null && !(flashObj.getColor().equalsIgnoreCase("")))
            llColor.setBackgroundColor(Color.parseColor(flashObj.getColor()));


        if(flashObj.getArenaName().contains("~~link~~")){
            String s[] = flashObj.getArenaName().split("~~link~~");
            ((TextView)findViewById(R.id.tv_title)).setText(s[0]);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(flashObj.getArenaName());
        }

        cvFlashCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (front){
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
                    oa1.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa2.setInterpolator(new DecelerateInterpolator());
                    oa1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            llFront.setVisibility(View.GONE);
                            oa2.start();
                        }
                    });
                    oa1.start();
                    front=false;
                }else {
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
                    oa1.setInterpolator(new DecelerateInterpolator());
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            llFront.setVisibility(View.VISIBLE);
                            oa2.start();
                        }
                    });
                    oa1.start();
                    front=true;
                }
            }
        });

        findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((TextView)findViewById(R.id.tv_next)).getText().toString().equalsIgnoreCase("NEXT")){
                    if ((selectedPos + 1) < cardsList.size()) {

                        selectedPos++;
                        if (!front) {
                            llFront.setVisibility(View.VISIBLE);
                            front = true;
                        }
                        loadQuestion();

                        if (status.equalsIgnoreCase("0")) {
                            if (selectedPos == (cardsList.size() - 1)) {
                                if (etRemarks.getText().toString().trim().length() > 0) {
                                    ((TextView) findViewById(R.id.tv_next)).setText("ReAssign");
                                } else {
                                    ((TextView) findViewById(R.id.tv_next)).setText("Approve");
                                }
                            }
                        }


                    } else {
                        if (status.equalsIgnoreCase("0")) {
                            if (etRemarks.getText().toString().trim().length() > 0) {
                                ((TextView) findViewById(R.id.tv_next)).setText("ReAssign");
                            } else {
                                ((TextView) findViewById(R.id.tv_next)).setText("Approve");
                            }
                        }
                    }
                }else {
                    if(etRemarks.getText().toString().trim().length()>0){
                        arenaReview("2",new JSONArray());
                    }else {
                        //do section call and add object
                        getSections();
                        //arenaReview("1");
                    }
                }
            }
        });

        findViewById(R.id.tv_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.tv_next)).setText("Next");
                if ((selectedPos - 1) >= 0){
                    selectedPos--;
                }
                if (!front) {
                    llFront.setVisibility(View.VISIBLE);
                    front = true;
                }
                loadQuestion();
            }
        });

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (status.equalsIgnoreCase("0")) {
                    if (selectedPos == (cardsList.size() - 1)) {
                        if (editable.toString().trim().length() > 0) {
                            ((TextView) findViewById(R.id.tv_next)).setText("ReAssign");
                        } else {
                            ((TextView) findViewById(R.id.tv_next)).setText("Approve");
                        }
                    }
                }
            }
        });

        TextToSpeech tts = new TextToSpeech(TeacherArenaFlashCardReview.this, null);
        tts.setLanguage(Locale.US);
        ivSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (front){
                    tts.speak(tvQues.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                }else {
                    tts.speak(tvAnswer.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                }


            }
        });


        getFlashCards();
    }

    void getFlashCards(){

        utils.showLoader(TeacherArenaFlashCardReview.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetQuizQuestionsById + "schemaName=" + sh_Pref.getString("schema", "") + "&arenaId=" + flashObj.getArenaId()+"&arenaCategory=2" )
                .build();

        utils.showLog(TAG, "url -" + new AppUrls().GetQuizQuestionsById + "schemaName=" + sh_Pref.getString("schema", "") + "&arenaId=" + flashObj.getArenaId()+"&arenaCategory=2" );

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = ParentjObject.getJSONArray("arenaCategories");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FlashCardFilesArray>>() {
                            }.getType();

                            cardsList.clear();
                            cardsList.addAll(gson.fromJson(jsonArray.toString(), type));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (cardsList.size()>0){
                                        selectedPos = 0;
                                        loadQuestion();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }

    void loadQuestion(){

        tvCount.setText((selectedPos+1)+"/"+cardsList.size());

        if (cardsList.get(selectedPos).getQuestion().contains("~~")){
            String[] title = cardsList.get(selectedPos).getQuestion().split("~~");
            if (title[0].equalsIgnoreCase("NA")){
                tvQues.setText("");
            }else {
                tvQues.setText(title[0]);

            }

            for (String str: title){
                if (str.contains("http")){
                    ivQ.setVisibility(View.VISIBLE);
                    Picasso.with(TeacherArenaFlashCardReview.this).load(str).placeholder(R.drawable.ic_arena_add_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQ);
                }
            }

        }else{
            ivQ.setVisibility(View.GONE);
            tvQues.setText(cardsList.get(selectedPos).getQuestion());
        }

        if (cardsList.get(selectedPos).getAnswer().contains("~~")){
            String[] title = cardsList.get(selectedPos).getAnswer().split("~~");
            if (title[0].equalsIgnoreCase("NA")){
                tvAnswer.setText("");
            }else {
                tvAnswer.setText(title[0]);

            }

            for (String str: title){
                if (str.contains("http")){
                    ivA.setVisibility(View.VISIBLE);
                    Picasso.with(TeacherArenaFlashCardReview.this).load(str).placeholder(R.drawable.ic_arena_add_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivA);
                }
            }

        }else {
            ivA.setVisibility(View.GONE);
            tvAnswer.setText(cardsList.get(selectedPos).getAnswer());
        }

    }

    public void arenaReview(String s,JSONArray sections) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("arenaId",flashObj.getArenaId()+"");
            if (s.equalsIgnoreCase("1")){
                jsonObject.put("arenaStatus","1");
                jsonObject.put("sections",sections);
            }else {
                jsonObject.put("arenaStatus","2");
                jsonObject.put("teacherReview",etRemarks.getText().toString());
            }
            jsonObject.put("createdBy",flashObj.getStudentId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().ReviewArenaStatus);
        utils.showLog(TAG,"url obj"+jsonObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().ReviewArenaStatus)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        utils.dismissDialog();
                        new AlertDialog.Builder(TeacherArenaFlashCardReview.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Oops! Something went wrong.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(true)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.v(TAG,"response "+resp);
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(TeacherArenaFlashCardReview.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Oops! Something went wrong.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObj = new JSONObject(resp);
                        if (jsonObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (s.equalsIgnoreCase("1")){
                                        new AlertDialog.Builder(TeacherArenaFlashCardReview.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Approved successfully!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }else {
                                        new AlertDialog.Builder(TeacherArenaFlashCardReview.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Review successfully sent!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"")
                .build();

        utils.showLog(TAG, AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(TeacherArenaFlashCardReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherArenaFlashCardReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);

                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){

                            JSONArray array = respObj.getJSONArray("collegesInfo");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollegeInfo>>() {
                            }.getType();

                            List<CollegeInfo> listBranches = new ArrayList<>();
                            listBranches.clear();
                            listBranches.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(TeacherArenaFlashCardReview.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherArenaFlashCardReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }



}