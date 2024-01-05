package myschoolapp.com.gsnedutech;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TextBookActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener{

    private static final String TAG = TextBookActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.wv_summary)
    WebView wvSummary;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    String summaryString="";
    String topicId;
    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);

        init();

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
            utils.alertDialog(1, TextBookActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getTopicContent();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        topicId = getIntent().getStringExtra("topicId");

        tvTitle.setText("Textbook Content");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        wvSummary .getSettings().setJavaScriptEnabled(true);
        wvSummary.getSettings().setLoadWithOverviewMode(true);
        wvSummary.getSettings().setUseWideViewPort(true);
        wvSummary.getSettings().setDomStorageEnabled(true);
        wvSummary.getSettings().setAllowContentAccess(true);
        wvSummary.getSettings().setDefaultFontSize(40);
        wvSummary.getSettings().setBuiltInZoomControls(true);
        wvSummary.getSettings().setDisplayZoomControls(false);
        wvSummary.getSettings().setAllowFileAccess(true);
        wvSummary.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // For final release of your app, comment the toast notification
                Toast.makeText(TextBookActivity.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    void getTopicContent(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetTopicContent +"schemaName="+sh_Pref.getString("schema","")+"&topicId=" + topicId +
                        "&topicCCMapId=" + getIntent().getStringExtra(AppConst.TopicCCMapId))
                .build();

        utils.showLog(TAG, "url -"+new AppUrls().GetTopicContent + "schemaName=rankr&topicId=" + topicId);


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadWebString(summaryString);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadWebString(summaryString);

                        }
                    });
                }else {
                    ResponseBody responseBody = response.body();
                    String resp = responseBody.string();
                    utils.showLog(TAG,"response "+resp);
                    try{
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            summaryString = ParentjObject.getString("TextBookContent");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadWebString(summaryString);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadWebString(summaryString);

                                }
                            });
                        }
                    }catch (Exception e){

                    }

                }
            }
        });

    }


    private void loadWebString(String summary) {

        summary = "<!DOCTYPE html> <html>"+summary+"</html>";
        File file = new File(getFilesDir(),"temp");
        if (!file.exists()) {
            file.mkdir();
        }
        File gpxfile = null;
        try {
            gpxfile = new File(file, "summary.html");
            if(gpxfile.exists()){
                file.delete();
                gpxfile = new File(file, "summary.html");
            }
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(summary);
            writer.flush();
            writer.close();
//            Toast.makeText(getActivity(), "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }
        if (gpxfile!=null){
            File file1 = new File(gpxfile.getAbsolutePath());
            wvSummary.loadUrl("file:///" + file1);
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}