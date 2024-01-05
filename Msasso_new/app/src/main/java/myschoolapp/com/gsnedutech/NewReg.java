package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FingerprintHandler;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static javax.crypto.Cipher.*;

public class NewReg extends AppCompatActivity {

    private static final String TAG = "SriRam -" + NewReg.class.getName();
    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    StudentObj sObj;
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

    String bottomView = "admission";

    boolean visible = false;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reg);

        ButterKnife.bind(this);

        init();
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

//        IMEI = "man"+Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        IMEI = "NA";
        utils.showLog("ID", "Android ID: " + IMEI);


        etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ivPassVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visible){
                    ivPassVisible.setImageResource(R.drawable.ic_no_visible);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().toString().length());
                    visible = false;
                }else{
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
                if(!fingerprintManager.isHardwareDetected()){
                    /**
                     * An error message will be displayed if the device does not contain the fingerprint hardware.
                     * However if you plan to implement a default authentication method,
                     * you can redirect the user to a default authentication activity from here.
                     * Example:
                     * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                     * startActivity(intent);
                     */
                    Toast.makeText(NewReg.this,"Your Device does not have a Fingerprint Sensor",Toast.LENGTH_SHORT).show();
                }
                else {
                    // Checks whether fingerprint permission is set on manifest
                    if (ActivityCompat.checkSelfPermission(NewReg.this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(NewReg.this,"Fingerprint authentication permission not enabled",Toast.LENGTH_SHORT).show();

                    }else{
                        // Check whether at least one fingerprint is registered
                        if (!fingerprintManager.hasEnrolledFingerprints()) {

                            Toast.makeText(NewReg.this,"Register at least one fingerprint in Settings",Toast.LENGTH_SHORT).show();

                        }else{
                            // Checks whether lock screen security is enabled or not
                            if (!keyguardManager.isKeyguardSecure()) {

                                Toast.makeText(NewReg.this,"Lock screen security not enabled in Settings",Toast.LENGTH_SHORT).show();

                            }else{
                                generateKey();


                                if (cipherInit()) {
                                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                    FingerprintHandler helper = new FingerprintHandler(NewReg.this);
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
                },300);
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
                },300);
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
                },300);
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
                },300);
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
                },300);
            }
        });

        findViewById(R.id.btn_sq_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NewReg.this,"Finish!",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_login_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUserName.getText().toString().length()>0 && etPassword.getText().toString().length()>0){
                    new LoginUser().execute(etUserName.getText().toString().trim(),etPassword.getText().toString());
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
                if (editable.toString().length()==6){
                    hideKeyboard(NewReg.this);
                    bottomView = "confirm";
                    ViewAnimation.showOut(llPassword);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ViewAnimation.showIn(llConfirm);
                        }
                    },300);
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

    public void onFingerPrintSuccess(){
        if (llPassword.getVisibility()==View.VISIBLE){
            Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show();

            bottomView = "confirm";
            ViewAnimation.showOut(llPassword);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewAnimation.showIn(llConfirm);
                }
            },300);
            llTop.setVisibility(View.GONE);
            llTopReg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        switch (bottomView){
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
                },300);
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
                        if(!fingerprintManager.isHardwareDetected()){
                            /**
                             * An error message will be displayed if the device does not contain the fingerprint hardware.
                             * However if you plan to implement a default authentication method,
                             * you can redirect the user to a default authentication activity from here.
                             * Example:
                             * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                             * startActivity(intent);
                             */
                            Toast.makeText(NewReg.this,"Your Device does not have a Fingerprint Sensor",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Checks whether fingerprint permission is set on manifest
                            if (ActivityCompat.checkSelfPermission(NewReg.this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(NewReg.this,"Fingerprint authentication permission not enabled",Toast.LENGTH_SHORT).show();

                            }else{
                                // Check whether at least one fingerprint is registered
                                if (!fingerprintManager.hasEnrolledFingerprints()) {

                                    Toast.makeText(NewReg.this,"Register at least one fingerprint in Settings",Toast.LENGTH_SHORT).show();

                                }else{
                                    // Checks whether lock screen security is enabled or not
                                    if (!keyguardManager.isKeyguardSecure()) {

                                        Toast.makeText(NewReg.this,"Lock screen security not enabled in Settings",Toast.LENGTH_SHORT).show();

                                    }else{
                                        generateKey();


                                        if (cipherInit()) {
                                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                            FingerprintHandler helper = new FingerprintHandler(NewReg.this);
                                            helper.startAuth(fingerprintManager, cryptoObject);
                                        }
                                    }
                                }
                            }
                        }
                    }
                },300);
                break;
            case "dob":
                bottomView = "confirm";
                ViewAnimation.showOut(llDob);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llConfirm);
                    }
                },300);
                break;
            case "otp":
                bottomView = "dob";
                ViewAnimation.showOut(llOtp);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llDob);
                    }
                },300);
                break;
            case "create_password":
                bottomView = "otp";
                ViewAnimation.showOut(llCreatePassword);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llOtp);
                    }
                },300);
                break;
            case "security_question":
                bottomView = "create_password";
                ViewAnimation.showOut(llSecurityQues);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ViewAnimation.showIn(llCreatePassword);
                    }
                },300);
                break;
        }
    }

    private class LoginUser extends AsyncTask<String, Void, String> {
        MyUtils utils = new MyUtils();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utils.showLoader(NewReg.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            String URL = "";

            JSONObject jsonObject = new JSONObject();
            try {
                URL = AppUrls.LOGIN_STUDENT;
                jsonObject.put("userId", params[0]);
                jsonObject.put("password", params[1]);
                jsonObject.put("schemaName", sh_Pref.getString("schema",""));
                jsonObject.put("deviceToken", deviceToken);
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

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                utils.showLog(TAG, "responseBody - " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            utils.dismissDialog();

            utils.showLog(TAG, "responseBody - result - " + result);

            if (result != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(result);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

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

                        Intent i = new Intent(NewReg.this, WelcomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewReg.this);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}