package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.LiveVideo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LiveDetails extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + LiveDetails.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    
    @BindView(R.id.tv_live_name)
    TextView tvLive;
    @BindView(R.id.tv_sub_name)
    TextView tvSubName;
    @BindView(R.id.tv_topic_name)
    TextView tvTopicName;
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;

    LiveVideo liveClass = null;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_live_details);

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
            utils.alertDialog(1, LiveDetails.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();

            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }
    
    void init(){
        liveClass = (LiveVideo) getIntent().getSerializableExtra("live");

        sh_Pref = LiveDetails.this.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        
//        tvSubName.setText(liveClass.getSubjectName());
        tvLive.setText(liveClass.getLiveStreamName());
        try {
            tvStartDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getLiveStreamStartTime())));
            tvStartTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getLiveStreamStartTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        findViewById(R.id.tv_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String[] val = (liveClass.getJoinUrl()).split("j/");
//                launchZoomUrl(val[1].replace("?", "&"));
                studentLiveAttendance(""+sObj.getStudentId(),""+liveClass.getLiveStreamId(),
                        ""+sObj.getClassCourseSectionId(),""+sObj.getClassId(),""+sObj.getCourseId(),liveClass.getJoinUrl(),
                        liveClass.getMeetingId(), liveClass.getMeetingPwd());

            }
        });
    }

    private void launchZoomUrl(String replace, String meetingId, String meetingPassword) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://narayanagroup.zoom.us/j/6583554590?pwd="));

        boolean isAppInstalled = appInstalledOrNot("us.zoom.videomeetings");

        if (isAppInstalled) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=6583554590" + "&pwd=ZGhSZytWUmtMNDJWdmFxdS8rN09SQT09"));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=84240144502" + "&pwd="));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=" + meetingId+"&uname="+sObj.getStudentRollnumber()+"("+sObj.getStudentName()+")"));
            if (intent.resolveActivity(LiveDetails.this.getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
//            Toast.makeText(LiveDetails.this, "Please Install Zoom App", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(LiveDetails.this);
            ViewGroup viewGroup = LiveDetails.this.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(LiveDetails.this).inflate(R.layout.cv_zoom, viewGroup, false);
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

       /* Intent zoomActivity = new Intent(this, ZoomActivity.class);
        zoomActivity.putExtra(AppConst.MEETING_ID, meetingId);
        zoomActivity.putExtra(AppConst.DISPLAY_NAME, sObj.getSectionName());
        zoomActivity.putExtra(AppConst.MEETING_USER_ID, sObj.getStudentId());
        if(meetingPassword != null)
            zoomActivity.putExtra(AppConst.MEETING_PASSWORD, meetingPassword);
        startActivity(zoomActivity);*/
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = LiveDetails.this.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    void studentLiveAttendance(String... params){
        utils.showLoader(LiveDetails.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray jsonArray = new JSONArray();

        JSONObject job = new JSONObject();
        try {
            job.put("studentId",params[0]);
            job.put("liveStreamId",params[1]);
            job.put("sectionId",params[2]);
            job.put("classId",params[3]);
            job.put("courseId",params[4]);
            job.put("createdBy",params[0]);

            jsonArray.put(job);

        }catch(Exception e){

        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("insertRecords", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String joinUrl = params[5];
        String meetingId = params[6];
        String meetingPassword = params[7];
        utils.showLog(TAG, "URL - " + AppUrls.AddStudentLiveClassAttendance);
        utils.showLog(TAG, "Obj - " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.AddStudentLiveClassAttendance)
                .post(body)
                .build();

        utils.showLog(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "responseBody - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            String[] val = joinUrl.split("j/");
                            runOnUiThread(() -> {
                                launchZoomUrl(val[1].replace("?", "&"), meetingId, meetingPassword);
                            });


                        }
                    }catch (Exception e){

                    }
                }

                runOnUiThread(() -> utils.dismissDialog());
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}