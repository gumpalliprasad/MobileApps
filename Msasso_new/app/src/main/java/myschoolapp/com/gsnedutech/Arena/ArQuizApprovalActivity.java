package myschoolapp.com.gsnedutech.Arena;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

import myschoolapp.com.gsnedutech.Arena.Fragments.ArQuizEditingFrag;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Fragments.ArAddQuizPreview;
import myschoolapp.com.gsnedutech.Arena.Fragments.ArQuizAllTypeQuestion;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArQuizApprovalActivity extends AppCompatActivity {

    private static final String TAG = "SriRam -" + ArQuizApprovalActivity.class.getName();

    public ArenaRecord quizObj;

    MyUtils utils = new MyUtils();

    public int stepNo = 0;

    public List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();
    SharedPreferences sh_Pref;

    TeacherObj tObj;

    public int qNum = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_approval);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        }

        quizObj = (ArenaRecord) getIntent().getSerializableExtra("quizObj");
        if(quizObj.getArenaName().contains("~~")){
            ((TextView)findViewById(R.id.tv_title)).setText(quizObj.getArenaName().split("~~")[0]);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(quizObj.getArenaName());
        }

        getQuizDetails();

    }

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + quizObj.getArenaId()+"" + "&userId="+tObj.getUserId()+"&arenaCategory=1";
        }else {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + quizObj.getArenaId()+""+"&arenaCategory=1";
        }

        Request get = new Request.Builder()
                .url(url)
                .build();

        utils.showLog(TAG, "url "+url);

        client.newCall(get).enqueue(new Callback() {
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

                Log.v(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaQuizQuestionFiles>>() {
                            }.getType();

                            listQuestions.clear();
                            listQuestions.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listQuestions.size()>0){
                                        loadFragment();
                                    }
                                }
                            });



                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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


    public void loadFragment(){
        Fragment fragment= null;
        switch (stepNo){
            case 0:
                fragment = new ArAddQuizPreview();
                break;
            case 1:
                fragment = new ArQuizAllTypeQuestion();
                break;
            case 2:
                fragment = new ArQuizEditingFrag();
                break;

        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (stepNo==0) {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }else if (stepNo==2){
            qNum = -1;
            stepNo=0;
            loadFragment();
        }
        else {
            qNum = -1;
            stepNo--;
            loadFragment();
        }
    }
}