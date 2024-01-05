package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import myschoolapp.com.gsnedutech.Models.ContentObject;
import myschoolapp.com.gsnedutech.Models.TopicSummary;
import myschoolapp.com.gsnedutech.Util.ApiClient;
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

public class SummaryActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener  {

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    WebView wvSummary;
    TextView tvTitle;

    MyUtils utils = new MyUtils();

    List<TopicSummary> topicSummaryList = new ArrayList<>();
    String contentType = "";
    String screenType = "";
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, SummaryActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                init();
                if(getIntent().getStringExtra(AppConst.API_TYPE).equalsIgnoreCase(AppConst.IMPORTANT) ||
                        getIntent().getStringExtra(AppConst.API_TYPE).equalsIgnoreCase(AppConst.TEXT_BOOK)){
                    getTextBookContent(getIntent().getStringExtra(AppConst.TOPIC_CC_MAP_ID), getIntent().getStringExtra(AppConst.API_TYPE));
                }else {
                    getSummary(getIntent().getStringExtra(AppConst.CHAPTER_ID), getIntent().getStringExtra(AppConst.CHAPTER_CC_MAP_ID));
                }
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    void init() {
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        wvSummary = findViewById(R.id.wv_summary);
        tvTitle = findViewById(R.id.tv_title);
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());

        if (getIntent().hasExtra(AppConst.CONTENT_TYPE))
            contentType = getIntent().getStringExtra(AppConst.CONTENT_TYPE);
        if (getIntent().hasExtra(AppConst.API_TYPE))
            screenType = getIntent().getStringExtra(AppConst.API_TYPE);
    }

    private void loadWebString(String summary) {
        summary = "<!DOCTYPE html> <html>" + summary + "</html>";
        wvSummary.loadData(summary, "text/html; charset=utf-8", "utf-8");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    void getSummary(String chapterId, String chapterCCMapId) {

        ApiClient apiClient = new ApiClient();
        utils.showLog("TAG", "url -" + AppUrls.GetCourseTopicSummary
                + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                + "&chapterId=" + chapterId +
                "&contentType=" + contentType +
                "&chapterCCMapId=" + chapterCCMapId);
        Request request = apiClient.getRequest(
                AppUrls.GetCourseTopicSummary
                        + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                        + "&chapterId=" + chapterId +
                        "&contentType=" + contentType +
                        "&chapterCCMapId=" + chapterCCMapId
                        + "&contentOwner=CEDZ",
                sh_Pref
        );
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //getQuestionCount();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {

                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        //getQuestionCount();

                    } else {
                        String resp = responseBody.string();

                        utils.showLog("TAG", "response- " + resp);

                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("chapterAnnexure");


                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TopicSummary>>() {
                            }.getType();

                            topicSummaryList.clear();
                            topicSummaryList.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog("TAG", "topicSummaryList - " + topicSummaryList.size());


                            topicSummaryList.clear();
                            topicSummaryList.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog("TAG", "topicSummaryList - " + topicSummaryList.size());

                            runOnUiThread(() -> {
                                String summaryString = "";
                                for (int i = 0; i < topicSummaryList.size(); i++) {
                                    summaryString = topicSummaryList.get(i).getAnnexureContent();
                                }
                                summaryString = summaryString.replaceAll("<span.*?>", "");
                                summaryString = summaryString.replaceAll("&#39;", "");

                                summaryString = "<!DOCTYPE html> <html><style>img{display: inline; height: auto; max-width: 100%;}</style>"
                                        + summaryString + "</html>";

                                loadWebString(summaryString);
                            });


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //getQuestionCount();

            }
        });


    }

    void getTextBookContent(String topicCCMapId, String apiType) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        String apiValues = "";
        if (apiType.equalsIgnoreCase(AppConst.TEXT_BOOK)) {
            apiValues = AppUrls.getTextbookContent;
        } else
            apiValues = AppUrls.getImportantContent;

        Request get = new Request.Builder()
                .url(apiValues + "schemaName=" + sh_Pref.getString("schema", "") +
                        "&topicCCMapId=" + topicCCMapId +
                        "&contentOwner=CEDZ")
                .build();

        utils.showLog("TAG", "url -" + apiValues + "schemaName=" + sh_Pref.getString("schema", "") +
                "&topicCCMapId=" + topicCCMapId);


        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                } else {
                    ResponseBody responseBody = response.body();
                    String resp = responseBody.string();
                    utils.showLog("TAG", "response " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            ContentObject contentObject = new Gson().fromJson(resp, ContentObject.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (contentObject.getTextbookContentList() != null && !contentObject.getTextbookContentList().isEmpty()) {
                                        String textBook = contentObject.getTextbookContentList().get(0).getTextbookContent();
                                        if (!textBook.equalsIgnoreCase("")) {
                                            if (!textBook.equalsIgnoreCase("NA")) {
                                                loadWebString(textBook);
                                            }
                                        }
                                    }

                                    if (contentObject.getImportantContentList() != null && !contentObject.getImportantContentList().isEmpty()) {
                                        String importantContent = contentObject.getImportantContentList().get(0).getImportantContent();
                                        if (!importantContent.equalsIgnoreCase("")) {
                                            if (!importantContent.equalsIgnoreCase("NA")) {
                                                loadWebString(importantContent);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }
}