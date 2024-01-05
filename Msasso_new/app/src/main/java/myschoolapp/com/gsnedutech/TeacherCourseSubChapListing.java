package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
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
import org.json.JSONException;
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
import myschoolapp.com.gsnedutech.Models.AdminCourseContentOwner;
import myschoolapp.com.gsnedutech.Models.Chaptertopic;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
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

public class TeacherCourseSubChapListing extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherCourseSubChapListing.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_subs)
    RecyclerView rvSubs;
    @BindView(R.id.rv_chapters)
    RecyclerView rvChapters;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    MyUtils utils = new MyUtils();

    List<TeacherCCSSSubject> subjectList = new ArrayList<>();
    List<Chaptertopic> chaptertopicList = new ArrayList<>();

    int pos=0;
    String classId, courseId, subId, contentType;
    int cardCounter = 0;

    int[] backgrounds = new DrawableUtils().getLightPalet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course_sub_chap_listing);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
            utils.alertDialog(1, TeacherCourseSubChapListing.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getContentType();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    void init(){
        pos = Integer.parseInt(getIntent().getStringExtra("position"));
        subId = getIntent().getStringExtra("subjectId");
        classId = getIntent().getStringExtra("classId");
        courseId = getIntent().getStringExtra("courseId");
        contentType = getIntent().getStringExtra("contentType");
        tvTitle.setText(getIntent().getStringExtra("className"));
        subjectList.addAll((Collection<? extends TeacherCCSSSubject>) getIntent().getSerializableExtra("Subjects"));

        if (subjectList.size()>1){
            rvSubs.setVisibility(View.VISIBLE);
            rvSubs.setLayoutManager(new LinearLayoutManager(TeacherCourseSubChapListing.this,RecyclerView.HORIZONTAL,false));
            rvSubs.setAdapter(new SubjectsAdapter(subjectList));
        }else{
            rvSubs.setVisibility(View.GONE);
        }
    }

    class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder>{

        List<TeacherCCSSSubject> subjectList;

        public SubjectsAdapter(List<TeacherCCSSSubject> subjectList) {
            this.subjectList = subjectList;
        }

        @NonNull
        @Override
        public SubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubjectsAdapter.ViewHolder(LayoutInflater.from(TeacherCourseSubChapListing.this).inflate(R.layout.item_chapter_topic,parent,false));
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

                    getChapters();

//                    tvTitle.setText(subjectList.get(position).getSubjectGroup());
//                    if (getIntent().hasExtra("type")){
//                        getDefaultChapterTopics();
//                    }else {
//                        getChapterTopics();
//                    }
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

    void getContentType(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG,"url"+new AppUrls().GetselectContentOwner+"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));

        Request request = new Request.Builder()
                .url(new AppUrls().GetselectContentOwner+"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""))
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("ContentOwner");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminCourseContentOwner>>() {
                            }.getType();


                            List<AdminCourseContentOwner> adminCourseContentOwnerList = new ArrayList<>();
                            adminCourseContentOwnerList.clear();
                            adminCourseContentOwnerList.addAll(gson.fromJson(jsonArr.toString(), type));

                            Log.v(TAG, "adminCourseContentOwner -" + adminCourseContentOwnerList.size());
                            Log.v(TAG, "courseid -" + courseId);

                            List<AdminCourseContentOwner> filteredList = new ArrayList<>();

                            for (int i = 0; i < adminCourseContentOwnerList.size(); i++) {
                                if (adminCourseContentOwnerList.get(i).getCourseId().equalsIgnoreCase("" + courseId))
//                                filteredList.add(adminCourseContentOwnerList.get(i));
                                    contentType = adminCourseContentOwnerList.get(i).getContentType();
                            }

                            getChapters();
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


    void getChapters(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.showLoader(TeacherCourseSubChapListing.this);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG,"url "+new AppUrls().GetChapterTopicsBySubId +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")
                + "&classId=" + classId + "&subjectId=" + subId + "&contentOwner=COLLEGE,CEDZ&contentType=" + contentType);

        Request request = new Request.Builder()
                .url(new AppUrls().GetChapterTopicsBySubId +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","") + "&classId=" + classId + "&subjectId=" + subId + "&contentOwner=COLLEGE,CEDZ&contentType=" + contentType)
                .build();
        client.newCall(request).enqueue(new Callback() {
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
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("Chaptertopics");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Chaptertopic>>() {
                            }.getType();

                            chaptertopicList.clear();
                            chaptertopicList.addAll(gson.fromJson(jsonArr.toString(), type));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final LayoutAnimationController controller =
                                            AnimationUtils.loadLayoutAnimation(rvChapters.getContext(), R.anim.layout_animation_fall_down);
                                    rvChapters.setLayoutManager(new LinearLayoutManager(TeacherCourseSubChapListing.this));
                                    rvChapters.setAdapter(new ChapterAdapter(chaptertopicList));
                                    rvChapters.scheduleLayoutAnimation();
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


    class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

        List<Chaptertopic> chapterList;

        public ChapterAdapter(List<Chaptertopic> chapterList) {
            this.chapterList = chapterList;
        }

        @NonNull
        @Override
        public ChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChapterAdapter.ViewHolder(LayoutInflater.from(TeacherCourseSubChapListing.this).inflate(R.layout.item_chapters,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ChapterAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(true);
            holder.progressIndicator.setVisibility(View.GONE);
            int val = position%backgrounds.length;
            utils.showLog(TAG,"value"+val);
            holder.frameLayout.setBackgroundResource(backgrounds[val]);
            holder.tvChap.setText(chapterList.get(position).getChapterName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(chapterList.get(position).getTopics().size()>1) {
                        Intent intent = new Intent(TeacherCourseSubChapListing.this, TeacherCourseChapTopicListing.class);
                        intent.putExtra("chapters", (Serializable) chapterList);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("classId", classId);
                        intent.putExtra("position", position+"");
                        intent.putExtra("val", val+"");
                        intent.putExtra("subId", subId);
                        intent.putExtra(AppConst.ChapterCCMapId, chapterList.get(position).getChapterCCMapId());
                        intent.putExtra("chapterId", ""+chapterList.get(position).getChapterId());
                        intent.putExtra("contentType", contentType);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                    else {
                        Intent intent = new Intent(TeacherCourseSubChapListing.this, TeacherCourseTopicView.class);

                        intent.putExtra("courseId", courseId);
                        intent.putExtra("classId", classId);
                        intent.putExtra("subId", subId);
                        intent.putExtra("chapterId", ""+chapterList.get(position).getChapterId());
                        intent.putExtra("topicId", chapterList.get(position).getTopics().get(0).getTopicId());
                        intent.putExtra(AppConst.ChapterCCMapId, chapterList.get(position).getChapterCCMapId());
                        intent.putExtra(AppConst.TopicCCMapId, chapterList.get(position).getTopics().get(0).getTopicCCMapId());
                        intent.putExtra("topicName", chapterList.get(position).getTopics().get(0).getTopicName());
                        intent.putExtra("contentType", contentType);
                        intent.putExtra("topics", (Serializable) chapterList.get(position).getTopics());
                        intent.putExtra("position", 0 + "");
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChap;
            ProgressIndicator progressIndicator;
            FrameLayout frameLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChap = itemView.findViewById(R.id.tv_chap);
                progressIndicator = itemView.findViewById(R.id.progressind);
                frameLayout = itemView.findViewById(R.id.fr_chap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}