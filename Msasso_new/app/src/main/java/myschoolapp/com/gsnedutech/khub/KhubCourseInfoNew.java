package myschoolapp.com.gsnedutech.khub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.khub.models.KhubModelContentType;
import myschoolapp.com.gsnedutech.khub.models.KhubModuleNew;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KhubCategoryCourse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KhubCourseInfoNew extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubCourseInfoNew.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.htab_header)
    ImageView ivHeaderImage;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.rv_modules)
    RecyclerView rvModules;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.tv_published_by)
    TextView tvPublishedBy;
    @BindView(R.id.tv_no_modules)
    TextView tvNoModules;
    @BindView(R.id.cv_desc)
    CardView cvDesc;
    @BindView(R.id.cv_what_we_learn)
    CardView cvWhatWeLearn;
    @BindView(R.id.tv_what_we_learn)
    TextView tvWhatWeLearn;
    @BindView(R.id.ll_ratings)
    LinearLayout llRatings;

    List<KhubModuleNew> listModules = new ArrayList<>();
    KhubCategoryCourse courseObj;
    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_course_info_new);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llRatings.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(KhubCourseInfoNew.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_khub_course_rating);
            dialog.setCancelable(true);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int wwidth = metrics.widthPixels;
            dialog.getWindow().setLayout((int) (wwidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            RatingBar ratingBar = dialog.findViewById(R.id.rb_course);
            Button btnSubmit = dialog.findViewById(R.id.btn_submit);
            ImageView close = dialog.findViewById(R.id.iv_close);;

            close.setOnClickListener(view1 -> {
                dialog.dismiss();
            });
            btnSubmit.setOnClickListener(view1 -> {
                if (ratingBar.getProgress()<=0){
                    Toast.makeText(this, "Please give the rating", Toast.LENGTH_SHORT).show();
                }
                else {
                    postDetails(ratingBar.getProgress());
                    dialog.show();
                }
            });

            dialog.show();
        });
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
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    float getModuleProgress(KhubModuleNew module){
        float progress = 0f;
        if (module.getModules().size()>0) {
            for (int j = 0 ; j <module.getModules().size(); j++) {
                if (module.getModules().get(j).getModules().size()>0){
                    progress = progress + getModuleProgress(module.getModules().get(j));
                }
                else progress = progress + module.getModules().get(j).getmProgress();
            }
            progress = progress/(module.getModules().size());
        }
        else progress = module.getmProgress();
        return  progress;
    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();
        courseObj = (KhubCategoryCourse) getIntent().getSerializableExtra("courseObj");

        Picasso.with(KhubCourseInfoNew.this).load(courseObj.getKCourseImage()).placeholder(R.drawable.improve_verbal)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeaderImage);

        tvCourseName.setText(courseObj.getKCourseName());
        tvTitle.setText(courseObj.getKCourseName());
        tvPublishedBy.setText(courseObj.getOwnerName());
        if (courseObj.getKCourseDesc()!=null && !courseObj.getKCourseDesc().isEmpty()){
            cvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(courseObj.getKCourseDesc());
        }
        else {
            cvDesc.setVisibility(View.GONE);
        }
        if (courseObj.getkCourseExp()!=null && !courseObj.getkCourseExp().isEmpty()){
            cvWhatWeLearn.setVisibility(View.VISIBLE);
            tvWhatWeLearn.setText(courseObj.getkCourseExp());
        }
        else {
            cvWhatWeLearn.setVisibility(View.GONE);
        }

        rvModules.setLayoutManager(new LinearLayoutManager(this));


        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.htab_appbar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    tvTitle.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    isShow = false;
                    tvTitle.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    void getModules(){

        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        Request get = apiClient.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.MODULES+"courseId="+courseObj.getId()+"&studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""), sh_Pref);

        utils.showLog(TAG,"url - "+AppUrls.KHUB_BASE_URL+AppUrls.MODULES+"courseId="+courseObj.getId()+"&studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""));

        apiClient.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvModules.setVisibility(View.GONE);
                        tvNoModules.setVisibility(View.VISIBLE);
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
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")){

                            JSONArray jar = jsonObject.getJSONArray("result");

                            Gson gson = new Gson();
//
                            Type type = new TypeToken<List<KhubModuleNew>>() {}.getType();
                            listModules.clear();
                            listModules.addAll(gson.fromJson(jar.toString(), type));

                            if (listModules.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvModules.setAdapter(new ModuleAdapter(listModules));
                                        rvModules.setVisibility(View.VISIBLE);
                                        tvNoModules.setVisibility(View.GONE);
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvModules.setVisibility(View.GONE);
                                        tvNoModules.setVisibility(View.VISIBLE);
                                    }
                                });
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubCourseInfoNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getModules();
            }
            isNetworkAvail = true;
        }
    }

    class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder>{

        List<KhubModuleNew> listModules;

        public ModuleAdapter(List<KhubModuleNew> listModules) {
            this.listModules = listModules;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubCourseInfoNew.this).inflate(R.layout.item_module_head_new,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvModuleName.setText(listModules.get(position).getModName());
            float progress = getModuleProgress(listModules.get(position));
            holder.tv_progress.setText((int)progress+"% ");
            holder.progressind.setProgress((int) progress);
            if (progress == 100){
                holder.ivCourseCompleted.setVisibility(View.VISIBLE);
                holder.tvStartModule.setText("Completed");
            }else{
                holder.ivCourseCompleted.setVisibility(View.INVISIBLE);
                holder.tvStartModule.setText("Resume this Module");
            }
            if (progress == 0){
                holder.tvStartModule.setText("Start this Module");
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listModules.get(position).getModules() != null && listModules.get(position).getModules().size()>0){
                        Intent intent = new Intent(KhubCourseInfoNew.this, KhubSubmoduleListing.class);
                        intent.putExtra("modules",(Serializable)listModules.get(position));
                        intent.putExtra("courseId", courseObj.getId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else{
                        getContentTypes(listModules.get(position).getModName(), listModules.get(position).getId(), courseObj.getId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModules.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvModuleName;
            ImageView ivCourseCompleted;
            ProgressIndicator progressind;
            TextView tv_progress, tvStartModule;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvModuleName = itemView.findViewById(R.id.tv_module_name);
                ivCourseCompleted = itemView.findViewById(R.id.iv_cos_complete);
                progressind = itemView.findViewById(R.id.progressind);
                tv_progress = itemView.findViewById(R.id.tv_progress);
                tvStartModule = itemView.findViewById(R.id.tv_startModule);
            }
        }
    }

    void getContentTypes(String modName, String modId, String courseObjId){

        List<KhubModelContentType> topicList = new ArrayList<>();

        utils.showLoader(KhubCourseInfoNew.this);
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
                            topicList.clear();
                            topicList.addAll(gson.fromJson(jsonArray.toString(), type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (topicList.size() > 0) {
                                        if (topicList.size() ==1) {
                                            try {
                                                KhubModelContentType obj = topicList.get(0);
                                                if(obj.getContentType().equalsIgnoreCase("Quiz")){
                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubQuizActivity.class);
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", courseObjId);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }else if(obj.getContentType().equalsIgnoreCase("flip")){

                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubFlipActivityNew.class);
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("name", obj.getTitle() );
                                                    intent.putExtra("courseId", courseObjId);
                                                    startActivity(intent);


                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }else if(obj.getContentType().equalsIgnoreCase("Blank Text")){

                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubBlankText.class);
                                                    intent.putExtra("name",obj.getTitle());
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", courseObjId);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                                }else if(obj.getContentType().equalsIgnoreCase("Text Book")){
                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubTextBookActivity.class);
                                                    intent.putExtra("name",obj.getTitle());
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", courseObjId);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                                }else if(obj.getContentType().equalsIgnoreCase("Video Player")){
                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubVideoActivity.class);
                                                    intent.putExtra("position",0+"");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", courseObjId);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                                }else if(obj.getContentType().equalsIgnoreCase("Image")){
                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubImageActivity.class);
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("name", obj.getTitle() );
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }else if (obj.getContentType().equalsIgnoreCase("PDF")){
                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubPPTPdfListing.class);
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("name", obj.getTitle() );
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                } else if (obj.getContentType().equalsIgnoreCase("PPT")){
                                                    Intent intent = new Intent(KhubCourseInfoNew.this, KhubPPTPdfListing.class);
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
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else {
                                            Intent intent = new Intent(KhubCourseInfoNew.this, KhubCourseActivity.class);
                                            intent.putExtra("module_name",modName);
                                            intent.putExtra("submodule_name","");
                                            intent.putExtra("mod_id",modId);
                                            intent.putExtra("courseId", courseObjId);
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        new AlertDialog.Builder(KhubCourseInfoNew.this)
                                                .setTitle(modName)
                                                .setMessage("No Content To Load")
                                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                                    dialog.dismiss();
                                                })
                                                .setCancelable(false)
                                                .show();
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

    private void postDetails(int rating) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("categoryId", courseObj.getCategoryId());
            jsonObject.put("courseId", courseObj.getId());
            jsonObject.put("rating", rating);

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.KhubCourseRating,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.KhubCourseRating);
        utils.showLog(TAG, "body -"+ jsonObject.toString());

        apiClient.getClient().newCall(postRequest).enqueue(new Callback() {
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
                        if (response.code() == 200) {
                            runOnUiThread(() -> {
                                onBackPressed();
//                                Toast.makeText(KhubCategoryDetail.this, "Course Enrolled Successfully", Toast.LENGTH_SHORT).show();
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