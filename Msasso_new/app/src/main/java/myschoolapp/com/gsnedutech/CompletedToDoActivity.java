package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.ToDosObj;

public class CompletedToDoActivity extends AppCompatActivity {

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_desscription)
    EditText etDescription;

    @BindView(R.id.et_start_time)
    EditText etStartTime;

    @BindView(R.id.et_end_time)
    EditText etEndTime;

    @BindView(R.id.tv_month_name)
    TextView tvMonthName;

    ToDosObj toDo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_to_do);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

    }

    public void  init(){
        toDo = (ToDosObj) getIntent().getSerializableExtra("todo");

        etTitle.setText(toDo.getTodoTitle());
        etDescription.setText(toDo.getTodoDesc());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date st = null;

        try {
            if (toDo.getTodoStartTime()!=null){
                Date et = sdf.parse(toDo.getTodoEndTime());
                st = sdf.parse(toDo.getTodoStartTime());
                etEndTime.setText(simpleDateFormat.format(et));
                etStartTime.setText(simpleDateFormat.format(st));
                tvMonthName.setText(new SimpleDateFormat("MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(toDo.getTodoStartTime())));
            }else {
                tvMonthName.setVisibility(View.INVISIBLE);
                etStartTime.setText("-");
                etEndTime.setText("-");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}