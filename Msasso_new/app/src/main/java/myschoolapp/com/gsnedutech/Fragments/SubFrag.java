/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import myschoolapp.com.gsnedutech.Models.AnalysisTestSubject;
import myschoolapp.com.gsnedutech.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubFrag extends Fragment {

    TextView tvPercentage, tvTestTaken, tvTimeSpent, tvSubName;
    ImageView ivSub;
    View v;


    AnalysisTestSubject sub;

    public SubFrag(AnalysisTestSubject sub) {
        this.sub = sub;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.cv_analysis_sub2, container, false);
        init();
        setSubject();
        return v;
    }

    public void init() {
        tvPercentage = v.findViewById(R.id.tv_score_sub);
        tvTestTaken = v.findViewById(R.id.tv_test_taken_sub);
        tvTimeSpent = v.findViewById(R.id.tv_time_spent_sub);
        tvSubName = v.findViewById(R.id.tv_sub_name);
        ivSub = v.findViewById(R.id.iv_sub);

    }

    void setSubject() {
        tvSubName.setText(sub.getSubjectName());
        tvPercentage.setText(sub.getPercentage() + "%");
        tvTestTaken.setText(sub.getTestCount() + "");
//        tvTimeSpent.setText(sub.getTotalTimeSpent() + " mins");
        tvTimeSpent.setText("" + String.format("%02d:%02d:%02d", Integer.parseInt(sub.getTotalTimeSpent() ) / 3600, Integer.parseInt(sub.getTotalTimeSpent() )  / 60, Integer.parseInt(sub.getTotalTimeSpent() )  % 60));

        switch (sub.getSubjectGroup()) {
            case "Maths":
                ivSub.setImageResource(R.drawable.ic_maths_icon_white);
                break;
            case "Science":
                ivSub.setImageResource(R.drawable.ic_science_white);
                break;
            case "English":
                ivSub.setImageResource(R.drawable.ic_english_white);
                break;
            case "Social Studies":
            case "SocialScience":
                ivSub.setImageResource(R.drawable.ic_social_white);
                break;
            case "GeneralKnowledge":
                ivSub.setImageResource(R.drawable.ic_gk_white);
                break;
            case "Hindi":
                ivSub.setImageResource(R.drawable.ic_hindi_white);
                break;
            case "Telugu":
                ivSub.setImageResource(R.drawable.ic_telugu_white);
                break;
            case "Physics":
                ivSub.setImageResource(R.drawable.ic_physics_white);
                break;
            case "Chemistry":
                ivSub.setImageResource(R.drawable.ic_chemistry_white);
                break;
            case "Biology":
                ivSub.setImageResource(R.drawable.ic_biology_white);
                break;
            default:
                ivSub.setImageResource(R.drawable.ic_science_white);
                break;
        }
    }


}
