package myschoolapp.com.gsnedutech;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.AddFileVideoAttach;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecordFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.VideoWebViewer;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ArenaVideoDisplayRejected extends YouTubeBaseActivity implements PlaybackPreparer, YouTubePlayer.OnInitializedListener, PlayerControlView.VisibilityListener {

    private static final String TAG = ArenaVideoDisplayRejected.class.getName();
    
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.rv_files)
    RecyclerView rvFiles;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.ll_new_files)
    LinearLayout llNewFiles;
    @BindView(R.id.rv_new_files)
    RecyclerView rvNewFiles;
    @BindView(R.id.button_upload)
    TextView btnUpload;

    int selectAudio = -1;
    String videoLink="";
    String vidType="";

    ArenaRecord videoObj;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;

    StudentObj sObj;

    List<ArenaRecordFiles> arenaFiles = new ArrayList<>();

    YouTubePlayer yPlayer;
    private YouTubePlayerView youTubeView;

    private static final int RECOVERY_DIALOG_REQUEST = 1;

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

    
    private static int REQUEST_TAKE_GALLERY_VIDEO = 2;
    private static int REQUEST_TAKE_VIDEO = 1;

    File vid;
    List<AddFileVideoAttach> fileList = new ArrayList<>();

    AmazonS3Client s3Client1;
    List<String> keyName = new ArrayList<>();
    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_video_display_rejected);
        ButterKnife.bind(this);

        setupPlayer(savedInstanceState);
        init();
    }
    
    void init(){

        youTubeView = findViewById(R.id.youtube_view);
        
        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        rvNewFiles.setLayoutManager(new LinearLayoutManager(this));
        rvNewFiles.setAdapter(new FilesAdapter(fileList));

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        videoObj = (ArenaRecord) getIntent().getSerializableExtra("videoObj");

        String[] title = videoObj.getArenaName().split("~~");

        String url = "NA";

        for (String s : title) {
            if (s.contains("http")) {
                url = s;
            }
        }


        //initial like count and display
        tvTitle.setText(title[0]);
        tvDesc.setText(videoObj.getArenaDesc());
        tvComment.setText(videoObj.getTeacherReview());

        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog();
            }
        });
        
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnUpload.getText().toString().equalsIgnoreCase("Upload")){
                    uploadToS3();
                }else{
                    getTeachers(videoObj.getArenaId()+"");
                }
            }
        });

        //get article related files service
        getArticleDetails();
    }

    private void showOptionDialog() {
        Dialog dialog = new Dialog(ArenaVideoDisplayRejected.this);
        dialog.setContentView(R.layout.dialog_video_record);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.iv_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File f = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    f = new File(getExternalFilesDir(null) + "/vids");
                }else {
                    f = new File(Environment.getExternalStorageDirectory(), "vids");
                }

