package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.LiveExamDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentLiveExamsTabs;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class StudentLiveExamsFrag extends Fragment implements StudentLiveExamsTabs.MonthChangeListner {

    private static final String TAG = "SriRam -" + StudentLiveExamsFrag.class.getName();
    String serverTime = "";

    MyUtils utils = new MyUtils();

    View viewStudentLiveExamsFrag;
    Unbinder unbinder;

    @BindView(R.id.tv_test_name)
    TextView tvTestName;
    @BindView(R.id.tv_test_type)
    TextView tvTestType;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_duration)
    TextView tvDuration;

    @BindView(R.id.rv_upcoming_exams)
    RecyclerView rvUpcomingExams;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    Activity mActivity;

    StudentOnlineTestObj liveExam = null;

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();

    List<StudentOnlineTestObj> listTest = new ArrayList<>();

    String currentMonth = "";

    HashMap<String, List<StudentOnlineTestObj>> filterList = new HashMap<>();

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }


    public StudentLiveExamsFrag() {
        // Required empty public constructor
    }

    public static StudentLiveExamsFrag newInstance(String monthYear) {

        Bundle args = new Bundle();
        args.putString("monthYear", monthYear);

        StudentLiveExamsFrag fragment = new StudentLiveExamsFrag();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            currentMonth = getArguments().getString("monthYear");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentLiveExamsFrag = inflater.inflate(R.layout.fragment_student_live_exams, container, false);
        unbinder = ButterKnife.bind(this, viewStudentLiveExamsFrag);

        init();

        return viewStudentLiveExamsFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        viewStudentLiveExamsFrag.findViewById(R.id.cv_start_exam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(mActivity, LiveExamDetails.class);
                    intent.putExtra("live", (Serializable) liveExam);
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
//        viewStudentLiveExamsFrag.findViewById(R.id.tv_view_all).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(mActivity, StudentExamsListingActivity.class));
//                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//            }
//        });

        rvUpcomingExams.setLayoutManager(new LinearLayoutManager(mActivity));

//        currentMonth = new SimpleDateFormat("MMMM yyyy").format(new Date());

        getServerTime();
    }

    private void getServerTime() {

        utils.showLoader(mActivity);


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetServerTime)
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetServerTime);

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getUpcomingExams();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    getUpcomingExams();
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        getUpcomingExams();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    void getUpcomingExams() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active");

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=active")
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(() -> utils.dismissDialog());
                } else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentOnlineTests>>() {
                            }.getType();

                            studentOnlineTests.clear();
                            studentOnlineTests.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "studentOnlineTests size- " + studentOnlineTests.size());

                            for (StudentOnlineTests onlineTests : studentOnlineTests){
                                filterList.put(onlineTests.getMonthName()+" "+ onlineTests.getYearId(),onlineTests.getTests());
                            }

                            if (studentOnlineTests.size() > 0) {
//                                Collections.reverse(studentOnlineTests);
                                listTest.clear();
//                                for (int i = 0; i < studentOnlineTests.size(); i++) {
//                                    listTest.addAll(studentOnlineTests.get(i).getTests());
//                                }

                                if (filterList!=null && filterList.get(currentMonth)!=null && filterList.get(currentMonth).size()>0){
                                    listTest.addAll(filterList.get(currentMonth));
                                }

                                mActivity.runOnUiThread(() -> setDatatoList());


                            }


                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    } catch (Exception e) {

                        utils.showLog(TAG, "error " + e.getMessage());
                    }
                }
                mActivity.runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }

    private void setDatatoList() {
        List<StudentOnlineTestObj> listUpcoming = new ArrayList<>();

        for (int i = 0; i < listTest.size(); i++) {
            try {
                if (getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestStartDate()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime)) != null) {
                    listUpcoming.add(listTest.get(i));
                } else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestEndDate()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime)) && (listTest.get(i).getStudentTestStatus().equalsIgnoreCase("Scheduled") || listTest.get(i).getStudentTestStatus().equalsIgnoreCase("Inprogress"))) {
                    liveExam = listTest.get(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (liveExam != null) {
            viewStudentLiveExamsFrag.findViewById(R.id.cv_start_exam).setVisibility(View.VISIBLE);
            tvTestName.setText(liveExam.getTestName());
            tvTestType.setText(liveExam.getTestCategoryName());
            tvDuration.setText(liveExam.getTestDuration());
            try {
                tvEndTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExam.getTestEndDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewStudentLiveExamsFrag.findViewById(R.id.cv_start_exam).setVisibility(View.GONE);
        }

        if (listUpcoming.size() > 0) {
            rvUpcomingExams.setAdapter(new UpcomingAdapter(listUpcoming));
        } else {
            viewStudentLiveExamsFrag.findViewById(R.id.cv_no_live_exams).setVisibility(View.VISIBLE);
        }
    }


    Date getDateDifference(Date endDate, Date startDate) {

        long duration = endDate.getTime() - startDate.getTime();

        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);

        utils.showLog(TAG, "diff " + diffInHours);

        if (diffInSeconds > 0) {
            Date d = new Date(diffInSeconds * 1000);

            return d;
        } else {
            return null;
        }

    }

    @Override
    public void onMonthChanged(String month) {
        currentMonth = month;
        liveExam = null;
        listTest.clear();
        if (filterList!=null && filterList.get(currentMonth)!=null && filterList.get(currentMonth).size()>0){
            listTest.addAll(filterList.get(currentMonth));
        }
        setDatatoList();
    }


    class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> {

        List<StudentOnlineTestObj> listTests;

        public UpcomingAdapter(List<StudentOnlineTestObj> listTests) {
            this.listTests = listTests;
        }

        @NonNull
        @Override
        public UpcomingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UpcomingAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_exams, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UpcomingAdapter.ViewHolder holder, int position) {

            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTests.get(position).getTestStartDate())));
                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTests.get(position).getTestStartDate())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvTestName.setText(listTests.get(position).getTestCategoryName());
            holder.tvSubject.setText(listTests.get(position).getTestName());
            holder.tvStatus.setText(listTests.get(position).getStudentTestStatus());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity,LiveExamDetails.class);
                    intent.putExtra("live", (Serializable) listTest.get(position));
                    startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                }
            });

        }

        @Override
        public int getItemCount() {
            return listTests.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate, tvSubject, tvTestName, tvTime, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvSubject = itemView.findViewById(R.id.tv_sub);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvTime = itemView.findViewById(R.id.tv_start_time);
                tvStatus = itemView.findViewById(R.id.tv_status);
            }
        }
    }
}