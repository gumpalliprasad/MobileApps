package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.DateObj;
import myschoolapp.com.gsnedutech.Models.Events;
import myschoolapp.com.gsnedutech.Models.EventsAndHoliday;
import myschoolapp.com.gsnedutech.Models.HomeWork;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetail;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.LiveVideo;
import myschoolapp.com.gsnedutech.Models.LiveVideoInfo;
import myschoolapp.com.gsnedutech.Models.ScheduleObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import myschoolapp.com.gsnedutech.activity.fee.FeeCategoriesActivity;
import myschoolapp.com.gsnedutech.activity.fee.FeeDueDetailsActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ParentHome extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + ParentHome.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    StudentObj sObj;

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout navDrawer;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.rv_hw)
    RecyclerView rvHw;
    @BindView(R.id.rv_cal)
    RecyclerView rvCal;
    @BindView(R.id.rv_events)
    RecyclerView rvEvents;

    @BindView(R.id.tv_month_name)
    TextView tvMonthName;
    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_view_more)
    TextView tvViewMore;

    @BindView(R.id.tv_attendance)
    TextView tvAttendance;

    @BindView(R.id.rv_upcoming_test)
    RecyclerView rvUpcomingTests;

    @BindView(R.id.cv_no_live_exams)
    CardView cvNoLiveExam;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;


    List<HomeWork> hwList = new ArrayList<>();

    String studentId = "";

    String currentDate = "";
    String month, year;
    String serverTime = "";

    boolean forSchedules = true;
    String scheduleSelectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    JSONArray schedulesArray = new JSONArray();
    List<ScheduleObj> scheduleEvents = new ArrayList<>();

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();
    List<StudentOnlineTestObj> listTest = new ArrayList<>();

    List<StudentOnlineTests> studentOnlineActiveTests = new ArrayList<>();
    List<StudentOnlineTestObj> listActiveTest = new ArrayList<>();

    List<DateObj> listDates = new ArrayList<>();
    List<EventsAndHoliday> eventList = new ArrayList<>();

    int count = 1;
    int selectedDay = 0;

    CalendarAdapter calendarAdapter;

    @BindView(R.id.tv_fees)
    TextView tvFees;

    @BindView(R.id.tv_due)
    TextView tvDue;

    @BindView(R.id.ll_fees)
    LinearLayout llFees;

    @BindView(R.id.ll_due_fee)
    LinearLayout llDueFee;

    int dueFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);
        ButterKnife.bind(this);

        init();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                isNetworkAvail = false;
                onNetworkConnectionChanged(NetworkConnectivity.isConnected(ParentHome.this));

            }
        });
    }

    void init() {

        navView.setItemIconTintList(null);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        month = new SimpleDateFormat("MM").format(new Date());
        year = new SimpleDateFormat("yyyy").format(new Date());


        selectedDay = Calendar.getInstance().get(Calendar.DATE);


        tvDate.setText(new SimpleDateFormat("dd MMMM, yyyy").format(new Date()));
        tvMonthName.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));


        tvName.setText(sObj.getStudentName().split(" ")[0]);

        View headerView = navView.getHeaderView(0);

        CircleImageView ivHeader = headerView.findViewById(R.id.img_profile);
        Picasso.with(ParentHome.this).load(sObj.getProfilePic()).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
        LinearLayout llProfile = headerView.findViewById(R.id.ll_profile);
        ((TextView) llProfile.findViewById(R.id.tv_name)).setText(sObj.getStudentName());
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, ProfileActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.nav_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the navigation drawer is not open then open it, if its already open then close it.
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(GravityCompat.START);
                else navDrawer.closeDrawer(GravityCompat.END);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_my_attendance:
                        startActivity(new Intent(ParentHome.this, StudentAttendance.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_leaves:
                        Intent intent = new Intent(ParentHome.this, StudentLeaveRequest.class);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("studentName", sObj.getStudentName());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_schedules:
//                    case R.id.navigation_calendarevents:
                        startActivity(new Intent(ParentHome.this, ScheduleAndCalendar.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_upcomingtest:
                        startActivity(new Intent(ParentHome.this, StudentLiveExamsTabs.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_performance:
                        startActivity(new Intent(ParentHome.this, ReportActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_circulars:
                        startActivity(new Intent(ParentHome.this, Circulars.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_switchchild:
                        startActivity(new Intent(ParentHome.this, ParentStudentList.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_terms_conditions:
                        Toast.makeText(ParentHome.this, "Terms and Conditions", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_log_out:
                        MyUtils.userLogOut(toEdit, ParentHome.this, sh_Pref);
                        break;
                }
                navDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, UserMessages.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });

        findViewById(R.id.tv_month_name).setOnClickListener(new View.OnClickListener() {
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

                        ParentHome.this.month = month + "";
                        ParentHome.this.year = year + "";

                        tvMonthName.setText(mon + " " + year);
                        tvDate.setText(1 + " " + mon + ", " + year);
                        selectedDay = 1;
                        calendarWork((month - 1), year);
                        scheduleSelectedDate = year + "-" + month + "-" + selectedDay;
                        forSchedules = true;
                        schedulesArray = new JSONArray();
                        getServerTime();
                    }
                });

                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

        calendarWork(Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR));

        tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvViewMore.getText().toString().equalsIgnoreCase("View More")) {
                    tvViewMore.setText("View Less");
                    tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_up_arrow, 0, R.drawable.ic_double_up_arrow, 0);
                    count = scheduleEvents.size();
                    final LayoutAnimationController controller =
                            AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
                    rvEvents.setLayoutManager(new LinearLayoutManager(ParentHome.this));
                    rvEvents.getAdapter().notifyDataSetChanged();
                    rvEvents.scheduleLayoutAnimation();
                } else {
                    tvViewMore.setText("View More");
                    tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_down_arrow, 0, R.drawable.ic_double_down_arrow, 0);
                    count = 1;
                    final LayoutAnimationController controller =
                            AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
                    rvEvents.setLayoutManager(new LinearLayoutManager(ParentHome.this));
                    rvEvents.getAdapter().notifyDataSetChanged();
                    rvEvents.scheduleLayoutAnimation();
                }
            }
        });

        findViewById(R.id.tv_view_all_assignments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, Assignments.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_view_all_uocoming_tests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, StudentLiveExamsTabs.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.ll_performance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, ReportActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.ll_attendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, StudentAttendance.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.cv_events).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, ScheduleAndCalendar.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.cv_circulars).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentHome.this, Circulars.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.cv_leavereq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ParentHome.this, StudentLeaveRequest.class);
                intent.putExtra("studentId", studentId);
                intent.putExtra("studentName", sObj.getStudentName());
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        llFees.setOnClickListener(v -> {
            startActivity(new Intent(ParentHome.this, FeeCategoriesActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        llDueFee.setOnClickListener(v -> {
            if (dueFee == 0) {
                Toast.makeText(this, "No Dues Pending", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(ParentHome.this, FeeDueDetailsActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
//        utils.showLoader(ParentHome.this);
//        getHomeWorks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    void calendarWork(int month, int year) {

        listDates.clear();

        int iDay = 1;

        Calendar mycal = Calendar.getInstance();
        mycal.set(year, month, iDay);

        utils.showLog("tag", mycal.get(Calendar.DATE) + " " + mycal.get(Calendar.MONTH) + " " + mycal.get(Calendar.YEAR));

        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, i);
            String day = "";
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    day = "S";
                    break;
                case 2:
                    day = "M";
                    break;
                case 3:
                    day = "T";
                    break;
                case 4:
                    day = "W";
                    break;
                case 5:
                    day = "Th";
                    break;
                case 6:
                    day = "F";
                    break;
                case 7:
                    day = "Sa";
                    break;
            }

            utils.showLog("tag", "date " + i + " day " + day);

            List<Events> events = new ArrayList<>();
            String d = "";

            if (i < 10) {
                d = "0" + i;
            } else {
                d = i + "";
            }

            if (d.equalsIgnoreCase(new SimpleDateFormat("dd").format(new Date()))) {
                events.add(new Events("09:00 am", "Mathematics Live Class", "Complete chapter 4", 60, "Live"));
                events.add(new Events("11:00 am", "Science Practice Test", "Complete chapter 4", 60, "Test"));
                events.add(new Events("12:00 am", "K-Hub Course Completion", "Complete chapter 4", 60, "K-Hub"));
                events.add(new Events("02:00 pm", "K-Hub Course Completion", "Complete chapter 4", 60, "Live"));
//                events.add(new Events("09:00 am", "Mathematics Live Class", "Complete chapter 4", 60, "Live"));
//                events.add(new Events("11:00 am", "Science Practice Test", "Complete chapter 4", 60, "Test"));
//                events.add(new Events("12:00 am", "K-Hub Course Completion", "Complete chapter 4", 60, "K-Hub"));
//                events.add(new Events("02:00 pm", "Science Live Class", "Complete chapter 4", 60, "Live"));

            }

            DateObj dateObj = new DateObj(day, i + "", events);
            listDates.add(dateObj);
        }

        LinearLayoutManager manager = new LinearLayoutManager(ParentHome.this, RecyclerView.HORIZONTAL, false);
        rvCal.setLayoutManager(manager);
        calendarAdapter = new CalendarAdapter(listDates);
        rvCal.setAdapter(calendarAdapter);
        manager.scrollToPosition((selectedDay - 1));


    }


    void getHomeWorks() {

        String currentMonth = new SimpleDateFormat("MM").format(new Date());
        String currentYear = new SimpleDateFormat("yyyy").format(new Date());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentHomeWorks + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&monthId=" + currentMonth + "&yearId=" + currentYear)
                .headers(MyUtils.addHeaders(sh_Pref))
//                .url("http://13.232.73.168:9000/getStudentHomeWorks?schemaName=nar666&studentId=65537&monthId=09&yearId=2020")
                .build();

        utils.showLog(TAG, "url -" + AppUrls.GetStudentHomeWorks + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&monthId=" + currentMonth + "&yearId=" + currentYear);


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getServerTime();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (response.body() != null) {

                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);

                        JSONObject parentjObject = new JSONObject(resp);

                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            if (parentjObject.has("studentHomeWorkDetails")) {
                                JSONArray jsonArr = parentjObject.getJSONArray("studentHomeWorkDetails");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<HomeWork>>() {
                                }.getType();

                                hwList.clear();
                                hwList = gson.fromJson(jsonArr.toString(), type);
                                utils.showLog(TAG, "hwListSize - " + hwList.size());

                                List<HomeWorkDetails> listHwPending = new ArrayList<>();
                                List<String> typeHw = new ArrayList<>();

                                for (int i = 0; i < hwList.size(); i++) {
                                    List<HomeWorkDetail> listHw = new ArrayList<>();
                                    listHw.addAll(hwList.get(i).getHomeWorkDetails());
                                    for (int j = 0; j < listHw.size(); j++) {
                                        List<HomeWorkDetails> listDetails = new ArrayList<>();
                                        listDetails.addAll(listHw.get(j).getHomeWorkDetail());
                                        for (int k = 0; k < listDetails.size(); k++) {
                                            if (listDetails.get(k).getHwStatus().equalsIgnoreCase("Completed")) {
//                                                listAssignmentsCompleted.add(listDetails.get(k));
                                            } else {
                                                listHwPending.add(listDetails.get(k));
                                                typeHw.add(hwList.get(i).getHomeWorkDesc());
                                            }
                                        }
                                    }
                                }

                                if (listHwPending.size() > 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvHw.setVisibility(View.VISIBLE);
                                            rvHw.setLayoutManager(new LinearLayoutManager(ParentHome.this, RecyclerView.HORIZONTAL, false));
                                            rvHw.setAdapter(new HwAdapter(listHwPending, typeHw));
                                        }
                                    });

                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvHw.setVisibility(View.GONE);
                                            findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)) { //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, ParentHome.this, message, sh_Pref);
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvHw.setVisibility(View.GONE);
                                    findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvHw.setVisibility(View.GONE);
                                findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                getServerTime();

            }
        });
    }

    private void getServerTime() {

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
                getUpcomingExams();
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
                            getUpcomingExams();
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)) { //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, ParentHome.this, message, sh_Pref);
                            });
                        } else {
                            getUpcomingExams();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getUpcomingExams();
                }
            }
        });
    }

    void getUpcomingExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = null;

        get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate=" + scheduleSelectedDate)
                .build();
        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate=" + scheduleSelectedDate);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getLiveClasses();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    getLiveClasses();
                } else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");

                            if (forSchedules) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    JSONArray newJar = object.getJSONArray("Tests");
                                    for (int j = 0; j < newJar.length(); j++) {
                                        schedulesArray.put(newJar.getJSONObject(j));
                                    }
                                }
                            }

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentOnlineTests>>() {
                            }.getType();

                            studentOnlineTests.clear();
                            studentOnlineTests.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "studentOnlineTests size- " + studentOnlineTests.size());


                            if (studentOnlineTests.size() > 0) {
                                Collections.reverse(studentOnlineTests);
                                listTest.clear();
                                for (int i = 0; i < studentOnlineTests.size(); i++) {
                                    listTest.addAll(studentOnlineTests.get(i).getTests());
                                }

                                List<StudentOnlineTestObj> listUpcoming = new ArrayList<>();

                                for (int i = 0; i < listTest.size(); i++) {
                                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestEndDate()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                        listUpcoming.add(listTest.get(i));
                                    }
                                }

                                long diff = 0;
                                int pos = -1;

