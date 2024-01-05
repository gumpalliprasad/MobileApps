package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.TeacherAttendStudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherStdnTakeAttendance extends AppCompatActivity implements SearchView.OnQueryTextListener, NetworkConnectivity.ConnectivityReceiverListener{

    private static final String TAG = "SriRam -" + TeacherStdnTakeAttendance.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.search)
    SearchView searchStdn;
    @BindView(R.id.absent)
    TextView absent;
    @BindView(R.id.legend)
    TextView legend;
    @BindView(R.id.rv_StdnList)
    RecyclerView rvStdnList;
    @BindView(R.id.btn_marksel)
    Button btnMarksel;
    @BindView(R.id.view_bar)
    View viewBar;
    @BindView(R.id.img_dp)
    CircleImageView imgDp;
    @BindView(R.id.tv_name_dp)
    TextView tvNameDp;
    @BindView(R.id.tv_rollnum)
    TextView tvRollnum;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_lr)
    TextView tvLr;
    @BindView(R.id.tv_lr_from)
    TextView tvLrFrom;
    @BindView(R.id.tv_lr_to)
    TextView tvLrTo;
    @BindView(R.id.tv_lr_reason)
    TextView tvLrReason;
    @BindView(R.id.tv_lr_descp)
    TextView tvLrDescp;
    @BindView(R.id.ll_lr)
    LinearLayout llLr;
    @BindView(R.id.sp_mark)
    Spinner spMark;
    @BindView(R.id.sp_reason)
    Spinner spReason;
    @BindView(R.id.et_otherreason)
    EditText etOtherreason;
    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.tv_markleave)
    TextView tvMarkleave;
    @BindView(R.id.rl_hiddenreason)
    RelativeLayout rlHiddenreason;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.tv_holiday)
    TextView tvHoliday;

    String branchId, sectionId,classId,courseId;
    int selpos = 0;

    List<TeacherAttendStudentObj> studList = new ArrayList<>();
    List<TeacherAttendStudentObj> studfliteredList = new ArrayList<>();

    StudAttandenceAdapter studAttandenceAdapter;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf;
    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMM, yyyy");
    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");

    List<String> markAs = new ArrayList<String>();
    List<String> reasons = new ArrayList<String>();

    int secAttdStatus;
    String sectionAttendanceId, reqDate;

    String selYear, selMon, selDate;
    String[] holidayArray = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_stdn_take_attendance);
        ButterKnife.bind(this);

