package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSClass;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSection;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherClassSections;
import myschoolapp.com.gsnedutech.TeacherHomeNew;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class TeacherBottomCourse extends Fragment {
    private static final String TAG = "SriRam -" + TeacherBottomCourse.class.getName();

    View teacherBottomCourseView;


    MyUtils utils = new MyUtils();
    Activity mActivity;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    View TeacherBottomCourseView;

    List<TeacherCCSSObj> teacherList = new ArrayList<>();

    @BindView(R.id.sp_courses)
    Spinner spCourses;
    @BindView(R.id.rv_courses)
    RecyclerView rvCourses;

    @BindView(R.id.tv_no_classes)
    TextView tvNoClasses;
    @BindView(R.id.ll_my_classes)
    LinearLayout llMYClasses;


    public TeacherBottomCourse() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        teacherBottomCourseView = inflater.inflate(R.layout.fragment_teacher_bottom_course, container, false);
        unbinder = ButterKnife.bind(this, teacherBottomCourseView);

        init();

        if (NetworkConnectivity.isConnected((TeacherHomeNew) mActivity)) {
            getTeacherCourses();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }

        return teacherBottomCourseView;
    }

    void init() {
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);


    }


    void getTeacherCourses() {
        utils.showLoader(getActivity());
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request get = new Request.Builder()
                .url(new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId())
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();
        utils.showLog(TAG, new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId());
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        llMYClasses.setVisibility(View.GONE);
                        tvNoClasses.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();

                utils.showLog(TAG, "response " + resp);
                mActivity.runOnUiThread(() -> {
                    utils.dismissDialog();
                });
                if (response.body() != null) {
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("userClassSubjects");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherCCSSObj>>() {
                            }.getType();
                            teacherList.clear();
                            teacherList.addAll(gson.fromJson(jsonArr.toString(), type));
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<TeacherCCSSObj> adapter =
                                            new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, teacherList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spCourses.setAdapter(adapter);
                                    spCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            rvCourses.setLayoutManager(new LinearLayoutManager(mActivity));
                                            if (teacherList.get(i).getClasses().size()>0){
                                                llMYClasses.setVisibility(View.VISIBLE);
                                                tvNoClasses.setVisibility(View.GONE);
                                                rvCourses.setAdapter(new TeacherBottomCourse.CouseAdapter(teacherList.get(i).getClasses()));
                                            }
                                            else {
                                                llMYClasses.setVisibility(View.GONE);
                                                tvNoClasses.setVisibility(View.VISIBLE);
                                            }

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                }
                            });

                            Log.v(TAG, "TeacherList Size " + teacherList.size());
                            if (teacherList.size() > 0) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        getTodoList();
                                    }
                                });
                            }
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                        }
                        else {
                            mActivity.runOnUiThread(() -> {
                                llMYClasses.setVisibility(View.GONE);
                                tvNoClasses.setVisibility(View.VISIBLE);
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                        TODO: Show an Alert
                            llMYClasses.setVisibility(View.GONE);
                            tvNoClasses.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    class CouseAdapter extends RecyclerView.Adapter<TeacherBottomCourse.CouseAdapter.ViewHolder> {

        List<TeacherCCSSClass> teacherCObjs;

        public CouseAdapter(List<TeacherCCSSClass> teacherClasses) {
            teacherCObjs = teacherClasses;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public TeacherBottomCourse.CouseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TeacherBottomCourse.CouseAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_teacher_course_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TeacherBottomCourse.CouseAdapter.ViewHolder holder, int position) {
            holder.tvClassName.setText(teacherCObjs.get(position).toString());
            holder.rvSections.setLayoutManager(new LinearLayoutManager(mActivity));
            holder.rvSections.setAdapter(new TeacherBottomCourse.ClassSectionAdapter(teacherCObjs.get(position).getClassName(),
                    teacherCObjs.get(position).getClassId()
                    , teacherCObjs.get(position).getSections()));
            holder.rvSections.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));

        }

        @Override
        public int getItemCount() {
            return teacherCObjs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView rvSections;
            TextView tvClassName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                rvSections = itemView.findViewById(R.id.rv_sections);
                tvClassName = itemView.findViewById(R.id.tv_className);
            }
        }
    }

    class ClassSectionAdapter extends RecyclerView.Adapter<TeacherBottomCourse.ClassSectionAdapter.ViewHolder> {

        List<TeacherCCSSSection> teacherCCSSSections;
        String className;
        String classId;

        public ClassSectionAdapter(String className, String classId, List<TeacherCCSSSection> sections) {
            teacherCCSSSections = sections;
            this.className = className;
            this.classId = classId;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public TeacherBottomCourse.ClassSectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TeacherBottomCourse.ClassSectionAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_teacher_csection_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TeacherBottomCourse.ClassSectionAdapter.ViewHolder holder, int position) {
            holder.tvSection.setText(className + " - " + teacherCCSSSections.get(position).getSectionName());
            String subs = "";
            for (TeacherCCSSSubject tsub : teacherCCSSSections.get(position).getSubjects()) {
                if (subs.isEmpty()) {
                    subs = tsub.getSubjectName();
                } else {
                    subs = subs + "," + tsub.getSubjectName();
                }
            }
            holder.tvSubjects.setText(subs);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, TeacherClassSections.class);
                    intent.putExtra("courseName", teacherList.get(spCourses.getSelectedItemPosition()).getCourseName());
                    intent.putExtra("courseId", teacherList.get(spCourses.getSelectedItemPosition()).getCourseId());
                    intent.putExtra("className", className);
                    intent.putExtra("classId", classId);
                    intent.putExtra("teacherCCSSObj", teacherCCSSSections.get(position));
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });


        }

        @Override
        public int getItemCount() {
            return teacherCCSSSections.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubjects, tvSection;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubjects = itemView.findViewById(R.id.tv_subjects);
                tvSection = itemView.findViewById(R.id.tv_section);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

}
