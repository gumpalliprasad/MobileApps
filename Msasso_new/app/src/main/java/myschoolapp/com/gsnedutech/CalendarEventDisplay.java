package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class CalendarEventDisplay extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.tv_date_time)
    TextView tvDateTime;

    MyUtils utils = new MyUtils();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_event_display);
        ButterKnife.bind(this);

        init();
    }

    void init(){

        tvTitle.setText(getIntent().getStringExtra("event title"));
        tvDesc.setText(getIntent().getStringExtra("event desc"));
        if (getIntent().getStringExtra("type").equalsIgnoreCase("Holidays")){
            llMain.setBackgroundResource(R.drawable.bg_orange_grad_cal);
        }else {
            llMain.setBackgroundResource(R.drawable.bg_blue_grad_cal);
        }


        tvDateTime.setText(getIntent().getStringExtra("date")+" 10:00-11:30 AM");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}