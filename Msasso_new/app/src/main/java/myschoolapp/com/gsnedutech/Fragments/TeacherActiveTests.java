/*
 * *
 *  * Created by SriRamaMurthy A on 16/10/19 12:32 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 16/10/19 12:31 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Models.TeacherTestMonth;
import myschoolapp.com.gsnedutech.Models.TeacherTestWeek;
import myschoolapp.com.gsnedutech.Models.TeacherTestYear;
import myschoolapp.com.gsnedutech.Models.Test;
import myschoolapp.com.gsnedutech.Models.TestCategory;
import myschoolapp.com.gsnedutech.R;
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
public class TeacherActiveTests extends Fragment {

    private static final String TAG = "SriRam -" + TeacherActiveTests.class.getName();
    View viewTeacherActiveTestsNew;
    Unbinder unbinder;
    MyUtils utils = new MyUtils();
    Activity mActivity;

    SharedPreferences sh_Pref;
    TeacherObj teacherObj;

    List<TestCategory> testCategoriesList = new ArrayList<>();
    @BindView(R.id.rv_months)
    RecyclerView rvMonths;
    @BindView(R.id.rv_tests)
    RecyclerView rvTests;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    MonthAdapter.ViewHolder onClicked = null;

    HashMap<String, List<Test>> map = new HashMap<>();
    @BindView(R.id.tv_no_records)
    TextView tvNoRecords;

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public TeacherActiveTests() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewTeacherActiveTestsNew = inflater.inflate(R.layout.fragment_teacher_active_tests, container, false);
        unbinder = ButterKnife.bind(this, viewTeacherActiveTestsNew);

        init();
        refresh();



        return viewTeacherActiveTestsNew;
    }

    void refresh(){
        if (NetworkConnectivity.isConnected(getActivity())) {
            getTestByCalendarForTeacherDetails("" + teacherObj.getUserId());
        }else{
            utils.alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }



    void init() {
        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvMonths.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

    }

    private void getTestByCalendarForTeacherDetails(String userId) {
        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + new AppUrls().Test_GetTestByCalendarForTeacher + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId);

        Request request = new Request.Builder()
                .url(new AppUrls().Test_GetTestByCalendarForTeacher + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId)
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
                String jsonResp = response.body().string();

                Log.v(TAG, "Teacher test - " + jsonResp);
                if (response.body()!=null){

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<TestCategory>>() {
                                    }.getType();

                                    testCategoriesList.clear();
                                    map = new HashMap<>();
                                    testCategoriesList.addAll(gson.fromJson(jsonArr.toString(), type));
                                    Log.v(TAG, "Test Categories size " + testCategoriesList.size());

                                    if (testCategoriesList.size()==0){
                                        rvMonths.setVisibility(View.GONE);
                                        rvTests.setVisibility(View.GONE);
                                        tvNoRecords.setVisibility(View.VISIBLE);
                                    }

                                    setLayout();

                                }else {
                                    rvMonths.setVisibility(View.GONE);
                                    rvTests.setVisibility(View.GONE);
                                    tvNoRecords.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                mActivity.runOnUiThread(() -> utils.dismissDialog());
            }
        });

    }


    void setLayout() {

        List<String> monthNames = new ArrayList<>();

        for (int i = 0; i < testCategoriesList.size(); i++) {
            List<TeacherTestYear> listYears = new ArrayList<>();
            listYears.addAll(testCategoriesList.get(i).getTeacherTestYears());
            for (int j = 0; j < listYears.size(); j++) {
                if (Integer.parseInt(listYears.get(j).getYearId()) >= Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()))) {
                    List<TeacherTestMonth> listMonths = new ArrayList<>();
                    listMonths.addAll(listYears.get(j).getTeacherTestMonths());
                    for (int k = 0; k < listMonths.size(); k++) {
                        if (Integer.parseInt(listMonths.get(k).getMonthId()) >= Integer.parseInt(getCurrentMonth())) {
                            monthNames.add(listMonths.get(k).getMonthName() + " " + listYears.get(j).getYearId());
                        }
                    }
                }
            }
        }

        rvMonths.setAdapter(new MonthAdapter(monthNames));


        for (int i = 0; i < testCategoriesList.size(); i++) {
            List<TeacherTestYear> listYears = new ArrayList<>();
            listYears.addAll(testCategoriesList.get(i).getTeacherTestYears());
            for (int j = 0; j < listYears.size(); j++) {
                if (Integer.parseInt(listYears.get(j).getYearId()) == Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()))) {
                    List<TeacherTestMonth> listMonths = new ArrayList<>();
                    listMonths.addAll(listYears.get(j).getTeacherTestMonths());
                    for (int k = 0; k < listMonths.size(); k++) {
                        if (Integer.parseInt(listMonths.get(k).getMonthId()) == Integer.parseInt(getCurrentMonth())) {
                            List<TeacherTestWeek> listWeeks = new ArrayList<>();
                            listWeeks.addAll(listMonths.get(k).getTeacherTestWeeks());

                            int c=0;

                            for (int l = 0; l < listWeeks.size(); l++) {
                                c++;
                                if (NetworkConnectivity.isConnected(getActivity())) {
//                                    getAllTestsForTeacherByCalendarDetails(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "Week "+listWeeks.get(l).getWeekOfMonth());
                                    new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "Week "+listWeeks.get(l).getWeekOfMonth());
                                }else{
                                    utils.alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                                            getString(R.string.action_settings), getString(R.string.action_close),false);
                                }
//                                if (Integer.parseInt(listWeeks.get(l).getWeekOfMonth()) == Integer.parseInt(getCurrentWeek())) {
//                                    c++;
//                                    if (NetworkConnectivity.isConnected(getActivity())) {
//                                        new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "This week");
//                                    }else{
//                                        new Utils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
//                                                getString(R.string.action_settings), getString(R.string.action_close));
//                                    }
//
//                                } else if (Integer.parseInt(listWeeks.get(l).getWeekOfMonth()) >= Integer.parseInt(getCurrentWeek())) {
//                                    c++;
//                                    if (NetworkConnectivity.isConnected(getActivity())) {
//                                        new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "Upcoming week");
//                                    }else{
//                                        new Utils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
//                                                getString(R.string.action_settings), getString(R.string.action_close));
//                                    }
//
//                                }
                            }
                            if (c == 0) {
                                tvNoRecords.setVisibility(View.VISIBLE);
                                rvTests.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                else if (Integer.parseInt(listYears.get(j).getYearId()) > Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()))){
                    List<TeacherTestMonth> listMonths = new ArrayList<>();
                    listMonths.addAll(listYears.get(j).getTeacherTestMonths());
                    for (int k = 0; k < listMonths.size(); k++) {
                        List<TeacherTestWeek> listWeeks = new ArrayList<>();
                        listWeeks.addAll(listMonths.get(k).getTeacherTestWeeks());
                        for (int l = 0; l < listWeeks.size(); l++) {
                            if (NetworkConnectivity.isConnected(getActivity())) {
                                new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "Week "+listWeeks.get(l).getWeekOfMonth());
                            }else{
                                utils.alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close),false);
                            }


                        }
                    }
                }
            }
        }

    }

    String getCurrentMonth() {
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        Log.v(TAG, "Current TeacherTestMonth: " + calender.get(Calendar.MONTH) + "");

        int month = calender.get(Calendar.MONTH) + 1;
        return month + "";
    }

    String getCurrentWeek() {
        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());
        Log.v(TAG, "Current TeacherTestWeek: " + calender.get(Calendar.WEEK_OF_MONTH) + "");
        return calender.get(Calendar.WEEK_OF_MONTH) + "";
    }

    int getNumberOfWeeks(String month) {
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 11; i++) {
            int maxWeeknumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
            if (Integer.parseInt(month) == i + 1) {
                return maxWeeknumber;
            }
            Log.v("LOG", "max week number" + maxWeeknumber);
        }
        return 13;
    }

    int getMonthNumber(String monthName) {
        Date date = null;
        try {
            date = new SimpleDateFormat("MMMM").parse(monthName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH) + 1;
    }

    class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {

        List<String> listMonths;

        MonthAdapter(List<String> listMonths) {
            this.listMonths = listMonths;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.tv_tests_header, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvHeader.setText(listMonths.get(position));

            if (position == 0) {
                onClicked = holder;
                holder.tvHeader.setBackgroundResource(R.drawable.underline);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClicked.itemView.setBackgroundResource(0);
                    holder.tvHeader.setBackgroundResource(R.drawable.underline);
                    onClicked = holder;
                    map.clear();

//                    if (position == 0) {
//                        for (int i = 0; i < testCategoriesList.size(); i++) {
//                            List<TeacherTestYear> listYears = new ArrayList<>();
//                            listYears.addAll(testCategoriesList.get(i).getTeacherTestYears());
//                            for (int j = 0; j < listYears.size(); j++) {
//                                if (Integer.parseInt(listYears.get(j).getYearId()) == Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()))) {
//                                    List<TeacherTestMonth> listMonths = new ArrayList<>();
//                                    listMonths.addAll(listYears.get(j).getTeacherTestMonths());
//                                    for (int k = 0; k < listMonths.size(); k++) {
//                                        if (Integer.parseInt(listMonths.get(k).getMonthId()) == Integer.parseInt(getCurrentMonth())) {
//                                            List<TeacherTestWeek> listWeeks = new ArrayList<>();
//                                            listWeeks.addAll(listMonths.get(k).getTeacherTestWeeks());
//
//                                            int c = 0;
//
//                                            for (int l = 0; l < listWeeks.size(); l++) {
//                                                if (Integer.parseInt(listWeeks.get(l).getWeekOfMonth()) == Integer.parseInt(getCurrentWeek())) {
//                                                    c++;
//                                                    if (NetworkConnectivity.isConnected(getActivity())) {
//                                                        new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "This week");
//                                                    }else{
//                                                        new Utils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
//                                                                getString(R.string.action_settings), getString(R.string.action_close));
//                                                    }
//
//                                                } else if (Integer.parseInt(listWeeks.get(l).getWeekOfMonth()) > Integer.parseInt(getCurrentWeek())) {
//                                                    c++;
//                                                    if (NetworkConnectivity.isConnected(getActivity())) {
//                                                        new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "Upcoming week");
//                                                    }else{
//                                                        new Utils().alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
//                                                                getString(R.string.action_settings), getString(R.string.action_close));
//                                                    }
//
//                                                }
//                                            }
//
//                                            if (c == 0) {
//                                                tvNoRecords.setVisibility(View.VISIBLE);
//                                                rvTests.setVisibility(View.VISIBLE);
//                                            }
//
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    } else {


                    String month = listMonths.get(position).split(" ")[0], year = listMonths.get(position).split(" ")[1];

                    for (int i = 0; i < testCategoriesList.size(); i++) {
                        List<TeacherTestYear> listYears = new ArrayList<>();
                        listYears.addAll(testCategoriesList.get(i).getTeacherTestYears());
                        for (int j = 0; j < listYears.size(); j++) {
                            if (Integer.parseInt(listYears.get(j).getYearId()) == Integer.parseInt(year)) {
                                List<TeacherTestMonth> listMonths = new ArrayList<>();
                                listMonths.addAll(listYears.get(j).getTeacherTestMonths());
                                for (int k = 0; k < listMonths.size(); k++) {
                                    if (Integer.parseInt(listMonths.get(k).getMonthId()) == getMonthNumber(month)) {
                                        List<TeacherTestWeek> listWeeks = new ArrayList<>();
                                        listWeeks.addAll(listMonths.get(k).getTeacherTestWeeks());

                                        for (int l = 0; l < listWeeks.size(); l++) {
                                            if (NetworkConnectivity.isConnected(getActivity())) {
                                                new GetAllTestsForTeacherByCalendarDetails().execute(teacherObj.getUserId() + "", teacherObj.getBranchId(), listYears.get(j).getYearId(), listMonths.get(k).getMonthId(), listWeeks.get(l).getWeekOfMonth(), "Week" + listWeeks.get(l).getWeekOfMonth());
                                            }else{
                                                utils.alertDialog(1, getActivity(), getString(R.string.error_connect), getString(R.string.error_internet),
                                                        getString(R.string.action_settings), getString(R.string.action_close),false);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

//                }
            });

        }

        @Override
        public int getItemCount() {
            return listMonths.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvHeader;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvHeader = itemView.findViewById(R.id.tv_header);

            }
        }
    }

    private void getAllTestsForTeacherByCalendarDetails(String userId, String branchId, String yearId, String monthId, String weekOfMonth, String weekString) {
        String key = weekString;
        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + new AppUrls().Test_GetAllTestsForTeacherByCalendar + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId
                + "&branchId=" + branchId + "&year=" + yearId + "&monthId=" + monthId + "&week=" +weekOfMonth);

        Request request = new Request.Builder()
                .url(new AppUrls().Test_GetAllTestsForTeacherByCalendar + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId
                        + "&branchId=" + branchId + "&year=" + yearId + "&monthId=" + monthId + "&week=" +weekOfMonth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }


    private class GetAllTestsForTeacherByCalendarDetails extends AsyncTask<String, Void, String> {

        String key;

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(getActivity());
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            key = strings[5];

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "URL - " + new AppUrls().Test_GetAllTestsForTeacherByCalendar + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + strings[0]
                    + "&branchId=" + strings[1] + "&year=" + strings[2] + "&monthId=" + strings[3] + "&week=" + strings[4]);

            Request request = new Request.Builder()
                    .url(new AppUrls().Test_GetAllTestsForTeacherByCalendar + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + strings[0]
                            + "&branchId=" + strings[1] + "&year=" + strings[2] + "&monthId=" + strings[3] + "&week=" + strings[4])
                    .build();


            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "Test Details - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (getActivity() != null && isAdded()) {
                if (responce != null) {
                    try {
                        JSONObject ParentjObject = new JSONObject(responce);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Test>>() {
                            }.getType();

                            List<Test> listTestDetails = new ArrayList<>();


                            listTestDetails.clear();
                            listTestDetails.addAll(gson.fromJson(jsonArr.toString(), type));

                            map.put(key, listTestDetails);

                            rvTests.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rvTests.setAdapter(new TestAdapter(map));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                loading.dismiss();
            }
        }
    }


    class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

        HashMap<String, List<Test>> mapExams;


        TestAdapter(HashMap<String, List<Test>> mapExams) {
            this.mapExams = mapExams;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_teacher_tests_weekly, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            List<String> keys = new ArrayList<String>(map.keySet());
            holder.tvHeader.setText(keys.get(position));
            holder.rvTestDetails.setLayoutManager(new GridLayoutManager(getActivity(),2));
            holder.rvTestDetails.setAdapter(new TestDetailsAdapter(mapExams.get(keys.get(position))));

        }

        @Override
        public int getItemCount() {
            return mapExams.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvHeader;
            RecyclerView rvTestDetails;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvHeader = itemView.findViewById(R.id.tv_header);
                rvTestDetails = itemView.findViewById(R.id.rv_test_details);

            }
        }
    }


    private class TestDetailsAdapter extends RecyclerView.Adapter<TestDetailsAdapter.ViewHolder> {

        List<Test> testList;

        TestDetailsAdapter(List<Test> testList) {
            this.testList = testList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.v(TAG, "onCreateViewHolder");
            View view_adapter = LayoutInflater.from(getActivity()).inflate(R.layout.item_teacher_upcoming_tests, parent, false);
            return new ViewHolder(view_adapter);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Log.v(TAG, "Test Name " + testList.get(position).getTestName());
            holder.tvTestName.setText(testList.get(position).getTestName());
            holder.tvTestStartDate.setText(testList.get(position).getTestStartDate());
//            holder.tvTestEndTime.setText("Test Category: " + testList.get(position).getTe());

//            holder.tvTestStatus.setText(testList.get(position).getTestStatus());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(getActivity(), TeacherActiveTestDetails.class);
//                    intent.putExtra("assignedBranchId", testList.get(position).getAssignedBranchId());
//                    intent.putExtra("branchId", testList.get(position).getBranchId());
//                    intent.putExtra("wrongMarks", testList.get(position).getWrongMarks());
//                    intent.putExtra("testCategory", testList.get(position).getTestCategory());
//                    intent.putExtra("testStartDate", testList.get(position).getTestStartDate());
//                    intent.putExtra("testType", testList.get(position).getTestType());
//                    intent.putExtra("numQuestions", testList.get(position).getNumQuestions());
//                    intent.putExtra("duration", testList.get(position).getDuration());
//                    intent.putExtra("sectionName", testList.get(position).getSectionName());
//                    intent.putExtra("testCreatedDate", testList.get(position).getTestCreatedDate());
//                    intent.putExtra("testStatus", testList.get(position).getTestStatus());
//                    intent.putExtra("testId", testList.get(position).getTestId());
//                    intent.putExtra("testName", testList.get(position).getTestName());
//                    intent.putExtra("correctMarks", testList.get(position).getCorrectMarks());
//                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.v(TAG, "size " + testList.size());
            return testList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTestName, tvTestStartDate, tvTestEndTime, tvTestStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTestStartDate = itemView.findViewById(R.id.tv_test_date);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
            }
        }
    }


}