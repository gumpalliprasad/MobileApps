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
import androidx.recyclerview.widget.GridLayoutManager;
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
import myschoolapp.com.gsnedutech.Models.fee.FeeData;
import myschoolapp.com.gsnedutech.Models.fee.TermArray;
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

public class FeeCategoriesActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = FeeCategoriesActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    StudentObj sObj;

    String studentId = "";

    List<FeeData> dataList = new ArrayList<>();

    @BindView(R.id.rv_fees_list)
    RecyclerView rvFeesList;

    @BindView(R.id.tv_cnf_pay)
    TextView tvCnfPay;

    FeeDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_categories);

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

                if (dataList.get(i).getTermArray().size()>0) {
                    for (int j = 0; j < dataList.get(i).getTermArray().size(); j++) {
                        if (dataList.get(i).getTermArray().get(j).isChecked()) {
                            TermPaidList paid = new TermPaidList();
                            paid.setTermName(dataList.get(i).getTermArray().get(j).getTermName());
                            paid.setTermFeeAmountPaying(dataList.get(i).getTermArray().get(j).getTermFeeAmount());
                            paid.setCcsFeeId(dataList.get(i).getTermArray().get(j).getCcsFeeId());
                            paid.setFeeCategoryId(dataList.get(i).getFeeCategoryId());
                            paid.setTermFeeRemainingAmount(0);
                            paid.setCcsFeeTermId(dataList.get(i).getTermArray().get(j).getTermId());
                            paidList.add(paid);
                        }
                    }
                }
                else {
                    if (dataList.get(i).isChecked()) {
                        TermPaidList paid = new TermPaidList();
                        paid.setTermName(dataList.get(i).getFeeCategoryName());
                        paid.setTermFeeAmountPaying(dataList.get(i).getTotalFeeAmout());
                        paid.setCcsFeeId(dataList.get(i).getCcsFeeId());
                        paid.setFeeCategoryId(dataList.get(i).getFeeCategoryId());
                        paid.setTermFeeRemainingAmount(0);
                        paid.setCcsFeeTermId(0);
                        paidList.add(paid);
                    }
                }

            }

            if (paidList.size()>0){
                Intent intent = new Intent(FeeCategoriesActivity.this, FeeConfirmandPay.class);
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
        rvFeesList.setLayoutManager(new LinearLayoutManager(this));

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
            utils.alertDialog(1, FeeCategoriesActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
//            calendarWork(Integer.parseInt(month),Integer.parseInt(year));
                utils.showLoader(FeeCategoriesActivity.this);
                getFeeCategories();
            }
            isNetworkAvail = true;
        }
    }


    void getFeeCategories(){
        utils.showLoader(FeeCategoriesActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - " + AppUrls.GetStudentFeeCategories +"schemaName=" +sh_Pref.getString("schema","")+ "&studentId=" + sObj.getStudentId());

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentFeeCategories +"schemaName=" +sh_Pref.getString("schema","")+ "&studentId=" + sObj.getStudentId())
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

                            JSONArray jsonArr = ParentjObject.getJSONArray("data");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FeeData>>() {
                            }.getType();

                            dataList.clear();
                            dataList = gson.fromJson(jsonArr.toString(), type);


                            runOnUiThread(() -> {
                                if (dataList.size()>0){
                                    adapter = new FeeDataAdapter();
                                    rvFeesList.setAdapter(adapter);
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

    private class FeeDataAdapter extends RecyclerView.Adapter<FeeDataAdapter.ViewHolder>{

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeeCategoriesActivity.this).inflate(R.layout.item_fee_category, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.rvTerms.setLayoutManager(new GridLayoutManager(FeeCategoriesActivity.this, 2));
            if (dataList.get(position).getTermArray().size()<=0){
                holder.rvTerms.setVisibility(View.GONE);
            }
            else {
                holder.rvTerms.setAdapter(new TermsAdapter(dataList.get(position).getTermArray(), position));
            }
            if (dataList.get(position).getFeeAmountRemaining()==0){
                holder.cbFeeCat.setVisibility(View.INVISIBLE);
            }
            holder.tvFeeCatName.setText(dataList.get(position).getFeeCategoryName()+"("+utils.addRupeeSymbol(dataList.get(position).getTotalFeeAmout())+")");
            holder.tvFeeCatDueDt.setText("Due ("+utils.convertStrtoStrDts("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",dataList.get(position).getLastDate())+") : " + utils.addRupeeSymbol(dataList.get(position).getFeeAmountRemaining())+")");
            holder.cbFeeCat.setChecked(dataList.get(position).isChecked());
            holder.cbFeeCat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()){
                    dataList.get(position).setChecked(isChecked);
                    if (isChecked){
                        for (int i =0 ;i< dataList.get(position).getTermArray().size(); i++){
                            dataList.get(position).getTermArray().get(i).setChecked(true);
                        }
                    }
                    else {
                        for (int i =0 ;i< dataList.get(position).getTermArray().size(); i++){
                            dataList.get(position).getTermArray().get(i).setChecked(false);
                        }
                    }
                    holder.rvTerms.setAdapter(new TermsAdapter(dataList.get(position).getTermArray(), position));
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

    private class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.ViewHolder> {

        List<TermArray> termArrayList = new ArrayList<>();
        int selectPosition;
        public TermsAdapter(List<TermArray> termArray, int selPosition) {
            termArrayList = termArray;
            selectPosition = selPosition;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeeCategoriesActivity.this).inflate(R.layout.item_fee_term, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.cbTerm.setChecked(termArrayList.get(position).isChecked());
            holder.tvTermName.setText(termArrayList.get(position).getTermName()+"("+utils.addRupeeSymbol(termArrayList.get(position).getTermFeeAmount())+")");
            holder.tvPayAmount.setText("Payed Amount : "+utils.addRupeeSymbol(termArrayList.get(position).getTermFeeAmountPaid()));
            holder.tvDueDt.setText("Due on "+ utils.convertStrtoStrDts("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",termArrayList.get(position).getTermToDate()));
            holder.tvPenAmount.setText("Pending Amount : " + utils.addRupeeSymbol(termArrayList.get(position).getTermFeeRemainingAmount()));
            if (termArrayList.get(position).getTermFeeRemainingAmount()==0){
                holder.cbTerm.setVisibility(View.INVISIBLE);
            }
            holder.cbTerm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()){
                    if (!isChecked){
                        dataList.get(selectPosition).setChecked(false);
                        termArrayList.get(position).setChecked(false);
                        dataList.get(selectPosition).getTermArray().get(position).setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        termArrayList.get(position).setChecked(true);
                        dataList.get(selectPosition).getTermArray().get(position).setChecked(true);
                        boolean isSelectAll = true;
                        for (int k = 0; k<dataList.get(selectPosition).getTermArray().size(); k++){
                            if(!dataList.get(selectPosition).getTermArray().get(k).isChecked()){
                                isSelectAll = false;
                            }
                        }
                        dataList.get(selectPosition).setChecked(isSelectAll);
                        if (isSelectAll){
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return termArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            CheckBox cbTerm;
            TextView tvTermName, tvPayAmount, tvDueDt, tvPenAmount;

            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                cbTerm = itemView.findViewById(R.id.cb_terms);
                tvTermName = itemView.findViewById(R.id.tv_termname);
                tvPayAmount = itemView.findViewById(R.id.tv_payamount);
                tvDueDt = itemView.findViewById(R.id.tv_duedt);
                tvPenAmount = itemView.findViewById(R.id.tv_penamount);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}