//                                for (int i=0;i<listUpcoming.size();i++){
//                                    Date difference = getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(i).getTestEndDate()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime));
//                                    long x = difference.getTime();
//                                    if (i==0){
//                                        diff = x;
//                                        pos = 0;
//                                    }else {
//                                        if (x<diff){
//                                            diff=x;
//                                            pos = i;
//                                        }
//                                    }
//                                }
//
//                                if (pos!=-1){
//                                    liveExam = listUpcoming.get(pos);
//                                }


//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        if (liveExam != null) {
//                                            viewStudentHomeBottomFrag.findViewById(R.id.ll_live).setVisibility(View.VISIBLE);
//                                            tvTestName.setText(liveExam.getTestName());
//                                            tvDuration.setText(" ("+liveExam.getTestDuration()+"mins)");
//                                            tvTestName.setVisibility(View.VISIBLE);
//                                            tvDuration.setVisibility(View.VISIBLE);
//                                            tvTodayDate.setVisibility(View.VISIBLE);
//                                            tvNotifyExam.setVisibility(View.GONE);
//
//                                            try {
//                                                tvTodayDate.setText(new SimpleDateFormat("dd MMM yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestStartDate())));
//
//                                                if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestStartDate()).before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))){
//                                                    tvStartInExam.setVisibility(View.VISIBLE);
//                                                    tvStartInExam.setText(" Start Exam ");
//                                                    tvStartInExam.setBackgroundResource(R.drawable.bg_grad_tab_select);
//                                                    tvStartInExam.setTextColor(Color.WHITE);
//                                                }else{
//                                                    Date d = getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestStartDate()),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime));
//                                                    tvStartInExam.setText("Starts in "+(d.getTime() / 60000)+" mins");
//                                                    tvStartInExam.setVisibility(View.VISIBLE);
//                                                    tvStartInExam.setTextColor(Color.BLACK);
//                                                }
//                                            } catch (ParseException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            viewStudentHomeBottomFrag.findViewById(R.id.cv_start_exam).setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//
//                                                    try {
//                                                        if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestStartDate()).before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))){
//                                                            Intent intent = new Intent(mActivity, LiveExamDetails.class);
//                                                            intent.putExtra("live", (Serializable) liveExam);
//                                                            mActivity.startActivity(intent);
//                                                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                                        }else{
//                                                            Intent intent = new Intent(mActivity, StudentLiveExams.class);
//                                                            mActivity.startActivity(intent);
//                                                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                                        }
//                                                    } catch (ParseException e) {
//                                                        e.printStackTrace();
//                                                    }
//
//
//                                                }
//                                            });
//
//
//                                        }
//                                        else {
//                                            mActivity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    viewStudentHomeBottomFrag.findViewById(R.id.cv_start_exam).setAlpha(0.7f);
//                                                    tvTestName.setVisibility(View.VISIBLE);
//                                                    tvTestName.setText("No Exams Available");
//                                                    tvStartInExam.setVisibility(View.INVISIBLE);
//                                                    tvDuration.setVisibility(View.INVISIBLE);
//                                                    tvTodayDate.setVisibility(View.INVISIBLE);
//                                                    tvNotifyExam.setVisibility(View.VISIBLE);
//
//                                                    viewStudentHomeBottomFrag.findViewById(R.id.cv_start_exam).setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            Intent intent = new Intent(mActivity, StudentLiveExams.class);
//                                                            intent.putExtra("live", (Serializable) liveExam);
//                                                            mActivity.startActivity(intent);
//                                                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//
//
//                                                        }
//                                                    });
//                                                }
//                                            });
//                                        }
//
//
//                                    }
//                                });
                            }


                        } else {
//                            mActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    viewStudentHomeBottomFrag.findViewById(R.id.cv_start_exam).setAlpha(0.7f);
//                                    tvTestName.setVisibility(View.VISIBLE);
//                                    tvTestName.setText("No Exams Available");
//                                    tvStartInExam.setVisibility(View.INVISIBLE);
//                                    tvDuration.setVisibility(View.INVISIBLE);
//                                    tvTodayDate.setVisibility(View.INVISIBLE);
//                                    tvNotifyExam.setVisibility(View.VISIBLE);
//
//                                    viewStudentHomeBottomFrag.findViewById(R.id.cv_start_exam).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            Intent intent = new Intent(mActivity, StudentLiveExams.class);
//                                            intent.putExtra("live", (Serializable) liveExam);
//                                            mActivity.startActivity(intent);
//                                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//
//
//                                        }
//                                    });
//                                }
//                            });
                        }
                    } catch (Exception e) {

                    }
                }


                getLiveClasses();
            }
        });
    }

    void getLiveClasses() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = null;

        get = new Request.Builder()
                .url(AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&sectionId=" + sObj.getClassCourseSectionId() + "&status=active&filterDate=" + scheduleSelectedDate)
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&sectionId=" + sObj.getClassCourseSectionId() + "&status=active&filterDate=" + scheduleSelectedDate);
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getStudentOverallAttendance();
                        createSchedulesList();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getStudentOverallAttendance();
                                createSchedulesList();
                            }
                        });

