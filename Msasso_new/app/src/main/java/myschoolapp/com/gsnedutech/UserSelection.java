package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.splash.SplashFragment;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class UserSelection extends AppCompatActivity {

    @BindView(R.id.ll_select)
    LinearLayout llSelect;
    @BindView(R.id.rl_selected)
    RelativeLayout rlSelected;
    @BindView(R.id.iv_student)
    ImageView ivStudent;
    @BindView(R.id.iv_teacher)
    ImageView ivTeacher;
    @BindView(R.id.iv_parent)
    ImageView ivParent;
    @BindView(R.id.iv_admin)
    ImageView ivAdmin;
    @BindView(R.id.iv_selection)
    ImageView ivSelection;
    @BindView(R.id.app_logo)
    ImageView appLogo;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.college_code_img)
    ImageView collegeCodeImg;
    MyUtils utils = new MyUtils();
    int backPress=0;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        ButterKnife.bind(this);

        init();
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        if(sh_Pref.contains(AppConst.COLLEGE_LOGO) && !sh_Pref.getString(AppConst.COLLEGE_LOGO, "").isEmpty())
            Picasso.with(UserSelection.this).load(sh_Pref.getString(AppConst.COLLEGE_LOGO, "")).placeholder(R.color.semi_transparent)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(appLogo);
        ivStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent studentIntent = new Intent(UserSelection.this, RegNew.class);
                studentIntent.putExtra("user", "student");
                MyUtils.updateSharedPreferences(toEdit, AppConst.USER_SELECTED, AppConst.USER_STUDENT);
                startActivity(studentIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        ivParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parentIntent = new Intent(UserSelection.this, RegAdmissionNew.class);
                parentIntent.putExtra("user", "parent");
                MyUtils.updateSharedPreferences(toEdit, AppConst.USER_SELECTED, AppConst.USER_PARENT);
                startActivity(parentIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        ivAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent teacherIntent = new Intent(UserSelection.this, RegAdmissionNew.class);
                teacherIntent.putExtra("user", "admin");
                startActivity(teacherIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        ivTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent teacherIntent = new Intent(UserSelection.this, RegAdmissionNew.class);
                teacherIntent.putExtra("user", "teacher");
                MyUtils.updateSharedPreferences(toEdit, AppConst.USER_SELECTED, AppConst.USER_TEACHER);
                startActivity(teacherIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        imgBack.setOnClickListener(v -> finish());

        collegeCodeImg.setOnClickListener(v -> {
            toEdit.clear().commit();
            showFragmentNoBackStack(new SplashFragment(), R.id.cc_splash);
        });
    }



    @Override
    public void onBackPressed() {
//        backPress++;
//        if (backPress == 1) {
//            Toast.makeText(UserSelection.this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
//        } else {
            super.onBackPressed();
//        }
        toEdit.clear().commit();
    }

    protected void showFragmentNoBackStack(Fragment fragment, int containerId) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(
                        containerId, //if (containerId == 0) R.id.fragment_container else containerId,
                        fragment,
                        fragment.getTag()
                )
                .commitAllowingStateLoss();
    }
}