//        getSupportActionBar().setElevation(0);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvStdnList.setLayoutManager(new LinearLayoutManager(this));
        rvStdnList.addItemDecoration(new DividerItemDecoration(rvStdnList.getContext(), DividerItemDecoration.VERTICAL));

        sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (getIntent().hasExtra("reqDate")) {
            reqDate = getIntent().getStringExtra("reqDate");
            setTitle("" + getIntent().getStringExtra("dispdate"));
            sectionAttendanceId = getIntent().getStringExtra("sectionAttendanceId");
        } else {
            reqDate = sdf.format(Calendar.getInstance().getTime());
            setTitle("Today");

            selMon = ""+(Calendar.getInstance().get(Calendar.MONTH)+1);
            selYear= ""+(Calendar.getInstance().get(Calendar.YEAR));
            selDate= ""+(Calendar.getInstance().get(Calendar.DATE));


            Log.v(TAG,"Month - "+selMon+" Year - "+selYear+" date - "+selDate);


        }

        spMark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    tvMarkleave.setVisibility(View.VISIBLE);
                    tvMarkleave.setText("Mark as " + spMark.getSelectedItem().toString());
                } else {
                    tvMarkleave.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        searchStdn.setOnQueryTextListener(this);
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
            utils.alertDialog(1, TeacherStdnTakeAttendance.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (getIntent().hasExtra("reqDate")) {
                    getStudents();
                }
                else {
                    getHolidaysForStudentAttendance();
                    getStudents();
                }
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
        branchId = getIntent().getStringExtra("branchId");
        sectionId = getIntent().getStringExtra("sectionId");
        courseId = getIntent().getStringExtra("courseId");
        classId = getIntent().getStringExtra("classId");

        markAs.add("Select Status");
        markAs.add("Present");
        markAs.add("Late");
        markAs.add("Approved Leave");
        markAs.add("Unapproved Leave");




        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, markAs);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spMark.setAdapter(dataAdapter);

        reasons.add("Select Reason");
        reasons.add("Health Issue");
        reasons.add("Vacation Leave");
        reasons.add("Others");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reasons);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spReason.setAdapter(dataAdapter2);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        studAttandenceAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        studAttandenceAdapter.getFilter().filter(query);
        return false;
    }

    public void slideup(int pos, List<TeacherAttendStudentObj> filteredlist) {

        this.selpos = pos;                    //get position
        this.studfliteredList = filteredlist;   //get filtered list

        tvNameDp.setText(studfliteredList.get(pos).getStudentName().charAt(0) + "");
        if (filteredlist.get(pos).getProfilePic().equalsIgnoreCase("NA")) {
            tvNameDp.setText((filteredlist.get(pos).getStudentName().charAt(0) + "").toUpperCase());
            tvNameDp.setVisibility(View.VISIBLE);
            imgDp.setVisibility(View.GONE);
        } else {
            tvNameDp.setVisibility(View.GONE);
            imgDp.setVisibility(View.VISIBLE);
            Picasso.with(TeacherStdnTakeAttendance.this).load(new AppUrls().GetstudentProfilePic + filteredlist.get(pos).getProfilePic()).
                    placeholder(R.drawable.user_default).into(imgDp);

        }
        tvRollnum.setText(studfliteredList.get(pos).getRollNumber());
        tvName.setText(studfliteredList.get(pos).getStudentName());

        if (isPanelShown()) {
            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
            rlHiddenreason.startAnimation(bottomDown);
            rlHiddenreason.setVisibility(View.GONE);
        } else {
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            rlHiddenreason.startAnimation(bottomUp);
            rlHiddenreason.setVisibility(View.VISIBLE);
            spMark.setSelection(0);
            spReason.setSelection(0);
            etComment.setText("");
        }

    }

    public boolean isPanelShown() {
        return rlHiddenreason.getVisibility() == View.VISIBLE;
    }

    @OnClick({R.id.btn_marksel, R.id.view_bar, R.id.tv_markleave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_marksel:
                postAttendance();
                break;
            case R.id.view_bar:
                break;
            case R.id.tv_markleave:
                if (spMark.getSelectedItemPosition() != 0 && spReason.getSelectedItemPosition() != 0) {

                    studfliteredList.get(selpos).setPreviousState(studfliteredList.get(selpos).getAttendanceType());

                    if (spMark.getSelectedItem().toString().equalsIgnoreCase("Unapproved Leave"))
                        studfliteredList.get(selpos).setAttendanceType("A");
                    else if (spMark.getSelectedItem().toString().equalsIgnoreCase("Approved Leave"))
                        studfliteredList.get(selpos).setAttendanceType("LR");
                    else if (spMark.getSelectedItem().toString().equalsIgnoreCase("Late"))
                        studfliteredList.get(selpos).setAttendanceType("L");
                    else if (spMark.getSelectedItem().toString().equalsIgnoreCase("Present"))
                        studfliteredList.get(selpos).setAttendanceType("P");

                    studfliteredList.get(selpos).setReason(spReason.getSelectedItem().toString());

                    for (int i = 0; i < studList.size(); i++) {
                        for (int j = 0; j < studfliteredList.size(); j++) {
                            if (studfliteredList.get(j).getStudentId() == studList.get(i).getStudentId()) {
                                studList.set(i, studfliteredList.get(j));
                            }
                        }

                        Log.v(TAG, " Pos " + i + " name - " + studList.get(i).getStudentName() + " attState - " + studList.get(i).getAttendanceType());
                        Log.v(TAG, " Pos " + i + " name - " + studList.get(i).getStudentName() + " Prev - " + studList.get(i).getPreviousState());
                    }
                    studAttandenceAdapter.notifyDataSetChanged();
                    slideup(0, studList);
                } else {
                    Toast.makeText(this, "Please Select the Reason", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void markAttendance(String attendStatus) {
        if (!attendStatus.equalsIgnoreCase("LR")) {
            studfliteredList.get(selpos).setAttendanceType(attendStatus);
            int index = studList.indexOf(studfliteredList.get(selpos));
            studList.get(index).setAttendanceType(attendStatus);
            studAttandenceAdapter.update(studfliteredList);   //updating the filtered list in adapter class
            slideup(selpos, studfliteredList); //closing the list after option selected
        } else {
            rlHiddenreason.setVisibility(View.GONE);
            rlHiddenreason.setVisibility(View.VISIBLE);
        }

    }

    void getHolidaysForStudentAttendance(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("yearId", "" + selYear);
            jsonObject.put("monthId", selMon);
            jsonObject.put("branchId", branchId);
            jsonObject.put("courseId", courseId);
            jsonObject.put("classId", classId);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Json Obj - " + jsonObject);
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + new AppUrls().GetHolidaysForStudentAttendance);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(new AppUrls().GetHolidaysForStudentAttendance)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();
                    Log.v(TAG, "GetHolidaysForStaffAttendance responce - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                                    //Split string with comma
                                    holidayArray = ParentjObject.getString("holidayDates").split(",");

                                    Log.v(TAG,"holidays "+ Arrays.toString(holidayArray));

                                    List<String> list = Arrays.asList(holidayArray);
                                    if (list.contains(selDate)){
                                        ll.setVisibility(View.GONE);
                                        rvStdnList.setVisibility(View.GONE);
                                        tvHoliday.setVisibility(View.VISIBLE);
                                    }else{
                                        ll.setVisibility(View.VISIBLE);
                                        rvStdnList.setVisibility(View.VISIBLE);
                                        tvHoliday.setVisibility(View.GONE);
                                        getStudents();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }


    void getStudents(){

        ProgressDialog loading = new ProgressDialog(TeacherStdnTakeAttendance.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "HW Types response - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
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
                                    studAttandenceAdapter = new StudAttandenceAdapter(TeacherStdnTakeAttendance.this, studList);
                                    rvStdnList.setAdapter(studAttandenceAdapter);
                                    getSectionDayReports();
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
                        loading.dismiss();

                    }
                });

            }
        });

    }



    //    Getting Section Attendance report of  Students

    void getSectionDayReports(){
        ProgressDialog loading = new ProgressDialog(TeacherStdnTakeAttendance.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Attendance SectionDayreport - " + new AppUrls().Attendance_GetSectionDayReports +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                + "&currentDate=" + reqDate);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetSectionDayReports +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                        + "&currentDate=" + reqDate)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Attendance Section Reports - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);

                                secAttdStatus = Integer.parseInt(ParentjObject.getString("StatusCode"));
                                if (secAttdStatus == 200) {
                                    sectionAttendanceId = ParentjObject.getString("sectionAttendanceId");
                                    getAbsentStudents();
                                    studAttandenceAdapter.notifyDataSetChanged();
                                } else if (secAttdStatus == 300) {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }
        });

    }
    //    Getting Absent Students

    void getAbsentStudents(){
        ProgressDialog loading = new ProgressDialog(TeacherStdnTakeAttendance.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Attendance Absent request - " + new AppUrls().Attendance_GetAbsentStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                + "&currentDate=" + reqDate);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetAbsentStudents +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&sectionId=" + sectionId
                        + "&currentDate=" + reqDate)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Attendance Absent response - " + jsonResp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    if (ParentjObject.getJSONArray("studentAttendanceArray").length() > 0) {

                                        JSONArray jsonArr = ParentjObject.getJSONArray("studentAttendanceArray").getJSONObject(0).
                                                getJSONArray("studentList");

                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<TeacherAttendStudentObj>>() {
                                        }.getType();

                                        List<TeacherAttendStudentObj> studAbsentList = new ArrayList<>();
                                        studAbsentList.clear();
                                        studAbsentList.addAll(gson.fromJson(jsonArr.toString(), type));
                                        Log.v(TAG, "studAbsentList - " + studAbsentList.size());

                                        for (TeacherAttendStudentObj absentStudentObj : studAbsentList) {
                                            for (TeacherAttendStudentObj attendStudentObj : studList) {
                                                if (absentStudentObj.getStudentId().equalsIgnoreCase(attendStudentObj.getStudentId())) {
                                                    attendStudentObj.setPreviousState(absentStudentObj.getAttendanceType());
                                                    attendStudentObj.setAttendanceType(absentStudentObj.getAttendanceType());
                                                    attendStudentObj.setAttendanceId(absentStudentObj.getAttendanceId());
                                                }
                                            }
                                        }

                                        for (TeacherAttendStudentObj attendStudentObj : studList) {
                                            Log.v(TAG, "attendStudentObj - " + attendStudentObj.getAttendanceType());
                                            Log.v(TAG, "attendStudentObj - " + attendStudentObj.getAttendanceId());
                                        }

                                        studAttandenceAdapter.notifyDataSetChanged();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }
        });
    }
    //    Posting the Student Subbmissions to the Server
    void postAttendance(){
        ProgressDialog  loading = new ProgressDialog(TeacherStdnTakeAttendance.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONObject jsonObject = new JSONObject();
        String url = null;
        try {

            jsonObject.put("attenDate", "" + reqDate);
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("sectionId", sectionId);
            jsonObject.put("branchId", branchId);
            jsonObject.put("totalStudents", studList.size());

            JSONArray jsonArray = new JSONArray();
            url = new AppUrls().Attendance_PostInsertStudents;
            if (secAttdStatus == 300) {

                jsonObject.put("createdBy", "1");

                for (int i = 0; i < studList.size(); i++) {
                    if (!studList.get(i).getAttendanceType().equalsIgnoreCase("blank")) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("studentId", "" + studList.get(i).getStudentId());
                        jObj.put("reason", "" + studList.get(i).getReason());
                        jObj.put("attendanceType", "" + studList.get(i).getAttendanceType());
                        jsonArray.put(jObj);
                    }
                }

                jsonObject.put("insertRecords", jsonArray);

            } else if (secAttdStatus == 200) {
                jsonObject.put("updatedBy", getIntent().getStringExtra("userId"));
                jsonObject.put("sectionAttendanceId", sectionAttendanceId);

                for (int i = 0; i < studList.size(); i++) {
                    if (!studList.get(i).getAttendanceType().equalsIgnoreCase("blank")) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("studentId", "" + studList.get(i).getStudentId());
                        jObj.put("reason", "" + studList.get(i).getReason());
                        jObj.put("attendanceType", "" + studList.get(i).getAttendanceType());
                        jObj.put("attendanceId", "" + studList.get(i).getAttendanceId());
                        jObj.put("previousState", "" + studList.get(i).getPreviousState());
                        jsonArray.put(jObj);
                    }
                }

                jsonObject.put("updateRecords", jsonArray);
                url = new AppUrls().Attendance_PostUpdateStudents;
            }


            Log.v(TAG, "Json Obj - " + jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + url);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null) {
                    String jsonResp = response.body().string();

                    Log.v(TAG, "Attendanc Update and Insertion Status responce - " + jsonResp);

                    try {
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherStdnTakeAttendance.this, "Attendance Posted Sucessfully", Toast.LENGTH_SHORT).show();
                                    finish();
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
                        loading.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        if (rlHiddenreason.isShown())
            rlHiddenreason.setVisibility(View.GONE);

        else if (rlHiddenreason.isShown())
            rlHiddenreason.setVisibility(View.GONE);

        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

    }

    public class StudAttandenceAdapter extends RecyclerView.Adapter<StudAttandenceAdapter.ViewHolder> implements Filterable {

        Context _context;
        List<TeacherAttendStudentObj> list, list_filtered;

        StudAttandenceAdapter(Context context, List<TeacherAttendStudentObj> list) {
            _context = context;
            this.list = list;
            list_filtered = list;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(_context).inflate(R.layout.attend_item, viewGroup, false);
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
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

            viewHolder.roll.setText(list_filtered.get(i).getRollNumber());
            viewHolder.name.setText(list_filtered.get(i).getStudentName());
            if (list_filtered.get(i).getProfilePic().equalsIgnoreCase("NA")) {
                viewHolder.tvNameImage.setText((list_filtered.get(i).getStudentName().charAt(0) + "").toUpperCase());
                viewHolder.tvNameImage.setVisibility(View.VISIBLE);
                viewHolder.imgProfile.setVisibility(View.GONE);
            } else {
                viewHolder.tvNameImage.setVisibility(View.GONE);
                viewHolder.imgProfile.setVisibility(View.VISIBLE);
                Picasso.with(TeacherStdnTakeAttendance.this).load(new AppUrls().GetstudentProfilePic + list_filtered.get(i).getProfilePic()).
                        placeholder(R.drawable.user_default).into(viewHolder.imgProfile);

            }

            switch (list_filtered.get(i).getAttendanceType()) {
                case "LR":
                    viewHolder.option.setImageResource(R.drawable.ic_att_absentinfo);
                    viewHolder.tvAttStatus.setText("Approved Leave");
                    break;
                case "L":
                    viewHolder.option.setImageResource(R.drawable.ic_att_late);
                    viewHolder.tvAttStatus.setText("Late");
                    break;
                case "A":
                    viewHolder.tvAttStatus.setText("Unapproved Leave");
                    viewHolder.option.setImageResource(R.drawable.ic_att_absent);
                    break;
                case "blank":
                    if (secAttdStatus == 300) {
                        viewHolder.tvAttStatus.setText("Present");
                    }
                    else {
                        viewHolder.tvAttStatus.setText("Present");
                    }
                    break;
                default:
                    viewHolder.tvAttStatus.setText("Present");
                    break;
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TeacherStdnTakeAttendance teacherStdnTakeAttendance = (TeacherStdnTakeAttendance) _context;
                    if (teacherStdnTakeAttendance.isPanelShown()) {

                    } else {
                        getStudentLeaveRequestInfo("" + list_filtered.get(i).getStudentId(), reqDate);
                        teacherStdnTakeAttendance.slideup(i, list_filtered);
                    }
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

                            if (s.getRollNumber().toLowerCase().contains(string.toLowerCase())
                                    || s.getStudentName().toLowerCase().contains(string.toLowerCase())) {
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


            ImageView option, imgProfile;
            TextView roll, name, tvNameImage, tvAttStatus;

            ViewHolder(View itemView) {
                super(itemView);

                option = itemView.findViewById(R.id.option);
                roll = itemView.findViewById(R.id.rollNum);
                name = itemView.findViewById(R.id.name);
                tvNameImage = itemView.findViewById(R.id.tv_name_image);
                imgProfile = itemView.findViewById(R.id.img_profile);
                tvAttStatus = itemView.findViewById(R.id.tv_att_status);

            }

        }
    }

    void getStudentLeaveRequestInfo(String stdntId, String date){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.Attendance_GetStudentLeaveRequestInfo +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&studentId=" + stdntId + "&date=" + date);


        Request request = new Request.Builder()
                .url(AppUrls.Attendance_GetStudentLeaveRequestInfo +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&studentId=" + stdntId + "&date=" + date)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp  = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//                        JSONArray jsonArr = ParentjObject.getJSONArray("staffForAttendance");


                                    llLr.setVisibility(View.VISIBLE);
                                    tvLr.setVisibility(View.VISIBLE);

                                    tvLrFrom.setText("" + displaysdf.format(serversdf.parse(ParentjObject.getString("leaveFrom"))));
                                    tvLrTo.setText("" + displaysdf.format(serversdf.parse(ParentjObject.getString("LeaveTo"))));
                                    tvLrReason.setText("" + ParentjObject.getString("reason"));
                                    tvLrDescp.setText("" + ParentjObject.getString("description"));



                                } else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {

                                    llLr.setVisibility(View.GONE);
                                    tvLr.setVisibility(View.GONE);

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }

}