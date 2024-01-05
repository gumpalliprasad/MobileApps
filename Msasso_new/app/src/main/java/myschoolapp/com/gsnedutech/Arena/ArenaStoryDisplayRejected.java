package myschoolapp.com.gsnedutech.Arena;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import myschoolapp.com.gsnedutech.Arena.Models.AddFileVideoAttach;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecordFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.LocalVideoPlayer;
import myschoolapp.com.gsnedutech.Models.PostFileObject;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.AudioRecorder;
import myschoolapp.com.gsnedutech.Util.DialogAudio;
import myschoolapp.com.gsnedutech.Util.DialogVideo;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.LocalImageDisplay;
import myschoolapp.com.gsnedutech.Util.LocalPdfViewer;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
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

public class ArenaStoryDisplayRejected extends YouTubeBaseActivity {
    
    private static final String TAG = ArenaStoryDisplayRejected.class.getName();

    @BindView(R.id.htab_header)
    ImageView ivHeader;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.rv_files)
    RecyclerView rvFiles;

    @BindView(R.id.ll_players)
    LinearLayout llPlayers;

    @BindView(R.id.ll_audio)
    LinearLayout llAudio;
    @BindView(R.id.ll_video)
    LinearLayout llVideo;
    @BindView(R.id.ll_other)
    LinearLayout llOther;

    @BindView(R.id.rv_audios)
    RecyclerView rvAudio;
    @BindView(R.id.rv_vids)
    RecyclerView rvVideos;
    @BindView(R.id.rv_docs)
    RecyclerView rvDocs;
    
    @BindView(R.id.button_upload)
    TextView btnUpload;
    
    SharedPreferences sh_Pref;

    StudentObj sObj;
    
    ArenaRecord storyObj;

    DialogAudio audio;
    DialogVideo video;

    WebView webView;

    MyUtils utils = new MyUtils();

    List<ArenaRecordFiles> arenaFiles = new ArrayList<>();

    String url = "";
    Dialog dialog;

    Bundle savedInstanceState;

    private YouTubePlayerView youTubeView;
    YouTubePlayer yPlayer;
    private static final int RECOVERY_DIALOG_REQUEST = 1;


    List<File> audioFiles = new ArrayList<>();
    List<AddFileVideoAttach> videoFiles = new ArrayList<>();
    List<File> attachmentFiles = new ArrayList<>();

    //picker start codes
    private static int PICK_COVER_IMAGE = 1;
    private static int TAKE_COVER_IMAGE = 6;
    private static int PICK_ATTACHMENTS = 2;
    private static int PICK_AUDIO_FILES = 3;
    private static int PICK_VIDEO_FILES = 4;
    private static int REQUEST_TAKE_VIDEO = 5;

    AudioRecorder recorder;

    Timer mTimer = new Timer();
    MediaPlayer player = new MediaPlayer();
    File vid;
    List<PostFileObject> listFiles = new ArrayList<>();

    AmazonS3Client s3Client1;
    List<String> keyName = new ArrayList<>();
    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_story_display_rejected);
        ButterKnife.bind(this);
        
        this.savedInstanceState = savedInstanceState;
        
        init();
    }
    
    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        webView = findViewById(R.id.webView);
        youTubeView = findViewById(R.id.youtube_view);

        rvFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        storyObj = (ArenaRecord) getIntent().getSerializableExtra("storyObj");

        String[] title = storyObj.getArenaName().split("~~");

        String url = "NA";

        for (String s : title) {
            if (s.contains("http")) {
                url = s;
            }
        }

        if (!url.equalsIgnoreCase("NA")) {
            Picasso.with(ArenaStoryDisplayRejected.this).load(url).placeholder(R.drawable.ic_arena_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
        }

        //initial like count and display
        tvTitle.setText(title[0]);
        tvDesc.setText(storyObj.getArenaDesc());
        tvComment.setText(storyObj.getTeacherReview());
        try {
            tvStudent.setText("By " + sObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(storyObj.getCreatedDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        llPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPlayers.setVisibility(View.GONE);
                if (webView!=null){
                    webView.loadUrl("about:blank");
                }
                if (yPlayer!=null){
                    yPlayer.pause();
                }
                youTubeView.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvAudio.setLayoutManager(new LinearLayoutManager(this));
        rvAudio.setAdapter(new AudioAdapter());

        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(new VideoAdapter());

        rvDocs.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvDocs.setAdapter(new FileAdapter());

        //attachment
        findViewById(R.id.iv_attachments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("*/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,PICK_ATTACHMENTS);
            }
        });

        //audio
        findViewById(R.id.iv_rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //audio recorder dialog
                showOptionDialog();
            }
        });

        //video
        findViewById(R.id.iv_vid_rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //video recorder dialog
                showVideoOptionDialog();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioFiles.size()>0 || videoFiles.size()>0 || attachmentFiles.size()>0){
                    uploadToS3();
                }else{
                    getTeachers(storyObj.getArenaId()+"");
                }
            }
        });
        
        getArticleDetails();
    }

    void getArticleDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        

        Request get = new Request.Builder()
                .url(AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + storyObj.getArenaId()+"")
                .build();

        utils.showLog(TAG, "url "+AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + storyObj.getArenaId()+"");

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
                                        rvFiles.setAdapter(new FilesAdapter());
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvFiles.setVisibility(View.GONE);
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvFiles.setVisibility(View.GONE);
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

    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaStoryDisplayRejected.this).inflate(R.layout.item_hw_file_sub,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvFileName.setText(arenaFiles.get(position).getFileName());


//            if (arenaFiles.get(position).getFilePath().contains("~~")){
//                String[] s = arenaFiles.get(position).getFilePath().split("~~");
//                for (String str : s){
//                    if (str.contains("https://")){
//                        url = str;
//                    }
//                }
//            }else {
//                url = arenaFiles.get(position).getFilePath();
//            }


            //OnClicks of types of files
            if (storyObj.getArenaCategory().equalsIgnoreCase("6")){
                switch (arenaFiles.get(position).getFileType()){
                    case "wav":
                    case "mp3":
                    case "audio/mpeg":
                    case "audio/mp3":
                    case "audio/wav":
                    case "audio":
                        holder.ivFile.setImageResource(R.drawable.ic_audio_note);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Intent intent = new Intent(this, PlayerActivity.class);
//                                intent.putExtra("url", finalUrl);
//                                startActivity(intent);

                                if (arenaFiles.get(position).getFilePath().contains("~~")){
                                    String[] s = arenaFiles.get(position).getFilePath().split("~~");
                                    for (String str : s){
                                        if (str.contains("https://")){
                                            url = str;
                                        }
                                    }
                                }else {
                                    url = arenaFiles.get(position).getFilePath();
                                }


                                audio = new DialogAudio(ArenaStoryDisplayRejected.this,savedInstanceState,url,holder.tvFileName.getText().toString());
                                audio.setupDialog();



                            }
                        });

                        break;

                    case "mp4":
                    case "video/mp4":
                    case "video/mkv":
                    case "mkv":
                    case "video":
                        holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (arenaFiles.get(position).getFilePath().contains("~~")){
                                    String[] s = arenaFiles.get(position).getFilePath().split("~~");
                                    for (String str : s){
                                        if (str.contains("https://")){
                                            url = str;
                                        }
                                    }
                                }else {
                                    url = arenaFiles.get(position).getFilePath();
                                }

                                String[] extFinder = url.split("\\.");

                                if (extFinder[(extFinder.length-1)].equalsIgnoreCase("webm") || extFinder[(extFinder.length-1)].equalsIgnoreCase("flv")){

                                    new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Video cant be played. Do you want to open in chrome?")
                                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(url));
                                                    startActivity(i);
                                                }
                                            })

                                            .setCancelable(true)
                                            .show();

                                }else {
                                    video = new DialogVideo(ArenaStoryDisplayRejected.this,savedInstanceState,url,"player");
                                    video.setupDialog();

                                }
                            }
                        });
                        break;
                    case "video/flv":
                    case "flv":
                    case "video/webm":
                        holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
                        if (arenaFiles.get(position).getFilePath().contains("~~")){
                            String[] s = arenaFiles.get(position).getFilePath().split("~~");
                            for (String str : s){
                                if (str.contains("https://")){
                                    url = str;
                                }
                            }
                        }else {
                            url = arenaFiles.get(position).getFilePath();
                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
                                        .setTitle(getResources().getString(R.string.app_name))
                                        .setMessage("Video cant be played. Do you want to open in chrome?")
                                        .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(url));
                                                startActivity(i);
                                            }
                                        })

                                        .setCancelable(true)
                                        .show();


                            }
                        });
                        break;
                    case "video/link":
                        holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String url="";
                                if (arenaFiles.get(position).getFilePath().contains("~~")){
                                    String[] s = arenaFiles.get(position).getFilePath().split("~~");
                                    for (String str : s){
                                        if (str.contains("https://")){
                                            url = str;
                                        }
                                    }
                                }else {
                                    url = arenaFiles.get(position).getFilePath();
                                }
