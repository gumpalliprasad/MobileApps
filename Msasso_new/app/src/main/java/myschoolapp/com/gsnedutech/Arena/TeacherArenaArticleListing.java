package myschoolapp.com.gsnedutech.Arena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Fragments.TeacherBottomArena;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherArenaArticleListing extends AppCompatActivity {

    int[] colors = new DrawableUtils().getDarkPalet();

    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    @BindView(R.id.rv_articles)
    RecyclerView rvArticles;

    String category = "";

    MyUtils utils = new MyUtils();
    private static final String TAG = TeacherBottomArena.class.getName();
    List<ArenaRecord> listArenas = new ArrayList<>();

    String arenaStatus = "0";

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_arena_article_listng);
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

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);


        category = getIntent().getStringExtra("category");
        switch (category){
            case "1":((TextView)findViewById(R.id.tv_title)).setText("Quizzes");
                break;
            case "2":((TextView)findViewById(R.id.tv_title)).setText("Flash Cards");
                break;
            case "6":((TextView)findViewById(R.id.tv_title)).setText("Story");
                break;
            case "5":((TextView)findViewById(R.id.tv_title)).setText("Video");
                break;
            case "4":((TextView)findViewById(R.id.tv_title)).setText("Audio");
                break;
            case "7":((TextView)findViewById(R.id.tv_title)).setText("Polls");
                break;
        }

        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arenaStatus="0";
                unSelectAll();
                tvPending.setTextColor(Color.BLACK);
                tvPending.setBackgroundResource(R.drawable.underline);
                getTeacherArenas();
            }
        });
        tvApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arenaStatus="1";
                unSelectAll();
                tvApproved.setTextColor(Color.BLACK);
                tvApproved.setBackgroundResource(R.drawable.underline);
                getTeacherArenas();
            }
        });
        tvRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arenaStatus="2";
                unSelectAll();
                tvRejected.setTextColor(Color.BLACK);
                tvRejected.setBackgroundResource(R.drawable.underline);
                getTeacherArenas();
            }
        });


    }

    @Override
    protected void onResume() {
        getTeacherArenas();
        super.onResume();
    }

    void unSelectAll(){
        tvPending.setBackgroundResource(0);
        tvApproved.setBackgroundResource(0);
        tvRejected.setBackgroundResource(0);
        tvPending.setTextColor(Color.parseColor("#64494949"));
        tvApproved.setTextColor(Color.parseColor("#64494949"));
        tvRejected.setTextColor(Color.parseColor("#64494949"));
    }

    void getTeacherArenas(){
        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
            postObject.put("teacherId",tObj.getUserId());
            postObject.put("arenaStatus",arenaStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().GetArenasforTeacher);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenasforTeacher)
                .post(body)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                    rvArticles.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                utils.showLog(TAG,"response "+resp);
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null){
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaRecords");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            listArenas.clear();
                            listArenas.addAll(gson.fromJson(array.toString(),type));

                            List<ArenaRecord> arenas = new ArrayList<>();
                            arenas.clear();

                            for (int i=0;i<listArenas.size();i++){
                                if (listArenas.get(i).getArenaCategory().equalsIgnoreCase(category)){
                                    arenas.add(listArenas.get(i));
                                }
                            }

                            if (arenas.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvArticles.setVisibility(View.VISIBLE);
                                        rvArticles.setLayoutManager(new LinearLayoutManager(TeacherArenaArticleListing.this));
                                        rvArticles.setAdapter(new AdapterArticles(arenas));
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvArticles.setVisibility(View.GONE);
                                    }
                                });
                            }

                        }else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)){ //TODO New Changes
                            String message = jsonObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, TeacherArenaArticleListing.this, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> rvArticles.setVisibility(View.GONE));
                    }
                }else{
                    runOnUiThread(() -> rvArticles.setVisibility(View.GONE));
                }
            }
        });
    }



    class AdapterArticles extends RecyclerView.Adapter<AdapterArticles.ViewHolder>{

        List<ArenaRecord> arenas;

        public AdapterArticles(List<ArenaRecord> arenas) {
            this.arenas = arenas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherArenaArticleListing.this).inflate(R.layout.item_arena_article_teacher_status_list,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.flBg.setBackgroundResource(colors[position % colors.length]);

            if (arenas.get(position).getArenaName().contains("~~")){
                holder.tvArticleTitle.setText(arenas.get(position).getArenaName().split("~~")[0]);
            }else {
                holder.tvArticleTitle.setText(arenas.get(position).getArenaName());
            }

            holder.tvCreatedBy.setText("Created By "+arenas.get(position).getStudentName());
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(arenas.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (arenaStatus.equalsIgnoreCase("0")){
                    Intent intent = null;
                    switch (category){
                        case "1":
                            intent = new Intent(TeacherArenaArticleListing.this,TeacherArenaQuizReview.class);
                            intent.putExtra("item",(Serializable) arenas.get(position));
                            intent.putExtra("status",arenaStatus);
                            if (arenaStatus.equalsIgnoreCase("1")){
                                intent.putExtra("reassign",true);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "2":
                            if(arenaStatus.equalsIgnoreCase("0")) {
                                intent = new Intent(TeacherArenaArticleListing.this, TeacherArenaFlashCardReview.class);
                                intent.putExtra("item", (Serializable) arenas.get(position));
                                intent.putExtra("status", arenaStatus);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }else {
                                intent = new Intent(TeacherArenaArticleListing.this, FlashCardsDisplayNew.class);
                                intent.putExtra("flashObj", (Serializable) arenas.get(position));
                                if (arenaStatus.equalsIgnoreCase("1")){
                                    intent.putExtra("reassign",true);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                        case "4":
                            if(arenaStatus.equalsIgnoreCase("0")) {
                                intent = new Intent(TeacherArenaArticleListing.this, TeacherArenaArticleReview.class);
                                intent.putExtra("item",(Serializable) arenas.get(position));
                                intent.putExtra("status",arenaStatus);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }else{
                                intent = new Intent(TeacherArenaArticleListing.this,ArenaAudioDisplay.class);
                                intent.putExtra("audioObj",(Serializable) arenas.get(position));
                                if (arenaStatus.equalsIgnoreCase("1")){
                                    intent.putExtra("reassign",true);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                        case "5":
                            if(arenaStatus.equalsIgnoreCase("0")) {
                                intent = new Intent(TeacherArenaArticleListing.this, TeacherArenaArticleReview.class);
                                intent.putExtra("item",(Serializable) arenas.get(position));
                                intent.putExtra("status",arenaStatus);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }else{
                                intent = new Intent(TeacherArenaArticleListing.this,ArenaVideoDisplay.class);
                                intent.putExtra("videoObj",(Serializable) arenas.get(position));
                                if (arenaStatus.equalsIgnoreCase("1")){
                                    intent.putExtra("reassign",true);
                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                        case "6":
                            intent = new Intent(TeacherArenaArticleListing.this, TeacherArenaArticleReview.class);
                            intent.putExtra("item",(Serializable) arenas.get(position));
                            intent.putExtra("status",arenaStatus);
                            if (arenaStatus.equalsIgnoreCase("1")){
                                intent.putExtra("reassign",true);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "7":
                            intent = new Intent(TeacherArenaArticleListing.this,TeacherPollReview.class);
                            intent.putExtra("item",(Serializable) arenas.get(position));
                            intent.putExtra("status",arenaStatus);
                            if (arenaStatus.equalsIgnoreCase("1")){
                                intent.putExtra("reassign",true);
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                    }
//                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arenas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvArticleTitle,tvCreatedBy,tvDate;
            FrameLayout flBg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvArticleTitle = itemView.findViewById(R.id.tv_article_title);
                tvCreatedBy = itemView.findViewById(R.id.tv_created_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                flBg = itemView.findViewById(R.id.fl_bg);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}