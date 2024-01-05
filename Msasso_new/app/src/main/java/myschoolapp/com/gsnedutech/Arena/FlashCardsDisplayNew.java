package myschoolapp.com.gsnedutech.Arena;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

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
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
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

public class FlashCardsDisplayNew extends AppCompatActivity {

    private static final String TAG = "SriRam -" + FlashCardsDisplayNew.class.getName();

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    ArenaRecord flashObj;

    List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    int currentPos = 0;
    int gotIt = 0;
    int studyAgain = 0;

    boolean front  = true;

    TextToSpeech tts;

    @BindView(R.id.stack_view)
    CardStackView cardStackView;


    @BindView(R.id.tv_count)
    TextView tvCount;

    List<CardsAdapter.ViewHolder> viewHolders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards_display_new);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    void init() {
        flashObj = (ArenaRecord) getIntent().getSerializableExtra("flashObj");

        cardStackView.setLayoutManager(new CardStackLayoutManager(this));
        cardStackView.setAdapter(new CardsAdapter());
        ((CardStackLayoutManager)cardStackView.getLayoutManager()).setSwipeableMethod(SwipeableMethod.Automatic);

        if (getIntent().hasExtra("reassign")){
            findViewById(R.id.tv_reassign_students).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_reassign_students).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSections();
                }
            });
        }


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




        findViewById(R.id.btn_flip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPos<listQuestions.size()) {
                    if (front) {
                        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardStackView, "scaleX", 1f, 0f);
                        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardStackView, "scaleX", 0f, 1f);
                        oa1.setInterpolator(new AccelerateDecelerateInterpolator());
                        oa2.setInterpolator(new DecelerateInterpolator());
                        oa1.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                viewHolders.get(currentPos).llFront.setVisibility(View.GONE);
                                viewHolders.get(currentPos).llBack.setVisibility(View.VISIBLE);
                                oa2.start();
                            }
                        });
                        oa1.start();
                        front = false;
                    } else {
                        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cardStackView, "scaleX", 1f, 0f);
                        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cardStackView, "scaleX", 0f, 1f);
                        oa1.setInterpolator(new DecelerateInterpolator());
                        oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                        oa1.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                viewHolders.get(currentPos).llBack.setVisibility(View.GONE);
                                viewHolders.get(currentPos).llFront.setVisibility(View.VISIBLE);
                                oa2.start();
                            }
                        });
                        oa1.start();
                        front = true;
                    }
                }
            }
        });

        tts = new TextToSpeech(FlashCardsDisplayNew.this, null);
        tts.setLanguage(Locale.US);



        findViewById(R.id.btn_got_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPos==listQuestions.size()){
                    if (getIntent().hasExtra("draft")){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                            submitArena(flashObj.getArenaId()+"","");
                            getSections();
                        }else {
                            getTeachers(flashObj.getArenaId()+"");
                        }
                    }
                    else {
                        showFinishDialog();
                    }
                }else {
                    viewHolders.get(currentPos).ivCorrect.setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_study_again).setEnabled(false);
                    findViewById(R.id.btn_got_it).setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentPos<listQuestions.size()){
                                gotIt++;
                            }
                            currentPos++;
                            if (currentPos<listQuestions.size()) {
                                tvCount.setText((currentPos + 1) + "/" + listQuestions.size());
                            }else {
                                if (getIntent().hasExtra("draft")){
                                    ((Button)findViewById(R.id.btn_got_it)).setText("Submit");
                                }
                                else {
                                    ((Button)findViewById(R.id.btn_got_it)).setText("Finish");
                                }

                            }
                            front = true;
                            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                                    .setDirection(Direction.Right)
                                    .setDuration(Duration.Slow.duration)
                                    .build();
                            ((CardStackLayoutManager)cardStackView.getLayoutManager()).setSwipeAnimationSetting(setting);
                            cardStackView.swipe();
                            findViewById(R.id.btn_study_again).setEnabled(true);
                            findViewById(R.id.btn_got_it).setEnabled(true);

                        }
                    },1000);


                }
            }
        });
        findViewById(R.id.btn_study_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPos==listQuestions.size()){
                    if (getIntent().hasExtra("draft")){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            submitArena(flashObj.getArenaId()+"","");
                            getSections();
                        }else {
                            getTeachers(flashObj.getArenaId()+"");
                        }
                    }
                    else {
                        showFinishDialog();
                    }
                }else {
                    viewHolders.get(currentPos).ivIncorrect.setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_study_again).setEnabled(false);
                    findViewById(R.id.btn_got_it).setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentPos<listQuestions.size()){
                                studyAgain++;
                            }
                            currentPos++;
                            if (currentPos<listQuestions.size()) {
                                tvCount.setText((currentPos + 1) + "/" + listQuestions.size());
                            }else {
                                ((Button)findViewById(R.id.btn_got_it)).setText("Finish");
                            }

                            front = true;
                            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                                    .setDirection(Direction.Left)
                                    .setDuration(Duration.Slow.duration)
                                    .build();
                            ((CardStackLayoutManager)cardStackView.getLayoutManager()).setSwipeAnimationSetting(setting);
                            cardStackView.swipe();
                            findViewById(R.id.btn_study_again).setEnabled(true);
                            findViewById(R.id.btn_got_it).setEnabled(true);
                        }
                    },1000);


                }
            }
        });

        getQuizDetails();

    }


    class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FlashCardsDisplayNew.this).inflate(R.layout.item_flash_card,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            viewHolders.add(holder);

            holder.llColor.setBackgroundColor(Color.parseColor(flashObj.getColor()));

            if (listQuestions.get(position).getQuestion().contains("~~")){
                String[] title = listQuestions.get(position).getQuestion().split("~~");
                if (title[0].equalsIgnoreCase("NA")){
                    holder.tvQuestion.setText("");
                }else {
                    holder.tvQuestion.setText(title[0]);

                }

                for (String str: title){
                    if (str.contains("http")){
                        holder.ivQImage.setVisibility(View.VISIBLE);
                        Picasso.with(FlashCardsDisplayNew.this).load(str).placeholder(R.drawable.ic_arena_add_image)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivQImage);
                    }
                }

            }else{
                holder.ivQImage.setVisibility(View.GONE);
                holder.tvQuestion.setText(listQuestions.get(position).getQuestion());
            }

            if (listQuestions.get(position).getAnswer().contains("~~")){
                String[] title = listQuestions.get(position).getAnswer().split("~~");
                if (title[0].equalsIgnoreCase("NA")){
                    holder.tvAns.setText("");
                }else {
                    holder.tvAns.setText(title[0]);

                }

                for (String str: title){
                    if (str.contains("http")){
                        holder.ivAImage.setVisibility(View.VISIBLE);
                        Picasso.with(FlashCardsDisplayNew.this).load(str).placeholder(R.drawable.ic_arena_add_image)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivAImage);
                    }
                }

            }else {
                holder.ivAImage.setVisibility(View.GONE);
                holder.tvAns.setText(listQuestions.get(position).getAnswer());
            }


            holder.ivSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (front){
                        tts.speak(holder.tvQuestion.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                    }else {
                        tts.speak(holder.tvAns.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listQuestions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llColor,llFront,llBack;
            TextView tvQuestion,tvAns;
            ImageView ivSpeak,ivQImage,ivAImage,ivCorrect,ivIncorrect;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                llColor = itemView.findViewById(R.id.ll_color);
                llFront = itemView.findViewById(R.id.ll_front);
                llBack = itemView.findViewById(R.id.ll_back);
                tvQuestion = itemView.findViewById(R.id.tv_ques);
                tvAns = itemView.findViewById(R.id.tv_ans);
                ivSpeak = itemView.findViewById(R.id.iv_speak);
                ivQImage = itemView.findViewById(R.id.iv_q_image);
                ivAImage = itemView.findViewById(R.id.iv_a_image);
                ivCorrect = itemView.findViewById(R.id.iv_correct);
                ivIncorrect = itemView.findViewById(R.id.iv_incorrect);
            }
        }
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
                                        cardStackView.getAdapter().notifyDataSetChanged();
                                        tvCount.setText((currentPos+1)+"/"+listQuestions.size());
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



    private void showFinishDialog() {
        final Dialog dialog = new Dialog(FlashCardsDisplayNew.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_finished_flash);
        dialog.setCancelable(false);
        DisplayMetrics metrics = new DisplayMetrics();
        FlashCardsDisplayNew.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int wwidth = metrics.widthPixels;
        dialog.getWindow().setLayout((int) (wwidth*0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView)dialog.findViewById(R.id.tv_no_q)).setText("Number of Questions - "+listQuestions.size());
        ((TextView)dialog.findViewById(R.id.tv_correct)).setText("You got it - "+gotIt);
        ((TextView)dialog.findViewById(R.id.tv_wrong)).setText("Study again - "+studyAgain);

        dialog.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        dialog.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(getIntent());
                finish();

            }
        });

        dialog.show();

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
                        new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                            new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                                    Toast.makeText(FlashCardsDisplayNew.this,"Success!",Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                        new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                            new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                                        Dialog dialog = new Dialog(FlashCardsDisplayNew.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(FlashCardsDisplayNew.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                                    new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FlashCardsDisplayNew.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+flashObj.getArenaId())
                .build();

        utils.showLog(TAG, AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+flashObj.getArenaId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(FlashCardsDisplayNew.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FlashCardsDisplayNew.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    for (int i=0;i<listBranches.size();i++){
                                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                            for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getAssignedId().equalsIgnoreCase("0")){
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(false);
                                                    }else {
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(true);
                                                        listBranches.get(i).setShow(true);
                                                        for (int a=0;a<listBranches.get(i).getCourses().size();a++) {
                                                            listBranches.get(i).getCourses().get(a).setShow(true);
                                                            if (listBranches.get(i).getCourses().get(a).getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())){
                                                                for (int b=0;b<listBranches.get(i).getCourses().get(a).getClasses().size();b++){
                                                                    listBranches.get(i).getCourses().get(a).getClasses().get(b).setShow(true);
                                                                    if (listBranches.get(i).getCourses().get(a).getClasses().get(b).getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                                                        for (int c=0;c<listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().size();c++){
                                                                            listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().get(c).setShow(true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(FlashCardsDisplayNew.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FlashCardsDisplayNew.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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

    public void arenaReview(String s, JSONArray sections) {

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
            jsonObject.put("arenaStatus","1");
            jsonObject.put("arenaDraftStatus","1");
            jsonObject.put("sections",sections);
            if (!flashObj.getStudentId().equalsIgnoreCase("")){
                jsonObject.put("createdBy",flashObj.getUserId()+"");
            }else {
                jsonObject.put("createdBy",tObj.getUserId()+"");
            }
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
                        new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                            new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                                        new AlertDialog.Builder(FlashCardsDisplayNew.this)
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
                                        new AlertDialog.Builder(FlashCardsDisplayNew.this)
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}