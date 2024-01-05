package myschoolapp.com.gsnedutech.descriptive;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.descriptive.fragments.StudentOTDescPreviousExamsFrag;
import myschoolapp.com.gsnedutech.descriptive.fragments.StudentOTDescriptiveExamsFrag;

public class StudentDescExamsTabs extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = StudentDescExamsTabs.class.getName();

    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    Adapter adapter;


    @BindView(R.id.tb_main)
    TabLayout tabLayout;
    @BindView(R.id.vp_main)
    ViewPager viewPager;

    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    String currentMonth,currentYear;
    int currentTab=0;

    private MonthChangeListner monthChangeListner ;
    private MonthChangeListner monthChangeListner1;

    public void setListener(MonthChangeListner listener)
    {
        this.monthChangeListner = listener ;
    }

    public void setListener1(MonthChangeListner listener)
    {
        this.monthChangeListner1 = listener ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_live_exams_tabs);

        ButterKnife.bind(this);

        currentMonth = new SimpleDateFormat("MM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());

        init();


        tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        findViewById(R.id.cv_date_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, (month - 1), 1);

                        currentMonth = month+"";
                        currentYear = year+"";

//                        getHomeWorks();
                        monthChangeListner.onMonthChanged(mon+" "+year);
                        monthChangeListner1.onMonthChanged(mon+" "+year);

                        tvMonthYear.setText(mon + " " + year);


                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

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
            utils.alertDialog(1, StudentDescExamsTabs.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
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



    void init(){
        adapter = new Adapter(getSupportFragmentManager());
//        adapter.addFragment(new StudentOTLiveExamsFrag(),"Live Exams");
        adapter.addFragment(new StudentOTDescriptiveExamsFrag(), "Live Descriptive Exams");
        adapter.addFragment(new StudentOTDescPreviousExamsFrag(),"Previous Descriptive Exams");
//            String month = new SimpleDateFormat("MMMM").format(new SimpleDateFormat("MM").parse(currentMonth));
//            StudentLiveExamsFrag liveExamsFrag = StudentLiveExamsFrag.newInstance(month+" "+ currentYear);
//            setListener(liveExamsFrag);
//            adapter.addFragment(liveExamsFrag,"Live Exams");
//            StudentPreviousExamsFrag previousExamsFrag = StudentPreviousExamsFrag.newInstance(month+" "+ currentYear);
//            setListener1(previousExamsFrag);
//            adapter.addFragment(previousExamsFrag,"Previous Exams");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        swipeLayout.setRefreshing(false);

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

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public interface MonthChangeListner
    {
        void onMonthChanged(String month) ;
    }
}