package myschoolapp.com.gsnedutech;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.AddArenaArticle;
import myschoolapp.com.gsnedutech.Arena.AddArenaAudioClips;
import myschoolapp.com.gsnedutech.Arena.AddArenaVideoClips;
import myschoolapp.com.gsnedutech.Arena.ArAddFlashCards;
import myschoolapp.com.gsnedutech.Arena.ArAddPoll;
import myschoolapp.com.gsnedutech.Arena.ArPollDisplayActivity;
import myschoolapp.com.gsnedutech.Arena.ArPollDisplayRejectedActivity;
import myschoolapp.com.gsnedutech.Arena.ArenaAudioDisplay;
import myschoolapp.com.gsnedutech.Arena.ArenaAudioDisplayRejected;
import myschoolapp.com.gsnedutech.Arena.ArenaDisplayActivity;
import myschoolapp.com.gsnedutech.Arena.ArenaQuizDisplay;
import myschoolapp.com.gsnedutech.Arena.ArenaQuizDisplayRejected;
import myschoolapp.com.gsnedutech.Arena.ArenaStoryDisplayRejected;
import myschoolapp.com.gsnedutech.Arena.ArenaVideoDisplay;
import myschoolapp.com.gsnedutech.Arena.FlashCardsDisplayNew;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.TeacherArenaQuizReview;
import myschoolapp.com.gsnedutech.Arena.Trial.ArAddQuizNew;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentArenaRecordings extends AppCompatActivity {

    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;
    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_drafts)
    TextView tvDrafts;

    @BindView(R.id.rv_audio_articles)
    RecyclerView rvAudioArticles;

    String arenaCategory="";

    MyUtils utils = new MyUtils();
    StudentObj sObj;
    TeacherObj tObj;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    String category ="1";

    boolean isDraft = false;

    List<ArenaRecord> listRecords = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_arena_recordings);

        ButterKnife.bind(this);

        init();

    }

    @Override
    protected void onResume() {
        if (isDraft){
            getArenas();
        }
        super.onResume();
    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            tvPending.setVisibility(View.GONE);
            tvRejected.setVisibility(View.GONE);
            tvApproved.setText("Submitted");
        }else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }



        arenaCategory = getIntent().getStringExtra("arenaCategory");

        findViewById(R.id.fab_arena).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (arenaCategory){
                    case "1":
                        startActivity(new Intent(StudentArenaRecordings.this, ArAddQuizNew.class));
                        break;
                    case "2":
                        startActivity(new Intent(StudentArenaRecordings.this, ArAddFlashCards.class));
                        break;
                    case "4":
                        startActivity(new Intent(StudentArenaRecordings.this, AddArenaAudioClips.class));
                        break;
                    case "5":
                        Intent intent = new Intent(StudentArenaRecordings.this, AddArenaVideoClips.class);
                        intent.putExtra("add", 1);
                        startActivity(intent);
                        break;
                    case "6":
                        startActivity(new Intent(StudentArenaRecordings.this, AddArenaArticle.class));
                        break;
                    case "7":
                        startActivity(new Intent(StudentArenaRecordings.this, ArAddPoll.class));
                        break;
                }
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });

        //title name according to category
        switch (arenaCategory){
            case "2":
                ((TextView)findViewById(R.id.tv_title)).setText("My Flash Cards");
                break;
            case "4":
                ((TextView)findViewById(R.id.tv_title)).setText("My Podcasts");
                break;
            case "5":
                ((TextView)findViewById(R.id.tv_title)).setText("My Vlogs");
                break;
            case "6":
                ((TextView)findViewById(R.id.tv_title)).setText("My Stories");
                break;
            case "1":
                ((TextView)findViewById(R.id.tv_title)).setText("My Quizzes");
                break;
            case "7":
                ((TextView)findViewById(R.id.tv_title)).setText("My Polls");
                break;
        }

        rvAudioArticles.setLayoutManager(new LinearLayoutManager(this));

        //approved parameters
        tvApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectAlTabs();
                isDraft = false;
                tvApproved.setTextColor(Color.WHITE);
                tvApproved.setBackgroundResource(R.drawable.bg_grad_tab_select);
                category="1";
                getArenas();

            }
        });

        //pending parameters
        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectAlTabs();
                isDraft = false;
                tvPending.setTextColor(Color.WHITE);
                tvPending.setBackgroundResource(R.drawable.bg_grad_tab_select);
                category="0";
                getArenas();

            }
        });

        //rejected parameters
        tvRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectAlTabs();
                isDraft = false;
                tvRejected.setTextColor(Color.WHITE);
                tvRejected.setBackgroundResource(R.drawable.bg_grad_tab_select);
                category="2";
                getArenas();

            }
        });

        //draft parameters
        tvDrafts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectAlTabs();
                isDraft = true;
                tvDrafts.setTextColor(Color.WHITE);
                tvDrafts.setBackgroundResource(R.drawable.bg_grad_tab_select);
                category="0";
                getArenas();

            }
        });

        //get student uploaded arenas according to parameters
        getArenas();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void unselectAlTabs() {
        tvApproved.setBackgroundResource(0);
        tvPending.setBackgroundResource(0);
        tvRejected.setBackgroundResource(0);
        tvDrafts.setBackgroundResource(0);
        tvApproved.setTextColor(Color.parseColor("#BBBBBB"));
        tvPending.setTextColor(Color.parseColor("#BBBBBB"));
        tvRejected.setTextColor(Color.parseColor("#BBBBBB"));
        tvDrafts.setTextColor(Color.parseColor("#BBBBBB"));
    }


    void getArenas(){

        utils.showLoader(this);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName",sh_Pref.getString("schema",""));

//            //if category 1 or 2 add arenaType Quiz
//            if (arenaCategory.equalsIgnoreCase("1") || arenaCategory.equalsIgnoreCase("2")){
//                jsonObject.put("arenaType","Quiz");
//            }else {
//                jsonObject.put("arenaType","General");
//            }

            //put arenaDraftStatus according to draft tab selected
            if (isDraft){
                jsonObject.put("arenaDraftStatus","0");
            }else {
                jsonObject.put("arenaDraftStatus","1");
            }

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                jsonObject.put("userId", tObj.getUserId());
                jsonObject.put("userRole","T");
            }else {
                jsonObject.put("userRole","S");
                jsonObject.put("arenaStatus",category);
                jsonObject.put("userId", sObj.getStudentId());
                jsonObject.put("sectionId", sObj.getClassCourseSectionId());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

//        String url = "";
//        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//            url = AppUrls.GetTeacherArenas;
//        }else {
//            url = AppUrls.GetStudentArenas;
//        }

        Log.v("tag","obj "+jsonObject);
        Log.v("tag","url "+AppUrls.GetStudentArenas);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentArenas)
                .post(body)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        rvAudioArticles.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null){
                    try {
                        JSONObject respObject = new JSONObject(resp);

                        if (respObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = respObject.getJSONArray("arenaRecords");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            listRecords.clear();
                            listRecords.addAll(gson.fromJson(array.toString(), type));

                            List<ArenaRecord> listAudioRecords = new ArrayList<>();


                            for (int i=0;i<listRecords.size();i++){
                                if (listRecords.get(i).getArenaCategory().equalsIgnoreCase(arenaCategory)){
                                    listAudioRecords.add(listRecords.get(i));
                                }
                            }

                            if (listAudioRecords.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvAudioArticles.setVisibility(View.VISIBLE);
                                        rvAudioArticles.setAdapter(new AudioArticleAdapter(listAudioRecords));
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvAudioArticles.setVisibility(View.GONE);
                                    }
                                });
                            }
