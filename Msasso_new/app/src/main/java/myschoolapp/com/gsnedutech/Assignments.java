package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Calendar;
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
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Assignments extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + Assignments.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    List<String> filterType = new ArrayList<>();

    String filterMonthYear = "";
    String filterDate = "";


    @BindView(R.id.tv_active)
    TextView tvActive;
    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_completed)
    TextView tvCompleted;
    @BindView(R.id.rv_assignments)
    RecyclerView rvAssignments;
    @BindView(R.id.sp_options)
    Spinner spType;

    @BindView(R.id.cv_active_count)
    CardView cvActiveCount;
    @BindView(R.id.cv_pending_count)
    CardView cvPendingCount;
    @BindView(R.id.cv_completed_count)
    CardView cvCompletedCount;

    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    boolean flag = false;

    List<HomeWork> hwList = new ArrayList<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "",homeWorktypeId="";

    List<HomeWorkDetails> listAssignmentsPending = new ArrayList<>();
    List<HomeWorkDetails> listAssignmentsCompleted = new ArrayList<>();

    List<HomeWorkTypes> hwTypeList = new ArrayList<>();

    String currentMonth,currentYear;

    Calendar calendar;

    boolean toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);
        ButterKnife.bind(this);

        init();
    }
    int type = 0;
    String hwStatusType = "active";


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
            utils.alertDialog(1, Assignments.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getHomeWorkTypes();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {
        calendar = Calendar.getInstance();
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        if (sh_Pref.getBoolean("student_loggedin", false)
                || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            studentId = sObj.getStudentId();
        }else{
            studentId = getIntent().getStringExtra("studentId");
        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //initial
        hwStatusType = "active";
        tvActive.setBackgroundResource(R.drawable.bg_grad_tab_select);
        tvActive.setTextColor(Color.WHITE);
        cvActiveCount.setVisibility(View.VISIBLE);

        tvCompleted.setBackground(null);
        tvCompleted.setTextColor(Color.rgb(73,73,73));
        tvCompleted.setAlpha(0.75f);
        cvCompletedCount.setVisibility(View.GONE);

        tvPending.setBackground(null);
        tvPending.setTextColor(Color.rgb(73,73,73));
        tvPending.setAlpha(0.75f);
        cvPendingCount.setVisibility(View.GONE);


        tvActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hwStatusType = "active";
                tvActive.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvActive.setTextColor(Color.WHITE);
                cvActiveCount.setVisibility(View.VISIBLE);

                tvCompleted.setBackground(null);
                tvCompleted.setTextColor(Color.rgb(73,73,73));
                tvCompleted.setAlpha(0.75f);
                cvCompletedCount.setVisibility(View.GONE);

                tvPending.setBackground(null);
                tvPending.setTextColor(Color.rgb(73,73,73));
                tvPending.setAlpha(0.75f);
                cvPendingCount.setVisibility(View.GONE);
            }
        });

        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hwStatusType = "pending";
                tvPending.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvPending.setTextColor(Color.WHITE);
                cvPendingCount.setVisibility(View.VISIBLE);

                tvCompleted.setBackground(null);
                tvCompleted.setTextColor(Color.rgb(73,73,73));
                tvCompleted.setAlpha(0.75f);
                cvCompletedCount.setVisibility(View.GONE);

                tvActive.setBackground(null);
                tvActive.setTextColor(Color.rgb(73,73,73));
                tvActive.setAlpha(0.75f);
                cvActiveCount.setVisibility(View.GONE);
            }
        });

        tvCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                hwStatusType = "completed";
                tvCompleted.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvCompleted.setTextColor(Color.WHITE);
                cvCompletedCount.setVisibility(View.VISIBLE);

                tvPending.setBackground(null);
                tvPending.setTextColor(Color.rgb(73,73,73));
                tvPending.setAlpha(0.75f);
                cvPendingCount.setVisibility(View.GONE);

                tvActive.setBackground(null);
                tvActive.setTextColor(Color.rgb(73,73,73));
                tvActive.setAlpha(0.75f);
                cvActiveCount.setVisibility(View.GONE);


            }
        });

        cvCompletedCount.setVisibility(View.GONE);
        cvPendingCount.setVisibility(View.GONE);

        currentMonth = new SimpleDateFormat("MM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());
        filterDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());


        tvMonthYear.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date()));


        findViewById(R.id.cv_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle){
                    ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                    toggle = false;
                }else {
                    ViewAnimation.showIn(findViewById(R.id.ll_backdrop));
                    toggle = true;
                }
            }
        });


        findViewById(R.id.tv_fbm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=1;

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


                        currentYear = year+"";
                        currentMonth = month+"";

                        tvMonthYear.setText(mon + " " + year);

                        ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                        toggle = false;
                        getHomeWorks();
                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        findViewById(R.id.tv_fbd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=0;

                int year = 0,month=0,date=0;
                try {
                    year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(filterDate)));
                    month = Integer.parseInt(new SimpleDateFormat("MM").format(new SimpleDateFormat("yyyy-MM-dd").parse(filterDate)));
                    date = Integer.parseInt(new SimpleDateFormat("dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(filterDate)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                calendar.set(year,month,date);

                DatePickerDialog dialog1 = new DatePickerDialog(Assignments.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yearCal, int monthOfYear, int dayOfMonth) {
                        utils.showLog(TAG,"vals "+yearCal+" "+monthOfYear+" "+dayOfMonth);
                        try {
                            monthOfYear = monthOfYear+1;
                            tvMonthYear.setText(new SimpleDateFormat("dd MMM yyyy").format( new SimpleDateFormat("yyyy-MM-dd").parse(yearCal+"-"+monthOfYear+"-"+dayOfMonth)));
                            filterDate = yearCal+"-"+monthOfYear+"-"+dayOfMonth;
                            currentMonth = monthOfYear+"";
                            currentYear = yearCal+"";
                            ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                            toggle = false;
                            getHomeWorks();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, (month-1), date);


                dialog1.show();
            }
        });

    }


    void getHomeWorkTypes(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "HW Types URL - " + new AppUrls().HOMEWORK_GetTypes + "schemaName=" + sh_Pref.getString("schema","") );

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetTypes + "schemaName=" + sh_Pref.getString("schema","") )
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (NetworkConnectivity.isConnected(Assignments.this)) {
                    getHomeWorks();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new MyUtils().alertDialog(1, Assignments.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close),false);
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                if (response.body() != null){
                    try{
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("HomeWorkTypes");

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
                                            new ArrayAdapter<HomeWorkTypes>(Assignments.this,  R.layout.spinner_tv, hwTypeList);
                                    adapter.setDropDownViewResource( R.layout.spinner_tv);

                                    spType.setAdapter(adapter);

                                    spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            homeWorktypeId = hwTypeList.get(i).getHomeTypeId()+"";
                                            getHomeWorks();
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                            });

                        }
                        else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, Assignments.this, message, sh_Pref);
                            });
                        }else{
                            if (NetworkConnectivity.isConnected(Assignments.this)) {
                                getHomeWorks();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new MyUtils().alertDialog(1, Assignments.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                                getString(R.string.action_settings), getString(R.string.action_close),false);
                                    }
                                });
                            }
                        }
                    }catch(Exception e){

                    }
                }else{
                    if (NetworkConnectivity.isConnected(Assignments.this)) {
                        getHomeWorks();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MyUtils().alertDialog(1, Assignments.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close),false);
                            }
                        });
                    }
                }


