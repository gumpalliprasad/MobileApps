package myschoolapp.com.gsnedutech.QBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.PostFileObject;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.QBox.model.FileArray;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.QBox.model.QboxReply;
import myschoolapp.com.gsnedutech.QBox.model.Reply;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QboxQuestionDetails extends AppCompatActivity {

    private static final String TAG = QboxQuestionDetails.class.getName();

    MyUtils utils = new MyUtils();

    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE_MULTIPLE = 2;

    List<String> fileName = new ArrayList<>();
    List<Uri> attachedListFiles = new ArrayList<>();
    List<PostFileObject> listFiles = new ArrayList<>();
    List<String> keyName = new ArrayList<>();
    AmazonS3Client s3Client1;

    RecyclerView rvAssignFiles;


    @BindView(R.id.tv_student_name)
    TextView tvStudentName;
    @BindView(R.id.tv_question)
    TextView tvQuestion;
    @BindView(R.id.tv_rollnum)
    TextView tvRollNumber;
    @BindView(R.id.tv_likes)
    TextView tvLikes;
    @BindView(R.id.tv_replies)
    TextView tvReplies;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.rv_que_replies)
    RecyclerView rvQueReplies;
    @BindView(R.id.tv_noreplies)
    TextView tvNoReplies;

    @BindView(R.id.ll_pending)
    LinearLayout llPending;

    @BindView(R.id.tv_reject)
    TextView tvReject;
    @BindView(R.id.tv_approve)
    TextView tvApprove;

    @BindView(R.id.tv_que_date)
    TextView tvQueDate;
    @BindView(R.id.tv_que_time)
    TextView tvQueTime;

    @BindView(R.id.iv_student_image)
    ImageView ivStudentImage;

    @BindView(R.id.tv_subjectName)
    TextView tvSubjectName;
    @BindView(R.id.tv_attachments)
    TextView tvAttachments;


    int offset=0;
    boolean hasNextPage = false;
    int itemCount = 15;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    List<QboxReply> replyList = new ArrayList<>();
    QboxQuestion qboxQuestion;
    TeacherObj tObj;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qbox_question_details);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        offset=0;
        hasNextPage = false;

        //get quiz arenas
        fetchQuestionReplies(true);
    }

    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        qboxQuestion = (QboxQuestion) getIntent().getSerializableExtra("queObj");
        tvRollNumber.setText(""+qboxQuestion.getAdmissionNumber());
        if (!qboxQuestion.getProfilePath().equalsIgnoreCase("NA")) {
            Picasso.with(QboxQuestionDetails.this).load(qboxQuestion.getProfilePath()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivStudentImage);
        }
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            tvTitle.setText(getIntent().getStringExtra("from"));
            if (getIntent().getStringExtra("from").equalsIgnoreCase("Pending")){
                llPending.setVisibility(View.VISIBLE);
            }
            else llPending.setVisibility(View.GONE);
            if (qboxQuestion.getLikes()>0) {
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_filled,0,0,0);
            }
            else {
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_border,0,0,0);
            }

        }
        else {
            String json = sh_Pref.getString("studentObj", "");
            if (qboxQuestion.getStudentLike()>0) {
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_filled,0,0,0);
            }
            else {
                tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_border,0,0,0);
            }

        }
        try {
            Date dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(qboxQuestion.getCreatedDate());
            tvQueDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(dt));
            tvQueTime.setText(new SimpleDateFormat("HH:mm a").format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (qboxQuestion.getQboxFileCount()>0){
            tvAttachments.setVisibility(View.VISIBLE);
        }
        else tvAttachments.setVisibility(View.GONE);

        tvStudentName.setText(qboxQuestion.getStudentName());
        tvSubjectName.setText(qboxQuestion.getSubjectName());
        tvQuestion.setText(qboxQuestion.getQboxQuestion());
        tvLikes.setText(qboxQuestion.getLikes()+" Likes");
        tvReplies.setText(qboxQuestion.getReplyCount()+" Replies");

        rvQueReplies.setLayoutManager(new LinearLayoutManager(QboxQuestionDetails.this));
        rvQueReplies.setAdapter(new RepliesAdapter());

        tvReject.setOnClickListener(v -> {
            showDialogforApproveorReject("rejected");
        });

        tvApprove.setOnClickListener(v -> {
            showDialogforApproveorReject("approve");
        });

        tvAttachments.setOnClickListener(view -> {
            getFilesArray(qboxQuestion.getStuqboxId());

        });


    }

    private void getFilesArray(int stuqboxId) {
        utils.showLoader(QboxQuestionDetails.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().GetQboxFiles+"stuqboxId="+stuqboxId+"&schemaName="+sh_Pref.getString("schema",""))
                .build();

        utils.showLog(TAG, "Attendance Student request - " + new AppUrls().GetQboxFiles+"stuqboxId="+stuqboxId+"&schemaName="+sh_Pref.getString("schema",""));

        client.newCall(get).enqueue(new Callback() {
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

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("fileArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FileArray>>() {
                            }.getType();

                            List<FileArray> files = new ArrayList<>();
                            files.addAll(gson.fromJson(array.toString(),type));


                            if (files.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showBottomDialog(files);
                                    }
                                });
                            }

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

    void showBottomDialog(List<FileArray> files){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(QboxQuestionDetails.this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(R.layout.qbox_bottom_sheet_attach);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.show();

        RecyclerView rvFiles = bottomSheetDialog.findViewById(R.id.rv_files);
        rvFiles.setLayoutManager(new LinearLayoutManager(QboxQuestionDetails.this,RecyclerView.HORIZONTAL, false));
        rvFiles.setAdapter(new QAttachmentAdapter(files,bottomSheetDialog));
    }



    private void showDialogforApproveorReject(String status) {

        fileName.clear();
        listFiles.clear();
        attachedListFiles.clear();
        keyName.clear();

        final Dialog dialog = new Dialog(QboxQuestionDetails.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_qbox_rej_or_approve);
        dialog.setCancelable(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int wwidth = metrics.widthPixels;
        dialog.getWindow().setLayout((int) (wwidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvDialogTitle = dialog.findViewById(R.id.tv_dialog_title);
        ImageView ivClose = dialog.findViewById(R.id.iv_close);
        LinearLayout llJustReply = dialog.findViewById(R.id.ll_just_reply);
        LinearLayout llReplyAndPublish = dialog.findViewById(R.id.ll_reply_publish);
        LinearLayout llReject = dialog.findViewById(R.id.ll_reject);
        LinearLayout llApprove = dialog.findViewById(R.id.ll_approve);
        LinearLayout llAltApprove = dialog.findViewById(R.id.ll_alt_approve);
        EditText etReply = dialog.findViewById(R.id.et_reply);
        LinearLayout llSend = dialog.findViewById(R.id.ll_send);
        rvAssignFiles = dialog.findViewById(R.id.rv_images);
        rvAssignFiles.setLayoutManager(new LinearLayoutManager(QboxQuestionDetails.this, RecyclerView.HORIZONTAL, false));
        rvAssignFiles.setAdapter(new FileAdapter());

        if (status.equalsIgnoreCase("approve")){
            llAltApprove.setVisibility(View.GONE);
            llApprove.setVisibility(View.VISIBLE);
            tvDialogTitle.setText("Select an Option");
        }
        else {
            llAltApprove.setVisibility(View.VISIBLE);
            llApprove.setVisibility(View.GONE);
            tvDialogTitle.setText("Reject");
        }
        llJustReply.setOnClickListener(v -> {
            tvDialogTitle.setText("Just Reply");
            llAltApprove.setVisibility(View.VISIBLE);
            llApprove.setVisibility(View.GONE);
        });

        llReplyAndPublish.setOnClickListener(v -> {
            tvDialogTitle.setText("Reply and Publish");
            llAltApprove.setVisibility(View.VISIBLE);
            llApprove.setVisibility(View.GONE);
        });

        llReject.setOnClickListener(v -> {
            tvDialogTitle.setText("Reject");
            llAltApprove.setVisibility(View.VISIBLE);
            llApprove.setVisibility(View.GONE);
        });

        llSend.setOnClickListener(v -> {
            if (etReply.getText().toString().isEmpty()){
                Toast.makeText(this, "Please type your Reply", Toast.LENGTH_SHORT).show();
            }
            else {
                String tvSendStatus = "";
                boolean updateStatus = false;
                if (tvDialogTitle.getText().toString().equalsIgnoreCase("reject")) {
                    updateStatus = true;
                    tvSendStatus = "3";
                } else if (tvDialogTitle.getText().toString().equalsIgnoreCase("Reply and Publish")) {
                    updateStatus = true;
                    tvSendStatus = "2";
                }
                if (attachedListFiles.size() > 0) {
                    uploadToS3(updateStatus, etReply.getText().toString(), tvSendStatus, dialog);
                } else {
                    dialog.dismiss();
                    postTeacherAnswerwithStatus(updateStatus, etReply.getText().toString(), tvSendStatus);
                }

            }
        });

        ivClose.setOnClickListener(v -> dialog.dismiss());




        dialog.show();


    }

    void uploadToS3(boolean updateStatus, String msg, String tvSendStatus, Dialog dialog) {

        utils.showLoader(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date expiration = new Date();
                    long msec = expiration.getTime();
                    msec += 1000 * 60 * 60; // 1 hour.
                    expiration.setTime(msec);

                    int c = 0;

                    for (int i = 0; i < attachedListFiles.size(); i++) {
                        c++;
                        File file = FileUtil.from(QboxQuestionDetails.this, attachedListFiles.get(i));
//                        String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                        File f = new File(Environment.getExternalStorageState(), file.getName());
                        boolean success = file.renameTo(file);


                        if (success) {
                            utils.showLog(TAG, "url - " + "qboz/" + qboxQuestion.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "/" + f.getName());
                            keyName.add("qboz/" + qboxQuestion.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "/" + f.getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "qboz/" + qboxQuestion.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "/" + f.getName(), file);


                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i)));

                            String url = s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i));

                            PostFileObject postFileObject = new PostFileObject();
                            postFileObject.setFileName(f.getName());
                            postFileObject.setFilePath(url);
                            listFiles.add(postFileObject);
                        } else {
                            Toast.makeText(QboxQuestionDetails.this, "Rename failed!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            if (dialog!=null && dialog.isShowing())
                                dialog.dismiss();
                            postTeacherAnswerwithStatus(updateStatus, msg, tvSendStatus);

                        }
                    });
                    for (int i = 0; i < listFiles.size(); i++) {
                        Log.v(TAG, "listfile " + i + " - " + listFiles.get(i).getFileName());
                        Log.v(TAG, "listfile " + i + " - " + listFiles.get(i).getFilePath());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG, "error " + e.getMessage());
                }
            }
        }).start();

    }

    class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @NonNull
        @Override
        public FileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new FileAdapter.ViewHolder(LayoutInflater.from(QboxQuestionDetails.this).inflate(R.layout.item_upload_laout, viewGroup, false));
            } else {
                return new FileAdapter.ViewHolder(LayoutInflater.from(QboxQuestionDetails.this).inflate(R.layout.item_files, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull FileAdapter.ViewHolder viewHolder, int i) {

            if (i == 0) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fileName.size() <= 2) {
                            final Dialog dialog = new Dialog(QboxQuestionDetails.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_imgselector);
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int wwidth = metrics.widthPixels;
                            dialog.getWindow().setLayout((int) (wwidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.setCancelable(true);

                            TextView tvCam = dialog.findViewById(R.id.tv_cam);
                            tvCam.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
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
                                                Uri photoURI = FileProvider.getUriForFile(QboxQuestionDetails.this,
                                                        getPackageName()+".provider",
                                                        image);
                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                startActivityForResult(takePictureIntent, 1);
                                            }
                                        }
                                        else {
                                            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                            File imageFile = new File(imageFilePath);
                                            picUri = Uri.fromFile(imageFile); // convert path to Uri
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                            startActivityForResult(takePictureIntent, 1);
                                        }
                                    } catch (ActivityNotFoundException | IOException anfe) {
                                        //display an error message
                                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                                        Toast.makeText(QboxQuestionDetails.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });

                            TextView tvFiles = dialog.findViewById(R.id.tv_gallery);
                            tvFiles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("*/*");
                                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                        else {
                            Toast.makeText(QboxQuestionDetails.this, "Max 3 files allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                viewHolder.ivRemove.setVisibility(View.VISIBLE);


                viewHolder.tvFilename.setText(fileName.get(i - 1));
                if (fileName.get(i - 1).contains(".pdf")) {
                    viewHolder.ivFile.setImageResource(R.drawable.ic_pdf);
                } else {
                    viewHolder.ivFile.setImageResource(R.drawable.ic_img_attachment);
                }


                viewHolder.ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        attachedListFiles.remove(i - 1);
                        fileName.remove(i - 1);
                        notifyDataSetChanged();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return fileName.size() + 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFilename;
            ImageView ivRemove, ivFile;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFilename = itemView.findViewById(R.id.tv_file_name);
                ivRemove = itemView.findViewById(R.id.iv_remove);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
//                //get the Uri for the captured image

                attachedListFiles.add(picUri);

                File file = null;
                try {
                    file = FileUtil.from(QboxQuestionDetails.this, picUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileName.add(file.getName());
                rvAssignFiles.setAdapter(new FileAdapter());


            } else if (requestCode == PICK_IMAGE_MULTIPLE && null != data) {
                // Get the Image from data
                try {
                    if (data.getClipData() == null) {
                        attachedListFiles.add(data.getData());
                        Uri selectedImageURI = data.getData();
                        File file = FileUtil.from(QboxQuestionDetails.this, selectedImageURI);
                        fileName.add(file.getName());
                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            attachedListFiles.add(data.getClipData().getItemAt(index).getUri());
                            File file = FileUtil.from(QboxQuestionDetails.this, attachedListFiles.get(index));
                            fileName.add(file.getName());
                        }
                    }

                    Log.v(TAG, "mImageUri F Filemanager- " + attachedListFiles.size());
                    rvAssignFiles.setAdapter(new FileAdapter());

                } catch (Exception e) {
                    Toast.makeText(QboxQuestionDetails.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    Log.v(TAG, e + "");
                }

            } else {
                Toast.makeText(QboxQuestionDetails.this, "You haven't picked any File", Toast.LENGTH_LONG).show();
            }

        }


    }





    private void fetchQuestionReplies(boolean loader) {
        if (loader) utils.showLoader(QboxQuestionDetails.this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("stuqboxId", ""+qboxQuestion.getStuqboxId());
//            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
//                jsonObject.put("teacherId", tObj.getUserId() );
//            }
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("itemCount", ""+itemCount);
            jsonObject.put("offset", offset+"");


        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.FetchQBoxReplies,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.FetchQBoxReplies);
        utils.showLog(TAG, "body -"+ jsonObject.toString());

        apiClient.getClient().newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                isLoading = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                isLoading = false;
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject parentjObject = new JSONObject(res);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = parentjObject.getJSONArray("qboxReplies");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<QboxReply>>() {
                            }.getType();

                            if (jsonArray.length()>0) {
                                if (offset==0){
                                    replyList.clear();
                                }
                                replyList.addAll(gson.fromJson(jsonArray.toString(), type));

                                if (replyList.size()%itemCount==0){
                                    hasNextPage = true;
                                }else {
                                    hasNextPage = false;
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (replyList.size()>0){
                                            rvQueReplies.setVisibility(View.VISIBLE);
                                            tvNoReplies.setVisibility(View.GONE);
                                            rvQueReplies.getAdapter().notifyDataSetChanged();
                                        }else{
//                                            llStories.setVisibility(View.GONE);
                                            rvQueReplies.setVisibility(View.GONE);
                                            tvNoReplies.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                String message = parentjObject.getString(AppConst.MESSAGE);
                                runOnUiThread(() -> {
                                    utils.dismissDialog();
                                    MyUtils.forceLogoutUser(toEdit, QboxQuestionDetails.this, message, sh_Pref);
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (replyList.size()==0) {
//                                            llStories.setVisibility(View.GONE);
                                            rvQueReplies.setVisibility(View.GONE);
                                            tvNoReplies.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            rvQueReplies.setVisibility(View.VISIBLE);
                                            tvNoReplies.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }

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

    class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            return replyList.get(position) == null ? 1:0;
        }

        @NonNull
        @Override
        public RepliesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RepliesAdapter.ViewHolder(LayoutInflater.from(QboxQuestionDetails.this).inflate(R.layout.item_qbox_que_reply, parent, false));
        }



        @Override
        public void onBindViewHolder(@NonNull RepliesAdapter.ViewHolder holder, int position) {


            holder.tvUserName.setText(replyList.get(position).getUserName());
            holder.tvAnswer.setText(replyList.get(position).getQboxAnswer());
            if (replyList.get(position).getReplies().size()>0){
                holder.llAttachments.setVisibility(View.VISIBLE);
            }
            else holder.llAttachments.setVisibility(View.GONE);

            holder.tvAttachments.setOnClickListener(view -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(QboxQuestionDetails.this);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setContentView(R.layout.qbox_bottom_sheet_attach);
                bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                bottomSheetDialog.show();

                RecyclerView rvFiles = bottomSheetDialog.findViewById(R.id.rv_files);
                rvFiles.setLayoutManager(new LinearLayoutManager(QboxQuestionDetails.this,RecyclerView.HORIZONTAL, false));
                rvFiles.setAdapter(new AttachmentAdapter(replyList.get(position).getReplies()));

            });


            if (!isLoading && position == (replyList.size()-1)){
                isLoading = true;
//                utils.showLoader(QboxQuestionDetails.this);
                if (hasNextPage){
                    offset = offset+itemCount;
                    fetchQuestionReplies(false);
                }else {
                    utils.dismissDialog();
                }
            }

        }

        @Override
        public int getItemCount() {
            return replyList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvUserName, tvAnswer, tvAttachments;
            LinearLayout llAttachments;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvUserName = itemView.findViewById(R.id.tv_userName);
                tvAnswer = itemView.findViewById(R.id.tv_answer);
                llAttachments = itemView.findViewById(R.id.ll_attachments);
                tvAttachments = itemView.findViewById(R.id.tv_attachments);

            }
        }
    }

    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder>{

        List<Reply> replies;

        public AttachmentAdapter(List<Reply> replies) {
            this.replies = replies;
        }

        @NonNull
        @Override
        public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AttachmentAdapter.ViewHolder(LayoutInflater.from(QboxQuestionDetails.this).inflate(R.layout.item_hw_attachments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(replies.get(position).getTqboxFileName());
            if (replies.get(position).getRqboxFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_pdf);
            }
            else if(replies.get(position).getRqboxFilePath().contains(".jpg") || replies.get(position).getRqboxFilePath().contains(".png") || replies.get(position).getRqboxFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(replies.get(position).getRqboxFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (replies.get(position).getRqboxFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(QboxQuestionDetails.this, NewPdfViewer.class);
                        intent.putExtra("url", replies.get(position).getRqboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getRqboxFilePath().contains("mp4")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, PlayerActivity.class);
                        intent.putExtra("url", replies.get(position).getRqboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getRqboxFilePath().contains("youtube")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, YoutubeActivity.class);

                        String s[] = replies.get(position).getRqboxFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getRqboxFilePath().contains("jpg") || replies.get(position).getRqboxFilePath().contains("jpeg") || replies.get(position).getRqboxFilePath().contains("png")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, ImageDisp.class);
                        intent.putExtra("path", replies.get(position).getRqboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getRqboxFilePath().contains("doc") || replies.get(position).getRqboxFilePath().equalsIgnoreCase("docx") || replies.get(position).getRqboxFilePath().equalsIgnoreCase("ppt") || replies.get(position).getRqboxFilePath().equalsIgnoreCase("pptx") | replies.get(position).getRqboxFilePath().equalsIgnoreCase("xls") || replies.get(position).getRqboxFilePath().equalsIgnoreCase("xlsx")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, PdfWebViewer.class);
                        intent.putExtra("url", replies.get(position).getRqboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else{
                        Toast.makeText(QboxQuestionDetails.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return replies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivFile;
            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }


    private void postTeacherAnswerwithStatus(boolean updateStatus, String answer, String status) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray filesArray = new JSONArray();
        if (listFiles.size() > 0) {
            for (int i = 0; i < listFiles.size(); i++) {
                JSONObject jsonObject1 = new JSONObject();

                try {
                    jsonObject1.put("qboxFilePath", listFiles.get(i).getFilePath());
                    jsonObject1.put("qboxFileName", listFiles.get(i).getFileName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                filesArray.put(jsonObject1);

            }
        }

        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("qboxAnswer", answer);
            jsonObject.put("stuqboxId", qboxQuestion.getStuqboxId());
            jsonObject.put("createdBy", tObj.getUserId());
            jsonObject.put("filesArray",filesArray);

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.InsertTeacherQBoxQuesAnswer,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.InsertTeacherQBoxQuesAnswer);
        utils.showLog(TAG, "body -"+ jsonObject.toString());

        apiClient.getClient().newCall(postRequest).enqueue(new Callback() {
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
                        JSONObject parentjObject = new JSONObject(res);
                        if (response.code() == 200) {
                            runOnUiThread(() -> {
                                if (updateStatus){
                                    postUpdateStaus(status);
                                }
                                else {
                                    onBackPressed();
                                }
//                                Toast.makeText(KhubCategoryDetail.this, "Course Enrolled Successfully", Toast.LENGTH_SHORT).show();
                            });

                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, QboxQuestionDetails.this, message, sh_Pref);
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


    private void postUpdateStaus(String status) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("stuqboxId", qboxQuestion.getStuqboxId());
            jsonObject.put("qboxStatus", status);

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.UpdateqboxQuestionStatus,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.UpdateqboxQuestionStatus);
        utils.showLog(TAG, "body -"+ jsonObject.toString());

        apiClient.getClient().newCall(postRequest).enqueue(new Callback() {
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
                        JSONObject parentjObject = new JSONObject(res);
                        if (response.code() == 200) {
                            runOnUiThread(() -> {
                                onBackPressed();

//                                Toast.makeText(KhubCategoryDetail.this, "Course Enrolled Successfully", Toast.LENGTH_SHORT).show();
                            });

                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, QboxQuestionDetails.this, message, sh_Pref);
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

    class QAttachmentAdapter extends RecyclerView.Adapter<QAttachmentAdapter.ViewHolder>{

        List<FileArray> replies;
        BottomSheetDialog dialog;

        public QAttachmentAdapter(List<FileArray> replies, BottomSheetDialog bottomSheetDialog) {
            this.replies = replies;
            dialog = bottomSheetDialog;
        }

        @NonNull
        @Override
        public QAttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QAttachmentAdapter.ViewHolder(LayoutInflater.from(QboxQuestionDetails.this).inflate(R.layout.item_hw_attachments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull QAttachmentAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(replies.get(position).getQboxFileName());
            if (replies.get(position).getQboxFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_pdf);
            }
            else if(replies.get(position).getQboxFilePath().contains(".jpg") || replies.get(position).getQboxFilePath().contains(".png") ||
                    replies.get(position).getQboxFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(replies.get(position).getQboxFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog!=null && dialog.isShowing()) dialog.dismiss();
                    if (replies.get(position).getQboxFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(QboxQuestionDetails.this, NewPdfViewer.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("mp4")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, PlayerActivity.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("youtube")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, YoutubeActivity.class);

                        String s[] = replies.get(position).getQboxFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("jpg") ||
                            replies.get(position).getQboxFilePath().contains("jpeg") ||
                            replies.get(position).getQboxFilePath().contains("png")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, ImageDisp.class);
                        intent.putExtra("path", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("doc") || replies.get(position).getQboxFilePath().equalsIgnoreCase("docx") || replies.get(position).getQboxFilePath().equalsIgnoreCase("ppt")
                            || replies.get(position).getQboxFilePath().equalsIgnoreCase("pptx") | replies.get(position).getQboxFilePath().equalsIgnoreCase("xls") ||
                            replies.get(position).getQboxFilePath().equalsIgnoreCase("xlsx")) {
                        Intent intent = new Intent(QboxQuestionDetails.this, PdfWebViewer.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else{
                        Toast.makeText(QboxQuestionDetails.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return replies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivFile;
            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}