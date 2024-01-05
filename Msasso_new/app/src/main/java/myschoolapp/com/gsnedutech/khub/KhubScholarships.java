package myschoolapp.com.gsnedutech.khub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KHubBanners;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KhubScholarships extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubScholarships.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.btn_apply)
    Button btnApply;
    @BindView(R.id.cb_terms)
    CheckBox cbTerms;

    KHubBanners kHubBanners;

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    StudentObj sObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_scholarships);
        ButterKnife.bind(this);
        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnApply.setOnClickListener(view -> {
            if (cbTerms.isChecked()){
                showDialogForm();
            }
            else {
                new AlertDialog.Builder(KhubScholarships.this).
                        setTitle("Alert").
                        setMessage("Please read the Terms &amp; Conditions and Check the box").
                        setPositiveButton("Ok", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }).show();
            }

        });

    }

    private void showDialogForm() {

        final Dialog dialog = new Dialog(KhubScholarships.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_khub_scholorship);
        dialog.setCancelable(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int wwidth = metrics.widthPixels;
        dialog.getWindow().setLayout((int) (wwidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText etStudentName = dialog.findViewById(R.id.et_stdn_name);
        EditText etSchoolName = dialog.findViewById(R.id.et_sh_name);
        EditText etLocality =  dialog.findViewById(R.id.et_locality);
        EditText etCity = dialog.findViewById(R.id.et_city);
        EditText etState =  dialog.findViewById(R.id.et_state);
        EditText etAdminNo = dialog.findViewById(R.id.et_adm_no);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        ImageView close = dialog.findViewById(R.id.iv_close);
        etStudentName.setText(sObj.getStudentName());

        close.setOnClickListener(view -> {
            dialog.dismiss();
        });
        btnSubmit.setOnClickListener(view -> {
            if (etAdminNo.getText().toString().isEmpty() || etStudentName.getText().toString().isEmpty()|| etSchoolName.getText().toString().isEmpty()||
                    etLocality.getText().toString().isEmpty() || etCity.getText().toString().isEmpty() || etState.getText().toString().isEmpty()){
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
            else {
                postDetails(etSchoolName.getText().toString(), etLocality.getText().toString(), etCity.getText().toString(), etState.getText().toString(),
                        etAdminNo.getText().toString(), etStudentName.getText().toString());
            }
        });

        dialog.show();


    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        kHubBanners = (KHubBanners) getIntent().getSerializableExtra("banner");
        tvDesc.setText(Html.fromHtml(kHubBanners.getBannerDesc()), TextView.BufferType.SPANNABLE);

    }

    private void postDetails(String schoolName, String locality, String city, String state, String admissionNo, String studentName) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schoolName", schoolName);
            jsonObject.put("locality", locality);
            jsonObject.put("city", city);
            jsonObject.put("State", state);
            jsonObject.put("studentClass", sObj.getClassName());
            jsonObject.put("admissionNo", admissionNo);
            jsonObject.put("studentName", studentName);
            jsonObject.put("studentId", sObj.getStudentId());
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.KhubScholorShip,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.KhubScholorShip);
        utils.showLog(TAG, "body -"+ jsonObject.toString());

        apiClient.getClient().newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject ParentjObject = new JSONObject(res);
                        if (response.code() == 200) {
                            runOnUiThread(() -> {
                                onBackPressed();
//                                Toast.makeText(KhubCategoryDetail.this, "Course Enrolled Successfully", Toast.LENGTH_SHORT).show();
                            });

                        }
                    }
                    catch (JSONException e) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubScholarships.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }
}