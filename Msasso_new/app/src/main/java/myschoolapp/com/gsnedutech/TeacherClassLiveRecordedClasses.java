package myschoolapp.com.gsnedutech;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherLiveClassObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TeacherClassLiveRecordedClasses extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherClassLiveRecordedClasses.class.getName();

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

    @BindView(R.id.sp_subjects)
    Spinner spSubjects;

    public int currentMonth,currentYear, dayOfMonth;

    DatePickerDialog datePickerDialog;

    SharedPreferences sh_Pref;
    TeacherObj tObj;
    AdminObj adminObj;

    String serverTime = "",liveJoinUrl="";

    List<TeacherLiveClassObj> videoSessionObjList = new ArrayList<>();

    TeacherLiveClassObj liveClass = null;

    MyUtils utils = new MyUtils();


    List<TeacherLiveClassObj> classes = new ArrayList<>();
    List<TeacherLiveClassObj> completed = new ArrayList<>();


    List<CountDownTimer> listTimers = new ArrayList<>();
    String userId = "";
    String subjectId="", sectionId="";
    int pos =0;

    List<TeacherCCSSSubject> subjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_live_recorded_classes);
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
                String[] val = (liveJoinUrl).split("j/");
                launchZoomUrl(val[1].replace("?", "&"));
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
                datePickerDialog = new DatePickerDialog(TeacherClassLiveRecordedClasses.this,R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                currentMonth = month+1;
                                currentYear = year;
                                dayOfMonth = day;
                                String mon = "";
                                switch (month+1) {
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

                                tvMonthYear.setText(day +" "+mon +" "+ year);
                                if (NetworkConnectivity.isConnected(TeacherClassLiveRecordedClasses.this)) {
                                    try {
                                        Date selctdt = new SimpleDateFormat("dd MM yyyy").parse(day +" "+ (month+1) +" "+ year);
                                        Date today = new SimpleDateFormat("dd MM yyyy").parse(new SimpleDateFormat("dd MM yyyy").format(new Date()));
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
                                    utils.alertDialog(1, TeacherClassLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                            getString(R.string.action_settings), getString(R.string.action_close), false);
                                }

                            }
                        }, currentYear, currentMonth-1, dayOfMonth);

//                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();

            }
        });
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        tvMonthYear.setText(new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
        sectionId = getIntent().getStringExtra("sectionId");
        subjectId = getIntent().getStringExtra("subjectId");

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

        pos = Integer.parseInt(getIntent().getStringExtra("position"));
        subjectList.addAll((Collection<? extends TeacherCCSSSubject>) getIntent().getSerializableExtra("Subjects"));
        TeacherCCSSSubject ff = new TeacherCCSSSubject();
        ff.setSubjectName("telugu");
        ff.setSubjectId("6");
        subjectList.add(ff);


        ArrayAdapter<TeacherCCSSSubject> adapter =
                new ArrayAdapter<>(TeacherClassLiveRecordedClasses.this, android.R.layout.simple_spinner_dropdown_item, subjectList);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spSubjects.setAdapter(adapter);
        spSubjects.setSelection(getIntent().getIntExtra("pos",0));
        spSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                subjectId = subjectList.get(i).getSubjectId();
//                rvCourses.setLayoutManager(new LinearLayoutManager(mActivity));
//                rvCourses.setAdapter(new TeacherHomeFrag.CouseAdapter(teacherList.get(i).getClasses()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            utils.alertDialog(1, TeacherClassLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getServerTime();
            }
            isNetworkAvail = true;
        }
    }



    private void getServerTime() {

        utils.showLoader(TeacherClassLiveRecordedClasses.this);


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
                getLiveClasses();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    getLiveClasses();
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        if (NetworkConnectivity.isConnected(TeacherClassLiveRecordedClasses.this)) {
                            getLiveClasses();

                        } else {
                            new MyUtils().alertDialog(1, TeacherClassLiveRecordedClasses.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

        String filtetDate = currentYear+"-"+currentMonth+"-"+dayOfMonth;
        utils.showLog(TAG, "URL - " + new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId+"&filterDate="+filtetDate+"&sectionId="+sectionId+"&subjectId="+subjectId);

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeacherAssignedLiveClasses + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + userId+"&filterDate="+filtetDate+"&sectionId="+sectionId+"&subjectId="+subjectId)
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
                                        liveJoinUrl = liveClass.getJoinUrl();
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
                                        rvLiveClasses.setVisibility(View.VISIBLE);
                                        rvLiveClasses.setLayoutManager(new LinearLayoutManager(TeacherClassLiveRecordedClasses.this));
                                        rvLiveClasses.setAdapter(new LiveAdapter(classes));
                                        findViewById(R.id.cv_no_live_classes).setVisibility(View.GONE);
                                    }else {
                                        rvLiveClasses.setVisibility(View.GONE);
                                        findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (completed.size()>0) {
                                        rvRecordedClass.setVisibility(View.VISIBLE);
                                        rvRecordedClass.setLayoutManager(new LinearLayoutManager(TeacherClassLiveRecordedClasses.this));
                                        rvRecordedClass.setAdapter(new LiveAdapter(completed));
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherClassLiveRecordedClasses.this).inflate(R.layout.item_live_class, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.ivLive.getVisibility() == View.VISIBLE) {
                        String[] val = (listUpcoming.get(position).getJoinUrl()).split("j/");
                        launchZoomUrl(val[1].replace("?", "&"));
                    } else {
                        if (holder.tvTimeType.getText().toString().equalsIgnoreCase("Ended At")){
                            try {
                                new MyUtils().alertDialog(3, TeacherClassLiveRecordedClasses.this, "Oops!", "Live Class Ended on " +
                                                new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime())),
                                        "Close", "", false);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                new MyUtils().alertDialog(3, TeacherClassLiveRecordedClasses.this, "Oops!", "Live Class will Start at " +
                                                new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime())),
                                        "Close", "", false);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://narayanagroup.zoom.us/j/6583554590?pwd="));

        boolean isAppInstalled = appInstalledOrNot("us.zoom.videomeetings");

        if (isAppInstalled) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=6583554590" + "&pwd=ZGhSZytWUmtMNDJWdmFxdS8rN09SQT09"));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=84240144502" + "&pwd="));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=" + meetingId));
            if (intent.resolveActivity(TeacherClassLiveRecordedClasses.this.getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
//            Toast.makeText(TeacherLiveRecordedClasses.this, "Please Install Zoom App", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherClassLiveRecordedClasses.this);
            ViewGroup viewGroup = TeacherClassLiveRecordedClasses.this.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(TeacherClassLiveRecordedClasses.this).inflate(R.layout.cv_zoom, viewGroup, false);
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

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = TeacherClassLiveRecordedClasses.this.getPackageManager();
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
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}