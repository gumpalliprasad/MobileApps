package myschoolapp.com.gsnedutech.descriptive.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.LiveExamDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.OnlIneTest.Config;
import myschoolapp.com.gsnedutech.OnlIneTest.OTLoginResult;
import myschoolapp.com.gsnedutech.OnlIneTest.OTStudentOnlineTestResult;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentHome;
import myschoolapp.com.gsnedutech.StudentLiveExamsTabs;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.DescriptiveSectionDetails;
import myschoolapp.com.gsnedutech.descriptive.models.SectionArray;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class StudentOTDescriptiveExamsFrag extends Fragment implements StudentLiveExamsTabs.MonthChangeListner {


    private static final String TAG = "SriRam -" + StudentOTDescriptiveExamsFrag.class.getName();
    String serverTime = "";

    MyUtils utils = new MyUtils();

    View viewStudentLiveExamsFrag;
    Unbinder unbinder;

    String duration;

    String _id = "";
    String eStatus = "SCHEDULED";
    OTLoginResult otLoginResultObj;
    ArrayList<LiveExam> examDetails = new ArrayList<>();

    String startTime;

    @BindView(R.id.tv_test_name)
    TextView tvTestName;
    @BindView(R.id.tv_test_type)
    TextView tvTestType;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_duration)
    TextView tvDuration;

    @BindView(R.id.rv_upcoming_exams)
    RecyclerView rvUpcomingExams;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "";

    Activity mActivity;
    OTLoginResult studentObjNew;

    StudentOnlineTestObj liveExam = null;

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();

    List<StudentOnlineTestObj> listTest = new ArrayList<>();

//    String currentMonth = "";

    HashMap<String, List<StudentOnlineTestObj>> filterList = new HashMap<>();

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }


    public StudentOTDescriptiveExamsFrag() {
        // Required empty public constructor
    }

    public static StudentOTDescriptiveExamsFrag newInstance(String monthYear) {

        Bundle args = new Bundle();
        args.putString("monthYear", monthYear);

        StudentOTDescriptiveExamsFrag fragment = new StudentOTDescriptiveExamsFrag();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
//            currentMonth = getArguments().getString("monthYear");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentLiveExamsFrag = inflater.inflate(R.layout.fragment_student_ot_live_exams, container, false);
        unbinder = ButterKnife.bind(this, viewStudentLiveExamsFrag);

        init();

        return viewStudentLiveExamsFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        viewStudentLiveExamsFrag.findViewById(R.id.cv_start_exam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(mActivity, LiveExamDetails.class);
                    intent.putExtra("live", (Serializable) liveExam);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });


        rvUpcomingExams.setLayoutManager(new LinearLayoutManager(mActivity));


        new LoginOnlineTest().execute();
    }

    private class LoginOnlineTest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utils.showLoader(mActivity);
        }
        @Override
        protected String doInBackground(String... params) {
            String jsonResp = "null";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
//                {"admissionNo":"A1","password":"1234"}
                jsonObject.put("admissionNo", sObj.getLoginId()+"");
                jsonObject.put("password", sh_Pref.getString("pin",""));
                jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "URL Loging- " + AppUrls.LOGIN_ONLINE_Test_STUDENT);
            Log.v(TAG, "URL LogingObj - " + jsonObject.toString());
            RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(AppUrls.LOGIN_ONLINE_Test_STUDENT)
                    .post(body)
                    .build();
            Log.v(TAG, request.body().toString());
            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();
                Log.v(TAG, "responseBody - " + jsonResp);
                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            utils.dismissDialog();
            Log.v(TAG, "responseBody - result - " + result);
            if (result != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(result);
                    if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
//                        if (!etNewPasscode.getText().toString().equals("")) {
//                            toEdit.putInt("pin", Integer.parseInt(etNewPasscode.getText().toString()));
//                            toEdit.commit();
//                        }
                        otLoginResultObj = new GsonBuilder().create().fromJson(ParentjObject.getJSONObject("result").getJSONObject("studentDetails").toString(), OTLoginResult.class);
                        Config config = new GsonBuilder().create().fromJson(ParentjObject.getJSONObject("result").getJSONObject("config").toString(), Config.class);
//                        JSONObject config = ParentjObject.getJSONObject("result").getJSONObject("config");
                        Log.v(TAG, "Student name- " + otLoginResultObj.getSName());
                        Gson gson = new Gson();
                        String json = gson.toJson(otLoginResultObj);
                        String configJson = gson.toJson(config);
                        toEdit.putString("otStudentObj", json);
                        toEdit.putString("config", configJson);
                        toEdit.putBoolean("student_ot_loggedin", true);
                        toEdit.commit();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setMessage("Please Check your details and try again!")
                                .setTitle(mActivity.getResources().getString(R.string.app_name))
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                            }
                                        }

                                );
                        AlertDialog alert = builder.create();
                        alert.show();

