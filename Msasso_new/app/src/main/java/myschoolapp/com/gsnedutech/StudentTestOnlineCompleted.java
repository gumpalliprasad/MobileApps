/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudentTestOnlineCompleted extends AppCompatActivity {

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


        setContentView(R.layout.activity_test_online_completed);
        ButterKnife.bind(this);

        tcans = getIntent().getIntExtra("cAns",0);
        twans = getIntent().getIntExtra("wAns",0);
        tunans = getIntent().getIntExtra("uAns",0);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logosplash / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                Intent i = new Intent(StudentTestOnlineCompleted.this, AssignedTestActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
//            }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(StudentTestOnlineCompleted.this, AssignedTestActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        overridePendingTransition(0, 0);
        finish();
    }

    @OnClick(R.id.btn_result)
    public void onViewClicked() {
//        Intent intent = new Intent(StudentTestOnlineCompleted.this, 3StudentTestOnlineResult.class);
//        intent.putExtra("cAns",tcans);
//        intent.putExtra("wAns",twans);
//        intent.putExtra("uAns",tunans);
//        startActivity(intent);
//        finish();
    }
}
