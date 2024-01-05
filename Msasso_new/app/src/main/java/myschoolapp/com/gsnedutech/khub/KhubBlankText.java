package myschoolapp.com.gsnedutech.khub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import myschoolapp.com.gsnedutech.khub.models.KhubBlankTextObj;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KhubBlankText extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubBlankText.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    MyUtils utils = new MyUtils();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wv_text)
    WebView wvText;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.ll_prev)
    LinearLayout llPrev;
    @BindView(R.id.ll_next)
    LinearLayout llNext;
    @BindView(R.id.tv_next)
    TextView tvNext;


    List<KhubBlankTextObj> listBlankTexts = new ArrayList<>();

    String contentType = "", moduleContentId = "", moduleId = "";

    int qNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_blank_text);

        ButterKnife.bind(this);

        init();
    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        tvTitle.setText(getIntent().getStringExtra("name"));
        moduleId = getIntent().getStringExtra("moduleId");
        moduleContentId = getIntent().getStringExtra("moduleContentId");
        contentType = getIntent().getStringExtra("contentType");
//        listPaths.addAll((Collection<? extends String>) getIntent().getSerializableExtra("paths"));


//        loadText(qNum);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llPrev.setOnClickListener(this);
        llNext.setOnClickListener(this);

    }

    void getContent() {

        utils.showLoader(this);

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
                            Type type = new TypeToken<List<KhubBlankTextObj>>() {
                            }.getType();
                            listBlankTexts.clear();
                            listBlankTexts.addAll(gson.fromJson(jsonArray.toString(), type));

                            utils.showLog(TAG, "Blank Text ArrayList - " + listBlankTexts.size());
                            if (listBlankTexts.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listBlankTexts.size() > 0) {
                                            try {
                                                if (object.has("activityData") && object.getJSONArray("activityData").length()>0){
                                                    JSONArray arr = object.getJSONArray("activityData");
                                                    JSONArray jsonArray1 = arr.getJSONObject(0).getJSONArray("activityData");
                                                    qNum = jsonArray1.getJSONObject(0).getInt("positionId");
                                                    if (qNum == listBlankTexts.size()-1){
                                                        showFinishAlert("finish");
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            loadText(qNum);
                                        }
                                    }
                                });
                            }
                            else {
                                runOnUiThread(() -> new AlertDialog.Builder(KhubBlankText.this)
                                        .setTitle(getString(R.string.app_name))
                                        .setMessage("No Data To Load")
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                            dialog.dismiss();
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show());
                            }
//


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

    void showFinishAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(KhubBlankText.this);
        builder1.setMessage("You have finished this Activity. Do you want to start Again?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        recreate();
                        postContentActivity("refresh");
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void loadText(int i) {

        llPrev.setVisibility(View.VISIBLE);
        llNext.setVisibility(View.VISIBLE);
        tvNext.setText("Next Page");

        if(i == 0)
        {
            llPrev.setVisibility(View.INVISIBLE);
        }
        if (i== listBlankTexts.size()-1){
            tvNext.setText("Finish");
        }
        tvCount.setText(""+(i+1)+"/"+listBlankTexts.size());

        String mainString = listBlankTexts.get(i).getDesc();

        mainString = utils.cleanWebString(mainString);
        wvText.loadData(mainString, "text/html; charset=utf-8", "utf-8");
        wvText.scrollTo(0, 0);
    }

    @Override
    public void onBackPressed() {
        postContentActivity("");
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    void postContentActivity(String refresh){
        utils.showLoader(this);
        ApiClient client  = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {

            float pr = (((float) (qNum+1)/ listBlankTexts.size())*100);


            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", getIntent().getStringExtra("courseId"));
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleContentId", moduleContentId);
            jsonObject.put("contentType", contentType);
            if (refresh.isEmpty()){
                jsonObject.put("progress", pr);
                JSONObject data = new JSONObject();
                data.put("positionId", qNum);
                data.put("moduleDataId", listBlankTexts.get(qNum).getId());
                array.put(data);
                jsonObject.put("activityData",array );
            }
            else {
                jsonObject.put("progress", 0);
                jsonObject.put("activityData",new JSONArray() );
            }


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
                            if (refresh.isEmpty())runOnUiThread(() -> finish());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_prev:
                if (--qNum<0){
                    qNum = 0;
                }
                loadText(qNum);
                break;
            case R.id.ll_next:
                if (!tvNext.getText().toString().equals("Finish")) {
                    if (++qNum >= listBlankTexts.size()) {
                        qNum = listBlankTexts.size() - 1;
                    }
                    loadText(qNum);
                }
                else {
                    onBackPressed();
                }
                break;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubBlankText.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (listBlankTexts.size() == 0)
                    getContent();
            }
            isNetworkAvail = true;
        }
    }
}