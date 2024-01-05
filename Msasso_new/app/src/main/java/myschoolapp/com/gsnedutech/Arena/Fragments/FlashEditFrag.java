package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.FlashCardsDisplayRejected;
import myschoolapp.com.gsnedutech.Models.StudentObj;
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
public class FlashEditFrag extends Fragment {

    private static final String TAG = ArQuizEditingFrag.class.getName();

    View viewFlashEditFrag;
    Unbinder unbinder;

    Activity mActivity;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    @BindView(R.id.ll_color)
    LinearLayout llColor;

    @BindView(R.id.et_answer)
    EditText etAnswer;

    @BindView(R.id.ll_front)
    LinearLayout llFront;

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.ll_image)
    LinearLayout llImage;

    @BindView(R.id.ll_a_image)
    LinearLayout llAImage;

    @BindView(R.id.tv_tap_ans)
    TextView tvTapAns;

    @BindView(R.id.tv_add_flash_card)
    TextView tvAddFlashCard;

    @BindView(R.id.iv_q_image)
    ImageView qImage;

    @BindView(R.id.et_ques)
    EditText etQues;

    @BindView(R.id.cv_falsh_card)
    CardView cvFlashCard;

    @BindView(R.id.tv_q_image)
    TextView tvQImage;

    @BindView(R.id.tv_a_image)
    TextView tvAImage;

    @BindView(R.id.iv_a_image)
    ImageView ivAImage;

    @BindView(R.id.tv_tap_ques)
    TextView tvTapQuestion;

    public List<ArenaQuizQuestionFiles> listQs = new ArrayList<>();
    public int qNum = 0;
    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE = 2;
    int imageQorA = 1;

    String[] imageUri = {"",""};
    String qKeyName = "";
    String ansKeyName = "";

    MyUtils utils = new MyUtils();

    AmazonS3Client s3Client1;

    public FlashEditFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFlashEditFrag = inflater.inflate(R.layout.fragment_flash_edit, container, false);
        unbinder = ButterKnife.bind(this,viewFlashEditFrag);

        init();

        return viewFlashEditFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        qNum = ((FlashCardsDisplayRejected) mActivity).qNum ;
        listQs = ((FlashCardsDisplayRejected)mActivity).listQuestions;

        tvTapAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
                oa1.setInterpolator(new AccelerateDecelerateInterpolator());
                oa2.setInterpolator(new DecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        llFront.setVisibility(View.GONE);
                        llBack.setVisibility(View.VISIBLE);
                        oa2.start();
                    }
                });
                oa1.start();
            }
        });

        tvTapQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        llFront.setVisibility(View.VISIBLE);
                        llBack.setVisibility(View.GONE);
                        oa2.start();
                    }
                });
                oa1.start();
            }
        });

        qImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageQorA = 1;
                addImage();
            }
        });
        ivAImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageQorA = 2;
                addImage();
            }
        });

        viewFlashEditFrag.findViewById(R.id.tv_add_flash_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etQues.getText().toString().trim().length()>0 && etAnswer.getText().toString().trim().length()>0){
                    if (!imageUri[0].equalsIgnoreCase("") || !imageUri[1].equalsIgnoreCase("")){
                        uploadToS3();
                    }else {
                        updateFlash();
                    }
                }
            }
        });

        loadQuestion();
    }

    void loadQuestion(){
        if (listQs.get(qNum).getQuestion().contains("~~")){

            String[] str = listQs.get(qNum).getQuestion().split("~~");
            etQues.setText(str[0]);
            Picasso.with(mActivity).load(str[(str.length-1)]).placeholder(R.drawable.ic_arena_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(qImage);
        }else {
            etQues.setText(listQs.get(qNum).getQuestion());

        }

        if (listQs.get(qNum).getAnswer().contains("~~")){

            String[] str = listQs.get(qNum).getAnswer().split("~~");
            etAnswer.setText(str[0]);
            Picasso.with(mActivity).load(str[(str.length-1)]).placeholder(R.drawable.ic_arena_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivAImage);
        }else {
            etAnswer.setText(listQs.get(qNum).getAnswer());
        }
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

    void addImage(){
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_imgselector);
        dialog.setCancelable(true);
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int wwidth = metrics.widthPixels;
        dialog.getWindow().setLayout((int) (wwidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvCam = dialog.findViewById(R.id.tv_cam);
        tvCam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode == CAMERA_CAPTURE) {
                //get the Uri for the captured image
                Uri uri = picUri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                    if (imageQorA == 1) {
                        try {
                            File file = FileUtil.from(mActivity,uri);
                            if (file.exists()){
                                imageUri[0] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        qImage.setImageBitmap(bitmap);
                    }
                    else {
                        try {
                            File file = FileUtil.from(mActivity,uri);
                            if (file.exists()){
                                imageUri[1] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ivAImage.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //carry out the crop operation
//                performCrop();
                Log.v("picUri", uri.toString());

            } else if (requestCode == PICK_IMAGE) {
                picUri = data.getData();
                Log.v("uriGallery", picUri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);
                    if (imageQorA == 1) {
                        try {
                            File file = FileUtil.from(mActivity,picUri);
                            if (file.exists()){
                                imageUri[0] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        qImage.setImageBitmap(bitmap);
                    }
                    else {
                        try {
                            File file = FileUtil.from(mActivity,picUri);
                            if (file.exists()){
                                imageUri[1] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ivAImage.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
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

                if (!imageUri[0].equalsIgnoreCase("")){
                    qKeyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ new File(imageUri[0]).getName();
                    PutObjectRequest por = null;
                    por = new PutObjectRequest(
                            sh_Pref.getString("bucket", ""),
                            qKeyName,
                            new File(imageUri[0]));
                    por.setCannedAcl(CannedAccessControlList.PublicRead);
                    s3Client1.putObject(por);
                }

                if (!imageUri[1].equalsIgnoreCase("")){
                    ansKeyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ new File(imageUri[1]).getName();
                    PutObjectRequest por = null;
                    por = new PutObjectRequest(
                            sh_Pref.getString("bucket", ""),
                            ansKeyName,
                            new File(imageUri[1]));
                    por.setCannedAcl(CannedAccessControlList.PublicRead);
                    s3Client1.putObject(por);
                }

                updateFlash();

            }
        }).start();
    }

    void updateFlash(){
        JSONObject postObj = new JSONObject();

        try {
            postObj.put("arenaQuestionId",listQs.get(qNum).getArenaQuestionId());
            if (qKeyName.equalsIgnoreCase("")){
                if (listQs.get(qNum).getQuestion().contains("~~")){
                    String[] s= listQs.get(qNum).getQuestion().split("~~");
                    postObj.put("question", etQues.getText().toString()+"~~link~~"+s[(s.length-1)]);
                }else {
                    postObj.put("question",etQues.getText().toString());
                }
            }else {
                postObj.put("question",etQues.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),qKeyName));
            }
            if (ansKeyName.equalsIgnoreCase("")){
                if (listQs.get(qNum).getAnswer().contains("~~")){
                    String[] s= listQs.get(qNum).getAnswer().split("~~");
                    postObj.put("answer", etAnswer.getText().toString()+"~~link~~"+s[(s.length-1)]);
                }else {
                    postObj.put("answer",etAnswer.getText().toString());
                }
            }else {
                postObj.put("answer",etAnswer.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),ansKeyName));
            }

            postObj.put("option1",listQs.get(qNum).getOption1());
            postObj.put("option2",listQs.get(qNum).getOption2());
            postObj.put("option3",listQs.get(qNum).getOption3());
            postObj.put("option4",listQs.get(qNum).getOption4());

            postObj.put("queTime",listQs.get(qNum).getQuestTime());
            postObj.put("updatedBy",sObj.getStudentId());

            postObj.put("schemaName", sh_Pref.getString("schema",""));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"post obj "+postObj);
        utils.showLog(TAG,"post url "+AppUrls.UpdateArenaQuestion);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, String.valueOf(postObj));

        Request request = new Request.Builder()
                .url(AppUrls.UpdateArenaQuestion)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        showErrorDialog("Oops! Failed to update question.");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("Oops! Failed to update question.");
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(mActivity,"Success!",Toast.LENGTH_SHORT).show();
                                    ((FlashCardsDisplayRejected)mActivity).showContinueDialog();

                                }
                            });
                        }else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showErrorDialog("Oops! Failed to update question.");
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
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}