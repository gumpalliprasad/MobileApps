package myschoolapp.com.gsnedutech.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.LiveDetails;
import myschoolapp.com.gsnedutech.Models.LiveVideo;
import myschoolapp.com.gsnedutech.Models.LiveVideoInfo;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;


public class StudentLiveClassesFragCopy extends Fragment {

    private static final String TAG = "SriRam -" + StudentLiveClassesFragCopy.class.getName();
    String serverTime = "";

    MyUtils utils = new MyUtils();

    View viewStudentLiveClassesFrag;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    List<LiveVideoInfo> listLiveClassesInfo = new ArrayList<>();
    List<LiveVideo> listLiveClasses = new ArrayList<>();
    LiveVideo liveClass = null;

    List<CountDownTimer> listTimers = new ArrayList<>();

    @BindView(R.id.tv_sub_name)
    TextView tvSubName;
    @BindView(R.id.tv_vid_name)
    TextView tvVidName;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.rv_live_class)
    RecyclerView rvLiveClasses;

    public StudentLiveClassesFragCopy() {
        // Required empty public constructor
    }

    Activity mActivity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        viewStudentLiveClassesFrag = inflater.inflate(R.layout.fragment_student_live_classes, container, false);
        unbinder = ButterKnife.bind(this, viewStudentLiveClassesFrag);

        init();

        return viewStudentLiveClassesFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();
        rvLiveClasses.setLayoutManager(new LinearLayoutManager(mActivity));

        viewStudentLiveClassesFrag.findViewById(R.id.tv_join).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, LiveDetails.class);
                intent.putExtra("live", (Serializable) liveClass);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

    }

    @Override
    public void onResume() {
        if (mActivity!=null){
            utils.showLoader(mActivity);
        }
        if (NetworkConnectivity.isConnected(mActivity)) {
            getServerTime();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
        super.onResume();
    }


    private void getServerTime() {


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
//                getRecordedVideos();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
//                    getRecordedVideos();
                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        serverTime = ParentjObject.getString("dateTime");
                        if (NetworkConnectivity.isConnected(mActivity)) {
                            getLiveClasses();

                        } else {
                            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                                    getString(R.string.action_settings), getString(R.string.action_close), false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    void getLiveClasses() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        StudentBottomClasses parentFrag = ((StudentBottomClasses) StudentLiveClassesFragCopy.this.getParentFragment());

        String month = parentFrag.currentMonth;
        String year = parentFrag.currentYear;


        Request get = new Request.Builder()
                .url(AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId()+"&sectionId="+sObj.getClassCourseSectionId()+"&filterMonth="+year+"-"+month+"-01")
                .build();

        utils.showLog(TAG, "url - " + AppUrls.GetStudentLiveVideos + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId()+"&sectionId="+sObj.getClassCourseSectionId()+"&filterMonth="+year+"-"+month+"-01");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
//                        rvLiveClasses.setVisibility(View.GONE);
//                        viewStudentBottomHome.findViewById(R.id.iv_no_live_classes).setVisibility(View.VISIBLE);
                    }
                });
//                getRecordedVideos();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });

//                        getRecordedVideos();
                    } else {
                        String resp = responseBody.string();



                        utils.showLog(TAG, "response- " + resp);

                        JSONObject ParentjObject = new JSONObject(resp);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<LiveVideoInfo>>() {
                        }.getType();

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("liveVideoInfo");
                            listLiveClassesInfo.clear();
                            listLiveClassesInfo.addAll(gson.fromJson(jsonArr.toString(), type));

                            listLiveClasses.clear();

                            for (int i=0;i<listLiveClassesInfo.size();i++){
                                listLiveClasses.addAll(listLiveClassesInfo.get(i).getLiveVideos());
                            }

                            for (int i = 0; i < listLiveClasses.size(); i++) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date d = null;
                                try {
                                    d = df.parse(listLiveClasses.get(i).getLiveStreamStartTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Calendar gc = new GregorianCalendar();
                                gc.setTime(d);
                                gc.add(Calendar.MINUTE, Integer.parseInt(listLiveClasses.get(i).getDuration()));
                                Date d2 = gc.getTime();
                                listLiveClasses.get(i).setEndTime(df.format(d2));
                                utils.showLog(TAG, "End time " + listLiveClasses.get(i).getEndTime());

                            }


                            List<LiveVideo> listUpcoming = new ArrayList<>();

                            for (int i = 0; i < listLiveClasses.size(); i++) {
                                if (getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listLiveClasses.get(i).getLiveStreamStartTime()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime)) != null) {
                                    listUpcoming.add(listLiveClasses.get(i));
                                } else if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listLiveClasses.get(i).getEndTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {
                                    liveClass = listLiveClasses.get(i);
                                }
                            }


                            if (listUpcoming.size() > 0) {

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvLiveClasses.setVisibility(View.VISIBLE);
                                        rvLiveClasses.setAdapter(new LiveAdapter(listUpcoming));
                                    }
                                });

                            } else {

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvLiveClasses.setVisibility(View.GONE);
                                        viewStudentLiveClassesFrag.findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
                                    }
                                });
                            }


                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (liveClass != null) {
                                        viewStudentLiveClassesFrag.findViewById(R.id.cv_live).setVisibility(View.VISIBLE);
                                        tvSubName.setText(liveClass.getSubjectName());
                                        tvVidName.setText(liveClass.getLiveStreamName());
                                        tvDuration.setText(liveClass.getDuration() + " mins");
                                        try {
                                            tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getEndTime())));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        viewStudentLiveClassesFrag.findViewById(R.id.cv_live).setVisibility(View.GONE);
                                    }
                                }
                            });

                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvLiveClasses.setVisibility(View.GONE);
                                    viewStudentLiveClassesFrag.findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mActivity.runOnUiThread(() -> utils.dismissDialog());

