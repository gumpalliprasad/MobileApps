package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.VideoSessionObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.SessionRecordingSubjectWise;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.WebViewActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class StudentRecordedVideosFragCopy extends Fragment {

    private static final String TAG = "SriRam -" + StudentRecordedVideosFragCopy.class.getName();

    MyUtils utils = new MyUtils();

    View viewStudentRecordedVideosFrag;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    List<VideoSessionObj> videoSessionrecordingObjList = new ArrayList<>();

    @BindView(R.id.tv_recorded_count)
    TextView tvRecordedCount;

    @BindView(R.id.rv_recorded_class)
    RecyclerView rvRecordedClass;

    @BindView(R.id.tv_no_recorded)
    TextView tvNoRecorded;



    Activity mActivity;

    @Override
    public void onAttach(@NonNull Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    public StudentRecordedVideosFragCopy() {
        // Required empty public constructor
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewStudentRecordedVideosFrag = inflater.inflate(R.layout.fragment_student_recorded_videos, container, false);
        unbinder = ButterKnife.bind(this, viewStudentRecordedVideosFrag);

        init();

        if (NetworkConnectivity.isConnected(mActivity)) {
           getRecordedVideos();
        } else {
            new MyUtils().alertDialog(1, mActivity, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }

        return viewStudentRecordedVideosFrag;
    }

    void init(){
        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        rvRecordedClass.setLayoutManager(new LinearLayoutManager(mActivity));

    }

    void getRecordedVideos() {
        utils.showLoader(mActivity);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        StudentBottomClasses parentFrag = ((StudentBottomClasses)StudentRecordedVideosFragCopy.this.getParentFragment());

        String month = parentFrag.currentMonth;
        String year = parentFrag.currentYear;
        utils.showLog(TAG,"url "+AppUrls.GetSessionRecBySecTopic+"schemaName="+sh_Pref.getString("schema", "")+"&sectionId="+sObj.getClassCourseSectionId()+"&filterMonth="+year+"-"+month+"-01");

        Request get = new Request.Builder()
                .url(AppUrls.GetSessionRecBySecTopic+"schemaName="+sh_Pref.getString("schema", "")+"&sectionId="+sObj.getClassCourseSectionId()+"&filterMonth="+year+"-"+month+"-01")
                .build();


//        utils.showLog(TAG, "url - https://www.e-vidya.online/getSessionRecBySecTopic?schemaName=cha5324&sectionId=4397");
//        Request get = new Request.Builder()
//                .url("https://www.e-vidya.online/getSessionRecBySecTopic?schemaName=cha5324&sectionId=4397")
//                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        }, 2000);

//                            rvHw.setVisibility(View.GONE);
//                            viewStudentBottomHome.findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                }
                            }, 2000);
//                            rvHw.setVisibility(View.GONE);
//                            viewStudentBottomHome.findViewById(R.id.iv_no_home_work).setVisibility(View.VISIBLE);
                        }
                    });
                } else {

                    String resp = responseBody.string();
//                    utils.showLog(TAG, "response- " + resp);

                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        JSONArray jsonArr = ParentjObject.getJSONArray("recordings");

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<VideoSessionObj>>() {
                        }.getType();

                        videoSessionrecordingObjList.clear();
                        videoSessionrecordingObjList.addAll(gson.fromJson(jsonArr.toString(), type));

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (videoSessionrecordingObjList.size()>0){
                                    rvRecordedClass.setAdapter(new SessionAdapter(videoSessionrecordingObjList));
                                    rvRecordedClass.setVisibility(View.VISIBLE);
                                    tvRecordedCount.setVisibility(View.VISIBLE);
                                    tvNoRecorded.setVisibility(View.GONE);
                                }
                                else {
                                    rvRecordedClass.setVisibility(View.GONE);
                                    tvRecordedCount.setVisibility(View.GONE);
                                    tvNoRecorded.setVisibility(View.VISIBLE);
                                }
//                                tvRecordedCount.setText(videoSessionrecordingObjList.size() + " Videos");


                               tvRecordedCount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(mActivity, SessionRecordingSubjectWise.class);
                                        intent.putExtra("videos", (Serializable) videoSessionrecordingObjList);
                                        startActivity(intent);
                                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                });
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

               mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        }, 2000);
                    }
                });

            }
        });


    }


    class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

        List<VideoSessionObj> videoSessionrecordingList;

        public SessionAdapter(List<VideoSessionObj> videoSessionrecordingList) {
            this.videoSessionrecordingList = videoSessionrecordingList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recorded_session, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tvVidName.setText(videoSessionrecordingList.get(position).getChapterName());
            holder.tvSubName.setText(videoSessionrecordingList.get(position).getSubjectName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(mActivity, WebViewActivity.class);
                        intent.putExtra("videoItem", videoSessionrecordingList.get(position).getVideoLink());
                        startActivity(intent);

                    } catch (Exception e) {
                        utils.showLog(TAG, e.getMessage());

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return videoSessionrecordingList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvVidName, tvSubName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVidName = itemView.findViewById(R.id.tv_vid_name);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
            }
        }
    }
}