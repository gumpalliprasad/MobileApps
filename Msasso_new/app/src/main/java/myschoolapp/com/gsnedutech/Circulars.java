package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.CircularTransactions;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Circulars extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {
    
    private static final String TAG = "SriRam -" + Circulars.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.iv_add_cc)
    ImageView ivAddCC;

    @BindView(R.id.rv_circulars)
    RecyclerView rvCirculars;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    MyUtils utils = new MyUtils();



//    @BindView(R.id.et_search)
//    EditText etSearch;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    TeacherObj teacherObj;
    List<CircularTransactions> listTransactions = new ArrayList<>();

    TransactionAdapter adapterTransactions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Window window = this.getWindow();
//        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
//        window.setBackgroundDrawable(background);

        setContentView(R.layout.activity_circulars);
        ButterKnife.bind(this);

       init();
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
            utils.alertDialog(1, Circulars.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getCirculars();
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

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            teacherObj = gson.fromJson(json, TeacherObj.class);
            ivAddCC.setVisibility(View.GONE);
        }else{
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            ivAddCC.setVisibility(View.GONE);
        }

        
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        ivAddCC.setOnClickListener(view -> {
            startActivity(new Intent(Circulars.this,AddCirculars.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });

//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                adapterTransactions.getFilter().filter(editable.toString());
//            }
//        });

    }


    class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> implements Filterable {

        List<CircularTransactions> list_filtered;

        TransactionAdapter( List<CircularTransactions> list){

            list_filtered = list;
            Collections.reverse(list_filtered);
        }

        @NonNull
        @Override
        public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(Circulars.this).inflate(R.layout.item_cc,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder viewHolder, int i) {
            viewHolder.setIsRecyclable(false);
            viewHolder.tvNameCC.setText(list_filtered.get(i).getTransactionName());
            viewHolder.tvDateCC.setText(list_filtered.get(i).getTransactionDate());
            viewHolder.tvType.setText(list_filtered.get(i).getTransactionType());

            if (!list_filtered.get(i).getFilePath().equalsIgnoreCase("NA")){
                viewHolder.ivAttach.setVisibility(View.VISIBLE);
            }else {
                viewHolder.ivAttach.setVisibility(View.GONE);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   if (viewHolder.ivAttach.getVisibility()==View.VISIBLE){
                       String url = new AppUrls().HomeWorkFileDownLoadPDF + list_filtered.get(i).getFilePath();

                       Intent intent = new Intent(Circulars.this, NewPdfViewer.class);
                       intent.putExtra("url",url);
                       startActivity(intent);
                       overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                   }else{
                       new MyUtils().alertDialog(3, Circulars.this, "Oops!", "No files are available for this circular. ",
                               "Close", "", false);
                   }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    utils.showLog("TAG", string);
                    if (string.isEmpty()) {
                        list_filtered = listTransactions;
                    } else {
                        List<CircularTransactions> filteredList = new ArrayList<>();
                        for (CircularTransactions s : listTransactions) {
                            if (s.getTransactionName().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }
                        list_filtered = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<CircularTransactions>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvNameCC, tvDateCC, tvType;
            ImageView ivAttach;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNameCC = itemView.findViewById(R.id.tv_name_cc);
                tvDateCC = itemView.findViewById(R.id.tv_date_cc);
                tvType = itemView.findViewById(R.id.tv_type_cc);
                ivAttach = itemView.findViewById(R.id.iv_attach);
            }
        }
    }

    void getCirculars(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", sh_Pref.getString("schema",""));

            if (sh_Pref.getBoolean("teacher_loggedin", false)) {
                jsonObject.put("branchId", teacherObj.getBranchId());
                jsonObject.put("roleId", "1");
                jsonObject.put("transactionUser", "1");
            }else {
                jsonObject.put("branchId", sObj.getBranchId());
                jsonObject.put("roleId", "0");
                jsonObject.put("transactionUser", "2");
                jsonObject.put("sectionId", sObj.getClassCourseSectionId());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        utils.showLog(TAG, "url " + new AppUrls().GetCirculars);
        utils.showLog(TAG, "body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(new AppUrls().GetCirculars)
                .post(body)
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (response.body() != null) {
                    String resp = responseBody.string();
                    try {
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = parentjObject.getJSONArray("transactions");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CircularTransactions>>() {
                            }.getType();
                            listTransactions.clear();
                            listTransactions.addAll(gson.fromJson(jsonArray.toString(), type));
                            if (listTransactions.size() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvCirculars.setLayoutManager(new LinearLayoutManager(Circulars.this));
                                        adapterTransactions = new TransactionAdapter(listTransactions);
//                                   rvCirculars.setAdapter(adapterTransactions);
                                        final LayoutAnimationController controller =
                                                AnimationUtils.loadLayoutAnimation(rvCirculars.getContext(), R.anim.layout_animation_fall_down);
                                        rvCirculars.setAdapter(new CCAdapter(listTransactions));
                                        rvCirculars.scheduleLayoutAnimation();
                                    }
                                });
                            } else {

                            }
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, Circulars.this, message, sh_Pref);
                            });
                        }

                    } catch (Exception e) {

                    }
                }
            }
        });

    }

    class CCAdapter extends RecyclerView.Adapter<CCAdapter.ViewHolder>{

        List<CircularTransactions> listTransaction;

        public CCAdapter(List<CircularTransactions> listTransaction) {
            this.listTransaction = listTransaction;
        }

        @NonNull
        @Override
        public CCAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(Circulars.this).inflate(R.layout.item_circular_transactions,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CCAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.tvCircularTitle.setText(listTransaction.get(position).getTransactionName());
//            holder.tvCircularDesc.setText(listTransaction.get(position).getTransactionType());
            try {
                holder.tvCircularTime.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(listTransaction.get(position).getTransactionDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!listTransaction.get(position).getFilePath().equalsIgnoreCase("NA")){
                holder.ivAttach.setVisibility(View.VISIBLE);
            }else {
                holder.ivAttach.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Circulars.this,CircularDisplay.class);
                    intent.putExtra("circulars",(Serializable)listTransaction.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listTransaction.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCircularTitle,tvCircularDesc,tvCircularTime;
            ImageView ivAttach;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCircularTitle = itemView.findViewById(R.id.tv_circular_title);
                tvCircularDesc = itemView.findViewById(R.id.tv_circular_desc);
                tvCircularTime = itemView.findViewById(R.id.tv_circular_time);
                ivAttach = itemView.findViewById(R.id.iv_attach);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}