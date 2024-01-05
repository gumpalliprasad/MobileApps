package myschoolapp.com.gsnedutech.Arena.Trial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class QuizDetails extends Fragment {

    private static final String TAG = QuizDetails.class.getName();

    Unbinder unbinder;
    View viewQuizDetails;

    Activity mActivity;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    Uri picUri=null;
    AmazonS3Client s3Client1;

    String keyName="";

    @BindView(R.id.et_quiz_title)
    EditText etQuizTitle;

    @BindView(R.id.et_quiz_description)
    EditText etQuizDescription;

    @BindView(R.id.et_no_of_questions)
    EditText etNoOfQuestions;



    @BindView(R.id.tv_continue)
    TextView tvContinue;

    @BindView(R.id.ll_add_image_cover)
    LinearLayout llAddImageCover;

    @BindView(R.id.iv_cover)
    ImageView ivCover;

    public QuizDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewQuizDetails =  inflater.inflate(R.layout.fragment_quiz_details, container, false);
        unbinder = ButterKnife.bind(this,viewQuizDetails);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

        return viewQuizDetails;
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

        viewQuizDetails.findViewById(R.id.ll_add_image_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_imgselector);
                dialog.setCancelable(true);
                DisplayMetrics metrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                                File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                                // Save a file: path for use with ACTION_VIEW intents
                                picUri =  Uri.fromFile(image);
                                if (image != null) {
                                    Uri photoURI = FileProvider.getUriForFile(mActivity,
                                            mActivity.getPackageName()+".provider",
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
                            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
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

//                Intent intent_upload = new Intent();
//                intent_upload.setType("image/*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload, 1);
            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etQuizTitle.getText().toString().length()>0 && etNoOfQuestions.getText().toString().length()>0 && etQuizDescription.getText().toString().length()>0 && picUri!=null){

                    uploadToS3();

                }else {
                    Toast.makeText(mActivity,"Please Enter All the Details!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void uploadToS3(){
        utils.showLoader(mActivity);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Date expiration = new Date();
                long msec = expiration.getTime();
                msec += 1000 * 60 * 60; // 1 hour.
                expiration.setTime(msec);

                s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                        Region.getRegion(Regions.AP_SOUTH_1));

                try {
                    if (picUri != null) {
                        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                            keyName = "arena/" + tObj.getBranchId() + "/" + tObj.getRoleName() + "/" + tObj.getUserId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, picUri).getName();
                        } else {
                            keyName = "arena/" + sObj.getBranchId() + "/" + sObj.getClassCourseSectionId() + "/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "/" + FileUtil.from(mActivity, picUri).getName();
                        }

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(mActivity, picUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        insertArenaRecord();

                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void insertArenaRecord() {
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

            postObject.put("arenaName", etQuizTitle.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName));


            postObject.put("arenaDesc",etQuizDescription.getText().toString());
            postObject.put("arenaType","Quiz");
            postObject.put("arenaCategory","1");

            postObject.put("color","#FFFFFF");
            postObject.put("questionCount",etNoOfQuestions.getText().toString().trim());
            postObject.put("filesArray",new JSONArray());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ArAddQuizNew)mActivity).count = Integer.parseInt(etNoOfQuestions.getText().toString().trim());

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
                        showErrorDialog("Error while creating new Quiz!");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("Error while creating new Quiz!");
                        }
                    });
                }else {
                    String resp = response.body().string();
                    utils.showLog(TAG,"resp "+resp);
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            ((ArAddQuizNew)mActivity).arenaId = respObj.getInt("arenaId")+"";
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ((ArAddQuizNew)mActivity).tvTitle.setText(etQuizTitle.getText().toString());
                                    Dialog dialog = new Dialog(mActivity);
                                    dialog.setContentView(R.layout.layout_arena_quiz_option);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
                                    dialog.show();

                                    dialog.findViewById(R.id.tv_close_dialog).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(mActivity,"Please select an option to continue.",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    dialog.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ((ArAddQuizNew)mActivity).typeOne = "MCQ";
                                            ((ArAddQuizNew)mActivity).stepNo = 2;
                                            ((ArAddQuizNew)mActivity).loadFragment();
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.findViewById(R.id.cv_maq).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ((ArAddQuizNew)mActivity).typeOne = "MAQ";
                                            ((ArAddQuizNew)mActivity).stepNo = 2;
                                            ((ArAddQuizNew)mActivity).loadFragment();
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.findViewById(R.id.cv_tf).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ((ArAddQuizNew)mActivity).typeOne = "TOF";
                                            ((ArAddQuizNew)mActivity).stepNo = 2;
                                            ((ArAddQuizNew)mActivity).loadFragment();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorDialog("Error while creating new Quiz!");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            picUri = data.getData();
            ivCover.setImageURI(picUri);
            viewQuizDetails.findViewById(R.id.ll_add_image).setVisibility(View.GONE);
            ivCover.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showErrorDialog(String message){
        new AlertDialog.Builder(mActivity)
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