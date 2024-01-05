/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 29/10/19 11:08 AM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.AdminMessages;
import myschoolapp.com.gsnedutech.Models.*;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminBottomFragRankrPlus extends Fragment {

    private static final String TAG = "SriRam -" + AdminBottomFragRankrPlus.class.getName();

    View adminPlusview;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    AdminObj adminObj;


    public AdminBottomFragRankrPlus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adminPlusview = inflater.inflate(R.layout.fragment_admin_bottom_frag_rankr_plus, container, false);
        unbinder = ButterKnife.bind(this, adminPlusview);
        init();
        return adminPlusview;
    }

    private void init() {
        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("adminObj", "");
        adminObj = gson.fromJson(json, AdminObj.class);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_manageMembers, R.id.ll_staffAttendance,R.id.ll_transport, R.id.ll_calendar, R.id.ll_gallery, R.id.ll_messg,R.id.set,R.id.ll_circular})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_manageMembers:
//                Intent manageMembersIntent = new Intent(getActivity(), ManageMembers.class);
//                manageMembersIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId","0"));
//                manageMembersIntent.putExtra("roleId", "" + adminObj.getRoleId());
//                startActivity(manageMembersIntent);
                break;
            case R.id.ll_transport:
//                startActivity(new Intent(getActivity(), StudentTransport.class));
                break;
            case R.id.ll_staffAttendance:
//                Intent staffAttRegIntent = new Intent(getActivity(), StaffAttendanceAnalysis.class);
//                staffAttRegIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId","0"));
//                staffAttRegIntent.putExtra("roleId", "" + adminObj.getRoleId());
//                staffAttRegIntent.putExtra("userId", "" + adminObj.getUserId());
//                startActivity(staffAttRegIntent);
                break;
            case R.id.ll_calendar:
//                startActivity(new Intent(getActivity(), CalendarAndEvents.class));
                break;
            case R.id.ll_gallery:
//                startActivity(new Intent(getActivity(), GalleryView.class));
                break;
            case R.id.ll_messg:
                startActivity(new Intent(getActivity(), AdminMessages.class));
                break;
            case R.id.set:
//                startActivity(new Intent(getActivity(),InstituteSettings.class));
                break;
            case R.id.ll_circular:
//                startActivity(new Intent(getActivity(),Circulars.class));
                break;
        }
    }
}
