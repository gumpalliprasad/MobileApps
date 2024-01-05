package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.FlashCardFilesArray;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FlashCardsActivity extends AppCompatActivity {

    private static final String TAG = "SriRam -" + FlashCardsActivity.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.iv_q_image)
    ImageView ivQImage;

    @BindView(R.id.iv_a_image)
    ImageView ivAImage;

    @BindView(R.id.sc_front)
    ScrollView scFront;

    @BindView(R.id.sc_back)
    ScrollView scBack;

    @BindView(R.id.tv_ques)
    TextView tvQues;

    @BindView(R.id.tv_answer)
    TextView tvAnswer;

    @BindView(R.id.tv_checkAnswer)
    TextView tvCheckAnswer;

    @BindView(R.id.cv_falsh_card)
    CardView cvFlashCard;

    @BindView(R.id.et_answer)
    EditText etAnswer;

    @BindView(R.id.tv_correct_wrong)
    TextView tvCorrectWrong;

    @BindView(R.id.tv_card_count)
    TextView tvCardCount;

    SharedPreferences sh_Pref;
    StudentObj sObj;
    String question, answer;

    int arenaId ;
    int selectedPos = 0;


    List<FlashCardFilesArray> cardsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getFlashCards();


    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        arenaId = getIntent().getIntExtra("arenaId",0);

        tvCheckAnswer.setOnClickListener(view -> {
            if (tvCheckAnswer.getText().toString().equalsIgnoreCase("Check answer")) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
                oa1.setInterpolator(new AccelerateDecelerateInterpolator());
                oa2.setInterpolator(new DecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        oa2.start();
                    }
                });
                scFront.setVisibility(View.GONE);
                scBack.setVisibility(View.VISIBLE);
                oa1.start();
                etAnswer.setVisibility(View.GONE);
                tvCorrectWrong.setVisibility(View.VISIBLE);
                if (etAnswer.getText().toString().trim().equalsIgnoreCase(answer)){
                    tvCorrectWrong.setText("Yes! You are Correct");
                    tvCorrectWrong.setTextColor(Color.parseColor("#008C16"));
                }
                else {
                    tvCorrectWrong.setText("No! You are Wrong");
                    tvCorrectWrong.setTextColor(Color.parseColor("#C82D27"));
                }
                tvCheckAnswer.setText("Next Flash Card");
            }
            else if (tvCheckAnswer.getText().toString().equalsIgnoreCase("Finish")){
                onBackPressed();
            }
            else {
                if (++selectedPos < cardsList.size()){
                    tvCheckAnswer.setText("Check answer");
                    loadQuestion(selectedPos);
                }
                else {
                    tvCheckAnswer.setText("Finish");
                }

            }
        });
    }

    void getFlashCards(){

        utils.showLoader(FlashCardsActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetQuizQuestionsById + "schemaName=" + sh_Pref.getString("schema", "") + "&arenaId=" + arenaId )
                .build();

        utils.showLog(TAG, "url -" + new AppUrls().GetQuizQuestionsById + "schemaName=" + sh_Pref.getString("schema", "") + "&arenaId=" + arenaId );

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
                                        loadQuestion(selectedPos);
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

    void loadQuestion(int pos){
        scFront.setVisibility(View.VISIBLE);
        scBack.setVisibility(View.GONE);
        tvCorrectWrong.setVisibility(View.GONE);
        etAnswer.setVisibility(View.VISIBLE);
        etAnswer.setText("");
        tvCardCount.setText((pos+1)+"/"+cardsList.size());
        String[] qArray = cardsList.get(pos).getQuestion().split("~~");
        if (qArray.length>2 && qArray[2].length()>0){
            Picasso.with(FlashCardsActivity.this).load(qArray[2]).placeholder(android.R.drawable.ic_menu_gallery)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
        }
        else if (qArray.length>1 && qArray[1].length()>0){
            Picasso.with(FlashCardsActivity.this).load(qArray[1]).placeholder(android.R.drawable.ic_menu_gallery)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
        }
        else ivQImage.setVisibility(View.GONE);
        String[] aArray = cardsList.get(pos).getAnswer().split("~~");
        if (aArray.length>2 && aArray[2].length()>0){
            Picasso.with(FlashCardsActivity.this).load(aArray[2]).placeholder(android.R.drawable.ic_menu_gallery)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivAImage);
        }
        else if (aArray.length>1 && aArray[1].length()>0){
            Picasso.with(FlashCardsActivity.this).load(aArray[1]).placeholder(android.R.drawable.ic_menu_gallery)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivAImage);
        }
        else ivAImage.setVisibility(View.GONE);
        question = cardsList.get(pos).getQuestion().split("~~")[0];
        answer = cardsList.get(pos).getAnswer().split("~~")[0];
        tvQues.setText(question);
        tvAnswer.setText(answer);

//        Picasso.with(FlashCardsActivity.this).load(sObj.getProfilePic()).placeholder(R.drawable.user_default)
//                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}