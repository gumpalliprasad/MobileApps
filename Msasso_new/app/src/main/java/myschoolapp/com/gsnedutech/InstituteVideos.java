package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.TeacherTopicVideo;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;

public class InstituteVideos extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + InstituteVideos.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;

    MyUtils utils = new MyUtils();
    DrawableUtils drawableUtils = new DrawableUtils();

    int x;

    List<TeacherTopicVideo> listVideos = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_videos);

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
            utils.alertDialog(1, InstituteVideos.this, getString(R.string.error_connect), getString(R.string.error_internet),
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

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        x = getRandom(1,21);

        utils.showLog(TAG,"pos "+x);

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(rvVideos.getContext(), R.anim.layout_animation_fall_down);
        rvVideos.setLayoutManager(new LinearLayoutManager(InstituteVideos.this));
        listVideos.addAll((Collection<? extends TeacherTopicVideo>) getIntent().getSerializableExtra("videos"));
        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(new VideoAdapter(listVideos));
        rvVideos.scheduleLayoutAnimation();
    }


    private int getRandom(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

        List<TeacherTopicVideo> videoList;

        public VideoAdapter(List<TeacherTopicVideo> videoList) {
            this.videoList = videoList;
        }

        @NonNull
        @Override
        public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(InstituteVideos.this).inflate(R.layout.item_video,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.tvVidName.setText(videoList.get(position).getVideoName());

            holder.llBg.setBackgroundResource(drawableUtils.darkPalet[x]);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(InstituteVideos.this, TopicVideoActivity.class);
                    i.putExtra("position",position+"");
                    i.putExtra("videos",(Serializable)videoList);

                    if (videoList.get(position).getVideoLink().contains("vimeo")) {
                        i.putExtra("type","vimeo");
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    } else if(videoList.get(position).getVideoLink().contains("youtube")){

                        i.putExtra("type","youtube");
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }else{
                        i.putExtra("type","player");
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvVidName;
            ProgressIndicator prog;
            LinearLayout llBg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVidName = itemView.findViewById(R.id.tv_vid_name);
                prog = itemView.findViewById(R.id.prog);
                llBg = itemView.findViewById(R.id.ll_bg);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}