package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminClassSubGroup;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.SectionTTPeriods;
import myschoolapp.com.gsnedutech.Models.SectionTimeTableObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSection;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherClassSections extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {
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

    @BindView(R.id.tv_sun)
    TextView tvSun;
    @BindView(R.id.tv_mon)
    TextView tvMon;
    @BindView(R.id.tv_tue)
    TextView tvTue;
    @BindView(R.id.tv_wed)
    TextView tvWed;
    @BindView(R.id.tv_thu)
    TextView tvThu;
    @BindView(R.id.tv_fri)
    TextView tvFri;
    @BindView(R.id.tv_sat)
    TextView tvSat;
    TextView[] days;

    @BindView(R.id.ll_attendance)
    LinearLayout llAttendance;

    @BindView(R.id.img_students)
    ImageView imgStudents;

    @BindView(R.id.rv_timetable)
    RecyclerView rvTimeTable;

    List<SectionTimeTableObj> timetableList = new ArrayList<>();
    HashMap<String, SectionTimeTableObj> map = new HashMap<>();


    List<TeacherCCSSSubject> subjectList = new ArrayList<>();
    TeacherCCSSSection teacherCCSSObj;
    String courseName="", courseId="", className = "", classId="";
    boolean isAdmin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_sections);
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

            rvSubjects.setLayoutManager(new GridLayoutManager(TeacherClassSections.this,3));
            rvSubjects.setAdapter(new SubjectsAdapter());

        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



