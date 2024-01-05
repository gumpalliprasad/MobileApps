package myschoolapp.com.gsnedutech.Util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;

public class YoutubeActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    // YouTube player view
    private YouTubePlayerView youTubeView;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        ButterKnife.bind(this);

        tvTitle.setText(getIntent().getStringExtra("name"));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        youTubeView = findViewById(R.id.youtube_view);

        // Initializing video player with developer key
        youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, this);

//        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
//            String errorMessage = String.format(
//                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, "errorMessage", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            Log.v("TAG","link "+getIntent().getStringExtra("videoItem"));

            String videoLink = getIntent().getStringExtra("videoItem");
            String[] s;
            if (videoLink.contains("watch?v")) {
                s = videoLink.split("=");
            } else {
                s = videoLink.split("/");
            }
            videoLink = s[s.length - 1];
            player.loadVideo(videoLink);
            // Hiding player controls
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}