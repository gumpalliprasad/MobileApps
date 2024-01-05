package myschoolapp.com.gsnedutech.Arena;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.PostFileObject;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.AudioRecorder;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
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

public class AddArenaArticle extends AppCompatActivity {
    private static final String TAG = "SriRam -" + AddArenaArticle.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.et_desc_text)
    EditText etDescText;
    @BindView(R.id.et_title)
    EditText etTitle;

    @BindView(R.id.rv_audios)
    RecyclerView rvAudio;
    @BindView(R.id.rv_vids)
    RecyclerView rvVideos;
    @BindView(R.id.rv_docs)
    RecyclerView rvDocs;

    @BindView(R.id.rv_prev_files)
    RecyclerView rvPrevFiles;

    File cover = null;

    AmazonS3Client s3Client1;

    ArenaRecord storyObj;

    List<ArenaRecordFiles> storyFiles = new ArrayList<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    Uri picUri = null;

    String arenaId = "";

    //picker start codes
    private static int PICK_COVER_IMAGE = 1;
    private static int TAKE_COVER_IMAGE = 6;
    private static int PICK_ATTACHMENTS = 2;
    private static int PICK_AUDIO_FILES = 3;
    private static int PICK_VIDEO_FILES = 4;
    private static int REQUEST_TAKE_VIDEO = 5;

    List<File> audioFiles = new ArrayList<>();
    List<AddFileVideoAttach> videoFiles = new ArrayList<>();
    List<File> attachmentFiles = new ArrayList<>();

    AudioRecorder recorder;
    Timer mTimer = new Timer();

    File vid;

    List<PostFileObject> listFiles = new ArrayList<>();

    MediaPlayer player = new MediaPlayer();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_arena_article);
        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        }else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }


        rvAudio.setLayoutManager(new LinearLayoutManager(this));
        rvAudio.setAdapter(new AudioAdapter());

        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(new VideoAdapter());

        rvDocs.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvDocs.setAdapter(new FileAdapter());


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //posting article to draft or approval
                if (getIntent().hasExtra("storyObj")){
                    if (tvSubmit.getText().toString().equalsIgnoreCase("Submit")){
                        uploadToS3();
                    }else {
                        //if drafted
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                            submitArena(storyObj.getArenaId()+"","");
                            getSections();
                        }else {
                            getTeachers(storyObj.getArenaId() + "");
                        }
                    }
                }else {
                    if (etTitle.getText().toString().length()>0 && etDescText.getText().toString().length()>0 && cover!=null) {
                        uploadToS3();
                    }else {
                        new MyUtils().alertDialog(3, AddArenaArticle.this, "Oops!", "Please fill in the details! ",
                                "Close", "", false);
                    }
                }

//                if (etTitle.getText().toString().length()>0 && etDescText.getText().toString().length()>0  ){
//                   uploadToS3();
//                }else{
//                    new MyUtils().alertDialog(3, AddArenaArticle.this, "Oops!", "Please fill in the details! ",
//                            "Close", "", false);
//                }

            }
        });

        //cover image picker
        findViewById(R.id.ll_add_image_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AddArenaArticle.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_imgselector);
                dialog.setCancelable(true);
                DisplayMetrics metrics = new DisplayMetrics();
                AddArenaArticle.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int wwidth = metrics.widthPixels;
                dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView tvCam = dialog.findViewById(R.id.tv_cam);
                tvCam.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

