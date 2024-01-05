package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
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
import myschoolapp.com.gsnedutech.Fragments.TeacherStdntAttendanceFrag;
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

public class TeacherStdnListAttendance extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherStdntAttendanceFrag.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_att_students)
    RecyclerView rvAttStudents;

    String sectionId;

    MyUtils utils = new MyUtils();
    List<TeacherAttendStudentObj> studList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_stdn_list_atttendance);
        ButterKnife.bind(this);

        init();

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
            utils.alertDialog(1, TeacherStdnListAttendance.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getStudentsAttendance();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    void init(){
        sectionId = getIntent().getStringExtra("sectionId");
        rvAttStudents.setLayoutManager(new LinearLayoutManager(this));
    }

    void getStudentsAttendance(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - Attendance Student request - " + new AppUrls().Attendance_GetStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId)
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
                String resp = response.body().string();
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendance");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherAttendStudentObj>>() {
                            }.getType();

                            studList.clear();
                            studList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "studList - " + studList.size());


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvAttStudents.setAdapter(new StudentsAdapter(studList));
                                    rvAttStudents.addItemDecoration(new DividerItemDecoration(TeacherStdnListAttendance.this, DividerItemDecoration.VERTICAL));

                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder>{

        List<TeacherAttendStudentObj> listStudents;

        public StudentsAdapter(List<TeacherAttendStudentObj> listStudents) {
            this.listStudents = listStudents;
        }

        @NonNull
        @Override
        public StudentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherStdnListAttendance.this).inflate(R.layout.item_student_list_attendance,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull StudentsAdapter.ViewHolder holder, int position) {

            holder.tvName.setText(listStudents.get(position).getStudentName());
            holder.tvRollNm.setText(listStudents.get(position).getRollNumber());
            Picasso.with(TeacherStdnListAttendance.this).load(new AppUrls().GetstudentProfilePic + listStudents.get(position).getProfilePic()).placeholder(R.drawable.user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivDp);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherStdnListAttendance.this,TeacherStudAttendance.class);
                    intent.putExtra("studentId",listStudents.get(position).getStudentId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listStudents.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName,tvRollNm;
            ImageView ivDp;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);
                tvRollNm = itemView.findViewById(R.id.tv_rollnum);
                ivDp = itemView.findViewById(R.id.img_profile);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}