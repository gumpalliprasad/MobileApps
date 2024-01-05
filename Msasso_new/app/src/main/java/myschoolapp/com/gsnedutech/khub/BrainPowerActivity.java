package myschoolapp.com.gsnedutech.khub;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

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
import myschoolapp.com.gsnedutech.khub.models.KhubCategoryCourse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BrainPowerActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = BrainPowerActivity.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_apt_cos)
    RecyclerView rvOptCourses;

    @BindView(R.id.rv_gk_courses)
    RecyclerView rvGkCourses;

    @BindView(R.id.tv_no_courses)
    TextView tvNoCourses;

    String id;

    List<KhubCategoryCourse> listDetails = new ArrayList<>();

    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brain_power);
        ButterKnife.bind(this);
        init();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

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


    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        id = getIntent().getStringExtra("id");
    }

    void getDetails(){

        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        Request get = apiClient.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.COURSES+"categoryId="+id+"&studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""), sh_Pref);

        utils.showLog(TAG,"url - "+AppUrls.KHUB_BASE_URL+AppUrls.COURSES+"categoryId="+id+"&studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""));

        apiClient.getClient().newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        findViewById(R.id.tv_no_courses).setVisibility(View.VISIBLE);
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
                            findViewById(R.id.tv_no_courses).setVisibility(View.VISIBLE);
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")){

                            JSONArray jar = jsonObject.getJSONArray("result");

                            Gson gson = new Gson();
//
                            Type type = new TypeToken<List<KhubCategoryCourse>>() {}.getType();
                            listDetails.clear();
                            listDetails.addAll(gson.fromJson(jar.toString(), type));

                            if (listDetails.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setRecyclerViews();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        findViewById(R.id.tv_no_courses).setVisibility(View.VISIBLE);
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

    private void setRecyclerViews() {
        List<KhubCategoryCourse> aptCourses = new ArrayList<>();
        List<KhubCategoryCourse> gkCourses = new ArrayList<>();
        for (int i = 0; i<listDetails.size();i++){
            if (listDetails.get(i).getCourseGroup().equalsIgnoreCase("aptitude")){
                aptCourses.add(listDetails.get(i));
            }
            else if (listDetails.get(i).getCourseGroup().equalsIgnoreCase("General Knowledge")){
                gkCourses.add(listDetails.get(i));
            }
        }

        if (aptCourses.size()>0){
            rvOptCourses.setLayoutManager(new LinearLayoutManager(BrainPowerActivity.this));
            rvOptCourses.setAdapter(new CourseAdapter(aptCourses));
        }
        if (gkCourses.size()>0){
            rvGkCourses.setLayoutManager(new LinearLayoutManager(BrainPowerActivity.this));
            rvGkCourses.setAdapter(new CourseAdapter(gkCourses));
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, BrainPowerActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getDetails();
            }
            isNetworkAvail = true;
        }
    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

        List<KhubCategoryCourse> listDetail;

        public CourseAdapter(List<KhubCategoryCourse> listDetail) {
            this.listDetail = listDetail;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(BrainPowerActivity.this).inflate(R.layout.item_khub_bp_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvTitle.setText(listDetail.get(position).getKCourseName());
            Picasso.with(BrainPowerActivity.this).load(listDetail.get(position).getKCourseImage()).placeholder(R.drawable.improve_verbal)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivCourses);
//            if (listDetail.get(position).getViewsCount()>0){
//                holder.tvViewsCount.setText(listDetail.get(position).getViewsCount()+ " Views");
//            }
//            else {
//                holder.tvViewsCount.setText("0 Views");
//            }
            if (!listDetail.get(position).getIsEnrolled()){
                enrollCourse(listDetail.get(position).getId(), listDetail, position);
            }
//            if (listDetail.get(position).getIsEnrolled()){
//                holder.tvEnrollCourse.setText("Enrolled");
////                holder.tvEnrollCourse.setEnabled(false);
//            }
//            holder.tvEnrollCourse.setOnClickListener(view -> {
//                if (listDetail.get(position).getIsEnrolled()){
//                    postCourseView(listDetail.get(position));
//                    Intent intent = new Intent(BrainPowerActivity.this, KhubCourseInfoNew.class);
//                    intent.putExtra("courseObj",(Serializable)listDetail.get(position));
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                }
//                else enrollCourse(listDetail.get(position).getId(), listDetail, position);
//            });
//            if (listDetail.get(position).getIsEnrolled()){
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        postCourseView(listDetail.get(position));
//                        Intent intent = new Intent(BrainPowerActivity.this, KhubCourseInfoNew.class);
//                        intent.putExtra("courseObj",(Serializable)listDetail.get(position));
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                    }
//                });
//            }
//            else {
//                holder.itemView.setOnClickListener(null);
//            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postCourseView(listDetail.get(position));
                    Intent intent = new Intent(BrainPowerActivity.this, KhubCourseInfoNew.class);
                    KhubCategoryCourse khubCategoryCourse = listDetail.get(position);
                    khubCategoryCourse.setCategoryId(id);
                    intent.putExtra("courseObj",khubCategoryCourse);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });


        }

        @Override
        public int getItemCount() {
            return listDetail.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvEnrollCourse, tvViewsCount;
            ImageView ivCourses;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tv_course_name);
                ivCourses = itemView.findViewById(R.id.iv_course_image);
            }
        }
    }

    private void postCourseView(KhubCategoryCourse khubCategoryCourse) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", khubCategoryCourse.getId());
            jsonObject.put("categoryId",id);

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.CourseView,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.CourseView);
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
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
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

    private void enrollCourse(String courseId, List<KhubCategoryCourse> list_filtered, int position) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("courseId", courseId);
            jsonObject.put("categoryId",id);

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.KHUB_BASE_URL+AppUrls.EnrollCourse,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.KHUB_BASE_URL+AppUrls.EnrollCourse);
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
                        if (ParentjObject.getString("status").equalsIgnoreCase("200")) {
                            runOnUiThread(() -> {
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
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }
}