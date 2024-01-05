package myschoolapp.com.gsnedutech.JeeMains.files;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import myschoolapp.com.gsnedutech.JeeMains.Utils.ApiInterface;
import myschoolapp.com.gsnedutech.JeeMains.Utils.CustomWebview;
import myschoolapp.com.gsnedutech.JeeMains.Utils.JeeApiClient;
import myschoolapp.com.gsnedutech.JeeMains.models.Chapter;
import myschoolapp.com.gsnedutech.JeeMains.models.Question;
import myschoolapp.com.gsnedutech.JeeMains.models.RecentPractice;
import myschoolapp.com.gsnedutech.JeeMains.models.SubmitAnswer;
import myschoolapp.com.gsnedutech.JeeMains.models.UserObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.databinding.ActivityJeeChapterQuestionsBinding;
import myschoolapp.com.gsnedutech.databinding.JeeDialogExpectedAnswerBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JeeChapterQuestions extends AppCompatActivity {

    private ActivityJeeChapterQuestionsBinding binding;
    ApiInterface apiInterface;
    String subName, chapName;
    int drawable;
    int progress;
    Dialog loading;
    int test_question;
    private LottieAnimationView animationView;
    String contentmatrixId;
    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    UserObj uObj;
    Chapter chapter;

    QuestionsAdapter adapter;
    ArrayList<RecentPractice> recents ;

    ArrayList<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        Drawable background = this.getResources().getDrawable(R.drawable.gradient_theme, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent, null));
        window.setBackgroundDrawable(background);
        binding = ActivityJeeChapterQuestionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        sh_Pref = getSharedPreferences(getResources().getString(R.string.jee_sh_pref), MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("usrObj", "");
        uObj = gson.fromJson(json, UserObj.class);
        recents = new Gson().fromJson(sh_Pref.getString("recents",""),new TypeToken<ArrayList<RecentPractice>>(){}.getType());
        if (recents==null){
            recents = new ArrayList<>();
        }
        chapter = (Chapter) getIntent().getSerializableExtra("chapter");
        binding.imgSub.setImageDrawable(getDrawable(getIntent().getIntExtra("imgsub",0)));
        subName = getIntent().getStringExtra("subname");
        chapName = chapter.getChapName();
        drawable = getIntent().getIntExtra("drawable", R.drawable.jee_physics_gradient);
        progress = Math.round(chapter.getPercentage());
        contentmatrixId = getIntent().getStringExtra("contentmatrix");
        binding.tvSubName.setText(subName);
        binding.tvChapName.setText(chapName);
        binding.pbQuestion.setProgressDrawable(getDrawable(drawable));
        binding.pbQuestion.setProgress(progress);
        binding.prog.setText(progress + "%");
        binding.tvQutnCnt.setText("Attempted - "+chapter.getNoOfQuestionsAttempted()+"/"+chapter.getTotalNoOfQuestions());
        if (progress > 50) {
            binding.prog.setTextColor(Color.parseColor("#ffffff"));
        } else binding.prog.setTextColor(Color.parseColor("#000000"));
        binding.tvChaterIndc.setText("" + chapName.charAt(0));
        if (subName.equalsIgnoreCase("physics")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#EB22AF"));
        } else if (subName.equalsIgnoreCase("chemistry")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#FB7900"));
        } else if (subName.equalsIgnoreCase("botany")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#41EB22"));
        } else if (subName.equalsIgnoreCase("zoology")) {
            binding.tvChaterIndc.setTextColor(Color.parseColor("#FF0000"));
        }

        binding.rvChapterQue.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        binding.rvChapterQue.addItemDecoration(dividerItemDecoration);
        adapter = new QuestionsAdapter(questions);

        apiInterface = JeeApiClient.getClient().create(ApiInterface.class);


        binding.imgBack.setOnClickListener(view1 -> onBackPressed());

        refresh(true);
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (NetworkConnectivity.isConnected(JeeChapterQuestions.this)) {
                    refresh(false);
                } else {
                    new MyUtils().alertDialog(1, JeeChapterQuestions.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close),false);
                }
            }
        });
        // Configure the refreshing colors
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    void refresh(boolean isFirst){
        if(isFirst){
            showProgress();
        }
        if (NetworkConnectivity.isConnected(JeeChapterQuestions.this)) {
            getChapter(contentmatrixId);
            getChapterQuestions(chapter.get_id());
        } else {
            new MyUtils().alertDialog(1, JeeChapterQuestions.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close),false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (questions.size()>0){
//            refresh(false);
//        }else
//            refresh(true);
    }

    private void getChapter(String contentmatrixId) {
        Call<ArrayList<Chapter>> call = apiInterface.getChapter(contentmatrixId,getIntent().getStringExtra("uid"),chapter.get_id());
        call.enqueue(new Callback<ArrayList<Chapter>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Chapter>> call, @NotNull Response<ArrayList<Chapter>> response) {
                if (response.body() != null) {
                    ArrayList<Chapter> chapters = response.body();
                    if (chapters.size() > 0) {
                        chapter = chapters.get(0);
                        int prog = Math.round(chapter.getPercentage());
                        binding.pbQuestion.setProgress(prog);
                        binding.prog.setText(prog + "%");
                        binding.tvQutnCnt.setText("Attempted - "+chapter.getNoOfQuestionsAttempted()+"/"+chapter.getTotalNoOfQuestions());
                        if (prog > 50) {
                            binding.prog.setTextColor(Color.parseColor("#ffffff"));
                        } else binding.prog.setTextColor(Color.parseColor("#000000"));
//                        new Handler().postDelayed(() -> dismissDialog(), 1000);
                    }
                }
//                else new Handler().postDelayed(() -> dismissDialog(), 1000);
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<Chapter>> call, @NotNull Throwable t) {

            }
        });
    }

    private void getChapterQuestions(String id) {
        Call<ArrayList<Question>> call = apiInterface.getQuestions(contentmatrixId,getIntent().getStringExtra("uid"),id);
        call.enqueue(new Callback<ArrayList<Question>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Question>> call, @NotNull Response<ArrayList<Question>> response) {
                if (response.body() != null) {
                    questions = response.body();
                    binding.swipeContainer.setRefreshing(false);
                    if (questions.size() > 0) {
                        binding.rvChapterQue.setVisibility(View.VISIBLE);
                        binding.tvNoavailable.setVisibility(View.GONE);
                        adapter = new QuestionsAdapter(questions);
                        binding.rvChapterQue.setAdapter(adapter);
                        new Handler().postDelayed(() -> dismissDialog(), 1000);
                    } else {
                        binding.tvNoavailable.setText("No Questions are asked from this chapter");
                        binding.rvChapterQue.setVisibility(View.GONE);
                        binding.tvNoavailable.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> dismissDialog(), 1000);
                    }
                } else {
                    new Handler().postDelayed(() -> dismissDialog(), 1000);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<Question>> call, @NotNull Throwable t) {
                if (questions.size()<=0){
                    binding.rvChapterQue.setVisibility(View.GONE);
                    binding.tvNoavailable.setVisibility(View.VISIBLE);
                }
                binding.swipeContainer.setRefreshing(false);
                new Handler().postDelayed(() -> dismissDialog(), 1000);
            }
        });
    }


    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        ArrayList<Question> cQuestions;

        public QuestionsAdapter(ArrayList<Question> questions) {
            cQuestions = questions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(JeeChapterQuestions.this).inflate(R.layout.jee_item_chap_que, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if(cQuestions.get(position).getIsAttempted() == 1){
                holder.ll_attempts.setVisibility(View.VISIBLE);
                holder.tvNoOfAttempts.setText("No Of Attempts : "+cQuestions.get(position).getNoOfAttempts());
                holder.tvCorrect.setText(""+cQuestions.get(position).getNoOfRights());
                holder.tvWrong.setText(""+cQuestions.get(position).getNoOfWrongs());
            }
            else {
                holder.ll_attempts.setVisibility(View.GONE);
            }

            holder.wvQuestion.setText(cQuestions.get(position).getqName());
            holder.wvQuestion.setBackgroundColor(Color.TRANSPARENT);
            if (cQuestions.get(position).getqAppearace().size() > 0) {
                holder.tvQAppearance.setVisibility(View.VISIBLE);
                holder.tvQAppearance.setText(cQuestions.get(position).getqAppearace().get(0));
            } else holder.tvQAppearance.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(view -> {
                test_question = position;
                gotoTest(position);
            });

//            holder.wvQuestion.setOnTouchListener(new View.OnTouchListener() {
//                @SuppressLint("ClickableViewAccessibility")
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (v.getId() == R.id.wv_question && event.getAction() == MotionEvent.ACTION_DOWN){
//                        Intent i = new Intent(ChapterQuestions.this, Test.class);
//                        i.putExtra("question", questions.get(position));
//                        i.putExtra("subname", subName);
//                        i.putExtra("chapname", chapName);
//                        i.putExtra("drawable", drawable);
//                        i.putExtra("percentage", progress);
//                        startActivity(i);
//                    }
//                    return false;
//                }
//            });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1234 && resultCode==4321){
            if (data.getSerializableExtra("submit")!=null){
                SubmitAnswer ans = (SubmitAnswer) data.getSerializableExtra("submit");
                if (questions.get(test_question)!=null){
                    Question que = questions.get(test_question);
                    if (que.getIsAttempted()==0){
                        questions.get(test_question).setIsAttempted(1);
                        int tot = chapter.getNoOfQuestionsAttempted()+1;
                        if (tot <= chapter.getTotalNoOfQuestions() ){
                            float pr = (((float) (chapter.getNoOfQuestionsAttempted()+1)/ chapter.getTotalNoOfQuestions())*100);
                            chapter.setPercentage(pr);
                            chapter.setNoOfQuestionsAttempted(chapter.getNoOfQuestionsAttempted()+1);
                            chapter.setIsAttempted(1);
                            binding.pbQuestion.setProgress(Math.round(pr));
                            binding.prog.setText(Math.round(pr) + "%");
                            binding.tvQutnCnt.setText("Attempted - "+chapter.getNoOfQuestionsAttempted()+"/"+chapter.getTotalNoOfQuestions());
                            if (pr > 50) {
                                binding.prog.setTextColor(Color.parseColor("#ffffff"));
                            } else binding.prog.setTextColor(Color.parseColor("#000000"));
                        }
//                    float pr = (((float) (chapter.getNoOfQuestionsAttempted()+1)/ chapter.getTotalNoOfQuestions())*100);
//                    chapter.setNoOfQuestionsAttempted(chapter.getNoOfQuestionsAttempted()+1);
//                    chapter.setPercentage(pr);
//                    chapter.setIsAttempted(1);
//                    binding.pbQuestion.setProgress(Math.round(pr));
//                    binding.prog.setText(Math.round(pr) + "%");
//                    binding.tvQutnCnt.setText("Attempted - "+chapter.getNoOfQuestionsAttempted()+"/"+chapter.getTotalNoOfQuestions());
//                    if (pr > 50) {
//                        binding.prog.setTextColor(Color.parseColor("#ffffff"));
//                    } else binding.prog.setTextColor(Color.parseColor("#000000"));

                    }
                    questions.get(test_question).setNoOfAttempts(que.getNoOfAttempts()+1);
                    if (ans.isCorrect()){
                        questions.get(test_question).setNoOfRights(que.getNoOfRights()+1);
                    }
                    else questions.get(test_question).setNoOfWrongs(que.getNoOfWrongs()+1);

                    adapter.notifyDataSetChanged();
                }


            }
            if(data.getBooleanExtra("isNext",false)) {
                test_question = test_question + 1;
                gotoTest(test_question);
            }
        }
    }

    private void gotoTest(int position) {

        if(position<questions.size()) {
//            if(questions.get(position).getqName()==null || questions.get(position).getqName().isEmpty() ||
//                    questions.get(position).getqOptions()==null || questions.get(position).getqOptions().size()<4){
//                AlertDialog.Builder builder = new AlertDialog.Builder(ChapterQuestions.this);
//                DialogQuestionExceptionBinding dView = DialogQuestionExceptionBinding.inflate(getLayoutInflater());
//                builder.setView(dView.getRoot());
//                AlertDialog alertDialog = builder.create();
//                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
//                alertDialog.show();
//
//                dView.llNext.setOnClickListener(view -> {
//                    test_question = test_question +1;
//                    gotoTest(test_question);
//                    alertDialog.dismiss();
//                });
//
//                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        alertDialog.dismiss();
//                    }
//                });
//            }
//            else {
                Intent i = new Intent(JeeChapterQuestions.this, JeeTest.class);
                i.putExtra("question", questions.get(position));
                i.putExtra("qsize",questions.size());
                i.putExtra("qNo",position );
                i.putExtra("imgsub",getIntent().getIntExtra("imgsub",0));
                i.putExtra("subname", subName);
                i.putExtra("chapname", chapName);
                i.putExtra("drawable", drawable);
                i.putExtra("uid", getIntent().getStringExtra("uid"));
                i.putExtra("percentage", progress);
                startActivityForResult(i,1234);
//            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(JeeChapterQuestions.this);
            JeeDialogExpectedAnswerBinding dView = JeeDialogExpectedAnswerBinding.inflate(getLayoutInflater());
            builder.setView(dView.getRoot());
            AlertDialog alertDialog = builder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();
            dView.llExplanation.setVisibility(View.GONE);
            dView.tvWrongAnswer.setVisibility(View.GONE);
            dView.tvCorrctAnswer.setText("There is no Next Questions");
            dView.tvDone.setText("Done");
            dView.llNext.setOnClickListener(view -> {
                alertDialog.dismiss();
            });
        }

    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, JeeChaptersActivity.class);
        in.putExtra("chapter",chapter);
        setResult(4321,in);
        RecentPractice rp = new RecentPractice();
        rp.setChapter(chapter);
        rp.setSubject(subName);
        rp.setContentMatrixId(contentmatrixId);
        for (int i = 0; i<recents.size();i++){
            if (recents.get(i).getChapter().get_id().equalsIgnoreCase(chapter.get_id())){
                recents.remove(i);
            }
        }
        recents.add(rp);
        if (recents.size()>3){
            recents.remove(0);
        }
        toEdit.putString("recents",new Gson().toJson(recents));
        toEdit.commit();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    MyUtils utils = new MyUtils();
    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();
    }


}