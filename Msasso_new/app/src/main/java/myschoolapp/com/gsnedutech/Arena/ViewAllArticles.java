package myschoolapp.com.gsnedutech.Arena;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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

public class ViewAllArticles extends AppCompatActivity {

    private static final String TAG = "SriRam -" + ViewAllArticles.class.getName();

    @BindView(R.id.rv_articles)
    RecyclerView rvArticles;

    List<ArenaRecord> listArticles = new ArrayList<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    TeacherObj tObj;

    int offset = 0;

    MyUtils utils = new MyUtils();

    int category = 0;

    boolean hasNextPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_articles);
        ButterKnife.bind(this);

        init();
    }

    void init(){

        if (getIntent().hasExtra("arenaCategory")){
            category = Integer.parseInt(getIntent().getStringExtra("arenaCategory"));
        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

        rvArticles.setLayoutManager(new LinearLayoutManager(this));
        rvArticles.setAdapter(new ArenaAdapter());
        rvArticles.addItemDecoration(new DividerItemDecoration(rvArticles.getContext(), DividerItemDecoration.VERTICAL));

        getArenas();
    }

    void getArenas(){
        if (offset==0){
            utils.showLoader(ViewAllArticles.this);
        }

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
//            postObject.put("arenaType", "General");
            if (category>0){
                if (category==1 || category==2){
                    postObject.put("arenaType", "Quiz");
                }
                postObject.put("arenaCategory",""+category);

            }
            postObject.put("arenaStatus","1");
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("myUserId",tObj.getUserId());
            }else {
                postObject.put("myUserId",sObj.getStudentId());
                postObject.put("sectionId", sObj.getClassCourseSectionId());
            }
            postObject.put("itemCount","25");
            postObject.put("offset",offset +"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"url "+AppUrls.GetArenas);
        Log.v(TAG,"url obj "+postObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetArenas)
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
                        rvArticles.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body()!=null){
                    String resp = response.body().string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = parentjObject.getJSONArray("arenaRecords");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecord>>() {
                            }.getType();

                            if (jsonArray.length()>0) {
                                if (offset==0){
                                    listArticles.clear();
                                }
                                listArticles.addAll(gson.fromJson(jsonArray.toString(), type));

                                if (listArticles.size()%25==0){
                                    hasNextPage = true;
                                }else {
                                    hasNextPage = false;
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listArticles.size()>0){
                                            rvArticles.getAdapter().notifyDataSetChanged();
                                        }else{
                                            rvArticles.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                String message = parentjObject.getString(AppConst.MESSAGE);
                                runOnUiThread(() -> {
                                    MyUtils.forceLogoutUser(toEdit, ViewAllArticles.this, message, sh_Pref);
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listArticles.size()==0) {
                                            rvArticles.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    class ArenaAdapter extends RecyclerView.Adapter<ArenaAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ViewAllArticles.this).inflate(R.layout.item_arena_with_likes,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            if (listArticles.get(position).getArenaName().contains("~~")){
                holder.tvName.setText(listArticles.get(position).getArenaName().split("~~")[0]);
                String[] title =  listArticles.get(position).getArenaName().split("~~");
                for (String s : title){
                    if (s.contains("http")){
                        Picasso.with(ViewAllArticles.this).load(s).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDisplayImage);
                    }
                }
            }else{
                holder.tvName.setText(listArticles.get(position).getArenaName());
            }

            if (listArticles.get(position).getUserRole().equalsIgnoreCase("S")) {
                holder.tvPostedBy.setText("By " + listArticles.get(position).getStudentName());
            }else {
                holder.tvPostedBy.setText("By " + listArticles.get(position).getUserName());
            }

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listArticles.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (position == (listArticles.size()-1)){
                utils.showLoader(ViewAllArticles.this);
                if (hasNextPage){
                    offset = offset+25;
                    getArenas();
                }else {
                    utils.dismissDialog();
                }
            }

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                holder.tvLikes.setVisibility(View.GONE);
            }else {

                holder.tvLikes.setText(listArticles.get(position).getLikesCount());
                holder.tvLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listArticles.get(position).getUserId()==null || sObj.getStudentId().equalsIgnoreCase(listArticles.get(position).getUserId())){
                            Toast.makeText(ViewAllArticles.this,"You cant like your own article!",Toast.LENGTH_SHORT).show();
                        }else{
                            if (listArticles.get(position).getStudentLike().equalsIgnoreCase("0")){
                                likeArticle(listArticles.get(position).getArenaId()+"",holder,position);
                            }else {
                                Toast.makeText(ViewAllArticles.this,"You have already liked the article!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

            if (!(listArticles.get(position).getStudentLike() == null) && !listArticles.get(position).getStudentLike().equalsIgnoreCase("0")){
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;

                    switch (listArticles.get(position).getArenaCategory()){
                        case "1":

                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                intent = new Intent(ViewAllArticles.this, TeacherArenaQuizReview.class);
                                intent.putExtra("item", (Serializable) listArticles.get(position));
                                intent.putExtra("status", listArticles.get(position).getArenaStatus());
                            }else {
                                intent = new Intent(ViewAllArticles.this, ArenaQuizDisplay.class);
                                intent.putExtra("quizObj",(Serializable) listArticles.get(position));
                            }
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "2":
                            intent = new Intent(ViewAllArticles.this, FlashCardsDisplayNew.class);
                            intent.putExtra("flashObj",(Serializable) listArticles.get(position));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;

                        case "4":
                            intent = new Intent(ViewAllArticles.this, ArenaAudioDisplay.class);
                            intent.putExtra("audioObj",(Serializable)listArticles.get(position));
                            ViewAllArticles.this.startActivity(intent);
                            ViewAllArticles.this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "5":
                            intent = new Intent(ViewAllArticles.this, ArenaVideoDisplay.class);
                            intent.putExtra("storyObj",(Serializable)listArticles.get(position));
                            ViewAllArticles.this.startActivity(intent);
                            ViewAllArticles.this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "6":
                            intent = new Intent(ViewAllArticles.this, ArenaDisplayActivity.class);
                            intent.putExtra("videoObj",(Serializable)listArticles.get(position));
                            ViewAllArticles.this.startActivity(intent);
                            ViewAllArticles.this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "7":
                            intent = new Intent(ViewAllArticles.this, ArPollDisplayActivity.class);
                            intent.putExtra("poll",(Serializable)listArticles.get(position));
                            ViewAllArticles.this.startActivity(intent);
                            ViewAllArticles.this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listArticles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivDisplayImage;
            TextView tvName,tvPostedBy,tvDate,tvLikes;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_description);
                tvPostedBy = itemView.findViewById(R.id.tv_posted_by);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDisplayImage = itemView.findViewById(R.id.iv_display_image);
                tvLikes = itemView.findViewById(R.id.tv_likes);


            }
        }
    }

    private void likeArticle(String arenaId, ArenaAdapter.ViewHolder holder, int pos) {

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
                        Toast.makeText(ViewAllArticles.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ViewAllArticles.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                                    listArticles.get(pos).setLikesCount((n+1)+"");
                                    Toast.makeText(ViewAllArticles.this,"Liked Successfully.",Toast.LENGTH_SHORT).show();
                                    listArticles.get(pos).setStudentLike(sObj.getStudentId());
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ViewAllArticles.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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