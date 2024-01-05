package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
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
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.TeacherLiveClassObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TeacherLiveRecordedClasses extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherLiveRecordedClasses.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.tv_sub_name)
    TextView tvSubName;
    @BindView(R.id.tv_vid_name)
    TextView tvVidName;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.ll_live_classes)
    LinearLayout llLiveClasses;

    @BindView(R.id.ll_recorded_videos)
    LinearLayout llRecordedVideos;

    @BindView(R.id.tv_live_classes)
    TextView tvLiveClasses;

    @BindView(R.id.tv_recorded_videos)
    TextView tvRecordedVideos;


    @BindView(R.id.rv_live_class)
    RecyclerView rvLiveClasses;
    @BindView(R.id.rv_recorded_class)
    RecyclerView rvRecordedClass;

    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    @BindView(R.id.ll_tabs)
    LinearLayout llTabs;

    @BindView(R.id.cv_filter)
    CardView cvFilter;

    public String currentMonth,currentYear;
    int dayOfMonth;
    public String filterDate = "";
    public int type = 0;
    boolean toggle = false;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;
    AdminObj adminObj;

    String serverTime = "",liveJoinUrl="", liveStreamId = "";

    List<TeacherLiveClassObj> videoSessionObjList = new ArrayList<>();

    TeacherLiveClassObj liveClass = null;

    MyUtils utils = new MyUtils();


    List<TeacherLiveClassObj> classes = new ArrayList<>();
    List<TeacherLiveClassObj> completed = new ArrayList<>();


    List<CountDownTimer> listTimers = new ArrayList<>();
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_live_recorded_classes);
        ButterKnife.bind(this);
        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        findViewById(R.id.tv_view_all).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(TeacherLiveRecordedClasses.this, TeacherClassesViewAll.class);
