/*
 * *
 *  * Created by SriRamaMurthy A on 16/9/19 2:15 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 16/9/19 12:35 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
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
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.ParentObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
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
public class NewLeaveRequest extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + NewLeaveRequest.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.et_fromdate)
    EditText fromdate;
    @BindView(R.id.et_todate)
    EditText todate;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.et_comment)
    EditText etComment;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    String fromReq = "", toReq = "";

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    ParentObj pObj;
    StudentObj sObj;
    TeacherObj teacherObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_new_leave_request);
        ButterKnife.bind(this);


        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        List<String> reasons = new ArrayList<String>();
        reasons.add("Health Issues");
        reasons.add("Vacation");
        reasons.add("Others");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reasons);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, NewLeaveRequest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
//                getLeaveRequest();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {

            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            teacherObj = gson.fromJson(json, TeacherObj.class);

        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {

        } else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("parentObj", "");
            pObj = gson.fromJson(json, ParentObj.class);
            json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }

        todate.setEnabled(false);


    }

    @OnClick({R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                String comment = "";
                if (!etComment.getText().toString().equalsIgnoreCase("")) {
                    comment = etComment.getText().toString();
                }

                if (NetworkConnectivity.isConnected(NewLeaveRequest.this)) {
                    if (validate()) {
                        postLeave(fromReq, toReq, spinner.getSelectedItem().toString(), comment);
                    } else {
                       utils.alertDialog(3, NewLeaveRequest.this, "Error", "All fields are Required, \nToDate should be greater than from date.",
                                "", getString(R.string.action_close),true);
                    }
                } else {
                    utils.alertDialog(1, NewLeaveRequest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close),true);
                }

                break;
        }
    }


    private boolean validate() {
        boolean valid = false;
        if (!fromdate.getText().toString().trim().isEmpty() && !todate.getText().toString().trim().isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date from = null;
            Date to = null;
            try {
                from = format.parse(fromdate.getText().toString().trim());
                to = format.parse(todate.getText().toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (to.equals(from) || to.after(from)) {
                valid = true;
            } else {
                valid = false;
                todate.getText().clear();
            }
        }

        if (!etComment.getText().toString().trim().isEmpty() || !etComment.getText().toString().equalsIgnoreCase("")) {
//            valid = true;
        } else {
            valid = false;
        }

        Log.v(TAG, "time - " + valid);
        Log.v(TAG, "time - " + etComment.getText().toString().trim());

        return valid;
    }



    public void callDatePicker(final View view) {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        EditText et = (EditText) view;
                        et.setText(day + "/" + (month + 1) + "/" + year);
                        if (view.getId() == R.id.et_fromdate) {
                            fromReq = year + "-" + (month + 1) + "-" + day;
                            todate.setEnabled(true);
                        } else {
                            toReq = year + "-" + (month + 1) + "-" + day;
                        }
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void postLeave(String ... strings) {
        utils.showLoader(NewLeaveRequest.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        String URL = "";

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            URL = AppUrls.AddStaffLeaveRequests;
            try {
                jsonObject.put("Description", strings[3]);
                jsonObject.put("branchId", "" + teacherObj.getBranchId());
                jsonObject.put("createdBy", "" + teacherObj.getUserId());
                jsonObject.put("isActive", "1");
                jsonObject.put("leaveFrom", strings[0]);
                jsonObject.put("leaveTo", strings[1]);
                jsonObject.put("reason", strings[2]);
                jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
                jsonObject.put("userId", "" + teacherObj.getUserId());
            } catch (Exception e) {

            }

        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {

        } else {
            URL = AppUrls.PostAddStudentsLeaveRequest;

            try {
                jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
                jsonObject.put("createdBy", pObj.getUserId());
                jsonObject.put("branchId", sObj.getBranchId());
                jsonObject.put("studentId", sObj.getStudentId());
                jsonObject.put("leaveFrom", strings[0]);
                jsonObject.put("leaveTo", strings[1]);
                jsonObject.put("reason", strings[2]);
                jsonObject.put("Description", strings[3]);
                jsonObject.put("isActive", "1");

            } catch (Exception e) {

            }
        }


        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Log.v(TAG, "URL " + URL);
        Log.v(TAG, "Body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
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
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Response - " + jsonResp);
                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewLeaveRequest.this, "Leave Request Suceessfully Submited", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        finish();
                    }
                });
            }
        });

    }

}
