package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
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
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.models.DescriptiveStudents;
import myschoolapp.com.gsnedutech.descriptive.models.TeacherDescExamsObj;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DescriptiveStudentList extends AppCompatActivity {

    private static final String TAG = DescriptiveStudentList.class.getName();


    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_descr_stdnts)
    RecyclerView rvDescStdnts;

    List<DescriptiveStudents> studentList = new ArrayList<>();

    TeacherDescExamsObj descExamsObj;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";
    String sectionId="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desriptive_student_list);
        ButterKnife.bind(this);
        init();
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        descExamsObj = (TeacherDescExamsObj) getIntent().getSerializableExtra("exam");
        sectionId = getIntent().getStringExtra("sectionId");


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvDescStdnts.setLayoutManager(new LinearLayoutManager(this));
        getStudents();

    }

    void getStudents(){
        utils.showLoader(DescriptiveStudentList.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Attendance Student request - " + AppUrls.DGetStudentsExamBySectionIds +"schemaName=" +sh_Pref.getString("schema","")+ "&testId="+descExamsObj.getDescExamId()+"&sectionId="+sectionId);

        Request request = new Request.Builder()
                .url(AppUrls.DGetStudentsExamBySectionIds +"schemaName=" +sh_Pref.getString("schema","")+ "&testId="+descExamsObj.getDescExamId()+"&sectionId="+sectionId)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "Students Types response - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<DescriptiveStudents>>() {
                            }.getType();

                            studentList.clear();
                            studentList.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog(TAG, "studList - " + studentList.size());

                            runOnUiThread(() -> {
                                if (studentList.size()>0)
                                    rvDescStdnts.setAdapter(new StudentListAdapter());
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }


    class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(DescriptiveStudentList.this).inflate(R.layout.item_descr_student, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
            viewHolder.tvName.setText(studentList.get(i).getStudentName());
            viewHolder.tvRoll.setText(studentList.get(i).getAdmissionNumber());
            viewHolder.tvStatus.setText(studentList.get(i).getTestStatus());

            viewHolder.itemView.setOnClickListener(v -> {
                if(studentList.get(i).getTestStatus().equalsIgnoreCase("Inprogress") ||
                    studentList.get(i).getTestStatus().equalsIgnoreCase("submitted")) {
                    Intent intent = new Intent(DescriptiveStudentList.this, TeacherDescriptiveSectionDetail.class);
                    intent.putExtra("testId", descExamsObj.getDescExamId());
                    intent.putExtra("studentId", studentList.get(i).getStudentId());
                    intent.putExtra("testFilePath", descExamsObj.getExamPath());
                    intent.putExtra("studentTestFilePath", studentList.get(i).getTestStatus());
                    startActivity(intent);
                }
                else {
                    new AlertDialog.Builder(DescriptiveStudentList.this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("Test not submitted by Student")
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss() )
                            .setCancelable(false)
                            .show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return studentList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            TextView tvRoll, tvName, tvStatus;

            ViewHolder(View itemView) {
                super(itemView);
                tvRoll = itemView.findViewById(R.id.tv_roll_number);
                tvName = itemView.findViewById(R.id.tv_name);
                tvStatus = itemView.findViewById(R.id.tv_status);


            }

        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}