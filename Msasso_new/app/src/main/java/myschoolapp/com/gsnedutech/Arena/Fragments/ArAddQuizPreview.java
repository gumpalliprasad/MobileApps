package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddQuizQuestion;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Arena.ArQuizApprovalActivity;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ArAddQuizPreview extends Fragment {

    private static final String TAG = "SriRam -" + ArAddQuizPreview.class.getName();

    View viewArAddQuizPreview;
    Unbinder unbinder;

    @BindView(R.id.rv_questions)
    RecyclerView rvQuestions;

    Activity mActivity;

    MyUtils utils = new MyUtils();
    AmazonS3Client s3Client1;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    TeacherObj tObj;

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    public ArAddQuizPreview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArAddQuizPreview = inflater.inflate(R.layout.fragment_ar_add_quiz_preview, container, false);
        unbinder = ButterKnife.bind(this,viewArAddQuizPreview);

        init();

        return viewArAddQuizPreview;
    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
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
        rvQuestions.setLayoutManager(new LinearLayoutManager(mActivity));

        if (mActivity instanceof ArQuizApprovalActivity){

            rvQuestions.setAdapter(new AprovalQuestionAdapter(((ArQuizApprovalActivity)mActivity).listQuestions));

            ((TextView)viewArAddQuizPreview.findViewById(R.id.tv_next)).setText("Submit For Approval");

            viewArAddQuizPreview.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                        submitArena(((ArQuizApprovalActivity) mActivity).quizObj.getArenaId() + "","");
                    }else {
                        getTeachers(((ArQuizApprovalActivity) mActivity).quizObj.getArenaId() + "");
                    }
                }
            });

        }else {

            rvQuestions.setAdapter(new QuestionsAdapter());

            viewArAddQuizPreview.findViewById(R.id.tv_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadtoS3();
                }
            });
        }
    }

    private void getTeachers(String arenaId) {

        utils.showLoader(mActivity);

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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(mActivity)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
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
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
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
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog dialog = new Dialog(mActivity);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(mActivity)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my audio. Can submit for approval later.")

                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });

                                        RecyclerView rvTeachers = dialog.findViewById(R.id.rv_teachers);
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(mActivity));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId));

                                        dialog.show();
                                    }
                                });
                            }else {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(mActivity)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nQuiz cannot be sent for approval\nPlease contact your school for more information.")

                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
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

                        }else{
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(mActivity)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
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
                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });


    }

    private void submitArena(String arenaId, String teacherId) {

        utils.showLoader(mActivity);

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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(mActivity)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("oops! something went wrong please try again later")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        mActivity.finish();
                                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("oops! something went wrong please try again later")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            mActivity.finish();
                                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity,"Success!",Toast.LENGTH_SHORT).show();
                                    mActivity.finish();
                                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(mActivity)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("oops! something went wrong please try again later")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    mActivity.finish();
                                                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    private void uploadtoS3() {

        utils.showLoader(mActivity);

        int c=0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 1000 * 60 * 60; // 1 hour.
                expiration.setTime(msec);

                String keyName ="";
                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                        Region.getRegion(Regions.AP_SOUTH_1));

                if (((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()!=null){
                    try {
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/"+tObj.getBranchId()+"/"+tObj.getRoleName()+"/"+tObj.getUserId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()).getName();
                        }else {
                            keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()).getName();
                        }
                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getCoverImage()));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);
                        ((ArAddQuizQuestion)mActivity).quizObject.setCoverImagePath(keyName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for (int i=0;i<((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().size();i++){
                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImage()!=null){
                        try {
                            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getQuesImage()).getName();
                            }else {
                                keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getQuesImage()).getName();
                            }
                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    keyName,
                                    FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImage()));
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);
                            ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setQuesImageFilepath(keyName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionType().equalsIgnoreCase("True/False")){
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionAImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionAImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionAImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionBImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionBImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionBImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionCImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionCImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionCImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImage()!=null){
                            try {
                                if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                                    keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionDImage()).getName();
                                }else {
                                    keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, ((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().get(i).getOptionDImage()).getName();
                                }
                                PutObjectRequest por = new PutObjectRequest(
                                        sh_Pref.getString("bucket", ""),
                                        keyName,
                                        FileUtil.from(mActivity,((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImage()));
                                por.setCannedAcl(CannedAccessControlList.PublicRead);
                                s3Client1.putObject(por);
                                ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).setOptionDImagePath(keyName);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                postToServer();

            }
        }).start();

    }

    private void postToServer() {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.dismissDialog();
            }
        });

        JSONArray filesArray = new JSONArray();
        for (int i=0;i<((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().size();i++){
            JSONObject fileObj = new JSONObject();
            if (!((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImageFilepath().equalsIgnoreCase("NA")){
                try {
                    fileObj.put("question",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestion()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuesImageFilepath()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    fileObj.put("question",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestion());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            try {
                fileObj.put("answer",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getIsCorrect());
                fileObj.put("explanation","");
                fileObj.put("queTime",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionType().equalsIgnoreCase("True/False")){
                try {
                    fileObj.put("questType","TOF");
                    fileObj.put("option1","NA");
                    fileObj.put("option2","NA");
                    fileObj.put("option3","NA");
                    fileObj.put("option4","NA");

                    filesArray.put(fileObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {

                if(((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getQuestionType().equalsIgnoreCase("MCQ")){
                    try {
                        fileObj.put("questType","MCQ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        fileObj.put("questType","MAQ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option1",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionA());
                    }else {
                        fileObj.put("option1","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionAImagePath()));
                    }

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option2",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionB());
                    }else {
                        fileObj.put("option2","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionBImagePath()));
                    }

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option3",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionC());
                    }else {
                        fileObj.put("option3","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionCImagePath()));
                    }

                    if (((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImagePath().equalsIgnoreCase("NA")){
                        fileObj.put("option4",((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionD());
                    }else {
                        fileObj.put("option4","NA~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(i).getOptionDImagePath()));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                filesArray.put(fileObj);

            }

        }


        JSONObject postObject = new JSONObject();
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
            if(((ArAddQuizQuestion)mActivity).quizObject.getCoverImagePath().equalsIgnoreCase("NA")) {
                postObject.put("arenaName", ((ArAddQuizQuestion) mActivity).quizObject.getQuizTitle());
            }else {
                postObject.put("arenaName", ((ArAddQuizQuestion) mActivity).quizObject.getQuizTitle()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),((ArAddQuizQuestion) mActivity).quizObject.getCoverImagePath()));
            }
            postObject.put("arenaDesc",((ArAddQuizQuestion) mActivity).quizObject.getQuizDesc());
            postObject.put("arenaType","Quiz");
            postObject.put("arenaCategory","1");

            postObject.put("color","#FFFFFF");
            postObject.put("questionCount",((ArAddQuizQuestion) mActivity).quizObject.getListMCQ().size()+"");
            postObject.put("filesArray",filesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"obj "+postObject.toString());
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
                mActivity.runOnUiThread(new Runnable() {
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
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(mActivity, "Success!", Toast.LENGTH_SHORT).show();

                                finishDialog();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void finishDialog(){
        Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_finish_quiz_add);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
        dialog.findViewById(R.id.tv_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

    }


    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_ar_quiz_preview,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//            holder.llBg.setBackgroundResource(new DrawableUtils().darkPalet[1]);
            holder.tvQ.setText("Q"+(position+1)+". "+((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().get(position).getQuestion());

        }

        @Override
        public int getItemCount() {
            return ((ArAddQuizQuestion)mActivity).quizObject.getListMCQ().size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQ;
            LinearLayout llBg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQ = itemView.findViewById(R.id.tv_q);
                llBg = itemView.findViewById(R.id.ll_bg);
            }
        }

    }

    class AprovalQuestionAdapter extends RecyclerView.Adapter<AprovalQuestionAdapter.ViewHolder>{

        List<ArenaQuizQuestionFiles> listQuestions;

        AprovalQuestionAdapter(List<ArenaQuizQuestionFiles> listQuestions){
            this.listQuestions = listQuestions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_ar_quiz_preview,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (listQuestions.get(position).getQuestion().contains("~~")) {
                holder.tvQ.setText(listQuestions.get(position).getQuestion().split("~~")[0]);
            }else {
                holder.tvQ.setText(listQuestions.get(position).getQuestion());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity instanceof ArQuizApprovalActivity){
                        ((ArQuizApprovalActivity) mActivity).qNum = position;
                        ((ArQuizApprovalActivity) mActivity).stepNo = 2;
                        ((ArQuizApprovalActivity) mActivity).loadFragment();
                    }else {
                        ((ArQuizApprovalActivity) mActivity).qNum = position;
                        ((ArQuizApprovalActivity) mActivity).stepNo++;
                        ((ArQuizApprovalActivity) mActivity).loadFragment();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listQuestions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQ;
            LinearLayout llBg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQ = itemView.findViewById(R.id.tv_q);
                llBg = itemView.findViewById(R.id.ll_bg);
            }
        }
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
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.tv_teacher_name,parent,false));
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
}


