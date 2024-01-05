package myschoolapp.com.gsnedutech.Fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.AdminStaffLeaveObj;
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
public class AdminLRFilteredFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "SriRam -" + AdminLRFilteredFragment.class.getName();
    MyUtils utils = new MyUtils();

    View view;
    SimpleDateFormat serversdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat displaysdf = new SimpleDateFormat("dd MMMM, yyyy");

    TextView tvDate;
    TextView tvNoLeaverequests;
    RecyclerView rvLrList;

    Calendar calendar;
    Date reqdate;

    String branchId, userId;

    List<AdminStaffLeaveObj> listLeaves = new ArrayList<>();
    Unbinder unbinder;

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

    public AdminLRFilteredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_lrfiltered, container, false);

        tvDate = view.findViewById(R.id.tv_date);
        tvNoLeaverequests = view.findViewById(R.id.tv_no_leaverequests);
        rvLrList = view.findViewById(R.id.rv_lrList);

        init();

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void init() {
        branchId = mActivity.getIntent().getStringExtra("branchId");
        userId = mActivity.getIntent().getStringExtra("userId");

        calendar = Calendar.getInstance();
        updateLabel(displaysdf.format(new Date()), 0);

        rvLrList.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    void updateLabel(String date, int change) {
        try {
            Date dateObj = displaysdf.parse(date);
            calendar.setTime(dateObj);
            if (change != 0)
                calendar.add(Calendar.DATE, change);
            dateObj = calendar.getTime();
            tvDate.setText(displaysdf.format(dateObj));

            reqdate = displaysdf.parse(tvDate.getText().toString());
            getStaffLeaveRequest(serversdf.format(reqdate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dayOfMonth + "/" + month + 1 + "/" + year);
            updateLabel(displaysdf.format(date), 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.img_prev, R.id.tv_date, R.id.img_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_prev:
                updateLabel(tvDate.getText().toString(), -1);
                break;
            case R.id.tv_date:
                DatePickerDialog dialog = new DatePickerDialog(mActivity, this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.img_next:
                updateLabel(tvDate.getText().toString(), 1);
                break;
        }
    }

    //    Getting Staff LeaveRequests  on selected Date
    void getStaffLeaveRequest(String... strings){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "Admin StaffAttendance Leaves request - " + new AppUrls().GetStaffLeaveRequestsByDate +"schemaName=" +mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&date=" + strings[0]);

        Request request = new Request.Builder()
                .url(new AppUrls().GetStaffLeaveRequestsByDate +"schemaName=" +mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&branchId=" + branchId + "&date=" + strings[0])
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String jsonResp = response.body().string();
                    Log.v(TAG, "Admin StaffAttendance response - " + jsonResp);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject ParentjObject = new JSONObject(jsonResp);
                                if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                    Log.v(TAG, "Count - " + ParentjObject.getJSONArray("staffLeaveReq").length());

                                    JSONArray jsonArr = ParentjObject.getJSONArray("staffLeaveReq");

                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<AdminStaffLeaveObj>>() {
                                    }.getType();

                                    listLeaves.clear();
                                    listLeaves.addAll(gson.fromJson(jsonArr.toString(), type));

                                    Log.v(TAG, "Admin StaffAttendance list Size - " + listLeaves.size());

                                    tvNoLeaverequests.setVisibility(View.GONE);
                                    rvLrList.setVisibility(View.VISIBLE);


                                    rvLrList.setAdapter(new LeaveDetailsAdapter(listLeaves));


                                } else {
                                    tvNoLeaverequests.setVisibility(View.VISIBLE);
                                    rvLrList.setVisibility(View.GONE);
                                }


                            } catch (JSONException e) {
                                Log.v("TAG", "error " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    class LeaveDetailsAdapter extends RecyclerView.Adapter<LeaveDetailsAdapter.ViewHolder> {

        List<AdminStaffLeaveObj> listLeaveDet = new ArrayList<>();

        LeaveDetailsAdapter(List<AdminStaffLeaveObj> listLeaveDet) {
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
            holder.tvName.setText(listLeaveDet.get(position).getStaffName());
            holder.tvDates.setText(listLeaveDet.get(position).getLeaveFrom() + " to " + listLeaveDet.get(position).getLeaveTo());
            holder.tvReason.setText(listLeaveDet.get(position).getReason());

            if (listLeaveDet.get(position).getLeaveReqestStatus().equalsIgnoreCase("RLR")) {
                holder.llOptions.setVisibility(View.GONE);
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.imgStatus.setImageResource(R.drawable.ic_rejected);
            } else if (listLeaveDet.get(position).getLeaveReqestStatus().equalsIgnoreCase("ALR")) {
                holder.llOptions.setVisibility(View.GONE);
                holder.imgStatus.setVisibility(View.VISIBLE);
                holder.imgStatus.setImageResource(R.drawable.ic_accepted);
            }

            holder.tvAccepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity, "Accepted", Toast.LENGTH_SHORT).show();
                    updateStaffLeaveRequest(userId, "" + listLeaveDet.get(position).getStaffLeaveReqId(), "1", "Comment");
                }
            });

            holder.tvRejected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity, "Rejected", Toast.LENGTH_SHORT).show();
                    updateStaffLeaveRequest(userId, "" + listLeaveDet.get(position).getStaffLeaveReqId(), "2", "Comment");

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

            jsonObject.put("schemaName", mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
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
                    mActivity.runOnUiThread(() -> {
                        try {
                            JSONObject ParentjObject = new JSONObject(jsonResp);

                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                Toast.makeText(mActivity, "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                getStaffLeaveRequest(serversdf.format(reqdate));
                            } else {
                                Toast.makeText(mActivity, "" + ParentjObject.getString("MESSAGE"), Toast.LENGTH_SHORT);
                                getStaffLeaveRequest(serversdf.format(reqdate));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                mActivity.runOnUiThread(() -> utils.dismissDialog());
            }
        });
    }

}
