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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.SubChapter;
import myschoolapp.com.gsnedutech.Models.SubChapterTopic;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;

public class CourseChapterTopicListing extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_chaps)
    RecyclerView rvChaps;
    @BindView(R.id.rv_topics)
    RecyclerView rvTopics;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    int selectedItem = 0;

    MyUtils utils = new MyUtils();
    int val;

    List<SubChapter> chaptertopicList = new ArrayList<>();
    List<SubChapterTopic> listTopic = new ArrayList<>();

    int[] backgrounds = new DrawableUtils().getBgSub();
    SharedPreferences sh_Pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_chapter_topic_listing);
        ButterKnife.bind(this);

        init();
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
            utils.alertDialog(1, CourseChapterTopicListing.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chaptertopicList.addAll((Collection<? extends SubChapter>) getIntent().getSerializableExtra("chapters"));
        selectedItem = Integer.parseInt(getIntent().getStringExtra("position"));
         val = Integer.parseInt(getIntent().getStringExtra("val"));

        rvChaps.setLayoutManager(new LinearLayoutManager(CourseChapterTopicListing.this,RecyclerView.HORIZONTAL,false));
        rvChaps.setAdapter(new ChapAdapter(chaptertopicList));
        rvChaps.getLayoutManager().scrollToPosition(selectedItem);

        listTopic = chaptertopicList.get(selectedItem).getChapterTopic();
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        rvTopics.setAdapter(new TopicAdapter(listTopic));

        tvTitle.setText(chaptertopicList.get(selectedItem).getChapterName());
    }


    class ChapAdapter extends RecyclerView.Adapter<ChapAdapter.ViewHolder>{

        List<SubChapter> chapterList;

        public ChapAdapter(List<SubChapter> chapterList) {
            this.chapterList = chapterList;
        }

        @NonNull
        @Override
        public ChapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(CourseChapterTopicListing.this).inflate(R.layout.item_chapter_topic,parent,false));
        }


        @Override
        public long getItemId(int position) {
            return  position;
        }

        @Override
        public void onBindViewHolder(@NonNull ChapAdapter.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            holder.tvChp.setText(chapterList.get(position).getChapterName());

            if (position==selectedItem){
                holder.tvChp.setBackgroundResource(R.drawable.bg_grad_chap_select);
                holder.tvChp.setTextColor(Color.WHITE);
            }else {
                holder.tvChp.setBackgroundResource(R.drawable.bg_border_10);
                holder.tvChp.setTextColor(Color.BLACK);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final LayoutAnimationController controller =
                            AnimationUtils.loadLayoutAnimation(rvTopics.getContext(), R.anim.layout_animation_fall_down);
                    tvTitle.setText(chaptertopicList.get(position).getChapterName());
                    selectedItem = position;
                    notifyDataSetChanged();
                    rvChaps.getLayoutManager().scrollToPosition(position);
                    listTopic = chaptertopicList.get(selectedItem).getChapterTopic();
                    rvTopics.setLayoutManager(new LinearLayoutManager(CourseChapterTopicListing.this));
                    rvTopics.setAdapter(new TopicAdapter(listTopic));
                    rvTopics.scheduleLayoutAnimation();

                }
            });

        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChp;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChp = itemView.findViewById(R.id.tv_chp);
            }
        }
    }

    class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{

        List<SubChapterTopic> topicList;

        public TopicAdapter(List<SubChapterTopic> topicList) {
            this.topicList = topicList;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(CourseChapterTopicListing.this).inflate(R.layout.item_topic,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            holder.tvTopicName.setText(topicList.get(position).getTopicName());
            holder.llTopic.setBackgroundResource(backgrounds[val]);
            if (utils.getAccessType(listTopic.get(position).getChaptertopicAccessId(), sh_Pref.getString(AppConst.ACCESS, ""))) {
                holder.llTopic.setAlpha(0.5F);
            } else {
                holder.llTopic.setAlpha(1F);
            }
            float stepSize = (float) ((1-0.6)/topicList.size());

//            holder.llTopic.setAlpha((float) (0.6+(position*stepSize)));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!utils.getAccessType(listTopic.get(position).getChaptertopicAccessId(), sh_Pref.getString(AppConst.ACCESS, ""))) {
                        Intent intent = new Intent(CourseChapterTopicListing.this, CourseTopicView.class);

                        if (getIntent().hasExtra("type")) {
                            intent.putExtra("type", "default");
                        }
                        intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                        intent.putExtra("classId", getIntent().getStringExtra("classId"));
                        intent.putExtra("subId", getIntent().getStringExtra("subId"));
                        intent.putExtra("chapterId", chaptertopicList.get(selectedItem).getChapterId());
                        intent.putExtra(AppConst.ChapterCCMapId,"" + chaptertopicList.get(selectedItem).getChapterCCMapId());
                        intent.putExtra("topicId", topicList.get(position).getTopicId());
                        intent.putExtra(AppConst.TopicCCMapId, topicList.get(position).getTopicCCMapId());
                        intent.putExtra("topicName", topicList.get(position).getTopicName());
                        intent.putExtra("contentType", getIntent().getStringExtra("contentType"));
                        intent.putExtra("topics", (Serializable) topicList);
                        intent.putExtra("position", position + "");
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return topicList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTeacherName,tvTopicName;
            FrameLayout llTopic;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTeacherName = itemView.findViewById(R.id.tv_topic_teacher);
                tvTopicName = itemView.findViewById(R.id.tv_topic_name);
                llTopic = itemView.findViewById(R.id.ll_topic);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}