/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.MyUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignedTestAnalyticsFrag extends Fragment {

    private static final String TAG = AssignedTestAnalyticsFrag.class.getName();
    MyUtils utils = new MyUtils();

    View view;
    @BindView(R.id.tv_recordsotavailable)
    TextView tvRecordsotavailable;
    @BindView(R.id.ll_tests)
    LinearLayout llTests;
    Unbinder unbinder;

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();

    SharedPreferences sh_Pref;
    StudentObj sObj;

    public AssignedTestAnalyticsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assigned_test_analytics, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();

        if (NetworkConnectivity.isConnected(getActivity())) {
            new GetStudentHisTests().execute();
        }else{
            new MyUtils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);

        }


        return view;
    }

    private void init() {
        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
    }

    private String getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        month = month + 1;
        return month + "";
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class GetStudentHisTests extends AsyncTask<String, Void, String> {

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

            utils.showLog(TAG, "URL - " + AppUrls.GetStudentOnlineTests +"schemaName=" +getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+"&studentId="+sObj.getStudentId()+"&branchId="+sObj.getBranchId()+"&flag=Completed");

            Request request = new Request.Builder()
                    .url( AppUrls.GetStudentOnlineTests +"schemaName=" +getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+"&studentId="+sObj.getStudentId()+"&branchId="+sObj.getBranchId()+"&flag=Completed")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "Online Test List Responce- " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("StudentTest");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<StudentOnlineTests>>() {
                        }.getType();

                        studentOnlineTests.clear();
                        studentOnlineTests.addAll(gson.fromJson(jsonArr.toString(), type));
                        utils.showLog(TAG,""+studentOnlineTests.size());

                        if (studentOnlineTests.size() > 0) {
                            for (int i = 0; i < studentOnlineTests.size(); i++) {

                                View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_months, llTests, false);
                                TextView tv = v.findViewById(R.id.tv_month);
                                if (studentOnlineTests.get(i).getMonthId().equalsIgnoreCase(getCurrentMonth())) {
                                    tv.setText("THIS MONTH");
                                } else {
                                    tv.setText("PREVIOUS MONTHS (" + studentOnlineTests.get(i).getMonthName().toUpperCase() + ")");
                                }
                                RecyclerView rv_tests = v.findViewById(R.id.rv_tests);
                                rv_tests.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                rv_tests.setAdapter(new OnlineTestListAdapter(studentOnlineTests.get(i).getTests()));

                                llTests.addView(v);
                            }

                        } else
                            tvRecordsotavailable.setVisibility(View.VISIBLE);


                    } else
                        tvRecordsotavailable.setVisibility(View.VISIBLE);


                } catch (Exception e) {
                    tvRecordsotavailable.setVisibility(View.VISIBLE);
                }

            } else {

            }
            utils.dismissDialog();
        }

    }

    class OnlineTestListAdapter extends RecyclerView.Adapter<OnlineTestListAdapter.ViewHolder>{

        List<StudentOnlineTestObj> testList = new ArrayList<>();

        public OnlineTestListAdapter(List<StudentOnlineTestObj> tests) {
            this.testList.clear();
            this.testList.addAll(tests);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.cv_onlinetest, parent, false);
            return new ViewHolder(v);
        }

        String convertDate(String d) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdft = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            String dsemi = sdft.format(date);

            String[] timeYear = dsemi.split(" ");
            String time = timeYear[0];
            String[] hourMin = time.split(":");
            String timeFinal = "";
            int hour = Integer.parseInt(hourMin[0]);
            if (hour > 12) {
                hour = hour - 12;
                timeFinal = hour + ":" + hourMin[1] + " PM";
            } else if (hour < 12) {
                timeFinal = hour + ":" + hourMin[1] + " AM";
            } else {
                timeFinal = hour + ":" + hourMin[1] + " PM";
            }
            return timeFinal + " " + timeYear[1];
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            holder.tvTestName.setText(testList.get(position).getTestName());
            holder.tvCategory.setText(testList.get(position).getTestCategoryName());
            holder.tvStart.setText(convertDate(testList.get(position).getTestStartDate()));
            holder.tvEnd.setText(convertDate(testList.get(position).getTestEndDate()));

            holder.tvStartTest.setText(testList.get(position).getStudentTestStatus());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date end = null;
            try {
                end = sdf.parse(testList.get(position).getTestEndDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvStartTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.tvStartTest.getText().toString().equalsIgnoreCase("Completed")) {
                        utils.showLog(TAG, "testId - " + testList.get(position).getTestId());
//                        Intent onlineTestIntent = new Intent(getActivity(), StudentOnlineTestResult.class);
//                        onlineTestIntent.putExtra("studentId", sObj.getStudentId());
//                        onlineTestIntent.putExtra("testId", testList.get(position).getTestId());
//                        onlineTestIntent.putExtra("testName", testList.get(position).getTestName());
//                        onlineTestIntent.putExtra("testType", testList.get(position).getTestCategoryName());
//                        onlineTestIntent.putExtra("jeeSectionTemplate", testList.get(position).getMjeeSectionTemplateName());
//                        startActivity(onlineTestIntent);
                    } else {
                        Toast.makeText(getActivity(), "Result will be Published once the Test is Complete", Toast.LENGTH_SHORT).show();
                    }

                }
            });



            holder.tvLive.setVisibility(View.INVISIBLE);

        }

        @Override
        public int getItemCount() {
            return testList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView tvTestName, tvStart, tvEnd, tvCategory, tvLive, tvStartTest;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvStart = itemView.findViewById(R.id.tv_start_date_time);
                tvEnd = itemView.findViewById(R.id.tv_end_date_time);
                tvCategory = itemView.findViewById(R.id.tv_test_category);
                tvLive = itemView.findViewById(R.id.tv_live);
                tvStartTest = itemView.findViewById(R.id.tv_startTest);
            }
        }
    }

}