//                intent.putExtra("type","live");
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//            }
//        });

        findViewById(R.id.tv_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStaffLiveClass(liveJoinUrl, liveStreamId);
//                String[] val = (liveJoinUrl).split("j/");
//                launchZoomUrl(val[1].replace("?", "&"));
            }
        });

        tvLiveClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLiveClasses.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvLiveClasses.setTextColor(Color.WHITE);
                tvRecordedVideos.setBackground(null);
                tvRecordedVideos.setTextColor(Color.rgb(73, 73, 73));
                tvRecordedVideos.setAlpha(0.75f);
                llLiveClasses.setVisibility(View.VISIBLE);
                llRecordedVideos.setVisibility(View.GONE);
            }
        });

        tvRecordedVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvRecordedVideos.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvRecordedVideos.setTextColor(Color.WHITE);
                tvLiveClasses.setBackground(null);
                tvLiveClasses.setTextColor(Color.rgb(73, 73, 73));
                tvLiveClasses.setAlpha(0.75f);
                llLiveClasses.setVisibility(View.GONE);
                llRecordedVideos.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.cv_date_time).setOnClickListener(new View.OnClickListener() {
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

                        currentMonth = month+"";
                        currentYear = year+"";
                        tvMonthYear.setText(mon + " " + year);
                        if (NetworkConnectivity.isConnected(TeacherLiveRecordedClasses.this)) {
                            try {
                                Date selctdt = new SimpleDateFormat("MM yyyy").parse((month) +" "+ year);
                                Date today = new SimpleDateFormat("MM yyyy").parse(new SimpleDateFormat("MM yyyy").format(new Date()));
                                llTabs.setVisibility(View.GONE);
                                if (selctdt.after(today)){
                                    tvLiveClasses.callOnClick();
                                }else if (selctdt.before(today)){
                                    tvRecordedVideos.callOnClick();
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
                        } else {
                            utils.alertDialog(1, TeacherLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }
                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

        cvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle){
                    ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                    toggle = false;
                }else {
                    ViewAnimation.showIn(findViewById(R.id.ll_backdrop));
                    toggle = true;
                }
            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        findViewById(R.id.tv_fbm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
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
//                        Calendar cal = Calendar.getInstance();
//                        cal.set(year, (month - 1), 1);
                        currentMonth = month + "";
                        currentYear = year + "";
//                        getHomeWorks();
                        tvMonthYear.setText(mon + " " + year);

                        ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                        toggle = false;
                        if (NetworkConnectivity.isConnected(TeacherLiveRecordedClasses.this)) {
                            try {
                                Date selctdt = new SimpleDateFormat("MM yyyy").parse((month) +" "+ year);
                                Date today = new SimpleDateFormat("MM yyyy").parse(new SimpleDateFormat("MM yyyy").format(new Date()));
                                llTabs.setVisibility(View.GONE);
                                if (selctdt.after(today)){
                                    tvLiveClasses.callOnClick();
                                }else if (selctdt.before(today)){
                                    tvRecordedVideos.callOnClick();
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
                        } else {
                            utils.alertDialog(1, TeacherLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }
                        //findViewById(R.id.ll_liveattendance).setVisibility(View.VISIBLE);
                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        findViewById(R.id.tv_fbd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type=0;
                DatePickerDialog dialog1 = new DatePickerDialog(TeacherLiveRecordedClasses.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yearCal, int monthOfYear, int dayOfMonth) {
                        utils.showLog(TAG,"vals "+yearCal+" "+monthOfYear+" "+dayOfMonth);
                        try {
                            tvMonthYear.setText(new SimpleDateFormat("dd MMMM yyyy").format( new SimpleDateFormat("yyyy-MM-dd").parse(yearCal+"-"+(monthOfYear+1)+"-"+dayOfMonth)));
                            filterDate = yearCal+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                            currentMonth = (monthOfYear+1)+"";
                            currentYear = yearCal+"";
                            ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                            toggle = false;
                            //currentTab = viewPager.getCurrentItem();
                            //viewPager.setCurrentItem(currentTab);
                           // findViewById(R.id.ll_liveattendance).setVisibility(View.VISIBLE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        getServerTime();
                    }
                }, Integer.parseInt(currentYear), Integer.parseInt(currentMonth)-1, dayOfMonth);
                dialog1.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getServerTime();
            }
            isNetworkAvail = true;
        }
    }


    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        currentMonth = new SimpleDateFormat("MM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());
        dayOfMonth = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        filterDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Gson gson = new Gson();
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            userId = ""+tObj.getUserId();
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            String json = sh_Pref.getString("adminObj", "");
            adminObj = gson.fromJson(json, AdminObj.class);
            userId = ""+adminObj.getUserId();
        }


    }



    private void getServerTime() {

        utils.showLoader(TeacherLiveRecordedClasses.this);


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
                getLiveClasses();
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
                            if (NetworkConnectivity.isConnected(TeacherLiveRecordedClasses.this)) {
                                getLiveClasses();

                            } else {
                                new MyUtils().alertDialog(1, TeacherLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close), false);
                            }
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, TeacherLiveRecordedClasses.this, message, sh_Pref);
                            });
                        }else {
                            getLiveClasses();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    getLiveClasses();
                }
            }
        });
    }

    void getLiveClasses(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "URL - " + new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId + "&month=" + currentMonth +"&year=" + currentYear);
       /* new AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                + "&userId=" + userId + "&month=" + currentMonth +"&year=" + currentYear*/
       String url = "";
        if (type == 1) {
            url = AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                    + "&userId=" + userId + "&filterMonth=" + currentYear + "-" + currentMonth + "-01";
            utils.showLog(TAG, "url - " + AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                    + "&userId=" + userId + "&filterMonth=" + currentYear + "-" + currentMonth + "-01");
        }
        else  if (type == 0){
            url = AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                    + "&userId=" + userId + "&filterDate="+filterDate;

            utils.showLog(TAG, "url - " + AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                    + "&userId=" + userId
                    +"&filterDate="+filterDate);
        }
        else {
            url = AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                    + "&userId=" + userId +"&status=active";
            utils.showLog(TAG, "url - " + AppUrls.GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "")
                    + "&userId=" + userId
                    +"&status=active");
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
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

                            classes.clear();
                            completed.clear();

                            for (int i = 0; i < videoSessionObjList.size(); i++) {
                                try {
                                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoSessionObjList.get(i).getLiveStreamStartTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                        classes.add(videoSessionObjList.get(i));
                                    }  else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoSessionObjList.get(i).getEndTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                        liveClass = videoSessionObjList.get(i);
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
                                    if (liveClass != null) {
                                        findViewById(R.id.cv_live).setVisibility(View.VISIBLE);
//                                        tvSubName.setText(liveClass.getFacultyName());
                                        tvVidName.setText(liveClass.getLiveStreamName());
                                        tvDuration.setText(liveClass.getLiveStreamDuration() + " mins");
                                        liveJoinUrl = liveClass.getStartUrl();
                                        liveStreamId = liveClass.getLiveStreamId().toString();
                                        try {
                                            tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getEndTime())));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        findViewById(R.id.cv_live).setVisibility(View.GONE);
                                    }
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (classes.size()>0){
                                        rvLiveClasses.setLayoutManager(new LinearLayoutManager(TeacherLiveRecordedClasses.this));
                                        rvLiveClasses.setAdapter(new LiveAdapter(classes));
                                        findViewById(R.id.cv_no_live_classes).setVisibility(View.GONE);
                                    }else {
                                        findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (completed.size()>0) {
                                        rvRecordedClass.setVisibility(View.VISIBLE);
                                        rvRecordedClass.setLayoutManager(new LinearLayoutManager(TeacherLiveRecordedClasses.this));
                                        rvRecordedClass.setAdapter(new TeacherLiveRecordedClasses.LiveAdapter(completed));
                                        findViewById(R.id.cv_no_record_classes).setVisibility(View.GONE);
                                    }
                                    else {
                                        rvRecordedClass.setVisibility(View.GONE);
                                        findViewById(R.id.cv_no_record_classes).setVisibility(View.VISIBLE);
                                    }
                                }
                            });


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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


    class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

        List<TeacherLiveClassObj> listUpcoming;

        public LiveAdapter(List<TeacherLiveClassObj> listUpcoming) {
            this.listUpcoming = listUpcoming;
        }

        @NonNull
        @Override
        public LiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LiveAdapter.ViewHolder(LayoutInflater.from(TeacherLiveRecordedClasses.this).inflate(R.layout.item_live_class, parent, false));
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

            holder.itemView.setOnClickListener(view -> {
                if (holder.ivLive.getVisibility() == View.VISIBLE) {
                    launchZoomUrl(listUpcoming.get(position).getStartUrl());
//                        String[] val = (listUpcoming.get(position).getJoinUrl()).split("j/");
//                        launchZoomUrl(val[1].replace("?", "&"));
                } else {
                    if (holder.tvTimeType.getText().toString().equalsIgnoreCase("Ended At")){
                        try {
                            new MyUtils().alertDialog(3, TeacherLiveRecordedClasses.this, "Oops!", "Live Class Ended on " +
                                            new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime())),
                                    "Close", "", false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            new MyUtils().alertDialog(3, TeacherLiveRecordedClasses.this, "Oops!", "Live Class will Start at " +
                                            new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime())),
                                    "Close", "", false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

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


    private void launchZoomUrl(String meetingId) {

        Log.v(TAG,"zoon - url - "+"" + meetingId);
        Log.v(TAG,"zoon - url - "+"zoomus://zoom.us/" + meetingId);

        boolean isAppInstalled = appInstalledOrNot("us.zoom.videomeetings");

        if (isAppInstalled) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(meetingId));
            startActivity(i);
        } else {
//            Toast.makeText(TeacherLiveRecordedClasses.this, "Please Install Zoom App", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherLiveRecordedClasses.this);
            ViewGroup viewGroup = TeacherLiveRecordedClasses.this.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(TeacherLiveRecordedClasses.this).inflate(R.layout.cv_zoom, viewGroup, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
            alertDialog.getWindow().setLayout(width, height);
            alertDialog.show();

            dialogView.findViewById(R.id.tv_install).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=us.zoom.videomeetings"));
                    startActivity(intent);
                    alertDialog.dismiss();

                }
            });

            dialogView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
    }

