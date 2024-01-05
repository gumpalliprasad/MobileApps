package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
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

public class ArPollDisplayRejectedActivity extends AppCompatActivity {

    private static final String TAG = ArAddPoll.class.getName();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_comment)
    TextView tvComment;

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

    ArenaRecord pollObj;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    MyUtils utils = new MyUtils();
    Uri qUri = null;
    Uri aUri=null,bUri=null,cUri=null,dUri=null;

    View toHide,toShow;

    List<ArenaQuizQuestionFiles> listQs = new ArrayList<>();
    int optionsCount = 0;

    AmazonS3Client s3Client1;
    HashMap<String,String> mapKeyNames = new HashMap<>();

    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_poll_display_rejected);
        ButterKnife.bind(this);

        init();
    }

    void init(){

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pollObj = (ArenaRecord) getIntent().getSerializableExtra("poll");

        if (pollObj.getArenaName().contains("~~")){
            tvTitle.setText(pollObj.getArenaName().split("~~")[0]);
        }else {
            tvTitle.setText(pollObj.getArenaName());
        }
        tvComment.setText(pollObj.getTeacherReview());

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        tvAddOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionsCount++;
                if (optionsCount>4){
                    optionsCount=4;
                    Toast.makeText(ArPollDisplayRejectedActivity.this,"Maximum amount of options allowed are 4!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ArPollDisplayRejectedActivity.this,"Minimum amount of options allowed are 2!",Toast.LENGTH_SHORT).show();
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

        findViewById(R.id.tv_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Toast.makeText(ArPollDisplayRejectedActivity.this,"Please enter a question and all options!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        getQuizDetails();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){

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

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + pollObj.getArenaId()+"&arenaCategory=7&studentId="+sObj.getStudentId())
                .build();

        utils.showLog(TAG, "url "+AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + pollObj.getArenaId()+"&arenaCategory=7&studentId="+sObj.getStudentId());

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

                Log.v(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaQuizQuestionFiles>>() {
                            }.getType();

                            listQs.clear();
                            listQs.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setOptionsLayout();
                                }
                            });



                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

    void setOptionsLayout(){

        for (int i = 0; i < listQs.size(); i++) {
            String str = listQs.get(i).getOption1();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                optionsCount++;
            }
            str = listQs.get(i).getOption2();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                optionsCount++;
            }
            str = listQs.get(i).getOption3();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                optionsCount++;
            }
            str = listQs.get(i).getOption4();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                optionsCount++;
            }
        }

        tvCount.setText(optionsCount+"");

        for (int i = 0; i < listQs.size(); i++) {

            String str = listQs.get(i).getQuestion();
            if (str.contains("~~")) {
                String[] s = str.split("~~");
                etMcqQues.setText(s[0]);

                Picasso.with(ArPollDisplayRejectedActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
                ivQImage.setVisibility(View.VISIBLE);
                llShowAddQImageText.setVisibility(View.GONE);
            } else {
                etMcqQues.setText(str);
                ivQImage.setVisibility(View.GONE);
            }

            str = listQs.get(i).getOption1();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    etOptionTextA.setText(s[0]);
                    tvSelectImageA.setVisibility(View.GONE);

                    Picasso.with(ArPollDisplayRejectedActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageA);
                    ivSelectedImageA.setVisibility(View.VISIBLE);
                } else {
                    etOptionTextA.setText(str);
                    ivSelectedImageA.setVisibility(View.GONE);
                }

            }

            str = listQs.get(i).getOption2();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    etOptionTextB.setText(s[0]);
                    tvSelectImageB.setVisibility(View.GONE);

                    Picasso.with(ArPollDisplayRejectedActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageB);
                    ivSelectedImageB.setVisibility(View.VISIBLE);
                } else {
                    etOptionTextB.setText(str);
                    ivSelectedImageB.setVisibility(View.GONE);
                }

            }

            str = listQs.get(i).getOption3();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                llBottomOp.setVisibility(View.VISIBLE);
                cvC.setVisibility(View.VISIBLE);
                cvD.setVisibility(View.INVISIBLE);
                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    etOptionTextC.setText(s[0]);
                    tvSelectImageC.setVisibility(View.GONE);

                    Picasso.with(ArPollDisplayRejectedActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageC);
                    ivSelectedImageC.setVisibility(View.VISIBLE);
                } else {
                    etOptionTextC.setText(str);
                    ivSelectedImageC.setVisibility(View.GONE);
                }

            }

            str = listQs.get(i).getOption4();
            if (str != null && !str.equalsIgnoreCase("NA") && !str.equalsIgnoreCase("")) {
                llBottomOp.setVisibility(View.VISIBLE);
                cvC.setVisibility(View.VISIBLE);
                cvD.setVisibility(View.VISIBLE);
                if (str.contains("~~")) {
                    String[] s = str.split("~~");
                    etOptionTextD.setText(s[0]);
                    tvSelectImageD.setVisibility(View.GONE);

                    Picasso.with(ArPollDisplayRejectedActivity.this).load(s[(s.length - 1)]).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSelectedImageD);
                    ivSelectedImageD.setVisibility(View.VISIBLE);
                } else {
                    etOptionTextD.setText(str);
                    ivSelectedImageD.setVisibility(View.GONE);
                }

            }
        }
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


                    /*uploading question cover*/

                    if (qUri!=null){
                        keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArPollDisplayRejectedActivity.this,qUri).getName();

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArPollDisplayRejectedActivity.this,qUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("question",keyName);
                    }

                    /*uploading question cover*/


                    /*uploading options*/


                    /*uploading optionA*/
                    if (aUri!=null){
                        keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArPollDisplayRejectedActivity.this,aUri).getName();


                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArPollDisplayRejectedActivity.this,aUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("A",keyName);
                    }
                    /*uploading optionA*/


                    /*uploading optionB*/
                    if (bUri!=null){

                        keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArPollDisplayRejectedActivity.this,bUri).getName();

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArPollDisplayRejectedActivity.this,bUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("B",keyName);
                    }
                    /*uploading optionB*/


                    /*uploading optionC*/
                    if (cUri!=null){
                        keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArPollDisplayRejectedActivity.this,cUri).getName();

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArPollDisplayRejectedActivity.this,cUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("C",keyName);
                    }
                    /*uploading optionC*/


                    /*uploading optionD*/
                    if (dUri!=null){

                        keyName = "arena/"+sObj.getBranchId()+"/"+sObj.getClassCourseSectionId()+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+ FileUtil.from(ArPollDisplayRejectedActivity.this,dUri).getName();

                        PutObjectRequest por = new PutObjectRequest(
                                sh_Pref.getString("bucket", ""),
                                keyName,
                                FileUtil.from(ArPollDisplayRejectedActivity.this,dUri));
                        por.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3Client1.putObject(por);

                        mapKeyNames.put("D",keyName);
                    }
                    /*uploading optionB*/


                    /*uploading options*/

                    updatePoll();

                }catch (IOException e){
                    e.printStackTrace();
                }



            }
        }).start();

    }

    void updatePoll(){
        JSONObject fileObj = new JSONObject();

        try {
            if(mapKeyNames.containsKey("question")){
                fileObj.put("question",etMcqQues.getText().toString()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("question")));
            }else {
                if (listQs.get(0).getQuestion()!=null || listQs.get(0).getQuestion().equalsIgnoreCase("") || listQs.get(0).getQuestion().equalsIgnoreCase("NA")){
                    if (ivQImage.getVisibility()!=View.VISIBLE){
                        fileObj.put("question", etMcqQues.getText().toString().trim());
                    }else {
                        String[] s1 = listQs.get(0).getQuestion().split("~~");
                        fileObj.put("question", etMcqQues.getText().toString().trim()+"~~link~~"+s1[s1.length-1]);
                    }
                }else {
                    fileObj.put("question", etMcqQues.getText().toString().trim());
                }
            }

            fileObj.put("questType","");

            if (mapKeyNames.containsKey("A")){
                fileObj.put("option1",etOptionTextA.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("A")));
            }else {
                if (listQs.get(0).getOption1()!=null || listQs.get(0).getOption1().equalsIgnoreCase("") || listQs.get(0).getOption1().equalsIgnoreCase("NA")){
                    if (ivSelectedImageA.getVisibility()!=View.VISIBLE){
                        fileObj.put("option1", etOptionTextA.getText().toString().trim());
                    }else {
                        String[] s1 = listQs.get(0).getOption1().split("~~");
                        fileObj.put("option1", etOptionTextA.getText().toString().trim()+"~~link~~"+s1[s1.length-1]);
                    }
                }else {
                    fileObj.put("option1", etOptionTextA.getText().toString().trim());
                }
            }
            if (mapKeyNames.containsKey("B")){
                fileObj.put("option2",etOptionTextB.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("B")));
            }else {
                if (listQs.get(0).getOption2()!=null || listQs.get(0).getOption2().equalsIgnoreCase("") || listQs.get(0).getOption2().equalsIgnoreCase("NA")){
                    if (ivSelectedImageB.getVisibility()!=View.VISIBLE){
                        fileObj.put("option2", etOptionTextB.getText().toString().trim());
                    }else {
                        String[] s1 = listQs.get(0).getOption2().split("~~");
                        fileObj.put("option2", etOptionTextB.getText().toString().trim()+"~~link~~"+s1[s1.length-1]);
                    }
                }else {
                    fileObj.put("option2", etOptionTextB.getText().toString().trim());
                }
            }
            if (cvC.getVisibility()==View.VISIBLE){
                if (mapKeyNames.containsKey("C")){
                    fileObj.put("option3",etOptionTextC.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("C")));
                }else {
                    if (listQs.get(0).getOption3()!=null || listQs.get(0).getOption3().equalsIgnoreCase("") || listQs.get(0).getOption3().equalsIgnoreCase("NA")){
                        if (ivSelectedImageC.getVisibility()!=View.VISIBLE){
                            fileObj.put("option3", etOptionTextC.getText().toString().trim());
                        }else {
                            String[] s1 = listQs.get(0).getOption3().split("~~");
                            fileObj.put("option3", etOptionTextC.getText().toString().trim()+"~~link~~"+s1[s1.length-1]);
                        }
                    }else {
                        fileObj.put("option3", etOptionTextC.getText().toString().trim());
                    }
                }
            }else {
                fileObj.put("option3","");
            }
            if (cvD.getVisibility()==View.VISIBLE){
                if (mapKeyNames.containsKey("D")){
                    fileObj.put("option4",etOptionTextD.getText().toString().trim()+"~~link~~"+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),mapKeyNames.get("D")));
                }else {
                    if (listQs.get(0).getOption4()!=null || listQs.get(0).getOption4().equalsIgnoreCase("") || listQs.get(0).getOption4().equalsIgnoreCase("NA")){
                        if (ivSelectedImageD.getVisibility()!=View.VISIBLE){
                            fileObj.put("option4", etOptionTextD.getText().toString().trim());
                        }else {
                            String[] s1 = listQs.get(0).getOption4().split("~~");
                            fileObj.put("option4", etOptionTextD.getText().toString().trim()+"~~link~~"+s1[s1.length-1]);
                        }
                    }else {
                        fileObj.put("option4", etOptionTextD.getText().toString().trim());
                    }
                }
            }else {
                fileObj.put("option4","");
            }

            fileObj.put("answer","");
            fileObj.put("queTime","0");
            fileObj.put("schemaName",sh_Pref.getString("schema",""));
            fileObj.put("updatedBy",sObj.getStudentId());
            fileObj.put("arenaQuestionId",listQs.get(0).getArenaQuestionId());

        }catch (JSONException e){
            e.printStackTrace();
        }

        utils.showLog(TAG,"post obj "+fileObj);
        utils.showLog(TAG,"post url "+AppUrls.UpdateArenaQuestion);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, String.valueOf(fileObj));

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
                runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTeachers(pollObj.getArenaId()+"");
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
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
                        new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nPoll Article saved in drafts in my polls. Can submit for approval later.")

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
                            new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nPoll Article saved in drafts in my polls. Can submit for approval later.")

                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
                                        Dialog dialog = new Dialog(ArPollDisplayRejectedActivity.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my audio. Can submit for approval later.")

                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                finish();
                                                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });

                                        RecyclerView rvTeachers = dialog.findViewById(R.id.rv_teachers);
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(ArPollDisplayRejectedActivity.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nPoll article cannot be sent for approval\nPlease contact your school for more information.")

                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
                                    new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nPoll Article saved in drafts in my polls. Can submit for approval later.")

                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
            postObject.put("arenaStatus", "0");
            postObject.put("createdBy", sObj.getStudentId()+"");
            postObject.put("userId", sObj.getStudentId()+"");
            postObject.put("teacherId", teacherId);
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

        utils.showLog(TAG,"url "+url);
        utils.showLog(TAG,"url obj "+postObject.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
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
                            new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
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
                                    Toast.makeText(ArPollDisplayRejectedActivity.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(ArPollDisplayRejectedActivity.this)
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
            return new ViewHolder(LayoutInflater.from(ArPollDisplayRejectedActivity.this).inflate(R.layout.tv_teacher_name,parent,false));
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}