//                        getOverAllAttendance();
                    } else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);


                        JSONObject ParentjObject = new JSONObject(resp);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<LiveVideoInfo>>() {
                        }.getType();

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("liveVideoInfo");


                            if (forSchedules) {
                                for (int i = 0; i < jsonArr.length(); i++) {
                                    JSONObject object = jsonArr.getJSONObject(i);
                                    JSONArray newJar = object.getJSONArray("LiveVideos");
                                    for (int j = 0; j < newJar.length(); j++) {
                                        schedulesArray.put(newJar.getJSONObject(j));
                                    }
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createSchedulesList();
                                }
                            });

                            utils.showLog(TAG, "schedules size " + schedulesArray.length());
                            utils.showLog(TAG, "schedules val " + schedulesArray.toString());

                            List<LiveVideoInfo> listLiveClassesInfo = new ArrayList<>();
                            List<LiveVideo> listLiveClasses = new ArrayList<>();
                            listLiveClassesInfo.clear();
                            listLiveClassesInfo.addAll(gson.fromJson(jsonArr.toString(), type));

                            listLiveClasses.clear();

                            for (int i = 0; i < listLiveClassesInfo.size(); i++) {
                                listLiveClasses.addAll(listLiveClassesInfo.get(i).getLiveVideos());
                            }

                            for (int i = 0; i < listLiveClasses.size(); i++) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date d = null;
                                try {
                                    d = df.parse(listLiveClasses.get(i).getLiveStreamStartTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar gc = new GregorianCalendar();
                                gc.setTime(d);
                                gc.add(Calendar.MINUTE, Integer.parseInt(listLiveClasses.get(i).getDuration()));
                                Date d2 = gc.getTime();
                                listLiveClasses.get(i).setEndTime(df.format(d2));
                                utils.showLog(TAG, "End time " + listLiveClasses.get(i).getEndTime());

                            }


                            List<LiveVideo> listUpcoming = new ArrayList<>();

                            for (int i = 0; i < listLiveClasses.size(); i++) {
                                if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listLiveClasses.get(i).getEndTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                    listUpcoming.add(listLiveClasses.get(i));
                                }
                            }


                            long diff = 0;
                            int pos = -1;

