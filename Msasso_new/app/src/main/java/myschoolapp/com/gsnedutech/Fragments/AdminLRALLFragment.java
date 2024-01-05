package myschoolapp.com.gsnedutech.Fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import myschoolapp.com.gsnedutech.Models.AdminStaffAllLeaveObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminLRALLFragment extends Fragment {

    private static final String TAG = "SriRam -" + AdminLRALLFragment.class.getName();
    MyUtils utils = new MyUtils();

    View view;
    RecyclerView rvLeaveRequests;

    List<AdminStaffAllLeaveObj> listLeaves = new ArrayList<>();
    List<String> listDates = new ArrayList<>();
    Calendar c;

    String branchId, userId;

    SharedPreferences sh_Pref;

    Activity mActivity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public AdminLRALLFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_lrall, container, false);

        init();

        getLeaves();


        return view;
    }

    private void init() {
        rvLeaveRequests = view.findViewById(R.id.rv_leave_list);
        rvLeaveRequests.setLayoutManager(new LinearLayoutManager(mActivity));
        c = Calendar.getInstance();

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        branchId = mActivity.getIntent().getStringExtra("branchId");
        userId = mActivity.getIntent().getStringExtra("userId");

    }

    void getLeaves(){
        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Admin Leave request - " + new AppUrls().GetTotalLeaveRequestsForStaffByBranch +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0"));

        Request request = new Request.Builder()
                .url(new AppUrls().GetTotalLeaveRequestsForStaffByBranch +"schemaName=" +sh_Pref.getString("schema","")+ "&branchId=" + sh_Pref.getString("admin_branchId", "0"))
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.d("tag", "Homework Details - " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    JSONArray jsonArr = ParentjObject.getJSONArray("staffLeaveReqArray");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<AdminStaffAllLeaveObj>>() {
                                    }.getType();

                                    listLeaves.clear();
                                    listLeaves.addAll(gson.fromJson(jsonArr.toString(), type));

                                    rvLeaveRequests.setVisibility(View.VISIBLE);

                                    listDates.clear();
                                    for (int i = 0; i < listLeaves.size(); i++) {
                                        listDates.add(listLeaves.get(i).getLeaveFrom());
                                    }

                                    HashSet hs = new HashSet();
                                    hs.addAll(listDates);
                                    listDates.clear();
                                    listDates.addAll(hs);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    List<Date> listD = new ArrayList<>();
                                    for (int i = 0; i < listDates.size(); i++) {
                                        listD.add(sdf.parse(listDates.get(i)));
                                    }

                                    Collections.sort(listD);

                                    listDates.clear();
                                    for (int i = 0; i < listD.size(); i++) {
                                        listDates.add(sdf.format(listD.get(i)));
                                    }

                                    rvLeaveRequests.setAdapter(new LeaveDateAdapter(listDates));

                                } else {
                                    rvLeaveRequests.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
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

    class LeaveDateAdapter extends RecyclerView.Adapter<LeaveDateAdapter.ViewHolder> {

        List<String> dates = new ArrayList<>();

        LeaveDateAdapter(List<String> dates) {
            this.dates = dates;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_leave_date, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvLeaveDate.setText(dates.get(position));
            List<AdminStaffAllLeaveObj> listLeaveDetails = new ArrayList<>();
            for (int i = 0; i < listLeaves.size(); i++) {
                if (listLeaves.get(i).getLeaveFrom().equalsIgnoreCase(dates.get(position))) {
                    listLeaveDetails.add(listLeaves.get(i));
                }
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
            holder.rvLeaveDetails.setLayoutManager(layoutManager);
            holder.rvLeaveDetails.setAdapter(new LeaveDetailsAdapter(listLeaveDetails));

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(holder.rvLeaveDetails.getContext(),
                    layoutManager.getOrientation());
            holder.rvLeaveDetails.addItemDecoration(dividerItemDecoration);
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvLeaveDate;
            RecyclerView rvLeaveDetails;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvLeaveDate = itemView.findViewById(R.id.tv_leave_date);
                rvLeaveDetails = itemView.findViewById(R.id.rv_leave_details_list);
            }
        }
    }

    class LeaveDetailsAdapter extends RecyclerView.Adapter<LeaveDetailsAdapter.ViewHolder> {

        List<AdminStaffAllLeaveObj> listLeaveDet = new ArrayList<>();

        LeaveDetailsAdapter(List<AdminStaffAllLeaveObj> listLeaveDet) {
            this.listLeaveDet = listLeaveDet;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.layout_admin_leaves, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //holder.tvName.setText(listLeaveDet.get(position).get);
            holder.tvDates.setText(listLeaveDet.get(position).getLeaveFrom() + " to " + listLeaveDet.get(position).getLeaveTo());
            holder.tvReason.setText(listLeaveDet.get(position).getReason());
            holder.tvName.setText(listLeaveDet.get(position).getUserName());

            if (listLeaveDet.get(position).getLeaveRequestStatus().equalsIgnoreCase("RLR")) {
                holder.llOptions.setVisibility(View.GONE);
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.imgStatus.setImageResource(R.drawable.ic_rejected);
            } else if (listLeaveDet.get(position).getLeaveRequestStatus().equalsIgnoreCase("ALR")) {
                holder.llOptions.setVisibility(View.GONE);
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.imgStatus.setImageResource(R.drawable.ic_accepted);
            }

            holder.tvAccepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity, "Accepted", Toast.LENGTH_SHORT).show();
                    updateStaffLeaveRequest(userId, "" + listLeaveDet.get(position).getStaffLeaveReqId(), "1", "");
                }
            });

            holder.tvRejected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity, "Rejected", Toast.LENGTH_SHORT).show();
                    updateStaffLeaveRequest(userId, "" + listLeaveDet.get(position).getStaffLeaveReqId(), "2", "");

                }
            });

        }

        @Override
        public int getItemCount() {
            return listLeaveDet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName, tvDates, tvReason, tvAccepted, tvRejected;
            LinearLayout llOptions;
            ImageView imgStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);
                tvDates = itemView.findViewById(R.id.tv_dates);
                tvReason = itemView.findViewById(R.id.tv_reason);
                tvAccepted = itemView.findViewById(R.id.tv_accepted);
                tvRejected = itemView.findViewById(R.id.tv_rejected);
                imgStatus = itemView.findViewById(R.id.img_status);
                llOptions = itemView.findViewById(R.id.ll_options);
            }
        }
    }

    void updateStaffLeaveRequest(String... strings){
        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("updatedBy", strings[0]);


            JSONObject jsonObj = new JSONObject();
            jsonObj.put("staffLeaveReqId", strings[1]);
            jsonObj.put("isApproved", strings[2]);
            jsonObj.put("updatedBy", strings[0]);
            jsonObj.put("comments", strings[3]);


            JSONArray jsArray = new JSONArray();
            jsArray.put(jsonObj);
            jsonObject.put("updateLeaveReq", jsArray);

            Log.v(TAG, String.valueOf(jsonObject));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Log.v(TAG, "" + new AppUrls().UpdateStaffLeaveRequest);
        Log.v(TAG, "" + jsonObject);

        Request request = new Request.Builder()
                .url(new AppUrls().UpdateStaffLeaveRequest)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                if (response.body()!=null){
                    String jsonResp = response.body().string();

                    Log.v(TAG, "UpdateStaffLeaveRequest responce - " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);

                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
//                        Toast.makeText(mActivity, "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                    getLeaves();
                                } else {
//                        Toast.makeText(mActivity, "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                    getLeaves();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
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

}
