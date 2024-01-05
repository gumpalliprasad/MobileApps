package myschoolapp.com.gsnedutech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.SubChapterTopic;
import myschoolapp.com.gsnedutech.Models.TopicQAObj;
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

public class QAActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = QAActivity.class.getName();
    MyUtils utils = new MyUtils();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    QueAdapter.ViewHolder selectedQues = null;

    List<TopicQAObj> topicQAList = new ArrayList<>();

    @BindView(R.id.rv_qa)
    RecyclerView rvQa;

    int queNo = 0;
    SubChapterTopic mTopic = new SubChapterTopic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_a);
        ButterKnife.bind(this);
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
            utils.alertDialog(1, QAActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getQA();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    private void init() {
        utils.showLoader(this);
        mTopic = (SubChapterTopic) getIntent().getSerializableExtra(AppConst.TOPIC);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
    }

    void getQA(){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetCourseTopicQA + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                        + "&contentOwner=CEDZ" +
                        "&topicId=" + mTopic.getTopicId() +
                        "&topicCCMapId=" + mTopic.getTopicCCMapId())
                .build();

        utils.showLog(TAG, "url -"+new AppUrls().GetCourseTopicQA + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "")
                + "&contentOwner=CEDZ" +
                "&topicId=" + mTopic.getTopicId() +
                "&topicCCMapId=" + mTopic.getTopicCCMapId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {



                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {


                }else {
                    String resp = responseBody.string();

                    utils.showLog(TAG,"response- "+resp);
                    try{
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("Questions");


                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TopicQAObj>>() {
                            }.getType();

                            topicQAList.clear();
                            topicQAList.addAll(gson.fromJson(jsonArr.toString(), type));

                            if (topicQAList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvQa.setLayoutManager(new LinearLayoutManager(QAActivity.this));
                                        rvQa.setAdapter(new QueAdapter(topicQAList));
                                    }
                                });

                            }

                        }
                    }catch(Exception e){
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


    private void loadQuestion(int queNo, WebView wvQ, WebView wvA) {

        String question = "<!DOCTYPE html> <html>" + topicQAList.get(queNo).getQuestion() + "</html>";
        question = question.replaceAll("<span.*?>", "");
        question = question.replaceAll("&#39;", "");
        utils.showLog(TAG, "mainString - " + question);
        wvQ.loadData(question, "text/html; charset=utf-8", "utf-8");
        wvQ.scrollTo(0, 0);

        String answer = "<!DOCTYPE html> <html>" + topicQAList.get(queNo).getAnswer() + "</html>";
        answer = answer.replaceAll("<span.*?>", "");
        answer = answer.replaceAll("&#39;", "");
        utils.showLog(TAG, "mainString - " + answer);
        wvA.loadData(answer, "text/html; charset=utf-8", "utf-8");
        wvA.scrollTo(0, 0);

    }


    //    Recycler View Adapter
    public class QueAdapter extends RecyclerView.Adapter<QueAdapter.ViewHolder> {
        private List<TopicQAObj> queslist;


        public QueAdapter(List<TopicQAObj> queslist) {
            this.queslist = queslist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_qa, viewGroup, false);

            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            viewHolder.tvQNum.setText("Q" + (i + 1));
            viewHolder.wvQ.setScrollbarFadingEnabled(true);
            loadQuestion(i, viewHolder.wvQ, viewHolder.wvA);


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (selectedQues == null) {
                        selectedQues = viewHolder;
                        selectedQues.llAnswer.setVisibility(View.VISIBLE);
                    } else {
                        selectedQues.llAnswer.setVisibility(View.GONE);
                        selectedQues = viewHolder;
                        selectedQues.llAnswer.setVisibility(View.VISIBLE);
                    }

                    rvQa.getLayoutManager().scrollToPosition(i);
                }
            });


        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return queslist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvQNum;
            WebView wvQ, wvA;
            LinearLayout llAnswer;

            public ViewHolder(View view) {
                super(view);

                tvQNum = view.findViewById(R.id.tv_q_num);
                wvQ = view.findViewById(R.id.wv_q);
                wvA = view.findViewById(R.id.wv_a);
                llAnswer = view.findViewById(R.id.ll_answer);

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}