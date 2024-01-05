package myschoolapp.com.gsnedutech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StaffLeaveObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TeacherLeaveRequest extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    private static final String TAG = TeacherLeaveRequest.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    StudentObj sObj;
    TeacherObj tObj;
    String staffId,branchId;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_leave_req)
    RecyclerView rvLeaveReq;

    @BindView(R.id.tv_no_leaves)
    TextView tvNoLeaves;

    @BindView(R.id.img_newleave)
    ImageView imgNewleave;
    List<StaffLeaveObj> listLeaves = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_leave_request);
        ButterKnife.bind(this);

        init();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherLeaveRequest.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getLeaveRequest();
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
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);
        staffId = tObj.getUserId()+"";
        branchId = tObj.getBranchId()+"";

        imgNewleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherLeaveRequest.this, NewLeaveRequest.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
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

    void getLeaveRequest(){

        utils.showLoader(TeacherLeaveRequest.this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL- " + AppUrls.GetTotalLeaveRequestsForStaff + "schemaName=" + sh_Pref.getString("schema","")  + "&staffId=" +staffId);

        Request request = new Request.Builder()
                .url(AppUrls.GetTotalLeaveRequestsForStaff + "schemaName=" + sh_Pref.getString("schema","")  + "&staffId=" + staffId)
                .headers(MyUtils.addHeaders(sh_Pref))
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
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
                if (response.body() != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject parentjObject = new JSONObject(resp);
                                if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = parentjObject.getJSONArray("staffLeaveReqArray");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<StaffLeaveObj>>() {
                                    }.getType();

                                    listLeaves.clear();
                                    listLeaves.addAll(gson.fromJson(jsonArr.toString(), type));
                                    if (listLeaves.size()>0){
                                        rvLeaveReq.setVisibility(VISIBLE);
                                        tvNoLeaves.setVisibility(GONE);
                                    }

                                    rvLeaveReq.setLayoutManager(new LinearLayoutManager(TeacherLeaveRequest.this));
                                    rvLeaveReq.setAdapter(new LeavesListAdapter(listLeaves));
                                }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                    String message = parentjObject.getString(AppConst.MESSAGE);
                                    MyUtils.forceLogoutUser(toEdit, TeacherLeaveRequest.this, message, sh_Pref);
                                }
                                else {
                                    rvLeaveReq.setVisibility(GONE);
                                    tvNoLeaves.setVisibility(VISIBLE);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    class LeavesListAdapter extends RecyclerView.Adapter<LeavesListAdapter.ViewHolder> {

        List<StaffLeaveObj> listLeave = new ArrayList<>();

        LeavesListAdapter(List<StaffLeaveObj> listLeave) {
            this.listLeave = listLeave;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(TeacherLeaveRequest.this).inflate(R.layout.item_leave, parent, false);
            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvNAme.setText(tObj.getUserName());
            holder.tvReason.setText(listLeave.get(position).getReason());
            try {
                holder.tvNumDays.setText(getDateDifference(new SimpleDateFormat("yyyy-MM-dd").parse(listLeave.get(position).getLeaveFrom()),new SimpleDateFormat("yyyy-MM-dd").parse(listLeave.get(position).getLeaveTo()))+" days");
                holder.tvStartDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listLeave.get(position).getLeaveFrom())));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            switch (listLeave.get(position).getLeaveRequestStatus()){
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


        }

        @Override
        public int getItemCount() {
            return listLeave.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvNAme,tvStartDate,tvNumDays,tvReason,tvStatus;

            public ViewHolder(@NonNull View itemView) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}