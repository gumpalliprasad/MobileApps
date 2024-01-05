package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.AssignedReportFragment;
import myschoolapp.com.gsnedutech.Fragments.MockReportFragment;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReportActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = "SriRam -" + ReportActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;

    @BindView(R.id.vp_repo)
    ViewPager vpRepo;

    @BindView(R.id.tv_student_name)
    TextView tvStudentName;
    @BindView(R.id.tv_class_name)
    TextView tvClassName;
    @BindView(R.id.tv_student_id)
    TextView tvStudentId;
    @BindView(R.id.dp)
    CircleImageView dp;

    SharedPreferences sh_Pref;
    StudentObj sObj;
    String studentId="", profilePic="", rollNumber="", studentName="", className="";

    MyUtils utils = new MyUtils();

    int selectedItem=0;

    CategoryAdapter categoryAdapter;

    List<String> listCtegories = new ArrayList<>();
    TabAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_report);

        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, ReportActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                vpRepo.setAdapter(adapter);
                vpRepo.setCurrentItem(selectedItem);
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    private void init() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("student_loggedin", false) || sh_Pref.getBoolean("parent_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
            studentId = sObj.getStudentId();
            profilePic = sObj.getProfilePic();
            rollNumber = sObj.getStudentRollnumber();
            className = sObj.getClassName();
            studentName = sObj.getStudentName();
        }else{
            studentId = getIntent().getStringExtra("studentId");
            profilePic = getIntent().getStringExtra("studentProfilePic");
            rollNumber = getIntent().getStringExtra("rollNumber");
            className = getIntent().getStringExtra("className");
            studentName = getIntent().getStringExtra("studentName");
        }

        Picasso.with(ReportActivity.this).load(profilePic).placeholder(R.drawable.user_default)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(dp);

        tvStudentId.setText(rollNumber);
        tvStudentName.setText(studentName);
        tvClassName.setText(className);





        listCtegories.add("Mock Tests");
        listCtegories.add("Assigned Tests");

        rvCategories.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        categoryAdapter = new CategoryAdapter();
        rvCategories.setAdapter(categoryAdapter);

//        rvClassRepo.setLayoutManager(new LinearLayoutManager(this));
//        rvClassRepo.setAdapter(new ClassAdapter());

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new MockReportFragment(),"");
        adapter.addFragment(new AssignedReportFragment(),"");
        vpRepo.setAdapter(adapter);

        vpRepo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedItem = position;
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder>{

        @NonNull
        @Override
        public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReportActivity.this).inflate(R.layout.item_class_repo,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, int position) {
            holder.rvSubjects.setLayoutManager(new LinearLayoutManager(ReportActivity.this));
            holder.rvSubjects.setAdapter(new SubjectAdapter());
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView rvSubjects;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                rvSubjects = itemView.findViewById(R.id.rv_sub_repo);
            }
        }
    }

    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{

        @NonNull
        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ReportActivity.this).inflate(R.layout.item_report_subject,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {

            switch (position){
                case 0:
                    holder.tvSubName.setText("Maths");
                    holder.pbSub.setIndicatorColors(new int[]{Color.rgb(22, 142, 80)});
                    holder.pbSub.setProgress(40);
                    
                    break;
                case 1:
                    holder.tvSubName.setText("English");
                    holder.pbSub.setIndicatorColors(new int[]{Color.rgb(180, 66, 81)});
                    holder.pbSub.setProgress(60);
                    

                    break;
                case 2:
                    holder.tvSubName.setText("Science");
                    holder.pbSub.setIndicatorColors(new int[]{Color.rgb(240, 194, 48)});
                    holder.pbSub.setProgress(50);
                    

                    break;
                case 3:
                    holder.tvSubName.setText("Social");
                    holder.pbSub.setIndicatorColors(new int[]{Color.rgb(22, 70, 142)});
                    holder.pbSub.setProgress(55);
                    

                    break;

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ReportActivity.this,ReportClassActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return 4;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubName;
            ProgressIndicator pbSub;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSubName = itemView.findViewById(R.id.tv_sub_name);
                pbSub = itemView.findViewById(R.id.progress_sub);

            }
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CategoryAdapter.ViewHolder(LayoutInflater.from(ReportActivity.this).inflate(R.layout.item_chapter_topic,parent,false));
        }


        @Override
        public long getItemId(int position) {
            return  position;
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            holder.tvChp.setText(listCtegories.get(position));

            if (position==selectedItem){
                holder.tvChp.setBackgroundResource(R.drawable.bg_grad_chap_select);
                holder.tvChp.setTextColor(Color.WHITE);
            }else {
                holder.tvChp.setBackgroundResource(R.drawable.bg_border_10);
                holder.tvChp.setTextColor(Color.BLACK);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItem = position;
                    notifyDataSetChanged();
                    rvCategories.getLayoutManager().scrollToPosition(position);

                   vpRepo.setCurrentItem(position);

                }
            });

        }

        @Override
        public int getItemCount() {
            return listCtegories.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChp;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChp = itemView.findViewById(R.id.tv_chp);
            }
        }
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

    
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}