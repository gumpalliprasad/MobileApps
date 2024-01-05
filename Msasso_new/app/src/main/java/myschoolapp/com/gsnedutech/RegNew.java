package myschoolapp.com.gsnedutech;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.OTUserDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.OnlIneTest.Config;
import myschoolapp.com.gsnedutech.OnlIneTest.LiveExams;
import myschoolapp.com.gsnedutech.OnlIneTest.OTLoginResult;
import myschoolapp.com.gsnedutech.OnlIneTest.OTStudentTestActivity;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegNew extends AppCompatActivity {

    private static final String TAG = "SriRam -" + RegNew.class.getName();

    OTUserDetails otUserDetails;
    StudentObj sObj;

    boolean otFail = false;
    boolean goToCourse = true;

    ArrayList<LiveExams> examDetails = new ArrayList<>();

    String bottomView = "admission";
    String serverTime = "";

    @BindView(R.id.ll_admission)
    LinearLayout llAdmission;
    @BindView(R.id.ll_password)
    LinearLayout llPassword;
    @BindView(R.id.ll_confirm)
    LinearLayout llConfirm;
    @BindView(R.id.ll_dob)
    LinearLayout llDob;
    @BindView(R.id.ll_otp)
    LinearLayout llOtp;
    @BindView(R.id.ll_create_password)
    LinearLayout llCreatePassword;
    @BindView(R.id.ll_security_ques)
    LinearLayout llSecurityQues;
    @BindView(R.id.txt_pin_entry)
    PinEntryEditText pinEntryEditText;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.ll_top_reg)
    LinearLayout llTopReg;
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_admissionNo)
    EditText etAdmissionNo;
    @BindView(R.id.et_user_password)
    EditText etPassword;
    @BindView(R.id.password_visible)
    ImageView ivPassVisible;

    @BindView(R.id.et_year)
    EditText etYear;
    @BindView(R.id.et_month)
    EditText etMonth;
    @BindView(R.id.et_day)
    EditText etDay;

    @BindView(R.id.et_new_pin)
    PinEntryEditText newPin;
    @BindView(R.id.et_new_pin_confirm)
    PinEntryEditText newPinConfirm;

    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.pin_last_four_digits)
    PinEntryEditText pinLastFourDigits;

    @BindView(R.id.ll_confirm_options)
    LinearLayout llConfirmOptions;
    @BindView(R.id.tv_op_dob)
    TextView tvOpDob;
    @BindView(R.id.tv_op_number)
    TextView tvOpNumber;
    @BindView(R.id.logo)
    ImageView appLogo;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    String deviceToken, IMEI;

    MyUtils utils = new MyUtils();

    OTLoginResult otLoginResultObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_new);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.tv_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:04066588070"))); //AppConst.CUSTOMER_CARE_NUMBER
            }
        });
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        if (sh_Pref.contains(AppConst.COLLEGE_LOGO) && !sh_Pref.getString(AppConst.COLLEGE_LOGO, "").isEmpty())
            Picasso.with(RegNew.this).load(sh_Pref.getString(AppConst.COLLEGE_LOGO, "")).placeholder(R.color.semi_transparent)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(appLogo);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        deviceToken = task.getResult();
                        Log.v("newToken", deviceToken);
                    }
                });

        if (getIntent().hasExtra("isLoggedin")) {
            if (sh_Pref.getBoolean("student_ot_loggedin", false)) {
                Gson gson = new Gson();
                String json = sh_Pref.getString("otStudentObj", "");
                OTLoginResult stdObj = gson.fromJson(json, OTLoginResult.class);
                getExamOtDetails(stdObj.getAdmissionNo());
            } else {
                otFail = true;
                getServerTime();
                Gson gson = new Gson();
                String json = sh_Pref.getString("studentObj", "");
                sObj = gson.fromJson(json, StudentObj.class);
                findViewById(R.id.ll_top).setVisibility(View.GONE);
                findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tv_student_name)).setText(sObj.getStudentName());
                ((TextView) findViewById(R.id.login_id)).setText(sObj.getLoginId());
                ((TextView) findViewById(R.id.tv_class)).setText(sObj.getClassName() + " - " + sObj.getSectionName());
            }
        }


        //next on admission
        findViewById(R.id.btn_admission_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etAdmissionNo.getText().toString().trim().equalsIgnoreCase("")) {
                    getExamOtDetails(etAdmissionNo.getText().toString().trim());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegNew.this);
                    builder.setMessage("Please enter the Admission Number")
                            .setTitle(getResources().getString(R.string.app_name))
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    }

                            );
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        //button Forgot Password
        findViewById(R.id.tv_forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAnimation.showOut(llPassword);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pinEntryEditText.setText("");
                        bottomView = "confirm";
                        ViewAnimation.showIn(llConfirm);
                    }
                }, 300);
            }
        });

        //Button Confirm It's me
        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewAnimation.showOut(llConfirm);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomView = "number";
                        //TODO change code for phone number
                        if (otFail) {
                            tvPhone.setText(sObj.getPhoneNumber().substring(0, 6) + "XXXX");
                        } else {
                            tvPhone.setText(otUserDetails.getPhoneNumber().substring(0, 6) + "XXXX");
                        }
                        ViewAnimation.showIn(llPhone);
                        //ViewAnimation.showIn(llDob);
                    }
                }, 300);
            }
        });

        //Button No, Go Back
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Button Confirm Phone Number
        findViewById(R.id.btn_confirm_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otFail) {
                    if (pinLastFourDigits.getText().toString().length() == 4) {
                        confirmPhone(sObj.getPhoneNumber().substring(0, 6) + pinLastFourDigits.getText().toString());
                    } else {
                        Toast.makeText(RegNew.this, "Please enter the Last 4 digits Admission number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (pinLastFourDigits.getText().toString().length() == 4) {
                        confirmPhone(otUserDetails.getPhoneNumber().substring(0, 6) + pinLastFourDigits.getText().toString());
                    } else {
                        Toast.makeText(RegNew.this, "Please enter the Last 4 digits Admission number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Button Next on Create Password
        findViewById(R.id.btn_cp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPin.getText().toString().length() == 4 && newPinConfirm.getText().toString().length() == 4 && newPin.getText().toString().equalsIgnoreCase(newPinConfirm.getText().toString())) {
                    createPassword(newPin.getText().toString());
                } else {
                    showErrorDialog("Please Enter valid 4 digit password!");
                }
            }
        });

        //Button Course Login
        findViewById(R.id.tv_course_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinEntryEditText.getText().toString().length() == 4) {

                    hideKeyboard(RegNew.this);

                    if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("student_ot_loggedin", false)) {

                        Log.v(TAG, "Pin - " + sh_Pref.getString("pin", ""));
                        Log.v(TAG, "Pin pin- " + pinEntryEditText.getText().toString());


                        if (sh_Pref.getString("pin", "").equalsIgnoreCase(pinEntryEditText.getText().toString()))
                            if (otFail) {
                                getStudentStatus(sObj.getStudentId(), sh_Pref.getString("schema", ""));
                            } else {
                                getStudentStatus(otUserDetails.getMStudentId() + "", sh_Pref.getString("schema", ""));
                            }
                        else {
                            utils.dismissDialog();
                            showErrorDialog("Incorrect Pin.Please check your credentials");
                        }
                    } else {
                        checkPassWord(pinEntryEditText.getText().toString());
                    }
                } else {
                    Toast.makeText(RegNew.this, "Please Enter the Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button Exam Login
        findViewById(R.id.tv_exam_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinEntryEditText.getText().toString().length() == 4) {
                    hideKeyboard(RegNew.this);
                    loginOnlineTest(pinEntryEditText.getText().toString());
                } else {
                    Toast.makeText(RegNew.this, "Please Enter the details!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Button Confirm Phone Number
        findViewById(R.id.btn_dob_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid(etDay.getText().toString()) && isValid(etMonth.getText().toString()) && isValid(etYear.getText().toString())) {
                    confirmDataOfBirth(etYear.getText().toString() + "-" + etMonth.getText().toString() + "-" + etDay.getText().toString());
                } else {
                    Toast.makeText(RegNew.this, "Please enter your date of birth!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Calling student detail from OT platform
    private void getExamOtDetails(String admissionNumber) {

        otFail = false;

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "url " + new AppUrls().GetSudentByAdmissionNo + "schemaName=" + sh_Pref.getString("schema", "") + "&admissionNo=" + admissionNumber);

        Request request = new Request.Builder()
                .url(new AppUrls().GetSudentByAdmissionNo + "schemaName=" + sh_Pref.getString("schema", "") + "&admissionNo=" + admissionNumber)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        //Call Sql Details if failed
                        getDetails(admissionNumber);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "resp " + resp);

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            //Call Sql Details if failed
                            getDetails(admissionNumber);
                        }
                    });
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            otUserDetails = new GsonBuilder().create().fromJson(jsonObject.getJSONObject("result").toString(), OTUserDetails.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toEdit.putString("stdPassword", otUserDetails.getMStudentPass());
                                    toEdit.commit();
                                    if (otUserDetails.getIsFirstLogin() == 1) {
                                        llPassword.setVisibility(View.GONE);
                                        findViewById(R.id.ll_top).setVisibility(View.GONE);
                                        findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);

                                        ((TextView) findViewById(R.id.tv_student_name)).setText(otUserDetails.getSName());
                                        ((TextView) findViewById(R.id.login_id)).setText(otUserDetails.getAdmissionNo());
                                        ((TextView) findViewById(R.id.tv_class)).setText(otUserDetails.getMClassName() + " - " + otUserDetails.getMSectionName());


                                        pinEntryEditText.setText("");

                                        bottomView = "confirm";
                                        ViewAnimation.showOut(llAdmission);
                                        ViewAnimation.showIn(llConfirm);
                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                //call getExams to check for exams
                                                getServerTime();


//                                                findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
//                                                findViewById(R.id.ll_top).setVisibility(View.GONE);
//
//                                                ((TextView) findViewById(R.id.tv_student_name)).setText(otUserDetails.getSName());
//                                                ((TextView) findViewById(R.id.login_id)).setText(otUserDetails.getAdmissionNo());
//                                                ((TextView) findViewById(R.id.tv_class)).setText(otUserDetails.getMClassName() + " - " + otUserDetails.getMSectionName());
//
//                                                bottomView = "password";
//                                                ViewAnimation.showOut(llAdmission);
//                                                ViewAnimation.showIn(llPassword);
                                            }
                                        }, 300);
                                    }
                                }
                            });
                        } else {
                            //Call Sql Details if failed
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getDetails(admissionNumber);
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Call Sql Details if failed
                        getDetails(admissionNumber);
                    }
                }

            }
        });

    }

    //Calling student detail from Sql Server
    void getDetails(String admissionNumber) {

        otFail = true;
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .callTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", admissionNumber);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put(AppConst.FORCE_LOGIN, "0");  //"isForceLogin":"1"
            jsonObject.put("deviceToken", deviceToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        utils.showLog(TAG, "URL - " + new AppUrls().GetStudentDetailsiForMobile);
        utils.showLog(TAG, jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().GetStudentDetailsiForMobile)
                .post(body)
                .build();

        utils.showLog(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        showErrorDialog("Please check your Admission Number and try again!");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);

                if (!response.isSuccessful()) {
                    updateProgressAndDialog("Please check your Admission Number and try again!");
                    return;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            sObj = gson.fromJson(jsonObject.getJSONObject("StudentObj").toString(), StudentObj.class);
                            MyUtils.updateSharedPreferences(toEdit, AppConst.USER_ID_DATA, sObj.getStudentId()); //TODO Change
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (sObj.getIsFirstLogin() == 1) {
                                        llPassword.setVisibility(View.GONE);
                                        findViewById(R.id.ll_top).setVisibility(View.GONE);
                                        findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);

                                        ((TextView) findViewById(R.id.tv_student_name)).setText(sObj.getStudentName());
                                        ((TextView) findViewById(R.id.login_id)).setText(sObj.getLoginId());
                                        ((TextView) findViewById(R.id.tv_class)).setText(sObj.getClassName() + " - " + sObj.getSectionName());

                                        pinEntryEditText.setText("");

                                        bottomView = "confirm";
                                        ViewAnimation.showOut(llAdmission);
                                        ViewAnimation.showIn(llConfirm);
                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                //call getExams to check for exams
                                                getServerTime();


//                                                findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
//                                                findViewById(R.id.ll_top).setVisibility(View.GONE);
//
//                                                ((TextView) findViewById(R.id.tv_student_name)).setText(otUserDetails.getSName());
//                                                ((TextView) findViewById(R.id.login_id)).setText(otUserDetails.getAdmissionNo());
//                                                ((TextView) findViewById(R.id.tv_class)).setText(otUserDetails.getMClassName() + " - " + otUserDetails.getMSectionName());
//
//                                                bottomView = "password";
//                                                ViewAnimation.showOut(llAdmission);
//                                                ViewAnimation.showIn(llPassword);
                                            }
                                        }, 300);
                                    }
                                }
                            });
                        } else {
                            updateProgressAndDialog("Please check your Admission Number and try again!");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        updateProgressAndDialog("Please check your Admission Number and try again!");
                        return;
                    }
                }

                updateProgressAndDialog("");
            }
        });

    }

    //get Server time
    private void getServerTime() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.showLoader(RegNew.this);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(AppUrls.OT_URL + "systemTime")
                .post(body)
                .build();
        utils.showLog(TAG, "url - " + AppUrls.GetServerTime);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getUpcomingExams();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("DateTime");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getUpcomingExams();
                }
            }
        });
    }

    //check for available exams for either course or exam login
    void getUpcomingExams() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                utils.showLoader(RegNew.this);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();


        String studentId = "";
        String sectionId = "";

        if (otFail) {
            studentId = sObj.getStudentId();
            sectionId = sObj.getClassCourseSectionId();
        } else {
            studentId = otUserDetails.getMStudentId() + "";
            sectionId = otUserDetails.getMSectionId() + "";
        }

