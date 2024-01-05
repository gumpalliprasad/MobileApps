package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AssignmentUpload extends AppCompatActivity {

    private static final String TAG = "SriRam -" + AssignmentUpload.class.getName();

    MyUtils utils = new MyUtils();


    @BindView(R.id.rv_assign_files)
    RecyclerView rvAssignFiles;
    @BindView(R.id.ll_submit_button)
    LinearLayout llSubmit;

    List<Uri> listFiles = new ArrayList<>();
    SharedPreferences sh_Pref;
    StudentObj sObj;


    HomeWorkDetails hwObj;
    String hwId;

    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE_MULTIPLE = 2;

    AmazonS3Client s3Client1;
    List <String> keyName = new ArrayList<>();

    int version = 1;

    MyUtils util = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_upload);
        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        hwObj = (HomeWorkDetails) getIntent().getSerializableExtra("hwObj");
        hwId = ""+hwObj.getHomeworkId();

        if (hwObj.getHwStatus().equalsIgnoreCase("Reassign")){
            version = Integer.parseInt(hwObj.getVersion());
        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.ll_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               uploadToS3();

            }
        });


        rvAssignFiles.setLayoutManager(new GridLayoutManager(AssignmentUpload.this,2));
        rvAssignFiles.setAdapter(new UploadFilesAdapter(listFiles));
    }

    class UploadFilesAdapter extends RecyclerView.Adapter<UploadFilesAdapter.ViewHolder>{

        List<Uri> listFiles;
        UploadFilesAdapter(List<Uri> listFiles){
            this.listFiles = listFiles;
        }

        @Override
        public int getItemViewType(int position) {
            if (position==listFiles.size()){
                return 0;
            }else {
                return 1;
            }
        }

        @NonNull
        @Override
        public UploadFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType==0){
                return new ViewHolder(LayoutInflater.from(AssignmentUpload.this).inflate(R.layout.item_upload_assignment,parent,false));
            }else {
                return new ViewHolder(LayoutInflater.from(AssignmentUpload.this).inflate(R.layout.item_assign_file,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull UploadFilesAdapter.ViewHolder holder, int position) {
            if (position==listFiles.size()) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(AssignmentUpload.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_imgselector);
                        dialog.setCancelable(true);
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int wwidth = metrics.widthPixels;
                        dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView tvCam = dialog.findViewById(R.id.tv_cam);
                        tvCam.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

//                fromGallery = false;
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
                                            Uri photoURI = FileProvider.getUriForFile(AssignmentUpload.this,
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
                                    Toast.makeText(AssignmentUpload.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });

                        TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
                        tvGallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                fromGallery = true;
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                                dialog.dismiss();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });
            }else{
                try {
                    File file = FileUtil.from(AssignmentUpload.this, listFiles.get(position));
                    holder.tvFileName.setText(file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount() {
            return listFiles.size()+1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                //get the Uri for the captured image

                listFiles.add(picUri);

                utils.showLog("picUri", picUri.toString());

            }
            else if (requestCode == PICK_IMAGE_MULTIPLE) {
                try {
                    if (data.getClipData() == null) {
                        listFiles.add(data.getData());
                        Uri selectedImageURI = data.getData();

                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            listFiles.add(data.getClipData().getItemAt(index).getUri());
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(AssignmentUpload.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }
            }

            if (listFiles.size()>0){
                rvAssignFiles.setAdapter(new UploadFilesAdapter(listFiles));
                llSubmit.setVisibility(View.VISIBLE);
            }else {
                llSubmit.setVisibility(View.GONE);
            }

        }


    }




    void uploadToS3(){

       util.showLoader(this);


      new Thread(new Runnable() {
          @Override
          public void run() {
              try {
                  Date expiration = new Date();
                  long msec = expiration.getTime();
                  msec += 1000 * 60 * 60; // 1 hour.
                  expiration.setTime(msec);
//                publishProgress(arg0);

                  int c=0;

                  for (int i=0;i<listFiles.size();i++){
                      c++;
                      File file = FileUtil.from(AssignmentUpload.this, listFiles.get(i));
                      String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                      File f = new File(Environment.getExternalStorageState(),file.getName());
                      boolean success = file.renameTo(file);


                      if (success){
                          utils.showLog(TAG,"url"+"testing/HWtest/student/"+hwId+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName());
                          keyName.add("testing/HWtest/student/"+hwId+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName());
                          s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                  Region.getRegion(Regions.AP_SOUTH_1));


                          PutObjectRequest por = new PutObjectRequest(
                                  sh_Pref.getString("bucket", ""),
                                  "testing/HWtest/student/"+hwId+"/"+sObj.getStudentId()+"/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName() ,
                                  file);//key is  URL



                          //making the object Public
                          por.setCannedAcl(CannedAccessControlList.PublicRead);
                          s3Client1.putObject(por);

                          utils.showLog(TAG,"urls - "+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                          if (c==listFiles.size()){
                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      postToServer();
                                  }
                              });
                          }

                      }else {
                          Toast.makeText(AssignmentUpload.this,"Rename failed!",Toast.LENGTH_SHORT).show();

                      }
                  }

              } catch (Exception e) {
                  e.printStackTrace();
                  utils.showLog(TAG,"error "+e.getMessage());
                  util.dismissDialog();
              }
          }
      }).start();


    }

    void postToServer(){


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<keyName.size();i++){
            JSONObject jObject = new JSONObject();
            try {


                jObject.put("studentHWFilePath",s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                jObject.put("version",version);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jObject);

        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hwId",hwId);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("studentId", sObj.getStudentId());
            jsonObject.put("hwFiles",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request post = new Request.Builder()
                .url(new AppUrls().UpdateStudentHomework)
//                .url("http://13.232.73.168:9000/updateStudentHomework")
                .post(body)
                .build();


        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                util.dismissDialog();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String resp = responseBody.string();


                try {
                    JSONObject ParentjObject = new JSONObject(resp);

                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AssignmentUpload.this,"Success!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               util.dismissDialog();

            }
        });


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}