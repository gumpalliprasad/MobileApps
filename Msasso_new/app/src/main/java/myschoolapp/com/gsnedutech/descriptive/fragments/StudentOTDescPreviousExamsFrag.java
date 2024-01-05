package myschoolapp.com.gsnedutech.descriptive.fragments;

import android.app.Activity;
import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.OnlIneTest.Config;
import myschoolapp.com.gsnedutech.OnlIneTest.OTLoginResult;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentHome;
import myschoolapp.com.gsnedutech.StudentLiveExamsTabs;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.TeacherDescriptiveSectionDetail;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class StudentOTDescPreviousExamsFrag extends Fragment implements StudentLiveExamsTabs.MonthChangeListner {


    private static final String TAG = "SriRam -" + StudentOTDescPreviousExamsFrag.class.getName();
    String serverTime = "";

    MyUtils utils = new MyUtils();

    View viewStudentLiveExamsFrag;
    Unbinder unbinder;

    String duration;

    String _id = "";
    String eStatus = "SCHEDULED";
    OTLoginResult otLoginResultObj;
    ArrayList<StudentOTDescriptiveExamsFrag.LiveExam> examDetails = new ArrayList<>();

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


    public StudentOTDescPreviousExamsFrag() {
        // Required empty public constructor
    }

    public static StudentOTDescPreviousExamsFrag newInstance(String monthYear) {

        Bundle args = new Bundle();
        args.putString("monthYear", monthYear);

        StudentOTDescPreviousExamsFrag fragment = new StudentOTDescPreviousExamsFrag();
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


        rvUpcomingExams.setLayoutManager(new LinearLayoutManager(mActivity));


        new LoginOnlineTest().execute();
    }

    @Override
    public void onMonthChanged(String month) {

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

            getUpcomingExams();
        }
    }

    void getUpcomingExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.DGETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId="+sObj.getStudentId()+"&sectionId="+sObj.getClassCourseSectionId()+"&scheduledFlag=COMPLETED")
                .build();
        utils.showLog(TAG, "url " + AppUrls.DGETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId="+sObj.getStudentId()+"&sectionId="+sObj.getClassCourseSectionId()+"&scheduledFlag=COMPLETED");


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
                            Type type = new TypeToken<ArrayList<StudentOTDescriptiveExamsFrag.LiveExam>>() {
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
                                        rvUpcomingExams.setAdapter(new PreviousExamAdapter());
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

    class PreviousExamAdapter extends RecyclerView.Adapter<PreviousExamAdapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_exams, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDetails.get(position).getsTime())));
                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDetails.get(position).geteTime())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvTestName.setText(examDetails.get(position).geteCatName());
            holder.tvSubject.setText(examDetails.get(position).geteName());
            holder.tvStatus.setText(examDetails.get(position).geteStatus());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (examDetails.get(position).geteStatus().equalsIgnoreCase("COMPLETED")) {
                        Intent i = new Intent(mActivity, TeacherDescriptiveSectionDetail.class);
                        i.putExtra("testFilePath", examDetails.get(position).getePath());
                        i.putExtra("studentTestFilePath", examDetails.get(position).geteStatus());
                        i.putExtra("studentId", otLoginResultObj.getMStudentId());
                        i.putExtra("testId", examDetails.get(position).getMyExamId());
                        i.putExtra("studentTest", (Serializable) examDetails.get(position));
                        startActivity(i);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else if (examDetails.get(position).geteStatus().equalsIgnoreCase("MISSED")) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(getString(R.string.app_name))
                                .setMessage("Sorry you had missed this exam")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .setCancelable(false)
                                .show();
                    } else if (examDetails.get(position).geteStatus().equalsIgnoreCase("INPROGRESS")) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(getString(R.string.app_name))
                                .setMessage("Sorry you didnt Submitted this")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .setCancelable(false)
                                .show();
                    } else {
                        Intent onlineTestIntent = new Intent(mActivity, TeacherDescriptiveSectionDetail.class);
                        onlineTestIntent.putExtra("testFilePath", examDetails.get(position).getePath());
                        onlineTestIntent.putExtra("studentTestFilePath", examDetails.get(position).geteStatus());
                        onlineTestIntent.putExtra("studentId", otLoginResultObj.getMStudentId());
                        onlineTestIntent.putExtra("testId", examDetails.get(position).getMyExamId());
                        onlineTestIntent.putExtra("studentTest", (Serializable) examDetails.get(position));
                        startActivity(onlineTestIntent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return examDetails.size();
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
}