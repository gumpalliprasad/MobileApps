package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.AudioRecorder;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddArenaAudioClips extends AppCompatActivity {

    private static final String TAG = AddArenaAudioClips.class.getName();

    @BindView(R.id.rv_files)
    RecyclerView rvFiles;

    @BindView(R.id.et_arena_title)
    EditText etArenaTitle;
    @BindView(R.id.et_arena_desc)
    EditText etArenaDesc;

    @BindView(R.id.rv_prev_files)
    RecyclerView rvPrevFiles;

    @BindView(R.id.ll_add_image_cover)
    LinearLayout llAddImageCover;

    @BindView(R.id.iv_cover)
    ImageView ivCover;

    List<AudioFileObj> fileList = new ArrayList<>();
    Timer mTimer = new Timer();
    MediaPlayer player = new MediaPlayer();
    int currentPos = -1;

    AudioRecorder recorder;

    MyUtils utils = new MyUtils();
    StudentObj sObj;
    TeacherObj tObj;
    SharedPreferences sh_Pref;
    AmazonS3Client s3Client1;

    List<String> keyName = new ArrayList<>();

    String branchId,sectionId,studentId;

    ArenaRecord audioObj;

    List<ArenaRecordFiles> audioFiles = new ArrayList<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    Uri picUri = null;

    String cover = "";

    String arenaId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_arena_audio_clips);
        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show option to record audio or upload from storage
                showOptionDialog();
            }
        });

        findViewById(R.id.button_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //upload audio article to drafts or submit them for teacher approval
                if (getIntent().hasExtra("audioObj")){
                    if (((TextView)findViewById(R.id.button_upload)).getText().toString().equalsIgnoreCase("Upload")){
                        uploadToS3();
                    }else {
                        //if drafted already
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                            submitArena(audioObj.getArenaId()+"","");
                            getSections();
                        }else {
                            getTeachers(audioObj.getArenaId() + "");

                        }
                    }
                }else {
                    if (etArenaTitle.getText().toString().length()>0 && etArenaDesc.getText().toString().length()>0 && fileList.size()>0 && picUri!=null) {
                        uploadToS3();
                    }else {
                        Toast.makeText(AddArenaAudioClips.this,"Please enter the title and description and also add atleast 1 fie!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void showOptionDialog(){
        Dialog dialog = new Dialog(AddArenaAudioClips.this);
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



    void init() {

        deleteExisting();

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            branchId = tObj.getBranchId()+"";
            sectionId = tObj.getRoleName()+"";
            studentId = tObj.getUserId()+"";
        }else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            branchId = sObj.getBranchId()+"";
            sectionId = sObj.getClassCourseSectionId()+"";
            studentId = sObj.getStudentId()+"";
        }




        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(rvFiles.getContext(), R.anim.layout_animation_fall_down);
        rvFiles.setAdapter(new FilesAdapter());
        rvFiles.scheduleLayoutAnimation();

        //if drafted article get details to display the draft
        if (getIntent().hasExtra("audioObj")){
            ((TextView)findViewById(R.id.button_upload)).setText("Send For Approval");
            findViewById(R.id.ll_draft_edit).setVisibility(View.VISIBLE);
            audioObj = (ArenaRecord) getIntent().getSerializableExtra("audioObj");
            etArenaDesc.setText(audioObj.getArenaDesc());
            if (audioObj.getArenaName().contains("~~link~~")){
                String s[] = audioObj.getArenaName().split("~~link~~");
                etArenaTitle.setText(s[0]);
                Picasso.with(AddArenaAudioClips.this).load(s[1]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into((ImageView) findViewById(R.id.iv_cover));
                ivCover.setVisibility(View.VISIBLE);
            }else {
                etArenaTitle.setText(audioObj.getArenaName());
                ivCover.setVisibility(View.VISIBLE);
                ivCover.setImageResource(R.drawable.ic_arena_img);
            }
            etArenaTitle.setFocusable(false);
            etArenaDesc.setFocusable(false);
            rvPrevFiles.setLayoutManager(new LinearLayoutManager(this));
            getArticleDetails();
        }else {
            findViewById(R.id.ll_add_image_cover).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(AddArenaAudioClips.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_imgselector);
                    dialog.setCancelable(true);
                    DisplayMetrics metrics = new DisplayMetrics();
                    AddArenaAudioClips.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                                        Uri photoURI = FileProvider.getUriForFile(AddArenaAudioClips.this,
                                                getPackageName()+".provider",
                                                image);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, 2);
                                    }
                                }
                                else {
                                    String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                    File imageFile = new File(imageFilePath);
                                    picUri = Uri.fromFile(imageFile); // convert path to Uri
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                    startActivityForResult(takePictureIntent, 2);
                                }
                            } catch (ActivityNotFoundException | IOException anfe) {
                                //display an error message
                                String errorMessage = "Whoops - your device doesn't support capturing images!";
                                Toast.makeText(AddArenaAudioClips.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                            startActivityForResult(intent_upload, 3);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }



    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){


        if(resultCode == RESULT_OK){
            if(requestCode == 1){

                //the selected audio.
                Uri uri = data.getData();
                try {
                    fileList.add(new AudioFileObj(FileUtil.from(AddArenaAudioClips.this,uri).getAbsoluteFile(),0));

                    if (getIntent().hasExtra("audioObj")){
                        ((TextView)findViewById(R.id.button_upload)).setText("Upload");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rvFiles.getAdapter().notifyDataSetChanged();
            }
            else if(requestCode==2 ){
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(AddArenaAudioClips.this.getContentResolver(), picUri);
                    ivCover.setImageBitmap(bitmap);
                    ivCover.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == 3) {
                picUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(AddArenaAudioClips.this.getContentResolver(), picUri);
                    ivCover.setImageBitmap(bitmap);
                    ivCover.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void pickAudioFile() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);
    }

    //displaying recorder screen
    private void startRecordDialog() {
        Dialog dialog = new Dialog(AddArenaAudioClips.this);
        dialog.setContentView(R.layout.dialog_audio_recorder);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        //AudioRecorder class available in utils to help audio recording and sve in storage
        recorder = new AudioRecorder(AddArenaAudioClips.this);

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
        Dialog dialog = new Dialog(AddArenaAudioClips.this);
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
                                if (getIntent().hasExtra("audioObj")){
                                    ((TextView)findViewById(R.id.button_upload)).setText("Upload");
                                }
                                fileList.add(new AudioFileObj(file, 0));
                                if (fileList.size()>0){
                                    findViewById(R.id.tv_new_files).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    rvFiles.getAdapter().notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    Toast.makeText(AddArenaAudioClips.this,"Please Enter a Name!",Toast.LENGTH_SHORT).show();
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

    private static int getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr);
    }

    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaAudioClips.this).inflate(R.layout.item_audio_file, parent, false));
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

            holder.ivDel.setVisibility(View.VISIBLE);

            holder.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = fileList.get(position).getFile();
                    if (file.delete()) {
                        fileList.remove(position);
                    }
                    if (fileList.size()==0){

                    }
                    notifyDataSetChanged();

                }
            });

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
                                fileList.get(position).setCurrentPosition(0);
                                //holder.waveView.setProgress(0);
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
                            player.setDataSource(fileList.get(position).getFile().getAbsolutePath());
                            player.prepare();
                            player.seekTo(fileList.get(position).getCurrentPosition());
                            player.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        final int duration = player.getDuration();

                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {

                                int currentPos =0;
                                if(fileList.get(position).getCurrentPosition()!=0){
                                    currentPos = fileList.get(position).getCurrentPosition();
                                }else{
                                    currentPos = player.getCurrentPosition();
                                }
                                fileList.get(position).setCurrentPosition(player.getCurrentPosition());

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

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mTimer.cancel();
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                    fileList.get(position).setCurrentPosition(0);
                    //holder.waveView.setProgress(0);
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

    void uploadToS3(){


        s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),Region.getRegion(Regions.AP_SOUTH_1));


        boolean vailid = true;
        String names = "";

        for (int i=0;i<fileList.size();i++){
            if (fileList.get(i).getFile()!=null){
                File file = fileList.get(i).getFile();
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

                        if (picUri != null) {
                            try {

                                File file = FileUtil.from(AddArenaAudioClips.this, picUri);

                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    cover = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + file.getName();
                                } else {
                                    cover = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + file.getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        cover,
                                        file);
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        for (int i = 0; i < fileList.size(); i++) {
                            c++;
                            utils.showLog(TAG, "url " + "arena/" + branchId + "/" + sectionId + "/" + studentId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName());
                            keyName.add("arena/" + branchId + "/" + sectionId + "/" + studentId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "arena/" + branchId + "/" + sectionId + "/" + studentId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName(),
                                    fileList.get(i).getFile());//key is  URL


                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i)));

                            if (c == fileList.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getIntent().hasExtra("audioObj")) {
                                            //in case of change in existing draft
                                            insertArenaSubRecord();
                                        } else {
                                            //creating the draft of the article
                                            insertArenaRecord();
                                        }
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
            new AlertDialog.Builder(AddArenaAudioClips.this)
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
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("createdBy", tObj.getUserId());
            }else {
                postObject.put("createdBy", studentId);
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
                                        new AlertDialog.Builder(AddArenaAudioClips.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Your audio article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?")
                                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
//                                                        submitArena(audioObj.getArenaId()+"");
                                                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                                            getSections();
                                                        }else {
                                                            getTeachers(audioObj.getArenaId()+"");
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

    //post the audio article as a draft
    private void insertArenaRecord() {

        JSONArray filesArray = new JSONArray();

        for (int i=0;i<fileList.size();i++){

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
            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("userRole","T");
            }else {
                postObject.put("userId", studentId);
                postObject.put("sectionId", sectionId);
                postObject.put("userRole","S");
            }
            if (!cover.equalsIgnoreCase("")){
                postObject.put("arenaName", etArenaTitle.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),cover));
            }else {
                postObject.put("arenaName", etArenaTitle.getText().toString());
            }
            postObject.put("arenaDesc", etArenaDesc.getText().toString());
            postObject.put("arenaType", "General");
            postObject.put("arenaCategory", "4");
            postObject.put("branchId", branchId);
            postObject.put("createdBy", studentId);
            postObject.put("questionCount", "0");
            postObject.put("color", "#ffffff");
            postObject.put("filesArray", filesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        String url = "";
//        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//            url = AppUrls.InsertTeacherArenaRecord;
//        }else {
//            url = AppUrls.InsertArenaRecord;
//        }

        utils.showLog(TAG,"obj "+postObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.InsertArenaRecord)
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
                                File root = new File(Environment.getExternalStorageDirectory(), "/audio");
                                File[] files = root.listFiles();

                                if (files.length>0){
                                    for(File f: files){
                                        f.delete();
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(AddArenaAudioClips.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Your audio article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?")
                                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        try {
                                                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                                                                submitArena(json.getString("arenaId"),"");
                                                                arenaId = json.getString("arenaId");
                                                                getSections();
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
                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                            new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                        Dialog dialog = new Dialog(AddArenaAudioClips.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(AddArenaAudioClips.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                    new AlertDialog.Builder(AddArenaAudioClips.this)
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

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                postObject.put("userId", tObj.getUserId());
                postObject.put("createdBy", tObj.getUserId());
                postObject.put("arenaStatus", "1");
            }else {
                postObject.put("userId", studentId);
                postObject.put("createdBy", studentId);
                postObject.put("teacherId", teacherId);
            }
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                            new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                    Toast.makeText(AddArenaAudioClips.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(AddArenaAudioClips.this)
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
            return new ViewHolder(LayoutInflater.from(AddArenaAudioClips.this).inflate(R.layout.tv_teacher_name,parent,false));
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


    //get article file details for drafted articles
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

                            audioFiles.clear();
                            audioFiles.addAll(gson.fromJson(array.toString(),type));

                            if (audioFiles.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.ll_draft_edit).setVisibility(View.VISIBLE);
                                        rvPrevFiles.setAdapter(new AudioAdapter());
                                        rvPrevFiles.scheduleLayoutAnimation();
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


    class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaAudioClips.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.seekBar.setEnabled(false);
            holder.tvFileName.setText(audioFiles.get(position).getFileName());
            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String url = "";

                    if (audioFiles.get(position).getFilePath().contains("~~")){
                        String[] s = audioFiles.get(position).getFilePath().split("~~");
                        for (String str : s){
                            if (str.contains("https://")){
                                url = str;
                            }
                        }
                    }else {
                        url = audioFiles.get(position).getFilePath();
                    }

                    Intent intent = new Intent(AddArenaAudioClips.this, PlayerActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(AddArenaAudioClips.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteArenaFile(audioFiles.get(position).getArenaFileDetailId()+"",audioFiles.get(position).getFilePath());
                                }
                            })

                            .setCancelable(true)
                            .show();

                }
            });

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
                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                            new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                    for (int i=0;i<audioFiles.size();i++){
                                        if (audioFiles.get(i).getArenaFileDetailId().equalsIgnoreCase(detailId)){
                                            audioFiles.remove(i);
                                            break;
                                        }
                                    }

                                    if (audioFiles.size()==0){
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
                                    new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                new AlertDialog.Builder(AddArenaAudioClips.this)
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

    }

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"")
                .build();

        utils.showLog(TAG, AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(AddArenaAudioClips.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddArenaAudioClips.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(AddArenaAudioClips.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddArenaAudioClips.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
            if (audioObj==null){
                jsonObject.put("arenaId",arenaId);
            }else {
                jsonObject.put("arenaId",audioObj.getArenaId()+"");
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
                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                            new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                                        new AlertDialog.Builder(AddArenaAudioClips.this)
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
                        AddArenaAudioClips.super.onBackPressed();
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