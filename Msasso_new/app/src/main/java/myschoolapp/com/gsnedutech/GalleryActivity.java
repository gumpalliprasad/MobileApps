package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Fragments.GalleryDynamicFragment;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class GalleryActivity extends AppCompatActivity {

    @BindView(R.id.rv_gallery)
    RecyclerView rvGallery;
    @BindView(R.id.vp_gallery)
    ViewPager vpGallery;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    MyUtils utils = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        if (getResources().getString(R.string.screen).equalsIgnoreCase("Xlarge")){
            rvGallery.setLayoutManager(new GridLayoutManager(this,5));
        }else{
            rvGallery.setLayoutManager(new GridLayoutManager(this,3));
        }

        rvGallery.setAdapter(new GalleryAdapter());
    }

    class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

        @NonNull
        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(GalleryActivity.this).inflate(R.layout.item_gallery,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    tvTitle.setText();
                    vpGallery.setVisibility(View.VISIBLE);
                    TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
                    for (int i=0;i<5;i++){
                        adapter.addFragment(new GalleryDynamicFragment(),"");
                    }
                    vpGallery.setAdapter(adapter);

                }
            });

        }

        @Override
        public int getItemCount() {

            return 15;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
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

        if (vpGallery.getVisibility()==View.VISIBLE){
            vpGallery.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

}