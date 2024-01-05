package myschoolapp.com.gsnedutech.khub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

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
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.khub.models.KhubImagePptPdfModels;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KhubPPTPdfListing extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubPPTPdfListing.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    int pos;
    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_mod_name)
    TextView tvModName;
    @BindView(R.id.rv_topics)
    RecyclerView rvTopics;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    String modId;
    int selectedPosition=0;

    String contentType = "", moduleContentId = "", moduleId = "";

    List<KhubImagePptPdfModels> pptPdfList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_p_p_t_pdf_listing);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        pos = Integer.parseInt(getIntent().getStringExtra("position"));

        moduleId = getIntent().getStringExtra("moduleId");
        moduleContentId = getIntent().getStringExtra("moduleContentId");
        contentType = getIntent().getStringExtra("contentType");
        tvModName.setText(getIntent().getStringExtra("name"));

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubPPTPdfListing.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (pptPdfList.size() == 0)
                    getContent();
            }
            isNetworkAvail = true;
        }
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




    void getContent() {

        utils.showLoader(KhubPPTPdfListing.this);

        ApiClient client = new ApiClient();
        String URL = AppUrls.KHUB_BASE_URL+AppUrls.Content+"contentType="+contentType+"&moduleContentId="+moduleContentId+"&moduleId="+moduleId+"&studentId="+sObj.getStudentId()+"&schemaName="+sh_Pref.getString("schema","");

        Request get = client.getRequest(URL, sh_Pref);
        utils.showLog(TAG, "URL - " + URL);
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

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject object = new JSONObject(resp);

                        if (object.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<KhubImagePptPdfModels>>() {
                            }.getType();
                            pptPdfList.clear();
                            pptPdfList.addAll(gson.fromJson(jsonArray.toString(), type));

                            Log.v(TAG, "pptPdfArrayList - " + pptPdfList.size());
                            if (pptPdfList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pptPdfList.size() > 0) {
                                            rvTopics.setLayoutManager(new LinearLayoutManager(KhubPPTPdfListing.this));
                                            rvTopics.setAdapter(new TopicNewAdapter(jsonArray));
                                        }
                                    }
                                });
                            }
                            else {
                                runOnUiThread(() -> new AlertDialog.Builder(KhubPPTPdfListing.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage("No Data To Load")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show());
                            }



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

    class TopicNewAdapter extends RecyclerView.Adapter<TopicNewAdapter.ViewHolder>{

//        JSONArray jsonArray;

        public TopicNewAdapter(JSONArray jsonArray) {
//            this.jsonArray = jsonArray;
        }

        @NonNull
        @Override
        public TopicNewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TopicNewAdapter.ViewHolder(LayoutInflater.from(KhubPPTPdfListing.this).inflate(R.layout.item_khub_topic_new,parent,false));

        }

        @Override
        public void onBindViewHolder(@NonNull TopicNewAdapter.ViewHolder holder, int position) {

            holder.tvTopicName.setText(pptPdfList.get(position).getTitle());

            holder.itemView.setOnClickListener(v -> {
                selectedPosition = position;
                if (pptPdfList.get(position).getPath().contains("pdf")) {
                    Intent intent = new Intent(KhubPPTPdfListing.this, NewPdfViewer.class);
                    intent.putExtra("url", pptPdfList.get(position).getPath());
                    startActivity(intent);
                }else if (pptPdfList.get(position).getPath().contains("doc") || pptPdfList.get(position).getPath().equalsIgnoreCase("docx") || pptPdfList.get(position).getPath().equalsIgnoreCase("ppt") || pptPdfList.get(position).getPath().equalsIgnoreCase("pptx")) {
                    Intent intent = new Intent(KhubPPTPdfListing.this, PdfWebViewer.class);
                    intent.putExtra("url", pptPdfList.get(position).getPath());
                    startActivity(intent);
                }else{
                    Toast.makeText(KhubPPTPdfListing.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return pptPdfList.size();
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
        postContentActivity();
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    void postContentActivity(){
        utils.showLoader(this);
        ApiClient client  = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {

            float pr = (((float) (selectedPosition+1)/ pptPdfList.size())*100);


            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", getIntent().getStringExtra("courseId"));
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleContentId", moduleContentId);
            jsonObject.put("contentType", contentType);
            jsonObject.put("progress", pr);
            jsonObject.put("activityData",array );

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = client.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity,body, sh_Pref);
        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.ContentActivity);
        utils.showLog(TAG, "body -"+ jsonObject.toString());
        client.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject ParentjObject = new JSONObject(res);
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
                                finish();
                            });

                        }
                    }
                    catch (JSONException e) {
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



}