//

                                if (url.contains("vimeo")){
                                    showVimeo(url);
                                }else {
                                    String[] s;

                                    if (url.contains("watch?v")) {
                                        s = url.split("=");
                                    } else {
                                        s = url.split("/");
                                    }

//                                videoLink = s[s.length - 1];

                                    showYoutube(s[s.length - 1]);
                                }
                            }
                        });
                        break;

                    case "pdf":
                    case "jpg":
                    case "png":
                    case "jpeg":
                    case "doc":
                    case "docx":
                    case "xls":
                    case "xlsx":
                    case "attachments/pdf":
                    case "attachments/jpg":
                    case "attachments/":
                    case "attachments/doc":
                    case "attachments/docx":
                    case "attachments/xls":
                    case "attachments/xlsx":
                    case "attachments/jpeg":
                    case "attachments/png":
                    case "image/jpeg":
                    case "image/jpg":
                    case "image/png":
                    case "attachments":

                        if (arenaFiles.get(position).getFilePath().contains("~~")){
                            String[] s = arenaFiles.get(position).getFilePath().split("~~");
                            for (String str : s){
                                if (str.contains("https://")){
                                    url = str;
                                }
                            }
                        }else {
                            url = arenaFiles.get(position).getFilePath();
                        }


                        holder.ivFile.setImageResource(R.drawable.ic_student_sub_pdf);

                        String finalUrl2 = url;
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (arenaFiles.get(position).getFilePath().contains("pdf")) {
                                    //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                                    Intent intent = new Intent(ArenaStoryDisplayRejected.this, NewPdfViewer.class);
                                    intent.putExtra("url", finalUrl2);
                                    startActivity(intent);
                                }else
                                if (arenaFiles.get(position).getFilePath().contains("jpg") || arenaFiles.get(position).getFilePath().contains("jpeg") || arenaFiles.get(position).getFilePath().contains("png")) {
                                    Intent intent = new Intent(ArenaStoryDisplayRejected.this, ImageDisp.class);
                                    intent.putExtra("path", finalUrl2);
                                    startActivity(intent);
                                }else
                                if (arenaFiles.get(position).getFilePath().contains("doc") || arenaFiles.get(position).getFilePath().equalsIgnoreCase("docx") || arenaFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || arenaFiles.get(position).getFilePath().equalsIgnoreCase("pptx")) {
                                    Intent intent = new Intent(ArenaStoryDisplayRejected.this, PdfWebViewer.class);
                                    intent.putExtra("url",finalUrl2);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(ArenaStoryDisplayRejected.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                    default:
//                        String finalUrl3 = url;
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (arenaFiles.get(position).getFileType().contains("image/png")){
//                                    Intent intent = new Intent(this, ImageDisp.class);
//                                    intent.putExtra("path", finalUrl3);
//                                    startActivity(intent);
//                                }
//                            }
//                        });
                        break;
                }

            }
            else{

                if(arenaFiles.get(position).getFileType().contains("video")){
                    holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
                }else {
                    holder.ivFile.setImageResource(R.drawable.ic_audio_note);
                }

                String[] extFinder = url.split("\\.");

                String finalUrl = url;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (extFinder[(extFinder.length-1)].equalsIgnoreCase("webm") || extFinder[(extFinder.length-1)].equalsIgnoreCase("flv")){
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(finalUrl));
                            startActivity(i);
                        }else {
                            Intent intent = new Intent(ArenaStoryDisplayRejected.this, PlayerActivity.class);
                            intent.putExtra("url", finalUrl);
                            startActivity(intent);
                        }
                    }
                });
            }

            holder.ivDel.setVisibility(View.VISIBLE);

            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteArenaFile(arenaFiles.get(position).getArenaFileDetailId()+"",arenaFiles.get(position).getFilePath());
                }
            });
        }

        @Override
        public int getItemCount() {
            return arenaFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFile;
            TextView tvFileName;
            ImageView ivDel;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivDel = itemView.findViewById(R.id.iv_del);
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
                        new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                            new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                                    new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                                new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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

    private void showVimeo(String url) {
        llPlayers.setVisibility(View.VISIBLE);

        youTubeView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        loadWebsite(url);
    }


    private void loadWebsite(String url) {

        webView.setWebViewClient(new Browser_home());
        WebSettings webSettings = webView.getSettings();
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(false);
        webView.setWebChromeClient(new MyChrome());


        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            Toast.makeText(ArenaStoryDisplayRejected.this, "Not Permitted", Toast.LENGTH_LONG).show();
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
            return BitmapFactory.decodeResource(getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            final Handler handler = new Handler();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    //Vimeo Functions end

    private void showYoutube(String videoLink) {

        llPlayers.setVisibility(View.VISIBLE);

        youTubeView.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);

        youTubeView.initialize(new AppUrls().YouTubeAPIKey1+new AppUrls().YouTubeAPIKey2+new AppUrls().YouTubeAPIKey3, new YouTubePlayer.OnInitializedListener() {
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
                    youTubeInitializationResult.getErrorDialog(ArenaStoryDisplayRejected.this, RECOVERY_DIALOG_REQUEST).show();
                } else {
//            String errorMessage = String.format(
//                    getString(R.string.error_player), errorReason.toString());
                    Toast.makeText(ArenaStoryDisplayRejected.this, "errorMessage", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaStoryDisplayRejected.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.seekBar.setEnabled(false);
            holder.tvFileName.setText(audioFiles.get(position).getName());

            //setting up media player with seekbar using timer
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(audioFiles.get(position).getAbsolutePath());
                mp.prepare();
                holder.tvDuration.setText(String.format("%02d:%02d:%02d", (mp.getDuration()/1000) / 3600, ((mp.getDuration()/1000) % 3600) / 60, ((mp.getDuration()/1000) % 60)));
                holder.seekBar.setMax(mp.getDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.ivDelete.setOnClickListener(v -> {

                new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage("Are you sure you want to delete?")
                        .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(audioFiles.get(position).getAbsolutePath().contains("audio")){
                                    if (audioFiles.get(position).delete()){
                                        audioFiles.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }
                                else {
                                    audioFiles.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        })

                        .setCancelable(true)
                        .show();


            });

//            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    if (mTimer == null) {
//                        mTimer = new Timer();
//                    }else{
//                        mTimer.cancel();
//                        mTimer = new Timer();
//                    }
//
//                    if(player == null){
//                        player = new MediaPlayer();
//                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                player.stop();
//                                player.reset();
//                                player.release();
//                                mTimer.cancel();
//
//                                holder.seekBar.setProgress(0);
//                            }
//                        });
//                    }else{
//                        player.stop();
//                        player.reset();
//                        player.release();
//                        player = new MediaPlayer();
//                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                player.stop();
//                                player.reset();
//                                player.release();
//                                mTimer.cancel();
//
//                                holder.seekBar.setProgress(0);
//                            }
//                        });
//                    }
//
//
//                    try {
//                        player.setDataSource(audioFiles.get(position).getAbsolutePath());
//                        player.prepare();
//                        player.start();
//                        mTimer.scheduleAtFixedRate(new TimerTask() {
//                            @Override
//                            public void run() {
//                                int currentPos = player.getCurrentPosition();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        holder.seekBar.setProgress(currentPos);
//                                    }
//                                });
//                            }
//                        },0,1000);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            holder.ivPlay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    //setting up the media player with seekbar using timer
                    if (mTimer == null) {
                        mTimer = new Timer();
                    }else{
                        mTimer.cancel();
                        mTimer = new Timer();
                    }

                    if(player == null){
                        player = new MediaPlayer();
                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mTimer.cancel();
                                player.stop();
                                player.reset();
                                player.release();
                                player = null;
//                                fileList.get(position).setCurrentPosition(0);
                                holder.seekBar.setProgress(0);
                            }
                        });
                    }

                    if (player.isPlaying()){
                        player.stop();
                        player.reset();
                        player.release();
                        player = null;
                        mTimer.cancel();
                    }
                    else{
                        try {
                            player.setDataSource(audioFiles.get(position).getAbsolutePath());
                            player.prepare();
                            player.seekTo(0);
                            player.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final int duration = player.getDuration();

                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {

                                int currentPos = player.getCurrentPosition();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.seekBar.setProgress(currentPos);
                                    }
                                });

                            }
                        },0,1);

                    }
                }
            });

            if (player!=null) {
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mTimer.cancel();
                        player.stop();
                        player.reset();
                        player.release();
                        player = null;
//                    fileList.get(position).setCurrentPosition(0);
                        holder.seekBar.setProgress(0);
                    }
                });
            }


