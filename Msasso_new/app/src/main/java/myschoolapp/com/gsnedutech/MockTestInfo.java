/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/9/19 5:42 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class MockTestInfo extends AppCompatActivity {
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_testinfo_title)
    TextView tvTestinfoTitle;
    @BindView(R.id.tvh2)
    TextView tvh2;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.tvh3)
    TextView tvh3;
    @BindView(R.id.lv1)
    LinearLayout lv1;
    @BindView(R.id.tv_testinfo_totque)
    TextView tvTestinfoTotque;
    @BindView(R.id.tv_testinfo_tottime)
    TextView tvTestinfoTottime;
    @BindView(R.id.tv_cansmark)
    TextView tvCansmark;
    @BindView(R.id.tv_wansmark)
    TextView tvWansmark;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    StdnTestDefClsCourseSub courseObj = new StdnTestDefClsCourseSub();
    StdnTestCategories testCategoryObj = new StdnTestCategories();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test_info);
        ButterKnife.bind(this);

        ivBack.setOnClickListener(view -> onBackPressed());

        courseObj = (StdnTestDefClsCourseSub) getIntent().getSerializableExtra("courseObj");
        testCategoryObj = (StdnTestCategories) getIntent().getSerializableExtra("testCategoryObj");


        if (testCategoryObj.getCategoryId().equalsIgnoreCase("1") || testCategoryObj.getCategoryId().equalsIgnoreCase("2") ||
                testCategoryObj.getCategoryId().equalsIgnoreCase("3") || testCategoryObj.getCategoryName().equalsIgnoreCase("CLASS-WISE")) {

            if (testCategoryObj.getCategoryName().equalsIgnoreCase("CLASS-WISE")) {
                tvh2.setVisibility(View.GONE);
                tvh3.setVisibility(View.GONE);
                view1.setVisibility(View.GONE);

                tvTestinfoTitle.setText(getIntent().getStringExtra("className"));
                tvTestinfoTotque.setText(testCategoryObj.getTotalQuestions().toString());
                tvTestinfoTottime.setText(testCategoryObj.getDuration().toString());
                tvCansmark.setText(testCategoryObj.getCorrectMarks().toString());
                tvWansmark.setText(testCategoryObj.getWrongMarks().toString());

            } else if (testCategoryObj.getCategoryId().equalsIgnoreCase("3")) {
                tvh2.setVisibility(View.VISIBLE);

                tvTestinfoTitle.setText(getIntent().getStringExtra("subjectName"));
                tvh2.setText(getIntent().getStringExtra("className"));
                tvTestinfoTotque.setText(testCategoryObj.getTotalQuestions().toString());
                tvTestinfoTottime.setText(testCategoryObj.getDuration().toString());
                tvCansmark.setText(testCategoryObj.getCorrectMarks().toString());
                tvWansmark.setText(testCategoryObj.getWrongMarks().toString());

            } else if (testCategoryObj.getCategoryId().equalsIgnoreCase("2")) {
                tvh2.setVisibility(View.VISIBLE);
                tvh3.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);

                tvTestinfoTitle.setText(getIntent().getStringExtra("chapterName"));
                tvh2.setText(getIntent().getStringExtra("subjectName"));
                tvh3.setText(getIntent().getStringExtra("className"));
                tvTestinfoTotque.setText(testCategoryObj.getTotalQuestions().toString());
                tvTestinfoTottime.setText(testCategoryObj.getDuration().toString());
                tvCansmark.setText(testCategoryObj.getCorrectMarks().toString());
                tvWansmark.setText(testCategoryObj.getWrongMarks().toString());

            } else if (testCategoryObj.getCategoryId().equalsIgnoreCase("1")) {
                tvh2.setVisibility(View.VISIBLE);
                tvh3.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);

                tvTestinfoTitle.setText(getIntent().getStringExtra("topicName"));
                tvh2.setText(getIntent().getStringExtra("chapterName"));
                tvh3.setText(getIntent().getStringExtra("subjectName") + " . " + getIntent().getStringExtra("className"));
                tvTestinfoTotque.setText(testCategoryObj.getTotalQuestions().toString());
                tvTestinfoTottime.setText(testCategoryObj.getDuration().toString());
                tvCansmark.setText(testCategoryObj.getCorrectMarks().toString());
                tvWansmark.setText(testCategoryObj.getWrongMarks().toString());

            }

        } else {
            tvh2.setVisibility(View.GONE);
            tvh3.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);

            tvTestinfoTitle.setText(testCategoryObj.getCategoryName());
            tvTestinfoTotque.setText(testCategoryObj.getTotalQuestions().toString());
            tvTestinfoTottime.setText(testCategoryObj.getDuration().toString());
            tvCansmark.setText(testCategoryObj.getCorrectMarks().toString());
            tvWansmark.setText(testCategoryObj.getWrongMarks().toString());

        }

    }

    @OnClick(R.id.btn_testinfo_start)
    public void onViewClicked() {
        if (testCategoryObj.getCategoryId().equalsIgnoreCase("1") || testCategoryObj.getCategoryId().equalsIgnoreCase("2") ||
                testCategoryObj.getCategoryId().equalsIgnoreCase("3") || testCategoryObj.getCategoryName().equalsIgnoreCase("CLASS-WISE")) {
            Intent testInfoIntent = new Intent(MockTestInfo.this, MockTestNew.class);
            testInfoIntent.putExtra("courseObj", courseObj);
            testInfoIntent.putExtra("testCategoryObj", testCategoryObj);
            testInfoIntent.putExtra("classId", getIntent().getStringExtra("classId"));
            testInfoIntent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
            testInfoIntent.putExtra("chapterId", getIntent().getStringExtra("chapterId"));
            testInfoIntent.putExtra("topicId", getIntent().getStringExtra("topicId"));
            testInfoIntent.putExtra("testTitle", tvTestinfoTitle.getText().toString());
            testInfoIntent.putExtra(AppConst.ChapterCCMapId,  getIntent().getStringExtra(AppConst.ChapterCCMapId));
            testInfoIntent.putExtra(AppConst.TopicCCMapId, getIntent().getStringExtra(AppConst.TopicCCMapId));
            startActivity(testInfoIntent);
            finish();
        } else {
            Intent testInfoIntent = new Intent(MockTestInfo.this, MockTestNew.class);
            testInfoIntent.putExtra("courseObj", courseObj);
            testInfoIntent.putExtra("testCategoryObj", testCategoryObj);
            testInfoIntent.putExtra(AppConst.ChapterCCMapId,  "0");
            testInfoIntent.putExtra(AppConst.TopicCCMapId, "0");
            startActivity(testInfoIntent);
            finish();

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}
