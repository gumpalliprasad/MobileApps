package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddFlashCards;
import myschoolapp.com.gsnedutech.Arena.Models.ArFlashCardsObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ArFlashCreationDetail extends Fragment {

    View viewArFlashCreationDetail;
    Unbinder unbinder;

    Activity mActivity;

    @BindView(R.id.et_flash_title)
    EditText etFlashTitle;

    @BindView(R.id.et_no_of_flashcards)
    EditText etNoOfFlashcards;

    @BindView(R.id.rv_colors)
    RecyclerView rvColors;

    @BindView(R.id.tv_next)
    TextView tvNext;

    @BindView(R.id.ll_add_image_cover)
    LinearLayout llAddImageCover;

    @BindView(R.id.iv_cover)
    ImageView ivCover;

    int selectedFlashColor = 0;

    ArFlashCardsObj flashObject;

    public String[] colorArray = {"#FFD439","#FFC19F","#42CBA2","#E64C3C","#38AAFF"};


    ColorAdapter colorAdapter;

    MyUtils utils = new MyUtils();
    StudentObj sObj;
    TeacherObj tObj;
    SharedPreferences sh_Pref;
    AmazonS3Client s3Client1;

    Uri picUri = null;

    String cover = "";

    String branchId,sectionId,studentId;

    public ArFlashCreationDetail() {
        // Required empty public constructor
    }

    public static ArFlashCreationDetail newInstance(ArFlashCardsObj flashObject) {

        Bundle args = new Bundle();
        args.putString("flashObj", new Gson().toJson(flashObject));
        ArFlashCreationDetail fragment = new ArFlashCreationDetail();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            this.flashObject = new Gson().fromJson(getArguments().getString("flashObj"), ArFlashCardsObj.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArFlashCreationDetail = inflater.inflate(R.layout.fragment_ar_flash_creation_detail, container, false);
        unbinder = ButterKnife.bind(this,viewArFlashCreationDetail);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();


        ((ArAddFlashCards)mActivity).tvTitle.setText("Flash Cards");

        if (flashObject.getArenaName()!=null){
            etFlashTitle.setText(flashObject.getArenaName());
            etNoOfFlashcards.setText(""+flashObject.getQuestionCount());
            String color = flashObject.getColor();
            int pos = new ArrayList<String>(Arrays.asList(colorArray)).indexOf(color);
            selectedFlashColor = pos;
            colorAdapter.notifyDataSetChanged();

            if (((ArAddFlashCards)mActivity).cover!=null){
                ivCover.setVisibility(View.VISIBLE);
                picUri = Uri.fromFile(((ArAddFlashCards)mActivity).cover);
                ivCover.setImageURI(picUri);
            }

        }

        return viewArFlashCreationDetail;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
            branchId = tObj.getBranchId()+"";
            sectionId = tObj.getRoleName()+"";
            studentId = tObj.getUserId()+"";
        }else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            branchId = sObj.getBranchId()+"";
            sectionId = sObj.getClassCourseSectionId()+"";
            studentId = sObj.getStudentId()+"";
        }

        rvColors.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
        colorAdapter = new ColorAdapter();
        rvColors.setAdapter(colorAdapter);

        tvNext.setOnClickListener(view -> {
            if (etFlashTitle.getText().toString().length()>0 && etNoOfFlashcards.getText().toString().length()>0 && picUri!=null){
                try {
                    File file = FileUtil.from(mActivity,picUri);
                    flashObject = new ArFlashCardsObj();
                    flashObject.setArenaName(etFlashTitle.getText().toString().trim());
                    flashObject.setColor(colorArray[selectedFlashColor]);
                    flashObject.setQuestionCount(Integer.parseInt(etNoOfFlashcards.getText().toString().trim()));
                    ((ArAddFlashCards)mActivity).setFlashObject(flashObject);
                    ((ArAddFlashCards)mActivity).stepNo = 1;
                    ((ArAddFlashCards)mActivity).cover = file;
                    ((ArAddFlashCards)mActivity).init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(mActivity,"Please Enter All the Details!",Toast.LENGTH_SHORT).show();
            }
        });

        viewArFlashCreationDetail.findViewById(R.id.ll_add_image_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_imgselector);
                dialog.setCancelable(true);
                DisplayMetrics metrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                                File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                                // Save a file: path for use with ACTION_VIEW intents
                                picUri =  Uri.fromFile(image);
                                if (image != null) {
                                    Uri photoURI = FileProvider.getUriForFile(mActivity,
                                            mActivity.getPackageName()+".provider",
                                            image);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, 2);
                                }
                            }
                            else {
                                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                File imageFile = new File(imageFilePath);
                                picUri = Uri.fromFile(imageFile); // convert path to Uri
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                startActivityForResult(takePictureIntent, 2);
                            }
                        } catch (ActivityNotFoundException | IOException anfe) {
                            //display an error message
                            String errorMessage = "Whoops - your device doesn't support capturing images!";
                            Toast.makeText(mActivity, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                TextView tvGallery = dialog.findViewById(R.id.tv_gallery);
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                fromGallery = true;
                        Intent intent_upload = new Intent();
                        intent_upload.setType("image/*");
                        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent_upload, 1);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode==1){
                picUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);
                    ivCover.setImageBitmap(bitmap);
                    ivCover.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Bitmap bitmap = null;
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);

//                    Log.v("tag","width "+bitmap.getWidth()+" height "+bitmap.getHeight());
//
//                    Bitmap bmp= BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(picUri));
//                    Log.e("bmp "," = " + bmp);
//                    ivCover.setImageBitmap(bmp);



                    if (bitmap.getHeight()>bitmap.getWidth()){
                        Matrix matrix = new Matrix();

                        matrix.postRotate(90);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                        ivCover.setImageBitmap(rotatedBitmap);

                    }else {
                        ivCover.setImageBitmap(bitmap);
                    }
                    ivCover.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{

        @NonNull
        @Override
        public ColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ColorAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_flash_card_color,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ColorAdapter.ViewHolder holder, int position) {

            holder.llColor.setBackgroundColor(Color.parseColor(colorArray[position]));
            if (position==selectedFlashColor){
                holder.ivColor.setImageResource(R.drawable.ic_tick_white);
            }else {
                holder.ivColor.setImageResource(0);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedFlashColor = position;
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return colorArray.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivColor;
            LinearLayout llColor;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivColor = itemView.findViewById(R.id.iv_color);
                llColor = itemView.findViewById(R.id.ll_color);
            }
        }
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