//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    player.stop();
//                    player.reset();
//                    player.release();
//                    mTimer.cancel();
//
//                    holder.seekBar.setProgress(0);
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return audioFiles.size();
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

    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaStoryDisplayRejected.this).inflate(R.layout.item_arena_videos,parent,false));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvVideoName.setText(videoFiles.get(position).getName());
            holder.ivDel.setVisibility(View.VISIBLE);
            if (videoFiles.get(position).getLink().equalsIgnoreCase("")){
                try {

                    Path file = Paths.get(videoFiles.get(position).getFile().getAbsolutePath());

                    BasicFileAttributes attr =
                            Files.readAttributes(file, BasicFileAttributes.class);


                    utils.showLog("tag","creationTime: " + attr.creationTime());
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
                    holder.tvDate.setText(output.format(input.parse(attr.creationTime().toString())));


                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(videoFiles.get(position).getFile().getAbsolutePath());
                    mp.prepare();
                    holder.tvDuration.setText(String.format("%02d:%02d:%02d", (mp.getDuration()/1000) / 3600, ((mp.getDuration()/1000) % 3600) / 60, ((mp.getDuration()/1000) % 60)));

                    //play local videos in new activity LocalVideoPlayer
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ArenaStoryDisplayRejected.this, LocalVideoPlayer.class);
                            intent.putExtra("path",videoFiles.get(position).getFile().getAbsolutePath());
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                holder.tvVideoName.setText(videoFiles.get(position).getName());
                holder.tvDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                holder.tvDuration.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = videoFiles.get(position).getLink();
                        if (videoFiles.get(position).getLink().contains("vimeo")){
                            Intent intent = new Intent(ArenaStoryDisplayRejected.this, VideoWebViewer.class);
                            intent.putExtra("videoItem", videoFiles.get(position).getLink());
                            intent.putExtra("name",holder.tvVideoName.getText().toString());
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(ArenaStoryDisplayRejected.this, YoutubeActivity.class);
                            String s[] = videoFiles.get(position).getLink().split("/");
                            intent.putExtra("videoItem", s[s.length - 1]);
                            startActivity(intent);
                        }
                    }
                });
            }

            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (videoFiles.get(position).getFile()!=null){
                                        videoFiles.get(position).getFile().delete();
                                    }
                                    videoFiles.remove(position);
                                    notifyDataSetChanged();
                                    if (videoFiles.size()==0){
                                        findViewById(R.id.ll_video).setVisibility(View.GONE);
                                    }
                                }
                            })

                            .setCancelable(true)
                            .show();


                }
            });


        }

        @Override
        public int getItemCount() {
            return videoFiles.size();
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

    class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaStoryDisplayRejected.this).inflate(R.layout.item_hw_file_sub,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            //extension images of types of file based on extension
            holder.tvAttach.setText(attachmentFiles.get(position).getName());
            if (attachmentFiles.get(position).getName().contains(".pdf")){
                holder.ivAttach.setImageResource(R.drawable.ic_student_sub_pdf);
            }else if(attachmentFiles.get(position).getName().contains(".jpg") || attachmentFiles.get(position).getName().contains(".png") || attachmentFiles.get(position).getName().contains(".jpeg")){
                holder.ivAttach.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(attachmentFiles.get(position).getName().contains(".mp4")){
                holder.ivAttach.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivAttach.setImageResource(R.drawable.ic_student_sub_doc);
            }

            //background gradients of types of file based on extension
            if (attachmentFiles.get(position).getName().contains(".pdf")) {
                holder.tvAttach.setBackgroundResource(R.drawable.bg_grad_text_pdf);
            }else
            if (attachmentFiles.get(position).getName().contains(".mp4")) {
                holder.tvAttach.setBackgroundResource(R.drawable.bg_grad_text_mp4);
            }else
            if (attachmentFiles.get(position).getName().contains("youtube")) {
                holder.tvAttach.setBackgroundResource(R.drawable.bg_grad_text_mp4);
            }else
            if (attachmentFiles.get(position).getName().contains(".jpg") || attachmentFiles.get(position).getName().contains(".png")) {
                holder.tvAttach.setBackgroundResource(R.drawable.bg_grad_text_jpg);
            }else
            if (attachmentFiles.get(position).getName().contains(".doc") || attachmentFiles.get(position).getName().equalsIgnoreCase(".docx") || attachmentFiles.get(position).getName().equalsIgnoreCase(".ppt") || attachmentFiles.get(position).getName().equalsIgnoreCase(".pptx")) {
                holder.tvAttach.setBackgroundResource(R.drawable.bg_grad_text_others);
            }else {
                holder.tvAttach.setBackgroundResource(R.drawable.bg_grad_text_others);
            }

            holder.tvAttach.setText(attachmentFiles.get(position).getName().split("/")[attachmentFiles.get(position).getName().split("/").length-1]);

            //onclick of types of file based on extension
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (attachmentFiles.get(position).getName().contains(".pdf")) {

                        //local pdf
                        Intent intent = new Intent(ArenaStoryDisplayRejected.this, LocalPdfViewer.class);
                        intent.putExtra("path",attachmentFiles.get(position).getAbsolutePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }else
                    if (attachmentFiles.get(position).getName().contains(".jpg") || attachmentFiles.get(position).getName().contains(".jpeg") || attachmentFiles.get(position).getName().contains(".png")) {

                        //local image
                        Intent intent = new Intent(ArenaStoryDisplayRejected.this, LocalImageDisplay.class);
                        intent.putExtra("path",attachmentFiles.get(position).getAbsolutePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }else{

                        //local docs
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String mimeType= MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(attachmentFiles.get(position).getAbsolutePath()));
                        intent.setDataAndType(Uri.fromFile(attachmentFiles.get(position)), mimeType);
                        Intent intent1 = Intent.createChooser(intent, "Open With");
                        startActivity(intent1);


                    }
                }
            });

            holder.ivDel.setVisibility(View.VISIBLE);
            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    attachmentFiles.remove(position);
                                    notifyDataSetChanged();
                                    if(attachmentFiles.size()==0){
                                        findViewById(R.id.ll_other).setVisibility(View.GONE);
                                    }
                                }
                            })

                            .setCancelable(true)
                            .show();


                }
            });

        }

        @Override
        public int getItemCount() {
            return attachmentFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivAttach,ivDel;
            TextView tvAttach;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivAttach = itemView.findViewById(R.id.iv_file);
                ivDel = itemView.findViewById(R.id.iv_del);
                tvAttach = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }


    /* Audio pick Start*/

    void showOptionDialog(){
        Dialog dialog = new Dialog(ArenaStoryDisplayRejected.this);
        dialog.setContentView(R.layout.dialog_audio_record_article);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();



        //start audio recording
        dialog.findViewById(R.id.iv_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startAudioRecordDialog();
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
        startActivityForResult(intent_upload,PICK_AUDIO_FILES);
    }

    private void startAudioRecordDialog() {

        Dialog dialog = new Dialog(ArenaStoryDisplayRejected.this);
        dialog.setContentView(R.layout.dialog_audio_recorder_article);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        //Check utils for AudioRecorder class
        recorder = new AudioRecorder(ArenaStoryDisplayRejected.this);

        final int[] flag = {0};



        ImageView ivPlayButton = dialog.findViewById(R.id.iv_record);
        TextView tvTime = dialog.findViewById(R.id.tv_timer);
        Timer t = new Timer();

        final int[] durationSeconds = {0};


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
                dialog.dismiss();
                init();
            }
        });

    }

    private void showRenameDialog() {
        //renaming the file before saving and same name will be used in s3
        Dialog dialog = new Dialog(ArenaStoryDisplayRejected.this);
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
//                    File root = new File(Environment.getExternalStorageDirectory(), "/audio");
                    File root = null;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        root = new File(getExternalFilesDir(null) + "/audio");
                    }else {
                        root = new File(Environment.getExternalStorageDirectory(), "/audio");
                    }
                    File[] files = root.listFiles();

                    if (files!=null) {
                        for (File file : files) {
                            int c=0;
                            for (int i=0;i<audioFiles.size();i++) {
                                if (audioFiles.get(i).equals(file)){
                                    c++;
                                }
                            }
                            if (c==0){
                                audioFiles.add(file);
                                llAudio.setVisibility(View.VISIBLE);
                            }else {
                                llAudio.setVisibility(View.GONE);
                            }
                        }
                    }
