package myschoolapp.com.gsnedutech.QBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.QBox.model.SearchQbox;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SuggestedQuestions extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "SriRam -" + SuggestedQuestions.class.getName();

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_suggestions)
    RecyclerView rvSuggestions;

    @BindView(R.id.sv_qbox_questions)
    SearchView svQboxQuestions;
    @BindView(R.id.tv_no_suggestions)
    TextView tvNoSuggestions;

    SharedPreferences sh_Pref;
    StudentObj sObj;

    int offset=0;
    boolean hasNextPage = false;
    int itemCount = 15;
    String suggestWord = "";

    List<SearchQbox> listSuggestions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_questions);
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
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        if (getIntent().hasExtra("keyword")) {
            suggestWord = getIntent().getStringExtra("keyword");
        }
        rvSuggestions.setLayoutManager(new LinearLayoutManager(SuggestedQuestions.this));
        rvSuggestions.setAdapter(new SuggestAdapter());
        svQboxQuestions.setOnQueryTextListener(this);
        svQboxQuestions.setQuery(suggestWord,true);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    void getSuggestions(String searchKeyWord){
//        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("searchString", searchKeyWord);
            postObject.put("qboxStatus", 2);
            postObject.put("itemCount", itemCount);
            postObject.put("offset", offset+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().AutoCompleteQboxSuggestions);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().AutoCompleteQboxSuggestions)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        utils.dismissDialog();
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
//                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jar = jsonObject.getJSONArray("qboxQuestions");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SearchQbox>>() {
                            }.getType();
                            if (jar.length()>0) {
                                if (offset==0){
                                    listSuggestions.clear();
                                }
                                listSuggestions.addAll(gson.fromJson(jar.toString(), type));

                                if (listSuggestions.size()%itemCount==0){
                                    hasNextPage = true;
                                }else {
                                    hasNextPage = false;
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listSuggestions.size()>0){
                                            rvSuggestions.setVisibility(View.VISIBLE);
                                            tvNoSuggestions.setVisibility(View.GONE);
                                            rvSuggestions.getAdapter().notifyDataSetChanged();
                                        }else{
//                                            llStories.setVisibility(View.GONE);
                                            rvSuggestions.setVisibility(View.GONE);
                                            tvNoSuggestions.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (listSuggestions.size()==0) {
//                                            llStories.setVisibility(View.GONE);
                                            rvSuggestions.setVisibility(View.GONE);
                                            tvNoSuggestions.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            rvSuggestions.setVisibility(View.VISIBLE);
                                            tvNoSuggestions.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                utils.dismissDialog();
                            }
                        });
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        utils.dismissDialog();
                    }
                });

            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (NetworkConnectivity.isConnected(SuggestedQuestions.this)) {
            if (query.length()>3)
                getSuggestions(query);
            else {
                rvSuggestions.setVisibility(View.GONE);
                tvNoSuggestions.setVisibility(View.VISIBLE);
            }
        } else {
            new MyUtils().alertDialog(1, SuggestedQuestions.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (NetworkConnectivity.isConnected(SuggestedQuestions.this)) {
            if (newText.length()>3)
                getSuggestions(newText);
            else {
                rvSuggestions.setVisibility(View.GONE);
                tvNoSuggestions.setVisibility(View.VISIBLE);
            }
        } else {
            new MyUtils().alertDialog(1, SuggestedQuestions.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }
        return false;
    }

    class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {


        @NonNull
        @Override
        public SuggestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SuggestAdapter.ViewHolder(LayoutInflater.from(SuggestedQuestions.this).inflate(R.layout.item_qbox_suggestion, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvSuggestQuestion.setText(listSuggestions.get(position).getQboxQuestion());

            holder.itemView.setOnClickListener(view -> {
                getQuestionDetailsById(listSuggestions.get(position).getStuQboxId());
            });

            if (position == (listSuggestions.size()-1)){
//                utils.showLoader(SuggestedQuestions.this);
                if (hasNextPage){
                    offset = offset+itemCount;
                    if (suggestWord.length()>3)
                        getSuggestions(suggestWord);
                }else {
                    utils.dismissDialog();
                }
            }


        }

        @Override
        public int getItemCount() {
            return listSuggestions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSuggestQuestion;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSuggestQuestion = itemView.findViewById(R.id.tv_sug_question);

            }
        }
    }

    void getQuestionDetailsById(String stuQboxId){
        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("myUserId", sObj.getStudentId());
            postObject.put("stuqboxId", stuQboxId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().AutoCompleteQboxSuggestions);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetQboxDetailQuestionById)
                .post(body)
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
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jar = jsonObject.getJSONArray("qboxReplies");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<QboxQuestion>>() {
                            }.getType();
                            if (jar.length()>0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<QboxQuestion> list = gson.fromJson(jar.toString(), type);
                                        Intent queIntent = new Intent(SuggestedQuestions.this, QboxQuestionDetails.class);
                                        queIntent.putExtra("queObj", list.get(0));
                                        startActivity(queIntent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        }
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
                        utils.dismissDialog();
                    }
                });

            }
        });

    }



}