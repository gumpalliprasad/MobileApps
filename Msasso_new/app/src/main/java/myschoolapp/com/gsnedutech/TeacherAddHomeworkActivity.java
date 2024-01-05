package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailsTeacher;
import myschoolapp.com.gsnedutech.Models.HomeWorkTypes;
import myschoolapp.com.gsnedutech.Models.TeacherFilesDetail;
import myschoolapp.com.gsnedutech.Models.TeacherHWStudentofHWObj;
import myschoolapp.com.gsnedutech.Models.TeacherHwStudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherAddHomeworkActivity extends AppCompatActivity {

    private static final String TAG = TeacherAddHomeworkActivity.class.getName();

    String sectionId,subjectId,elective;

    MyUtils utils = new MyUtils();

    AmazonS3Client s3Client1;
    List <String> keyName = new ArrayList<>();

    @BindView(R.id.sp_type)
    Spinner spType;
    @BindView(R.id.et_date_selector)
    EditText etDateSelector;
    @BindView(R.id.rv_files)
    RecyclerView rvFiles;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_hw_date_selector)
    EditText etHwDateSelector;
    @BindView(R.id.rv_prev_files)
    RecyclerView rvPrevFiles;

    List<TeacherHWStudentofHWObj> teachstndlist = new ArrayList<>();

    HomeWorkDetailsTeacher hwObj;

    Dialog dialogMain;
    Dialog dialogStudents;
    RecyclerView rvSelectedStudents;

    List<String> listTypes = new ArrayList<String>(){};
    static final int PICK_IMAGE_MULTIPLE = 2;
    static final int CAMERA_CAPTURE = 1;

    List<Uri> mImageUri = new ArrayList<>();
    List<String> fileName = new ArrayList<>();

    List<TeacherFilesDetail> filesDetailList = new ArrayList<>();

    private Uri picUri;

    SharedPreferences sh_Pref;
    TeacherObj tObj;

    List<TeacherHwStudentObj> studList = new ArrayList<>();
    List<TeacherHwStudentObj> selectedStudList = new ArrayList<>();

    StudentAdapter adapter;
    SelectedStudentsAdapter selectedAdapter;


    String hwType = "",description = "",lastDate = "",homeWorktypeId="",submissionDate="",title="",hwdate="";

    List<HomeWorkTypes> hwTypeList = new ArrayList<>();

    TeacherObj teacherObj;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_homework);

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

        etDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(TeacherAddHomeworkActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etDateSelector.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        etHwDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(TeacherAddHomeworkActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etHwDateSelector.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(etDesc.getText().toString().length()>0 && etTitle.getText().toString().length()>0){

                   description = etDesc.getText().toString();
                   title = etTitle.getText().toString();
                   try {
                       submissionDate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(etDateSelector.getText().toString()));
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }
                   try {
                       hwdate = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(etHwDateSelector.getText().toString()));
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   dialogMain = new Dialog(TeacherAddHomeworkActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                   dialogMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
                   dialogMain.setContentView(R.layout.activity_teacher_h_w_assign);

                   ((TextView)dialogMain.findViewById(R.id.tv_class_name)).setText(getIntent().getStringExtra("className"));

                   if (selectedStudList.size()>0){
                       ((TextView)dialogMain.findViewById(R.id.tv_studs)).setText(selectedStudList.size()+" Students");
                   }

                   dialogMain.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
//                           dialogMain.dismiss();
                           onBackPressed();
                       }
                   });
                   dialogMain.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           showStudentsDialog();
                       }
                   });

                   dialogMain.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           if (getIntent().hasExtra("type")){
                               updateHomeWork();
                           }else{
                               postHomeWork();
                           }
                       }
                   });

                   dialogMain.show();
               }else{
                    Toast.makeText(TeacherAddHomeworkActivity.this,"Please Enter all the details!",Toast.LENGTH_SHORT).show();
               }
            }
        });

        if (NetworkConnectivity.isConnected(TeacherAddHomeworkActivity.this)) {
            getHomeWorkTypes();
        } else {
            new MyUtils().alertDialog(1, TeacherAddHomeworkActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }


        if(getIntent().hasExtra("type")){
            hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");

            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            teacherObj = gson.fromJson(json, TeacherObj.class);
            try {
                etDateSelector.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(hwObj.getSubmissionDate())));
                etHwDateSelector.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(hwObj.getHomeworkDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            etTitle.setText(hwObj.getHomeworkTitle());
            etDesc.setText(hwObj.getHomeworkDetail());
            filesDetailList = hwObj.getTeacherFilesDetails();
            if (filesDetailList!=null && filesDetailList.size()>0){
                findViewById(R.id.ll_previous_submissions).setVisibility(View.VISIBLE);
                rvPrevFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
                rvPrevFiles.setAdapter(new PrevFilesAdapter(filesDetailList));
            }

        }

    }



    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);


        sectionId = getIntent().getStringExtra("sectionId");
        subjectId = getIntent().getStringExtra("subjectId");
        elective = getIntent().getStringExtra("elective");

        listTypes.add("HomeWork");
        listTypes.add("Assignments");
        listTypes.add("Project");



        etDateSelector.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        etHwDateSelector.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        rvFiles.setLayoutManager(new LinearLayoutManager(TeacherAddHomeworkActivity.this,RecyclerView.HORIZONTAL,false));
        rvFiles.setAdapter(new FileAdapter());
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
//                //get the Uri for the captured image

                mImageUri.add(picUri);

                File file = null;
                try {
                    file = FileUtil.from(TeacherAddHomeworkActivity.this, picUri);
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
                        mImageUri.add(data.getData());
                        Uri selectedImageURI = data.getData();
                        File file = FileUtil.from(TeacherAddHomeworkActivity.this, selectedImageURI);
                        fileName.add(file.getName());
                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            mImageUri.add(data.getClipData().getItemAt(index).getUri());
                            File file = FileUtil.from(TeacherAddHomeworkActivity.this, mImageUri.get(index));
                            fileName.add(file.getName());
                        }
                    }

                    Log.v(TAG, "mImageUri F Filemanager- " + mImageUri.size());
                    rvFiles.setAdapter(new FileAdapter());

                } catch (Exception e) {
                    Toast.makeText(TeacherAddHomeworkActivity.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    Log.v(TAG, e + "");
                }
                
            } else {
                Toast.makeText(TeacherAddHomeworkActivity.this, "You haven't picked any File", Toast.LENGTH_LONG).show();
            }

        }


        
    }

    void showStudentsDialog(){
        Dialog dialogStudents = new Dialog(TeacherAddHomeworkActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogStudents.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogStudents.setContentView(R.layout.layout_hw_student_selection);
        dialogStudents.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialogStudents.dismiss();
                onBackPressed();

                if (selectedStudList.size()>0){
                    ((TextView)dialogMain.findViewById(R.id.tv_studs)).setText(selectedStudList.size()+" Students");
                }else{
                    ((TextView)dialogMain.findViewById(R.id.tv_studs)).setText("All Students");
                }

            }
        });

        dialogStudents.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStudents.dismiss();

                if (selectedStudList.size()>0){
                    ((TextView)dialogMain.findViewById(R.id.tv_studs)).setText(selectedStudList.size()+" Students");
                }else{
                    ((TextView)dialogMain.findViewById(R.id.tv_studs)).setText("All Students");
                }

            }
        });

        EditText etSearch = dialogStudents.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable.toString());
            }
        });

        RecyclerView rvStudents = dialogStudents.findViewById(R.id.rv_students);
        rvSelectedStudents = dialogStudents.findViewById(R.id.rv_selected_students);
        rvSelectedStudents.setLayoutManager(new LinearLayoutManager(TeacherAddHomeworkActivity.this,RecyclerView.HORIZONTAL,false));
        selectedAdapter = new SelectedStudentsAdapter();
        rvSelectedStudents.setAdapter(selectedAdapter);

        rvStudents.setLayoutManager(new LinearLayoutManager(TeacherAddHomeworkActivity.this));
        adapter = new StudentAdapter(studList);
        rvStudents.setAdapter(adapter);
        rvStudents.addItemDecoration(new DividerItemDecoration(rvStudents.getContext(), DividerItemDecoration.VERTICAL));
        dialogStudents.show();


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
        public FileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i==0){
                return new ViewHolder(LayoutInflater.from(TeacherAddHomeworkActivity.this).inflate(R.layout.item_upload_laout, viewGroup, false));
            }else{
                return new ViewHolder(LayoutInflater.from(TeacherAddHomeworkActivity.this).inflate(R.layout.item_files, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull FileAdapter.ViewHolder viewHolder, int i) {

            if (i == 0) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(TeacherAddHomeworkActivity.this);
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
                                            Uri photoURI = FileProvider.getUriForFile(TeacherAddHomeworkActivity.this,
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
                                    Toast.makeText(TeacherAddHomeworkActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }

//                        try {
//                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
//                            File imageFile = new File(imageFilePath);
//                            picUri = Uri.fromFile(imageFile); // convert path to Uri
//                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                            TeacherAddHomeworkActivity.this.startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
//
//                        } catch (ActivityNotFoundException anfe) {
//                            //display an error message
//                            String errorMessage = "Whoops - your device doesn't support capturing images!";
//                            Toast.makeText(TeacherAddHomeworkActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                        }
                                dialog.dismiss();
                            }
                        });

                        TextView tvFiles = dialog.findViewById(R.id.tv_gallery);
                        tvFiles.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
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
                viewHolder.ivRemove.setVisibility(View.VISIBLE);


                viewHolder.tvFilename.setText(fileName.get(i-1));
                if (fileName.get(i-1).contains(".pdf")) {
                    viewHolder.ivFile.setImageResource(R.drawable.ic_pdf);
                } else {
                    viewHolder.ivFile.setImageResource(R.drawable.ic_img_attachment);
                }



                viewHolder.ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mImageUri.remove(i-1);
                        fileName.remove(i-1);
                        notifyDataSetChanged();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return fileName.size()+1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFilename;
            ImageView ivRemove, ivFile;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFilename = itemView.findViewById(R.id.tv_file_name);
                ivRemove = itemView.findViewById(R.id.iv_remove);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }


    void getHomeWorkTypes(){

        utils.showLoader(TeacherAddHomeworkActivity.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "HW Types URL - " + new AppUrls().HOMEWORK_GetTypes + "schemaName=" + sh_Pref.getString("schema","") );

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetTypes + "schemaName=" + sh_Pref.getString("schema","") )
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (NetworkConnectivity.isConnected(TeacherAddHomeworkActivity.this)) {
                    getStudentsForHomeWork();
                } else {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           new MyUtils().alertDialog(1, TeacherAddHomeworkActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                   getString(R.string.action_settings), getString(R.string.action_close),false);


                       }
                   });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                if (!response.isSuccessful()){

                }else{
                    try{
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("HomeWorkTypes");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<HomeWorkTypes>>() {
                            }.getType();

                            hwTypeList.clear();
                            hwTypeList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "hwTypeList - " + hwTypeList.size());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<HomeWorkTypes> adapter =
                                            new ArrayAdapter<HomeWorkTypes>(TeacherAddHomeworkActivity.this,  android.R.layout.simple_spinner_dropdown_item, hwTypeList);
                                    adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                                    spType.setAdapter(adapter);

                                    if(getIntent().hasExtra("type")){
                                        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");
                                        for (int i=0;i<hwTypeList.size();i++){
                                            if (hwTypeList.get(i).getHomeWorkType().equalsIgnoreCase(hwObj.getType())){
                                                spType.setSelection(i);
                                            }
                                        }
                                    }

                                    spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            homeWorktypeId = hwTypeList.get(i).getHomeTypeId()+"";
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                            });

                        }
                    }catch(Exception e){

                    }
                }

                if (getIntent().hasExtra("type")){
                    getStudentSubmission();
                }else{
                    if (NetworkConnectivity.isConnected(TeacherAddHomeworkActivity.this)) {
                        getStudentsForHomeWork();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MyUtils().alertDialog(1, TeacherAddHomeworkActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                        getString(R.string.action_settings), getString(R.string.action_close),false);
                            }
                        });
                    }
                }

            }
        });
    }

    class PrevFilesAdapter extends RecyclerView.Adapter<PrevFilesAdapter.ViewHolder>{
        List<TeacherFilesDetail> listFiles;
        public PrevFilesAdapter(List<TeacherFilesDetail> listFiles) {
            this.listFiles = listFiles;
        }

        @NonNull
        @Override
        public PrevFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PrevFilesAdapter.ViewHolder(LayoutInflater.from(TeacherAddHomeworkActivity.this).inflate(R.layout.item_files, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull PrevFilesAdapter.ViewHolder holder, int position) {
            holder.ivRemove.setVisibility(View.VISIBLE);


            holder.tvFilename.setText(listFiles.get(position).getFileName());
            if (listFiles.get(position).getFilePath().contains(".pdf")) {
                holder.ivFile.setImageResource(R.drawable.ic_pdf);
            } else {
                holder.ivFile.setImageResource(R.drawable.ic_img_attachment);
            }

            holder.ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteHwFile(listFiles.get(position).getAttachmentId());

                    listFiles.remove(position);

                    if (listFiles.size()==0){
                        findViewById(R.id.ll_previous_submissions).setVisibility(View.GONE);
                    }

                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFilename;
            ImageView ivRemove, ivFile;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFilename = itemView.findViewById(R.id.tv_file_name);
                ivRemove = itemView.findViewById(R.id.iv_remove);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }

    private void deleteHwFile(String attachmentId) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Request request = new Request.Builder()
                .url(AppUrls.HOMEWORK_GetHWFileDelete +"schemaName=" +sh_Pref.getString("schema","")+ "&homeworkId=" + hwObj.getHomeworkId() + "&attachementId=" + attachmentId)
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


                String resp = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TeacherAddHomeworkActivity.this, "Attachment Successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    void getStudentsForHomeWork(){


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG,"url - "+ new AppUrls().HOMEWORK_GetStudentsBySubject + "schemaName=" + sh_Pref.getString("schema","") + "&sectionId=" + sectionId + "&branchId=" + tObj.getBranchId()
                + "&isElective=" + elective + "&subjectId=" + subjectId);

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetStudentsBySubject + "schemaName=" + sh_Pref.getString("schema","") + "&sectionId=" + sectionId + "&branchId=" + tObj.getBranchId()
                        + "&isElective=" + elective + "&subjectId=" + subjectId)
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

                String resp = response.body().string();

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    JSONObject ParentjObject = null;
                    try {
                        ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("studentHomeWork");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherHwStudentObj>>() {
                            }.getType();

                            studList.clear();
                            studList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "studList - " + studList.size());

                            if (getIntent().hasExtra("type")){
                                for (int i=0;i<studList.size();i++){
                                    for (int j=0;j<teachstndlist.size();j++){
                                        if (getIntent().hasExtra("type")){
                                            if ((teachstndlist.get(j).getStudentId()+"").equalsIgnoreCase(studList.get(i).getStudentId()+"")){
                                                studList.get(j).setChecked(true);
                                                selectedStudList.add(studList.get(j));
                                            }
                                        }
                                    }
                                }
                            }
                        }else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {
                            JSONObject finalParentjObject = ParentjObject;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        showAlertDialog(finalParentjObject.getString("MESSAGE"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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


    public void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddHomeworkActivity.this);
        builder.setTitle("rankrPlus")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //this for skip dialog
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        }, 2000); //change 5000 with a specific time you want
    }


    class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> implements Filterable {

        List<TeacherHwStudentObj> listStudents;
        List<TeacherHwStudentObj> list_filtered;

        public StudentAdapter(List<TeacherHwStudentObj> listStudents) {
            this.listStudents = listStudents;
            list_filtered = studList;
        }

        @NonNull
        @Override
        public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherAddHomeworkActivity.this).inflate(R.layout.item_teacher_student_selection,parent,false));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {



            holder.tvStudentName.setText(list_filtered.get(position).getStudentName());
            holder.tvRollNumber.setText(list_filtered.get(position).getRollnumber());

            Picasso.with(TeacherAddHomeworkActivity.this).load(new AppUrls().GetstudentProfilePic + list_filtered.get(position).getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivProfPic);

            if (list_filtered.get(position).isChecked()){
                holder.cbOption.setChecked(true);
            }else{
                holder.cbOption.setChecked(false);
            }

            holder.cbOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        list_filtered.get(position).setChecked(true);
                        int index = studList.indexOf(list_filtered.get(position));
                        studList.get(index).setChecked(true);
                       if (!selectedStudList.contains(list_filtered.get(position))){
                           selectedStudList.add(list_filtered.get(position));
                       }
                    }
                    else{
                        list_filtered.get(position).setChecked(false);
                        int index = studList.indexOf(list_filtered.get(position));
                        studList.get(index).setChecked(false);

                        for (int i=0;i<selectedStudList.size();i++){
                            if (selectedStudList.get(i).getStudentId().equalsIgnoreCase(list_filtered.get(position).getStudentId())){
                                selectedStudList.remove(i);
                            }
                        }
                    }

                  selectedAdapter.notifyDataSetChanged();
                }
            });
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    utils.showLog("TAG", string);
                    if (string.isEmpty()) {
                        list_filtered = studList;
                    } else {
                        List<TeacherHwStudentObj> filteredList = new ArrayList<>();
                        for (TeacherHwStudentObj s : studList) {
                            if (s.getStudentName().toLowerCase().contains(string.toLowerCase()) || s.getRollnumber().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }
                        list_filtered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<TeacherHwStudentObj>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvStudentName,tvRollNumber;
            ImageView ivProfPic;
            CheckBox cbOption;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivProfPic = itemView.findViewById(R.id.iv_student_image);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvRollNumber = itemView.findViewById(R.id.tv_rollnum);
                cbOption = itemView.findViewById(R.id.cb_option);

            }
        }
    }

    class SelectedStudentsAdapter extends RecyclerView.Adapter<SelectedStudentsAdapter.ViewHolder>{

        @NonNull
        @Override
        public SelectedStudentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherAddHomeworkActivity.this).inflate(R.layout.item_selected_student,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull SelectedStudentsAdapter.ViewHolder holder, int position) {

            holder.tvStudentName.setText(selectedStudList.get(position).getStudentName());

            Picasso.with(TeacherAddHomeworkActivity.this).load(new AppUrls().GetstudentProfilePic + selectedStudList.get(position).getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivProfPic);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i=0;i<studList.size();i++){
                        if (studList.get(i).getStudentId().equalsIgnoreCase(selectedStudList.get(position).getStudentId())){
                            studList.get(i).setChecked(false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectedStudList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvStudentName;
            ImageView ivProfPic;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivProfPic = itemView.findViewById(R.id.iv_prof_pic);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
            }
        }
    }

    void postHomeWork(){

        utils.showLoader(TeacherAddHomeworkActivity.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray sectionArray = new JSONArray();
        JSONObject job = new JSONObject();
        try{
            job.put("sectionId",sectionId);
            sectionArray.put(job);
        }catch(Exception e){

        }


        JSONArray studentArray = new JSONArray();
        if (selectedStudList.size()>0){
            for (int i=0;i<selectedStudList.size();i++){
                try {
                    studentArray.put(selectedStudList.get(i).getStudentId()+"");
                }catch (Exception e){

                }
            }
        }else{
            for (int i=0;i<studList.size();i++){
                try {
                    studentArray.put(studList.get(i).getStudentId()+"");
                }catch (Exception e){

                }
            }
        }


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("sectionId",sectionId);
            jsonObject.put("subjectId",subjectId);
            jsonObject.put("branchId",tObj.getBranchId());
            jsonObject.put("homeworkDetail",description);
            jsonObject.put("homeworkTitle",title);
            jsonObject.put("homeworkDate",hwdate);
            jsonObject.put("createdBy",tObj.getUserId());
            jsonObject.put("submissionDate",submissionDate);
            jsonObject.put("homeWorktypeId",homeWorktypeId);
            jsonObject.put("sectionArray",sectionArray);
            jsonObject.put("studentArray",studentArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        utils.showLog(TAG,"url - "+new AppUrls().HOMEWORK_PostInsertSecHomeWork);
        utils.showLog(TAG,"url - "+jsonObject.toString());

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request post = new Request.Builder()
                .url(new AppUrls().HOMEWORK_PostInsertSecHomeWork)
                .post(body)
                .build();

        client.newCall(post).enqueue(new Callback() {
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
                utils.showLog(TAG,"response "+resp);

                if(!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }
                else{

                    try {
                        JSONObject ParentObject = new JSONObject(resp);

                        if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")){
                            if (mImageUri.size()>0){
                                uploadToS3(ParentObject.getString("HWID"));
                            }else {
//                                finish();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddHomeworkActivity.this);
                                        builder.setMessage("Homework updated successfully")
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setCancelable(false)
                                                .setPositiveButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                finish();
                                                            }
                                                        }

                                                );
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }


    private void updateHomeWork() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONArray sectionArray = new JSONArray();
        JSONObject job = new JSONObject();
        try{
            job.put("sectionId",sectionId);
            sectionArray.put(job);
        }catch(Exception e){

        }


        JSONArray studentArray = new JSONArray();
        if (selectedStudList.size()>0){
            for (int i=0;i<selectedStudList.size();i++){
                try {
                    studentArray.put(selectedStudList.get(i).getStudentId()+"");
                }catch (Exception e){

                }
            }
        }else{
            for (int i=0;i<studList.size();i++){
                try {
                    studentArray.put(studList.get(i).getStudentId()+"");
                }catch (Exception e){

                }
            }
        }


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("sectionId",sectionId);
            jsonObject.put("subjectId",subjectId);
            jsonObject.put("homeworkId", hwObj.getHomeworkId());
            jsonObject.put("homeWorktypeId", hwTypeList.get(spType.getSelectedItemPosition()));
            jsonObject.put("homeworkDetail",description);
            jsonObject.put("homeworkTitle",title);
            jsonObject.put("homeworkDate",hwdate);
            jsonObject.put("submissionDate", submissionDate);
            jsonObject.put("sectionArray",sectionArray);
            jsonObject.put("studentArray",studentArray);
            jsonObject.put("updatedBy",tObj.getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void uploadToS3(String hwId){

        utils.showLoader(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date expiration = new Date();
                    long msec = expiration.getTime();
                    msec += 1000 * 60 * 60; // 1 hour.
                    expiration.setTime(msec);

                    int c=0;

                    for (int i=0;i<mImageUri.size();i++){
                        c++;
                        File file = FileUtil.from(TeacherAddHomeworkActivity.this, mImageUri.get(i));
                        String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                        File f = new File(Environment.getExternalStorageState(),file.getName());
                        boolean success = file.renameTo(file);


                        if (success){
                            utils.showLog(TAG,"url"+"testing/HWtest/teacher/"+hwId+"/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/"+f.getName());
                            keyName.add("testing/HWtest/teacher/"+hwId+"/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/"+f.getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "testing/HWtest/teacher/"+hwId+"/"+new SimpleDateFormat("yyyyMMddHHmm").format(new Date())+"/"+f.getName() ,
                                    file);//key is  URL



                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG,"urls - "+s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i)));

                            String url = s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""),keyName.get(i));
                            
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        postToServer(hwId,url);
                                    }
                                });

                        }else {
                            Toast.makeText(TeacherAddHomeworkActivity.this,"Rename failed!",Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG,"error "+e.getMessage());
                }
            }
        }).start();

    }


    void getStudentSubmission(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().HOMEWORK_GetHomeworkDetailsById + "schemaName=" + sh_Pref.getString("schema","") + "&branchId=" + teacherObj.getBranchId() + "&sectionId=" + hwObj.getSectionId() + "&homeworkId=" + hwObj.getHomeworkId());

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetHomeworkDetailsById + "schemaName=" + sh_Pref.getString("schema","") + "&branchId=" + teacherObj.getBranchId() + "&sectionId=" + hwObj.getSectionId() + "&homeworkId=" + hwObj.getHomeworkId())
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

                String resp = response.body().string();
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("homeworkStudentsStatus");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherHWStudentofHWObj>>() {
                            }.getType();

                            List<TeacherHWStudentofHWObj> filteredList = new ArrayList<>();

                            teachstndlist.clear();
                            filteredList.clear();
                            teachstndlist = gson.fromJson(jsonArr.toString(), type);

                            List<TeacherHWStudentofHWObj> filter = new ArrayList<>();

                            for (int i=0;i<teachstndlist.size();i++){
                                if(!teachstndlist.get(i).getHWStatus().equalsIgnoreCase("NA")){
                                    filter.add(teachstndlist.get(i));
                                }
                            }

                            teachstndlist.clear();
                            teachstndlist.addAll(filter);

                            filteredList.addAll(teachstndlist);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (NetworkConnectivity.isConnected(TeacherAddHomeworkActivity.this)) {
                    getStudentsForHomeWork();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new MyUtils().alertDialog(1, TeacherAddHomeworkActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close),false);
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


    int counter=0;
    private void postToServer(String hwId, String url) {


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONArray jsonArray = new JSONArray();
        JSONObject job = new JSONObject();
        try {
            job.put("fileName",url.split("/")[url.split("/").length - 1]);
            job.put("targetPath",url);
            job.put("isActive","1");
            jsonArray.put(job);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
            jsonObject.put("hwId", hwId);
            jsonObject.put("createdBy", tObj.getUserId());
            jsonObject.put("insertRecords", jsonArray);
            url = url;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_PostUploadFiles)
                .post(body)
                .build();

        String finalUrl = url;
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

                String resp = response.body().string();

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                ++counter;
                            if (counter==keyName.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();
//                                        Toast.makeText(TeacherAddHomeworkActivity.this, "HomeWork Posted Sucessfully", Toast.LENGTH_SHORT).show();
//                                        finish();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAddHomeworkActivity.this);
                                        builder.setMessage("Homework Added Successfully!")
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setCancelable(false)
                                                .setPositiveButton("OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                finish();
                                                            }
                                                        }

                                                );
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });

                            }

                        }

                    }catch (Exception e){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }
                }

            }
        });
        
    }

    @Override
    public void onBackPressed() {

        if (dialogStudents!=null && dialogStudents.isShowing()){
            dialogStudents.dismiss();
        }else if(dialogMain!=null && dialogMain.isShowing()){
            dialogMain.dismiss();
        }else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

}