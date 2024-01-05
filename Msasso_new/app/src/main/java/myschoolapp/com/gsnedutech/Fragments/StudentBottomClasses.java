package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.LiveStudentAttendance;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
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

import static android.content.Context.MODE_PRIVATE;

public class StudentBottomClasses extends Fragment implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + StudentBottomClasses.class.getName();

    MyUtils utils = new MyUtils();

    View studentBottomClassesView;
    Unbinder unbinder;

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    
    Activity mActivity;


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
    StudentObj sObj;

    int currentTab = 0;

    public int type = -1;
    boolean toggle = false;

    Calendar calendar;

    public String filterDate = "";

    List<LiveStudentAttendance> liveStudentAttendances = new ArrayList<>();
    public StudentBottomClasses() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        studentBottomClassesView = inflater.inflate(R.layout.fragment_student_bottom_classes, container, false);
        unbinder = ButterKnife.bind(this, studentBottomClassesView);

        currentMonth = new SimpleDateFormat("MM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());
        dayOfMonth = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        filterDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
        studentBottomClassesView.findViewById(R.id.ll_liveattendance).setVisibility(View.GONE);
//        init();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==1){
                    studentBottomClassesView.findViewById(R.id.tv_fbd).setVisibility(View.INVISIBLE);
                }
                else studentBottomClassesView.findViewById(R.id.tv_fbd).setVisibility(View.VISIBLE);
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
                    ViewAnimation.showOut(studentBottomClassesView.findViewById(R.id.ll_backdrop));
                    toggle = false;
                }else {
                    ViewAnimation.showIn(studentBottomClassesView.findViewById(R.id.ll_backdrop));
                    toggle = true;
                }
            }
        });



        studentBottomClassesView.findViewById(R.id.tv_fbm).setOnClickListener(new View.OnClickListener() {
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

                        ViewAnimation.showOut(studentBottomClassesView.findViewById(R.id.ll_backdrop));
                        toggle = false;
                        currentTab = viewPager.getCurrentItem();
                        init();
                        viewPager.setCurrentItem(currentTab);
                        studentBottomClassesView.findViewById(R.id.ll_liveattendance).setVisibility(View.VISIBLE);


                    }
                });
                pickerDialog.show(getFragmentManager(), "MonthYearPickerDialog");

            }
        });

        studentBottomClassesView.findViewById(R.id.tv_fbd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type=0;



                DatePickerDialog dialog1 = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yearCal, int monthOfYear, int dayOfMonth) {
                        utils.showLog(TAG,"vals "+yearCal+" "+monthOfYear+" "+dayOfMonth);
                        try {
                            tvMonthYear.setText(new SimpleDateFormat("dd MMMM yyyy").format( new SimpleDateFormat("yyyy-MM-dd").parse(yearCal+"-"+(monthOfYear+1)+"-"+dayOfMonth)));
                            filterDate = yearCal+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                            currentMonth = (monthOfYear+1)+"";
                            currentYear = yearCal+"";
                            StudentBottomClasses.this.dayOfMonth = dayOfMonth;
                            ViewAnimation.showOut(studentBottomClassesView.findViewById(R.id.ll_backdrop));
                            toggle = false;
                            currentTab = viewPager.getCurrentItem();
                            init();
                            viewPager.setCurrentItem(currentTab);
                            studentBottomClassesView.findViewById(R.id.ll_liveattendance).setVisibility(View.VISIBLE);
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
        
        
        return studentBottomClassesView;
    }


    @Override
    public void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = StudentBottomClasses.this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mActivity.registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
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
        mActivity.unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void init() {
        calendar = Calendar.getInstance();
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        getLiveAttendance();
        setAdapter();
    }

    void setAdapter() {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new StudentLiveClassesFragCopy(), "Live Classes");
        adapter.addFragment(new StudentRecordedVideosFragCopy(), "Recorded Videos");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentTab);
        swipeLayout.setRefreshing(false);
//        getLiveAttendance();
    }

    void getLiveAttendance() {
        utils.showLoader(mActivity);

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
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
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
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = ParentjObject.getJSONArray("studentAttendance");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<LiveStudentAttendance>>() {
                            }.getType();

                            liveStudentAttendances.clear();
                            liveStudentAttendances.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "liveStudentAttendances size- " + liveStudentAttendances.size());
                            mActivity.runOnUiThread(new Runnable() {
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
                                                //    Toast.makeText(mActivity, "hello", Toast.LENGTH_SHORT).show();
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
                        }
                    } catch (Exception e) {

                    }
                }

                mActivity.runOnUiThread(new Runnable() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    
}