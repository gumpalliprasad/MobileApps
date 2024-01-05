package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
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
import myschoolapp.com.gsnedutech.Fragments.StudentBottomArena;
import myschoolapp.com.gsnedutech.Fragments.StudentBottomCourse;
import myschoolapp.com.gsnedutech.Fragments.StudentBottomHomeFrag;
import myschoolapp.com.gsnedutech.Fragments.StudentBottomKHubNewDesign;
import myschoolapp.com.gsnedutech.Fragments.StudentBottomQBoxNew;
import myschoolapp.com.gsnedutech.Models.Course;
import myschoolapp.com.gsnedutech.Models.HomeWorkDetails;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.StudentSub;
import myschoolapp.com.gsnedutech.Util.AppConst;
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

public class StudentHome extends AppCompatActivity implements View.OnClickListener, NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + StudentHome.class.getName();

    private AppUpdateManager mAppUpdateManager;
    private static int APP_UPDATE_REQUEST_CODE = 1;

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout navDrawer;
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
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.fab_arena)
    public ImageView fbArena;
    @BindView(R.id.fb_mydoubts)
    public ImageView fabMyDoubts;
    @BindView(R.id.rv_main)
    public RelativeLayout rvMain;
//    @BindView(R.id.view_app_bar)
//    public View vAppBar;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.toolbar)
    Toolbar toolBar;

//    @BindView(R.id.htab_collapse_toolbar)
//    public CollapsingToolbarLayout collapsingToolbarLayout;

    int backPress = 0, selectedTab = 0;

    public List<StudentSub> subject_list = new ArrayList<>();
    public List<Course> courses_list = new ArrayList<>();

    public List<HomeWorkDetails> listHwPending = new ArrayList<>();
    public List<String> typeHw = new ArrayList<>();

    public List<ArenaCategories> arenaCategoriesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar);
        getSupportActionBar().setElevation(0);


        init();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                isNetworkAvail = false;
                onNetworkConnectionChanged(NetworkConnectivity.isConnected(StudentHome.this));

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateApp();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, StudentHome.this, getString(R.string.error_connect), getString(R.string.error_internet),
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

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }


    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        //GetArenacategories();

        GetCredDetails(sh_Pref.getString("schema", ""));

        View headerView = navView.getHeaderView(0);


        tvName.setText(sObj.getStudentName().split(" ")[0]);

        CircleImageView ivHeader = headerView.findViewById(R.id.img_profile);
        Picasso.with(StudentHome.this).load(sObj.getProfilePic()).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivHeader);
        TextView tvClass = headerView.findViewById(R.id.tv_class);
        tvClass.setText(sObj.getClassName());
        TextView tvId = headerView.findViewById(R.id.tv_id);
        tvId.setText(sObj.getLoginId());

        navView.setItemIconTintList(null);