//

                        }else if (respObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(respObject)){ //TODO New Changes
                            String message = respObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, StudentArenaRecordings.this, message, sh_Pref);
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvAudioArticles.setVisibility(View.GONE);
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

    class AudioArticleAdapter extends RecyclerView.Adapter<AudioArticleAdapter.ViewHolder>{

        List<ArenaRecord> listAudioArticles;

        public AudioArticleAdapter(List<ArenaRecord> listAudioArticles) {
            this.listAudioArticles = listAudioArticles;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            //different view type according to category Quiz or general
//            if (arenaCategory.equalsIgnoreCase("6") ){
//                return new ViewHolder(LayoutInflater.from(StudentArenaRecordings.this).inflate(R.layout.item_arena_recent_article,parent,false));
//            }else if(arenaCategory.equalsIgnoreCase("1") || arenaCategory.equalsIgnoreCase("2")){
//                return new ViewHolder(LayoutInflater.from(StudentArenaRecordings.this).inflate(R.layout.item_arena_quiz,parent,false));
//            }
//            else{
//                return new ViewHolder(LayoutInflater.from(StudentArenaRecordings.this).inflate(R.layout.item_arena_audio_article,parent,false));
//            }
            return new ViewHolder(LayoutInflater.from(StudentArenaRecordings.this).inflate(R.layout.item_arena_recent_article,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            String title[] = listAudioArticles.get(position).getArenaName().split("~~");
            holder.tvTitle.setText(title[0]);

            String url = "NA";

            for (String s:title){
                if (s.contains("http")){
                    url = s;
                }
            }

            if (!url.equalsIgnoreCase("NA")){
                Glide.with(StudentArenaRecordings.this)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true).placeholder(R.drawable.ic_arena_img).into(holder.ivArenaImg);
//                Picasso.with(StudentArenaRecordings.this).load().placeholder(R.drawable.ic_arena_img)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivArenaImg);
            }

            if (listAudioArticles.get(position).getArenaDesc().equalsIgnoreCase("") || listAudioArticles.get(position).getArenaDesc().equalsIgnoreCase("NA")){
                holder.tvDesc.setText("");
            }else {
                holder.tvDesc.setText(listAudioArticles.get(position).getArenaDesc());
            }

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                holder.tvStudentName.setText("By "+tObj.getUserName().split(" ")[0]);
            }else{
                holder.tvStudentName.setText("By "+sObj.getStudentName().split(" ")[0]);
            }
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listAudioArticles.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //displaying data of different view type according to category
            if (arenaCategory.equalsIgnoreCase("6")){

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = null;
                        if (isDraft){
                            intent = new Intent(StudentArenaRecordings.this, AddArenaArticle.class);
                        }else {
                            if (category.equalsIgnoreCase("2")){
                                intent = new Intent(StudentArenaRecordings.this, ArenaStoryDisplayRejected.class);
                            }else {
                                intent = new Intent(StudentArenaRecordings.this, ArenaDisplayActivity.class);
                                if (sh_Pref.getBoolean("teacher_loggedin", false) && category.equalsIgnoreCase("1")) {
                                    //teacher accepted reassign section
                                    intent.putExtra("reassign",true);
                                }
                            }
                        }

                        intent.putExtra("storyObj",(Serializable) listAudioArticles.get(position));
                        intent.putExtra("my",true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                });


                if (isDraft){
                    holder.ivDel.setVisibility(View.VISIBLE);
                    holder.ivDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            new AlertDialog.Builder(StudentArenaRecordings.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Are you sure you want to delete?")
                                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            deleteArena(listAudioArticles.get(position).getArenaId());
                                        }
                                    })

                                    .setCancelable(true)
                                    .show();

                        }
                    });
                }else {
                    holder.ivDel.setVisibility(View.GONE);
                }

            }

            else if(arenaCategory.equalsIgnoreCase("1") || arenaCategory.equalsIgnoreCase("2")){

                if (isDraft){
                    holder.ivDel.setVisibility(View.VISIBLE);
                    holder.ivDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            new AlertDialog.Builder(StudentArenaRecordings.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Are you sure you want to delete?")
                                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            deleteArena(listAudioArticles.get(position).getArenaId());
                                        }
                                    })

                                    .setCancelable(true)
                                    .show();

                        }
                    });
                }else {
                    holder.ivDel.setVisibility(View.GONE);
                }

