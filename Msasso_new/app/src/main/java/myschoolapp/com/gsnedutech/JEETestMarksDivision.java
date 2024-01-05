package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
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
import butterknife.OnClick;
import myschoolapp.com.gsnedutech.Models.JEETestSectionDetail;
import myschoolapp.com.gsnedutech.Models.TemplateInstructions;
import myschoolapp.com.gsnedutech.Models.TemplateSection;
import myschoolapp.com.gsnedutech.OnlIneTest.Config;
import myschoolapp.com.gsnedutech.OnlIneTest.LiveExams;
import myschoolapp.com.gsnedutech.OnlIneTest.OTStudentOnlineTestActivity;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JEETestMarksDivision extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = JEETestMarksDivision.class.getName();
    List<TemplateInstructions> listInstructions = new ArrayList<>();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_section)
    RecyclerView rvSection;

    List<JEETestSectionDetail> listSectionDetails = new ArrayList<>();
    MyUtils utils = new MyUtils();
    LiveExams liveExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeetest_marks_division);
        ButterKnife.bind(this);

        init();

        SharedPreferences sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String confJson = sh_Pref.getString("config", "");
        Config config = gson.fromJson(confJson, Config.class);

        AmazonS3Client s3Client1 = new AmazonS3Client(new BasicAWSCredentials(config.getS3Details().getaKey(), config.getS3Details().getsKey()),
                Region.getRegion(Regions.AP_SOUTH_1));

        String url =  "https://edvantageexams.s3.ap-south-1.amazonaws.com/templates/"+getIntent().getStringExtra("jeeSectionTemplate")+".json";

        getInstructions(url);

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
            utils.alertDialog(1, JEETestMarksDivision.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    // Initialization
    void init() {
        liveExam = (LiveExams) getIntent().getSerializableExtra("live");
        utils.showLoader(this);
    }

    // Start Test
    @OnClick({R.id.tv_start})
    public void onViewClicked() {
        Intent onlineTestIntent = new Intent(JEETestMarksDivision.this, OTStudentOnlineTestActivity.class);
        onlineTestIntent.putExtra("studentId", getIntent().getIntExtra("studentId",0));
        onlineTestIntent.putExtra("live", (Serializable) liveExam);
        onlineTestIntent.putExtra("sections", (Serializable) listSectionDetails);

        onlineTestIntent.putExtra("studentId", getIntent().getIntExtra("studentId",0));
        onlineTestIntent.putExtra("testId", getIntent().getIntExtra("testId",0));
        onlineTestIntent.putExtra("testName", getIntent().getStringExtra("testName"));
        onlineTestIntent.putExtra("examSTime", getIntent().getStringExtra("examSTime"));
        onlineTestIntent.putExtra("examETime", getIntent().getStringExtra("examETime"));
        onlineTestIntent.putExtra("examRTime", getIntent().getStringExtra("examRTime"));
        onlineTestIntent.putExtra("testTime", getIntent().getStringExtra("testTime"));
        onlineTestIntent.putExtra("eDuration", getIntent().getIntExtra("eDuration",0));
        onlineTestIntent.putExtra("correctMarks", getIntent().getIntExtra("correctMarks",0));
        onlineTestIntent.putExtra("wrongMarks", getIntent().getIntExtra("wrongMarks",0));
        onlineTestIntent.putExtra("testCategory", getIntent().getIntExtra("testCategory",0));
        onlineTestIntent.putExtra("examdet_Id",getIntent().getStringExtra("examdet_Id"));
        onlineTestIntent.putExtra("startTime", getIntent().getStringExtra("startTime"));

        onlineTestIntent.putExtra("jeeSectionTemplate", getIntent().getStringExtra("jeeSectionTemplate"));
        onlineTestIntent.putExtra("testFilePath", getIntent().getStringExtra("testFilePath"));
        onlineTestIntent.putExtra("studentTestFilePath", getIntent().getStringExtra("studentTestFilePath"));


        startActivity(onlineTestIntent);
        finish();
    }

    // Get Test Sections
    void getSections(String catId,String templateName){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(AppUrls.GetStudentOnlineTestSectons +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&catId=" + catId + "&templateName=" + templateName)
                .build();

        utils.showLog(TAG,"url - "+AppUrls.GetStudentOnlineTestSectons +"schemaName=" +getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema","")+ "&catId=" + catId + "&templateName=" + templateName);

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

                ResponseBody resp = response.body();
                String responce = resp.string();

                utils.showLog(TAG,"response - "+responce);


                if (responce != null) {
                    try {
                        JSONObject ParentjObject = new JSONObject(responce);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("TestSections");
                            for (int i=0;i<jsonArr.length();i++){
                                JSONObject jOb = jsonArr.optJSONObject(i);
                                JSONArray jArray = jOb.getJSONArray("testSectionDetails");

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<JEETestSectionDetail>>() {
                                }.getType();
                                List<JEETestSectionDetail> list = new ArrayList<>();
                                list.clear();
                                list.addAll(gson.fromJson(jArray.toString(), type));
                                listSectionDetails.addAll(list);
                            }

                            for (int i=0;i<listSectionDetails.size();i++){

                                List<String> listSubjects = new ArrayList<>();
                                List<TemplateSection> listTempSec = new ArrayList<>();

                                for (int j=0;j<listInstructions.size();j++){
                                    for (int k=0;k<listInstructions.get(j).getSections().size();k++){
                                        if (listInstructions.get(j).getSections().get(k).getSectionName().equalsIgnoreCase(listSectionDetails.get(i).getSectionName())){

                                            listSubjects.add(listInstructions.get(j).getSubject());
                                            listTempSec.add(listInstructions.get(j).getSections().get(k));

                                        }
                                    }
                                }
                                listSectionDetails.get(i).setSubject(listSubjects);
                                listSectionDetails.get(i).setSection(listTempSec);

                            }

                            utils.showLog(TAG,"Jee sections "+ listSectionDetails.size());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rvSection.setLayoutManager(new LinearLayoutManager(JEETestMarksDivision.this));
                                    rvSection.setAdapter(new JeeSectionsAdapter());
                                }
                            });


                        }
                    }catch (Exception e) {
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


    class JeeSectionsAdapter extends RecyclerView.Adapter<JeeSectionsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(JEETestMarksDivision.this).inflate(R.layout.item_jee_section_details, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.tvSectionName.setText(listSectionDetails.get(i).getSectionName());
            viewHolder.tvQuestType.setText(listSectionDetails.get(i).getQuestType());
            viewHolder.tvPosMarks.setText(listSectionDetails.get(i).getCorrectAnswerMarks());
            viewHolder.tvNegMarks.setText(listSectionDetails.get(i).getWrongAnswerMarks());
            viewHolder.tvNumOfQuestions.setText(listSectionDetails.get(i).getNumQuestions());
            viewHolder.tvTotal.setText((Integer.parseInt(listSectionDetails.get(i).getNumQuestions()) * (Integer.parseInt(listSectionDetails.get(i).getCorrectAnswerMarks()))) + "");
            viewHolder.rvInstructions.setLayoutManager(new LinearLayoutManager(JEETestMarksDivision.this));
            viewHolder.rvInstructions.setAdapter(new InstructionAdapter(listSectionDetails.get(i).getSubject(),listSectionDetails.get(i).getTemplateSection()));
        }

        @Override
        public int getItemCount() {
            return listSectionDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSectionName, tvQuestType, tvPosMarks, tvNegMarks, tvNumOfQuestions, tvTotal;
            RecyclerView rvInstructions;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSectionName = itemView.findViewById(R.id.tv_jee_sec_name);
                tvQuestType = itemView.findViewById(R.id.tv_jee_q_type);
                tvPosMarks = itemView.findViewById(R.id.tv_jee_positive);
                tvNegMarks = itemView.findViewById(R.id.tv_jee_negative);
                tvNumOfQuestions = itemView.findViewById(R.id.tv_jee_questions);
                tvTotal = itemView.findViewById(R.id.tv_jee_total);
                rvInstructions = itemView.findViewById(R.id.rv_instructions);
            }
        }
    }
    class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.ViewHolder>{

        List<String> subject; List<TemplateSection> templateSection;

        public InstructionAdapter(List<String> subject, List<TemplateSection> templateSection) {
            this.subject = subject;
            this.templateSection = templateSection;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(JEETestMarksDivision.this).inflate(R.layout.item_instructions,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvSubName.setText(subject.get(position));
            holder.tvInstructions.setText(templateSection.get(position).getSectionsDesc());
        }

        @Override
        public int getItemCount() {
            return subject.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubName,tvInstructions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                tvInstructions = itemView.findViewById(R.id.tv_instructions);
            }
        }
    }

    // Get Test Instructions
    private void getInstructions(String url) {

        utils.showLog(TAG,"url "+url);



        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        getSections(""+liveExam.geteCatId(),getIntent().getStringExtra("jeeSectionTemplate"));
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
                        JSONObject obj = new JSONObject(resp);

                        JSONArray jsonArray = obj.getJSONArray("instructions");
                        Gson gson = new Gson();
                        listInstructions.clear();
                        Type type = new TypeToken<List<TemplateInstructions>>() {
                        }.getType();

                        listInstructions.addAll(gson.fromJson(jsonArray.toString(),type));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
                            }
                        });
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSections(""+liveExam.geteCatId(),getIntent().getStringExtra("jeeSectionTemplate"));
                    }
                });
            }
        });



    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
