package myschoolapp.com.gsnedutech.Neet;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiClient;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiInterface;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetNetworkConnectivity;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetUtil;
import myschoolapp.com.gsnedutech.Neet.models.NeetSubjectsObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityNeetSubjectsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NeetSubjectsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityNeetSubjectsBinding binding;
    NeetApiInterface neetApiInterface;
    ArrayList<NeetSubjectsObj> subjects = new ArrayList<>();

    HashMap<String, String> subs = new HashMap<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    Dialog loading;
    private LottieAnimationView animationView;

    MyUtils utils = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNeetSubjectsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        neetApiInterface = NeetApiClient.getClient().create(NeetApiInterface.class);
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(this));

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        JSONObject obj = null;
        try {
            obj = new JSONObject(sh_Pref.getString("studentObj", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sObj = gson.fromJson(obj.toString(), StudentObj.class);

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

    @Override
    protected void onResume() {
        super.onResume();
        if (subjects.size()>0){
            refresh(false);
        }else
            refresh(true);
    }

    void refresh(boolean isFirst){
        if (NeetNetworkConnectivity.isConnected(NeetSubjectsActivity.this)) {
            getSubjects(isFirst);
        } else {
            new NeetUtil().alertDialog(1, NeetSubjectsActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close));
        }
    }
    private void getSubjects(boolean isFirst) {
        if (isFirst)
            showProgress();
        Call<ArrayList<NeetSubjectsObj>> call = neetApiInterface.getSubjects(sObj.getStudentId());
        call.enqueue(new Callback<ArrayList<NeetSubjectsObj>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<NeetSubjectsObj>> call, @NotNull Response<ArrayList<NeetSubjectsObj>> response) {
                if(response.body()!=null){
                    subjects=response.body();
                    subs.clear();
                    for (int i = 0; i < subjects.size(); i++) {
                        for (int j = 0; j < subjects.get(i).getContentmatricese().size(); j++) {
                            if (subjects.get(i).getContentmatricese().get(j).isStatus()) {
                                if (subjects.get(i).getContentmatricese().get(j).getClas().get(0).getDisplayName().equalsIgnoreCase("Class XI")) {
                                    subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatricese().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatricese().get(j).get_id());
                                } else{
                                    subs.put(subjects.get(i).getSubjectName()+" "+subjects.get(i).getContentmatricese().get(j).getClas().get(0).getDisplayName(),subjects.get(i).getContentmatricese().get(j).get_id());
                                }

                            }
                        }
                    }
                    setUpView();
                    if(subjects.size()>0){
                        setUpView();
//                        binding.rvSubjects.setVisibility(View.VISIBLE);
//                        binding.tvNoavailable.setVisibility(View.GONE);
//                        binding.rvSubjects.setAdapter(new SubjectAdapter(subjects));
                    }
                    else {
                        binding.llYear1.setVisibility(View.GONE);
                        binding.llYear2.setVisibility(View.GONE);
                        binding.tvNoSubjects.setVisibility(View.VISIBLE);
//                        binding.tvNoavailable.setVisibility(View.VISIBLE);
//                        binding.rvSubjects.setVisibility(View.GONE);
                    }
                    binding.swipeContainer.setRefreshing(false);
//                    if (loading.isShowing())
//                        new Handler().postDelayed(() -> dismissDialog(),500);
                    utils.dismissDialog();
                }
                else{
//                    if (loading.isShowing())
//                        new Handler().postDelayed(() -> dismissDialog(),500);
                    utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<NeetSubjectsObj>> call, @NotNull Throwable t) {
                if(subjects.size()<=0) {
//                    binding.tvNoavailable.setVisibility(View.VISIBLE);
//                    binding.rvSubjects.setVisibility(View.GONE);
                }
                binding.swipeContainer.setRefreshing(false);
                if (loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
            }
        });
    }


    private void setUpView() {
        binding.llYear1.setVisibility(View.VISIBLE);
        binding.llYear2.setVisibility(View.VISIBLE);
        binding.tvNoSubjects.setVisibility(View.GONE);
        binding.llBotany.setOnClickListener(this);
        binding.llChemistry.setOnClickListener(this);
        binding.llBotany2.setOnClickListener(this);
        binding.llChemistry2.setOnClickListener(this);
        binding.llPhysics.setOnClickListener(this);
        binding.llPhysics2.setOnClickListener(this);
        binding.llZoology.setOnClickListener(this);
        binding.llZoology2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_botany:
                gotoChapter(subs.get("Botany Class XI"), R.drawable.ic_courses_biology,"Botany XI", R.drawable.neet_botany_gradient);
                break;
            case R.id.ll_botany2:
                gotoChapter(subs.get("Botany Class XII"), R.drawable.ic_courses_biology, "Botany XII", R.drawable.neet_botany_gradient);
                break;
            case R.id.ll_physics:
                gotoChapter(subs.get("Physics Class XI"), R.drawable.ic_courses_physics, "Physics XI", R.drawable.neet_physics_gradient);
                break;
            case R.id.ll_physics2:
                gotoChapter(subs.get("Physics Class XII"), R.drawable.ic_courses_physics, "Physics XII", R.drawable.neet_physics_gradient);
                break;
            case R.id.ll_chemistry:
                gotoChapter(subs.get("Chemistry Class XI"), R.drawable.ic_courses_chemistry, "Chemistry XI", R.drawable.neet_chemistry_gradient);
                break;
            case R.id.ll_chemistry2:
                gotoChapter(subs.get("Chemistry Class XII"), R.drawable.ic_courses_chemistry, "Chemistry XII", R.drawable.neet_chemistry_gradient);
                break;
            case R.id.ll_zoology:
                gotoChapter(subs.get("Zoology Class XI"), R.drawable.ic_courses_zoology, "Zoology XI", R.drawable.neet_zoology_gradient);
                break;
            case R.id.ll_zoology2:
                gotoChapter(subs.get("Zoology Class XII"), R.drawable.ic_courses_zoology, "Zoology XII", R.drawable.neet_zoology_gradient);
                break;
        }
    }


    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

        ArrayList<NeetSubjectsObj> subjects ;
        public SubjectAdapter(ArrayList<NeetSubjectsObj> neetSubjectsObjs) {
            subjects = neetSubjectsObjs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(NeetSubjectsActivity.this).inflate(R.layout.item_neet_subjects, parent, false));
        }
        public void clear() {
            subjects.clear();
            notifyDataSetChanged();
        }

        // Add a list of items -- change to type used
        public void addAll(ArrayList<NeetSubjectsObj> list) {
            subjects.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String clasxiId = "", classxiiId = "";
            holder.subName.setText(subjects.get(position).getSubjectName());
            int progress1 = 0, progress2 = 0;
            if (subjects.get(position).getContentmatricese()!=null && subjects.get(position).getContentmatricese().size()>0) {
                for (int i = 0; i < subjects.get(position).getContentmatricese().size(); i++) {
                    if (subjects.get(position).getContentmatricese().get(i).isStatus()) {
                        if (subjects.get(position).getContentmatricese().get(i).getClas().get(0).getDisplayName().equalsIgnoreCase("Class XI")) {
                            holder.tvSubClsName1.setText(subjects.get(position).getContentmatricese().get(i).getClas().get(0).getDisplayName());
                            clasxiId = subjects.get(position).getContentmatricese().get(i).get_id();
                            progress1 = Math.round(subjects.get(position).getContentmatricese().get(i).getPercentage());
                            holder.pbFirstYear.setProgress(progress1);

                        } else{
                            holder.tvSubClsName2.setText(subjects.get(position).getContentmatricese().get(i).getClas().get(0).getDisplayName());
                            classxiiId = subjects.get(position).getContentmatricese().get(i).get_id();
                            progress2 = Math.round(subjects.get(position).getContentmatricese().get(i).getPercentage());
                            holder.pbSecondYear.setProgress(progress2);
                        }

                    }
                }
            }
            if(subjects.get(position).getSubjectName().equalsIgnoreCase("physics")){
                holder.imgSubject.setImageDrawable(getDrawable(R.drawable.ic_courses_physics));
                holder.pbFirstYear.setProgressDrawable(getDrawable(R.drawable.neet_physics_gradient));
                holder.pbSecondYear.setProgressDrawable(getDrawable(R.drawable.neet_physics_gradient));
            }else if(subjects.get(position).getSubjectName().equalsIgnoreCase("chemistry")){
                holder.imgSubject.setImageDrawable(getDrawable(R.drawable.ic_courses_chemistry));
                holder.pbFirstYear.setProgressDrawable(getDrawable(R.drawable.neet_chemistry_gradient));
                holder.pbSecondYear.setProgressDrawable(getDrawable(R.drawable.neet_chemistry_gradient));
            }else if(subjects.get(position).getSubjectName().equalsIgnoreCase("botany")){
                holder.imgSubject.setImageDrawable(getDrawable(R.drawable.ic_courses_biology));
                holder.pbFirstYear.setProgressDrawable(getDrawable(R.drawable.neet_botany_gradient));
                holder.pbSecondYear.setProgressDrawable(getDrawable(R.drawable.neet_botany_gradient));
            }else if(subjects.get(position).getSubjectName().equalsIgnoreCase("zoology")){
                holder.imgSubject.setImageDrawable(getDrawable(R.drawable.ic_courses_zoology));
                holder.pbFirstYear.setProgressDrawable(getDrawable(R.drawable.neet_zoology_gradient));
                holder.pbSecondYear.setProgressDrawable(getDrawable(R.drawable.neet_zoology_gradient));
            }
            holder.tvProg1.setText(Math.round(progress1)+"%");
            if (progress1>50){
                holder.tvProg1.setTextColor(Color.parseColor("#ffffff"));
            }
            else holder.tvProg1.setTextColor(Color.parseColor("#000000"));
            holder.tvProg2.setText(Math.round(progress2)+"%");
            if (progress2>50){
                holder.tvProg2.setTextColor(Color.parseColor("#ffffff"));
            }
            else holder.tvProg2.setTextColor(Color.parseColor("#000000"));
            String finalClasxiId = clasxiId;
            holder.llClassXI.setOnClickListener(view -> {
                gotoChapters(finalClasxiId,position,"XI");
            });
            String finalClassxiiId = classxiiId;
            holder.llClassXII.setOnClickListener(view -> {
                gotoChapters(finalClassxiiId,position,"XII");
            });
        }


        @Override
        public int getItemCount() {
            return subjects.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView subName, tvSubClsName1, tvSubClsName2, tvProg1, tvProg2;
            ProgressBar pbFirstYear, pbSecondYear;
            LinearLayout llClassXI, llClassXII;
            ImageView imgSubject;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                subName = itemView.findViewById(R.id.tv_sub_name);
                tvSubClsName1 = itemView.findViewById(R.id.tv_sub_class_name1);
                tvSubClsName2 = itemView.findViewById(R.id.tv_sub_class_name2);
                pbFirstYear = itemView.findViewById(R.id.pb_first_year);
                pbSecondYear = itemView.findViewById(R.id.pb_second_year);
                llClassXI = itemView.findViewById(R.id.ll_classXI);
                llClassXII = itemView.findViewById(R.id.ll_classXII);
                tvProg1 = itemView.findViewById(R.id.prog1);
                tvProg2 = itemView.findViewById(R.id.prog2);
                imgSubject = itemView.findViewById(R.id.img_sub);
            }
        }
    }

    private void gotoChapter(String contentmatrixId, int imgsub, String subname, int drawavle) {
        Intent i = new Intent(NeetSubjectsActivity.this, NeetChaptersActivity.class);
        i.putExtra("subname",subname);
        i.putExtra("id",contentmatrixId);
        i.putExtra("uId",sObj.getStudentId());
        i.putExtra("drawable", drawavle);
        startActivity(i);
    }

    private void gotoChapters(String clasxiId, int position, String year) {
        Intent i = new Intent(NeetSubjectsActivity.this, NeetChaptersActivity.class);
        i.putExtra("subname",subjects.get(position).getSubjectName()+" "+year);
        i.putExtra("id",clasxiId);
        i.putExtra("uId",sObj.getStudentId());
        if(subjects.get(position).getSubjectName().equalsIgnoreCase("physics")){
            i.putExtra("drawable", R.drawable.neet_physics_gradient);
        }else if(subjects.get(position).getSubjectName().equalsIgnoreCase("chemistry")){
            i.putExtra("drawable", R.drawable.neet_chemistry_gradient);
        }else if(subjects.get(position).getSubjectName().equalsIgnoreCase("botany")){
            i.putExtra("drawable", R.drawable.neet_botany_gradient);
        }else if(subjects.get(position).getSubjectName().equalsIgnoreCase("zoology")){
            i.putExtra("drawable", R.drawable.neet_zoology_gradient);
        }
        startActivity(i);
    }

    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();

    }
}