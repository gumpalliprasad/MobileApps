package myschoolapp.com.gsnedutech.OnlIneTest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OTStudentCompletedTest extends AppCompatActivity {

    private static final String TAG = OTStudentCompletedTest.class.getName();
    MyUtils utils = new MyUtils();
    ApiClient apiClient = new ApiClient();

    @BindView(R.id.rv_tests)
    RecyclerView rvTests;
    @BindView(R.id.tv_recordsotavailable)
    TextView tvRecordsotavailable;
    @BindView(R.id.img_back)
    ImageView imgBack;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    OTLoginResult sObj;
    ProgressDialog loading;

    String nowdt = "";
    ArrayList<OTCompleteObJ> examDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_student_completed_test);
        ButterKnife.bind(this);
        imgBack.setOnClickListener(view -> onBackPressed());
    }

    // get Previous Tests
    private void getStudentHisTests() {

        Log.v(TAG, "TestGetQue Url - " + AppUrls.GETSTUDENTEXAMBYID + "?studentId=" + sObj.getMStudentId() + "&schemaName=" + sh_Pref.getString("schema", "") + "&eStatus=COMPLETED");

        Request request = apiClient.getRequest(AppUrls.GETSTUDENTEXAMBYID + "?studentId=" + sObj.getMStudentId() + "&schemaName=" + sh_Pref.getString("schema", "") + "&eStatus=COMPLETED", sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
                runOnUiThread(() -> {
                    tvRecordsotavailable.setVisibility(View.VISIBLE);
                    rvTests.setVisibility(View.GONE);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {

                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("result");
                            Gson gson = new Gson();
                            Type type = new TypeToken<ArrayList<OTCompleteObJ>>() {
                            }.getType();
                            examDetails.clear();
                            examDetails.addAll(gson.fromJson(jsonArr.toString(), type));
                            if (examDetails.size() > 0) {
                                runOnUiThread(() -> {
                                    tvRecordsotavailable.setVisibility(View.GONE);
                                    rvTests.setVisibility(View.VISIBLE);
                                    rvTests.setLayoutManager(new LinearLayoutManager(OTStudentCompletedTest.this));
                                    rvTests.setAdapter(new OTExamsAdapter());
                                });
                            } else {
                                runOnUiThread(() -> {
                                    tvRecordsotavailable.setVisibility(View.VISIBLE);
                                    rvTests.setVisibility(View.GONE);
                                });
                            }

                        } else if (ParentjObject.getString("status").equalsIgnoreCase("204")) {
                            runOnUiThread(() -> {
                                tvRecordsotavailable.setVisibility(View.VISIBLE);
                                rvTests.setVisibility(View.GONE);
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            loading.dismiss();
                            tvRecordsotavailable.setVisibility(View.VISIBLE);
                            rvTests.setVisibility(View.GONE);
                        });

                    }
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // Initialization
    private void init() {

        loading = new ProgressDialog(OTStudentCompletedTest.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("otStudentObj", "");
        sObj = gson.fromJson(json, OTLoginResult.class);
        getStudentHisTests();
    }

    // refreshing the whole screen
    void refresh() {
        if (NetworkConnectivity.isConnected(OTStudentCompletedTest.this)) {
            getServerTime();
        } else {
            new MyUtils().alertDialog(1, OTStudentCompletedTest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkConnectivity.isConnected(this)) {
            refresh();
        } else {
            new MyUtils().alertDialog(1, OTStudentCompletedTest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
    }

    // Adapter for LiveExams RecyclerView
    private class OTExamsAdapter extends RecyclerView.Adapter<OTExamsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(OTStudentCompletedTest.this).inflate(R.layout.item_upcoming_ot_tests_new, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSubject.setText(examDetails.get(position).geteName());
            holder.tvTestName.setVisibility(View.INVISIBLE);
            try {
                holder.tvDate.setText(new SimpleDateFormat("dd MMM, yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDetails.get(position).getsTime())));
                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(examDetails.get(position).getsTime())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvTestStatus.setText(examDetails.get(position).geteStatus());
            holder.tvStartTest.setVisibility(View.VISIBLE);
            holder.tvStartTest.setText("View Results");
            holder.tvStartTest.setOnClickListener(view -> {
                if (!holder.tvStartTest.getText().toString().equalsIgnoreCase("Missed"))
                    getServerTimeStatus(position);
                else
                    Toast.makeText(OTStudentCompletedTest.this, "You Missed the Exam", Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public int getItemCount() {
            return examDetails.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate, tvSubject, tvTestName, tvTime, tvTestStatus, tvStartTest;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvSubject = itemView.findViewById(R.id.tv_sub);
                tvTestName = itemView.findViewById(R.id.tv_test_name);
                tvTime = itemView.findViewById(R.id.tv_start_time);
                tvTestStatus = itemView.findViewById(R.id.tv_status);
                tvStartTest = itemView.findViewById(R.id.tv_start_test);
            }
        }
    }

    // Get Server Time
    private void getServerTime() {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        Log.v("TAG", "TestGetQue Url - " + AppUrls.OT_URL + "systemTime");
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = apiClient.postRequest(AppUrls.OT_URL + "systemTime", body, sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("status").equalsIgnoreCase("200")) {
//                            Log.v(TAG, "Date - " + ParentjObject.getString("DateTime"));
                            nowdt = parentjObject.getString("DateTime");
                            runOnUiThread(() -> init());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Checking and Using Server Time
    private void getServerTimeStatus(int position) {
        ProgressDialog loading = new ProgressDialog(OTStudentCompletedTest.this);
        loading.setMessage("Please wait...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();
        String jsonResp = "null";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        Log.v("TAG", "TestGetQue Url - " + AppUrls.OT_URL + "systemTime");
        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Request request = new Request.Builder()
                .url(AppUrls.OT_URL + "systemTime")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loading.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                loading.dismiss();
                if (response.body() != null) {
                    try {
                        String jsonResp = response.body().string();
                        Log.v("status", "responseBody - " + jsonResp);
                        JSONObject ParentjObject = new JSONObject(jsonResp);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
                                try {
                                    initialNavigate(examDetails.get(position), ParentjObject.getString("DateTime"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Navigate to Diff Activities
    private void initialNavigate(OTCompleteObJ liveExams, String dateTime) {
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();

        Date endDate = null, resultDate = null;

        try {
            endDate = dtf.parse(liveExams.getExamEndTime());
            resultDate = dtf.parse(liveExams.getrDate());
            now = dtf.parse(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (now.after(endDate) && liveExams.geteStatus().equalsIgnoreCase("SCHEDULED")) {
            new AlertDialog.Builder(OTStudentCompletedTest.this)
                    .setTitle("Sorry..!")
                    .setMessage("Sorry you missed the Exam...!")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        } else if (now.after(endDate) && liveExams.geteStatus().equalsIgnoreCase("INPROGRESS")) {
            if (now.after(endDate) && now.after(resultDate) && liveExams.geteStatus().equalsIgnoreCase("INPROGRESS")) {
                Intent onlineJeeTestIntent = new Intent(OTStudentCompletedTest.this, OTStudentOnlineTestResult.class);
                onlineJeeTestIntent.putExtra("studentId", sObj.getMStudentId());
                onlineJeeTestIntent.putExtra("studentTestFilePath", liveExams.get_id());
                onlineJeeTestIntent.putExtra("testFilePath", liveExams.getePath());
                onlineJeeTestIntent.putExtra("testId", liveExams.getExamId());
                onlineJeeTestIntent.putExtra("testName", liveExams.geteName());
                onlineJeeTestIntent.putExtra("jeeSectionTemplate", "NA");
                startActivity(onlineJeeTestIntent);
            } else {
                new AlertDialog.Builder(OTStudentCompletedTest.this)
                        .setTitle("Plase wait...!")
                        .setMessage("Results will be released on \n" + liveExams.getrDate())
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            }
        } else if (now.after(endDate) && now.after(resultDate) && (liveExams.geteStatus().equalsIgnoreCase("SUBMITTED") || liveExams.geteStatus().equalsIgnoreCase("COMPLETED"))) {
            Intent onlineJeeTestIntent = new Intent(OTStudentCompletedTest.this, OTStudentOnlineTestResult.class);
            onlineJeeTestIntent.putExtra("studentId", sObj.getMStudentId());
            onlineJeeTestIntent.putExtra("studentTestFilePath", liveExams.get_id());
            onlineJeeTestIntent.putExtra("testFilePath", liveExams.getePath());
            onlineJeeTestIntent.putExtra("testId", liveExams.getExamId());
            onlineJeeTestIntent.putExtra("testName", liveExams.geteName());
            onlineJeeTestIntent.putExtra("jeeSectionTemplate", "NA");
            startActivity(onlineJeeTestIntent);
        } else {
            if (liveExams.geteStatus().equalsIgnoreCase("SUBMITTED")) {
                new AlertDialog.Builder(OTStudentCompletedTest.this)
                        .setTitle("Plase wait...!")
                        .setMessage("Results will be released on \n" + liveExams.getrDate())
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            } else {
                String sdate = "";
                try {
                    sdate = new SimpleDateFormat("hh:mm aa dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveExams.getsTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new AlertDialog.Builder(OTStudentCompletedTest.this)
                        .setTitle("Plase wait...!")
                        .setMessage("Exam Scheduled at \n" + sdate)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            }
        }
    }


}