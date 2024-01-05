package myschoolapp.com.gsnedutech.Arena;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArQuizObject;
import myschoolapp.com.gsnedutech.Arena.Fragments.ArAddQuizPreview;
import myschoolapp.com.gsnedutech.Arena.Fragments.ArQuizAllTypeQuestion;
import myschoolapp.com.gsnedutech.Arena.Fragments.ArQuizCreationDetail;
import myschoolapp.com.gsnedutech.R;

public class ArAddQuizQuestion extends AppCompatActivity {


    @BindView(R.id.tv_title)
    public TextView tvTitle;

    public int stepNo = 0;

    public ArQuizObject quizObject = null;

    public String typeFirst = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_add_quiz_question);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void init(){

        Fragment fragment=null;

        switch (stepNo){
            case 0:
                fragment = new ArQuizCreationDetail();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                break;

            case 5:
                fragment = new ArQuizAllTypeQuestion();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                break;

            case 4:
                fragment = new ArAddQuizPreview();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .commit();
                break;
        }
    }

    public void setQuizObject(ArQuizObject quizObject) {
        this.quizObject = quizObject;
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure you want to go back?\nAll progress will be lost.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        switch (stepNo){
                            case 0:
                                ArAddQuizQuestion.super.onBackPressed();
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                break;

                            case 5:
                                stepNo = 0;
                                init();
                                break;
                            case 4:
                                stepNo=5;
                                init();
                                break;

                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)
                .show();


    }


}