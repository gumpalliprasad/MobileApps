package myschoolapp.com.gsnedutech.JeeAdvanced.Files;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubjectsObj;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeYearsObj;
import myschoolapp.com.gsnedutech.databinding.ContentAdvjeeMainBinding;
import retrofit2.Call;
import retrofit2.Callback;


public class AdvJeeMainActivity extends AppCompatActivity {

    private ContentAdvjeeMainBinding binding;

    ApiInterface apiInterface;
    ArrayList<AdvJeeYearsObj> years = new ArrayList<>();
    SharedPreferences sh_Pref;
    SharedPreferences shPref;
    SharedPreferences.Editor toEdit;

    StudentObj sObj;
//    UserObj uObj;
    YearsAdapter adapter;
    int selectedPaper = 0;
    int selectedyear = -1;
    String yearString;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ContentAdvjeeMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sh_Pref = getSharedPreferences(getResources().getString(R.string.adv_sh_pref), MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        toEdit.apply();

        Gson gson = new Gson();
        String json = sh_Pref.getString("usrObj", "");

        shPref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
         gson = new Gson();
         json = shPref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
//        uObj = gson.fromJson(json, UserObj.class);
        apiInterface = ApiClient.getAdvJeeClient().create(ApiInterface.class);
        binding.rvYears.setLayoutManager(new LinearLayoutManager(this));

        binding.imgBack.setOnClickListener(view1 -> onBackPressed());

        yearString = sh_Pref.getString("years", "");
        if (yearString != null && yearString.equalsIgnoreCase("")) {
//            binding.content.tvNoavailable.setVisibility(View.VISIBLE);
            binding.rvYears.setVisibility(View.GONE);
        } else {
            String year = sh_Pref.getString("years", "");
            if (year != null) {
                years = new Gson().fromJson(year, new TypeToken<ArrayList<AdvJeeYearsObj>>() {
                }.getType());
                binding.rvYears.setVisibility(View.VISIBLE);
                binding.tvNoavailable.setVisibility(View.GONE);
                adapter = new YearsAdapter(years);
                binding.rvYears.setAdapter(adapter);
            }

        }
        init();



    }

    @Override
    protected void onResume() {
        if (years.size()>0){
            refresh(false);
        }else {
            refresh(true);
        }
        super.onResume();
    }

