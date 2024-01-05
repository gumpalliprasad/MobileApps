package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.AnalysisTest;
import myschoolapp.com.gsnedutech.Models.Course;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.ReportClassActivity;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MockReportFragment extends Fragment {

    private static final String TAG = "SriRam -" + MockReportFragment.class.getName();

    @BindView(R.id.rv_courses)
    RecyclerView rvCourses;

    View viewRepoDynamicFragment;
    Unbinder unbinder;

    Activity mActivity;

    MyUtils utils = new MyUtils();

    String studentId, branchId, courseId, courseName, classId, rollNumber="", profilePic="", className="", studentName="";

    List<Course> courses_list = new ArrayList<>();

    SharedPreferences sh_Pref;
    StudentObj sObj;

    Course defaultCourse;

    public MockReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewRepoDynamicFragment =  inflater.inflate(R.layout.fragment_mock_report, container, false);
        unbinder = ButterKnife.bind(this,viewRepoDynamicFragment);

        init();

        return viewRepoDynamicFragment;

    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);

            studentId = sObj.getStudentId();
            courseId = "" + sObj.getCourseId();
            classId = "" + sObj.getClassId();
            courseName = sObj.getCourseName();
            branchId = sObj.getBranchId();
            rollNumber = sObj.getStudentRollnumber();
            className = sObj.getClassName();
            profilePic = sObj.getProfilePic();
            studentName = sObj.getStudentName();
        }
        else {
            if (mActivity!=null) {
                studentId = mActivity.getIntent().getStringExtra("studentId");
                courseId = mActivity.getIntent().getStringExtra("courseId");
                classId = mActivity.getIntent().getStringExtra("classId");
                courseName = mActivity.getIntent().getStringExtra("courseName");
                branchId = mActivity.getIntent().getStringExtra("branchId");
                rollNumber = mActivity.getIntent().getStringExtra("rollNumber");
                className = mActivity.getIntent().getStringExtra("className");
                profilePic =  mActivity.getIntent().getStringExtra("studentProfilePic");
                studentName = mActivity.getIntent().getStringExtra("studentName");
            }
        }


        defaultCourse = new Course();
        defaultCourse.setCourseId("" + courseId);
        defaultCourse.setCourseName("" + courseName);
        courses_list.add(defaultCourse);

        rvCourses.setLayoutManager(new LinearLayoutManager(mActivity));

        if (NetworkConnectivity.isConnected(mActivity)) {
            getCourses();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

    }

    void getCourses(){
        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "URL - " + new AppUrls().GetDefaultCourseClassByInstType +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + branchId + "&courseId=" + courseId + "&classId=" + classId);

        Request request = new Request.Builder()
                .url(new AppUrls().GetDefaultCourseClassByInstType +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + branchId + "&courseId=" + courseId + "&classId=" + classId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
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
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentObject = new JSONObject(resp);
                        if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentObject.getJSONArray("defaultCourseClasses");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Course>>() {
                            }.getType();

                            courses_list.clear();
                            courses_list.add(defaultCourse);
                            courses_list.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog(TAG, "Course List size " + courses_list.size());

                            if (courses_list.size()>0){
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvCourses.setAdapter(new CoursesAdapter(courses_list));
                                    }
                                });
                            }else{
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();
                                    }
                                });
                            }
                        }else{
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }
                }





            }
        });

    }


    class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder>{

        List<Course> listCourses;

        public CoursesAdapter(List<Course> listCourses) {
            this.listCourses = listCourses;
        }

        @NonNull
        @Override
        public CoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_course_overall_performance,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CoursesAdapter.ViewHolder holder, int position) {
            holder.tvCourseName.setText(listCourses.get(position).getCourseName());
            getAnalysis(listCourses.get(position).getCourseId(),holder);
        }

        @Override
        public int getItemCount() {
            return listCourses.size();
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

    private void getAnalysis(String cId, CoursesAdapter.ViewHolder holder) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetTotalStudentMockTestsByTestCategory +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + cId + "&studentId=" + studentId);

        Request request = new Request.Builder()
                .url(new AppUrls().GetTotalStudentMockTestsByTestCategory +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + cId + "&studentId=" + studentId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                if(!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisArray");

                            List<AnalysisTest> listPerformance = new ArrayList<>();

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AnalysisTest>>() {
                            }.getType();

                            listPerformance.clear();
                            listPerformance.addAll(gson.fromJson(jsonArr.toString(), type));

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int totalTests = 0, totalTime = 0;
                                    Float totalPercentage = 0f;
                                    for (int i = 0; i < listPerformance.size(); i++) {
                                        totalTests = totalTests + Integer.parseInt(listPerformance.get(i).getTestCount());
                                        totalTime = totalTime + Integer.parseInt(listPerformance.get(i).getTotalTimeSpent());
                                        totalPercentage = totalPercentage + Float.parseFloat(listPerformance.get(i).getPercentage());
                                    }
                                    totalPercentage = totalPercentage / listPerformance.size();


                                    holder.tvTestTaken.setText("" + totalTests);
                                    holder.tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60));
                                    holder.tvAvgScore.setText("" + MyUtils.roundTwoDecimals(totalPercentage) + "%");
                                    holder.rb.setRating((totalPercentage*5/100));

                                    if (totalTests>0){
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent  = new Intent(mActivity, ReportClassActivity.class);
                                                intent.putExtra("analysisTestClass",(Serializable) listPerformance);
                                                intent.putExtra("courseId", cId);
                                                intent.putExtra("studentId", studentId);
                                                intent.putExtra("courseName", courseName);
                                                intent.putExtra("studentName", studentName);
                                                intent.putExtra("branchId", branchId);
                                                intent.putExtra("courseId", courseId);
                                                intent.putExtra("classId", classId);
                                                intent.putExtra("className", className);
                                                intent.putExtra("studentProfilePic", profilePic);
                                                intent.putExtra("rollNumber", rollNumber);
                                                startActivity(intent);
                                                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);



                                            }
                                        });
                                    }else {
                                        holder.itemView.setAlpha(0.5f);
                                    }
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