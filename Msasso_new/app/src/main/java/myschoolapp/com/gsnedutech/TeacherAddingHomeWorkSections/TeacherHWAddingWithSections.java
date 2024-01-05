package myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections.Model.AddHwobj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherHWAddingWithSections extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherHWAddingWithSections.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    int stepNumber = 1;

    public String courseId = "",elective = "";

    public AddHwobj newHwObj;

    SharedPreferences sh_Pref;
    TeacherObj tObj;

    int pos = -1;

    MyUtils utils = new MyUtils();

    AmazonS3Client s3Client1;
    List<String> keyName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_h_w_adding_with_sections);
        ButterKnife.bind(this);

        init();

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
            utils.alertDialog(1, TeacherHWAddingWithSections.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                loadFrag(stepNumber);
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }


    void init(){

        courseId = getIntent().getStringExtra("courseId");

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        newHwObj = new AddHwobj();

        newHwObj.setBranchId(tObj.getBranchId());
        newHwObj.setSectionId(getIntent().getStringExtra("sectionId"));
        newHwObj.setSubjectId(getIntent().getStringExtra("subjectId"));
        elective = getIntent().getStringExtra("elective");

    }

    public void loadFrag(int stepNumber) {
        Fragment fragment;
        this.stepNumber = stepNumber;
        switch (stepNumber){
            case 1:
                fragment = new HwDetailFrag();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                break;
            case 2:
                fragment = new SectionHwFrag();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                break;
            case 3:

                fragment = new StudentSelectFrag();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                break;
            default:onBackPressed();
        }

    }


    void startUpload(){

        utils.showLoader(TeacherHWAddingWithSections.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray sectionArray = new JSONArray();
        JSONArray studentArray = new JSONArray();

        for (int i=0;i<newHwObj.getListAssigned().size();i++){

            //Section Array
            JSONObject job = new JSONObject();
            try{
                job.put("sectionId",newHwObj.getListAssigned().get(i).getSectionId());
                sectionArray.put(job);
            }catch(Exception e){

            }

            for(int j=0;j<newHwObj.getListAssigned().get(i).getStudList().size();j++){
                try {

                    if(newHwObj.getListAssigned().get(i).getStudList().get(j).isChecked()){

                        JSONObject jsonOb = new JSONObject();
                        jsonOb.put("studentId",newHwObj.getListAssigned().get(i).getStudList().get(j).getStudentId()+"");
                        jsonOb.put("sectionId",newHwObj.getListAssigned().get(i).getSectionId());
                        jsonOb.put("branchId",newHwObj.getBranchId());

                        studentArray.put(jsonOb);

//                        studentArray.put(newHwObj.getListAssigned().get(i).getStudList().get(j).getStudentId()+"");

                    }

                }catch (Exception e){

                }
            }
        }

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("sectionId",newHwObj.getSectionId());
            jsonObject.put("subjectId",newHwObj.getSubjectId());
            jsonObject.put("branchId",newHwObj.getBranchId());
            jsonObject.put("createdBy",tObj.getUserId());
            jsonObject.put("homeworkDetail",newHwObj.getHomeworkDetail());
            jsonObject.put("homeworkTitle",newHwObj.getHomeworkTitle());
            jsonObject.put("homeworkDate",new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(newHwObj.getHomeworkDate())));
            jsonObject.put("submissionDate",new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(newHwObj.getSubmissionDate())));
            jsonObject.put("homeWorktypeId",newHwObj.getHomeWorktypeId());
            jsonObject.put("sectionArray",sectionArray);
            jsonObject.put("studentArray",studentArray);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+jsonObject.toString());
        utils.showLog(TAG,"url - "+new AppUrls().HOMEWORK_PostInsertSecHomeWork);


        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request post = new Request.Builder()
                .url(new AppUrls().HOMEWORK_PostInsertSecHomeWork)
                .post(body)
                .build();

        client.newCall(post).enqueue(new Callback() {
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

                if(!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }
                else{

                    try {
                        JSONObject ParentObject = new JSONObject(resp);

                        if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")){
                            if (newHwObj.getmImageUri().size()>0){
                                uploadToS3(ParentObject.getString("HWID"));
                            }else {
//                                finish();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherHWAddingWithSections.this);
                                        builder.setMessage("Homework updated successfully")
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setCancelable(false)
                                                .setPositiveButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                finish();
                                                            }
                                                        }

                                                );
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    void uploadToS3(String hwId){

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

                    for (int i=0;i<newHwObj.getmImageUri().size();i++){
                        c++;
                        File file = FileUtil.from(TeacherHWAddingWithSections.this, newHwObj.getmImageUri().get(i));
                        String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                        File f = new File(Environment.getExternalStorageState(),file.getName());
                        boolean success = file.renameTo(file);


                        if (success){
                            utils.showLog(TAG,"url"+"testing/HWtest/teacher/"+hwId+"/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/"+f.getName());
                            keyName.add("testing/HWtest/teacher/"+hwId+"/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/"+f.getName());

                            ClientConfiguration cc = new ClientConfiguration();
                            cc.setSocketTimeout(120000);

                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1),cc);


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "testing/HWtest/teacher/"+hwId+"/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/"+f.getName() ,
                                    file);//key is  URL



                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG,"urls - "+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                            String url = s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    postToServer(hwId,url);
                                }
                            });

                        }else {
                            Toast.makeText(TeacherHWAddingWithSections.this,"Rename failed!",Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG,"error "+e.getMessage());
                }
            }
        }).start();

    }

    int counter=0;
    private void postToServer(String hwId, String url) {


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray jsonArray = new JSONArray();
        JSONObject job = new JSONObject();
        try {
            job.put("fileName",url.split("/")[url.split("/").length - 1]);
            job.put("targetPath",url);
            job.put("isActive","1");
            jsonArray.put(job);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
            jsonObject.put("hwId", hwId);
            jsonObject.put("createdBy", tObj.getUserId());
            jsonObject.put("insertRecords", jsonArray);
            url = url;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_PostUploadFiles)
                .post(body)
                .build();

        String finalUrl = url;
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

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            ++counter;
                            if (counter==keyName.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();
//                                        Toast.makeText(TeacherAddHomeworkActivity.this, "HomeWork Posted Sucessfully", Toast.LENGTH_SHORT).show();
//                                        finish();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherHWAddingWithSections.this);
                                        builder.setMessage("Homework Added Successfully!")
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setCancelable(false)
                                                .setPositiveButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                finish();
                                                            }
                                                        }

                                                );
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });

                            }

                        }

                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        switch (stepNumber){
            case 2:
                loadFrag(1);
                break;
            case 3:
                loadFrag(2);
                break;
            case 1:
            default:
                super.onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;

        }
    }
}