package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class TeacherClassesViewAll extends AppCompatActivity {

    private static final String TAG = "SriRam -" + TeacherClassesViewAll.class.getName();

    @BindView(R.id.rv_classes) 
    RecyclerView rvClasses;
    
    SharedPreferences sh_Pref;
    TeacherObj tObj;

    String serverTime = "";

    List<TeacherLiveClassObj> videoSessionObjList = new ArrayList<>();

    MyUtils utils = new MyUtils();


    List<TeacherLiveClassObj> classes = new ArrayList<>();
    List<TeacherLiveClassObj> completed = new ArrayList<>();


    List<CountDownTimer> listTimers = new ArrayList<>();
    
    String typeClass = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classes_view_all);
        ButterKnife.bind(this);
        
        init();
    }
    
    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        typeClass = getIntent().getStringExtra("type");

    }


    @Override
    public void onPause() {
        for (CountDownTimer t :listTimers){
            t.cancel();
        }
        super.onPause();
    }
    
    @Override
    public void onResume() {
        if (NetworkConnectivity.isConnected(this)) {
            getServerTime();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
        super.onResume();
    }


    private void getServerTime() {

        utils.showLoader(this);


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
                        if (NetworkConnectivity.isConnected(TeacherClassesViewAll.this)) {
                            getLiveClasses();

                        } else {
                            new MyUtils().alertDialog(1, TeacherClassesViewAll.this, getString(R.string.error_connect), getString(R.string.error_internet),
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

                            if (videoSessionObjList.size()>0) {
                                classes.clear();
                                completed.clear();
                            }

                            for (int i = 0; i < videoSessionObjList.size(); i++) {
                                try {
                                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(videoSessionObjList.get(i).getLiveStreamStartTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {

                                        classes.add(videoSessionObjList.get(i));
                                    } else {
                                        completed.add(videoSessionObjList.get(i));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    
                                    if (typeClass.equalsIgnoreCase("live")){
                                        rvClasses.setLayoutManager(new GridLayoutManager(TeacherClassesViewAll.this,2));
                                        rvClasses.setAdapter(new LiveAdapter(classes));
                                    }else{
                                        rvClasses.setLayoutManager(new GridLayoutManager(TeacherClassesViewAll.this,2));
                                        rvClasses.setAdapter(new LiveAdapter(completed));
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


    private void launchZoomUrl(String meetingId) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://narayanagroup.zoom.us/j/6583554590?pwd="));

        boolean isAppInstalled = appInstalledOrNot("us.zoom.videomeetings");

        if (isAppInstalled) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=6583554590" + "&pwd=ZGhSZytWUmtMNDJWdmFxdS8rN09SQT09"));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=84240144502" + "&pwd="));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=" + meetingId));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
//            Toast.makeText(mActivity, "Please Install Zoom App", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.cv_zoom, viewGroup, false);
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
        PackageManager pm = getPackageManager();
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

    class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

        List<TeacherLiveClassObj> listUpcoming;

        public LiveAdapter(List<TeacherLiveClassObj> listUpcoming) {
            this.listUpcoming = listUpcoming;
        }

        @NonNull
        @Override
        public LiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LiveAdapter.ViewHolder(LayoutInflater.from(TeacherClassesViewAll.this).inflate(R.layout.item_home_live_class, parent, false));
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.ivLive.getVisibility() == View.VISIBLE) {
                        String[] val = (listUpcoming.get(position).getJoinUrl()).split("j/");
                        launchZoomUrl(val[1].replace("?", "&"));
                    } else {
                        if (holder.tvTimeType.getText().toString().equalsIgnoreCase("Ended At")){
                            try {
                                new MyUtils().alertDialog(3, TeacherClassesViewAll.this, "Oops!", "Live Class Ended on " +
                                                new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime())),
                                        "Close", "", false);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                new MyUtils().alertDialog(3, TeacherClassesViewAll.this, "Oops!", "Live Class will Start at " +
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


}