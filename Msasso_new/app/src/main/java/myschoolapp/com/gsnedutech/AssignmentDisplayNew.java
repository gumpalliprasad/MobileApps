package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.HomeWorkFilesDetail;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
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
import okhttp3.ResponseBody;

public class AssignmentDisplayNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = AssignmentDisplayNew.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    String type;
    HomeWorkDetails hwObj;
    String hwId;

    MyUtils utils = new MyUtils();

    List<Uri> listFiles = new ArrayList<>();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.rv_attachments)
    RecyclerView rvAttachments;
    @BindView(R.id.rv_submission)
    RecyclerView rvSubmission; 
    @BindView(R.id.rv_submit)
    RecyclerView rvSubmit;

    SharedPreferences sh_Pref;
//    StudentObj sObj;

    List<StudentHWFile> fileList = new ArrayList<>();

    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE_MULTIPLE = 2;

    AmazonS3Client s3Client1;
    List <String> keyName = new ArrayList<>();

    int version = 1;

    String studentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_display_new);
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
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, AssignmentDisplayNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getHomeWorkFiles();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

//        Gson gson = new Gson();
//        String json = sh_Pref.getString("studentObj", "");
//        sObj = gson.fromJson(json, StudentObj.class);


        hwId = getIntent().getStringExtra("hwId");
        type = getIntent().getStringExtra("type");
        hwObj = (HomeWorkDetails) getIntent().getSerializableExtra("hwObj");
        studentId = getIntent().getStringExtra("studentId");

        setUpView();

        if (!sh_Pref.getBoolean("student_loggedin", false)){
            findViewById(R.id.btn_submit).setVisibility(View.GONE);
            rvSubmit.setVisibility(View.GONE);
        }




    }


    void setUpView(){
        tvTitle.setText(type+"/"+hwObj.getSubjectName());

        tvType.setText(type);

        tvDesc.setText(hwObj.getHomeDetails());

        try {
            tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(hwObj.getSubmissionDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (hwObj.getFilesDetails()!=null & hwObj.getFilesDetails().size()>0){
            findViewById(R.id.ll_attachments).setVisibility(View.VISIBLE);
            rvAttachments.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
            rvAttachments.setAdapter(new AttachmentAdapter(hwObj.getFilesDetails()));
        }else{
            findViewById(R.id.ll_attachments).setVisibility(View.GONE);
        }

      


        //OG completed
        if (hwObj.getHwStatus().equalsIgnoreCase("Completed")){
            findViewById(R.id.ll_submission).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_submit).setVisibility(View.GONE);
            findViewById(R.id.cv_teacher_review).setVisibility(View.VISIBLE);
            findViewById(R.id.cv_teacher_review).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AssignmentDisplayNew.this,AssignmentTeacherReviewNew.class);
                    intent.putExtra("files",(Serializable)fileList);
                    intent.putExtra("hwObj",(Serializable)hwObj);
                    intent.putExtra("type",type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        //OG Submitted
        if (hwObj.getHwStatus().equalsIgnoreCase("Submitted")){
            findViewById(R.id.cv_teacher_review).setVisibility(View.GONE);
            findViewById(R.id.ll_submission).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_submit).setVisibility(View.GONE);
        }

        //OG Reassigned
        if (hwObj.getHwStatus().equalsIgnoreCase("Reassign")){
            findViewById(R.id.cv_teacher_review).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_submission).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
            rvSubmit.setLayoutManager(new LinearLayoutManager(AssignmentDisplayNew.this,RecyclerView.HORIZONTAL,false));
            rvSubmit.setAdapter(new ToSubmitAdapter(listFiles));

            if (sh_Pref.getBoolean("parent_loggedin", false)){
                rvSubmit.setVisibility(View.GONE);
                findViewById(R.id.btn_submit).setVisibility(View.GONE);
            }

            findViewById(R.id.cv_teacher_review).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AssignmentDisplayNew.this,AssignmentTeacherReviewNew.class);
                    intent.putExtra("files",(Serializable)fileList);
                    intent.putExtra("hwObj",(Serializable)hwObj);
                    intent.putExtra("type",type);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        //OG Assigned
        if (hwObj.getHwStatus().equalsIgnoreCase("Assigned")){
            findViewById(R.id.cv_teacher_review).setVisibility(View.GONE);
            findViewById(R.id.ll_submission).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
            rvSubmit.setLayoutManager(new LinearLayoutManager(AssignmentDisplayNew.this,RecyclerView.HORIZONTAL,false));
            rvSubmit.setAdapter(new ToSubmitAdapter(listFiles));

            if (sh_Pref.getBoolean("parent_loggedin", false)){
                rvSubmit.setVisibility(View.GONE);
                findViewById(R.id.ll_submission).setVisibility(View.GONE);
                findViewById(R.id.btn_submit).setVisibility(View.GONE);
            }
        }

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listFiles.size()>0){
                    uploadToS3();
                }
            }
        });

        version = Integer.parseInt(hwObj.getVersion());
    }


    void uploadToS3(){

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

                    for (int i=0;i<listFiles.size();i++){
                        c++;
                        File file = FileUtil.from(AssignmentDisplayNew.this, listFiles.get(i));
                        String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                        File f = new File(Environment.getExternalStorageState(),file.getName());
                        boolean success = file.renameTo(file);


                        if (success){
                            utils.showLog(TAG,"url"+"testing/HWtest/student/"+hwId+"/"+studentId+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName());
                            keyName.add("testing/HWtest/student/"+hwId+"/"+studentId+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "testing/HWtest/student/"+hwId+"/"+studentId+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName() ,
                                    file);//key is  URL



                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG,"urls - "+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                            if (c==listFiles.size()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        postToServer();
                                    }
                                });
                            }

                        }else {
                            Toast.makeText(AssignmentDisplayNew.this,"Rename failed!",Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG,"error "+e.getMessage());
                    utils.dismissDialog();
                }
            }
        }).start();


    }

    void postToServer(){


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<keyName.size();i++){
            JSONObject jObject = new JSONObject();
            try {


                jObject.put("studentHWFilePath",s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                jObject.put("version",version);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jObject);

        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hwId",hwId);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("studentId", studentId);
            jsonObject.put("hwFiles",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request post = new Request.Builder()
                .url(new AppUrls().UpdateStudentHomework)
//                .url("http://13.232.73.168:9000/updateStudentHomework")
                .post(body)
                .build();


        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                utils.dismissDialog();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String resp = responseBody.string();


                try {
                    JSONObject ParentjObject = new JSONObject(resp);

                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AssignmentDisplayNew.this,"Success!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                utils.dismissDialog();

            }
        });


    }

    void getHomeWorkFiles(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetHomeWorkFilesByStudent + "studentId=" + studentId + "&schemaName=" + sh_Pref.getString("schema", "") + "&hwId=" + hwObj.getHomeworkId())
                .build();

        utils.showLog(TAG, "url -"+ new AppUrls().GetHomeWorkFilesByStudent + "studentId=" + studentId + "&schemaName=" + sh_Pref.getString("schema", "") + "&hwId=" + hwObj.getHomeworkId());


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }else{

                        String resp = responseBody.string();
                        utils.showLog(TAG,resp);


                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            if (ParentjObject.has("studentHWFiles")) {
                                JSONArray jsonArr = ParentjObject.getJSONArray("studentHWFiles");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<StudentHWFile>>() {
                                }.getType();

                                fileList.clear();
                                fileList = gson.fromJson(jsonArr.toString(), type);
                                utils.showLog(TAG, "hwListSize - " + fileList.size());
                                if (fileList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (hwObj.getHwStatus().equalsIgnoreCase("Submitted") || hwObj.getHwStatus().equalsIgnoreCase("Completed") || hwObj.getHwStatus().equalsIgnoreCase("Reassign")){
                                                rvSubmission.setLayoutManager(new LinearLayoutManager(AssignmentDisplayNew.this,RecyclerView.HORIZONTAL,false));
                                                rvSubmission.setAdapter(new SubmissionFilesAdapter());
                                            }
                                        }
                                    });

                                }


                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }

                } catch (Exception e) {
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

    class SubmissionFilesAdapter extends RecyclerView.Adapter<SubmissionFilesAdapter.ViewHolder>{

        @NonNull
        @Override
        public SubmissionFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubmissionFilesAdapter.ViewHolder(LayoutInflater.from(AssignmentDisplayNew.this).inflate(R.layout.item_hw_file_sub,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubmissionFilesAdapter.ViewHolder holder, int position) {

//            holder.tvVersion.setText(fileList.get(position).getVersion()+"");
//            holder.itemView.findViewById(R.id.cv_version).setVisibility(View.VISIBLE);


            if (fileList.get(position).getEvaluated()!=0){
                holder.flincorrect.setVisibility(View.VISIBLE);
            }else{
                holder.flincorrect.setVisibility(View.GONE);
            }

            if (fileList.get(position).getStudentHWFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_pdf);
            }else if(fileList.get(position).getStudentHWFilePath().contains(".jpg") || fileList.get(position).getStudentHWFilePath().contains(".png") || fileList.get(position).getStudentHWFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(fileList.get(position).getStudentHWFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }

            if (fileList.get(position).getStudentHWFilePath().contains(".pdf")) {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_pdf);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains(".mp4")) {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains("youtube")) {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
            }else
            if (fileList.get(position).getStudentHWFilePath().contains(".jpg") || fileList.get(position).getStudentHWFilePath().contains(".png")) {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_jpg);

            }else
            if (fileList.get(position).getStudentHWFilePath().contains(".doc") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".docx") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".ppt") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".pptx")) {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
            }else {
                holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
            }

            holder.tvFileName.setText(fileList.get(position).getStudentHWFilePath().split("/")[fileList.get(position).getStudentHWFilePath().split("/").length-1]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fileList.get(position).getStudentHWFilePath().contains(".pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_pdf);
                        Intent intent = new Intent(AssignmentDisplayNew.this, NewPdfViewer.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains(".mp4")) {
                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
                        Intent intent = new Intent(AssignmentDisplayNew.this, PlayerActivity.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains("youtube")) {
                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);

                        Intent intent = new Intent(AssignmentDisplayNew.this, YoutubeActivity.class);

                        String s[] = fileList.get(position).getStudentHWFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains(".jpg") || fileList.get(position).getStudentHWFilePath().contains(".png")) {
                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_jpg);

                        Intent intent = new Intent(AssignmentDisplayNew.this, ImageDisp.class);
                        intent.putExtra("path", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else
                    if (fileList.get(position).getStudentHWFilePath().contains(".doc") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".docx") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".ppt") || fileList.get(position).getStudentHWFilePath().equalsIgnoreCase(".pptx")) {
                        holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);

                        Intent intent = new Intent(AssignmentDisplayNew.this, PdfWebViewer.class);
                        intent.putExtra("url", fileList.get(position).getStudentHWFilePath());
                        startActivity(intent);
                    }else{
                        Toast.makeText(AssignmentDisplayNew.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName,tvVersion;
            ImageView ivFile;
            FrameLayout flincorrect;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
//                tvVersion = itemView.findViewById(R.id.tv_version);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivFile = itemView.findViewById(R.id.iv_file);
                flincorrect = itemView.findViewById(R.id.fl_incorrect);
            }
        }
    }

    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder>{

        List<HomeWorkFilesDetail> filesDetails;

        public AttachmentAdapter(List<HomeWorkFilesDetail> filesDetails) {
            this.filesDetails = filesDetails;
        }

        @NonNull
        @Override
        public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentDisplayNew.this).inflate(R.layout.item_hw_attachments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(filesDetails.get(position).getFileName());
            if (filesDetails.get(position).getFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_pdf);
            }
            else if(filesDetails.get(position).getFilePath().contains(".jpg") || filesDetails.get(position).getFilePath().contains(".png") || filesDetails.get(position).getFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(filesDetails.get(position).getFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }

            
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (filesDetails.get(position).getFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(AssignmentDisplayNew.this, NewPdfViewer.class);
                        intent.putExtra("url", filesDetails.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (filesDetails.get(position).getFilePath().contains("mp4")) {
                        Intent intent = new Intent(AssignmentDisplayNew.this, PlayerActivity.class);
                        intent.putExtra("url", filesDetails.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (filesDetails.get(position).getFilePath().contains("youtube")) {
                        Intent intent = new Intent(AssignmentDisplayNew.this, YoutubeActivity.class);

                        String s[] = filesDetails.get(position).getFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                    }else
                    if (filesDetails.get(position).getFilePath().contains("jpg") || filesDetails.get(position).getFilePath().contains("png")) {
                        Intent intent = new Intent(AssignmentDisplayNew.this, ImageDisp.class);
                        intent.putExtra("path", filesDetails.get(position).getFilePath());
                        startActivity(intent);
                    }else
                    if (filesDetails.get(position).getFilePath().contains("doc") || filesDetails.get(position).getFilePath().equalsIgnoreCase("docx") || filesDetails.get(position).getFilePath().equalsIgnoreCase("ppt") || filesDetails.get(position).getFilePath().equalsIgnoreCase("pptx") | filesDetails.get(position).getFilePath().equalsIgnoreCase("xls") || filesDetails.get(position).getFilePath().equalsIgnoreCase("xlsx")) {
                        Intent intent = new Intent(AssignmentDisplayNew.this, PdfWebViewer.class);
                        intent.putExtra("url", filesDetails.get(position).getFilePath());
                        startActivity(intent);
                    }else{
                        Toast.makeText(AssignmentDisplayNew.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return filesDetails.size();
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

    public Bitmap resizeBitmap(Bitmap getBitmap, int maxSize) {
        int width = getBitmap.getWidth();
        int height = getBitmap.getHeight();
        double x;

        if (width >= height && width > maxSize) {
            x = width / height;
            width = maxSize;
            height = (int) (maxSize / x);
        } else if (height >= width && height > maxSize) {
            x = height / width;
            height = maxSize;
            width = (int) (maxSize / x);
        }
        return Bitmap.createScaledBitmap(getBitmap, width, height, false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                //get the Uri for the captured image


                listFiles.add(picUri);

                utils.showLog("picUri", picUri.toString());

            }
            else if (requestCode == PICK_IMAGE_MULTIPLE) {
                try {
                    if (data.getClipData() == null) {
                        listFiles.add(data.getData());
                        Uri selectedImageURI = data.getData();

                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            listFiles.add(data.getClipData().getItemAt(index).getUri());
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(AssignmentDisplayNew.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }
            }

            if (listFiles.size()>0){
               rvSubmit.setAdapter(new ToSubmitAdapter(listFiles));
            }

        }


    }
    
    
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }


    class ToSubmitAdapter extends RecyclerView.Adapter<ToSubmitAdapter.ViewHolder>{
        List<Uri> listFiles;
        public ToSubmitAdapter(List<Uri> listFiles) {
            this.listFiles = listFiles;
        }

        @Override
        public int getItemViewType(int position) {
            if (position==0){
                return 0;
            }else{
                return 1;
            }
        }

        @NonNull
        @Override
        public ToSubmitAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType==0){
                return new ToSubmitAdapter.ViewHolder(LayoutInflater.from(AssignmentDisplayNew.this).inflate(R.layout.item_upload_laout,parent,false));
            }else{
                return new ToSubmitAdapter.ViewHolder(LayoutInflater.from(AssignmentDisplayNew.this).inflate(R.layout.item_hw_file_sub ,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ToSubmitAdapter.ViewHolder holder, int position) {

//            holder.tvVersion.setText(fileList.get(position).getVersion()+"");
//            holder.itemView.findViewById(R.id.cv_version).setVisibility(View.VISIBLE);

          if (position==0){
              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      final Dialog dialog = new Dialog(AssignmentDisplayNew.this);
                      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                      dialog.setContentView(R.layout.dialog_imgselector);
                      dialog.setCancelable(true);
                      DisplayMetrics metrics = new DisplayMetrics();
                      getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                                          Uri photoURI = FileProvider.getUriForFile(AssignmentDisplayNew.this,
                                                  getPackageName()+".provider",
                                                  image);
                                          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                          startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                                      }
                                  }
                                  else {
                                      String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                      File imageFile = new File(imageFilePath);
                                      picUri = Uri.fromFile(imageFile); // convert path to Uri
                                      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                      startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                                  }
                              } catch (ActivityNotFoundException | IOException anfe) {
                                  //display an error message
                                  String errorMessage = "Whoops - your device doesn't support capturing images!";
                                  Toast.makeText(AssignmentDisplayNew.this, errorMessage, Toast.LENGTH_SHORT).show();
                              }
                              dialog.dismiss();
                          }
                      });

                      TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
                      tvGallery.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
//                fromGallery = true;
                              Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                              intent.setType("image/*");
                              //intent.addCategory(Intent.CATEGORY_OPENABLE);
                              intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                              intent.setAction(Intent.ACTION_GET_CONTENT);
                              startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                              dialog.dismiss();
                              dialog.dismiss();
                          }
                      });

                      dialog.show();
                  }
              });
          }else{
              try {
                  File file = FileUtil.from(AssignmentDisplayNew.this,listFiles.get(position-1));
                  holder.tvFileName.setText(file.getName());
              } catch (IOException e) {
                  e.printStackTrace();
              }


              if (listFiles.get(position-1).toString().contains(".pdf")){
                  holder.ivFile.setImageResource(R.drawable.ic_student_sub_pdf);
              }else if(listFiles.get(position-1).toString().contains(".jpg") || listFiles.get(position-1).toString().contains(".png") || listFiles.get(position-1).toString().contains(".jpeg")){
                  holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
              }else if(listFiles.get(position-1).toString().contains(".mp4")){
                  holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
              }else{
                  holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
              }



              if (listFiles.get(position-1).toString().contains(".pdf")) {
                  holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_pdf);


              }else
              if (listFiles.get(position-1).toString().contains(".mp4")) {
                  holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);

              }else
              if (listFiles.get(position-1).toString().contains("youtube")) {
                  holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_mp4);
              }else
              if (listFiles.get(position-1).toString().contains(".jpg") || listFiles.get(position-1).toString().contains(".png")) {
                  holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_jpg);

              }else
              if (listFiles.get(position-1).toString().contains(".doc") || listFiles.get(position-1).toString().equalsIgnoreCase(".docx") || listFiles.get(position-1).toString().equalsIgnoreCase(".ppt") || listFiles.get(position-1).toString().equalsIgnoreCase(".pptx")) {
                  holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
              }else {
                  holder.tvFileName.setBackgroundResource(R.drawable.bg_grad_text_others);
              }

              holder.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                  }
              });
          }

        }

        @Override
        public int getItemCount() {
            return listFiles.size()+1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName,tvVersion;
            ImageView ivFile;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
//                tvVersion = itemView.findViewById(R.id.tv_version);
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