//        Request get = new Request.Builder()
//                .url(AppUrls.GETSTUDENTEXAMSTATUS + "?schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId)
//                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&sectionId=" + sectionId + "&scheduledFlag=SCHEDULED")
                .build();
        utils.showLog(TAG, "url " + AppUrls.GETSTUDENTEXAMS + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&sectionId=" + sectionId + "&scheduledFlag=SCHEDULED");


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
                findViewById(R.id.tv_exam_time).setVisibility(View.INVISIBLE);
                findViewById(R.id.tv_exam_login).setVisibility(View.GONE);
                findViewById(R.id.tv_course_login).setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> utils.dismissDialog());
                    findViewById(R.id.tv_exam_time).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tv_exam_login).setVisibility(View.GONE);
                    findViewById(R.id.tv_course_login).setVisibility(View.VISIBLE);
                } else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {


                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {

                            goToCourse = true;

                            JSONArray jsonArray = ParentjObject.getJSONArray("result");


                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<LiveExams>>() {
                            }.getType();

                            examDetails.clear();
                            examDetails.addAll(gson.fromJson(jsonArray.toString(), type));

                            String dispString = "";

                            for (int i = 0; i < examDetails.size(); i++) {
                                if (examDetails.get(i).geteStatus().equalsIgnoreCase("SCHEDULED")
                                        || examDetails.get(i).geteStatus().equalsIgnoreCase("INPROGRESS")
                                        || examDetails.get(i).geteStatus().equalsIgnoreCase("SUBMITTED")) {
                                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date now = new Date();
                                    Date startDate = null;
                                    Date endDate = null;

                                    try {
                                        now = dtf.parse(serverTime);
                                        startDate = dtf.parse(examDetails.get(i).getsTime());
                                        endDate = dtf.parse(examDetails.get(i).geteTime());

                                        if (now.after(startDate) && now.before(endDate)) {
                                            goToCourse = false;
                                            dispString = "You have a live exam at \n" + new SimpleDateFormat("dd MMM yyyy hh:mm a").format(startDate);
                                            break;
                                        }

                                        if (now.before(startDate)) {
                                            long duration = startDate.getTime() - now.getTime();
                                            long diffInHours = duration / 1000;

                                            utils.showLog(TAG, "time difference " + duration);
                                            utils.showLog(TAG, "time difference " + diffInHours);

                                            if (diffInHours <= 3600) {
                                                goToCourse = false;
                                                dispString = "You have a live exam at \n" + new SimpleDateFormat("dd MMM yyyy hh:mm a").format(startDate);
                                                break;
                                            }

                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        goToCourse = true;
                                    }

                                }
                            }

                            String finalDispString = dispString;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (goToCourse) {
                                        findViewById(R.id.tv_exam_time).setVisibility(View.INVISIBLE);
                                        findViewById(R.id.tv_exam_login).setVisibility(View.GONE);
                                        findViewById(R.id.tv_course_login).setVisibility(View.VISIBLE);
                                    } else {
                                        ((TextView) findViewById(R.id.tv_exam_time)).setText(finalDispString);
                                        findViewById(R.id.tv_exam_time).setVisibility(View.VISIBLE);
                                        findViewById(R.id.tv_exam_login).setVisibility(View.VISIBLE);
                                        findViewById(R.id.tv_course_login).setVisibility(View.GONE);
//                                        findViewById(R.id.tv_course_login).setVisibility(View.VISIBLE);
                                    }
                                }
                            });


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.tv_exam_time).setVisibility(View.INVISIBLE);
                                    findViewById(R.id.tv_exam_login).setVisibility(View.GONE);
                                    findViewById(R.id.tv_course_login).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } catch (Exception e) {

                        utils.showLog(TAG, "error " + e.getMessage());
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();

                        if (otFail) {
                            runOnUiThread(() -> {
                                if (llCreatePassword.getVisibility() == View.VISIBLE) {
                                    ViewAnimation.showOut(llCreatePassword);
                                }
                                if (llAdmission.getVisibility() == View.VISIBLE) {
                                    ViewAnimation.showOut(llAdmission);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
                                        findViewById(R.id.ll_top).setVisibility(View.GONE);

                                        ((TextView) findViewById(R.id.tv_student_name)).setText(sObj.getStudentName());
                                        ((TextView) findViewById(R.id.login_id)).setText(sObj.getLoginId());
                                        ((TextView) findViewById(R.id.tv_class)).setText(sObj.getClassName() + " - " + sObj.getSectionName());


                                        bottomView = "password";

                                        ViewAnimation.showIn(llPassword);
                                    }
                                }, 300);
                            });
