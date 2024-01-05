package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArenaAudioDisplay extends AppCompatActivity implements PlayerControlView.VisibilityListener, PlaybackPreparer {

    private static final String TAG = ArenaAudioDisplay.class.getName();

    @BindView(R.id.htab_header)
    ImageView ivHeader;
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

    TeacherObj tObj;

    ArenaRecord audioObj;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;

    StudentObj sObj;

    List<ArenaRecordFiles> arenaFiles = new ArrayList<>();



    int selectAudio = -1;
    String videoLink="";
    int selectedPosition=-1;

    private PlayerView playerView;
    AudioAdapter adapter;

    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    ImageView fullscreenButton;
    boolean fullscreen = false;
    long percentage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_audio_display);
        ButterKnife.bind(this);
        init();
        setupPlayer(savedInstanceState);
    }

    void init(){

        rvFiles.setLayoutManager(new LinearLayoutManager(this));

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

        if (getIntent().hasExtra("audioObj")) {

            //get details of article
            audioObj = (ArenaRecord) getIntent().getSerializableExtra("audioObj");

            String[] title = audioObj.getArenaName().split("~~");

            String url = "NA";

            for (String s : title) {
                if (s.contains("http")) {
                    url = s;
                }
            }

            if (!url.equalsIgnoreCase("NA")) {
                Picasso.with(ArenaAudioDisplay.this).load(url).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
                Picasso.with(ArenaAudioDisplay.this).load(url).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(((ImageView)findViewById(R.id.iv_album_cover)));

            }

            //initial like count and display
            tvTitle.setText(title[0]);
            tvDesc.setText(audioObj.getArenaDesc());
            tvLikes.setText(audioObj.getLikesCount()+"");

            //check if student liked article before or not
            if (!(audioObj.getStudentLike() == null) && !audioObj.getStudentLike().equalsIgnoreCase("0")){
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
            }

            try {
                if (audioObj.getUserRole()!=null && audioObj.getUserRole().equalsIgnoreCase("T")) {

                    if (getIntent().hasExtra("my")){
                        tvStudent.setText("By " + tObj.getUserName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(audioObj.getCreatedDate())));
                    }else {
                        tvStudent.setText("By " + audioObj.getUserName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(audioObj.getCreatedDate())));
                    }

                }else {
                    if (getIntent().hasExtra("my")){
                        tvStudent.setText("By " + sObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(audioObj.getCreatedDate())));
                    }else {
                        tvStudent.setText("By " + audioObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(audioObj.getCreatedDate())));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //onclick for like button
            findViewById(R.id.tv_likes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (getIntent().hasExtra("self") || audioObj.getUserId()==null || sObj.getStudentId().equalsIgnoreCase(audioObj.getUserId())){
                        Toast.makeText(ArenaAudioDisplay.this,"You cant like your own article!",Toast.LENGTH_SHORT).show();
                    }else{
                        if (audioObj.getStudentLike().equalsIgnoreCase("0")){
                            likeArticle();
                        }else {
                            Toast.makeText(ArenaAudioDisplay.this,"You have already liked the article!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });



            //get article related files service
            getArticleDetails();

        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
            jsonObject.put("arenaId", audioObj.getArenaId()+"");
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
                        Toast.makeText(ArenaAudioDisplay.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArenaAudioDisplay.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ArenaAudioDisplay.this,"Liked Successfully.",Toast.LENGTH_SHORT).show();
                                    audioObj.setStudentLike(sObj.getStudentId());
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaAudioDisplay.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + audioObj.getArenaId()+""+ "&userId="+tObj.getUserId();
        }else {
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + audioObj.getArenaId()+"";
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
                                        rvFiles.setAdapter(new AudioAdapter());
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

    class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaAudioDisplay.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//            holder.seekBar.setProgress((int) audioFiles.get(position).getPosition());

            if(position==selectAudio){
                holder.ivPlay.setImageDrawable(getDrawable(R.drawable.exo_controls_pause));
            }else {
                holder.ivPlay.setImageDrawable(getDrawable(R.drawable.ic_gg_play_button));
            }
            holder.seekBar.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);

            holder.tvFileName.setText(arenaFiles.get(position).getFileName());
            holder.ivPlay.setOnClickListener(v -> {
                selectAudio = position;
                findViewById(R.id.ll_player).setVisibility(View.VISIBLE);
                player = null;
                String link = "";
                if (arenaFiles.get(position).getFilePath().contains("~~")){
                    link = arenaFiles.get(position).getFilePath().split("~~")[(arenaFiles.get(position).getFilePath().split("~~").length)-1];
                }else {
                    link = arenaFiles.get(position).getFilePath();
                }
                initializePlayer(link);
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return arenaFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName,tvDuration;
            ImageView ivPlay,ivDelete;
            SeekBar seekBar;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
                tvDuration = itemView.findViewById(R.id.tv_file_duration);
                ivPlay = itemView.findViewById(R.id.iv_play);
                ivDelete = itemView.findViewById(R.id.iv_delete);
                seekBar = itemView.findViewById(R.id.seek);
            }
        }
    }

    //Exo player functions start

    void setupPlayer(Bundle savedInstanceState){
        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        String sphericalStereoMode = null;

        playerView = findViewById(R.id.player_view);

        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        playerView.setControllerShowTimeoutMs(0);
        playerView.setControllerHideOnTouch(false);
        playerView.setUseArtwork(false);


        fullscreenButton = findViewById(R.id.exo_fullscreen_icon);
        fullscreenButton.setVisibility(View.GONE);
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

            playerView.setVisibility(View.VISIBLE);

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
            if (playbackState == Player.STATE_ENDED){
                selectAudio = -1;
                findViewById(R.id.ll_player).setVisibility(View.GONE);
                rvFiles.getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                if (videoLink != null && !videoLink.isEmpty()) {
                    String link = "";
                    if (videoLink.contains("~~")) {
                        link = videoLink.split("~~")[(videoLink.split("~~").length) - 1];
                    } else {
                        link = videoLink;
                    }
                    initializePlayer(link);
                }
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
            String link = "";
            if (videoLink.contains("~~")) {
                link = videoLink.split("~~")[(videoLink.split("~~").length) - 1];
            } else {
                link = videoLink;
            }
            initializePlayer(link);
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
                .url(AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+audioObj.getArenaId())
                .build();

        utils.showLog(TAG, AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+audioObj.getArenaId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(ArenaAudioDisplay.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArenaAudioDisplay.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(ArenaAudioDisplay.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaAudioDisplay.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
            jsonObject.put("arenaId",audioObj.getArenaId()+"");
            jsonObject.put("arenaStatus","1");
            jsonObject.put("sections",sections);
            if(audioObj.getStudentId()!=null){
                jsonObject.put("createdBy",tObj.getUserId());
            }else {
                jsonObject.put("createdBy",audioObj.getStudentId()+"");
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
                        new AlertDialog.Builder(ArenaAudioDisplay.this)
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
                            new AlertDialog.Builder(ArenaAudioDisplay.this)
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
                                        new AlertDialog.Builder(ArenaAudioDisplay.this)
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
                                        new AlertDialog.Builder(ArenaAudioDisplay.this)
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