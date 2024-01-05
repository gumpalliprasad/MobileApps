package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.JeeMains.files.JeeMainsMainActivity;
import myschoolapp.com.gsnedutech.Models.Course;
import myschoolapp.com.gsnedutech.Models.CourseClass;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentSub;
import myschoolapp.com.gsnedutech.Neet.NeetSubjectsActivity;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockOptionTypeSelectionNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + MockOptionTypeSelectionNew.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_test_prac)
    RecyclerView rvTestPrac;
    @BindView(R.id.rv_test_mock)
    RecyclerView rvTestMock;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.rv_course)
    RecyclerView rvCourses;
    @BindView(R.id.iv_type)
    ImageView ivType;

    MyUtils utils = new MyUtils();

    StdnTestDefClsCourseSub obj;
    List<Course> courses_list = new ArrayList<>();


    int classPos;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_option_type_selection_new);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_prevjee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MockOptionTypeSelectionNew.this, JeeMainsMainActivity.class));
            }
        });

//        findViewById(R.id.iv_prevadvjee).setOnClickListener(view -> {
//            Intent intent = new Intent(MockOptionTypeSelectionNew.this, AdvJeeMainActivity.class);
//            startActivity(intent);
//        });


        findViewById(R.id.iv_prevneet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MockOptionTypeSelectionNew.this, NeetSubjectsActivity.class));
            }
        });

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
            utils.alertDialog(1, MockOptionTypeSelectionNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                getPrepCourseSubjects();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /*if (getIntent().hasExtra("type")) {
            ivType.setImageResource(R.drawable.ic_neet_type);
            findViewById(R.id.iv_prevneet).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_prevjee).setVisibility(View.GONE);
        }*/

        obj = (StdnTestDefClsCourseSub) getIntent().getSerializableExtra("obj");
        classPos = Integer.parseInt(getIntent().getStringExtra("position"));

        String className = obj.getClasses().get(classPos).getClassName();

        if (className.contains("6")) {
            className = "Class VI";
        }
        if (className.contains("7")) {
            className = "Class VII";
        }
        if (className.contains("8")) {
            className = "Class VIII";
        }
        if (className.contains("9")) {
            className = "Class IX";
        }
        if (className.contains("10")) {
            className = "Class X";
        }
        if (className.contains("11")) {
            className = "Class XI";
        }
        if (className.contains("12")) {
            className = "Class XII";
        }

        tvClass.setText(className);

        tvType.setText(obj.getCourseName());
        utils.showLoader(this);

      /*  if (Integer.parseInt(obj.getCourseId()) == 1 || Integer.parseInt(obj.getCourseId())== 6 ||
                Integer.parseInt(obj.getCourseId()) == 20 || Integer.parseInt(obj.getCourseId())== 21  ) {
            ivType.setImageResource(R.drawable.ic_prep_courses_jee);
            findViewById(R.id.iv_prevneet).setVisibility(View.GONE);
            findViewById(R.id.iv_prevjee).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_learn_n_prac).setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(obj.getCourseId()) == 2 || Integer.parseInt(obj.getCourseId())== 7 ||
                Integer.parseInt(obj.getCourseId()) == 22 ) {
            ivType.setImageResource(R.drawable.ic_neet_type);
            findViewById(R.id.iv_prevneet).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_learn_n_prac).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_prevjee).setVisibility(View.GONE);
        }else if(Integer.parseInt(obj.getCourseId())  == 11){
            ivType.setImageResource(R.drawable.ic_vedic_maths);
            findViewById(R.id.iv_prevneet).setVisibility(View.GONE);
            findViewById(R.id.iv_prevjee).setVisibility(View.GONE);
            findViewById(R.id.tv_learn_n_prac).setVisibility(View.GONE);
        }else {
            ivType.setImageResource(R.drawable.ic_prep_courses_jee_neet);
            findViewById(R.id.iv_prevneet).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_prevjee).setVisibility(View.GONE);
            findViewById(R.id.tv_learn_n_prac).setVisibility(View.VISIBLE);
        }
*/
    }


    void getPrepCourseSubjects() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetDefaultCourseClassByInstType + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                        + "&branchId=" + sObj.getBranchId() + "&courseId=" + sObj.getCourseId() + "&classId=" + sObj.getClassId())
                .build();

        utils.showLog(TAG, "url -" + new AppUrls().GetDefaultCourseClassByInstType + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&branchId=" + sObj.getBranchId() + "&courseId=" + sObj.getCourseId() + "&classId=" + sObj.getClassId());


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getTestCategories("1", obj);

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {


                } else {
                    try {

                        String resp = responseBody.string();

                        utils.showLog(TAG, "defaultCourseClasses response- " + resp);

                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("defaultCourseClasses");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Course>>() {
                            }.getType();
                            courses_list.clear();
                            courses_list.addAll(gson.fromJson(jsonArr.toString(), type));

                            List<Course> listCourse = new ArrayList<>();

                            for (int i = 0; i < courses_list.size(); i++) {
                                if (courses_list.get(i).getClasses().get(0).getSubjects().size() > 0) {
                                    listCourse.add(courses_list.get(i));
                                }
                            }

                            courses_list.clear();
                            courses_list.addAll(listCourse);


                            List<StudentSub> listSubs = new ArrayList<>();

                            for (int i = 0; i < courses_list.size(); i++) {
                                if (courses_list.get(i).getCourseId().equalsIgnoreCase(obj.getCourseId())) {
                                    List<CourseClass> listClasses = new ArrayList<>();
                                    listClasses.addAll(courses_list.get(i).getClasses());
                                    for (int j = 0; j < listClasses.size(); j++) {
                                        if (listClasses.get(j).getClassId().equalsIgnoreCase(obj.getClasses().get(classPos).getClassId())) {
                                            listSubs.addAll(listClasses.get(j).getSubjects());
                                        }
                                    }
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvCourses.setLayoutManager(new GridLayoutManager(MockOptionTypeSelectionNew.this, 4));
                                    rvCourses.setAdapter(new CourseAdapter(listSubs));
                                }
                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                getTestCategories("1", obj);


            }
        });

    }

    private void getTestCategories(String s, StdnTestDefClsCourseSub obj) {
        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "Log - " + new AppUrls().GetTestCategories + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                + "&instId=" + s + "&courseId=" + obj.getCourseId());
        Request request = apiClient.getRequest(new AppUrls().GetTestCategories + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                + "&instId=" + s + "&courseId=" + obj.getCourseId(), sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
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
                if (response.body() != null) {
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Students Subjects - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("TestCategories");

                            List<List<StdnTestCategories>> stdnCategoryList = new ArrayList<>();

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestCategories>>() {
                            }.getType();
                            stdnCategoryList.add(gson.fromJson(jsonArr.toString(), type));

                            List<StdnTestCategories> categoryList = new ArrayList<>();

                            categoryList.clear();
                            categoryList.addAll(stdnCategoryList.get(0));

                            utils.showLog(TAG, "size " + categoryList.size());

                            Collections.reverse(categoryList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    rvTestPrac.setLayoutManager(new LinearLayoutManager(MockOptionTypeSelectionNew.this,RecyclerView.HORIZONTAL,false));
                                    rvTestMock.setLayoutManager(new LinearLayoutManager(MockOptionTypeSelectionNew.this,RecyclerView.HORIZONTAL,false));

                                    rvTestPrac.setAdapter(new SchoolPracticeAdapter(categoryList, obj));
                                    rvTestMock.setAdapter(new SchoolMockAdapter(categoryList, obj));

                                }
                            });


                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, MockOptionTypeSelectionNew.this, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        }, 2000);
                    }
                });
            }
        });
    }

    class SchoolPracticeAdapter extends RecyclerView.Adapter<SchoolPracticeAdapter.ViewHolder> {

        List<StdnTestCategories> categoryList;
        StdnTestDefClsCourseSub clsCourseSubObj;

        public SchoolPracticeAdapter(List<StdnTestCategories> categoryList, StdnTestDefClsCourseSub clsCourseSubObj) {
            this.categoryList = categoryList;
            this.clsCourseSubObj = clsCourseSubObj;
        }

        @NonNull
        @Override
        public SchoolPracticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SchoolPracticeAdapter.ViewHolder(LayoutInflater.from(MockOptionTypeSelectionNew.this).inflate(R.layout.item_tests_options, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SchoolPracticeAdapter.ViewHolder holder, int position) {

            switch (categoryList.get(position).getCategoryName()) {
                case "TOPIC-WISE":
                    holder.tvType.setText("Topic");
                    holder.ivType.setImageResource(R.drawable.ic_topic_prac);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("class_selected", classPos + "");
                            intent.putExtra("navto", "prac");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "CHAPTER-WISE":
                    holder.tvType.setText("Chapter");
                    holder.ivType.setImageResource(R.drawable.ic_prac_chap);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("class_selected", classPos + "");
                            intent.putExtra("navto", "prac");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "SUBJECT-WISE":
                    holder.tvType.setText("Subject");
                    holder.ivType.setImageResource(R.drawable.ic_prac_sub);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("class_selected", classPos + "");
                            intent.putExtra("navto", "prac");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "CLASS-WISE":
                    holder.tvType.setText("Class");
                    holder.ivType.setImageResource(R.drawable.ic_prac_class);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, PracticeTest.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(classPos).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(classPos).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("testType", "CLASS-WISE");
                            intent.putExtra("topicName", "0");
                            intent.putExtra("courseId", clsCourseSubObj.getCourseId());
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(classPos).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryId", categoryList.get(position).getCategoryId());
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    break;
                default:
                    holder.tvType.setText("Main Exam");
                    holder.ivType.setImageResource(R.drawable.ic_prac_main);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, PracticeTest.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(classPos).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(classPos).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("testType", categoryList.get(position).getCategoryName());
                            intent.putExtra("topicName", "0");
                            intent.putExtra("courseId", clsCourseSubObj.getCourseId());
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(classPos).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryId", categoryList.get(position).getCategoryId());
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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

    class SchoolMockAdapter extends RecyclerView.Adapter<SchoolMockAdapter.ViewHolder> {

        List<StdnTestCategories> categoryList;
        StdnTestDefClsCourseSub clsCourseSubObj;

        public SchoolMockAdapter(List<StdnTestCategories> categoryList, StdnTestDefClsCourseSub clsCourseSubObj) {
            this.categoryList = categoryList;
            this.clsCourseSubObj = clsCourseSubObj;
        }

        @NonNull
        @Override
        public SchoolMockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SchoolMockAdapter.ViewHolder(LayoutInflater.from(MockOptionTypeSelectionNew.this).inflate(R.layout.item_tests_options, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SchoolMockAdapter.ViewHolder holder, int position) {

//            holder.tvTestType.setText("Test On ");

            switch (categoryList.get(position).getCategoryName()) {
                case "TOPIC-WISE":
                    holder.tvType.setText("Topic");
                    holder.ivType.setImageResource(R.drawable.ic_mock_topic);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("class_selected", classPos + "");
                            intent.putExtra("navto", "mock");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "CHAPTER-WISE":
                    holder.tvType.setText("Chapter");
                    holder.ivType.setImageResource(R.drawable.ic_mock_chap);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("class_selected", classPos + "");
                            intent.putExtra("navto", "mock");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "SUBJECT-WISE":
                    holder.tvType.setText("Subject");
                    holder.ivType.setImageResource(R.drawable.ic_mock_sub);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockOptionTypeSelection.class);
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra("type", categoryList.get(position).getCategoryName());
                            intent.putExtra("class_selected", classPos + "");
                            intent.putExtra("navto", "mock");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                    break;
                case "CLASS-WISE":
                    holder.tvType.setText("Class");
                    holder.ivType.setImageResource(R.drawable.ic_mock_classwise);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MockOptionTypeSelectionNew.this, MockTestInfo.class);
                            intent.putExtra("classId", clsCourseSubObj.getClasses().get(classPos).getClassId());
                            intent.putExtra("className", clsCourseSubObj.getClasses().get(classPos).getClassName());
                            intent.putExtra("subjectId", "0");
                            intent.putExtra("subjectName", "0");
                            intent.putExtra("chapterId", "0");
                            intent.putExtra("chapterName", "0");
                            intent.putExtra("topicId", "0");
                            intent.putExtra("topicName", "0");
                            intent.putExtra("testType", "CLASS-WISE");
                            intent.putExtra("courseObj", clsCourseSubObj);
                            intent.putExtra("contentType", clsCourseSubObj.getClasses().get(classPos).getSubjects().get(0).getContentType());
                            intent.putExtra("testCategoryObj", categoryList.get(position));
                            intent.putExtra(AppConst.ChapterCCMapId, "0");
                            intent.putExtra(AppConst.TopicCCMapId, "0");
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                    break;
                default:
                    holder.tvType.setText("Main Exam");
                    holder.ivType.setImageResource(R.drawable.ic_mock_main);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent testInfoIntent = new Intent(MockOptionTypeSelectionNew.this, MockTestInfo.class);
                            testInfoIntent.putExtra("courseObj", clsCourseSubObj);
                            testInfoIntent.putExtra("testCategoryObj", categoryList.get(position));
                            testInfoIntent.putExtra(AppConst.ChapterCCMapId, "0");
                            testInfoIntent.putExtra(AppConst.TopicCCMapId, "0");
                            startActivity(testInfoIntent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

        List<StudentSub> subjectList;

        public CourseAdapter(List<StudentSub> subjectList) {
            this.subjectList = subjectList;
        }

        @NonNull
        @Override
        public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CourseAdapter.ViewHolder(LayoutInflater.from(MockOptionTypeSelectionNew.this).inflate(R.layout.item_course_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
            Log.v(TAG,"group - "+subjectList.get(position).getSubjectGroup());
            switch (subjectList.get(position).getSubjectGroup()) {
                case "Vedic Mathematics":
                    holder.ivSub.setImageResource(R.drawable.ic_vedic_maths);
                    break;
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
                    holder.ivSub.setImageResource(R.drawable.ic_courses_biology);
                    break;
                default:
                    holder.ivSub.setImageResource(R.drawable.ic_courses_science);
                    break;
            }
            holder.tvSub.setText(subjectList.get(position).getSubjectName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                            Student School or College Courses
                        Intent intent = new Intent(MockOptionTypeSelectionNew.this, CourseSubjectChapterListing.class);

                        intent.putExtra("classId", "" + obj.getClasses().get(classPos).getClassId());
                        intent.putExtra("className", obj.getClasses().get(classPos).getClassName());
                        intent.putExtra("courseId", "" + obj.getCourseId());
                        intent.putExtra("sectionId", sObj.getClassCourseSectionId());
                        intent.putExtra("Subjects", (Serializable) subjectList);
                        intent.putExtra("position", "" + position);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

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


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}