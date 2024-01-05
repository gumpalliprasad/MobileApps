package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.ProgressIndicator;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaScore;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArenaQuizDisplay extends AppCompatActivity {
    private static final String TAG = "SriRam -" + ArenaQuizDisplay.class.getName();

    @BindView(R.id.rv_q_num)
    RecyclerView rvQNum;
    @BindView(R.id.tv_q)
    TextView tvQ;
    @BindView(R.id.tv_q_no_image)
    TextView tvQNoImage;
    @BindView(R.id.iv_q_image)
    ImageView ivQImage;

    @BindView(R.id.iv_op_1)
    ImageView ivOp1;
    @BindView(R.id.iv_op_2)
    ImageView ivOp2;
    @BindView(R.id.iv_op_3)
    ImageView ivOp3;
    @BindView(R.id.iv_op_4)
    ImageView ivOp4;
    @BindView(R.id.tv_op_1)
    TextView tvOp1;
    @BindView(R.id.tv_op_2)
    TextView tvOp2;
    @BindView(R.id.tv_op_3)
    TextView tvOp3;
    @BindView(R.id.tv_op_4)
    TextView tvOp4;
    @BindView(R.id.view_op1_overlay)
    View viewOp1Overlay;
    @BindView(R.id.view_op2_overlay)
    View viewOp2Overlay;
    @BindView(R.id.view_op3_overlay)
    View viewOp3Overlay;
    @BindView(R.id.view_op4_overlay)
    View viewOp4Overlay;
    @BindView(R.id.iv_tick_op1)
    ImageView ivTickOp1;
    @BindView(R.id.iv_tick_op2)
    ImageView ivTickOp2;
    @BindView(R.id.iv_tick_op3)
    ImageView ivTickOp3;
    @BindView(R.id.iv_tick_op4)
    ImageView ivTickOp4;

    @BindView(R.id.tv_true)
    TextView tvTrue;
    @BindView(R.id.tv_false)
    TextView tvFalse;

    @BindView(R.id.progress_timer)
    ProgressIndicator progressTimer;
    @BindView(R.id.tv_time)
    TextView tvTime;

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;

    ArenaRecord quizObj;

    List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();

    int currentPos = 0;

    QuestionNumberAdapter adapter;

    CountDownTimer timer;

    int correct = 0,wrong = 0,skipped = 0;

    StudentObj sObj;

    int totalTime = 0;

    Dialog  dialog = null;

    List<ArenaScore> leaderBoard = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_quiz_display);
        ButterKnife.bind(this);

        init();
    }

    void  init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        quizObj = (ArenaRecord) getIntent().getSerializableExtra("quizObj");
        // you already attempted the quiz. your score is not updated to the leaderboard
        if(!quizObj.getArenaUserStatus().equalsIgnoreCase("NA")){
            new AlertDialog.Builder(ArenaQuizDisplay.this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setCancelable(false)
                    .setMessage("Already quiz started by you. Your score will not be updated in leaderboard")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getQuizDetails();
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
        else getQuizDetails();


        if(quizObj.getArenaName().contains("~~")){
            ((TextView)findViewById(R.id.tv_title)).setText(quizObj.getArenaName().split("~~")[0]);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(quizObj.getArenaName());
        }

        findViewById(R.id.btn_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ArenaQuizDisplay.this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Are you sure you want to quit this quiz?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Button)findViewById(R.id.btn_submit)).getText().toString().equalsIgnoreCase("Submit")){
                    timer.cancel();
                    if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase("NA")){
                        Toast.makeText(ArenaQuizDisplay.this,"Please Enter an option!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("MCQ")){
                            if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase(listQuestions.get(currentPos).getAnswer())){
                                correct++;
                                int resID=getResources().getIdentifier("ding", "raw", getPackageName());

                                MediaPlayer mediaPlayer= MediaPlayer.create(ArenaQuizDisplay.this,resID);
                                mediaPlayer.start();
                                switch (listQuestions.get(currentPos).getAnswer()){
                                    case "A":
                                        viewOp1Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp1.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp1.setVisibility(View.VISIBLE);
                                        break;
                                    case "B":
                                        viewOp2Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp2.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp2.setVisibility(View.VISIBLE);
                                        break;
                                    case "C":
                                        viewOp3Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp3.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp3.setVisibility(View.VISIBLE);
                                        break;
                                    case "D":
                                        viewOp4Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp4.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp4.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }
                            else {
                                wrong++;
                                int resID=getResources().getIdentifier("wrong", "raw", getPackageName());

                                MediaPlayer mediaPlayer= MediaPlayer.create(ArenaQuizDisplay.this,resID);
                                mediaPlayer.start();
                                switch (listQuestions.get(currentPos).getSelectedAnswer()){
                                    case "A":
                                        viewOp1Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                        ivTickOp1.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                        ivTickOp1.setVisibility(View.VISIBLE);
                                        break;
                                    case "B":
                                        viewOp2Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                        ivTickOp2.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                        ivTickOp2.setVisibility(View.VISIBLE);
                                        break;
                                    case "C":
                                        viewOp3Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                        ivTickOp3.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                        ivTickOp3.setVisibility(View.VISIBLE);
                                        break;
                                    case "D":
                                        viewOp4Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                        ivTickOp4.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                        ivTickOp4.setVisibility(View.VISIBLE);
                                        break;
                                }

                                switch (listQuestions.get(currentPos).getAnswer()){
                                    case "A":
                                        viewOp1Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        viewOp1Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp1.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp1.setVisibility(View.VISIBLE);
                                        break;
                                    case "B":
                                        viewOp2Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        viewOp2Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp2.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp2.setVisibility(View.VISIBLE);
                                        break;
                                    case "C":
                                        viewOp3Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        viewOp3Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp3.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp3.setVisibility(View.VISIBLE);
                                        break;
                                    case "D":
                                        viewOp4Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        viewOp4Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp4.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        ivTickOp4.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }
                        }

                        if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("TOF")){
                            if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase(listQuestions.get(currentPos).getAnswer())){
                                correct++;
                                int resID=getResources().getIdentifier("ding", "raw", getPackageName());

                                MediaPlayer mediaPlayer= MediaPlayer.create(ArenaQuizDisplay.this,resID);
                                mediaPlayer.start();
                                switch (listQuestions.get(currentPos).getSelectedAnswer()){
                                    case "true":
                                        tvTrue.setTextColor(Color.WHITE);
                                        tvTrue.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        break;
                                    case "false":
                                        tvFalse.setTextColor(Color.WHITE);
                                        tvFalse.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        break;
                                }
                            }
                            else {
                                wrong++;
                                int resID=getResources().getIdentifier("wrong", "raw", getPackageName());

                                MediaPlayer mediaPlayer= MediaPlayer.create(ArenaQuizDisplay.this,resID);
                                mediaPlayer.start();
                                switch (listQuestions.get(currentPos).getAnswer().toLowerCase()){
                                    case "true":
                                        tvTrue.setTextColor(Color.WHITE);
                                        tvTrue.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        break;
                                    case "false":
                                        tvFalse.setTextColor(Color.WHITE);
                                        tvFalse.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        break;
                                }
                                switch (listQuestions.get(currentPos).getSelectedAnswer()){
                                    case "true":
                                        tvTrue.setTextColor(Color.WHITE);
                                        tvTrue.setBackgroundColor(Color.parseColor("#B10D0D"));
                                        break;
                                    case "false":
                                        tvFalse.setTextColor(Color.WHITE);
                                        tvFalse.setBackgroundColor(Color.parseColor("#B10D0D"));
                                        break;
                                }
                            }
                        }

                        if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("MAQ")){
                            String[] answers = listQuestions.get(currentPos).getAnswer().split(",");
                            String[] answered = listQuestions.get(currentPos).getSelectedAnswer().split(",");

                            if (compareArrays(answered,answers)){
                                correct++;
                                int resID=getResources().getIdentifier("ding", "raw", getPackageName());

                                MediaPlayer mediaPlayer= MediaPlayer.create(ArenaQuizDisplay.this,resID);
                                mediaPlayer.start();
                            }else {
                                wrong++;
                                int resID=getResources().getIdentifier("wrong", "raw", getPackageName());

                                MediaPlayer mediaPlayer= MediaPlayer.create(ArenaQuizDisplay.this,resID);
                                mediaPlayer.start();
                            }

                            for (int i=0;i<answered.length;i++){
                                int c=0;
                                for (int j=0;j<answers.length;j++){
                                    if (answers[j].equalsIgnoreCase(answered[i])){
                                        c++;
                                    }
                                }
                                if (c == 0) {
                                    switch (answered[i]){
                                        case "A":
                                            viewOp1Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                            ivTickOp1.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                            viewOp1Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp1.setVisibility(View.VISIBLE);
                                            break;
                                        case "B":
                                            viewOp2Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                            ivTickOp2.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                            viewOp2Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp2.setVisibility(View.VISIBLE);
                                            break;
                                        case "C":
                                            viewOp3Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                            ivTickOp3.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                            viewOp3Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp3.setVisibility(View.VISIBLE);
                                            break;
                                        case "D":
                                            viewOp4Overlay.setBackgroundColor(Color.parseColor("#B10D0D"));
                                            ivTickOp4.setImageResource(R.drawable.ic_incorrect_arena_quiz);
                                            viewOp4Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp4.setVisibility(View.VISIBLE);
                                            break;
                                    }
                                }
                                else {
                                    switch (answered[i]){
                                        case "A":
                                            viewOp1Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                            ivTickOp1.setImageResource(R.drawable.ic_correct_arena_quiz);
                                            viewOp1Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp1.setVisibility(View.VISIBLE);
                                            break;
                                        case "B":
                                            viewOp2Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                            ivTickOp2.setImageResource(R.drawable.ic_correct_arena_quiz);
                                            viewOp2Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp2.setVisibility(View.VISIBLE);
                                            break;
                                        case "C":
                                            viewOp3Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                            ivTickOp3.setImageResource(R.drawable.ic_correct_arena_quiz);
                                            viewOp3Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp3.setVisibility(View.VISIBLE);
                                            break;
                                        case "D":
                                            viewOp4Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                            ivTickOp4.setImageResource(R.drawable.ic_correct_arena_quiz);
                                            viewOp4Overlay.setVisibility(View.VISIBLE);
                                            ivTickOp4.setVisibility(View.VISIBLE);
                                            break;
                                    }
                                }
                            }

                            for (int i=0;i<answers.length;i++){
                                switch (answers[i]){
                                    case "A":
                                        viewOp1Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp1.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        viewOp1Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp1.setVisibility(View.VISIBLE);
                                        break;
                                    case "B":
                                        viewOp2Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp2.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        viewOp2Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp2.setVisibility(View.VISIBLE);
                                        break;
                                    case "C":
                                        viewOp3Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp3.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        viewOp3Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp3.setVisibility(View.VISIBLE);
                                        break;
                                    case "D":
                                        viewOp4Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                                        ivTickOp4.setImageResource(R.drawable.ic_correct_arena_quiz);
                                        viewOp4Overlay.setVisibility(View.VISIBLE);
                                        ivTickOp4.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }

                        }

                        ((Button)findViewById(R.id.btn_submit)).setText("Next");
                    }
                }else {

                    if((currentPos+1)<listQuestions.size()){
                        currentPos++;
                        adapter.notifyDataSetChanged();
                        loadQuestion();
                        ((Button)findViewById(R.id.btn_submit)).setText("Submit");
                    }else{
                        //call update
                        if (!quizObj.getUserId().equalsIgnoreCase(sObj.getStudentId()) &&!quizObj.getStudentId().equalsIgnoreCase(sObj.getStudentId()) && quizObj.getArenaUserStatus().equalsIgnoreCase("NA")){
                            updateArenaScore();
                        }else {
                            getLeaderBoard();
                        }
                    }

                }
            }
        });



        findViewById(R.id.cv_op1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("MCQ")){
                    listQuestions.get(currentPos).setSelectedAnswer("A");
                    unselectAll();
                    viewOp1Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                    viewOp1Overlay.setVisibility(View.VISIBLE);
                }else {
                    if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase("NA")){
                        listQuestions.get(currentPos).setSelectedAnswer("A");
                        unselectAll();
                        viewOp1Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                        viewOp1Overlay.setVisibility(View.VISIBLE);
                    }else{
                        if (listQuestions.get(currentPos).getSelectedAnswer().contains(",A")){
                            viewOp1Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace(",A","");
                        }else if (listQuestions.get(currentPos).getSelectedAnswer().contains("A")){
                            viewOp1Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace("A","");
                        }else {
                            listQuestions.get(currentPos).setSelectedAnswer(listQuestions.get(currentPos).getSelectedAnswer()+",A");
                            viewOp1Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                            viewOp1Overlay.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        findViewById(R.id.cv_op2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("MCQ")){
                    listQuestions.get(currentPos).setSelectedAnswer("B");
                    unselectAll();
                    viewOp2Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                    viewOp2Overlay.setVisibility(View.VISIBLE);
                }else {
                    if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase("NA")){
                        listQuestions.get(currentPos).setSelectedAnswer("B");
                        unselectAll();
                        viewOp2Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                        viewOp2Overlay.setVisibility(View.VISIBLE);
                    }else{
                        if (listQuestions.get(currentPos).getSelectedAnswer().contains(",B")){
                            viewOp2Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace(",B","");
                        }else if (listQuestions.get(currentPos).getSelectedAnswer().contains("B")){
                            viewOp2Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace("B","");
                        }else {
                            listQuestions.get(currentPos).setSelectedAnswer(listQuestions.get(currentPos).getSelectedAnswer()+",B");
                            viewOp2Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                            viewOp2Overlay.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        findViewById(R.id.cv_op3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("MCQ")){
                    listQuestions.get(currentPos).setSelectedAnswer("C");
                    unselectAll();
                    viewOp3Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                    viewOp3Overlay.setVisibility(View.VISIBLE);
                }else {
                    if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase("NA")){
                        listQuestions.get(currentPos).setSelectedAnswer("C");
                        unselectAll();
                        viewOp3Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                        viewOp3Overlay.setVisibility(View.VISIBLE);
                    }else{
                        if (listQuestions.get(currentPos).getSelectedAnswer().contains(",C")){
                            viewOp3Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace(",C","");
                        }else if (listQuestions.get(currentPos).getSelectedAnswer().contains("C")){
                            viewOp3Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace("C","");
                        }else {
                            listQuestions.get(currentPos).setSelectedAnswer(listQuestions.get(currentPos).getSelectedAnswer()+",C");
                            viewOp3Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                            viewOp3Overlay.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        findViewById(R.id.cv_op4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("MCQ")){
                    listQuestions.get(currentPos).setSelectedAnswer("D");
                    unselectAll();
                    viewOp4Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                    viewOp4Overlay.setVisibility(View.VISIBLE);
                }else {
                    if (listQuestions.get(currentPos).getSelectedAnswer().equalsIgnoreCase("NA")){
                        listQuestions.get(currentPos).setSelectedAnswer("D");
                        unselectAll();
                        viewOp4Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                        viewOp4Overlay.setVisibility(View.VISIBLE);
                    }else{
                        if (listQuestions.get(currentPos).getSelectedAnswer().contains(",D")){
                            viewOp4Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace(",D","");
                        }else if (listQuestions.get(currentPos).getSelectedAnswer().contains("D")){
                            viewOp4Overlay.setVisibility(View.GONE);
                            listQuestions.get(currentPos).getSelectedAnswer().replace("D","");
                        }else {
                            listQuestions.get(currentPos).setSelectedAnswer(listQuestions.get(currentPos).getSelectedAnswer()+",D");
                            viewOp4Overlay.setBackgroundColor(Color.parseColor("#2E2E8B"));
                            viewOp4Overlay.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        findViewById(R.id.cv_true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectAll();
                listQuestions.get(currentPos).setSelectedAnswer("true");
                tvTrue.setBackgroundColor(Color.parseColor("#2E2E8B"));
                tvTrue.setTextColor(Color.WHITE);
            }
        });

        findViewById(R.id.cv_false).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectAll();
                listQuestions.get(currentPos).setSelectedAnswer("false");
                tvFalse.setBackgroundColor(Color.parseColor("#2E2E8B"));
                tvFalse.setTextColor(Color.WHITE);
            }
        });


    }

    public boolean compareArrays(String[] arr1, String[] arr2) {
        HashSet<String> set1 = new HashSet<String>(Arrays.asList(arr1));
        HashSet<String> set2 = new HashSet<String>(Arrays.asList(arr2));
        return set1.equals(set2);
    }


    private void unselectAll() {
        viewOp1Overlay.setVisibility(View.GONE);
        viewOp2Overlay.setVisibility(View.GONE);
        viewOp3Overlay.setVisibility(View.GONE);
        viewOp4Overlay.setVisibility(View.GONE);
        ivTickOp1.setVisibility(View.GONE);
        ivTickOp2.setVisibility(View.GONE);
        ivTickOp3.setVisibility(View.GONE);
        ivTickOp4.setVisibility(View.GONE);
        tvTrue.setBackgroundColor(Color.WHITE);
        tvTrue.setTextColor(Color.BLACK);
        tvFalse.setBackgroundColor(Color.WHITE);
        tvFalse.setTextColor(Color.BLACK);
    }

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + quizObj.getArenaId()+""+"&arenaCategory=1")
                .build();

        utils.showLog(TAG, AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + quizObj.getArenaId()+""+"&arenaCategory=1");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
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
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaQuizQuestionFiles>>() {
                            }.getType();

                            listQuestions.clear();
                            listQuestions.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listQuestions.size()>0){
                                         dialog = new Dialog(ArenaQuizDisplay.this);
                                        dialog.setContentView(R.layout.dialog_quiz_overview);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                        dialog.show();

                                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                onBackPressed();
                                            }
                                        });

                                        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                onBackPressed();
                                            }
                                        });

                                        if(quizObj.getArenaName().contains("~~")){
                                            ((TextView)dialog.findViewById(R.id.tv_quiz_name)).setText(quizObj.getArenaName().split("~~")[0]);
                                        }else {
                                            ((TextView)dialog.findViewById(R.id.tv_quiz_name)).setText(quizObj.getArenaName());
                                        }

                                        ((TextView)dialog.findViewById(R.id.tv_questions)).setText(listQuestions.size()+"");

                                        int time = 0;

                                        for (int i=0;i<listQuestions.size();i++){
                                            time = time + Integer.parseInt(listQuestions.get(i).getQuestTime());
                                        }

                                        ((TextView)dialog.findViewById(R.id.tv_time)).setText(time+" seconds");

                                        if (quizObj.getArenaDesc().length()>0){
                                            ((TextView)dialog.findViewById(R.id.tv_desc)).setText(quizObj.getArenaDesc()+"");
                                        }else{
                                            ((TextView)dialog.findViewById(R.id.tv_desc)).setText("");
                                        }

                                        dialog.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                rvQNum.setLayoutManager(new LinearLayoutManager(ArenaQuizDisplay.this,RecyclerView.HORIZONTAL,false));
                                                adapter = new QuestionNumberAdapter(listQuestions.size());
                                                rvQNum.setAdapter(adapter);
                                                if (!quizObj.getUserId().equalsIgnoreCase(sObj.getStudentId()) &&!quizObj.getStudentId().equalsIgnoreCase(sObj.getStudentId()) && quizObj.getArenaUserStatus().equalsIgnoreCase("NA")){
                                                    //call insert

                                                    insertArenaScore(dialog);

                                                }else {
                                                    dialog.dismiss();
                                                    loadQuestion();
                                                }
                                            }
                                        });

                                        dialog.show();

                                    }
                                }
                            });



                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

    private void insertArenaScore(Dialog dialog) {

        utils.showLoader(this);

        JSONObject postObj = new JSONObject();

        try {
            postObj.put("arenaId",quizObj.getArenaId());
            postObj.put("userId",sObj.getStudentId());
            postObj.put("score","0");
            postObj.put("numQuestions",quizObj.getQuestionCount());
            postObj.put("totalTimeTaken","0");
            postObj.put("createdBy",quizObj.getUserId());
            postObj.put("schemaName",getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
            postObj.put("correctCount","0");
            postObj.put("wrongCount","0");
            postObj.put("unansweredCount","0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"insertScore "+postObj.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObj));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.InsertArenaScore)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(ArenaQuizDisplay.this,"Couldn't start the quiz!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            Toast.makeText(ArenaQuizDisplay.this,"Couldn't start the quiz!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObject = new JSONObject(resp);
                        if (respObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadQuestion();
                                    dialog.dismiss();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    Toast.makeText(ArenaQuizDisplay.this,"Couldn't start the quiz!",Toast.LENGTH_SHORT).show();
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

    private void updateArenaScore(){

        utils.showLoader(this);

        int score = correct*2;

        JSONObject postObj = new JSONObject();

        try {
            postObj.put("arenaId",quizObj.getArenaId());
            postObj.put("userId",sObj.getStudentId());
            postObj.put("score",""+score);
            postObj.put("numQuestions",quizObj.getQuestionCount());
            postObj.put("totalTimeTaken",""+totalTime);
            postObj.put("createdBy",quizObj.getUserId());
            postObj.put("schemaName",getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
            postObj.put("correctCount",""+correct);
            postObj.put("wrongCount",""+wrong);
            postObj.put("unansweredCount",""+skipped);
            postObj.put("arenaUserStatus","C");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"obj "+postObj);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObj));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.UpdateArenaScore)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArenaQuizDisplay.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Oops! There was a problem posting your answers! Please try again later!")

                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(ArenaQuizDisplay.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Oops! There was a problem posting your answers! Please try again later!")

                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            onBackPressed();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    utils.showLog(TAG,"resp "+resp);
                    try {
                        JSONObject responseObj = new JSONObject(resp);
                        if (responseObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getLeaderBoard();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArenaQuizDisplay.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Oops! There was a problem posting your answers! Please try again later!")

                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    onBackPressed();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
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

    boolean rankNext = false;
    int rankOffset = 0;

    void getLeaderBoard(){

        JSONObject postObj = new JSONObject();
        try {
            postObj.put("itemCount","10");
            postObj.put("schemaName", sh_Pref.getString("schema", ""));
            postObj.put("arenaId",quizObj.getArenaId());
            postObj.put("offset",rankOffset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"obj "+postObj);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObj));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.FetchArenaLeaderboard)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishedDialog();
                        Toast.makeText(ArenaQuizDisplay.this,"Failed to fetch leader board!",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finishedDialog();
                            Toast.makeText(ArenaQuizDisplay.this,"Failed to fetch leader board!",Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    utils.showLog(TAG,"resp "+resp);
                    try {
                        JSONObject responseObj = new JSONObject(resp);
                        if (responseObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONArray array = responseObj.getJSONArray("arenaRecords");

                                        if (array.length()==10){
                                            rankNext = true;
                                        }else {
                                            rankNext = false;
                                        }

                                        if (leaderBoard.size()==0) {
                                            leaderBoard.clear();
                                        }
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<ArenaScore>>() {
                                        }.getType();
                                        leaderBoard.addAll(gson.fromJson(array.toString(),type));

                                        if (!quizObj.getUserId().equalsIgnoreCase(sObj.getStudentId()) &&!quizObj.getStudentId().equalsIgnoreCase(sObj.getStudentId())) {
                                            if (rankOffset==0) {
                                                fetchMyRank();
                                            }
                                        }else   {
                                            finishedDialog();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

//                                    Toast.makeText(ArenaQuizDisplay.this,"Failed to fetch leader board!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    finishedDialog();
                                    Toast.makeText(ArenaQuizDisplay.this,"Failed to fetch leader board!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    boolean isMyRankAvail = false;
    JSONObject myRank = new JSONObject();


    void fetchMyRank(){

        JSONObject postObj = new JSONObject();
        try {
            postObj.put("userId",sObj.getStudentId());
            postObj.put("schemaName",sh_Pref.getString("schema",""));
            postObj.put("arenaId",quizObj.getArenaId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObj));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.FetchArenaUserRank)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishedDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.v(TAG,"rank "+resp);


                try {
                    JSONObject respObj = new JSONObject(resp);
                    if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONArray array = respObj.getJSONArray("arenaRank");
                                    if (array.length()>0){
                                        isMyRankAvail = true;
                                        myRank.put("rank",array.getJSONObject(0).getString("rank"));
                                        myRank.put("score",array.getJSONObject(0).getInt("score"));
                                        myRank.put("name",sObj.getStudentName());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                finishedDialog();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finishedDialog();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void finishedDialog(){
        Dialog  dialog = new Dialog(ArenaQuizDisplay.this);
        dialog.setContentView(R.layout.dialog_quiz_finish);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ((TextView)dialog.findViewById(R.id.tv_no_q)).setText("Number of Questions - "+listQuestions.size());
        ((TextView)dialog.findViewById(R.id.tv_correct)).setText("Correct Answers - "+correct);
        ((TextView)dialog.findViewById(R.id.tv_wrong)).setText("Wrong Answers - "+wrong);
        ((TextView)dialog.findViewById(R.id.tv_skipped)).setText("Skipped Answers - "+skipped);

        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if(quizObj.getArenaName().contains("~~")){
            ((TextView)dialog.findViewById(R.id.tv_title)).setText(quizObj.getArenaName().split("~~")[0]);
        }else {
            ((TextView)dialog.findViewById(R.id.tv_title)).setText(quizObj.getArenaName());
        }

        dialog.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        if (leaderBoard.size()>0){
            dialog.findViewById(R.id.ll_leaderboard).setVisibility(View.VISIBLE);

            ((RecyclerView)dialog.findViewById(R.id.rv_ranks)).setLayoutManager(new LinearLayoutManager(ArenaQuizDisplay.this));
            ((RecyclerView)dialog.findViewById(R.id.rv_ranks)).setAdapter(new RankAdapter());

        }else {
            dialog.findViewById(R.id.ll_leaderboard).setVisibility(View.GONE);
        }

        if (isMyRankAvail){
            dialog.findViewById(R.id.ll_leaderboard).setVisibility(View.VISIBLE);
            try {
                ((TextView)dialog.findViewById(R.id.tv_my_rank)).setText(myRank.getString("rank"));
                ((TextView)dialog.findViewById(R.id.tv_name)).setText(myRank.getString("name"));
                ((TextView)dialog.findViewById(R.id.tv_score)).setText(myRank.getString("score"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            dialog.findViewById(R.id.ll_leaderboard).setVisibility(View.GONE);
        }

        dialog.show();

    }

    class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder>{


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaQuizDisplay.this).inflate(R.layout.item_ranks,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSno.setText((position+1)+"");
            holder.tvName.setText(leaderBoard.get(position).getStudentName());
            holder.tvScore.setText(leaderBoard.get(position).getScore()+"");
            if (position==(leaderBoard.size()-1)){
                if (rankNext){
                    rankNext = false;
                    rankOffset = leaderBoard.size();
                    getLeaderBoard();
                }
            }
        }

        @Override
        public int getItemCount() {
            return leaderBoard.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSno,tvName,tvScore;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSno = itemView.findViewById(R.id.tv_s_no);
                tvName = itemView.findViewById(R.id.tv_name);
                tvScore = itemView.findViewById(R.id.tv_score);
            }
        }
    }

    private void loadQuestion() {

        unselectAll();

        int time = Integer.parseInt(listQuestions.get(currentPos).getQuestTime());

        switch (time){
            case 5:
                progressTimer.setMax(5);
                progressTimer.setProgress(5);
                break;
            case 30:
                progressTimer.setMax(30);
                progressTimer.setProgress(30);
                break;
            case 60:
                progressTimer.setMax(60);
                progressTimer.setProgress(60);
                break;
            case 120:
                progressTimer.setMax(120);
                progressTimer.setProgress(120);
                break;

        }

        timer = new CountDownTimer(time*1000,1000) {
            @Override
            public void onTick(long l) {
                int secondsRemaining = (int) (l / 1000);
                tvTime.setText(secondsRemaining+"");
                progressTimer.setProgress((progressTimer.getProgress()-1));
                totalTime++;
            }

            @Override
            public void onFinish() {
                skipped++;
                timer.cancel();
                progressTimer.setProgress(0);
                new AlertDialog.Builder(ArenaQuizDisplay.this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Your time is up for this question.")

                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if((currentPos+1)<listQuestions.size()){
                                    currentPos++;
                                    adapter.notifyDataSetChanged();
                                    loadQuestion();
                                    ((Button)findViewById(R.id.btn_submit)).setText("Submit");
                                }else{

                                    //call update
                                    if (!quizObj.getUserId().equalsIgnoreCase(sObj.getStudentId()) &&!quizObj.getStudentId().equalsIgnoreCase(sObj.getStudentId()) && quizObj.getArenaUserStatus().equalsIgnoreCase("NA")){
                                        getLeaderBoard();
                                    }else {
                                        updateArenaScore();
                                    }
                                }
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        };

        timer.start();

        if (listQuestions.get(currentPos).getQuestion().contains("~~")){

            findViewById(R.id.ll_q_image).setVisibility(View.VISIBLE);
            tvQ.setText("Q"+(currentPos+1)+". "+listQuestions.get(currentPos).getQuestion().split("~~")[0]);
            String[] s = listQuestions.get(currentPos).getQuestion().split("~~");
            tvQNoImage.setVisibility(View.GONE);

            for (String str:s){
                if (str.contains("http")){
                    Picasso.with(ArenaQuizDisplay.this).load(str).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
                }
            }

        }else {
            findViewById(R.id.ll_q_image).setVisibility(View.GONE);
            tvQNoImage.setText("Q"+(currentPos+1)+". "+listQuestions.get(currentPos).getQuestion());
            tvQNoImage.setVisibility(View.VISIBLE);
        }


        if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("TOF")){
            findViewById(R.id.ll_tof).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_mcq).setVisibility(View.GONE);



        }else{
            findViewById(R.id.ll_tof).setVisibility(View.GONE);
            findViewById(R.id.ll_mcq).setVisibility(View.VISIBLE);
            if (listQuestions.get(currentPos).getOption1().contains("~~")){
                tvOp1.setVisibility(View.GONE);
                ivOp1.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption1().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(ArenaQuizDisplay.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp1);
                    }
                }
            }else {
                tvOp1.setVisibility(View.VISIBLE);
                ivOp1.setVisibility(View.GONE);
                tvOp1.setText(listQuestions.get(currentPos).getOption1());
            }
            if (listQuestions.get(currentPos).getOption2().contains("~~")){
                tvOp2.setVisibility(View.GONE);
                ivOp2.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption2().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(ArenaQuizDisplay.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp2);
                    }
                }
            }else {
                tvOp2.setVisibility(View.VISIBLE);
                ivOp2.setVisibility(View.GONE);
                tvOp2.setText(listQuestions.get(currentPos).getOption2());
            }
            if (listQuestions.get(currentPos).getOption3().contains("~~")){
                tvOp3.setVisibility(View.GONE);
                ivOp3.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption3().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(ArenaQuizDisplay.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp3);
                    }
                }
            }else {
                tvOp3.setVisibility(View.VISIBLE);
                ivOp3.setVisibility(View.GONE);
                tvOp3.setText(listQuestions.get(currentPos).getOption3());
            }
            if (listQuestions.get(currentPos).getOption4().contains("~~")){
                tvOp4.setVisibility(View.GONE);
                ivOp4.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption4().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(ArenaQuizDisplay.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp4);
                    }
                }
            }else {
                tvOp4.setVisibility(View.VISIBLE);
                ivOp4.setVisibility(View.GONE);
                tvOp4.setText(listQuestions.get(currentPos).getOption4());
            }
        }
    }

    class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder>{

        int numberOfQuestions;

        public QuestionNumberAdapter(int numberOfQuestions) {
            this.numberOfQuestions = numberOfQuestions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaQuizDisplay.this).inflate(R.layout.item_question_number,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvQNum.setText((position+1)+"");

            if (position==currentPos){
                holder.llItem.setBackgroundResource(R.drawable.bg_blue_circle);
                holder.tvQNum.setTextColor(Color.parseColor("#2E2E8B"));
                holder.tvQNum.setTextColor(Color.WHITE);
            }else{
                holder.llItem.setBackgroundResource(R.drawable.bg_grey_circle_border);
                if (position<currentPos){
                    holder.tvQNum.setTextColor(Color.parseColor("#000000"));
                }else {
                    holder.tvQNum.setTextColor(Color.parseColor("#40000000"));
                }
            }

        }

        @Override
        public int getItemCount() {
            return numberOfQuestions;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQNum;
            LinearLayout llItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQNum = itemView.findViewById(R.id.tv_queNo);
                llItem = itemView.findViewById(R.id.ll_itemview);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (timer!=null){
            timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}