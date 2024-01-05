package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.VideoSessionObj;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.WebViewActivity;

public class SessionRecordingSubjectWise extends AppCompatActivity {

    private static final String TAG = "SriRam -" + SessionRecordingSubjectWise.class.getName();

    @BindView(R.id.rv_subs)
    RecyclerView rvSubs;
    MyUtils utils = new MyUtils();

    HashMap<String,List<VideoSessionObj>> mapVideos = new HashMap<>();

    List<VideoSessionObj> videoSessionrecordingObjList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_recording_subject_wise);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        videoSessionrecordingObjList.addAll((Collection<? extends VideoSessionObj>) getIntent().getSerializableExtra("videos"));


        List<String> keys = new ArrayList<>();

        for (int i=0;i<videoSessionrecordingObjList.size();i++){
            if (!keys.contains(videoSessionrecordingObjList.get(i).getSubjectName())){
                keys.add(videoSessionrecordingObjList.get(i).getSubjectName());
            }
        }
        
        for (int x=0;x<keys.size();x++){

            List<VideoSessionObj> list = new ArrayList<>();
            
            for (int i=0;i<videoSessionrecordingObjList.size();i++){
                if (videoSessionrecordingObjList.get(i).getSubjectName().equalsIgnoreCase(keys.get(x))){
                    list.add(videoSessionrecordingObjList.get(i));
                }
            }
            mapVideos.put(keys.get(x),list);
        }
        
        rvSubs.setLayoutManager(new LinearLayoutManager(this));
        rvSubs.setAdapter(new SubjectAdapter(keys));

    }
    
    
    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{

        List<String> keys;
        
        public SubjectAdapter(List<String> keys) {
            this.keys = keys;
        }

        @NonNull
        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(SessionRecordingSubjectWise.this).inflate(R.layout.item_recording_subjects,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {

            holder.tvSubName.setText(keys.get(position));
            holder.rvVids.setLayoutManager(new LinearLayoutManager(SessionRecordingSubjectWise.this,RecyclerView.HORIZONTAL,false));
            holder.rvVids.setAdapter(new SessionAdapter(mapVideos.get(keys.get(position))));
            holder.tvViewAll.setVisibility(View.VISIBLE);
            holder.tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SessionRecordingSubjectWise.this, SessionRecordingListing.class);
                    intent.putExtra("videos",(Serializable)mapVideos.get(keys.get(position)));
                    intent.putExtra("name",keys.get(position));
                    SessionRecordingSubjectWise.this.startActivity(intent);
                    SessionRecordingSubjectWise.this.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return keys.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            
            TextView tvSubName,tvViewAll;
            RecyclerView rvVids;
            
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvViewAll = itemView.findViewById(R.id.tv_view_all);
                rvVids = itemView.findViewById(R.id.rv_vids);
            }
        }
    }

    class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder>{

        List<VideoSessionObj> videoSessionrecordingList;

        public SessionAdapter(List<VideoSessionObj> videoSessionrecordingList) {
            this.videoSessionrecordingList = videoSessionrecordingList;
        }

        @NonNull
        @Override
        public SessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SessionAdapter.ViewHolder(LayoutInflater.from(SessionRecordingSubjectWise.this).inflate(R.layout.item_recorded_session,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SessionAdapter.ViewHolder holder, int position) {

            holder.tvVidName.setText(videoSessionrecordingList.get(position).getChapterName());
            holder.tvSubName.setText(videoSessionrecordingList.get(position).getSubjectName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(SessionRecordingSubjectWise.this, WebViewActivity.class);
                        intent.putExtra("videoItem", videoSessionrecordingList.get(position).getVideoLink());
                        startActivity(intent);

                    } catch (Exception e) {
                        utils.showLog(TAG, e.getMessage());

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return videoSessionrecordingList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvVidName,tvSubName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVidName = itemView.findViewById(R.id.tv_vid_name);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}