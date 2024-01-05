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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import myschoolapp.com.gsnedutech.Models.ScheduleObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherLiveRecordedClasses;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TeacherSchedules extends Fragment {

    private static final String TAG = CalendarFragment.class.getName();

    View viewTeacherSchedules;
    Unbinder unbinder;
    
    SharedPreferences sh_Pref;
    TeacherObj tObj;

    @BindView(R.id.rv_events)
    RecyclerView rvEvents;
    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView cvEvents;

    MyUtils utils = new MyUtils();
    Activity mActivity;

    List<ScheduleObj> scheduleEvents = new ArrayList<>();
    JSONArray schedulesArray = new JSONArray();
    
    String reqDate="";


    public TeacherSchedules() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewTeacherSchedules = inflater.inflate(R.layout.fragment_teacher_schedules, container, false);
        unbinder = ButterKnife.bind(this,viewTeacherSchedules);

        init();

        return viewTeacherSchedules;
    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        tvMonthYear.setText(sdf.format(d));

        reqDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
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

                        reqDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());


                        cvEvents.setCurrentDate(cal.getTime());
                        tvMonthYear.setText(mon+" "+year);

                        getLiveClassesForSchedules();

                    }
                });

                pickerDialog.show(getFragmentManager(), "MonthYearPickerDialog");
            }
        });



        cvEvents.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                reqDate = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);
                getLiveClassesForSchedules();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
                tvMonthYear.setText(sdf.format(firstDayOfNewMonth));
                reqDate = new SimpleDateFormat("yyyy-MM-dd").format(firstDayOfNewMonth);
                getLiveClassesForSchedules();
            }
        });

        getLiveClassesForSchedules();
    }


    private void createScheduleList() {

        scheduleEvents.clear();

        utils.showLog(TAG,"schedules");

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

        if (scheduleEvents.size()>0){
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
            rvEvents.setLayoutManager(new LinearLayoutManager(mActivity));
            rvEvents.setAdapter(new EventAdapter(scheduleEvents));
            rvEvents.scheduleLayoutAnimation();
            rvEvents.setVisibility(View.VISIBLE);
            viewTeacherSchedules.findViewById(R.id.iv_no_schedules).setVisibility(View.GONE);
        }else{
            rvEvents.setVisibility(View.GONE);
            viewTeacherSchedules.findViewById(R.id.iv_no_schedules).setVisibility(View.VISIBLE);
        }
    }

    void getLiveClassesForSchedules(){

        schedulesArray = new JSONArray();
        
        utils.showLoader(mActivity);

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
                mActivity.runOnUiThread(new Runnable() {
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
                    mActivity.runOnUiThread(new Runnable() {
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

                mActivity.runOnUiThread(new Runnable() {
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
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

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createScheduleList();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
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
                            mActivity.startActivity(new Intent(mActivity, TeacherLiveRecordedClasses.class));
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "test":
//                            mActivity.startActivity(new Intent(mActivity, StudentLiveExams.class));
//                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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