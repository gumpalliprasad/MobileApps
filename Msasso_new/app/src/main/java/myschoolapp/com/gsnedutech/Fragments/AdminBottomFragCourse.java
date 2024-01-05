/*
 * *
 *  * Created by SriRamaMurthy A on 22/9/19 4:25 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 22/9/19 4:24 PM
 *
 */

package myschoolapp.com.gsnedutech.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.*;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherCourseSubChapListing;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminBottomFragCourse extends Fragment {

    private static final String TAG = "SriRam -" + AdminBottomFragCourse.class.getName();

    View adminCourseView;
    @BindView(R.id.sp_course)
    Spinner spCourse;
    @BindView(R.id.sp_class)
    Spinner spClass;
    @BindView(R.id.sp_content)
    Spinner spContent;

    @BindView(R.id.rv_sub_list)
    RecyclerView rvSubList;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    AdminObj adminObj;

    AdminCourseObj seladminCourse;
    AdminCourseClassObj seladminCourseClass;
    AdminCourseContentOwner seladminCourseContentOwner;

    List<AdminCourseObj> adminCourseObjList = new ArrayList<>();
    List<AdminCourseClassObj> adminCourseClassObjArrayList = new ArrayList<>();
    List<AdminCourseContentOwner> adminCourseContentOwnerList = new ArrayList<>();
    List<AdminCourseClassSubject> adminCourseClassSubjectList = new ArrayList<>();
    List<TeacherCCSSSubject> subjectList = new ArrayList<>();

    SpCourseAdapter spCourseAdapter;
    SpCourseClassAdapter spCourseClassAdapter;
    SpContentAdapter spContentAdapter;


    public AdminBottomFragCourse() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adminCourseView = inflater.inflate(R.layout.fragment_admin_bottom_frag_course, container, false);
        unbinder = ButterKnife.bind(this, adminCourseView);

        init();

        new GetCourses().execute();

        spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                seladminCourse = spCourseAdapter.getItem(position);

                new GetClasses().execute("" + seladminCourse.getCourseId());
                new GetContent().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        spClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                seladminCourseClass = spCourseClassAdapter.getItem(position);

                new GetSubjects().execute("" + seladminCourse.getCourseId(), "" + seladminCourseClass.getClassId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });

        spContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                // Here you get the current item (a User object) that is selected by its position
                seladminCourseContentOwner = spContentAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {
            }
        });


        return adminCourseView;
    }

    void init() {
        sh_Pref = getActivity().getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("adminObj", "");
        adminObj = gson.fromJson(json, AdminObj.class);


//        showSkeleton();
    }


    private class GetCourses extends AsyncTask<String, Void, String> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(getActivity());
            loading.setMessage("Please wait...");
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setCancelable(false);
            loading.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "Admin getTestOfAdminByCalender request - " + new AppUrls().GetCourseBranchId +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId","0"));

            Request request = new Request.Builder()
                    .url(new AppUrls().GetCourseBranchId +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId","0"))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Test Details- " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                loading.dismiss();
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("Courses");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AdminCourseObj>>() {
                        }.getType();

                        adminCourseObjList.addAll(gson.fromJson(jsonArr.toString(), type));

                        Log.v(TAG, "adminCourseObjList -" + adminCourseObjList.size());

                        spCourseAdapter = new SpCourseAdapter(getActivity(), android.R.layout.simple_spinner_item, adminCourseObjList);
                        spCourse.setAdapter(spCourseAdapter);
                    }

                } catch (Exception e) {

                }

            }

        }

    }

    private class GetClasses extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "Admin getTestOfAdminByCalender request - " + new AppUrls().GetClassByCoursesID +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId","0") + "&courseId=" + strings[0]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetClassByCoursesID +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId","0")+ "&courseId=" + strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "Test Details- " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("Classes");
                        if (getActivity() != null && isAdded()) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AdminCourseClassObj>>() {
                        }.getType();

                        adminCourseClassObjArrayList.clear();
                        adminCourseClassObjArrayList.addAll(gson.fromJson(jsonArr.toString(), type));

                        Log.v(TAG, "adminCourseClassObjArrayList -" + adminCourseClassObjArrayList.size());

                        spCourseClassAdapter = new SpCourseClassAdapter(getActivity(), android.R.layout.simple_spinner_item, adminCourseClassObjArrayList);
                        spClass.setAdapter(spCourseClassAdapter);
                    }}

                } catch (Exception e) {

                }

            }

        }

    }

    private class GetContent extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "Admin getTestOfAdminByCalender request - " + new AppUrls().GetselectContentOwner+"schemaName=" +sh_Pref.getString("schema",""));

            Request request = new Request.Builder()
                    .url(new AppUrls().GetselectContentOwner+"schemaName=" +sh_Pref.getString("schema",""))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "GetContent responce- " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("ContentOwner");
                        if (getActivity() != null && isAdded()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminCourseContentOwner>>() {
                            }.getType();

                            adminCourseContentOwnerList.clear();
                            adminCourseContentOwnerList.addAll(gson.fromJson(jsonArr.toString(), type));

                            Log.v(TAG, "adminCourseContentOwner -" + adminCourseContentOwnerList.size());
                            Log.v(TAG, "courseid -" + seladminCourse.getCourseId());

                            List<AdminCourseContentOwner> filteredList = new ArrayList<>();

                            for (int i = 0; i < adminCourseContentOwnerList.size(); i++) {
                                if (adminCourseContentOwnerList.get(i).getCourseId().equalsIgnoreCase("" + seladminCourse.getCourseId()))
                                    filteredList.add(adminCourseContentOwnerList.get(i));
                            }

                            Log.v(TAG, "filteredList -" + filteredList.size());
                            spContentAdapter = new SpContentAdapter(getActivity(), android.R.layout.simple_spinner_item, filteredList);
                            spContent.setAdapter(spContentAdapter);
                        }
                    }

                } catch (Exception e) {

                }

            }

        }

    }

    private class GetSubjects extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Log.v(TAG, "Admin GetClassByCoursesID request - " + new AppUrls().GetAdminCourseClassSubjects +"schemaName=" +sh_Pref.getString("schema","")+ "&classId=" + strings[1] + "&courseId=" + strings[0]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetAdminCourseClassSubjects +"schemaName=" +sh_Pref.getString("schema","")+ "&classId=" + strings[1] + "&courseId=" + strings[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v("TAG", "GetClassByCoursesID responce- " + jsonResp);

                // Do something with the response.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResp;
        }

        @Override
        protected void onPostExecute(String responce) {
            super.onPostExecute(responce);
            if (responce != null) {
                try {
                    JSONObject ParentjObject = new JSONObject(responce);
                    if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArr = ParentjObject.getJSONArray("Classes");
                        if (getActivity() != null && isAdded()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AdminCourseClassSubject>>() {
                            }.getType();

                            adminCourseClassSubjectList.clear();
                            adminCourseClassSubjectList.addAll(gson.fromJson(jsonArr.toString(), type));

                            subjectList.clear();
                            Log.v(TAG, "adminCourseClassSubjectList -" + adminCourseClassSubjectList.size());
                            for (int i = 0; i < adminCourseClassSubjectList.size(); i++) {
                                TeacherCCSSSubject teacherCCSSSubject = new TeacherCCSSSubject();
                                teacherCCSSSubject.setSubjectId(""+adminCourseClassSubjectList.get(i).getSubjectId());
                                teacherCCSSSubject.setSubjectName(adminCourseClassSubjectList.get(i).getSubjectName());
                                subjectList.add(teacherCCSSSubject);
                            }

                            rvSubList.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rvSubList.setAdapter(new AdapterClassCourse(adminCourseClassSubjectList));

                        }
                    }

                } catch (Exception e) {

                }

            }
            if (getActivity() != null && isAdded()) {
                rvSubList.setVisibility(View.VISIBLE);
//                shimmerClasses.stopShimmerAnimation();
//                skeletonClasses.setVisibility(View.GONE);
            }


        }

    }

    public class SpCourseAdapter extends ArrayAdapter<AdminCourseObj> {

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<AdminCourseObj> coursesList;

        public SpCourseAdapter(Context context, int textViewResourceId, List<AdminCourseObj> coursesList) {
            super(context, textViewResourceId, coursesList);
            this.context = context;
            this.coursesList = coursesList;
        }

        @Override
        public int getCount() {
            return coursesList.size();
        }

        @Override
        public AdminCourseObj getItem(int position) {
            return coursesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(coursesList.get(position).getCourseName());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(coursesList.get(position).getCourseName());

            return label;
        }
    }

    public class SpCourseClassAdapter extends ArrayAdapter<AdminCourseClassObj> {

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<AdminCourseClassObj> classesList;

        public SpCourseClassAdapter(Context context, int textViewResourceId, List<AdminCourseClassObj> classesList) {
            super(context, textViewResourceId, classesList);
            this.context = context;
            this.classesList = classesList;
        }

        @Override
        public int getCount() {
            return classesList.size();
        }

        @Override
        public AdminCourseClassObj getItem(int position) {
            return classesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(classesList.get(position).getClassName());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(classesList.get(position).getClassName());

            return label;
        }
    }

    public class SpContentAdapter extends ArrayAdapter<AdminCourseContentOwner> {

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<AdminCourseContentOwner> contentOwnerList;

        public SpContentAdapter(Context context, int textViewResourceId, List<AdminCourseContentOwner> contentOwnerList) {
            super(context, textViewResourceId, contentOwnerList);
            this.context = context;
            this.contentOwnerList = contentOwnerList;
        }

        @Override
        public int getCount() {
            return contentOwnerList.size();
        }

        @Override
        public AdminCourseContentOwner getItem(int position) {
            return contentOwnerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(contentOwnerList.get(position).getDisplayName());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(contentOwnerList.get(position).getDisplayName());

            return label;
        }
    }

    class AdapterClassCourse extends RecyclerView.Adapter<AdapterClassCourse.ViewHolder> {

        List<AdminCourseClassSubject> list;

        AdapterClassCourse(List<AdminCourseClassSubject> list) {

            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.cv_admin_course_subject, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSubName.setText(list.get(position).getSubjectName());
            switch (list.get(position).getSubjectGroup()) {
                case "Maths":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_maths);
//                    holder.imgSub.setTag(R.drawable.ic_img_maths);
                    break;
                case "Science":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_science);
//                    holder.imgSub.setTag(R.drawable.ic_img_science);

                    break;
                case "English":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_english);
