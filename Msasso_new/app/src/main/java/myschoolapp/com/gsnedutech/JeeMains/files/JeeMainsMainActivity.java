package myschoolapp.com.gsnedutech.JeeMains.files;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.JeeMains.Utils.JeeApiClient;
import myschoolapp.com.gsnedutech.JeeMains.Utils.JeeConstants;
import myschoolapp.com.gsnedutech.JeeMains.models.RecentPractice;
import myschoolapp.com.gsnedutech.JeeMains.models.SubjectsObj;
import myschoolapp.com.gsnedutech.JeeMains.models.UserObj;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.databinding.JeeMainMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JeeMainsMainActivity extends AppCompatActivity implements View.OnClickListener {

    private JeeMainMainBinding binding;
    ApiInterface apiInterface;
    ArrayList<SubjectsObj> subjects = new ArrayList<>();
    HashMap<String, String> subs = new HashMap<>();
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    UserObj uObj;
    StudentObj sObj;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
        binding = JeeMainMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        apiInterface = JeeApiClient.getClient().create(ApiInterface.class);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.jee_sh_pref), MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        SharedPreferences shPref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shPref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        binding.tvUserName.setText("Welcome "+sObj.getStudentName().split(" ")[0]+"!");
        init();
        int randomNum = ThreadLocalRandom.current().nextInt(0, JeeConstants.quotes.length);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.tvQuotes.setText(JeeConstants.quotes[randomNum]);
        if (sh_Pref.getString("subjects","").equalsIgnoreCase("")){
            binding.llYear1.setVisibility(View.GONE);
            binding.llYear2.setVisibility(View.GONE);
            binding.tvNoSubjects.setVisibility(View.VISIBLE);
        }
        else {
            subs.clear();
            for (int i = 0; i < subjects.size(); i++) {
                for (int j = 0; j < subjects.get(i).getContentmatriceses().size(); j++) {
                    if (subjects.get(i).getContentmatriceses().get(j).isStatus()) {
                        if (subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName().equalsIgnoreCase("Class XI")) {
                            subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                        } else{
                            subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                        }

                    }
                }
            }
            setUpView();
        }
        refresh(true);

    }

    private void init() {
        
    }

    void refresh(boolean isFirst){
        if (NetworkConnectivity.isConnected(JeeMainsMainActivity.this)) {
            getSubjects(isFirst);
        } else {
            binding.swipeContainer.setRefreshing(false);
            new MyUtils().alertDialog(1, this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);

        }
    }

    private void getSubjects(boolean isFirst) {
        if (isFirst)
            showProgress();
//        Call<ArrayList<SubjectsObj>> call = apiInterface.getSubjects(uObj.get_id());
        Call<ArrayList<SubjectsObj>> call = apiInterface.getSubjects(sObj.getStudentId());
        call.enqueue(new Callback<ArrayList<SubjectsObj>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<SubjectsObj>> call, @NotNull Response<ArrayList<SubjectsObj>> response) {
                subjects = response.body();
                if (subjects!=null && subjects.size()>0) {
                    toEdit.putString("subjects", new Gson().toJson(subjects));
                    toEdit.commit();
                    subs.clear();
                    for (int i = 0; i < subjects.size(); i++) {
                        for (int j = 0; j < subjects.get(i).getContentmatriceses().size(); j++) {
                            if (subjects.get(i).getContentmatriceses().get(j).isStatus()) {
                                if (subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName().equalsIgnoreCase("Class XI")) {
                                    subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                                } else{
                                    subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                                }

                            }
                        }
                    }
                    setUpView();
                }
                else {
                    if (sh_Pref.getString("subjects","").equalsIgnoreCase("")){
                        binding.llYear1.setVisibility(View.GONE);
                        binding.llYear2.setVisibility(View.GONE);
                        binding.tvNoSubjects.setVisibility(View.VISIBLE);
                    }
                    else {
                        subs.clear();
                        for (int i = 0; i < subjects.size(); i++) {
                            for (int j = 0; j < subjects.get(i).getContentmatriceses().size(); j++) {
                                if (subjects.get(i).getContentmatriceses().get(j).isStatus()) {
                                    if (subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName().equalsIgnoreCase("Class XI")) {
                                        subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                                    } else{
                                        subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                                    }

                                }
                            }
                        }
                        setUpView();
                    }


                }
                binding.swipeContainer.setRefreshing(false);
                    new Handler().postDelayed(() -> dismissDialog(),500);
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<SubjectsObj>> call, @NotNull Throwable t) {
                binding.swipeContainer.setRefreshing(false);
                if (sh_Pref.getString("subjects","").equalsIgnoreCase("")){
                    binding.llYear1.setVisibility(View.GONE);
                    binding.llYear2.setVisibility(View.GONE);
                    binding.tvNoSubjects.setVisibility(View.VISIBLE);
                }
                else {
                    subs.clear();
                    for (int i = 0; i < subjects.size(); i++) {
                        for (int j = 0; j < subjects.get(i).getContentmatriceses().size(); j++) {
                            if (subjects.get(i).getContentmatriceses().get(j).isStatus()) {
                                if (subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName().equalsIgnoreCase("Class XI")) {
                                    subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                                } else{
                                    subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatriceses().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatriceses().get(j).get_id());
                                }

                            }
                        }
                    }
                    setUpView();
                }
                    new Handler().postDelayed(() -> dismissDialog(),500);
            }
        });
    }


    MyUtils utils = new MyUtils();
    private LottieAnimationView animationView;
    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();
    }

    private void setUpView() {
        binding.llYear1.setVisibility(View.VISIBLE);
        binding.llYear2.setVisibility(View.VISIBLE);
        binding.tvNoSubjects.setVisibility(View.GONE);
        binding.llMaths.setOnClickListener(this);
        binding.llChemistry.setOnClickListener(this);
        binding.llMaths2.setOnClickListener(this);
        binding.llChemistry2.setOnClickListener(this);
        binding.llPhysics.setOnClickListener(this);
        binding.llPhysics2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_maths:
                gotoChapter(subs.get("Maths Class XI"), R.drawable.ic_courses_maths,"Maths XI", R.drawable.jee_botany_gradient);
                break;
            case R.id.ll_maths2:
                gotoChapter(subs.get("Maths Class XII"), R.drawable.ic_courses_maths, "Maths XII", R.drawable.jee_botany_gradient);
                break;
            case R.id.ll_physics:
                gotoChapter(subs.get("Physics Class XI"), R.drawable.ic_courses_physics, "Physics XI", R.drawable.jee_physics_gradient);
                break;
            case R.id.ll_physics2:
                gotoChapter(subs.get("Physics Class XII"), R.drawable.ic_courses_physics, "Physics XII", R.drawable.jee_physics_gradient);
                break;
            case R.id.ll_chemistry:
                gotoChapter(subs.get("Chemistry Class XI"), R.drawable.ic_courses_chemistry, "Chemistry XI", R.drawable.jee_chemistry_gradient);
                break;
            case R.id.ll_chemistry2:
                gotoChapter(subs.get("Chemistry Class XII"), R.drawable.ic_courses_chemistry, "Chemistry XII", R.drawable.jee_chemistry_gradient);
                break;
        }
    }

    private void gotoChapter(String contentmatrixId, int imgsub, String subname, int drawavle) {
        Intent i = new Intent(JeeMainsMainActivity.this, JeeChaptersActivity.class);
        i.putExtra("subname",subname);
        i.putExtra("id",contentmatrixId);
        i.putExtra("imgsub", imgsub);
        i.putExtra("drawable", drawavle);
        i.putExtra("uid", sObj.getStudentId());
        startActivityForResult(i,1234);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateRecents();
    }

    private void populateRecents() {
        binding.rvRecent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        ArrayList<RecentPractice> rp = new Gson().fromJson(sh_Pref.getString("recents",""),new TypeToken<ArrayList<RecentPractice>>(){}.getType());
        if (rp!=null && rp.size()>0){
            binding.rvRecent.setVisibility(View.VISIBLE);
            binding.tvNoavailable.setVisibility(View.GONE);
            binding.rvRecent.setAdapter(new RecentAdapter(rp));
        }
        else {
            binding.tvNoavailable.setVisibility(View.VISIBLE);
            binding.rvRecent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

        ArrayList<RecentPractice> rPractices ;
        public RecentAdapter(ArrayList<RecentPractice> rp) {
            rPractices = rp;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(JeeMainsMainActivity.this).inflate(R.layout.jee_item_recent_practice, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (rPractices.get(position).getSubject().contains("Physics")){
                holder.tvSubName.setTextColor(getResources().getColor(R.color.jee_physics));
                holder.tvChapName.setTextColor(getResources().getColor(R.color.jee_physics));
            }
            else if (rPractices.get(position).getSubject().contains("Chemistry")){
                holder.tvSubName.setTextColor(getResources().getColor(R.color.jee_chemistry));
                holder.tvChapName.setTextColor(getResources().getColor(R.color.jee_chemistry));
            }
            else {
                holder.tvSubName.setTextColor(getResources().getColor(R.color.jee_maths));
                holder.tvChapName.setTextColor(getResources().getColor(R.color.jee_maths));
            }

            holder.tvSubName.setText(rPractices.get(position).getSubject());
            holder.tvChapName.setText(rPractices.get(position).getChapter().getChapDisplayName());
            holder.itemView.setOnClickListener(view -> {
                Intent i = new Intent(JeeMainsMainActivity.this,JeeChapterQuestions.class);
                i.putExtra("chapter",rPractices.get(position).getChapter());
                i.putExtra("subname",rPractices.get(position).getSubject());
                if (rPractices.get(position).getSubject().contains("Maths")){
                    i.putExtra("imgsub", R.drawable.ic_courses_maths);
                    i.putExtra("drawable", R.drawable.jee_botany_gradient);
                }
                else if (rPractices.get(position).getSubject().contains("Physics")){
                    i.putExtra("drawable", R.drawable.jee_physics_gradient);
                    i.putExtra("imgsub", R.drawable.ic_courses_physics);
                }
                else if (rPractices.get(position).getSubject().contains("Chemistry")){
                    i.putExtra("drawable", R.drawable.jee_chemistry_gradient);
                    i.putExtra("imgsub", R.drawable.ic_courses_chemistry);
                }
                i.putExtra("contentmatrix", rPractices.get(position).getContentMatrixId());
                startActivity(i);
            });

        }

        @Override
        public int getItemCount() {
            return rPractices.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvChapName, tvSubName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChapName = itemView.findViewById(R.id.tv_chap);
                tvSubName = itemView.findViewById(R.id.tv_sub);
            }
        }
    }



}