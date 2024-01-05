package myschoolapp.com.gsnedutech.JeeAdvanced.Files;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.CustomWebview;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeAllSubjects;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeQuestion;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubOrder;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubjectsObj;
import myschoolapp.com.gsnedutech.JeeAdvanced.Models.AdvJeeSubmitAnswer;
import myschoolapp.com.gsnedutech.databinding.ActivityAdvJeeSubjectsBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvJeeSubjectsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAdvJeeSubjectsBinding binding;

    ArrayList<AdvJeeQuestion> questions = new ArrayList<>();
    ArrayList<AdvJeeSubjectsObj> subjects = new ArrayList<>();
    ArrayList<AdvJeeAllSubjects> allSubjects = new ArrayList<>();
    String physicsId, mathsId, chemistryId;
    String physics, maths, chemistry;
    ArrayList<AdvJeeSubOrder> subOrder = new ArrayList<>();

    HashMap<String, ArrayList<AdvJeeQuestion>> progress = new HashMap<>();

    ApiInterface apiInterface;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
//    UserObj uObj;
    int selectedPos = 0;
    String paperId;
    String yearId;
    int test_question;
    String subs, from, year;
    String subId;
    int attempted = 0;
    public static AdvJeeQuestion quesString;

    SharedPreferences shPref;
    StudentObj sObj;

    QuestionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdvJeeSubjectsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        sh_Pref = getSharedPreferences(getResources().getString(R.string.adv_sh_pref), MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        toEdit.apply();

        init();
//        refresh();
        subId = String.valueOf(binding.tvSub1.getTag());
        refresh(subId);

    }

    void init() {
        Gson gson = new Gson();
        String json = sh_Pref.getString("usrObj", "");
        subs = sh_Pref.getString("subjects", "");
        if (subs != null && !subs.equalsIgnoreCase("")) {
            subjects = gson.fromJson(subs, new TypeToken<ArrayList<AdvJeeSubjectsObj>>() {
            }.getType());
//            setSubjectId();
        }
        if (getIntent() != null && getIntent().getSerializableExtra("subOrder") != null) {
            subOrder = (ArrayList<AdvJeeSubOrder>) getIntent().getSerializableExtra("subOrder");
        }
        if (subOrder.size() > 0) {
            binding.tvSub1.setText(subOrder.get(0).getsName());
            binding.tvSub1.setTag(subOrder.get(0).getsId());
            binding.tvSub2.setText(subOrder.get(1).getsName());
            binding.tvSub2.setTag(subOrder.get(1).getsId());
            binding.tvSub3.setText(subOrder.get(2).getsName());
            binding.tvSub3.setTag(subOrder.get(2).getsId());
        }

//        uObj = gson.fromJson(json, UserObj.class);
        apiInterface = ApiClient.getAdvJeeClient().create(ApiInterface.class);
        yearId = getIntent().getStringExtra("yearid");
        from = getIntent().getStringExtra("from");
        year = getIntent().getStringExtra("year");
        if (from != null && from.equalsIgnoreCase("paper1")) {
            paperId = "paper I";
//            getWindow().setStatusBarColor(ContextCompat.getColor(AdvJeeSubjectsActivity.this, R.color.paper1));
//            binding.llMain.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_paper_one));
            binding.tvPaperNo.setText(MessageFormat.format("{0} Paper-1", year));
        } else {
            paperId = "paper II";
            binding.llMain.setBackground(ContextCompat.getDrawable(this, R.drawable.advjee_bg_paper_two));
            binding.tvPaperNo.setText(MessageFormat.format("{0} Paper-2", year));
        }

        binding.tvSub1.setOnClickListener(this);
        binding.tvSub2.setOnClickListener(this);
        binding.tvSub3.setOnClickListener(this);
        binding.imgBack.setOnClickListener(view1 -> onBackPressed());
        binding.rvChapterQue.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        binding.rvChapterQue.addItemDecoration(dividerItemDecoration);
        adapter = new QuestionsAdapter(questions);

        binding.swipeContainer.setOnRefreshListener(() -> refresh(subId));
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        shPref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        gson = new Gson();
        json = shPref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
    }

    void refresh(String subId) {
        if (NetworkConnectivity.isConnected(AdvJeeSubjectsActivity.this)) {
            getQuestions(yearId, paperId, subId, sObj.getStudentId());
        } else {
            binding.swipeContainer.setRefreshing(false);
            new MyUtils().alertDialog(1, AdvJeeSubjectsActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }


    }

//    void refresh(){
//        if (NetworkConnectivity.isConnected(AdvJeeSubjectsActivity.this)) {
//            getAllSubjectQuestions(yearId,paperId,uObj.get_id());
//        } else {
//            binding.swipeContainer.setRefreshing(false);
//            new Util().alertDialog(1, AdvJeeSubjectsActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
//                    getString(R.string.action_settings), getString(R.string.action_close));
//        }
//    }

    Dialog loading;
    private LottieAnimationView animationView;

    public void showProgress() {
        if (loading != null && loading.isShowing()) loading.dismiss();
        loading = new Dialog(AdvJeeSubjectsActivity.this);
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

    private void getQuestions(String yearId, String paperId, String subjectId, String id) {
        showProgress();
        Call<ArrayList<AdvJeeQuestion>> call = apiInterface.advjeeFetQuestions(yearId, paperId, subjectId, id);
        call.enqueue(new Callback<ArrayList<AdvJeeQuestion>>() {
            @Override
            public void onResponse(Call<ArrayList<AdvJeeQuestion>> call, Response<ArrayList<AdvJeeQuestion>> response) {
                if (response.body() != null) {
                    questions = response.body();
                    binding.swipeContainer.setRefreshing(false);
                    refreshViews1();
                }
                binding.swipeContainer.setRefreshing(false);
                if (loading != null && loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
            }

            @Override
            public void onFailure(Call<ArrayList<AdvJeeQuestion>> call, Throwable t) {
                binding.swipeContainer.setRefreshing(false);
                if (loading != null && loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
                t.printStackTrace();
            }
        });

    }

    private void getAllSubjectQuestions(String yearId, String paperId, String id) {
        showProgress();
        Call<ArrayList<AdvJeeAllSubjects>> call = apiInterface.advjeeGetAllSubjects(yearId, paperId, id);
        call.enqueue(new Callback<ArrayList<AdvJeeAllSubjects>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<AdvJeeAllSubjects>> call, @NotNull Response<ArrayList<AdvJeeAllSubjects>> response) {
                if (response.body() != null) {
                    allSubjects = response.body();
                    binding.swipeContainer.setRefreshing(false);
                    if (allSubjects.size() > 0) {
                        binding.rvChapterQue.setVisibility(View.VISIBLE);
                        binding.tvNoavailable.setVisibility(View.GONE);
                        for (AdvJeeAllSubjects as : allSubjects) {
                            progress.put(as.getSubjectName(), as.getQueresult());
                        }
                        refreshViews();
                    } else {
                        binding.rvChapterQue.setVisibility(View.GONE);
                        binding.tvNoavailable.setVisibility(View.VISIBLE);

                    }
                }
                binding.swipeContainer.setRefreshing(false);
                if (loading != null && loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<AdvJeeAllSubjects>> call, @NotNull Throwable t) {
                binding.swipeContainer.setRefreshing(false);
                if (loading != null && loading.isShowing())
                    new Handler().postDelayed(() -> dismissDialog(), 500);
                t.printStackTrace();
            }
        });
    }

    private void refreshViews1() {
        setUnFocusAll();
        if (selectedPos == 0) {
//            questions = progress.get(maths);
            binding.tvSub1.setBackground(ContextCompat.getDrawable(AdvJeeSubjectsActivity.this, R.drawable.rounded_corners_sub_select));
            binding.tvSub1.setTextColor(ContextCompat.getColor(AdvJeeSubjectsActivity.this, R.color.white));
            binding.tvSub1.setTextSize(14.0f);
        } else if (selectedPos == 1) {
//            questions = progress.get(physics);
            binding.tvSub2.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_select));
            binding.tvSub2.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.tvSub2.setTextSize(14.0f);
        } else if (selectedPos == 2) {
//            questions = progress.get(chemistry);
            binding.tvSub3.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_select));
            binding.tvSub3.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.tvSub3.setTextSize(14.0f);
        }

        if (questions.size() > 0) {
            binding.rvChapterQue.setVisibility(View.VISIBLE);
            binding.tvNoavailable.setVisibility(View.GONE);
            adapter = new QuestionsAdapter(questions);
            binding.rvChapterQue.setAdapter(adapter);
        } else {
            binding.rvChapterQue.setVisibility(View.GONE);
            binding.tvNoavailable.setVisibility(View.VISIBLE);
            binding.tvNoavailable.setText(MessageFormat.format("{0}", "Questions Will be Updated Soon"));
        }

    }

    private void refreshViews() {
        setUnFocusAll();
        if (selectedPos == 0) {
            questions = progress.get(maths);
            binding.tvSub1.setBackground(ContextCompat.getDrawable(AdvJeeSubjectsActivity.this, R.drawable.rounded_corners_sub_select));
            binding.tvSub1.setTextColor(ContextCompat.getColor(AdvJeeSubjectsActivity.this, R.color.white));
            binding.tvSub1.setTextSize(14.0f);
        } else if (selectedPos == 1) {
            questions = progress.get(physics);
            binding.tvSub2.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_select));
            binding.tvSub2.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.tvSub2.setTextSize(14.0f);
        } else if (selectedPos == 2) {
            questions = progress.get(chemistry);
            binding.tvSub3.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_select));
            binding.tvSub3.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.tvSub3.setTextSize(14.0f);
        }

        if (questions.size() > 0) {
            binding.rvChapterQue.setVisibility(View.VISIBLE);
            binding.tvNoavailable.setVisibility(View.GONE);
            adapter = new QuestionsAdapter(questions);
            binding.rvChapterQue.setAdapter(adapter);
        } else {
            binding.rvChapterQue.setVisibility(View.GONE);
            binding.tvNoavailable.setVisibility(View.VISIBLE);
            binding.tvNoavailable.setText(MessageFormat.format("{0}", "Questions Will be Updated Soon"));
        }

    }

    private void setSubjectId() {
        for (AdvJeeSubjectsObj sobj : subjects) {
            if (sobj.getSubjectName().equalsIgnoreCase("Physics")) {
                physics = sobj.getSubjectName();
                physicsId = sobj.get_id();
            }
            if (sobj.getSubjectName().equalsIgnoreCase("Mathematics")) {
                maths = sobj.getSubjectName();
                mathsId = sobj.get_id();
            }
            if (sobj.getSubjectName().equalsIgnoreCase("Chemistry")) {
                chemistry = sobj.getSubjectName();
                chemistryId = sobj.get_id();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sub1:
                if (selectedPos != 0) {
                    selectedPos = 0;
                    subId = String.valueOf(view.getTag());
                    refresh(subId);
//                    refreshViews();
                }
                break;
            case R.id.tv_sub2:
                if (selectedPos != 1) {
                    selectedPos = 1;
                    subId = String.valueOf(view.getTag());
                    refresh(subId);
//                    refreshViews();
                }
                break;
            case R.id.tv_sub3:
                if (selectedPos != 2) {
                    selectedPos = 2;
                    subId = String.valueOf(view.getTag());
                    refresh(subId);
//                    refreshViews();
                }
                break;
        }
    }

    private void setUnFocusAll() {
        binding.tvSub1.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_unsel));
        binding.tvSub1.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvSub1.setTextSize(12.0f);
        binding.tvSub2.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_unsel));
        binding.tvSub2.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvSub2.setTextSize(12.0f);
        binding.tvSub3.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corners_sub_unsel));
        binding.tvSub3.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.tvSub3.setTextSize(12.0f);

    }


    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        ArrayList<AdvJeeQuestion> cQuestions;

        public QuestionsAdapter(ArrayList<AdvJeeQuestion> questions) {
            cQuestions = questions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(AdvJeeSubjectsActivity.this).inflate(R.layout.item_adv_jee_chap_que, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if (cQuestions.get(position).getIsAttempted() == 1) {
                holder.ll_attempts.setVisibility(View.VISIBLE);
                holder.tvNoOfAttempts.setText(MessageFormat.format("No Of Attempts : {0}", cQuestions.get(position).getNoOfAttempts()));
                holder.tvCorrect.setText(MessageFormat.format("{0}", cQuestions.get(position).getNoOfRights()));
                holder.tvWrong.setText(MessageFormat.format("{0}", cQuestions.get(position).getNoOfWrongs()));
            } else {
                holder.ll_attempts.setVisibility(View.GONE);
            }

            holder.wvQuestion.setText(cQuestions.get(position).getQuestion());
            holder.wvQuestion.setBackgroundColor(Color.TRANSPARENT);
            holder.tvQAppearance.setText("QNo:" + cQuestions.get(position).getqDisplayOrder() + " " + cQuestions.get(position).getQuestionTypeCode());


            holder.itemView.setOnClickListener(view -> {
                test_question = position;
                gotoTest(position);
            });

        }

        @Override
        public int getItemCount() {
            return cQuestions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQAppearance, tvNoOfAttempts, tvCorrect, tvWrong;
            LinearLayout ll_attempts;
            CustomWebview wvQuestion;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvQAppearance = itemView.findViewById(R.id.tv_qappearance);
                wvQuestion = itemView.findViewById(R.id.wv_question);
                ll_attempts = itemView.findViewById(R.id.ll_attempts);
                tvNoOfAttempts = itemView.findViewById(R.id.tv_noof_attempts);
                tvCorrect = itemView.findViewById(R.id.tv_noof_correct);
                tvWrong = itemView.findViewById(R.id.tv_noof_wrong);
            }
        }
    }


    private void gotoTest(int position) {

        if (position < questions.size()) {
            Intent i = new Intent(AdvJeeSubjectsActivity.this, AdvJeeTest.class);
            quesString = questions.get(position);
//            i.putExtra("question", questions.get(position));
            i.putExtra("qsize", questions.size());
            i.putExtra("qNo", position);
            i.putExtra("papertype", paperId);
            if (questions.get(position).getSubjectName().equalsIgnoreCase("Physics")) {
                i.putExtra("imgsub", R.drawable.ic_courses_physics);
            }
            if (questions.get(position).getSubjectName().equalsIgnoreCase("Mathematics")) {
                i.putExtra("imgsub", R.drawable.ic_courses_maths);
            }
            if (questions.get(position).getSubjectName().equalsIgnoreCase("Chemistry")) {
                i.putExtra("imgsub", R.drawable.ic_courses_chemistry);
            }
            i.putExtra("subname", questions.get(position).getSubjectName());
            i.putExtra("chapname", questions.get(position).getChapterCode());
            i.putExtra("drawable", R.drawable.jee_physics_gradient);
            i.putExtra("percentage", 0);
            startActivityForResult(i, 1234);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == 4321) {
            if (data != null && data.getSerializableExtra("submit") != null) {
                AdvJeeSubmitAnswer ans = (AdvJeeSubmitAnswer) data.getSerializableExtra("submit");
                if (questions.get(test_question) != null) {
                    AdvJeeQuestion que = questions.get(test_question);
                    if (que.getIsAttempted() == 0) {
                        attempted = attempted + 1;
                        questions.get(test_question).setIsAttempted(1);
                        progress.put(questions.get(test_question).getSubjectName(), questions);
                    }
                    questions.get(test_question).setNoOfAttempts(que.getNoOfAttempts() + 1);
                    if (ans != null && ans.isCorrect()) {
                        questions.get(test_question).setNoOfRights(que.getNoOfRights() + 1);
                    } else questions.get(test_question).setNoOfWrongs(que.getNoOfWrongs() + 1);

                    adapter.notifyDataSetChanged();
                }


            }
            if (data != null && data.getBooleanExtra("isNext", false)) {
                test_question = test_question + 1;
                gotoTest(test_question);
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, AdvJeeMainActivity.class);
        in.putExtra("attempted", attempted);
        setResult(4321, in);
        finish();
    }

    private int calculateTotalAttempted() {
        int attempt = 0;
        String[] subs = {maths, physics, chemistry};
        for (String sub : subs) {
            ArrayList<AdvJeeQuestion> arrayList = progress.get(sub);
            if (arrayList != null && arrayList.size() > 0) {
                for (AdvJeeQuestion question : arrayList) {
                    if (question.getIsAttempted() == 1) {
                        attempt = attempt + 1;
                    }
                }
            }
        }
        return attempt;
    }


}