//                    holder.imgSub.setTag(R.drawable.ic_img_english);

                    break;
                case "GeneralKnowledge":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_gk);
//                    holder.imgSub.setTag(R.drawable.ic_img_gk);

                    break;
                case "SocialScience":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_social);
//                    holder.imgSub.setTag(R.drawable.ic_img_social);

                    break;
                case "Hindi":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_hindi);
//                    holder.imgSub.setTag(R.drawable.ic_img_maths);

                    break;
                case "Telugu":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_telugu);
//                    holder.imgSub.setTag(R.drawable.ic_img_maths);

                    break;
                case "Physics":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_physics);
//                    holder.imgSub.setTag(R.drawable.ic_img_maths);

                    break;
                case "Chemistry":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_chemistry);
//                    holder.imgSub.setTag(R.drawable.ic_img_chemistry);

                    break;
                case "Biology":
                    holder.imgSub.setImageResource(R.drawable.ic_courses_biology);
//                    holder.imgSub.setTag(R.drawable.ic_bg_biology);

                    break;
//                default:
//                    holder.imgSub.setImageResource(R.drawable.ic_bg_generic);
//                    holder.imgSub.setTag(R.drawable.ic_img_maths);
//
//                    break;
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

//                            Student School or College Courses
//                        Intent intent = new Intent(getActivity(), StudentCourseSubChapListing.class);
//                        intent.putExtra("subjectId", "" + list.get(position).getSubjectId());
//                        intent.putExtra("subjectName", list.get(position).getSubjectName());
//                        intent.putExtra("subjectGroup", list.get(position).getSubjectGroup());
//                        intent.putExtra("classId", "" + seladminCourseClass.getClassId());
//                        intent.putExtra("className", seladminCourseClass.getClassName());
//                        intent.putExtra("courseId", "" + seladminCourse.getCourseId());
//                        intent.putExtra("contentType", seladminCourseContentOwner.getContentType());
//                        startActivity(intent);

                        Intent intent = new Intent(getActivity(), TeacherCourseSubChapListing.class);
                        intent.putExtra("subjectId", "" + list.get(position).getSubjectId());
                        intent.putExtra("subjectGroup", "" + list.get(position).getSubjectGroup());
                        intent.putExtra("courseId", "" + seladminCourse.getCourseId());
                        intent.putExtra("courseName", "" + seladminCourse.getCourseName());
                        intent.putExtra("classId", "" + seladminCourseClass.getClassId());
                        intent.putExtra("className", seladminCourseClass.getClassName());
                        intent.putExtra("Subjects", (Serializable) subjectList);
                        intent.putExtra("position", "" + position);
                        intent.putExtra("contentType", seladminCourseContentOwner.getContentType());
                        intent.putExtra("schemaName", seladminCourseContentOwner.getContentSchema());
                        startActivity(intent);

                    } catch (Exception e) {
                        Log.v(TAG, e.getMessage());

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName;
            ImageView imgSub;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubName = itemView.findViewById(R.id.tv_subName);
                imgSub = itemView.findViewById(R.id.img_sub);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
