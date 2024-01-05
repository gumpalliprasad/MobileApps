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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentProfileObj;
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

public class ProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = ProfileActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    StudentProfileObj studentProfileObj = null;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.tv_stud_id)
    TextView tvStudentId;
    @BindView(R.id.tv_yoj)
    TextView tvYoj;
    @BindView(R.id.tv_dob)
    TextView tvDob;
    @BindView(R.id.tv_parent_name)
    TextView tvParentName;
    @BindView(R.id.tv_parent_num)
    TextView tvParentNum;
    @BindView(R.id.tv_parent_email)
    TextView tvParentEmail;
    @BindView(R.id.img_profile)
    CircleImageView profImgPicBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        if (studentProfileObj!=null){
            Picasso.with(ProfileActivity.this).load(studentProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
//        }else{
//            Picasso.with(ProfileActivity.this).load(new AppUrls().GetstudentProfilePic + sObj.getProfilePic()).placeholder(R.drawable.user_default)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
        }
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, ProfileActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getProfileDetails();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("student", (Serializable) studentProfileObj);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

    }


    void getProfileDetails() {
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetstudentDetailsById + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId())
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, new AppUrls().GetstudentDetailsById + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId());

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
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            studentProfileObj = gson.fromJson(jsonObject.toString(), StudentProfileObj.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    tvName.setText(studentProfileObj.getStudentName());
                                    tvClass.setText(studentProfileObj.getClassName());
                                    tvStudentId.setText(studentProfileObj.getLoginName());
                                    try {
                                        if (studentProfileObj.getDOB() != null && !studentProfileObj.getDOB().isEmpty())
                                            tvDob.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(studentProfileObj.getDOB())));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    tvParentName.setText(studentProfileObj.getParentName());
                                    if (!studentProfileObj.getContactNumber().equalsIgnoreCase("NA")) {
                                        tvParentNum.setText("+91 " + studentProfileObj.getContactNumber());
                                    } else {
                                        tvParentNum.setText("-");
                                    }
                                    if (studentProfileObj.getParentEmailId() != null)
                                        tvParentEmail.setText(studentProfileObj.getParentEmailId().toLowerCase());

                                    Picasso.with(ProfileActivity.this).load(studentProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
                                }
                            });
                        }else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)){ //TODO New Changes
                            String message = jsonObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, ProfileActivity.this, message, sh_Pref);
                            });
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}