//        timetableTablayout.addOnTabSelectedListener(this);

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
            utils.alertDialog(1, TeacherClassSections.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (isAdmin){
                    getAdminClassSubjects(courseId,classId);
                }
                else {
                    getSectionTimeTable(teacherCCSSObj.getSectionId());
                }
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
        days = new TextView[]{tvSun, tvMon, tvTue, tvWed, tvThu, tvFri, tvSat};
        for (TextView tvday : days){
            tvday.setOnClickListener(this);
        }
        rvTimeTable.setLayoutManager(new LinearLayoutManager(TeacherClassSections.this));

        llAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent attregIntent = new Intent(TeacherClassSections.this, TeacherSectionAttendance.class);
                if (isAdmin)
                    attregIntent.putExtra("userId", "" + adminObj.getUserId());
                else attregIntent.putExtra("userId", "" + teacherObj.getUserId());
                if (isAdmin)
                    attregIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId", "0"));
                else attregIntent.putExtra("branchId", "" + teacherObj.getBranchId());
                attregIntent.putExtra("sectionId", teacherCCSSObj.getSectionId());
                attregIntent.putExtra("className", className);
                attregIntent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                attregIntent.putExtra("classId", getIntent().getStringExtra("classId"));
                attregIntent.putExtra("secName", teacherCCSSObj.getSectionName());
                startActivity(attregIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        imgStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stndIntent = new Intent(TeacherClassSections.this, TeacherClassStudents.class);
                if (isAdmin)
                    stndIntent.putExtra("branchId", "" + sh_Pref.getString("admin_branchId", "0"));
                else stndIntent.putExtra("branchId", "" + teacherObj.getBranchId());
                stndIntent.putExtra("sectionId", teacherCCSSObj.getSectionId());
                stndIntent.putExtra("courseId",courseId);
                stndIntent.putExtra("courseName", courseName);
                stndIntent.putExtra("className", className);
                stndIntent.putExtra("classId", classId);
                stndIntent.putExtra("title", tvTitle.getText().toString());
                startActivity(stndIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
    }

    private void getSectionTimeTable(String sectionId) {
        utils.showLoader(TeacherClassSections.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetSectionTimeTable +"schemaName=" +sh_Pref.getString("schema","")+ "&sectionId=" + sectionId);


        Request request = new Request.Builder()
                .url(AppUrls.GetSectionTimeTable +"schemaName=" +sh_Pref.getString("schema","")+ "&sectionId=" + sectionId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Month Wise Attendance analysis- " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("sectionTimeTable");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<SectionTimeTableObj>>() {
                                    }.getType();

                                    timetableList.clear();
                                    timetableList.addAll(gson.fromJson(jsonArr.toString(), type));

                                    displayTimeTabletabs();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
    }


    private void displayTimeTabletabs() {
        if (timetableList.size() > 0) {
            map.clear();
            for (int i = 0; i < timetableList.size(); i++) {
                map.put(timetableList.get(i).getDayName(), timetableList.get(i));
            }
        }
        unselectAll(days);
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    selectDay(tvSun);
                    break;
                case Calendar.MONDAY:
                    selectDay(tvMon);
                    break;
                case Calendar.TUESDAY:
                    selectDay(tvTue);
                    break;
                case Calendar.WEDNESDAY:
                    selectDay(tvWed);
                    break;
                case Calendar.THURSDAY:
                    selectDay(tvThu);
                    break;
                case Calendar.FRIDAY:
                    selectDay(tvFri);
                    break;
                case Calendar.SATURDAY:
                    selectDay(tvSat);
                    break;
            }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_sun:
                unselectAll(days);
                selectDay(tvSun);
                break;
            case R.id.tv_mon:
                unselectAll(days);
                selectDay(tvMon);
                break;
            case R.id.tv_tue:
                unselectAll(days);
                selectDay(tvTue);
                break;
            case R.id.tv_wed:
                unselectAll(days);
                selectDay(tvWed);
                break;
            case R.id.tv_thu:
                unselectAll(days);
                selectDay(tvThu);
                break;
            case R.id.tv_fri:
                unselectAll(days);
                selectDay(tvFri);
                break;
            case R.id.tv_sat:
                unselectAll(days);
                selectDay(tvSat);
                break;
        }
    }

    private void selectDay(TextView tvDay) {
        tvDay.setBackgroundResource(R.drawable.bg_grad_tab_select);
        tvDay.setTextColor(Color.WHITE);
        SectionTimeTableObj sectionTimeTableObj = map.get(tvDay.getText().toString().trim());
        if (sectionTimeTableObj!=null){
            if (sectionTimeTableObj.getPeriods().size()>0)
                rvTimeTable.setAdapter(new TimeTableAdapter(sectionTimeTableObj.getPeriods()));
        }
    }

    private void unselectAll(TextView[] days) {
        for (TextView day : days){
            day.setBackground(null);
            day.setTextColor(Color.rgb(73, 73, 73));
            day.setAlpha(0.75f);
        }
    }


    class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {

        List<SectionTTPeriods> secTimeTable;


        TimeTableAdapter(List<SectionTTPeriods> periods) {
            secTimeTable = periods;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public TimeTableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TimeTableAdapter.ViewHolder(LayoutInflater.from(TeacherClassSections.this).inflate(R.layout.item_time_table, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TimeTableAdapter.ViewHolder holder, int position) {
            holder.tvSub.setText(secTimeTable.get(position).getPeriodName());
            if (secTimeTable.get(position).getPeriodName().contains("Break"))
                holder.tvTeacher.setVisibility(View.GONE);
            else holder.tvTeacher.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm aa");
            try {
                Date dt = sdf.parse(secTimeTable.get(position).getPeriodStartTime());
                holder.tvTime.setText(sdf1.format(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (secTimeTable.get(position).getSubjectTeacher()!=null && secTimeTable.get(position).getSubjectTeacher().size()>0) {
                holder.tvTeacher.setText(secTimeTable.get(position).getSubjectTeacher().get(0).getTeacherName());
                holder.tvTeacher.setVisibility(View.VISIBLE);
            }
            else holder.tvTeacher.setVisibility(View.GONE);

            if(secTimeTable.get(position).getPeriodName().contains("Maths") || secTimeTable.get(position).getPeriodName().contains("MATHS"))
                holder.ivSub.setImageResource(R.drawable.ic_maths_icon_white);
            if(secTimeTable.get(position).getPeriodName().contains("Science") || secTimeTable.get(position).getPeriodName().contains("SCIENCE"))
                holder.ivSub.setImageResource(R.drawable.ic_science_white);
            if(secTimeTable.get(position).getPeriodName().contains("English") || secTimeTable.get(position).getPeriodName().contains("ENGLISH"))
                holder.ivSub.setImageResource(R.drawable.ic_english_white);
            if(secTimeTable.get(position).getPeriodName().contains("General") || secTimeTable.get(position).getPeriodName().contains("GENERAL"))
                holder.ivSub.setImageResource(R.drawable.ic_gk_white);
            if(secTimeTable.get(position).getPeriodName().contains("Social") || secTimeTable.get(position).getPeriodName().contains("SOCIAL"))
                holder.ivSub.setImageResource(R.drawable.ic_social_white);
            if(secTimeTable.get(position).getPeriodName().contains("Hindi") || secTimeTable.get(position).getPeriodName().contains("HINDI"))
                holder.ivSub.setImageResource(R.drawable.ic_hindi_white);
            if(secTimeTable.get(position).getPeriodName().contains("Telugu") || secTimeTable.get(position).getPeriodName().contains("TELUGU"))
                holder.ivSub.setImageResource(R.drawable.ic_telugu_white);
            if(secTimeTable.get(position).getPeriodName().contains("Physics") || secTimeTable.get(position).getPeriodName().contains("PHYSICS"))
                holder.ivSub.setImageResource(R.drawable.ic_physics_white);
            if(secTimeTable.get(position).getPeriodName().contains("Chemistry") || secTimeTable.get(position).getPeriodName().contains("CHEMISTRY"))
                holder.ivSub.setImageResource(R.drawable.ic_chemistry_white);
            if(secTimeTable.get(position).getPeriodName().contains("Biology") || secTimeTable.get(position).getPeriodName().contains("BIOLOGY"))
                holder.ivSub.setImageResource(R.drawable.ic_biology_white);
            if(secTimeTable.get(position).getPeriodName().contains("Zoology") || secTimeTable.get(position).getPeriodName().contains("ZOOLOGY"))
                holder.ivSub.setImageResource(R.drawable.ic_zoology_white);

        }

        @Override
        public int getItemCount() {
            return secTimeTable.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivSub;
            TextView tvSub, tvTime, tvTeacher;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivSub  = itemView.findViewById(R.id.img_sub);
                tvSub = itemView.findViewById(R.id.tv_sub);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvTeacher = itemView.findViewById(R.id.tv_teacher);

            }
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
        public SubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubjectsAdapter.ViewHolder(LayoutInflater.from(TeacherClassSections.this).inflate(R.layout.item_course_teacher, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectsAdapter.ViewHolder holder, int position) {
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
            if(subjectList.get(position).getSubjectName().contains("Vedic Mathematics"))
                holder.ivSub.setImageResource(R.drawable.ic_vedic_maths);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherClassSections.this, TeacherClassView.class);
                    intent.putExtra("courseId", courseId);
                    intent.putExtra("courseName", courseName);
                    intent.putExtra("classId", classId);
                    intent.putExtra("className", className);
                    intent.putExtra("pos", position);
                    intent.putExtra("sectionId", teacherCCSSObj.getSectionId());
                    intent.putExtra("sectionName", teacherCCSSObj.getSectionName());
                    intent.putExtra("subjectId",subjectList.get(position).getSubjectId()+"");
                    intent.putExtra("elective",subjectList.get(position).getIsElective()+"");
                    intent.putExtra("subjects", (Serializable) subjectList);
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

    private void getAdminClassSubjects(String... strings){
        utils.showLoader(TeacherClassSections.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        Log.v(TAG, "Admin ClassCourses Sections request - " + new AppUrls().getAllCourseClassSubjects +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + strings[0] + "&classId=" + strings[1]);

        Request request = new Request.Builder()
                .url(new AppUrls().getAllCourseClassSubjects +"schemaName=" +sh_Pref.getString("schema","")+ "&courseId=" + strings[0] + "&classId=" + strings[1])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Admin ClassCourses Sections responce - " + jsonResp);
                    runOnUiThread(() -> {
                        try {
                            JSONObject ParentjObject = new JSONObject(jsonResp);
                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                JSONArray jsonArr = ParentjObject.getJSONArray("Subjects");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<AdminClassSubGroup>>() {
                                }.getType();

                                adminClassSubGroups.clear();
                                adminClassSubGroups.addAll(gson.fromJson(jsonArr.toString(), type));

                                Log.v(TAG, "adminClassSubGroups size - " + adminClassSubGroups.size());
                                Log.v(TAG, "adminClassSubGroups subj size - " + adminClassSubGroups.get(0).getSubjects().size());

                                subjectList.clear();
                                for (int i = 0; i < adminClassSubGroups.size(); i++) {
                                    for (int j = 0; j < adminClassSubGroups.get(i).getSubjects().size(); j++) {
                                        TeacherCCSSSubject teacherCCSSSubject = new TeacherCCSSSubject();
                                        teacherCCSSSubject.setElectiveGroupId(adminClassSubGroups.get(i).getElectiveGroupId());
                                        teacherCCSSSubject.setElectiveName(adminClassSubGroups.get(i).getElectiveGroupName());
                                        teacherCCSSSubject.setIsElective(adminClassSubGroups.get(i).getIsElective());
                                        teacherCCSSSubject.setSubjectId(adminClassSubGroups.get(i).getSubjects().get(j).getSubjectId());
                                        teacherCCSSSubject.setSubjectName(adminClassSubGroups.get(i).getSubjects().get(j).getSubjectName());
                                        teacherCCSSSubject.setContentType(adminClassSubGroups.get(i).getSubjects().get(j).getContentType());
                                        subjectList.add(teacherCCSSSubject);
                                    }
                                }
                                teacherCCSSObj.setSubjects(subjectList);
                                if (subjectList.size()>0) {
                                    rvSubjects.setLayoutManager(new GridLayoutManager(TeacherClassSections.this, 3));
                                    rvSubjects.setAdapter(new SubjectsAdapter());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                runOnUiThread(() -> {
                    utils.dismissDialog();
                    getSectionTimeTable(teacherCCSSObj.getSectionId());
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}