//                if (NetworkConnectivity.isConnected(Assignments.this)) {
//                    getHomeWorks();
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            new MyUtils().alertDialog(1, Assignments.this, getString(R.string.error_connect), getString(R.string.error_internet),
//                                    getString(R.string.action_settings), getString(R.string.action_close),false);
//                        }
//                    });
//                }
            }
        });
    }


    private void setAssignments(List<HomeWorkDetails> listAssignments) {

        rvAssignments.setVisibility(View.VISIBLE);



        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(rvAssignments.getContext(), R.anim.layout_animation_left_in);

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
            return new ViewHolder(LayoutInflater.from(Assignments.this).inflate(R.layout.item_final_hw_cards,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull HWAdapter.ViewHolder holder, int position) {
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
                    Intent intent = new Intent(Assignments.this, AssignmentDisplayNew.class);
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





    void getHomeWorks(){

        listAssignmentsCompleted.clear();
        listAssignmentsPending.clear();
        hwList.clear();



        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = null;
        if (type==1){

            get = new Request.Builder()
                    .url(AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&status="+hwStatusType)
                    .build();

            utils.showLog(TAG, "url -"+ AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&status="+hwStatusType);
        }else {
            get = new Request.Builder()
                    .url(AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&filterDate"+filterDate+"&status="+hwStatusType)
                    .build();

            utils.showLog(TAG, "url -"+ AppUrls.GetStudentHomeWorksByStatus+"schemaName="+sh_Pref.getString("schema", "")+"&studentId="+studentId+"&monthId="+currentMonth+"&yearId="+currentYear+"&filterDate"+filterDate+"&status="+hwStatusType);

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
                                                if (listDetails.get(k).getHwStatus().equalsIgnoreCase("Completed")){
                                                    listAssignmentsCompleted.add(listDetails.get(k));
                                                }else {
                                                    listAssignmentsPending.add(listDetails.get(k));
                                                    typeHw.add(hwList.get(i).getHomeWorkDesc());
                                                }
                                            }

                                        }
                                    }

                                }


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView)findViewById(R.id.tv_pending_count)).setText(listAssignmentsPending.size()+"");
                                        ((TextView)findViewById(R.id.tv_completed_count)).setText(listAssignmentsCompleted.size()+"");
                                    }
                                });


                                if (listAssignmentsPending.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAssignments(listAssignmentsPending);
                                        }
                                    });

                                }



                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    ((TextView)findViewById(R.id.tv_pending_count)).setText("0");
                                    ((TextView)findViewById(R.id.tv_completed_count)).setText("0");

                                    rvAssignments.setVisibility(View.GONE);
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




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}