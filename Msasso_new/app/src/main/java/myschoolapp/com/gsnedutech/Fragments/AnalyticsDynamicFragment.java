/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 31/10/19 3:26 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.AnalyticsClassSubPerformance;
import myschoolapp.com.gsnedutech.Models.AnalysisTest;
import myschoolapp.com.gsnedutech.Models.AnalysisTestClass;
import myschoolapp.com.gsnedutech.Models.Course;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.MyUtils;

import static android.content.Context.MODE_PRIVATE;

public class AnalyticsDynamicFragment extends Fragment {

    private static final String TAG = "SriRam -" + AnalyticsDynamicFragment.class.getName();
    private static MyUtils utils = new MyUtils();

    View view;
    @BindView(R.id.tv_totalTestsCount)
    TextView tvTotalTestsCount;
    @BindView(R.id.tvTotalPercentage)
    TextView tvTotalPercentage;
    @BindView(R.id.tvTotalTimeSpent)
    TextView tvTotalTimeSpent;
    @BindView(R.id.tv_no_exams)
    TextView tvNoExams;
    @BindView(R.id.gl_testsTaken)
    GridView glTestsTaken;
    @BindView(R.id.rv_classList)
    RecyclerView rvClassList;
    @BindView(R.id.tv_classwiseTitle)
    TextView tvClasswiseTitle;
    Unbinder unbinder;

    String studentId, branchId, courseId;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    List<AnalysisTest> listPerformance = new ArrayList<>();
    List<AnalysisTestClass> listClassPerformance = new ArrayList<>();


    public static AnalyticsDynamicFragment newInstance(int val, Course course, String studentId, String branchId) {

        AnalyticsDynamicFragment fragment = new AnalyticsDynamicFragment();

        fragment.studentId = studentId;
        fragment.branchId = branchId;

        utils.showLog(TAG, "val 1- " + val);

        Bundle args = new Bundle();
        args.putInt("someInt", val);
        args.putSerializable("course", course);
        fragment.setArguments(args);
        return fragment;
    }

    int val;
    LinearLayout llHomeworks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Course course = (Course) getArguments().getSerializable("course");
        courseId = course.getCourseId();

        view = inflater.inflate(R.layout.layout_analytics_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();

        if (NetworkConnectivity.isConnected(getActivity())) {
            new GetAnalysis().execute(courseId, studentId);
        } else {
            new MyUtils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

        return view;
    }

    private void init() {
        rvClassList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class GetAnalysis extends AsyncTask<String, Void, String> {

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


            utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetTotalStudentMockTestsByTestCategory +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + strings[0] + "&studentId=" + strings[1]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetTotalStudentMockTestsByTestCategory +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + strings[0] + "&studentId=" + strings[1])
                    .build();


            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Test Analysis - " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisArray");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AnalysisTest>>() {
                        }.getType();

                        listPerformance.clear();
                        listPerformance.addAll(gson.fromJson(jsonArr.toString(), type));

                        int totalTests = 0, totalTime = 0;
                        Float totalPercentage = 0f;
                        for (int i = 0; i < listPerformance.size(); i++) {
                            totalTests = totalTests + Integer.parseInt(listPerformance.get(i).getTestCount());
                            totalTime = totalTime + Integer.parseInt(listPerformance.get(i).getTotalTimeSpent());
                            totalPercentage = totalPercentage + Float.parseFloat(listPerformance.get(i).getPercentage());
                        }
                        totalPercentage = totalPercentage / listPerformance.size();


                        tvTotalTestsCount.setText("" + totalTests);


                        tvTotalTimeSpent.setText("" + String.format("%02d:%02d:%02d", totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60));
                        tvTotalPercentage.setText("" + MyUtils.roundTwoDecimals(totalPercentage) + "%");
                    }

//                    listPerformance.add(new AnalysisTest());
                    if (listPerformance.size() > 0)
                        glTestsTaken.setAdapter(new CustomGrid(listPerformance));
                    else {
                        glTestsTaken.setVisibility(View.GONE);
                        tvNoExams.setVisibility(View.VISIBLE);
                    }


