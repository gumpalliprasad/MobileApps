package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentSub;
import myschoolapp.com.gsnedutech.Models.SubChapter;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CourseSubjectChapterListing extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = "SriRam -" + CourseSubjectChapterListing.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_subs)
    RecyclerView rvSubs;
    @BindView(R.id.rv_chapters)
    RecyclerView rvChapters;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_no_content)
    TextView tvNoContent;

    MyUtils utils = new MyUtils();

    int pos=0;
    int cardCounter = 0;


    List<StudentSub> subjectList = new ArrayList<>();
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String classId, courseId, subId, contentType;

    List<SubChapter> chaptertopicList = new ArrayList<>();

    int[] backgrounds = new DrawableUtils().getBgSub();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subject_chapter_listing);
        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        if (getIntent().hasExtra("type")){
            tvTitle.setText(getIntent().getStringExtra("subjectGroup"));
            pos = Integer.parseInt(getIntent().getStringExtra("pos"));
            subId = getIntent().getStringExtra("subjectId");
            classId = getIntent().getStringExtra("classId");
            courseId = getIntent().getStringExtra("courseId");
            contentType = getIntent().getStringExtra("contentType");
            subjectList.addAll((Collection<? extends StudentSub>) getIntent().getSerializableExtra("Subjects"));
            rvSubs.setLayoutManager(new LinearLayoutManager(CourseSubjectChapterListing.this,RecyclerView.HORIZONTAL,false));
            rvSubs.setAdapter(new SubjectsAdapter(subjectList));
            rvSubs.getLayoutManager().scrollToPosition(pos);
            rvChapters.setLayoutManager(new LinearLayoutManager(this));

        }
        else {
            pos = Integer.parseInt(getIntent().getStringExtra("position"));
            subjectList.addAll((Collection<? extends StudentSub>) getIntent().getSerializableExtra("Subjects"));
            subId = subjectList.get(pos).getSubjectId();
            contentType = subjectList.get(pos).getContentType();
            classId = getIntent().getStringExtra("classId");
            courseId = getIntent().getStringExtra("courseId");
            rvSubs.setLayoutManager(new LinearLayoutManager(CourseSubjectChapterListing.this,RecyclerView.HORIZONTAL,false));
            rvSubs.setAdapter(new SubjectsAdapter(subjectList));
            rvSubs.getLayoutManager().scrollToPosition(pos);
            rvChapters.setLayoutManager(new LinearLayoutManager(this));
            tvTitle.setText(subjectList.get(pos).getSubjectGroup());

        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, CourseSubjectChapterListing.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (getIntent().hasExtra("type")){
                    getDefaultChapterTopics();
                }
                else {
                    getChapterTopics();
                }
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder>{

        List<StudentSub> subjectList;

        public SubjectsAdapter(List<StudentSub> subjectList) {
            this.subjectList = subjectList;
        }

        @NonNull
        @Override
        public SubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(CourseSubjectChapterListing.this).inflate(R.layout.item_chapter_topic,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectsAdapter.ViewHolder holder, int position) {

           holder.tvSub.setText(subjectList.get(position).getSubjectName());

           if (position==pos){
               holder.tvSub.setBackgroundResource(R.drawable.bg_grad_tab_select);
               holder.tvSub.setTextColor(Color.WHITE);
           }else {
               holder.tvSub.setTextColor(Color.BLACK);
               holder.tvSub.setBackgroundResource(R.drawable.bg_border_10);
           }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rvSubs.getLayoutManager().scrollToPosition(position);
                    pos = position;
                    notifyDataSetChanged();
                    subId = subjectList.get(position).getSubjectId();
                    tvTitle.setText(subjectList.get(position).getSubjectGroup());
                   if (getIntent().hasExtra("type")){
                       getDefaultChapterTopics();
                   }else {
                       getChapterTopics();
                   }
                }
            });

        }

        @Override
        public int getItemCount() {
            return subjectList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSub;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSub = itemView.findViewById(R.id.tv_chp);
            }
        }
    }


    class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

        List<SubChapter> chapterList;

        public ChapterAdapter(List<SubChapter> chapterList) {
            this.chapterList = chapterList;
        }

        @NonNull
        @Override
        public ChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(CourseSubjectChapterListing.this).inflate(R.layout.item_chapters,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ChapterAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(true);

            int val = position%backgrounds.length;
            utils.showLog(TAG,"value"+val);
            holder.frameLayout.setBackgroundResource(backgrounds[val]);
            if(chapterList.get(position).getIsaccessible()){
                holder.frameLayout.setAlpha(0.5F);
            }else{
                holder.frameLayout.setAlpha(1F);
            }
            holder.progressIndicator.setVisibility(View.GONE);
            holder.tvChap.setText(chapterList.get(position).getChapterName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //disable option to display data when access is true
                    if(!chapterList.get(position).getIsaccessible()) {
                        if (chapterList.get(position).getChapterTopic().size() > 1) {
                            Intent intent = new Intent(CourseSubjectChapterListing.this, CourseChapterTopicListing.class);
                            intent.putExtra("chapters", (Serializable) chapterList);
                            intent.putExtra("courseId", courseId);
                            intent.putExtra("classId", classId);
                            intent.putExtra("position", position + "");
                            intent.putExtra("val", val + "");
                            intent.putExtra("subId", subId);
                            intent.putExtra("chapterId", "" + chapterList.get(position).getChapterId());
                            intent.putExtra(AppConst.ChapterCCMapId,"" + chapterList.get(position).getChapterCCMapId());
                            intent.putExtra("contentType", contentType);

                            if (getIntent().hasExtra("type")) {
                                intent.putExtra("type", "default");
                            }

                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            Intent intent = new Intent(CourseSubjectChapterListing.this, CourseTopicView.class);

                            if (getIntent().hasExtra("type")) {
                                intent.putExtra("type", "default");
                            }
                            intent.putExtra("courseId", courseId);
                            intent.putExtra("classId", classId);
                            intent.putExtra("subId", subId);
                            intent.putExtra("chapterId", chapterList.get(position).getChapterId());
                            intent.putExtra("topicId", chapterList.get(position).getChapterTopic().get(0).getTopicId());
                            intent.putExtra("topicName", chapterList.get(position).getChapterTopic().get(0).getTopicName());
                            intent.putExtra(AppConst.TopicCCMapId, chapterList.get(position).getChapterTopic().get(0).getTopicCCMapId());
                            intent.putExtra("contentType", contentType);
                            intent.putExtra(AppConst.ChapterCCMapId,"" + chapterList.get(position).getChapterCCMapId());
                            intent.putExtra("topics", (Serializable) chapterList.get(position).getChapterTopic());
                            intent.putExtra("position", 0 + "");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChap;
            FrameLayout frameLayout;
            ProgressIndicator progressIndicator;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                progressIndicator = itemView.findViewById(R.id.progressind);
                tvChap = itemView.findViewById(R.id.tv_chap);
                frameLayout = itemView.findViewById(R.id.fr_chap);
            }
        }
    }

    void getChapterTopics(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "")
                        +"&contentType=" + contentType+ "&subjectId=" + subId
                +"&sectionId="+getIntent().getStringExtra("sectionId")
                        +"&courseId="+courseId +"&classId="+classId)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url -"+new AppUrls().GetContentAccessBySection + "schemaName="
                + sh_Pref.getString("schema", "")+"&contentType=" + contentType+ "&subjectId=" + subId
                +"&sectionId="+getIntent().getStringExtra("sectionId")+"&courseId="+courseId +"&classId="+classId);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvChapters.setVisibility(View.GONE);
                        tvNoContent.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try{

                    ResponseBody responseBody = response.body();
                    if (response.body() != null) {
                        String resp = responseBody.string();

                        utils.showLog(TAG,"response- "+resp);

                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = parentjObject.getJSONArray("ContentOwner");


                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SubChapter>>() {
                            }.getType();

                            chaptertopicList.clear();
                            chaptertopicList.addAll(gson.fromJson(jsonArr.toString(), type));
                            List<SubChapter> updateChapterTopicList = getChapterDetails(chaptertopicList);
                            if (updateChapterTopicList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final LayoutAnimationController controller =
                                                AnimationUtils.loadLayoutAnimation(rvChapters.getContext(), R.anim.layout_animation_fall_down);
                                        rvChapters.setVisibility(View.VISIBLE);
                                        rvChapters.setAdapter(new ChapterAdapter(updateChapterTopicList));
                                        rvChapters.scheduleLayoutAnimation();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvChapters.setVisibility(View.GONE);
                                        tvNoContent.setVisibility(View.VISIBLE);

                                    }
                                });
                            }

                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, CourseSubjectChapterListing.this, message, sh_Pref);
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    rvChapters.setVisibility(View.GONE);
                                    tvNoContent.setVisibility(View.VISIBLE);

                                }
                            });
                        }


                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvChapters.setVisibility(View.GONE);
                                tvNoContent.setVisibility(View.VISIBLE);

                            }
                        });
                    }


                    }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    void getDefaultChapterTopics(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "")
                        +"&contentType=" + contentType+ "&subjectId=" + subId
                        +"&sectionId="+sObj.getClassCourseSectionId()
                        +"&courseId="+courseId +"&classId="+classId)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url -"+new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "")
                +"&contentType=" + contentType+ "&subjectId=" + subId
                +"&sectionId="+sObj.getClassCourseSectionId()
                +"&courseId="+courseId +"&classId="+classId);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvChapters.setVisibility(View.GONE);
                        tvNoContent.setVisibility(View.VISIBLE);

                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try{

                    ResponseBody responseBody = response.body();
                    if (response.body() != null) {
                        String resp = responseBody.string();

                        utils.showLog(TAG,"response- "+resp);

                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = parentjObject.getJSONArray("ContentOwner");


                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SubChapter>>() {
                            }.getType();

                            chaptertopicList.clear();
                            chaptertopicList.addAll(gson.fromJson(jsonArr.toString(), type));
                            List<SubChapter> updateChapterTopicList = getChapterDetails(chaptertopicList);
                            if (updateChapterTopicList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final LayoutAnimationController controller =
                                                AnimationUtils.loadLayoutAnimation(rvChapters.getContext(), R.anim.layout_animation_fall_down);
                                        rvChapters.setVisibility(View.VISIBLE);
                                        rvChapters.setAdapter(new ChapterAdapter(updateChapterTopicList));
                                        rvChapters.scheduleLayoutAnimation();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvChapters.setVisibility(View.GONE);
                                        tvNoContent.setVisibility(View.VISIBLE);

                                    }
                                });
                            }

                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, CourseSubjectChapterListing.this, message, sh_Pref);
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvChapters.setVisibility(View.GONE);
                                    tvNoContent.setVisibility(View.VISIBLE);

                                }
                            });
                        }


                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvChapters.setVisibility(View.GONE);
                                tvNoContent.setVisibility(View.VISIBLE);

                            }
                        });
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    //get access details to each chapter
    private List<SubChapter> getChapterDetails(List<SubChapter> chapterTopicList) {
        List<SubChapter> updateChapterTopicList = new ArrayList<>();
        for(int chapter = 0; chapter < chapterTopicList.size(); chapter++) {
            SubChapter mSubChapter = chapterTopicList.get(chapter);
            boolean access = false;
            for (int i = 0; i < mSubChapter.getChapterTopic().size(); i++) {
                if (utils.getAccessType(mSubChapter.getChapterTopic().get(i).getChaptertopicAccessId(),
                        sh_Pref.getString(AppConst.ACCESS, ""))) {
                    access = true;
                } else {
                    access = false;
                    break;
                }
            }
            SubChapter subChapter = new SubChapter();
            subChapter.setIsaccessible(access);
            subChapter.setChapterId(mSubChapter.getChapterId());
            subChapter.setChapterCCMapId(mSubChapter.getChapterCCMapId());
            subChapter.setChapterName(mSubChapter.getChapterName());
            subChapter.setChapterOwner(mSubChapter.getChapterOwner());
            subChapter.setChapterTopic(mSubChapter.getChapterTopic());
            updateChapterTopicList.add(subChapter);
        }

        return updateChapterTopicList;
    }
}