//                        btnOnline.setEnabled(true);
//                        alertWrongPassword();
                    }
                } catch (JSONException e) {
//                    btnOnline.setEnabled(true);
                    e.printStackTrace();
                }
            }

            getServerTime();
        }
    }

    private void getServerTime() {

        utils.showLoader(mActivity);

        String jsonResp = "null";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        Log.v("TAG", "TestGetQue Url - " + AppUrls.OT_URL + "systemTime");
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(AppUrls.OT_URL + "systemTime")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getUpcomingExams();
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
                            serverTime = ParentjObject.getString("DateTime");

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    init();
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getUpcomingExams();
            }
        });
    }


//    private void getServerTime() {
//
//        utils.showLoader(mActivity);
//
//
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        Request get = new Request.Builder()
//                .url(AppUrls.GetServerTime)
//                .build();
//
//        utils.showLog(TAG, "url - " + AppUrls.GetServerTime);
//
//        client.newCall(get).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                getUpcomingExams();
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                ResponseBody responseBody = response.body();
//                if (!response.isSuccessful()) {
//                    getUpcomingExams();
//                } else {
//                    String resp = responseBody.string();
//                    try {
//                        JSONObject ParentjObject = new JSONObject(resp);
//                        serverTime = ParentjObject.getString("dateTime");
//                        getUpcomingExams();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }


    void getUpcomingExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.DGETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId="+sObj.getStudentId()+"&sectionId="+sObj.getClassCourseSectionId()+"&scheduledFlag=SCHEDULED")
                .build();
        utils.showLog(TAG, "url " + AppUrls.DGETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId="+sObj.getStudentId()+"&sectionId="+sObj.getClassCourseSectionId()+"&scheduledFlag=SCHEDULED");


//        Request get = new Request.Builder()
//                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active")
//                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(() -> utils.dismissDialog());
                } else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
//                            JSONObject ob = ParentjObject.getJSONObject("result");
                            JSONArray jsonArray = ParentjObject.getJSONArray("result");


                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<LiveExam>>() {
                            }.getType();

                            examDetails.clear();
                            examDetails.addAll(gson.fromJson(jsonArray.toString(), type));

                            if (examDetails.size() > 0) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvUpcomingExams.setLayoutManager(new LinearLayoutManager(mActivity));
//                                        examDetails.get(0).seteTime("2021-04-24 18:40:00.0");
                                        Collections.reverse(examDetails);
                                        rvUpcomingExams.setAdapter(new OTLiveExamsAdapter());
                                    }
                                });
                            }else{
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setVisibility(View.VISIBLE);
                                        viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                utils.showLog(TAG,"name "+mActivity.getLocalClassName());

                                                if (mActivity.getLocalClassName().equalsIgnoreCase("StudentHome")){
                                                    ((StudentHome)mActivity).selectCourseTab();
                                                }else{
                                                    Intent intent = new Intent(mActivity,StudentHome.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("course",true);
                                                    startActivity(intent);
                                                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                    mActivity.finishAffinity();

                                                }
                                            }
                                        });
                                    }
                                });
                            }



                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setVisibility(View.VISIBLE);
                                    viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            utils.showLog(TAG,"name "+mActivity.getLocalClassName());

                                            if (mActivity.getApplicationContext() instanceof StudentHome){
                                                ((StudentHome)mActivity).selectCourseTab();
                                            }else{
                                                Intent intent = new Intent(mActivity,StudentHome.class);
                                                intent.putExtra("course",true);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                mActivity.finishAffinity();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    } catch (Exception e) {

                        utils.showLog(TAG, "error " + e.getMessage());
                    }
                }
                mActivity.runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }


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
            utils.showLog(TAG,"error "+e.getMessage());
        }

