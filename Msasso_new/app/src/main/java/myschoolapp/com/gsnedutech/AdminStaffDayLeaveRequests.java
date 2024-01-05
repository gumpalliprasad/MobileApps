package myschoolapp.com.gsnedutech;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.AdminLRALLFragment;
import myschoolapp.com.gsnedutech.Fragments.AdminLRFilteredFragment;


public class AdminStaffDayLeaveRequests extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SriRam -" + AdminStaffDayLeaveRequests.class.getName();


    @BindView(R.id.ll_fragcontainer)
    LinearLayout llFragcontainer;

    @BindView(R.id.iv_cal)
    ImageView ivCal;

    @BindView(R.id.iv_all)
    ImageView ivAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_staff_day_leave_requests_new);
        ButterKnife.bind(this);
        ivAll.setOnClickListener(this);
        ivCal.setOnClickListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_fragcontainer, new AdminLRALLFragment(), "frag1")
                .commit();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.ll_fragcontainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_cal:
                loadFragment(new AdminLRALLFragment());
                break;
            case R.id.iv_all:
                loadFragment(new AdminLRFilteredFragment());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
