package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.AdminObj;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailTeacher;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailsTeacher;
import myschoolapp.com.gsnedutech.Models.TeacherHwObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections.TeacherHWAddingWithSections;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.ViewAnimation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherHwAssignments extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherHwAssignments.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_assignments)
    RecyclerView rvAssignments;

    @BindView(R.id.fab_hw)
    ImageView fabHw;

    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    boolean toggle = false;

    TeacherObj teacherObj;
    AdminObj adminObj;
    boolean isAdmin = false;
    String userId = "", branchId = "";
    SharedPreferences sh_Pref;

    String sectionId = "";

    MyUtils utils = new MyUtils();
    List<TeacherHwObj> hwList = new ArrayList<>();

    String selMon="",selYear="";

    String hwType = "";

    List<HomeWorkDetailsTeacher> listPending = new ArrayList<>();
    List<HomeWorkDetailsTeacher> listComplete = new ArrayList<>();

    String tabType = "pending";

    int[] backgrounds ={R.drawable.ic_bg_teacher_assignment_peach,R.drawable.ic_bg_teacher_assignment_teal,R.drawable.ic_bg_teacher_assignment_purple,R.drawable.ic_bg_teacher_assignment_blue,R.drawable.ic_bg_teacher_assignment_orange};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_hw_assignments);

        ButterKnife.bind(this);

        init();
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
            utils.alertDialog(1, TeacherHwAssignments.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getTeacherHomeWorks();
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

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            isAdmin = false;
            String json = sh_Pref.getString("teacherObj", "");
            teacherObj = gson.fromJson(json, TeacherObj.class);
            userId = ""+teacherObj.getUserId();
            branchId = teacherObj.getBranchId();
        } else if (sh_Pref.getBoolean("admin_loggedin", false)) {
            isAdmin = true;
            String json = sh_Pref.getString("adminObj", "");
            adminObj = gson.fromJson(json, AdminObj.class);
            userId = ""+adminObj.getUserId();
            branchId = "" + sh_Pref.getString("admin_branchId", "0");
        }
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));

        sectionId = getIntent().getStringExtra("sectionId");

        selMon = new SimpleDateFormat("MM").format(new Date());
        selYear = new SimpleDateFormat("yyyy").format(new Date());

        tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));

        fabHw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAdmin) return;
                if (toggle){
                    toggle=false;
//                    ViewAnimation.showOut(findViewById(R.id.ll_hw));
//                    ViewAnimation.showOut(findViewById(R.id.ll_assignment));
//                    ViewAnimation.showOut(findViewById(R.id.ll_project));
                    findViewById(R.id.ll_hw).setVisibility(View.GONE);
                    findViewById(R.id.ll_assignment).setVisibility(View.GONE);
                    findViewById(R.id.ll_project).setVisibility(View.GONE);
                    findViewById(R.id.ll_backdrop).setVisibility(View.GONE);

                }else{
                    toggle=true;
                    ViewAnimation.showIn(findViewById(R.id.ll_hw));
                    ViewAnimation.showIn(findViewById(R.id.ll_assignment));
                    ViewAnimation.showIn(findViewById(R.id.ll_project));
                    findViewById(R.id.ll_backdrop).setVisibility(View.VISIBLE);
                    }
                rotateFab(view,toggle);
            }
        });

        findViewById(R.id.ll_hw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(TeacherHwAssignments.this,"hw",Toast.LENGTH_SHORT).show();

                findViewById(R.id.ll_hw).setVisibility(View.GONE);
                findViewById(R.id.ll_assignment).setVisibility(View.GONE);
                findViewById(R.id.ll_project).setVisibility(View.GONE);
                findViewById(R.id.ll_backdrop).setVisibility(View.GONE);
                rotateFab(fabHw,false);

                Intent intent = new Intent(TeacherHwAssignments.this, TeacherHWAddingWithSections.class);
                intent.putExtra("sectionId",sectionId);
                intent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
                intent.putExtra("elective", getIntent().getStringExtra("elective"));
                intent.putExtra("className", getIntent().getStringExtra("className"));
                intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.ll_assignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(TeacherHwAssignments.this,"hw",Toast.LENGTH_SHORT).show();

                findViewById(R.id.ll_hw).setVisibility(View.GONE);
                findViewById(R.id.ll_assignment).setVisibility(View.GONE);
                findViewById(R.id.ll_project).setVisibility(View.GONE);
                findViewById(R.id.ll_backdrop).setVisibility(View.GONE);
                rotateFab(fabHw,false);

                Intent intent = new Intent(TeacherHwAssignments.this, TeacherHWAddingWithSections.class);
                intent.putExtra("sectionId",sectionId);
                intent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
                intent.putExtra("elective", getIntent().getStringExtra("elective"));
                intent.putExtra("className", getIntent().getStringExtra("className"));
                intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
        findViewById(R.id.ll_project).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(TeacherHwAssignments.this,"hw",Toast.LENGTH_SHORT).show();

                findViewById(R.id.ll_hw).setVisibility(View.GONE);
                findViewById(R.id.ll_assignment).setVisibility(View.GONE);
                findViewById(R.id.ll_project).setVisibility(View.GONE);
                findViewById(R.id.ll_backdrop).setVisibility(View.GONE);
                rotateFab(fabHw,false);

                Intent intent = new Intent(TeacherHwAssignments.this, TeacherHWAddingWithSections.class);
                intent.putExtra("sectionId",sectionId);
                intent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
                intent.putExtra("elective", getIntent().getStringExtra("elective"));
                intent.putExtra("className", getIntent().getStringExtra("className"));
                intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });





        findViewById(R.id.tv_pending).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.tv_pending)).setBackgroundResource(R.drawable.underline_colored);
                ((TextView)findViewById(R.id.tv_reviewed)).setBackgroundResource(0);

                tabType = "pending";
                rvAssignments.setAdapter(new AssignmentAdapter(listPending));


            }
        });
        findViewById(R.id.tv_reviewed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)findViewById(R.id.tv_reviewed)).setBackgroundResource(R.drawable.underline_colored);
                ((TextView)findViewById(R.id.tv_pending)).setBackgroundResource(0);
                tabType = "complete";
                rvAssignments.setAdapter(new AssignmentAdapter(listComplete));
            }
        });


        findViewById(R.id.cv_date_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        String mon = "";
                        switch (month) {
                            case 1:
                                mon = "January";
                                break;
                            case 2:
                                mon = "February";
                                break;
                            case 3:
                                mon = "March";
                                break;
                            case 4:
                                mon = "April";
                                break;
                            case 5:
                                mon = "May";
                                break;
                            case 6:
                                mon = "June";
                                break;
                            case 7:
                                mon = "July";
                                break;
                            case 8:
                                mon = "August";
                                break;
                            case 9:
                                mon = "September";
                                break;
                            case 10:
                                mon = "October";
                                break;
                            case 11:
                                mon = "November";
                                break;
                            case 12:
                                mon = "December";
                                break;
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, (month - 1), 1);

                        selMon = month+"";
                        selYear = year+"";

                        onResume();

                        tvMonthYear.setText(mon + " " + year);


                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

    }

    public static boolean rotateFab(final View v, boolean rotate) {
        v.animate().setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                })
                .rotation(rotate ? 135f : 0f);
        return rotate;
    }


    void getTeacherHomeWorks(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().HOMEWORK_GetSectionHomeWorkDetails + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","") + "&sectionId=" + sectionId + "&branchId=" + branchId+"&userId="+userId
                + "&monthId=" + selMon + "&yearId=" + selYear);

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetSectionHomeWorkDetails + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","") + "&sectionId=" + sectionId + "&branchId=" + branchId+"&userId="+userId
                        + "&monthId=" + selMon + "&yearId=" + selYear)
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
                utils.showLog(TAG,"response "+resp);

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

                            if (ParentjObject.has("homeWorkDetails")) {
                                JSONArray jsonArr = ParentjObject.getJSONArray("homeWorkDetails");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<TeacherHwObj>>() {
                                }.getType();

                                hwList.clear();
                                hwList = gson.fromJson(jsonArr.toString(), type);

                                Log.v(TAG, "hwListSize - " + hwList.size());

                                List<HomeWorkDetailsTeacher> listDetails = new ArrayList<>();
                                for (int i=0;i<hwList.size();i++){
                                    List<HomeWorkDetailTeacher> listDetail = new ArrayList<>();
                                    listDetail.addAll(hwList.get(i).getHomeWorkDetails());
                                    hwType = hwList.get(i).getHomeWorkDesc();
                                    for (int j=0;j<listDetail.size();j++){
                                        List<HomeWorkDetailsTeacher> list = new ArrayList<>();
                                        list.addAll(listDetail.get(j).getHomeWorkDetail());
                                        for (int k=0;k<list.size();k++){
                                            list.get(k).setType(hwType);
                                            if (!list.get(k).getHomeworkTitle().equalsIgnoreCase("NA")) {
                                                listDetails.add(list.get(k));
                                            }
                                        }
                                    }

                                }

                                listComplete.clear();
                                listPending.clear();

                                for (int i=0;i<listDetails.size();i++){
                                    try {
                                        if (listDetails.get(i).getSubmissionDate().equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))){
                                            listPending.add(listDetails.get(i));
                                        }else if(new SimpleDateFormat("yyyy-MM-dd").parse(listDetails.get(i).getSubmissionDate()).before(new Date())){
                                            listComplete.add(listDetails.get(i));
                                        } else {
                                            listPending.add(listDetails.get(i));
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                Collections.reverse(listComplete);


                                if (listDetails.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvAssignments.setVisibility(View.VISIBLE);
                                           if (tabType.equalsIgnoreCase("pending")){
                                               rvAssignments.setAdapter(new AssignmentAdapter(listPending));
                                           }
                                           else{
                                               rvAssignments.setAdapter(new AssignmentAdapter(listComplete));
                                           }
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            rvAssignments.setVisibility(View.GONE);
                                        }
                                    });
                                }

                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvAssignments.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        },1000);
                    }
                });
            }
        });

    }


    class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder>{

        List<HomeWorkDetailsTeacher> listDetails;

        public AssignmentAdapter(List<HomeWorkDetailsTeacher> listDetails) {
            this.listDetails = listDetails;
        }

        @NonNull
        @Override
        public AssignmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherHwAssignments.this).inflate(R.layout.item_teacher_assignments_new,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AssignmentAdapter.ViewHolder holder, int position) {

            int val = position%(backgrounds.length);

            holder.flBg.setBackgroundResource(backgrounds[val]);

            holder.tvSubName.setText(listDetails.get(position).getSubjectName());
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listDetails.get(position).getSubmissionDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvHwName.setText(listDetails.get(position).getHomeworkTitle());

            holder.tvSubmission.setText(""+(listDetails.get(position).getSubmitted()+listDetails.get(position).getCompleted()+listDetails.get(position).getReAssign())+"/"+listDetails.get(position).getTotalCount());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherHwAssignments.this,TeacherHwDisplayNew.class);
                    intent.putExtra("obj",(Serializable) listDetails.get(position));
                    intent.putExtra("sectionId",sectionId);
                    intent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
                    intent.putExtra("elective", getIntent().getStringExtra("elective"));
                    intent.putExtra("className", getIntent().getStringExtra("className"));
                    intent.putExtra("selMon", selMon+"");
                    intent.putExtra("selYear", selYear+"");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

            holder.llSubStudents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherHwAssignments.this,TeacherHwStudentSubmission.class);
                    intent.putExtra("obj",(Serializable) (Serializable) listDetails.get(position));
                    intent.putExtra("type","submitted");
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName,tvHwName,tvDate,tvSubmission;
            FrameLayout flBg;
            LinearLayout llSubStudents;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvHwName = itemView.findViewById(R.id.tv_hw_name);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvSubmission = itemView.findViewById(R.id.tv_sub_mission);
                flBg = itemView.findViewById(R.id.fl_bg);
                llSubStudents = itemView.findViewById(R.id.ll_sub_students);
            }
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}