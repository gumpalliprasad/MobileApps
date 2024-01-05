/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/10/19 4:44 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.AdminClassSections;
import myschoolapp.com.gsnedutech.AdminHome;
import myschoolapp.com.gsnedutech.AdminMessages;
import myschoolapp.com.gsnedutech.AdminStaffDayLeaveRequests;
import myschoolapp.com.gsnedutech.AdminStaffTakeAttendance;
import myschoolapp.com.gsnedutech.GalleryActivity;
import myschoolapp.com.gsnedutech.Models.*;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StaffAttendanceRegister;
import myschoolapp.com.gsnedutech.TeacherCalAndEvents;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DatabaseHelper;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminBottomFragFeatured extends Fragment {

    private static final String TAG = "SriRam -" + AdminBottomFragFeatured.class.getName();
    MyUtils utils = new MyUtils();
    Activity mActivity;

    View adminFeaturedView;
    @BindView(R.id.sp_branch)
    Spinner spBranch;
    @BindView(R.id.sp_course)
    Spinner spCourse;
    RecyclerView rvClassList, rvExamList, rvCheckBoxList;

    LinearLayout llAttendance, llLeaveReq;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    AdminObj adminObj;

    String instType = "";

    List<String> instBranchesListName = new ArrayList<>();
    List<AdminCollegeBranches> instBranchesList = new ArrayList<>();
    List<AdminClassCourseObj> adminClassCourseObjList = new ArrayList<>();
    List<String> courses = new ArrayList<>();
    List<AdminClassObj> listClasses = new ArrayList<>();
    List<AdminStudAttendObj> listStudAttendance = new ArrayList<>();
    List<AdminStaffAttendObj> listStaffAttendance = new ArrayList<>();
    List<AdminFeaturedUpcomingExamObj> listExams = new ArrayList<>();


    @BindView(R.id.tv_group)
    TextView tvGroup;

    @BindView(R.id.tv_late_stud)
    TextView tvLateStud;
    @BindView(R.id.tv_present_stud)
    TextView tvPresentStud;
    @BindView(R.id.tv_absent_stud)
    TextView tvAbsentStud;
    @BindView(R.id.tv_group_staff)
    TextView tvGroupStaff;
    @BindView(R.id.tv_late_teacher)
    TextView tvLateTeacher;
    @BindView(R.id.tv_present_teach)
    TextView tvPresentTeach;
    @BindView(R.id.tv_absent_teach)
    TextView tvAbsentTeach;
    @BindView(R.id.tv_takeAttendance)
    TextView tvTakeAttendance;
    @BindView(R.id.tv_attendNottaken)
    TextView tvAttendNottaken;
    @BindView(R.id.tv_lr_stud)
    TextView tvLrStud;
    @BindView(R.id.tv_lr_staff)
    TextView tvLrStaff;
    @BindView(R.id.tv_no_exams)
    TextView tvNoExams;
    @BindView(R.id.et_add_checklist)
    EditText etAddChecklist;
    @BindView(R.id.rv_fees_list)
    RecyclerView rvFeesList;

    AdminHome adminHome = new AdminHome();

    DatabaseHelper db;
    private List<Note> notesList = new ArrayList<>();
    CheckAdapter checkAdapter;

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public AdminBottomFragFeatured() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adminFeaturedView = inflater.inflate(R.layout.fragment_admin_bottom_frag_featured, container, false);
        unbinder = ButterKnife.bind(this, adminFeaturedView);

        rvClassList = adminFeaturedView.findViewById(R.id.rv_class_list);

        llAttendance = adminFeaturedView.findViewById(R.id.ll_attendance);


        llLeaveReq = adminFeaturedView.findViewById(R.id.ll_LR);

        rvExamList = adminFeaturedView.findViewById(R.id.rv_exam_list);
        rvCheckBoxList = adminFeaturedView.findViewById(R.id.rv_check_box_list);


        init();


        spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listClasses.clear();
                listClasses.addAll(adminClassCourseObjList.get(position).getClasses());

                rvClassList.setAdapter(new ClassCourseAdapter(listClasses));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                toEdit.putString("admin_branchId", "" + instBranchesList.get(position).getBranchId());

                Log.v(TAG, "Admin CollegeInstituteTypes List Size " + instBranchesList.get(position).getBranchInstType().size());
                instType = "";
                for (int i = 0; i < instBranchesList.get(position).getBranchInstType().size(); i++) {
                    instType = instType + instBranchesList.get(position).getBranchInstType().get(i).getInstTypeId();
                    if (i != (instBranchesList.get(position).getBranchInstType().size() - 1))
                        instType = instType + ",";
                }

                toEdit.putString("instType", instType);
                toEdit.commit();
//                showSkeleton();
                getAdminClassCoursesByBId(sh_Pref.getString("instType", "1"), sh_Pref.getString("admin_branchId", "0"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return adminFeaturedView;
    }


    private void init() {
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("adminObj", "");
        adminObj = gson.fromJson(json, AdminObj.class);


        rvCheckBoxList.setLayoutManager(new LinearLayoutManager(mActivity));
        db = new DatabaseHelper(mActivity);
        notesList.addAll(db.getAllNotes("ADMIN"));
        checkAdapter = new CheckAdapter(notesList);
        rvCheckBoxList.setLayoutManager(new LinearLayoutManager(mActivity));
        rvCheckBoxList.setAdapter(checkAdapter);


        getAllBranches();


        rvClassList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvFeesList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvFeesList.setAdapter(new FeesAdapter());


//        showSkeleton();


    }

    @Override
    public void onResume() {
        notesList.clear();

        notesList.addAll(db.getAllNotes("ADMIN"));
        checkAdapter = new CheckAdapter(notesList);
        rvCheckBoxList.setAdapter(new CheckAdapter(notesList));
        super.onResume();
    }



    private void setCourseslayout() {

        for (AdminClassCourseObj adminClassCourseObj : adminClassCourseObjList) {
            courses.add(adminClassCourseObj.getCourseName());
        }

        if (mActivity != null) {
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item_branch, courses);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCourse.setAdapter(spinnerArrayAdapter);
        }


    }


    class FeesAdapter extends RecyclerView.Adapter<FeesAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_fees_payments, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 2;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_staffLeaveRequest, R.id.cv_msg, R.id.tv_todo_view_more, R.id.ll_staffattendance, R.id.img_addCheckList, R.id.tv_testViewAll,R.id.cv_gallery,R.id.cv_calandevents})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_staffLeaveRequest:
                Intent staffAttLRIntent = new Intent(mActivity, AdminStaffDayLeaveRequests.class);
                staffAttLRIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId", "0"));
                staffAttLRIntent.putExtra("userId", "" + adminObj.getUserId());
                startActivity(staffAttLRIntent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
            case R.id.cv_msg:
                startActivity(new Intent(mActivity, AdminMessages.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
            case R.id.tv_todo_view_more:
//                startActivity(new Intent(mActivity, ToDoActivity.class));
                break;
            case R.id.ll_staffattendance:
                if (tvTakeAttendance.getVisibility() == View.VISIBLE) {
                    Intent staffAttIntent = new Intent(mActivity, AdminStaffTakeAttendance.class);
                    staffAttIntent.putExtra("branchId", sh_Pref.getString("admin_branchId", "0"));
                    staffAttIntent.putExtra("roleId", adminObj.getRoleId());
                    staffAttIntent.putExtra("userId", "" + adminObj.getUserId());
                    startActivity(staffAttIntent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                } else {
                    Intent staffAttRegIntent = new Intent(mActivity, StaffAttendanceRegister.class);
                    staffAttRegIntent.putExtra("branchId", sh_Pref.getString("admin_branchId", "0"));
                    staffAttRegIntent.putExtra("roleId", adminObj.getRoleId());
                    staffAttRegIntent.putExtra("userId", "" + adminObj.getUserId());
                    startActivity(staffAttRegIntent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
                break;
            case R.id.img_addCheckList:
                if (!etAddChecklist.getText().toString().equalsIgnoreCase("")) {
                    db.insertNote(etAddChecklist.getText().toString(), 0, "ADMIN", adminObj.getUserId() + "");
                    notesList.clear();
                    notesList.addAll(db.getAllNotes("ADMIN"));
                    checkAdapter = new CheckAdapter(notesList);
                    rvCheckBoxList.setAdapter(checkAdapter);
                    etAddChecklist.setText("");
                    try {
                        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(mActivity, "Enter Some value", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.tv_testViewAll:
                Intent intent = new Intent(mActivity, AdminHome.class);
                intent.putExtra("tabNo", 3);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                break;

            case R.id.cv_gallery:
                startActivity(new Intent(mActivity, GalleryActivity.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
            case R.id.cv_calandevents:
                startActivity(new Intent(mActivity, TeacherCalAndEvents.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
        }
    }


    class ClassCourseAdapter extends RecyclerView.Adapter<ClassCourseAdapter.ViewHolder> {

        List<AdminClassObj> listClasses;

        ClassCourseAdapter(List<AdminClassObj> listClasses) {
            this.listClasses = listClasses;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.item_classname, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvClassName.setText(listClasses.get(position).getClassName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "courseId - " + adminClassCourseObjList.get(spCourse.getSelectedItemPosition()).getCourseId());
                    Intent secIntent = new Intent(mActivity, AdminClassSections.class);
                    secIntent.putExtra("courseId", "" + adminClassCourseObjList.get(spCourse.getSelectedItemPosition()).getCourseId());
                    secIntent.putExtra("classCourseId", listClasses.get(position).getClassCourseId());
                    secIntent.putExtra("classId", "" + listClasses.get(position).getClassId());
                    secIntent.putExtra("className", listClasses.get(position).getClassName());
                    secIntent.putExtra("courseName", "" + courses.get(spCourse.getSelectedItemPosition()));
                    secIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId", "0"));
                    startActivity(secIntent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listClasses.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvClassName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvClassName = itemView.findViewById(R.id.tv_class);
            }
        }
    }

    void getAllBranches(){
        if (utils.loading!=null && !utils.loading.isShowing()){
            utils.showLoader(mActivity);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Log.v(TAG, "Admin CollegeBranches request - " + new AppUrls().GetAllBranches +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + adminObj.getInstId() + "&roleId=" + adminObj.getRoleId() + "&branchId=" + adminObj.getBranchId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetAllBranches +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + adminObj.getInstId() + "&roleId=" + adminObj.getRoleId() + "&branchId=" + adminObj.getBranchId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Admin CollegeBranches responce - " + jsonResp);
                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        JSONArray jsonArr = ParentjObject.getJSONArray("collegeBranches");
                        if (mActivity != null && isAdded()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminCollegeBranches>>() {
                            }.getType();

                            instBranchesList.clear();
                            instBranchesListName.clear();
                            instBranchesList.addAll(gson.fromJson(jsonArr.toString(), type));

                            for (AdminCollegeBranches adminCollegeBranches : instBranchesList) {
                                instBranchesListName.add(adminCollegeBranches.getBranchName());
                            }

                            Log.v(TAG, "Admin Collegebranches List Size " + instBranchesList.size());
                            Log.v(TAG, "Admin Collegebranches ListName Size " + instBranchesListName.size());

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item_branch, instBranchesListName);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    spBranch.setAdapter(spinnerArrayAdapter);
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

    void getAdminClassCoursesByBId(String ...strings){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Log.v(TAG, "Admin ClassCourses request - " + new AppUrls().GetAdminClassCoursesByBId +"schemaName=" +sh_Pref.getString("schema","")+ "&instType=" + strings[0] + "&branchId=" + strings[1]);

        Request request = new Request.Builder()
                .url(new AppUrls().GetAdminClassCoursesByBId +"schemaName=" +sh_Pref.getString("schema","")+ "&instType=" + strings[0] + "&branchId=" + strings[1])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String  jsonResp = response.body().string();

                    Log.v(TAG, "Admin ClassCourses responce - " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("ClassCourses");
                                    if (mActivity != null && isAdded()) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<AdminClassCourseObj>>() {
                                        }.getType();

                                        adminClassCourseObjList.clear();
                                        adminClassCourseObjList.addAll(gson.fromJson(jsonArr.toString(), type));

                                        Log.v(TAG, "Admin ClassCourses List Size " + adminClassCourseObjList.size());

                                        List<AdminClassCourseObj> filterdList = new ArrayList<>();

                                        for (AdminClassCourseObj adminClassCourseObj : adminClassCourseObjList) {
                                            if (adminClassCourseObj.getIsDefault() == 0) {
                                                filterdList.add(adminClassCourseObj);
                                            }

                                        }

                                        adminClassCourseObjList.clear();
                                        adminClassCourseObjList.addAll(filterdList);

                                        setCourseslayout();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rvClassList.setVisibility(View.VISIBLE);
                            }
                        }, 2000);
                        getStudentAttendance();
                    }
                });
            }
        });


    }

    void getStudentAttendance(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Date d = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd");

        String newDateStr = postFormater.format(d);

        Log.v(TAG, "Admin StudentAttendance request - " + new AppUrls().GetStudentAttendanceReportForAdmin +"schemaName=" +sh_Pref.getString("schema","")+ "&currentDate=" + newDateStr + "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetStudentAttendanceReportForAdmin +"schemaName=" +sh_Pref.getString("schema","")+ "&currentDate=" + newDateStr + "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v("TAG", "Admin StudentAttendance request - " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendanceArray");
                                    if (mActivity != null && isAdded()) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<AdminStudAttendObj>>() {
                                        }.getType();

                                        listStudAttendance.clear();
                                        listStudAttendance = gson.fromJson(jsonArr.toString(), type);

                                        tvAbsentStud.setText(listStudAttendance.get(0).getAbsentPercentageCount() + "");
                                        tvPresentStud.setText(listStudAttendance.get(0).getPresentPercentageCount() + "");
                                        tvLateStud.setText(listStudAttendance.get(0).getLatePercentageCount() + "");

                                    } else {
                                        tvAbsentStud.setText("-");
                                        tvPresentStud.setText("-");
                                        tvLateStud.setText("-");
                                        tvAttendNottaken.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                llAttendance.setVisibility(View.VISIBLE);
//                    shimmerAttendance.setVisibility(View.GONE);
//                    skeletonAttendance.setVisibility(View.GONE);
                            }
                        }, 2000);

                        getStaffAttendance();
                    }
                });
            }
        });
    }

    void getStaffAttendance(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Date d = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd");

        String newDateStr = postFormater.format(d);

        Log.v(TAG, "Admin StudentAttendance request - " + new AppUrls().GetStaffAttendanceReportForAdmin +"schemaName=" +sh_Pref.getString("schema","")+ "&currentDate=" + newDateStr + "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetStaffAttendanceReportForAdmin +"schemaName=" +sh_Pref.getString("schema","")+ "&currentDate=" + newDateStr + "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v("TAG", "Admin StudentAttendance responce - " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("staffAttendanceArray");
                                    if (mActivity != null && isAdded()) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<AdminStaffAttendObj>>() {
                                        }.getType();

                                        listStaffAttendance = gson.fromJson(jsonArr.toString(), type);

                                        tvAbsentTeach.setText(listStaffAttendance.get(0).getAbsentCount());
                                        tvPresentTeach.setText(listStaffAttendance.get(0).getPresentCount());
                                        tvLateTeacher.setText(listStaffAttendance.get(0).getLateCount());
                                    } else {
                                        tvAbsentTeach.setText("-");
                                        tvPresentTeach.setText("-");
                                        tvLateTeacher.setText("-");
                                        tvTakeAttendance.setVisibility(View.VISIBLE);
                                    }}

                            } catch (Exception e) {

                            }
                        }
                    });
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                llAttendance.setVisibility(View.VISIBLE);
//                    shimmerAttendance.setVisibility(View.GONE);
//                    skeletonAttendance.setVisibility(View.GONE);
                            }
                        }, 2000);

                        getLeaveRequest();
                    }
                });
            }
        });

    }

    void getLeaveRequest(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Date d = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd");

        String newDateStr = postFormater.format(d);

        Log.v(TAG, "Admin Leave request - " + new AppUrls().GetTotalLeaveRequestsForStaffByBranch +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0"));

        Request request = new Request.Builder()
                .url(new AppUrls().GetTotalLeaveRequestsForStaffByBranch +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0"))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v("TAG", "Admin Leave request- " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    if (mActivity != null && isAdded()) {
                                        tvLrStaff.setText(ParentjObject.getJSONArray("staffLeaveReqArray").length() + "");
                                    }
                                } else {
                                    if (mActivity != null && isAdded()) {
                                        tvLrStaff.setText("0");
                                    }
                                }

                            } catch (Exception e) {

                            }
                        }
                    });
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                    shimmerLeave.setVisibility(View.GONE);
//                    skeletonLeave.setVisibility(View.GONE);
                                llLeaveReq.setVisibility(View.VISIBLE);
                            }
                        }, 2000);

                        getAllLeaveRequest();
                    }
                });
            }
        });
    }


    void getAllLeaveRequest(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Date d = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd");

        String newDateStr = postFormater.format(d);

        Log.v(TAG, "Admin Leave request - " + new AppUrls().GetAllLeaveRequestsByBranch +"schemaName=" +sh_Pref.getString("schema","")+ "&currentDate=" + newDateStr + "&branchId=" + adminObj.getBranchId() + "&roleId=" + adminObj.getRoleId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetAllLeaveRequestsByBranch +"schemaName=" +sh_Pref.getString("schema","")+ "&currentDate=" + newDateStr + "&branchId=" + adminObj.getBranchId() + "&roleId=" + adminObj.getRoleId())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v("TAG", "Admin Leave request- " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    if (mActivity != null && isAdded()) {
                                        tvLrStud.setText(ParentjObject.get("studentLeaveRequest") + "");
                                    }
                                } else {
                                    if (mActivity != null && isAdded()) {
                                        tvLrStud.setText("0");
                                    }
                                }

                            } catch (Exception e) {

                            }
                        }
                    });
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                    shimmerLeave.setVisibility(View.GONE);
//                    skeletonLeave.setVisibility(View.GONE);
                                llLeaveReq.setVisibility(View.VISIBLE);
                            }
                        }, 2000);

                        getUpcomingExams();
                    }
                });
            }
        });
    }

    void getUpcomingExams(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Admin Upcoming tests request - " + new AppUrls().GetUpcomingTestForAdminDashBoard +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetUpcomingTestForAdminDashBoard +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId())
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
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v("TAG", "Student Attendance- " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("upComingTestArray");
                                    if (mActivity != null && isAdded()) {
                                        if (mActivity != null && isAdded()) {
                                            Gson gson = new Gson();
                                            Type type = new TypeToken<List<AdminFeaturedUpcomingExamObj>>() {
                                            }.getType();

                                            listExams.clear();
                                            listExams = gson.fromJson(jsonArr.toString(), type);

                                            rvExamList.setLayoutManager(new LinearLayoutManager(mActivity));
                                            rvExamList.setAdapter(new ExamAdapter(listExams));

                                        } else {
                                            tvNoExams.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    });
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

//                    shimmerUpcoming.setVisibility(View.GONE);
//                    skeletonUpcoming.setVisibility(View.GONE);
                                rvExamList.setVisibility(View.VISIBLE);
                            }
                        }, 2000);
                        utils.dismissDialog();
                    }
                });
            }
        });
    }


    class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {

        List<AdminFeaturedUpcomingExamObj> listExams;

        ExamAdapter(List<AdminFeaturedUpcomingExamObj> listExams) {
            this.listExams = listExams;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_tests, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvTestDate.setText(listExams.get(position).getTestStartDate());
            holder.tvTestName.setText(listExams.get(position).getTestName());
        }

        @Override
        public int getItemCount() {
            return listExams.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTestName, tvTestType, tvTestDate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTestDate = itemView.findViewById(R.id.tv_test_date);
                tvTestType = itemView.findViewById(R.id.tv_test_type);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
            }
        }
    }

    class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {

        List<Note> listOption;

        CheckAdapter(List<Note> listOption) {
            this.listOption = listOption;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.layout_item_checkbox, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.cbOp.setText(listOption.get(position).getNote());
            holder.cbOp.setId(position);
            if (listOption.get(position).isCompleted() == 1) {
                holder.cbOp.setChecked(true);
            } else {
                holder.cbOp.setChecked(false);
            }
            holder.cbOp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        db.onTableUpdate(listOption.get(position).getId(), 1);

                    } else {
                        buttonView.setPaintFlags(buttonView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        db.onTableUpdate(listOption.get(position).getId(), 0);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listOption.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            CheckBox cbOp;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                cbOp = itemView.findViewById(R.id.cb_option);
            }
        }
    }

