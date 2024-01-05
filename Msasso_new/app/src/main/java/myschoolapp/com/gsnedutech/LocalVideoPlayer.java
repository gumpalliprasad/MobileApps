package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalVideoPlayer extends AppCompatActivity {

    @BindView(R.id.vid_view)
    VideoView vidView;

    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_player);

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
        path = getIntent().getStringExtra("path");

        vidView.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(vidView);
        vidView.setMediaController(mediaController);

        vidView.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}