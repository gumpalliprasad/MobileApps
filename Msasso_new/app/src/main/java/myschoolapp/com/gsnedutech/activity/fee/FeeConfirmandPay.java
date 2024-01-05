package myschoolapp.com.gsnedutech.activity.fee;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentProfileObj;
import myschoolapp.com.gsnedutech.Models.fee.TermPaidList;
import myschoolapp.com.gsnedutech.ParentHome;
import myschoolapp.com.gsnedutech.R;
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

public class FeeConfirmandPay extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener, PaymentResultWithDataListener {

    private static final String TAG = FeeDueDetailsActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    JSONObject ParentjObject;
    String orderId = "";

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_paid_list)
    RecyclerView rvPaidList;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;

    @BindView(R.id.tv_tot_amount)
    TextView tvTotAmount;


    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    StudentObj sObj;
    StudentProfileObj studentProfileObj;

    String studentId = "";

    String academicYear = "";
    int totalAmount;


    List<TermPaidList> paidList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_confirmand_pay);

        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.tv_pay).setOnClickListener(v -> {
            createOrder();
        });
    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        Type type = new TypeToken<List<TermPaidList>>() {
        }.getType();

        academicYear = getIntent().getStringExtra("academicYear");
        paidList = gson.fromJson(getIntent().getStringExtra("paidList"),type);

        if(paidList.size()>0){
            rvPaidList.setLayoutManager(new LinearLayoutManager(this));
            rvPaidList.setAdapter(new PaidAdapter());
            totalAmount =0;
            for (TermPaidList term : paidList){
                totalAmount = totalAmount + term.getTermFeeAmountPaying();
            }
            tvTotAmount.setText("Total Amount : "+utils.addRupeeSymbol(totalAmount));
        }


        studentId = sObj.getStudentId();

    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
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
            utils.alertDialog(1, FeeConfirmandPay.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                getProfileDetails();
            }
            isNetworkAvail = true;
        }
    }

    void getProfileDetails() {
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetstudentDetailsById + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId())
                .build();

        utils.showLog(TAG, new AppUrls().GetstudentDetailsById + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId());

        client.newCall(get).enqueue(new Callback() {
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
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        Gson gson = new Gson();
                        studentProfileObj = gson.fromJson(jsonObject.toString(), StudentProfileObj.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                tvName.setText(studentProfileObj.getParentName());
                                tvMobile.setText(studentProfileObj.getContactNumber());
                                tvEmail.setText(studentProfileObj.getParentEmailId());
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }


    private class PaidAdapter extends RecyclerView.Adapter<PaidAdapter.ViewHolder>{

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeeConfirmandPay.this).inflate(R.layout.item_fee_confirm, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.tvListName.setText(paidList.get(position).getTermName());
            holder.tvAmount.setText(utils.addRupeeSymbol(paidList.get(position).getTermFeeAmountPaying()));
        }

        @Override
        public int getItemCount() {
            return paidList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView tvListName, tvAmount;


            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                tvListName = itemView.findViewById(R.id.tv_name);
                tvAmount = itemView.findViewById(R.id.tv_amount);

            }
        }
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    private void createOrder() {
        utils.showLoader(FeeConfirmandPay.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", (float)(totalAmount) );
            jsonObject.put("currency", "INR");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Log.d(TAG, "body" + jsonObject);

        Request request = new Request.Builder()
                .url(AppUrls.KHUB_BASE_URL+"generateOrderId")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {

                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(() -> {
                        try {
                            String jsonResp = response.body().string();

                            Log.d(TAG, "responce - " + jsonResp);
                            JSONObject jjpp = new JSONObject(jsonResp);
                            ParentjObject = jjpp.getJSONObject("result");
//                            Toast.makeText(KhubCategoryDetail.this, "Order Created", Toast.LENGTH_SHORT).show();

                            orderId = ParentjObject.getString("id");
                            startPayment();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }



    public void startPayment() {   /**   * Instantiate Checkout   */
        Checkout checkout = new Checkout();
        checkout.setFullScreenDisable(false);
        checkout.setKeyID("rzp_test_p1mRiCAF527pDj"); /**   * Set your logo here   */
        checkout.setImage(R.mipmap.ic_launcher);  /**   * Reference to current activity   */
        final Activity activity = this;  /**   * Pass your payment options to the Razorpay Checkout as a JSONObject   */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "MySchool");
            options.put("order_id", orderId);
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("theme.color", "#3399cc");
            options.put("currency", ParentjObject.get("currency"));
            options.put("amount", ParentjObject.get("amount"));//pass amount in currency subunits
//            options.put("prefill.email", sObj.getLoginId());
            if (sObj.getPhoneNumber()!=null && !sObj.getPhoneNumber().isEmpty()){
                options.put("prefill.contact",sObj.getPhoneNumber() );
            }
            else options.put("prefill.contact", "");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            String message = "OrderId : "+orderId+"\nName : "+sObj.getStudentName()+"\n"+
                    "PaymentId : "+ s+"\n"+"Status : Success";
            validateTransaction("Success",paymentData,-1);

//            new AlertDialog.Builder(MainActivity.this).
//                    setTitle("Payment Successful").
//                    setMessage(message).
//                    setPositiveButton("Ok",(dialogInterface, i) -> dialogInterface.dismiss()).show();
//            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try {

            String message = "OrderId : "+orderId+"\nName : "+sObj.getStudentName()+"\n"+
                    "PaymentStatus : "+ s +"\n"+"Status : Failed";
//            validateTransaction("Failed",paymentData,i);

            new androidx.appcompat.app.AlertDialog.Builder(FeeConfirmandPay.this).
                    setTitle("Payment Failed").
                    setMessage(message).
                    setPositiveButton("Ok",(dialogInterface, j) -> {
                        dialogInterface.dismiss();
                        Intent parentIntent = new Intent(FeeConfirmandPay.this, ParentHome.class);
                        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(parentIntent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }).show();
//            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    private void validateTransaction(String status, PaymentData paymentData, int errorCode){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("razorpay_payment_id", paymentData.getPaymentId());
            jsonObject.put("razorpay_order_id", paymentData.getOrderId());
            jsonObject.put("status", status);
            jsonObject.put("errorCode", errorCode);
            jsonObject.put("razorpay_signature", paymentData.getSignature());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Log.d(TAG, "body" + jsonObject);

        Request request = new Request.Builder()
                .url(AppUrls.KHUB_BASE_URL+"validatePayment")
                .post(body)
                .addHeader("x-razorpay-signature", paymentData.getSignature())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String message = "OrderId : "+orderId+"\nName : "+sObj.getStudentName()+"\n"+
                        "PaymentStatus : "+ "failed" +"\n"+"Status : Failed";

                runOnUiThread(() -> {
                    utils.dismissDialog();
                    new AlertDialog.Builder(FeeConfirmandPay.this).
                            setTitle("Payment Failed").
                            setMessage(message).
                            setPositiveButton("Ok",(dialogInterface, j) -> dialogInterface.dismiss()).show();
                    e.printStackTrace();
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
                if (response.isSuccessful()){
                    runOnUiThread(() -> {
                        try {
                            String jsonResp = response.body().string();

                            Log.d(TAG, "valid responce - " + jsonResp);

                            String message = "OrderId : "+orderId+"\nName : "+sObj.getStudentName()+"\n"+
                                    "PaymentId : "+ paymentData.getPaymentId()+"\n"+"Status : Sucess";
                            new AlertDialog.Builder(FeeConfirmandPay.this).
                                    setTitle("Payment Successful").
                                    setCancelable(false).
                                    setMessage(message).
                                    setPositiveButton("Ok", (dialog, which) -> {
                                        updatePayment();
                                        dialog.dismiss();
                                    }).show();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void updatePayment() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("studentId", studentId);
            jsonObject.put("academicYearId", academicYear);
            jsonObject.put("createdBy", studentId);
            jsonObject.put("totalPayingAmount", (float)(totalAmount));
            jsonObject.put("sectionId",sObj.getClassCourseSectionId());
            jsonObject.put("branchId",sObj.getBranchId());
            jsonObject.put("feeCategories",createFeeCategories(paidList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));
        Log.d(TAG, "body" + jsonObject);

        Request request = new Request.Builder()
                .url(AppUrls.UpdateFeePayment)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(() -> {
                        try {
                            String jsonResp = response.body().string();

                            Log.d(TAG, "valid responce - " + jsonResp);

                            JSONObject jjpp = new JSONObject(jsonResp);

                            if (jjpp.getString("StatusCode").equalsIgnoreCase("200")){
                                Intent parentIntent = new Intent(FeeConfirmandPay.this, ParentHome.class);
                                parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(parentIntent);
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                runOnUiThread(() -> {
                                    utils.dismissDialog();
                                });
                                finish();
                            }

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });


    }

    private JSONArray createFeeCategories(List<TermPaidList> paidList) {
        JSONArray jsonArray = new JSONArray();

        try{
            for (int i=0; i<paidList.size(); i++){
                JSONObject paid = new JSONObject();
                paid.put("ccsFeeId", paidList.get(i).getCcsFeeId());
                paid.put("ccsFeeTermId", paidList.get(i).getCcsFeeTermId());
                paid.put("feeCategoryId", paidList.get(i).getFeeCategoryId());
                paid.put("termFeeAmountPaying", paidList.get(i).getTermFeeAmountPaying());
                paid.put("termFeeRemainingAmount", paidList.get(i).getTermFeeRemainingAmount());
                jsonArray.put(paid);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return jsonArray;
    }


}