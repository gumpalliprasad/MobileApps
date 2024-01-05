package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.StudentsLeaveReq;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AllStudentsLeaveRequests extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = AllStudentsLeaveRequests.class.getName();

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.rv_request_list)
    RecyclerView rvRequestList;
    @BindView(R.id.tv_no_leaverequests)
    TextView tvNoLeaverequests;
    @BindView(R.id.rl_Optionshidden)
    RelativeLayout rlOptHidden;
    @BindView(R.id.rl_hiddenreason)
    RelativeLayout rlHiddenreason;

    Calendar calendar;
    Date reqdate;

    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMMM, yyyy");

    String userId, sectionId;
    List<StudentsLeaveReq> reqList = new ArrayList<>();

    int selpos = 0;

    SharedPreferences sh_Pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students_leave_requests);
        ButterKnife.bind(this);


        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        http://45.127.101.187:9010/getTotalLeaveRequestsDetailsByDate?sectionId=6&schemaName=rankrplus&date=2019-10-17

        getLeaveRquests(sectionId,serversdf.format(reqdate));

    }

    private void init() {
        sectionId = getIntent().getStringExtra("sectionId");

        calendar = Calendar.getInstance();
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        updateLabel(displaysdf.format(new Date()), 0);

        rvRequestList.setLayoutManager(new LinearLayoutManager(this));



        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            TeacherObj teacherObj = gson.fromJson(json, TeacherObj.class);

            userId = "" + teacherObj.getUserId();

        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {

            Gson gson = new Gson();
            String json = sh_Pref.getString("adminObj", "");
            AdminObj adminObj = gson.fromJson(json, AdminObj.class);

            userId = "" + adminObj.getUserId();
        }
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
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dayOfMonth + "/" + month + 1 + "/" + year);
            updateLabel(displaysdf.format(date), 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void updateLabel(String date, int change) {
        try {
            Date dateObj = displaysdf.parse(date);
            calendar.setTime(dateObj);
            if (change != 0)
                calendar.add(Calendar.DATE, change);
            dateObj = calendar.getTime();
            tvDate.setText(displaysdf.format(dateObj));

            reqdate = displaysdf.parse(tvDate.getText().toString());
            getLeaveRquests(sectionId,serversdf.format(reqdate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.bottom_absent, R.id.ll_approve, R.id.ll_reject})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bottom_absent:
                break;
            case R.id.ll_approve:
//                markLeaveRequest(1);
                break;
            case R.id.ll_reject:
//                markLeaveRequest(0);
                break;
        }
    }

//    private void markLeaveRequest(int Status) {
//            if (!attendStatus.equalsIgnoreCase("LR")) {
//                studfliteredList.get(selpos).setAttendanceType(attendStatus);
//                int index = studList.indexOf(studfliteredList.get(selpos));
//                studList.get(index).setAttendanceType(attendStatus);
//                studAttandenceAdapter.update(studfliteredList);   //updating the filtered list in adapter class
//                slideup(selpos, studfliteredList); //closing the list after option selected
//            } else {
//                rlOptHidden.setVisibility(View.GONE);
////
////            TextView name = findViewById(R.id.name_bottom);
////            TextView roll = findViewById(R.id.roll_num);
////            roll.setText(staffFilteredList.get(pos).getStudentRollnumber());
////            name.setText(staffFilteredList.get(pos).getStudentName());
//                rlHiddenreason.setVisibility(View.VISIBLE);
//            }
//
//
//    }

//    public void slideup(int pos) {
//
//        this.selpos = pos;                    //get position
//
//        if (isPanelShown()) {
//            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
//            rlOptHidden.startAnimation(bottomDown);
//            rlOptHidden.setVisibility(View.GONE);
//        } else {
//            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
//            rlOptHidden.startAnimation(bottomUp);
//            rlOptHidden.setVisibility(View.VISIBLE);
//        }
//
//    }

//    public boolean isPanelShown() {
//        return rlOptHidden.getVisibility() == View.VISIBLE;
//    }

    @Override
    public void onBackPressed() {

        if (rlOptHidden.isShown())
            rlOptHidden.setVisibility(View.GONE);

        else if (rlHiddenreason.isShown())
            rlHiddenreason.setVisibility(View.GONE);

        else
            super.onBackPressed();
    }

    //    Getting the Students for attendance
    void getLeaveRquests(String sectionId, String date){
        ProgressDialog  loading = new ProgressDialog(AllStudentsLeaveRequests.this);
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

        Log.v(TAG, "Leave request List - " + new AppUrls().Attendance_GetTotalLeaveRequestsDetailsByDate+"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&sectionId=" + sectionId + "&date=" + date);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetTotalLeaveRequestsDetailsByDate +"schemaName=" +sh_Pref.getString(AppConst.SCHEMA,"")+ "&sectionId=" + sectionId + "&date=" + date)
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

                                    JSONArray jsonArr = ParentjObject.getJSONArray("studentsLeaveReq");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<StudentsLeaveReq>>() {
                                    }.getType();

                                    reqList.clear();
                                    reqList.addAll(gson.fromJson(jsonArr.toString(), type));
                                    Log.v(TAG, "studList - " + reqList.size());

                                    StudAttandenceAdapter studAttandenceAdapter = new StudAttandenceAdapter(AllStudentsLeaveRequests.this, reqList);
                                    rvRequestList.setAdapter(studAttandenceAdapter);
                                    rvRequestList.setVisibility(View.VISIBLE);
                                    tvNoLeaverequests.setVisibility(View.GONE);

                                } else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {
                                    rvRequestList.setVisibility(View.GONE);
                                    tvNoLeaverequests.setVisibility(View.VISIBLE);
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

    public class StudAttandenceAdapter extends RecyclerView.Adapter<StudAttandenceAdapter.ViewHolder> {

        Context _context;
        List<StudentsLeaveReq> list;

        StudAttandenceAdapter(Context context, List<StudentsLeaveReq> list) {
            this._context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(_context).inflate(R.layout.attend_leaverequest, viewGroup, false);
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

            viewHolder.name.setText(list.get(i).getStudentName());
            viewHolder.tvNameImage.setText((list.get(i).getStudentName().charAt(0) + "").toUpperCase());
            viewHolder.tvNameImage.setVisibility(View.VISIBLE);

            switch (list.get(i).getLeaveReqestStatus()) {
                case "LR":
                    viewHolder.llOptions.setVisibility(View.VISIBLE);
                    viewHolder.imgStatus.setVisibility(View.GONE);
                    break;
                case "ALR":
                    viewHolder.llOptions.setVisibility(View.GONE);
                    viewHolder.imgStatus.setVisibility(View.VISIBLE);
                    viewHolder.imgStatus.setImageResource(R.drawable.ic_accepted);
                    break;
                case "RLR":
                    viewHolder.llOptions.setVisibility(View.GONE);
                    viewHolder.imgStatus.setVisibility(View.VISIBLE);
                    viewHolder.imgStatus.setImageResource(R.drawable.ic_rejected);
                    break;
                default:
                    break;
            }

            viewHolder.tvAccepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AllStudentsLeaveRequests.this, "Accepted", Toast.LENGTH_SHORT).show();
                    updateStudentLeaveRequest(userId, "" + list.get(i).getStudentLeaveReqId(), "1", "");
                }
            });

            viewHolder.tvRejected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AllStudentsLeaveRequests.this, "Rejected", Toast.LENGTH_SHORT).show();
                    updateStudentLeaveRequest(userId, "" + list.get(i).getStudentLeaveReqId(), "2", "");

                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llOptions;
            ImageView imgStatus;
            ImageView imgProfile;
            TextView  name, tvNameImage, tvAccepted, tvRejected;
            ;

            ViewHolder(View itemView) {
                super(itemView);

                llOptions = itemView.findViewById(R.id.ll_options);
                name = itemView.findViewById(R.id.name);
                tvNameImage = itemView.findViewById(R.id.tv_name_image);
                imgProfile = itemView.findViewById(R.id.img_profile);
                imgStatus = itemView.findViewById(R.id.img_status);
                tvAccepted = itemView.findViewById(R.id.tv_accepted);
                tvRejected = itemView.findViewById(R.id.tv_rejected);
            }

        }
    }

    void updateStudentLeaveRequest(String updatedBy, String studentLeaveReqId, String isApproved, String teacherComments){
        ProgressDialog loading = new ProgressDialog(AllStudentsLeaveRequests.this);
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

            jsonObject.put("schemaName", sh_Pref.getString(AppConst.SCHEMA,""));
            jsonObject.put("updatedBy", updatedBy);


            JSONObject jsonObj = new JSONObject();
            jsonObj.put("studentLeaveReqId", studentLeaveReqId);
            jsonObj.put("isApproved", isApproved);
            jsonObj.put("updatedBy", updatedBy);
            jsonObj.put("teacherComments", teacherComments);


            JSONArray jsArray = new JSONArray();
            jsArray.put(jsonObj);
            jsonObject.put("updateLeaveReq", jsArray);

            Log.v(TAG, String.valueOf(jsonObject));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + new AppUrls().UpdateStudentLeaveRequest);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(new AppUrls().UpdateStudentLeaveRequest)
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

                    Log.v(TAG, "UpdateStaffLeaveRequest responce - " + jsonResp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);

                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//                        Toast.makeText(getActivity(), "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                    getLeaveRquests(sectionId,serversdf.format(reqdate));
                                } else {
//                        Toast.makeText(getActivity(), "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                    getLeaveRquests(sectionId,serversdf.format(reqdate));
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

}