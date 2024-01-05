package myschoolapp.com.gsnedutech.OnlIneTest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.JEETestMarksDivision;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OTStudentTestActivity extends AppCompatActivity {

    private static final String TAG = OTStudentTestActivity.class.getName();
    MyUtils utils = new MyUtils();
    ApiClient apiClient = new ApiClient();

    @BindView(R.id.tv_notavailable)
    TextView tvNotAvailable;
    @BindView(R.id.rv_test_list)
    RecyclerView rvTestList;
    @BindView(R.id.ll_history)
    LinearLayout llHistory;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipe;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    OTLoginResult studentObjNew;
    OTLiveExamsAdapter adapter;

    boolean doubleBackToExitPressedOnce = false;
    ArrayList<LiveExams> examDetails = new ArrayList<>();
    String startTime;
    String duration;
    String _id = "";
    String eStatus = "SCHEDULED";
    int count = 0;
    String nowdt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_student_test);
        ButterKnife.bind(this);
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        toEdit.apply();
        Gson gson = new Gson();
        String json = sh_Pref.getString("otStudentObj", "");
        studentObjNew = gson.fromJson(json, OTLoginResult.class);
        if (studentObjNew.getLiveExams() != null)
            examDetails = studentObjNew.getLiveExams();
        swipe.setOnRefreshListener(() -> {
            count = count + 1;
            refresh();
        });
        llHistory.setOnClickListener(view -> startActivity(new Intent(OTStudentTestActivity.this, OTStudentCompletedTest.class)));
    }

    // Logout From OT Platform
    @OnClick(R.id.img_power)
    public void onViewClicked() {
        MyUtils.userLogOut(toEdit, OTStudentTestActivity.this, sh_Pref);
        int intro = Integer.parseInt(sh_Pref.getString("intro", "0"));
        toEdit.putString("intro", "" + intro++);
        toEdit.commit();
    }

    // Initializing LiveExams if available
    void init() {
        if (count == 1) {
            if (examDetails.size() > 0) {
                rvTestList.setVisibility(View.VISIBLE);
                tvNotAvailable.setVisibility(View.GONE);
                for (int i = 0; i < examDetails.size(); i++) {
                    if (examDetails.get(i).getrDate() == null) {
                        Date d = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(i).geteTime());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(d);
                        cal.add(Calendar.HOUR_OF_DAY, 24);
                        Date date = cal.getTime();
                        Log.v(TAG, "Result Date - " + utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", date));
                        examDetails.get(i).setrDate(utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", date));
                    }
                }
                rvTestList.setLayoutManager(new LinearLayoutManager(this));
                Collections.reverse(examDetails);
                adapter = new OTLiveExamsAdapter();
                rvTestList.setAdapter(adapter);
            } else {
                getLiveExams();
            }
        } else {
            getLiveExams();
        }
    }


    private void getLiveExams() {

        Log.v("TAG", "TestGetQue Url - " + AppUrls.GETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentObjNew.getMStudentId()+"&sectionId="+studentObjNew.getmSectionId()+"&scheduledFlag=SCHEDULED");

        Request request = apiClient.getRequest(AppUrls.GETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "")
                + "&studentId=" + studentObjNew.getMStudentId()+"&sectionId="+studentObjNew.getmSectionId()+"&scheduledFlag=SCHEDULED", sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    rvTestList.setVisibility(View.GONE);
                    tvNotAvailable.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {

                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("status").equalsIgnoreCase("200")) {
//                            JSONObject jsonobj = ParentjObject.getJSONObject("result");
                            JSONArray jsonArr = parentjObject.getJSONArray("result");
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<LiveExams>>() {
                            }.getType();
                            examDetails.clear();
                            examDetails.addAll(gson.fromJson(jsonArr.toString(), type));
                            if (examDetails.size() > 0) {
                                runOnUiThread(() -> {
                                    rvTestList.setVisibility(View.VISIBLE);
                                    tvNotAvailable.setVisibility(View.GONE);
                                    rvTestList.setLayoutManager(new LinearLayoutManager(OTStudentTestActivity.this));
                                    Collections.reverse(examDetails);

                                    // TODO: 04-01-2021 if result date is null app crashes use this

                                    for (int i = 0; i < examDetails.size(); i++) {
                                        if (examDetails.get(i).getrDate() == null) {
                                            Date d = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(i).geteTime());
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(d);
                                            cal.add(Calendar.HOUR_OF_DAY, 24);
                                            Date date = cal.getTime();
                                            Log.v(TAG, "Result Date - " + utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", date));
                                            examDetails.get(i).setrDate(utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", date));
                                        }
                                    }

                                    adapter = new OTLiveExamsAdapter();
                                    rvTestList.setAdapter(adapter);

//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            init();
//                                        }
//                                    });
                                });
                            } else {
                                runOnUiThread(() -> {
                                    rvTestList.setVisibility(View.GONE);
                                    tvNotAvailable.setVisibility(View.VISIBLE);
                                });
                            }

                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, OTStudentTestActivity.this, message, sh_Pref);
                            });
                        }else if (parentjObject.getString("status").equalsIgnoreCase("204")) {
                            runOnUiThread(() -> {
                                rvTestList.setVisibility(View.GONE);
                                tvNotAvailable.setVisibility(View.VISIBLE);
                            });

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        runOnUiThread(() -> {
                            rvTestList.setVisibility(View.GONE);
                            tvNotAvailable.setVisibility(View.VISIBLE);
                        });

                    }
                }
            }
        });
    }

    // refreshing the whole screen
    void refresh() {
        swipe.setRefreshing(false);
        if (NetworkConnectivity.isConnected(OTStudentTestActivity.this)) {
            getServerTime();
        } else {
            new MyUtils().alertDialog(1, OTStudentTestActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = count + 1;
        refresh();

    }

    // Checking and Using Server System Time
    private void getServerTimeStatus(int position) {
        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        Log.v("TAG", "TestGetQue Url - " + AppUrls.OT_URL + "systemTime");
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = apiClient.postRequest(AppUrls.OT_URL + "systemTime", body, sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("status").equalsIgnoreCase("200")) {
                            Log.v(TAG, "Date - " + parentjObject.getString("DateTime"));
                            Log.v(TAG, "" + examDetails.get(0).getsTime());

                            runOnUiThread(() -> {
                                try {
                                    initialNavigate(examDetails.get(position), parentjObject.getString("DateTime"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Checking Last Modified Time
    private void getModifiedTime(int position) {
        ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        Request request = apiClient.getRequest(AppUrls.GetExamLastModifiedDate + "?schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&examId=" + examDetails.get(position).getMyExamId() + "&studentId=" + studentObjNew.getMStudentId(), sh_Pref);

        Log.v(TAG, "url " + AppUrls.GetExamLastModifiedDate + "?schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&examId=" + examDetails.get(position).getMyExamId() + "&studentId=" + studentObjNew.getMStudentId());

        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                loading.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("status").equalsIgnoreCase("200") && parentjObject.getJSONObject("result") != null
                                && parentjObject.getJSONObject("result").has("lastModifiedDate")) {


                            JSONObject result = parentjObject.getJSONObject("result");

                            Gson gson = new Gson();
                            String confJson = sh_Pref.getString("config", "");
                            Config config = gson.fromJson(confJson, Config.class);

                            int delay = config.getDbSync() + 120;

                            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date lastModified = dtf.parse(result.getString("lastModifiedDate"));
                                Date current = dtf.parse(result.getString("currentDate"));

                                long diff = current.getTime() - lastModified.getTime();


                                if ((TimeUnit.MILLISECONDS.toSeconds(diff)) >= delay) {
                                    runOnUiThread(() -> {
                                        try {
                                            initialNavigate(examDetails.get(position), result.getString("currentDate"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } else {
                                    runOnUiThread(() -> new AlertDialog.Builder(OTStudentTestActivity.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Your last session has abruptly stopped. Please wait for " + ((delay - TimeUnit.MILLISECONDS.toMinutes(diff)) / 60) + " mins.")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setCancelable(true)
                                            .show());
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, OTStudentTestActivity.this, message, sh_Pref);
                            });
                        } else {
                            runOnUiThread(() -> getServerTimeStatus(position));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    // Get Student Test Status By TestId
    private void getStudentStatus(int position) {
        ProgressDialog loading = new ProgressDialog(OTStudentTestActivity.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        Log.v("TAG", "TestGetQue Url - " + AppUrls.GETSTUDENTEXAMBYID + "?studentId=" + studentObjNew.getMStudentId() + "&examId=" + examDetails.get(position).getMyExamId() + "&schemaName=" + sh_Pref.getString("schema", ""));

        Request request = apiClient.getRequest(AppUrls.GETSTUDENTEXAMBYID + "?studentId=" + studentObjNew.getMStudentId()
                + "&examId=" + examDetails.get(position).getMyExamId() + "&schemaName=" + sh_Pref.getString("schema", ""), sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                runOnUiThread(() -> {
                    swipe.setRefreshing(false);
                    showRetrivingErrorDialogue();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {

                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("status").equalsIgnoreCase("200")) {
                            if (parentjObject.has("result")) {
                                JSONObject resultObj = parentjObject.getJSONObject("result");
                                if (resultObj.has("eStatus")) {
                                    eStatus = resultObj.getString("eStatus");
                                }

                                if (resultObj.has("_id")) {
                                    _id = resultObj.getString("_id");
                                }
                            }


                            runOnUiThread(() -> {
                                swipe.setRefreshing(false);
                                getModifiedTime(position);
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, OTStudentTestActivity.this, message, sh_Pref);
                            });
                        }else {
                            runOnUiThread(() -> {
                                swipe.setRefreshing(false);
                                getModifiedTime(position);
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        runOnUiThread(() -> {
                            swipe.setRefreshing(false);
                            showRetrivingErrorDialogue();
                        });
                    }
                }
            }
        });
    }

    private void showRetrivingErrorDialogue() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Error While Retriving the Exam Details\n Please try again")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // Navigate to Diff activities based on Test Status
    private void initialNavigate(LiveExams liveExams, String dateTime) {

        Log.v(TAG, "" + liveExams.getsTime());
        Date startDate = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", liveExams.getsTime());
        Date endDate = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", liveExams.geteTime());
        Date resultDate = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", liveExams.getrDate());
        Date now = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", dateTime);
        if (now.after(startDate) && now.before(endDate)) {
            duration = getDuration(utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", now), liveExams.geteTime());

            Log.v(TAG, "duration - " + duration);

            if (eStatus.equalsIgnoreCase("SCHEDULED") ||
                    eStatus.equalsIgnoreCase("INPROGRESS") ||
                    eStatus.equalsIgnoreCase("SUBMITTED")) {

                Intent onlineTestIntent;
                if (!liveExams.getJeeSectionTemplate().equalsIgnoreCase("NA")) {
                    onlineTestIntent = new Intent(OTStudentTestActivity.this, JEETestMarksDivision.class);
                } else {
                    onlineTestIntent = new Intent(OTStudentTestActivity.this, OTStudentOnlineTestActivity.class);
                }
                onlineTestIntent.putExtra("live", liveExams);
                onlineTestIntent.putExtra("studentId", studentObjNew.getMStudentId());
                onlineTestIntent.putExtra("testId", liveExams.getMyExamId());
                onlineTestIntent.putExtra("testName", liveExams.geteName());
                onlineTestIntent.putExtra("examSTime", liveExams.getsTime());
                onlineTestIntent.putExtra("examETime", liveExams.geteTime());
                onlineTestIntent.putExtra("examRTime", liveExams.getrDate());
                onlineTestIntent.putExtra("testTime", duration);
                onlineTestIntent.putExtra("eDuration", liveExams.geteDuration());
                onlineTestIntent.putExtra("correctMarks", liveExams.getcMarks());
                onlineTestIntent.putExtra("wrongMarks", liveExams.getwMarks());
                onlineTestIntent.putExtra("testCategory", liveExams.eCatId);
                onlineTestIntent.putExtra("examdet_Id", _id);
                Date now1 = new Date();
                if (liveExams.geteStatus().equalsIgnoreCase("SCHEDULED"))
                    onlineTestIntent.putExtra("startTime", utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", now1));
                else onlineTestIntent.putExtra("startTime", startTime);
                onlineTestIntent.putExtra("jeeSectionTemplate", liveExams.getJeeSectionTemplate());
                onlineTestIntent.putExtra("testFilePath", liveExams.getePath());
                onlineTestIntent.putExtra("studentTestFilePath", "NA");
                if (liveExams.geteStatus().equalsIgnoreCase("INPROGRESS"))
                    onlineTestIntent.putExtra("studentTestFilePath", "INPROGRESS");
                if (liveExams.geteStatus().equalsIgnoreCase("SUBMITTED"))
                    onlineTestIntent.putExtra("studentTestFilePath", "SUBMITTED");
                startActivity(onlineTestIntent);
            }
        } else if (now.after(endDate) && eStatus.equalsIgnoreCase("SCHEDULED")) {
            new AlertDialog.Builder(OTStudentTestActivity.this)
                    .setTitle("Sorry..!")
                    .setMessage("Sorry you missed the Exam...!")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        } else if (now.after(endDate) && eStatus.equalsIgnoreCase("INPROGRESS")) {
            new AlertDialog.Builder(OTStudentTestActivity.this)
                    .setTitle("Plase wait...!")
                    .setMessage("Results will be released after \n" + liveExams.getrDate())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();

        } else if (now.after(endDate) && now.after(resultDate) && eStatus.equalsIgnoreCase("COMPLETED")) {
            Intent onlineJeeTestIntent = new Intent(OTStudentTestActivity.this, OTStudentOnlineTestResult.class);
            onlineJeeTestIntent.putExtra("studentId", studentObjNew.getMStudentId());
            onlineJeeTestIntent.putExtra("studentTestFilePath", _id);
            onlineJeeTestIntent.putExtra("testFilePath", liveExams.getePath());
            onlineJeeTestIntent.putExtra("testId", liveExams.getMyExamId());
            onlineJeeTestIntent.putExtra("testName", liveExams.geteName());
            onlineJeeTestIntent.putExtra("jeeSectionTemplate", "NA");
            startActivity(onlineJeeTestIntent);
        } else {
            if (eStatus.equalsIgnoreCase("SUBMITTED") || eStatus.equalsIgnoreCase("COMPLETED")) {
                new AlertDialog.Builder(OTStudentTestActivity.this)
                        .setTitle("Plase wait...!")
                        .setMessage("Results will be released after \n" + liveExams.getrDate())
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            } else {
                String sdate = utils.getStringFromDate("hh:mm aa dd MMM yyyy", utils.getDateFromString("yyyy-MM-dd HH:mm:ss", liveExams.getsTime()));
                new AlertDialog.Builder(OTStudentTestActivity.this)
                        .setTitle("Plase wait...!")
                        .setMessage("Exam Scheduled at \n" + sdate)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            }
        }
    }

    // calculating duration between two Times
    private String getDuration(String dateTime, String testEndDate) {
        String duration;
        SimpleDateFormat serverSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startDate = null;
        Date endDate = null;
        Date currentDate = null;
        try {
            endDate = serverSdf.parse(testEndDate);
            currentDate = serverSdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        duration = "" + (endDate.getTime() - currentDate.getTime());
        return duration;
    }

    // Get Server Time
    private void getServerTime() {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        Log.v("TAG", "TestGetQue Url - " + AppUrls.OT_URL + "systemTime");
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = apiClient.postRequest(AppUrls.OT_URL + "systemTime", body, sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            Log.v(TAG, "Date - " + ParentjObject.getString("DateTime"));
                            nowdt = ParentjObject.getString("DateTime");
                            runOnUiThread(() -> init());
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
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    // Showing Exam Details in Dialog
    private void startExam(LiveExams liveExams, int position) {
        Dialog dialog = new Dialog(OTStudentTestActivity.this);
        dialog.setContentView(R.layout.fragment_live_exam_deatils);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        dialog.findViewById(R.id.ll_header).setVisibility(View.VISIBLE);
        ((TextView) dialog.findViewById(R.id.tv_test_name)).setText(liveExams.geteName());
        ((TextView) dialog.findViewById(R.id.tv_test_category)).setText(liveExams.geteCatName());
        ((TextView) dialog.findViewById(R.id.tv_start_date)).setText(utils.getStringFromDate("dd MMM, yyyy", utils.getDateFromString("yyyy-MM-dd HH:mm:ss", liveExams.getsTime())));
        ((TextView) dialog.findViewById(R.id.tv_start_time)).setText(utils.getStringFromDate("hh:mm a", utils.getDateFromString("yyyy-MM-dd HH:mm:ss", liveExams.getsTime())));
        dialog.findViewById(R.id.btn_join).setOnClickListener(view -> {
            getStudentStatus(position);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.iv_back).setOnClickListener(view -> dialog.dismiss());
    }

    // Adapter for LiveExams RecyclerView
    private class OTLiveExamsAdapter extends RecyclerView.Adapter<OTLiveExamsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(OTStudentTestActivity.this).inflate(R.layout.item_upcoming_ot_tests_new, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.tvSubject.setText(examDetails.get(position).geteName());
            holder.tvTestName.setVisibility(View.INVISIBLE);
            holder.tvDate.setText(utils.getStringFromDate("dd MMM, yyyy", utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(position).getsTime())));
            holder.tvTime.setText(utils.getStringFromDate("hh:mm a", utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(position).getsTime())));
            holder.tvTestStatus.setText(examDetails.get(position).geteStatus());
            holder.tvStartTest.setVisibility(View.VISIBLE);
            Date now = new Date();
            Log.v(TAG, "" + examDetails.get(position).getsTime());
            Date startDate = null;
            Date endDate = null;
            Date resultDate = null;
            startDate = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(position).getsTime());
            endDate = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(position).geteTime());
            resultDate = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", examDetails.get(position).getrDate());
            now = utils.getDateFromString("yyyy-MM-dd HH:mm:ss", nowdt);

            if (holder.tvTestStatus.getText().toString().equalsIgnoreCase("COMPLETED")) {
                holder.tvStartTest.setText("View Results");
            } else if (now.after(startDate) && now.before(endDate)) {
                duration = getDuration(utils.getStringFromDate("yyyy-MM-dd HH:mm:ss", now), examDetails.get(position).geteTime());
                if (holder.tvTestStatus.getText().toString().equalsIgnoreCase("SCHEDULED")) {
                    holder.tvStartTest.setText("Start Test");
                } else if (holder.tvTestStatus.getText().toString().equalsIgnoreCase("INPROGRESS") ||
                        holder.tvTestStatus.getText().toString().equalsIgnoreCase("SUBMITTED")) {
                    holder.tvStartTest.setText("Resume Test");
                }
            } else if (now.after(endDate) && now.after(resultDate) && holder.tvTestStatus.getText().toString().equalsIgnoreCase("SUBMITTED")) {
                runOnUiThread(() -> holder.tvStartTest.setText("View Results"));


            } else if (now.after(endDate) && holder.tvTestStatus.getText().toString().equalsIgnoreCase("SCHEDULED")) {
                runOnUiThread(() -> holder.tvStartTest.setText("Missed"));


            } else if (now.after(endDate) && (holder.tvTestStatus.getText().toString().equalsIgnoreCase("INPROGRESS") || holder.tvTestStatus.getText().toString().equalsIgnoreCase("SUBMITTED"))) {
                runOnUiThread(() -> {
                    holder.tvTestStatus.setText("SUBMITTED");
                    holder.tvStartTest.setText("View Results");
                });

            }
            holder.tvStartTest.setOnClickListener(view -> {
                if (holder.tvStartTest.getText().toString().equalsIgnoreCase("Start Test")) {
                    startExam(examDetails.get(position), position);
                } else if (!holder.tvStartTest.getText().toString().equalsIgnoreCase("Missed"))
                    getStudentStatus(position);
                else
                    Toast.makeText(OTStudentTestActivity.this, "You Missed the Exam", Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public int getItemCount() {
            return examDetails.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate, tvSubject, tvTestName, tvTime, tvTestStatus, tvStartTest;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvSubject = itemView.findViewById(R.id.tv_sub);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvTime = itemView.findViewById(R.id.tv_start_time);
                tvTestStatus = itemView.findViewById(R.id.tv_status);
                tvStartTest = itemView.findViewById(R.id.tv_start_test);
            }
        }
    }
}