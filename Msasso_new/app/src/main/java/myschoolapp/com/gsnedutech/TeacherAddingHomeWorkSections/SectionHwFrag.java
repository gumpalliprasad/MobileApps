package myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSClass;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSObj;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSection;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSSubject;
import myschoolapp.com.gsnedutech.Models.TeacherHwStudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections.Model.HwAssign;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SectionHwFrag extends Fragment {

    private static final String TAG = "SriRam -" + SectionHwFrag.class.getName();

    View viewSectionHwFrag;
    Unbinder unbinder;

    Activity mActivity;

    @BindView(R.id.rv_assigned)
    RecyclerView rvAssigned;
    @BindView(R.id.rv_can_assigned)
    RecyclerView rvCanAssign;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    TeacherObj tObj;

    List<HwAssign> listAssignedSections = new ArrayList<>();
    List<HwAssign> listCanAssign = new ArrayList<>();

    List<TeacherCCSSObj> teacherList = new ArrayList<>();

    AssignedSectionAdapter assignedSectionAdapter;
    CanAssignAdapter canAssignAdapter;

//    List<TeacherHwStudentObj> studList = new ArrayList<>();

    public SectionHwFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewSectionHwFrag = inflater.inflate(R.layout.fragment_section_hw, container, false);
        unbinder = ButterKnife.bind(this,viewSectionHwFrag);

        init();

        return viewSectionHwFrag;
    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        viewSectionHwFrag.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TeacherHWAddingWithSections)mActivity).loadFrag(1);
            }
        });



        if (((TeacherHWAddingWithSections)mActivity).newHwObj.getListAssigned().size()==0){
            if (NetworkConnectivity.isConnected(mActivity)) {
                getTeacherCourses();
            } else {
                new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                        getString(R.string.action_settings), getString(R.string.action_close),false);
            }
        }else{
            listAssignedSections.addAll(((TeacherHWAddingWithSections)mActivity).newHwObj.getListAssigned());
            listCanAssign.addAll(((TeacherHWAddingWithSections)mActivity).newHwObj.getListCanAssign());
            rvAssigned.setLayoutManager(new LinearLayoutManager(mActivity));
            assignedSectionAdapter = new AssignedSectionAdapter(listAssignedSections);
            rvAssigned.setAdapter(assignedSectionAdapter);
            rvCanAssign.setLayoutManager(new LinearLayoutManager(mActivity));
            canAssignAdapter = new CanAssignAdapter(listCanAssign);
            rvCanAssign.setAdapter(canAssignAdapter);


        }


        viewSectionHwFrag.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TeacherHWAddingWithSections)mActivity).newHwObj.setListAssigned(listAssignedSections);
                ((TeacherHWAddingWithSections)mActivity).startUpload();
            }
        });

    }


    void getTeacherCourses() {
        utils.showLoader(mActivity);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId())
                .build();

        utils.showLog(TAG, new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema", "") + "&userId=" + tObj.getUserId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();


                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);

                        JSONArray jsonArr = ParentjObject.getJSONArray("userClassSubjects");
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<TeacherCCSSObj>>() {
                        }.getType();

                        teacherList.clear();
                        teacherList.addAll(gson.fromJson(jsonArr.toString(), type));

                        for (int i=0;i<teacherList.size();i++){
                            if (teacherList.get(i).getCourseId().equalsIgnoreCase(((TeacherHWAddingWithSections)mActivity).courseId)){
                                List<TeacherCCSSClass> listClasses = teacherList.get(i).getClasses();
                                for (int j=0;j<listClasses.size();j++){
                                    List<TeacherCCSSSection> listSection = listClasses.get(j).getSections();
                                    for (int k=0;k<listSection.size();k++){
                                        List<TeacherCCSSSubject> listSubject = listSection.get(k).getSubjects();

                                        if (listSection.get(k).getSectionId().equalsIgnoreCase(((TeacherHWAddingWithSections)mActivity).newHwObj.getSectionId())){
                                            for(int l=0;l<listSubject.size();l++){
                                                if (listSubject.get(l).getSubjectId().equalsIgnoreCase(((TeacherHWAddingWithSections)mActivity).newHwObj.getSubjectId())){
                                                    HwAssign hwAssign = new HwAssign();

                                                    hwAssign.setClassId(listClasses.get(j).getClassId());
                                                    hwAssign.setClassName(listClasses.get(j).getClassName());

                                                    hwAssign.setSectionId(listSection.get(k).getSectionId());
                                                    hwAssign.setSectionName(listSection.get(k).getSectionName());

                                                    hwAssign.setSubjectId(listSubject.get(l).getSubjectId());
                                                    hwAssign.setSubjectName(listSubject.get(l).getSubjectName());

                                                    listAssignedSections.add(hwAssign);


                                                }
                                            }
                                        }else{
                                            for(int l=0;l<listSubject.size();l++){
                                                if (listSubject.get(l).getSubjectId().equalsIgnoreCase(((TeacherHWAddingWithSections)mActivity).newHwObj.getSubjectId())){
                                                    HwAssign hwAssign = new HwAssign();

                                                    hwAssign.setClassId(listClasses.get(j).getClassId());
                                                    hwAssign.setClassName(listClasses.get(j).getClassName());

                                                    hwAssign.setSectionId(listSection.get(k).getSectionId());
                                                    hwAssign.setSectionName(listSection.get(k).getSectionName());

                                                    hwAssign.setSubjectId(listSubject.get(l).getSubjectId());
                                                    hwAssign.setSubjectName(listSubject.get(l).getSubjectName());

                                                    listCanAssign.add(hwAssign);

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listAssignedSections.size()>0){
                                    for (int x=0;x<listAssignedSections.size();x++){
                                        getStudentsForHomeWork(listAssignedSections.get(x).getSectionId(),listAssignedSections.get(x).getSubjectId());
                                    }
                                }

                                if (listCanAssign.size()>0){

                                    rvCanAssign.setLayoutManager(new LinearLayoutManager(mActivity));
                                    canAssignAdapter = new CanAssignAdapter(listCanAssign);
                                    rvCanAssign.setAdapter(canAssignAdapter);


                                }else{
                                    viewSectionHwFrag.findViewById(R.id.ll_can_assign).setVisibility(View.GONE);
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });

            }
        });
    }

    class AssignedSectionAdapter extends RecyclerView.Adapter<AssignedSectionAdapter.ViewHolder>{

        List<HwAssign> listAssigned;

        public AssignedSectionAdapter(List<HwAssign> listAssigned) {
            this.listAssigned = listAssigned;
        }

        @NonNull
        @Override
        public AssignedSectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_hw_assign_section,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AssignedSectionAdapter.ViewHolder holder, int position) {
            holder.tvClassName.setText(listAssigned.get(position).getClassName()+" - "+listAssigned.get(position).getSectionName());
            if (listAssigned.get(position).getStudList().size()!=0){

                int c=0;
                for (int i=0;i<listAssigned.get(position).getStudList().size();i++){
                    if (listAssigned.get(position).getStudList().get(i).isChecked()){
                        c++;
                    }
                }
                holder.tvStuds.setText(c+" Students");


            }else{
                holder.tvStuds.setText("All Students");
            }

            if (position==0){
                holder.ivDelete.setVisibility(View.GONE);
            }

            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TeacherHWAddingWithSections)mActivity).newHwObj.setListAssigned(listAssigned);
                    ((TeacherHWAddingWithSections)mActivity).newHwObj.setListCanAssign(listCanAssign);
                    ((TeacherHWAddingWithSections)mActivity).pos = position;
                    ((TeacherHWAddingWithSections)mActivity).loadFrag(3);
                }
            });

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listCanAssign.add(listAssigned.get(position));
                    canAssignAdapter.notifyDataSetChanged();
                    listAssignedSections.remove(position);
                    notifyDataSetChanged();
                }
            });


        }

        @Override
        public int getItemCount() {
            return listAssigned.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvClassName,tvStuds;
            ImageView ivEdit,ivDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvClassName = itemView.findViewById(R.id.tv_class_name);
                tvStuds = itemView.findViewById(R.id.tv_studs);
                ivEdit = itemView.findViewById(R.id.iv_edit);
                ivDelete = itemView.findViewById(R.id.iv_delete);
            }
        }
    }

    class CanAssignAdapter extends RecyclerView.Adapter<CanAssignAdapter.ViewHolder>{

        List<HwAssign> listCanAssignSection;

        public CanAssignAdapter(List<HwAssign> listCanAssignSection) {
            this.listCanAssignSection = listCanAssignSection;

        }

        @NonNull
        @Override
        public CanAssignAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_hw_can_assign,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CanAssignAdapter.ViewHolder holder, int position) {
            holder.tvClassName.setText(listCanAssignSection.get(position).getClassName()+" - "+listCanAssignSection.get(position).getSectionName());

            if (listCanAssign.size()>0){
                viewSectionHwFrag.findViewById(R.id.ll_can_assign).setVisibility(View.VISIBLE);
            }else{
                viewSectionHwFrag.findViewById(R.id.ll_can_assign).setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listAssignedSections.add(listCanAssign.get(position));
                    getStudentsForHomeWork(listAssignedSections.get(listAssignedSections.size()-1).getSectionId(),listAssignedSections.get(listAssignedSections.size()-1).getSubjectId());
                    listCanAssign.remove(position);
                    notifyDataSetChanged();
                    assignedSectionAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return listCanAssign.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClassName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvClassName = itemView.findViewById(R.id.tv_class_name);
            }
        }
    }

    void getStudentsForHomeWork(String sectionId,String subjectId){


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG,"url - "+ new AppUrls().HOMEWORK_GetStudentsBySubject + "schemaName=" + sh_Pref.getString("schema","") + "&sectionId=" + sectionId + "&branchId=" + tObj.getBranchId()
                + "&isElective=" + ((TeacherHWAddingWithSections)mActivity).elective + "&subjectId=" + subjectId);

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetStudentsBySubject + "schemaName=" + sh_Pref.getString("schema","") + "&sectionId=" + sectionId + "&branchId=" + tObj.getBranchId()
                        + "&isElective=" + ((TeacherHWAddingWithSections)mActivity).elective + "&subjectId=" + subjectId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    JSONObject ParentjObject = null;
                    try {
                        ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("studentHomeWork");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherHwStudentObj>>() {
                            }.getType();

                            List<TeacherHwStudentObj> studList = new ArrayList<>();


                            studList.clear();
                            studList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "studList - " + studList.size());

                            for (int i=0;i<studList.size();i++){
                                studList.get(i).setChecked(true);
                            }

                            for (int i=0;i<listAssignedSections.size();i++){
                                if (listAssignedSections.get(i).getSectionId().equalsIgnoreCase(sectionId)){
                                    listAssignedSections.get(i).setStudList(studList);
                                }
                            }

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listAssignedSections.size()>0){
                                        rvAssigned.setLayoutManager(new LinearLayoutManager(mActivity));
                                        assignedSectionAdapter = new AssignedSectionAdapter(listAssignedSections);
                                        rvAssigned.setAdapter(assignedSectionAdapter);
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
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