//                    recorder.deleteFile();
                    Log.v("tag","size "+audioFiles.size());
                    dialog.dismiss();
                    if (audioFiles.size()>0){
                        findViewById(R.id.ll_audio).setVisibility(View.VISIBLE);
                        rvAudio.getAdapter().notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(ArenaStoryDisplayRejected.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.deleteFile();
                dialog.dismiss();
            }
        });

    }

    /* Audio End*/


    /* Video Start*/

    private void showVideoOptionDialog() {
        Dialog dialog = new Dialog(ArenaStoryDisplayRejected.this);
        dialog.setContentView(R.layout.dialog_video_record_article);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();


        //start video recording
        dialog.findViewById(R.id.iv_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(Environment.getExternalStorageDirectory(), "vids");
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
                    Toast.makeText(ArenaStoryDisplayRejected.this,"Oops! there was a problem.",Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }
        });

        //pick video file
        dialog.findViewById(R.id.ll_video_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO_FILES);

                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.ll_att_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Dialog dialog1 = new Dialog(ArenaStoryDisplayRejected.this);
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
                            videoFiles.add(fNew);
                            dialog1.dismiss();
                            if (videoFiles.size()>0) {
                                findViewById(R.id.ll_video).setVisibility(View.VISIBLE);
                                rvVideos.getAdapter().notifyDataSetChanged();
                                llVideo.setVisibility(View.VISIBLE);
                            }else {
                                llVideo.setVisibility(View.GONE);
                            }
                        }else {
                            Toast.makeText(ArenaStoryDisplayRejected.this,"Please enter all the details!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showVideoRenameDialog(Intent data){
        //renaming the video file before saving and same name will be used in s3
        Dialog dialog = new Dialog(ArenaStoryDisplayRejected.this);
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
                        videoFiles.add(fNew);
                        llVideo.setVisibility(View.VISIBLE);
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
                                for (int i=0;i<videoFiles.size();i++) {
                                    if (videoFiles.get(i).getFile()!=null && videoFiles.get(i).getFile().equals(file)){
                                        c++;
                                    }
                                }
                                if (c==0){
                                    AddFileVideoAttach fNew = new AddFileVideoAttach();
                                    fNew.setFile(file);
                                    videoFiles.add(fNew);
                                    llVideo.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    }

                    if (videoFiles.size()>0){

                        rvVideos.getAdapter().notifyDataSetChanged();
                        llVideo.setVisibility(View.VISIBLE);
                    }else {
                        llVideo.setVisibility(View.GONE);
                    }

                    dialog.dismiss();
//                    vid.delete();

                    Log.v("tag","size "+videoFiles.size());
                }else {
                    Toast.makeText(ArenaStoryDisplayRejected.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vid.delete();
                dialog.dismiss();
                init();
            }
        });
    }

    /*Video End*/


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_TAKE_VIDEO){
                showVideoRenameDialog(data);
            }

            //handling audio files
            if(requestCode == PICK_AUDIO_FILES){
                Uri uri = data.getData();
                try{
                    File f = FileUtil.from(ArenaStoryDisplayRejected.this,uri).getAbsoluteFile();
                    audioFiles.add(f);
                    Log.v("tag",f.getName());
                    if (audioFiles.size()>0){
                        llAudio.setVisibility(View.VISIBLE);
                        rvAudio.getAdapter().notifyDataSetChanged();

                    }else {
                        llAudio.setVisibility(View.GONE);
                    }
                }catch (Exception e){

                }
            }

            //handling video files
            if(requestCode == PICK_VIDEO_FILES){
                Uri uri = data.getData();
                try{
                    File f = FileUtil.from(ArenaStoryDisplayRejected.this,uri).getAbsoluteFile();
                    AddFileVideoAttach fNew = new AddFileVideoAttach();
                    fNew.setName(f.getName());
                    fNew.setFile(f);
                    videoFiles.add(fNew);
                    if (videoFiles.size()>0){
                        llVideo.setVisibility(View.VISIBLE);
                        rvVideos.getAdapter().notifyDataSetChanged();

                    }else {
                        llVideo.setVisibility(View.GONE);
                    }
                }catch (Exception e){

                }
            }

            //handling attachment files
            if(requestCode == PICK_ATTACHMENTS){
                Uri uri = data.getData();
                try{
                    File f = FileUtil.from(ArenaStoryDisplayRejected.this,uri).getAbsoluteFile();
                    attachmentFiles.add(f);

                    if (attachmentFiles.size()>0){
                        llOther.setVisibility(View.VISIBLE);
                        rvDocs.getAdapter().notifyDataSetChanged();

                    }else {
                        llOther.setVisibility(View.GONE);
                    }

                }catch (Exception e){

                }
            }

        }


    }

    void uploadToS3(){

        boolean vailid = true;
        String names = "";

        for (int i=0;i<videoFiles.size();i++){
            if (videoFiles.get(i).getFile()!=null){
                File file = videoFiles.get(i).getFile();
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                if (file_size>100){
                    vailid = false;
                    if (names.length()==0){
                        names = names+file.getName();
                    }else{
                        names = names+", "+file.getName();
                    }
                }
            }
        }

//        for (int i=0;i<audioFiles.size();i++){
//            if (audioFiles.get(i)!=null){
//                File file = audioFiles.get(i);
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


        if (vailid){
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

                        int c=0;

                        //add all kinds of files in one list to upload to s3 bucket
                        

                        for (int i=0;i<audioFiles.size();i++){
                            PostFileObject obj = new PostFileObject();
                            obj.setF(audioFiles.get(i));
                            obj.setFileName(audioFiles.get(i).getName());
                            obj.setFileType("audio");
                            obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + audioFiles.get(i).getName());
                            listFiles.add(obj);
                        }
                        for (int i=0;i<videoFiles.size();i++){
                            PostFileObject obj = new PostFileObject();
                            if (videoFiles.get(i).getLink().equalsIgnoreCase("")) {
                                obj.setF(videoFiles.get(i).getFile());
                                obj.setFileName(videoFiles.get(i).getFile().getName());
                            }else {
                                obj.setLink(videoFiles.get(i).getLink());
                                obj.setFileName(videoFiles.get(i).getName());
                            }
                            obj.setFileType("video");
                            obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + videoFiles.get(i).getName());
                            listFiles.add(obj);
                        }

                        for (int i=0;i<attachmentFiles.size();i++){
                            PostFileObject obj = new PostFileObject();
                            obj.setF(attachmentFiles.get(i));
                            obj.setFileName(attachmentFiles.get(i).getName());
                            obj.setFileType("attachments");
                            obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + attachmentFiles.get(i).getName());
                            listFiles.add(obj);
                        }



                        for (int i=0;i<listFiles.size();i++){
                            c++;
                            if (listFiles.get(i).getLink().equalsIgnoreCase("")) {
                                utils.showLog(TAG, "url " + listFiles.get(i).getFilePath());
                                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                        Region.getRegion(Regions.AP_SOUTH_1));


                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        listFiles.get(i).getFilePath(),
                                        listFiles.get(i).getF());//key is  URL


                                //making the object Public
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);

                                utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), listFiles.get(i).getFilePath()));
                            }

                            if (c==listFiles.size()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();
                                        insertArenaSubRecord();
                                    }
                                });
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.showLog(TAG,"error "+e.getMessage());
                        utils.dismissDialog();
                    }
                }
            }).start();
        }else {
            new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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


        for (int i=0;i<listFiles.size();i++){

            JSONObject obj = new JSONObject();
            try {
                obj.put("fileName",listFiles.get(i).getFileName());
                if (listFiles.get(i).getLink()!=null){
                    obj.put("fileType",listFiles.get(i).getFileType()+"/link");
                    obj.put("filePath",listFiles.get(i).getLink());
                }else {
                    String[] split= keyName.get(i).split("\\.");
                    String ext = split[split.length-1];
                    obj.put("fileType",listFiles.get(i).getFileType()+"/"+ext);
                    obj.put("filePath", s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            filesArray.put(obj);
        }

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("arenaType","General");
            postObject.put("arenaId",storyObj.getArenaId()+"");
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
                                        getTeachers(storyObj.getArenaId()+"");
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
                        new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                            new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                                        Dialog dialog = new Dialog(ArenaStoryDisplayRejected.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(ArenaStoryDisplayRejected.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                                    new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                        new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                            new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
                                    Toast.makeText(ArenaStoryDisplayRejected.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArenaStoryDisplayRejected.this)
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArenaStoryDisplayRejected.this).inflate(R.layout.tv_teacher_name,parent,false));
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root = new File(getExternalFilesDir(null) + "/vids");
        }else {
            root = new File(Environment.getExternalStorageDirectory(), "/vids");
        }
//        root = new File(Environment.getExternalStorageDirectory(), "/vids");
        files = root.listFiles();

        if (files != null && files.length>0){
            for(File f: files){
                f.delete();
            }
        }
    }

    @Override
    protected void onPause() {
        if (audio!=null){
            audio.close();
        }
        if (video!=null){
            video.close();
        }

        llPlayers.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (audio!=null){
            audio.close();
        }
        if (video!=null){
            video.close();
        }

        llPlayers.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (audio!=null){
            audio.close();
        }
        if (video!=null){
            video.close();
        }

        deleteExisting();

        llPlayers.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}