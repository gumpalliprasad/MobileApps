/*
 * *
 *  * Created by SriRamaMurthy A on 25/9/19 12:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 25/9/19 12:43 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.Models.Course;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MockTestAnalyticsFrag extends Fragment implements TabLayout.BaseOnTabSelectedListener {

    private static final String TAG = "SriRam -" + MockTestAnalyticsFrag.class.getName();
    MyUtils utils = new MyUtils();

    View view;
    @BindView(R.id.analyticsTablayout)
    TabLayout analyticsTablayout;
    @BindView(R.id.analyticsPager)
    ViewPager analyticsPager;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId, branchId, courseId, courseName, classId;

    List<Course> courses_list = new ArrayList<>();


    public MockTestAnalyticsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mock_test_analytics, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();

        if (NetworkConnectivity.isConnected(getActivity())) {
            new GetDefaultStudentSubjects().execute();
        } else {
            new MyUtils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

        analyticsTablayout.addOnTabSelectedListener(this);

        return view;
    }

    private void init() {
        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("admin_loggedin", false) || sh_Pref.getBoolean("teacher_loggedin", false)) {
            studentId = getActivity().getIntent().getStringExtra("studentId");
            courseId = "" + getActivity().getIntent().getStringExtra("courseId");
            classId = "" + getActivity().getIntent().getStringExtra("classId");
            courseName = getActivity().getIntent().getStringExtra("courseName");
            branchId = getActivity().getIntent().getStringExtra("branchId");
        } else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);

            studentId = sObj.getStudentId();
            courseId = "" + sObj.getCourseId();
            classId = "" + sObj.getClassId();
            courseName = sObj.getCourseName();
            branchId = sObj.getBranchId();
        }

        Course defaultCourse = new Course();
        defaultCourse.setCourseId("" + courseId);
        defaultCourse.setCourseName("" + courseName);
        courses_list.add(defaultCourse);


    }

    private void displayTabs() {

        analyticsTablayout.removeAllTabs();

        if (courses_list.size() > 0) {
            analyticsTablayout.setVisibility(View.VISIBLE);
            analyticsPager.setVisibility(View.VISIBLE);

            //Adding the tabs using addTab() method
            for (int i = 0; i < courses_list.size(); i++) {
                analyticsTablayout.addTab(analyticsTablayout.newTab().setText(courses_list.get(i).getCourseName()));

            }


            //Initializing viewPager
            CoursesPagerAdapter courseAdapter = new CoursesPagerAdapter(getActivity().getSupportFragmentManager(), analyticsTablayout.getTabCount(), courses_list);

            //Creating our pager adapter
            analyticsPager.setAdapter(courseAdapter);
            analyticsPager.setOffscreenPageLimit(1);
            analyticsPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(analyticsTablayout));

        } else {
            analyticsTablayout.setVisibility(View.GONE);
            analyticsPager.setVisibility(View.GONE);

        }


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        analyticsPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class CoursesPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        List<Course> coursesList;

        public CoursesPagerAdapter(FragmentManager fm, int NumOfTabs, List<Course> coursesList) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.coursesList = coursesList;
            utils.showLog(TAG, "Position - " + coursesList.size());
        }

        @Override
        public Fragment getItem(int position) {
            utils.showLog(TAG, "Position - " + coursesList.get(position).getCourseName());
            return AnalyticsDynamicFragment.newInstance(position, coursesList.get(position), studentId, branchId);
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    private class GetDefaultStudentSubjects extends AsyncTask<String, Void, String> {
        MyUtils utils = new MyUtils();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           utils.showLoader(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

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

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Default Student Subjects - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                        JSONArray jsonArr = ParentjObject.getJSONArray("defaultCourseClasses");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Course>>() {
                        }.getType();

                        courses_list.addAll(gson.fromJson(jsonArr.toString(), type));
                        utils.showLog(TAG, "Course List size " + courses_list.size());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            utils.dismissDialog();
            displayTabs();

        }
    }
}
