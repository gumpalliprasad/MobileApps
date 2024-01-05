package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.descriptive.TeacherDesExams;

public class TeacherClassView extends AppCompatActivity {

    private static final String TAG = "SriRam -" + TeacherClassView.class.getName();

    SharedPreferences sh_Pref;
    TeacherObj teacherObj;
    AdminObj adminObj;
    boolean isAdmin = false;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.sp_subjects)
    Spinner spSubjects;





    @BindView(R.id.tv_take_attendance)
    TextView tvTakeAttendance;

    List<TeacherCCSSSubject> subjectList = new ArrayList<>();

    String courseName="", courseId="", className = "", classId="", sectionName="", sectionId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_view);
        ButterKnife.bind(this);
        init();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.ll_hw).setOnClickListener(view -> {

            Intent intent = new Intent(TeacherClassView.this, TeacherHwAssignments.class);
            intent.putExtra("sectionId", getIntent().getStringExtra("sectionId"));
            intent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
            intent.putExtra("courseId", "" + courseId);
            intent.putExtra("elective", "" + subjectList.get(spSubjects.getSelectedItemPosition()).getIsElective());
            intent.putExtra("className", className);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

//                startActivity(new Intent(TeacherClassView.this,TeacherHwAssignments.class));
//                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        findViewById(R.id.ll_chapters).setOnClickListener(view -> {
            Intent intent = new Intent(TeacherClassView.this, TeacherCourseSubChapListing.class);
            intent.putExtra("classId", "" + classId);
            intent.putExtra("className", className);
            intent.putExtra("courseId", "" + courseId);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("subjectId", subjectList.get(spSubjects.getSelectedItemPosition()).getSubjectId());
            intent.putExtra("Subjects", (Serializable) subjectList);
            intent.putExtra("position", "" + spSubjects.getSelectedItemPosition());
            intent.putExtra("contentType", subjectList.get(spSubjects.getSelectedItemPosition()).getContentType()
            );
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });


        findViewById(R.id.ll_live_classes).setOnClickListener(view -> {
            Intent intent = new Intent(TeacherClassView.this, TeacherClassLiveRecordedClasses.class);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("subjectId", subjectList.get(spSubjects.getSelectedItemPosition()).getSubjectId());
            intent.putExtra("Subjects", (Serializable) subjectList);
            intent.putExtra("position", "" + spSubjects.getSelectedItemPosition());
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        findViewById(R.id.ll_live_exams).setOnClickListener(view -> {
            Intent intent = new Intent(TeacherClassView.this, TeacherClassLivePreviousExams.class);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("subjectId", subjectList.get(spSubjects.getSelectedItemPosition()).getSubjectId());
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        findViewById(R.id.ll_desc_exams).setOnClickListener(v -> {
            Intent intent = new Intent(TeacherClassView.this, TeacherDesExams.class);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("subjectId", subjectList.get(spSubjects.getSelectedItemPosition()).getSubjectId());
            startActivity(intent);
        });
    }

    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            isAdmin = false;
            String json = sh_Pref.getString("teacherObj", "");
            teacherObj = gson.fromJson(json, TeacherObj.class);
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            isAdmin = true;
            String json = sh_Pref.getString("adminObj", "");
            adminObj = gson.fromJson(json, AdminObj.class);
        }

        courseName = getIntent().getStringExtra("courseName");
        courseId  = getIntent().getStringExtra("courseId");
        className = getIntent().getStringExtra("className");
        classId = getIntent().getStringExtra("classId");
        sectionId = getIntent().getStringExtra("sectionId");
        sectionName = getIntent().getStringExtra("sectionName");

//        Log.v(TAG, "branchId - " + teacherObj.getBranchId() + " SectionId - " + getIntent().getStringExtra("sectionId"));

        tvTitle.setText(courseName+" / "+className+" / "+sectionName);

        subjectList.addAll((List<TeacherCCSSSubject>) getIntent().getSerializableExtra("subjects"));

        tvTakeAttendance.setOnClickListener(view -> {
            Intent attIntent = new Intent(this, TeacherStdnTakeAttendance.class);
            if (isAdmin)
                attIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId", "0"));
            else  attIntent.putExtra("branchId", teacherObj.getBranchId());
            if (isAdmin)
                attIntent.putExtra("userId", "" + adminObj.getUserId());
            else attIntent.putExtra("userId", "" + teacherObj.getUserId());
            attIntent.putExtra("sectionId", getIntent().getStringExtra("sectionId"));
            attIntent.putExtra("courseId", "" + getIntent().getStringExtra("courseId"));
            attIntent.putExtra("classId", "" + getIntent().getStringExtra("classId"));
            startActivity(attIntent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        ArrayAdapter<TeacherCCSSSubject> adapter =
                new ArrayAdapter<>(TeacherClassView.this, android.R.layout.simple_spinner_dropdown_item, subjectList);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spSubjects.setAdapter(adapter);
        spSubjects.setSelection(getIntent().getIntExtra("pos",0));
        spSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                rvCourses.setLayoutManager(new LinearLayoutManager(mActivity));
//                rvCourses.setAdapter(new TeacherHomeFrag.CouseAdapter(teacherList.get(i).getClasses()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}