package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.AssignmentDisplayNew;
import myschoolapp.com.gsnedutech.AssignmentsNew;
import myschoolapp.com.gsnedutech.Models.ScheduleObj;
import myschoolapp.com.gsnedutech.Models.ToDosObj;
import myschoolapp.com.gsnedutech.StudentAttendance;
import myschoolapp.com.gsnedutech.Models.DateObj;
import myschoolapp.com.gsnedutech.Models.Events;
import myschoolapp.com.gsnedutech.Models.EventsAndHoliday;
import myschoolapp.com.gsnedutech.Models.HomeWork;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetail;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.LiveVideo;
import myschoolapp.com.gsnedutech.Models.LiveVideoInfo;
import myschoolapp.com.gsnedutech.Models.PersonalNotesObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.PersonalNotes;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.ReportActivity;
import myschoolapp.com.gsnedutech.ScheduleAndCalendar;
import myschoolapp.com.gsnedutech.StudentHome;
import myschoolapp.com.gsnedutech.StudentLiveClassesTabs;
import myschoolapp.com.gsnedutech.StudentLiveExamsTabs;
import myschoolapp.com.gsnedutech.ToDoActivity;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.db.NotesDatabase;
import myschoolapp.com.gsnedutech.descriptive.StudentDescExamsTabs;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StudentBottomHomeFrag extends Fragment {

    private static final String TAG = "SriRam -" + StudentBottomHomeFrag.class.getName();
    MyUtils utils = new MyUtils();

    Activity mActivity;

    View viewStudentHomeBottomFrag;
    Unbinder unbinder;

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


    @BindView(R.id.rv_personal_notes)
    RecyclerView rvPersonalNotes;

    @BindView(R.id.ll_personalnote)
    LinearLayout llPersonalNotes;

    List<PersonalNotesObj> listNotes = new ArrayList<>();

    NotesDatabase ndb;

    NoteAdapter noteAdapter;

    //personalNotes End

    //todos Start

    @BindView(R.id.rv_todos)
    RecyclerView rvToDos;

    @BindView(R.id.ll_todolist)
    LinearLayout llTodos;

    List<ToDosObj> listTodos = new ArrayList<>();

    ToDoAdapter todoAdapter;

    //todoend


    List<HomeWork> hwList = new ArrayList<>();

    LiveVideo liveClass = null;
    StudentOnlineTestObj liveExam = null;

    String serverTime = "";

    String month, year;

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();
    List<StudentOnlineTestObj> listTest = new ArrayList<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "";


    String currentDate = "";

    int count = 1;

    List<DateObj> listDates = new ArrayList<>();
    List<EventsAndHoliday> eventList = new ArrayList<>();


    CalendarAdapter calendarAdapter;
    int selectedDay = 0;

    JSONArray schedulesArray = new JSONArray();
    List<ScheduleObj> scheduleEvents = new ArrayList<>();

    boolean forSchedules = true;
    String scheduleSelectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    String hwDate = "";


    public StudentBottomHomeFrag() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentHomeBottomFrag = inflater.inflate(R.layout.fragment_student_bottom_home_new, container, false);
        unbinder = ButterKnife.bind(this, viewStudentHomeBottomFrag);

        ((StudentHome)getActivity()).getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent,null));

        init();

        viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return viewStudentHomeBottomFrag;
    }

    @Override
    public void onResume() {
        ((StudentHome) mActivity).fabMyDoubts.setVisibility(View.GONE);
        super.onResume();
        schedulesArray = new JSONArray();
        scheduleEvents.clear();
        getHomeWorks();
        getServerTime(false);
        getUpcomingExams();
    }

    void init() {


        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        hwDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        month = new SimpleDateFormat("MM").format(new Date());
        year = new SimpleDateFormat("yyyy").format(new Date());

//        tvAttDate.setText(new SimpleDateFormat("dd").format(new Date()));
//        tvAttMonthYear.setText(new SimpleDateFormat("MMM,yyyy").format(new Date()));


        selectedDay = Calendar.getInstance().get(Calendar.DATE);


        tvDate.setText(new SimpleDateFormat("dd MMMM, yyyy").format(new Date()));
        tvMonthName.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));

        viewStudentHomeBottomFrag.findViewById(R.id.tv_view_all_assignments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, AssignmentsNew.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.tv_month_name).setOnClickListener(new View.OnClickListener() {
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
                                StudentBottomHomeFrag.this.month = "01";
                                break;
                            case 2:
                                mon = "February";
                                StudentBottomHomeFrag.this.month = "02";
                                break;
                            case 3:
                                mon = "March";
                                StudentBottomHomeFrag.this.month = "03";
                                break;
                            case 4:
                                mon = "April";
                                StudentBottomHomeFrag.this.month = "04";
                                break;
                            case 5:
                                mon = "May";
                                StudentBottomHomeFrag.this.month = "05";
                                break;
                            case 6:
                                mon = "June";
                                StudentBottomHomeFrag.this.month = "06";
                                break;
                            case 7:
                                mon = "July";
                                StudentBottomHomeFrag.this.month = "07";
                                break;
                            case 8:
                                mon = "August";
                                StudentBottomHomeFrag.this.month = "08";
                                break;
                            case 9:
                                mon = "September";
                                StudentBottomHomeFrag.this.month = "09";
                                break;
                            case 10:
                                mon = "October";
                                StudentBottomHomeFrag.this.month = "10";
                                break;
                            case 11:
                                mon = "November";
                                StudentBottomHomeFrag.this.month = "11";
                                break;
                            case 12:
                                mon = "December";
                                StudentBottomHomeFrag.this.month = "12";
                                break;
                        }
                        Calendar cal = Calendar.getInstance();

                        StudentBottomHomeFrag.this.year = year + "";


                        tvMonthName.setText(mon + " " + year);
                        tvDate.setText(1 + " " + mon + ", " + year);
                        selectedDay = 1;
                        calendarWork((month - 1), year);
                        scheduleSelectedDate = year + "-" + month + "-" + selectedDay;
                        forSchedules = true;
                        schedulesArray = new JSONArray();
                        getServerTime(true);
                    }
                });

                pickerDialog.show(getFragmentManager(), "MonthYearPickerDialog");

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
                    rvEvents.setLayoutManager(new LinearLayoutManager(mActivity));
                    rvEvents.getAdapter().notifyDataSetChanged();
                    rvEvents.scheduleLayoutAnimation();
                } else {
                    tvViewMore.setText("View More");
                    tvViewMore.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_double_down_arrow, 0, R.drawable.ic_double_down_arrow, 0);
                    count = 1;
                    final LayoutAnimationController controller =
                            AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
                    rvEvents.setLayoutManager(new LinearLayoutManager(mActivity));
                    rvEvents.getAdapter().notifyDataSetChanged();
                    rvEvents.scheduleLayoutAnimation();
                }
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.tv_analysis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, ReportActivity.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.tv_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, ScheduleAndCalendar.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.tv_view_all_personalnotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, PersonalNotes.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.tv_view_all_todos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, ToDoActivity.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.iv_upcoming_exams).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, StudentLiveExamsTabs.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.iv_upcoming_classes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, StudentLiveClassesTabs.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.tv_attendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, StudentAttendance.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        viewStudentHomeBottomFrag.findViewById(R.id.iv_desc_exams).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startActivity(new Intent(mActivity, StudentDescExamsTabs.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
    }

    void getHomeWorks() {

        viewStudentHomeBottomFrag.findViewById(R.id.rv_hw).setVisibility(View.GONE);
        viewStudentHomeBottomFrag.findViewById(R.id.pb_hw).setVisibility(View.VISIBLE);

        String currentMonth = new SimpleDateFormat("MM").format(new Date());
        String currentYear = new SimpleDateFormat("yyyy").format(new Date());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentHomeWorksByStatus + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&monthId=" + currentMonth + "&yearId=" + currentYear+"&filterDate="+hwDate + "&status=active")
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url -" + AppUrls.GetStudentHomeWorksByStatus + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&monthId=" + currentMonth + "&yearId=" + currentYear+"&filterDate="+hwDate + "&status=active");


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mActivity.runOnUiThread(() -> {
                    viewStudentHomeBottomFrag.findViewById(R.id.pb_hw).setVisibility(View.GONE);
                });
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
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvHw.setVisibility(View.VISIBLE);
                                            viewStudentHomeBottomFrag.findViewById(R.id.pb_hw).setVisibility(View.GONE);
                                            rvHw.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
                                            rvHw.setAdapter(new HwAdapter(listHwPending, typeHw));
                                        }
                                    });
                                } else {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvHw.setVisibility(View.GONE);
                                            viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                                            viewStudentHomeBottomFrag.findViewById(R.id.pb_hw).setVisibility(View.GONE);
                                            viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    ((StudentHome) mActivity).selectCourseTab();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                        }else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvHw.setVisibility(View.GONE);
                                    viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                                    viewStudentHomeBottomFrag.findViewById(R.id.pb_hw).setVisibility(View.GONE);
                                    viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ((StudentHome) mActivity).selectCourseTab();
                                        }
                                    });
                                }
                            });
                        }
                    }else{
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvHw.setVisibility(View.GONE);
                                viewStudentHomeBottomFrag.findViewById(R.id.pb_hw).setVisibility(View.GONE);
                                viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                                viewStudentHomeBottomFrag.findViewById(R.id.iv_no_home_work).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((StudentHome) mActivity).selectCourseTab();
                                    }
                                });
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    float pxToDp(int someDpValue) {
        float density = mActivity.getResources().getDisplayMetrics().density;
        float px = someDpValue * density;
        return px;
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

        LinearLayoutManager manager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        rvCal.setLayoutManager(manager);
        calendarAdapter = new CalendarAdapter(listDates);
        rvCal.setAdapter(calendarAdapter);
        manager.scrollToPosition((selectedDay - 1));


    }

    private void getServerTime(boolean otherCalls) {

        viewStudentHomeBottomFrag.findViewById(R.id.rv_events).setVisibility(View.GONE);
        viewStudentHomeBottomFrag.findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);
        viewStudentHomeBottomFrag.findViewById(R.id.pb_sc).setVisibility(View.VISIBLE);

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
                if(otherCalls)
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(otherCalls)
                    getUpcomingExams();
            }
        });
    }

    void getUpcomingExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId="
                        + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate=" + scheduleSelectedDate)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();
        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate=" + scheduleSelectedDate);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (mActivity != null) {
                    getLiveClasses();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (response.body() != null) {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);

                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                JSONArray newJar = object.getJSONArray("Tests");
                                for (int j = 0; j < newJar.length(); j++) {
                                    schedulesArray.put(newJar.getJSONObject(j));
                                }
                            }
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
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&sectionId=" + sObj.getClassCourseSectionId() + "&status=active&filterDate=" + scheduleSelectedDate);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewStudentHomeBottomFrag.findViewById(R.id.pb_sc).setVisibility(View.GONE);
                            createSchedulesList();
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewStudentHomeBottomFrag.findViewById(R.id.pb_sc).setVisibility(View.GONE);
                                    createSchedulesList();
                                }
                            });
                        }
                    } else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);

                        JSONObject ParentjObject = new JSONObject(resp);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<LiveVideoInfo>>() {
                        }.getType();

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("liveVideoInfo");
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject object = jsonArr.getJSONObject(i);
                                JSONArray newJar = object.getJSONArray("LiveVideos");
                                for (int j = 0; j < newJar.length(); j++) {
                                    schedulesArray.put(newJar.getJSONObject(j));
                                }
                            }

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewStudentHomeBottomFrag.findViewById(R.id.pb_sc).setVisibility(View.GONE);
                                    createSchedulesList();
                                }
                            });

                            utils.showLog(TAG, "schedules size " + schedulesArray.length());
                            utils.showLog(TAG, "schedules val " + schedulesArray.toString());

                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewStudentHomeBottomFrag.findViewById(R.id.pb_sc).setVisibility(View.GONE);
                                    createSchedulesList();
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
            viewStudentHomeBottomFrag.findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
            rvEvents.setLayoutManager(new LinearLayoutManager(mActivity));
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
            viewStudentHomeBottomFrag.findViewById(R.id.iv_no_schedules).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

        @NonNull
        @Override
        public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NoteAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_notes_home, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            holder.tvTitle.setText(listNotes.get(position).getPersonalNoteTitle());
            holder.tvNote.setText(listNotes.get(position).getPersonalNote());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd");
            try {
                holder.tvCreatedOn.setText("Created on " + sdf1.format(sdf.parse(listNotes.get(position).getCreatedDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (listNotes.get(position).getUpdatedDate().length() > 0) {
                try {
                    holder.tvCreatedOn.setText("Updated at " + sdf1.format(sdf.parse(listNotes.get(position).getUpdatedDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
//
            if (listNotes.get(position).getColor().length() > 4) {
                try {
                    holder.llMainBackground.setBackgroundColor(Color.parseColor(listNotes.get(position).getColor()));
                } catch (NumberFormatException e) {

                }
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.startActivity(new Intent(mActivity, PersonalNotes.class));
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listNotes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvNote, tvCreatedOn;
            ImageView ivEdit, ivDelete;
            LinearLayout llMainBackground;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tv_title);
                tvNote = itemView.findViewById(R.id.tv_note);
                ivEdit = itemView.findViewById(R.id.iv_edit);
                ivDelete = itemView.findViewById(R.id.iv_delete);
                llMainBackground = itemView.findViewById(R.id.ll_main_bg);
                tvCreatedOn = itemView.findViewById(R.id.tv_createddate);

            }
        }
    }

    class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

        @NonNull
        @Override
        public ToDoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_todo_incomplete, parent, false));
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
                    Intent intent = new Intent(mActivity, ToDoActivity.class);
                    intent.putExtra("todo", listTodos.get(position));
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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
            return new HwAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_dashboard_homework, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HwAdapter.ViewHolder holder, int position) {

            if (position == 0) {
                CardView.LayoutParams params = new CardView.LayoutParams(
                        (int) pxToDp(320),
                        CardView.LayoutParams.WRAP_CONTENT
                );
                params.setMargins((int) pxToDp(16), (int) pxToDp(5), (int) pxToDp(5), (int) pxToDp(5));
                holder.itemView.setLayoutParams(params);
            }

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
                    Intent intent = new Intent(mActivity, AssignmentDisplayNew.class);
                    intent.putExtra("hwObj", (Serializable) listHwPending.get(position));
                    intent.putExtra("type", typeHw.get(position));
                    intent.putExtra("hwId",listHwPending.get(position).getHomeworkId()+"");
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
        List<DateObj> listDate;

        public CalendarAdapter(List<DateObj> listDate) {
            this.listDate = listDate;
        }

        @NonNull
        @Override
        public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CalendarAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_calendar, parent, false));
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
                    getServerTime(true);
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
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_schedule_60, parent, false));
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
                            mActivity.startActivity(new Intent(mActivity, StudentLiveClassesTabs.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "test":
                            mActivity.startActivity(new Intent(mActivity, StudentLiveExamsTabs.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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

}