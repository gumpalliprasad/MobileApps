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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailTeacher;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetailsTeacher;
import myschoolapp.com.gsnedutech.Models.TeacherFilesDetail;
import myschoolapp.com.gsnedutech.Models.TeacherHwObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherHwDisplayNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherHwAssignments.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.et_date)
    EditText etDate;
    @BindView(R.id.rv_attachments)
    RecyclerView rvAttachments;

    List<TeacherHwObj> hwList = new ArrayList<>();



    HomeWorkDetailsTeacher hwObj;

    String hwId="";
    String hwType="";

    TeacherObj teacherObj;

    SharedPreferences sh_Pref;
    List<TeacherFilesDetail> listFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_hw_display_new);

        ButterKnife.bind(this);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);


        hwObj = (HomeWorkDetailsTeacher) getIntent().getSerializableExtra("obj");

        hwId = hwObj.getHomeworkId()+"";


        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        teacherObj = gson.fromJson(json, TeacherObj.class);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, TeacherHwDisplayNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getTeacherHomeWorks();
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

        tvTitle.setText(hwObj.getHomeworkTitle());
        tvType.setText(hwObj.getType());
        tvDesc.setText(hwObj.getHomeworkDetail());
        try {
            etDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(hwObj.getSubmissionDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        etDate.setEnabled(false);

        if (hwObj.getTeacherFilesDetails() != null && hwObj.getTeacherFilesDetails().size()>0){
            listFiles.clear();
            listFiles.addAll(hwObj.getTeacherFilesDetails());
            rvAttachments.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
            rvAttachments.setAdapter(new AttachmentAdapter());
        }else{
            rvAttachments.setVisibility(View.GONE);
        }


        findViewById(R.id.iv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHwDisplayNew.this,TeacherAddHomeworkActivity.class);
                intent.putExtra("obj",(Serializable)hwObj);
                intent.putExtra("type","edit");
                intent.putExtra("sectionId",getIntent().getStringExtra("sectionId"));
                intent.putExtra("subjectId", getIntent().getStringExtra("subjectId"));
                intent.putExtra("elective", getIntent().getStringExtra("elective"));
                intent.putExtra("className", getIntent().getStringExtra("className"));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

    }

    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder>{

        @NonNull
        @Override
        public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(TeacherHwDisplayNew.this).inflate(R.layout.item_hw_attachments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentAdapter.ViewHolder holder, int position) {
            if (listFiles.get(position).getFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_pdf);

            }else{
                holder.ivFile.setImageResource(R.drawable.ic_img_attachment);
            }

            holder.tvFileName.setText(listFiles.get(position).getFileName());

          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (listFiles.get(position).getFilePath().contains("pdf")) {
                      //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                      Intent intent = new Intent(TeacherHwDisplayNew.this, NewPdfViewer.class);
                      intent.putExtra("url", listFiles.get(position).getFilePath());
                      startActivity(intent);
                  }else
                  if (listFiles.get(position).getFilePath().contains("mp4")) {
                      Intent intent = new Intent(TeacherHwDisplayNew.this, PlayerActivity.class);
                      intent.putExtra("url", listFiles.get(position).getFilePath());
                      startActivity(intent);
                  }else
                  if (listFiles.get(position).getFilePath().contains("youtube")) {
                      Intent intent = new Intent(TeacherHwDisplayNew.this, YoutubeActivity.class);

                      String s[] = listFiles.get(position).getFilePath().split("/");

                      intent.putExtra("videoItem", s[s.length - 1]);
                      startActivity(intent);
                  }else
                  if (listFiles.get(position).getFilePath().contains("jpg") || listFiles.get(position).getFilePath().contains("png")) {
                      Intent intent = new Intent(TeacherHwDisplayNew.this, ImageDisp.class);
                      intent.putExtra("path", listFiles.get(position).getFilePath());
                      startActivity(intent);
                  }else
                  if (listFiles.get(position).getFilePath().contains("doc") || listFiles.get(position).getFilePath().equalsIgnoreCase("docx") || listFiles.get(position).getFilePath().equalsIgnoreCase("ppt") || listFiles.get(position).getFilePath().equalsIgnoreCase("pptx") | listFiles.get(position).getFilePath().equalsIgnoreCase("xls") || listFiles.get(position).getFilePath().equalsIgnoreCase("xlsx")) {
                      Intent intent = new Intent(TeacherHwDisplayNew.this, PdfWebViewer.class);
                      intent.putExtra("url", listFiles.get(position).getFilePath());
                      startActivity(intent);
                  }else{
                      Toast.makeText(TeacherHwDisplayNew.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                  }
              }
          });

        }

        @Override
        public int getItemCount() {
            return listFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName;
            ImageView ivFile;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }


    void getTeacherHomeWorks(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        utils.showLog(TAG, "" + new AppUrls().HOMEWORK_GetSectionHomeWorkDetails + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","") + "&sectionId=" + getIntent().getStringExtra("sectionId") + "&branchId=" + teacherObj.getBranchId()+"&userId="+teacherObj.getUserId()
                + "&monthId=" + getIntent().getStringExtra("selMon") + "&yearId=" + getIntent().getStringExtra("selYear"));

        Request request = new Request.Builder()
                .url(new AppUrls().HOMEWORK_GetSectionHomeWorkDetails + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","") + "&sectionId=" + getIntent().getStringExtra("sectionId") + "&branchId=" + teacherObj.getBranchId()+"&userId="+teacherObj.getUserId()
                        + "&monthId=" + getIntent().getStringExtra("selMon") + "&yearId=" + getIntent().getStringExtra("selYear"))
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

                String resp = response.body().string();
                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            if (ParentjObject.has("homeWorkDetails")) {
                                JSONArray jsonArr = ParentjObject.getJSONArray("homeWorkDetails");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<TeacherHwObj>>() {
                                }.getType();

                                hwList.clear();
                                hwList = gson.fromJson(jsonArr.toString(), type);

                                Log.v(TAG, "hwListSize - " + hwList.size());

                                List<HomeWorkDetailsTeacher> listDetails = new ArrayList<>();
                                for (int i=0;i<hwList.size();i++){
                                    List<HomeWorkDetailTeacher> listDetail = new ArrayList<>();
                                    listDetail.addAll(hwList.get(i).getHomeWorkDetails());
                                    hwType = hwList.get(i).getHomeWorkDesc();
                                    for (int j=0;j<listDetail.size();j++){
                                        List<HomeWorkDetailsTeacher> list = new ArrayList<>();
                                        list.addAll(listDetail.get(j).getHomeWorkDetail());
                                        for (int k=0;k<list.size();k++){
                                            list.get(k).setType(hwType);
                                            if (!list.get(k).getHomeworkTitle().equalsIgnoreCase("NA")) {
                                                listDetails.add(list.get(k));
                                            }
                                        }
                                    }

                                }


                                if (listDetails.size()>0){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (int i=0;i<listDetails.size();i++){
                                                if (listDetails.get(i).getHomeworkId()==Integer.parseInt(hwId)){
                                                    hwObj = listDetails.get(i);
                                                    init();
                                                }
                                            }
                                        }
                                    });
                                }

                            }

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        },1000);
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}