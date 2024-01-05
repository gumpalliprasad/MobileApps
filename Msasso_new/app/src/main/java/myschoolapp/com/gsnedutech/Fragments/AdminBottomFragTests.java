/*
 * *
 *  * Created by SriRamaMurthy A on 16/9/19 2:15 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 16/9/19 12:35 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.*;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class AdminBottomFragTests extends Fragment {

    private static final String TAG = "SriRam -" + AdminBottomFragFeatured.class.getName();

    View adminTestsView;
    @BindView(R.id.ll_months)
    LinearLayout llMonths;
    @BindView(R.id.ll_tests)
    LinearLayout llTests;
    TextView selected;
    @BindView(R.id.tv_records)
    TextView tvRecords;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    AdminObj adminObj;

    List<AdminTestCategory> listTestCategories = new ArrayList<>();
    List<AdminTestsYearObj> listYears = new ArrayList<>();
    List<AdminTestObj> listTests = new ArrayList<>();


    public AdminBottomFragTests() {
        // Required empty public constructor
    }

    int x = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adminTestsView = inflater.inflate(R.layout.fragment_admin_bottom_frag_tests, container, false);
        unbinder = ButterKnife.bind(this, adminTestsView);

        init();

        return adminTestsView;
    }

    void init() {


        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("adminObj", "");
        adminObj = gson.fromJson(json, AdminObj.class);

        llTests = adminTestsView.findViewById(R.id.ll_tests);
        llMonths = adminTestsView.findViewById(R.id.ll_months);

//        showSkeleton();

        new GetAllTests().execute();

    }

    @OnClick(R.id.fab_newTest)
    public void onViewClicked() {
//        startActivity(new Intent(getActivity(), AdminNewTestCreation.class));
    }




    void setLayout() {

        for (int i = 0; i < listYears.size(); i++) {
            List<AdminTestsMonthObj> listMonths = new ArrayList<>();
            listMonths = listYears.get(i).getMonths();
            for (int j = 0; j < listMonths.size(); j++) {
                View view_tv_headers = LayoutInflater.from(getActivity()).inflate(R.layout.tv_tests_header, null, false);
                TextView tv = view_tv_headers.findViewById(R.id.tv_header);
                tv.setGravity(Gravity.CENTER);
                tv.setText(listMonths.get(j).getMonthName() + "-" + listYears.get(i).getYearId() + "");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                lp.setMargins(0, 12, 24, 32);
                llMonths.addView(tv, lp);
                if (j == 0 && i==0) {
                    selected = tv;
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundResource(R.drawable.rounded_corners_blue14sh8pad);
                    new GetTestDetails().execute(listYears.get(i).getYearId(), listMonths.get(j).getMonthId());
                } else {
                    tv.setBackgroundResource(R.drawable.rounded_corners_white14_p2015);
                }

                List<AdminTestsMonthObj> finalListMonths = listMonths;
                int finalJ = j;
                int finalI = i;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected.setBackgroundResource(R.drawable.rounded_corners_white14_p2015);
                        selected.setTextColor(Color.BLACK);
                        selected = (TextView) view;
                        selected.setBackgroundResource(R.drawable.rounded_corners_blue14sh8pad);
                        selected.setTextColor(Color.WHITE);
                        selected.getParent().requestChildFocus(selected, selected);
//                        showSecondarySkeleton();
                        //setWeekLayout(finalListMonths.get(finalJ).getWeeks(), finalListMonths.get(finalJ).getMonthId(), listYears.get(finalI).getYearId());
                        new GetTestDetails().execute(listYears.get(finalI).getYearId(), finalListMonths.get(finalJ).getMonthId());
                    }
                });

            }
        }
    }

    int getTotalWeeks(String monthId, String yearId) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Integer.parseInt(yearId));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(monthId));
        cal.set(Calendar.MONTH, 1);
        int maxWeeknumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        Log.v("TAG", "Max WEEk " + maxWeeknumber + "");
        return maxWeeknumber;

    }

    int getWeekNumberFromDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        Calendar now = Calendar.getInstance();

        try {
            now.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int x = now.get(Calendar.WEEK_OF_MONTH);
        Log.v("TAG", "week " + x);
        x = x - 1;

        return x;
    }

    void setWeekLayout(String monthId, String yearId) {
        Log.v("TAG", "Month year " + monthId + " " + yearId);
        int totalWeeks = getTotalWeeks(monthId, yearId);
        llTests.removeAllViews();
        for (int k = 0; k < totalWeeks; k++) {
            List<AdminTestObj> testObjList = new ArrayList<>();
            testObjList.clear();
            RecyclerView rvTests = null;
            int z = 0;
            for (int i = 0; i < listTests.size(); i++) {

                String date = "";

                if (listTests.get(i).getTestStartDate().equalsIgnoreCase("blank")) {
                    date = listTests.get(i).getCreatedDate();
                } else {
                    date = listTests.get(i).getTestStartDate();
                }

                if (k == getWeekNumberFromDate(date)) {
                    testObjList.add(listTests.get(i));
                    z = k + 1;
                }
            }
            Log.v("TAG", "test in week  " + testObjList.size());
            if (testObjList.size() > 0) {
                Log.v("TAG", "Z value " + z);
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_months, llTests, false);
                TextView tv = v.findViewById(R.id.tv_month);
                tv.setText("Week " + z);
                rvTests = v.findViewById(R.id.rv_tests);
                rvTests.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                llTests.addView(v);
                TestAdapter testAdapter = new TestAdapter(testObjList);
                rvTests.setAdapter(testAdapter);
            } else {
//                TODO : Remove the Skeleton iof we are not getting the Tests

            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class GetAllTests extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            Log.v(TAG, "URL - " + AppUrls.GetTestOfAdminByCalender +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId());


            Request request = new Request.Builder()
                    .url(AppUrls.GetTestOfAdminByCalender +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&roleId=" + adminObj.getRoleId())
                    .build();


//            Request request = new Request.Builder()
//                    .url("http://45.127.101.187:9009/getTestByCalender?schemaName=abc5527&roleId=1&branchId=1")
//                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "Test Details- " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");
                        if (getActivity() != null && isAdded()) {

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminTestCategory>>() {
                            }.getType();

                            listTestCategories.addAll(gson.fromJson(jsonArr.toString(), type));
                            if (listTestCategories.size() > 0) {
                                for (int i = 0; i < listTestCategories.size(); i++) {
                                    if (listTestCategories.get(i).getBranchId().equalsIgnoreCase("1")) {
                                        listYears.addAll(listTestCategories.get(0).getYears());
                                        setLayout();
                                    }
                                }
                            } else {
                                tvRecords.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                } catch (Exception e) {

                }

            }

        }

    }

    private class GetTestDetails extends AsyncTask<String, Void, String> {


        String month, year;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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

            year = strings[0];
            month = strings[1];

            Log.v(TAG, "URL - " + AppUrls.GetAllTestsForAdminByCalnderForMobile +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&year=" + strings[0] + "&monthId=" + strings[1]);


            Request request = new Request.Builder()
                    .url(AppUrls.GetAllTestsForAdminByCalnderForMobile +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0") + "&year=" + strings[0] + "&monthId=" + strings[1])
                    .build();

//            Request request = new Request.Builder()
//                    .url("http://45.127.101.187:9009/getAllTestsForAdminByCalnderForMobile?schemaName=abc5527&branchId=1&year=" + strings[0] + "&monthId=" + strings[1])
//                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Test Details- " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("TestList");
                        if (getActivity() != null && isAdded()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminTestObj>>() {
                            }.getType();


                            listTests = gson.fromJson(jsonArr.toString(), type);
                            Log.v("TAG", "testSize " + listTests.size());
                            setWeekLayout(month, year);
                            //rvTests.setAdapter(new TestAdapter(listTests));
                        }
                    }

                } catch (Exception e) {

                }

            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (x == 0) {
                        if (getActivity() != null && isAdded()) {
//                            shimmer.stopShimmerAnimation();
//                            shimmer.setVisibility(View.GONE);
                            llMonths.setVisibility(View.VISIBLE);
                            llTests.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (getActivity() != null && isAdded()) {
                            llTests.setVisibility(View.VISIBLE);
//                            shimmerSecondary.stopShimmerAnimation();
//                            shimmerSecondary.setVisibility(View.GONE);
                        }
                    }

                }
            }, 2000);

        }

    }

    class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

        List<AdminTestObj> listtest;

        TestAdapter(List<AdminTestObj> listtest) {
            this.listtest = listtest;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.cv_testcard, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvTestName.setText(listtest.get(position).getTestName());

            if (listtest.get(position).getTestStatus().equalsIgnoreCase("COMPLETED")){
                holder.llTest.setBackground(getActivity().getDrawable(R.drawable.rounded_corner_0155b7_5));
            } else if (listtest.get(position).getTestStatus().equalsIgnoreCase("INCOMPLETE")){
                holder.tvStatus.setBackground(getActivity().getDrawable(R.drawable.bg_live));
                holder.llTest.setBackground(getActivity().getDrawable(R.drawable.rounded_corner_575757_5));
            }

            if (listtest.get(position).getTestStartDate().equalsIgnoreCase("blank")) {
                holder.tvScheduleDate.setText("");
            } else {
                holder.tvScheduleDate.setText(listtest.get(position).getTestStartDate());
            }
            holder.tvTestCategory.setText("Test Category: " + listtest.get(position).getTestCategory());
            holder.tvAssignedTo.setText(listtest.get(position).getSectionName());
            holder.tvLastEdited.setText(listtest.get(position).getCreatedDate());
            holder.tvStatus.setText(listtest.get(position).getTestStatus());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), AdminTestInfo.class);
//                    intent.putExtra("testDetails", listtest.get(position));
//                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listtest.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llTest;
            TextView tvTestName, tvScheduleDate, tvTestCategory, tvAssignedTo, tvLastEdited, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvScheduleDate = itemView.findViewById(R.id.tv_schedule_date);
                tvTestCategory = itemView.findViewById(R.id.tv_test_category);
                tvAssignedTo = itemView.findViewById(R.id.tv_assigned_to);
                tvLastEdited = itemView.findViewById(R.id.tv_last_edited);
                tvStatus = itemView.findViewById(R.id.tv_status);
                llTest = itemView.findViewById(R.id.ll_test);
            }
        }
    }

}
