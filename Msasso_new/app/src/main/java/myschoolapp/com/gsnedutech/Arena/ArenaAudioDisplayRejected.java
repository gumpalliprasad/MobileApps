package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecordFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Models.AudioFileObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.AudioRecorder;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArenaAudioDisplayRejected extends AppCompatActivity implements PlayerControlView.VisibilityListener, PlaybackPreparer {

    private static final String TAG = ArenaAudioDisplayRejected.class.getName();

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

    @BindView(R.id.rv_new_files)
    RecyclerView rvNewFiles;
    @BindView(R.id.ll_new_files)
    LinearLayout llNewFiles;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.button_upload)
    TextView btnUpload;

    AmazonS3Client s3Client1;
    List<String> keyName = new ArrayList<>();


    int selectAudio = -1;

    ArenaRecord audioObj;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;

    StudentObj sObj;

    List<ArenaRecordFiles> arenaFiles = new ArrayList<>();
    List<ArenaTeacherList> listTeachers = new ArrayList<>();

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

    AudioRecorder recorder;
    List<AudioFileObj> fileList = new ArrayList<>();
    Timer mTimer = new Timer();
    MediaPlayer mediaPlayer = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_audio_display_rejected);
        ButterKnife.bind(this);

        init();
        setupPlayer(savedInstanceState);
    }

    void init(){

        deleteExisting();
        
        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        rvNewFiles.setLayoutManager(new LinearLayoutManager(this));
        rvNewFiles.setAdapter(new FilesAdapter());

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        audioObj = (ArenaRecord) getIntent().getSerializableExtra("audioObj");

        String[] title = audioObj.getArenaName().split("~~");

        String url = "NA";

        for (String s : title) {
            if (s.contains("http")) {
                url = s;
            }
        }

        if (!url.equalsIgnoreCase("NA")) {
            Picasso.with(ArenaAudioDisplayRejected.this).load(url).placeholder(R.drawable.ic_arena_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
            Picasso.with(ArenaAudioDisplayRejected.this).load(url).placeholder(R.drawable.ic_arena_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(((ImageView)findViewById(R.id.iv_album_cover)));

        }

        //initial like count and display
        tvTitle.setText(title[0]);
        tvDesc.setText(audioObj.getArenaDesc());
        tvComment.setText(audioObj.getTeacherReview());

        try {
            tvStudent.setText("By " + sObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(audioObj.getCreatedDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show option to record audio or upload from storage
                showOptionDialog();
            }
        });
        
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnUpload.getText().toString().equalsIgnoreCase("Upload")){
                    uploadToS3();
                }else{
                    getTeachers(audioObj.getArenaId()+"");
                }
            }
        });
        
        getArticleDetails();
        
    }

    void showOptionDialog(){
        Dialog dialog = new Dialog(ArenaAudioDisplayRejected.this);
        dialog.setContentView(R.layout.dialog_audio_record);
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

        //start recording audio
        dialog.findViewById(R.id.iv_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startRecordDialog();
            }
        });

        //pick audio file
        dialog.findViewById(R.id.ll_audio_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                pickAudioFile();
            }
        });
    }

    private void pickAudioFile() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
    }

    //displaying recorder screen
    private void startRecordDialog() {
        Dialog dialog = new Dialog(ArenaAudioDisplayRejected.this);
        dialog.setContentView(R.layout.dialog_audio_recorder);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        //AudioRecorder class available in utils to help audio recording and sve in storage
        recorder = new AudioRecorder(ArenaAudioDisplayRejected.this);

        final int[] flag = {0};

        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();
            }
        });

        ImageView ivPlayButton = dialog.findViewById(R.id.iv_record);
        TextView tvTime = dialog.findViewById(R.id.tv_timer);
        Timer t = new Timer();

        final int[] durationSeconds = {0};

        //record audio
        dialog.findViewById(R.id.iv_record).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                boolean pause = false;

                try {

                    switch (flag[0]) {
                        case 0:
                            recorder.startRecording("recording");
                            flag[0] = 1;
                            ivPlayButton.setImageResource(R.drawable.ic_pause_audio);
                            boolean finalPause = pause;
                            t.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!finalPause) {
                                                durationSeconds[0]++;
                                            }
                                            tvTime.setText(String.format("%02d:%02d:%02d", durationSeconds[0] / 3600, (durationSeconds[0] % 3600) / 60, (durationSeconds[0] % 60)));
                                        }
                                    });
                                }
                            }, 0, 1000);
                            break;
                        case 1:
                            finalPause = true;
                            recorder.pause();
                            flag[0] = 2;
                            ivPlayButton.setImageResource(R.drawable.ic_mic_small_white);
                            break;
                        case 2:
                            finalPause = false;
                            recorder.resume();
                            flag[0] = 1;
                            ivPlayButton.setImageResource(R.drawable.ic_pause_audio);
                            break;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //stop recording and display renaming option for files
        dialog.findViewById(R.id.iv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    recorder.stop();
                    t.cancel();
                    dialog.dismiss();
                    showRenameDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });

    }

    private void showRenameDialog() {
        // display renaming option to rename file before uploading to s3
        Dialog dialog = new Dialog(ArenaAudioDisplayRejected.this);
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
                    recorder.renameFile(et.getText().toString());

                    File root = null;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        root = new File(getExternalFilesDir(null) + "/audio");
                    }else {
                        root = new File(Environment.getExternalStorageDirectory(), "/audio");
                    }

