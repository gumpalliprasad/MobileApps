package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentExamsListingActivity;
import myschoolapp.com.gsnedutech.StudentLiveExamsTabs;
import myschoolapp.com.gsnedutech.StudentOnlineTestResult;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class StudentOTPreviousExamsFrag extends Fragment implements StudentLiveExamsTabs.MonthChangeListner {

    private static final String TAG = "SriRam -" + StudentOTPreviousExamsFrag.class.getName();

    MyUtils utils = new MyUtils();

    View viewStudentPreviousExamsFrag;
    Unbinder unbinder;

    @BindView(R.id.rv_previous_exams)
    RecyclerView rvPreviousExams;

    @BindView(R.id.tv_view_all)
    TextView tvViewAll;

    @BindView(R.id.tv_no_previous)
    TextView tvNoPrevious;

    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;

    Activity mActivity;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "";
    String currentMonth,currentYear;

    HashMap<String, List<StudentOnlineTestObj>> filterList = new HashMap<>();

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();
    List<StudentOnlineTestObj> listTest = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    public StudentOTPreviousExamsFrag() {
        // Required empty public constructor
    }

    public static StudentOTPreviousExamsFrag newInstance(String monthYear) {

        Bundle args = new Bundle();
        args.putString("monthYear", monthYear);

        StudentOTPreviousExamsFrag fragment = new StudentOTPreviousExamsFrag();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
//            currentMonth = getArguments().getString("monthYear");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentPreviousExamsFrag = inflater.inflate(R.layout.fragment_student_ot_previous_exams, container, false);
        unbinder = ButterKnife.bind(this, viewStudentPreviousExamsFrag);

        currentMonth = new SimpleDateFormat("MMMM").format(new Date());
        currentYear = new SimpleDateFormat("yyyy").format(new Date());
        init();

        tvMonthYear.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
//        currentMonth = new SimpleDateFormat("MMMM yyyy").format(new Date());

        getPreviousTests();

        tvViewAll.setOnClickListener(view -> {
            startActivity(new Intent(mActivity, StudentExamsListingActivity.class));
            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

        viewStudentPreviousExamsFrag.findViewById(R.id.cv_date_time).setOnClickListener(new View.OnClickListener() {
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

                        currentMonth = mon;
                        currentYear = year+"";

                        tvMonthYear.setText(mon + " " + year);

                        if (studentOnlineTests.size() > 0) {
                            Collections.reverse(studentOnlineTests);
                            listTest.clear();
                            if (filterList!=null && filterList.get(currentMonth+" "+currentYear)!=null && filterList.get(currentMonth+" "+currentYear).size()>0){
                                listTest.addAll(filterList.get(currentMonth+" "+currentYear));
                            }
//                                for (int i = 0; i < studentOnlineTests.size(); i++) {
//                                    listTest.addAll(studentOnlineTests.get(i).getTests());
//                                }
                            if (listTest.size() > 0) {
                                rvPreviousExams.setAdapter(new PreviousExamAdapter(listTest));
                                tvNoPrevious.setVisibility(View.GONE);
                                tvViewAll.setVisibility(View.VISIBLE);
                                rvPreviousExams.setVisibility(View.VISIBLE);
                            }
                            else {
                                tvNoPrevious.setVisibility(View.VISIBLE);
                                tvViewAll.setVisibility(View.GONE);
                                rvPreviousExams.setVisibility(View.GONE);
                            }
                        }

//                        getHomeWorks();



                    }
                });
                pickerDialog.show(getChildFragmentManager(), "MonthYearPickerDialog");

            }
        });


        return viewStudentPreviousExamsFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        rvPreviousExams.setLayoutManager(new LinearLayoutManager(mActivity));

    }

    void getPreviousTests() {
        utils.showLoader(mActivity);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=completed");

        Request get = new Request.Builder()
//                .url("http://13.233.58.88:9000/getStudentTests?schemaName=myschoolgorankr&studentId=144&branchId=1&flag=completed")
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=completed")
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                mActivity.runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null){
                    ResponseBody responseBody = response.body();
                    String resp = responseBody.string();
                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = parentjObject.getJSONArray("StudentTest");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StudentOnlineTests>>() {
                            }.getType();

                            studentOnlineTests.clear();
                            studentOnlineTests.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "studentOnlineTests size- " + studentOnlineTests.size());