//    private void launchZoomUrl(String meetingId) {
////        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://narayanagroup.zoom.us/j/6583554590?pwd="));
//
//        boolean isAppInstalled = appInstalledOrNot("us.zoom.videomeetings");
//
//        if (isAppInstalled) {
////            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=6583554590" + "&pwd=ZGhSZytWUmtMNDJWdmFxdS8rN09SQT09"));
////            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=84240144502" + "&pwd="));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=" + meetingId));
//            if (intent.resolveActivity(TeacherLiveRecordedClasses.this.getPackageManager()) != null) {
//                startActivity(intent);
//            }
//        } else {
////            Toast.makeText(TeacherLiveRecordedClasses.this, "Please Install Zoom App", Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherLiveRecordedClasses.this);
//            ViewGroup viewGroup = TeacherLiveRecordedClasses.this.findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(TeacherLiveRecordedClasses.this).inflate(R.layout.cv_zoom, viewGroup, false);
//            builder.setView(dialogView);
//            AlertDialog alertDialog = builder.create();
//            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
//            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
//            alertDialog.getWindow().setLayout(width, height);
//            alertDialog.show();
//
//            dialogView.findViewById(R.id.tv_install).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=us.zoom.videomeetings"));
//                    startActivity(intent);
//                    alertDialog.dismiss();
//
//                }
//            });
//
//            dialogView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//        }
//    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = TeacherLiveRecordedClasses.this.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
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
        for (CountDownTimer t :listTimers){
            t.cancel();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (toggle){
            ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
            toggle = false;
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

    //attendance for staff live classes
    private void startStaffLiveClass(String liveStreamUrl, String liveStreamId) {
        utils.showLoader(TeacherLiveRecordedClasses.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("liveStreamId", liveStreamId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        utils.showLog(TAG, "URL - " + AppUrls.startStaffLiveClass);
        utils.showLog(TAG, jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(AppUrls.startStaffLiveClass)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
                runOnUiThread(() ->
                        new AlertDialog.Builder(TeacherLiveRecordedClasses.this)
                                .setMessage(getString(R.string.try_again))
                                .setTitle(getResources().getString(R.string.app_name))
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        (dialog, id) -> {
                                            dialog.dismiss();
                                            startStaffLiveClass(liveJoinUrl, liveStreamId);
                                        })
                                .show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> utils.dismissDialog());
                ResponseBody responseBody = response.body();
                if (response.body() != null) {
                    String resp = responseBody.string();
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> launchZoomUrl(liveStreamUrl));
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)) { //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, TeacherLiveRecordedClasses.this, message, sh_Pref);
                            });
                        } else {
                            runOnUiThread(() ->
                             new AlertDialog.Builder(TeacherLiveRecordedClasses.this)
                                     .setMessage(getString(R.string.try_again))
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            (dialog, id) -> {
                                                dialog.dismiss();
                                                startStaffLiveClass(liveJoinUrl, liveStreamId);
                                            })
                                     .show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}