package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import myschoolapp.com.gsnedutech.Models.ParentObj;
import myschoolapp.com.gsnedutech.Models.StudentLeavesObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
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

public class StudentLeaveRequest extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = StudentLeaveRequest.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    StudentObj sObj;
    ParentObj pObj;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_leave_req)
    RecyclerView rvLeaveReq;

    @BindView(R.id.tv_no_requests)
    TextView tvNoRequests;

    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    @BindView(R.id.img_newleave)
    ImageView imgNewleave;

    @BindView(R.id.rl_hiddenreason)
    RelativeLayout rlHiddenreason;

    @BindView(R.id.et_fromdate)
    EditText fromdate;
    @BindView(R.id.et_todate)
    EditText todate;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.et_comment)
    EditText etComment;

    @BindView(R.id.img_newleave_cancel)
    ImageView imgLeaveCancel;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    String fromReq = "", toReq = "";

    int selectedTab = 0;

    List<StudentLeavesObj> listLeaves = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_leave_request);
        ButterKnife.bind(this);

        init();
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
            utils.alertDialog(1, StudentLeaveRequest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                getLeaveRequest();
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
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String stdnJson = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(stdnJson, StudentObj.class);

        if (sh_Pref.getBoolean("parent_loggedin", false)) {
            imgNewleave.setVisibility(View.VISIBLE);
            String json = sh_Pref.getString("parentObj", "");
            pObj = gson.fromJson(json, ParentObj.class);
        } else {
            imgNewleave.setVisibility(View.GONE);
        }
        tvPending.setOnClickListener(this);
        tvApproved.setOnClickListener(this);
        tvRejected.setOnClickListener(this);


        imgNewleave.setOnClickListener(view -> {
            showPanel();
        });

        imgLeaveCancel.setOnClickListener(view -> {
            showPanel();
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

        findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = "";
                if (!etComment.getText().toString().equalsIgnoreCase("")) {
                    comment = etComment.getText().toString();
                }

                if (NetworkConnectivity.isConnected(StudentLeaveRequest.this)) {
                    if (validate()) {
                        postLeave(fromReq, toReq, spinner.getSelectedItem().toString(), comment);
                    } else {
                        utils.alertDialog(3, StudentLeaveRequest.this, "Error", "All fields are Required, \nToDate should be greater than from date.",
                                "", getString(R.string.action_close), true);
                    }
                } else {
                    utils.alertDialog(1, StudentLeaveRequest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close), true);
                }
            }
        });
    }


    public boolean isPanelShown() {
        return rlHiddenreason.getVisibility() == View.VISIBLE;
    }

    void showPanel() {
        if (isPanelShown()) {
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
            rlHiddenreason.startAnimation(bottomDown);
            rlHiddenreason.setVisibility(View.GONE);
        } else {
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            rlHiddenreason.startAnimation(bottomUp);
            rlHiddenreason.setVisibility(View.VISIBLE);
        }
    }


    public void callDatePicker(final View view) {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
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

    void getLeaveRequest() {

        utils.showLoader(StudentLeaveRequest.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL- " + AppUrls.GetTotalLeaveRequestsForStudent + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&studentId=" + getIntent().getStringExtra("studentId"));

        Request request = new Request.Builder()
                .url(AppUrls.GetTotalLeaveRequestsForStudent + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&studentId=" + getIntent().getStringExtra("studentId"))
                .headers(MyUtils.addHeaders(sh_Pref))
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

                String resp = response.body().string();

                if (response.body() != null) {
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("studentLeaveReqArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentLeavesObj>>() {
                            }.getType();

                            listLeaves.clear();
                            listLeaves.addAll(gson.fromJson(jsonArr.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (selectedTab == 0)
                                        tvPending.callOnClick();
                                    else if (selectedTab == 1)
                                        tvApproved.callOnClick();
                                    else tvRejected.callOnClick();
                                }
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)) { //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, StudentLeaveRequest.this, message, sh_Pref);
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pending:
                selectedTab = 0;
                tvPending.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvPending.setTextColor(Color.WHITE);
                tvApproved.setBackground(null);
                tvApproved.setTextColor(Color.rgb(73, 73, 73));
                tvApproved.setAlpha(0.75f);
                tvRejected.setBackground(null);
                tvRejected.setTextColor(Color.rgb(73, 73, 73));
                tvRejected.setAlpha(0.75f);
                setDatatoList("LR", "Pending");
                break;
            case R.id.tv_approved:
                selectedTab = 1;
                tvApproved.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvApproved.setTextColor(Color.WHITE);
                tvPending.setBackground(null);
                tvPending.setTextColor(Color.rgb(73, 73, 73));
                tvPending.setAlpha(0.75f);
                tvRejected.setBackground(null);
                tvRejected.setTextColor(Color.rgb(73, 73, 73));
                tvRejected.setAlpha(0.75f);
                setDatatoList("ALR", "Approved");
                break;
            case R.id.tv_rejected:
                selectedTab = 2;
                tvRejected.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvRejected.setTextColor(Color.WHITE);
                tvApproved.setBackground(null);
                tvApproved.setTextColor(Color.rgb(73, 73, 73));
                tvApproved.setAlpha(0.75f);
                tvPending.setBackground(null);
                tvPending.setTextColor(Color.rgb(73, 73, 73));
                tvPending.setAlpha(0.75f);
                setDatatoList("RLR", "Rejected");
                break;
        }
    }

    private void setDatatoList(String leaveState, String status) {
        List<StudentLeavesObj> list = new ArrayList<>();
        for (StudentLeavesObj lrq : listLeaves) {
            if (lrq.getLeaveRequestStatus().equalsIgnoreCase(leaveState)) {
                list.add(lrq);
            }
        }
        if (list.size() > 0) {
            tvNoRequests.setVisibility(View.GONE);
            rvLeaveReq.setVisibility(View.VISIBLE);
            rvLeaveReq.setLayoutManager(new LinearLayoutManager(StudentLeaveRequest.this));
            rvLeaveReq.setAdapter(new LeavesListAdapter(list));
        } else {
            tvNoRequests.setVisibility(View.VISIBLE);
            rvLeaveReq.setVisibility(View.GONE);
            tvNoRequests.setText(String.format("No %s Requests", status));
        }

    }


    class LeavesListAdapter extends RecyclerView.Adapter<LeavesListAdapter.ViewHolder> {

        List<StudentLeavesObj> listLeave = new ArrayList<>();

        LeavesListAdapter(List<StudentLeavesObj> listLeave) {
            this.listLeave = listLeave;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(StudentLeaveRequest.this).inflate(R.layout.item_leave, parent, false);
            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvNAme.setText(getIntent().getStringExtra("studentName"));
            holder.tvReason.setText(listLeave.get(position).getReason());
            try {
                holder.tvNumDays.setText(getDateDifference(new SimpleDateFormat("yyyy-MM-dd").parse(listLeave.get(position).getLeaveFrom()), new SimpleDateFormat("yyyy-MM-dd").parse(listLeave.get(position).getLeaveTo())) + " days");
                holder.tvStartDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listLeave.get(position).getLeaveFrom())));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            switch (listLeave.get(position).getLeaveRequestStatus()) {
                case "LR":
                    holder.tvStatus.setText("Pending");
                    holder.tvStatus.setTextColor(Color.parseColor("#F68D1E"));
                    break;
                case "ALR":
                    holder.tvStatus.setText("Approved");
                    holder.tvStatus.setTextColor(Color.parseColor("#037855"));
                    break;
                case "RLR":
                    holder.tvStatus.setText("Rejected");
                    holder.tvStatus.setTextColor(Color.parseColor("#B40E22"));
                    break;
            }


        }

        @Override
        public int getItemCount() {
            return listLeave.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvNAme, tvStartDate, tvNumDays, tvReason, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvNAme = itemView.findViewById(R.id.tv_student_name);
                tvStartDate = itemView.findViewById(R.id.tv_start_days);
                tvNumDays = itemView.findViewById(R.id.tv_num_days);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvReason = itemView.findViewById(R.id.tv_reason);
            }
        }
    }


    int getDateDifference(Date startDate, Date endDate) {

        int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return diffInDays >= 0 ? diffInDays + 1 : 0;

    }


    @Override
    public void onBackPressed() {

        if (rlHiddenreason.isShown())
            rlHiddenreason.setVisibility(View.GONE);
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

    }


    private void postLeave(String... strings) {
        utils.showLoader(StudentLeaveRequest.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        String URL = "";

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


        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Log.v(TAG, "URL " + URL);
        Log.v(TAG, "Body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .headers(MyUtils.addHeaders(sh_Pref))
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
                runOnUiThread(() -> {
                    utils.dismissDialog();
//                        showPanel();
                });
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Response - " + jsonResp);
                    try {
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(StudentLeaveRequest.this, "Leave Request Suceessfully Submited", Toast.LENGTH_SHORT).show();
                                    showPanel();
                                    tvPending.callOnClick();
                                    onResume();

                                }
                            });
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, StudentLeaveRequest.this, message, sh_Pref);
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });

    }


}