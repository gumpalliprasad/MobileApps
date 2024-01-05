package myschoolapp.com.gsnedutech;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
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
import myschoolapp.com.gsnedutech.Fragments.MockOptionDynamicFrag;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MockTestOptionActivity extends AppCompatActivity {
    MyUtils utils = new MyUtils();

    private static final String TAG = MockTestOptionActivity.class.getName();
    @BindView(R.id.tv_cbse)
    TextView tvCbse;
    @BindView(R.id.tv_iit)
    TextView tvIit;
    @BindView(R.id.tv_neet)
    TextView tvNeet;
    @BindView(R.id.vp_mock_options)
    ViewPager vpMockOptions;
    @BindView(R.id.tv_header)
    TextView tvHeader;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    TabAdapter adapter;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    String navto;



    List<StdnTestDefClsCourseSub> stdnTestCoursesList = new ArrayList<>();
    List<List<StdnTestCategories>> stdnTestCategoryLists = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_test_option);
        ButterKnife.bind(this);
        init();

        if (NetworkConnectivity.isConnected(this)) {
            getAllStudentClassCourseSubjects();
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }



    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        navto = getIntent().getStringExtra("navto");
        if (navto!=null &&navto.equalsIgnoreCase("mock")){
            tvHeader.setText("Mock Tests");
        }
        else {
            tvHeader.setText("Practice");
        }

        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());

        tvCbse.setOnClickListener(view -> {
            tvCbse.setBackgroundResource(R.drawable.bg_grad_tab_select);
            tvCbse.setTextColor(Color.WHITE);
            tvIit.setBackground(null);
            tvIit.setTextColor(Color.rgb(73, 73, 73));
            tvIit.setAlpha(0.75f);
            tvNeet.setBackground(null);
            tvNeet.setTextColor(Color.rgb(73, 73, 73));
            tvNeet.setAlpha(0.75f);

            vpMockOptions.setCurrentItem(0);
        });

        tvIit.setOnClickListener(view -> {
            tvIit.setBackgroundResource(R.drawable.bg_grad_tab_select);
            tvIit.setTextColor(Color.WHITE);
            tvCbse.setBackground(null);
            tvCbse.setTextColor(Color.rgb(73, 73, 73));
            tvCbse.setAlpha(0.75f);
            tvNeet.setBackground(null);
            tvNeet.setTextColor(Color.rgb(73, 73, 73));
            tvNeet.setAlpha(0.75f);

            vpMockOptions.setCurrentItem(1);
        });

        tvNeet.setOnClickListener(view -> {
            tvNeet.setBackgroundResource(R.drawable.bg_grad_tab_select);
            tvNeet.setTextColor(Color.WHITE);
            tvCbse.setBackground(null);
            tvCbse.setTextColor(Color.rgb(73, 73, 73));
            tvCbse.setAlpha(0.75f);
            tvIit.setBackground(null);
            tvIit.setTextColor(Color.rgb(73, 73, 73));
            tvIit.setAlpha(0.75f);

            vpMockOptions.setCurrentItem(2);
        });
    }


    public class TabAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        TabAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }


    private void getAllStudentClassCourseSubjects() {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "Log - " + new AppUrls().GetAllStudentClassCourseSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&branchId=" + sObj.getBranchId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId());

        Request request = apiClient.getRequest(new AppUrls().GetAllStudentClassCourseSubjects + "schemaName=" + sh_Pref.getString("schema", "")
                + "&branchId=" + sObj.getBranchId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId="
                + sObj.getStudentId(), sh_Pref);

        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body()!=null){
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Students Subjects - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = parentjObject.getJSONArray("defaultCourseClasses");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestDefClsCourseSub>>() {
                            }.getType();

                            stdnTestCoursesList.clear();
                            stdnTestCoursesList.addAll(gson.fromJson(jsonArr.toString(), type));

                            utils.showLog(TAG, "CoursesList - " + stdnTestCoursesList.size());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (adapter==null){
                                        adapter = new TabAdapter(getSupportFragmentManager());
                                    }

                                    for (int i=0;i<stdnTestCoursesList.size();i++){
                                        adapter.addFragment(new MockOptionDynamicFrag(navto,stdnTestCoursesList.get(i)),stdnTestCoursesList.get(i).getCourseName());
                                    }

                                    vpMockOptions.setAdapter(adapter);
                                    tabLayout.setupWithViewPager(vpMockOptions);
                                }
                            });



                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, MockTestOptionActivity.this, message, sh_Pref);
                            });
                        }else if (parentjObject.getString("StatusCode").equalsIgnoreCase("300")) {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }



}