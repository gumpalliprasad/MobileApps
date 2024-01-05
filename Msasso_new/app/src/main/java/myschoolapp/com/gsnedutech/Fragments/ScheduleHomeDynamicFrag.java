package myschoolapp.com.gsnedutech.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ScheduleHomeDynamicFrag extends Fragment {

    View viewScheduleHomeDynamicFrag;
    Unbinder unbinder;

    public ScheduleHomeDynamicFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewScheduleHomeDynamicFrag = inflater.inflate(R.layout.fragment_schedule_home_dynamic, container, false);
        unbinder = ButterKnife.bind(this, viewScheduleHomeDynamicFrag);

        return viewScheduleHomeDynamicFrag;
    }
}