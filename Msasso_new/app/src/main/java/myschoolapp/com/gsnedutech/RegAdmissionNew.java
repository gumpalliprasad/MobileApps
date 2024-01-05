package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.ParentObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FingerprintHandler;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static javax.crypto.Cipher.*;

public class RegAdmissionNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + RegAdmissionNew.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    String deviceToken, IMEI;

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
    @BindView(R.id.et_user_password)
    EditText etPassword;
    @BindView(R.id.password_visible)
    ImageView ivPassVisible;
    @BindView(R.id.iv_selection)
    ImageView ivSelection;
    @BindView(R.id.tv_user)
    TextView tv_user;
    @BindView(R.id.logo)
    ImageView appLogo;

    String bottomView = "admission";

    boolean visible = false;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;

    String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_admission_new);

        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        if(sh_Pref.contains(AppConst.COLLEGE_LOGO) && !sh_Pref.getString(AppConst.COLLEGE_LOGO, "").isEmpty())
            Picasso.with(RegAdmissionNew.this).load(sh_Pref.getString(AppConst.COLLEGE_LOGO, "")).placeholder(R.color.semi_transparent)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(appLogo);

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                deviceToken = instanceIdResult.getToken();
//                Log.v("newToken", deviceToken);
//            }
//        });

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
                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(RegAdmissionNew.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


//        IMEI = "man"+Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        IMEI = "NA";
        utils.showLog("ID", "Android ID: " + IMEI);

        user = getIntent().getStringExtra("user");
        switch (user) {
            case "student":
                ivSelection.setImageResource(R.drawable.ic_student_select);
                tv_user.setText("User Id");
                etUserName.setHint("Enter Your UserId");
                break;
            case "teacher":
                ivSelection.setImageResource(R.drawable.ic_teacher_select);
                tv_user.setText("Email");
                etUserName.setHint("Enter Your Email");
                break;
            case "admin":
                ivSelection.setImageResource(R.drawable.ic_admin_select);
                tv_user.setText("Email");
                etUserName.setHint("Enter Your Email");
                break;
            case "parent":
                ivSelection.setImageResource(R.drawable.ic_parent_select);
                tv_user.setText(getString(R.string.admission_number)); //R.string.phone_number
                //etUserName.setInputType(InputType.TYPE_CLASS_PHONE);
                etUserName.setHint(getString(R.string.admission_number_hint)); //R.string.phone_number_hint
                etUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                break;
        }

        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ivPassVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visible) {
                    ivPassVisible.setImageResource(R.drawable.ic_no_visible);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().toString().length());
                    visible = false;
                } else {
                    ivPassVisible.setImageResource(R.drawable.ic_visible);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    etPassword.setSelection(etPassword.getText().toString().length());
                    visible = true;
                }
            }
        });

        findViewById(R.id.btn_admission_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView = "password";
                ViewAnimation.showOut(llAdmission);
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                if (!fingerprintManager.isHardwareDetected()) {
                    /**
                     * An error message will be displayed if the device does not contain the fingerprint hardware.
                     * However if you plan to implement a default authentication method,
                     * you can redirect the user to a default authentication activity from here.
                     * Example:
                     * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                     * startActivity(intent);
                     */
                    Toast.makeText(RegAdmissionNew.this, "Your Device does not have a Fingerprint Sensor", Toast.LENGTH_SHORT).show();
                } else {
                    // Checks whether fingerprint permission is set on manifest
                    if (ActivityCompat.checkSelfPermission(RegAdmissionNew.this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(RegAdmissionNew.this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();

                    } else {
                        // Check whether at least one fingerprint is registered
                        if (!fingerprintManager.hasEnrolledFingerprints()) {

                            Toast.makeText(RegAdmissionNew.this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();

                        } else {
                            // Checks whether lock screen security is enabled or not
                            if (!keyguardManager.isKeyguardSecure()) {

                                Toast.makeText(RegAdmissionNew.this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();

                            } else {
                                generateKey();


                                if (cipherInit()) {
                                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                    FingerprintHandler helper = new FingerprintHandler(RegAdmissionNew.this);
                                    helper.startAuth(fingerprintManager, cryptoObject);
                                }
                            }
                        }
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llPassword);
                    }
                }, 300);
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView = "dob";
                ViewAnimation.showOut(llConfirm);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llDob);
                    }
                }, 300);
            }
        });
        findViewById(R.id.btn_dob_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView = "otp";
                ViewAnimation.showOut(llDob);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llOtp);
                    }
                }, 300);
            }
        });

        findViewById(R.id.btn_otp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView = "create_password";
                ViewAnimation.showOut(llOtp);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llCreatePassword);
                    }
                }, 300);
            }
        });
        findViewById(R.id.btn_cp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView = "security_question";
                ViewAnimation.showOut(llCreatePassword);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llSecurityQues);
                    }
                }, 300);
            }
        });

        findViewById(R.id.btn_sq_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegAdmissionNew.this, "Finish!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_login_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUserName.getText().toString().length() > 0 && etPassword.getText().toString().length() > 0) {
                    loginUser(etUserName.getText().toString().trim(), etPassword.getText().toString());
                }
            }
        });

        pinEntryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 6) {
                    hideKeyboard(RegAdmissionNew.this);
                    bottomView = "confirm";
                    ViewAnimation.showOut(llPassword);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ViewAnimation.showIn(llConfirm);
                        }
                    }, 300);
                    llTop.setVisibility(View.GONE);
                    llTopReg.setVisibility(View.VISIBLE);
                }
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

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void onFingerPrintSuccess() {
        if (llPassword.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();

            bottomView = "confirm";
            ViewAnimation.showOut(llPassword);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewAnimation.showIn(llConfirm);
                }
            }, 300);
            llTop.setVisibility(View.GONE);
            llTopReg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        switch (bottomView) {
            case "admission":
                super.onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
            case "password":
                bottomView = "admission";
                ViewAnimation.showOut(llPassword);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llAdmission);
                    }
                }, 300);
                break;
            case "confirm":
                llTopReg.setVisibility(View.GONE);
                llTop.setVisibility(View.VISIBLE);
                bottomView = "password";
                ViewAnimation.showOut(llConfirm);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llPassword);
                        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                        if (!fingerprintManager.isHardwareDetected()) {
                            /**
                             * An error message will be displayed if the device does not contain the fingerprint hardware.
                             * However if you plan to implement a default authentication method,
                             * you can redirect the user to a default authentication activity from here.
                             * Example:
                             * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                             * startActivity(intent);
                             */
                            Toast.makeText(RegAdmissionNew.this, "Your Device does not have a Fingerprint Sensor", Toast.LENGTH_SHORT).show();
                        } else {
                            // Checks whether fingerprint permission is set on manifest
                            if (ActivityCompat.checkSelfPermission(RegAdmissionNew.this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(RegAdmissionNew.this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();

                            } else {
                                // Check whether at least one fingerprint is registered
                                if (!fingerprintManager.hasEnrolledFingerprints()) {

                                    Toast.makeText(RegAdmissionNew.this, "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Checks whether lock screen security is enabled or not
                                    if (!keyguardManager.isKeyguardSecure()) {

                                        Toast.makeText(RegAdmissionNew.this, "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();

                                    } else {
                                        generateKey();


                                        if (cipherInit()) {
                                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                            FingerprintHandler helper = new FingerprintHandler(RegAdmissionNew.this);
                                            helper.startAuth(fingerprintManager, cryptoObject);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 300);
                break;
            case "dob":
                bottomView = "confirm";
                ViewAnimation.showOut(llDob);
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
                bottomView = "otp";
                ViewAnimation.showOut(llCreatePassword);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llOtp);
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
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, RegAdmissionNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();

            }
            isNetworkAvail = true;
        }
    }

    void loginUser(String... params) {
        utils.showLoader(RegAdmissionNew.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String URL = "";

        JSONObject jsonObject = new JSONObject();
        try {
            if (user.equalsIgnoreCase("student")) {
                URL = AppUrls.LOGIN_STUDENT;
                jsonObject.put("userId", params[0]);
            } else {
                URL = AppUrls.LOGIN_USER;
                jsonObject.put("loginId", params[0]);
            }
            jsonObject.put("password", params[1]);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("deviceToken", deviceToken);
            jsonObject.put(AppConst.FORCE_LOGIN, "1");
//                jsonObject.put("imeinumber", IMEI);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        utils.showLog(TAG, "URL - " + URL);
        utils.showLog(TAG, jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        utils.showLog(TAG, request.body().toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null) {
                    String jsonResp = response.body().string();

                    utils.showLog(TAG, "responseBody - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            //User Role ID
                            if (ParentjObject.has(AppConst.PARENT_ROLE_ID)) {
                                MyUtils.updateSharedPreferences(toEdit, AppConst.USER_ROLE_ID, ParentjObject.getString(AppConst.PARENT_ROLE_ID));
                            }
                            if (ParentjObject.has(AppConst.ACCESS_TOKEN)) //TODO new changes
                                MyUtils.updateSharedPreferences(toEdit, AppConst.ACCESS_TOKEN, ParentjObject.getString(AppConst.ACCESS_TOKEN));

                            if (ParentjObject.has(AppConst.USER_ID_OTHER))
                                MyUtils.updateSharedPreferences(toEdit, AppConst.USER_ID_DATA, ParentjObject.getString(AppConst.USER_ID_OTHER)); //TODO Change

                            if (user.equalsIgnoreCase("student")) {
                                StudentObj studentObj = new GsonBuilder().create().fromJson(ParentjObject.getJSONObject("StudentObj").toString(), StudentObj.class);

                                utils.showLog(TAG, "Student name- " + studentObj.getStudentName());
                                utils.showLog(TAG, "Student name- " + studentObj.getCourseName());

                                Gson gson = new Gson();
                                String json = gson.toJson(studentObj);
                                toEdit.putString("studentObj", json);
                                toEdit.putBoolean("student_loggedin", true);
                                toEdit.putString("studentuser", etUserName.getText().toString().trim());
                                toEdit.putString("studentpassword", etPassword.getText().toString().trim());
                                toEdit.commit();

                                if (studentObj.getIsFirstLogin() == 1) {
                                    updateFirstLogin(studentObj.getStudentId());
                                }

                                Intent i = new Intent(RegAdmissionNew.this, WelcomeActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();
                            } else {
                                if (ParentjObject.getString("roleId").equalsIgnoreCase("1")
                                        || ParentjObject.getString("roleId").equalsIgnoreCase("2")) {
                                    if (user.equalsIgnoreCase("admin")) {
                                        //Admin code
                                        AdminObj obj = new AdminObj();
                                        Gson gson = new Gson();
                                        obj = gson.fromJson(ParentjObject.toString(), AdminObj.class);

                                        String json = gson.toJson(obj);
                                        toEdit.putString("adminObj", json);
                                        toEdit.putBoolean("admin_loggedin", true);
                                        toEdit.commit();

                                        Intent teacherIntent = new Intent(RegAdmissionNew.this, AdminHome.class);
                                        teacherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(teacherIntent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        finish();
                                    } else {
                                        runOnUiThread(() -> showAlertDialogue());
                                    }
                                } else if (ParentjObject.getString("roleId").equalsIgnoreCase("4")) {
                                    //parent code
                                    ParentObj obj = new ParentObj();
                                    Gson gson = new Gson();
                                    obj = gson.fromJson(ParentjObject.toString(), ParentObj.class);

                                    Log.v(TAG, "Parent StudentListSize - " + obj.getStudentDetails().size());

                                    String json = gson.toJson(obj);
                                    toEdit.putString("parentObj", json);
                                    toEdit.putBoolean("parent_loggedin", true);
                                    toEdit.putInt("parent_StudentsCount", obj.getStudentDetails().size());
                                    toEdit.commit();

                                    if (obj.getStudentDetails().size() > 1) {
                                        Intent parentIntent = new Intent(RegAdmissionNew.this, ParentStudentList.class);
                                        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(parentIntent);
                                        finish();
                                    } else {
                                        //Placing the Parent Student Obj if 1 Student
                                        String sjson = gson.toJson(obj.getStudentDetails().get(0));
                                        toEdit.putString("studentObj", sjson);
                                        // if(ParentjObject.has(AppConst.ACCESS_TOKEN)) //TODO new changes
                                        // toEdit.putString(AppConst.ACCESS_TOKEN, ParentjObject.get(AppConst.ACCESS_TOKEN).toString());
                                        toEdit.commit();
                                        Intent parentIntent = new Intent(RegAdmissionNew.this, ParentHome.class);
                                        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(parentIntent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        finish();
                                    }

                                } else if (ParentjObject.getString("roleId").equalsIgnoreCase("3")) {
                                    TeacherObj obj = new TeacherObj();
                                    Gson gson = new Gson();
                                    obj = gson.fromJson(ParentjObject.toString(), TeacherObj.class);

                                    String json = gson.toJson(obj);
                                    toEdit.putString("teacherObj", json);
                                    toEdit.putBoolean("teacher_loggedin", true);

                                    toEdit.commit();

                                    Intent teacherIntent = new Intent(RegAdmissionNew.this, TeacherHomeNew.class);
                                    teacherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(teacherIntent);
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    finish();
                                } else {
                                    runOnUiThread(() -> showAlertDialogue());

                                }
                            }
                        } else {
                            runOnUiThread(() -> showAlertDialogue());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> showAlertDialogue());
                    }
                }
            }
        });
    }

    private void showAlertDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegAdmissionNew.this);
        builder.setMessage("Incorrect UserId or Password.Please check your credentials")
                .setTitle(getResources().getString(R.string.app_name))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }

                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateFirstLogin(String studentId) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("schemaName", sh_Pref.getString("schema", ""));
            json.put("studentId", studentId);
            json.put("isFirstLogin", 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, json.toString());

        utils.showLog(TAG, "url " + new AppUrls().UPDATE_STUDENT_LOGINSTATUS);
        utils.showLog(TAG, "url " + json.toString());

        Request request = new Request.Builder()
                .url(new AppUrls().UPDATE_STUDENT_LOGINSTATUS)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody resp = response.body();
                String responce = resp.string();

                utils.showLog(TAG, "response - " + responce);
            }
        });

    }
}
