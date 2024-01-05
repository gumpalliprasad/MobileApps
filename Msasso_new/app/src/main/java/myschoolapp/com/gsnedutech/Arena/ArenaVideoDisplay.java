package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecordFiles;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ArenaVideoDisplay extends YouTubeBaseActivity implements PlaybackPreparer, YouTubePlayer.OnInitializedListener, PlayerControlView.VisibilityListener {

    private static final String TAG = ArenaVideoDisplay.class.getName();

    
//    @BindView(R.id.htab_header)
//    ImageView ivHeader;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.tv_likes)
    TextView tvLikes;
    @BindView(R.id.rv_files)
    RecyclerView rvFiles;
    @BindView(R.id.webView)
    WebView webView;

    YouTubePlayer yPlayer;
    private YouTubePlayerView youTubeView;

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    
    TeacherObj tObj;

    ArenaRecord videoObj;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;

    StudentObj sObj;

    String videoLink="";
    String vidType="";

    List<ArenaRecordFiles> arenaFiles = new ArrayList<>();

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_video_display);
        ButterKnife.bind(this);
        
        init();
        setupPlayer(savedInstanceState);
    }
    
    void init(){

        youTubeView = findViewById(R.id.youtube_view);
        
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            tvLikes.setVisibility(View.GONE);
        }else {
            Gson gson = new Gson();
            String json = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }

        if (getIntent().hasExtra("reassign")){
            findViewById(R.id.tv_reassign_students).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_reassign_students).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSections();
                }
            });
        }

        if (getIntent().hasExtra("videoObj")) {

            //get details of article
            videoObj = (ArenaRecord) getIntent().getSerializableExtra("videoObj");

            String[] title = videoObj.getArenaName().split("~~");

            String url = "NA";

            for (String s : title) {
                if (s.contains("http")) {
                    url = s;
                }
            }

//            if (!url.equalsIgnoreCase("NA")) {
//                Picasso.with(ArenaVideoDisplay.this).load(url).placeholder(R.drawable.ic_arena_img)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
//
//            }

            //initial like count and display
            tvTitle.setText(title[0]);
            tvDesc.setText(videoObj.getArenaDesc());
            tvLikes.setText(videoObj.getLikesCount()+"");

            //check if student liked article before or not
            if (!(videoObj.getStudentLike() == null) && !videoObj.getStudentLike().equalsIgnoreCase("0")){
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
            }

            try {
                if (videoObj.getUserRole()!=null && videoObj.getUserRole().equalsIgnoreCase("T")) {

                    if (getIntent().hasExtra("my") || getIntent().hasExtra("self")){
                        tvStudent.setText("By " + tObj.getUserName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoObj.getCreatedDate())));
                    }else {
                        tvStudent.setText("By " + videoObj.getUserName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoObj.getCreatedDate())));
                    }

                }else {
                    if (getIntent().hasExtra("my") || getIntent().hasExtra("self")){
                        tvStudent.setText("By " + sObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoObj.getCreatedDate())));
                    }else {
                        tvStudent.setText("By " + videoObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoObj.getCreatedDate())));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //onclick for like button
            findViewById(R.id.tv_likes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (getIntent().hasExtra("self") || videoObj.getUserId()==null || sObj.getStudentId().equalsIgnoreCase(videoObj.getUserId())){
                        Toast.makeText(ArenaVideoDisplay.this,"You cant like your own article!",Toast.LENGTH_SHORT).show();
                    }else{
                        if (videoObj.getStudentLike().equalsIgnoreCase("0")){
                            likeArticle();
                        }else {
                            Toast.makeText(ArenaVideoDisplay.this,"You have already liked the article!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });



            //get article related files service
            getArticleDetails();

        }


    }

    private void likeArticle() {

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
            jsonObject.put("arenaId", videoObj.getArenaId()+"");
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
                        Toast.makeText(ArenaVideoDisplay.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArenaVideoDisplay.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                                    int n = Integer.parseInt(tvLikes.getText().toString());
                                    tvLikes.setText((n+1)+"");
                                    tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
                                    Toast.makeText(ArenaVideoDisplay.this,"Liked Successfully.",Toast.LENGTH_SHORT).show();
                                    videoObj.setStudentLike(sObj.getStudentId());
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaVideoDisplay.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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

    void getArticleDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + videoObj.getArenaId()+""+ "&userId="+tObj.getUserId();
        }else {
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + videoObj.getArenaId()+"";
        }

        Request get = new Request.Builder()
                .url(url)
                .build();

        utils.showLog(TAG, "url "+url);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        rvFiles.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                Log.v(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvFiles.setVisibility(View.GONE);
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaRecordFiles>>() {
                            }.getType();

                            arenaFiles.clear();
                            arenaFiles.addAll(gson.fromJson(array.toString(),type));

                            if (arenaFiles.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvFiles.setVisibility(View.VISIBLE);
                                        rvFiles.setLayoutManager(new GridLayoutManager(ArenaVideoDisplay.this,2));
                                        videoLink = arenaFiles.get(0).getFilePath();
                                        vidType = arenaFiles.get(0).getFileType();
                                        if (videoLink.contains("~~")){
                                            videoLink = videoLink.split("~~")[1];
                                        }
                                        
                                        if (vidType.contains("vimeo")){
                                            playerView.setVisibility(View.GONE);
                                            youTubeView.setVisibility(View.GONE);
                                            webView.setVisibility(View.VISIBLE);
                                            loadWebsite(videoLink);
                                        }else if(vidType.contains("youtube")){
                                            playerView.setVisibility(View.GONE);
                                            youTubeView.setVisibility(View.VISIBLE);
                                            webView.setVisibility(View.GONE);

                                            String[] s = null;
                                            if (videoLink.contains("watch?v")) {
                                                s = videoLink.split("=");
                                            } else {
                                                s = videoLink.split("/");
                                            }
                                            videoLink = s[s.length - 1];
                                            youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, ArenaVideoDisplay.this);

                                        }else {
                                            playerView.setVisibility(View.VISIBLE);
                                            youTubeView.setVisibility(View.GONE);
                                            webView.setVisibility(View.GONE);
                                            initializePlayer(videoLink);
                                            
                                        }
                                        rvFiles.setAdapter(new VideoAdapter(arenaFiles));
                                        utils.dismissDialog();


                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvFiles.setVisibility(View.GONE);
                                        utils.dismissDialog();
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvFiles.setVisibility(View.GONE);
                                    utils.dismissDialog();
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
    
    class Browser_home extends WebViewClient {

        Browser_home() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Toast.makeText(ArenaVideoDisplay.this, "Not Permitted", Toast.LENGTH_LONG).show();
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
            return BitmapFactory.decodeResource(ArenaVideoDisplay.this.getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) ArenaVideoDisplay.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            ArenaVideoDisplay.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            ArenaVideoDisplay.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = ArenaVideoDisplay.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = ArenaVideoDisplay.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) ArenaVideoDisplay.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            ArenaVideoDisplay.this.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            final Handler handler = new Handler();
            ArenaVideoDisplay.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
    
    //Youtube start
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {

            yPlayer = youTubePlayer;

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            youTubePlayer.loadVideo(videoLink);
            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youTubePlayer.play();
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

    //Youtube end


    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

        List<ArenaRecordFiles> videoList;

        public VideoAdapter(List<ArenaRecordFiles> videoList) {
            this.videoList = videoList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaVideoDisplay.this).inflate(R.layout.item_topic_vid,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.tvVidName.setText(videoList.get(position).getFileName());
            holder.tvSubName.setVisibility(View.GONE);
            String link = videoList.get(position).getFilePath();
            String type = videoList.get(position).getFileType();
            if (link.contains("~~")){
                link = (videoList.get(position).getFilePath()).split("~~")[1];

            }

            if (type.contains("vimeo")){
                String[] s = null;
                s = link.split("/");
                getThumbnail("https://vimeo.com/api/oembed.json?url=https://vimeo.com/"+s[s.length-1],holder.llItem);
            }else if(type.contains("youtube")){
                String[] s = null;
                if (link.contains("watch?v")) {
                    s = link.split("=");
                } else {
                    s = link.split("/");
                }
                Picasso.with(ArenaVideoDisplay.this).load("https://img.youtube.com/vi/"+s[s.length-1]+"/0.jpg").placeholder(R.drawable.bg_whitetoblack).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.llItem);
            }else {
                try {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.isMemoryCacheable();

                    Glide.with(ArenaVideoDisplay.this).setDefaultRequestOptions(requestOptions).load(link).placeholder(R.drawable.bg_whitetoblack).into(holder.llItem);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    videoLink = videoList.get(position).getFilePath();
                    vidType = videoList.get(position).getFileType();

                    if (videoLink.contains("~~")){
                        videoLink = (videoList.get(position).getFilePath()).split("~~")[1];
                    }

                    if (vidType.contains("vimeo")){
                        playerView.setVisibility(View.GONE);
                        youTubeView.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        loadWebsite(videoLink);
                    }else if(vidType.contains("youtube")){
                        playerView.setVisibility(View.GONE);
                        youTubeView.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);

                        String[] s = null;
                        if (videoLink.contains("watch?v")) {
                            s = videoLink.split("=");
                        } else {
                            s = videoLink.split("/");
                        }
                        videoLink = s[s.length - 1];
                        youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, ArenaVideoDisplay.this);

                    }else {
                        playerView.setVisibility(View.VISIBLE);
                        youTubeView.setVisibility(View.GONE);
                        webView.setVisibility(View.GONE);

                        releasePlayer();
                        initializePlayer(videoLink);
                        utils.showLog(TAG, "video - " + videoLink);
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
                                    Picasso.with(ArenaVideoDisplay.this).load(ParentjObject.getString("thumbnail_url"))
                                            .placeholder(R.drawable.bg_whitetoblack).memoryPolicy(MemoryPolicy.NO_CACHE).into(iv);
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

    void setupPlayer(Bundle savedInstanceState){
        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        String sphericalStereoMode = null;

        playerView = findViewById(R.id.exoplayer);

        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();

        fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArenaVideoDisplay.this, PlayerActivity.class);
                intent.putExtra("url", videoLink);
                intent.putExtra("from","topic");
                intent.putExtra("percentage", player.getCurrentPosition());
                startActivityForResult(intent, 1234);
            }
        });

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


    public DataSource.Factory buildDataSourceFactory() {
        return new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getResources().getString(R.string.app_name)));
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

            new AlertDialog.Builder(ArenaVideoDisplay.this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("There was an error playing this File. Open in Web?")
                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(videoLink));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setPackage("com.android.chrome");
                            startActivity(intent);
                        }
                    })

                    .setCancelable(true)
                    .show();

            return Pair.create(0, errorString);
        }
    }

    @NotNull
    private MediaSource createTopLevelMediaSource(String url) {

        MediaSource[] mediaSources = new MediaSource[1];
        mediaSources[0] = createLeafMediaSource(Uri.parse(url));
        ;

        return mediaSources[0];
    }

    private MediaSource createLeafMediaSource(Uri uri) {
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

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            mediaSource = null;
        }
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

    @Override
    public void preparePlayback() {

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    //Exo player functions end


    @Override
    protected void onPause() {
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (Util.SDK_INT <= 23 || player == null) {
            if (videoLink!=null && !videoLink.equalsIgnoreCase("") && !vidType.contains("vimeo") && !vidType.contains("youtube")){
                initializePlayer(videoLink);
            }
            if (playerView != null) {
                playerView.onResume();
            }
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        webView.loadUrl("about:blank");
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
        super.onDestroy();

    }

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+videoObj.getArenaId())
                .build();

        utils.showLog(TAG, AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+videoObj.getArenaId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(ArenaVideoDisplay.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArenaVideoDisplay.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);

                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){

                            JSONArray array = respObj.getJSONArray("collegesInfo");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollegeInfo>>() {
                            }.getType();

                            List<CollegeInfo> listBranches = new ArrayList<>();
                            listBranches.clear();
                            listBranches.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<listBranches.size();i++){
                                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                            for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getAssignedId().equalsIgnoreCase("0")){
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(false);
                                                    }else {
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(true);
                                                        listBranches.get(i).setShow(true);
                                                        for (int a=0;a<listBranches.get(i).getCourses().size();a++) {
                                                            listBranches.get(i).getCourses().get(a).setShow(true);
                                                            if (listBranches.get(i).getCourses().get(a).getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())){
                                                                for (int b=0;b<listBranches.get(i).getCourses().get(a).getClasses().size();b++){
                                                                    listBranches.get(i).getCourses().get(a).getClasses().get(b).setShow(true);
                                                                    if (listBranches.get(i).getCourses().get(a).getClasses().get(b).getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                                                        for (int c=0;c<listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().size();c++){
                                                                            listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().get(c).setShow(true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(ArenaVideoDisplay.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaVideoDisplay.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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

    public void arenaReview(String s, JSONArray sections) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("arenaId",videoObj.getArenaId()+"");
            jsonObject.put("arenaStatus","1");
            jsonObject.put("sections",sections);
            if(videoObj.getStudentId()!=null){
                jsonObject.put("createdBy",tObj.getUserId());
            }else {
                jsonObject.put("createdBy",videoObj.getStudentId()+"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().ReviewArenaStatus);
        utils.showLog(TAG,"url obj"+jsonObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().ReviewArenaStatus)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArenaVideoDisplay.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Oops! Something went wrong.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(true)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.v(TAG,"response "+resp);
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(ArenaVideoDisplay.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Oops! Something went wrong.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObj = new JSONObject(resp);
                        if (jsonObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (s.equalsIgnoreCase("1")){
                                        new AlertDialog.Builder(ArenaVideoDisplay.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Approved successfully!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }else {
                                        new AlertDialog.Builder(ArenaVideoDisplay.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Review successfully sent!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}