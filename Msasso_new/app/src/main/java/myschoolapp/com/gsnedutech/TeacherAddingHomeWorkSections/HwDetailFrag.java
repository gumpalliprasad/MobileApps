package myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.HomeWorkTypes;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HwDetailFrag extends Fragment {

    private static final String TAG = HwDetailFrag.class.getName();

    View viewHwDetailFrag;
    Unbinder unbinder;

    Activity mActivity;

    MyUtils utils = new MyUtils();

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

    static final int PICK_IMAGE_MULTIPLE = 2;
    static final int CAMERA_CAPTURE = 1;

    List<Uri> mImageUri = new ArrayList<>();
    List<String> fileName = new ArrayList<>();

    List<HomeWorkTypes> hwTypeList = new ArrayList<>();

    private Uri picUri;

    SharedPreferences sh_Pref;

    public HwDetailFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewHwDetailFrag = inflater.inflate(R.layout.fragment_hw_detail, container, false);
        unbinder = ButterKnife.bind(this,viewHwDetailFrag);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();

        return viewHwDetailFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if(((TeacherHWAddingWithSections)mActivity).newHwObj.getSubmissionDate().length()>0) {
            etDateSelector.setText(((TeacherHWAddingWithSections) mActivity).newHwObj.getSubmissionDate());
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            Date dt = calendar.getTime();
            etDateSelector.setText(new SimpleDateFormat("dd/MM/yyyy").format(dt));
        }
        if(((TeacherHWAddingWithSections)mActivity).newHwObj.getHomeworkDate().length()>0) {
            etHwDateSelector.setText(((TeacherHWAddingWithSections) mActivity).newHwObj.getHomeworkDate());
        }else{
            etHwDateSelector.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        }
        if(((TeacherHWAddingWithSections) mActivity).newHwObj.getHomeworkTitle().length()>0) {
            etTitle.setText(((TeacherHWAddingWithSections) mActivity).newHwObj.getHomeworkTitle());
        }
        if(((TeacherHWAddingWithSections) mActivity).newHwObj.getHomeworkDetail().length()>0) {
            etDesc.setText(((TeacherHWAddingWithSections) mActivity).newHwObj.getHomeworkDetail());
        }



        rvFiles.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));

        if (((TeacherHWAddingWithSections) mActivity).newHwObj.getmImageUri().size()>0 && ((TeacherHWAddingWithSections) mActivity).newHwObj.getFileName().size()>0){
            fileName.addAll(((TeacherHWAddingWithSections) mActivity).newHwObj.getFileName());
            mImageUri.addAll(((TeacherHWAddingWithSections) mActivity).newHwObj.getmImageUri());
        }

        rvFiles.setAdapter(new FileAdapter());

        etDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(mActivity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etDateSelector.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
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
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE,0);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(mActivity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etHwDateSelector.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                picker.show();
            }
        });

        viewHwDetailFrag.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTitle.getText().toString().length()>0 && etDesc.getText().toString().length()>0){
                    if (checkDates(etHwDateSelector.getText().toString(), etDateSelector.getText().toString())){
                        ((TeacherHWAddingWithSections) mActivity).newHwObj.setHomeworkTitle(etTitle.getText().toString());
                        ((TeacherHWAddingWithSections) mActivity).newHwObj.setHomeworkDetail(etDesc.getText().toString());
                        ((TeacherHWAddingWithSections) mActivity).newHwObj.setSubmissionDate(etDateSelector.getText().toString());
                        ((TeacherHWAddingWithSections) mActivity).newHwObj.setHomeworkDate(etHwDateSelector.getText().toString());
                        ((TeacherHWAddingWithSections) mActivity).newHwObj.setFileName(fileName);
                        ((TeacherHWAddingWithSections) mActivity).newHwObj.setmImageUri(mImageUri);
                        ((TeacherHWAddingWithSections)mActivity).loadFrag(2);
                    }
                }else{
                    Toast.makeText(mActivity,"Please Enter All the Details!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHwDetailFrag.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TeacherHWAddingWithSections)mActivity).loadFrag(0);
            }
        });

        if (NetworkConnectivity.isConnected(mActivity)) {
            getHomeWorkTypes();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }


    }

    private boolean checkDates(String hwDateStr, String hwSubDateStr) {
        Date hwDate = new Date();
        Date hwSubDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            hwDate = sdf.parse(hwDateStr);
            hwSubDate = sdf.parse(hwSubDateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        if (hwSubDate.equals(hwDate) || hwSubDate.before(hwDate)){
            etHwDateSelector.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
            alertDialogBuilder.setMessage("Homework Date is less than Submission Date");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return false;
        }
        else return true;

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
                return new FileAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upload_laout, viewGroup, false));
            }else{
                return new FileAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_files, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull FileAdapter.ViewHolder viewHolder, int i) {

            if (i == 0) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(mActivity);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_imgselector);
                        DisplayMetrics metrics = new DisplayMetrics();
                        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                                        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                                        // Save a file: path for use with ACTION_VIEW intents
                                        picUri =  Uri.fromFile(image);
                                        if (image != null) {
                                            Uri photoURI = FileProvider.getUriForFile(mActivity,
                                                    mActivity.getPackageName()+".provider",
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
                                    Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
                                }

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
                } else if(fileName.get(i-1).contains(".jpg") || fileName.get(i-1).contains(".png") || fileName.get(i-1).contains(".jpeg")){
                    viewHolder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
                }else if(fileName.get(i-1).contains(".mp4")){
                    viewHolder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
                }else{
                    viewHolder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
//                //get the Uri for the captured image



                mImageUri.add(picUri);

                File file = null;
                try {
                    file = FileUtil.from(mActivity, picUri);
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
                        File file = FileUtil.from(mActivity, selectedImageURI);
                        fileName.add(file.getName());
                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            mImageUri.add(data.getClipData().getItemAt(index).getUri());
                            File file = FileUtil.from(mActivity, data.getClipData().getItemAt(index).getUri());
                            fileName.add(file.getName());
                        }
                    }

                    Log.v(TAG, "mImageUri F Filemanager- " + mImageUri.size());
                    rvFiles.setAdapter(new FileAdapter());

                } catch (Exception e) {
                    Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    Log.v(TAG, e + "");
                }

            } else {
                Toast.makeText(mActivity, "You haven't picked any File", Toast.LENGTH_LONG).show();
            }

        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    void getHomeWorkTypes(){

        utils.showLoader(mActivity);

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
                mActivity.runOnUiThread(new Runnable() {
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
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
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

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<HomeWorkTypes> adapter =
                                            new ArrayAdapter<HomeWorkTypes>(mActivity,  android.R.layout.simple_spinner_dropdown_item, hwTypeList);
                                    adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                                    spType.setAdapter(adapter);

                                    if (!((TeacherHWAddingWithSections)mActivity).newHwObj.getHomeWorktypeId().equalsIgnoreCase("")){
                                        for (int i=0;i<hwTypeList.size();i++){
                                            if ((""+hwTypeList.get(i).getHomeTypeId()).equalsIgnoreCase(((TeacherHWAddingWithSections)mActivity).newHwObj.getHomeWorktypeId())){
                                                spType.setSelection(i);
                                            }
                                        }
                                    }


                                    spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            ((TeacherHWAddingWithSections)mActivity).newHwObj.setHomeWorktypeId(hwTypeList.get(i).getHomeTypeId()+"");
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


                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}