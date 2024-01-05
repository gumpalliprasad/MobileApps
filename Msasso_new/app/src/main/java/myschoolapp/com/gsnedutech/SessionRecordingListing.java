package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.VideoSessionObj;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.WebViewActivity;

public class SessionRecordingListing extends AppCompatActivity {

    private static final String TAG = "SriRam -" + SessionRecordingListing.class.getName();

    @BindView(R.id.rv_session)
    RecyclerView rvSessions;
    MyUtils utils = new MyUtils();

    List<VideoSessionObj> videoSessionrecordingObjList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_recording_listing);
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

        ((TextView)findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("name"));

        videoSessionrecordingObjList.addAll((Collection<? extends VideoSessionObj>) getIntent().getSerializableExtra("videos"));

        rvSessions.setLayoutManager(new GridLayoutManager(SessionRecordingListing.this,2));
        rvSessions.setAdapter(new SessionAdapter(videoSessionrecordingObjList));


    }

    class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder>{

        List<VideoSessionObj> videoSessionrecordingList;

        public SessionAdapter(List<VideoSessionObj> videoSessionrecordingList) {
            this.videoSessionrecordingList = videoSessionrecordingList;
        }

        @NonNull
        @Override
        public SessionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SessionAdapter.ViewHolder(LayoutInflater.from(SessionRecordingListing.this).inflate(R.layout.item_session_listing,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SessionAdapter.ViewHolder holder, int position) {

            holder.tvVidName.setText(videoSessionrecordingList.get(position).getChapterName());
            holder.tvSubName.setText(videoSessionrecordingList.get(position).getSubjectName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(SessionRecordingListing.this, WebViewActivity.class);
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