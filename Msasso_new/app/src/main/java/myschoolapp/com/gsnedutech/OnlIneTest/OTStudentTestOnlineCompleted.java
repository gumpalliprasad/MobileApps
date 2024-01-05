/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.OnlIneTest;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.R;

public class OTStudentTestOnlineCompleted extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000;

    int tunans=0,tcans=0,twans=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_ot_test_online_completed);
        ButterKnife.bind(this);

        tcans = getIntent().getIntExtra("cAns",0);
        twans = getIntent().getIntExtra("wAns",0);
        tunans = getIntent().getIntExtra("uAns",0);

        new Handler().postDelayed(() -> finish(), SPLASH_TIME_OUT);
//            }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.btn_result)
    public void onViewClicked() {
        finish();
    }
}
