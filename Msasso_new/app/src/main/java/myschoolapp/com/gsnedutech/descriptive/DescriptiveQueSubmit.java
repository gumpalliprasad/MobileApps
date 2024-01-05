package myschoolapp.com.gsnedutech.descriptive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.LocalImageDisplay;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.descriptive.models.DescCategory;
import myschoolapp.com.gsnedutech.descriptive.models.StudentAnswer;

public class DescriptiveQueSubmit extends AppCompatActivity {

    private static final String TAG = DescriptiveQueSubmit.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.cwv_question)
    CustomWebview cwvQuestion;

    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    @BindView(R.id.rv_files)
    RecyclerView rvFiles;
    @BindView(R.id.rv_prevFiles)
    RecyclerView rvPrevFiles;
    List<StudentAnswer> answers = new ArrayList<>();

    class FileUri{
        FileUri(Uri fileUri, int fileOrder){
            this.uri = fileUri;
            this.order = fileOrder;
        }
        Uri uri;
        Integer order;
    }

    List<FileUri> mImageUri = new ArrayList<>();
    AmazonS3Client s3Client1;
    List<String> fileName = new ArrayList<>();
    List <String> keyName = new ArrayList<>();
    DescCategory descQuestionObj = new DescCategory();

    String baseUrl = "";
    private Uri picUri;
    String schemaName = "";
    int ExamId = 0, studentId = 0, Questionid = 0;



    SharedPreferences sh_Pref;

    static final int PICK_IMAGE_MULTIPLE = 2;
    static final int CAMERA_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptive_que_submit);
        ButterKnife.bind(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

        if (descQuestionObj.getStudentAnswer().size()>0){
            answers = descQuestionObj.getStudentAnswer();
            rvPrevFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
            rvPrevFiles.setAdapter(new PrevAdapter());
        }
        else {
            findViewById(R.id.tv_prev_head).setVisibility(View.GONE);
        }


        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());

        tvSubmit.setOnClickListener(v -> {
            if (fileName.size() != 0){
                uploadToS3();
            }
            else{
                setResulttoPrevious();
            }

        });
    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        schemaName = sh_Pref.getString("schema", "");

        descQuestionObj  = (DescCategory) getIntent().getSerializableExtra("questionObj");
        ExamId = getIntent().getIntExtra("examId", 0);
        studentId = getIntent().getIntExtra("studentId", 0);
        Questionid = descQuestionObj.getQuestionId();
        baseUrl = "descriptive/"+schemaName+"/"+ExamId+"/"+studentId+"/"+Questionid+"/";
        rvFiles.setLayoutManager(new LinearLayoutManager(DescriptiveQueSubmit.this,RecyclerView.HORIZONTAL,false));
        rvFiles.setAdapter(new FileAdapter());

        cwvQuestion.setText(getIntent().getStringExtra("question"));

    }

    class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position==0){
                return 0;
            }
            else{
                return 1;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i==0){
                return new ViewHolder(LayoutInflater.from(DescriptiveQueSubmit.this).inflate(R.layout.item_descr_upload_laout, viewGroup, false));
            }else{
                return new ViewHolder(LayoutInflater.from(DescriptiveQueSubmit.this).inflate(R.layout.item_descr_files, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            if (i == 0) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(DescriptiveQueSubmit.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_imgselector);
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int wwidth = metrics.widthPixels;
                        dialog.getWindow().setLayout((int) (wwidth*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.setCancelable(true);

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
                                            Uri photoURI = FileProvider.getUriForFile(DescriptiveQueSubmit.this,
                                                    getPackageName()+".provider",
                                                    image);
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                            startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                                        }
                                    }
                                    else {
                                        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + ".jpg";
                                        File imageFile = new File(imageFilePath);
                                        picUri = Uri.fromFile(imageFile); // convert path to Uri
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                        startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
                                    }
                                } catch (ActivityNotFoundException | IOException anfe) {
                                    //display an error message
                                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                                    Toast.makeText(DescriptiveQueSubmit.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();
                            }
                        });

                        TextView tvFiles = dialog.findViewById(R.id.tv_gallery);
                        tvFiles.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

            }else{
                viewHolder.tvAttach.setText(fileName.get(i-1));
                viewHolder.ivAttach.setImageResource(R.drawable.ic_student_sub_jpg);
                viewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(DescriptiveQueSubmit.this, LocalImageDisplay.class);
                    try {
                        File f = FileUtil.from(DescriptiveQueSubmit.this,mImageUri.get(i-1).uri).getAbsoluteFile();
                        intent.putExtra("path",f.getAbsolutePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                viewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(DescriptiveQueSubmit.this).setTitle("Alert")
                                .setMessage("Are you sure to delete the file")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    mImageUri.remove(i-1);
                                    fileName.remove(i-1);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }).setCancelable(true)
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                }).show();
                    }
                });
            }




        }

        @Override
        public int getItemCount() {
            return fileName.size()+1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivAttach,ivDel;
            TextView tvAttach;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivAttach = itemView.findViewById(R.id.iv_file);
                ivDel = itemView.findViewById(R.id.iv_del);
                tvAttach = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
//                //get the Uri for the captured image



                mImageUri.add(new FileUri(picUri,0));

                File file = null;
                try {
                    file = FileUtil.from(DescriptiveQueSubmit.this, picUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileName.add(file.getName());
                rvFiles.setAdapter(new FileAdapter());


            }
            else if (requestCode == PICK_IMAGE_MULTIPLE && null != data) {
                // Get the Image from data
                try {
                    if (data.getClipData() == null) {
                        mImageUri.add(new FileUri(data.getData(),0));
                        Uri selectedImageURI = data.getData();
                        File file = FileUtil.from(DescriptiveQueSubmit.this, selectedImageURI);
                        fileName.add(file.getName());
                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            mImageUri.add(new FileUri(data.getClipData().getItemAt(index).getUri(),0));
                            File file = FileUtil.from(DescriptiveQueSubmit.this, data.getClipData().getItemAt(index).getUri());
                            fileName.add(file.getName());
                        }
                    }

                    Log.v(TAG, "mImageUri F Filemanager- " + mImageUri.size());
                    rvFiles.setAdapter(new FileAdapter());

                } catch (Exception e) {
                    Toast.makeText(DescriptiveQueSubmit.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    Log.v(TAG, e + "");
                }

            } else {
                Toast.makeText(DescriptiveQueSubmit.this, "You haven't picked any File", Toast.LENGTH_LONG).show();
            }

        }
        else if (requestCode == 1234 && resultCode == 4321){
            Intent intent = new Intent(DescriptiveQueSubmit.this, DescriptiveSectionDetails.class);
            intent.putExtra("resultObj", data.getSerializableExtra("resultObj"));
            intent.putExtra("pos", getIntent().getIntExtra("pos",0));
            setResult(4321, intent);
            onBackPressed();
        }


    }


    void uploadToS3(){

        utils.showLoader(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date expiration = new Date();
                    long msec = expiration.getTime();
                    msec += 1000 * 60 * 60; // 1 hour.
                    expiration.setTime(msec);
//                publishProgress(arg0);

                    Collections.sort(mImageUri, new Comparator<FileUri>() {
                        public int compare(FileUri o1, FileUri o2) {
                            return o1.order.compareTo(o2.order);
                        }
                    });

                    int c=0;


                    for (int i=0;i<mImageUri.size();i++){
                        c++;
                        File file = FileUtil.from(DescriptiveQueSubmit.this, mImageUri.get(i).uri);
                        String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                        File f = new File(Environment.getExternalStorageState(),file.getName());
                        boolean success = file.renameTo(file);


                        if (success){
                            utils.showLog(TAG,"url"+baseUrl+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName());
                            keyName.add(baseUrl+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    baseUrl+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+"/"+f.getName() ,
                                    file);//key is  URL



                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG,"urls - "+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                            if (c==mImageUri.size()){
                                runOnUiThread(() -> {
                                    utils.dismissDialog();
                                    setResulttoPrevious();
                                });
                            }

                        }else {
                            Toast.makeText(DescriptiveQueSubmit.this,"Rename failed!",Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG,"error "+e.getMessage());
                    utils.dismissDialog();
                }
            }
        }).start();


    }

    private void setResulttoPrevious() {
        List<StudentAnswer> answers = descQuestionObj.getStudentAnswer();

        for ( int i =0; i< keyName.size(); i++) {
            StudentAnswer ans = new StudentAnswer();
            ans.setPath(s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));
            ans.setFileOrder(i+1);
            answers.add(ans);
        }

        descQuestionObj.setStudentAnswer(answers);
        if (descQuestionObj.getStudentAnswer().size()==0){
            Intent intent = new Intent(DescriptiveQueSubmit.this, DescriptiveSectionDetails.class);
            intent.putExtra("resultObj", descQuestionObj);
            intent.putExtra("pos", getIntent().getIntExtra("pos",0));
            setResult(4321, intent);
            onBackPressed();
        }
        else {
            Intent intent = new Intent(DescriptiveQueSubmit.this, DescriptiveFileOrder.class);
            intent.putExtra("resultObj", descQuestionObj);
            startActivityForResult(intent, 1234);
        }

//        Intent intent = new Intent(DescriptiveQueSubmit.this, DescriptiveSectionDetails.class);
//        intent.putExtra("resultObj", descQuestionObj);
//        intent.putExtra("pos", getIntent().getIntExtra("pos",0));
//        setResult(4321, intent);
//        onBackPressed();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    class PrevAdapter extends RecyclerView.Adapter<PrevAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(DescriptiveQueSubmit.this).inflate(R.layout.item_article_files,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvFileName.setText(URLUtil.guessFileName(answers.get(position).getPath(),null,null));

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(DescriptiveQueSubmit.this, ImageDisp.class);
                intent.putExtra("path", answers.get(position).getPath());
                startActivity(intent);
            });

            holder.cvDel.setOnClickListener(v -> {
                answers.remove(position);
                if (answers.size()==0){
                    findViewById(R.id.tv_prev_head).setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return answers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFile;
            TextView tvFileName;
            CardView cvDel;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                cvDel = itemView.findViewById(R.id.cv_del);

            }
        }
    }

}