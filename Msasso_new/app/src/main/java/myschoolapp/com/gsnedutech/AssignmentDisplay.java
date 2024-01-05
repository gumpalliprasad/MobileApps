package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.StudentHWFile;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AssignmentDisplay extends AppCompatActivity {
    private static final String TAG = AssignmentDisplay.class.getName();
    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_assignment_keys)
    RecyclerView rvAssignmentKeys;
    @BindView(R.id.rv_vid_explain)
    RecyclerView rvVidExplanation;
    @BindView(R.id.tv_sub)
    TextView tvSub;
    @BindView(R.id.tv_desc)
    TextView tvDesc;

    List<StudentHWFile> fileList = new ArrayList<>();

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    HomeWorkDetails hwObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_display);

        ButterKnife.bind(this);

        init();

    }

    @Override
    protected void onResume() {
        getHomeWorkFiles();

        super.onResume();
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("student_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            studentId = sObj.getStudentId();
        }else{
            studentId = getIntent().getStringExtra("studentId");
        }

        hwObj = (HomeWorkDetails) getIntent().getSerializableExtra("hwObj");

        tvSub.setText(hwObj.getSubjectName());

        if (hwObj.getHwStatus().equalsIgnoreCase("reassign")){
            tvSub.setText(hwObj.getSubjectName()+" | "+hwObj.getHwStatus());

        }
        tvDesc.setText(hwObj.getHomeDetails());

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.ll_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (findViewById(R.id.iv_upload).getVisibility()==View.VISIBLE){
                    Intent i = new Intent(AssignmentDisplay.this, AssignmentUpload.class);
                    i.putExtra("hwObj",(Serializable)hwObj);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }else {
                    Intent i = new Intent(AssignmentDisplay.this, AssignmentFileDisplay.class);

                    i.putExtra("hwObj",(Serializable)hwObj);
                    i.putExtra("studentFiles",(Serializable)fileList);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            }
        });

        if (hwObj.getFilesDetails().size()==0){
            findViewById(R.id.ll_open_attachments).setAlpha(0.5f);
        }

        findViewById(R.id.ll_open_attachments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentDisplay.this, AssignmentFileDisplay.class);
                intent.putExtra("files",(Serializable)hwObj.getFilesDetails());
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });


        if (hwObj.getHwStatus().equalsIgnoreCase("REASSIGN")){
            findViewById(R.id.ll_reassign_upload).setVisibility(View.VISIBLE);
           findViewById(R.id.ll_reassign_upload).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i = new Intent(AssignmentDisplay.this, AssignmentUpload.class);
                   i.putExtra("hwObj",(Serializable)hwObj);
                   startActivity(i);
                   overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
               }
           });
        }



        rvAssignmentKeys.setLayoutManager(new LinearLayoutManager(AssignmentDisplay.this,RecyclerView.HORIZONTAL,false));
        rvAssignmentKeys.setAdapter(new AssignmentKeyAdapter());

        rvVidExplanation.setLayoutManager(new LinearLayoutManager(AssignmentDisplay.this,RecyclerView.HORIZONTAL,false));
        rvVidExplanation.setAdapter(new VidExplanationAdapter());

//        getHomeWorkFiles();


    }

    class AssignmentKeyAdapter extends RecyclerView.Adapter<AssignmentKeyAdapter.ViewHolder>{

        @NonNull
        @Override
        public AssignmentKeyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentDisplay.this).inflate(R.layout.item_assignments_key,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AssignmentKeyAdapter.ViewHolder holder, int position) {
            holder.tvKeyName.setText("Assignment Key "+(position+1));
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvKeyName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvKeyName = itemView.findViewById(R.id.tv_key_name);

            }
        }
    }

    class VidExplanationAdapter extends RecyclerView.Adapter<VidExplanationAdapter.ViewHolder>{

        @NonNull
        @Override
        public VidExplanationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AssignmentDisplay.this).inflate(R.layout.item_assign_video_explanation,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull VidExplanationAdapter.ViewHolder holder, int position) {
            holder.tvVidTitle.setText("Video Explanation "+(position+1));
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvVidTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvVidTitle = itemView.findViewById(R.id.tv_vid_title);
            }
        }
    }

    void getHomeWorkFiles(){

        String currentMonth = new SimpleDateFormat("MM").format(new Date());
        String currentYear = new SimpleDateFormat("yyyy").format(new Date());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetHomeWorkFilesByStudent + "studentId=" + studentId + "&schemaName=" + sh_Pref.getString(AppConst.SCHEMA, "") + "&hwId=" + hwObj.getHomeworkId())
                .build();

        utils.showLog(TAG, "url -"+ new AppUrls().GetHomeWorkFilesByStudent + "studentId=" + studentId + "&schemaName=" + sh_Pref.getString(AppConst.SCHEMA, "") + "&hwId=" + hwObj.getHomeworkId());


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.showLog(TAG,"error");
                            }
                        });
                    }else{

                        String resp = responseBody.string();
                        utils.showLog(TAG,resp);


                        JSONObject ParentjObject = new JSONObject(resp);

                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            if (ParentjObject.has("studentHWFiles")) {
                                JSONArray jsonArr = ParentjObject.getJSONArray("studentHWFiles");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<StudentHWFile>>() {
                                }.getType();

                                fileList.clear();
                                fileList = gson.fromJson(jsonArr.toString(), type);
                                utils.showLog(TAG, "hwListSize - " + fileList.size());
                                if (fileList.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            findViewById(R.id.iv_upload).setVisibility(View.GONE);
                                            ((TextView)findViewById(R.id.tv_upload)).setText("Your Works");
                                        }
                                    });

                                }


                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!sh_Pref.getBoolean("student_loggedin", false)) {
                                        findViewById(R.id.ll_upload).setVisibility(View.GONE);
                                    }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }



}