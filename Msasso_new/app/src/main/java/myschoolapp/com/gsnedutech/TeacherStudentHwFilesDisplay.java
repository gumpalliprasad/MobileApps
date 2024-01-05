package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailsTeacher;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Models.TeacherHWStudentofHWObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherStudentHwFilesDisplay extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherStudentHwFilesDisplay.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.img_file)
    ImageView ivSubmission;
    @BindView(R.id.et_marks)
    EditText etRemarks;
    @BindView(R.id.left)
    ImageView left;
    @BindView(R.id.right)
    ImageView right;
    @BindView(R.id.tv_edited)
    TextView tvEdited;
    @BindView(R.id.rv_comments)
    RecyclerView rvComments;
    @BindView(R.id.cb_edit)
    CheckBox cbEdit;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;

    HomeWorkDetailsTeacher hwObj;
    TeacherHWStudentofHWObj studentofHWObj;
    List<StudentHWFile> fileList = new ArrayList<>();

    int evaluated = 0;

    TeacherObj teacherObj;

    String statusType;

    int pos;

    List<String> comment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_student_hw_files_display);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ((TextView)findViewById(R.id.tv_title)).setText(hwObj.getType()+"/"+hwObj.getSubjectName());


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
            utils.alertDialog(1, TeacherStudentHwFilesDisplay.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
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

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("hwObj");
        studentofHWObj = (TeacherHWStudentofHWObj) getIntent().getSerializableExtra("student");
        fileList.addAll((Collection<? extends StudentHWFile>) getIntent().getSerializableExtra("files"));
        pos = Integer.parseInt(getIntent().getStringExtra("filePos"));

        evaluated = fileList.get(pos).getEvaluated();


        utils.showLog("tag"," file url- "+ fileList.get(pos).getStudentHWFilePath());

        Picasso.with(this).load(fileList.get(pos).getStudentHWFilePath()).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSubmission);

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

        if (fileList.size()>1){
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);


            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(--pos<=0){
                        pos=0;
                    }

                    evaluated = fileList.get(pos).getEvaluated();


                    if (evaluated!=0){
                        cbEdit.setChecked(true);
                    }else{
                        cbEdit.setChecked(false);
                    }

                    utils.showLog("tag"," file url- "+ fileList.get(pos).getStudentHWFilePath());


                    Picasso.with(TeacherStudentHwFilesDisplay.this).load(fileList.get(pos).getStudentHWFilePath()).placeholder(R.drawable.progress_animation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSubmission);

                    updateRecycler();
                }
            });

            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (++pos>=(fileList.size()-1)){
                        pos = fileList.size()-1;
                    }

                    evaluated = fileList.get(pos).getEvaluated();

                    if (evaluated!=0){
                        cbEdit.setChecked(true);
                    }else{
                        cbEdit.setChecked(false);
                    }

                    utils.showLog("tag"," file url- "+ fileList.get(pos).getStudentHWFilePath());

                    Picasso.with(TeacherStudentHwFilesDisplay.this).load(fileList.get(pos).getStudentHWFilePath()).placeholder(R.drawable.progress_animation)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSubmission);

                    updateRecycler();
                }
            });

        }else{
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.iv_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etRemarks.getText().toString().trim().length()>0){
                    fileList.get(pos).setTeacherFeedback(etRemarks.getText().toString());
                    postComment();

                }
            }
        });


        if (studentofHWObj.getHWStatus().equalsIgnoreCase("completed")){
            if (evaluated!=0){
                cbEdit.setChecked(true);
            }else{
                cbEdit.setChecked(false);
            }
            cbEdit.setEnabled(false);
            etRemarks.setEnabled(false);
        }

        if (studentofHWObj.getHWStatus().equalsIgnoreCase("reassign")){
            if (evaluated!=0){
                cbEdit.setChecked(false);
            }else{
                cbEdit.setChecked(true);
            }
            cbEdit.setEnabled(true);
        }


        cbEdit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    evaluated=1;
                }else{
                    evaluated=0;
                }
            }
        });


        updateRecycler();

        findViewById(R.id.iv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent = new Intent(TeacherStudentHwFilesDisplay.this, EditImageActivity.class);
                editIntent.putExtra("filePath",fileList.get(pos).getStudentHWFilePath());
                editIntent.putExtra("rollno",getIntent().getStringExtra("rollno"));
                editIntent.putExtra("hwId",getIntent().getStringExtra("hwId"));
                startActivityForResult(editIntent,1234);
            }
        });


        findViewById(R.id.tv_edited).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileList.get(pos).getTeacherEvaluatePath()!=null && !(fileList.get(pos).getTeacherEvaluatePath().equalsIgnoreCase("NA"))){
                    if (tvEdited.getText().toString().trim().equalsIgnoreCase("View Edited File")){
                        Picasso.with(TeacherStudentHwFilesDisplay.this).load(fileList.get(pos).getTeacherEvaluatePath()).placeholder(R.drawable.user_default)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSubmission);


                        tvEdited.setText("View Original File");
                    }
                    else{

                        Picasso.with(TeacherStudentHwFilesDisplay.this).load(fileList.get(pos).getStudentHWFilePath()).placeholder(R.drawable.user_default)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSubmission);
                        tvEdited.setText("View Edited File");
                    }
                }
                else {
                    Toast.makeText(TeacherStudentHwFilesDisplay.this, "File is not edited by Teacher", Toast.LENGTH_SHORT).show();
                }


            }
        });

        statusType = getIntent().getStringExtra("statusType");


        if(statusType.equalsIgnoreCase("completed")){
            findViewById(R.id.ll_comment).setVisibility(View.GONE);
            findViewById(R.id.iv_edit).setVisibility(View.GONE);
        }


    }

    private void updateRecycler() {

        comment.clear();
        for (int i=0;i<fileList.size();i++){
            if (i==pos){
                if (!fileList.get(pos).getTeacherFeedback().equalsIgnoreCase("NA")){
                    comment.add(fileList.get(pos).getTeacherFeedback());
                    rvComments.setLayoutManager(new LinearLayoutManager(this));
                    rvComments.setAdapter(new CommentAdapter(comment));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == 4321){
            if (data!=null && !(data.getStringExtra("editFileUrl").isEmpty()) )
                fileList.get(pos).setTeacherEvaluatePath(data.getStringExtra("editFileUrl"));

            Picasso.with(this).load(fileList.get(pos).getTeacherEvaluatePath()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivSubmission);
            tvEdited.setText("View Original File");
        }
    }

    void postComment(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("schemaName", sh_Pref.getString("schema", ""));
            json.put("studentHWFileId", fileList.get(pos).getStudentHWFileId());
            json.put("updatedBy", String.valueOf(teacherObj.getUserId()));
            json.put("teacherFeedback", fileList.get(pos).getTeacherFeedback());
            json.put("teacherEvaluatePath", fileList.get(pos).getTeacherEvaluatePath());
            json.put("evaluated", evaluated+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(new AppUrls().UPDATESTUDENTFILEFEEDBACK)
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
//                        feedback = "";
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   etRemarks.setText("");
                                   updateRecycler();
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


    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

        List<String> comments;

        public CommentAdapter(List<String> comments) {
            this.comments = comments;
        }

        @NonNull
        @Override
        public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherStudentHwFilesDisplay.this).inflate(R.layout.item_file_comments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
            holder.tvComment.setText(comments.get(position));
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvComment;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvComment = itemView.findViewById(R.id.tv_teacher_fb);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}