//                    File root = new File(Environment.getExternalStorageDirectory(), "/audio");
                    File[] files = root.listFiles();



                    if (files!=null) {
                        for (File file : files) {
                            int c=0;
                            for (int i=0;i<fileList.size();i++) {
                                if (fileList.get(i).getFile().equals(file)){
                                    c++;
                                }
                            }
                            if (c==0){

                                fileList.add(new AudioFileObj(file, 0));
                                if (fileList.size()>0){
                                    llNewFiles.setVisibility(View.VISIBLE);
                                    btnUpload.setText("Upload");
                                }else {
                                    btnUpload.setText("Re-Submit");
                                }
                            }
                        }
                    }
                    rvFiles.getAdapter().notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    Toast.makeText(ArenaAudioDisplayRejected.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //to delete file from storage
        dialog.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.deleteFile();
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){


        if(resultCode == RESULT_OK){
            if(requestCode == 1){

                //the selected audio.
                Uri uri = data.getData();
                try {
                    fileList.add(new AudioFileObj(FileUtil.from(ArenaAudioDisplayRejected.this,uri).getAbsoluteFile(),0));
                    
                } catch (IOException e) {
                    e.printStackTrace();
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
//            else if(requestCode==2 ){
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(ArenaAudioDisplayRejected.this.getContentResolver(), picUri);
//                    ivCover.setImageBitmap(bitmap);
//                    ivCover.setVisibility(View.VISIBLE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            else if (requestCode == 3) {
//                picUri = data.getData();
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(ArenaAudioDisplayRejected.this.getContentResolver(), picUri);
//                    ivCover.setImageBitmap(bitmap);
//                    ivCover.setVisibility(View.VISIBLE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaAudioDisplayRejected.this).inflate(R.layout.item_audio_file, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            holder.tvFileName.setText(fileList.get(position).getFile().getName());

            try {

                Path file = Paths.get(fileList.get(position).getFile().getAbsolutePath());

                BasicFileAttributes attr =
                        Files.readAttributes(file, BasicFileAttributes.class);

                utils.showLog("tag","creationTime: " + attr.creationTime());
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
                holder.tvDate.setText(output.format(input.parse(attr.creationTime().toString())));


                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(fileList.get(position).getFile().getAbsolutePath());
                mp.prepare();
                holder.tvDuration.setText(String.format("%02d:%02d:%02d", (mp.getDuration()/1000) / 3600, ((mp.getDuration()/1000) % 3600) / 60, ((mp.getDuration()/1000) % 60)));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }




            //holder.waveView.setRawData(getByteArray(fileList.get(position).getFile()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //setting up the media player with seekbar using timer
                    if (mTimer == null) {
                        mTimer = new Timer();
                    }else{
                        mTimer.cancel();
                        mTimer = new Timer();
                    }

                    if(mediaPlayer == null){
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mTimer.cancel();
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                fileList.get(position).setCurrentPosition(0);
                                //holder.waveView.setProgress(0);
                            }
                        });
                    }

                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        mTimer.cancel();
                    }
                    else{
                        try {
                            mediaPlayer.setDataSource(fileList.get(position).getFile().getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.seekTo(fileList.get(position).getCurrentPosition());
                            mediaPlayer.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final int duration = mediaPlayer.getDuration();

                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {

                                int currentPos =0;
                                if(fileList.get(position).getCurrentPosition()!=0){
                                    currentPos = fileList.get(position).getCurrentPosition();
                                }else{
                                    currentPos = mediaPlayer.getCurrentPosition();
                                }
                                fileList.get(position).setCurrentPosition(mediaPlayer.getCurrentPosition());

                                float percentage =((float) currentPos/(float) duration)*100;
                                utils.showLog("tag",""+currentPos);
                                utils.showLog("tag",""+duration);
                                utils.showLog("tag",""+percentage);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //holder.waveView.setProgress((int) percentage);
                                    }
                                });

                            }
                        },0,1);

                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mTimer.cancel();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    fileList.get(position).setCurrentPosition(0);
                    //holder.waveView.setProgress(0);
                }
            });

            holder.ivDel.setVisibility(View.VISIBLE);

            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = fileList.get(position).getFile();
                    if (file.delete()) {
                        fileList.remove(position);
                    }
                    if (fileList.size()==0){
                        llNewFiles.setVisibility(View.GONE);
                        btnUpload.setText("Re-submit");
                    }
                    notifyDataSetChanged();

                }
            });

        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName,tvDuration,tvDate;
            ProgressIndicator prog;
            //AudioWaveView waveView;
            ImageView ivDel;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_recording);
