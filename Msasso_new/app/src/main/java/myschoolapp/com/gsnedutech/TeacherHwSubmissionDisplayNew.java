package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class TeacherHwSubmissionDisplayNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherHwSubmissionDisplayNew.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    HomeWorkDetailsTeacher hwObj;
    TeacherHWStudentofHWObj studentObj;

    @BindView(R.id.sp_remark)
    Spinner spRemark;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_roll_number)
    TextView tvRollNum;
    @BindView(R.id.rv_files)
    RecyclerView rvFiles;
    @BindView(R.id.et_remarks)
    EditText etRemarks;
    @BindView(R.id.cb_edit)
    CheckBox cbEdit;
    @BindView(R.id.ll_submission)
    LinearLayout llSubmission; 
    @BindView(R.id.rv_comments)
    RecyclerView rvComments;

    List<String> listOptions = new ArrayList<>();
    String rating;

    MyUtils utils = new MyUtils();

    List<StudentHWFile> fileList = new ArrayList<>();

    SharedPreferences sh_Pref;

    List<String> listComments = new ArrayList<>();

    TeacherObj teacherObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_hw_submission_display_new);

        ButterKnife.bind(this);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");
        studentObj = (TeacherHWStudentofHWObj) getIntent().getSerializableExtra("student");

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

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
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherHwSubmissionDisplayNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                init();
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
        listComments.clear();
        tvName.setText(studentObj.getStudentName());
        tvRollNum.setText(studentObj.getRollNumber());

        listOptions.clear();

        listOptions.add("Poor");
        listOptions.add("Average");
        listOptions.add("Good");
        listOptions.add("Excellent");
        listOptions.add("OutStanding");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TeacherHwSubmissionDisplayNew.this, R.layout.spinner_test_item, listOptions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRemark.setAdapter(dataAdapter);

        spRemark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        rating = "1";
                        break;
                    case 1:
                        rating = "2";
                        break;
                    case 2:
                        rating = "3";
                        break;
                    case 3:
                        rating = "4";
                        break;
                    case 4:
                        rating = "5";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //If completed or reassign control

        if (studentObj.getHWStatus().equalsIgnoreCase("SUBMITTED")){
            rating = studentObj.getHwRating()+"";
            spRemark.setSelection((studentObj.getHwRating()-1));
            findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etRemarks.getText().toString().length()>0){
                        postEvaluation(0+"");
                    }else {
                        Toast.makeText(TeacherHwSubmissionDisplayNew.this,"Please Enter Marks!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (studentObj.getHWStatus().equalsIgnoreCase("Completed")){
            rating = studentObj.getHwRating()+"";
            spRemark.setSelection((studentObj.getHwRating()-1));
            llSubmission.setVisibility(View.GONE);
            listComments.add(studentObj.getTeacherComments());
            
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(new CommentAdapter(listComments));

            etRemarks.setEnabled(false);
            spRemark.setEnabled(false);
            cbEdit.setEnabled(false);
            findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etRemarks.getText().toString().length()>0){
                        postEvaluation(0+"");
                    }else {
                        Toast.makeText(TeacherHwSubmissionDisplayNew.this,"Please Enter Marks!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (studentObj.getHWStatus().equalsIgnoreCase("Reassign")){
            rating = studentObj.getHwRating()+"";
            spRemark.setSelection((studentObj.getHwRating()-1));
            cbEdit.setChecked(true);
            listComments.add(studentObj.getTeacherComments());
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            rvComments.setAdapter(new CommentAdapter(listComments));
        }




        if (NetworkConnectivity.isConnected(this)) {
            getStudentFiles();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

    }

    void getStudentFiles(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().GetHomeWorkFilesByStudent + "schemaName=" + sh_Pref.getString("schema","") + "&studentId=" + studentObj.getStudentId() + "&hwId=" + hwObj.getHomeworkId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetHomeWorkFilesByStudent + "schemaName=" + sh_Pref.getString("schema","") + "&studentId=" + studentObj.getStudentId() + "&hwId=" + hwObj.getHomeworkId())
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
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = jsonObject.getJSONArray("studentHWFiles");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentHWFile>>() {
                            }.getType();

                            fileList.clear();
                            fileList = gson.fromJson(jsonArray.toString(), type);
                            utils.showLog(TAG, "hwListSize - " + fileList.size());

                            if (fileList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvFiles.setLayoutManager(new LinearLayoutManager(TeacherHwSubmissionDisplayNew.this, RecyclerView.HORIZONTAL,false));
                                        rvFiles.setAdapter(new StudentFilesAdapter(fileList));
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

    class StudentFilesAdapter extends RecyclerView.Adapter<StudentFilesAdapter.ViewHolder>{

        List<StudentHWFile> listFiles;

        public StudentFilesAdapter(List<StudentHWFile> listFiles) {
            this.listFiles=listFiles;
        }

        @NonNull
        @Override
        public StudentFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherHwSubmissionDisplayNew.this).inflate(R.layout.item_hw_file_sub,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull StudentFilesAdapter.ViewHolder holder, int position) {

            if (listFiles.get(position).getStudentHWFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_pdf);
            }else if(listFiles.get(position).getStudentHWFilePath().contains(".jpg") || listFiles.get(position).getStudentHWFilePath().contains(".png") || listFiles.get(position).getStudentHWFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(listFiles.get(position).getStudentHWFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }

            holder.tvFileName.setText(listFiles.get(position).getStudentHWFilePath().split("/")[listFiles.get(position).getStudentHWFilePath().split("/").length-1]);
            if (listFiles.get(position).getEvaluated()==1){
                holder.flIncorrect.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherHwSubmissionDisplayNew.this,TeacherStudentHwFilesDisplay.class);
                    intent.putExtra("hwObj",(Serializable)hwObj);
                    intent.putExtra("files",(Serializable)fileList);
                    intent.putExtra("student",(Serializable)studentObj);
                    intent.putExtra("filePos",""+position);
                    intent.putExtra("statusType",""+hwObj.getType());
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivFile;
            TextView tvFileName;
            FrameLayout flIncorrect;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                flIncorrect = itemView.findViewById(R.id.fl_incorrect);
            }
        }
    }

    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

        List<String> comments;

        public CommentAdapter(List<String> comments) {
            this.comments = comments;
        }

        @NonNull
        @Override
        public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CommentAdapter.ViewHolder(LayoutInflater.from(TeacherHwSubmissionDisplayNew.this).inflate(R.layout.item_file_comments,parent,false));
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

    void postEvaluation(String marks){

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
            json.put("studentHWID",studentObj.getStudentHWId());
            json.put("hwId", hwObj.getHomeworkId());
            json.put("studentId", studentObj.getStudentId());
            json.put("createdBy", teacherObj.getUserId());
            json.put("teacherComments", etRemarks.getText().toString());
            json.put("marks", Integer.parseInt(marks));
            json.put("hwRating", rating);

            if (cbEdit.isChecked()) {
                json.put("hwStatus", "REASSIGN");
            } else {
                json.put("hwStatus", "COMPLETED");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, json.toString());

        utils.showLog(TAG,"url "+new AppUrls().UpdateStudentHWSubmission);
        utils.showLog(TAG,"url "+json.toString());

        Request request = new Request.Builder()
                .url(new AppUrls().UpdateStudentHWSubmission)
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
                utils.showLog(TAG,"url "+resp);

                if(!response.isSuccessful()){
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherHwSubmissionDisplayNew.this, "Success!", Toast.LENGTH_SHORT).show();
                                    onResume();
                                }
                            });
                            finish();
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
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}