    private void init() {





        binding.swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            refresh(false);
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    void refresh(boolean isFirst) {
        if (NetworkConnectivity.isConnected(AdvJeeMainActivity.this)) {
            getYears(isFirst);
            getSubjects();
        } else {
            binding.swipeContainer.setRefreshing(false);
            new MyUtils().alertDialog(1, AdvJeeMainActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }

    private void getSubjects() {
        Call<ArrayList<AdvJeeSubjectsObj>> call = apiInterface.AdvjeeGetSubjects();
        call.enqueue(new Callback<ArrayList<AdvJeeSubjectsObj>>() {
            @Override
            public void onResponse(Call<ArrayList<AdvJeeSubjectsObj>> call, retrofit2.Response<ArrayList<AdvJeeSubjectsObj>> response) {
                if (response.body() != null && response.body().size() > 0) {
                    toEdit.putString("subjects", new Gson().toJson(response.body()));
                    toEdit.commit();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AdvJeeSubjectsObj>> call, Throwable t) {

            }
        });
    }

    Dialog loading;
    private LottieAnimationView animationView;

    public void showProgress() {
        if (loading != null && loading.isShowing()) loading.dismiss();
        loading = new Dialog(AdvJeeMainActivity.this);
        loading.setContentView(R.layout.dialog_loader);
        loading.setCancelable(false);
        if (loading.getWindow() != null)
            loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        animationView = loading.findViewById(R.id.animation_view);
//        animationView.playAnimation();
        loading.show();
    }

    public void dismissDialog() {
//        if (animationView != null && animationView.isAnimating())
//            animationView.cancelAnimation();
        if (loading != null && loading.isShowing())
            loading.dismiss();
    }

    private void getYears(boolean isFirst) {
        if (isFirst)
            showProgress();
        Call<ArrayList<AdvJeeYearsObj>> call = apiInterface.advjeeGetYears(sObj.getStudentId());
        call.enqueue(new Callback<ArrayList<AdvJeeYearsObj>>() {
            @Override
            public void onResponse(Call<ArrayList<AdvJeeYearsObj>> call, retrofit2.Response<ArrayList<AdvJeeYearsObj>> response) {
                if (response.body() != null) {
                    years = response.body();
                    if (years.size() > 0) {
                        toEdit.putString("years", new Gson().toJson(years));
                        toEdit.commit();
                        binding.rvYears.setVisibility(View.VISIBLE);
                        binding.tvNoavailable.setVisibility(View.GONE);
                        adapter = new YearsAdapter(years);
                        binding.rvYears.setAdapter(adapter);
                    } else {
                        if (yearString != null && yearString.equalsIgnoreCase("")) {
                            binding.tvNoavailable.setVisibility(View.VISIBLE);
                            binding.rvYears.setVisibility(View.GONE);
                        } else {
                            years = new Gson().fromJson(sh_Pref.getString("years", ""), new TypeToken<ArrayList<AdvJeeYearsObj>>() {
                            }.getType());
                            binding.rvYears.setVisibility(View.VISIBLE);
                            binding.tvNoavailable.setVisibility(View.GONE);
                            adapter = new YearsAdapter(years);
                            binding.rvYears.setAdapter(adapter);
                        }

                    }
                    binding.swipeContainer.setRefreshing(false);
                }
                if (loading != null && loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
            }

            @Override
            public void onFailure(Call<ArrayList<AdvJeeYearsObj>> call, Throwable t) {
                binding.swipeContainer.setRefreshing(false);
                if (loading != null && loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
            }
        });

    }

    private class YearsAdapter extends RecyclerView.Adapter<YearsAdapter.ViewHolder> {

        ArrayList<AdvJeeYearsObj> yearObj;

        public YearsAdapter(ArrayList<AdvJeeYearsObj> years) {
            yearObj = years;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_year_wise_advjee, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvYear.setText(yearObj.get(position).getYear());
            holder.tvPaper1Attempt.setText(yearObj.get(position).getP1Attemted()+"");
            holder.tvPaper2Attempt.setText(yearObj.get(position).getP2Attemted()+"");

            if (yearObj.get(position).getP1Attemted()==0){
                holder.itemView.findViewById(R.id.ll_accuracy_p1).setVisibility(View.GONE);
                ((TextView)holder.itemView.findViewById(R.id.tv_p1_att)).setText("No Of Attempts");
            }

            if (yearObj.get(position).getP2Attemted()==0){
                holder.itemView.findViewById(R.id.ll_accuracy_p2).setVisibility(View.GONE);
                ((TextView)holder.itemView.findViewById(R.id.tv_p2_att)).setText("No Of Attempts");
            }


//            holder.tvProg1.setText(MessageFormat.format("{0}%", Math.round(yearObj.get(position).getPercentagep1())));
            holder.tvAccuracyP1.setText(yearObj.get(position).getPercentagep1()+"");
            holder.tvAccuracyP2.setText(yearObj.get(position).getPercentagep2()+"");
//            holder.tvProg2.setText(MessageFormat.format("{0}%", Math.round(yearObj.get(position).getPercentagep2())));

            if (yearObj.get(position).getP2Total() == 0) {
                holder.llPaper2.setVisibility(View.INVISIBLE);
            }
            holder.llPaper1.setOnClickListener(view -> {
                selectedPaper = 1;
                selectedyear = position;
                Intent intent = new Intent(AdvJeeMainActivity.this, AdvJeeSubjectsActivity.class);
                intent.putExtra("from", "paper1");
                intent.putExtra("year", yearObj.get(position).getYear());
                intent.putExtra("yearid", yearObj.get(position).get_id());
                intent.putExtra("subOrder", (Serializable) yearObj.get(position).getDisplayOrder().get(0).getSubjects());
                startActivityForResult(intent, 1234);
            });
            holder.llPaper2.setOnClickListener(view -> {
                selectedPaper = 2;
                selectedyear = position;
                Intent intent = new Intent(AdvJeeMainActivity.this,AdvJeeSubjectsActivity.class);
                intent.putExtra("from", "paper2");
                intent.putExtra("year", yearObj.get(position).getYear());
                intent.putExtra("yearid", yearObj.get(position).get_id());
                intent.putExtra("subOrder", (Serializable) yearObj.get(position).getDisplayOrder().get(1).getSubjects());
                startActivityForResult(intent, 1234);
            });

        }

        @Override
        public int getItemCount() {
            return yearObj.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            //            LinearLayout llPaper1, llPaper2;
//            TextView tvProg1, tvProg2, tvYear, tvPaper;
//            ProgressBar pbProg1, pbProg2;
//            CircularProgressBar cpb;
            TextView tvYear, tvPaper1, tvPaper2,tvPaper1Attempt,tvPaper2Attempt,tvAccuracyP1,tvAccuracyP2;
            LinearLayout llPaper1, llPaper2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
//                tvPaper = itemView.findViewById(R.id.tv_paper1);
//                llPaper1 = itemView.findViewById(R.id.ll_paper1);
//                llPaper2 = itemView.findViewById(R.id.ll_paper2);
//                tvProg1 = itemView.findViewById(R.id.prog);
//                tvProg2 = itemView.findViewById(R.id.prog2);
//                pbProg1 = itemView.findViewById(R.id.pb_question);
//                pbProg2 = itemView.findViewById(R.id.pb_question2);
//                tvYear = itemView.findViewById(R.id.tv_year);
//                cpb = itemView.findViewById(R.id.circularProgress);


                tvYear = itemView.findViewById(R.id.tv_year);
                tvPaper1 = itemView.findViewById(R.id.tv_paper1);
                tvPaper2 = itemView.findViewById(R.id.tv_paper2);
                tvPaper1Attempt = itemView.findViewById(R.id.tv_noof_attempts_p1);
                tvPaper2Attempt = itemView.findViewById(R.id.tv_noof_attempts_p2);
                tvAccuracyP1 = itemView.findViewById(R.id.tv_accuracy_p1);
                tvAccuracyP2 = itemView.findViewById(R.id.tv_accuracy_p2);
                llPaper1 = itemView.findViewById(R.id.ll_paper1);
                llPaper2 = itemView.findViewById(R.id.ll_paper2);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && years.get(selectedyear) != null) {
            AdvJeeYearsObj sObj = years.get(selectedyear);
            float per = 0f;
            if (selectedPaper == 1) {
                sObj.setP1Attemted((sObj.getP1Attemted() + data.getIntExtra("attempted", 0)));
                if (sObj.getP1Attemted() > 0 && sObj.getP1Total() > 0)
                    per = (float) sObj.getP1Attemted() / sObj.getP1Total();
                sObj.setPercentagep1(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", (per * 100))));
            } else if (selectedPaper == 2) {
                sObj.setP2Attemted((sObj.getP2Attemted() + data.getIntExtra("attempted", 0)));
                if (sObj.getP2Attemted() > 0 && sObj.getP2Total() > 0)
                    per = (float) sObj.getP2Attemted() / sObj.getP2Total();
                sObj.setPercentagep2(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", (per * 100))));
            }
            int totattemp = sObj.getP1Attemted() + sObj.getP2Attemted();
            sObj.setPracticeCount(totattemp);
            if (totattemp > 0 && sObj.getQuestionCount() > 0) {
                float totPer = (float) sObj.getPracticeCount() / sObj.getQuestionCount();
                sObj.setPercentage(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", (totPer * 100))));
            }

            toEdit.putString("years", new Gson().toJson(years));
            toEdit.commit();
            adapter.notifyDataSetChanged();
        }

//        if (data!=null && years.get(selectedyear)!=null) {
//            YearsObj sObj = years.get(selectedyear);
//            float per = 0f;
//            if (selectedPaper == 1) {
//                sObj.setP1Attemted(data.getIntExtra("attempted", 0));
//                if (sObj.getP1Attemted()>0 && sObj.getP1Total()>0)
//                    per = (float) sObj.getP1Attemted() / sObj.getP1Total();
//                sObj.setPercentagep1(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", (per * 100))));
//            } else if (selectedPaper == 2) {
//                sObj.setP2Attemted(data.getIntExtra("attempted", 0));
//                if (sObj.getP2Attemted()>0 && sObj.getP2Total()>0)
//                    per = (float) sObj.getP2Attemted() / sObj.getP2Total();
//                sObj.setPercentagep2(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", (per * 100))));
//            }
//            int totattemp = sObj.getP1Attemted()+sObj.getP2Attemted();
//            sObj.setPracticeCount(totattemp);
//            if (totattemp>0 && sObj.getQuestionCount()>0){
//                float totPer = (float)sObj.getPracticeCount()/sObj.getQuestionCount();
//                sObj.setPercentage(Float.parseFloat(String.format(Locale.getDefault(), "%.2f", (totPer * 100))));
//            }
//
//            toEdit.putString("years", new Gson().toJson(years));
//            toEdit.commit();
//            adapter.notifyDataSetChanged();
//        }

    }
}