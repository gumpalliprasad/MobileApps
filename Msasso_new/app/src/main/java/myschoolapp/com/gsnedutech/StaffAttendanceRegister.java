/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 30/10/19 10:37 AM
 *
 */

package myschoolapp.com.gsnedutech;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

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
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.AdminStaffObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StaffAttendanceRegister extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "SriRam -" + StaffAttendanceRegister.class.getName();


    @BindView(R.id.tv_lrcout)
    TextView tvLrcout;
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
    @BindView(R.id.ll_attOverView)
    LinearLayout llAttOverView;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.tv_holiday)
    TextView tvHoliday;
    @BindView(R.id.img_nextdate)
    ImageView imgNextdate;

    Calendar calendar;
    Date reqdate;
    Boolean pause = false;

    List<AdminStaffObj> staffList = new ArrayList<>();

    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMMM, yyyy");

    StaffAttandenceAdapter staffAttandenceAdapter;
    int secAttdStatus, staffDayReport;
    String staffAttendanceReportId, branchId, roleId, userId;


    String selYear, selMon, selDate;

    String[] holidayArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_attendance_register);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Staff Attendance Register");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
    }

    private void init() {
        branchId = getIntent().getStringExtra("branchId");
        roleId = getIntent().getStringExtra("roleId");
        userId = getIntent().getStringExtra("userId");

        calendar = Calendar.getInstance();
        updateLabel(displaysdf.format(new Date()), 0);

        rvStdnList.setLayoutManager(new LinearLayoutManager(this));
    }


    @OnClick({R.id.tv_lrcout, R.id.tv_date, R.id.tv_edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_lrcout:
                if (!tvLrcout.getText().toString().equalsIgnoreCase("LR : 0")) {
                    Intent lrIntent = new Intent(StaffAttendanceRegister.this, AdminStaffDayLeaveRequests.class);
                    lrIntent.putExtra("branchId", "" + branchId);
                    lrIntent.putExtra("userId", "" + userId);
                    startActivity(lrIntent);
                } else {
                    Toast.makeText(StaffAttendanceRegister.this, "No  Leave Requests", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_edit:
                Intent staffAttIntent = new Intent(StaffAttendanceRegister.this, AdminStaffTakeAttendance.class);
                staffAttIntent.putExtra("branchId", branchId);
                staffAttIntent.putExtra("roleId", roleId);
                staffAttIntent.putExtra("userId", userId);
                staffAttIntent.putExtra("dispdate", "" + displaysdf.format(reqdate));
                staffAttIntent.putExtra("reqDate", "" + serversdf.format(reqdate));
                staffAttIntent.putExtra("staffAttendanceReportId", staffAttendanceReportId);
                startActivity(staffAttIntent);
                break;
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
            calendar.setTime(dateObj);
            if (change != 0)
                calendar.add(Calendar.DATE, change);
            dateObj = calendar.getTime();
            tvDate.setText(displaysdf.format(dateObj));

            selMon = "" + (calendar.get(Calendar.MONTH) + 1);
            selYear = "" + (calendar.get(Calendar.YEAR));
            selDate = "" + (calendar.get(Calendar.DATE));


            Log.v(TAG, "Month - " + selMon + " Year - " + selYear + " date - " + selDate);

            reqdate = displaysdf.parse(tvDate.getText().toString());

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

            new GetHolidaysForStaffAttendance().execute();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pause)
            new GetStaffDayAttendanceReport().execute(serversdf.format(reqdate));
        else
            pause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class GetHolidaysForStaffAttendance extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

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


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "Json Obj - " + jsonObject);
            RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

            Log.v(TAG, "" + new AppUrls().GetHolidaysForStaffAttendance);
            Log.v(TAG, "" + jsonObject);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetHolidaysForStaffAttendance)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "GetHolidaysForStaffAttendance responce - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                        //Split string with comma
                        holidayArray = ParentjObject.getString("holidayDates").split(",");

                        Log.v(TAG, "holidays " + Arrays.toString(holidayArray));

                        List<String> list = Arrays.asList(holidayArray);
                        if (list.contains(selDate)) {
                            llAttOverView.setVisibility(View.GONE);
                            rvStdnList.setVisibility(View.GONE);
                            tvEdit.setVisibility(View.GONE);
                            tvHoliday.setVisibility(View.VISIBLE);
                        } else {
                            llAttOverView.setVisibility(View.VISIBLE);
                            rvStdnList.setVisibility(View.VISIBLE);
                            tvEdit.setVisibility(View.VISIBLE);
                            tvHoliday.setVisibility(View.GONE);
                            new GetStaffDayAttendanceReport().execute(serversdf.format(reqdate));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    //    Getting Staff Attendance report on selected Date
    private class GetStaffDayAttendanceReport extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Date d = new Date();
            SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-dd-MM");

            String newDateStr = postFormater.format(d);

            Log.v(TAG, "Admin StaffDayAttendanceReport request - " + new AppUrls().GetStaffDayAttendanceReport +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&currentDate=" + strings[0] + "&branchId=" + branchId);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetStaffDayAttendanceReport +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&currentDate=" + strings[0] + "&branchId=" + branchId)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Admin StaffDayAttendanceReport responce - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                        staffAttendanceReportId = ParentjObject.getString("staffAttendanceReportId");
                        tvAbsentCount.setText(ParentjObject.getString("absentCount"));
                        tvPresentCount.setText(ParentjObject.getString("presentCount"));
                        tvLateCount.setText(ParentjObject.getString("lateCount"));
                        tvEdit.setText("Edit Attendance");
                        staffDayReport = 200;
                    } else {
                        tvAbsentCount.setText(" - ");
                        tvPresentCount.setText(" - ");
                        tvLateCount.setText(" - ");
                        tvEdit.setText("Take Attendance");
                        staffDayReport = 300;
                    }

                } catch (Exception e) {

                }

            } else {

            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    llAttendance.setVisibility(View.VISIBLE);
//                    shimmerAttendance.setVisibility(View.GONE);
//                    skeletonAttendance.setVisibility(View.GONE);
                }
            }, 2000);

            new GetStaffLeaveRequest().execute(serversdf.format(reqdate));
        }

    }

    //    Getting Staff LeaveRequests  on selected Date
    public class GetStaffLeaveRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "Admin StaffAttendance Leaves request - " + new AppUrls().GetStaffLeaveRequestsByDate +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&date=" + strings[0]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetStaffLeaveRequestsByDate +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&date=" + strings[0])
                    .build();


            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();
                Log.v(TAG, "Admin StaffAttendance response - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }


        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        Log.v(TAG, "Count - " + ParentjObject.getJSONArray("staffLeaveReq").length());
                        tvLrcout.setText("LR : " + ParentjObject.getJSONArray("staffLeaveReq").length());
                    } else {
                        tvLrcout.setText("LR : 0");
                    }


                } catch (JSONException e) {
                    Log.v("TAG", "error " + e.getMessage());
                }

            }

            new GetStaff().execute();

        }
    }


    //    Getting the Staff for attendance
    public class GetStaff extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(StaffAttendanceRegister.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "URL - Attendance Staff request - " + new AppUrls().GetStaffForAttendanceAnalysis +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetStaffForAttendanceAnalysis +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "HW Types response - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                        JSONArray jsonArr = ParentjObject.getJSONArray("staffForAttendance");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AdminStaffObj>>() {
                        }.getType();

                        staffList.clear();
                        staffList.addAll(gson.fromJson(jsonArr.toString(), type));
                        Log.v(TAG, "studList - " + staffList.size());

                        staffAttandenceAdapter = new StaffAttandenceAdapter(StaffAttendanceRegister.this, staffList);
                        rvStdnList.setAdapter(staffAttandenceAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();

            new GetAbsentStaffByMonthByDate().execute(serversdf.format(reqdate));

        }
    }

    //    Getting Absent Staff
    public class GetAbsentStaffByMonthByDate extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(StaffAttendanceRegister.this);
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "URL - Attendance Absent request - " + new AppUrls().GetAbsentStaffByMonth +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&currentDate=" + strings[0]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetAbsentStaffByMonth +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&currentDate=" + strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "Attendance Absent response - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        secAttdStatus = 200;
                        if (ParentjObject.getJSONArray("staffAttendanceArray").length() > 0) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("staffAttendanceArray").getJSONObject(0).getJSONArray("staffList");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminStaffObj>>() {
                            }.getType();

                            List<AdminStaffObj> staffAbsentList = new ArrayList<>();
                            staffAbsentList.clear();
                            staffAbsentList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "staffAbsentList - " + staffAbsentList.size());
                            Log.v(TAG, "staffAbsentList - " + staffAbsentList.get(0).getUserId());
                            Log.v(TAG, "staffAbsentList - " + staffAbsentList.get(0).getAttendanceType());

                            for (AdminStaffObj staffList : staffList) {
                                for (AdminStaffObj absentStudentObj : staffAbsentList) {
                                    if (staffList.getUserId().equalsIgnoreCase(absentStudentObj.getUserId())) {
                                        staffList.setPreviousState(absentStudentObj.getAttendanceType());
                                        staffList.setAttendanceType(absentStudentObj.getAttendanceType());
                                        staffList.setAttendanceId(absentStudentObj.getAttendanceId());
                                    }
                                }
                            }


                        }
                    } else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {
                        secAttdStatus = 300;
                    }

                    staffAttandenceAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();
        }
    }

    public class StaffAttandenceAdapter extends RecyclerView.Adapter<StaffAttandenceAdapter.ViewHolder> implements Filterable {

        Context _context;
        List<AdminStaffObj> list, list_filtered;

        StaffAttandenceAdapter(Context context, List<AdminStaffObj> list) {
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

            viewHolder.roll.setText(list_filtered.get(i).getUserName());
            viewHolder.name.setText(list_filtered.get(i).getContactNo() + " . " + list_filtered.get(i).getEmailId());

            if (list_filtered.get(i).getProfilePic().equalsIgnoreCase("NA")) {
                viewHolder.tvNameImage.setText((list_filtered.get(i).getUserName().charAt(0) + "").toUpperCase());
                viewHolder.tvNameImage.setVisibility(View.VISIBLE);
                viewHolder.imgProfile.setVisibility(View.GONE);
            } else {
                viewHolder.tvNameImage.setVisibility(View.GONE);
                viewHolder.imgProfile.setVisibility(View.VISIBLE);
                Picasso.with(StaffAttendanceRegister.this).load(new AppUrls().GetstudentProfilePic + list_filtered.get(i).getProfilePic()).
                        placeholder(R.drawable.user_default).into(viewHolder.imgProfile);

            }


            switch (list_filtered.get(i).getAttendanceType()) {

                case "LR":
                    viewHolder.option.setImageResource(R.drawable.ic_att_leaverequest);
                    break;
                case "ALR":
                    viewHolder.option.setImageResource(R.drawable.ic_att_absentinfo);
                    break;
                case "L":
                    viewHolder.option.setImageResource(R.drawable.ic_att_late);
                    break;
                case "A":
                    viewHolder.option.setImageResource(R.drawable.ic_att_absent);
                    break;
                case "blank":
                    if (secAttdStatus == 300 && staffDayReport == 300)
                        viewHolder.option.setImageResource(R.drawable.ic_radio);
                    else
                        viewHolder.option.setImageResource(R.drawable.ic_att_present);

                    break;
                default:
                    break;
            }
        }

        public void update(List<AdminStaffObj> listUpdated) {
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

                        List<AdminStaffObj> filteredList = new ArrayList<>();
                        for (AdminStaffObj s : list) {

                            if (s.getUserName().toLowerCase().contains(string.toLowerCase()) || s.getContactNo().toLowerCase().contains(string.toLowerCase())
                                    || s.getEmailId().toLowerCase().contains(string.toLowerCase())) {
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
                    list_filtered = (List<AdminStaffObj>) results.values;

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
