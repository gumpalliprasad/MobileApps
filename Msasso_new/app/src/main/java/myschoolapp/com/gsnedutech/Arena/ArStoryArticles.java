package myschoolapp.com.gsnedutech.Arena;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

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
import myschoolapp.com.gsnedutech.Fragments.StudentBottomArena;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
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

public class ArStoryArticles extends AppCompatActivity {

    @BindView(R.id.rv_popular_articles)
    RecyclerView rvPopularArticles;
    @BindView(R.id.rv_recent_articles)
    RecyclerView rvRecentArticles;
    @BindView(R.id.nsv)
    NestedScrollView scroll;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    TeacherObj tObj;

    int popularOffset = 0;
    int recentOffset = 0;
    boolean hasNextPopularPage = false;
    boolean hasNextRecentPage = false;

    List<ArenaRecord> listPopularArenas = new ArrayList<>();
    List<ArenaRecord> listRecentArenas = new ArrayList<>();

    MyUtils utils = new MyUtils();

    private static final String TAG = "SriRam -" + StudentBottomArena.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_story_articles);

        ButterKnife.bind(this);

        init();
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        }else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }


        rvPopularArticles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
//        rvPopularArticles.setAdapter(new PopularArticleAdapter());

        rvRecentArticles.setLayoutManager(new LinearLayoutManager(this));
        rvRecentArticles.setAdapter(new RecentArticleAdapter(listRecentArenas));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) scroll.getChildAt(scroll.getChildCount() - 1);

                int diff = (view.getBottom() - (scroll.getHeight() + scroll
                        .getScrollY()));

                if (diff == 0) {
                    if (hasNextRecentPage){
                        utils.showLoader(ArStoryArticles.this);
                        recentOffset = recentOffset+25;
                        getRecentArenas();
                    }
                }
            }
        });


        //get popular arenas service
        getPopularArenas();
    }

    private void getRecentArenas() {
        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
//            postObject.put("studentId", sObj.getStudentId());
            if (!(sh_Pref.getBoolean("teacher_loggedin", false))) {
                postObject.put("sectionId", "0," + sObj.getClassCourseSectionId());
                postObject.put("myUserId",sObj.getStudentId());
            }else {
                postObject.put("myUserId",tObj.getUserId());
            }
            postObject.put("arenaType", "General");
            postObject.put("arenaStatus","1");
            postObject.put("itemCount","25");
            postObject.put("offset",recentOffset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        utils.showLog(TAG, "post body"+String.valueOf(postObject));
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenas)
                .post(body)
                .build();

        utils.showLog(TAG, "url "+ AppUrls.GetArenas);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        rvRecentArticles.setVisibility(View.GONE);
                        findViewById(R.id.tv_no_recent_articles).setVisibility(View.VISIBLE);
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
                            rvRecentArticles.setVisibility(View.GONE);
                            findViewById(R.id.tv_no_recent_articles).setVisibility(View.VISIBLE);
                        }
                    });
                }else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("arenaRecords");

                            if (jsonArray.length()==25){
                                hasNextRecentPage = true;
                            }else {
                                hasNextRecentPage = false;
                            }

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (recentOffset==0) {
                                listRecentArenas.clear();
                            }
                            listRecentArenas.addAll(gson.fromJson(jsonArray.toString(),type));

