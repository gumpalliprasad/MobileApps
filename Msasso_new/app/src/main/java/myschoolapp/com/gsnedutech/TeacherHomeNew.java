package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaCategories;
import myschoolapp.com.gsnedutech.Fragments.TeacherBottomArena;
import myschoolapp.com.gsnedutech.Fragments.TeacherBottomCourse;
import myschoolapp.com.gsnedutech.Fragments.TeacherBottomFrag;
import myschoolapp.com.gsnedutech.Fragments.TeacherBottomKHub;
import myschoolapp.com.gsnedutech.Fragments.TeacherBottomQBoxNew;
import myschoolapp.com.gsnedutech.Models.TeacherCCSSObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TeacherHomeNew extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + TeacherHomeNew.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;
    MyUtils utils = new MyUtils();


    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout navDrawer;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.tv_course)
    TextView tvCourse;
    @BindView(R.id.tv_live)
    TextView tvLive;
    @BindView(R.id.tv_q_box)
    TextView tvQbox;
    @BindView(R.id.tv_k_hub)
    TextView tvKHub;

    @BindView(R.id.fab_arena)
    public ImageView fbArena;
    @BindView(R.id.rv_main)
    public RelativeLayout rvMain;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    public List<ArenaCategories> arenaCategoriesList = new ArrayList<>();

    int backPress = 0, selectedTab = 0;

    public List<TeacherCCSSObj> teacherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        ButterKnife.bind(this);

        init();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                isNetworkAvail = false;
                onNetworkConnectionChanged(NetworkConnectivity.isConnected(TeacherHomeNew.this));

            }
        });

        findViewById(R.id.nav_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the navigation drawer is not open then open it, if its already open then close it.
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(GravityCompat.START);
                else navDrawer.closeDrawer(GravityCompat.END);
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_tests:
                        startActivity(new Intent(TeacherHomeNew.this, TeacherLivePreviousExams.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_schedules:
                        startActivity(new Intent(TeacherHomeNew.this, TeacherCalAndEvents.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_my_attendance:
                        startActivity(new Intent(TeacherHomeNew.this, TeacherAttendance.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_leaves:
                        startActivity(new Intent(TeacherHomeNew.this, TeacherLeaveRequest.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_todo:
                        startActivity(new Intent(TeacherHomeNew.this, TeacherToDoActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_gallery:
                        startActivity(new Intent(TeacherHomeNew.this, GalleryActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_circulars:
                        startActivity(new Intent(TeacherHomeNew.this, Circulars.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_log_out:
                        MyUtils.userLogOut(toEdit, TeacherHomeNew.this, sh_Pref);
                        break;
                }
                navDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHomeNew.this, UserMessages.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });

        getTeacherCourses();
    }

    void init(){

        navView.setItemIconTintList(null);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        View headerView = navView.getHeaderView(0);

        tvName.setText("HEY, " +tObj.getUserName().split(" ")[0]);
        ((TextView)headerView.findViewById(R.id.tv_name)).setText(tObj.getUserName());
        Picasso.with(TeacherHomeNew.this).load(new AppUrls().GetStaffProfilePic +tObj.getProfilePic()).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into((CircleImageView)headerView.findViewById(R.id.img_profile));
        ((TextView)headerView.findViewById(R.id.tv_class)).setText(tObj.getRoleName());
        ((TextView)headerView.findViewById(R.id.tv_id)).setText(tObj.getBranchName());

        headerView.findViewById(R.id.ll_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherHomeNew.this, TeacherProfileActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        tvHome.setOnClickListener(this);
        tvLive.setOnClickListener(this);
        tvCourse.setOnClickListener(this);
        tvQbox.setOnClickListener(this);
        tvKHub.setOnClickListener(this);

        GetCredDetails(sh_Pref.getString("schema", ""));

        GetArenacategories();

    }


    private void GetCredDetails(String schema) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetCredDetails + "id=1&schemaName=" + schema)
                //.headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "S3 Credentials request - " + new AppUrls().GetCredDetails);
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.body() != null) {
                        try {
                            ResponseBody body = response.body();
                            String jsonResponse = response.body().string();
                            JSONObject ParentjObject = new JSONObject(jsonResponse);
                            if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = ParentjObject.getJSONArray("userClassSubjects");
                                JSONObject jObj = jsonArray.getJSONObject(0);
                                toEdit.putString("akey", jObj.getString("akey"));
                                toEdit.putString("skey", jObj.getString("skey"));
                                toEdit.putString("bucket", jObj.getString("name"));
                                toEdit.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GetArenacategories() {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Log.v(TAG, "URL - ARENA CATEGORIES - " + new AppUrls().Arena_GetArenaCategories + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""));

        Request request = new Request.Builder()
                .url(new AppUrls().Arena_GetArenaCategories + "schemaName=" + getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema", ""))
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
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaCategories>>() {
                            }.getType();

                            arenaCategoriesList.clear();
                            arenaCategoriesList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "arenaCategoriesList - " + arenaCategoriesList.size());

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
            utils.alertDialog(1, TeacherHomeNew.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                switch (selectedTab) {
                    case 0:
                        selectedTab--;
                        tvHome.callOnClick();
                        break;
                    case 1:
                        selectedTab--;
                        tvCourse.callOnClick();
                        break;
                    case 2:
                        selectedTab--;
                        tvLive.callOnClick();
                        break;
                    case 3:
                        selectedTab--;
                        tvQbox.callOnClick();
                        break;
                    case 4:
                        selectedTab--;
                        tvKHub.callOnClick();
                        break;
                }
            }
            swipeLayout.setRefreshing(false);
            isNetworkAvail = true;
        }
    }

    public boolean loadFragment(Fragment fragment) {


        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onBackPressed() {
        backPress++;
        if (backPress == 1) {
            Toast.makeText(TeacherHomeNew.this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.tv_home:
                if (selectedTab != 0) {
                    unSelectAll();
                    fragment = new TeacherBottomFrag();
                    tvHome.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 0;
                }

                break;
            case R.id.tv_course:
                if (selectedTab != 1) {
                    unSelectAll();
                    fragment = new TeacherBottomCourse();
                    tvCourse.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 1;
                }

                break;
            case R.id.tv_live:
                if (selectedTab != 2) {
                    unSelectAll();
                    fragment = new TeacherBottomArena();
                    tvLive.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 2;
                }

                break;
            case R.id.tv_q_box:
                if (selectedTab != 3) {
                    unSelectAll();
                    fragment = new TeacherBottomQBoxNew();
                    tvQbox.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 3;
                }

                break;
            case R.id.tv_k_hub:
                if (selectedTab != 4) {
                    unSelectAll();
                    fragment = new TeacherBottomKHub();
                    tvKHub.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 4;
                }

                break;

        }
    }

    private void unSelectAll() {
        tvHome.setAlpha(0.5f);
        tvCourse.setAlpha(0.5f);
        tvLive.setAlpha(0.5f);
        tvQbox.setAlpha(0.5f);
        tvKHub.setAlpha(0.5f);
    }


    void getTeacherCourses(){
        utils.showLoader(TeacherHomeNew.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request get = new Request.Builder()
                .url(new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + tObj.getUserId())
                .build();
        utils.showLog(TAG, new AppUrls().TeacherCCSSDetials + "schemaName=" + sh_Pref.getString("schema","") + "&userId=" + tObj.getUserId());
        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        tvHome.callOnClick();
//                        getTodoList();
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
                            tvHome.callOnClick();
//                            getTodoList();
                        }
                    });
                } else {
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = ParentjObject.getJSONArray("userClassSubjects");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<TeacherCCSSObj>>() {
                            }.getType();
                            teacherList.clear();
                            teacherList.addAll(gson.fromJson(jsonArr.toString(), type));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<TeacherCCSSObj> adapter =
                                            new ArrayAdapter<>(TeacherHomeNew.this, android.R.layout.simple_spinner_dropdown_item, teacherList);
                                    adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
//                                    spCourses.setAdapter(adapter);
//                                    spCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                        @Override
//                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                                            rvCourses.setLayoutManager(new LinearLayoutManager(TeacherHome.this));
//                                            rvCourses.setAdapter(new TeacherHome.CouseAdapter(teacherList.get(i).getClasses()));
//                                        }
//
//                                        @Override
//                                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                                        }
//                                    });
                                }
                            });

                            Log.v(TAG, "TeacherList Size " + teacherList.size());
                            if (teacherList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        utils.dismissDialog();
                                        tvHome.callOnClick();
//                                        getTodoList();
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}