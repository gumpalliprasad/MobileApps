package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddQuizQuestion;
import myschoolapp.com.gsnedutech.Arena.Models.ArQuizObject;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ArQuizCreationDetail extends Fragment {

    private static final String TAG = ArQuizCreationDetail.class.getName();

    View viewArQuizCreationDetail;
    Unbinder unbinder;
    Uri picUri = null;
    
    public ArQuizCreationDetail() {
        // Required empty public constructor
    }

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    
    Activity mActivity;



    @BindView(R.id.et_quiz_title)
    EditText etQuizTitle;

    @BindView(R.id.et_quiz_description)
    EditText etQuizDescription;

    @BindView(R.id.et_no_of_questions)
    EditText etNoOfQuestions;



    @BindView(R.id.tv_continue)
    TextView tvContinue;

    @BindView(R.id.ll_add_image_cover)
    LinearLayout llAddImageCover;

    @BindView(R.id.iv_cover)
    ImageView ivCover;


    String[] quizTypeList = {"Scholastic","Co - Scholastic"};
    String[] quizModeList = {"MCQ","True/False","FIll up the Blanks"};

    int colorPos = 0;

    int[] colors = new DrawableUtils().getDarkPalet();

    ArQuizObject quizObject = null;

   

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArQuizCreationDetail = inflater.inflate(R.layout.fragment_ar_quiz_creation_detail, container, false);
        unbinder = ButterKnife.bind(this,viewArQuizCreationDetail);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        init();
        ((ArAddQuizQuestion)mActivity).tvTitle.setText("Create A Quiz");
        
        return viewArQuizCreationDetail;
    }
    
    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        if (((ArAddQuizQuestion)mActivity).quizObject!=null){
            quizObject = ((ArAddQuizQuestion)mActivity).quizObject;
            etQuizTitle.setText(quizObject.getQuizTitle());
            etQuizDescription.setText(quizObject.getQuizDesc());
            etNoOfQuestions.setText(quizObject.getNumberOfQuestions()+"");
            if (quizObject.getCoverImage()!=null){
                try {
                    ivCover.setImageBitmap(MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), quizObject.getCoverImage()));
                    viewArQuizCreationDetail.findViewById(R.id.ll_add_image).setVisibility(View.GONE);
                    ivCover.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                viewArQuizCreationDetail.findViewById(R.id.ll_add_image).setVisibility(View.VISIBLE);
                ivCover.setVisibility(View.GONE);
            }

        }


        viewArQuizCreationDetail.findViewById(R.id.ll_add_image_cover).setOnClickListener(new View.OnClickListener() {
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

//                Intent intent_upload = new Intent();
//                intent_upload.setType("image/*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload, 1);
            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etQuizTitle.getText().toString().length()>0 && etNoOfQuestions.getText().toString().length()>0 && etQuizDescription.getText().toString().length()>0){

                    if (quizObject==null) {
                        quizObject = new ArQuizObject();
                    }
                    quizObject.setQuizTitle(etQuizTitle.getText().toString());
                    quizObject.setQuizDesc(etQuizDescription.getText().toString());
                    quizObject.setNumberOfQuestions(Integer.parseInt(etNoOfQuestions.getText().toString()));
                    quizObject.setCoverImage(picUri);

                    if (quizObject.getListMCQ().size()==0){
                        Dialog dialog = new Dialog(mActivity);
                        dialog.setContentView(R.layout.layout_arena_quiz_option);
                        dialog.setCancelable(true);
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
                        dialog.show();

                        dialog.findViewById(R.id.tv_close_dialog).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                if (picUri!=null){
                                    quizObject.setCoverImage(picUri);
                                }
                                ((ArAddQuizQuestion)mActivity).typeFirst = "MCQ";
                                ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
                                ((ArAddQuizQuestion)mActivity).stepNo = 5;
                                ((ArAddQuizQuestion)mActivity).init();
                            }
                        });
                        dialog.findViewById(R.id.cv_maq).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                if (picUri!=null){
                                    quizObject.setCoverImage(picUri);
                                }
                                ((ArAddQuizQuestion)mActivity).typeFirst = "MAQ";
                                ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
                                ((ArAddQuizQuestion)mActivity).stepNo = 5;
                                ((ArAddQuizQuestion)mActivity).init();
                            }
                        });
                        dialog.findViewById(R.id.cv_tf).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                if (picUri!=null){
                                    quizObject.setCoverImage(picUri);
                                }
                                ((ArAddQuizQuestion)mActivity).typeFirst = "True/False";
                                ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
                                ((ArAddQuizQuestion)mActivity).stepNo = 5;
                                ((ArAddQuizQuestion)mActivity).init();
                            }
                        });
                    }else {
                        quizObject.setCoverImage(picUri);
                        ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
                        ((ArAddQuizQuestion)mActivity).stepNo = 5;
                        ((ArAddQuizQuestion)mActivity).init();
                    }


                }else {
                    Toast.makeText(mActivity,"Please Enter All the Details!",Toast.LENGTH_SHORT).show();
                }


