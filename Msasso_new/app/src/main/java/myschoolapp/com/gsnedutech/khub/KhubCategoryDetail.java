package myschoolapp.com.gsnedutech.khub;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.io.Serializable;
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

public class KhubCategoryDetail extends AppCompatActivity implements SearchView.OnQueryTextListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubCategoryDetail.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.rv_courses)
    RecyclerView rvCourses;

    @BindView(R.id.sv_search_cat)
    SearchView svSearchCat;

    String id;

    MyUtils utils = new MyUtils();

    List<KhubCategoryCourse> listDetails = new ArrayList<>();

    CourseAdapter adapterClass;
    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.activity_khub_category_detail);

        ButterKnife.bind(this);

        init();

        svSearchCat.setOnQueryTextListener(this);
        svSearchCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSearchCat.setIconified(false);
            }
        });
    }

    void init(){
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        id = getIntent().getStringExtra("id");

        tvTitle.setText(getIntent().getStringExtra("title"));


        rvCourses.setLayoutManager(new GridLayoutManager(this,2));



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
                                        adapterClass = new CourseAdapter(listDetails);
                                        rvCourses.setAdapter(adapterClass);
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (adapterClass!=null)
        adapterClass.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (adapterClass!=null)
        adapterClass.getFilter().filter(s);
        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, KhubCategoryDetail.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getDetails();
            }
            isNetworkAvail = true;
        }
    }


    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> implements Filterable {

        List<KhubCategoryCourse> listDetail, list_filtered;

        public CourseAdapter(List<KhubCategoryCourse> listDetail) {
            this.listDetail = listDetail;
            this.list_filtered = listDetail;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubCategoryDetail.this).inflate(R.layout.item_khub_cat_courses,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvTitle.setText(list_filtered.get(position).getKCourseName());
            Picasso.with(KhubCategoryDetail.this).load(list_filtered.get(position).getKCourseImage()).placeholder(R.drawable.improve_verbal)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivCourses);
            if (list_filtered.get(position).getViewsCount()>0){
                holder.tvViewsCount.setText(list_filtered.get(position).getViewsCount()+ " Views");
            }
            else {
                holder.tvViewsCount.setText("0 Views");
            }

            if (list_filtered.get(position).getIsEnrolled()){
                holder.tvEnrollCourse.setText("Enrolled");
//                holder.tvEnrollCourse.setEnabled(false);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list_filtered.get(position).getIsEnrolled()){
                        postCourseView(list_filtered.get(position));
                        Intent intent = new Intent(KhubCategoryDetail.this, KhubCourseInfoNew.class);
                        KhubCategoryCourse khubCategoryCourse = list_filtered.get(position);
                        khubCategoryCourse.setCategoryId(id);
                        intent.putExtra("courseObj",(Serializable)khubCategoryCourse);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                    else enrollCourse(list_filtered.get(position).getId(), list_filtered, position);
                }
            });
//            holder.tvEnrollCourse.setOnClickListener(view -> {
//
//            });
//            if (list_filtered.get(position).getIsEnrolled()){
//
//            }
//            else {
//                holder.itemView.setOnClickListener(null);
//            }

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    postCourseView(list_filtered.get(position));
//                    Intent intent = new Intent(KhubCategoryDetail.this, KhubCourseInfoNew.class);
//                    intent.putExtra("courseObj",(Serializable)list_filtered.get(position));
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                }
//            });


        }

        public void update(List<KhubCategoryCourse> listUpdated) {
            list_filtered = listUpdated;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    if (string.isEmpty()) {
                        list_filtered = listDetail;
                    } else {

                        List<KhubCategoryCourse> filteredList = new ArrayList<>();
                        for (KhubCategoryCourse s : listDetail) {

                            if (s.getKCourseName().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }

                        list_filtered = filteredList;

                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<KhubCategoryCourse>) results.values;

                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle, tvEnrollCourse, tvViewsCount;
            ImageView ivCourses;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tv_title);
                ivCourses = itemView.findViewById(R.id.iv_course_image);
                tvEnrollCourse = itemView.findViewById(R.id.tv_enroll_course);
                tvViewsCount = itemView.findViewById(R.id.tv_views_count);
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
                                list_filtered.get(position).setIsEnrolled(true);
                                adapterClass.update(list_filtered);
                                Toast.makeText(KhubCategoryDetail.this, "Course Enrolled Successfully", Toast.LENGTH_SHORT).show();
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
