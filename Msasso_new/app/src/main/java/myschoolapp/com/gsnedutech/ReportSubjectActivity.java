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
import android.widget.ImageView;
import android.widget.RatingBar;
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
import myschoolapp.com.gsnedutech.Models.AnalysisTestClass;
import myschoolapp.com.gsnedutech.Models.AnalysisTestSubject;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReportSubjectActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + ReportSubjectActivity.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_subjects_report)
    RecyclerView rvSubjects; 
    
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_test_taken)
    TextView tvTestTaken;
    @BindView(R.id.tv_avg_score)
    TextView tvAvgScore;
    @BindView(R.id.tv_avg_time_spent)
    TextView tvTimeSpent;
    
    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String courseId,classId,studentId,branchId;

    AnalysisTestClass classObj;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_subject);

        ButterKnife.bind(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        init();

        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
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
            utils.alertDialog(1, ReportSubjectActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getAnalysisSub();
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
        classObj = (AnalysisTestClass) getIntent().getSerializableExtra("analysisTestClass");
        tvTitle.setText(classObj.getClassName()+" Performance");
        tvTestTaken.setText(classObj.getTestCount()+"");
        tvAvgScore.setText(classObj.getPercentage() + "%");
        tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", classObj.getTotalTimeSpent() / 3600, (classObj.getTotalTimeSpent() % 3600) / 60, classObj.getTotalTimeSpent() % 60));

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);

            courseId = getIntent().getStringExtra("courseId");
            classId = classObj.getClassId()+"";
            studentId = sObj.getStudentId();
            branchId = sObj.getBranchId();
        }
        else {
            courseId = getIntent().getStringExtra("courseId");
            classId = getIntent().getStringExtra("classId");
            studentId = getIntent().getStringExtra("studentId");
            branchId = getIntent().getStringExtra("branchId");
        }


    }

    private void getAnalysisSub() {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetSubjectAnalysisByclass +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&courseId=" + courseId + "&studentId=" + studentId + "&classId=" + classId);

        Request request = new Request.Builder()
                .url(new AppUrls().GetSubjectAnalysisByclass +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&courseId=" + courseId + "&studentId=" + studentId + "&classId=" + classId)
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
                            JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisSubjectArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AnalysisTestSubject>>() {
                            }.getType();

                            List<AnalysisTestSubject> listSubPerformance = new ArrayList<>();
                            listSubPerformance.addAll(gson.fromJson(jsonArr.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvSubjects.setAdapter(new SubjectsAdapter(listSubPerformance));
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

    class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder>{

        List<AnalysisTestSubject> listSubPerformance;

        public SubjectsAdapter(List<AnalysisTestSubject> listSubPerformance) {
            this.listSubPerformance = listSubPerformance;
        }

        @NonNull
        @Override
        public SubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReportSubjectActivity.this).inflate(R.layout.item_report_subject_details,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectsAdapter.ViewHolder holder, int position) {
            holder.tvSubName.setText(listSubPerformance.get(position).getSubjectName());
            holder.tvAvgScore.setText("" + new MyUtils().roundTwoDecimals(listSubPerformance.get(position).getPercentage()) + "%");
            holder.tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", Integer.parseInt(listSubPerformance.get(position).getTotalTimeSpent() ) / 3600, Integer.parseInt(listSubPerformance.get(position).getTotalTimeSpent() )  / 60, Integer.parseInt(listSubPerformance.get(position).getTotalTimeSpent() )  % 60));
            holder.tvTestTaken.setText(listSubPerformance.get(position).getTestCount() + "");
            float rating = (float)(listSubPerformance.get(position).getPercentage()*5)/100;
            holder.rb.setRating(rating);

            switch (listSubPerformance.get(position).getSubjectGroup()) {

                case "Maths":
                    holder.ivSub.setImageResource(R.drawable.ic_maths_icon_white);
                    break;
                case "Science":
                    holder.ivSub.setImageResource(R.drawable.ic_science_white);
                    break;
                case "English":
                    holder.ivSub.setImageResource(R.drawable.ic_english_white);
                    break;
                case "GeneralKnowledge":
                    holder.ivSub.setImageResource(R.drawable.ic_gk_white);
                    break;
                case "SocialScience":
                case "Social Studies":
                    holder.ivSub.setImageResource(R.drawable.ic_social_white);
                    break;
                case "Hindi":
                    holder.ivSub.setImageResource(R.drawable.ic_hindi_white);
                    break;
                case "Telugu":
                    holder.ivSub.setImageResource(R.drawable.ic_telugu_white);
                    break;
                case "Physics":
                    holder.ivSub.setImageResource(R.drawable.ic_physics_white);
                    break;
                case "Chemistry":
                    holder.ivSub.setImageResource(R.drawable.ic_chemistry_white);
                    break;
                case "Biology":
                    holder.ivSub.setImageResource(R.drawable.ic_biology_white);
                    break;
                default:
                    holder.ivSub.setImageResource(R.drawable.ic_science_white);
                    break;
            }
            if (Integer.parseInt(listSubPerformance.get(position).getTestCount())>0){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ReportSubjectActivity.this, ReportSubChapterActivity.class);
                        intent.putExtra("subjects", (Serializable) listSubPerformance);
                        intent.putExtra("pos", position);
                        intent.putExtra("classId", classId);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("studentId", studentId);
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
            return listSubPerformance.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubName;
            TextView tvTestTaken,tvTimeSpent,tvAvgScore;
            RatingBar rb;
            ImageView ivSub;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvTestTaken = itemView.findViewById(R.id.tv_test_taken);
                tvAvgScore = itemView.findViewById(R.id.tv_avg_score);
                tvTimeSpent = itemView.findViewById(R.id.tv_time_spent);
                rb = itemView.findViewById(R.id.rb);
                ivSub = itemView.findViewById(R.id.iv_sub);
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}