package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Models.TeacherProfileObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener{
    private static final String TAG = TeacherProfileActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj teacherObj;
    TeacherProfileObj teacherProfileObj = null;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_designation)
    TextView tvDesignation;
    @BindView(R.id.tv_emp_id)
    TextView tvEmpId;
    @BindView(R.id.tv_yoj)
    TextView tvYoj;
    @BindView(R.id.tv_dob)
    TextView tvDob;
    @BindView(R.id.tv_subjects)
    TextView tvSubjects;
    @BindView(R.id.tv_parent_num)
    TextView tvParentNum;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.img_profile)
    CircleImageView profImgPicBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme,null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent,null));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_teacher_profile);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherProfileActivity.this, EditTeacherProfileActivity.class);
                intent.putExtra("teacher",(Serializable)teacherProfileObj);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

//        if (NetworkConnectivity.isConnected(this)) {
//            getProfileDetails();
//        } else {
//            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
//                    getString(R.string.action_settings), getString(R.string.action_close),false);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherProfileActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getProfileDetails(""+teacherObj.getUserId(), teacherObj.getRoleId());
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }



    void getProfileDetails(String... strings){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetuserDetailsForProfile +"schemaName=" +sh_Pref.getString("schema","")+ "&userId=" + strings[0] + "&roleId=" + strings[1])
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, new AppUrls().GetuserDetailsForProfile +"schemaName=" +sh_Pref.getString("schema","")+ "&userId=" + strings[0] + "&roleId=" + strings[1]);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null){
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            teacherProfileObj = gson.fromJson(parentjObject.toString(), TeacherProfileObj.class);
                            runOnUiThread(() -> {
                                tvName.setText(teacherProfileObj.getUserName());
                                tvDesignation.setText(teacherProfileObj.getRoleName());
                                tvEmpId.setText(teacherProfileObj.getInstName());
                                tvYoj.setText(teacherProfileObj.getUserJoiningDate());
//                        tvDob.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(teacherProfileObj.get())));
                                tvSubjects.setText(teacherProfileObj.getSubjects());
//                        if (!studentProfileObj.getContactNumber().equalsIgnoreCase("NA")){
//                            tvParentNum.setText("+91 "+studentProfileObj.getContactNumber());
//                        }else {
//                            tvParentNum.setText("-");
//                        }
                                tvEmail.setText(teacherProfileObj.getEmailId().toLowerCase());

                                Picasso.with(TeacherProfileActivity.this).load(new AppUrls().GetStaffProfilePic + teacherProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, TeacherProfileActivity.this, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}