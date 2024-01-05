package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import androidx.annotation.Nullable;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.AddFileVideoAttach;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecordFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.LocalVideoPlayer;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
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

public class AddArenaVideoClips extends AppCompatActivity {

    private static final String TAG = AddArenaVideoClips.class.getName();

    MyUtils utils = new MyUtils();

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

    File vid;

    private static int REQUEST_ADD_COVER_CAM = 4;
    private static int REQUEST_ADD_COVER = 3;
    private static int REQUEST_TAKE_GALLERY_VIDEO = 2;
    private static int REQUEST_TAKE_VIDEO = 1;

    ArenaRecord videoObj;

    List<ArenaRecordFiles> audioFiles = new ArrayList<>();

    StudentObj sObj;
    TeacherObj tObj;
    SharedPreferences sh_Pref;
    AmazonS3Client s3Client1;
    List<String> keyName = new ArrayList<>();

    String branchId,sectionId,studentId;

    List<AddFileVideoAttach> fileList = new ArrayList<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    Uri picUri = null;

    String cover = "";
    String arenaId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_arena_video_clips);

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
                showOptionDialog();
            }
        });

        findViewById(R.id.button_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (getIntent().hasExtra("videoObj")){
                    if (((TextView)findViewById(R.id.button_upload)).getText().toString().equalsIgnoreCase("Upload")){
                        uploadToS3();
                    }else {
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                            submitArena(videoObj.getArenaId()+"","");
                            getSections();
                        }else {
                            getTeachers(videoObj.getArenaId() + "");
                        }
                    }
                }else {
                    if (etArenaTitle.getText().toString().length()>0 && etArenaDesc.getText().toString().length()>0 && fileList.size()>0 && picUri!=null) {
                        uploadToS3();
                    }else {
                        Toast.makeText(AddArenaVideoClips.this,"Please enter the title and description and also add at least 1 file!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showOptionDialog() {
        Dialog dialog = new Dialog(AddArenaVideoClips.this);
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
                    Toast.makeText(AddArenaVideoClips.this,"Oops! there was a problem.",Toast.LENGTH_SHORT).show();
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

                Dialog dialog1 = new Dialog(AddArenaVideoClips.this);
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
                            ((TextView)findViewById(R.id.button_upload)).setText("Upload");
                            dialog1.dismiss();
                            rvFiles.getAdapter().notifyDataSetChanged();



                        }else {
                            Toast.makeText(AddArenaVideoClips.this,"Please enter all the details!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_VIDEO) {

                if (resultCode == RESULT_OK) {

                    Dialog dialog = new Dialog(AddArenaVideoClips.this);
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
                            if (et.getText().toString().length() > 0) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    Uri selectedImage = data.getData();


                                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                                    cursor.moveToFirst();
                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                    String filePath = cursor.getString(columnIndex);
                                    Log.v("log", "filePath is : " + filePath);

                                    cursor.close();


                                    File newdir = getDir("vids", Context.MODE_PRIVATE);  //Don't do
                                    if (!newdir.exists())
                                        newdir.mkdirs();

                                    File f = new File(filePath);

                                    File rename = new File(newdir.getAbsolutePath() + "/" + et.getText().toString() + ".mp4");

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
                                    ((TextView) findViewById(R.id.button_upload)).setText("Upload");
                                } else {
                                    File f = new File(Environment.getExternalStorageDirectory(), "vids");


//                            File f = new File(Environment.getExternalStorageDirectory(), "vids");
                                    if (!f.exists()) {
                                        f.mkdirs();
                                    }

                                    File rename = new File(f, et.getText().toString() + ".mp4");
                                    vid.renameTo(rename);

                                    File root = new File(Environment.getExternalStorageDirectory(), "/vids");

//                            File root = new File(Environment.getExternalStorageDirectory(), "/vids");
                                    File[] files = root.listFiles();


                                    if (files != null) {
                                        for (File file : files) {
                                            int c = 0;
                                            for (int i = 0; i < fileList.size(); i++) {
                                                if (fileList.get(i).getFile() != null && fileList.get(i).getFile().equals(file)) {
                                                    c++;
                                                }
                                            }
                                            if (c == 0) {
                                                AddFileVideoAttach fNew = new AddFileVideoAttach();
                                                fNew.setFile(file);
                                                fileList.add(fNew);
                                                ((TextView) findViewById(R.id.button_upload)).setText("Upload");
                                            }
                                        }
                                    }

                                }

                                rvFiles.getAdapter().notifyDataSetChanged();

                                if (getIntent().hasExtra("videoObj")) {
                                    ((TextView) findViewById(R.id.button_upload)).setText("Upload");
                                }
                                if (fileList.size() > 0) {
                                    findViewById(R.id.tv_new_files).setVisibility(View.VISIBLE);
                                }
                                dialog.dismiss();
                            } else {
                                Toast.makeText(AddArenaVideoClips.this, "Please Enter a Name!", Toast.LENGTH_SHORT).show();
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
            } else if (requestCode == REQUEST_ADD_COVER_CAM) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(AddArenaVideoClips.this.getContentResolver(), picUri);
                    ivCover.setImageBitmap(bitmap);
                    ivCover.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_ADD_COVER) {
                picUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(AddArenaVideoClips.this.getContentResolver(), picUri);
                    ivCover.setImageBitmap(bitmap);
                    ivCover.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (resultCode == RESULT_OK) {
                    File file = null;
                    try {
                        file = FileUtil.from(AddArenaVideoClips.this, data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AddFileVideoAttach fNew = new AddFileVideoAttach();
                    fNew.setFile(file);
                    fileList.add(fNew);
                    ((TextView) findViewById(R.id.button_upload)).setText("Upload");
                    if (getIntent().hasExtra("videoObj")) {
                        ((TextView) findViewById(R.id.button_upload)).setText("Upload");
                    }

                    if (fileList.size() > 0) {
                        findViewById(R.id.tv_new_files).setVisibility(View.VISIBLE);
                    }

                    rvFiles.getAdapter().notifyDataSetChanged();

                }
            }
        }

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
        rvFiles.setAdapter(new FilesAdapter(fileList));
        rvFiles.scheduleLayoutAnimation();



        if (getIntent().hasExtra("videoObj")){
            ((TextView)findViewById(R.id.button_upload)).setText("Send For Approval");
            findViewById(R.id.ll_draft_edit).setVisibility(View.VISIBLE);
            videoObj = (ArenaRecord) getIntent().getSerializableExtra("videoObj");
            etArenaDesc.setText(videoObj.getArenaDesc());
            if (videoObj.getArenaName().contains("~~link~~")){
                String s[] = videoObj.getArenaName().split("~~link~~");
                etArenaTitle.setText(s[0]);
                Picasso.with(AddArenaVideoClips.this).load(s[1]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into((ImageView) findViewById(R.id.iv_cover));
                ivCover.setVisibility(View.VISIBLE);
            }else {
                etArenaTitle.setText(videoObj.getArenaName());
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

                    final Dialog dialog = new Dialog(AddArenaVideoClips.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_imgselector);
                    dialog.setCancelable(true);
                    DisplayMetrics metrics = new DisplayMetrics();
                    AddArenaVideoClips.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                                        Uri photoURI = FileProvider.getUriForFile(AddArenaVideoClips.this,
                                                getPackageName()+".provider",
                                                image);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, REQUEST_ADD_COVER_CAM);
                                    }
                                }
                                else {
                                    String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                    File imageFile = new File(imageFilePath);
                                    picUri = Uri.fromFile(imageFile); // convert path to Uri
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                    startActivityForResult(takePictureIntent, REQUEST_ADD_COVER_CAM);
                                }

                            } catch (ActivityNotFoundException anfe) {
                                //display an error message
                                String errorMessage = "Whoops - your device doesn't support capturing images!";
                                Toast.makeText(AddArenaVideoClips.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
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
                            startActivityForResult(intent_upload, REQUEST_ADD_COVER);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();


                }
            });
        }

    }


    class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder>{

        List<AddFileVideoAttach> files;

        public FilesAdapter(List<AddFileVideoAttach> files) {
            this.files = files;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaVideoClips.this).inflate(R.layout.item_arena_videos,parent,false));
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


                    new AlertDialog.Builder(AddArenaVideoClips.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Are you sure you want to delete?")
                            .setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    fileList.get(position).getFile().delete();
                                    fileList.remove(position);
                                    notifyDataSetChanged();
                                    if (fileList.size()==0){
                                        findViewById(R.id.tv_new_files).setVisibility(View.GONE);
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
                        Intent intent = new Intent(AddArenaVideoClips.this, LocalVideoPlayer.class);
                        intent.putExtra("path", fileList.get(position).getFile().getAbsolutePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else {
                        String link = fileList.get(position).getLink();
                        if (fileList.get(position).getLink().contains("vimeo")){
                            Intent intent = new Intent(AddArenaVideoClips.this, VideoWebViewer.class);
                            intent.putExtra("videoItem", fileList.get(position).getLink());
                            intent.putExtra("name",holder.tvVideoName.getText().toString());
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(AddArenaVideoClips.this, YoutubeActivity.class);
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



        s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                Region.getRegion(Regions.AP_SOUTH_1));

        boolean vailid = true;
        String names = "";

        for (int i=0;i<fileList.size();i++){
            if (fileList.get(i).getFile()!=null){
                File file = fileList.get(i).getFile();
                int file_size = (Integer.parseInt(String.valueOf(file.length())))/1048576;
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

                                File file = FileUtil.from(AddArenaVideoClips.this, picUri);

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

                            //check link or file
                            c++;

                            if (fileList.get(i).getFile() != null) {
                                utils.showLog(TAG, "url " + "arena/" + branchId + "/" + sectionId + "/" + studentId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName());
                                keyName.add("arena/" + branchId + "/" + sectionId + "/" + studentId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName());


                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        "arena/" + branchId + "/" + sectionId + "/" + studentId + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + fileList.get(i).getFile().getName(),
                                        fileList.get(i).getFile());//key is  URL


                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);

                                utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i)));


                            } else {
                                keyName.add("");
                            }

                        }

                        if (c == fileList.size()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (getIntent().hasExtra("videoObj")) {
                                        insertArenaSubRecord();
                                    } else {
                                        insertArenaRecord();
                                    }
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.showLog(TAG, "error " + e.getMessage());
                        utils.dismissDialog();
                    }
                }
            }).start();
        }else {
            new AlertDialog.Builder(AddArenaVideoClips.this)
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

    private void insertArenaSubRecord() {

        JSONArray filesArray = new JSONArray();

        for (int i=0;i<fileList.size();i++){

            String[] split= keyName.get(i).split("\\.");
            String ext = split[split.length-1];

            JSONObject obj = new JSONObject();
            try {
                if (fileList.get(i).getFile()!=null) {
                    obj.put("fileName",fileList.get(i).getFile().getName());
                    obj.put("fileType","video/"+ext);
                    obj.put("filePath",s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));
                }else {
                    obj.put("fileName",fileList.get(i).getName());
                    if (fileList.get(i).getLink().contains("vimeo")){
                        obj.put("fileType","vimeo");
                    }else {
                        obj.put("fileType","youtube");
                    }
                    obj.put("filePath",fileList.get(i).getLink());
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
            postObject.put("arenaId",videoObj.getArenaId()+"");
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
                                File root = null;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    root = new File(getExternalFilesDir(null) + "/vids");
                                }else {
                                    root = new File(Environment.getExternalStorageDirectory(), "/vids");
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
                                        new AlertDialog.Builder(AddArenaVideoClips.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Your video article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?")
                                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                                                            submitArena(videoObj.getArenaId()+"","");
                                                            getSections();
                                                        }else {
                                                            getTeachers(videoObj.getArenaId()+"");
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


    private void insertArenaRecord() {

        JSONArray filesArray = new JSONArray();

        for (int i=0;i<fileList.size();i++){

            String[] split= keyName.get(i).split("\\.");
            String ext = split[split.length-1];

            JSONObject obj = new JSONObject();
            try {
                if (fileList.get(i).getFile()!=null) {
                    obj.put("fileName",fileList.get(i).getFile().getName());
                    obj.put("fileType","video/"+ext);
                    obj.put("filePath",s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));
                }else {
                    obj.put("fileName",fileList.get(i).getName());
                    if (fileList.get(i).getLink().contains("vimeo")){
                        obj.put("fileType","vimeo");
                    }else {
                        obj.put("fileType","youtube");
                    }
                    obj.put("filePath",fileList.get(i).getLink());
                }
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
                postObject.put("userId", tObj.getUserId());
                postObject.put("userRole","T");
            }else {
                postObject.put("userRole","S");
                postObject.put("userId", studentId);
                postObject.put("sectionId", sectionId);
            }
            if (!cover.equalsIgnoreCase("")){
                postObject.put("arenaName", etArenaTitle.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),cover));
            }else {
                postObject.put("arenaName", etArenaTitle.getText().toString());
            }
            postObject.put("arenaDesc", etArenaDesc.getText().toString());
            postObject.put("arenaType", "General");
            postObject.put("arenaCategory", "5");
            postObject.put("branchId", branchId);
            postObject.put("createdBy", studentId);
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
                                    root = new File(getExternalFilesDir(null) + "/vids");
                                }else {
                                    root = new File(Environment.getExternalStorageDirectory(), "/vids");
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
                                        new AlertDialog.Builder(AddArenaVideoClips.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Your video article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?")
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
            url = new AppUrls().SubmitTeacherArena;
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
                        new AlertDialog.Builder(AddArenaVideoClips.this)
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
                            new AlertDialog.Builder(AddArenaVideoClips.this)
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
                                    Toast.makeText(AddArenaVideoClips.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(AddArenaVideoClips.this)
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
            return new ViewHolder(LayoutInflater.from(AddArenaVideoClips.this).inflate(R.layout.item_areticle_arena_audio,parent,false));
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

                    String[] extFinder = url.split("\\.");

                    if (audioFiles.get(position).getFileType().equalsIgnoreCase("vimeo")){
                        Intent intent = new Intent(AddArenaVideoClips.this, VideoWebViewer.class);
                        intent.putExtra("videoItem", audioFiles.get(position).getFilePath());
                        intent.putExtra("name", audioFiles.get(position).getFileName());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    }else if (audioFiles.get(position).getFileType().equalsIgnoreCase("youtube")){
                        Intent intent = new Intent(AddArenaVideoClips.this, YoutubeActivity.class);
                        intent.putExtra("videoItem", audioFiles.get(position).getFilePath());
                        intent.putExtra("name", audioFiles.get(position).getFileName());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else {
                        if (extFinder[(extFinder.length - 1)].equalsIgnoreCase("webm") || extFinder[(extFinder.length - 1)].equalsIgnoreCase("flv")) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {

                            Intent intent = new Intent(AddArenaVideoClips.this, PlayerActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    }
                }
            });

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(AddArenaVideoClips.this)
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
                        new AlertDialog.Builder(AddArenaVideoClips.this)
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
                            new AlertDialog.Builder(AddArenaVideoClips.this)
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
                                    new AlertDialog.Builder(AddArenaVideoClips.this)
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
                                new AlertDialog.Builder(AddArenaVideoClips.this)
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
                        new AlertDialog.Builder(AddArenaVideoClips.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nVideo Article saved in drafts in my video. Can submit for approval later.")

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
                            new AlertDialog.Builder(AddArenaVideoClips.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nVideo Article saved in drafts in my video. Can submit for approval later.")

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
                                        Dialog dialog = new Dialog(AddArenaVideoClips.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(AddArenaVideoClips.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my video. Can submit for approval later.")

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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(AddArenaVideoClips.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(AddArenaVideoClips.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nVideo article cannot be sent for approval\nPlease contact your school for more information.")

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
                                    new AlertDialog.Builder(AddArenaVideoClips.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nVideo Article saved in drafts in my video. Can submit for approval later.")

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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AddArenaVideoClips.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(listTeachers.get(position).getUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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



    @Override
    protected void onDestroy() {
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
                        Toast.makeText(AddArenaVideoClips.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddArenaVideoClips.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(AddArenaVideoClips.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddArenaVideoClips.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
            if (videoObj==null){
                jsonObject.put("arenaId",arenaId);
            }else {
                jsonObject.put("arenaId",videoObj.getArenaId()+"");
            }
            jsonObject.put("arenaDraftStatus", "1");
            jsonObject.put("arenaStatus","1");
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
                        new AlertDialog.Builder(AddArenaVideoClips.this)
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
                            new AlertDialog.Builder(AddArenaVideoClips.this)
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
                                        new AlertDialog.Builder(AddArenaVideoClips.this)
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
                                        new AlertDialog.Builder(AddArenaVideoClips.this)
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
                        AddArenaVideoClips.super.onBackPressed();
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