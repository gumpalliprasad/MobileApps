package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.TeacherStdntAttendanceFrag;
import myschoolapp.com.gsnedutech.Models.AttendaceAnalysis;
import myschoolapp.com.gsnedutech.Models.StudentMonthWiseAttendance;
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

public class TeacherStudAttendance extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = "SriRam -" + TeacherStdntAttendanceFrag.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    String studentId = "";
    MyUtils utils =  new MyUtils();

    SharedPreferences sh_Pref;

    float[] yData;
    String[] xData;
    List<String> pieLabels = new ArrayList<>();

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
    @BindView(R.id.idPieChart)
    PieChart pieChart;

    String dateRequest = "";

    List<StudentMonthWiseAttendance> listMonthWiseAttendance = new ArrayList<>();
    List<AttendaceAnalysis> listAttendanceAnalysis = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_stud_attendance);

        ButterKnife.bind(this);
        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


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
            utils.alertDialog(1, TeacherStudAttendance.this, getString(R.string.error_connect), getString(R.string.error_internet),
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

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        studentId = getIntent().getStringExtra("studentId");
        dateRequest = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

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
    }

    void getOverAllAttendance() {

        utils.showLoader(TeacherStudAttendance.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentOverallAttendance + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId);

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOverallAttendance + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId)
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getAttendanceAnalysis();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    getAttendanceAnalysis();
                }else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        tvTotalTaken.setText(ParentjObject.getInt("attendanceTakenDays")+"/"+ParentjObject.getInt("wokingDays"));

                                        int present = ParentjObject.getInt("attendanceTakenDays") - ParentjObject.getInt("studentLeaves") - ParentjObject.getInt("studentLates");

                                        tvTotalPresent.setText(present+"");

                                        tvTotalAbsent.setText(ParentjObject.getInt("studentLeaves")+"");
                                        tvTotalLate.setText(ParentjObject.getInt("studentLates")+"");

                                        float percent = 100 - (Float.parseFloat(ParentjObject.getString("studentLeavePercentage")) + Float.parseFloat(ParentjObject.getString("studentLatePercentage")));

                                        tvTotalPercent.setText(percent+"%");

                                        int total = ParentjObject.getInt("attendanceTakenDays");

                                        int absentPercent = calculatePercent(ParentjObject.getInt("studentLeaves"),total);
                                        int latePercent = calculatePercent(ParentjObject.getInt("studentLates"),total);
                                        int presentPercent = 100 - latePercent - absentPercent;

                                        setUpChart(pieChart, presentPercent, absentPercent, 0, latePercent);


                                    }catch (Exception e){

                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                getAttendanceAnalysis();

            }
        });
    }

    int calculatePercent(int x,int y){
        double a = x;
        double b = y;
        double p = (a/b)*100;
        return (int)p;
    }

    void getAttendanceAnalysis() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentAttendanceForAnalysis + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId);

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentAttendanceForAnalysis + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId)
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

                            JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendance");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AttendaceAnalysis>>() {
                            }.getType();

                            listAttendanceAnalysis.clear();

                            listAttendanceAnalysis.addAll(gson.fromJson(String.valueOf(jsonArr), type));
                            utils.showLog(TAG, "Analysis size " + listAttendanceAnalysis.size());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


//                                   findViewById(R.id.ll_chart).setVisibility(View.VISIBLE);


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

    private void setUpChart(PieChart pieChart, int present, int uiabsent, int iabsent, int late) {

        Log.v(TAG,"late - "+late);

        yData = new float[]{present, uiabsent, late};
        xData = new String[]{"Present", "Uninformed-Absent",  "Late"};

        pieLabels.add(present+"%");
        pieLabels.add(uiabsent+"%");
        pieLabels.add(late+"%");


        pieChart.setNoDataTextColor(Color.WHITE);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);
        pieChart.animateXY(3000, 3000);
        pieChart.setTouchEnabled(false);
        pieChart.getDescription().setText("");
        pieChart.calculateOffsets();
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);

        addDataSet(pieChart);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });


    }

    private void addDataSet(PieChart pieChart) {

        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], pieLabels.get(i)));
        }

        for (int i = 1; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(0);
        pieDataSet.setValueTextColor(Color.WHITE);


        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#23A37C"));
        colors.add(Color.parseColor("#B10C20"));
        colors.add(Color.parseColor("#F9A71B"));


        pieDataSet.setColors(colors);



        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new DefaultValueFormatter(1));
        pieChart.setData(pieData);
        pieChart.setExtraOffsets(0, 0, 0, 0);
        pieChart.invalidate();
    }


    void getMonthWiseAttendance() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentMonthWiseAttendanceDetails + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&date=" + dateRequest);

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentMonthWiseAttendanceDetails + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&date=" + dateRequest)
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
                            JSONArray jsonArray = ParentjObject.getJSONArray("studentAttendance");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentMonthWiseAttendance>>() {
                            }.getType();

                            listMonthWiseAttendance.clear();
                            listMonthWiseAttendance.addAll(gson.fromJson(jsonArray.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}