/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.AssignedTestAnalyticsFrag;
import myschoolapp.com.gsnedutech.Fragments.MockTestAnalyticsFrag;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class StudentAnalytics extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "SriRam -" + StudentAnalytics.class.getName();

    @BindView(R.id.Tablayout)
    TabLayout Tablayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    MyUtils utils = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_analytics);
        ButterKnife.bind(this);

//        getSupportActionBar().setElevation(0);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("Analytics");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        Tablayout.addTab(Tablayout.newTab().setText("Mock Tests"));
//        Tablayout.addTab(Tablayout.newTab().setText("Assigned Tests"));
        if (getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getBoolean("parent_loggedin", false))
            Tablayout.addTab(Tablayout.newTab().setText("Assigned Tests"));

        //Creating our pager adapter
        AnalyticsViewPageAdapter adapter = new AnalyticsViewPageAdapter(getSupportFragmentManager(), Tablayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(Tablayout));

        //Adding onTabSelectedListener to swipe views
        Tablayout.addOnTabSelectedListener(this);

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class AnalyticsViewPageAdapter extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;

        //Constructor to the class
        public AnalyticsViewPageAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount = tabCount;
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {

                case 0:
                    MockTestAnalyticsFrag tab1 = new MockTestAnalyticsFrag();
                    return tab1;

                case 1:
                    AssignedTestAnalyticsFrag tab2 = new AssignedTestAnalyticsFrag();
                    return tab2;

                default:
                    return null;
            }
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }

}