//                if (etQuizTitle.getText().toString().length()>0 && etNoOfQuestions.getText().toString().length()>0 ){
//                    quizObject = new ArQuizObject();
//                    quizObject.setQuizType(quizTypeList[spQuizType.getSelectedItemPosition()]);
//                    quizObject.setQuestionType(quizModeList[spQuizMode.getSelectedItemPosition()]);
//                    quizObject.setQuizTitle(etQuizTitle.getText().toString());
//                    quizObject.setNumberOfQuestions(Integer.parseInt(etNoOfQuestions.getText().toString()));
//
//
//                    if (quizModeList[spQuizMode.getSelectedItemPosition()].equalsIgnoreCase("MCQ")){
//                        ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
//                        ((ArAddQuizQuestion)mActivity).stepNo = 1;
//                        ((ArAddQuizQuestion)mActivity).init();
//                    }
//                    if (quizModeList[spQuizMode.getSelectedItemPosition()].equalsIgnoreCase("True/False")){
//                        ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
//                        ((ArAddQuizQuestion)mActivity).stepNo = 2;
//                        ((ArAddQuizQuestion)mActivity).init();
//                    }
//                    if (quizModeList[spQuizMode.getSelectedItemPosition()].equalsIgnoreCase("FIll up the Blanks")){
//                        ((ArAddQuizQuestion)mActivity).setQuizObject(quizObject);
//                        ((ArAddQuizQuestion)mActivity).stepNo = 3;
//                        ((ArAddQuizQuestion)mActivity).init();
//                    }
//
//                }
//                else {
//                    Toast.makeText(mActivity,"Please Enter All the Details!",Toast.LENGTH_SHORT).show();
//                }


            }
        });


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode==RESULT_OK){
           if (requestCode==1){
               picUri = data.getData();
//               Bitmap bitmap = null;
//               try {
//
//                   bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);
//               } catch (IOException e) {
//                   e.printStackTrace();
//               }

               viewArQuizCreationDetail.findViewById(R.id.ll_add_image).setVisibility(View.GONE);
               viewArQuizCreationDetail.findViewById(R.id.iv_cover).setVisibility(View.VISIBLE);

//               ((ImageView)viewArQuizCreationDetail.findViewById(R.id.iv_cover)).setImageBitmap(bitmap);
               ((ImageView)viewArQuizCreationDetail.findViewById(R.id.iv_cover)).setImageURI(picUri);
           }else {
//               picUri = data.getData();
//               Bitmap bitmap = null;
//               try {
//                   bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);
//
//               } catch (IOException e) {
//                   e.printStackTrace();
//               }

               viewArQuizCreationDetail.findViewById(R.id.ll_add_image).setVisibility(View.GONE);
               viewArQuizCreationDetail.findViewById(R.id.iv_cover).setVisibility(View.VISIBLE);

//               ((ImageView)viewArQuizCreationDetail.findViewById(R.id.iv_cover)).setImageURI(bitmap);
               ((ImageView)viewArQuizCreationDetail.findViewById(R.id.iv_cover)).setImageURI(picUri);
           }


        }

        super.onActivityResult(requestCode, resultCode, data);
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