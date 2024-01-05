package myschoolapp.com.gsnedutech.Neet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiClient;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiInterface;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetNetworkConnectivity;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetUtil;
import myschoolapp.com.gsnedutech.Neet.models.NeetChapter;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityNeetSubjectDetailsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NeetChaptersActivity extends AppCompatActivity {

    private ActivityNeetSubjectDetailsBinding binding;
    MyUtils utils = new MyUtils();


    ArrayList<NeetChapter> neetChapters = new ArrayList<>();
    NeetApiInterface neetApiInterface;
    String subName;
    int drawable;
    SharedPreferences sh_Pref;
    StudentObj sObj;
    private LottieAnimationView animationView;

    int selectPos =-1;
    ChaptersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNeetSubjectDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);


        Gson gson = new Gson();
        JSONObject obj = null;
        try {
            obj = new JSONObject(sh_Pref.getString("studentObj", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sObj = gson.fromJson(obj.toString(), StudentObj.class);

        subName = getIntent().getStringExtra("subname");
        drawable = getIntent().getIntExtra("drawable",R.drawable.neet_physics_gradient);
        binding.tvSubName.setText(subName);
        binding.rvSubChapters.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
//        binding.rvSubChapters.addItemDecoration(dividerItemDecoration);
        neetApiInterface = NeetApiClient.getClient().create(NeetApiInterface.class);
        adapter = new ChaptersAdapter(neetChapters);

        binding.imgBack.setOnClickListener(view1 -> onBackPressed());

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

    }

    void refresh(boolean isFirst){
        if (NeetNetworkConnectivity.isConnected(NeetChaptersActivity.this)) {
            getChapters(isFirst,getIntent().getStringExtra("id"));
        } else {
            new NeetUtil().alertDialog(1, NeetChaptersActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (neetChapters.size()>0){
//            refresh(false);
//        }else
//            refresh(true);
    }

    private void getChapters(boolean isFirst,String id) {
        if (isFirst)
        showProgress();
        Call<ArrayList<NeetChapter>> call = neetApiInterface.getChapters(id,getIntent().getStringExtra("uId"));
        call.enqueue(new Callback<ArrayList<NeetChapter>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<NeetChapter>> call, @NotNull Response<ArrayList<NeetChapter>> response) {
                if(response.body()!=null){
                    neetChapters = response.body();
                    binding.swipeContainer.setRefreshing(false);
                    if(neetChapters.size()>0){
                        binding.rvSubChapters.setVisibility(View.VISIBLE);
                        binding.tvNoavailable.setVisibility(View.GONE);
                        adapter = new ChaptersAdapter(neetChapters);
                        binding.rvSubChapters.setAdapter(adapter);
                    }
                    else {
                        binding.rvSubChapters.setVisibility(View.GONE);
                        binding.tvNoavailable.setVisibility(View.VISIBLE);
                    }
                    new Handler().postDelayed(() -> dismissDialog(),1000);
                }
                else {
                    new Handler().postDelayed(() -> dismissDialog(),1000);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<NeetChapter>> call, @NotNull Throwable t) {
                if (neetChapters.size()<=0){
                    binding.rvSubChapters.setVisibility(View.GONE);
                    binding.tvNoavailable.setVisibility(View.VISIBLE);
                }
                binding.swipeContainer.setRefreshing(false);

                new Handler().postDelayed(() -> dismissDialog(),1000);
            }
        });
    }

    class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ViewHolder> {

        ArrayList<NeetChapter> sNeetChapters;
        public ChaptersAdapter(ArrayList<NeetChapter> neetChapters) {
            sNeetChapters = neetChapters;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(NeetChaptersActivity.this).inflate(R.layout.item_neet_sub_chapter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvChapName.setText(sNeetChapters.get(position).getChapDisplayName());
            holder.tvChapInd.setText(""+ sNeetChapters.get(position).getChapDisplayName().charAt(0));
            if(subName.equalsIgnoreCase("physics")){
                holder.tvChapInd.setTextColor(Color.parseColor("#EB22AF"));
            }
            else if (subName.equalsIgnoreCase("chemistry")){
                holder.tvChapInd.setTextColor(Color.parseColor("#FB7900"));
            }
            else if (subName.equalsIgnoreCase("botany")){
                holder.tvChapInd.setTextColor(Color.parseColor("#41EB22"));
            }
            else if (subName.equalsIgnoreCase("zoology")){
                holder.tvChapInd.setTextColor(Color.parseColor("#FF0000"));
            }
            holder.pbChapters.setProgressDrawable(getDrawable(drawable));
            int progress = Math.round(sNeetChapters.get(position).getPercentage());
            holder.pbChapters.setProgress(progress);
            holder.tvProg.setText(progress+"%");
            if (progress>50){
                holder.tvProg.setTextColor(Color.parseColor("#ffffff"));
            }
            else holder.tvProg.setTextColor(Color.parseColor("#000000"));
            holder.itemView.setOnClickListener(view -> {
                selectPos = position;
                Intent i = new Intent(NeetChaptersActivity.this, NeetChapterQuestions.class);
                i.putExtra("neetChapter", neetChapters.get(position));
                i.putExtra("uId", getIntent().getStringExtra("uId"));
                i.putExtra("subname",subName);
                i.putExtra("drawable", drawable);
                i.putExtra("contentmatrix", getIntent().getStringExtra("id"));
                startActivityForResult(i,1234);
            });
        }

        @Override
        public int getItemCount() {
            return sNeetChapters.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChapName, tvProg, tvChapInd;
            ProgressBar pbChapters;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChapName = itemView.findViewById(R.id.tv_chap_name);
                pbChapters = itemView.findViewById(R.id.pb_chapters);
                tvProg = itemView.findViewById(R.id.prog);
                tvChapInd = itemView.findViewById(R.id.tv_chater_indc);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1234){
            if (resultCode==4321){
                if (selectPos>=0 && selectPos <= neetChapters.size()){
                    neetChapters.set(selectPos, (NeetChapter) data.getSerializableExtra("neetChapter"));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();

    }
}