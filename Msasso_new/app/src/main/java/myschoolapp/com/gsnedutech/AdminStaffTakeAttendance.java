/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 31/10/19 3:26 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import myschoolapp.com.gsnedutech.Models.AdminStaffAttendanceObj;
import myschoolapp.com.gsnedutech.Models.AdminStaffObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminStaffTakeAttendance extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = AdminStaffTakeAttendance.class.getName();

    @BindView(R.id.search)
    SearchView searchStaff;
    @BindView(R.id.absent)
    TextView absent;
    @BindView(R.id.legend)
    TextView legend;
    @BindView(R.id.rv_List)
    RecyclerView rvStaffList;
    @BindView(R.id.btn_marksel)
    Button btnMarksel;
    @BindView(R.id.view_bar)
    View viewBar;
    @BindView(R.id.img_dp)
    ImageView imgDp;
    @BindView(R.id.tv_name_dp)
    TextView tvNameDp;
    @BindView(R.id.tv_rollnum)
    TextView tvRollnum;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.sp_mark)
    Spinner spMark;
    @BindView(R.id.sp_reason)
    Spinner spReason;
    @BindView(R.id.et_otherreason)
    EditText etOtherreason;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_markleave)
    TextView tvMarkleave;
    @BindView(R.id.rl_hiddenreason)
    RelativeLayout rlHiddenreason;
    @BindView(R.id.ll_lr)
    LinearLayout llLr;
    @BindView(R.id.tv_lr)
    TextView tvLr;
    @BindView(R.id.tv_lr_from)
    TextView tvLrFrom;
    @BindView(R.id.tv_lr_to)
    TextView tvLrTo;
    @BindView(R.id.tv_lr_reason)
    TextView tvLrReason;
    @BindView(R.id.tv_lr_descp)
    TextView tvLrDescp;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.tv_holiday)
    TextView tvHoliday;

    @BindView(R.id.tv_title)
    TextView tvTitle;


    String staffAttendanceReportId = "", branchId, userId, reqDate;
    List<AdminStaffAttendanceObj> staffList = new ArrayList<>();

    int selpos = -1;
    List<AdminStaffAttendanceObj> staffFilteredList;

    StaffAttandenceAdapter staffAttandenceAdapter;

    SimpleDateFormat sdf;

    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMM, yyyy");
    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");

    List<String> markAs = new ArrayList<String>();
    List<String> reasons = new ArrayList<String>();

    int secAttdStatus, staffDayReport;
    String selYear,selMon,selDate;
    String[] holidayArray = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_staff_attendance);
        ButterKnife.bind(this);

        tvTitle.setText("Today");

        init();

        if (getIntent().hasExtra("staffAttendanceReportId"))
            staffAttendanceReportId = getIntent().getStringExtra("staffAttendanceReportId");

        if (getIntent().hasExtra("reqDate")) {
            reqDate = getIntent().getStringExtra("reqDate");
            tvTitle.setText("" + getIntent().getStringExtra("dispdate"));
            new GetStaffs().execute();

        } else {
            reqDate = sdf.format(Calendar.getInstance().getTime());
            tvTitle.setText("Today");

            selMon = ""+(Calendar.getInstance().get(Calendar.MONTH)+1);
            selYear= ""+(Calendar.getInstance().get(Calendar.YEAR));
            selDate= ""+(Calendar.getInstance().get(Calendar.DATE));


            Log.v(TAG,"Month - "+selMon+" Year - "+selYear+" date - "+selDate);

            new GetHolidaysForStaffAttendance().execute();

        }


        spMark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    tvMarkleave.setVisibility(View.VISIBLE);
                    tvMarkleave.setText("Mark as " + spMark.getSelectedItem().toString());
                } else {
                    tvMarkleave.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchStaff.setOnQueryTextListener(this);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void init() {
        branchId = getIntent().getStringExtra("branchId");
        userId = getIntent().getStringExtra("userId");
        rvStaffList.setLayoutManager(new LinearLayoutManager(this));

        sdf = new SimpleDateFormat("yyyy-MM-dd");


        markAs.add("Select Status");
        markAs.add("UnInformed Absent");
        markAs.add("Informed Absent");
        markAs.add("Present");
        markAs.add("Late");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, markAs);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spMark.setAdapter(dataAdapter);

        reasons.add("Select Reason");
        reasons.add("Sick");
        reasons.add("Vacation");
        reasons.add("Others");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reasons);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spReason.setAdapter(dataAdapter2);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        staffAttandenceAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        staffAttandenceAdapter.getFilter().filter(s);
        return false;
    }


    public boolean isPanelShown() {
        return rlHiddenreason.getVisibility() == View.VISIBLE;
    }

    public void slideup(int pos, List<AdminStaffAttendanceObj> filteredlist) {

        this.selpos = pos;
        this.staffFilteredList = filteredlist;   //get filtered list

        tvNameDp.setText(staffFilteredList.get(pos).getUserName().charAt(0) + "");
        tvRollnum.setText(staffFilteredList.get(pos).getUserName());
        tvName.setText(staffFilteredList.get(pos).getEmailId());

        if (isPanelShown()) {
            Animation bottomDown = AnimationUtils.loadAnimation(AdminStaffTakeAttendance.this, R.anim.bottom_down);
            rlHiddenreason.startAnimation(bottomDown);
            rlHiddenreason.setVisibility(View.GONE);
        } else {
            Animation bottomUp = AnimationUtils.loadAnimation(AdminStaffTakeAttendance.this, R.anim.bottom_up);
            rlHiddenreason.startAnimation(bottomUp);
            rlHiddenreason.setVisibility(View.VISIBLE);
        }

    }

    @OnClick({R.id.btn_marksel, R.id.view_bar, R.id.tv_markleave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_marksel:
                new PostAttendance().execute();
                break;
            case R.id.view_bar:
                break;
            case R.id.tv_markleave:

                if (spMark.getSelectedItemPosition() != 0 && spReason.getSelectedItemPosition() != 0) {

                    staffFilteredList.get(selpos).setPreviousState(staffFilteredList.get(selpos).getAttendanceType());

                    if (spMark.getSelectedItem().toString().equalsIgnoreCase("UnInformed Absent"))
                        staffFilteredList.get(selpos).setAttendanceType("A");
                    else if (spMark.getSelectedItem().toString().equalsIgnoreCase("Informed Absent"))
                        staffFilteredList.get(selpos).setAttendanceType("LR");
                    else if (spMark.getSelectedItem().toString().equalsIgnoreCase("Late"))
                        staffFilteredList.get(selpos).setAttendanceType("L");
                    else if (spMark.getSelectedItem().toString().equalsIgnoreCase("Present"))
                        staffFilteredList.get(selpos).setAttendanceType("P");

                    staffFilteredList.get(selpos).setReason(spReason.getSelectedItem().toString());

                    for (int i = 0; i < staffList.size(); i++) {
                        for (int j = 0; j < staffFilteredList.size(); j++) {
                            if (staffFilteredList.get(j).getUserId() == staffList.get(i).getUserId()) {
                                staffList.set(i, staffFilteredList.get(j));
                            }
                        }

                        Log.v(TAG, " Pos " + i + " name - " + staffList.get(i).getUserName() + " attState - " + staffList.get(i).getAttendanceType());
                        Log.v(TAG, " Pos " + i + " name - " + staffList.get(i).getUserName() + " Prev - " + staffList.get(i).getPreviousState());
                    }
                    staffAttandenceAdapter.notifyDataSetChanged();
                    slideup(0, staffList);


                } else {
                    Toast.makeText(this, "Please Select the Reason", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (rlHiddenreason.isShown())
            rlHiddenreason.setVisibility(View.GONE);

        else
            super.onBackPressed();
    }

    //    Posting the Student Subbmissions to the Serverfd
    public class PostAttendance extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(AdminStaffTakeAttendance.this);
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


            JSONObject jsonObject = new JSONObject();
            String url = null;
            try {

                jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
                jsonObject.put("attenDate", "" + reqDate);
                jsonObject.put("branchId", branchId);
                jsonObject.put("totalStaff", staffList.size());

                JSONArray jsonArray = new JSONArray();
                url = new AppUrls().Attendance_PostInsertStafff;
                if (secAttdStatus == 300) {

                    jsonObject.put("createdBy", userId);

                    for (int i = 0; i < staffList.size(); i++) {
                        if (!staffList.get(i).getAttendanceType().equalsIgnoreCase("blank")) {
                            JSONObject jObj = new JSONObject();
                            jObj.put("userId", "" + staffList.get(i).getUserId());
                            jObj.put("reason", "" + staffList.get(i).getReason());
                            jObj.put("attendanceType", "" + staffList.get(i).getAttendanceType());
                            jsonArray.put(jObj);
                        }
                    }

                    jsonObject.put("insertRecords", jsonArray);

                } else if (secAttdStatus == 200) {
                    jsonObject.put("updatedBy", userId);
                    jsonObject.put("staffAttendanceReportId", staffAttendanceReportId);

                    for (int i = 0; i < staffList.size(); i++) {
                        if (!staffList.get(i).getAttendanceType().equalsIgnoreCase(staffList.get(i).getPreviousState())) {
                            JSONObject jObj = new JSONObject();
                            jObj.put("userId", "" + staffList.get(i).getUserId());
                            jObj.put("reason", "" + staffList.get(i).getReason());
                            jObj.put("attendanceType", "" + staffList.get(i).getAttendanceType());
                            jObj.put("attendanceId", "" + staffList.get(i).getAttendanceId());
                            jObj.put("previousState", "" + staffList.get(i).getPreviousState());
                            jsonArray.put(jObj);
                        }


                    }


                    jsonObject.put("updateRecords", jsonArray);
                    url = new AppUrls().Attendance_PostUpdateStaff;
                }


                Log.v(TAG, "Json Obj - " + jsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

            Log.v(TAG, "" + url);
            Log.v(TAG, "" + jsonObject);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "Attendanc Update and Insertion Status responce - " + jsonResp);

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

                        Toast.makeText(AdminStaffTakeAttendance.this, "Attendance Posted Sucessfully", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();

        }
    }

    public class StaffAttandenceAdapter extends RecyclerView.Adapter<StaffAttandenceAdapter.ViewHolder> implements Filterable {

        Context _context;
        List<AdminStaffAttendanceObj> list, list_filtered;

        StaffAttandenceAdapter(Context context, List<AdminStaffAttendanceObj> list) {
            this._context = context;
            this.list = list;
            this.list_filtered = list;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(_context).inflate(R.layout.item_attend_staff, viewGroup, false);
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

            viewHolder.name.setText(list_filtered.get(i).getUserName());
            viewHolder.dp.setText(list_filtered.get(i).getUserName().charAt(0) + "");
            viewHolder.contact.setText(list_filtered.get(i).getEmailId() + " " + list_filtered.get(i).getContactNo());

//            if (list_filtered.get(i).getProfilePic().equalsIgnoreCase("NA")) {
//                viewHolder.tvNameImage.setText((list_filtered.get(i).getStudentName().charAt(0) + "").toUpperCase());
//                viewHolder.tvNameImage.setVisibility(View.VISIBLE);
//                viewHolder.imgProfile.setVisibility(View.GONE);
//            } else {
//                viewHolder.tvNameImage.setVisibility(View.GONE);
//                viewHolder.imgProfile.setVisibility(View.VISIBLE);
//                Picasso.with(this).load(new AppUrls().GetstudentProfilePic + list_filtered.get(i).getProfilePic()).
//                        placeholder(R.drawable.default_student).into(viewHolder.imgProfile);
//
//            }

            Log.v(TAG, " Pos " + i + " name - " + list_filtered.get(i).getUserName() + " attState - " + list_filtered.get(i).getAttendanceType());

            switch (list_filtered.get(i).getAttendanceType()) {
                case "LR":
                    viewHolder.option.setImageResource(R.drawable.ic_att_leaverequest);
                    break;
                case "ALR":
                    viewHolder.option.setImageResource(R.drawable.ic_att_absentcross);
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
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdminStaffTakeAttendance adminStaffTakeAttendance = (AdminStaffTakeAttendance) _context;
                    if (adminStaffTakeAttendance.isPanelShown()) {

                    } else {
                        new GetStaffLeaveRequestInfo().execute("" + list_filtered.get(i).getUserId(), reqDate);
                        adminStaffTakeAttendance.slideup(i, list_filtered);
                    }
                }
            });
        }


        public void update(List<AdminStaffAttendanceObj> listUpdated) {
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

                        List<AdminStaffAttendanceObj> filteredList = new ArrayList<>();
                        for (AdminStaffAttendanceObj s : list) {

                            if (s.getUserName().toLowerCase().contains(string.toLowerCase())) {
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
                    list_filtered = (List<AdminStaffAttendanceObj>) results.values;

                    notifyDataSetChanged();
                }
            };
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            ImageView option;
            TextView contact, name, dp;

            ViewHolder(View itemView) {
                super(itemView);

                dp = itemView.findViewById(R.id.tv_name_image);
                option = itemView.findViewById(R.id.option);
                contact = itemView.findViewById(R.id.tv_contact);
                name = itemView.findViewById(R.id.tv_name);
            }

        }
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

                        Log.v(TAG,"holidays "+ Arrays.toString(holidayArray));

                        List<String> list = Arrays.asList(holidayArray);
                        if (list.contains(selDate)){
                            ll.setVisibility(View.GONE);
                            tvHoliday.setVisibility(View.VISIBLE);
                        }else{
                            ll.setVisibility(View.VISIBLE);
                            tvHoliday.setVisibility(View.GONE);
                            new GetStaffs().execute();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    public class GetStaffs extends AsyncTask<String, Void, String> {

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

            Log.v(TAG, "URL - " + AppUrls.GetStaffForAttendanceAnalysis +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId);


            Request request = new Request.Builder()
                    .url(AppUrls.GetStaffForAttendanceAnalysis +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Staff list - " + jsonResp);

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
                        Type type = new TypeToken<List<AdminStaffAttendanceObj>>() {
                        }.getType();

                        staffList.clear();
                        staffList.addAll(gson.fromJson(jsonArr.toString(), type));

                        for (int i = 0; i < staffList.size(); i++) {
                            staffList.get(i).setAttendance("blank");
                        }


                        new GetStaffDayAttendanceReport().execute(reqDate);

                        Log.v("TAG", "staff list size - " + staffList.size());


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

            Log.v(TAG, "Admin StaffAttendance request - " + new AppUrls().GetStaffDayAttendanceReport +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&currentDate=" + strings[0] + "&branchId=" + branchId);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetStaffDayAttendanceReport +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&currentDate=" + strings[0] + "&branchId=" + branchId)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Admin StaffAttendance responce - " + jsonResp);

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
                        staffDayReport = 200;
                    } else {
                        staffDayReport = 300;
                    }

                } catch (Exception e) {

                }

                new GetAbsentStaffByMonthByDate().execute();

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

        }

    }

    //    Getting Absent Staff
    public class GetAbsentStaffByMonthByDate extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(AdminStaffTakeAttendance.this);
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

            Log.v(TAG, "URL - Attendance Absent request - " + new AppUrls().GetAbsentStaffByMonth +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&currentDate=" + reqDate);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetAbsentStaffByMonth +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&currentDate=" + reqDate)
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

                            for (AdminStaffAttendanceObj staffList : staffList) {
                                for (AdminStaffObj absentStudentObj : staffAbsentList) {
                                    if ((staffList.getUserId() + "").equalsIgnoreCase(absentStudentObj.getUserId())) {
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

                    staffAttandenceAdapter = new StaffAttandenceAdapter(AdminStaffTakeAttendance.this, staffList);
                    rvStaffList.setAdapter(staffAttandenceAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            loading.dismiss();
        }
    }

    public class GetStaffLeaveRequestInfo extends AsyncTask<String, Void, String> {

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

            Log.v(TAG, "URL - " + AppUrls.Attendance_GetStaffLeaveRequestInfo +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&userId=" + strings[0] + "&date=" + strings[1]);


            Request request = new Request.Builder()
                    .url(AppUrls.Attendance_GetStaffLeaveRequestInfo +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&userId=" + strings[0] + "&date=" + strings[1])
                    .build();


            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Staff list - " + jsonResp);

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
//                        JSONArray jsonArr = ParentjObject.getJSONArray("staffForAttendance");

                        llLr.setVisibility(View.VISIBLE);
                        tvLr.setVisibility(View.VISIBLE);

                        tvLrFrom.setText("" + displaysdf.format(serversdf.parse(ParentjObject.getString("leaveFrom"))));
                        tvLrTo.setText("" + displaysdf.format(serversdf.parse(ParentjObject.getString("LeaveTo"))));
                        tvLrReason.setText("" + ParentjObject.getString("reason"));
                        tvLrDescp.setText("" + ParentjObject.getString("description"));

                    } else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {

                        llLr.setVisibility(View.GONE);
                        tvLr.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
