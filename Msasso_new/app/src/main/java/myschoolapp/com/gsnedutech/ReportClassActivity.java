package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AnalysisTest;
import myschoolapp.com.gsnedutech.Models.AnalysisTestClass;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReportClassActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + ReportClassActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    @BindView(R.id.rv_classes)
    RecyclerView rvClasses;

    MyUtils utils = new MyUtils();

    String studentId="", branchId="", courseId="", courseName="", classId="", rollNumber="", profilePic="", className="", studentName="";

    List<AnalysisTest> listPerformance = new ArrayList<>();

    @BindView(R.id.tv_test_taken)
    TextView tvTestTaken;
    @BindView(R.id.tv_avg_score)
    TextView tvAvgScore;
    @BindView(R.id.tv_avg_time_spent)
    TextView tvTimeSpent;

    @BindView(R.id.tv_student_name)
    TextView tvStudentName;
    @BindView(R.id.tv_class_name)
    TextView tvClassName;
    @BindView(R.id.tv_student_id)
    TextView tvStudentId;
    @BindView(R.id.dp)
    CircleImageView dp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_report_class);
        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();

        rvClasses.setLayoutManager(new LinearLayoutManager(this));
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
            utils.alertDialog(1, ReportClassActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getAnalysisClass();
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
        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            studentId = sObj.getStudentId();
            courseId = ""+sObj.getCourseId();
            classId = ""+sObj.getClassId();
            courseName = sObj.getCourseName();
            branchId = sObj.getBranchId();
            rollNumber = sObj.getStudentRollnumber();
            className = sObj.getClassName();
            profilePic =  sObj.getProfilePic();
            studentName = sObj.getStudentName();
        }
        else {
            studentId = getIntent().getStringExtra("studentId");
            courseId = getIntent().getStringExtra("courseId");
            classId = getIntent().getStringExtra("classId");
            courseName = getIntent().getStringExtra("courseName");
            branchId = getIntent().getStringExtra("branchId");
            rollNumber = getIntent().getStringExtra("rollNumber");
            className = getIntent().getStringExtra("className");
            profilePic =  getIntent().getStringExtra("studentProfilePic");
            studentName = getIntent().getStringExtra("studentName");
        }

        Picasso.with(ReportClassActivity.this).load(profilePic).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(dp);

        tvStudentId.setText(rollNumber);
        tvStudentName.setText(studentName);
        tvClassName.setText(className);

//        studentId = sObj.getStudentId();
//        branchId = sObj.getBranchId();

//        courseId = getIntent().getStringExtra("courseId");
        listPerformance.addAll((Collection<? extends AnalysisTest>) getIntent().getSerializableExtra("analysisTestClass"));

        int totalTests = 0, totalTime = 0;
        Float totalPercentage = 0f;
        for (int i = 0; i < listPerformance.size(); i++) {
            totalTests = totalTests + Integer.parseInt(listPerformance.get(i).getTestCount());
            totalTime = totalTime + Integer.parseInt(listPerformance.get(i).getTotalTimeSpent());
            totalPercentage = totalPercentage + Float.parseFloat(listPerformance.get(i).getPercentage());
        }
        totalPercentage = totalPercentage / listPerformance.size();


        tvTestTaken.setText("" + totalTests);
        tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60));
        tvAvgScore.setText("" + MyUtils.roundTwoDecimals(totalPercentage) + "%");



    }

    private void getAnalysisClass() {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetTotalStudentMockTestsByClass+"schemaName=" +sh_Pref.getString("schema","") + "&courseId=" + courseId + "&studentId=" + studentId + "&branchId=" + branchId);

        Request request = new Request.Builder()
                .url(new AppUrls().GetTotalStudentMockTestsByClass +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + courseId + "&studentId=" + studentId + "&branchId=" + branchId)
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
                            JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisClassArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AnalysisTestClass>>() {
                            }.getType();

                            List<AnalysisTestClass> listClassPerformance = new ArrayList<>();

                            listClassPerformance.clear();
                            listClassPerformance.addAll(gson.fromJson(jsonArr.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listClassPerformance.size()>0){
                                        rvClasses.setAdapter(new ClassAdapter(listClassPerformance));
                                    }
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


    class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder>{

        List<AnalysisTestClass> listClassPerformance;

        public ClassAdapter(List<AnalysisTestClass> listClassPerformance) {
            this.listClassPerformance = listClassPerformance;
        }

        @NonNull
        @Override
        public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReportClassActivity.this).inflate(R.layout.item_course_overall_performance,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, int position) {
            holder.tvCourseName.setText(listClassPerformance.get(position).getClassName());
            holder.tvTestTaken.setText(listClassPerformance.get(position).getTestCount()+"");
            holder.tvAvgScore.setText(listClassPerformance.get(position).getPercentage() + "%");
            holder.tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", listClassPerformance.get(position).getTotalTimeSpent() / 3600, (listClassPerformance.get(position).getTotalTimeSpent() % 3600) / 60, listClassPerformance.get(position).getTotalTimeSpent() % 60));
            float rating = (listClassPerformance.get(position).getPercentage()*5)/100;
            holder.rb.setRating(rating);
            if (listClassPerformance.get(position).getTestCount()>0){

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ReportClassActivity.this, ReportSubjectActivity.class);
                        intent.putExtra("analysisTestClass",(Serializable) listClassPerformance.get(position));
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("classId", classId);
                        intent.putExtra("branchId", branchId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                });

            }else {
                holder.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public int getItemCount() {
            return listClassPerformance.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCourseName;
            TextView tvTestTaken,tvTimeSpent,tvAvgScore;
            RatingBar rb;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCourseName = itemView.findViewById(R.id.tv_course_name);
                tvTestTaken = itemView.findViewById(R.id.tv_test_taken);
                tvAvgScore = itemView.findViewById(R.id.tv_avg_score);
                tvTimeSpent = itemView.findViewById(R.id.tv_avg_time_spent);
                rb = itemView.findViewById(R.id.rb);
            }
        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}