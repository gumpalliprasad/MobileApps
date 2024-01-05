package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import myschoolapp.com.gsnedutech.CourseSubjectChapterListing;
import myschoolapp.com.gsnedutech.MockOptionTypeSelection;
import myschoolapp.com.gsnedutech.MockOptionTypeSelectionNew;
import myschoolapp.com.gsnedutech.MockTestInfo;
import myschoolapp.com.gsnedutech.MockTestOptionActivity;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestClass;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOptedCourse;
import myschoolapp.com.gsnedutech.Models.StudentSub;
import myschoolapp.com.gsnedutech.PracticeTest;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentHome;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StudentBottomCourse extends Fragment {

    private static final String TAG = "SriRam -" + StudentBottomCourse.class.getName();

    Activity mActivity;

    View viewStudentBottomCourse;
    Unbinder unbinder;

    int pos = 0;

    @BindView(R.id.rv_prev_papers)
    RecyclerView rvPrevPapers;
    @BindView(R.id.ll_prev_papers)
    LinearLayout llPreviousPapers;

    @BindView(R.id.rv_course)
    RecyclerView rvCourses;
    @BindView(R.id.rv_prep_course)
    RecyclerView rvPrepCourse;

    @BindView(R.id.rv_test_prac)
    RecyclerView rvTestPrac;
    @BindView(R.id.rv_test_mock)
    RecyclerView rvTestMock;

    @BindView(R.id.tv_class_name)
    TextView tvClassName;
    @BindView(R.id.sp_classes)
    Spinner spClasses;

    @BindView(R.id.ll_exam_prep)
    LinearLayout llExamPrep;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "";

    List<StudentSub> subject_list = new ArrayList<>();
    public List<StudentOptedCourse> optedCourseList = new ArrayList<>();

    List<StdnTestDefClsCourseSub> stdnTestCoursesList = new ArrayList<>();


    MyUtils utils = new MyUtils();

    public StudentBottomCourse() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentBottomCourse =  inflater.inflate(R.layout.fragment_student_bottom_course, container, false);
        unbinder = ButterKnife.bind(this, viewStudentBottomCourse);

        init();
//        viewStudentBottomCourse.findViewById(R.id.hs_exam_prep).setVisibility(View.GONE);
//        llExamPrep.setVisibility(View.GONE);

        return viewStudentBottomCourse;
    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        String className = "";

        if (sObj.getClassName().contains("6")){
            className = "VI";
        } if (sObj.getClassName().contains("7")){
            className = "VII";
        } if (sObj.getClassName().contains("8")){
            className = "VII";
        } if (sObj.getClassName().contains("9")){
            className = "IX";
        } if (sObj.getClassName().contains("10")){
            className = "X";
        } if (sObj.getClassName().contains("11")){
            className = "XI";
        } if (sObj.getClassName().contains("12")){
            className = "XII";
        }

        tvClassName.setText("Prepare for "+sObj.getCourseName()+" "+className);

//        rvCourses.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
        rvCourses.setLayoutManager(new GridLayoutManager(mActivity, 4));
        rvPrepCourse.setLayoutManager(new LinearLayoutManager(mActivity));
        getCollegeCode();
        getCourses();
        llPreviousPapers.setVisibility(View.GONE);
//        getPrevPapers();

        viewStudentBottomCourse.findViewById(R.id.cv_mock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, MockTestOptionActivity.class);
                intent.putExtra("navto", "mock");
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        viewStudentBottomCourse.findViewById(R.id.cv_prac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, MockTestOptionActivity.class);
                intent.putExtra("navto", "practice");
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentBottomCourse.findViewById(R.id.ll_jee_adv).setVisibility(View.GONE);
        viewStudentBottomCourse.findViewById(R.id.ll_jee_main).setVisibility(View.GONE);
    }


    void getCourses() {
        rvCourses.setVisibility(View.GONE);
        viewStudentBottomCourse.findViewById(R.id.pb_course).setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetStudentSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&sectionId=" + sObj.getClassCourseSectionId()
                        + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId())
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url -" + new AppUrls().GetStudentSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&sectionId=" + sObj.getClassCourseSectionId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId());


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (mActivity!=null) {
                    mActivity.runOnUiThread(() -> {
                        viewStudentBottomCourse.findViewById(R.id.pb_course).setVisibility(View.GONE);
                        getAllStudentClassCourseSubjects();
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                mActivity.runOnUiThread(() -> viewStudentBottomCourse.findViewById(R.id.pb_course).setVisibility(View.GONE));
                if (response.body() != null) {
                    try {

                        String resp = responseBody.string();
                        utils.showLog(TAG, "response- " + resp);
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("studentSub");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentSub>>() {
                            }.getType();

                            subject_list.clear();
                            subject_list.addAll(gson.fromJson(jsonArr.toString(), type));
                            if (subject_list.size() > 0)
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvCourses.setVisibility(View.VISIBLE);
                                        rvCourses.setAdapter(new CourseAdapter(subject_list));
                                    }
                                });
                        }
                        else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                getAllStudentClassCourseSubjects();
            }
        });

    }

    private void getAllStudentClassCourseSubjects() {
        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "Log - " + new AppUrls().GetAllStudentClassCourseSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&branchId=" + sObj.getBranchId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId());

        Request request = apiClient.getRequest(new AppUrls().GetAllStudentClassCourseSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&branchId=" + sObj.getBranchId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId(), sh_Pref);

        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Students Subjects - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("defaultCourseClasses");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestDefClsCourseSub>>() {
                            }.getType();

                            stdnTestCoursesList.clear();
                            if(jsonArr.toString().contains("Classes")) {
                                stdnTestCoursesList.addAll(gson.fromJson(jsonArr.toString(), type));
                                utils.showLog(TAG, "CoursesList - " + stdnTestCoursesList.size());
                            }

                            List<StdnTestDefClsCourseSub> prep =new ArrayList<>();

                            for (int i=0;i<stdnTestCoursesList.size();i++){
                                if (stdnTestCoursesList.get(i).getCourseId().equalsIgnoreCase(sObj.getCourseId()+"")){
                                    getTestCategories("1",stdnTestCoursesList.get(i));
                                }else {
                                    prep.add(stdnTestCoursesList.get(i));
                                }
                            }
                            int max=0;
                            int index = 0;

                            for (int i=0;i<prep.size();i++){
                                if (prep.get(i).getClasses().size()>max){
                                    max = prep.get(i).getClasses().size();
                                    index = i;
                                }
                            }

                            List<StdnTestClass> listClasses = new ArrayList<>();
                            if(index < prep.size()){
                                listClasses.addAll(prep.get(index).getClasses());
                                if (listClasses.size() > 0 ){
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            spClasses.setVisibility(View.VISIBLE);
                                            ArrayAdapter<StdnTestClass> adapter =
                                                    new ArrayAdapter<StdnTestClass>(mActivity,  android.R.layout.simple_spinner_dropdown_item, listClasses);
                                            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                                            spClasses.setAdapter(adapter);

                                            spClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    pos = i;

                                                    List<StdnTestDefClsCourseSub> spinnerPrep =new ArrayList<>();

                                                    for (int x=0;x<prep.size();x++){
                                                        if (prep.get(x).getClasses().size()>i){
                                                            spinnerPrep.add(prep.get(x));
                                                        }
                                                    }

                                                    if (prep.size()>0){
                                                        rvPrepCourse.setVisibility(View.VISIBLE);
                                                        llExamPrep.setVisibility(View.VISIBLE);
                                                        rvPrepCourse.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
                                                        rvPrepCourse.setAdapter(new PrepAdapter(spinnerPrep));
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });
                                        }
                                    });
                                }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                    String message = parentjObject.getString(AppConst.MESSAGE);
                                    mActivity.runOnUiThread(() -> {
                                        utils.dismissAlertDialog();
                                        MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                                    });
                                }else {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            spClasses.setVisibility(View.GONE);
                                            rvPrepCourse.setVisibility(View.GONE);
                                            llExamPrep.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                            else {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        spClasses.setVisibility(View.GONE);
                                        rvPrepCourse.setVisibility(View.GONE);
                                        llExamPrep.setVisibility(View.GONE);
                                    }
                                });
                            }

                        } else if (parentjObject.getString("StatusCode").equalsIgnoreCase("300")) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    public void onResume() {
        ((StudentHome) mActivity).fabMyDoubts.setVisibility(View.GONE);
        super.onResume();
    }

    private void getTestCategories(String s, StdnTestDefClsCourseSub obj) {
        mActivity.runOnUiThread(() -> {
            rvTestMock.setVisibility(View.GONE);
            rvTestPrac.setVisibility(View.GONE);
            viewStudentBottomCourse.findViewById(R.id.pb_mock).setVisibility(View.VISIBLE);
            viewStudentBottomCourse.findViewById(R.id.pb_practice).setVisibility(View.VISIBLE);
        });

        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "Log - " +new AppUrls().GetTestCategories + "schemaName=" + mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&instId=" + s+ "&courseId=" + obj.getCourseId());
        Request request = apiClient.getRequest(new AppUrls().GetTestCategories + "schemaName=" + mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&instId=" + s+ "&courseId=" + obj.getCourseId(), sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewStudentBottomCourse.findViewById(R.id.pb_mock).setVisibility(View.GONE);
                        viewStudentBottomCourse.findViewById(R.id.pb_practice).setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Students Subjects - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        mActivity.runOnUiThread(() -> {
                            viewStudentBottomCourse.findViewById(R.id.pb_mock).setVisibility(View.GONE);
                            viewStudentBottomCourse.findViewById(R.id.pb_practice).setVisibility(View.GONE);
                        });
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("TestCategories");

                            List<List<StdnTestCategories>> stdnCategoryList = new ArrayList<>();

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestCategories>>() {}.getType();
                            stdnCategoryList.add(gson.fromJson(jsonArr.toString(), type));

                            List<StdnTestCategories> categoryList = new ArrayList<>();


                            categoryList.clear();
                            categoryList.addAll(stdnCategoryList.get(0));

                            utils.showLog(TAG,"size "+categoryList.size());

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvTestMock.setVisibility(View.VISIBLE);
                                    rvTestPrac.setVisibility(View.VISIBLE);
                                    rvTestPrac.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
                                    rvTestMock.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));

                                    rvTestPrac.setAdapter(new SchoolPracticeAdapter(categoryList,obj));
                                    rvTestMock.setAdapter(new SchoolMockAdapter(categoryList,obj));

                                }
                            });


                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    class SchoolPracticeAdapter extends RecyclerView.Adapter<SchoolPracticeAdapter.ViewHolder>{

        List<StdnTestCategories> categoryList;
        StdnTestDefClsCourseSub clsCourseSubObj;
        public SchoolPracticeAdapter(List<StdnTestCategories> categoryList, StdnTestDefClsCourseSub clsCourseSubObj) {
            this.categoryList = categoryList;
            this.clsCourseSubObj = clsCourseSubObj;
        }

        @NonNull
        @Override
        public SchoolPracticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_tests_options,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SchoolPracticeAdapter.ViewHolder holder, int position) {

            switch (categoryList.get(position).getCategoryName()){
                case "TOPIC-WISE":
                    holder.tvType.setText("Topic");
                    holder.ivType.setImageResource(R.drawable.ic_topic_prac);
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(mActivity, MockOptionTypeSelection.class);
                        intent.putExtra("courseObj", clsCourseSubObj);
                        intent.putExtra("testCategoryObj", categoryList.get(position));
                        intent.putExtra("type", categoryList.get(position).getCategoryName());
                        intent.putExtra("navto", "prac");
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "CHAPTER-WISE":
                    holder.tvType.setText("Chapter");
                    holder.ivType.setImageResource(R.drawable.ic_prac_chap);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("navto", "prac");
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "SUBJECT-WISE":
                    holder.tvType.setText("Subject");
                    holder.ivType.setImageResource(R.drawable.ic_prac_sub);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("navto", "prac");
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;

                case "CLASS-WISE":
                    holder.tvType.setText("Class");
                    holder.ivType.setImageResource(R.drawable.ic_prac_class);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, PracticeTest.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(pos).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(pos).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("testType", "CLASS-WISE");
                            intent.putExtra("topicName", "0");
                            intent.putExtra("courseId", clsCourseSubObj.getCourseId());
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(pos).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryId", categoryList.get(position).getCategoryId());
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    break;
                default:
                    holder.tvType.setText("Main Exam");
                    holder.ivType.setImageResource(R.drawable.ic_prac_main);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, PracticeTest.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(pos).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(pos).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("testType", categoryList.get(position).getCategoryName());
                            intent.putExtra("topicName", "0");
                            intent.putExtra("courseId", clsCourseSubObj.getCourseId());
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(pos).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryId", categoryList.get(position).getCategoryId());
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
            }




        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivType;
            TextView tvType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivType = itemView.findViewById(R.id.iv_type);
                tvType = itemView.findViewById(R.id.tv_type);
            }
        }
    }

    class SchoolMockAdapter extends RecyclerView.Adapter<SchoolMockAdapter.ViewHolder>{

        List<StdnTestCategories> categoryList;
        StdnTestDefClsCourseSub clsCourseSubObj;

        public SchoolMockAdapter(List<StdnTestCategories> categoryList, StdnTestDefClsCourseSub clsCourseSubObj) {
            this.categoryList = categoryList;
            this.clsCourseSubObj = clsCourseSubObj;
        }

        @NonNull
        @Override
        public SchoolMockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_tests_options,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SchoolMockAdapter.ViewHolder holder, int position) {

            holder.tvTestType.setText("Test By ");

            switch (categoryList.get(position).getCategoryName()){
                case "TOPIC-WISE":
                    holder.tvType.setText("Topic");
                    holder.ivType.setImageResource(R.drawable.ic_mock_topic);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("navto", "mock");
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "CHAPTER-WISE":
                    holder.tvType.setText("Chapter");
                    holder.ivType.setImageResource(R.drawable.ic_mock_chap);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("navto", "mock");
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "SUBJECT-WISE":
                    holder.tvType.setText("Subject");
                    holder.ivType.setImageResource(R.drawable.ic_mock_sub);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("navto", "mock");
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;

                case "CLASS-WISE":
                    holder.tvType.setText("Class");
                    holder.ivType.setImageResource(R.drawable.ic_mock_classwise);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mActivity, MockTestInfo.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(pos).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(pos).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("topicName", "0");
                            intent.putExtra("testType", "CLASS-WISE");
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(pos).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra(AppConst.ChapterCCMapId, "0");
                            intent.putExtra(AppConst.TopicCCMapId, "0");
                            startActivity(intent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                default:
                    holder.tvType.setText("Main Exam");
                    holder.ivType.setImageResource(R.drawable.ic_mock_main);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent testInfoIntent = new Intent(mActivity, MockTestInfo.class);
                            testInfoIntent.putExtra("courseObj", clsCourseSubObj);
                            testInfoIntent.putExtra("testCategoryObj", categoryList.get(position));
                            testInfoIntent.putExtra(AppConst.ChapterCCMapId, "0");
                            testInfoIntent.putExtra(AppConst.TopicCCMapId, "0");
                            startActivity(testInfoIntent);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
            }



        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivType;
            TextView tvType,tvTestType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivType = itemView.findViewById(R.id.iv_type);
                tvType = itemView.findViewById(R.id.tv_type);
                tvTestType = itemView.findViewById(R.id.tv_test_type);
            }
        }
    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

        List<StudentSub> subjectList;

        public CourseAdapter(List<StudentSub> subjectList) {
            this.subjectList = subjectList;
        }

        @NonNull
        @Override
        public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CourseAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_course_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
            switch (subjectList.get(position).getSubjectGroup()) {
                case "Maths":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_maths);
                    break;
                case "Science":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_science);
                    break;
                case "English":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_english);
                    break;
                case "GeneralKnowledge":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_gk);
                    break;
                case "SocialScience":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_social);
                    break;
                case "Hindi":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_hindi);
                    break;
                case "Telugu":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_telugu);
                    break;
                case "Physics":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_physics);
                    break;
                case "Chemistry":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_chemistry);
                    break;
                case "Biology":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_biology);
                    break;
                case "Zoology":
                    holder.ivSub.setImageResource(R.drawable.ic_courses_zoology);
                    break;
                default:
                    holder.ivSub.setImageResource(R.drawable.ic_courses_zoology);
                    break;
            }
            holder.tvSub.setText(subjectList.get(position).getSubjectName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                            Student School or College Courses
                        Intent intent = new Intent(mActivity, CourseSubjectChapterListing.class);
                        intent.putExtra("classId", "" + sObj.getClassId());
                        intent.putExtra("className", sObj.getClassName());
                        intent.putExtra("courseId", "" + sObj.getCourseId());
                        intent.putExtra("sectionId", sObj.getClassCourseSectionId());
                        intent.putExtra("Subjects", (Serializable) subjectList);
                        intent.putExtra("position", "" + position);
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                    } catch (Exception e) {
                        utils.showLog(TAG, e.getMessage());

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return subjectList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivSub;
            TextView tvSub;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivSub = itemView.findViewById(R.id.iv_sub_image);
                tvSub = itemView.findViewById(R.id.tv_sub);
            }
        }
    }

    class PrepAdapter extends RecyclerView.Adapter<PrepAdapter.ViewHolder>{

        List<StdnTestDefClsCourseSub> prep;

        public PrepAdapter(List<StdnTestDefClsCourseSub> prep) {
            this.prep = prep;
        }

        @NonNull
        @Override
        public PrepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_exam_prep,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull PrepAdapter.ViewHolder holder, int position) {
            holder.tvType.setText(prep.get(position).getCourseName());


            if (Integer.parseInt(prep.get(position).getCourseId())  == 1){
                holder.ivType.setImageResource(R.drawable.ic_prep_courses_jee);
            }else if (Integer.parseInt(prep.get(position).getCourseId())  == 2){
                holder.ivType.setImageResource(R.drawable.ic_neet_type);
            }else if (Integer.parseInt(prep.get(position).getCourseId())  == 11){
                holder.ivType.setImageResource(R.drawable.ic_vedic_maths);
            }else {
                holder.ivType.setImageResource(R.drawable.ic_prep_courses_jee_neet);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mActivity, MockOptionTypeSelectionNew.class);
                    i.putExtra("obj",(Serializable) prep.get(position));
                    i.putExtra("position",pos+"");
                    if (prep.get(position).getCourseName().contains("NEET")){
                        i.putExtra("type","NEET");
                    }
                    mActivity.startActivity(i);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return prep.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivType;
            TextView tvType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivType = itemView.findViewById(R.id.iv_type);
                tvType = itemView.findViewById(R.id.tv_type);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mActivity = (Activity)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //to know access type
    private void getCollegeCode() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().GetCollegeCode)
                .build();

        utils.showLog(TAG, "getCollegeCode request - " + new AppUrls().GetCollegeCode);
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.body() != null) {
                        try {
                            ResponseBody body = response.body();
                            String jsonResponse = response.body().string();
                            JSONObject ParentjObject = new JSONObject(jsonResponse);
                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                toEdit.putString(AppConst.ACCESS, ParentjObject.getString("access"));
                                toEdit.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}