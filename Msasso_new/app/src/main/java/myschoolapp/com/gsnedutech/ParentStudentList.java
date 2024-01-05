/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.ParentObj;
import myschoolapp.com.gsnedutech.Models.ParentStudentDetail;
import myschoolapp.com.gsnedutech.Util.AppUrls;

public class ParentStudentList extends AppCompatActivity {

    private static final String TAG = ParentStudentList.class.getName();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    ParentObj pObj;

    @BindView(R.id.rv_studentList)
    RecyclerView rvStudentList;

    List<ParentStudentDetail> studentList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Hiding Title bar of this activity screen */
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /** Making this activity, full screen */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_parent_student_list);
        ButterKnife.bind(this);

        init();

    }

    void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("parentObj", "");
        pObj = gson.fromJson(json, ParentObj.class);

        studentList.clear();
        studentList.addAll(pObj.getStudentDetails());


        rvStudentList.setLayoutManager(new GridLayoutManager(this,2));
        rvStudentList.setAdapter(new StudentAdapter(studentList));
    }

    class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

        List<ParentStudentDetail> studentList;

        StudentAdapter(List<ParentStudentDetail> studentList) {
            this.studentList = studentList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ParentStudentList.this).inflate(R.layout.item_child, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(studentList.get(position).getStudentName());
            holder.tvClass.setText(studentList.get(position).getClassName()+"-"+studentList.get(position).getSectionName());
            holder.tvStudentId.setText(studentList.get(position).getStudentRollnumber());

            Picasso.with(ParentStudentList.this).load(new AppUrls().GetstudentProfilePic + studentList.get(position).getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivStud);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String sjson = gson.toJson(studentList.get(position));
                    toEdit.putString("studentObj", sjson);
                    toEdit.commit();
                    startActivity(new Intent(ParentStudentList.this, ParentHome.class));
                    finishAffinity();
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return studentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName, tvClass, tvStudentId;
            ImageView ivStud;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);
                tvClass = itemView.findViewById(R.id.tv_class);
                ivStud = itemView.findViewById(R.id.img_stud);
                tvStudentId = itemView.findViewById(R.id.tv_student_id);
            }
        }
    }
}
