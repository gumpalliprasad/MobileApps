package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSClass;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSection;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherCourseActivity extends AppCompatActivity {
    private static final String TAG = TeacherChatActivity.class.getName();

    @BindView(R.id.rv_course)
    RecyclerView rvCourse;

    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj teacherObj;

    MyUtils utils = new MyUtils();

    List<TeacherCCSSObj> teacherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_course);

        ButterKnife.bind(this);

        init();
    }

    void init(){

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);
        if (NetworkConnectivity.isConnected(this)) {
            getTeacherCourses();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }

    void getTeacherCourses(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + teacherObj.getUserId())
                .build();

        utils.showLog(TAG, new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + teacherObj.getUserId());

        client.newCall(get).enqueue(new Callback() {
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
                String resp = response.body().string();
                utils.showLog(TAG, "response " + resp);

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("userClassSubjects");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherCCSSObj>>() {
                            }.getType();

                            teacherList.clear();
                            teacherList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "TeacherList Size " + teacherList.size());
                            if (teacherList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();
                                        rvCourse.setLayoutManager(new LinearLayoutManager(TeacherCourseActivity.this));
                                        rvCourse.setAdapter(new CourseAdapter(teacherList));
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder>{

        List<TeacherCCSSObj> teacherCoursesList;

        public CourseAdapter(List<TeacherCCSSObj> teacherCoursesList) {
            this.teacherCoursesList = teacherCoursesList;
        }

        @NonNull
        @Override
        public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherCourseActivity.this).inflate(R.layout.item_courses_teacher,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
            holder.tvCourseName.setText(teacherCoursesList.get(position).getCourseName());

            List<TeacherCCSSClass> listClasses = new ArrayList<>();
            listClasses.clear();
            listClasses.addAll(teacherCoursesList.get(position).getClasses());

            holder.spClasses.setVisibility(View.VISIBLE);
            ArrayAdapter<TeacherCCSSClass> adapter =
                    new ArrayAdapter<TeacherCCSSClass>(TeacherCourseActivity.this,  android.R.layout.simple_spinner_dropdown_item, listClasses);
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

            holder.spClasses.setAdapter(adapter);
            
            holder.spClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    List<TeacherCCSSSection> listSections = new ArrayList<>();
                    listSections.clear();
                    listSections.addAll(listClasses.get(i).getSections());
                    holder.rvSubjects.setLayoutManager(new LinearLayoutManager(TeacherCourseActivity.this));
                    holder.rvSubjects.setAdapter(new SectionSubAdapter(listSections));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return teacherCoursesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCourseName;
            Spinner spClasses;
            RecyclerView rvSubjects;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCourseName = itemView.findViewById(R.id.tv_course_name);
                spClasses = itemView.findViewById(R.id.sp_classes);
                rvSubjects = itemView.findViewById(R.id.rv_subject);

            }
        }
    }
    
    class SectionSubAdapter extends RecyclerView.Adapter<SectionSubAdapter.ViewHolder>{
        
        List<TeacherCCSSSection> listSections;
        
        public SectionSubAdapter(List<TeacherCCSSSection> listSections) {
            this.listSections = listSections;
        }

        @NonNull
        @Override
        public SectionSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherCourseActivity.this).inflate(R.layout.item_section_sub,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SectionSubAdapter.ViewHolder holder, int position) {
//            holder.tvSectionName.setText(listSections.get(position).getSectionName());
            holder.rvSubs.setLayoutManager(new GridLayoutManager(TeacherCourseActivity.this,3));
            holder.rvSubs.setAdapter(new SubjectAdapter(listSections.get(position).getSubjects()));
        }

        @Override
        public int getItemCount() {
            return listSections.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
//            TextView tvSectionName;
            RecyclerView rvSubs;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                
//                tvSectionName = itemView.findViewById(R.id.tv_section_name);
                rvSubs = itemView.findViewById(R.id.rv_subs);
            }
        }
    }
    
    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{

        List<TeacherCCSSSubject> subjectList;
        
        public SubjectAdapter(List<TeacherCCSSSubject> subjectList) {
            this.subjectList = subjectList;
        }

        @NonNull
        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherCourseActivity.this).inflate(R.layout.item_course_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {
            
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
                    
               
            
            
            holder.tvSub.setText(subjectList.get(position).getSubjectName());
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

                ivSub = itemView.findViewById(R.id.iv_sub_image);
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