                    if (NetworkConnectivity.isConnected(getActivity())) {
                        new GetClassAnalysis().execute(branchId, studentId, courseId);
                    } else {
                        new MyUtils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close),false);
                    }

                } catch (Exception e) {

                }

            }
            utils.dismissDialog();
        }

    }

    private class GetClassAnalysis extends AsyncTask<String, Void, String> {

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


            utils.showLog(TAG, "Analysis Url - " + new AppUrls().GetTotalStudentMockTestsByClass+"schemaName=" +sh_Pref.getString("schema","") + "&courseId=" + strings[2] + "&studentId=" + strings[1] + "&branchId=" + strings[0]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetTotalStudentMockTestsByClass +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + strings[2] + "&studentId=" + strings[1] + "&branchId=" + strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Class Test Analysis - " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("testAnalysisClassArray");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AnalysisTestClass>>() {
                        }.getType();

                        listClassPerformance.clear();
                        listClassPerformance.addAll(gson.fromJson(jsonArr.toString(), type));

                        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
                            if (courseId.equalsIgnoreCase("" + sObj.getCourseId())) {
                                List<AnalysisTestClass> listClassPerformance2 = new ArrayList<>();
                                for (int i = 0; i < listClassPerformance.size(); i++) {
                                    if (listClassPerformance.get(i).getClassId().equals(sObj.getClassId())) {
                                        listClassPerformance2.add(listClassPerformance.get(i));
                                    }
                                }

                                listClassPerformance.clear();
                                listClassPerformance.addAll(listClassPerformance2);
                            }
                        }
                        if (listClassPerformance.size() > 0)
                            rvClassList.setAdapter(new ClassPerformanceAdapter(listClassPerformance));
                        else
                            tvClasswiseTitle.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    utils.showLog(TAG, e.getMessage());
                }

            }
            utils.dismissDialog();
        }

    }

    public class CustomGrid extends BaseAdapter {

        List<AnalysisTest> list;

        public CustomGrid(List<AnalysisTest> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View grid;
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
//                if (position != list.size() - 1) {
                grid = new View(getActivity());
                grid = inflater.inflate(R.layout.item_analysis_grid, null);
                TextView tvTestName = grid.findViewById(R.id.tv_test_name);
                TextView tvTestTaken = grid.findViewById(R.id.tv_test_taken);
                View v = grid.findViewById(R.id.divider);

                tvTestName.setText(list.get(position).getTestCategoryName());
                tvTestTaken.setText(list.get(position).getTestCount());
                if ((position + 1) % 3 == 0) {
                    v.setVisibility(View.INVISIBLE);
                }
//                } else {
//                    grid = new View(getActivity());
////                    grid = inflater.inflate(R.layout.view_all_layout, null);
////                    TextView tvViewAll = grid.findViewById(R.id.tv_view_all);
////                    tvViewAll.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////                            Toast.makeText(getActivity(), "View all exam logs", Toast.LENGTH_SHORT).show();
////                        }
////                    });
//                }
            } else {
                grid = convertView;
            }

            return grid;
        }
    }

    class ClassPerformanceAdapter extends RecyclerView.Adapter<ClassPerformanceAdapter.ViewHolder> {

        List<AnalysisTestClass> listPerf;

        ClassPerformanceAdapter(List<AnalysisTestClass> listPerf) {
            this.listPerf = listPerf;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.cv_analysisclass, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvClassName.setText(listPerf.get(position).getClassName());
            holder.tvScore.setText(listPerf.get(position).getPercentage() + "%");

            holder.tvTimeTaken.setText("" + String.format("%02d:%02d:%02d", listPerf.get(position).getTotalTimeSpent() / 3600, (listPerf.get(position).getTotalTimeSpent() % 3600) / 60, listPerf.get(position).getTotalTimeSpent() % 60));
            holder.tvTestTaken.setText(listPerf.get(position).getTestCount() + "");

            if (listPerf.get(position).getTestCount() == 0) {
                holder.layoutLinear.setBackground(getActivity().getResources().getDrawable(R.drawable.rounded_corners_grey14sh));
                holder.layoutLinear.setPadding(28,28,28,28);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listPerf.get(position).getTestCount() != 0) {
                        Intent intent = new Intent(getActivity(), AnalyticsClassSubPerformance.class);
                        intent.putExtra("analysisTestClass", listPerf.get(position));
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("studentId", studentId);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getActivity(), "No Test Taken", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listPerf.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvClassName, tvScore, tvTimeTaken, tvTestTaken;
            LinearLayout layoutLinear;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvClassName = itemView.findViewById(R.id.tv_className);
                tvScore = itemView.findViewById(R.id.tv_score);
                tvTimeTaken = itemView.findViewById(R.id.tv_timeSpent);
                tvTestTaken = itemView.findViewById(R.id.tv_testTaken);
                layoutLinear = itemView.findViewById(R.id.layout_linear);
            }
        }
    }
}
