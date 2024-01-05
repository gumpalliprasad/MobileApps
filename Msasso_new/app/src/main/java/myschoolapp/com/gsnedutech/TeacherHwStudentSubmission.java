package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailsTeacher;
import myschoolapp.com.gsnedutech.Models.TeacherHWStudentofHWObj;
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

public class TeacherHwStudentSubmission extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherHwStudentSubmission.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_submissions)
    RecyclerView rvSubmissions;


    @BindView(R.id.tv_hw_title)
    TextView tvHwTitle;
    @BindView(R.id.tv_hw_type)
    TextView tvHwType;
    @BindView(R.id.tv_hw_last_date)
    TextView tvHwLastDate;
    @BindView(R.id.sp_type)
    Spinner spType;

    TeacherObj teacherObj;
    SharedPreferences sh_Pref;

    MyUtils utils = new MyUtils();

    HomeWorkDetailsTeacher hwObj;

    String statusType = "";

    List<TeacherHWStudentofHWObj> teachstndlist = new ArrayList<>();
    List<TeacherHWStudentofHWObj> filteredList = new ArrayList<>();
    List<String> listOptions = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_hw_student_submission);

        ButterKnife.bind(this);

        init();

        ((TextView)findViewById(R.id.tv_title)).setText(hwObj.getType()+"/"+hwObj.getSubjectName());

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
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherHwStudentSubmission.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getStudentSubmission();
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

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);

        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");

        statusType = getIntent().getStringExtra("type");

        tvHwTitle.setText(hwObj.getHomeworkTitle());
        tvHwType.setText(hwObj.getType());
        try {
            tvHwLastDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(hwObj.getSubmissionDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        listOptions.add("All");
        listOptions.add("Pending");
        listOptions.add("Submitted");

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(TeacherHwStudentSubmission.this,  R.layout.spinner_tv, listOptions);
        adapter.setDropDownViewResource( R.layout.spinner_tv);

        spType.setAdapter(adapter);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filteredList.clear();
                switch (i){
                    case 0:
                        filteredList.addAll(teachstndlist);
                        break;
                    case 1:
                        for (int x=0;x<teachstndlist.size();x++) {
                            if (teachstndlist.get(x).getHomeworkSubmitDate() != null && !teachstndlist.get(x).getHomeworkSubmitDate().equalsIgnoreCase("") && !teachstndlist.get(x).getHomeworkSubmitDate().equalsIgnoreCase("NA")) {
                            } else {
                                filteredList.add(teachstndlist.get(x));

                            }
                        }
                            break;
                    case 2:
                        for (int x=0;x<teachstndlist.size();x++) {
                            if (teachstndlist.get(x).getHomeworkSubmitDate() != null && !teachstndlist.get(x).getHomeworkSubmitDate().equalsIgnoreCase("") && !teachstndlist.get(x).getHomeworkSubmitDate().equalsIgnoreCase("NA")) {
                                filteredList.add(teachstndlist.get(x));
                            }
                        }
                        break;
                        }

                rvSubmissions.setAdapter(new StudentAdapter(filteredList));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    void getStudentSubmission(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().HOMEWORK_GetHomeworkDetailsById + "schemaName=" + sh_Pref.getString("schema","") + "&branchId=" + teacherObj.getBranchId() + "&sectionId=" + hwObj.getSectionId() + "&homeworkId=" + hwObj.getHomeworkId());

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetHomeworkDetailsById + "schemaName=" + sh_Pref.getString("schema","") + "&branchId=" + teacherObj.getBranchId() + "&sectionId=" + hwObj.getSectionId() + "&homeworkId=" + hwObj.getHomeworkId())
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
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("homeworkStudentsStatus");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherHWStudentofHWObj>>() {
                            }.getType();


                            teachstndlist.clear();
                            filteredList.clear();
                            teachstndlist = gson.fromJson(jsonArr.toString(), type);

                            List<TeacherHWStudentofHWObj> filter = new ArrayList<>();

                            for (int i=0;i<teachstndlist.size();i++){
                                if(!teachstndlist.get(i).getHWStatus().equalsIgnoreCase("NA")){
                                    filter.add(teachstndlist.get(i));
                                }
                            }

                            teachstndlist.clear();
                            teachstndlist.addAll(filter);

                            filteredList.addAll(teachstndlist);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvSubmissions.setLayoutManager(new LinearLayoutManager(TeacherHwStudentSubmission.this));
                                    rvSubmissions.setAdapter(new StudentAdapter(filteredList));
                                    rvSubmissions.addItemDecoration(new DividerItemDecoration(rvSubmissions.getContext(), DividerItemDecoration.VERTICAL));

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

    class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder>{

        List<TeacherHWStudentofHWObj> listStudents;

        public StudentAdapter(List<TeacherHWStudentofHWObj> listStudents) {
            this.listStudents = listStudents;
        }

        @NonNull
        @Override
        public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherHwStudentSubmission.this).inflate(R.layout.item_teacher_student_submission_new,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, int position) {
            holder.tvStudentName.setText(listStudents.get(position).getStudentName());
            holder.tvStudentRollNum.setText(listStudents.get(position).getAdmissionNo());

            if (listStudents.get(position).getHomeworkSubmitDate()!=null && !listStudents.get(position).getHomeworkSubmitDate().equalsIgnoreCase("") && !listStudents.get(position).getHomeworkSubmitDate().equalsIgnoreCase("NA")){
                try {
                    holder.tvSubmissionDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(listStudents.get(position).getHomeworkSubmitDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.tvSubmissionDate.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_teach_sub_student),null);
                holder.tvSubmissionDate.setTextColor(Color.BLACK);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TeacherHwStudentSubmission.this,TeacherHwSubmissionDisplayNew.class);
                        intent.putExtra("obj",(Serializable) hwObj);
                        intent.putExtra("student",(Serializable) listStudents.get(position));
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                });
            }else {
                holder.tvSubmissionDate.setText("Pending");
                holder.tvSubmissionDate.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
                holder.tvSubmissionDate.setTextColor(Color.parseColor("#FF0000"));


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(TeacherHwStudentSubmission.this);
                        dialog.setTitle(getResources().getString(R.string.app_name));
                        dialog.setMessage("No submission made by the student!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                       dialog.create().dismiss();
                                    }
                                });

                        dialog.create().show();
                    }
                });
            }






        }

        @Override
        public int getItemCount() {
            return listStudents.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivStudent,ivStatus;
            TextView tvStudentName,tvStudentRollNum,tvSubmissionDate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                ivStudent = itemView.findViewById(R.id.iv_student_image);
                ivStatus = itemView.findViewById(R.id.iv_status);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvStudentRollNum = itemView.findViewById(R.id.tv_rollnum);
                tvSubmissionDate = itemView.findViewById(R.id.tv_submission_date);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


}