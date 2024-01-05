package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.Events;
import myschoolapp.com.gsnedutech.Models.LiveVideoInfo;
import myschoolapp.com.gsnedutech.Models.ScheduleObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentLiveClassesTabs;
import myschoolapp.com.gsnedutech.StudentLiveExamsTabs;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SchedulesFrag extends Fragment {

    private static final String TAG = "SriRam -" + SchedulesFrag.class.getName();

    View viewSchedules;
    Unbinder unbinder;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView cvEvents;

    MyUtils utils = new MyUtils();


    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;
    @BindView(R.id.rv_schedules)
    RecyclerView rvSchedules;
    List<Events> events = new ArrayList<>();
    Activity mActivity;

    String serverTime = "";

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    JSONArray schedulesArray = new JSONArray();
    List<ScheduleObj> scheduleEvents = new ArrayList<>();
    String scheduleSelectedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    
    public SchedulesFrag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewSchedules =  inflater.inflate(R.layout.fragment_schedules, container, false);
        unbinder = ButterKnife.bind(this, viewSchedules);

        init();

        return viewSchedules;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        tvMonthYear.setText(sdf.format(d));

//        events.add(new Events("09:00 am", "Mathematics Live Class", "Complete chapter 4", 60, "Live"));
//        events.add(new Events("11:00 am", "Science Practice Test", "Complete chapter 4", 60, "Test"));
//        events.add(new Events("12:00 am", "K-Hub Course Completion", "Complete chapter 4", 60, "K-Hub"));
//        events.add(new Events("02:00 pm", "K-Hub Course Completion", "Complete chapter 4", 60, "Live"));

        getServerTime();
//        rvSchedules.setVisibility(View.VISIBLE);
//        final LayoutAnimationController controller =
//                AnimationUtils.loadLayoutAnimation(rvSchedules.getContext(), R.anim.layout_animation_fall_down);
//        rvSchedules.setLayoutManager(new LinearLayoutManager(mActivity));
//        rvSchedules.setAdapter(new EventAdapter(events));
//        rvSchedules.scheduleLayoutAnimation();
//        viewSchedules.findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);

        tvMonthYear.setOnClickListener(new View.OnClickListener() {
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
                        cal.set(year,(month-1),1);

//                        scrolledDate = cal.getTime();

                        cvEvents.setCurrentDate(cal.getTime());
                        tvMonthYear.setText(mon+" "+year);

//                        getEvents(month + "", year + "");

                    }
                });

                pickerDialog.show(getFragmentManager(), "MonthYearPickerDialog");
            }
        });

        cvEvents.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                scheduleSelectedDate = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);
                getServerTime();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(firstDayOfNewMonth));
                scheduleSelectedDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDayOfNewMonth);
                getServerTime();
            }
        });
    }

