package myschoolapp.com.gsnedutech.JeeMains.files;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.JeeMains.Utils.JeeApiClient;
import myschoolapp.com.gsnedutech.JeeMains.models.Chapter;
import myschoolapp.com.gsnedutech.JeeMains.models.UserObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.databinding.ActivityJeeChaptersBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JeeChaptersActivity extends AppCompatActivity {

    private ActivityJeeChaptersBinding binding;
    ApiInterface apiInterface;
    ArrayList<Chapter> chapters = new ArrayList<>();
    ChaptersAdapter adapter;
    SharedPreferences sh_Pref;
    UserObj uObj;
    int drawable;
    int selectPos =-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
        binding = ActivityJeeChaptersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.jee_sh_pref), MODE_PRIVATE);


        Gson gson = new Gson();
        String json = sh_Pref.getString("usrObj", "");
        uObj = gson.fromJson(json, UserObj.class);
        drawable = getIntent().getIntExtra("drawable",R.drawable.jee_physics_gradient);
        binding.tvChapname.setText(getIntent().getStringExtra("subname"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.imgSub.setImageDrawable(getDrawable(getIntent().getIntExtra("imgsub",0)));
        }
        else  binding.imgSub.setImageDrawable(getResources().getDrawable(getIntent().getIntExtra("imgsub",0)));
        apiInterface = JeeApiClient.getClient().create(ApiInterface.class);
        refresh(true);
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refresh(false);
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        binding.imgBack.setOnClickListener(view1 -> onBackPressed());
    }

    void refresh(boolean isFirst){
        if (NetworkConnectivity.isConnected(JeeChaptersActivity.this)) {
            getChapters(isFirst,getIntent().getStringExtra("id"));
        } else {
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }

    private void getChapters(boolean isFirst,String id) {
        if (isFirst)
            showProgress();
        Call<ArrayList<Chapter>> call = apiInterface.getChapters(id,getIntent().getStringExtra("uid"));
        call.enqueue(new Callback<ArrayList<Chapter>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Chapter>> call, @NotNull Response<ArrayList<Chapter>> response) {
                if(response.body()!=null){
                    chapters = response.body();
                    binding.swipeContainer.setRefreshing(false);
                    if(chapters.size()>0){
                        binding.rvChapters.setVisibility(View.VISIBLE);
                        binding.tvNoChapters.setVisibility(View.GONE);
                        adapter = new ChaptersAdapter(chapters);
                        binding.rvChapters.setAdapter(adapter);
                    }
                    else {
                        binding.rvChapters.setVisibility(View.GONE);
                        binding.tvNoChapters.setVisibility(View.VISIBLE);
                    }
                        new Handler().postDelayed(() -> dismissDialog(),1000);
                }
                else {
                        new Handler().postDelayed(() -> dismissDialog(),1000);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<Chapter>> call, @NotNull Throwable t) {
                if (chapters.size()<=0){
                    binding.rvChapters.setVisibility(View.GONE);
                    binding.tvNoChapters.setVisibility(View.VISIBLE);
                }
                binding.swipeContainer.setRefreshing(false);
                    new Handler().postDelayed(() -> dismissDialog(),1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1234){
            if (resultCode==4321){
                if (selectPos>=0 && selectPos <= chapters.size()){
                    chapters.set(selectPos, (Chapter) data.getSerializableExtra("chapter"));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    MyUtils utils = new MyUtils();
    private LottieAnimationView animationView;
    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();
    }


    class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ViewHolder> {

        ArrayList<Chapter> sChapters ;
        public ChaptersAdapter(ArrayList<Chapter> chapters) {
            sChapters = chapters;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(JeeChaptersActivity.this).inflate(R.layout.item_jee_chapters, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvChapNo.setText(position>=9?""+(position+1):("0"+(position+1)));
            holder.tvChapName.setText(sChapters.get(position).getChapDisplayName());
            holder.itemView.setOnClickListener(view -> {
                selectPos = position;
                Intent i = new Intent(JeeChaptersActivity.this,JeeChapterQuestions.class);
                i.putExtra("chapter",chapters.get(position));
                i.putExtra("subname",getIntent().getStringExtra("subname"));
                i.putExtra("drawable", drawable);
                i.putExtra("uid", getIntent().getStringExtra("uid"));
                i.putExtra("imgsub",getIntent().getIntExtra("imgsub",0));
                i.putExtra("contentmatrix", getIntent().getStringExtra("id"));
                startActivityForResult(i,1234);
            });

        }

        @Override
        public int getItemCount() {
            return sChapters.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChapName, tvChapNo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChapName = itemView.findViewById(R.id.tv_chapname);
                tvChapNo = itemView.findViewById(R.id.tv_chapno);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}