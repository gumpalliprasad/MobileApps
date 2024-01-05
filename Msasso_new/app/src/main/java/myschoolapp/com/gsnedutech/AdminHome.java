/*
 * *
 *  * Created by SriRamaMurthy A on 24/10/19 6:07 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 21/10/19 3:11 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Fragments.AdminBottomFragCourse;
import myschoolapp.com.gsnedutech.Fragments.AdminBottomFragFeatured;
import myschoolapp.com.gsnedutech.Fragments.AdminBottomFragNotifications;
import myschoolapp.com.gsnedutech.Fragments.AdminBottomFragRankrPlus;
import myschoolapp.com.gsnedutech.Fragments.AdminBottomFragTests;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import de.hdodenhof.circleimageview.CircleImageView;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class AdminHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SriRam -" + AdminHome.class.getName();


    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    boolean doubleBackToExitPressedOnce = false;

    @BindView(R.id.admin_nav_view)
    NavigationView adminNavView;
    @BindView(R.id.nav_img)
    CircleImageView navImg;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.nav_bview)
    BottomNavigationView navBview;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_featured:
                    fragment = new AdminBottomFragFeatured();
                    break;
                case R.id.navigation_course:
                    fragment = new AdminBottomFragCourse();
                    break;
                case R.id.navigation_tests:
                    fragment = new AdminBottomFragTests();
                    break;
                case R.id.navigation_rankrPlus:
                    fragment = new AdminBottomFragRankrPlus();
                    break;
                case R.id.navigation_notifications:
                    fragment = new AdminBottomFragNotifications();
                    break;

            }

            try {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }

            return loadFragment(fragment);
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        ButterKnife.bind(this);

        init();

        adminNavView.setNavigationItemSelectedListener(this);
        adminNavView.bringToFront();

        navBview.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            if (getIntent().getIntExtra("tabNo",0)==3){
                loadFragment(new AdminBottomFragTests());
                navBview.getMenu().findItem(R.id.navigation_tests).setChecked(true);
            }else{
                loadFragment(new AdminBottomFragFeatured());

            }


    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (navBview.getMenu().findItem(R.id.navigation_featured).isChecked()){
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }else{
//                loadFragment(new AdminBottomFragFeatured());
//                navBview.getMenu().findItem(R.id.navigation_featured).setChecked(true);
            }

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.v(TAG, "" + id);

        if (id == R.id.nav_schinfo) {
            Toast.makeText(this, "Features is Disabled", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_news) {
            Toast.makeText(this, "Features is Disabled", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            MyUtils.userLogOut(toEdit, AdminHome.this, sh_Pref);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @OnClick(R.id.nav_img)
    public void onViewClicked() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.openDrawer(GravityCompat.START);
        else drawerLayout.closeDrawer(GravityCompat.END);
    }



}