//                            if (sObj.getIsFirstLogin() == 1) {
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        llPassword.setVisibility(View.GONE);
//                                        findViewById(R.id.ll_top).setVisibility(View.GONE);
//                                        findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
//
//                                        ((TextView) findViewById(R.id.tv_student_name)).setText(sObj.getStudentName());
//                                        ((TextView) findViewById(R.id.login_id)).setText(sObj.getLoginId());
//                                        ((TextView) findViewById(R.id.tv_class)).setText(sObj.getClassName() + " - " + sObj.getSectionName());
//
//                                        pinEntryEditText.setText("");
//
//                                        bottomView = "confirm";
//                                        if (llCreatePassword.getVisibility()==View.VISIBLE){
//                                            ViewAnimation.showOut(llCreatePassword);
//                                        }
//                                        if (llAdmission.getVisibility()==View.VISIBLE){
//                                            ViewAnimation.showOut(llAdmission);
//                                        }
//                                        ViewAnimation.showIn(llConfirm);
//                                    }
//                                });
//                            }
//                            else {
//                                runOnUiThread(() -> {
//                                    if (llCreatePassword.getVisibility()==View.VISIBLE){
//                                        ViewAnimation.showOut(llCreatePassword);
//                                    }
//                                    if (llAdmission.getVisibility()==View.VISIBLE){
//                                        ViewAnimation.showOut(llAdmission);
//                                    }
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
//                                            findViewById(R.id.ll_top).setVisibility(View.GONE);
//
//                                            ((TextView) findViewById(R.id.tv_student_name)).setText(sObj.getStudentName());
//                                            ((TextView) findViewById(R.id.login_id)).setText(sObj.getLoginId());
//                                            ((TextView) findViewById(R.id.tv_class)).setText(sObj.getClassName() + " - " + sObj.getSectionName());
//
//
//                                            bottomView = "password";
//
//                                            ViewAnimation.showIn(llPassword);
//                                        }
//                                    }, 300);
//                                });
//                            }
                        } else {
//                            if (otUserDetails.getIsFirstLogin() == 1) {
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        llPassword.setVisibility(View.GONE);
//                                        findViewById(R.id.ll_top).setVisibility(View.GONE);
//                                        findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
//
//                                        ((TextView) findViewById(R.id.tv_student_name)).setText(otUserDetails.getSName());
//                                        ((TextView) findViewById(R.id.login_id)).setText(otUserDetails.getAdmissionNo());
//                                        ((TextView) findViewById(R.id.tv_class)).setText(otUserDetails.getMClassName() + " - " + otUserDetails.getMSectionName());
//
//                                        pinEntryEditText.setText("");
//
//                                        bottomView = "confirm";
//                                        if (llCreatePassword.getVisibility()==View.VISIBLE){
//                                            ViewAnimation.showOut(llCreatePassword);
//                                        }
//                                        if (llAdmission.getVisibility()==View.VISIBLE){
//                                            ViewAnimation.showOut(llAdmission);
//                                        }
//
//                                        ViewAnimation.showIn(llConfirm);
//                                    }
//                                });
//                            } else {
//                                runOnUiThread(() -> {
//                                    if (llCreatePassword.getVisibility()==View.VISIBLE){
//                                        ViewAnimation.showOut(llCreatePassword);
//                                    }
//                                    if (llAdmission.getVisibility()==View.VISIBLE){
//                                        ViewAnimation.showOut(llAdmission);
//                                    }
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
//                                            findViewById(R.id.ll_top).setVisibility(View.GONE);
//
//                                            ((TextView) findViewById(R.id.tv_student_name)).setText(otUserDetails.getSName());
//                                            ((TextView) findViewById(R.id.login_id)).setText(otUserDetails.getAdmissionNo());
//                                            ((TextView) findViewById(R.id.tv_class)).setText(otUserDetails.getMClassName() + " - " + otUserDetails.getMSectionName());
//
//
//                                            bottomView = "password";
//
//                                            ViewAnimation.showIn(llPassword);
//                                        }
//                                    }, 300);
//                                });
//                            }
                            runOnUiThread(() -> {
                                if (llCreatePassword.getVisibility() == View.VISIBLE) {
                                    ViewAnimation.showOut(llCreatePassword);
                                }
                                if (llAdmission.getVisibility() == View.VISIBLE) {
                                    ViewAnimation.showOut(llAdmission);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        findViewById(R.id.ll_top_reg).setVisibility(View.VISIBLE);
                                        findViewById(R.id.ll_top).setVisibility(View.GONE);

                                        ((TextView) findViewById(R.id.tv_student_name)).setText(otUserDetails.getSName());
                                        ((TextView) findViewById(R.id.login_id)).setText(otUserDetails.getAdmissionNo());
                                        ((TextView) findViewById(R.id.tv_class)).setText(otUserDetails.getMClassName() + " - " + otUserDetails.getMSectionName());


                                        bottomView = "password";

                                        ViewAnimation.showIn(llPassword);
                                    }
                                }, 300);
                            });
                        }
                    }
                });
            }
        });
    }

    //confirm user phone number
    private void confirmPhone(String number) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String studentId = "";

        if (otFail) {
            studentId = sObj.getStudentId();
        } else {
            studentId = otUserDetails.getMStudentId() + "";
        }

        Log.v(TAG, "Dob Url- " + new AppUrls().ValidateUserByDOB + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&phoneNumber=" + number);

        Request request = new Request.Builder()
                .url(new AppUrls().ValidateUserByDOB + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&phoneNumber=" + number)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                updateProgressAndDialog("Incorrect Phone Number!");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()) {
                    updateProgressAndDialog("Incorrect Phone Number!");
                    return;
                } else {

                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ViewAnimation.showOut(llPhone);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pinLastFourDigits.setText("");
                                            bottomView = "create_password";
                                            ViewAnimation.showIn(llCreatePassword);
                                        }
                                    }, 300);
                                }
                            });
                        } else {
                            updateProgressAndDialog("Incorrect Phone Number!");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateProgressAndDialog("");
            }
        });
    }


    //create new course Password
    private void createPassword(String s) {
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        JSONObject object = new JSONObject();

        try {
            if (otFail) {
                object.put("loginName", sObj.getLoginId());
                object.put("updatedBy", sObj.getStudentId());
            } else {
                object.put("loginName", otUserDetails.getAdmissionNo());
                object.put("updatedBy", otUserDetails.getMStudentId() + "");
            }

            object.put("newPassword", newPin.getText().toString());
            object.put("schemaName", sh_Pref.getString("schema", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(object));

        Request request = new Request.Builder()
                .url(new AppUrls().ResetStudentPassword)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                updateProgressAndDialog("Oops! There was a problem");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()) {
                    updateProgressAndDialog("Oops! There was a problem");
                    return;
                } else {
                    try {
                        JSONObject obj = new JSONObject(resp);

                        if (obj.getString("StatusCode").equalsIgnoreCase("200")) {

                            toEdit.putString("pin", newPin.getText().toString());
                            toEdit.commit();

                            //mongo password service called
                            createExamPassword();

                        } else {
                            updateProgressAndDialog("Oops! There was a problem");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        updateProgressAndDialog("Oops! There was a problem");
                        return;
                    }
                }
                updateProgressAndDialog("");
            }
        });
    }

    //create new exam password
    private void createExamPassword() {


        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        JSONObject object = new JSONObject();

        try {
            if (otFail) {
                object.put("loginName", sObj.getLoginId());
                object.put("updatedBy", sObj.getStudentId());
            } else {
                object.put("loginName", otUserDetails.getAdmissionNo());
                object.put("updatedBy", otUserDetails.getMStudentId() + "");

            }
            object.put("newPassword", newPin.getText().toString());
            object.put("schemaName", sh_Pref.getString("schema", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(object));

        Request request = new Request.Builder()
                .url(new AppUrls().SetStudentPassword)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                updateProgressAndDialog("Oops! There was a problem");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response exam pass - " + resp);
                if (!response.isSuccessful()) {
                    updateProgressAndDialog("Oops! There was a problem");
                    return;
                } else {
                    try {
                        JSONObject obj = new JSONObject(resp);

                        if (obj.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    ViewAnimation.showOut(llCreatePassword);
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            newPin.setText("");
//                                            newPinConfirm.setText("");
//                                            bottomView = "password";
//                                            ViewAnimation.showIn(llPassword);
//                                        }
//                                    }, 300);
                                    if (otFail) {
                                        getDetails(sObj.getLoginId());
                                    } else {
                                        getExamOtDetails(otUserDetails.getAdmissionNo());
                                    }
                                }
                            });
                        } else {
                            updateProgressAndDialog("Oops! There was a problem");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateProgressAndDialog("");
            }
        });
    }

    //get Student Status
    private void getStudentStatus(String studentId, String schema) {

        utils.showLoader(RegNew.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        utils.showLog(TAG, "Student Status URL- " + new AppUrls().GetStudentActiveStatus + "studentId=" + studentId + "&schemaName=" + schema);

        Request get = new Request.Builder()
                .url(new AppUrls().GetStudentActiveStatus + "studentId=" + studentId + "&schemaName=" + schema)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                updateProgressAndDialog("Oops there was a problem");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    updateProgressAndDialog("");
                    if (response.body() != null) {
                        try {
                            ResponseBody body = response.body();
                            String jsonResponse = response.body().string();
                            JSONObject ParentjObject = new JSONObject(jsonResponse);
                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                if (ParentjObject.getString("isActive").equalsIgnoreCase("1")) {
                                    if (!pinEntryEditText.getText().toString().equals("")) {
                                        toEdit.putString("pin", pinEntryEditText.getText().toString());
                                        toEdit.commit();
                                    }
                                    if (ParentjObject.getString("password").equalsIgnoreCase(sh_Pref.getString("stdPassword", ""))) {
                                        if (ParentjObject.has(AppConst.ACCESS_TOKEN)) //TODO new changes
                                            toEdit.putString(AppConst.ACCESS_TOKEN, ParentjObject.get(AppConst.ACCESS_TOKEN).toString());
                                        toEdit.commit();
                                        runOnUiThread(() -> {
                                            Intent loginIntent = (new Intent(RegNew.this, WelcomeActivity.class));
                                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(loginIntent);
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                            finish();
                                        });
                                        // checkPassWord(pinEntryEditText.getText().toString(), "0");
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                utils.dismissDialog();
                                                showErrorDialog("Incorrect Pin.Please check your credentials");
                                            }
                                        });

                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegNew.this);
                                            builder.setMessage("Your account has been Disabled, Please Contact College")
                                                    .setTitle(getResources().getString(R.string.app_name))
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    MyUtils.userLogOut(toEdit, RegNew.this, sh_Pref);
                                                                }
                                                            }

                                                    );
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }
                                    });
                                }
                            } else if (ParentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(ParentjObject)) { //TODO New Changes
                                String message = ParentjObject.getString(AppConst.MESSAGE);
                                runOnUiThread(() -> {
                                    MyUtils.forceLogoutUser(toEdit, RegNew.this, message, sh_Pref);
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Regular SQL Login
    private void checkPassWord(String password) {
        utils.showLoader(RegNew.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            if (otFail) {
                jsonObject.put("userId", sObj.getLoginId());
            } else {
                jsonObject.put("userId", otUserDetails.getAdmissionNo());
            }
            jsonObject.put("password", password);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("deviceToken", deviceToken);
            jsonObject.put(AppConst.FORCE_LOGIN, AppConst.isForceLogin); //TODO new changes
        } catch (JSONException e) {
            e.printStackTrace();
        }
        utils.showLog(TAG, "URL - " + new AppUrls().GetStudentDetailsiForMobile);
        utils.showLog(TAG, jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().GetStudentDetailsiForMobile)
                .post(body)
                .build();

        utils.showLog(TAG, request.body().toString());
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                updateProgressAndDialog("Incorrect Password!");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()) {
                    updateProgressAndDialog("Incorrect Password!");
                } else {
                    try {
                        JSONObject object = new JSONObject(resp);
                        if (object.getString("StatusCode").equalsIgnoreCase("200")) {
                            Gson gson = new Gson();
                            StudentObj json = gson.fromJson(object.getJSONObject("StudentObj").toString(), StudentObj.class);
                            toEdit.putString("studentObj", object.getJSONObject("StudentObj").toString());
                            toEdit.putBoolean("student_loggedin", true);
                            toEdit.putString("pin", password);
                            toEdit.putString("stdPassword", object.getJSONObject("StudentObj").getString("password"));
                            if (object.has(AppConst.ACCESS_TOKEN)) //TODO new changes
                                toEdit.putString(AppConst.ACCESS_TOKEN, object.get(AppConst.ACCESS_TOKEN).toString());
                            MyUtils.updateSharedPreferences(toEdit, AppConst.USER_ID_DATA, json.getStudentId());
                            toEdit.commit();
                            updateProgressAndDialog("");
                            runOnUiThread(() -> {
                                Intent loginIntent = (new Intent(RegNew.this, WelcomeActivity.class));
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();
                            });
                        } else {
                            //TODO new changes
                            if (object.getString(AppConst.STATUS_CODE).equalsIgnoreCase("409")) {
                                String message = object.get("MESSAGE").toString();
                                updateProgressAndDialog("");
                                runOnUiThread(() -> {
                                    showAlreadyLoginPopUp(password, message);
                                });
                            } else {
                                updateProgressAndDialog("Incorrect Password!");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //Ot Login
    private void loginOnlineTest(String pin) {
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            if (otFail) {
                jsonObject.put("admissionNo", sObj.getLoginId() + "");
            } else {
                jsonObject.put("admissionNo", otUserDetails.getAdmissionNo() + "");
            }
            jsonObject.put("password", pin);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "URL Log " + AppUrls.LOGIN_ONLINE_Test_STUDENT);
        Log.v(TAG, "URL Log Obj - " + jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(AppUrls.LOGIN_ONLINE_Test_STUDENT)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        showErrorDialog("Please Check your details and try again!");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            showErrorDialog("Please Check your details and try again!");
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {

                            otLoginResultObj = new GsonBuilder().create().fromJson(ParentjObject.getJSONObject("result").getJSONObject("studentDetails").toString(), OTLoginResult.class);
                            Config config = new GsonBuilder().create().fromJson(ParentjObject.getJSONObject("result").getJSONObject("config").toString(), Config.class);
                            Gson gson = new Gson();
                            String json = gson.toJson(otLoginResultObj);
                            String configJson = gson.toJson(config);
                            toEdit.putString("otStudentObj", json);
                            toEdit.putString("config", configJson);
                            toEdit.putBoolean("student_ot_loggedin", true);
                            toEdit.putString("pin", pin);
                            toEdit.commit();
                            Intent intent = new Intent(RegNew.this, OTStudentTestActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finishAffinity();

                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    showErrorDialog("Please Check your details and try again!");
                                }
                            });

                        }

                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                                showErrorDialog("Please Check your details and try again!");
                            }
                        });
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    @Override
    public void onBackPressed() {
        switch (bottomView) {
            case "admission":
                super.onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
            case "password":
                if (sh_Pref.getBoolean("student_loggedin", false)
                        || sh_Pref.getBoolean("parent_loggedin", false)
                        || sh_Pref.getBoolean("student_ot_loggedin", false)) {
                    super.onBackPressed();
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                } else {
                    pinEntryEditText.setText("");
                    findViewById(R.id.ll_top).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_top_reg).setVisibility(View.GONE);
                    bottomView = "admission";
                    ViewAnimation.showOut(llPassword);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ViewAnimation.showIn(llAdmission);
                        }
                    }, 300);
                }
                break;
            case "confirm":
                toEdit.remove("pin");
                toEdit.remove("optedCourses");
                toEdit.remove("studentObj");
                toEdit.remove("student_loggedin");
                toEdit.remove("intro");
                toEdit.remove("otStudentObj");
                toEdit.remove("config");
                toEdit.remove("student_ot_loggedin");
                toEdit.commit();
                bottomView = "admission";
                findViewById(R.id.ll_top).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_top_reg).setVisibility(View.GONE);
                ViewAnimation.showOut(llConfirm);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llAdmission);
                    }
                }, 300);

                break;
            case "confirm_options":
                bottomView = "confirm";
                ViewAnimation.showOut(llConfirmOptions);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llConfirm);
                    }
                }, 300);
                break;
            case "dob":
                etDay.setText("");
                etMonth.setText("");
                etYear.setText("");
                bottomView = "confirm_options";
                ViewAnimation.showOut(llDob);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llConfirmOptions);
                    }
                }, 300);
                break;
            case "number":
                bottomView = "confirm";
                //TODO change code for phone number
                ViewAnimation.showOut(llPhone);
                //ViewAnimation.showOut(llDob);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llConfirm);
                    }
                }, 300);
                break;
            case "otp":
                bottomView = "dob";
                ViewAnimation.showOut(llOtp);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llDob);
                    }
                }, 300);
                break;
            case "create_password":
                newPin.setText("");
                newPinConfirm.setText("");
                bottomView = "confirm";
                ViewAnimation.showOut(llCreatePassword);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llConfirm);
                    }
                }, 300);
                break;
            case "security_question":
                bottomView = "create_password";
                ViewAnimation.showOut(llSecurityQues);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llCreatePassword);
                    }
                }, 300);
                break;
