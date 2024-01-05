package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentsLeaveReq;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherStdntLRinDetail;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherStdntLeaveReqFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherStdntLeaveReqFrag extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    Activity mActivity;
    View viewTeacherStdntLRFrag;
    Unbinder unbinder;



    private static final String TAG = "SriRam -" + TeacherStdntLeaveReqFrag.class.getName();
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.cv_date_time)
    CardView cvDateTime;

    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    @BindView(R.id.tv_no_requests)
    TextView tvNoRequests;

    @BindView(R.id.rv_leave_req)
    RecyclerView rvLeaveReq;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
//    TeacherObj tObj;
//
//    int teacherId = 0;
    String sectionId = "";
    int selectedTab = 0;

    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMMM, yyyy");

    Date reqdate;
    Calendar calendar;
    String date = "";

    List<StudentsLeaveReq> reqList = new ArrayList<>();

    public TeacherStdntLeaveReqFrag() {
        // Required empty public constructor
    }


    public static TeacherStdntLeaveReqFrag newInstance(String secId) {
        TeacherStdntLeaveReqFrag fragment = new TeacherStdntLeaveReqFrag();
        Bundle args = new Bundle();
        args.putString("secId", secId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sectionId = getArguments().getString("secId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewTeacherStdntLRFrag = inflater.inflate(R.layout.fragment_teacher_stdnt_leave_req, container, false);
        unbinder = ButterKnife.bind(this, viewTeacherStdntLRFrag);

        init();

        return viewTeacherStdntLRFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
//        tObj = gson.fromJson(json, TeacherObj.class);
//
//        teacherId = tObj.getUserId();

        calendar = Calendar.getInstance();
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        date = displaysdf.format(new Date());
        tvPending.setOnClickListener(this);
        tvApproved.setOnClickListener(this);
        tvRejected.setOnClickListener(this);
        cvDateTime.setOnClickListener(this);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(dayOfMonth + "/" + (month + 1) + "/" + year);
            date = displaysdf.format(date1);
            updateLabel(date, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void updateLabel(String date, int change) {
        try {
            Date dateObj = displaysdf.parse(date);
            calendar.setTime(dateObj);
            if (change != 0)
                calendar.add(Calendar.DATE, change);
            dateObj = calendar.getTime();
            tvDate.setText(displaysdf.format(dateObj));

            reqdate = displaysdf.parse(tvDate.getText().toString());
            getLeaveRquests(sectionId,serversdf.format(reqdate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateLabel(date, 0);
    }

    //    Getting the Students for attendance
    void getLeaveRquests(String sectionId, String date){
       utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Leave request List - " + new AppUrls().Attendance_GetTotalLeaveRequestsDetailsByDate+"schemaName=" +sh_Pref.getString("schema","")+ "&sectionId=" + sectionId + "&date=" + date);

        Request request = new Request.Builder()
                .url(new AppUrls().Attendance_GetTotalLeaveRequestsDetailsByDate +"schemaName=" +sh_Pref.getString("schema","")+ "&sectionId=" + sectionId + "&date=" + date)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        tvPending.callOnClick();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "HW Types response - " + jsonResp);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                                    JSONArray jsonArr = ParentjObject.getJSONArray("studentsLeaveReq");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<StudentsLeaveReq>>() {
                                    }.getType();

                                    reqList.clear();
                                    reqList.addAll(gson.fromJson(jsonArr.toString(), type));
                                    Log.v(TAG, "studList - " + reqList.size());
                                    if (selectedTab==0)
                                        tvPending.callOnClick();
                                    else if (selectedTab==1)
                                        tvApproved.callOnClick();
                                    else tvRejected.callOnClick();

//
//                                    AllStudentsLeaveRequests.StudAttandenceAdapter studAttandenceAdapter = new AllStudentsLeaveRequests.StudAttandenceAdapter(AllStudentsLeaveRequests.this, reqList);
//                                    rvRequestList.setAdapter(studAttandenceAdapter);
//                                    rvRequestList.setVisibility(View.VISIBLE);
//                                    tvNoLeaverequests.setVisibility(View.GONE);

                                } else if (ParentjObject.getString("StatusCode").equalsIgnoreCase("300")) {
//                                    rvRequestList.setVisibility(View.GONE);
//                                    tvNoLeaverequests.setVisibility(View.VISIBLE);
                                    reqList.clear();
                                    tvPending.callOnClick();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
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

    private void setDatatoList(String leaveState,String status) {
        List<StudentsLeaveReq> list = new ArrayList<>();
        for (StudentsLeaveReq lrq:reqList){
            if (lrq.getLeaveReqestStatus().equalsIgnoreCase(leaveState)){
                list.add(lrq);
            }
        }
        if (list.size()>0){
            tvNoRequests.setVisibility(View.GONE);
            rvLeaveReq.setLayoutManager(new LinearLayoutManager(mActivity));
            rvLeaveReq.setAdapter(new StudLRAdapter(mActivity,list));
        }
        else {
            tvNoRequests.setVisibility(View.VISIBLE);
            tvNoRequests.setText(String.format("No %s Request for this Section",status));
        }

    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_pending:
                selectedTab = 0;
                tvPending.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvPending.setTextColor(Color.WHITE);
                tvApproved.setBackground(null);
                tvApproved.setTextColor(Color.rgb(73, 73, 73));
                tvApproved.setAlpha(0.75f);
                tvRejected.setBackground(null);
                tvRejected.setTextColor(Color.rgb(73, 73, 73));
                tvRejected.setAlpha(0.75f);
                setDatatoList("LR","Pending");
                break;
            case R.id.tv_approved:
                selectedTab = 1;
                tvApproved.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvApproved.setTextColor(Color.WHITE);
                tvPending.setBackground(null);
                tvPending.setTextColor(Color.rgb(73, 73, 73));
                tvPending.setAlpha(0.75f);
                tvRejected.setBackground(null);
                tvRejected.setTextColor(Color.rgb(73, 73, 73));
                tvRejected.setAlpha(0.75f);
                setDatatoList("ALR","Approved");
                break;
            case R.id.tv_rejected:
                selectedTab = 2;
                tvRejected.setBackgroundResource(R.drawable.bg_grad_tab_select);
                tvRejected.setTextColor(Color.WHITE);
                tvApproved.setBackground(null);
                tvApproved.setTextColor(Color.rgb(73, 73, 73));
                tvApproved.setAlpha(0.75f);
                tvPending.setBackground(null);
                tvPending.setTextColor(Color.rgb(73, 73, 73));
                tvPending.setAlpha(0.75f);
                setDatatoList("RLR","Rejected");
                break;
            case R.id.cv_date_time:
                DatePickerDialog dialog = new DatePickerDialog(mActivity, TeacherStdntLeaveReqFrag.this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
        }
    }


    public class StudLRAdapter extends RecyclerView.Adapter<StudLRAdapter.ViewHolder> {

        Context _context;
        List<StudentsLeaveReq> list;

        StudLRAdapter(Context context, List<StudentsLeaveReq> list) {
            this._context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public StudLRAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(_context).inflate(R.layout.item_teacher_stdnt_lr, viewGroup, false);
            return new StudLRAdapter.ViewHolder(view);
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
        public void onBindViewHolder(@NonNull final StudLRAdapter.ViewHolder holder, final int position) {

            holder.tvNAme.setText(list.get(position).getStudentName());
            holder.tvReason.setText(list.get(position).getReason());
            try {
                holder.tvNumDays.setText(getDateDifference(new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getLeaveFrom()),new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getLeaveTo()))+" days");
                holder.tvStartDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getLeaveFrom())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (list.get(position).getLeaveReqestStatus()){
                case "LR":
                    holder.tvStatus.setText("Pending");
                    holder.tvStatus.setTextColor(Color.parseColor("#F68D1E"));
                    break;
                case "ALR":
                    holder.tvStatus.setText("Approved");
                    holder.tvStatus.setTextColor(Color.parseColor("#037855"));
                    break;
                case "RLR":
                    holder.tvStatus.setText("Rejected");
                    holder.tvStatus.setTextColor(Color.parseColor("#B40E22"));
                    break;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, TeacherStdntLRinDetail.class);
                    intent.putExtra("reqObj", list.get(position));
                    intent.putExtra("sectionId", sectionId);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvNAme,tvStartDate,tvNumDays,tvReason,tvStatus;

            ViewHolder(View itemView) {
                super(itemView);

                tvNAme = itemView.findViewById(R.id.tv_student_name);
                tvStartDate = itemView.findViewById(R.id.tv_start_days);
                tvNumDays = itemView.findViewById(R.id.tv_num_days);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvReason = itemView.findViewById(R.id.tv_reason);
            }

        }
    }


    int getDateDifference(Date startDate, Date endDate) {

        int diffInDays = (int)( (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) );
        return diffInDays>=0?diffInDays+1:0;

    }
}