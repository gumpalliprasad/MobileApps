package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWork;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetail;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.HomeWorkTypes;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AssignmentsNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, View.OnClickListener {

    private static final String TAG = "SriRam -" + AssignmentsNew.class.getName();

    @BindView(R.id.sp_options)
    Spinner spType;

    @BindView(R.id.ll_regular_courses)
    LinearLayout llRegularCourses;
    @BindView(R.id.tv_regular)
    TextView tvRegular;
    @BindView(R.id.ll_opted_courses)
    LinearLayout llOptedCourses;
    @BindView(R.id.tv_opted)
    TextView tvOpted;

    @BindView(R.id.tv_active)
    TextView tvActive;
    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_completed)
    TextView tvCompleted;
    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    @BindView(R.id.tv_filter)
    TextView tvFilter;

    @BindView(R.id.rv_assignments)
    RecyclerView rvAssignments;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    MyUtils utils = new MyUtils();
    String studentId = "", homeWorktypeId = "";

    String courseId = "";

    int type = 0;
    String hwStatusType = "active";

    String currentMonth, currentYear;
    String filterDate = "";

    List<HomeWork> hwList = new ArrayList<>();
    List<HomeWorkDetails> listAssignments = new ArrayList<>();
    List<HomeWorkTypes> hwTypeList = new ArrayList<>();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_new);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("student_loggedin", false)
                || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            studentId = sObj.getStudentId()+"";
            courseId = sObj.getCourseId()+"";
        } else {
            studentId = getIntent().getStringExtra("studentId");
        }

        findViewById(R.id.cv_filter).setEnabled(false);
        findViewById(R.id.cv_filter).setAlpha(0.5f);

        currentMonth = new SimpleDateFormat("MM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());
        filterDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        tvMonthYear.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date()));

        llOptedCourses.setOnClickListener(this);
        llRegularCourses.setOnClickListener(this);

        tvActive.setOnClickListener(this);
        tvPending.setOnClickListener(this);
        tvCompleted.setOnClickListener(this);


        findViewById(R.id.cv_date_picker).setOnClickListener(this);
        findViewById(R.id.cv_filter).setOnClickListener(this);

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
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, AssignmentsNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                getHomeWorkTypes();
            }
            isNetworkAvail = true;
        }
    }

    void getHomeWorkTypes() {


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "HW Types URL - " + new AppUrls().HOMEWORK_GetTypes + "schemaName=" + sh_Pref.getString("schema", ""));

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetTypes + "schemaName=" + sh_Pref.getString("schema", ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (NetworkConnectivity.isConnected(AssignmentsNew.this)) {
                    getHomeWorks();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new MyUtils().alertDialog(1, AssignmentsNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                if (!response.isSuccessful()) {
                    if (NetworkConnectivity.isConnected(AssignmentsNew.this)) {
                        getHomeWorks();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MyUtils().alertDialog(1, AssignmentsNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close), false);
                            }
                        });
                    }
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("HomeWorkTypes");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<HomeWorkTypes>>() {
                            }.getType();

                            hwTypeList.clear();
                            hwTypeList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "hwTypeList - " + hwTypeList.size());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<HomeWorkTypes> adapter =
                                            new ArrayAdapter<HomeWorkTypes>(AssignmentsNew.this, R.layout.spinner_tv, hwTypeList);
                                    adapter.setDropDownViewResource(R.layout.spinner_tv);

                                    spType.setAdapter(adapter);

                                    spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            homeWorktypeId = hwTypeList.get(i).getHomeTypeId() + "";
                                            getHomeWorks();
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                            });

                        }
                    } catch (Exception e) {

                    }
                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_regular_courses:
                llRegularCourses.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvRegular.setTextColor(Color.WHITE);
                llOptedCourses.setBackgroundResource(0);
                tvOpted.setTextColor(Color.parseColor("#aeaeae"));
                courseId = sObj.getCourseId()+"";
                getHomeWorks();
                break;
            case R.id.ll_opted_courses:
                llOptedCourses.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvOpted.setTextColor(Color.WHITE);
                llRegularCourses.setBackgroundResource(0);
                tvRegular.setTextColor(Color.parseColor("#aeaeae"));
                courseId = sObj.getCourseId()+"";
                getHomeWorks();
                break;

            case R.id.tv_active:
                tvActive.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvActive.setTextColor(Color.WHITE);
                tvPending.setBackgroundResource(0);
                tvPending.setTextColor(Color.parseColor("#80494949"));
                tvCompleted.setBackgroundResource(0);
                tvCompleted.setTextColor(Color.parseColor("#80494949"));
                findViewById(R.id.cv_filter).setEnabled(false);
                findViewById(R.id.cv_filter).setAlpha(0.5f);
                hwStatusType = "active";
                getHomeWorks();

                break;
            case R.id.tv_pending:
                tvPending.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvPending.setTextColor(Color.WHITE);
                tvActive.setBackgroundResource(0);
                tvActive.setTextColor(Color.parseColor("#80494949"));
                tvCompleted.setBackgroundResource(0);
                tvCompleted.setTextColor(Color.parseColor("#80494949"));
                findViewById(R.id.cv_filter).setEnabled(true);
                findViewById(R.id.cv_filter).setAlpha(1f);
                hwStatusType = "pending";
                getHomeWorks();

                break;
            case R.id.tv_completed:
                tvCompleted.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvCompleted.setTextColor(Color.WHITE);
                tvActive.setBackgroundResource(0);
                tvActive.setTextColor(Color.parseColor("#80494949"));
                tvPending.setBackgroundResource(0);
                tvPending.setTextColor(Color.parseColor("#80494949"));
                findViewById(R.id.cv_filter).setEnabled(false);
                findViewById(R.id.cv_filter).setAlpha(0.5f);
                hwStatusType = "completed";
                getHomeWorks();

                break;
            case R.id.cv_date_picker:

                Rect displayRectangle = new Rect();
                Window window = AssignmentsNew.this.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                Dialog dialog = new Dialog(AssignmentsNew.this);
                dialog.setContentView(R.layout.dialog_hw_filter);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.show();

                dialog.findViewById(R.id.tv_month).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type = 1;

                        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                        pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                                String mon = "";
                                switch (month) {
                                    case 1:
                                        mon = "January";
                                        break;
                                    case 2:
                                        mon = "February";
                                        break;
                                    case 3:
                                        mon = "March";
                                        break;
                                    case 4:
                                        mon = "April";
                                        break;
                                    case 5:
                                        mon = "May";
                                        break;
                                    case 6:
                                        mon = "June";
                                        break;
                                    case 7:
                                        mon = "July";
                                        break;
                                    case 8:
                                        mon = "August";
                                        break;
                                    case 9:
                                        mon = "September";
                                        break;
                                    case 10:
                                        mon = "October";
                                        break;
                                    case 11:
                                        mon = "November";
                                        break;
                                    case 12:
                                        mon = "December";
                                        break;
                                }


                                currentYear = year + "";
                                currentMonth = month + "";

                                tvMonthYear.setText(mon + " " + year);

                                getHomeWorks();

                            }
                        });
                        pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.tv_date).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type = 0;

                        int year = 0, month = 0, date = 0;
                        try {

                            year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(filterDate)));
                            month = Integer.parseInt(new SimpleDateFormat("MM").format(new SimpleDateFormat("yyyy-MM-dd").parse(filterDate)));
                            date = Integer.parseInt(new SimpleDateFormat("dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(filterDate)));


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        DatePickerDialog dialog1 = new DatePickerDialog(AssignmentsNew.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int yearCal, int monthOfYear, int dayOfMonth) {
                                utils.showLog(TAG, "vals " + yearCal + " " + monthOfYear + " " + dayOfMonth);
                                try {
                                    monthOfYear = monthOfYear + 1;
                                    tvMonthYear.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(yearCal + "-" + monthOfYear + "-" + dayOfMonth)));
                                    filterDate = yearCal + "-" + monthOfYear + "-" + dayOfMonth;
                                    currentMonth = monthOfYear + "";
                                    currentYear = yearCal + "";
                                    getHomeWorks();

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, year, (month - 1), date);


                        dialog1.show();
                        dialog.dismiss();
                    }
                });

                break;

            case R.id.cv_filter:
                displayRectangle = new Rect();
                window = AssignmentsNew.this.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                Dialog dialogFilter = new Dialog(AssignmentsNew.this);
                dialogFilter.setContentView(R.layout.dialog_hw_filter_status);
                dialogFilter.setCancelable(true);
                dialogFilter.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialogFilter.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), ViewGroup.LayoutParams.WRAP_CONTENT);

                dialogFilter.show();

                CheckBox cbAll = dialogFilter.findViewById(R.id.cb_all);
                CheckBox cbPending = dialogFilter.findViewById(R.id.cb_pending);
                CheckBox cbMissed = dialogFilter.findViewById(R.id.cb_missed);
                CheckBox cbSubmitted = dialogFilter.findViewById(R.id.cb_submitted);

                cbAll.setChecked(false);
                cbPending.setChecked(false);
                cbMissed.setChecked(false);
                cbSubmitted.setChecked(false);

                switch (tvFilter.getText().toString()){
                    case "All":
                        cbAll.setChecked(true);
                        break;
                    case "Pending":
                        cbPending.setChecked(true);
                        break;
                    case "Missed":
                        cbMissed.setChecked(true);
                        break;
                    case "Submitted":
                        cbSubmitted.setChecked(true);
                        break;
                }

                cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            tvFilter.setText("All");
                            dialogFilter.dismiss();
                            getHomeWorks();
                        }
                    }
                });

                cbPending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            tvFilter.setText("Pending");
                            dialogFilter.dismiss();
                            getHomeWorks();

                        }
                    }
                });
                cbMissed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            tvFilter.setText("Missed");
                            dialogFilter.dismiss();
                            getHomeWorks();
                        }
                    }
                });
                cbSubmitted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            tvFilter.setText("Submitted");
                            dialogFilter.dismiss();
                            getHomeWorks();
                        }
                    }
                });


        }
    }

    void getHomeWorks(){

        utils.showLoader(this);

        hwList.clear();
        listAssignments.clear();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = null;
        if (type==1){

            get = new Request.Builder()
                    .url(AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&courseId="+ courseId+"&status="+hwStatusType)
                    .build();

            utils.showLog(TAG, "url -"+ AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&courseId="+ courseId+"&status="+hwStatusType);
        }else {
            get = new Request.Builder()
                    .url(AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&filterDate="+filterDate+"&courseId="+ courseId+"&status="+hwStatusType)
                    .build();

            utils.showLog(TAG, "url -"+ AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&filterDate="+filterDate+"&courseId="+ courseId+"&status="+hwStatusType);

        }


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }else{

                        String resp = responseBody.string();


                        utils.showLog(TAG,"response- "+resp);

                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            if (ParentjObject.has("studentHomeWorkDetails")) {
                                JSONArray jsonArr = ParentjObject.getJSONArray("studentHomeWorkDetails");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<HomeWork>>() {
                                }.getType();



                                hwList = gson.fromJson(jsonArr.toString(), type);
                                utils.showLog(TAG, "hwListSize - " + hwList.size());


                                List<String> typeHw = new ArrayList<>();

                                for (int i=0;i<hwList.size();i++){

                                    if (hwList.get(i).getHomeworkTypeId().equalsIgnoreCase(homeWorktypeId)){
                                        List<HomeWorkDetail> listHw = new ArrayList<>();
                                        listHw.addAll(hwList.get(i).getHomeWorkDetails());
                                        for (int j=0;j<listHw.size();j++){
                                            List<HomeWorkDetails> listDetails = new ArrayList<>();
                                            listDetails.addAll(listHw.get(j).getHomeWorkDetail());
                                            for (int k=0;k<listDetails.size();k++){

                                                if (hwStatusType.equalsIgnoreCase("pending")){

                                                    switch(tvFilter.getText().toString()){
                                                        case "All":
                                                            listAssignments.add(listDetails.get(k));
                                                            typeHw.add(hwList.get(i).getHomeWorkDesc());
                                                            break;
                                                        case "Pending":
                                                            if (listDetails.get(k).getHwStatus().equalsIgnoreCase("Assigned") || listDetails.get(k).getHwStatus().equalsIgnoreCase("REASSIGN")){
                                                                listAssignments.add(listDetails.get(k));
                                                                typeHw.add(hwList.get(i).getHomeWorkDesc());
                                                            }
                                                            break;
                                                        case "Submitted":
                                                            if (listDetails.get(k).getHwStatus().equalsIgnoreCase("Submitted")){
                                                                listAssignments.add(listDetails.get(k));
                                                                typeHw.add(hwList.get(i).getHomeWorkDesc());
                                                            }
                                                            break;

                                                        case "Missed":
                                                            if (listDetails.get(k).getHwStatus().equalsIgnoreCase("Assigned") && new SimpleDateFormat("yyyy-MM-dd").parse(listDetails.get(k).getSubmissionDate()).before(new Date())){
                                                                listAssignments.add(listDetails.get(k));
                                                                typeHw.add(hwList.get(i).getHomeWorkDesc());
                                                            }
                                                            break;
                                                    }

                                                }else{
                                                    listAssignments.add(listDetails.get(k));
                                                    typeHw.add(hwList.get(i).getHomeWorkDesc());
                                                }


                                            }

                                        }
                                    }

                                }


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAssignments();
                                    }
                                });

                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setAssignments();
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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

    void setAssignments(){
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(rvAssignments.getContext(), R.anim.layout_animation_fall_down);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));
        rvAssignments.setAdapter(new HWAdapter(listAssignments));
        rvAssignments.scheduleLayoutAnimation();
    }

    class HWAdapter extends RecyclerView.Adapter<HWAdapter.ViewHolder>{

        List<HomeWorkDetails> listAssignments;

        public HWAdapter(List<HomeWorkDetails> listAssignments) {
            this.listAssignments = listAssignments;
        }

        @NonNull
        @Override
        public HWAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HWAdapter.ViewHolder(LayoutInflater.from(AssignmentsNew.this).inflate(R.layout.item_final_hw_cards,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull HWAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvHwName.setText(listAssignments.get(position).getHwTitle());
            holder.tvSubName.setText(listAssignments.get(position).getSubjectName());
            try {
                holder.tvSubmitBy.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listAssignments.get(position).getSubmissionDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvStatus.setText(listAssignments.get(position).getHwStatus());

            if (listAssignments.get(position).getHwStatus().equalsIgnoreCase("Assigned")){
                holder.llSubmission.setVisibility(View.INVISIBLE);
                try {
                    if (new SimpleDateFormat("yyyy-MM-dd").parse(listAssignments.get(position).getSubmissionDate()).before(new Date())){
                        holder.llMainBg.setBackgroundResource(R.drawable.bg_grad_hw_missed);
                    }else{
                        holder.llMainBg.setBackgroundResource(R.drawable.bg_grad_hw_pending);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (listAssignments.get(position).getHwStatus().equalsIgnoreCase("Reassign")){
                holder.llSubmission.setVisibility(View.INVISIBLE);
                holder.llMainBg.setBackgroundResource(R.drawable.bg_grad_hw_pending);
            }

            if (listAssignments.get(position).getHwStatus().equalsIgnoreCase("Submitted")){
                holder.llSubmission.setVisibility(View.VISIBLE);
                try {
                    holder.tvSubmittedDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listAssignments.get(position).getHwSubmitDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.llMainBg.setBackgroundResource(R.drawable.bg_grad_hw_submitted);

            }

            if (listAssignments.get(position).getHwStatus().equalsIgnoreCase("Completed")){
                holder.llSubmission.setVisibility(View.VISIBLE);
                try {
                    holder.tvSubmittedDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listAssignments.get(position).getHwSubmitDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.llMainBg.setBackgroundResource(R.drawable.bg_grad_hw_completed);
                holder.tvStatusHead.setText("Feedback");
                switch (Integer.parseInt(listAssignments.get(position).getHwRating())){
//                    case 1:
//                        holder.tvStatus.setText("Poor");
//                        holder.llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_poor);
//                        break;
                    case 2:
                        holder.tvStatus.setText("Average");
                        holder.llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_poor);
                        break;
                    case 3:
                        holder.tvStatus.setText("Good");
                        holder.llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_good);
                        break;
                    case 4:
                        holder.tvStatus.setText("Excellent");
                        holder.llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_os);
                        break;
                    case 5:
                        holder.tvStatus.setText("OutStanding");
                        holder.llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_os);
                        break;

                    default:
                        holder.tvStatus.setText("Poor");
                        holder.llTeacherReview.setBackgroundResource(R.drawable.bg_hw_feedback_poor);
                        break;
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AssignmentsNew.this, AssignmentDisplayNew.class);
                    intent.putExtra("hwObj",(Serializable)listAssignments.get(position));
                    intent.putExtra("hwId",listAssignments.get(position).getHomeworkId()+"");
                    intent.putExtra("type",spType.getSelectedItem().toString());
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listAssignments.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvHwName,tvSubName,tvSubmittedDate,tvSubmitBy,tvStatus,tvStatusHead;
            LinearLayout llSubmission,llMainBg,llTeacherReview;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvHwName = itemView.findViewById(R.id.tv_hw_name);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvSubmittedDate = itemView.findViewById(R.id.tv_submitted_date);
                tvSubmitBy = itemView.findViewById(R.id.tv_submit_by);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvStatusHead = itemView.findViewById(R.id.tv_status_head);
                llSubmission = itemView.findViewById(R.id.ll_submission);
                llMainBg = itemView.findViewById(R.id.ll_main_bg);
                llTeacherReview = itemView.findViewById(R.id.ll_teacher_review);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}