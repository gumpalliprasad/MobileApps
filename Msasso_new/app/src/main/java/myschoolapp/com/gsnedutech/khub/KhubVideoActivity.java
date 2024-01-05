package myschoolapp.com.gsnedutech.khub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.spherical.SphericalGLSurfaceView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherTopicVideo;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class KhubVideoActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, PlayerControlView.VisibilityListener, PlaybackPreparer, NetworkConnectivity.ConnectivityReceiverListener, YouTubePlayer.PlaybackEventListener {

    private static final String TAG = KhubVideoActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    SharedPreferences sh_Pref;
    StudentObj sObj;
    int start = -1;
    int end = -1;

    final Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //For every 1 second, check the current time and endTime
            if (yPlayer.getCurrentTimeMillis() <= end) {
                handler.postDelayed(this, 1000);
            } else {
                handler.removeCallbacks(this); //no longer required
                yPlayer.pause(); //and Pause the video
            }
        }
    };


    MyUtils utils = new MyUtils();

    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private PlayerView playerView;

    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    ImageView fullscreenButton;
    boolean fullscreen = false;
    long percentage;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_vid_name)
    TextView tvVidName;
    @BindView(R.id.iv_heart)
    ImageView ivHeart;
    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;
    
    String videoLink="";
    int selectedPosition=0;

    YouTubePlayer yPlayer;

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    // YouTube player view
    private YouTubePlayerView youTubeView;

    List<TeacherTopicVideo> listVideos = new ArrayList<>();

    String contentType = "", moduleContentId = "", moduleId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_video);
        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init(savedInstanceState);
    }



    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubVideoActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }

    void getContent() {

        utils.showLoader(this);

        ApiClient client = new ApiClient();
        String URL = AppUrls.KHUB_BASE_URL+AppUrls.Content+"contentType="+contentType+"&moduleContentId="+moduleContentId+"&moduleId="+moduleId+"&studentId="+sObj.getStudentId()+"&schemaName="+sh_Pref.getString("schema","");

        Request get = client.getRequest(URL, sh_Pref);
        utils.showLog(TAG, "URL - " + URL);
        client.getClient().newCall(get).enqueue(new Callback() {
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

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("result");

                            listVideos.clear();
                            for (int i=0;i<jsonArray.length();i++){
                                TeacherTopicVideo vid = new TeacherTopicVideo();
                                vid.setVideoName(jsonArray.getJSONObject(i).getString("title"));
                                vid.setVideoLink(jsonArray.getJSONObject(i).getString("value"));
                                listVideos.add(vid);
                            }

//                            TeacherTopicVideo vid = new TeacherTopicVideo();
//                            vid.setVideoName("title");
//                            vid.setVideoLink("https://www.youtube.com/embed/JMUxmLyrhSk?start=200");
//                            TeacherTopicVideo vid1 = new TeacherTopicVideo();
//                            vid1.setVideoName("title1");
//                            vid1.setVideoLink("https://www.youtube.com/embed/JMUxmLyrhSk?end=400");
//                            TeacherTopicVideo vid2 = new TeacherTopicVideo();
//                            vid2.setVideoName("vimeo");
//                            vid2.setVideoLink("https://vimeo.com/253989945");
//                            TeacherTopicVideo vid3 = new TeacherTopicVideo();
//                            vid3.setVideoName("default youtube");
//                            vid3.setVideoLink("https://www.youtube.com/embed/JMUxmLyrhSk?autoplay=0");
//                            TeacherTopicVideo vid4 = new TeacherTopicVideo();
//                            vid4.setVideoName("strange");
//                            vid4.setVideoLink("https://www.mobiles24.co/metapreview.php?id=592718&cat=106&h=12743437");
//                            listVideos.add(vid);
//                            listVideos.add(vid1);
//                            listVideos.add(vid2);
//                            listVideos.add(vid3);
//                            listVideos.add(0,vid4);

                            utils.showLog(TAG, "Blank Text ArrayList - " + listVideos.size());
                            if (listVideos.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listVideos.size() > 0) {
                                            selectedPosition = 0;
                                            setPlayList();
                                        }
                                    }
                                });
                            }
                            else {
                                runOnUiThread(() -> new AlertDialog.Builder(KhubVideoActivity.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage("No Data To Load")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show());
                            }
//


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



    void init(Bundle savedInstanceState){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        youTubeView = findViewById(R.id.youtube_view);

        moduleId = getIntent().getStringExtra("moduleId");
        moduleContentId = getIntent().getStringExtra("moduleContentId");
        contentType = getIntent().getStringExtra("contentType");

        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        String sphericalStereoMode = null;

        playerView = findViewById(R.id.exoplayer);
        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        if (sphericalStereoMode != null) {
            ((SphericalGLSurfaceView) playerView.getVideoSurfaceView()).setDefaultStereoMode(0);
        }

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            clearStartPosition();
        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KhubVideoActivity.this, PlayerActivity.class);
                intent.putExtra("url", videoLink);
                intent.putExtra("from","topic");
                intent.putExtra("percentage", player.getCurrentPosition());
                startActivityForResult(intent, 1234);
            }
        });

        getContent();


    }

    void setPlayList(){

        if (listVideos.get(selectedPosition).getVideoName()!= null && listVideos.get(selectedPosition).getVideoName().length()>0){
            tvTitle.setText(listVideos.get(selectedPosition).getVideoName());
            tvVidName.setText(listVideos.get(selectedPosition).getVideoName());
        }else{
            tvTitle.setText(getIntent().getStringExtra("title"));
            tvVidName.setText(getIntent().getStringExtra("title"));
        }

//        rvVideos.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvVideos.setLayoutManager(new GridLayoutManager(this, 2));
        rvVideos.setAdapter(new VideoAdapter(listVideos));
        rvVideos.getLayoutManager().scrollToPosition(selectedPosition);

        if (listVideos.get(selectedPosition).getVideoLink().contains("vimeo")) {
            youTubeView.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            loadWebsite(listVideos.get(selectedPosition).getVideoLink());
        } else if (listVideos.get(selectedPosition).getVideoLink().contains("youtube")) {//testing link - https://www.youtube.com/watch?v=f_MBC0ON_JM

            youTubeView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);

//            String ss = "https://www.youtube.com/embed/JMUxmLyrhSk?start=10000&end=10015";
            videoLink = getCodeForYoutube(listVideos.get(selectedPosition).getVideoLink(), selectedPosition);

            youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, this);
        } else if (listVideos.get(selectedPosition).getVideoLink().contains("player")) {
            youTubeView.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            playerView.setVisibility(View.VISIBLE);
            //testing link - http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4

            videoLink = listVideos.get(selectedPosition).getVideoLink();

            initializePlayer(videoLink);
        }
    }

    private void loadWebsite(String url) {
        videoLink = url;

        webView.setWebViewClient(new Browser_home());
        WebSettings webSettings = webView.getSettings();
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(false);
        webView.setWebChromeClient(new MyChrome());


        ConnectivityManager cm = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            webView.loadUrl(url);
        } else {
            webView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {

            yPlayer = youTubePlayer;

            loadYoutubeVideo();
        }
    }

    private void loadYoutubeVideo() {
        // loadVideo() will auto play video
        // Use cueVideo() method, if you don't want to play it automatically
        if(yPlayer!=null) {
            if (start != -1)
                yPlayer.loadVideo(videoLink, start);
            else yPlayer.loadVideo(videoLink);
            // Hiding player controls
            yPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            yPlayer.setPlaybackEventListener(this);
            yPlayer.play();
            if (end != -1) {
                handler.postDelayed(runnable, 1000);
            }
        }
        else {
            youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, this);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
//            String errorMessage = String.format(
//                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, "errorMessage", Toast.LENGTH_LONG).show();
        }
    }

    public DataSource.Factory buildDataSourceFactory() {
        return new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getResources().getString(R.string.app_name)));
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    @Override
    public void preparePlayback() {

    }

    @Override
    public void onPlaying() {
        if (start!=-1 && end!=-1) {
            if (yPlayer.getCurrentTimeMillis() < start || yPlayer.getCurrentTimeMillis() > end) {
                yPlayer.pause(); //and Pause the video
            }
        }
        else if (start!=-1){
            if (yPlayer.getCurrentTimeMillis() < start) {
                yPlayer.pause(); //and Pause the video
            }
        }
        else if (end!=-1){
            if (yPlayer.getCurrentTimeMillis() > end) {
                yPlayer.pause(); //and Pause the video
            }
        }
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onSeekTo(int i) {

    }

    class Browser_home extends WebViewClient {

        Browser_home() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Toast.makeText(KhubVideoActivity.this, "Not Permitted", Toast.LENGTH_LONG).show();
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(KhubVideoActivity.this.getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) KhubVideoActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            KhubVideoActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            KhubVideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = KhubVideoActivity.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = KhubVideoActivity.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) KhubVideoActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            KhubVideoActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            final Handler handler = new Handler();
            KhubVideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }


    void getThumbnail(String url,ImageView iv){

        final String[] imgUrl = {""};

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(url)
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {

                } else {
                    try {
                        String resp = responseBody.string();
                        JSONObject ParentjObject = new JSONObject(resp);
                        Log.v("TAG","VimeoThumbnailURL - "+ParentjObject.getString("thumbnail_url"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Picasso.with(KhubVideoActivity.this).load(ParentjObject.getString("thumbnail_url"))
                                            .placeholder(R.drawable.ic_videodefault).memoryPolicy(MemoryPolicy.NO_CACHE).into(iv);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch (Exception e){

                    }
                }
            }
        });

//        Log.v("TAG","VimeoThumbnailURL - "+imgUrl[0]);

//        return imgUrl[0];
    }


    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

        List<TeacherTopicVideo> videoList;

        public VideoAdapter(List<TeacherTopicVideo> videoList) {
            this.videoList = videoList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubVideoActivity.this).inflate(R.layout.item_topic_vid,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            if(videoList.get(position).getVideoName() == null || videoList.get(position).getVideoName().equalsIgnoreCase("")){
                holder.tvVidName.setText(getIntent().getStringExtra("title"));
            }else{
                holder.tvVidName.setText(videoList.get(position).getVideoName());
            }
            holder.tvSubName.setVisibility(View.GONE);

            if (videoList.get(position).getVideoLink().contains("vimeo")) {
                String[] s = null;
                s = videoList.get(position).getVideoLink().split("/");
                getThumbnail("https://vimeo.com/api/oembed.json?url=https://vimeo.com/"+s[s.length-1],holder.llItem);

            }

            if(videoList.get(position).getVideoLink().contains("youtube")){
                String code = getCodeForYoutube(listVideos.get(position).getVideoLink(), position);
                Picasso.with(KhubVideoActivity.this).load("https://img.youtube.com/vi/"+code+"/0.jpg").placeholder(R.drawable.ic_videodefault).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.llItem);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    webView.loadUrl("about:blank");
                    if (player!=null){
                        player.stop(true);
                    }
                    if (yPlayer!=null){
                        yPlayer.pause();
                    }
                    if (listVideos.get(selectedPosition).getVideoName()!= null && listVideos.get(selectedPosition).getVideoName().length()>0) {
                        tvTitle.setText(videoList.get(position).getVideoName());
                        tvVidName.setText(videoList.get(position).getVideoName());
                    }
                    selectedPosition = position;
                    rvVideos.getLayoutManager().scrollToPosition(selectedPosition);

                    if (videoList.get(position).getVideoLink().contains("vimeo")) {

                        playerView.setVisibility(View.GONE);
                        youTubeView.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);

                        String[] s = null;
                        s = videoList.get(position).getVideoLink().split("/");
                        videoLink = "https://player.vimeo.com/video/" + s[s.length - 1];
                        loadWebsite(videoLink);
                    } else if(videoList.get(position).getVideoLink().contains("youtube")){
                        playerView.setVisibility(View.GONE);
                        youTubeView.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);

                        start = -1;
                        end = -1;
                        videoLink = getCodeForYoutube(listVideos.get(position).getVideoLink(),position);
                        loadYoutubeVideo();
