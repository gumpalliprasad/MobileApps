package myschoolapp.com.gsnedutech.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import myschoolapp.com.gsnedutech.R;

public class TeacherBottomKHub extends Fragment {

    View teacherBottomKhubView;


    public TeacherBottomKHub() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        teacherBottomKhubView =  inflater.inflate(R.layout.fragment_teacher_bottom_khub, container, false);
        return teacherBottomKhubView;
    }
}