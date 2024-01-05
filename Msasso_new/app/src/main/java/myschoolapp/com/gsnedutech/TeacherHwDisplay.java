package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
import okhttp3.Response;

public class TeacherHwDisplay extends AppCompatActivity {

    private static final String TAG = TeacherHwDisplay.class.getName();

    @BindView(R.id.tv_sub_name)
    TextView tvSubName;
    @BindView(R.id.tv_hw_desc)
    TextView tvHwDesc;

    @BindView(R.id.tv_submitted)
    TextView tvSubmitted;
    @BindView(R.id.tv_count)
    TextView tvCount;

    @BindView(R.id.tv_assigned)
    TextView tvAssigned;
    @BindView(R.id.tv_t_count)
    TextView tvTCount;

    @BindView(R.id.tv_reviewed)
    TextView tvReviewed;
    @BindView(R.id.tv_tot_count)
    TextView tvTotCount;

    @BindView(R.id.tv_reassigned)
    TextView tvReassigned;
    @BindView(R.id.tv_total_count)
    TextView tvTotalCount;

    HomeWorkDetailsTeacher hwObj;

    TeacherObj teacherObj;
    SharedPreferences sh_Pref;

    MyUtils utils = new MyUtils();

    List<TeacherHWStudentofHWObj> teachstndlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_hw_display);
        ButterKnife.bind(this);

        init();

    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");

//        if (hwObj.getTeacherFilesDetails().size()>0){
//            findViewById(R.id.ll_view_attachments).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(TeacherHwDisplay.this,TeacherHwFileDisplay.class);
//                    intent.putExtra("files",(Serializable) hwObj.getFilesDetails());
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                }
//            });
//        }else{
//            findViewById(R.id.ll_view_attachments).setAlpha(0.5f);
//        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.cv_submission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHwDisplay.this,TeacherHwStudentSubmission.class);
                intent.putExtra("obj",(Serializable) hwObj);
                intent.putExtra("type","submitted");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        findViewById(R.id.cv_assigned).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHwDisplay.this,TeacherHwStudentSubmission.class);
                intent.putExtra("obj",(Serializable) hwObj);
                intent.putExtra("type","assigned");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.cv_re_assigned).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHwDisplay.this,TeacherHwStudentSubmission.class);
                intent.putExtra("obj",(Serializable) hwObj);
                intent.putExtra("type","reassign");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.cv_reviewed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHwDisplay.this,TeacherHwStudentSubmission.class);
                intent.putExtra("obj",(Serializable) hwObj);
                intent.putExtra("type","completed");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        tvSubName.setText(hwObj.getSubjectName());
        tvHwDesc.setText(hwObj.getHomeworkDetail());





    }


    @Override
    protected void onResume() {
        if (NetworkConnectivity.isConnected(this)) {
            getStudentSubmission();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
        super.onResume();
    }

    void getStudentSubmission(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().HOMEWORK_GetStudentsofHomework + "schemaName=" + sh_Pref.getString("schema","") + "&branchId=" + teacherObj.getBranchId() + "&sectionId=" + "5" + "&homeworkId=" + hwObj.getHomeworkId());

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetStudentsofHomework + "schemaName=" + sh_Pref.getString("schema","") + "&branchId=" + teacherObj.getBranchId() + "&sectionId=" + "5" + "&homeworkId=" + hwObj.getHomeworkId())
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
                            JSONArray jsonArr = ParentjObject.getJSONArray("homeworkStudentsStatus");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherHWStudentofHWObj>>() {
                            }.getType();


                            teachstndlist = gson.fromJson(jsonArr.toString(), type);
                            List<TeacherHWStudentofHWObj> filter = new ArrayList<>();

                            for (int i=0;i<teachstndlist.size();i++){
                                if(!teachstndlist.get(i).getHWStatus().equalsIgnoreCase("NA")){
                                    filter.add(teachstndlist.get(i));
                                }
                            }

                            teachstndlist.clear();
                            teachstndlist.addAll(filter);

                            int submittedCount = 0;
                            int assignedCount = 0;
                            int reviewedCount = 0;
                            int reassignCount = 0;

                            for (int i=0;i<teachstndlist.size();i++){
                                if (teachstndlist.get(i).getHWStatus().equalsIgnoreCase("Submitted")){
                                    submittedCount++;
                                }
                                if (teachstndlist.get(i).getHWStatus().equalsIgnoreCase("Assigned")){
                                    assignedCount++;
                                }
                                if (teachstndlist.get(i).getHWStatus().equalsIgnoreCase("Reassign")){
                                    reassignCount++;
                                }
                                if (teachstndlist.get(i).getHWStatus().equalsIgnoreCase("completed")){
                                    reviewedCount++;
                                }
                            }


                            int finalSubmittedCount = submittedCount;
                            int finalAssignedCount = assignedCount;
                            int finalReviewedCount = reviewedCount;
                            int finalReassignCount = reassignCount;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvCount.setText("/"+teachstndlist.size());
                                    tvSubmitted.setText(finalSubmittedCount +"");
                                    tvTCount.setText("/"+teachstndlist.size());
                                    tvAssigned.setText(finalAssignedCount +"");
                                    tvTotCount.setText("/"+teachstndlist.size());
                                    tvReviewed.setText(finalReviewedCount +"");
                                    tvTotalCount.setText("/"+teachstndlist.size());
                                    tvReassigned.setText(finalReassignCount +"");
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}