//                        youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, KhubVideoActivity.this);

                    }else{
                        youTubeView.setVisibility(View.GONE);
                        webView.setVisibility(View.GONE);
                        playerView.setVisibility(View.VISIBLE);
                        //testing link - http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4

                        videoLink = listVideos.get(selectedPosition).getVideoLink();

                        initializePlayer(videoLink);
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
            TextView tvSubName;
            ImageView llItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVidName = itemView.findViewById(R.id.tv_vid_name);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                llItem = itemView.findViewById(R.id.ll_item);
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkConnectivity);
        if (Util.SDK_INT <= 23) {
            if (videoLink!=null&& videoLink.length()>0) {
                if (playerView != null) {
                    playerView.onPause();
                }
            }
            releasePlayer();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
        if (Util.SDK_INT <= 23 || player == null) {
            if (videoLink!=null&& videoLink.length()>0) {
                initializePlayer(videoLink);
                if (playerView != null) {
                    playerView.onResume();
                }
            }
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (videoLink!=null&& videoLink.length()>0) {
                if (playerView != null) {
                    playerView.onPause();
                }
            }
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        webView.loadUrl("about:blank");
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
        super.onDestroy();
        handler.removeCallbacks(runnable);
        utils.dismissDialog();
    }

    private void initializePlayer(String url) {
        if (player == null) {
//            Intent intent = getIntent();

            mediaSource = createTopLevelMediaSource(url);
            if (mediaSource == null) {
                return;
            }

            RenderersFactory renderersFactory = new DefaultRenderersFactory(/* context= */ this)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

            player =
                    new SimpleExoPlayer.Builder(/* context= */ this, renderersFactory)
                            .build();
            player.addListener(new PlayerEventListener());
            player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
            player.setPlayWhenReady(startAutoPlay);
            playerView.setPlayer(player);
            playerView.setPlaybackPreparer(this);

        }
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        player.prepare(mediaSource, !haveStartPosition, false);

    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
            if (playbackState == Player.STATE_READY) {
                if (percentage != 0) {
                    player.seekTo(percentage);
                    percentage = 0;
                }
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                if (videoLink != null && !videoLink.isEmpty())
                    initializePlayer(videoLink);
            }
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.codecInfo.name);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            mediaSource = null;
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

    @NotNull
    private MediaSource createTopLevelMediaSource(String url) {

        MediaSource[] mediaSources = new MediaSource[1];
        mediaSources[0] = createLeafMediaSource(Uri.parse(url));
        ;

        return mediaSources[0];
    }

    private MediaSource createLeafMediaSource(
            Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }


    @Override
    public void onBackPressed() {
        postContentActivity();
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    void postContentActivity(){
        utils.showLoader(this);
        ApiClient client  = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {

            float pr = (((float) (selectedPosition+1)/ listVideos.size())*100);


            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", getIntent().getStringExtra("courseId"));
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleContentId", moduleContentId);
            jsonObject.put("contentType", contentType);
            jsonObject.put("progress", pr);
            jsonObject.put("activityData",array );

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = client.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity,body, sh_Pref);
        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity);
        utils.showLog(TAG, "body -"+ jsonObject.toString());
        client.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject ParentjObject = new JSONObject(res);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
                                finish();
                            });

                        }
                    }
                    catch (JSONException e) {
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

    String getCodeForYoutube(String url, int pos){
        String res = url;
        String[] s = null;
        if (listVideos.get(pos).getVideoLink().contains("watch?v")) {
            s = listVideos.get(pos).getVideoLink().split("=");
        } else {
            s = listVideos.get(pos).getVideoLink().split("/");
        }

        if (s[s.length-1].contains("?")){
            String[] embeded = s[s.length-1].split("\\?");
            res = embeded[0];
            String[] params = embeded[1].split("&");
            for (String param : params) {
                if (param.contains("start")) {
                    start = (Integer.parseInt(param.split("=")[1])*1000);
                }
                if (param.contains("end")) {
                    end = (Integer.parseInt(param.split("=")[1])*1000);
                }
            }
        }
        else res = s[s.length - 1];
        return res;
    }

}