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
import myschoolapp.com.gsnedutech.Util.CircularProgressBar;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.khub.models.KhubCategoryCourse;
import myschoolapp.com.gsnedutech.khub.models.KhubMyCourses;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class KhubMyCoursesActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = KhubMyCoursesActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    StudentObj sObj;

    String studentId = "";
    @BindView(R.id.rv_myCourses)
    RecyclerView rvMyCourses;

    List<KhubMyCourses> listMyCourses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khub_my_courses);
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

        studentId = sObj.getStudentId();
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

    void getMyCourses() {

        utils.showLoader(KhubMyCoursesActivity.this);
        ApiClient client = new ApiClient();
        Request get = client.getRequest(AppUrls.KHUB_BASE_URL+AppUrls.MyCourses+"studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""), sh_Pref);

        utils.showLog(TAG, "url -" + AppUrls.KHUB_BASE_URL+AppUrls.MyCourses+"studentId="+studentId+"&schemaName="+sh_Pref.getString("schema",""));
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
                utils.showLog(TAG, "response " + resp);

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jar = jsonObject.getJSONArray("result");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<KhubMyCourses>>() {
                            }.getType();
                            listMyCourses.clear();
                            listMyCourses.addAll(gson.fromJson(jar.toString(), type));

                            if (listMyCourses.size() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvMyCourses.setVisibility(View.VISIBLE);
                                        rvMyCourses.setLayoutManager(new LinearLayoutManager(KhubMyCoursesActivity.this));
                                        rvMyCourses.setAdapter(new CourseAdapter(listMyCourses));
                                    }
                                });
                            }
                            else {
                                runOnUiThread(() -> {
                                    rvMyCourses.setVisibility(View.GONE);
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
            utils.alertDialog(1, KhubMyCoursesActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getMyCourses();
            }
            isNetworkAvail = true;
        }
    }

    class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

        List<KhubMyCourses> listCat;

        public CourseAdapter(List<KhubMyCourses> listCat) {
            this.listCat = listCat;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(KhubMyCoursesActivity.this).inflate(R.layout.item_khub_mycourse_main, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            holder.tvCourseName.setText(listCat.get(position).getKCourseName());
            holder.tvAuthor.setText(listCat.get(position).getOwnerName());

            if (listCat.get(position).getKCourseImage() != null && !listCat.get(position).getKCourseImage().equalsIgnoreCase("")) {
                Picasso.with(KhubMyCoursesActivity.this).load(listCat.get(position).getKCourseImage()).placeholder(R.drawable.ic_khub_extreme)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivCourseImage);
            }
            holder.courseProgress.setInnerBackgroundColor(Color.parseColor("#ffffff"));
            holder.courseProgress.setTextSize(0);
            holder.courseProgress.setFinishedStrokeColor(Color.parseColor("#00D1FF"));
            holder.courseProgress.setProgress(listCat.get(position).getCProgress());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(KhubMyCoursesActivity.this, KhubCourseInfoNew.class);
                    KhubCategoryCourse khubCategoryCourse = new KhubCategoryCourse();
                    khubCategoryCourse.setIsEnrolled(true);
                    khubCategoryCourse.setId(listCat.get(position).getId());
                    khubCategoryCourse.setKCourseDesc(listCat.get(position).getKCourseDesc());
                    khubCategoryCourse.setKCourseImage(listCat.get(position).getKCourseImage());
                    khubCategoryCourse.setKCourseName(listCat.get(position).getKCourseName());
                    khubCategoryCourse.setOwnerName(listCat.get(position).getOwnerName());
                    intent.putExtra("courseObj",(Serializable)khubCategoryCourse);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return listCat.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCourseName, tvAuthor;
            ImageView ivCourseImage;
            CircularProgressBar courseProgress;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                courseProgress = itemView.findViewById(R.id.cp_cProgress);
                tvAuthor = itemView.findViewById(R.id.tv_author);
                tvCourseName = itemView.findViewById(R.id.tv_courseName);
                ivCourseImage = itemView.findViewById(R.id.iv_course_image);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        utils.dismissDialog();
    }

}