//        }

        }
    }

    boolean isValid(String s) {
        if (s.length() > 0) {
            return true;
        }
        return false;
    }


    //confirm user Data of Birth
    private void confirmDataOfBirth(String dataOfBirth) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)

                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String studentId = "";

        if (otFail) {
            studentId = sObj.getStudentId();
        } else {
            studentId = otUserDetails.getMStudentId() + "";
        }

        Log.v(TAG, "Dob Url- " + new AppUrls().ValidateUserByDOB + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&DOB=" + dataOfBirth);

        Request request = new Request.Builder()
                .url(new AppUrls().ValidateUserByDOB + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&DOB=" + dataOfBirth)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        showErrorDialog("Incorrect Date of Birth!");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            showErrorDialog("Incorrect Date of Birth!");
                        }
                    });
                } else {

                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ViewAnimation.showOut(llDob);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pinLastFourDigits.setText("");
                                            bottomView = "create_password";
                                            ViewAnimation.showIn(llCreatePassword);
                                        }
                                    }, 300);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    showErrorDialog("Incorrect Date of Birth!");
                                }
                            });
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

    //Dismiss progress dialog with error message
    private void updateProgressAndDialog(String message) {
        runOnUiThread(() -> {
            utils.dismissDialog();
            if (!message.isEmpty())
                showErrorDialog(message);
        });
    }

    private void showAlreadyLoginPopUp(String password, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegNew.this);
        builder.setMessage(message)
                .setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("OK",
                        (dialog, id) -> {
                            dialog.dismiss();
                            checkPassWord(password);
                        })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}