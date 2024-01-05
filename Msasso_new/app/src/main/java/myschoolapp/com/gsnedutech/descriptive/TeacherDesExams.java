package myschoolapp.com.gsnedutech.descriptive;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.models.TeacherDescExamsObj;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherDesExams extends AppCompatActivity {

    private static final String TAG = TeacherDesExams.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_desc_exams)
    RecyclerView rvDescExams;

    List<TeacherDescExamsObj> examsObjs = new ArrayList<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;
    AdminObj adminObj;
    String userId, branchId;
    DatePickerDialog datePickerDialog;
    String subjectId = "", sectionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_des_exams);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        sectionId = getIntent().getStringExtra("sectionId");
        subjectId = getIntent().getStringExtra("subjectId");
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            userId = "" + tObj.getUserId();
            branchId = tObj.getBranchId();
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            String json = sh_Pref.getString("adminObj", "");
            adminObj = gson.fromJson(json, AdminObj.class);
            userId = "" + adminObj.getUserId();
            branchId = adminObj.getBranchId();
        }
        rvDescExams.setLayoutManager(new LinearLayoutManager(this));

        getDescExams();

    }

    void getDescExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "URL - " + AppUrls.GetTeacherDescExams + "userId=" + userId + "&schemaName=" + sh_Pref.getString("schema", "") + "&sectionId=" + sectionId + "&subjectId=" + subjectId);

        Request request = new Request.Builder()
                .url(AppUrls.GetTeacherDescExams + "userId=" + userId + "&schemaName=" + sh_Pref.getString("schema", "") + "&sectionId=" + sectionId + "&subjectId=" + subjectId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        findViewById(R.id.cv_no_active_exams).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);


                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("descExams");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherDescExamsObj>>() {
                            }.getType();
                            examsObjs.clear();
                            examsObjs.addAll(gson.fromJson(jsonArr.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (examsObjs.size() > 0) {
                                        rvDescExams.setAdapter(new DescExamAdapter());
                                    }
                                }
                            });


                        } else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {
                            runOnUiThread(() -> {

                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {

                        });
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }


            }
        });
    }

    private class DescExamAdapter extends RecyclerView.Adapter<DescExamAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.v(TAG, "onCreateViewHolder");
            View view_adapter = LayoutInflater.from(TeacherDesExams.this).inflate(R.layout.item_teacher_upcoming_tests, parent, false);
            return new ViewHolder(view_adapter);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvTestName.setText(examsObjs.get(position).getExamName());
            holder.tvTestStartDate.setText(examsObjs.get(position).getExamStartTime());
//            holder.tvTestEndTime.setText( examsObjs.get(position).getExamEndTime());

//            holder.tvTestStatus.setText(examsObjs.get(position).getExamStatus());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherDesExams.this, DescriptiveStudentList.class);
                    intent.putExtra("exam", examsObjs.get(position));
                    intent.putExtra("sectionId", sectionId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return examsObjs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTestName, tvTestStartDate, tvTestEndTime, tvTestStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTestStartDate = itemView.findViewById(R.id.tv_test_date);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
            }
        }
    }


}