package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class TeacherHWSubmissionDisplay extends AppCompatActivity {

    private static final String TAG = TeacherHwStudentSubmission.class.getName();

    TeacherObj teacherObj;
    SharedPreferences sh_Pref;

    MyUtils utils = new MyUtils();

    HomeWorkDetailsTeacher hwObj;
    TeacherHWStudentofHWObj studentofHWObj;

    List<StudentHWFile> fileList = new ArrayList<>();

    @BindView(R.id.rv_files)
    RecyclerView rvFiles;
    @BindView(R.id.sp_remark)
    Spinner spRemark;
    @BindView(R.id.cb_edit)
    CheckBox cbEdit;
    @BindView(R.id.et_marks)
    EditText etMarks;
    List<String> listOptions = new ArrayList<>();
    String rating;

    String statusType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_h_w_submission_display);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.cv_evaluated).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etMarks.getText().toString().length()>0){

                    int marks = Integer.parseInt(etMarks.getText().toString());
                    postEvaluation(marks+"");
                }else {
                    Toast.makeText(TeacherHWSubmissionDisplay.this,"Please Enter Marks!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");
        studentofHWObj = (TeacherHWStudentofHWObj) getIntent().getSerializableExtra("student");


        listOptions.add("Poor");
        listOptions.add("Average");
        listOptions.add("Good");
        listOptions.add("Excellent");
        listOptions.add("OutStanding");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(TeacherHWSubmissionDisplay.this, R.layout.spinner_test_item, listOptions);
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





        statusType = getIntent().getStringExtra("statusType");

        if (statusType.equalsIgnoreCase("completed")){

            int index = listOptions.indexOf(studentofHWObj.getTeacherComments());
            spRemark.setSelection(index);
            spRemark.setEnabled(false);
            etMarks.setText(studentofHWObj.getMarks()+"");
            etMarks.setEnabled(false);
            findViewById(R.id.cb_edit).setVisibility(View.GONE);
            findViewById(R.id.cv_evaluated).setVisibility(View.GONE);
        }

        if (statusType.equalsIgnoreCase("reassign")){
            int index = listOptions.indexOf(studentofHWObj.getTeacherComments());
            spRemark.setSelection(index);
            etMarks.setText(studentofHWObj.getMarks()+"");
            cbEdit.setChecked(true);
        }

    }

    @Override
    protected void onResume() {

        if (NetworkConnectivity.isConnected(this)) {
            getStudentFiles();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
        super.onResume();
    }

    void getStudentFiles(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().GetHomeWorkFilesByStudent + "schemaName=" + sh_Pref.getString("schema","") + "&studentId=" + studentofHWObj.getStudentId() + "&hwId=" + hwObj.getHomeworkId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetHomeWorkFilesByStudent + "schemaName=" + sh_Pref.getString("schema","") + "&studentId=" + studentofHWObj.getStudentId() + "&hwId=" + hwObj.getHomeworkId())
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
                                        rvFiles.setLayoutManager(new LinearLayoutManager(TeacherHWSubmissionDisplay.this,RecyclerView.HORIZONTAL,false));
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
            this.listFiles = listFiles;
        }

        @NonNull
        @Override
        public StudentFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherHWSubmissionDisplay.this).inflate(R.layout.item_submission_assign_file,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull StudentFilesAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(listFiles.get(position).getStudentHWFilePath().split("/")[listFiles.get(position).getStudentHWFilePath().split("/").length-1]);
            holder.tvVersion.setText(fileList.get(position).getVersion()+"");
            holder.itemView.findViewById(R.id.cv_version).setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherHWSubmissionDisplay.this,TeacherStudentHwFilesDisplay.class);
                    intent.putExtra("hwObj",(Serializable)hwObj);
                    intent.putExtra("files",(Serializable)fileList);
                    intent.putExtra("student",(Serializable)studentofHWObj);
                    intent.putExtra("filePos",""+position);
                    intent.putExtra("statusType",""+statusType);
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

            TextView tvFileName,tvVersion;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
                tvVersion = itemView.findViewById(R.id.tv_version);
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
            json.put("studentHWID",studentofHWObj.getStudentHWId());
            json.put("hwId", hwObj.getHomeworkId());
            json.put("studentId", studentofHWObj.getStudentId());
            json.put("createdBy", teacherObj.getUserId());
            json.put("teacherComments", listOptions.get(spRemark.getSelectedItemPosition()));
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
                                    Toast.makeText(TeacherHWSubmissionDisplay.this, "Success!", Toast.LENGTH_SHORT).show();
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