//                File f = new File(Environment.getExternalStorageDirectory(), "vids");
                if (!f.exists()) {
                    f.mkdirs();
                }

                vid = new File(f,"vid_example"+System.currentTimeMillis()+".mp4");

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 180);
//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(vid));
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(vid));
                    }else {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, vid);
                        takeVideoIntent.putExtra("return-data", true);
                    }
                    startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
                }else {
                    Toast.makeText(ArenaVideoDisplayRejected.this,"Oops! there was a problem.",Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.ll_video_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);

                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ll_att_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Dialog dialog1 = new Dialog(ArenaVideoDisplayRejected.this);
                dialog1.setContentView(R.layout.layout_attach_youtube);
                dialog1.setCancelable(true);
                dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog1.show();

                EditText etLink = dialog1.findViewById(R.id.et_link);
                EditText etName = dialog1.findViewById(R.id.et_name);

                dialog1.findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etLink.getText().toString().trim().length()>0 && etName.getText().toString().trim().length()>0) {
                            AddFileVideoAttach fNew = new AddFileVideoAttach();
                            fNew.setLink(etLink.getText().toString().trim());
                            fNew.setName(etName.getText().toString().trim());
                            fileList.add(fNew);
                            
                            dialog1.dismiss();
                            rvNewFiles.getAdapter().notifyDataSetChanged();

                            if (fileList.size()>0){
                                llNewFiles.setVisibility(View.VISIBLE);
                                btnUpload.setText("Upload");
                            }else {
                                llNewFiles.setVisibility(View.GONE);
                                btnUpload.setText("Re-Submit");
                            }
                            
                        }else {
                            Toast.makeText(ArenaVideoDisplayRejected.this,"Please enter all the details!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_VIDEO) {

            if (resultCode == RESULT_OK) {

                Dialog dialog = new Dialog(ArenaVideoDisplayRejected.this);
                dialog.setContentView(R.layout.layout_save_audio);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();

                EditText et = dialog.findViewById(R.id.et_name);

                dialog.findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (et.getText().toString().length()>0){

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                                Uri selectedImage = data.getData();


                                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String filePath = cursor.getString(columnIndex);
                                Log.v("log","filePath is : "+filePath);

                                cursor.close();


                                File newdir= getDir("vids", Context.MODE_PRIVATE);  //Don't do
                                if (!newdir.exists())
                                    newdir.mkdirs();

                                File f = new File(filePath);

                                File rename = new File(newdir.getAbsolutePath()+"/"+et.getText().toString()+".mp4");

                                InputStream in = null;
                                try {
                                    in = new FileInputStream(filePath);
                                    OutputStream out = new FileOutputStream(rename.getAbsolutePath());

                                    byte[] buf = new byte[1024];
                                    int len;

                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }

                                    in.close();
                                    out.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // Copy the bits from instream to outstream

                                AddFileVideoAttach fNew = new AddFileVideoAttach();
                                fNew.setFile(rename);
                                fileList.add(fNew);
                                if (fileList.size()>0){
                                    llNewFiles.setVisibility(View.VISIBLE);
                                    btnUpload.setText("Upload");
                                }else {
                                    llNewFiles.setVisibility(View.GONE);
                                    btnUpload.setText("Re-Submit");
                                }
                            }
                            else {
                                File f = new File(Environment.getExternalStorageDirectory(), "vids");



//                            File f = new File(Environment.getExternalStorageDirectory(), "vids");
                                if (!f.exists()) {
                                    f.mkdirs();
                                }

                                File rename = new File(f,et.getText().toString()+".mp4");
                                vid.renameTo(rename);

                                File root = new File(Environment.getExternalStorageDirectory(), "/vids");

//                            File root = new File(Environment.getExternalStorageDirectory(), "/vids");
                                File[] files = root.listFiles();



                                if (files!=null) {
                                    for (File file : files) {
                                        int c=0;
                                        for (int i=0;i<fileList.size();i++) {
                                            if (fileList.get(i).getFile()!=null && fileList.get(i).getFile().equals(file)){
                                                c++;
                                            }
                                        }
                                        if (c==0){
                                            AddFileVideoAttach fNew = new AddFileVideoAttach();
                                            fNew.setFile(file);
                                            fileList.add(fNew);
                                            if (fileList.size()>0){
                                                llNewFiles.setVisibility(View.VISIBLE);
                                                btnUpload.setText("Upload");
                                            }else {
                                                llNewFiles.setVisibility(View.GONE);
                                                btnUpload.setText("Re-Submit");
                                            }
                                            
                                        }
                                    }
                                }

                            }

                            rvNewFiles.getAdapter().notifyDataSetChanged();

                            if (getIntent().hasExtra("videoObj")){
                                
                            }
                           
                            dialog.dismiss();
                        }else {
                            Toast.makeText(ArenaVideoDisplayRejected.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vid.delete();
                        dialog.dismiss();
                    }
                });
            }
        
        } else{
            if (resultCode == RESULT_OK){
                File file = null;
                try {
                    file = FileUtil.from(ArenaVideoDisplayRejected.this,data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AddFileVideoAttach fNew = new AddFileVideoAttach();
                fNew.setFile(file);
                fileList.add(fNew);
                
                if (getIntent().hasExtra("videoObj")){
                    
                }

                if (fileList.size()>0){
                    llNewFiles.setVisibility(View.VISIBLE);
                    btnUpload.setText("Upload");
                }else {
                    llNewFiles.setVisibility(View.GONE);
                    btnUpload.setText("Re-Submit");
                }

                rvNewFiles.getAdapter().notifyDataSetChanged();

            }
        }

    }
    

    void getArticleDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

       

        Request get = new Request.Builder()
                .url(AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + videoObj.getArenaId()+"")
                .build();

        utils.showLog(TAG, "url "+AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + videoObj.getArenaId()+"");

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
                                        rvFiles.setLayoutManager(new GridLayoutManager(ArenaVideoDisplayRejected.this,2));
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
                                            youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, ArenaVideoDisplayRejected.this);

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
            Toast.makeText(ArenaVideoDisplayRejected.this, "Not Permitted", Toast.LENGTH_LONG).show();
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
            return BitmapFactory.decodeResource(ArenaVideoDisplayRejected.this.getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) ArenaVideoDisplayRejected.this.getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            ArenaVideoDisplayRejected.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            ArenaVideoDisplayRejected.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = ArenaVideoDisplayRejected.this.getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = ArenaVideoDisplayRejected.this.getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) ArenaVideoDisplayRejected.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            ArenaVideoDisplayRejected.this.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            final Handler handler = new Handler();
            ArenaVideoDisplayRejected.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
            return new ViewHolder(LayoutInflater.from(ArenaVideoDisplayRejected.this).inflate(R.layout.item_topic_vid,parent,false));
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
                Picasso.with(ArenaVideoDisplayRejected.this).load("https://img.youtube.com/vi/"+s[s.length-1]+"/0.jpg").placeholder(R.drawable.bg_whitetoblack).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.llItem);
            }else {
                try {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.isMemoryCacheable();

                    Glide.with(ArenaVideoDisplayRejected.this).setDefaultRequestOptions(requestOptions).load(link).placeholder(R.drawable.bg_whitetoblack).into(holder.llItem);
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
                        youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, ArenaVideoDisplayRejected.this);

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
                                    Picasso.with(ArenaVideoDisplayRejected.this).load(ParentjObject.getString("thumbnail_url"))
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
                Intent intent = new Intent(ArenaVideoDisplayRejected.this, PlayerActivity.class);
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

            new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage("There was an error playing this File. Open in Web?")
                    .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
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


    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder>{

        List<AddFileVideoAttach> files;

        public FilesAdapter(List<AddFileVideoAttach> files) {
            this.files = files;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaVideoDisplayRejected.this).inflate(R.layout.item_arena_videos,parent,false));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.ivDel.setVisibility(View.VISIBLE);
            if(files.get(position).getFile()!=null){
                holder.tvVideoName.setText(files.get(position).getFile().getName());
                try {

                    Path file = Paths.get(files.get(position).getFile().getAbsolutePath());

                    BasicFileAttributes attr =
                            Files.readAttributes(file, BasicFileAttributes.class);


                    utils.showLog("tag","creationTime: " + attr.creationTime());
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
                    holder.tvDate.setText(output.format(input.parse(attr.creationTime().toString())));


                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(files.get(position).getFile().getAbsolutePath());
                    mp.prepare();
                    holder.tvDuration.setText(String.format("%02d:%02d:%02d", (mp.getDuration()/1000) / 3600, ((mp.getDuration()/1000) % 3600) / 60, ((mp.getDuration()/1000) % 60)));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                holder.tvVideoName.setText(fileList.get(position).getName());
                holder.tvDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                holder.tvDuration.setVisibility(View.INVISIBLE);
            }




            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    fileList.get(position).getFile().delete();
                                    fileList.remove(position);
                                    notifyDataSetChanged();
                                    if (fileList.size()==0){
                                        llNewFiles.setVisibility(View.GONE);
                                        btnUpload.setText("Upload");
                                    }else {
                                        llNewFiles.setVisibility(View.VISIBLE);
                                        btnUpload.setText("Re-Submit");
                                    }
                                }
                            })

                            .setCancelable(true)
                            .show();


                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fileList.get(position).getFile()!=null) {
                        Intent intent = new Intent(ArenaVideoDisplayRejected.this, LocalVideoPlayer.class);
                        intent.putExtra("path", fileList.get(position).getFile().getAbsolutePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else {
                        String link = fileList.get(position).getLink();
                        if (fileList.get(position).getLink().contains("vimeo")){
                            Intent intent = new Intent(ArenaVideoDisplayRejected.this, VideoWebViewer.class);
                            intent.putExtra("videoItem", fileList.get(position).getLink());
                            intent.putExtra("name",holder.tvVideoName.getText().toString());
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(ArenaVideoDisplayRejected.this, YoutubeActivity.class);
                            String s[] = fileList.get(position).getLink().split("/");
                            intent.putExtra("videoItem", s[s.length - 1]);
                            startActivity(intent);
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvVideoName,tvDuration,tvDate;
            ImageView ivDel;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVideoName = itemView.findViewById(R.id.tv_vid_name);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                tvDate = itemView.findViewById(R.id.tv_date);
                ivDel = itemView.findViewById(R.id.iv_del);
            }
        }
    }


    void uploadToS3(){


        s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")), Region.getRegion(Regions.AP_SOUTH_1));


        boolean vailid = true;
        String names = "";

//        for (int i=0;i<fileList.size();i++){
//            if (fileList.get(i).getFile()!=null){
//                File file = fileList.get(i).getFile();
//                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
//                if (file_size>100){
//                    vailid = false;
//                    if (names.length()==0){
//                        names = names+file.getName();
//                    }else{
//                        names = names+", "+file.getName();
//                    }
//                }
//            }
//        }

        if (vailid) {
            utils.showLoader(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Date expiration = new Date();
                        long msec = expiration.getTime();
                        msec += 1000 * 60 * 60; // 1 hour.
                        expiration.setTime(msec);
//                publishProgress(arg0);

                        int c = 0;


                        for (int i = 0; i < fileList.size(); i++) {
                            c++;
                            utils.showLog(TAG, "url " + "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName());
                            keyName.add("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName(),
                                    fileList.get(i).getFile());//key is  URL


                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i)));

                            if (c == fileList.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertArenaSubRecord();
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.showLog(TAG, "error " + e.getMessage());
                        utils.dismissDialog();
                    }
                }
            }).start();
        }else {
            new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(names+" sizes exceed 100 MB limit!")
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })

                    .setCancelable(true)
                    .show();
        }


    }

    //service to add file changes in existing draft
    private void insertArenaSubRecord() {

        JSONArray filesArray = new JSONArray();


        for (int i=0;i<fileList.size();i++){

            Log.v(TAG,"url "+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));


            String[] split= keyName.get(i).split("\\.");
            String ext = split[split.length-1];

            JSONObject obj = new JSONObject();
            try {
                obj.put("fileName",fileList.get(i).getFile().getName());
                obj.put("fileType","audio/"+ext);
                obj.put("filePath",s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            filesArray.put(obj);
        }

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("arenaType","General");
            postObject.put("arenaId",videoObj.getArenaId()+"");
            postObject.put("createdBy", sObj.getStudentId());
            postObject.put("filesArray", filesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.InsertTeacherArenaSubRecords;
        }else {
            url = AppUrls.InsertArenaSubRecords;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
                utils.showLog(TAG,"response "+resp);

                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.getString("StatusCode").equalsIgnoreCase("200")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                File root = new File(Environment.getExternalStorageDirectory(), "/audio");
                                File root = null;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    root = new File(getExternalFilesDir(null) + "/audio");
                                }else {
                                    root = new File(Environment.getExternalStorageDirectory(), "/audio");
                                }
                                File[] files = root.listFiles();

                                if (files != null && files.length>0){
                                    for(File f: files){
                                        f.delete();
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getTeachers(videoObj.getArenaId()+"");
                                    }
                                });


                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
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

    //service to get teachers of your section to send for approval
    private void getTeachers(String arenaId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("sectionId",sObj.getClassCourseSectionId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeachersForArena)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = respObj.getJSONArray("arenaRecords");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaTeacherList>>(){}.getType();
                            listTeachers.clear();
                            listTeachers.addAll(gson.fromJson(array.toString(),type));

                            if (listTeachers.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog dialog = new Dialog(ArenaVideoDisplayRejected.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my audio. Can submit for approval later.")

                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });

                                        RecyclerView rvTeachers = dialog.findViewById(R.id.rv_teachers);
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(ArenaVideoDisplayRejected.this));
                                        rvTeachers.setAdapter(new ArenaVideoDisplayRejected.TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nAudio article cannot be sent for approval\nPlease contact your school for more information.")

                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
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

    //submit arena for approval
    private void submitArena(String arenaId,String teacherId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("arenaId",arenaId);
            postObject.put("arenaStatus", "0");
            postObject.put("createdBy", sObj.getStudentId()+"");
            postObject.put("userId", sObj.getStudentId()+"");
            postObject.put("teacherId", teacherId);
            postObject.put("arenaDraftStatus","1");
            postObject.put("schemaName", sh_Pref.getString("schema",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = new AppUrls().ReviewArenaStatus;
        }else {
            url = new AppUrls().SubmitStudentArena;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        utils.showLog(TAG,"url "+url);
        utils.showLog(TAG,"url obj "+postObject.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("oops! something went wrong please try again later")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                })

                                .setCancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("oops! something went wrong please try again later")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        }
                                    })

                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaVideoDisplayRejected.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArenaVideoDisplayRejected.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("oops! something went wrong please try again later")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }
                                            })

                                            .setCancelable(false)
                                            .show();
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


    class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder>{

        List<ArenaTeacherList> teacherList; String arenaId;
        Dialog dialog;

        public TeacherAdapter(List<ArenaTeacherList> teacherList, String arenaId,Dialog dialog) {
            this.teacherList = teacherList;
            this.arenaId = arenaId;
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public ArenaVideoDisplayRejected.TeacherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ArenaVideoDisplayRejected.TeacherAdapter.ViewHolder(LayoutInflater.from(ArenaVideoDisplayRejected.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ArenaVideoDisplayRejected.TeacherAdapter.ViewHolder holder, int position) {
            holder.tvName.setText(listTeachers.get(position).getUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitArena(arenaId,listTeachers.get(position).getTeacherId()+"");
                    dialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return teacherList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);

            }
        }
    }


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
        deleteExisting();
        super.onDestroy();

    }

    void deleteExisting(){
        File root = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root = getDir("vids", Context.MODE_PRIVATE);

        }else {
            root = new File(Environment.getExternalStorageDirectory(), "/vids");
        }
        File[] files = root.listFiles();

        if (files != null && files.length>0){
            for(File f: files){
                f.delete();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}