//                prog = itemView.findViewById(R.id.prog);
                //waveView = itemView.findViewById(R.id.wave);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                ivDel = itemView.findViewById(R.id.iv_del);
            }
        }
    }

    //get byte array of audio file to display noise graph
    byte[] getByteArray(File f) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read;
        byte[] buff = new byte[1024];
        try {
            while (true) {

                if (!((read = in.read(buff)) > 0)) break;

                out.write(buff, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] audioBytes = out.toByteArray();

        return audioBytes;
    }

    void getArticleDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

//        String url = "";
//        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + audioObj.getArenaId()+""+ "&userId="+tObj.getUserId();
//        }else {
//            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + audioObj.getArenaId()+"";
//        }

        Request get = new Request.Builder()
                .url(AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + audioObj.getArenaId()+"")
                .build();

        utils.showLog(TAG, "url "+AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + audioObj.getArenaId()+"");

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
            return new ViewHolder(LayoutInflater.from(ArenaAudioDisplayRejected.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
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

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteArenaFile(arenaFiles.get(position).getArenaFileDetailId()+"",arenaFiles.get(position).getFilePath());
                                }
                            })

                            .setCancelable(true)
                            .show();

                }
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

    //service to delete existing files from drafted adpater
    private void deleteArenaFile(String detailId, String filePath) {
        utils.showLoader(this);

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("arenaType","General");
            postObject.put("detailId",detailId);
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
                .url(new AppUrls().DeleteArenaDetailFile)
                .post(body)
                .build();

//        s3Client1.deleteObject(new DeleteObjectRequest(sh_Pref.getString("bucket", ""), filePath));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Oops! Something went wrong.")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
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
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Oops! Something went wrong.")
                                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<arenaFiles.size();i++){
                                        if (arenaFiles.get(i).getArenaFileDetailId().equalsIgnoreCase(detailId)){
                                            arenaFiles.remove(i);
                                            break;
                                        }
                                    }

                                    if (arenaFiles.size()==0){
                                        findViewById(R.id.ll_draft_edit).setVisibility(View.GONE);
                                    }

                                    rvFiles.getAdapter().notifyDataSetChanged();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Oops! Something went wrong.")
                                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                                new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
                                        .setTitle(getResources().getString(R.string.app_name))
                                        .setMessage("Oops! Something went wrong.")
                                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        });
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
            new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
            postObject.put("arenaId",audioObj.getArenaId()+"");
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
                                        getTeachers(audioObj.getArenaId()+"");
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
                        new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                            new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                                        Dialog dialog = new Dialog(ArenaAudioDisplayRejected.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(ArenaAudioDisplayRejected.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                                    new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                        new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                            new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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
                                    Toast.makeText(ArenaAudioDisplayRejected.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArenaAudioDisplayRejected.this)
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

        public TeacherAdapter(List<ArenaTeacherList> teacherList, String arenaId, Dialog dialog) {
            this.teacherList = teacherList;
            this.arenaId = arenaId;
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaAudioDisplayRejected.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
    

    void deleteExisting(){
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
        deleteExisting();
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
    
    
}