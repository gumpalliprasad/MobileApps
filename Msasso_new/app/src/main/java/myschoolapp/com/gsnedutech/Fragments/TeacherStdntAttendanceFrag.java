package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.TeacherStudentsMonthWiseAttendance;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.TeacherSectionAttendance;
import myschoolapp.com.gsnedutech.TeacherStdnListAttendance;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TeacherStdntAttendanceFrag extends Fragment {

    private static final String TAG = "SriRam -" + TeacherStdntAttendanceFrag.class.getName();

    List<TeacherStudentsMonthWiseAttendance> listAttendance = new ArrayList<>();

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    View viewTeacherStdntAttendanceFrag;
    Unbinder unbinder;

    @BindView(R.id.sp_type)
    Spinner spType;

    @BindView(R.id.rv_chart)
    RecyclerView rvChart;

    @BindView(R.id.tv_total_taken)
    TextView tvTotalTaken;
    @BindView(R.id.tv_total_present)
    TextView tvTotalPresent;
    @BindView(R.id.tv_total_absent)
    TextView tvTotalAbsent;
    @BindView(R.id.tv_total_late)
    TextView tvTotalLAte;
    @BindView(R.id.tv_type)
    TextView tvType;

    Activity mActivity;

    List<String> types= new ArrayList<>();

    public TeacherStdntAttendanceFrag() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewTeacherStdntAttendanceFrag = inflater.inflate(R.layout.fragment_teacher_stdnt_attendance, container, false);
        unbinder = ButterKnife.bind(this,viewTeacherStdntAttendanceFrag);

        init();

        if (NetworkConnectivity.isConnected(mActivity)) {
            getMonthlyAttendance();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }

        viewTeacherStdntAttendanceFrag.findViewById(R.id.btn_view_all_students).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TeacherStdnListAttendance.class);
                intent.putExtra("sectionId",((TeacherSectionAttendance)mActivity).sectionId);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        return viewTeacherStdntAttendanceFrag;
    }

    void init(){

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);


//        types.add("Daily");
//        types.add("Weekly");
        types.add("Monthly");

        tvType.setText(new SimpleDateFormat("MMM yyyy").format(new Date()));

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(mActivity, R.layout.spinner_test_item, types);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(dataAdapter1);


    }


    void getMonthlyAttendance(){

        utils.showLoader(mActivity);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Log.v(TAG, "Leave request List - " + new AppUrls().GetMonthWiseAttendanceAnalysis+"schemaName=" +sh_Pref.getString("schema","")+ "&sectionId=" +((TeacherSectionAttendance)mActivity).sectionId+ "&endDate=" + date);

        Request request = new Request.Builder()
                .url(new AppUrls().GetMonthWiseAttendanceAnalysis+"schemaName=" +sh_Pref.getString("schema","")+ "&sectionId=" +((TeacherSectionAttendance)mActivity).sectionId+ "&endDate=" + date)
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
                String resp = response.body().string();
                if (!response.isSuccessful()){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jsonArray = jsonObject.getJSONArray("studentsMonthWiseAttendance");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherStudentsMonthWiseAttendance>>() {
                            }.getType();

                            listAttendance.clear();

                            listAttendance.addAll(gson.fromJson(String.valueOf(jsonArray), type));

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listAttendance.size()>0){
                                        setChart();
                                        viewTeacherStdntAttendanceFrag.findViewById(R.id.ll_chart).setVisibility(View.VISIBLE);
                                    }else{
                                        viewTeacherStdntAttendanceFrag.findViewById(R.id.ll_chart).setVisibility(View.GONE);
                                    }

                                }
                            });


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    private void setChart() {

        int totalWkDays=0,attendanceDays=0,present=0,absent=0,late=0;
        double presentpercent=0,absentPercent=0,latePercent=0;

        int divisor = 0;

        for (int i=0;i<listAttendance.size();i++){
            totalWkDays = totalWkDays+listAttendance.get(i).getWorkingDays();
            attendanceDays = attendanceDays+listAttendance.get(i).getAttendanceTakenDays();

            presentpercent =  presentpercent+listAttendance.get(i).getPresentPercentage();

            absentPercent =  absentPercent+listAttendance.get(i).getAbsentPercentage();

            latePercent =  latePercent+listAttendance.get(i).getLatePercentage();

//            present = present + (int) Math.abs((presentpercent*listAttendance.get(i).getAttendanceTakenDays())/100);
//            late = late + (int) Math.abs((latePercent*listAttendance.get(i).getAttendanceTakenDays())/100);
//            absent = absent + (int) Math.abs((absentPercent*listAttendance.get(i).getAttendanceTakenDays())/100);

            if (listAttendance.get(i).getAttendanceTakenDays()>0){
                divisor++;
            }

        }


        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);




        tvTotalTaken.setText(attendanceDays+"/"+totalWkDays);
        tvTotalPresent.setText((df.format((presentpercent/divisor)).equals("NaN")?0:df.format((presentpercent/divisor)))+"%");
        tvTotalAbsent.setText((df.format((absentPercent/divisor)).equals("NaN")?0:df.format((absentPercent/divisor)))+"%");
        tvTotalLAte.setText(((df.format((latePercent/divisor)).equals("NaN")?0:df.format((latePercent/divisor))))+"%");



        rvChart.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL,false));
        rvChart.setAdapter(new AttendanceAdapter());

    }


    class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder>{

        @NonNull
        @Override
        public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_attend_cahrt_teacher,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {

            holder.tvMonthName.setText(listAttendance.get(position).getMonthName());

            try {
                double absentPercent = listAttendance.get(position).getAbsentPercentage() / 100;
                double latePercent = listAttendance.get(position).getLatePercentage() / 100;
                double presentPercent = listAttendance.get(position).getPresentPercentage() / 100;

                LinearLayout.LayoutParams param;

                //present
                param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        (float) presentPercent
                );
                holder.tvP.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        (float) latePercent
                );
                holder.tvL.setLayoutParams(param);

                param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        (float) absentPercent
                );
                holder.tvA.setLayoutParams(param);

                if (listAttendance.get(position).getAttendanceTakenDays()==0){
                    param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            1f
                    );
                    holder.tvH.setLayoutParams(param);
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return listAttendance.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvMonthName,tvP,tvL,tvA,tvH;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMonthName = itemView.findViewById(R.id.tv_month_name);
                tvP = itemView.findViewById(R.id.tv_p);
                tvL = itemView.findViewById(R.id.tv_l);
                tvA = itemView.findViewById(R.id.tv_a);
                tvH = itemView.findViewById(R.id.tv_h);
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}