//    public class SpinAdapter extends ArrayAdapter<AdminClassCourseObj> {
//
//        // Your sent context
//        private Context context;
//        // Your custom values for the spinner (User)
//        private List<AdminClassCourseObj> classCourseObjs;
//
//        public SpinAdapter(Context context, int textViewResourceId, List<AdminClassCourseObj> classCourseObjs) {
//            super(context, textViewResourceId, classCourseObjs);
//            this.context = context;
//            this.classCourseObjs = classCourseObjs;
//        }
//
//        @Override
//        public int getCount() {
//            return classCourseObjs.size();
//        }
//
//        @Override
//        public AdminClassCourseObj getItem(int position) {
//            return classCourseObjs.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//
//        // And the "magic" goes here
//        // This is for the "passive" state of the spinner
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
//            TextView label = (TextView) super.getView(position, convertView, parent);
//            label.setTextColor(Color.BLACK);
//            // Then you can get the current item using the values array (Users array) and the current position
//            // You can NOW reference each method you has created in your bean object (User class)
//            label.setText(classCourseObjs.get(position).getCourseName());
//
//            // And finally return your dynamic (or custom) view for each spinner item
//            return label;
//        }
//
//        // And here is when the "chooser" is popped up
//        // Normally is the same view, but you can customize it if you want
//        @Override
//        public View getDropDownView(int position, View convertView,
//                                    ViewGroup parent) {
//            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
//            label.setTextColor(Color.BLACK);
//            label.setText(classCourseObjs.get(position).getCourseName());
//
//            return label;
//        }
//    }
}
