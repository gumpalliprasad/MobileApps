/*
 * *
 *  * Created by SriRamaMurthy A on 6/9/19 2:14 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 6/9/19 2:14 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import myschoolapp.com.gsnedutech.Models.AdminClassSecObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.ItemOffsetDecoration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AdminClassSections extends AppCompatActivity {

    private static final String TAG = "SriRam -" + AdminClassSections.class.getName();


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String classCourseId;
    @BindView(R.id.rv_sec_list)
    RecyclerView rvSecList;

    List<AdminClassSecObj> secObjList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_class_sections);
        ButterKnife.bind(this);


        this.setTitle(getIntent().getStringExtra("courseName") + " . " + getIntent().getStringExtra("className"));
        classCourseId = getIntent().getStringExtra("classCourseId");
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new GetAdminClassSections().execute(classCourseId, getIntent().getStringExtra("branchId"));


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class GetAdminClassSections extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String jsonResp = "null";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            Log.v(TAG, "url - " + new AppUrls().GetSectionsByBranchCourseClass +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString(AppConst.SCHEMA,"")+ "&classCourseId=" + strings[0] + "&branchId=" + strings[1]);

            Request request = new Request.Builder()
                    .url(new AppUrls().GetSectionsByBranchCourseClass +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString(AppConst.SCHEMA,"")+ "&classCourseId=" + strings[0] + "&branchId=" + strings[1])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                jsonResp = response.body().string();

                Log.v(TAG, "Admin ClassCourses Sections responce - " + jsonResp);

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
                        JSONArray jsonArr = ParentjObject.getJSONArray("Sections");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AdminClassSecObj>>() {
                        }.getType();

                        secObjList.clear();
                        secObjList.addAll(gson.fromJson(jsonArr.toString(), type));

//                        AutofitGridLayoutManager layoutManager = new AutofitGridLayoutManager(AdminClassSections.this, 273);
                        rvSecList.setLayoutManager(new GridLayoutManager(AdminClassSections.this, 3));
                        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(AdminClassSections.this, R.dimen.item_offset);
                        rvSecList.addItemDecoration(itemDecoration);
                        AdapterClass classCustomAdapter = new AdapterClass(AdminClassSections.this, secObjList);
                        rvSecList.setAdapter(classCustomAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        shimmerClasses.setVisibility(View.GONE);
//                        skeletonClasses.setVisibility(View.GONE);
//                        rvClassList.setVisibility(View.VISIBLE);
                    }
                }, 2000);

            }

        }
    }

    class AdapterClass extends RecyclerView.Adapter<AdapterClass.ViewHolder> {

        List<AdminClassSecObj> secObjAdapterList;
        Context context;

        public AdapterClass(Context context, List<AdminClassSecObj> secObjAdapterList) {
            this.secObjAdapterList = secObjAdapterList;
            this.context = context;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_classname, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.tvClassname.setText(secObjAdapterList.get(i).getSectionName());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminClassSections.this, TeacherClassSections.class);
                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                    intent.putExtra("courseName", getIntent().getStringExtra("courseName"));
                    intent.putExtra("branchId", getIntent().getStringExtra("branchId"));
                    intent.putExtra("classId", getIntent().getStringExtra("classId"));
                    intent.putExtra("className", getIntent().getStringExtra("className"));
                    intent.putExtra("sectionName", secObjAdapterList.get(i).getSectionName());
                    intent.putExtra("sectionId", "" + secObjAdapterList.get(i).getSectionId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return secObjAdapterList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvClassname, tvCount;

            ViewHolder(View itemView) {
                super(itemView);
                tvClassname = itemView.findViewById(R.id.tv_class);
                tvCount = itemView.findViewById(R.id.tv_count);
            }
        }
    }
}