//                            for (int i=0;i<listUpcoming.size();i++){
//                                Date difference = getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(i).getEndTime()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime));
//                                long x = difference.getTime();
//                                if (i==0){
//                                    diff = x;
//                                    pos = 0;
//                                }else {
//                                    if (x<diff){
//                                        diff=x;
//                                        pos = i;
//                                    }
//                                }
//                            }
//
//                            if (pos!=-1){
//                                liveClass = listUpcoming.get(pos);
//                            }


//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (liveClass != null) {
//                                        viewStudentHomeBottomFrag.findViewById(R.id.ll_classes).setVisibility(View.VISIBLE);
//                                        tvSubName.setVisibility(View.VISIBLE);
//                                        tvVidName.setVisibility(View.VISIBLE);
//                                        tvDurationClass.setVisibility(View.VISIBLE);
//                                        tvVidName.setText(liveClass.getLiveStreamName());
//                                        tvNotifyVid.setVisibility(View.GONE);
//                                        try {
//                                            if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getLiveStreamStartTime()).before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))){
//                                                tvStartIn.setVisibility(View.INVISIBLE);
//                                            }else{
//                                                Date d = getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getLiveStreamStartTime()),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime));
//                                                tvStartIn.setText("Starts in "+(d.getTime() / 60000)+" mins");
//                                                tvStartIn.setVisibility(View.VISIBLE);
//                                            }
//                                        } catch (ParseException e) {
//                                            e.printStackTrace();
//                                        }
//                                        tvDurationClass.setText(" ("+liveClass.getDuration() + " mins)");
//                                        //                                        try {
//                                        //                                            tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getEndTime())));
//                                        //                                        } catch (ParseException e) {
//                                        //                                            e.printStackTrace();
//                                        //                                        }
//                                        viewStudentHomeBottomFrag.findViewById(R.id.cv_live).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                try {
//                                                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getLiveStreamStartTime()).before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))){
//                                                        Intent intent = new Intent(mActivity, LiveDetails.class);
//                                                        intent.putExtra("live", (Serializable) liveClass);
//                                                        mActivity.startActivity(intent);
//                                                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                                    }else{
//                                                        Intent intent = new Intent(mActivity, StudentLiveClasses.class);
//                                                        mActivity.startActivity(intent);
//                                                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                                    }
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        viewStudentHomeBottomFrag.findViewById(R.id.cv_live).setAlpha(0.7f);
//                                        tvSubName.setVisibility(View.INVISIBLE);
//                                        tvVidName.setVisibility(View.VISIBLE);
//                                        tvVidName.setText("No Classes Available");
//                                        tvDurationClass.setVisibility(View.INVISIBLE);
//                                        tvStartIn.setVisibility(View.INVISIBLE);
//                                        tvNotifyVid.setVisibility(View.VISIBLE);
//
//                                        //
//                                        viewStudentHomeBottomFrag.findViewById(R.id.cv_live).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                Intent intent = new Intent(mActivity, StudentLiveClasses.class);
//                                                //                                                intent.putExtra("live", (Serializable) liveClass);
//                                                mActivity.startActivity(intent);
//                                                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                            }
//                                        });
//                                        //                                        viewStudentHomeBottomFrag.findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
//                                    }
//
//                                }
//                            });


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createSchedulesList();
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    findViewById(R.id.cv_live).setAlpha(0.7f);
//                                    tvSubName.setVisibility(View.INVISIBLE);
//                                    tvVidName.setVisibility(View.VISIBLE);
//                                    tvVidName.setText("No Classes Available");
//                                    tvDurationClass.setVisibility(View.INVISIBLE);
//                                    tvStartIn.setVisibility(View.INVISIBLE);
//                                    tvNotifyVid.setVisibility(View.VISIBLE);

                                    //