//                fromGallery = false;
                        try {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                String imageFileName = "JPEG_" + timeStamp + "_";
                                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                                // Save a file: path for use with ACTION_VIEW intents
                                picUri =  Uri.fromFile(image);
                                if (image != null) {
                                    Uri photoURI = FileProvider.getUriForFile(AddArenaArticle.this,
                                            getPackageName()+".provider",
                                            image);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, TAKE_COVER_IMAGE);
                                }
                            }
                            else {
                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                File imageFile = new File(imageFilePath);
                                picUri = Uri.fromFile(imageFile); // convert path to Uri
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                startActivityForResult(takePictureIntent, TAKE_COVER_IMAGE);
                            }
                        } catch (ActivityNotFoundException | IOException anfe) {
                            //display an error message
                            String errorMessage = "Whoops - your device doesn't support capturing images!";
                            Toast.makeText(AddArenaArticle.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                fromGallery = true;
                        Intent intent_upload = new Intent();
                        intent_upload.setType("image/*");
                        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent_upload, PICK_COVER_IMAGE);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //atchment picker
        findViewById(R.id.iv_attachments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_upload = new Intent();
                intent_upload.setType("*/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,PICK_ATTACHMENTS);
            }
        });

        findViewById(R.id.iv_rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //audio recorder dialog
                showOptionDialog();
            }
        });


        findViewById(R.id.iv_vid_rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //video recorder dialog
                showVideoOptionDialog();
            }
        });

        //if drafted get details of draft and display
        if (getIntent().hasExtra("storyObj")){
            if (sh_Pref.getBoolean("teacher_loggedin", false)){
                tvSubmit.setText("Publish in Arena");
            }
            else {
                tvSubmit.setText("Send For Approval");
            }
            findViewById(R.id.ll_draft_edit).setVisibility(View.VISIBLE);
            storyObj = (ArenaRecord) getIntent().getSerializableExtra("storyObj");
            etDescText.setText(storyObj.getArenaDesc());

            findViewById(R.id.ll_add_image).setVisibility(View.GONE);
            findViewById(R.id.iv_cover).setVisibility(View.VISIBLE);

            if(storyObj.getArenaName().contains("~~")){
                String[] title = storyObj.getArenaName().split("~~");
                etTitle.setText(title[0]);


                String url = "NA";

                for (String s : title) {
                    if (s.contains("http")) {
                        url = s;
                    }
                }


                Picasso.with(AddArenaArticle.this).load(url).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(((ImageView)findViewById(R.id.iv_cover)));


            }else {
                etTitle.setText(storyObj.getArenaName());
                etTitle.setTextColor(Color.WHITE);
                ((ImageView)findViewById(R.id.iv_cover)).setImageResource(R.drawable.ic_arena_img);
            }

            etTitle.setFocusable(false);
            etTitle.setFocusable(false);
            rvPrevFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
            getArticleDetails();
        }

    }


    //return file name from uri
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }




    /* Audio Start*/

    void showOptionDialog(){
        Dialog dialog = new Dialog(AddArenaArticle.this);
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

        Dialog dialog = new Dialog(AddArenaArticle.this);
        dialog.setContentView(R.layout.dialog_audio_recorder_article);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        //Check utils for AudioRecorder class
        recorder = new AudioRecorder(AddArenaArticle.this);

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
        Dialog dialog = new Dialog(AddArenaArticle.this);
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
                                tvSubmit.setText("Submit");
                            }
                        }
                    }
//                    recorder.deleteFile();
                    Log.v("tag","size "+audioFiles.size());
                    dialog.dismiss();
                    hideKeyboard(AddArenaArticle.this);
                    if (audioFiles.size()>0){
                        findViewById(R.id.ll_audio).setVisibility(View.VISIBLE);
                        rvAudio.getAdapter().notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(AddArenaArticle.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.deleteFile();
                hideKeyboard(AddArenaArticle.this);
                dialog.dismiss();
            }
        });

    }

    /* Audio End*/


    /* Video Start*/

    private void showVideoOptionDialog() {
        Dialog dialog = new Dialog(AddArenaArticle.this);
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
                    Toast.makeText(AddArenaArticle.this,"Oops! there was a problem.",Toast.LENGTH_SHORT).show();
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

                Dialog dialog1 = new Dialog(AddArenaArticle.this);
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
                                if (getIntent().hasExtra("storyObj")){
                                    tvSubmit.setText("Submit");
                                }
                            }
                        }else {
                            Toast.makeText(AddArenaArticle.this,"Please enter all the details!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showVideoRenameDialog(Intent data){
        //renaming the video file before saving and same name will be used in s3
        Dialog dialog = new Dialog(AddArenaArticle.this);
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
                                }
                            }
                        }

                    }

                    if (videoFiles.size()>0){
                        findViewById(R.id.ll_video).setVisibility(View.VISIBLE);
                        rvVideos.getAdapter().notifyDataSetChanged();
                        if (getIntent().hasExtra("storyObj")){
                            tvSubmit.setText("Submit");
                        }
                    }

                    dialog.dismiss();
