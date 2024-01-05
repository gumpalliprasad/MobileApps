package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.OptionArray;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import it.gilvegliach.android.transparenttexttextview.TransparentTextTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FeedbackFormActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + FeedbackFormActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_feed_back)
    RecyclerView rvFeedBack;
    @BindView(R.id.btn_next)
    TransparentTextTextView btnNext;

    String surveyFormId;

    int currentQuestion=0;
    List<SurveyFormQuestions> listQues = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = new ColorDrawable(Color.rgb(66,203,162));
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_feedback_form);
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
            utils.alertDialog(1, FeedbackFormActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getFeedBack();
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

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        surveyFormId = getIntent().getStringExtra("surveyFormId");




        rvFeedBack = findViewById(R.id.rv_feed_back);

        rvFeedBack.setLayoutManager(new LinearLayoutManager(this));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuestion++;

                if (currentQuestion!=listQues.size()){
                    rvFeedBack.setAdapter(new QuestionAdapter(listQues));
                    btnNext.setVisibility(View.GONE);
                }else {
                    if (NetworkConnectivity.isConnected(FeedbackFormActivity.this)) {
                        postSurvey();
                    } else {
                        new MyUtils().alertDialog(1, FeedbackFormActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                                getString(R.string.action_settings), getString(R.string.action_close),false);
                    }

                }
            }
        });



    }

    void getFeedBack(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        utils.showLog(TAG,"url "+ AppUrls.GetSurveyQuestionByForm + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&surveyFormId=" + surveyFormId);

        Request get = new Request.Builder()
                .url("http://13.232.73.168:9000/getSurveyQuestionByForm?schemaName=nar666" + "&surveyFormId=" + surveyFormId)
                .build();
//        Request get = new Request.Builder()
//                .url(AppUrls.GetSurveyQuestionByForm + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", "") + "&surveyFormId=" + surveyFormId)
//                .build();

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvFeedBack.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {


                }else {
                    String resp = responseBody.string();

                    utils.showLog(TAG, "response- " + resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArray = ParentjObject.getJSONArray("TopicVideos");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SurveyFormQuestions>>() {
                            }.getType();

                            listQues.clear();
                            listQues.addAll(gson.fromJson(jsonArray.toString(), type));


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvFeedBack.setAdapter(new QuestionAdapter(listQues));
                                }
                            });

                        }
                    }catch (Exception e){

                    }
                }
            }
        });
    }

    void postSurvey(){



      utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < listQues.size(); i++) {
            JSONObject job = new JSONObject();
            try {
                job.put("surveyQuestionId", listQues.get(i).getSurveyQuestionId());
                if (listQues.get(i).getSurveyQuesType().equalsIgnoreCase("Option")) {
                    job.put("surveyOptionId", listQues.get(i).getSelectedOption());
                }else if (listQues.get(i).getSurveyQuesType().equalsIgnoreCase("rating")){
                    job.put("surveyRating", listQues.get(i).getSelectedOption());
                }
                jsonArray.put(job);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));
            jsonObject.put("schemaName", "nar666");
            jsonObject.put("surveyFormId", surveyFormId);
            jsonObject.put("studentId", getIntent().getStringExtra("studentId"));
            jsonObject.put("insertRecords", jsonArray);
        } catch (Exception e) {

        }


        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request post = new Request.Builder()
                .url("http://13.232.73.168:9000/saveStudentSurveyFormFeedback")
                .post(body)
                .build();
//     Request post = new Request.Builder()
//                    .url(new AppUrls().SaveStudentSurveyFormFeedback)
//                    .post(body)
//                    .build();

        client.newCall(post).enqueue(new Callback() {
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
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });


                }else {
                    String resp = responseBody.string();
                    utils.showLog(TAG,"response "+resp);
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Dialog dialog = new Dialog(FeedbackFormActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    dialog.setContentView(R.layout.layout_dialog_feedback_finish);
                                    TextView dialogButton =  dialog.findViewById(R.id.btn_done);
                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            onBackPressed();
                                        }
                                    });


                                    dialog.show();

                                }
                            });
                           }
                    }catch (Exception e){

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

    class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{

        List<SurveyFormQuestions> listQues;

        public QuestionAdapter(List<SurveyFormQuestions> listQues) {
            this.listQues = listQues;
        }

        @NonNull
        @Override
        public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeedbackFormActivity.this).inflate(R.layout.item_feed_back_questions,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
            holder.tvQ.setText(listQues.get(position).getSurveyQuestion());
            holder.tvQNum.setText((position+1)+"");
            if (position==currentQuestion){
                holder.rvOptions.setVisibility(View.VISIBLE);
                holder.rvOptions.setLayoutManager(new LinearLayoutManager(FeedbackFormActivity.this));
                holder.rvOptions.setAdapter(new OptionsAdapter(listQues.get(position).getOptionArray(),position));
            }else {
                holder.rvOptions.setVisibility(View.GONE);
                holder.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public int getItemCount() {
            return (currentQuestion+1);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQNum,tvQ;
            RecyclerView rvOptions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQNum = itemView.findViewById(R.id.tv_q_num);
                tvQ = itemView.findViewById(R.id.tv_q);
                rvOptions = itemView.findViewById(R.id.rv_options);
            }
        }
    }


    class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

        List<OptionArray> listOptions;
        int selectedOption=-1;
        int pos;

        public OptionsAdapter(List<OptionArray> listOptions, int position) {
            this.listOptions = listOptions;
            pos = position;
        }

        @NonNull
        @Override
        public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FeedbackFormActivity.this).inflate(R.layout.item_options,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
            holder.rbOption.setText(listOptions.get(position).getSurveyOption());
            if (position==selectedOption){
                holder.rbOption.setChecked(true);
            }else {
                holder.rbOption.setChecked(false);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listQues.get(pos).setSelectedOption(listOptions.get(position).getSurveyOptionId() + "");
                    selectedOption = position;
                    notifyDataSetChanged();
                    btnNext.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listOptions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton rbOption;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
;                rbOption = itemView.findViewById(R.id.rb_option);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}