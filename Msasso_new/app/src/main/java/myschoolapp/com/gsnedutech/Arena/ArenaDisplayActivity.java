package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
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
import myschoolapp.com.gsnedutech.Util.DialogAudio;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.DialogVideo;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArenaDisplayActivity extends YouTubeBaseActivity {

    private static final String TAG = ArenaDisplayActivity.class.getName();

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

    @BindView(R.id.ll_players)
    LinearLayout llPlayers;

    TeacherObj tObj;

    ArenaRecord storyObj;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;

    StudentObj sObj;

    List<ArenaRecordFiles> arenaFiles = new ArrayList<>();

    String url = "";
    Dialog dialog;

    Bundle savedInstanceState;

    private PlayerView playerView;

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

    DialogAudio audio;
    DialogVideo video;

    WebView webView;

    private YouTubePlayerView youTubeView;
    YouTubePlayer yPlayer;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arena_display);
        ButterKnife.bind(this);

        this.savedInstanceState = savedInstanceState;

        init();
    }

    void init(){

        webView = findViewById(R.id.webView);
        youTubeView = findViewById(R.id.youtube_view);

        rvFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

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

        if (getIntent().hasExtra("storyObj")) {

            //get details of article
            storyObj = (ArenaRecord) getIntent().getSerializableExtra("storyObj");

            String[] title = storyObj.getArenaName().split("~~");

            String url = "NA";

            for (String s : title) {
                if (s.contains("http")) {
                    url = s;
                }
            }

            if (!url.equalsIgnoreCase("NA")) {
                Picasso.with(ArenaDisplayActivity.this).load(url).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
            }

            //initial like count and display
            tvTitle.setText(title[0]);
            tvDesc.setText(storyObj.getArenaDesc());
            tvLikes.setText(storyObj.getLikesCount()+"");

            //check if student liked article before or not
            if (!(storyObj.getStudentLike() == null) && !storyObj.getStudentLike().equalsIgnoreCase("0")){
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_heart_filled),null,null,null);
            }

            try {
                if (storyObj.getUserRole().equalsIgnoreCase("T")) {

                    if (getIntent().hasExtra("my")){
                        tvStudent.setText("By " + tObj.getUserName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(storyObj.getCreatedDate())));
                    }else {
                        tvStudent.setText("By " + storyObj.getUserName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(storyObj.getCreatedDate())));
                    }

                }else {
                    if (getIntent().hasExtra("my")){
                        tvStudent.setText("By " + sObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(storyObj.getCreatedDate())));
                    }else {
                        tvStudent.setText("By " + storyObj.getStudentName() + " \u2022 " + new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(storyObj.getCreatedDate())));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //onclick for like button
            findViewById(R.id.tv_likes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (storyObj.getUserId()==null || sObj.getStudentId().equalsIgnoreCase(storyObj.getUserId())){
                        Toast.makeText(ArenaDisplayActivity.this,"You cant like your own article!",Toast.LENGTH_SHORT).show();
                    }else{
                        if (storyObj.getStudentLike().equalsIgnoreCase("0")){
                            likeArticle();
                        }else {
                            Toast.makeText(ArenaDisplayActivity.this,"You have already liked the article!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

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
            jsonObject.put("arenaId", storyObj.getArenaId()+"");
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
                        Toast.makeText(ArenaDisplayActivity.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ArenaDisplayActivity.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ArenaDisplayActivity.this,"Liked Successfully.",Toast.LENGTH_SHORT).show();
                                    storyObj.setStudentLike(sObj.getStudentId());
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaDisplayActivity.this,"Oops! There was an error liking this article.",Toast.LENGTH_SHORT).show();
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
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + storyObj.getArenaId()+""+ "&userId="+tObj.getUserId();
        }else {
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + storyObj.getArenaId()+"";
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
            return new ViewHolder(LayoutInflater.from(ArenaDisplayActivity.this).inflate(R.layout.item_hw_file_sub,parent,false));
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
//                                Intent intent = new Intent(ArenaDisplayActivity.this, PlayerActivity.class);
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


                                audio = new DialogAudio(ArenaDisplayActivity.this,savedInstanceState,url,holder.tvFileName.getText().toString());
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

                                    new AlertDialog.Builder(ArenaDisplayActivity.this)
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
                                    video = new DialogVideo(ArenaDisplayActivity.this,savedInstanceState,url,"player");
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
                                new AlertDialog.Builder(ArenaDisplayActivity.this)
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
                    case "application/vnd.ms-excel":
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
                    case "application/pdf":
                    case "text/plain":

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
                                    Intent intent = new Intent(ArenaDisplayActivity.this, NewPdfViewer.class);
                                    intent.putExtra("url", finalUrl2);
                                    startActivity(intent);
                                }else
                                if (arenaFiles.get(position).getFilePath().contains("jpg") || arenaFiles.get(position).getFilePath().contains("jpeg") || arenaFiles.get(position).getFilePath().contains("png")) {
                                    Intent intent = new Intent(ArenaDisplayActivity.this, ImageDisp.class);
                                    intent.putExtra("path", finalUrl2);
                                    startActivity(intent);
                                }else
                                if (arenaFiles.get(position).getFilePath().contains("doc") || arenaFiles.get(position).getFilePath().equalsIgnoreCase("docx") || arenaFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || arenaFiles.get(position).getFilePath().equalsIgnoreCase("pptx")
                                    || arenaFiles.get(position).getFilePath().contains("xls") || arenaFiles.get(position).getFilePath().contains("xlsx") || arenaFiles.get(position).getFilePath().contains("txt")) {
                                    Intent intent = new Intent(ArenaDisplayActivity.this, PdfWebViewer.class);
                                    intent.putExtra("url",finalUrl2);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(ArenaDisplayActivity.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
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
//                                    Intent intent = new Intent(ArenaDisplayActivity.this, ImageDisp.class);
//                                    intent.putExtra("path", finalUrl3);
//                                    startActivity(intent);
//                                }
//                            }
//                        });
                        break;
                }

            }else{

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
                            Intent intent = new Intent(ArenaDisplayActivity.this, PlayerActivity.class);
                            intent.putExtra("url", finalUrl);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return arenaFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFile;
            TextView tvFileName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileName = itemView.findViewById(R.id.tv_file_name);

            }
        }
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
            Toast.makeText(ArenaDisplayActivity.this, "Not Permitted", Toast.LENGTH_LONG).show();
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
                    youTubeInitializationResult.getErrorDialog(ArenaDisplayActivity.this, RECOVERY_DIALOG_REQUEST).show();
                } else {
//            String errorMessage = String.format(
//                    getString(R.string.error_player), errorReason.toString());
                    Toast.makeText(ArenaDisplayActivity.this, "errorMessage", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+storyObj.getArenaId())
                .build();

        utils.showLog(TAG, AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+storyObj.getArenaId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(ArenaDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArenaDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(ArenaDisplayActivity.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArenaDisplayActivity.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
            jsonObject.put("arenaId",storyObj.getArenaId()+"");
            jsonObject.put("arenaStatus","1");
            jsonObject.put("sections",sections);
            if(storyObj.getStudentId()!=null){
                jsonObject.put("createdBy",tObj.getUserId());
            }else {
                jsonObject.put("createdBy",storyObj.getStudentId()+"");
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
                        new AlertDialog.Builder(ArenaDisplayActivity.this)
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
                            new AlertDialog.Builder(ArenaDisplayActivity.this)
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
                                        new AlertDialog.Builder(ArenaDisplayActivity.this)
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
                                        new AlertDialog.Builder(ArenaDisplayActivity.this)
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

        llPlayers.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}