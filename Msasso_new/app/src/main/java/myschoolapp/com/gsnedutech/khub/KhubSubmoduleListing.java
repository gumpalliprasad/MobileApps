package myschoolapp.com.gsnedutech.khub;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.ProgressIndicator;
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
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KhubModelContentType;
import myschoolapp.com.gsnedutech.khub.models.KhubModuleNew;
import myschoolapp.com.gsnedutech.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class KhubSubmoduleListing extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubSubmoduleListing.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_sub_modules)
    RecyclerView rvSubModules;

    @BindView(R.id.tv_module_name)
    TextView tvModuleName;

    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.progressind)
    ProgressIndicator progressIndicator;

    boolean isResume = false;

    List<List<KhubModuleNew>> listModules = new ArrayList<>();

    List<KhubModuleNew> listModulesMain = new ArrayList<>();

    SharedPreferences sh_Pref;
    StudentObj sObj;
    String courseId = "";

    String studentId = "";



    KhubModuleNew ogObj;

    int pos = 0;

    List<Integer> selPos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.activity_khub_submodule_listing);

        ButterKnife.bind(this);

        init();
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
        courseId = getIntent().getStringExtra("courseId");
        ogObj = (KhubModuleNew) getIntent().getSerializableExtra("modules");

        tvModuleName.setText(ogObj.getModName());
        float progress = getModuleProgress(ogObj);
        tvProgress.setText((int)progress+"% ");
        progressIndicator.setProgress((int) progress);
        listModules.add(ogObj.getModules());
        rvSubModules.setLayoutManager(new LinearLayoutManager(this));
        List<KhubModuleNew> filteredList = new ArrayList<>();
        for (int i = 0; i <listModules.get(pos).size() ; i++) {
            if (listModules.get(pos).get(i).getIsActive()){
                filteredList.add(listModules.get(pos).get(i));
            }
        }
        rvSubModules.setAdapter(new ModuleAdapter(filteredList));

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubSubmoduleListing.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                if (isResume)
                    getModules();
            }
            isNetworkAvail = true;
        }
    }


    class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder>{

        List<KhubModuleNew> listSubModules;

        public ModuleAdapter(List<KhubModuleNew> listSubModules) {
            this.listSubModules = listSubModules;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubSubmoduleListing.this).inflate(R.layout.item_sub_module_new,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvModName.setText(listSubModules.get(position).getModName());

            if (listSubModules.get(position).getModules() != null && listSubModules.get(position).getModules().size()>0){
                holder.llBg.setBackgroundResource(R.drawable.ic_sub_with_sub);
            }
            else{
                holder.llBg.setBackgroundResource(R.drawable.ic_sub_with_no_sub);
//                holder.progressind.setVisibility(View.GONE);
//                holder.tvProgress.setVisibility(View.GONE);
            }

            float progress = 0f;
            if (listSubModules.get(position).getModules().size()>0) {
                for (KhubModuleNew khubModuleNew : listSubModules.get(position).getModules()) {
                    progress = progress + khubModuleNew.getmProgress();
                }
                progress = progress/(listSubModules.get(position).getModules().size());
            }
            else progress = listSubModules.get(position).getmProgress();
            holder.tvProgress.setText((int)progress+"% ");
            holder.progressind.setProgress((int) progress);

            if (progress==100){
                holder.ivCourseCompleted.setVisibility(View.VISIBLE);
            }else{
                holder.ivCourseCompleted.setVisibility(View.INVISIBLE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listSubModules.get(position).getModules() != null && listSubModules.get(position).getModules().size()>0){
                        tvModuleName.setText(listSubModules.get(position).getModName());
                        pos++;
                        listModules.add(listSubModules.get(position).getModules());
                        float progress = 0f;
                        if (listSubModules.get(position).getModules().size()>0) {
                            for (KhubModuleNew khubModuleNew : listSubModules.get(position).getModules()) {
                                progress = progress + khubModuleNew.getmProgress();
                            }
                            progress = progress/(listSubModules.get(position).getModules().size());
                        }
                        else progress = listSubModules.get(position).getmProgress();
                        tvProgress.setText((int)progress+"% ");
                        progressIndicator.setProgress((int) progress);
                        rvSubModules.setAdapter(new ModuleAdapter(listModules.get(pos)));
                        selPos.add(position);
                    }else{
                        isResume = true;
                        getContentTypes(listSubModules.get(position).getModName(), listSubModules.get(position).getId(), getIntent().getStringExtra("courseId"));
//                        Intent intent = new Intent(KhubSubmoduleListing.this, KhubCourseActivity.class);
//                        intent.putExtra("module_name",ogObj.getModName());
//                        intent.putExtra("submodule_name",listSubModules.get(position).getModName());
//                        intent.putExtra("mod_id",listSubModules.get(position).getId());
//                        intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
//                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listSubModules.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvModName, tvProgress;
            ProgressIndicator progressind;
            LinearLayout llBg;
            ImageView ivCourseCompleted;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivCourseCompleted = itemView.findViewById(R.id.iv_cos_complete);
                tvModName = itemView.findViewById(R.id.tv_module_name);
                progressind = itemView.findViewById(R.id.progressind);
                llBg = itemView.findViewById(R.id.ll_bg);
                tvProgress = itemView.findViewById(R.id.tv_progress);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (pos==0){
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }else{
            listModules.remove(pos);
            pos--;
            rvSubModules.setAdapter(new ModuleAdapter(listModules.get(pos)));
            if (selPos.size()>0){
                selPos.remove(selPos.size()-1);
                if (selPos.size()==0){
                    tvModuleName.setText(ogObj.getModName());
                    float progress = getModuleProgress(ogObj);
                    tvProgress.setText((int)progress+"% ");
                    progressIndicator.setProgress((int) progress);
                }else{
                    tvModuleName.setText(listModules.get(pos).get(selPos.get(selPos.size()-1)).getModName());
                    float progress = getModuleProgress(listModules.get(pos).get(selPos.get(selPos.size()-1)));
                    tvProgress.setText((int)progress+"% ");
                    progressIndicator.setProgress((int) progress);
                }
            }
        }
    }


    void getModules(){

        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        Request get = apiClient.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.MODULES+"courseId="+courseId+"&studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""), sh_Pref);

        utils.showLog(TAG,"url - "+AppUrls.KHUB_BASE_URL+AppUrls.MODULES+"courseId="+courseId+"&studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""));

        apiClient.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        rvModules.setVisibility(View.GONE);
//                        tvNoModules.setVisibility(View.VISIBLE);
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
                            listModulesMain.clear();
                            listModulesMain.addAll(gson.fromJson(jar.toString(), type));

                            if (listModulesMain.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(KhubModuleNew khubModuleNew: listModulesMain){
                                            if (khubModuleNew.getId().equalsIgnoreCase(ogObj.getId())){
                                                ogObj = khubModuleNew;
                                                break;
                                            }
                                        }
                                        listModules.clear();
                                        pos = 0;
                                        selPos = new ArrayList<>();
                                        listModules.add(ogObj.getModules());
                                        rvSubModules.setLayoutManager(new LinearLayoutManager(KhubSubmoduleListing.this));
                                        List<KhubModuleNew> filteredList = new ArrayList<>();
                                        for (int i = 0; i <listModules.get(pos).size() ; i++) {
                                            if (listModules.get(pos).get(i).getIsActive()){
                                                filteredList.add(listModules.get(pos).get(i));
                                            }
                                        }
                                        rvSubModules.setAdapter(new ModuleAdapter(filteredList));
                                        if (selPos.size()==0){
                                                tvModuleName.setText(ogObj.getModName());
                                                float progress = getModuleProgress(ogObj);
                                                tvProgress.setText((int)progress+"% ");
                                                progressIndicator.setProgress((int) progress);
                                        }
//                                        listModules.remove(pos);
//                                        pos--;
//                                        rvSubModules.setAdapter(new ModuleAdapter(listModules.get(pos)));
//                                        if (selPos.size()>0){
//                                            selPos.remove(selPos.size()-1);
//                                            if (selPos.size()==0){
//                                                tvModuleName.setText(ogObj.getModName());
//                                                float progress = getModuleProgress(ogObj);
//                                                tvProgress.setText((int)progress+"% ");
//                                                progressIndicator.setProgress((int) progress);
//                                            }else{
//                                                tvModuleName.setText(listModules.get(pos).get(selPos.get(selPos.size()-1)).getModName());
//                                                float progress = getModuleProgress(listModules.get(pos).get(selPos.get(selPos.size()-1)));
//                                                tvProgress.setText((int)progress+"% ");
//                                                progressIndicator.setProgress((int) progress);
//                                            }
//                                        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }

    void getContentTypes(String modName, String modId, String courseObjId){

        List<KhubModelContentType> topicList = new ArrayList<>();

        utils.showLoader(KhubSubmoduleListing.this);
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
                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubQuizActivity.class);
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }else if(obj.getContentType().equalsIgnoreCase("flip")){

                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubFlipActivityNew.class);
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("name", obj.getTitle() );
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);


                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }else if(obj.getContentType().equalsIgnoreCase("Blank Text")){

                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubBlankText.class);
                                                    intent.putExtra("name",obj.getTitle());
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                                }else if(obj.getContentType().equalsIgnoreCase("Text Book")){
                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubTextBookActivity.class);
                                                    intent.putExtra("name",obj.getTitle());
                                                    intent.putExtra("position","0");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                                }else if(obj.getContentType().equalsIgnoreCase("Video Player")){
                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubVideoActivity.class);
                                                    intent.putExtra("position",0+"");
                                                    intent.putExtra("moduleId",modId+"");
                                                    intent.putExtra("moduleContentId", obj.getId());
                                                    intent.putExtra("contentType", obj.getContentType());
                                                    intent.putExtra("studentId", studentId);
                                                    intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

                                                }
                                                else if(obj.getContentType().equalsIgnoreCase("Image")){
                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubImageActivity.class);
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
                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubPPTPdfListing.class);
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
                                                    Intent intent = new Intent(KhubSubmoduleListing.this, KhubPPTPdfListing.class);
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
                                            Intent intent = new Intent(KhubSubmoduleListing.this, KhubCourseActivity.class);
                                            intent.putExtra("module_name",modName);
                                            intent.putExtra("submodule_name","");
                                            intent.putExtra("mod_id",modId);
                                            intent.putExtra("courseId", courseObjId);
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                            startActivity(intent);
                                        }
                                    }
                                    else {
                                        new AlertDialog.Builder(KhubSubmoduleListing.this)
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
}