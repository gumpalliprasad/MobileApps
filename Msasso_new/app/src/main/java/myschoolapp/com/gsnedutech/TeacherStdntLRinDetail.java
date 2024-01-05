package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.StudentsLeaveReq;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherStdntLRinDetail extends AppCompatActivity {

    private static final String TAG = "SriRam -" + TeacherStdntLRinDetail.class.getName();
    MyUtils utils = new MyUtils();

    @BindView(R.id.sp_leave_status)
    Spinner spLeaveStatus;

    @BindView(R.id.tv_done)
    TextView tvDone;
    @BindView(R.id.iv_student)
    CircleImageView ivStudent;
    @BindView(R.id.tv_student_name)
    TextView tvStudentName;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.tv_applied)
    TextView tvApplied;
    @BindView(R.id.tv_lr_from)
    TextView tvLrFrom;
    @BindView(R.id.tv_lr_to)
    TextView tvLrTo;
    @BindView(R.id.tv_lr_days)
    TextView tvLrDays;
    @BindView(R.id.tv_lr_desc)
    TextView tvLrDesc;

    StudentsLeaveReq lrObj;
    String[] status = {"Pending", "Approved", "Rejected"};

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;
    AdminObj adminObj;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_stdnt_l_rin_detail);
        ButterKnife.bind(this);
        init();
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());

    }

    public void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            isAdmin = false;
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            isAdmin = true;
            String json = sh_Pref.getString("adminObj", "");
            adminObj = gson.fromJson(json, AdminObj.class);
        }
        lrObj = (StudentsLeaveReq) getIntent().getSerializableExtra("reqObj");
        if (lrObj!=null){
            tvStudentName.setText(lrObj.getStudentName());
            tvReason.setText(lrObj.getReason());
            tvLrDesc.setText(lrObj.getDescription());
            try {
                tvLrDays.setText(getDateDifference(new SimpleDateFormat("yyyy-MM-dd").parse(lrObj.getLeaveFrom()),new SimpleDateFormat("yyyy-MM-dd").parse(lrObj.getLeaveTo()))+" days");
                tvApplied.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(lrObj.getLeaveFrom())));
                tvLrFrom.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(lrObj.getLeaveFrom())));
                tvLrTo.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(lrObj.getLeaveTo())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, status);
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
            spLeaveStatus.setAdapter(adapter);
            if (lrObj.getLeaveReqestStatus().equalsIgnoreCase("LR")){
                spLeaveStatus.setEnabled(true);
            }
            else {
                if (lrObj.getLeaveReqestStatus().equalsIgnoreCase("ALR")){
                    spLeaveStatus.setSelection(1);
                }
                else {
                    spLeaveStatus.setSelection(2);
                }
                tvDone.setVisibility(View.GONE);
                spLeaveStatus.setEnabled(false);
            }
            spLeaveStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i>0){
                        if (spLeaveStatus.isEnabled())
                            tvDone.setVisibility(View.VISIBLE);
                        else {
                            ((TextView)view).setTextColor(ContextCompat.getColor(TeacherStdntLRinDetail.this,R.color.black));
                        }
                    }
                    else tvDone.setVisibility(View.GONE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spLeaveStatus.getSelectedItemPosition()==1){
                    if (isAdmin)
                        updateStudentLeaveRequest(""+adminObj.getUserId(), "" + lrObj.getStudentLeaveReqId(), "1", "");
                    else updateStudentLeaveRequest(""+tObj.getUserId(), "" + lrObj.getStudentLeaveReqId(), "1", "");
                }
                else if (spLeaveStatus.getSelectedItemPosition() ==2){
                    if (isAdmin)
                        updateStudentLeaveRequest(""+adminObj.getUserId(), "" + lrObj.getStudentLeaveReqId(), "2", "");
                    else updateStudentLeaveRequest(""+tObj.getUserId(), "" + lrObj.getStudentLeaveReqId(), "2", "");
                }
            }
        });

    }


    int getDateDifference(Date startDate, Date endDate) {

        int diffInDays = (int)( (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) );
        return diffInDays>=0?diffInDays+1:0;

    }

    void updateStudentLeaveRequest(String updatedBy, String studentLeaveReqId, String isApproved, String teacherComments){
        utils.showLoader(this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("updatedBy", updatedBy);


            JSONObject jsonObj = new JSONObject();
            jsonObj.put("studentLeaveReqId", studentLeaveReqId);
            jsonObj.put("isApproved", isApproved);
            jsonObj.put("updatedBy", updatedBy);
            jsonObj.put("teacherComments", teacherComments);


            JSONArray jsArray = new JSONArray();
            jsArray.put(jsonObj);
            jsonObject.put("updateLeaveReq", jsArray);

            Log.v(TAG, String.valueOf(jsonObject));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + new AppUrls().UpdateStudentLeaveRequest);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(new AppUrls().UpdateStudentLeaveRequest)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "UpdateStaffLeaveRequest responce - " + jsonResp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);

                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//                        Toast.makeText(getActivity(), "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                   onBackPressed();
                                } else {
//                        Toast.makeText(getActivity(), "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                  onBackPressed();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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