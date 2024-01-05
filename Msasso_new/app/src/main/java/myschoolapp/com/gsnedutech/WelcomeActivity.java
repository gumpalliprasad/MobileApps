package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "SriRam -" + WelcomeActivity.class.getName();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    String studentId = "";
    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_synopsis)
    RecyclerView rvSynopsis;

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.nav_img)
    CircleImageView profImg;
    @BindView(R.id.welcome_logo)
    ImageView appLogo;
    Timer t;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.ll_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(WelcomeActivity.this,StudentHome.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            }
        });


        tvDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
                    }
                });
            }
        }, 0, 1000);


    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        if(sh_Pref.contains(AppConst.COLLEGE_LOGO) && !sh_Pref.getString(AppConst.COLLEGE_LOGO, "").isEmpty())
            Picasso.with(WelcomeActivity.this).load(sh_Pref.getString(AppConst.COLLEGE_LOGO, "")).placeholder(R.color.semi_transparent)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(appLogo);
        Picasso.with(WelcomeActivity.this).load(sObj.getProfilePic()).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImg);

        tvName.setText(sObj.getStudentName());
        tvDetail.setText(sObj.getLoginId()+" | "+sObj.getClassName()+" "+sObj.getSectionName());

        studentId = sObj.getStudentId();

        rvSynopsis.setLayoutManager(new LinearLayoutManager(WelcomeActivity.this,RecyclerView.HORIZONTAL,false));
        rvSynopsis.setAdapter(new SynopsisAdapter());

    }


    @Override
    protected void onStop() {
        t.cancel();
        super.onStop();
    }


    class SynopsisAdapter extends RecyclerView.Adapter<SynopsisAdapter.ViewHolder>{

        @NonNull
        @Override
        public SynopsisAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.item_welcome_synopsis,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SynopsisAdapter.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

}