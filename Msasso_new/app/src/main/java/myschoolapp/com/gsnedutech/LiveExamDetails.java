package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.LiveExamDeatilsFrag;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;

public class LiveExamDetails extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + LiveExamDetails.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    StudentOnlineTestObj liveExam;
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_test_name)
    TextView tvTestName;
    @BindView(R.id.tv_test_category)
    TextView tvTestCategory;
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;

    StudentObj sObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_live_exam_details);
        ButterKnife.bind(this);

//        init();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, new LiveExamDeatilsFrag())
                .commit();


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btn_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!liveExam.getMjeeSectionTemplateName().equalsIgnoreCase("NA")) {
                    Intent onlineTestIntent = new Intent(LiveExamDetails.this, JEETestMarksDivision.class);
                    onlineTestIntent.putExtra("studentId", sObj.getStudentId());
                    onlineTestIntent.putExtra("live", (Serializable) liveExam);
                    startActivity(onlineTestIntent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                } else {
                    Intent onlineTestIntent = new Intent(LiveExamDetails.this, StudentOnlineTestActivity.class);
                    onlineTestIntent.putExtra("studentId", sObj.getStudentId());
                    onlineTestIntent.putExtra("live", (Serializable) liveExam);
                    startActivity(onlineTestIntent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            }
        });
    }

    public boolean loadFragment(Fragment fragment) {

        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack("details")
                    .commit();
            return true;
        }
        return false;
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
            utils.alertDialog(1, LiveExamDetails.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}