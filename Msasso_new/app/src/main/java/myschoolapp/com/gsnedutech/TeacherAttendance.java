package myschoolapp.com.gsnedutech;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import myschoolapp.com.gsnedutech.Models.AttendaceAnalysis;
import myschoolapp.com.gsnedutech.Models.StudentMonthWiseAttendance;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
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

public class TeacherAttendance extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = "SriRam -" + TeacherAttendance.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    @BindView(R.id.rv_chart)
    RecyclerView rvChart;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_working)
    TextView tvWorking;
    @BindView(R.id.tv_absent)
    TextView tvAbsent;
    @BindView(R.id.tv_holidays)
    TextView tvHolidays;

    @BindView(R.id.tv_total_taken)
    TextView tvTotalTaken;
    @BindView(R.id.tv_total_present)
    TextView tvTotalPresent;
    @BindView(R.id.tv_total_absent)
    TextView tvTotalAbsent;
    @BindView(R.id.tv_total_late)
    TextView tvTotalLate;
    @BindView(R.id.tv_total_percent)
    TextView tvTotalPercent;

    @BindView(R.id.rv_month_options)
    RecyclerView rvMonthOptions;

    MyUtils utils = new MyUtils();

    List<ChartAdapter.ViewHolder> holderList = new ArrayList<>();

    String dateRequest;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    String studentId;

    TeacherObj tObj;
    String staffId,branchId;

    List<StudentMonthWiseAttendance> listMonthWiseAttendance = new ArrayList<>();

    List<AttendaceAnalysis> listAttendanceAnalysis = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherAttendance.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getOverAllAttendance();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    private void init() {
        dateRequest = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        rvMonthOptions.setLayoutManager(new LinearLayoutManager(TeacherAttendance.this));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.tv_leave_req).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherAttendance.this,TeacherLeaveRequest.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        tvDate.setText(new SimpleDateFormat("MMM yyyy").format(new Date()));
        findViewById(R.id.ll_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        String mon = "";
                        switch (month) {
                            case 1:
                                mon = "Jan";
                                break;
                            case 2:
                                mon = "Feb";
                                break;
                            case 3:
                                mon = "Mar";
                                break;
                            case 4:
                                mon = "Apr";
                                break;
                            case 5:
                                mon = "May";
                                break;
                            case 6:
                                mon = "Jun";
                                break;
                            case 7:
                                mon = "Jul";
                                break;
                            case 8:
                                mon = "Aug";
                                break;
                            case 9:
                                mon = "Sep";
                                break;
                            case 10:
                                mon = "Oct";
                                break;
                            case 11:
                                mon = "Nov";
                                break;
                            case 12:
                                mon = "Dec";
                                break;
                        }
                        Calendar cal = Calendar.getInstance();

                        tvDate.setText(mon+", "+year);

                        dateRequest= year+"-"+month+"-01";
                       for (int i=0;i<listAttendanceAnalysis.size();i++){
                           utils.showLog(TAG,"year "+listAttendanceAnalysis.get(i).getMonthandYear().split("-")[0]+" "+year);
                         if (listAttendanceAnalysis.get(i).getMonthandYear().split("-")[0].equalsIgnoreCase(year+"")){
                             utils.showLog(TAG,"year "+listAttendanceAnalysis.get(i).getMonthName()+" "+mon);
                             if (listAttendanceAnalysis.get(i).getMonthName().contains(mon)){
                                 tvWorking.setText(listAttendanceAnalysis.get(i).getAttendanceTakenDays()+"/"+listAttendanceAnalysis.get(i).getWorkingDays());
                                 tvAbsent.setText(listAttendanceAnalysis.get(i).getAbsentCount()+"");
                                 tvHolidays.setText(""+(listAttendanceAnalysis.get(i).getWorkingDays()-listAttendanceAnalysis.get(i).getAttendanceTakenDays()));
                                 break;
                             }else{
                                 tvWorking.setText("0/0");
                                 tvAbsent.setText("0");
                                 tvHolidays.setText("0");
                             }
                         }else{
                             tvWorking.setText("0/0");
                             tvAbsent.setText("0");
                             tvHolidays.setText("0");
                         }
                       }

//                       utils.showLoader(StudentAttendance.this);

                       getMonthWiseAttendance();

                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);
        staffId = tObj.getUserId()+"";
        branchId = tObj.getBranchId()+"";

    }





    void getOverAllAttendance() {

        utils.showLoader(TeacherAttendance.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStaffOverallAttendance + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + staffId + "&branchId=" + branchId);

        Request get = new Request.Builder()
                .url(AppUrls.GetStaffOverallAttendance + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + staffId + "&branchId=" + branchId)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getAttendanceAnalysis();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (response.body() != null) {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                try {
                                    tvTotalTaken.setText(parentjObject.getInt("attendanceTakenDays")+"/"+parentjObject.getInt("workingDays"));

                                    int present = parentjObject.getInt("attendanceTakenDays") - parentjObject.getInt("staffLeaves") - parentjObject.getInt("staffLates");

                                    tvTotalPresent.setText(present+"");

                                    tvTotalAbsent.setText(parentjObject.getInt("staffLeaves")+"");
                                    tvTotalLate.setText(parentjObject.getInt("staffLates")+"");

                                    float percent = 100 - (Float.parseFloat(parentjObject.getString("staffLeavePercentage")) + Float.parseFloat(parentjObject.getString("staffLatePercentage")));

                                    tvTotalPercent.setText(percent+"%");
                                    getAttendanceAnalysis();
                                }catch (Exception e){

                                }
                              }
                          });
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, TeacherAttendance.this, message, sh_Pref);
                            });
                        }else{
                            getAttendanceAnalysis();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    getAttendanceAnalysis();
                }
            }
        });
    }

    void getAttendanceAnalysis() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStaffAttendanceForAnalysis + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + staffId + "&branchId=" + branchId);

        Request get = new Request.Builder()
                .url(AppUrls.GetStaffAttendanceForAnalysis + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + staffId + "&branchId=" + branchId)
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getMonthWiseAttendance();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    getMonthWiseAttendance();
                }else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("staffMonthWiseAttendance");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AttendaceAnalysis>>() {
                            }.getType();

                            listAttendanceAnalysis.clear();

                            listAttendanceAnalysis.addAll(gson.fromJson(String.valueOf(jsonArr), type));
                            utils.showLog(TAG, "Analysis size " + listAttendanceAnalysis.size());

                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
