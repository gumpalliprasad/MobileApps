/*
 * *
 *  * Created by SriRamaMurthy A on 24/10/19 6:07 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 23/10/19 3:02 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentProfileObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class StudentProfileInDetail extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + StudentProfileInDetail.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_student_id)
    TextView tvStudentId;

    @BindView(R.id.tv_email)
    TextView tvEmail;

    @BindView(R.id.tv_mob_no)
    TextView tvContact;

    @BindView(R.id.img_profile)
    ImageView profImgPicBackground;

    @BindView(R.id.tv_hw)
    TextView tvHw;

    @BindView(R.id.tv_performance)
    TextView tvPerformance;

    @BindView(R.id.tv_attendance)
    TextView tvAttendance;

    String studentId, studentName;
    StudentProfileObj studentProfileObj;

    SharedPreferences sh_Pref;
    StudentObj studentObj;
    SharedPreferences.Editor toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_student_profile_teacher_view);
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
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, StudentProfileInDetail.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getStudentProfile(studentId);
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        studentObj = gson.fromJson(json, StudentObj.class);

        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");

        tvAttendance.setOnClickListener(this);
        tvHw.setOnClickListener(this);
        tvPerformance.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_performance:
                Intent perIntent = new Intent(StudentProfileInDetail.this, ReportActivity.class);
                perIntent.putExtra("studentId", studentId);
                perIntent.putExtra("courseName", getIntent().getStringExtra("courseName"));
                perIntent.putExtra("branchId", getIntent().getStringExtra("branchId"));
                perIntent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                perIntent.putExtra("classId", getIntent().getStringExtra("classId"));
                perIntent.putExtra("className", getIntent().getStringExtra("className"));
                perIntent.putExtra("studentProfilePic", getIntent().getStringExtra("studentProfilePic"));
                perIntent.putExtra("rollNumber", getIntent().getStringExtra("rollNumber"));
                perIntent.putExtra("sectionId", getIntent().getStringExtra("sectionId"));
                startActivity(perIntent);
                break;
            case R.id.tv_hw:
                Intent dairyIntent = new Intent(StudentProfileInDetail.this, Assignments.class);
                dairyIntent.putExtra("studentId", studentId);
                dairyIntent.putExtra("branchId", studentId);
                dairyIntent.putExtra("sectionId", studentId);
                startActivity(dairyIntent);
                break;
            case R.id.tv_attendance:
                Intent attIntent = new Intent(StudentProfileInDetail.this, StudentAttendance.class);
                attIntent.putExtra("studentId", studentId);
                attIntent.putExtra("studentName", studentName);
                startActivity(attIntent);
                break;
        }
    }


    void getStudentProfile(String... strings){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Attendance Student request - " + new AppUrls().GetstudentDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&studentId=" + strings[0]);
        Request request = new Request.Builder()
                .url(new AppUrls().GetstudentDetailsById +"schemaName=" +sh_Pref.getString("schema","")+ "&studentId=" + strings[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "Details - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            Gson gson = new Gson();

                            runOnUiThread(() -> {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                appbarLayout.setVisibility(View.VISIBLE);
//                                llMain.setVisibility(View.VISIBLE);
                                    }
                                }, 2000);
                                studentProfileObj = gson.fromJson(ParentjObject.toString(), StudentProfileObj.class);
                                tvName.setText(studentProfileObj.getStudentName());
                                tvStudentId.setText(studentProfileObj.getLoginName());
                                tvContact.setText(studentProfileObj.getContactNumber());
                                tvEmail.setText(studentProfileObj.getStudentEmail());


//                        Picasso.with(StudentProfileInDetail.this).load("http://15.206.127.17:9000/home/ubuntu/EA21Nov/Activities/1/2019-11-23/_image")
//                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);

                                Picasso.with(StudentProfileInDetail.this).load(new AppUrls().GetstudentProfilePic + studentProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
                            });


                            if (sh_Pref.getBoolean("student_loggedin",false)){
                                studentObj.setProfilePic(studentProfileObj.getProfilePic());
                                String json = gson.toJson(studentObj);
                                toEdit.putString("studentObj", json);
                                toEdit.commit();
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}