//               if(listAudioArticles.get(position).getArenaName().contains("~~")){
//                   holder.tvTitle.setText(listAudioArticles.get(position).getArenaName().split("~~")[0]);
//               }else {
//                   holder.tvTitle.setText(listAudioArticles.get(position).getArenaName());
//               }
                if (arenaCategory.equalsIgnoreCase("1")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isDraft){
                                Intent intent = new Intent(StudentArenaRecordings.this, ArAddQuizNew.class);
                                intent.putExtra("arenaId", listAudioArticles.get(position).getArenaId()+"");
                                intent.putExtra("count", listAudioArticles.get(position).getQuestionCount()+"");
                                intent.putExtra("title",listAudioArticles.get(position).getArenaName());
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }else {
                                if (sh_Pref.getBoolean("student_loggedin", false)) {

                                    if (category.equalsIgnoreCase("2")){
                                        Intent intent = new Intent(StudentArenaRecordings.this, ArenaQuizDisplayRejected.class);
                                        intent.putExtra("quizObj", (Serializable) listAudioArticles.get(position));
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }else {

                                        Intent intent = new Intent(StudentArenaRecordings.this, ArenaQuizDisplay.class);
                                        intent.putExtra("quizObj", (Serializable) listAudioArticles.get(position));

                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                }else{
                                    Intent intent = new Intent(StudentArenaRecordings.this, TeacherArenaQuizReview.class);
                                    intent.putExtra("item", (Serializable) listAudioArticles.get(position));
                                    intent.putExtra("status", "1");
                                    if (sh_Pref.getBoolean("teacher_loggedin", false) && category.equalsIgnoreCase("1")) {
                                        //teacher accepted reassign section
                                        intent.putExtra("reassign",true);
                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            }
                        }
                    });
                }else{
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (category.equalsIgnoreCase("2")){
                                Intent intent = new Intent(StudentArenaRecordings.this, FlashCardsDisplayRejected.class);
                                intent.putExtra("flashObj",(Serializable) listAudioArticles.get(position));
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }else {
                                Intent intent = new Intent(StudentArenaRecordings.this, FlashCardsDisplayNew.class);
                                intent.putExtra("flashObj",(Serializable) listAudioArticles.get(position));

                                if (isDraft){
                                    intent.putExtra("draft",true);
                                }
                                if (sh_Pref.getBoolean("teacher_loggedin", false) && category.equalsIgnoreCase("1")) {
                                    //teacher accepted reassign section
                                    intent.putExtra("reassign",true);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }

                        }
                    });
                }
            }
            else if (arenaCategory.equalsIgnoreCase("7")){
                if (isDraft){
                    holder.ivDel.setVisibility(View.VISIBLE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(StudentArenaRecordings.this, ArPollDisplayActivity.class);
                            intent.putExtra("poll",(Serializable) listAudioArticles.get(position));
                            intent.putExtra("draft",true);
                            startActivity(intent);
                        }
                    });
                }else {
                    holder.ivDel.setVisibility(View.GONE);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (category.equalsIgnoreCase("2")){
                                Intent intent = new Intent(StudentArenaRecordings.this, ArPollDisplayRejectedActivity.class);
                                intent.putExtra("poll", (Serializable) listAudioArticles.get(position));
                                intent.putExtra("self", true);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(StudentArenaRecordings.this, ArPollDisplayActivity.class);
                                intent.putExtra("poll", (Serializable) listAudioArticles.get(position));
                                intent.putExtra("self", true);
                                if (sh_Pref.getBoolean("teacher_loggedin", false) && category.equalsIgnoreCase("1")) {
                                    //teacher accepted reassign section
                                    intent.putExtra("reassign",true);
                                }
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
            else{
//               if(listAudioArticles.get(position).getArenaName().contains("~~")){
//                   holder.tvArenaTitle.setText(listAudioArticles.get(position).getArenaName().split("~~")[0]);
//               }else {
//                   holder.tvArenaTitle.setText(listAudioArticles.get(position).getArenaName());
//               }
//               holder.tvArenaDesc.setText(listAudioArticles.get(position).getArenaDesc());
//               try {
//                   holder.tvArenaDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listAudioArticles.get(position).getCreatedDate())));
//                   holder.tvArenaTime.setText(new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listAudioArticles.get(position).getCreatedDate())));
//               } catch (ParseException e) {
//                   e.printStackTrace();
//               }

                if (isDraft){
                    holder.ivDel.setVisibility(View.VISIBLE);
                    holder.ivDel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AlertDialog.Builder(StudentArenaRecordings.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Are you sure you want to delete?")
                                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            deleteArena(listAudioArticles.get(position).getArenaId());
                                        }
                                    })

                                    .setCancelable(true)
                                    .show();
                        }
                    });
                }else {
                    holder.ivDel.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = null;
                        if (isDraft){
                            if(arenaCategory.equalsIgnoreCase("4")){
                                intent = new Intent(StudentArenaRecordings.this, AddArenaAudioClips.class);
                                intent.putExtra("audioObj", (Serializable) listAudioArticles.get(position));
                            }else if (arenaCategory.equalsIgnoreCase("5")){
                                intent = new Intent(StudentArenaRecordings.this, AddArenaVideoClips.class);
                                intent.putExtra("videoObj", (Serializable) listAudioArticles.get(position));
                            }
                        }else {
                            if(arenaCategory.equalsIgnoreCase("4")){

                                //check rejected

                                if (category.equalsIgnoreCase("2")){
                                    intent = new Intent(StudentArenaRecordings.this, ArenaAudioDisplayRejected.class);
                                    intent.putExtra("audioObj", (Serializable) listAudioArticles.get(position));
                                }else {
                                    intent = new Intent(StudentArenaRecordings.this, ArenaAudioDisplay.class);
                                    intent.putExtra("self", true);
                                    if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                        //teacher accepted reassign section
                                        intent.putExtra("reassign",true);
                                    }
                                    intent.putExtra("audioObj", (Serializable) listAudioArticles.get(position));
                                }
                            }else if (arenaCategory.equalsIgnoreCase("5")){
                                if (category.equalsIgnoreCase("2")){
                                    intent = new Intent(StudentArenaRecordings.this, ArenaVideoDisplayRejected.class);
                                    intent.putExtra("videoObj", (Serializable) listAudioArticles.get(position));
                                }else {
                                    intent = new Intent(StudentArenaRecordings.this, ArenaVideoDisplay.class);
                                    intent.putExtra("self", true);
                                    intent.putExtra("videoObj", (Serializable) listAudioArticles.get(position));
                                    if (sh_Pref.getBoolean("teacher_loggedin", false) && category.equalsIgnoreCase("1")) {
                                        //teacher accepted reassign section
                                        intent.putExtra("reassign",true);
                                    }
                                }
                            }
                            else {
                                intent = new Intent(StudentArenaRecordings.this, StudentArenaFiles.class);
                                intent.putExtra("audioObj", (Serializable) listAudioArticles.get(position));

                            }

                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return listAudioArticles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvArenaTitle,tvArenaDesc,tvArenaDate,tvArenaTime;

            ImageView ivArenaImg,ivDel;
            TextView tvTitle,tvDesc,tvStudentName,tvDate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvArenaTitle = itemView.findViewById(R.id.tv_arena_title);
                tvArenaDesc = itemView.findViewById(R.id.tv_arena_desc);
                tvArenaDate = itemView.findViewById(R.id.tv_arena_date);
                tvArenaTime = itemView.findViewById(R.id.tv_arena_time);

                ivArenaImg = itemView.findViewById(R.id.iv_arena_img);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDesc = itemView.findViewById(R.id.tv_desc);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvDate = itemView.findViewById(R.id.tv_date);

                ivDel = itemView.findViewById(R.id.iv_del);

            }
        }
    }


    void deleteArena(int arenaId){
        utils.showLoader(this);

        JSONObject obj = new JSONObject();

        try {
            obj.put("schemaName", sh_Pref.getString("schema",""));
            obj.put("arenaId",""+arenaId);

            if (arenaCategory.equalsIgnoreCase("1") || arenaCategory.equalsIgnoreCase("2")){
                obj.put("arenaType","Quiz");
            }else {
                obj.put("arenaType","General");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("tag","obj "+obj);
        Log.v("tag","url "+AppUrls.DeleteArena);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(obj));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.DeleteArena)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(StudentArenaRecordings.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("oops! something went wrong please try again later")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                utils.showLog("tag","response "+resp);

                if (!response.isSuccessful()){
                    utils.dismissDialog();
                    new AlertDialog.Builder(StudentArenaRecordings.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("oops! something went wrong please try again later")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })

                            .setCancelable(false)
                            .show();
                }else {
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(StudentArenaRecordings.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Deleted successfully!")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getArenas();
                                                    dialog.dismiss();
                                                }
                                            })

                                            .setCancelable(false)
                                            .show();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(StudentArenaRecordings.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("oops! something went wrong please try again later")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


}
