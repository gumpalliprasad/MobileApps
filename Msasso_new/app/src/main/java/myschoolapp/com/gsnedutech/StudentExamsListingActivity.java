package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTestObj;
import myschoolapp.com.gsnedutech.Models.StudentOnlineTests;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StudentExamsListingActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + StudentExamsListingActivity.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    SharedPreferences sh_Pref;
    StudentObj sObj;
    String studentId = "";

    String serverTime = "";


    List<StudentOnlineTests> studentOnlineTests = new ArrayList<>();

    @BindView(R.id.rv_exams)
    RecyclerView rvExams;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exams_listing);

        ButterKnife.bind(this);
        init();
    }

    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        studentId = sObj.getStudentId();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        rvExams.setLayoutManager(new LinearLayoutManager(this));

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
            utils.alertDialog(1, StudentExamsListingActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getServerTime();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    private void getServerTime() {

        utils.showLoader(StudentExamsListingActivity.this);



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
                if (NetworkConnectivity.isConnected(StudentExamsListingActivity.this)) {
                    getPreviousExams();

                } else {
                    new MyUtils().alertDialog(1, StudentExamsListingActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close), false);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    if (NetworkConnectivity.isConnected(StudentExamsListingActivity.this)) {
                        getPreviousExams();

                    } else {
                        new MyUtils().alertDialog(1, StudentExamsListingActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close), false);
                    }
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        if (NetworkConnectivity.isConnected(StudentExamsListingActivity.this)) {
                            getPreviousExams();

                        } else {
                            new MyUtils().alertDialog(1, StudentExamsListingActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

//    void getUpcomingExams(){
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//
//        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId()+"&flag=active");
//
//        Request get = new Request.Builder()
//                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId()+"&flag=active")
//                .build();
//
//        client.newCall(get).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//               runOnUiThread(new Runnable() {
//                   @Override
//                   public void run() {
//                       new Handler().postDelayed(new Runnable() {
//                           @Override
//                           public void run() {
//                               loading.dismiss();
//                           }
//                       },2000);
//                   }
//               });
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                ResponseBody responseBody = response.body();
//                if (!response.isSuccessful()) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    loading.dismiss();
//                                }
//                            },2000);
//                        }
//                    });
//                }else {
//                    String resp = responseBody.string();
//
//                    utils.showLog(TAG, "response- " + resp);
//                    try {
//                        JSONObject ParentjObject = new JSONObject(resp);
//                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//                            JSONArray jsonArray = ParentjObject.getJSONArray("StudentTest");
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<List<StudentOnlineTests>>() {
//                            }.getType();
//
//                            studentOnlineTests.clear();
//                            studentOnlineTests.addAll(gson.fromJson(jsonArray.toString(), type));
//                            utils.showLog(TAG, "studentOnlineTests size- " + studentOnlineTests.size());
//
//
//                            if (studentOnlineTests.size() > 0) {
//                                Collections.reverse(studentOnlineTests);
//                                listTest.clear();
//                                for (int i=0;i<studentOnlineTests.size();i++){
//                                    listTest.addAll(studentOnlineTests.get(i).getTests());
//                                }
//
//                                List<StudentOnlineTestObj> listUpcoming = new ArrayList<>();
//
//                                for (int i = 0; i < listTest.size(); i++) {
//                                    if (getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestStartDate()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime)) != null) {
//                                        listUpcoming.add(listTest.get(i));
//                                    } else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listTest.get(i).getTestEndDate()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
////                                        liveExam = listTest.get(i);
//                                    }
//                                }
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
////
//                                        rvExams.setAdapter(new UpcomingAdapter(listUpcoming));
//
//                                    }
//                                });
//                            }
//
//
//
//
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                loading.dismiss();
//                            }
//                        },2000);
//                    }
//                });
//            }
//        });
//    }


    void getPreviousExams(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG, "url " + AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId()+"&flag=completed");

        Request get = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTests + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + studentId + "&branchId=" + sObj.getBranchId()+"&flag=completed")
                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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
                                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
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
            Collections.reverse(keys);
        }

        @NonNull
        @Override
        public DateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DateAdapter.ViewHolder(LayoutInflater.from(StudentExamsListingActivity.this).inflate(R.layout.item_prev_exams,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull DateAdapter.ViewHolder holder, int position) {

            holder.tvSubName.setText(keys.get(position).getMonthName());
            holder.rvVids.setLayoutManager(new LinearLayoutManager(StudentExamsListingActivity.this,RecyclerView.HORIZONTAL,false));
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
            return new PreviousExamAdapter.ViewHolder(LayoutInflater.from(StudentExamsListingActivity.this).inflate(R.layout.item_upcoming_exams,parent,false));
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
                            Intent i = new Intent(StudentExamsListingActivity.this, StudentOnlineTestResult.class);
                            i.putExtra("testId",listTests.get(position).getTestId());
                            i.putExtra("testTitle",listTests.get(position).getTestName());
                            i.putExtra("studentId", sObj.getStudentId());
                            i.putExtra("studentTest",(Serializable)listTests.get(position));
                            startActivity(i);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }else{
                            new AlertDialog.Builder(StudentExamsListingActivity.this)
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
    

}