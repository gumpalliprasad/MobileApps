/*
 * *
 *  * Created by SriRamaMurthy A on 3/10/19 7:01 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/10/19 7:01 PM
 *
 */

package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

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
import myschoolapp.com.gsnedutech.Models.TeacherAttendStudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherClassStudents extends AppCompatActivity implements SearchView.OnQueryTextListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherClassStudents.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.sv_search_students)
    SearchView svSearchStudents;
    @BindView(R.id.rv_student_list)
    RecyclerView rvStudentList;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String sectionId;

    List<TeacherAttendStudentObj> listStudents = new ArrayList<>();
    StudentListAdapter adapterClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_students);
        ButterKnife.bind(this);


        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        svSearchStudents.setOnQueryTextListener(this);
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
            utils.alertDialog(1, TeacherClassStudents.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getStudents();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    private void init() {
        sectionId = getIntent().getStringExtra("sectionId");
        tvTitle.setText(getIntent().getStringExtra("title"));
        rvStudentList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        adapterClass.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapterClass.getFilter().filter(s);
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    void getStudents(){
        utils.showLoader(TeacherClassStudents.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Attendance Student request - " + new AppUrls().Attendance_GetStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId)
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

                    utils.showLog(TAG, "Students Types response - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendance");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherAttendStudentObj>>() {
                            }.getType();

                            listStudents.clear();
                            listStudents.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog(TAG, "studList - " + listStudents.size());

                            runOnUiThread(() -> {
                                adapterClass = new StudentListAdapter(listStudents);
                                rvStudentList.setAdapter(adapterClass);
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }

    class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> implements Filterable {


        List<TeacherAttendStudentObj> list, list_filtered;

        StudentListAdapter(List<TeacherAttendStudentObj> list) {
            this.list = list;
            this.list_filtered = list;

        }

        @NonNull
        @Override
        public StudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(TeacherClassStudents.this).inflate(R.layout.item_student, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull final StudentListAdapter.ViewHolder viewHolder, final int i) {
            viewHolder.tvName.setText(list_filtered.get(i).getStudentName());
            viewHolder.tvRoll.setText(list_filtered.get(i).getAdmissionNumber());

            if (list_filtered.get(i).getProfilePic().equalsIgnoreCase("NA")) {
                viewHolder.tvNameImage.setText((list_filtered.get(i).getStudentName().charAt(0) + "").toUpperCase());
                viewHolder.tvNameImage.setVisibility(View.VISIBLE);
                viewHolder.imgProfile.setVisibility(View.GONE);
            } else {
                viewHolder.tvNameImage.setVisibility(View.GONE);
                viewHolder.imgProfile.setVisibility(View.VISIBLE);
                Picasso.with(TeacherClassStudents.this).load(new AppUrls().GetstudentProfilePic + list_filtered.get(i).getProfilePic()).
                        placeholder(R.drawable.user_default).into(viewHolder.imgProfile);

            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent stdnIntent = new Intent(TeacherClassStudents.this, StudentProfileInDetail.class);
                    stdnIntent.putExtra("studentId", list_filtered.get(i).getStudentId());
                    stdnIntent.putExtra("studentName",list_filtered.get(i).getStudentName());
                    stdnIntent.putExtra("studentProfilePic", list_filtered.get(i).getProfilePic());
                    stdnIntent.putExtra("rollNumber", list_filtered.get(i).getRollNumber());
                    stdnIntent.putExtra("courseName", getIntent().getStringExtra("courseName"));
                    stdnIntent.putExtra("branchId", getIntent().getStringExtra("branchId"));
                    stdnIntent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                    stdnIntent.putExtra("classId", getIntent().getStringExtra("classId"));
                    stdnIntent.putExtra("className", getIntent().getStringExtra("className"));
                    stdnIntent.putExtra("sectionId", sectionId);
                    stdnIntent.putExtra("lookinto", true);
                    startActivity(stdnIntent);
                }
            });
        }

        public void update(List<TeacherAttendStudentObj> listUpdated) {
            list_filtered = listUpdated;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    if (string.isEmpty()) {
                        list_filtered = list;
                    } else {

                        List<TeacherAttendStudentObj> filteredList = new ArrayList<>();
                        for (TeacherAttendStudentObj s : list) {

                            if (s.getRollNumber().toLowerCase().contains(string.toLowerCase()) || s.getStudentName().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }

                        list_filtered = filteredList;

                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<TeacherAttendStudentObj>) results.values;

                    notifyDataSetChanged();
                }
            };
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            TextView tvRoll, tvName, tvNameImage;
            ImageView imgProfile;

            ViewHolder(View itemView) {
                super(itemView);
                tvRoll = itemView.findViewById(R.id.tv_roll_number);
                tvName = itemView.findViewById(R.id.tv_name);
                tvNameImage = itemView.findViewById(R.id.tv_name_image);
                imgProfile = itemView.findViewById(R.id.img_profile);


            }

        }
    }
}
