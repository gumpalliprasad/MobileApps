package myschoolapp.com.gsnedutech.QBox;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminClassSubGroup;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.SectionTimeTableObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSection;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherClassView;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;

public class TeacherQBoxClsSec extends AppCompatActivity implements  NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = "SriRam -" + TeacherClassView.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    TeacherObj teacherObj;
    AdminObj adminObj;

    List<AdminClassSubGroup> adminClassSubGroups = new ArrayList<>();

    @BindView(R.id.rv_subjects)
    RecyclerView rvSubjects;
    @BindView(R.id.tv_title)
    TextView tvTitle;



    HashMap<String, SectionTimeTableObj> map = new HashMap<>();


    List<TeacherCCSSSubject> subjectList = new ArrayList<>();
    TeacherCCSSSection teacherCCSSObj;
    String courseName="", courseId="", className = "", classId="";
    boolean isAdmin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_qbox_clss_sec);
        ButterKnife.bind(this);
        init();
        courseName = getIntent().getStringExtra("courseName");
        courseId  = getIntent().getStringExtra("courseId");
        className = getIntent().getStringExtra("className");
        classId = getIntent().getStringExtra("classId");
        if (isAdmin){
            teacherCCSSObj = new TeacherCCSSSection();
            teacherCCSSObj.setSectionId(getIntent().getStringExtra("sectionId"));
            teacherCCSSObj.setSectionName(getIntent().getStringExtra("sectionName"));
            tvTitle.setText(courseName+" / "+className+" / "+teacherCCSSObj.getSectionName());
        }
        else {
            teacherCCSSObj = (TeacherCCSSSection) getIntent().getSerializableExtra("teacherCCSSObj");
            tvTitle.setText(courseName+" / "+className+" / "+teacherCCSSObj.getSectionName());
            subjectList = teacherCCSSObj.getSubjects();

            rvSubjects.setLayoutManager(new GridLayoutManager(TeacherQBoxClsSec.this,3));
            rvSubjects.setAdapter(new SubjectsAdapter());

        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherQBoxClsSec.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    public void init(){

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



    }



    class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {


        SubjectsAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherQBoxClsSec.this).inflate(R.layout.item_course_teacher, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSub.setText(subjectList.get(position).getSubjectName());
            if(subjectList.get(position).getSubjectName().contains("Maths"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_maths);
            if(subjectList.get(position).getSubjectName().contains("Science"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_science);
            if(subjectList.get(position).getSubjectName().contains("English"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_english);
            if(subjectList.get(position).getSubjectName().contains("GeneralKnowledge"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_gk);
            if(subjectList.get(position).getSubjectName().contains("SocialScience"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_social);
            if(subjectList.get(position).getSubjectName().contains("Hindi"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_hindi);
            if(subjectList.get(position).getSubjectName().contains("Telugu"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_telugu);
            if(subjectList.get(position).getSubjectName().contains("Physics"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_physics);
            if(subjectList.get(position).getSubjectName().contains("Chemistry"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_chemistry);
            if(subjectList.get(position).getSubjectName().contains("Biology"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_biology);
            if(subjectList.get(position).getSubjectName().contains("Zoology"))
                holder.ivSub.setImageResource(R.drawable.ic_courses_zoology);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherQBoxClsSec.this, TeacherQBoxQuestions.class);
//                    intent.putExtra("courseId", courseId);
//                    intent.putExtra("courseName", courseName);
//                    intent.putExtra("classId", classId);
//                    intent.putExtra("className", className);
//                    intent.putExtra("pos", position);
//                    intent.putExtra("sectionId", teacherCCSSObj.getSectionId());
//                    intent.putExtra("sectionName", teacherCCSSObj.getSectionName());
                    intent.putExtra("subjectId",subjectList.get(position).getSubjectId()+"");
//                    intent.putExtra("elective",subjectList.get(position).getIsElective()+"");
//                    intent.putExtra("subjects", (Serializable) subjectList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return subjectList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivSub;
            TextView tvSub;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivSub  = itemView.findViewById(R.id.iv_sub_image);
                tvSub = itemView.findViewById(R.id.tv_sub);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}