//                    vid.delete();

                    Log.v("tag","size "+videoFiles.size());
                }else {
                    Toast.makeText(AddArenaArticle.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
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


//    class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{
//
//        @NonNull
//        @Override
//        public AudioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new ViewHolder(LayoutInflater.from(AddArenaArticle.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull AudioAdapter.ViewHolder holder, int position) {
//            holder.seekBar.setEnabled(false);
//            holder.tvFileName.setText(audioFiles.get(position).getName());
//
//            //setting up media player with seekbar using timer
//            MediaPlayer mp = new MediaPlayer();
//            try {
//                mp.setDataSource(audioFiles.get(position).getAbsolutePath());
//                mp.prepare();
//                holder.tvDuration.setText(String.format("%02d:%02d:%02d", (mp.getDuration()/1000) / 3600, ((mp.getDuration()/1000) % 3600) / 60, ((mp.getDuration()/1000) % 60)));
//                holder.seekBar.setMax(mp.getDuration());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
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
//
//
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
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return audioFiles.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//
//            TextView tvFileName,tvDuration;
//            ImageView ivPlay,ivDelete;
//            SeekBar seekBar;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                tvFileName = itemView.findViewById(R.id.tv_file_name);
//                tvDuration = itemView.findViewById(R.id.tv_file_duration);
//                ivPlay = itemView.findViewById(R.id.iv_play);
//                ivDelete = itemView.findViewById(R.id.iv_delete);
//                seekBar = itemView.findViewById(R.id.seek);
//            }
//        }
//    }

    class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaArticle.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
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

                new AlertDialog.Builder(AddArenaArticle.this)
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
            return new ViewHolder(LayoutInflater.from(AddArenaArticle.this).inflate(R.layout.item_arena_videos,parent,false));
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
                            Intent intent = new Intent(AddArenaArticle.this, LocalVideoPlayer.class);
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
                            Intent intent = new Intent(AddArenaArticle.this, VideoWebViewer.class);
                            intent.putExtra("videoItem", videoFiles.get(position).getLink());
                            intent.putExtra("name",holder.tvVideoName.getText().toString());
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(AddArenaArticle.this, YoutubeActivity.class);
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

                    new AlertDialog.Builder(AddArenaArticle.this)
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

    @Override
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
                    File f = FileUtil.from(AddArenaArticle.this,uri).getAbsoluteFile();
                    audioFiles.add(f);
                    Log.v("tag",f.getName());
                    if (audioFiles.size()>0){
                        findViewById(R.id.ll_audio).setVisibility(View.VISIBLE);
                        rvAudio.getAdapter().notifyDataSetChanged();
                        if (getIntent().hasExtra("storyObj")){
                            tvSubmit.setText("Submit");
                        }
                    }
                }catch (Exception e){

                }
            }

            //handling video files
            if(requestCode == PICK_VIDEO_FILES){
                Uri uri = data.getData();
                try{
                    File f = FileUtil.from(AddArenaArticle.this,uri).getAbsoluteFile();
                    AddFileVideoAttach fNew = new AddFileVideoAttach();
                    fNew.setName(f.getName());
                    fNew.setFile(f);
                    videoFiles.add(fNew);
                    if (videoFiles.size()>0){
                        findViewById(R.id.ll_video).setVisibility(View.VISIBLE);
                        rvVideos.getAdapter().notifyDataSetChanged();
                        if (getIntent().hasExtra("storyObj")){
                            tvSubmit.setText("Submit");
                        }
                    }
                }catch (Exception e){

                }
            }

            //handling attachment files
            if(requestCode == PICK_ATTACHMENTS){
                Uri uri = data.getData();
                try{
                    File f = FileUtil.from(AddArenaArticle.this,uri).getAbsoluteFile();
                    attachmentFiles.add(f);

                    if (attachmentFiles.size()>0){
                        findViewById(R.id.ll_other).setVisibility(View.VISIBLE);
                        rvDocs.getAdapter().notifyDataSetChanged();
                        if (getIntent().hasExtra("storyObj")){
                            tvSubmit.setText("Submit");
                        }
                    }

                }catch (Exception e){

                }
            }

            //handling cover image and displaying
            if(requestCode == PICK_COVER_IMAGE){
                Uri uri = data.getData();
                try{
                    cover = FileUtil.from(AddArenaArticle.this,uri).getAbsoluteFile();

                    findViewById(R.id.ll_add_image).setVisibility(View.GONE);
                    findViewById(R.id.iv_cover).setVisibility(View.VISIBLE);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ((ImageView) findViewById(R.id.iv_cover)).setImageBitmap(bitmap);

                }catch (Exception e){

                }
            }

            if (requestCode == TAKE_COVER_IMAGE){
                try{
                    if (picUri!=null){
                        cover = FileUtil.from(AddArenaArticle.this,picUri).getAbsoluteFile();


                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                        ((ImageView) findViewById(R.id.iv_cover)).setImageBitmap(bitmap);
                        findViewById(R.id.ll_add_image).setVisibility(View.GONE);
                        findViewById(R.id.iv_cover).setVisibility(View.VISIBLE);

                    }
                    else {
                        Toast.makeText(AddArenaArticle.this,"Oops! There was a problem!",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }
            }
        }


    }

    class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaArticle.this).inflate(R.layout.item_hw_file_sub,parent,false));

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
                        Intent intent = new Intent(AddArenaArticle.this, LocalPdfViewer.class);
                        intent.putExtra("path",attachmentFiles.get(position).getAbsolutePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }else
                    if (attachmentFiles.get(position).getName().contains(".jpg") || attachmentFiles.get(position).getName().contains(".jpeg") || attachmentFiles.get(position).getName().contains(".png")) {

                        //local image
                        Intent intent = new Intent(AddArenaArticle.this, LocalImageDisplay.class);
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

                    new AlertDialog.Builder(AddArenaArticle.this)
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


    //remove all files created for uploading when page finished
    @Override
    protected void onDestroy() {

        deleteExisting();
        super.onDestroy();
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


    private static String getFileType(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return extension;
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

        for (int i=0;i<audioFiles.size();i++){
            if (audioFiles.get(i)!=null){
                File file = audioFiles.get(i);
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
                        if (cover!=null){
                            PostFileObject obj = new PostFileObject();
                            obj.setF(cover);
                            obj.setFileName(cover.getName());
                            obj.setFileType("cover");
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                obj.setFilePath("arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + cover.getName());
                            }
                            else {
                                obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + cover.getName());
                            }
                            listFiles.add(obj);
                        }

                        for (int i=0;i<audioFiles.size();i++){
                            PostFileObject obj = new PostFileObject();
                            obj.setF(audioFiles.get(i));
                            obj.setFileName(audioFiles.get(i).getName());
                            obj.setFileType("audio");
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                obj.setFilePath("arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + audioFiles.get(i).getName());
                            }
                            else {
                                obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + audioFiles.get(i).getName());
                            }
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
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                obj.setFilePath("arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + videoFiles.get(i).getName());
                            }
                            else {
                                obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + videoFiles.get(i).getName());
                            }
                            listFiles.add(obj);
                        }

                        for (int i=0;i<attachmentFiles.size();i++){
                            PostFileObject obj = new PostFileObject();
                            obj.setF(attachmentFiles.get(i));
                            obj.setFileName(attachmentFiles.get(i).getName());
                            obj.setFileType("attachments");
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                obj.setFilePath("arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + attachmentFiles.get(i).getName());
                            }
                            else {
                                obj.setFilePath("arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + attachmentFiles.get(i).getName());
                            }
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
                                        if (getIntent().hasExtra("storyObj")){
                                            insertArenaSubRecord();
                                        }else {
                                            //post arena in sql server along with s3 links
                                            postArena();
                                        }
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
            new AlertDialog.Builder(AddArenaArticle.this)
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


    void postArena(){
        JSONArray filesArray = new JSONArray();

        String coverUrl = "";

        if (listFiles.size()>0){
            for (int i=0;i<listFiles.size();i++){
                if (listFiles.get(i).getFileType().equalsIgnoreCase("cover")){
                    coverUrl = listFiles.get(i).getFilePath();
                }else{
                    JSONObject jsonObject = new JSONObject();

                    try {
                        if (listFiles.get(i).getLink().equalsIgnoreCase("")) {
                            jsonObject.put("fileType", listFiles.get(i).getFileType() + "/" + listFiles.get(i).getFilePath().split("\\.")[((listFiles.get(i).getFilePath().split("\\.")).length - 1)]);
                            jsonObject.put("filePath", s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), listFiles.get(i).getFilePath()));
                        }else {
                            jsonObject.put("fileType","video/link");
                            jsonObject.put("filePath",listFiles.get(i).getLink());
                        }
                        jsonObject.put("fileName", listFiles.get(i).getFileName());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    filesArray.put(jsonObject);
                }

            }
        }

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("branchId", tObj.getBranchId());
                postObject.put("createdBy", tObj.getUserId());
                postObject.put("userRole","T");
            }else {
                postObject.put("userRole","S");
                postObject.put("userId", sObj.getStudentId()+"");
                postObject.put("sectionId", sObj.getClassCourseSectionId()+"");
                postObject.put("branchId", sObj.getBranchId());
                postObject.put("createdBy", sObj.getStudentId());
            }
            if (coverUrl.length()>0){
                postObject.put("arenaName", etTitle.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),coverUrl));
            }else{
                postObject.put("arenaName", etTitle.getText().toString()+"~~NA");
            }
            postObject.put("arenaDesc", etDescText.getText().toString());
            postObject.put("arenaType", "General");
            postObject.put("arenaCategory", "6");

            postObject.put("questionCount", "0");
            postObject.put("color", "#ffffff");
            postObject.put("filesArray", filesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.InsertTeacherArenaRecord;
        }else {
            url = AppUrls.InsertArenaRecord;
        }

        utils.showLog(TAG,"post article obj "+postObject.toString());

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
                                String message = "Your story article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?";
                                if (sh_Pref.getBoolean("teacher_loggedin", false)){
                                    message = "Your story article was uploaded successfully and saved in drafts.\nDo you want to publish it?";
                                }
                                new AlertDialog.Builder(AddArenaArticle.this)
                                        .setTitle(getResources().getString(R.string.app_name))
                                        .setMessage(message)
                                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                try {

                                                    //arena is in drafts pick teacher to send for approval
                                                    if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                                        arenaId = json.getString("arenaId");
                                                        getSections();
//                                                        submitArena(json.getString("arenaId"),"");
                                                    }else {
                                                        getTeachers(json.getString("arenaId"));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .setPositiveButton("Later", new DialogInterface.OnClickListener() {
                                            @Override
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    private void insertArenaSubRecord() {

        JSONArray filesArray = new JSONArray();

        for (int i=0;i<listFiles.size();i++){
            JSONObject jsonObject = new JSONObject();

            try {
                if (listFiles.get(i).getLink().equalsIgnoreCase("")) {
                    jsonObject.put("fileType", listFiles.get(i).getFileType() + "/" + listFiles.get(i).getFilePath().split("\\.")[((listFiles.get(i).getFilePath().split("\\.")).length - 1)]);
                    jsonObject.put("filePath", s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), listFiles.get(i).getFilePath()));
                }else {
                    jsonObject.put("fileType","video/link");
                    jsonObject.put("filePath",listFiles.get(i).getLink());
                }
                jsonObject.put("fileName", listFiles.get(i).getFileName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            filesArray.put(jsonObject);

        }

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
            postObject.put("arenaType","General");
            postObject.put("arenaId",storyObj.getArenaId()+"");
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("createdBy", tObj.getUserId());
            }else {
                postObject.put("createdBy", sObj.getStudentId());
            }
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
                                files = root.listFiles();

                                if (files != null && files.length>0){
                                    for(File f: files){
                                        f.delete();
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(AddArenaArticle.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Your story article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?")
                                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                                                            submitArena(storyObj.getArenaId()+"","");
                                                            getSections();
                                                        }else {
                                                            getTeachers(storyObj.getArenaId()+"");
                                                        }
                                                    }
                                                })
                                                .setPositiveButton("Later", new DialogInterface.OnClickListener() {
                                                    @Override
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


    private void submitArena(String arenaId,String teacherId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("arenaId",arenaId);
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("createdBy", tObj.getUserId());
                postObject.put("arenaStatus", "1");
            }else {
                postObject.put("userId", sObj.getStudentId());
                postObject.put("createdBy", sObj.getStudentId());
                postObject.put("teacherId", teacherId);
            }
            postObject.put("arenaDraftStatus","1");
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = new AppUrls().SubmitTeacherArena;
        }else {
            url = new AppUrls().SubmitStudentArena;
        }

        utils.showLog(TAG,"url "+url);
        utils.showLog(TAG,"url obj "+postObject.toString());

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
                        new AlertDialog.Builder(AddArenaArticle.this)
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
                            new AlertDialog.Builder(AddArenaArticle.this)
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
                                    Toast.makeText(AddArenaArticle.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(AddArenaArticle.this)
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


    void getArticleDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&arenaId=" + storyObj.getArenaId()+""+ "&userId="+tObj.getUserId();
        }else {
            url = AppUrls.GetGeneralArenaDetailsById +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&arenaId=" + storyObj.getArenaId()+"";
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
                        findViewById(R.id.ll_draft_edit).setVisibility(View.VISIBLE);
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
                            findViewById(R.id.ll_draft_edit).setVisibility(View.GONE);
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

                            storyFiles.clear();
                            storyFiles.addAll(gson.fromJson(array.toString(),type));

                            if (storyFiles.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.ll_draft_edit).setVisibility(View.VISIBLE);
                                        rvPrevFiles.setAdapter(new PrevAdapter());
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.ll_draft_edit).setVisibility(View.GONE);
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.ll_draft_edit).setVisibility(View.GONE);
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


    class PrevAdapter extends RecyclerView.Adapter<PrevAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaArticle.this).inflate(R.layout.item_article_files,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvFileName.setText(storyFiles.get(position).getFileName());

            String url = "";

            if (storyFiles.get(position).getFilePath().contains("~~")){
                String[] s = storyFiles.get(position).getFilePath().split("~~");
                for (String str : s){
                    if (str.contains("https://")){
                        url = str;
                    }
                }
            }else {
                url = storyFiles.get(position).getFilePath();
            }

            switch (storyFiles.get(position).getFileType()){
                case "audio":
                case "audio/mp3":
                case "audio/wav":
                    holder.ivFile.setImageResource(R.drawable.ic_audio_note);

                    String finalUrl = url;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(AddArenaArticle.this, PlayerActivity.class);
                            intent.putExtra("url", finalUrl);
                            startActivity(intent);
                        }
                    });

                    break;
                case "video":
                case "video/mp4":
                case "video/mkv":
                case "video/flv":
                    holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
                    String finalUrl1 = url;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String[] extFinder = finalUrl1.split("\\.");

                            if (extFinder[(extFinder.length-1)].equalsIgnoreCase("webm") || extFinder[(extFinder.length-1)].equalsIgnoreCase("flv")){
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(finalUrl1));
                                startActivity(i);
                            }else {

                                Intent intent = new Intent(AddArenaArticle.this, PlayerActivity.class);
                                intent.putExtra("url", finalUrl1);
                                startActivity(intent);
                            }


                        }
                    });
                    break;
                case "video/link":
                    holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
                    String finalUrl3 = url;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (finalUrl3.contains("vimeo")){
                                Intent intent = new Intent(AddArenaArticle.this, VideoWebViewer.class);
                                intent.putExtra("videoItem", finalUrl3);
                                intent.putExtra("name",storyFiles.get(position).getFileName());
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(AddArenaArticle.this, YoutubeActivity.class);
                                String s[] = finalUrl3.split("/");
                                intent.putExtra("videoItem", s[s.length - 1]);
                                startActivity(intent);
                            }
                        }
                    });
                    break;

                case "image/jpeg":
                case "image/jpg":
                case "image/png":
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
                case "attachments/jpeg":
                case "attachments/doc":
                case "attachments/docx":
                case "attachments/xls":
                case "attachments/xlsx":
                case "attachments":
                    holder.ivFile.setImageResource(R.drawable.ic_student_sub_pdf);

                    String finalUrl2 = url;
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (storyFiles.get(position).getFilePath().contains("pdf")) {
                                //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                                Intent intent = new Intent(AddArenaArticle.this, NewPdfViewer.class);
                                intent.putExtra("url", finalUrl2);
                                startActivity(intent);
                            }else
                            if (storyFiles.get(position).getFilePath().contains("jpg") || storyFiles.get(position).getFilePath().contains("png")) {
                                Intent intent = new Intent(AddArenaArticle.this, ImageDisp.class);
                                intent.putExtra("path", finalUrl2);
                                startActivity(intent);
                            }else
                            if (storyFiles.get(position).getFilePath().contains("doc") || storyFiles.get(position).getFilePath().equalsIgnoreCase("docx") || storyFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || storyFiles.get(position).getFilePath().equalsIgnoreCase("pptx")) {
                                Intent intent = new Intent(AddArenaArticle.this, PdfWebViewer.class);
                                intent.putExtra("url",finalUrl2);
                                startActivity(intent);
                            }else{
                                Toast.makeText(AddArenaArticle.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }

            holder.cvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(AddArenaArticle.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteArenaFile(storyFiles.get(position).getArenaFileDetailId()+"",storyFiles.get(position).getFilePath());
                                }
                            })

                            .setCancelable(true)
                            .show();


                }
            });
        }

        @Override
        public int getItemCount() {
            return storyFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFile;
            TextView tvFileName;
            CardView cvDel;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                cvDel = itemView.findViewById(R.id.cv_del);

            }
        }
    }


    private void deleteArenaFile(String detailId, String filePath) {
        utils.showLoader(this);

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
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
                        new AlertDialog.Builder(AddArenaArticle.this)
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
                            new AlertDialog.Builder(AddArenaArticle.this)
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
                                    for (int i=0;i<storyFiles.size();i++){
                                        if (storyFiles.get(i).getArenaFileDetailId().equalsIgnoreCase(detailId)){
                                            storyFiles.remove(i);
                                            break;
                                        }
                                    }

                                    if (storyFiles.size()==0){
                                        findViewById(R.id.ll_draft_edit).setVisibility(View.GONE);
                                    }

                                    rvPrevFiles.getAdapter().notifyDataSetChanged();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(AddArenaArticle.this)
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
                                new AlertDialog.Builder(AddArenaArticle.this)
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


    private void getTeachers(String arenaId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
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
                        new AlertDialog.Builder(AddArenaArticle.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nStory Article saved in drafts in my stories. Can submit for approval later.")

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
                            new AlertDialog.Builder(AddArenaArticle.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nStory Article saved in drafts in my stories. Can submit for approval later.")

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
                                        Dialog dialog = new Dialog(AddArenaArticle.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(AddArenaArticle.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my stories. Can submit for approval later.")

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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(AddArenaArticle.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(AddArenaArticle.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nArticle cannot be sent for approval\nPlease contact your school for more information.")

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
                                    new AlertDialog.Builder(AddArenaArticle.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nStory Article saved in drafts in my stories. Can submit for approval later.")

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

    class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder>{

        List<ArenaTeacherList> teacherList; String arenaId;

        public TeacherAdapter(List<ArenaTeacherList> teacherList, String arenaId) {
            this.teacherList = teacherList;
            this.arenaId = arenaId;
        }

        @NonNull
        @Override
        public AddArenaArticle.TeacherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AddArenaArticle.TeacherAdapter.ViewHolder(LayoutInflater.from(AddArenaArticle.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AddArenaArticle.TeacherAdapter.ViewHolder holder, int position) {
            holder.tvName.setText(listTeachers.get(position).getUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send arena for teacher approval to the selected teacher
                    submitArena(arenaId,listTeachers.get(position).getTeacherId()+"");
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

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&instId=" + tObj.getInstId()+"")
                .build();

        utils.showLog(TAG, AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&instId=" + tObj.getInstId()+"");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(AddArenaArticle.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddArenaArticle.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(AddArenaArticle.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddArenaArticle.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString(AppConst.SCHEMA,""));
            if (storyObj==null){
                jsonObject.put("arenaId",arenaId);
            }else {
                jsonObject.put("arenaId",storyObj.getArenaId()+"");
            }
            jsonObject.put("arenaStatus","1");
            jsonObject.put("arenaDraftStatus", "1");
            jsonObject.put("sections",sections);
            jsonObject.put("createdBy",tObj.getUserId()+"");
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
                        new AlertDialog.Builder(AddArenaArticle.this)
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
                            new AlertDialog.Builder(AddArenaArticle.this)
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
                                        new AlertDialog.Builder(AddArenaArticle.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Approved successfully!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }else {
                                        new AlertDialog.Builder(AddArenaArticle.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Review successfully sent!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
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

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure you want to go back?\nAll progress will be lost.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AddArenaArticle.super.onBackPressed();
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)
                .show();


    }

}

