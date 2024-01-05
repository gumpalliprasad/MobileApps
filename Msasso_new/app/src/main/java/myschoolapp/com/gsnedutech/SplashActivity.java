package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myschoolapp.com.gsnedutech.Fragments.splash.SplashFragment;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.OnlIneTest.OTStudentTestActivity;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SriRam -" + SplashActivity.class.getName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    MyUtils myUtils = new MyUtils();

    private AppUpdateManager mAppUpdateManager;
    private static int APP_UPDATE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Making this activity, full screen */

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
        setContentView(R.layout.activity_splash);

//        updateApp();
        init();
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

//        toEdit.putString(AppConst.SCHEMA,
//                "GSN1473");
//        toEdit.putString(
//                AppConst.COLLEGE_LOGO,
//                "https://msasso.s3.ap-south-1.amazonaws.com/GSN1473/LOGOS/50/GSN1473gsn_logo.png");
//
//        toEdit.putString(
//                AppConst.BACKGROUND_COLLEGE_LOGO,
//                "https://msasso.s3.ap-south-1.amazonaws.com/GSN1473/LOGOS/50/GSN1473gsn_logo.png");
//

//        toEdit.commit();


        //getCollegeCode();

        int SPLASH_TIME_OUT = 2000;

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logosplash / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (checkAndRequestPermissions()) {
                    initialNavigate();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void updateApp() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, SplashActivity.this, APP_UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else {
                init();
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }
                    } else {
                        Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.cc_splash),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
                init();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    private void initialNavigate() {
        if (sh_Pref.getBoolean("student_loggedin", false)) {
            StudentObj sObj;
            sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
            toEdit = sh_Pref.edit();

            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);

            Intent i = new Intent(SplashActivity.this, RegNew.class);
            i.putExtra("isLoggedin", true);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        } else if (sh_Pref.getBoolean("student_ot_loggedin", false)) {
            Intent intent = new Intent(SplashActivity.this, OTStudentTestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            startActivity(intent);
            finish();
        } else if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Intent i = new Intent(SplashActivity.this, TeacherHomeNew.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            Intent i = new Intent(SplashActivity.this, AdminHome.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        } else if (sh_Pref.getBoolean("parent_loggedin", false)) {
            if (sh_Pref.getInt("parent_StudentsCount", 1) > 1)
                startActivity(new Intent(SplashActivity.this, ParentStudentList.class));
            else
                startActivity(new Intent(SplashActivity.this, ParentHome.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();

        } else {
            if (sh_Pref.contains(AppConst.SCHEMA) && !sh_Pref.getString(AppConst.SCHEMA, "").isEmpty()) {
                Intent i = new Intent(SplashActivity.this, UserSelection.class);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } else
                showFragmentNoBackStack(new SplashFragment(), R.id.cc_splash);


        }
    }


    private boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int recordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int phoneCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (phoneCall != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        myUtils.showLog(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        myUtils.showLog(TAG, "phonestate services permission granted");
                        initialNavigate();
                        //finish();
                        //else any one or both the permissions are not granted
                    } else {
                        myUtils.showLog(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
//                        show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                            showDialogOK("You need to give some mandatory permissions to continue.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:rankr.io.nrankguru")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.cc_splash);
        if (f instanceof SplashFragment)
            // do something with f
            finish();
    }
}