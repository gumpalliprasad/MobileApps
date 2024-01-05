package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
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
import myschoolapp.com.gsnedutech.Fragments.StudentLiveClassesFrag;
import myschoolapp.com.gsnedutech.Fragments.StudentRecordedVideosFrag;
import myschoolapp.com.gsnedutech.Models.LiveStudentAttendance;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentLiveClassesTabs extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + StudentLiveClassesTabs.class.getName();

    MyUtils utils = new MyUtils();


    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    @BindView(R.id.tb_main)
    TabLayout tabLayout;
    @BindView(R.id.vp_main)
    ViewPager viewPager;

    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    @BindView(R.id.tv_total_classes)
    TextView tvTotal;

    @BindView(R.id.tv_attend_classes)
    TextView tvAttend;

    @BindView(R.id.tv_missed_classes)
    TextView tvMissed;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.cv_filter)
    CardView cvFilter;

    public String currentMonth, currentYear;

    int dayOfMonth;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    int currentTab = 0;

    public int type = 0;
    boolean toggle = false;

    Calendar calendar;

    public String filterDate = "";

    List<LiveStudentAttendance> liveStudentAttendances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_live_classes_tabs);
        ButterKnife.bind(this);

        currentMonth = new SimpleDateFormat("MM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());
        dayOfMonth = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        filterDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
        findViewById(R.id.ll_liveattendance).setVisibility(View.GONE);
//        init();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==1){
                    findViewById(R.id.tv_fbd).setVisibility(View.INVISIBLE);
                }
                else findViewById(R.id.tv_fbd).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));

      cvFilter.setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        findViewById(R.id.tv_fbm).setOnClickListener(new View.OnClickListener() {
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

//                        Calendar cal = Calendar.getInstance();
//                        cal.set(year, (month - 1), 1);

                        currentMonth = month + "";
                        currentYear = year + "";

//                        getHomeWorks();


                        tvMonthYear.setText(mon + " " + year);

                        ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                        toggle = false;
                        currentTab = viewPager.getCurrentItem();
                        init();
                        viewPager.setCurrentItem(currentTab);
                        findViewById(R.id.ll_liveattendance).setVisibility(View.VISIBLE);


                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

        findViewById(R.id.tv_fbd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=0;



                DatePickerDialog dialog1 = new DatePickerDialog(StudentLiveClassesTabs.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yearCal, int monthOfYear, int dayOfMonth) {
                        utils.showLog(TAG,"vals "+yearCal+" "+monthOfYear+" "+dayOfMonth);
                        try {
                            tvMonthYear.setText(new SimpleDateFormat("dd MMMM yyyy").format( new SimpleDateFormat("yyyy-MM-dd").parse(yearCal+"-"+(monthOfYear+1)+"-"+dayOfMonth)));
                            filterDate = yearCal+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                            currentMonth = (monthOfYear+1)+"";
                            currentYear = yearCal+"";
                            StudentLiveClassesTabs.this.dayOfMonth = dayOfMonth;
                            ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
                            toggle = false;
                            currentTab = viewPager.getCurrentItem();
                            init();
                            viewPager.setCurrentItem(currentTab);
                            findViewById(R.id.ll_liveattendance).setVisibility(View.VISIBLE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, Integer.parseInt(currentYear), Integer.parseInt(currentMonth)-1, dayOfMonth);


                dialog1.show();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentTab = viewPager.getCurrentItem();
                swipeLayout.setRefreshing(true);
                init();
                viewPager.setCurrentItem(currentTab);
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
            utils.alertDialog(1, StudentLiveClassesTabs.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                currentTab = viewPager.getCurrentItem();
                init();
                viewPager.setCurrentItem(currentTab);
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void init() {
        calendar = Calendar.getInstance();
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        getLiveAttendance();
        setAdapter();
    }

    void setAdapter() {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new StudentLiveClassesFrag(), "Live Classes");
        adapter.addFragment(new StudentRecordedVideosFrag(), "Recorded Videos");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentTab);
        swipeLayout.setRefreshing(false);
//        getLiveAttendance();
    }

    void getLiveAttendance() {
        utils.showLoader(StudentLiveClassesTabs.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();

        if (type == 1){
            try {
                jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
                jsonObject.put("studentId", sObj.getStudentId());
                jsonObject.put("month", "" + currentMonth);
                jsonObject.put("year", currentYear);
                jsonObject.put("sectionId", sObj.getClassCourseSectionId());
            } catch (Exception e) {

            }
        }
        else {
            try {
                jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
                jsonObject.put("studentId", sObj.getStudentId());
                jsonObject.put("filterDate", currentYear+"-"+ currentMonth+"-"+dayOfMonth);
                jsonObject.put("sectionId", sObj.getClassCourseSectionId());
            } catch (Exception e) {

            }
        }



        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Log.v(TAG, "URL " + AppUrls.StudentLiveAttendance);
        Log.v(TAG, "Body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(AppUrls.StudentLiveAttendance)
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
                if (response.body() != null) {
                    String jsonResp = response.body().string();
                    utils.showLog("response live attendance", jsonResp);
                    try {
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = parentjObject.getJSONArray("studentAttendance");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<LiveStudentAttendance>>() {
                            }.getType();

                            liveStudentAttendances.clear();
                            liveStudentAttendances.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "liveStudentAttendances size- " + liveStudentAttendances.size());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (liveStudentAttendances.size() > 0) {
                                        int attend = 0, missed = 0;
                                        tvTotal.setText("" + liveStudentAttendances.size());
                                        for (LiveStudentAttendance lstAtt : liveStudentAttendances) {
                                            try {
                                                Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lstAtt.getLiveStreamStartTime());
                                                String todayString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                                Date todayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(todayString);
                                                if (todayTime.after(startTime)) {
                                                    if (lstAtt.getAttendance() > 0) {
                                                        attend = attend + 1;
                                                    } else {
                                                        missed = missed + 1;
                                                    }
                                                } else {
//                                                    Toast.makeText(StudentLiveClassesTabs.this, "hello", Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        tvAttend.setText("" + attend);
                                        tvMissed.setText("" + missed);
                                    } else {
                                        tvAttend.setText("0");
                                        tvMissed.setText("0");
                                        tvTotal.setText("0");
                                    }
                                }
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, StudentLiveClassesTabs.this, message, sh_Pref);
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


    private static class Adapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private ArrayList<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    @Override
    public void onBackPressed() {
        if (toggle){
            ViewAnimation.showOut(findViewById(R.id.ll_backdrop));
            toggle = false;
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }
}