//        loadFragment(new StudentBottomHomeFrag());

        //loading the fragment based on the request from otheer activities

        tvHome.setOnClickListener(this);
        tvLive.setOnClickListener(this);
        tvCourse.setOnClickListener(this);
        tvQbox.setOnClickListener(this);
        tvKHub.setOnClickListener(this);


        LinearLayout llProfile = headerView.findViewById(R.id.ll_profile);
        ((TextView) llProfile.findViewById(R.id.tv_name)).setText(sObj.getStudentName());
        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentHome.this, ProfileActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });


        findViewById(R.id.iv_notify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentHome.this, Notifications.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
        findViewById(R.id.iv_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentHome.this, ChatActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });
        findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentHome.this, SearchActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

            }
        });

        findViewById(R.id.nav_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the navigation drawer is not open then open it, if its already open then close it.
                openCLoseNav();
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_wishlist:
                        Toast.makeText(StudentHome.this, "Wishlist", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_my_achievements:
                        startActivity(new Intent(StudentHome.this, MyAchievements.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_report:
                        Intent perIntent = new Intent(StudentHome.this, StudentAnalytics.class);
                        perIntent.putExtra("studentId", sObj.getStudentId());
                        perIntent.putExtra("courseName", sObj.getClassName());
                        perIntent.putExtra("branchId", sObj.getBranchId());
                        perIntent.putExtra("courseId", sObj.getCourseId() + "");
                        perIntent.putExtra("classId", sObj.getClassId() + "");
                        perIntent.putExtra("sectionId", sObj.getClassCourseSectionId() + "");
                        startActivity(perIntent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_personal_report:
                        startActivity(new Intent(StudentHome.this, PersonalNotes.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_todo:
                        startActivity(new Intent(StudentHome.this, ToDoActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_gallery:
                        startActivity(new Intent(StudentHome.this, GalleryActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_feedback:
                        startActivity(new Intent(StudentHome.this, FeedbackListActivity.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_circulars:
                        startActivity(new Intent(StudentHome.this, Circulars.class));
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        break;
                    case R.id.navigation_contact:
                        Toast.makeText(StudentHome.this, "Contact Us", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_terms_conditions:
                        Toast.makeText(StudentHome.this, "Terms and Conditions", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_log_out:
                        MyUtils.userLogOut(toEdit, StudentHome.this, sh_Pref);
                        break;
                }
                navDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        if (getIntent().getBooleanExtra("course",false)){
            selectCourseTab();
        }


    }

    public void openCLoseNav() {
        if (!navDrawer.isDrawerOpen(GravityCompat.START))
            navDrawer.openDrawer(GravityCompat.START);
        else navDrawer.closeDrawer(GravityCompat.END);
    }

    public void selectCourseTab() {
        unSelectAll();
        Fragment fragment = new StudentBottomCourse();
        tvCourse.setAlpha(1f);
        loadFragment(fragment);
        selectedTab = 1;
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
                //.headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body() != null)  {
                    try {
                        String resp = response.body().string();
                        JSONObject parentjObject = new JSONObject(resp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("arenaCategories");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaCategories>>() {
                            }.getType();

                            arenaCategoriesList.clear();
                            arenaCategoriesList.addAll(gson.fromJson(jsonArr.toString(), type));
                            Log.v(TAG, "arenaCategoriesList - " + arenaCategoriesList.size());
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, StudentHome.this, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

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
                    runOnUiThread(() -> utils.dismissDialog());
                    if (response.body() != null) {
                        try {
                            ResponseBody body = response.body();
                            String jsonResponse = response.body().string();
                            JSONObject parentjObject = new JSONObject(jsonResponse);
                            if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                JSONArray jsonArray = parentjObject.getJSONArray("userClassSubjects");
                                JSONObject jObj = jsonArray.getJSONObject(0);
                                toEdit.putString("akey", jObj.getString("akey"));
                                toEdit.putString("skey", jObj.getString("skey"));
                                toEdit.putString("bucket", jObj.getString("name"));
                                toEdit.commit();
                            }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                String message = parentjObject.getString(AppConst.MESSAGE);
                                runOnUiThread(() -> {
                                    MyUtils.forceLogoutUser(toEdit, StudentHome.this, message, sh_Pref);
                                });
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
    public void onBackPressed() {
        backPress++;
        if (backPress == 1) {
            Toast.makeText(StudentHome.this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        toolBar.setBackgroundColor(getColor(R.color.colorPrimary));
        ((ImageView) findViewById(R.id.nav_img)).setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        tvName.setTextColor(Color.parseColor("#000000"));
        ((ImageView) findViewById(R.id.iv_search)).setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        ((ImageView) findViewById(R.id.iv_chat)).setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        ((ImageView) findViewById(R.id.iv_notify)).setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        switch (view.getId()) {
            case R.id.tv_home:
                if (selectedTab != 0) {
                    unSelectAll();
//                    findViewById(R.id.htab_header).setVisibility(View.VISIBLE);
                    fragment = new StudentBottomHomeFrag();
                    tvHome.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 0;
                }

                break;
            case R.id.tv_course:
                if (selectedTab != 1) {
                    unSelectAll();
                    fragment = new StudentBottomCourse();
                    tvCourse.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 1;
                }

                break;
            case R.id.tv_live:
                if (selectedTab != 2) {
                    unSelectAll();
                    fragment = new StudentBottomArena();
                    tvLive.setAlpha(1f);
                    loadFragment(fragment);
                    selectedTab = 2;
                    if (!sh_Pref.contains("bucket")){
                        GetCredDetails(sh_Pref.getString("schema",""));
                    }
                }

                break;
            case R.id.tv_q_box:
                if (selectedTab != 3) {
                    unSelectAll();
                    fragment = new StudentBottomQBoxNew();
                    tvQbox.setAlpha(1f);
                    toolBar.setBackground(getDrawable(R.drawable.bg_qbox_ff2d2d));
                    ((ImageView)findViewById(R.id.nav_img)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    tvName.setTextColor(Color.parseColor("#ffffff"));
                    ((ImageView)findViewById(R.id.iv_search)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    ((ImageView)findViewById(R.id.iv_chat)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    ((ImageView)findViewById(R.id.iv_notify)).setColorFilter(ContextCompat.getColor(this, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

                    loadFragment(fragment);
                    selectedTab = 3;
                }

                break;
            case R.id.tv_k_hub:
                if (selectedTab != 4) {
                    unSelectAll();
                    fragment = new StudentBottomKHubNewDesign();
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

    private void updateApp() {
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, StudentHome.this, APP_UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else {
//                init();
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }
                    } else {
                        Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.cc_splash),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
//                init();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }


}