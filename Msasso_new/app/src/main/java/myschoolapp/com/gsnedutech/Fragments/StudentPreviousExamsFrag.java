package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import java.util.Collections;
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
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class StudentPreviousExamsFrag extends Fragment implements StudentLiveExamsTabs.MonthChangeListner {

    private static final String TAG = "SriRam -" + StudentPreviousExamsFrag.class.getName();

    MyUtils utils = new MyUtils();

    View viewStudentPreviousExamsFrag;
    Unbinder unbinder;

    @BindView(R.id.rv_previous_exams)
    RecyclerView rvPreviousExams;

    @BindView(R.id.tv_view_all)
    TextView tvViewAll;

    @BindView(R.id.tv_no_previous)
    TextView tvNoPrevious;

    Activity mActivity;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";
    String currentMonth = "";

    HashMap<String, List<StudentOnlineTestObj>> filterList = new HashMap<>();

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();
    List<StudentOnlineTestObj> listTest = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    public StudentPreviousExamsFrag() {
        // Required empty public constructor
    }

    public static StudentPreviousExamsFrag newInstance(String monthYear) {

        Bundle args = new Bundle();
        args.putString("monthYear", monthYear);

        StudentPreviousExamsFrag fragment = new StudentPreviousExamsFrag();
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
        viewStudentPreviousExamsFrag = inflater.inflate(R.layout.fragment_student_previous_exams, container, false);
        unbinder = ButterKnife.bind(this, viewStudentPreviousExamsFrag);

        init();

//        currentMonth = new SimpleDateFormat("MMMM yyyy").format(new Date());

        getPreviousTests();

        tvViewAll.setOnClickListener(view -> {
            startActivity(new Intent(mActivity, StudentExamsListingActivity.class));
            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });


        return viewStudentPreviousExamsFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
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
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId() + "&flag=completed")
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        }, 2000);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                }
                            }, 2000);
                        }
                    });
                } else {
                    ResponseBody responseBody = response.body();
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

//                            Collections.reverse(studentOnlineTests);

                            for (StudentOnlineTests onlineTests : studentOnlineTests){
                                filterList.put(onlineTests.getMonthName()+" "+ onlineTests.getYearId(),onlineTests.getTests());
                            }

                            if (studentOnlineTests.size() > 0) {
//                                Collections.reverse(studentOnlineTests);
                                listTest.clear();
                                if (filterList!=null && filterList.get(currentMonth)!=null && filterList.get(currentMonth).size()>0){
                                    listTest.addAll(filterList.get(currentMonth));
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

                        }
                    } catch (Exception e) {

                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        }, 2000);
                    }
                });
            }
        });

    }

    @Override
    public void onMonthChanged(String month) {
        currentMonth = month;
        if (studentOnlineTests.size() > 0) {
//                                Collections.reverse(studentOnlineTests);
            listTest.clear();
            if (filterList!=null && filterList.get(currentMonth)!=null && filterList.get(currentMonth).size()>0){
                listTest.addAll(filterList.get(currentMonth));
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
    }

    class PreviousExamAdapter extends RecyclerView.Adapter<PreviousExamAdapter.ViewHolder> {

        List<StudentOnlineTestObj> listTests;

        public PreviousExamAdapter(List<StudentOnlineTestObj> listTests) {
            this.listTests = listTests;
            Collections.reverse(listTests);
        }

        @NonNull
        @Override
        public PreviousExamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PreviousExamAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_exams, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PreviousExamAdapter.ViewHolder holder, int position) {
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
//                        i.putExtra("testId", listTests.get(position).getTestId());
//                        i.putExtra("testTitle", listTests.get(position).getTestName());
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