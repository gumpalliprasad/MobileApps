package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentProfileObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EditProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = EditProfileActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.img_profile)
    CircleImageView profImgPicBackground;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_dob)
    TextView tvDob;
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.old_password_visible)
    ImageView ivOldPassVisible;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.new_password_visible)
    ImageView ivNewPassVisible;
    SharedPreferences sh_Pref;

    File postFile;
    boolean visible = false;
    boolean newVisible = false;

    StudentProfileObj studentProfileObj = null;

    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE = 2;

    AmazonS3Client s3Client1;
    String keyName;
    SharedPreferences.Editor toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivOldPassVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visible) {
                    ivOldPassVisible.setImageResource(R.drawable.ic_no_visible);
                    etOldPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    etOldPassword.setSelection(etOldPassword.getText().toString().length());
                    visible = false;
                } else {
                    ivOldPassVisible.setImageResource(R.drawable.ic_visible);
                    etOldPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etOldPassword.setSelection(etOldPassword.getText().toString().length());
                    visible = true;
                }
            }
        });

        ivNewPassVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newVisible) {
                    ivNewPassVisible.setImageResource(R.drawable.ic_no_visible);
                    etNewPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    etNewPassword.setSelection(etNewPassword.getText().toString().length());
                    newVisible = false;
                } else {
                    ivNewPassVisible.setImageResource(R.drawable.ic_visible);
                    etNewPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etNewPassword.setSelection(etNewPassword.getText().toString().length());
                    newVisible = true;
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etOldPassword.getText().toString().length() > 0 && etNewPassword.getText().toString().length() > 0) {
                    editPassword(etOldPassword.getText().toString(), etNewPassword.getText().toString());
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter the details!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.fl_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(EditProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_imgselector);
                dialog.setCancelable(true);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int wwidth = metrics.widthPixels;
                dialog.getWindow().setLayout((int) (wwidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView tvCam = dialog.findViewById(R.id.tv_cam);
                tvCam.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                String imageFileName = "JPEG_" + timeStamp + "_";
                                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                                // Save a file: path for use with ACTION_VIEW intents
                                picUri =  Uri.fromFile(image);
                                if (image != null) {
                                    Uri photoURI = FileProvider.getUriForFile(EditProfileActivity.this,
                                            getPackageName()+".provider",
                                            image);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                                }
                            }
                            else {
                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                File imageFile = new File(imageFilePath);
                                picUri = Uri.fromFile(imageFile); // convert path to Uri
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                            }
                        } catch (ActivityNotFoundException | IOException anfe) {
                            //display an error message
                            String errorMessage = "Whoops - your device doesn't support capturing images!";
                            Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        //intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        dialog.dismiss();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

       etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 3) {
                    if(etNewPassword.toString().length() > 3) {
                        findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.btn_next).setVisibility(View.GONE);
                    }
                } else {
                    findViewById(R.id.btn_next).setVisibility(View.GONE);
                }
            }
        });

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 3) {
                        if(etOldPassword.toString().length() > 3) {
                            findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.btn_next).setVisibility(View.GONE);
                        }
                } else {
                    findViewById(R.id.btn_next).setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, EditProfileActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
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
        studentProfileObj = (StudentProfileObj) getIntent().getSerializableExtra("student");

        tvName.setText(studentProfileObj.getStudentName());
        try {
            tvDob.setText(new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(studentProfileObj.getDOB())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v(TAG,"profileUrl - "+studentProfileObj.getProfilePic());
//        Picasso.with(EditProfileActivity.this).load(studentProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
//                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);

        keyName = "profile/" + studentProfileObj.getSectionName().replace(" " , "")+ "/" + studentProfileObj.getAdmissionNumber();
//        keyName = "androidTesting";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                //get the Uri for the captured image
                Uri uri = picUri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    profImgPicBackground.setImageBitmap(bitmap);

                    postFile = FileUtil.from(EditProfileActivity.this, uri);
                    uploadtos3();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //carry out the crop operation
//                performCrop();
                Log.v("picUri", uri.toString());

            } else if (requestCode == PICK_IMAGE) {
                picUri = data.getData();
                Log.v("uriGallery", picUri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    profImgPicBackground.setImageBitmap(bitmap);
                    postFile = FileUtil.from(EditProfileActivity.this, picUri);
                    uploadtos3();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


    }


    void uploadtos3() {

        utils.showLoader(EditProfileActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date expiration = new Date();
                    long msec = expiration.getTime();
                    msec += 1000 * 60 * 60; // 1 hour.
                    expiration.setTime(msec);

                    s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                            Region.getRegion(Regions.AP_SOUTH_1));
                    PutObjectRequest por = new PutObjectRequest(sh_Pref.getString("bucket", ""),
                            keyName + ".png", postFile);//key is  URL

                    //making the object Public
                    por.setCannedAcl(CannedAccessControlList.PublicRead);
                    s3Client1.putObject(por);
                    utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName + ".png"));
                    utils.dismissDialog();
                    postImage(s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName + ".png"));
                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG, "error " + e.getMessage());
                    utils.dismissDialog();
                }
            }
        }).start();

    }

    void postImage(String picUrl) {
        utils.showLoader(EditProfileActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentProfileObj.getStudentId());
//            jsonObject.put("admissionNo", studentProfileObj.getAdmissionNumber());
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("targetPath", picUrl);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(AppUrls.UploadStudentProfilePic)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(EditProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        Picasso.with(EditProfileActivity.this).load(studentProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            Toast.makeText(EditProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            Picasso.with(EditProfileActivity.this).load(studentProfileObj.getProfilePic()).placeholder(R.drawable.user_default)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(profImgPicBackground);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            Toast.makeText(EditProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    void editPassword(String oldPass, String newPass) {

        utils.showLoader(EditProfileActivity.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String URL = AppUrls.CHANGE_STUDENT_PASSWORD;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginName", studentProfileObj.getLoginName());
            jsonObject.put("oldPassword", oldPass);
            jsonObject.put("newPassword", newPass);
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
            jsonObject.put("updatedBy", studentProfileObj.getStudentId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "URL - " + URL);
        Log.v(TAG, jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(EditProfileActivity.this, "Your Old Password did not match.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (response.body() != null) {
                        String resp = responseBody.string();
                        utils.showLog(TAG, "response- " + resp);
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    etOldPassword.setText("");
                                    etNewPassword.setText("");
                                    Toast.makeText(EditProfileActivity.this, "Password Changed Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, EditProfileActivity.this, message, sh_Pref);
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    Toast.makeText(EditProfileActivity.this, "Your Old Password did not match.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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