//                            Collections.reverse(studentOnlineTests);

                            for (StudentOnlineTests onlineTests : studentOnlineTests){
                                filterList.put(onlineTests.getMonthName()+" "+ onlineTests.getYearId(),onlineTests.getTests());
                            }

                            if (studentOnlineTests.size() > 0) {
//                                Collections.reverse(studentOnlineTests);
                                listTest.clear();
                                if (filterList!=null && filterList.get(currentMonth+" "+currentYear)!=null && filterList.get(currentMonth+" "+currentYear).size()>0){
                                    listTest.addAll(filterList.get(currentMonth+" "+currentYear));
                                }
//                                for (int i = 0; i < studentOnlineTests.size(); i++) {
//                                    listTest.addAll(studentOnlineTests.get(i).getTests());
//                                }
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listTest.size() > 0) {
                                            rvPreviousExams.setAdapter(new PreviousExamAdapter(listTest));
                                            tvNoPrevious.setVisibility(View.GONE);
                                            tvViewAll.setVisibility(View.VISIBLE);
                                            rvPreviousExams.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            tvNoPrevious.setVisibility(View.VISIBLE);
                                            tvViewAll.setVisibility(View.GONE);
                                            rvPreviousExams.setVisibility(View.GONE);

                                        }
                                    }
                                });

                            }

                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                        }
                    } catch (Exception e) {

                    }
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

    @Override
    public void onMonthChanged(String month) {
//        currentMonth = month;
//        if (studentOnlineTests.size() > 0) {
//                                Collections.reverse(studentOnlineTests);
//            listTest.clear();
//            if (filterList!=null && filterList.get(currentMonth)!=null && filterList.get(currentMonth).size()>0){
//                listTest.addAll(filterList.get(currentMonth));
//            }
////                                for (int i = 0; i < studentOnlineTests.size(); i++) {
////                                    listTest.addAll(studentOnlineTests.get(i).getTests());
////                                }
//            if (listTest.size() > 0) {
//                rvPreviousExams.setAdapter(new PreviousExamAdapter(listTest));
//                tvNoPrevious.setVisibility(View.GONE);
//                tvViewAll.setVisibility(View.VISIBLE);
//                rvPreviousExams.setVisibility(View.VISIBLE);
//            }
//            else {
//                tvNoPrevious.setVisibility(View.VISIBLE);
//                tvViewAll.setVisibility(View.GONE);
//                rvPreviousExams.setVisibility(View.GONE);
//            }
//        }
    }

    class PreviousExamAdapter extends RecyclerView.Adapter<PreviousExamAdapter.ViewHolder> {

        List<StudentOnlineTestObj> listTests;

        public PreviousExamAdapter(List<StudentOnlineTestObj> listTests) {
            this.listTests = listTests;
            Collections.reverse(listTests);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_exams, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                    if (listTests.get(position).getStudentTestStatus().equalsIgnoreCase("COMPLETED")) {
                        Intent i = new Intent(mActivity, StudentOnlineTestResult.class);
                        i.putExtra("studentId", sObj.getStudentId());
                        i.putExtra("studentTest", (Serializable) listTests.get(position));
                        startActivity(i);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else if (listTests.get(position).getStudentTestStatus().equalsIgnoreCase("MISSED")) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(getString(R.string.app_name))
                                .setMessage("Sorry you had missed this exam")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .setCancelable(false)
                                .show();
                    } else if (listTests.get(position).getStudentTestStatus().equalsIgnoreCase("INPROGRESS")) {
                        new AlertDialog.Builder(mActivity)
                                .setTitle(getString(R.string.app_name))
                                .setMessage("Sorry you didnt Submitted this")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .setCancelable(false)
                                .show();
                    } else {
                        Intent onlineTestIntent = new Intent(mActivity, StudentOnlineTestResult.class);
                        onlineTestIntent.putExtra("studentId", sObj.getStudentId());
                        onlineTestIntent.putExtra("studentTest", (Serializable) listTests.get(position));
                        startActivity(onlineTestIntent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


                    }
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