//        duration = "" + TimeUnit.MILLISECONDS.toMinutes(endDate.getTime() - currentDate.getTime());
        duration = "" + (endDate.getTime() - currentDate.getTime());
        return duration;
    }
    
    private class OTLiveExamsAdapter extends RecyclerView.Adapter<OTLiveExamsAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_ot_tests_new, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSubject.setText(examDetails.get(position).geteName());
            holder.tvTestName.setVisibility(View.INVISIBLE);
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDetails.get(position).getsTime())));
                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDetails.get(position).getsTime())));

            } catch (ParseException e) {
                e.printStackTrace();
                utils.showLog(TAG,"error "+e.getMessage());
            }

            holder.tvTestStatus.setText(examDetails.get(position).geteStatus());


            holder.tvStartTest.setVisibility(View.VISIBLE);

            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Log.v(TAG, "" + examDetails.get(position).getsTime());
            Date startDate = null;
            Date endDate = null;
            Date resultDate = null;
            try {
                startDate = dtf.parse(examDetails.get(position).getsTime());
                endDate = dtf.parse(examDetails.get(position).geteTime());
                resultDate = dtf.parse(examDetails.get(position).getrDate());
                now = dtf.parse(serverTime);

                if (holder.tvTestStatus.getText().toString().equalsIgnoreCase("COMPLETED")) {
                    holder.tvStartTest.setText("View Results");
                }
                else if (now.after(startDate) && now.before(endDate)) {
                    utils.showLog(TAG,"here i started");
                    duration = getDuration(dtf.format(now), examDetails.get(position).geteTime());
                    if (holder.tvTestStatus.getText().toString().equalsIgnoreCase("SCHEDULED")) {

                    } else if (holder.tvTestStatus.getText().toString().equalsIgnoreCase("INPROGRESS") || holder.tvTestStatus.getText().toString().equalsIgnoreCase("SUBMITTED")) {
                        utils.showLog(TAG,"here i am");
                        holder.tvStartTest.setText("Resume Test");
                    }
                }
                else if (now.after(endDate) && now.after(resultDate) && holder.tvTestStatus.getText().toString().equalsIgnoreCase("SUBMITTED")) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tvStartTest.setText("View Results");
                        }
                    });


                }
                else if (now.after(endDate) && holder.tvTestStatus.getText().toString().equalsIgnoreCase("SCHEDULED")) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tvStartTest.setText("Missed");
                        }
                    });


                }
                else if (now.after(endDate) && (holder.tvTestStatus.getText().toString().equalsIgnoreCase("INPROGRESS") || holder.tvTestStatus.getText().toString().equalsIgnoreCase("SUBMITTED"))) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tvTestStatus.setText("SUBMITTED");
                            holder.tvStartTest.setText("View Results");
                        }
                    });


                }
            } catch (ParseException e) {
                e.printStackTrace();

                utils.showLog(TAG,"error "+e.getMessage());

            }
            holder.tvStartTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.tvStartTest.getText().toString().equalsIgnoreCase("Start Test")){
                        startExam(examDetails.get(position),position);
                    }
                    else if (!holder.tvStartTest.getText().toString().equalsIgnoreCase("Missed"))
                        getStudentStatus(position);
                    else
                        Toast.makeText(mActivity, "You Missed the Exam", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return examDetails.size();
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


    private void showRetrivingErrorDialogue() {
        new AlertDialog.Builder(mActivity)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Error While Retriving the Exam Details\n Please try again")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }



    private void getStudentStatus(int position) {

        Gson gson = new Gson();
        String json = sh_Pref.getString("otStudentObj", "");
        studentObjNew = gson.fromJson(json, OTLoginResult.class);
        
        ProgressDialog loading = new ProgressDialog(mActivity);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        String jsonResp = "null";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Log.v("TAG", "TestGetQue Url - " + AppUrls.DGETSTUDENTEXAMBYID + "?studentId=" + studentObjNew.getMStudentId() + "&examId=" + examDetails.get(position).getMyExamId() + "&schemaName=" + sh_Pref.getString("schema", ""));

        Request request = new Request.Builder()
                .url(AppUrls.DGETSTUDENTEXAMBYID + "?studentId=" + studentObjNew.getMStudentId() + "&examId=" + examDetails.get(position).getMyExamId() + "&schemaName=" + sh_Pref.getString("schema", ""))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRetrivingErrorDialogue();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {

                    try {
                        String jsonResp = response.body().string();

                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            if (ParentjObject.has("result")) {
                                JSONObject resultObj = ParentjObject.getJSONObject("result");
                                if (resultObj.has("eStatus")) {
                                    eStatus = resultObj.getString("eStatus");
                                }

                                if (resultObj.has("_id")) {
                                    _id = resultObj.getString("_id");
                                }
                            }

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getModifiedTime(position);
                                }
                            });
                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getModifiedTime(position);
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        mActivity. runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showRetrivingErrorDialogue();
                            }
                        });
                    }
                }
            }
        });
    }



    private void getServerTimeStatus(int position) {
        ProgressDialog loading = new ProgressDialog(mActivity);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();
        String jsonResp = "null";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        Log.v("TAG", "TestGetQue Url - " + AppUrls.OT_URL + "systemTime");
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(AppUrls.OT_URL + "systemTime")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
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
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            Log.v(TAG, "Date - " + ParentjObject.getString("DateTime"));
                            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date now = new Date();
                            Log.v(TAG, "" + examDetails.get(0).getsTime());
                            Date startDate = null;
                            Date endDate = null;
                            Date resultDate = null;
                            try {
                                startDate = dtf.parse(examDetails.get(0).getsTime());
                                endDate = dtf.parse(examDetails.get(0).geteTime());
                                resultDate = dtf.parse(examDetails.get(0).getrDate());
                                now = dtf.parse(ParentjObject.getString("DateTime"));
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            initialNavigate(examDetails.get(position), ParentjObject.getString("DateTime"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getModifiedTime(int position) {
        ProgressDialog loading = new ProgressDialog(mActivity);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();
        String jsonResp = "null";

        utils.showLog(TAG,"url "+AppUrls.DGetExamLastModifiedDate + "?schemaName=" + mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+"&examId="+examDetails.get(position).getMyExamId()+"&studentId="+studentId);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.DGetExamLastModifiedDate + "?schemaName=" + mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+"&examId="+examDetails.get(position).getMyExamId()+"&studentId="+studentId)
                .build();
        client.newCall(request).enqueue(new Callback() {
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
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200") && ParentjObject.getJSONObject("result")!=null && ParentjObject.getJSONObject("result").has("lastModifiedDate")) {


                            JSONObject result = ParentjObject.getJSONObject("result");

                            Gson gson = new Gson();
                            String confJson = sh_Pref.getString("config", "");
                            Config config = gson.fromJson(confJson, Config.class);

                            int delay = config.getDbSync()+120;

                            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                Date lastModified = dtf.parse(result.getString("lastModifiedDate"));
                                Date current = dtf.parse(result.getString("currentDate"));

                                long diff = current.getTime() - lastModified.getTime();


                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ((TimeUnit.MILLISECONDS.toSeconds(diff))>=delay){
                                            try {
                                                initialNavigate(examDetails.get(position), result.getString("currentDate"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {
                                            new AlertDialog.Builder(mActivity)
                                                    .setTitle(mActivity.getResources().getString(R.string.app_name))
                                                    .setMessage("Your last session has abruptly stopped. Please wait for "+((delay - TimeUnit.MILLISECONDS.toMinutes(diff))/60)+" mins.")
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .setCancelable(true)
                                                    .show();
                                        }
                                    }
                                });

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getServerTimeStatus(position);
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


    private void initialNavigate(LiveExam liveExams, String dateTime) {

        Gson gson = new Gson();
        String json = sh_Pref.getString("otStudentObj", "");
        studentObjNew = gson.fromJson(json, OTLoginResult.class);
        
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        Log.v(TAG, "" + liveExams.getsTime());

        Date startDate = null, endDate = null, resultDate = null;

        try {
            startDate = dtf.parse(liveExams.getsTime());
            endDate = dtf.parse(liveExams.geteTime());
            resultDate = dtf.parse(liveExams.getrDate());
            now = dtf.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (now.after(startDate) && now.before(endDate)) {
            duration = getDuration(dtf.format(now), liveExams.geteTime());

            Log.v(TAG, "duration - " + duration);

            if (eStatus.equalsIgnoreCase("SCHEDULED") ||
                    eStatus.equalsIgnoreCase("INPROGRESS") ||
                    eStatus.equalsIgnoreCase("SUBMITTED")) {

//                Intent onlineTestIntent;
//                if (liveExams.getJeeSectionTemplate()!=null && !liveExams.getJeeSectionTemplate().equalsIgnoreCase("NA")) {
//                    onlineTestIntent = new Intent(mActivity, JEETestMarksDivision.class);
//                } else {
//                    onlineTestIntent = new Intent(mActivity, OTStudentOnlineTestActivity.class);
//                }
//                onlineTestIntent.putExtra("live", (Serializable) liveExams);
//
//                onlineTestIntent.putExtra("studentId", studentObjNew.getMStudentId());
//                onlineTestIntent.putExtra("testId", liveExams.getMyExamId());
//                onlineTestIntent.putExtra("testName", liveExams.geteName());
//                onlineTestIntent.putExtra("examSTime", liveExams.getsTime());
//                onlineTestIntent.putExtra("examETime", liveExams.geteTime());
//                onlineTestIntent.putExtra("examRTime", liveExams.getrDate());
//                onlineTestIntent.putExtra("testTime", duration);
//                onlineTestIntent.putExtra("eDuration", liveExams.geteDuration());
//                onlineTestIntent.putExtra("correctMarks", liveExams.getcMarks());
//                onlineTestIntent.putExtra("wrongMarks", liveExams.getwMarks());
//                onlineTestIntent.putExtra("testCategory", "3");
//                onlineTestIntent.putExtra("examdet_Id", _id);
//                SimpleDateFormat dtf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date now1 = new Date();
//                if (liveExams.geteStatus().equalsIgnoreCase("SCHEDULED"))
//                    onlineTestIntent.putExtra("startTime", dtf1.format(now1));
//                else onlineTestIntent.putExtra("startTime", startTime);
//                onlineTestIntent.putExtra("jeeSectionTemplate", liveExams.getJeeSectionTemplate()!=null?liveExams.getJeeSectionTemplate():"NA");
//                onlineTestIntent.putExtra("testFilePath", liveExams.getePath());
//                onlineTestIntent.putExtra("studentTestFilePath", "NA");
//                if (liveExams.geteStatus().equalsIgnoreCase("INPROGRESS"))
//                    onlineTestIntent.putExtra("studentTestFilePath", "INPROGRESS");
//                if (liveExams.geteStatus().equalsIgnoreCase("SUBMITTED"))
//                    onlineTestIntent.putExtra("studentTestFilePath", "SUBMITTED");
//                startActivity(onlineTestIntent);
                Intent ii = new Intent(mActivity, DescriptiveSectionDetails.class);
                ii.putExtra("studentTestFilePath", "NA");
                ii.putExtra("testId", liveExams.getMyExamId());
                ii.putExtra("testName", liveExams.geteName());
                ii.putExtra("examSTime", liveExams.getsTime());
                ii.putExtra("examETime", liveExams.geteTime());
                ii.putExtra("examRTime", liveExams.getrDate());
                ii.putExtra("studentId", otLoginResultObj.getMStudentId());
                if (liveExams.geteStatus().equalsIgnoreCase("INPROGRESS"))
                    ii.putExtra("studentTestFilePath", "INPROGRESS");
                if (liveExams.geteStatus().equalsIgnoreCase("SUBMITTED"))
                    ii.putExtra("studentTestFilePath", "SUBMITTED");
                ii.putExtra("examdet_Id", _id);
                ii.putExtra("testFilePath", liveExams.getePath());
                ii.putExtra("correctMarks", liveExams.getcMarks());
                ii.putExtra("wrongMarks", liveExams.getwMarks());
                startActivity(ii);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


            }
        } else if (now.after(endDate) && eStatus.equalsIgnoreCase("SCHEDULED")) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("Sorry..!")
                    .setMessage("Sorry you missed the Exam...!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();
        } else if (now.after(endDate) && eStatus.equalsIgnoreCase("INPROGRESS")) {

            new AlertDialog.Builder(mActivity)
                    .setTitle("Plase wait...!")
                    .setMessage("Results will be released after \n" + liveExams.getrDate())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true)
                    .show();

        } else if (now.after(endDate) && now.after(resultDate) && eStatus.equalsIgnoreCase("COMPLETED")) {
            Intent onlineJeeTestIntent = new Intent(mActivity, OTStudentOnlineTestResult.class);
            onlineJeeTestIntent.putExtra("studentId", otLoginResultObj.getMStudentId());
            onlineJeeTestIntent.putExtra("studentTestFilePath", _id);
            onlineJeeTestIntent.putExtra("testFilePath", liveExams.getePath());
            onlineJeeTestIntent.putExtra("testId", liveExams.getMyExamId());
            onlineJeeTestIntent.putExtra("testName", liveExams.geteName());
            onlineJeeTestIntent.putExtra("jeeSectionTemplate", "NA");
            startActivity(onlineJeeTestIntent);
        } else {
            if (eStatus.equalsIgnoreCase("SUBMITTED") || eStatus.equalsIgnoreCase("COMPLETED")) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("Plase wait...!")
                        .setMessage("Results will be released after \n" + liveExams.getrDate())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            } else {
                String sdate = "";
                try {
                    sdate = new SimpleDateFormat("hh:mm aa dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExams.getsTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new AlertDialog.Builder(mActivity)
                        .setTitle("Plase wait...!")
                        .setMessage("Exam Scheduled at \n" + sdate)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        }
    }



    private void setDatatoList() {
        List<StudentOnlineTestObj> listUpcoming = new ArrayList<>();

        for (int i = 0; i < listTest.size(); i++) {
            try {
                if (getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestStartDate()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime)) != null) {
                    listUpcoming.add(listTest.get(i));
                } else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestEndDate()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime)) && (listTest.get(i).getStudentTestStatus().equalsIgnoreCase("Scheduled") || listTest.get(i).getStudentTestStatus().equalsIgnoreCase("Inprogress"))) {
                    liveExam = listTest.get(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (liveExam != null) {
            viewStudentLiveExamsFrag.findViewById(R.id.cv_start_exam).setVisibility(View.VISIBLE);
            tvTestName.setText(liveExam.getTestName());
            tvTestType.setText(liveExam.getTestCategoryName());
            tvDuration.setText(liveExam.getTestDuration());
            try {
                tvEndTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestEndDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewStudentLiveExamsFrag.findViewById(R.id.cv_start_exam).setVisibility(View.GONE);
        }

        if (listUpcoming.size() > 0) {
            rvUpcomingExams.setAdapter(new UpcomingAdapter(listUpcoming));
        } else {
            viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setVisibility(View.VISIBLE);
        }
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
    public void onMonthChanged(String month) {
//        currentMonth = month;
        liveExam = null;
        listTest.clear();
//        if (filterList!=null && filterList.get(currentMonth)!=null && filterList.get(currentMonth).size()>0){
//            listTest.addAll(filterList.get(currentMonth));
//        }
        setDatatoList();
    }


    class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> {

        List<StudentOnlineTestObj> listTests;

        public UpcomingAdapter(List<StudentOnlineTestObj> listTests) {
            this.listTests = listTests;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_exams, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTests.get(position).getTestStartDate())));
                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTests.get(position).getTestStartDate())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvTestName.setText(listTests.get(position).getTestCategoryName());
            holder.tvSubject.setText(listTests.get(position).getTestName());
            holder.tvStatus.setText(listTests.get(position).getStudentTestStatus());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity,LiveExamDetails.class);
                    intent.putExtra("live", (Serializable) listTest.get(position));
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
            });

        }

        @Override
        public int getItemCount() {
            return listTests.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate, tvSubject, tvTestName, tvTime, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvSubject = itemView.findViewById(R.id.tv_sub);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvTime = itemView.findViewById(R.id.tv_start_time);
                tvStatus = itemView.findViewById(R.id.tv_status);
            }
        }
    }


    private void startExam(LiveExam liveExams, int position) {

        Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.fragment_live_exam_deatils);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        dialog.findViewById(R.id.ll_header).setVisibility(View.VISIBLE);

        ((TextView)dialog.findViewById(R.id.tv_test_name)).setText(liveExams.geteName());
        ((TextView)dialog.findViewById(R.id.tv_test_category)).setText(liveExams.geteCatName());
        try {
            ((TextView)dialog.findViewById(R.id.tv_start_date)).setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExams.getsTime())));
            ((TextView)dialog.findViewById(R.id.tv_start_time)).setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExams.getsTime())));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.btn_join).setOnClickListener(view -> {
            getStudentStatus(position);
            dialog.dismiss();
        });


        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



    }


    public class LiveExam implements Serializable{

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("schemaName")
        @Expose
        private String schemaName;
        @SerializedName("myExamId")
        @Expose
        private int myExamId;
        @SerializedName("courseId")
        @Expose
        private int courseId;
        @SerializedName("courseName")
        @Expose
        private String courseName;
        @SerializedName("classId")
        @Expose
        private int classId;
        @SerializedName("className")
        @Expose
        private String className;
        @SerializedName("subjectId")
        @Expose
        private int subjectId;
        @SerializedName("subjectName")
        @Expose
        private String subjectName;
        @SerializedName("eName")
        @Expose
        private String eName;
        @SerializedName("eType")
        @Expose
        private String eType;
        @SerializedName("eStatus")
        @Expose
        private String eStatus;
        @SerializedName("sectionCount")
        @Expose
        private int sectionCount;
        @SerializedName("eDuration")
        @Expose
        private Integer eDuration;
        @SerializedName("eCatId")
        @Expose
        private Integer eCatId;
        @SerializedName("eCatName")
        @Expose
        private String eCatName;
        @SerializedName("sTime")
        @Expose
        private String sTime;
        @SerializedName("eTime")
        @Expose
        private String eTime;
        @SerializedName("rDate")
        @Expose
        private String rDate;
        @SerializedName("ePath")
        @Expose
        private String ePath;
        @SerializedName("cMarks")
        @Expose
        private int cMarks;
        @SerializedName("wMarks")
        @Expose
        private Integer wMarks;
        @SerializedName("creationDts")
        @Expose
        private String creationDts;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
        }

        public int getMyExamId() {
            return myExamId;
        }

        public void setMyExamId(int myExamId) {
            this.myExamId = myExamId;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public int getClassId() {
            return classId;
        }

        public void setClassId(int classId) {
            this.classId = classId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public int getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(int subjectId) {
            this.subjectId = subjectId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String geteName() {
            return eName;
        }

        public void seteName(String eName) {
            this.eName = eName;
        }

        public String geteType() {
            return eType;
        }

        public void seteType(String eType) {
            this.eType = eType;
        }

        public String geteStatus() {
            return eStatus;
        }

        public void seteStatus(String eStatus) {
            this.eStatus = eStatus;
        }

        public int getSectionCount() {
            return sectionCount;
        }

        public void setSectionCount(int sectionCount) {
            this.sectionCount = sectionCount;
        }

        public Integer geteDuration() {
            return eDuration;
        }

        public void seteDuration(Integer eDuration) {
            this.eDuration = eDuration;
        }

        public Integer geteCatId() {
            return eCatId;
        }

        public void seteCatId(Integer eCatId) {
            this.eCatId = eCatId;
        }

        public String geteCatName() {
            return eCatName;
        }

        public void seteCatName(String eCatName) {
            this.eCatName = eCatName;
        }

        public String getsTime() {
            return sTime;
        }

        public void setsTime(String sTime) {
            this.sTime = sTime;
        }

        public String geteTime() {
            return eTime;
        }

        public void seteTime(String eTime) {
            this.eTime = eTime;
        }

        public String getrDate() {
            return rDate;
        }

        public void setrDate(String rDate) {
            this.rDate = rDate;
        }

        public String getePath() {
            return ePath;
        }

        public void setePath(String ePath) {
            this.ePath = ePath;
        }

        public int getcMarks() {
            return cMarks;
        }

        public void setcMarks(int cMarks) {
            this.cMarks = cMarks;
        }

        public Integer getwMarks() {
            return wMarks;
        }

        public void setwMarks(Integer wMarks) {
            this.wMarks = wMarks;
        }

        public String getCreationDts() {
            return creationDts;
        }

        public void setCreationDts(String creationDts) {
            this.creationDts = creationDts;
        }

        public List<SectionArray> getSectionArray() {
            return sectionArray;
        }

        public void setSectionArray(List<SectionArray> sectionArray) {
            this.sectionArray = sectionArray;
        }

        @SerializedName("sectionArray")
        @Expose
        private List<SectionArray> sectionArray = null;
    }
}