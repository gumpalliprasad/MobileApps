package myschoolapp.com.gsnedutech;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.CircularTransactions;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;

public class CircularDisplay extends AppCompatActivity {
    
    @BindView(R.id.tv_circular_title)
    TextView tvCircularTitle;
    @BindView(R.id.tv_circular_desc)
    TextView tvCircularDesc;
    @BindView(R.id.tv_circular_time)
    TextView tvCircularTime;
    @BindView(R.id.iv_attach)
    TextView ivAttach;
    
    CircularTransactions circularTransactionsObj;

    MyUtils utils = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_display);
        ButterKnife.bind(this);
        
        init();
    }
    
    void init(){

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        circularTransactionsObj = (CircularTransactions) getIntent().getSerializableExtra("circulars");
        tvCircularTitle.setText(circularTransactionsObj.getTransactionName());
//            tvCircularDesc.setText(circularTransactionsObj.getTransactionType());
        try {
            tvCircularTime.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(circularTransactionsObj.getTransactionDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!circularTransactionsObj.getFilePath().equalsIgnoreCase("NA")){
            ivAttach.setVisibility(View.VISIBLE);
        }else {
            ivAttach.setVisibility(View.GONE);
        }

        ivAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = new AppUrls().HomeWorkFileDownLoadPDF + circularTransactionsObj.getFilePath();

                Intent intent = new Intent(CircularDisplay.this, NewPdfViewer.class);
                intent.putExtra("url",url);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}