package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Models.Test;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TeacherLivePreviousExams extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherLivePreviousExams.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_month_name)
    TextView tvMonthName;




    @BindView(R.id.rv_active)
    RecyclerView rvActive;


    @BindView(R.id.tv_active)
    TextView tvActive;

    @BindView(R.id.tv_history)
    TextView tvHistory;

    @BindView(R.id.cv_date_time)
    CardView cvDateTime;

    @BindView(R.id.ll_tabs)
    LinearLayout llTabs;

    String serverTime = "";


    String hmonth,hyear,aMonth, aYear;

    List<Test> active = new ArrayList<>();
    List<Test> history = new ArrayList<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;
    AdminObj adminObj;
    String userId, branchId;
    int selectTab = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_live_previous_exams);
        ButterKnife.bind(this);
        init();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
            utils.alertDialog(1, TeacherLivePreviousExams.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getServerTime();
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
        hmonth = new SimpleDateFormat("MM").format(new Date());
        hyear = new SimpleDateFormat("yyyy").format(new Date());
        aMonth = new SimpleDateFormat("MM").format(new Date());
        aYear = new SimpleDateFormat("yyyy").format(new Date());
        tvMonthName.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            userId = ""+tObj.getUserId();
            branchId = tObj.getBranchId();
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            String json = sh_Pref.getString("adminObj", "");
            adminObj = gson.fromJson(json, AdminObj.class);
            userId = ""+adminObj.getUserId();
            branchId = adminObj.getBranchId();
        }
        tvActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab = 0;
                findViewById(R.id.cv_no_active_exams).setVisibility(View.GONE);
                tvActive.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvActive.setTextColor(Color.WHITE);
                tvHistory.setBackground(null);
                tvHistory.setTextColor(Color.rgb(73, 73, 73));
                tvHistory.setAlpha(0.75f);
                if (active.size()>0){
                    rvActive.setVisibility(View.VISIBLE);
                    findViewById(R.id.cv_no_active_exams).setVisibility(View.GONE);
                    rvActive.setLayoutManager(new GridLayoutManager(TeacherLivePreviousExams.this,2));
                    rvActive.setAdapter(new TestActiveDetailsAdapter(active));
                }else {
                    rvActive.setVisibility(View.GONE);
                    findViewById(R.id.cv_no_active_exams).setVisibility(View.VISIBLE);
                }
            }
        });

        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab = 1;
                findViewById(R.id.cv_no_active_exams).setVisibility(View.GONE);
                tvHistory.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvHistory.setTextColor(Color.WHITE);
                tvActive.setBackground(null);
                tvActive.setTextColor(Color.rgb(73, 73, 73));
                tvActive.setAlpha(0.75f);
                if (history.size()>0){
                    rvActive.setVisibility(View.VISIBLE);
                    findViewById(R.id.cv_no_active_exams).setVisibility(View.GONE);
                    rvActive.setLayoutManager(new GridLayoutManager(TeacherLivePreviousExams.this,2));
                    rvActive.setAdapter(new TestHisDetailsAdapter(history));
                }else {
                    rvActive.setVisibility(View.GONE);
                    findViewById(R.id.cv_no_active_exams).setVisibility(View.VISIBLE);
                }
            }
        });

        cvDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        String mon = "";
                        switch (month) {
                            case 1:
                                mon = "January";
                                break;
                            case 2:
                                mon = "February";
                                break;
                            case 3:
                                mon = "March";
                                break;
                            case 4:
                                mon = "April";
                                break;
                            case 5:
                                mon = "May";
                                break;
                            case 6:
                                mon = "June";
                                break;
                            case 7:
                                mon = "July";
                                break;
                            case 8:
                                mon = "August";
                                break;
                            case 9:
                                mon = "September";
                                break;
                            case 10:
                                mon = "October";
                                break;
                            case 11:
                                mon = "November";
                                break;
                            case 12:
                                mon = "December";
                                break;
                        }
                        Calendar cal = Calendar.getInstance();

                        tvMonthName.setText(mon + " " + year);
                        aMonth = ""+month;
                        aYear = ""+year;
                        try {
                            Date selctdt = new SimpleDateFormat("MM yyyy").parse((month) +" "+ year);
                            Date today = new SimpleDateFormat("MM yyyy").parse(new SimpleDateFormat("MM yyyy").format(new Date()));
                            llTabs.setVisibility(View.GONE);
                            if (selctdt.after(today)){
                                tvActive.callOnClick();
                            }else if (selctdt.before(today)){
                                tvHistory.callOnClick();
                            }
                            else {
                                if (selctdt.equals(today)){
                                    llTabs.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        getServerTime();
                    }
                });

                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });
    }

    private void getServerTime() {

        utils.showLoader(TeacherLivePreviousExams.this);


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetServerTime)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetServerTime);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getTeacherClasses();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (response.body() != null) {
                    String resp = responseBody.string();
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            serverTime = parentjObject.getString("dateTime");
                            getTeacherClasses();
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, TeacherLivePreviousExams.this, message, sh_Pref);
                            });
                        }else {
                            getTeacherClasses();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    getTeacherClasses();
                }
            }
        });
    }


    private void getTeacherClasses() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "URL - " + AppUrls.GetMonthTestsForTeacher + "userId="+userId+"&schemaName=" + sh_Pref.getString("schema", "") + "&branchId="+branchId+"&year="+aYear+"&month="+aMonth);

        Request request = new Request.Builder()
                .url(AppUrls.GetMonthTestsForTeacher + "userId="+userId+"&schemaName=" + sh_Pref.getString("schema", "") + "&branchId="+branchId+"&year="+aYear+"&month="+aMonth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
//                        findViewById(R.id.cv_no_live_exams).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        active.clear();
                        history.clear();
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Test>>() {
                            }.getType();

                            active.clear();
                            history.clear();
                            List<Test> all = new ArrayList<>(gson.fromJson(jsonArr.toString(), type));

                            for (int i = 0; i < all.size(); i++) {
                                if (all.get(i).getTestEndDate()==null){
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date d = null;
                                    try {
                                        d = df.parse(all.get(i).getTestStartDate());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Calendar gc = new GregorianCalendar();
                                    gc.setTime(d);
                                    gc.add(Calendar.MINUTE, Integer.parseInt(all.get(i).getDuration()));
                                    Date d2 = gc.getTime();
                                    all.get(i).setTestEndDate(df.format(d2));
                                    Log.v(TAG, "End time " + all.get(i).getTestEndDate());
                                }
                            }

                            for (int i = 0; i < all.size(); i++) {
                                try {
                                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(all.get(i).getTestStartDate()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                        active.add(all.get(i));
                                    }  else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(all.get(i).getTestEndDate()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                        active.add(all.get(i));
                                    }else {
                                        history.add(all.get(i));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (selectTab==0){
                                        tvActive.callOnClick();
                                    }
                                    else tvHistory.callOnClick();
                                }
                            });
                        }
                        else {
                            runOnUiThread(() ->{
                                if (selectTab==0){
                                    tvActive.callOnClick();
                                }
                                else tvHistory.callOnClick();
                            } );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->{
                            if (selectTab==0){
                                tvActive.callOnClick();
                            }
                            else tvHistory.callOnClick();
                        } );

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


    private class TestActiveDetailsAdapter extends RecyclerView.Adapter<TestActiveDetailsAdapter.ViewHolder> {

        List<Test> testList;

        TestActiveDetailsAdapter(List<Test> testList) {
            this.testList = testList;
        }

        @NonNull
        @Override
        public TestActiveDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.v(TAG, "onCreateViewHolder");
            View view_adapter = LayoutInflater.from(TeacherLivePreviousExams.this).inflate(R.layout.item_teacher_upcoming_tests, parent, false);
            return new TestActiveDetailsAdapter.ViewHolder(view_adapter);
        }

        @Override
        public void onBindViewHolder(@NonNull TestActiveDetailsAdapter.ViewHolder holder, int position) {
            Log.v(TAG, "Test Name " + testList.get(position).getTestName());
            holder.tvTestName.setText(testList.get(position).getTestName());
            holder.tvTestStartDate.setText(testList.get(position).getTestStartDate());
//            holder.tvTestEndTime.setText("Test Category: " + testList.get(position).getTe());

//            holder.tvTestStatus.setText(testList.get(position).getTestStatus());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherLivePreviousExams.this, TeacherLiveExamDetails.class);
                    intent.putExtra("test", testList.get(position));
                    startActivity(intent);
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

    private class TestHisDetailsAdapter extends RecyclerView.Adapter<TestHisDetailsAdapter.ViewHolder> {

        List<Test> testList;

        TestHisDetailsAdapter(List<Test> testList) {
            this.testList = testList;
        }

        @NonNull
        @Override
        public TestHisDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.v(TAG, "onCreateViewHolder");
            View view_adapter = LayoutInflater.from(TeacherLivePreviousExams.this).inflate(R.layout.item_teacher_upcoming_tests, parent, false);
            return new TestHisDetailsAdapter.ViewHolder(view_adapter);
        }

        @Override
        public void onBindViewHolder(@NonNull TestHisDetailsAdapter.ViewHolder holder, int position) {
            Log.v(TAG, "Test Name " + testList.get(position).getTestName());
            holder.tvTestName.setText(testList.get(position).getTestName());
            holder.tvTestStartDate.setText(testList.get(position).getTestStartDate());
//            holder.tvTestEndTime.setText("Test Category: " + testList.get(position).getTe());

//            holder.tvTestStatus.setText(testList.get(position).getTestStatus());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherLivePreviousExams.this, TeacherLiveExamDetails.class);
                    intent.putExtra("test", testList.get(position));
                    startActivity(intent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }



}