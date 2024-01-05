package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.LiveExamDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentOnlineTestActivity;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;

import static android.content.Context.MODE_PRIVATE;

public class LiveExamDeatilsFrag extends Fragment {

    private static final String TAG = "SriRam -" + LiveExamDeatilsFrag.class.getName();

    View liveExamDeatilsFragView;
    Unbinder unbinder;

    StudentOnlineTestObj liveExam;
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_test_name)
    TextView tvTestName;
    @BindView(R.id.tv_test_category)
    TextView tvTestCategory;
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;

    Activity mActivity;

    StudentObj sObj;

    SharedPreferences sh_Pref;


    public LiveExamDeatilsFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        liveExamDeatilsFragView =  inflater.inflate(R.layout.fragment_live_exam_deatils, container, false);
        unbinder = ButterKnife.bind(this, liveExamDeatilsFragView);

        init();

        liveExamDeatilsFragView.findViewById(R.id.btn_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!liveExam.getMjeeSectionTemplateName().equalsIgnoreCase("NA")) {
//                    Intent onlineTestIntent = new Intent(getActivity(), JEETestMarksDivision.class);
//                    onlineTestIntent.putExtra("studentId", sObj.getStudentId());
//                    onlineTestIntent.putExtra("live", (Serializable) liveExam);
//                    startActivity(onlineTestIntent);
//                    getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    ((LiveExamDetails)getActivity()).loadFragment(new LiveExamMarksDivision());
                } else {
                    Intent onlineTestIntent = new Intent(getActivity(), StudentOnlineTestActivity.class);
                    onlineTestIntent.putExtra("studentId", sObj.getStudentId());
                    onlineTestIntent.putExtra("live", (Serializable) liveExam);
                    startActivity(onlineTestIntent);
                    getActivity().overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    getActivity().finish();
                }
            }
        });

        return liveExamDeatilsFragView;
    }

    void init() {

        Gson gson = new Gson();
        String json = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        liveExam = (StudentOnlineTestObj)getActivity().getIntent().getSerializableExtra("live");

        tvTestName.setText(liveExam.getTestName());
        tvTestCategory.setText(liveExam.getTestCategoryName());
        try {
            tvStartDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestStartDate())));
            tvStartTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestStartDate())));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("student_loggedin",false)){
            liveExamDeatilsFragView.findViewById(R.id.btn_join).setVisibility(View.VISIBLE);
        }else{
            liveExamDeatilsFragView.findViewById(R.id.btn_join).setVisibility(View.GONE);
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