//                                   setUpChart();
                                   rvChart.setLayoutManager(new LinearLayoutManager(TeacherAttendance.this,RecyclerView.HORIZONTAL,false));
                                   final LayoutAnimationController controller =
                                           AnimationUtils.loadLayoutAnimation(rvChart.getContext(), R.anim.layout_animation_bottom_up);

                                   rvChart.setAdapter(new ChartAdapter());
                                   rvChart.scheduleLayoutAnimation();

//                                   findViewById(R.id.ll_chart).setVisibility(View.VISIBLE);
                                   ViewAnimation.showIn(findViewById(R.id.ll_chart));


                                   tvWorking.setText(listAttendanceAnalysis.get((listAttendanceAnalysis.size()-1)).getAttendanceTakenDays()+"/"+listAttendanceAnalysis.get((listAttendanceAnalysis.size()-1)).getWorkingDays());
                                   tvAbsent.setText(listAttendanceAnalysis.get((listAttendanceAnalysis.size()-1)).getAbsentCount()+"");
                                   tvHolidays.setText(""+(listAttendanceAnalysis.get((listAttendanceAnalysis.size()-1)).getWorkingDays()-listAttendanceAnalysis.get((listAttendanceAnalysis.size()-1)).getAttendanceTakenDays()));

                               }
                           });
                        }
                    } catch (Exception e) {

                    }
                }
                getMonthWiseAttendance();
            }
        });
    }

    void getMonthWiseAttendance() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStaffMonthWiseAttendanceDetails + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + staffId+ "&date=" + dateRequest);

        Request get = new Request.Builder()
                .url(AppUrls.GetStaffMonthWiseAttendanceDetails + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + staffId+ "&date=" + dateRequest)
                .build();

        client.newCall(get).enqueue(new Callback() {
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
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {


                }else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "month wise response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("staffAttendance");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentMonthWiseAttendance>>() {
                            }.getType();

                            listMonthWiseAttendance.clear();
                            listMonthWiseAttendance.addAll(gson.fromJson(jsonArray.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listMonthWiseAttendance.size()>0){
                                        rvMonthOptions.setAdapter(new OptionAdapter(listMonthWiseAttendance));
                                        rvMonthOptions.setVisibility(View.VISIBLE);
                                    }else{
                                        rvMonthOptions.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else {
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   rvMonthOptions.setVisibility(View.GONE);
                               }
                           });
                        }
                    } catch (Exception e) {

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


    class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder>{

        @NonNull
        @Override
        public ChartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherAttendance.this).inflate(R.layout.item_attend_chart,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChartAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvMonthName.setText(listAttendanceAnalysis.get(position).getMonthName());

            int attDays = listAttendanceAnalysis.get(position).getAttendanceTakenDays();
            int workingDays = listAttendanceAnalysis.get(position).getWorkingDays();
            int absentDays = Integer.parseInt(listAttendanceAnalysis.get(position).getAbsentCount());
            int presentDays = attDays - absentDays;

            float holidays = ((float)(workingDays-attDays)/(float)workingDays);
            float absentPercent = ((float)absentDays/(float)attDays);
            float presentPercent = ((float)presentDays/(float)attDays);

            LinearLayout.LayoutParams param;

            //present
             param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    presentPercent
            );
            holder.presentPrimary.setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    (1- presentPercent)
            );
            holder.presentSecondary.setLayoutParams(param);

            //absent
            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    absentPercent
            );
            holder.absentPrimary.setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    (1- absentPercent)
            );
            holder.absentSecondary.setLayoutParams(param);

            //late
            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    holidays
            );
            holder.latePrimary.setLayoutParams(param);

            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    (1- holidays)
            );
            holder.lateSecondary.setLayoutParams(param);

            holderList.add(holder);

            holder.latePrimary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                unSelectAll();

                    if ((holidays*100)>80){
                        holder.latePrimary.setText(((int)(holidays*100))+"%");
                    }else {
                        holder.lateSecondary.setText(((int)(holidays*100))+"%");
                    }
                }
            });

            holder.absentPrimary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unSelectAll();

                    if ((absentPercent*100) > 80) {
                        holder.absentPrimary.setText(((int) (absentPercent * 100)) + "%");
                    }else {
                        holder.absentSecondary.setText(((int) (absentPercent * 100)) + "%");
                    }
                }
            });

            holder.presentPrimary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unSelectAll();

                    if ((presentPercent*100)>80){
                        holder.presentPrimary.setText(((int) (presentPercent * 100)) + "%");
                    }else {
                        holder.presentSecondary.setText(((int) (presentPercent * 100)) + "%");
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listAttendanceAnalysis.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvMonthName;
            TextView presentSecondary,presentPrimary,lateSecondary,latePrimary,absentSecondary,absentPrimary;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvMonthName = itemView.findViewById(R.id.tv_month_name);
                presentPrimary = itemView.findViewById(R.id.view_p_primary);
                presentSecondary = itemView.findViewById(R.id.view_p_secondary);
                latePrimary = itemView.findViewById(R.id.view_l_primary);
                lateSecondary = itemView.findViewById(R.id.view_l_secondary);
                absentPrimary = itemView.findViewById(R.id.view_a_primary);
                absentSecondary = itemView.findViewById(R.id.view_a_secondary);

            }
        }
    }

    private void unSelectAll() {
        for (int i=0;i<holderList.size();i++){
            holderList.get(i).presentPrimary.setText("");
            holderList.get(i).presentSecondary.setText("");
            holderList.get(i).absentPrimary.setText("");
            holderList.get(i).absentSecondary.setText("");
            holderList.get(i).latePrimary.setText("");
            holderList.get(i).lateSecondary.setText("");
        }
    }


    class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder>{

        List<StudentMonthWiseAttendance> listAttendance;

        public OptionAdapter(List<StudentMonthWiseAttendance> listAttendance) {
            this.listAttendance = listAttendance;
        }

        @NonNull
        @Override
        public OptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherAttendance.this).inflate(R.layout.item_month_att,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull OptionAdapter.ViewHolder holder, int position) {
            holder.tvReason.setText(listAttendance.get(position).getReason());
            try {
                holder.tvStartDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listAttendance.get(position).getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvToDate.setText("-");
            holder.tvNumDays.setText("-");

            if (listAttendance.get(position).getReason().equalsIgnoreCase("Vacation")){
                holder.ivType.setImageResource(R.drawable.ic_vacay_attendance);
            }else {
                holder.ivType.setImageResource(R.drawable.ic_sick_attendance);
            }

        }

        @Override
        public int getItemCount() {
            return listAttendance.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvReason,tvStartDate,tvToDate,tvNumDays;
            ImageView ivType;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvReason = itemView.findViewById(R.id.tv_reason);
                tvStartDate = itemView.findViewById(R.id.tv_start_days);
                tvToDate = itemView.findViewById(R.id.tv_to_days);
                tvNumDays = itemView.findViewById(R.id.tv_day_num);
                ivType = itemView.findViewById(R.id.iv_type);
            }
        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}