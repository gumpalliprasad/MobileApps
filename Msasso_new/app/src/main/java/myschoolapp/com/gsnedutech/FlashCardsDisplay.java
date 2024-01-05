package myschoolapp.com.gsnedutech;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlashCardsDisplay extends AppCompatActivity {

    private static final String TAG = "SriRam -" + FlashCardsDisplay.class.getName();

    @BindView(R.id.rv_q_num)
    RecyclerView rvQNum;

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

    @BindView(R.id.ll_color)
    LinearLayout llColor;

    @BindView(R.id.iv_speak)
    ImageView ivSpeak;

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    ArenaRecord flashObj;

    List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    int currentPos = 0;

    QuestionNumberAdapter adapter;

    boolean front  = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards_display);
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
        flashObj = (ArenaRecord) getIntent().getSerializableExtra("flashObj");

        if (flashObj.getColor()!= null && !(flashObj.getColor().equalsIgnoreCase("")))
            llColor.setBackgroundColor(Color.parseColor(flashObj.getColor()));

        if(flashObj.getArenaName().contains("~~")){
            ((TextView)findViewById(R.id.tv_title)).setText(flashObj.getArenaName().split("~~")[0]);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(flashObj.getArenaName());
        }


        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        }else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }

        rvQNum.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("draft")){
            ((Button)findViewById(R.id.btn_got_it)).setText("Send For Approval");
            findViewById(R.id.btn_got_it).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                        submitArena(flashObj.getArenaId()+"","");
                    }else {
                        getTeachers(flashObj.getArenaId()+"");
                    }
                }
            });
        }else {

            findViewById(R.id.btn_got_it).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((currentPos + 1) < listQuestions.size()) {

                        currentPos++;
                        if (!front) {
                            llFront.setVisibility(View.VISIBLE);
                            front = true;
                        }
                        loadQuestion();
                        adapter.notifyDataSetChanged();

                    } else {
                        showFinishDialog();
                    }
                }
            });
        }


        findViewById(R.id.cv_falsh_card).setOnClickListener(new View.OnClickListener() {
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

        TextToSpeech tts = new TextToSpeech(FlashCardsDisplay.this, null);
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

        getQuizDetails();

    }

    private void submitArena(String arenaId,String teacherId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("arenaId",arenaId);
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("createdBy", tObj.getUserId());
                postObject.put("arenaStatus", "1");
            }else {
                postObject.put("userId", sObj.getStudentId());
                postObject.put("createdBy", sObj.getStudentId());
                postObject.put("teacherId", teacherId);
            }
            postObject.put("arenaDraftStatus","1");
            postObject.put("schemaName", sh_Pref.getString("schema",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = new AppUrls().SubmitTeacherArena;
        }else {
            url = new AppUrls().SubmitStudentArena;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(FlashCardsDisplay.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("oops! something went wrong please try again later")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(FlashCardsDisplay.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("oops! something went wrong please try again later")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
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
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FlashCardsDisplay.this,"Success!",Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(FlashCardsDisplay.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("oops! something went wrong please try again later")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
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

    private void showFinishDialog() {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Completed all the questions. Do you want to finish?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                })
                .setCancelable(false)
                .show();

    }

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            if (flashObj.getStudentName()!=null && !(flashObj.getStudentName().equalsIgnoreCase(""))){
                url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + flashObj.getArenaId()+""+"&arenaCategory=2";
            }else {
                url = AppUrls.GetQuizQuestionsById + "schemaName=" + sh_Pref.getString("schema", "") + "&arenaId=" + flashObj.getArenaId() + "" + "&userId=" + tObj.getUserId()+"&arenaCategory=2";
            }
        }else {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + flashObj.getArenaId()+""+"&arenaCategory=2";
        }

        Request get = new Request.Builder()
                .url(url)
                .build();

        utils.showLog(TAG, "url "+url);

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
//                                        Dialog dialog = new Dialog(FlashCardsDisplay.this);
//                                        dialog.setContentView(R.layout.dialog_flash_intro);
//                                        dialog.setCancelable(true);
//                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                                        dialog.show();
//
//
//
//                                        dialog.findViewById(R.id.fl_background).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                dialog.dismiss();
//                                                rvQNum.setLayoutManager(new LinearLayoutManager(FlashCardsDisplay.this,RecyclerView.HORIZONTAL,false));
//                                                adapter = new QuestionNumberAdapter(listQuestions.size());
//                                                rvQNum.setAdapter(adapter);
//                                                loadQuestion();
//                                            }
//                                        });
//
//                                        dialog.show();

                                        rvQNum.setLayoutManager(new LinearLayoutManager(FlashCardsDisplay.this,RecyclerView.HORIZONTAL,false));
                                        adapter = new QuestionNumberAdapter(listQuestions.size());
                                        rvQNum.setAdapter(adapter);
                                        loadQuestion();

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

    void loadQuestion(){

        if((currentPos+1)==listQuestions.size()){

            ((TextView)findViewById(R.id.btn_got_it)).setText("FINISH");
        }else{
            if (getIntent().hasExtra("draft")) {
                ((Button) findViewById(R.id.btn_got_it)).setText("Send For Approval");
            }else {
                ((TextView) findViewById(R.id.btn_got_it)).setText("NEXT");
            }
        }

        if (listQuestions.get(currentPos).getQuestion().contains("~~")){
            String[] title = listQuestions.get(currentPos).getQuestion().split("~~");
            if (title[0].equalsIgnoreCase("NA")){
                tvQues.setText("");
            }else {
                tvQues.setText(title[0]);

            }

            for (String str: title){
                if (str.contains("http")){
                    ivQ.setVisibility(View.VISIBLE);
                    Picasso.with(FlashCardsDisplay.this).load(str).placeholder(R.drawable.ic_arena_add_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQ);
                }
            }

        }else{
            ivQ.setVisibility(View.GONE);
            tvQues.setText(listQuestions.get(currentPos).getQuestion());
        }

        if (listQuestions.get(currentPos).getAnswer().contains("~~")){
            String[] title = listQuestions.get(currentPos).getAnswer().split("~~");
            if (title[0].equalsIgnoreCase("NA")){
                tvAnswer.setText("");
            }else {
                tvAnswer.setText(title[0]);

            }

            for (String str: title){
                if (str.contains("http")){
                    ivA.setVisibility(View.VISIBLE);
                    Picasso.with(FlashCardsDisplay.this).load(str).placeholder(R.drawable.ic_arena_add_image)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivA);
                }
            }

        }else {
            ivA.setVisibility(View.GONE);
            tvAnswer.setText(listQuestions.get(currentPos).getAnswer());
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
            return new ViewHolder(LayoutInflater.from(FlashCardsDisplay.this).inflate(R.layout.item_question_number,parent,false));
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentPos=position;
                    if (!front){
                        llFront.setVisibility(View.VISIBLE);
                        front=true;
                    }
                    loadQuestion();
                    notifyDataSetChanged();
                }
            });

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


    private void getTeachers(String arenaId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("sectionId",sObj.getClassCourseSectionId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeachersForArena)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(FlashCardsDisplay.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nFlash card Article saved in drafts in my flash cards. Can submit for approval later.")

                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(FlashCardsDisplay.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my stories. Can submit for approval later.")

                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = respObj.getJSONArray("arenaRecords");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaTeacherList>>(){}.getType();
                            listTeachers.clear();
                            listTeachers.addAll(gson.fromJson(array.toString(),type));

                            if (listTeachers.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog dialog = new Dialog(FlashCardsDisplay.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(FlashCardsDisplay.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my stories. Can submit for approval later.")

                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });

                                        RecyclerView rvTeachers = dialog.findViewById(R.id.rv_teachers);
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(FlashCardsDisplay.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(FlashCardsDisplay.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("OOps! There was a problem.\nFlash Card Article saved in drafts in my flash cards. Can submit for approval later.")

                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(FlashCardsDisplay.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("No Teachers assigned for your section.\nFlash Cards cannot be sent for approval\nPlease contact your school for more information.")

                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
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

    class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder>{

        List<ArenaTeacherList> teacherList; String arenaId;

        public TeacherAdapter(List<ArenaTeacherList> teacherList, String arenaId) {
            this.teacherList = teacherList;
            this.arenaId = arenaId;
        }

        @NonNull
        @Override
        public FlashCardsDisplay.TeacherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FlashCardsDisplay.TeacherAdapter.ViewHolder(LayoutInflater.from(FlashCardsDisplay.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FlashCardsDisplay.TeacherAdapter.ViewHolder holder, int position) {
            holder.tvName.setText(listTeachers.get(position).getUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitArena(arenaId,listTeachers.get(position).getTeacherId()+"");
                }
            });

        }

        @Override
        public int getItemCount() {
            return teacherList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}