//                getRecordedVideos();

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

    class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

        List<LiveVideo> listUpcoming;

        public LiveAdapter(List<LiveVideo> listUpcoming) {
            this.listUpcoming = listUpcoming;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_home_live_class, parent, false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvTitle.setText(listUpcoming.get(position).getLiveStreamName());
            holder.tvSubject.setText(listUpcoming.get(position).getSubjectName());
            try {
                Date d = getDateDifference(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime));
                if (d != null) {
                    CountDownTimer t = new CountDownTimer(d.getTime(), 1000) {
                        public void onTick(long millisUntilFinished) {
                            holder.tvTime.setText(hmsTimeFormatter(millisUntilFinished));
                            holder.ivLive.setVisibility(View.GONE);
                        }

                        public void onFinish() {
                            viewStudentLiveClassesFrag.findViewById(R.id.cv_live).setVisibility(View.VISIBLE);

                            liveClass = listUpcoming.get(position);
                            tvSubName.setText(liveClass.getSubjectName());
                            tvVidName.setText(liveClass.getLiveStreamName());
                            tvDuration.setText(liveClass.getDuration() + " mins");
                            try {
                                tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(liveClass.getEndTime())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            listUpcoming.remove(position);
                            notifyDataSetChanged();

                            if (listUpcoming.size()<=0){
                                rvLiveClasses.setVisibility(View.GONE);
                                viewStudentLiveClassesFrag.findViewById(R.id.cv_no_live_classes).setVisibility(View.VISIBLE);
                            }



//                            holder.tvTimeType.setText("Ends At");
//                            try {
//                                holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime())));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            holder.ivLive.setVisibility(View.VISIBLE);


                        }

                    };

                    t.start();

                    listTimers.add(t);

                    utils.showLog(TAG, "starts in " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
                } else {
                    if (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime()).after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(serverTime))) {

                        holder.tvTimeType.setText("Ends At");
                        holder.tvTime.setText(new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getEndTime())));
                        holder.ivLive.setVisibility(View.VISIBLE);

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.ivLive.getVisibility() == View.VISIBLE) {
                        String[] val = (listUpcoming.get(position).getJoinUrl()).split("j/");
                        launchZoomUrl(val[1].replace("?", "&"), listUpcoming.get(position).getMeetingPwd());
                    } else {
                        try {
                            new MyUtils().alertDialog(3, mActivity, "Oops!", "Live Class will Start at " +
                                            new SimpleDateFormat("dd MMM, yyyy hh:mm a").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(listUpcoming.get(position).getLiveStreamStartTime())),
                                    "Close", "", false);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listUpcoming.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubject, tvTimeType, tvTime, tvTitle;
            ImageView ivLive;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubject = itemView.findViewById(R.id.tv_sub);
                tvTitle  =itemView.findViewById(R.id.tv_title);
                tvTimeType = itemView.findViewById(R.id.tv_time_type);
                tvTime = itemView.findViewById(R.id.tv_time);
                ivLive = itemView.findViewById(R.id.iv_live);
            }
        }
    }


    private void launchZoomUrl(String meetingId, String meetingPassword) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://narayanagroup.zoom.us/j/6583554590?pwd="));

        boolean isAppInstalled = appInstalledOrNot("us.zoom.videomeetings");

        if (isAppInstalled) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=6583554590" + "&pwd=ZGhSZytWUmtMNDJWdmFxdS8rN09SQT09"));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=84240144502" + "&pwd="));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("zoomus://zoom.us/join?confno=" + meetingId));
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
//            Toast.makeText(StudentLiveClasses.this, "Please Install Zoom App", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            ViewGroup viewGroup = mActivity.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.cv_zoom, viewGroup, false);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
            alertDialog.getWindow().setLayout(width, height);
            alertDialog.show();

            dialogView.findViewById(R.id.tv_install).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=us.zoom.videomeetings"));
                    startActivity(intent);
                    alertDialog.dismiss();

                }
            });

            dialogView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
        /*Intent zoomActivity = new Intent(getActivity(), ZoomActivity.class);
        zoomActivity.putExtra(AppConst.MEETING_ID, meetingId);
        zoomActivity.putExtra(AppConst.DISPLAY_NAME, sObj.getSectionName());
        zoomActivity.putExtra(AppConst.MEETING_USER_ID, sObj.getStudentId());
        if(meetingPassword != null)
            zoomActivity.putExtra(AppConst.MEETING_PASSWORD, meetingPassword);
        getActivity().startActivity(zoomActivity);*/
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = mActivity.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private String hmsTimeFormatter(long milliSeconds) {

        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    @Override
    public void onPause() {

        for (int i = 0; i < listTimers.size(); i++) {
            listTimers.get(i).cancel();
        }

        super.onPause();
    }

}