//    class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
//
//        List<Events> listEvent;
//
//        EventAdapter(List<Events> listEvent) {
//            this.listEvent = listEvent;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (listEvent.get(position).getDuration() <= 15) {
//                return 0;
//            } else if (listEvent.get(position).getDuration() > 15 && listEvent.get(position).getDuration() <= 30) {
//                return 1;
//            } else if (listEvent.get(position).getDuration() > 30 && listEvent.get(position).getDuration() <= 60) {
//                return 2;
//            } else {
//                return 3;
//            }
//
//        }
//
//        @NonNull
//        @Override
//        public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            switch (viewType) {
//                case 1:
//                    return new EventAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_schedule_30, parent, false));
//                case 2:
//                    return new EventAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_schedule_60, parent, false));
//                case 3:
//                    return new EventAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_schedule_90, parent, false));
//                default:
//                    return new EventAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_schedule_60_not_present, parent, false));
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
//
//            if (listEvent.get(position).getDuration() <= 15) {
//                holder.tvTime.setText(listEvent.get(position).getTime());
//                holder.tvTime.setTextSize(14);
//            } else {
//                holder.tvTime.setText(listEvent.get(position).getTime().split(" ")[0] + "\n" + listEvent.get(position).getTime().split(" ")[1]);
//            }
//            holder.tvDuration.setText(listEvent.get(position).getDuration() + " mins");
//            holder.tvTitle.setText(listEvent.get(position).getEventTitle());
//            holder.tvDesc.setText(listEvent.get(position).getEventDesc());
//
//            switch (listEvent.get(position).getType()) {
//                case "Live":
//                    holder.flBackground.setBackgroundResource(R.drawable.bg_red_gradient);
//                    holder.llMain.setBackgroundResource(R.drawable.bg_red_mask);
//                    break;
//                case "K-Hub":
//                    holder.flBackground.setBackgroundResource(R.drawable.bg_blue_gradient);
//                    holder.llMain.setBackgroundResource(R.drawable.bg_blue_mask);
//                    break;
//                case "Test":
//                    holder.flBackground.setBackgroundResource(R.drawable.bg_green_gradient);
//                    holder.llMain.setBackgroundResource(R.drawable.bg_green_mask);
//                    break;
//            }
//
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return listEvent.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//
//            TextView tvTime, tvTitle, tvDesc, tvDuration;
//            LinearLayout llMain;
//            FrameLayout flBackground;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                tvTime = itemView.findViewById(R.id.tv_time);
//                tvTitle = itemView.findViewById(R.id.tv_title);
//                tvDesc = itemView.findViewById(R.id.tv_desc);
//                tvDuration = itemView.findViewById(R.id.tv_duration);
//                llMain = itemView.findViewById(R.id.ll_main);
//                flBackground = itemView.findViewById(R.id.fl_background);
//            }
//        }
//    }

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
                if (mActivity!=null) {
                    if (NetworkConnectivity.isConnected(mActivity)) {
                        getUpcomingExams();

                    } else {
                        new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close), false);
                    }
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    if (mActivity!=null) {
                        if (NetworkConnectivity.isConnected(mActivity)) {
                            getUpcomingExams();

                        } else {
                            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }
                    }
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        if (mActivity!=null) {
                            if (NetworkConnectivity.isConnected(mActivity)) {
                                getUpcomingExams();

                            } else {
                                new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close), false);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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



        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate=" + scheduleSelectedDate)
                .build();
        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active&filterDate="+scheduleSelectedDate);
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (mActivity!=null) {
                    if (NetworkConnectivity.isConnected(mActivity)) {
                        getLiveClasses();

                    } else {
                        new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close), false);
                    }
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    if (mActivity!=null) {
                        if (NetworkConnectivity.isConnected(mActivity)) {
                            getLiveClasses();

                        } else {
                            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }
                    }
                } else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    schedulesArray = new JSONArray();

                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");

                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                JSONArray newJar = object.getJSONArray("Tests");
                                for (int j=0;j<newJar.length();j++){
                                    schedulesArray.put(newJar.getJSONObject(j));
                                }
                            }
                        }else{
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//
                                }
                            });
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

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&sectionId=" + sObj.getClassCourseSectionId() + "&status=active&filterDate=" + scheduleSelectedDate)
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId() + "&sectionId=" + sObj.getClassCourseSectionId() + "&status=active&filterDate=" + scheduleSelectedDate);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (mActivity!=null) {
                    if (NetworkConnectivity.isConnected(mActivity)) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createSchedulesList();
                            }
                        });

                    } else {
                        new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close), false);
                    }
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        if (mActivity!=null) {
                            if (NetworkConnectivity.isConnected(mActivity)) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        createSchedulesList();
                                    }
                                });
                            } else {
                                new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close), false);
                            }
                        }

//                        getOverAllAttendance();
                    }
                    else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);


                        JSONObject ParentjObject = new JSONObject(resp);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<LiveVideoInfo>>() {
                        }.getType();

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("liveVideoInfo");

                            for (int i=0;i<jsonArr.length();i++){
                                JSONObject object = jsonArr.getJSONObject(i);
                                JSONArray newJar = object.getJSONArray("LiveVideos");
                                for (int j=0;j<newJar.length();j++){
                                    schedulesArray.put(newJar.getJSONObject(j));
                                }
                            }

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createSchedulesList();
                                }
                            });

                            utils.showLog(TAG,"schedules size "+schedulesArray.length());
                            utils.showLog(TAG,"schedules val "+schedulesArray.toString());
                        }
                        else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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


    void createSchedulesList(){


        scheduleEvents.clear();

        for (int i=0;i<schedulesArray.length();i++){
            try {

                JSONObject obj = schedulesArray.getJSONObject(i);

                if (obj.has("testName")){
                    scheduleEvents.add(new ScheduleObj(obj.getString("testStartDate"),obj.getString("testName"),obj.getString("testCategoryName"),obj.getString("testDuration"),"test"));
                }else{
                    scheduleEvents.add(new ScheduleObj(obj.getString("liveStreamStartTime"),obj.getString("liveStreamName"),obj.getString("facultyName"),obj.getString("duration"),"live"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (scheduleEvents.size()>0){
            rvSchedules.setVisibility(View.VISIBLE);
            viewSchedules.findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(rvSchedules.getContext(), R.anim.layout_animation_fall_down);
            rvSchedules.setLayoutManager(new LinearLayoutManager(mActivity));
//            count = 1;
            rvSchedules.setAdapter(new EventAdapter(scheduleEvents));
            rvSchedules.scheduleLayoutAnimation();

        }else {
            rvSchedules.setVisibility(View.GONE);
            viewSchedules.findViewById(R.id.iv_no_schedules).setVisibility(View.VISIBLE);
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
            return new EventAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_schedule_60, parent, false));
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
                    holder.tvDesc.setText("By - "+listEvent.get(position).getEventDesc());

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
                rvSchedules.getLayoutManager().scrollToPosition(position);
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
            return listEvent.size();
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


    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}