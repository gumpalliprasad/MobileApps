package myschoolapp.com.gsnedutech.Arena.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Arena.ArAddFlashCards;
import myschoolapp.com.gsnedutech.Models.FlashCardFilesArray;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.FileUtil;

import static android.app.Activity.RESULT_OK;

public class ArAddFalsh extends Fragment {

    View viewArAddFlashCard;
    Unbinder unbinder;

    Activity mActivity;

    @BindView(R.id.ll_color)
    LinearLayout llColor;

    @BindView(R.id.tv_card_count)
    TextView tvCardCount;

    @BindView(R.id.et_answer)
    EditText etAnswer;

    @BindView(R.id.ll_front)
    LinearLayout llFront;

    @BindView(R.id.tv_tap_ans)
    TextView tvTapAns;

    @BindView(R.id.tv_add_flash_card)
    TextView tvAddFlashCard;

    @BindView(R.id.iv_q_image)
    ImageView qImage;

    @BindView(R.id.et_ques)
    EditText etQues;

    @BindView(R.id.cv_falsh_card)
    CardView cvFlashCard;

    @BindView(R.id.tv_q_image)
    TextView tvQImage;

    @BindView(R.id.tv_a_image)
    TextView tvAImage;

    @BindView(R.id.iv_a_image)
    ImageView ivAImage;

    @BindView(R.id.tv_tap_ques)
    TextView tvTapQuestion;

    String[] imageUri = {"",""};

    ArrayList<FlashCardFilesArray> filesArrays = new ArrayList<>();

    int totalCards;

    int currentPos = 0;
    int postClick = 0;

    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE = 2;
    int imageQorA = 1;



    public ArAddFalsh() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewArAddFlashCard = inflater.inflate(R.layout.fragment_ar_add_falsh, container, false);
        unbinder = ButterKnife.bind(this,viewArAddFlashCard);

        init();

        return viewArAddFlashCard;
    }

    void init(){
        llColor.setBackgroundColor(Color.parseColor(((ArAddFlashCards)mActivity).flashObject.getColor()));
        totalCards = ((ArAddFlashCards)mActivity).flashObject.getQuestionCount();
        tvCardCount.setText((currentPos+1)+"/"+totalCards);

        tvTapAns.setOnClickListener(view -> {
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
            oa1.setInterpolator(new AccelerateDecelerateInterpolator());
            oa2.setInterpolator(new DecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    llFront.setVisibility(View.GONE);
                    oa2.start();
                }
            });
            oa1.start();

        });

        tvTapQuestion.setOnClickListener(view -> {
            final ObjectAnimator oa1 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 1f, 0f);
            final ObjectAnimator oa2 = ObjectAnimator.ofFloat(cvFlashCard, "scaleX", 0f, 1f);
            oa1.setInterpolator(new DecelerateInterpolator());
            oa2.setInterpolator(new AccelerateDecelerateInterpolator());
            oa1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    llFront.setVisibility(View.VISIBLE);
                    oa2.start();
                }
            });
            oa1.start();

        });

        tvAddFlashCard.setOnClickListener(view -> {
            if (etQues.getText().toString().length()>0 && etAnswer.getText().toString().length()>0){
                if (++currentPos<totalCards){
                    if (currentPos==(totalCards-1)){
                        tvAddFlashCard.setText("Upload");
                    }
                    createFlashFile();
                    etQues.setText("");
                    imageUri[0] = "";
                    imageUri[1] = "";
                    qImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    ivAImage.setImageResource(android.R.drawable.ic_menu_gallery);
                    etAnswer.setText("");
                    etAnswer.setHint("Enter the Answer");
                    tvCardCount.setText((currentPos+1)+"/"+totalCards);
                    llFront.setVisibility(View.VISIBLE);
                }
                else {
                    if (postClick == 0) {
                        createFlashFile();
                        postClick = 1;
                    }
                    ((ArAddFlashCards)mActivity).postFlashCard(filesArrays);
                }
            }
            else {
                Toast.makeText(mActivity,"Please enter all the details!",Toast.LENGTH_SHORT).show();
            }

        });

        qImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageQorA = 1;
                addImage();
//                Intent intent_upload = new Intent();
//                intent_upload.setType("image/*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload,1);
            }
        });

        ivAImage.setOnClickListener(view -> {
            imageQorA = 2;
            addImage();
//            Intent intent_upload = new Intent();
//            intent_upload.setType("image/*");
//            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent_upload,2);
        });

    }

    void addImage(){
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_imgselector);
        dialog.setCancelable(true);
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
            }
        });

        dialog.show();
    }

    private void createFlashFile() {
        FlashCardFilesArray file = new FlashCardFilesArray();
        if(imageUri[0]!=null&& imageUri[0].length()>0)
            file.setQuestion(etQues.getText().toString().trim()+"~~"+imageUri[0]);
        else file.setQuestion(etQues.getText().toString().trim());
        if(imageUri[1]!=null&& imageUri[1].length()>0)
            file.setAnswer(etAnswer.getText().toString().trim()+"~~"+imageUri[1]);
        else file.setAnswer(etAnswer.getText().toString().trim());
        file.setExplanation("NA");
        file.setOption1("NA");
        file.setOption2("NA");
        file.setOption3("NA");
        file.setOption4("NA");
        filesArrays.add(file);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){
            if (requestCode == CAMERA_CAPTURE) {
                //get the Uri for the captured image
                Uri uri = picUri;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
                    if (imageQorA == 1) {
                        try {
                            File file = FileUtil.from(mActivity,uri);
                            if (file.exists()){
                                imageUri[0] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        qImage.setImageBitmap(bitmap);
                        tvQImage.setVisibility(View.GONE);
                    }
                    else {
                        try {
                            File file = FileUtil.from(mActivity,uri);
                            if (file.exists()){
                                imageUri[1] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ivAImage.setImageBitmap(bitmap);
                        tvAImage.setVisibility(View.GONE);
                    }
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), picUri);
                    if (imageQorA == 1) {
                        try {
                            File file = FileUtil.from(mActivity,picUri);
                            if (file.exists()){
                                imageUri[0] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        qImage.setImageBitmap(bitmap);
                        tvQImage.setVisibility(View.GONE);
                    }
                    else {
                        try {
                            File file = FileUtil.from(mActivity,picUri);
                            if (file.exists()){
                                imageUri[1] = file.getAbsolutePath();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ivAImage.setImageBitmap(bitmap);
                        tvAImage.setVisibility(View.GONE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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