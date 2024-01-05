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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentOnlineTestResult;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignedReportFragment extends Fragment {
    private static final String TAG = "SriRam -" + AssignedReportFragment.class.getName();
    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    String studentId = "", branchId = "";

    String serverTime = "";

    Activity mActivity;

    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();
    
    View viewAssignedReportFragment;
    Unbinder unbinder;

    @BindView(R.id.rv_exams)
    RecyclerView rvExams;

    public AssignedReportFragment() {
        // Required empty public constructor
    }

   

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewAssignedReportFragment = inflater.inflate(R.layout.fragment_assigned_report, container, false);
        unbinder = ButterKnife.bind(this,viewAssignedReportFragment);
        init();
        return viewAssignedReportFragment;
    }

    private void init() {

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            studentId = sObj.getStudentId();
            branchId = sObj.getBranchId();
        }
        else {
            if (mActivity!=null) {
                studentId = mActivity.getIntent().getStringExtra("studentId");
                branchId = mActivity.getIntent().getStringExtra("branchId");
            }
        }

        

        rvExams.setLayoutManager(new LinearLayoutManager(mActivity));

        if (NetworkConnectivity.isConnected(mActivity)) {
            getServerTime();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

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
                if (NetworkConnectivity.isConnected(mActivity)) {
                    getPreviousExams();

                } else {
                    new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close), false);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    if (NetworkConnectivity.isConnected(mActivity)) {
                        getPreviousExams();

                    } else {
                        new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close), false);
                    }
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        if (NetworkConnectivity.isConnected(mActivity)) {
                            getPreviousExams();

                        } else {
                            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    void getPreviousExams(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + branchId+"&flag=completed");

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + branchId+"&flag=completed")
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
                        },2000);
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
                            },2000);
                        }
                    });
                }else {
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

                            Collections.reverse(studentOnlineTests);


                            if (studentOnlineTests.size() > 0) {
//
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvExams.setAdapter(new DateAdapter(studentOnlineTests));
                                    }
                                });
                            }

                        }
                    }catch(Exception e){

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
                        },2000);
                    }
                });
            }
        });

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


    class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder>{

        List<StudentOnlineTests> keys;

        public DateAdapter(List<StudentOnlineTests> keys) {
            this.keys = keys;
        }

        @NonNull
        @Override
        public DateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DateAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_prev_exams,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull DateAdapter.ViewHolder holder, int position) {

            holder.tvSubName.setText(keys.get(position).getMonthName());
            holder.rvVids.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
            holder.rvVids.setAdapter(new PreviousExamAdapter(keys.get(position).getTests()));
            holder.tvViewAll.setVisibility(View.GONE);


        }

        @Override
        public int getItemCount() {
            return keys.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName,tvViewAll;
            RecyclerView rvVids;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvViewAll = itemView.findViewById(R.id.tv_view_all);
                rvVids = itemView.findViewById(R.id.rv_vids);
            }
        }
    }


    class PreviousExamAdapter extends RecyclerView.Adapter<PreviousExamAdapter.ViewHolder>{

        List<StudentOnlineTestObj> listTests;

        public PreviousExamAdapter(List<StudentOnlineTestObj> listTests) {
            this.listTests = listTests;
        }

        @NonNull
        @Override
        public PreviousExamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PreviousExamAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_upcoming_exams_list,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull PreviousExamAdapter.ViewHolder holder, int position) {
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTests.get(position).getTestStartDate())));
                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTests.get(position).getTestStartDate())));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listTests.get(position).getStudentTestStatus().equalsIgnoreCase("COMPLETED")){
                            Intent i = new Intent(mActivity, StudentOnlineTestResult.class);
                            i.putExtra("studentId", studentId);
                            i.putExtra("studentTest", (Serializable) listTests.get(position));
//                            i.putExtra("testId",listTests.get(position).getTestId());
//                            i.putExtra("testTitle",listTests.get(position).getTestName());
                            mActivity.startActivity(i);
                            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }else{
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(getString(R.string.app_name))
                                    .setMessage("Sorry No results for Missed Exams!")
                                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })

                                    .setCancelable(false)
                                    .show();
                        }
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvTestName.setText(listTests.get(position).getTestCategoryName());
            holder.tvSubject.setText(listTests.get(position).getTestName());
            holder.tvStatus.setText(listTests.get(position).getStudentTestStatus());
        }

        @Override
        public int getItemCount() {
            return listTests.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate,tvSubject,tvTestName,tvTime,tvStatus;

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

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}