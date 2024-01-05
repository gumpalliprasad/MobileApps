package myschoolapp.com.gsnedutech.Neet;

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
import androidx.appcompat.app.AlertDialog;
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
import java.util.Objects;

import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiClient;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetApiInterface;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetCustomWebview;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetNetworkConnectivity;
import myschoolapp.com.gsnedutech.Neet.Utils.NeetUtil;
import myschoolapp.com.gsnedutech.Neet.models.NeetChapter;
import myschoolapp.com.gsnedutech.Neet.models.NeetQuestion;
import myschoolapp.com.gsnedutech.Neet.models.NeetSubmitAnswer;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.databinding.ActivityNeetChapterQuestionsBinding;
import myschoolapp.com.gsnedutech.databinding.NeetDialogExpectedAnswerBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NeetChapterQuestions extends AppCompatActivity {

    MyUtils utils = new MyUtils();
    private ActivityNeetChapterQuestionsBinding binding;
    NeetApiInterface neetApiInterface;
    String subName, chapName;
    int drawable;
    int progress;
    int test_question;
    private LottieAnimationView animationView;
    String contentmatrixId;
    SharedPreferences sh_Pref;
    StudentObj sObj;
    NeetChapter neetChapter;

    QuestionsAdapter adapter;

    ArrayList<NeetQuestion> neetQuestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNeetChapterQuestionsBinding.inflate(getLayoutInflater());
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

        neetChapter = (NeetChapter) getIntent().getSerializableExtra("neetChapter");
        subName = getIntent().getStringExtra("subname");
        chapName = neetChapter.getChapName();
        drawable = getIntent().getIntExtra("drawable", R.drawable.neet_physics_gradient);
        progress = Math.round(neetChapter.getPercentage());
        contentmatrixId = getIntent().getStringExtra("contentmatrix");
        binding.tvSubName.setText(subName);
        binding.tvChapName.setText(chapName);
        binding.pbQuestion.setProgressDrawable(getDrawable(drawable));
        binding.pbQuestion.setProgress(progress);
        binding.prog.setText(progress + "%");
        binding.tvQutnCnt.setText("Attempted - "+ neetChapter.getNoOfQuestionsAttempted()+"/"+ neetChapter.getTotalNoOfQuestions());
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
        adapter = new QuestionsAdapter(neetQuestions);

        neetApiInterface = NeetApiClient.getClient().create(NeetApiInterface.class);


        binding.imgBack.setOnClickListener(view1 -> onBackPressed());

        refresh(true);
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                if (NeetNetworkConnectivity.isConnected(NeetChapterQuestions.this)) {
                    refresh(false);
                } else {
                    new NeetUtil().alertDialog(1, NeetChapterQuestions.this, getString(R.string.error_connect), getString(R.string.error_internet),
                            getString(R.string.action_settings), getString(R.string.action_close));
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
        if (NeetNetworkConnectivity.isConnected(NeetChapterQuestions.this)) {
            getChapter(contentmatrixId);
            getChapterQuestions(neetChapter.get_id());
        } else {
            new NeetUtil().alertDialog(1, NeetChapterQuestions.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (neetQuestions.size()>0){
//            refresh(false);
//        }else
//            refresh(true);
    }

    private void getChapter(String contentmatrixId) {
        Call<ArrayList<NeetChapter>> call = neetApiInterface.getChapter(contentmatrixId,getIntent().getStringExtra("uId"), neetChapter.get_id());
        call.enqueue(new Callback<ArrayList<NeetChapter>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<NeetChapter>> call, @NotNull Response<ArrayList<NeetChapter>> response) {
                if (response.body() != null) {
                    ArrayList<NeetChapter> neetChapters = response.body();
                    if (neetChapters.size() > 0) {
                        neetChapter = neetChapters.get(0);
                        int prog = Math.round(neetChapter.getPercentage());
                        binding.pbQuestion.setProgress(prog);
                        binding.prog.setText(prog + "%");
                        binding.tvQutnCnt.setText("Attempted - "+ neetChapter.getNoOfQuestionsAttempted()+"/"+ neetChapter.getTotalNoOfQuestions());
                        if (prog > 50) {
                            binding.prog.setTextColor(Color.parseColor("#ffffff"));
                        } else binding.prog.setTextColor(Color.parseColor("#000000"));
//                        new Handler().postDelayed(() -> dismissDialog(), 1000);
                    }
                }
//                else new Handler().postDelayed(() -> dismissDialog(), 1000);
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<NeetChapter>> call, @NotNull Throwable t) {

            }
        });
    }

    private void getChapterQuestions(String id) {
        Call<ArrayList<NeetQuestion>> call = neetApiInterface.getQuestions(contentmatrixId,getIntent().getStringExtra("uId"),id);
        call.enqueue(new Callback<ArrayList<NeetQuestion>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<NeetQuestion>> call, @NotNull Response<ArrayList<NeetQuestion>> response) {
                if (response.body() != null) {
                    neetQuestions = response.body();
                    binding.swipeContainer.setRefreshing(false);
                    if (neetQuestions.size() > 0) {
                        binding.rvChapterQue.setVisibility(View.VISIBLE);
                        binding.tvNoavailable.setVisibility(View.GONE);
                        adapter = new QuestionsAdapter(neetQuestions);
                        binding.rvChapterQue.setAdapter(adapter);
                        new Handler().postDelayed(() -> dismissDialog(), 1000);
                    } else {
                        binding.rvChapterQue.setVisibility(View.GONE);
                        binding.tvNoavailable.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(() -> dismissDialog(), 1000);
                    }
                } else {
                    new Handler().postDelayed(() -> dismissDialog(), 1000);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<NeetQuestion>> call, @NotNull Throwable t) {
                if (neetQuestions.size()<=0){
                    binding.rvChapterQue.setVisibility(View.GONE);
                    binding.tvNoavailable.setVisibility(View.VISIBLE);
                }
                binding.swipeContainer.setRefreshing(false);
                new Handler().postDelayed(() -> dismissDialog(), 1000);
            }
        });
    }


    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        ArrayList<NeetQuestion> cNeetQuestions;

        public QuestionsAdapter(ArrayList<NeetQuestion> neetQuestions) {
            cNeetQuestions = neetQuestions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(NeetChapterQuestions.this).inflate(R.layout.item_neet_chap_que, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if(cNeetQuestions.get(position).getIsAttempted() == 1){
                holder.ll_attempts.setVisibility(View.VISIBLE);
                holder.tvNoOfAttempts.setText("No Of Attempts : "+ cNeetQuestions.get(position).getNoOfAttempts());
                holder.tvCorrect.setText(""+ cNeetQuestions.get(position).getNoOfRights());
                holder.tvWrong.setText(""+ cNeetQuestions.get(position).getNoOfWrongs());
            }
            else {
                holder.ll_attempts.setVisibility(View.GONE);
            }

            holder.wvQuestion.setText(cNeetQuestions.get(position).getqName());
            holder.wvQuestion.setBackgroundColor(Color.TRANSPARENT);
            if (cNeetQuestions.get(position).getqAppearace().size() > 0) {
                holder.tvQAppearance.setVisibility(View.VISIBLE);
                holder.tvQAppearance.setText(cNeetQuestions.get(position).getqAppearace().get(0));
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
//                        Intent i = new Intent(NeetChapterQuestions.this, NeetTest.class);
//                        i.putExtra("question", neetQuestions.get(position));
//                        i.putExtra("subname", subName);
//                        i.putExtra("chapname", chapName);
//                        i.putExtra("drawable", drawable);
//                        i.putExtra("uId", getIntent().getStringExtra("uId"));
//                        i.putExtra("percentage", progress);
//                        startActivity(i);
//                    }
//                    return false;
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return cNeetQuestions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQAppearance, tvNoOfAttempts, tvCorrect, tvWrong;
            LinearLayout ll_attempts;
            NeetCustomWebview wvQuestion;

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
                NeetSubmitAnswer ans = (NeetSubmitAnswer) data.getSerializableExtra("submit");
                NeetQuestion que = neetQuestions.get(test_question);
                if (que.getIsAttempted()==0){
                    neetQuestions.get(test_question).setIsAttempted(1);
                    int tot = neetChapter.getNoOfQuestionsAttempted()+1;
                    if (tot <= neetChapter.getTotalNoOfQuestions() ){
                        float pr = (((float) (neetChapter.getNoOfQuestionsAttempted()+1)/ neetChapter.getTotalNoOfQuestions())*100);
                        neetChapter.setPercentage(pr);
                        neetChapter.setNoOfQuestionsAttempted(neetChapter.getNoOfQuestionsAttempted()+1);
                        neetChapter.setIsAttempted(1);
                        binding.pbQuestion.setProgress(Math.round(pr));
                        binding.prog.setText(Math.round(pr) + "%");
                        binding.tvQutnCnt.setText("Attempted - "+ neetChapter.getNoOfQuestionsAttempted()+"/"+ neetChapter.getTotalNoOfQuestions());
                        if (pr > 50) {
                            binding.prog.setTextColor(Color.parseColor("#ffffff"));
                        } else binding.prog.setTextColor(Color.parseColor("#000000"));
                    }
//                    float pr = (((float) (neetChapter.getNoOfQuestionsAttempted()+1)/ neetChapter.getTotalNoOfQuestions())*100);
//                    neetChapter.setNoOfQuestionsAttempted(neetChapter.getNoOfQuestionsAttempted()+1);
//                    neetChapter.setPercentage(pr);
//                    neetChapter.setIsAttempted(1);
//                    binding.pbQuestion.setProgress(Math.round(pr));
//                    binding.prog.setText(Math.round(pr) + "%");
//                    binding.tvQutnCnt.setText("Attempted - "+neetChapter.getNoOfQuestionsAttempted()+"/"+neetChapter.getTotalNoOfQuestions());
//                    if (pr > 50) {
//                        binding.prog.setTextColor(Color.parseColor("#ffffff"));
//                    } else binding.prog.setTextColor(Color.parseColor("#000000"));

                }
                neetQuestions.get(test_question).setNoOfAttempts(que.getNoOfAttempts()+1);
                if (ans.isCorrect()){
                    neetQuestions.get(test_question).setNoOfRights(que.getNoOfRights()+1);
                }
                else neetQuestions.get(test_question).setNoOfWrongs(que.getNoOfWrongs()+1);

                adapter.notifyDataSetChanged();

            }
            if(data.getBooleanExtra("isNext",false)) {
                test_question = test_question + 1;
                gotoTest(test_question);
            }
        }
    }

    private void gotoTest(int position) {

        if(position< neetQuestions.size()) {
            if(neetQuestions.get(position).getqName()==null || neetQuestions.get(position).getqName().isEmpty() ||
                    neetQuestions.get(position).getqOptions()==null || neetQuestions.get(position).getqOptions().size()<4){
//                AlertDialog.Builder builder = new AlertDialog.Builder(NeetChapterQuestions.this);
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
            }
            else {
                Intent i = new Intent(NeetChapterQuestions.this, NeetTest.class);
                i.putExtra("question", neetQuestions.get(position));
                i.putExtra("qsize", neetQuestions.size());
                i.putExtra("qNo",position );
                i.putExtra("subname", subName);
                i.putExtra("chapname", chapName);
                i.putExtra("drawable", drawable);
                i.putExtra("uId", getIntent().getStringExtra("uId"));
                i.putExtra("percentage", progress);
                startActivityForResult(i,1234);
            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(NeetChapterQuestions.this);
            NeetDialogExpectedAnswerBinding dView = NeetDialogExpectedAnswerBinding.inflate(getLayoutInflater());
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
        Intent i = new Intent(this, NeetChaptersActivity.class);
        i.putExtra("neetChapter", neetChapter);
        setResult(4321,i);
        finish();
    }

    public void showProgress() {
        utils.showLoader(this);
    }

    public void dismissDialog() {
        utils.dismissDialog();

    }
}