//                            List<ArenaRecord> list = new ArrayList<>();
//
//                            for (int i=0;i<listRecentArenas.size();i++){
//                                if (!listRecentArenas.get(i).getArenaCategory().equalsIgnoreCase("1") || !listRecentArenas.get(i).getArenaCategory().equalsIgnoreCase("2")){
//                                    list.add(listRecentArenas.get(i));
//                                }
//                            }
//
//
//                            listRecentArenas.clear();
//                            listRecentArenas.addAll(list);

                            if (listRecentArenas.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvRecentArticles.setVisibility(View.VISIBLE);
                                        findViewById(R.id.tv_no_recent_articles).setVisibility(View.GONE);
                                        rvRecentArticles.getAdapter().notifyDataSetChanged();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvRecentArticles.setVisibility(View.GONE);
                                        findViewById(R.id.tv_no_recent_articles).setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvRecentArticles.setVisibility(View.GONE);
                                    findViewById(R.id.tv_no_recent_articles).setVisibility(View.VISIBLE);
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

    private void getPopularArenas() {
        utils.showLoader(this);
        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            if (!(sh_Pref.getBoolean("teacher_loggedin", false))) {
                postObject.put("sectionId", "0," + sObj.getClassCourseSectionId());
                postObject.put("myUserId",sObj.getStudentId());
            }else {
                postObject.put("myUserId",tObj.getUserId());
            }
            postObject.put("itemCount","25");
            postObject.put("offset",popularOffset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        utils.showLog(TAG, "url body"+postObject.toString());
        utils.showLog(TAG, "url "+new AppUrls().GetPopularArenas);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetPopularArenas)
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
                        findViewById(R.id.tv_no_popular_article).setVisibility(View.VISIBLE);
                        if (popularOffset==0){
                            getRecentArenas();
                        }

                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();
                utils.showLog(TAG,"rsponse "+resp);
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
                if (response.body() != null){
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaRecords");
                            if (array.length()==25){
                                hasNextPopularPage = true;
                            }else {
                                hasNextPopularPage = false;
                            }

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (popularOffset ==0) {
                                listPopularArenas.clear();
                            }

                            listPopularArenas.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listPopularArenas.size()>0){

                                        List<ArenaRecord> list = new ArrayList<>();

                                        for (int i=0;i<listPopularArenas.size();i++){
                                            if (!listPopularArenas.get(i).getArenaCategory().equalsIgnoreCase("1") || !listPopularArenas.get(i).getArenaCategory().equalsIgnoreCase("2")){
                                                list.add(listPopularArenas.get(i));
                                            }
                                        }
//
                                        listPopularArenas.addAll(list);

                                        rvPopularArticles.setAdapter(new PopularArticleAdapter(listPopularArenas));
                                        findViewById(R.id.tv_no_popular_article).setVisibility(View.GONE);
                                    }else {
                                        findViewById(R.id.tv_no_popular_article).setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)){ //TODO New Changes
                            String message = jsonObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, ArStoryArticles.this, message, sh_Pref);
                            });
                            return;
                        }else{
                            runOnUiThread(() -> {
                                findViewById(R.id.tv_no_popular_article).setVisibility(View.VISIBLE);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    runOnUiThread(() -> {
                        findViewById(R.id.tv_no_popular_article).setVisibility(View.VISIBLE);
                    });
                }

                runOnUiThread(() -> {
                    //get recent arenas service call
                    if (popularOffset==0){
                        getRecentArenas();
                    }
                });
            }
        });

    }

    class PopularArticleAdapter extends RecyclerView.Adapter<PopularArticleAdapter.ViewHolder> {

        List<ArenaRecord> listPopularArena;

        public PopularArticleAdapter(List<ArenaRecord> listPopularArena) {
            this.listPopularArena = listPopularArena;
        }

        @NonNull
        @Override
        public PopularArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PopularArticleAdapter.ViewHolder(LayoutInflater.from(ArStoryArticles.this).inflate(R.layout.item_arena,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull PopularArticleAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            if (listPopularArena.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(listPopularArena.get(position).getArenaName().split("~~")[0]);
                String[] title =  listPopularArena.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(ArStoryArticles.this).load(s).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(listPopularArena.get(position).getArenaName());
            }

            if (listPopularArena.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + listPopularArena.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + listPopularArena.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listPopularArena.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;

                    switch (listPopularArena.get(position).getArenaCategory()){
                        case "1":
                            intent = new Intent(ArStoryArticles.this, ArenaQuizDisplay.class);
                            intent.putExtra("quizObj",(Serializable) listPopularArena.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "2":
                            intent = new Intent(ArStoryArticles.this, FlashCardsDisplayNew.class);
                            intent.putExtra("flashObj",(Serializable) listPopularArena.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;

                        case "4":
                        case "5":
                        case "6":
                        case "7":
                            intent = new Intent(ArStoryArticles.this, ArenaDisplayActivity.class);
                            intent.putExtra("storyObj",(Serializable)listPopularArena.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;

                    }


                }
            });

            if (position == (listPopularArena.size()-1)){
                if (hasNextPopularPage){
                    utils.showLoader(ArStoryArticles.this);
                    popularOffset = popularOffset+25;
                    getPopularArenas();
                }
            }



        }

        @Override
        public int getItemCount() {
            return listPopularArena.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivDisplayImage;
            TextView tvName,tvPostedBy,tvDate;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_description);
                tvPostedBy = itemView.findViewById(R.id.tv_posted_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDisplayImage = itemView.findViewById(R.id.iv_display_image);


            }
        }
    }

    class RecentArticleAdapter extends RecyclerView.Adapter<RecentArticleAdapter.ViewHolder> {

        List<ArenaRecord> listRecentArena;

        public RecentArticleAdapter(List<ArenaRecord> listRecentArena) {
            this.listRecentArena = listRecentArena;
        }

        @NonNull
        @Override
        public RecentArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecentArticleAdapter.ViewHolder(LayoutInflater.from(ArStoryArticles.this).inflate(R.layout.item_arena_recent_article,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecentArticleAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            if (listRecentArena.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(listRecentArena.get(position).getArenaName().split("~~")[0]);
                String[] title =  listRecentArena.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(ArStoryArticles.this).load(s).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(listRecentArena.get(position).getArenaName());
            }

            if (listRecentArena.get(position).getArenaDesc().equalsIgnoreCase("NA")){
                holder.tvDesc.setText("");
            }else {
                holder.tvDesc.setText(listRecentArena.get(position).getArenaDesc());
            }

            if (listRecentArena.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + listRecentArena.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + listRecentArena.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listRecentArena.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //Uncomment for like in list
            /*
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                holder.tvLikes.setVisibility(View.GONE);
            }
            else {
                holder.tvLikes.setVisibility(View.VISIBLE);
                holder.tvLikes.setText(listRecentArena.get(position).getLikesCount());
                holder.tvLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listRecentArena.get(position).getUserId()==null || sObj.getStudentId().equalsIgnoreCase(listRecentArena.get(position).getUserId())){
                            Toast.makeText(ArStoryArticles.this,"You cant like your own article!",Toast.LENGTH_SHORT).show();
                        }else{
                            if (listRecentArena.get(position).getStudentLike().equalsIgnoreCase("0")){
                                likeArticle(listRecentArena.get(position).getArenaId()+"",holder,position);
                            }else {
                                Toast.makeText(ArStoryArticles.this,"You have already liked the article!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

            if (!(listRecentArena.get(position).getStudentLike() == null) && !listRecentArena.get(position).getStudentLike().equalsIgnoreCase("0")){
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
            }
            */


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = null;

                    switch (listRecentArena.get(position).getArenaCategory()){
                        case "1":
                            intent = new Intent(ArStoryArticles.this, ArenaQuizDisplay.class);
                            intent.putExtra("quizObj",(Serializable) listRecentArena.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "2":
                            intent = new Intent(ArStoryArticles.this, FlashCardsDisplayNew.class);
                            intent.putExtra("flashObj",(Serializable) listRecentArena.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;

                        case "4":
                        case "5":
                        case "6":
                        case "7":
                            intent = new Intent(ArStoryArticles.this, ArenaDisplayActivity.class);
                            intent.putExtra("storyObj",(Serializable)listRecentArena.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;

                    }
                }
            });

            utils.showLog("tag","position "+position);

//            if (position == (listRecentArena.size()-1)){
//
//            }

        }

        @Override
        public int getItemCount() {
            return listRecentArena.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivDisplayImage;
            TextView tvName,tvDesc,tvPostedBy,tvDate,tvLikes;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_title);
                tvDesc = itemView.findViewById(R.id.tv_desc);
                tvPostedBy = itemView.findViewById(R.id.tv_student_name);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvLikes = itemView.findViewById(R.id.tv_likes);
                ivDisplayImage = itemView.findViewById(R.id.iv_arena_img);

            }
        }
    }


    private void likeArticle(String arenaId, RecentArticleAdapter.ViewHolder holder, int pos) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("userId", sObj.getStudentId());
            jsonObject.put("like", "1");
            jsonObject.put("arenaId", arenaId);
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                jsonObject.put("userRole","T");
            }else {
                jsonObject.put("userRole","S");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + new AppUrls().LikeArena);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(new AppUrls().LikeArena)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ArStoryArticles.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.v(TAG,"like response "+resp);
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArStoryArticles.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    try {
                        JSONObject obj = new JSONObject(resp);

                        if (obj.getString("StatusCode").equalsIgnoreCase("200")){

                            //update like button if service successful
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int n = Integer.parseInt(holder.tvLikes.getText().toString());
                                    holder.tvLikes.setText((n+1)+"");
                                    holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
                                    listRecentArenas.get(pos).setLikesCount((n+1)+"");
                                    Toast.makeText(ArStoryArticles.this,"Liked Successfully.",Toast.LENGTH_SHORT).show();
                                    listRecentArenas.get(pos).setStudentLike(sObj.getStudentId());
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArStoryArticles.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}