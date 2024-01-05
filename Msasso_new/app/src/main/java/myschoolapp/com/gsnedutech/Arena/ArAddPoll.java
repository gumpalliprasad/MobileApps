package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArAddPoll extends AppCompatActivity {

    private static final String TAG = ArAddPoll.class.getName();

    @BindView(R.id.et_quiz_title)
    EditText etQuizTitle;
    @BindView(R.id.et_quiz_description)
    EditText etQuizDescription;
    @BindView(R.id.tv_continue)
    TextView tvContinue;
    @BindView(R.id.ll_add_image_cover)
    LinearLayout llAddImageCover;
    @BindView(R.id.iv_cover)
    ImageView ivCover;
    @BindView(R.id.ll_arena_details)
    LinearLayout llArenaDetails;
    @BindView(R.id.ll_poll_details)
    LinearLayout llPollDetails;


    @BindView(R.id.tv_add_op)
    TextView tvAddOp;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_remove_op)
    TextView tvRemoveOp;
    @BindView(R.id.ll_bottom_op)
    LinearLayout llBottomOp;

    @BindView(R.id.cv_a)
    CardView cvA;
    @BindView(R.id.cv_b)
    CardView cvB;
    @BindView(R.id.cv_c)
    CardView cvC;
    @BindView(R.id.cv_d)
    CardView cvD;

    @BindView(R.id.et_mcq_ques)
    EditText etMcqQues;
    @BindView(R.id.ll_show_add_q_image_text)
    LinearLayout llShowAddQImageText;
    @BindView(R.id.iv_q_image)
    ImageView ivQImage;
    @BindView(R.id.et_option_text_a)
    EditText etOptionTextA;
    @BindView(R.id.et_option_text_b)
    EditText etOptionTextB;
    @BindView(R.id.et_option_text_c)
    EditText etOptionTextC;
    @BindView(R.id.et_option_text_d)
    EditText etOptionTextD;
    @BindView(R.id.tv_select_image_a)
    TextView tvSelectImageA;
    @BindView(R.id.tv_select_image_b)
    TextView tvSelectImageB;
    @BindView(R.id.tv_select_image_c)
    TextView tvSelectImageC;
    @BindView(R.id.tv_select_image_d)
    TextView tvSelectImageD;
    @BindView(R.id.iv_selected_image_a)
    ImageView ivSelectedImageA;
    @BindView(R.id.iv_selected_image_b)
    ImageView ivSelectedImageB;
    @BindView(R.id.iv_selected_image_c)
    ImageView ivSelectedImageC;
    @BindView(R.id.iv_selected_image_d)
    ImageView ivSelectedImageD;
    @BindView(R.id.tl_a)
    LinearLayout tlA;
    @BindView(R.id.tl_b)
    LinearLayout tlB;
    @BindView(R.id.tl_c)
    LinearLayout tlC;
    @BindView(R.id.tl_d)
    LinearLayout tlD;
    @BindView(R.id.fl_a)
    FrameLayout flA;
    @BindView(R.id.fl_b)
    FrameLayout flB;
    @BindView(R.id.fl_c)
    FrameLayout flC;
    @BindView(R.id.fl_d)
    FrameLayout flD;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    Uri picUri=null;

    int optionsCount = 2;

    Uri qUri = null;
    Uri aUri=null,bUri=null,cUri=null,dUri=null;

    String arenaId = "";

    View toHide,toShow;

    AmazonS3Client s3Client1;
    HashMap<String,String> mapKeyNames = new HashMap<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poll);

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
        }
        else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }

        findViewById(R.id.ll_add_image_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ArAddPoll.this);
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
                                    Uri photoURI = FileProvider.getUriForFile(ArAddPoll.this,
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
                            Toast.makeText(ArAddPoll.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                        startActivityForResult(intent_upload, 1);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvContinue.getText().toString().equalsIgnoreCase("Continue")){
                    if (etQuizTitle.getText().toString().trim().length()>0 && etQuizDescription.getText().toString().trim().length()>0 && picUri!=null){
                        tvContinue.setText("Save");
                        llArenaDetails.setVisibility(View.GONE);
                        llPollDetails.setVisibility(View.VISIBLE);
                        handleOptionsCount();
                    }else{
                        Toast.makeText(ArAddPoll.this,"Please enter all the details and a cover image!",Toast.LENGTH_SHORT).show();
                    }
                }else{

                    boolean valid = false;

                    switch (optionsCount){
                        case 2:
                            if (etOptionTextA.getText().toString().trim().length()>0 &&
                                    etOptionTextB.getText().toString().trim().length()>0){
                                valid=true;
                            }
                            break;
                        case 3:
                            if (etOptionTextA.getText().toString().trim().length()>0 &&
                                    etOptionTextB.getText().toString().trim().length()>0 &&
                                    etOptionTextC.getText().toString().trim().length()>0){
                                valid=true;
                            }
                            break;
                        case 4:
                            if (etOptionTextA.getText().toString().trim().length()>0 &&
                                    etOptionTextB.getText().toString().trim().length()>0 &&
                                    etOptionTextC.getText().toString().trim().length()>0 &&
                                    etOptionTextD.getText().toString().trim().length()>0){
                                valid=true;
                            }
                            break;
                    }


                    if (etMcqQues.getText().toString().length()>0 && valid){
                        uploadToS3();
                    }else{
                        Toast.makeText(ArAddPoll.this,"Please enter a question and all options!",Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        setUpPollLayout();

    }

    void uploadToS3(){

        utils.showLoader(this);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 1000 * 60 * 60; // 1 hour.
                expiration.setTime(msec);

                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                        Region.getRegion(Regions.AP_SOUTH_1));

                String keyName ="";
                try{

                    /*uploading cover*/

                    if (picUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,picUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,picUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArAddPoll.this,picUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("cover",keyName);
                    }

                    /*uploading cover*/

                    /*uploading question cover*/

                    if (qUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,qUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,qUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArAddPoll.this,qUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("question",keyName);
                    }

                    /*uploading question cover*/


                    /*uploading options*/


                    /*uploading optionA*/
                    if (aUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,aUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,aUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArAddPoll.this,aUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("A",keyName);
                    }
                    /*uploading optionA*/


                    /*uploading optionB*/
                    if (bUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,bUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,bUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArAddPoll.this,bUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("B",keyName);
                    }
                    /*uploading optionB*/


                    /*uploading optionC*/
                    if (cUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,cUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,cUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArAddPoll.this,cUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("C",keyName);
                    }
                    /*uploading optionC*/


                    /*uploading optionD*/
                    if (dUri!=null){
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,dUri).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArAddPoll.this,dUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArAddPoll.this,dUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("D",keyName);
                    }
                    /*uploading optionB*/


                    /*uploading options*/

                    postPoll();

                }catch (IOException e){
                    e.printStackTrace();
                }



            }
        }).start();

    }


    void postPoll(){
        JSONArray filesArray = new JSONArray();
        JSONObject fileObj = new JSONObject();
        JSONObject postObject = new JSONObject();
        try {
            if(mapKeyNames.containsKey("question")){
                fileObj.put("question",etMcqQues.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("question")));
            }else {
                fileObj.put("question", etMcqQues.getText().toString());
            }

            fileObj.put("questType","");

            if (mapKeyNames.containsKey("A")){
                fileObj.put("option1",etOptionTextA.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("A")));
            }else {
                fileObj.put("option1",etOptionTextA.getText().toString().trim());
            }
            if (mapKeyNames.containsKey("B")){
                fileObj.put("option2",etOptionTextB.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("B")));
            }else {
                fileObj.put("option2",etOptionTextB.getText().toString().trim());
            }
            if (mapKeyNames.containsKey("C")){
                fileObj.put("option3",etOptionTextC.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("C")));
            }else {
                fileObj.put("option3",etOptionTextC.getText().toString().trim());
            }
            if (mapKeyNames.containsKey("D")){
                fileObj.put("option4",etOptionTextD.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("D")));
            }else {
                fileObj.put("option4",etOptionTextD.getText().toString().trim());
            }

            fileObj.put("answer","");
            fileObj.put("explanation","");

            fileObj.put("queTime","0");

            filesArray.put(fileObj);


            try {
                postObject.put("schemaName",sh_Pref.getString("schema",""));
                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                    postObject.put("userId", tObj.getUserId());
                    postObject.put("branchId",tObj.getBranchId());
                    postObject.put("createdBy",tObj.getUserId());
                    postObject.put("userRole","T");
                }else {
                    postObject.put("userRole","S");
                    postObject.put("userId", sObj.getStudentId());
                    postObject.put("sectionId", sObj.getClassCourseSectionId());
                    postObject.put("branchId",sObj.getBranchId());
                    postObject.put("createdBy",sObj.getStudentId());
                }

                postObject.put("arenaName", etQuizTitle.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("cover")));


                postObject.put("arenaDesc",etQuizDescription.getText().toString());
                postObject.put("arenaType","Quiz");
                postObject.put("arenaCategory","7");

                postObject.put("color","#FFFFFF");
                postObject.put("questionCount","1");
                postObject.put("filesArray",filesArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }catch (JSONException e){
            e.printStackTrace();
        }


        utils.showLog(TAG,"post "+postObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.InsertTeacherArenaRecord;
        }else {
            url = AppUrls.InsertArenaRecord;
        }

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
                        showErrorDialog("Error while creating new Poll!");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("Error while creating new Poll!");
                        }
                    });
                }else {
                    String resp = response.body().string();
                    utils.showLog(TAG,"resp "+resp);
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(ArAddPoll.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Your poll article was uploaded successfully and saved in drafts.\nDo you want to send it for approval?")
                                            .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
//                                                        submitArena(audioObj.getArenaId()+"");
                                                    try {
                                                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                                            arenaId = respObj.getString("arenaId")+"";
//                                                            submitArena(respObj.getString("arenaId")+"","");
                                                            getSections();

                                                        }else {
                                                            getTeachers(respObj.getString("arenaId")+"");
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
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorDialog("Error while creating new Poll!");
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

    private void setUpPollLayout() {

        tvAddOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsCount++;
                if (optionsCount>4){
                    optionsCount=4;
                    Toast.makeText(ArAddPoll.this,"Maximum amount of options allowed are 4!",Toast.LENGTH_SHORT).show();
                }else{
                    handleOptionsCount();
                }
            }
        });

        tvRemoveOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsCount--;
                if (optionsCount<2){
                    optionsCount=2;
                    Toast.makeText(ArAddPoll.this,"Minimum amount of options allowed are 2!",Toast.LENGTH_SHORT).show();
                }else{
                    handleOptionsCount();
                }
            }
        });

        findViewById(R.id.cv_remove_q_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivQImage.setVisibility(View.GONE);
                llShowAddQImageText.setVisibility(View.VISIBLE);
                qUri = null;
            }
        });

        llShowAddQImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHide = llShowAddQImageText;
                toShow = ivQImage;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        ivQImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHide = llShowAddQImageText;
                toShow = ivQImage;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        findViewById(R.id.cv_remove_a_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aUri = null;
                ivSelectedImageA.setVisibility(View.GONE);
                tvSelectImageA.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.cv_remove_b_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bUri = null;
                ivSelectedImageB.setVisibility(View.GONE);
                tvSelectImageB.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.cv_remove_c_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cUri = null;
                ivSelectedImageC.setVisibility(View.GONE);
                tvSelectImageC.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.cv_remove_d_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dUri = null;
                ivSelectedImageD.setVisibility(View.GONE);
                tvSelectImageD.setVisibility(View.VISIBLE);
                if (tvSelectImageA.getVisibility()==View.VISIBLE && tvSelectImageB.getVisibility()==View.VISIBLE && tvSelectImageC.getVisibility()==View.VISIBLE && tvSelectImageD.getVisibility()==View.VISIBLE){
                    flA.setVisibility(View.VISIBLE);
                    flB.setVisibility(View.VISIBLE);
                    flC.setVisibility(View.VISIBLE);
                    flD.setVisibility(View.VISIBLE);
                    tlA.setVisibility(View.VISIBLE);
                    tlB.setVisibility(View.VISIBLE);
                    tlC.setVisibility(View.VISIBLE);
                    tlD.setVisibility(View.VISIBLE);
                }
            }
        });

        tvSelectImageA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageA;
                toHide = tvSelectImageA;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        ivSelectedImageA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageA;
                toHide = tvSelectImageA;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        tvSelectImageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageB;
                toHide = tvSelectImageB;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        ivSelectedImageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageB;
                toHide = tvSelectImageB;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        tvSelectImageC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageC;
                toHide = tvSelectImageC;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        ivSelectedImageC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageC;
                toHide = tvSelectImageC;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        tvSelectImageD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageD;
                toHide = tvSelectImageD;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });

        ivSelectedImageD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShow = ivSelectedImageD;
                toHide = tvSelectImageD;
                Intent intent_upload = new Intent();
                intent_upload.setType("image/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 3);
            }
        });



    }

    private void handleOptionsCount() {

        tvCount.setText(optionsCount+"");

        switch (optionsCount){
            case 2:
                llBottomOp.setVisibility(View.GONE);
                cvC.setVisibility(View.GONE);
                cvD.setVisibility(View.GONE);
                break;
            case 3:
                llBottomOp.setVisibility(View.VISIBLE);
                cvC.setVisibility(View.VISIBLE);
                cvD.setVisibility(View.INVISIBLE);
                break;
            case 4:
                llBottomOp.setVisibility(View.VISIBLE);
                cvC.setVisibility(View.VISIBLE);
                cvD.setVisibility(View.VISIBLE);
                break;
        }
    }

    void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                picUri = data.getData();
                ivCover.setImageURI(picUri);
                findViewById(R.id.ll_add_image).setVisibility(View.GONE);
                ivCover.setVisibility(View.VISIBLE);
            }
            if (requestCode==2){
                ivCover.setImageURI(picUri);
                findViewById(R.id.ll_add_image).setVisibility(View.GONE);
                ivCover.setVisibility(View.VISIBLE);
            }
            if (requestCode==3){
                toHide.setVisibility(View.GONE);
                toShow.setVisibility(View.VISIBLE);
                ((ImageView)toShow).setImageURI(data.getData());
                if (toShow.getId()==R.id.iv_q_image){
                    qUri = data.getData();
                }
                if (toShow.getId()==R.id.iv_selected_image_a){
                    aUri = data.getData();
                }
                if (toShow.getId()==R.id.iv_selected_image_b){
                    bUri = data.getData();
                }
                if (toShow.getId()==R.id.iv_selected_image_c){
                    cUri = data.getData();
                }
                if (toShow.getId()==R.id.iv_selected_image_d){
                    dUri = data.getData();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                        new AlertDialog.Builder(ArAddPoll.this)
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
                            new AlertDialog.Builder(ArAddPoll.this)
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
                                        Dialog dialog = new Dialog(ArAddPoll.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(ArAddPoll.this)
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
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(ArAddPoll.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ArAddPoll.this)
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
                                    new AlertDialog.Builder(ArAddPoll.this)
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
                postObject.put("userId", sObj.getStudentId());
                postObject.put("createdBy", sObj.getStudentId());
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
                        new AlertDialog.Builder(ArAddPoll.this)
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
                            new AlertDialog.Builder(ArAddPoll.this)
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
                                    Toast.makeText(ArAddPoll.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArAddPoll.this)
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
            return new ViewHolder(LayoutInflater.from(ArAddPoll.this).inflate(R.layout.tv_teacher_name,parent,false));
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
                        Toast.makeText(ArAddPoll.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ArAddPoll.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(ArAddPoll.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ArAddPoll.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
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

            jsonObject.put("arenaId",arenaId);

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
                        new AlertDialog.Builder(ArAddPoll.this)
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
                            new AlertDialog.Builder(ArAddPoll.this)
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
                                        new AlertDialog.Builder(ArAddPoll.this)
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
                                        new AlertDialog.Builder(ArAddPoll.this)
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

}