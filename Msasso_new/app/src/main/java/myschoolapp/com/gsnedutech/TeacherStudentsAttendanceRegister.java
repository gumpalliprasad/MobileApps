package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.TeacherAttendStudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherStudentsAttendanceRegister extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "SriRam -" + TeacherStudentsAttendanceRegister.class.getName();

    String branchId,courseId,classId,sectionId;
    List<TeacherAttendStudentObj> studList = new ArrayList<>();

    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_lateCount)
    TextView tvLateCount;
    @BindView(R.id.tv_absentCount)
    TextView tvAbsentCount;
    @BindView(R.id.tv_presentCount)
    TextView tvPresentCount;
    @BindView(R.id.rv_stdnList)
    RecyclerView rvStdnList;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.tv_lrcout)
    TextView tvLrcout;
    @BindView(R.id.ll_attOverView)
    LinearLayout llAttOverView;
    @BindView(R.id.tv_holiday)
    TextView tvHoliday;
    @BindView(R.id.tv_sendSMS)
    TextView tvSendSMS;
    @BindView(R.id.img_nextdate)
    ImageView imgNextdate;


    Calendar calendar;
    Date reqdate;
    Boolean pause = false;

    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMMM, yyyy");

    StudAttandenceAdapter studAttandenceAdapter;
    int secAttdStatus;
    String sectionAttendanceId;

    String selYear,selMon,selDate;

    String[] holidayArray = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_students_attendance_register);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);


        getSupportActionBar().setTitle("Attendance Register");
        getSupportActionBar().setSubtitle("" + getIntent().getStringExtra("className") + " . " + getIntent().getStringExtra("secName"));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();


        tvLrcout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvLrcout.getText().toString().equalsIgnoreCase("LR : 0")) {
                    Intent lrIntent = new Intent(TeacherStudentsAttendanceRegister.this, AllStudentsLeaveRequests.class);
                    lrIntent.putExtra("sectionId", "" + sectionId);
                    startActivity(lrIntent);
                } else {
                    Toast.makeText(TeacherStudentsAttendanceRegister.this, "No  Leave Requests", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                new SendAttendanceNotification().execute();
            }
        });


    }

    private void init() {
        branchId = getIntent().getStringExtra("branchId");
        courseId = getIntent().getStringExtra("courseId");
        classId = getIntent().getStringExtra("classId");
        sectionId = getIntent().getStringExtra("sectionId");

        calendar = Calendar.getInstance();
        updateLabel(displaysdf.format(new Date()), 0);

        rvStdnList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pause) {
            tvEdit.setClickable(true);
            updateLabel(tvDate.getText().toString(), 0);
            pause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause = true;
    }

    public void editAttendance(View view) {
        tvEdit.setClickable(false);
        Intent i = new Intent(TeacherStudentsAttendanceRegister.this, TeacherStdnTakeAttendance.class);
        i.putExtra("dispdate", "" + displaysdf.format(reqdate));
        i.putExtra("reqDate", "" + serversdf.format(reqdate));
        i.putExtra("sectionAttendanceId", "" + sectionAttendanceId);
        i.putExtra("sectionId", "" + sectionId);
        i.putExtra("branchId", "" + branchId);
        i.putExtra("courseId", "" + courseId);
        i.putExtra("classId", "" + classId);
        i.putExtra("userId", getIntent().getStringExtra("userId"));
        startActivity(i);
    }

    public void nextDay(View view) {
        updateLabel(tvDate.getText().toString(), 1);
    }

    public void previousDay(View view) {
        updateLabel(tvDate.getText().toString(), -1);
    }

    public void showCalendar(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dayOfMonth + "/" + (month + 1) + "/" + year);
            updateLabel(displaysdf.format(date), 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void updateLabel(String date, int change) {
        try {
            Date dateObj = displaysdf.parse(date);
//            calendar.setTime(dateObj);
//            if (change != 0)
//                calendar.add(Calendar.DATE, change);
//            dateObj = calendar.getTime();
            tvDate.setText(displaysdf.format(dateObj));


            selMon = ""+(calendar.get(Calendar.MONTH)+1);
            selYear= ""+(calendar.get(Calendar.YEAR));
            selDate= ""+(calendar.get(Calendar.DATE));


            Log.v(TAG,"Month - "+selMon+" Year - "+selYear+" date - "+selDate);

            reqdate = displaysdf.parse(tvDate.getText().toString());
            getHolidaysForStaffAttendance();

            Log.v(TAG,"req - "+reqdate);
            Log.v(TAG,"req pre - "+new Date());

            if (displaysdf.format(reqdate).equalsIgnoreCase(displaysdf.format(new Date()))) {
                imgNextdate.setVisibility(View.INVISIBLE);
                imgNextdate.setEnabled(false);
            }else if (reqdate.compareTo(new Date()) < 0) {
                imgNextdate.setVisibility(View.VISIBLE);
                imgNextdate.setEnabled(true);
            }else if (reqdate.compareTo(new Date()) > 0) {
                imgNextdate.setVisibility(View.INVISIBLE);
                imgNextdate.setEnabled(false);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//        Sending Attendance Notificcations
    void sendAttendanceNotification(){
        ProgressDialog loading = new ProgressDialog(TeacherStudentsAttendanceRegister.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("sectionId", sectionId);
            jsonObject.put("SMSNotification", "1");
            jsonObject.put("pushNotification", "1");
            jsonObject.put("branchId", branchId);

            Log.v(TAG, "Json Obj - " + jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" +  new AppUrls().Attendance_StudentAttendanceNotification);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url( new AppUrls().Attendance_StudentAttendanceNotification)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Attendanc Update and Insertion Status responce - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                                    Toast.makeText(TeacherStudentsAttendanceRegister.this, "Notifications Sent Sucessfully", Toast.LENGTH_SHORT).show();
                                    finish();


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }
        });
    }


    void  getHolidaysForStaffAttendance(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("yearId", "" + selYear);
            jsonObject.put("monthId", selMon);
            jsonObject.put("branchId", branchId);
            jsonObject.put("courseId", courseId);
            jsonObject.put("classId", classId);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Json Obj - " + jsonObject);
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + new AppUrls().GetHolidaysForStudentAttendance);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(new AppUrls().GetHolidaysForStudentAttendance)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "GetHolidaysForStaffAttendance responce - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                                    //Split string with comma
                                    holidayArray = ParentjObject.getString("holidayDates").split(",");

                                    Log.v(TAG,"holidays "+ Arrays.toString(holidayArray));

                                    List<String> list = Arrays.asList(holidayArray);
                                    if (list.contains(selDate)){
                                        llAttOverView.setVisibility(View.GONE);
                                        rvStdnList.setVisibility(View.GONE);
                                        tvEdit.setVisibility(View.GONE);
                                        tvSendSMS.setVisibility(View.GONE);
                                        tvHoliday.setVisibility(View.VISIBLE);
                                    }else{
                                        llAttOverView.setVisibility(View.VISIBLE);
                                        rvStdnList.setVisibility(View.VISIBLE);
                                        tvEdit.setVisibility(View.VISIBLE);

                                        if (displaysdf.format(reqdate).equalsIgnoreCase(displaysdf.format(new Date()))) {
                                            tvSendSMS.setVisibility(View.VISIBLE);
                                            tvSendSMS.setEnabled(true);
                                        }else {
                                            tvSendSMS.setVisibility(View.INVISIBLE);
                                            tvSendSMS.setEnabled(false);
                                        }

                                        tvHoliday.setVisibility(View.GONE);
                                        getStudents();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }


    //    Getting the Students for attendance

    void getStudents(){
        ProgressDialog loading = new ProgressDialog(TeacherStudentsAttendanceRegister.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - Attendance Student request - " + new AppUrls().Attendance_GetStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "HW Types response - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                                    JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendance");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<TeacherAttendStudentObj>>() {
                                    }.getType();

                                    studList.clear();
                                    studList.addAll(gson.fromJson(jsonArr.toString(), type));
                                    Log.v(TAG, "studList - " + studList.size());

                                    studAttandenceAdapter = new StudAttandenceAdapter(TeacherStudentsAttendanceRegister.this, studList);
                                    rvStdnList.setAdapter(studAttandenceAdapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        getTotalLeaveRequestCountByDate(serversdf.format(reqdate));
                    }
                });
            }
        });
    }

    //    Getting Section Attendance report of  Students

    void getSectionDayReports(String currdate){
        ProgressDialog loading = new ProgressDialog(TeacherStudentsAttendanceRegister.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Log.v(TAG, "URL -  Attendance SectionDayreport - " + new AppUrls().Attendance_GetSectionDayReports +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","") + "&sectionId=" + sectionId
                + "&currentDate=" + currdate);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetSectionDayReports +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                        + "&currentDate=" + currdate)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "response - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                secAttdStatus = Integer.parseInt(ParentjObject.getString("StatusCode"));

                                loading.dismiss();

                                if (secAttdStatus == 200) {
                                    sectionAttendanceId = ParentjObject.getString("sectionAttendanceId");
                                    tvLateCount.setText(ParentjObject.getString("lateCount"));
                                    tvPresentCount.setText(ParentjObject.getString("presentCount"));
                                    tvAbsentCount.setText(ParentjObject.getString("absentCount"));
                                    getAbsentStudents(serversdf.format(reqdate));

                                } else if (secAttdStatus == 300) {
                                } else {
                                    tvLateCount.setText("0");
                                    tvPresentCount.setText("0");
                                    tvAbsentCount.setText("0");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (studAttandenceAdapter!=null)
                        studAttandenceAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    //    Getting Absent Students
    void getAbsentStudents(String currDate){
        ProgressDialog loading = new ProgressDialog(TeacherStudentsAttendanceRegister.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - Attendance Absent request - " + new AppUrls().Attendance_GetAbsentStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                + "&currentDate=" + currDate);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetAbsentStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                        + "&currentDate=" + currDate)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Attendance Absent response - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    if (ParentjObject.getJSONArray("studentAttendanceArray").length() > 0) {

                                        JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendanceArray").getJSONObject(0).
                                                getJSONArray("studentList");

                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<TeacherAttendStudentObj>>() {
                                        }.getType();

                                        List<TeacherAttendStudentObj> studAbsentList = new ArrayList<>();
                                        studAbsentList.clear();
                                        studAbsentList.addAll(gson.fromJson(jsonArr.toString(), type));
                                        Log.v(TAG, "studAbsentList - " + studAbsentList.size());

                                        for (TeacherAttendStudentObj absentStudentObj : studAbsentList) {
                                            for (TeacherAttendStudentObj attendStudentObj : studList) {
                                                if (absentStudentObj.getStudentId().equalsIgnoreCase(attendStudentObj.getStudentId())) {
                                                    attendStudentObj.setPreviousState(absentStudentObj.getAttendanceType());
                                                    attendStudentObj.setAttendanceType(absentStudentObj.getAttendanceType());
                                                    attendStudentObj.setAttendanceId(absentStudentObj.getAttendanceId());
                                                }
                                            }
                                        }

                                        studAttandenceAdapter.notifyDataSetChanged();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }
        });

    }


    //    Getting Absent Students

    void getTotalLeaveRequestCountByDate(String date){
        ProgressDialog loading = new ProgressDialog(TeacherStudentsAttendanceRegister.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - Attendance leave request - " + new AppUrls().Attendance_GetTotalLeaveRequestCountByDate +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId + "&date=" + date);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetTotalLeaveRequestCountByDate +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId + "&date=" + date)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Attendance Absent response - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    tvLrcout.setText("LR : " + ParentjObject.get("totalLeaveReqCount"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        getSectionDayReports(serversdf.format(reqdate));
                    }
                });
            }
        });

    }


    public class StudAttandenceAdapter extends RecyclerView.Adapter<StudAttandenceAdapter.ViewHolder> implements Filterable {

        Context _context;
        List<TeacherAttendStudentObj> list, list_filtered;

        StudAttandenceAdapter(Context context, List<TeacherAttendStudentObj> list) {
            this._context = context;
            this.list = list;
            this.list_filtered = list;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(_context).inflate(R.layout.attend_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

            viewHolder.roll.setText(list_filtered.get(i).getRollNumber());
            viewHolder.name.setText(list_filtered.get(i).getStudentName());

            if (list_filtered.get(i).getProfilePic().equalsIgnoreCase("NA")) {
                viewHolder.tvNameImage.setText((list_filtered.get(i).getStudentName().charAt(0) + "").toUpperCase());
                viewHolder.tvNameImage.setVisibility(View.VISIBLE);
                viewHolder.imgProfile.setVisibility(View.GONE);
            } else {
                viewHolder.tvNameImage.setVisibility(View.GONE);
                viewHolder.imgProfile.setVisibility(View.VISIBLE);
                Picasso.with(TeacherStudentsAttendanceRegister.this).load(new AppUrls().GetstudentProfilePic + list_filtered.get(i).getProfilePic()).
                        placeholder(R.drawable.user_default).into(viewHolder.imgProfile);

            }


            switch (list_filtered.get(i).getAttendanceType()) {
//                case "LR":
//                    viewHolder.option.setImageResource(R.drawable.ic_att_leaverequest);
//                    break;
                case "LR":
                    viewHolder.option.setImageResource(R.drawable.ic_att_absentinfo);
                    break;
                case "L":
                    viewHolder.option.setImageResource(R.drawable.ic_att_late);
                    break;
                case "A":
                    viewHolder.option.setImageResource(R.drawable.ic_att_absent);
                    break;
                case "blank":
                    if (secAttdStatus == 300)
                        viewHolder.option.setImageResource(R.drawable.ic_radio);
                    else
                        viewHolder.option.setImageResource(R.drawable.ic_att_present);


//                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            TeacherStdnTakeAttendance teacherTakeAttendance = (TeacherStdnTakeAttendance) _context;
//                            if (teacherTakeAttendance.isPanelShown()) {
//
//                            } else {
//                                teacherTakeAttendance.slideup(i, list_filtered);
//                            }
//                        }
//                    });

                    break;
                default:
                    break;
            }
        }

        public void update(List<TeacherAttendStudentObj> listUpdated) {
            list_filtered = listUpdated;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    if (string.isEmpty()) {
                        list_filtered = list;
                    } else {

                        List<TeacherAttendStudentObj> filteredList = new ArrayList<>();
                        for (TeacherAttendStudentObj s : list) {

                            if (s.getRollNumber().toLowerCase().contains(string.toLowerCase())
                                    || s.getStudentName().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }

                        list_filtered = filteredList;

                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<TeacherAttendStudentObj>) results.values;

                    notifyDataSetChanged();
                }
            };
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            ImageView imgProfile, option;
            TextView roll, name, tvNameImage;

            ViewHolder(View itemView) {
                super(itemView);

                option = itemView.findViewById(R.id.option);
                roll = itemView.findViewById(R.id.rollNum);
                name = itemView.findViewById(R.id.name);
                tvNameImage = itemView.findViewById(R.id.tv_name_image);
                imgProfile = itemView.findViewById(R.id.img_profile);
            }

        }
    }
}