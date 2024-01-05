package myschoolapp.com.gsnedutech.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import myschoolapp.com.gsnedutech.R;

public class StudentBottomExams extends Fragment {

    public StudentBottomExams() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_bottom_exams, container, false);
    }
}