//                                    viewStudentHomeBottomFrag.findViewById(R.id.cv_live).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            Intent intent = new Intent(mActivity, StudentLiveClasses.class);
//                                            //                                                intent.putExtra("live", (Serializable) liveClass);
//                                            mActivity.startActivity(intent);
//                                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                        }
//                                    });
//                                    viewStudentHomeBottomFrag.findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(() -> {
                    getStudentOverallAttendance();
                });


            }
        });

    }

    void getStudentOverallAttendance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetStudentOverallAttendance + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId());

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentOverallAttendance + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActiveTests();
                        tvAttendance.setText("0 %");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActiveTests();
                                tvAttendance.setText("0 %");
                            }
                        });

                    } else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);


                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            int stdnAtteendance = ParentjObject.getInt("attendanceTakenDays") - ParentjObject.getInt("studentLeaves");

                            Float totalPercentage = (float) (stdnAtteendance * 100) / (float) ParentjObject.getInt("attendanceTakenDays");

                            runOnUiThread(() -> {
                                if (totalPercentage.isNaN()) tvAttendance.setText("0 %");
                                else {
                                    String s = String.format("%.2f", totalPercentage);
                                    tvAttendance.setText("" + s + " %");
                                }
                            });
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(() -> {
                    getActiveTests();
                });
            }
        });
    }

    void getActiveTests() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active");

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active")
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                    swipeLayout.setRefreshing(false);
                    cvNoLiveExam.setVisibility(View.VISIBLE);
                    rvUpcomingTests.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        utils.dismissDialog();
                        swipeLayout.setRefreshing(false);
                        cvNoLiveExam.setVisibility(View.VISIBLE);
                        rvUpcomingTests.setVisibility(View.GONE);
                    });

                } else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentOnlineTests>>() {
                            }.getType();

                            studentOnlineActiveTests.clear();
                            studentOnlineActiveTests.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "studentOnlineActiveTests size- " + studentOnlineActiveTests.size());

                            if (studentOnlineActiveTests.size() > 0) {
                                Collections.reverse(studentOnlineActiveTests);
                                listActiveTest.clear();
                                for (int i = 0; i < studentOnlineActiveTests.size(); i++) {
                                    listActiveTest.addAll(studentOnlineActiveTests.get(i).getTests());
                                }
                                runOnUiThread(() -> setUpcomingTests());
                            }
                        } else {
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                swipeLayout.setRefreshing(false);
                                cvNoLiveExam.setVisibility(View.VISIBLE);
                                rvUpcomingTests.setVisibility(View.GONE);
                            });

                        }
                    } catch (Exception e) {

                        utils.showLog(TAG, "error " + e.getMessage());
                    }
                }
                runOnUiThread(() -> {
                    utils.dismissDialog();
                    swipeLayout.setRefreshing(false);
                });
            }
        });
    }

    private void setUpcomingTests() {

        if (listActiveTest.size() < 3) {
            if (listActiveTest.size() == 0) {
                cvNoLiveExam.setVisibility(View.VISIBLE);
            } else {
                rvUpcomingTests.setLayoutManager(new LinearLayoutManager(ParentHome.this, RecyclerView.HORIZONTAL, false));
                rvUpcomingTests.setAdapter(new UpcomingTestAdapter(listActiveTest));
            }
        } else {
            List<StudentOnlineTestObj> tests = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                tests.add(listActiveTest.get(i));
            }
            rvUpcomingTests.setLayoutManager(new LinearLayoutManager(ParentHome.this, RecyclerView.HORIZONTAL, false));
            rvUpcomingTests.setAdapter(new UpcomingTestAdapter(tests));
        }


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, ParentHome.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
//            calendarWork(Integer.parseInt(month),Integer.parseInt(year));
                utils.showLoader(ParentHome.this);
                getHomeWorks();
                getOverAllFee();
                getDueFee();
            }
            isNetworkAvail = true;
        }
    }


    class HwAdapter extends RecyclerView.Adapter<HwAdapter.ViewHolder> {

        List<HomeWorkDetails> listHwPending;
        List<String> typeHw;

        public HwAdapter(List<HomeWorkDetails> listHwPending, List<String> typeHw) {
            this.listHwPending = listHwPending;
            this.typeHw = typeHw;
        }

        @NonNull
        @Override
        public HwAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HwAdapter.ViewHolder(LayoutInflater.from(ParentHome.this).inflate(R.layout.item_dashboard_homework, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HwAdapter.ViewHolder holder, int position) {

            holder.tvSubType.setText(listHwPending.get(position).getSubjectName() + " | " + typeHw.get(position));
            holder.tvTitle.setText(listHwPending.get(position).getHomeDetails());
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listHwPending.get(position).getSubmissionDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ParentHome.this, AssignmentDisplayNew.class);
                    intent.putExtra("hwObj", (Serializable) listHwPending.get(position));
                    intent.putExtra("type", typeHw.get(position));
                    intent.putExtra("hwId", listHwPending.get(position).getHomeworkId() + "");
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listHwPending.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubType, tvTitle, tvDate, tvTime;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSubType = itemView.findViewById(R.id.tv_sub_type);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvTime = itemView.findViewById(R.id.tv_time);

            }
        }
    }

    void createSchedulesList() {

        forSchedules = false;

        scheduleEvents.clear();

        for (int i = 0; i < schedulesArray.length(); i++) {
            try {

                JSONObject obj = schedulesArray.getJSONObject(i);

                if (obj.has("testName")) {
                    scheduleEvents.add(new ScheduleObj(obj.getString("testStartDate"), obj.getString("testName"), obj.getString("testCategoryName"), obj.getString("testDuration"), "test"));
                } else {
                    scheduleEvents.add(new ScheduleObj(obj.getString("liveStreamStartTime"), obj.getString("liveStreamName"), obj.getString("facultyName"), obj.getString("duration"), "live"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (scheduleEvents.size() > 1) {
            tvViewMore.setVisibility(View.VISIBLE);
        } else {
            tvViewMore.setVisibility(View.GONE);
        }

        if (scheduleEvents.size() > 0) {
            rvEvents.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
            rvEvents.setLayoutManager(new LinearLayoutManager(ParentHome.this));
            count = 1;

            Collections.sort(scheduleEvents, new Comparator<ScheduleObj>() {
                @Override
                public int compare(ScheduleObj o1, ScheduleObj o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
            rvEvents.setAdapter(new EventAdapter(scheduleEvents));
            rvEvents.scheduleLayoutAnimation();
            tvViewMore.setText("View More");
            tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_down_arrow, 0, R.drawable.ic_double_down_arrow, 0);
        } else {
            tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_down_arrow, 0, R.drawable.ic_double_down_arrow, 0);
            tvViewMore.setText("View More");
            rvEvents.setVisibility(View.GONE);
            findViewById(R.id.iv_no_schedules).setVisibility(View.VISIBLE);
        }
    }

    class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

        List<ScheduleObj> listEvent;

        EventAdapter(List<ScheduleObj> listEvent) {
            this.listEvent = listEvent;
        }


        @NonNull
        @Override
        public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EventAdapter.ViewHolder(LayoutInflater.from(ParentHome.this).inflate(R.layout.item_schedule_60, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {


            holder.tvDuration.setText(listEvent.get(position).getDuration() + " mins");
            holder.tvTitle.setText(listEvent.get(position).getEventTitle());
            holder.tvDesc.setText(listEvent.get(position).getEventDesc());
            try {
                holder.tvTime.setText(new SimpleDateFormat("hh:mm\na").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listEvent.get(position).getTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (listEvent.get(position).getType()) {
                case "live":
                    holder.flBackground.setBackgroundResource(R.drawable.bg_red_gradient);
                    holder.llMain.setBackgroundResource(R.drawable.bg_red_mask);
                    holder.tvDesc.setText("By - " + listEvent.get(position).getEventDesc());

                    break;
                case "K-Hub":
                    holder.flBackground.setBackgroundResource(R.drawable.bg_blue_gradient);
                    holder.llMain.setBackgroundResource(R.drawable.bg_blue_mask);
                    break;
                case "test":
                    holder.flBackground.setBackgroundResource(R.drawable.bg_green_gradient);
                    holder.llMain.setBackgroundResource(R.drawable.bg_green_mask);
                    break;
            }

            if ((position + 1) == listEvent.size()) {
                rvEvents.getLayoutManager().scrollToPosition(position);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (listEvent.get(position).getType()) {
                        case "live":
                            startActivity(new Intent(ParentHome.this, StudentLiveClassesTabs.class));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "test":
                            startActivity(new Intent(ParentHome.this, StudentLiveExamsTabs.class));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return count;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTime, tvTitle, tvDesc, tvDuration;
            LinearLayout llMain;
            FrameLayout flBackground;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDesc = itemView.findViewById(R.id.tv_desc);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                llMain = itemView.findViewById(R.id.ll_main);
                flBackground = itemView.findViewById(R.id.fl_background);
            }
        }
    }


    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
        List<DateObj> listDate;

        public CalendarAdapter(List<DateObj> listDate) {
            this.listDate = listDate;
        }

        @NonNull
        @Override
        public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CalendarAdapter.ViewHolder(LayoutInflater.from(ParentHome.this).inflate(R.layout.item_calendar, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            holder.tvDate.setText(listDate.get(position).getDate());
            holder.tvDay.setText(listDate.get(position).getDay());

            if ((position + 1) == selectedDay) {
                holder.tvDate.setTextColor(Color.WHITE);
                holder.tvDate.setAlpha(1);
                holder.tvDate.setBackgroundResource(R.drawable.bg_date_selected);
            } else {
                holder.tvDate.setTextColor(Color.rgb(73, 73, 73));
                holder.tvDate.setAlpha(0.5f);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rvCal.getLayoutManager().scrollToPosition(position);
                    selectedDay = Integer.parseInt(listDate.get(position).getDate());
                    notifyDataSetChanged();
                    tvDate.setText(selectedDay + " " + tvMonthName.getText().toString().split(" ")[0] + ", " + tvMonthName.getText().toString().split(" ")[1]);

                    scheduleSelectedDate = year + "-" + month + "-" + selectedDay;
                    forSchedules = true;
                    schedulesArray = new JSONArray();
                    getServerTime();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listDate.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate, tvDay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvDate = itemView.findViewById(R.id.tv_date);
                tvDay = itemView.findViewById(R.id.tv_day);
            }
        }
    }


    class UpcomingTestAdapter extends RecyclerView.Adapter<UpcomingTestAdapter.ViewHolder> {

        List<StudentOnlineTestObj> testsObjs;

        UpcomingTestAdapter(List<StudentOnlineTestObj> tests) {
            testsObjs = tests;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public UpcomingTestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UpcomingTestAdapter.ViewHolder(LayoutInflater.from(ParentHome.this).inflate(R.layout.item_parent_upcoming_tests, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UpcomingTestAdapter.ViewHolder holder, int position) {
            holder.tvTestName.setText(testsObjs.get(position).getTestName());
            holder.tvTestType.setText(testsObjs.get(position).getTestCategoryName());
            holder.tvDuration.setText("(" + testsObjs.get(position).getTestDuration() + " Mins)");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ParentHome.this, LiveExamDetails.class);
                    intent.putExtra("live", (Serializable) testsObjs.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
            });
        }

        @Override
        public int getItemCount() {
            return testsObjs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTestType, tvDuration, tvTestName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTestType = itemView.findViewById(R.id.tv_test_type);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvDuration = itemView.findViewById(R.id.tv_duration);

            }
        }
    }

    void getOverAllFee() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetStudentTotalFee + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId());

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentTotalFee + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvFees.setText(utils.addRupeeSymbol(0));
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvFees.setText(utils.addRupeeSymbol(0));
                            }
                        });

                    } else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);


                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            int totalFee = ParentjObject.getInt("totalFee");

                            runOnUiThread(() -> {
                                if (totalFee == 0) tvAttendance.setText(utils.addRupeeSymbol(0));
                                else {
                                    tvFees.setText(utils.addRupeeSymbol(totalFee));
                                }
                            });
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getDueFee() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetStudentDueFee + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId());

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentDueFee + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDue.setText(utils.addRupeeSymbol(0));
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvDue.setText(utils.addRupeeSymbol(0));
                            }
                        });

                    } else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);


                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            dueFee = ParentjObject.getInt("totalDueFee");

                            runOnUiThread(() -> {
                                if (dueFee == 0) tvDue.setText(utils.addRupeeSymbol(0));
                                else {
                                    tvDue.setText(utils.addRupeeSymbol(dueFee));
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                tvDue.setText(utils.addRupeeSymbol(0));
                            });
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}