package myschoolapp.com.gsnedutech.activity.fee;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.fee.FeeDetail;
import myschoolapp.com.gsnedutech.Models.fee.TermPaidList;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FeeDueDetailsActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = FeeDueDetailsActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    StudentObj sObj;

    String studentId = "";

    List<FeeDetail> dataList = new ArrayList<>();

    @BindView(R.id.rv_due_list)
    RecyclerView rvDueList;

    @BindView(R.id.tv_cnf_pay)
    TextView tvCnfPay;

    FeeDueAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_due_details);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvCnfPay.setOnClickListener(v -> {
            List<TermPaidList> paidList = new ArrayList<>();
            for(int i =0 ; i< dataList.size(); i++){
                if (dataList.get(i).isChecked()) {
                    TermPaidList paid = new TermPaidList();
                    paid.setTermName(dataList.get(i).getFeeCategoryName());
                    paid.setTermFeeAmountPaying(Integer.valueOf(dataList.get(i).getTermFeeRemainingAmount()));
                    paid.setCcsFeeId(Integer.parseInt(dataList.get(i).getCcsFeeId()));
                    paid.setFeeCategoryId(0);
                    paid.setTermFeeRemainingAmount(0);
                    paid.setCcsFeeTermId(Integer.valueOf(dataList.get(i).getCcsFeeTermId()));
                    paidList.add(paid);
                }

            }

            if (paidList.size()>0){
                Intent intent = new Intent(FeeDueDetailsActivity.this, FeeConfirmandPay.class);
                intent.putExtra("paidList", new Gson().toJson(paidList));
                intent.putExtra("academicYear", dataList.get(0).getAcademicYearId());
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
            else {
                Toast.makeText(this, "Please Select Something to Pay", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();
        rvDueList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
//        utils.showLoader(ParentHome.this);
//        getHomeWorks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, FeeDueDetailsActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
//            calendarWork(Integer.parseInt(month),Integer.parseInt(year));
                utils.showLoader(FeeDueDetailsActivity.this);
                getFeeCategories();
            }
            isNetworkAvail = true;
        }
    }


    void getFeeCategories(){
        utils.showLoader(FeeDueDetailsActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetStudentDueFeeDetail +"schemaName=" +sh_Pref.getString("schema","")+ "&studentId=" + sObj.getStudentId());

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentDueFeeDetail +"schemaName=" +sh_Pref.getString("schema","")+ "&studentId=" + sObj.getStudentId())
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });

                    }
                    else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);


                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("feeDetails");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FeeDetail>>() {
                            }.getType();

                            dataList.clear();
                            dataList = gson.fromJson(jsonArr.toString(), type);


                            runOnUiThread(() -> {
                                if (dataList.size()>0){
                                    adapter = new FeeDueAdapter();
                                    rvDueList.setAdapter(adapter);
                                }
                            });
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class FeeDueAdapter extends RecyclerView.Adapter<FeeDueAdapter.ViewHolder>{

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeeDueDetailsActivity.this).inflate(R.layout.item_fee_category, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.rvTerms.setVisibility(View.GONE);
            holder.tvFeeCatName.setText(dataList.get(position).getFeeCategoryName()+"("+utils.addRupeeSymbol(dataList.get(position).getTermFeeAmount())+")");
            holder.tvFeeCatDueDt.setText("Due ("+utils.convertStrtoStrDts("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",dataList.get(position).getLastDate())+") : " + utils.addRupeeSymbol(dataList.get(position).getTermFeeRemainingAmount()));
            holder.cbFeeCat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()){
                    dataList.get(position).setChecked(isChecked);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            CheckBox cbFeeCat;
            TextView tvFeeCatName, tvFeeCatDueDt;
            RecyclerView rvTerms;

            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                cbFeeCat = itemView.findViewById(R.id.cb_fee_cat);
                tvFeeCatName = itemView.findViewById(R.id.tv_feecatname);
                tvFeeCatDueDt = itemView.findViewById(R.id.tv_feecatduedt);
                rvTerms = itemView.findViewById(R.id.rv_terms);

            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}