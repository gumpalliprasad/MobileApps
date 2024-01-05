package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.DateObj;
import myschoolapp.com.gsnedutech.Models.Events;
import myschoolapp.com.gsnedutech.Models.EventsAndHoliday;
import myschoolapp.com.gsnedutech.Models.ScheduleObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSClass;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSection;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherLiveClassObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Models.TeacherTestsObj;
import myschoolapp.com.gsnedutech.Models.ToDosObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TeacherHome extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherHome.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    MyUtils utils = new MyUtils();

    @BindView(R.id.nav_view)
    NavigationView navView;

    @BindView(R.id.drawer_layout)
    DrawerLayout navDrawer;

    @BindView(R.id.tv_name)
    TextView tvName;



    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    List<ScheduleObj> scheduleEvents = new ArrayList<>();


    String reqDate = "";

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
    @BindView(R.id.rv_todos)
    RecyclerView rvToDos;

    @BindView(R.id.ll_todolist)
    LinearLayout llTodos;
    @BindView(R.id.rv_courses)
    RecyclerView rvCourses;
    @BindView(R.id.rv_upcoming_test)
    RecyclerView rvUpcomingTests;
    @BindView(R.id.sp_courses)
    Spinner spCourses;

    @BindView(R.id.cv_no_live_classes)
    CardView cvNoLiveClasses;

    @BindView(R.id.cv_no_live_exams)
    CardView cvNoLiveExams;

    @BindView(R.id.rv_upcoming_classes)
    RecyclerView rvUpcomingClasses;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    List<TeacherTestsObj> allUpcomingTests = new ArrayList<>();
    List<TeacherLiveClassObj> videoSessionObjList = new ArrayList<>();

    List<TeacherLiveClassObj> classes = new ArrayList<>();
    List<TeacherLiveClassObj> completed = new ArrayList<>();

    String serverTime = "";

    List<CountDownTimer> listTimers = new ArrayList<>();
    List<ToDosObj> listTodos = new ArrayList<>();

    ToDoAdapter todoAdapter;


    JSONArray schedulesArray = new JSONArray();

    String month,year;

    int teacherId = 0;


    String currentDate = "";

    int count = 1;

    List<DateObj> listDates = new ArrayList<>();
    List<EventsAndHoliday> eventList = new ArrayList<>();


    CalendarAdapter calendarAdapter;
    int selectedDay = 0;

    List<TeacherCCSSObj> teacherList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        ButterKnife.bind(this);
        init();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                isNetworkAvail = false;
                onNetworkConnectionChanged(NetworkConnectivity.isConnected(TeacherHome.this));

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


    void init(){

        navView.setItemIconTintList(null);


        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();


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
                    case R.id.navigation_tests:
                        startActivity(new Intent(TeacherHome.this, TeacherLivePreviousExams.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_schedules:
                        startActivity(new Intent(TeacherHome.this, TeacherCalAndEvents.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_my_attendance:
                        startActivity(new Intent(TeacherHome.this, TeacherAttendance.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_leaves:
                        startActivity(new Intent(TeacherHome.this, TeacherLeaveRequest.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_todo:
                        startActivity(new Intent(TeacherHome.this, TeacherToDoActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_gallery:
                        startActivity(new Intent(TeacherHome.this, GalleryActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_circulars:
                        startActivity(new Intent(TeacherHome.this, Circulars.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_log_out:
                        MyUtils.userLogOut(toEdit, TeacherHome.this, sh_Pref);
                        break;
                }
                navDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, UserMessages.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });


        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        View headerView = navView.getHeaderView(0);

        tvName.setText("HEY," +tObj.getUserName().split(" ")[0]);

        ((TextView)headerView.findViewById(R.id.tv_name)).setText(tObj.getUserName());

        CircleImageView ivHeader =  headerView.findViewById(R.id.img_profile);
        Picasso.with(TeacherHome.this).load(new AppUrls().GetStaffProfilePic +tObj.getProfilePic()).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
        TextView tvClass = headerView.findViewById(R.id.tv_class);
        tvClass.setText(tObj.getRoleName());
        TextView tvId = headerView.findViewById(R.id.tv_id);
        tvId.setText(tObj.getBranchName());

        LinearLayout llProfile = headerView.findViewById(R.id.ll_profile);
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, TeacherProfileActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        teacherId = tObj.getUserId();

        reqDate =  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        month = (Integer.parseInt(new SimpleDateFormat("MM").format(new Date()))-1)+"";
        year = new SimpleDateFormat("yyyy").format(new Date());

//        tvAttDate.setText(new SimpleDateFormat("dd").format(new Date()));
//        tvAttMonthYear.setText(new SimpleDateFormat("MMM,yyyy").format(new Date()));


        selectedDay = Calendar.getInstance().get(Calendar.DATE);


        tvDate.setText(new SimpleDateFormat("dd MMMM, yyyy").format(new Date()));
        tvMonthName.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));



        findViewById(R.id.tv_view_all_assignments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, Assignments.class));
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

                        TeacherHome.this.month = month+"";
                        TeacherHome.this.year = year+"";

                        tvMonthName.setText(mon + " " + year);
                        tvDate.setText(1 + " " + mon + ", " + year);
                        selectedDay = 1;

                        schedulesArray = new JSONArray();
                        scheduleEvents.clear();
                        count = 1;
                        tvViewMore.setText("View More");

                        calendarWork((month-1), year);

                    }
                });

                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

//        calendarWork(Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR));

        tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvViewMore.getText().toString().equalsIgnoreCase("View More")) {
                    tvViewMore.setText("View Less");
                    tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_up_arrow,0,R.drawable.ic_double_up_arrow,0);
                    count = scheduleEvents.size();
                    calendarAdapter.notifyDataSetChanged();
                } else {
                    tvViewMore.setText("View More");
                    tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_down_arrow,0,R.drawable.ic_double_down_arrow,0);
                    count = 1;
                    calendarAdapter.notifyDataSetChanged();
                }
            }
        });

        findViewById(R.id.tv_analysis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, ReportActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, TeacherCalAndEvents.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_view_all_uocoming_tests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, TeacherLivePreviousExams.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_view_all_uocoming_classes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, TeacherLiveRecordedClasses.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_attendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, TeacherAttendance.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_view_all_todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHome.this, TeacherToDoActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
//        getTeacherCourses();



    }

    private void GetCredDetails(String schema) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetCredDetails + "id=1&schemaName=" + schema)
                //.headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "S3 Credentials request - " + new AppUrls().GetCredDetails);
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
                                JSONArray jsonArray = ParentjObject.getJSONArray("userClassSubjects");
                                JSONObject jObj = jsonArray.getJSONObject(0);
                                toEdit.putString("akey", jObj.getString("akey"));
                                toEdit.putString("skey", jObj.getString("skey"));
                                toEdit.putString("bucket", jObj.getString("name"));
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
            }else {
                d=i+"";
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

        LinearLayoutManager manager = new LinearLayoutManager(TeacherHome.this, RecyclerView.HORIZONTAL, false);
        rvCal.setLayoutManager(manager);
        calendarAdapter = new CalendarAdapter(listDates);
        rvCal.setAdapter(calendarAdapter);
        manager.scrollToPosition((selectedDay - 1));


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherHome.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                calendarWork(Integer.parseInt(month),Integer.parseInt(year));
                getTeacherCourses();
            }
            isNetworkAvail = true;
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
            return new CalendarAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_calendar, parent, false));
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
                schedulesArray = new JSONArray();
                String date = selectedDay + " " + tvMonthName.getText().toString().split(" ")[0] + ", " + tvMonthName.getText().toString().split(" ")[1];
                try {
                    reqDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd MMMM, yyyy").parse(date));
                    getLiveClassesForSchedules();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                utils.showLog(TAG,"date "+selectedDay + " " + tvMonthName.getText().toString().split(" ")[0] + ", " + tvMonthName.getText().toString().split(" ")[1]);
                holder.tvDate.setTextColor(Color.WHITE);
                holder.tvDate.setAlpha(1);
                holder.tvDate.setBackgroundResource(R.drawable.bg_date_selected);
                if (listDate.get(position).getListEvents().size() > 0) {
                    rvEvents.setVisibility(View.VISIBLE);
                } else {
                    rvEvents.setVisibility(View.GONE);
                    findViewById(R.id.iv_no_schedules).setVisibility(View.VISIBLE);
                }
            } else {
                holder.tvDate.setTextColor(Color.rgb(73, 73, 73));
                holder.tvDate.setAlpha(0.5f);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    schedulesArray = new JSONArray();
                    scheduleEvents.clear();
                    count = 1;
                    tvViewMore.setText("View More");
                    tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_down_arrow,0,R.drawable.ic_double_down_arrow,0);
                    rvCal.getLayoutManager().scrollToPosition(position);
                    selectedDay = Integer.parseInt(listDate.get(position).getDate());
                    notifyDataSetChanged();
                    tvDate.setText(selectedDay + " " + tvMonthName.getText().toString().split(" ")[0] + ", " + tvMonthName.getText().toString().split(" ")[1]);
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

    class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

        List<ScheduleObj> listEvent;

        EventAdapter(List<ScheduleObj> listEvent) {
            this.listEvent = listEvent;
        }



        @NonNull
        @Override
        public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EventAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_schedule_60, parent, false));
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
                    holder.tvDesc.setText(listEvent.get(position).getEventDesc());

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
                            startActivity(new Intent(TeacherHome.this, TeacherLiveRecordedClasses.class));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "test":
                            startActivity(new Intent(TeacherHome.this, TeacherLivePreviousExams.class));
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

    class CouseAdapter extends RecyclerView.Adapter<CouseAdapter.ViewHolder> {

        List<TeacherCCSSClass> teacherCObjs;

        public CouseAdapter(List<TeacherCCSSClass> teacherClasses) {
            teacherCObjs = teacherClasses;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public CouseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CouseAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_teacher_course_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CouseAdapter.ViewHolder holder, int position) {
            holder.tvClassName.setText(teacherCObjs.get(position).toString());
            holder.rvSections.setLayoutManager(new LinearLayoutManager(TeacherHome.this));
            holder.rvSections.setAdapter(new ClassSectionAdapter(teacherCObjs.get(position).getClassName(),
                    teacherCObjs.get(position).getClassId()
                    ,teacherCObjs.get(position).getSections()));
            holder.rvSections.addItemDecoration(new DividerItemDecoration(TeacherHome.this,DividerItemDecoration.VERTICAL));

        }

        @Override
        public int getItemCount() {
            return teacherCObjs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView rvSections;
            TextView tvClassName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                rvSections = itemView.findViewById(R.id.rv_sections);
                tvClassName = itemView.findViewById(R.id.tv_className);
            }
        }
    }

    void getTeacherCourses(){
        utils.showLoader(TeacherHome.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request get = new Request.Builder()
                .url(new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + tObj.getUserId())
                .build();
        utils.showLog(TAG, new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + tObj.getUserId());
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTodoList();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getTodoList();
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("userClassSubjects");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherCCSSObj>>() {
                            }.getType();
                            teacherList.clear();
                            teacherList.addAll(gson.fromJson(jsonArr.toString(), type));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<TeacherCCSSObj> adapter =
                                            new ArrayAdapter<>(TeacherHome.this, android.R.layout.simple_spinner_dropdown_item, teacherList);
                                    adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                                    spCourses.setAdapter(adapter);
                                    spCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            rvCourses.setLayoutManager(new LinearLayoutManager(TeacherHome.this));
                                            rvCourses.setAdapter(new CouseAdapter(teacherList.get(i).getClasses()));
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                            });

                            Log.v(TAG, "TeacherList Size " + teacherList.size());
                            if (teacherList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getTodoList();
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ClassSectionAdapter extends RecyclerView.Adapter<ClassSectionAdapter.ViewHolder> {

        List<TeacherCCSSSection> teacherCCSSSections;
        String className;
        String classId;

        public ClassSectionAdapter(String className, String classId, List<TeacherCCSSSection> sections) {
            teacherCCSSSections = sections;
            this.className = className;
            this.classId = classId;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public ClassSectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ClassSectionAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_teacher_csection_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ClassSectionAdapter.ViewHolder holder, int position) {
            holder.tvSection.setText(className + " - "+teacherCCSSSections.get(position).getSectionName());
            String subs = "";
            for (TeacherCCSSSubject tsub : teacherCCSSSections.get(position).getSubjects()){
                if (subs.isEmpty()){
                    subs = tsub.getSubjectName();
                }
                else {
                    subs = subs+","+tsub.getSubjectName();
                }
            }
            holder.tvSubjects.setText(subs);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherHome.this, TeacherClassSections.class);
                    intent.putExtra("courseName", teacherList.get(spCourses.getSelectedItemPosition()).getCourseName());
                    intent.putExtra("courseId", teacherList.get(spCourses.getSelectedItemPosition()).getCourseId());
                    intent.putExtra("className", className);
                    intent.putExtra("classId", classId);
                    intent.putExtra("teacherCCSSObj", teacherCCSSSections.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });


        }

        @Override
        public int getItemCount() {
            return teacherCCSSSections.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubjects, tvSection;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubjects = itemView.findViewById(R.id.tv_subjects);
                tvSection = itemView.findViewById(R.id.tv_section);
            }
        }
    }

    class UpcomingTestAdapter extends RecyclerView.Adapter<UpcomingTestAdapter.ViewHolder> {

        List<TeacherTestsObj> teacherTestsObjs;

        UpcomingTestAdapter(List<TeacherTestsObj> tests) {
            teacherTestsObjs = tests;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public UpcomingTestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UpcomingTestAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_teacher_home_upcoming_tests, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UpcomingTestAdapter.ViewHolder holder, int position) {
            holder.tvSub.setText(teacherTestsObjs.get(position).getTestName());
            holder.tvStartTime.setText(teacherTestsObjs.get(position).getTestStartDate());
        }

        @Override
        public int getItemCount() {
            return teacherTestsObjs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSub, tvStartTime;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSub = itemView.findViewById(R.id.tv_test_name);
                tvStartTime = itemView.findViewById(R.id.tv_test_date);

            }
        }
    }

    class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

        List<TeacherLiveClassObj> listUpcoming;

        public LiveAdapter(List<TeacherLiveClassObj> listUpcoming) {
            this.listUpcoming = listUpcoming;
            Collections.reverse(listUpcoming);
        }

        @NonNull
        @Override
        public LiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LiveAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_live_class, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull LiveAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvSubject.setText(listUpcoming.get(position).getLiveStreamName());
            try {
                try {
                    holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.ivLive.setVisibility(View.GONE);
                Date d = getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime));
                if (d != null) {
                    CountDownTimer t = new CountDownTimer(d.getTime(), 1000) {
                        public void onTick(long millisUntilFinished) {
                            holder.tvTime.setText(hmsTimeFormatter(millisUntilFinished));
                            holder.ivLive.setVisibility(View.GONE);
                        }

                        public void onFinish() {
                            holder.tvTimeType.setText("Ends At");
                            try {
                                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            holder.ivLive.setVisibility(View.VISIBLE);

                        }

                    };

                    t.start();

                    listTimers.add(t);

                    utils.showLog(TAG, "starts in " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
                } else {
                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime()).before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {

                        holder.tvTimeType.setText("Ended At");
                        holder.tvTime.setText(new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime())));
                        holder.ivLive.setVisibility(View.GONE);

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (holder.ivLive.getVisibility() == View.VISIBLE) {
//                        String[] val = (listUpcoming.get(position).getJoinUrl()).split("j/");
//                        launchZoomUrl(val[1].replace("?", "&"));
//                    } else {
//                        if (holder.tvTimeType.getText().toString().equalsIgnoreCase("Ended At")){
//                            try {
//                                new MyUtils().alertDialog(3, TeacherLiveRecordedClasses.this, "Oops!", "Live Class Ended on " +
//                                                new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(listUpcoming.get(position).getLiveStreamStartTime())),
//                                        "Close", "", false);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }else{
//                            try {
//                                new MyUtils().alertDialog(3, TeacherLiveRecordedClasses.this, "Oops!", "Live Class will Start at " +
//                                                new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(listUpcoming.get(position).getLiveStreamStartTime())),
//                                        "Close", "", false);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listUpcoming.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubject, tvTimeType, tvTime;
            ImageView ivLive;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubject = itemView.findViewById(R.id.tv_sub);
                tvTimeType = itemView.findViewById(R.id.tv_time_type);
                tvTime = itemView.findViewById(R.id.tv_time);
                ivLive = itemView.findViewById(R.id.iv_live);
            }
        }
    }

    void getTodoList(){


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().GetTeacherToDo+"schemaName="+sh_Pref.getString("schema", "")+"&userId="+tObj.getUserId())
                .build();

        utils.showLog(TAG, "url " + new AppUrls().GetTeacherToDo+"schemaName="+sh_Pref.getString("schema", "")+"&userId="+tObj.getUserId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getUpcomingTests();
                        findViewById(R.id.ll_todolist).setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body();
                String resp = body.string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getUpcomingTests();
                    }
                });
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                        JSONArray jsonArray = jsonObject.getJSONArray("todoList");

                        listTodos.clear();
                        listTodos.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<ToDosObj>>() {
                        }.getType()));

                        if (listTodos.size()>0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.ll_todolist).setVisibility(View.VISIBLE);
                                    todoAdapter = new ToDoAdapter();
                                    rvToDos.setLayoutManager(new LinearLayoutManager(TeacherHome.this));
                                    rvToDos.setAdapter(todoAdapter);
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.ll_todolist).setVisibility(View.GONE);
                                }
                            });
                        }


                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.ll_todolist).setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    void getUpcomingTests(){


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Request request = new Request.Builder()
                .url(AppUrls.GetUpcomingTestForTeacherDashBoard + "schemaName=" + sh_Pref.getString("schema","")  + "&userId=" + tObj.getUserId() + "&branchId=" + tObj.getBranchId())
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetUpcomingTestForTeacherDashBoard + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + tObj.getUserId() + "&branchId=" + tObj.getBranchId());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getServerTime();
                        cvNoLiveExams.setVisibility(View.VISIBLE);
                        rvUpcomingTests.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body();
                String jsonResp = response.body().string();

                Log.v(TAG, "Online Test List Responce- " + jsonResp);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getServerTime();
                    }
                });
                try {
                    JSONObject ParentjObject = new JSONObject(jsonResp);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("TestCategories");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<TeacherTestsObj>>() {
                        }.getType();

                        allUpcomingTests.addAll(gson.fromJson(jsonArr.toString(), type));


                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rvUpcomingTests.setVisibility(View.VISIBLE);
                                    setUpcomingTests();
                                }
                            }, 2000);
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void setUpcomingTests() {

        if (allUpcomingTests.size() < 3) {
            if (allUpcomingTests.size() == 0) {
                cvNoLiveExams.setVisibility(View.VISIBLE);
            } else {
                rvUpcomingTests.setLayoutManager(new LinearLayoutManager(TeacherHome.this,RecyclerView.HORIZONTAL,false));
                rvUpcomingTests.setAdapter(new UpcomingTestAdapter(allUpcomingTests));
            }
        } else {
            List<TeacherTestsObj> tests = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                tests.add(allUpcomingTests.get(i));
            }
            rvUpcomingTests.setLayoutManager(new LinearLayoutManager(TeacherHome.this,RecyclerView.HORIZONTAL,false));
            rvUpcomingTests.setAdapter(new UpcomingTestAdapter(tests));
        }


    }

    private void getServerTime() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetServerTime)
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetServerTime);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                getLiveClasses();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    getUpcomingClasses();
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        getUpcomingClasses();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    void getUpcomingClasses(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "URL - " + new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId());

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        swipeLayout.setRefreshing(false);
                        rvUpcomingClasses.setVisibility(View.GONE);
                        cvNoLiveClasses.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        swipeLayout.setRefreshing(false);
                    }
                });
                try {
                    JSONObject ParentjObject = new JSONObject(resp);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("info");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<TeacherLiveClassObj>>() {
                        }.getType();

                        videoSessionObjList.clear();
                        videoSessionObjList.addAll(gson.fromJson(jsonArr.toString(), type));

                        for (int i = 0; i < videoSessionObjList.size(); i++) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date d = null;
                            try {
                                d = df.parse(videoSessionObjList.get(i).getLiveStreamStartTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar gc = new GregorianCalendar();
                            gc.setTime(d);
                            gc.add(Calendar.MINUTE, videoSessionObjList.get(i).getLiveStreamDuration());
                            Date d2 = gc.getTime();
                            videoSessionObjList.get(i).setEndTime(df.format(d2));
                            Log.v(TAG, "End time " + videoSessionObjList.get(i).getEndTime());
                        }

                        if (videoSessionObjList.size()>0) {
                            classes.clear();
                            completed.clear();
                        }

                        for (int i = 0; i < videoSessionObjList.size(); i++) {
                            try {
                                if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoSessionObjList.get(i).getLiveStreamStartTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                    classes.add(videoSessionObjList.get(i));
                                }  else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoSessionObjList.get(i).getEndTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                    classes.add(videoSessionObjList.get(i));
                                }else {
                                    completed.add(videoSessionObjList.get(i));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (classes.size()>0){
                                    rvUpcomingClasses.setLayoutManager(new LinearLayoutManager(TeacherHome.this, RecyclerView.HORIZONTAL,false));
                                    rvUpcomingClasses.setAdapter(new LiveAdapter(classes));
                                    cvNoLiveClasses.setVisibility(View.GONE);
                                }else {
                                    cvNoLiveClasses.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.dismissDialog();
                swipeLayout.setRefreshing(false);
            }
        });

    }

    void getLiveClassesForSchedules(){

        if (utils.loading!=null && !utils.loading.isShowing()){
            utils.showLoader(TeacherHome.this);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "URL - " + new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId()+"&filterDate="+reqDate);

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId()+"&filterDate="+reqDate)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getExamsForSchedules();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getExamsForSchedules();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentObject = new JSONObject(resp);
                        if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")){
                            if (ParentObject.getJSONArray("info")!=null)
                            {
                                for (int i=0;i<ParentObject.getJSONArray("info").length();i++){
                                    schedulesArray.put(ParentObject.getJSONArray("info").getJSONObject(i));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getExamsForSchedules();
                    }
                });
            }
        });

    }

    void getExamsForSchedules(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "URL - " + new AppUrls().GetMonthTestsForTeacher + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId()+"&branchId="+tObj.getBranchId()+"&filterDate="+reqDate);

        Request request = new Request.Builder()
                .url(new AppUrls().GetMonthTestsForTeacher + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId()+"&branchId="+tObj.getBranchId()+"&filterDate="+reqDate)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        swipeLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            swipeLayout.setRefreshing(false);
                        }
                    });
                }else{
                    try {
                        JSONObject ParentObject = new JSONObject(resp);
                        if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")){
                            for (int i=0;i<ParentObject.getJSONArray("TestCategories").length();i++){
                                schedulesArray.put(ParentObject.getJSONArray("TestCategories").getJSONObject(i));
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createScheduleList();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        swipeLayout.setRefreshing(false);
                    }
                });
            }
        });
    }



    private void createScheduleList() {

        scheduleEvents.clear();


        for (int i=0;i<schedulesArray.length();i++){
            try {
                utils.showLog(TAG,"schedules "+schedulesArray.getJSONObject(i).toString());

                JSONObject obj = schedulesArray.getJSONObject(i);

                if (obj.has("testName")){
                    scheduleEvents.add(new ScheduleObj(obj.getString("testStartDate"),obj.getString("testName"),obj.getString("testCategory"),obj.getString("duration"),"test"));
                }else{
                    scheduleEvents.add(new ScheduleObj(obj.getString("liveStreamStartTime"),obj.getString("liveStreamName"),obj.getString("liveStreamStatus"),obj.getString("liveStreamDuration"),"live"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        utils.showLog(TAG,"schedules "+scheduleEvents.size());
        utils.showLog(TAG,"schedules "+count);



        if (scheduleEvents.size()>0){
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
            rvEvents.setLayoutManager(new LinearLayoutManager(TeacherHome.this));
            rvEvents.setAdapter(new EventAdapter(scheduleEvents));
            rvEvents.scheduleLayoutAnimation();
            rvEvents.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);
        }else{
            rvEvents.setVisibility(View.GONE);
            findViewById(R.id.iv_no_schedules).setVisibility(View.VISIBLE);
        }

        if (scheduleEvents.size()>1){
            tvViewMore.setVisibility(View.VISIBLE);
        }else{
            tvViewMore.setVisibility(View.GONE);
        }
    }
    
    class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>{

        @NonNull
        @Override
        public ToDoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ToDoAdapter.ViewHolder(LayoutInflater.from(TeacherHome.this).inflate(R.layout.item_todo_incomplete,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ToDoAdapter.ViewHolder holder, int position) {
            holder.tvTitle.setText(listTodos.get(position).getTodoTitle());
            holder.tvDescription.setText(listTodos.get(position).getTodoDesc());
            holder.viewComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ToDo todo = toDosActive.get(position);
//                    if (todo.getReminderCode()!=0){
//                        Intent intent = new Intent(MainActivity.this, FinalAlarm.class);//the same as up
//                        boolean isWorking = (PendingIntent.getBroadcast(MainActivity.this, todo.getReminderCode(), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
//                        if (isWorking) {
//                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                            Intent intent1 = new Intent(MainActivity.this, FinalAlarm.class);//the same as up
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, todo.getReminderCode(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);//the same as up
//                            am.cancel(pendingIntent);//important
//                            pendingIntent.cancel();//important
//                            todo.setReminderCode(0);
//                            todo.setIsRemainder(0);
//                        }
//                        else {
//                            todo.setReminderCode(0);
//                            todo.setIsRemainder(0);
//                        }
//                    }
//                    todo.setIsCompleted(1);
//                    ToDoDatabase.databaseWriteExecutor.execute(() -> {
//                        qdb.toDoDao().updateToDO(todo);
//                    });
//                    getTodos();
                }
            });
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherHome.this, TeacherToDoActivity.class);
                    intent.putExtra("todo",listTodos.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listTodos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription;
            View viewComplete;
            LinearLayout llItem;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_todotitle);
                tvDescription = itemView.findViewById(R.id.tv_tododesc);
                viewComplete = itemView.findViewById(R.id.vw_complete);
                llItem = itemView.findViewById(R.id.ll_item);
            }
        }
    }

    private String hmsTimeFormatter(long milliSeconds) {

        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    Date getDateDifference(Date endDate, Date startDate) {

        long duration = endDate.getTime() - startDate.getTime();

        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        utils.showLog(TAG, "diff " + diffInHours);

        if (diffInSeconds > 0) {
            Date d = new Date(diffInSeconds * 1000);

            return d;
        } else {
            return null;
        }

    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        if (listTimers.size()>0) {
            for (CountDownTimer t : listTimers) {
                t.cancel();
            }
        }
        super.onPause();
    }

}