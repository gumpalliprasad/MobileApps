package myschoolapp.com.gsnedutech.khub;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KhubModelContentType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class KhubCourseActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubCourseActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_mod_name)
    TextView tvModName;
    @BindView(R.id.rv_topics)
    RecyclerView rvTopics;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    String modId;

    List<KhubModelContentType> topicList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));

        setContentView(R.layout.activity_khub_course);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    void init(){

        if (getIntent().getStringExtra("submodule_name").length()>0){
            tvModName.setText(getIntent().getStringExtra("submodule_name"));
        }else {
            tvModName.setText(getIntent().getStringExtra("module_name"));
        }
        modId = getIntent().getStringExtra("mod_id");
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();


    }


    void getContentTypes(){

        utils.showLoader(KhubCourseActivity.this);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.ModuleContentTypes+"moduleId="+modId, sh_Pref);
        utils.showLog(TAG, "URL - " + AppUrls.KHUB_BASE_URL+AppUrls.ModuleContentTypes+"moduleId="+modId);
        client.getClient().newCall(get).enqueue(new Callback() {
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

                if(!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("result");

                            Gson gson = new Gson();
//
                            Type type = new TypeToken<List<KhubModelContentType>>() {}.getType();
                            topicList.addAll(gson.fromJson(jsonArray.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (topicList.size() > 0) {
                                        rvTopics.setLayoutManager(new LinearLayoutManager(KhubCourseActivity.this));
                                        rvTopics.setAdapter(new TopicNewAdapter(jsonArray));
                                    }
                                }
                            });

                        }

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

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubCourseActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getContentTypes();
            }
            isNetworkAvail = true;
        }
    }


    class TopicNewAdapter extends RecyclerView.Adapter<TopicNewAdapter.ViewHolder>{

//        JSONArray jsonArray;

        public TopicNewAdapter(JSONArray jsonArray) {
//            this.jsonArray = jsonArray;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubCourseActivity.this).inflate(R.layout.item_khub_topic_new,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            try {
                KhubModelContentType obj = topicList.get(position);
                holder.tvTopicName.setText(obj.getTitle());

                if(obj.getContentType().equalsIgnoreCase("Quiz")){

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubQuizActivity.class);
                            intent.putExtra("position",position+"");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                        }
                    });

                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }else if(obj.getContentType().equalsIgnoreCase("flip")){

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubFlipActivityNew.class);
                            intent.putExtra("position",position+"");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("name", obj.getTitle() );
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                        }
                    });


                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }else if(obj.getContentType().equalsIgnoreCase("Blank Text")){

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubBlankText.class);
                            intent.putExtra("name",obj.getTitle());
                            intent.putExtra("position",position+"");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                }else if(obj.getContentType().equalsIgnoreCase("Text Book")){

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubTextBookActivity.class);
                            intent.putExtra("name",obj.getTitle());
                            intent.putExtra("position",position+"");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                }else if(obj.getContentType().equalsIgnoreCase("Video Player")){

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubVideoActivity.class);
                            intent.putExtra("position",0+"");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });

                } else if(obj.getContentType().equalsIgnoreCase("Image")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubImageActivity.class);
                            intent.putExtra("position","0");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("name", obj.getTitle() );
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                } else if (obj.getContentType().equalsIgnoreCase("PDF")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubPPTPdfListing.class);
                            intent.putExtra("position","0");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("name", obj.getTitle() );
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                } else if (obj.getContentType().equalsIgnoreCase("PPT")){
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(KhubCourseActivity.this, KhubPPTPdfListing.class);
                            intent.putExtra("position","0");
                            intent.putExtra("moduleId",modId+"");
                            intent.putExtra("moduleContentId", obj.getId());
                            intent.putExtra("contentType", obj.getContentType());
                            intent.putExtra("studentId", studentId);
                            intent.putExtra("name", obj.getTitle() );
                            intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return topicList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTopicName;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }
}