package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class MyAchievements extends AppCompatActivity {
    
    @BindView(R.id.rv_daily)
    RecyclerView rvDaily;
    @BindView(R.id.rv_weekly)
    RecyclerView rvWeekly;
    @BindView(R.id.rv_subject)
    RecyclerView rvSubject;
    MyUtils utils = new MyUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_achievemnets);
        ButterKnife.bind(this);
        
        init();
    }

    private void init() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvDaily.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvDaily.setAdapter(new DailyAdapter());
        rvWeekly.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvWeekly.setAdapter(new WeeklyAdapter());
        rvSubject.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvSubject.setAdapter(new SubjectAdapter());
    }

    class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder>{

        @NonNull
        @Override
        public DailyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MyAchievements.this).inflate(R.layout.item_achievements,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull DailyAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            if (position==0){
                holder.llBackGround.setBackgroundResource(0);
                holder.ivBadge.setImageResource(R.drawable.ic_daily_usage_badge);
            }else {
                holder.ivBadge.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout llBackGround;
            ImageView ivBadge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                llBackGround = itemView.findViewById(R.id.ll_back_ground);
                ivBadge = itemView.findViewById(R.id.iv_badge);
            }
        }
    }

    class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.ViewHolder>{

        @NonNull
        @Override
        public WeeklyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MyAchievements.this).inflate(R.layout.item_achievements,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull WeeklyAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            if (position==0){
                holder.llBackGround.setBackgroundResource(0);
                holder.ivBadge.setImageResource(R.drawable.ic_weekly_usage_badge);
            }else {
                holder.ivBadge.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout llBackGround;
            ImageView ivBadge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                llBackGround = itemView.findViewById(R.id.ll_back_ground);
                ivBadge = itemView.findViewById(R.id.iv_badge);
            }
        }
    }

    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder>{

        @NonNull
        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MyAchievements.this).inflate(R.layout.item_achievements,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            if (position==0){
                holder.llBackGround.setBackgroundResource(0);
                holder.ivBadge.setImageResource(R.drawable.ic_subject_badge);
            }else {
                holder.ivBadge.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout llBackGround;
            ImageView ivBadge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                llBackGround = itemView.findViewById(R.id.ll_back_ground);
                ivBadge = itemView.findViewById(R.id.iv_badge);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}