package myschoolapp.com.gsnedutech.Arena;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Models.CollegeInfo;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DialogInstituteDetails;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherArenaQuizReview extends AppCompatActivity {

    private static final String TAG = "SriRam -" + TeacherArenaQuizReview.class.getName();

    @BindView(R.id.rv_q_num)
    RecyclerView rvQNum;
    @BindView(R.id.tv_q)
    TextView tvQ;
    @BindView(R.id.tv_q_no_image)
    TextView tvQNoImage;
    @BindView(R.id.iv_q_image)
    ImageView ivQImage;

    @BindView(R.id.iv_op_1)
    ImageView ivOp1;
    @BindView(R.id.iv_op_2)
    ImageView ivOp2;
    @BindView(R.id.iv_op_3)
    ImageView ivOp3;
    @BindView(R.id.iv_op_4)
    ImageView ivOp4;
    @BindView(R.id.tv_op_1)
    TextView tvOp1;
    @BindView(R.id.tv_op_2)
    TextView tvOp2;
    @BindView(R.id.tv_op_3)
    TextView tvOp3;
    @BindView(R.id.tv_op_4)
    TextView tvOp4;
    @BindView(R.id.view_op1_overlay)
    View viewOp1Overlay;
    @BindView(R.id.view_op2_overlay)
    View viewOp2Overlay;
    @BindView(R.id.view_op3_overlay)
    View viewOp3Overlay;
    @BindView(R.id.view_op4_overlay)
    View viewOp4Overlay;
    @BindView(R.id.iv_tick_op1)
    ImageView ivTickOp1;
    @BindView(R.id.iv_tick_op2)
    ImageView ivTickOp2;
    @BindView(R.id.iv_tick_op3)
    ImageView ivTickOp3;
    @BindView(R.id.iv_tick_op4)
    ImageView ivTickOp4;
    @BindView(R.id.tv_true)
    TextView tvTrue;
    @BindView(R.id.tv_false)
    TextView tvFalse;
    @BindView(R.id.et_remarks)
    EditText etRemarks;

    String status = "";

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    TeacherObj tObj;
    ArenaRecord quizObj;

    List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();
    int currentPos = 0;

    QuestionNumberAdapter adapter;

    Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_arena_quiz_review);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    void init(){

        status = getIntent().getStringExtra("status");
        if (!status.equalsIgnoreCase("0"))
            etRemarks.setVisibility(View.GONE);

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);
        quizObj = (ArenaRecord) getIntent().getSerializableExtra("item");
        getQuizDetails();
        if(quizObj.getArenaName().contains("~~")){
            ((TextView)findViewById(R.id.tv_title)).setText(quizObj.getArenaName().split("~~")[0]);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(quizObj.getArenaName());
        }

        if (getIntent().hasExtra("reassign")) {
            findViewById(R.id.tv_reassign_students).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_reassign_students).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getNewSections();
                }
            });
        }

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Button)findViewById(R.id.btn_next)).getText().toString().equalsIgnoreCase("NEXT")){
                    if ((currentPos + 1) < listQuestions.size()) {

                        currentPos++;

                        loadQuestion();
                        adapter.notifyDataSetChanged();

                        if (status.equalsIgnoreCase("0")) {
                            if (currentPos == (listQuestions.size() - 1)) {
                                if (etRemarks.getText().toString().trim().length() > 0) {
                                    ((Button) findViewById(R.id.btn_next)).setText("ReAssign");
                                } else {
                                    ((Button) findViewById(R.id.btn_next)).setText("Approve");
                                }
                            }
                        }


                    } else {
                        if (status.equalsIgnoreCase("0")) {
                            if (etRemarks.getText().toString().trim().length() > 0) {
                                ((Button) findViewById(R.id.btn_next)).setText("ReAssign");
                            } else {
                                ((Button) findViewById(R.id.btn_next)).setText("Approve");
                            }
                        }
                    }
                }else {
                    if(etRemarks.getText().toString().trim().length()>0){
                        arenaReview("2",new JSONArray());
                    }else {
                        //do section call and add object
                        getSections();
                        //arenaReview("1");
                    }
                }
            }
        });

        findViewById(R.id.btn_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button)findViewById(R.id.btn_next)).setText("Next");
                if ((currentPos - 1) >= 0){
                    currentPos--;
                }

                loadQuestion();
                adapter.notifyDataSetChanged();
            }
        });

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (status.equalsIgnoreCase("0")) {
                    if (currentPos == (listQuestions.size() - 1)) {
                        if (editable.toString().trim().length() > 0) {
                            ((Button) findViewById(R.id.btn_next)).setText("ReAssign");
                        } else {
                            ((Button) findViewById(R.id.btn_next)).setText("Approve");
                        }
                    }
                }
            }
        });
    }

    private void unselectAll() {
        viewOp1Overlay.setVisibility(View.GONE);
        viewOp2Overlay.setVisibility(View.GONE);
        viewOp3Overlay.setVisibility(View.GONE);
        viewOp4Overlay.setVisibility(View.GONE);
        ivTickOp1.setVisibility(View.GONE);
        ivTickOp2.setVisibility(View.GONE);
        ivTickOp3.setVisibility(View.GONE);
        ivTickOp4.setVisibility(View.GONE);
        tvTrue.setBackgroundColor(Color.WHITE);
        tvTrue.setTextColor(Color.BLACK);
        tvFalse.setBackgroundColor(Color.WHITE);
        tvFalse.setTextColor(Color.BLACK);
    }

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + quizObj.getArenaId()+""+"&arenaCategory=1")
                .build();

        utils.showLog(TAG, AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + quizObj.getArenaId()+""+"&arenaCategory=1");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                Log.v(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);

                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("arenaCategories");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaQuizQuestionFiles>>() {
                            }.getType();

                            listQuestions.clear();
                            listQuestions.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (listQuestions.size()>0){
                                        dialog = new Dialog(TeacherArenaQuizReview.this);
                                        dialog.setContentView(R.layout.dialog_quiz_overview);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                        dialog.show();

                                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                onBackPressed();
                                            }
                                        });

                                        dialog.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                onBackPressed();
                                            }
                                        });

                                        if(quizObj.getArenaName().contains("~~")){
                                            ((TextView)dialog.findViewById(R.id.tv_quiz_name)).setText(quizObj.getArenaName().split("~~")[0]);
                                        }else {
                                            ((TextView)dialog.findViewById(R.id.tv_quiz_name)).setText(quizObj.getArenaName());
                                        }

                                        ((TextView)dialog.findViewById(R.id.tv_questions)).setText(listQuestions.size()+"");

                                        int time = 0;

                                        for (int i=0;i<listQuestions.size();i++){
                                            time = time + Integer.parseInt(listQuestions.get(i).getQuestTime());
                                        }

                                        ((TextView)dialog.findViewById(R.id.tv_time)).setText(time+" seconds");

                                        if (quizObj.getArenaDesc().length()>0){
                                            ((TextView)dialog.findViewById(R.id.tv_desc)).setText(quizObj.getArenaDesc()+"");
                                        }else{
                                            ((TextView)dialog.findViewById(R.id.tv_desc)).setText("");
                                        }

                                        dialog.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                rvQNum.setLayoutManager(new LinearLayoutManager(TeacherArenaQuizReview.this,RecyclerView.HORIZONTAL,false));
                                                adapter = new QuestionNumberAdapter(listQuestions.size());
                                                rvQNum.setAdapter(adapter);
                                                loadQuestion();
                                            }
                                        });

                                        dialog.show();

                                    }
                                }
                            });



                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    private void loadQuestion() {

        unselectAll();

        if (currentPos==0){
            findViewById(R.id.btn_prev).setVisibility(View.INVISIBLE);
        }else {
            findViewById(R.id.btn_prev).setVisibility(View.VISIBLE);
        }
//        if (currentPos == listQuestions.size()-1){
//            findViewById(R.id.btn_next).setVisibility(View.INVISIBLE);
//        }
//        else {
//            findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
//        }

        if (listQuestions.get(currentPos).getQuestion().contains("~~")){

            findViewById(R.id.ll_q_image).setVisibility(View.VISIBLE);
            tvQ.setText("Q"+(currentPos+1)+". "+listQuestions.get(currentPos).getQuestion().split("~~")[0]);
            String[] s = listQuestions.get(currentPos).getQuestion().split("~~");
            tvQNoImage.setVisibility(View.GONE);

            for (String str:s){
                if (str.contains("http")){
                    Picasso.with(TeacherArenaQuizReview.this).load(str).placeholder(R.drawable.ic_arena_img)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivQImage);
                }
            }

        }else {
            findViewById(R.id.ll_q_image).setVisibility(View.GONE);
            tvQNoImage.setText("Q"+(currentPos+1)+". "+listQuestions.get(currentPos).getQuestion());
            tvQNoImage.setVisibility(View.VISIBLE);
        }


        if (listQuestions.get(currentPos).getQuestType().equalsIgnoreCase("TOF")){
            findViewById(R.id.ll_tof).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_mcq).setVisibility(View.GONE);

            if (listQuestions.get(currentPos).getAnswer().equalsIgnoreCase("true")){
                tvTrue.setBackgroundColor(Color.parseColor("#1FA655"));
                tvTrue.setTextColor(Color.WHITE);
                tvFalse.setBackgroundColor(Color.WHITE);
                tvFalse.setTextColor(Color.BLACK);
            }else {
                tvFalse.setBackgroundColor(Color.parseColor("#1FA655"));
                tvFalse.setTextColor(Color.WHITE);
                tvTrue.setBackgroundColor(Color.WHITE);
                tvTrue.setTextColor(Color.BLACK);
            }

        }else{
            findViewById(R.id.ll_tof).setVisibility(View.GONE);
            findViewById(R.id.ll_mcq).setVisibility(View.VISIBLE);
            if (listQuestions.get(currentPos).getOption1().contains("~~")){
                tvOp1.setVisibility(View.GONE);
                ivOp1.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption1().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(TeacherArenaQuizReview.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp1);
                    }
                }
            }else {
                tvOp1.setVisibility(View.VISIBLE);
                ivOp1.setVisibility(View.GONE);
                tvOp1.setText(listQuestions.get(currentPos).getOption1());
            }
            if (listQuestions.get(currentPos).getOption2().contains("~~")){
                tvOp2.setVisibility(View.GONE);
                ivOp2.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption2().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(TeacherArenaQuizReview.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp2);
                    }
                }
            }else {
                tvOp2.setVisibility(View.VISIBLE);
                ivOp2.setVisibility(View.GONE);
                tvOp2.setText(listQuestions.get(currentPos).getOption2());
            }
            if (listQuestions.get(currentPos).getOption3().contains("~~")){
                tvOp3.setVisibility(View.GONE);
                ivOp3.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption3().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(TeacherArenaQuizReview.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp3);
                    }
                }
            }else {
                tvOp3.setVisibility(View.VISIBLE);
                ivOp3.setVisibility(View.GONE);
                tvOp3.setText(listQuestions.get(currentPos).getOption3());
            }
            if (listQuestions.get(currentPos).getOption4().contains("~~")){
                tvOp4.setVisibility(View.GONE);
                ivOp4.setVisibility(View.VISIBLE);
                String[] s = listQuestions.get(currentPos).getOption4().split("~~");
                for (String str:s){
                    if (str.contains("http")){
                        Picasso.with(TeacherArenaQuizReview.this).load(str).placeholder(R.drawable.ic_arena_img)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).into(ivOp4);
                    }
                }
            }else {
                tvOp4.setVisibility(View.VISIBLE);
                ivOp4.setVisibility(View.GONE);
                tvOp4.setText(listQuestions.get(currentPos).getOption4());
            }

            String[] ans = listQuestions.get(currentPos).getAnswer().split(",");
            for (int i=0;i<ans.length;i++){
                switch (ans[i]){
                    case "A":
                        viewOp1Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                        viewOp1Overlay.setVisibility(View.VISIBLE);
                        ivTickOp1.setImageResource(R.drawable.ic_correct_arena_quiz);
                        ivTickOp1.setVisibility(View.VISIBLE);
                        break;
                    case "B":
                        viewOp2Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                        viewOp2Overlay.setVisibility(View.VISIBLE);
                        ivTickOp2.setImageResource(R.drawable.ic_correct_arena_quiz);
                        ivTickOp2.setVisibility(View.VISIBLE);
                        break;
                    case "C":
                        viewOp3Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                        viewOp3Overlay.setVisibility(View.VISIBLE);
                        ivTickOp3.setImageResource(R.drawable.ic_correct_arena_quiz);
                        ivTickOp3.setVisibility(View.VISIBLE);
                        break;
                    case "D":
                        viewOp4Overlay.setBackgroundColor(Color.parseColor("#0EAC44"));
                        viewOp4Overlay.setVisibility(View.VISIBLE);
                        ivTickOp4.setImageResource(R.drawable.ic_correct_arena_quiz);
                        ivTickOp4.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder>{

        int numberOfQuestions;

        public QuestionNumberAdapter(int numberOfQuestions) {
            this.numberOfQuestions = numberOfQuestions;
        }

        @NonNull
        @Override
        public TeacherArenaQuizReview.QuestionNumberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TeacherArenaQuizReview.QuestionNumberAdapter.ViewHolder(LayoutInflater.from(TeacherArenaQuizReview.this).inflate(R.layout.item_question_number,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull TeacherArenaQuizReview.QuestionNumberAdapter.ViewHolder holder, int position) {
            holder.tvQNum.setText((position+1)+"");

            if (position==currentPos){
                holder.llItem.setBackgroundResource(R.drawable.bg_blue_circle);
                holder.tvQNum.setTextColor(Color.parseColor("#2E2E8B"));
                holder.tvQNum.setTextColor(Color.WHITE);
            }else{
                holder.llItem.setBackgroundResource(R.drawable.bg_grey_circle_border);
                if (position<currentPos){
                    holder.tvQNum.setTextColor(Color.parseColor("#000000"));
                }else {
                    holder.tvQNum.setTextColor(Color.parseColor("#40000000"));
                }
            }

        }

        @Override
        public int getItemCount() {
            return numberOfQuestions;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvQNum;
            LinearLayout llItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvQNum = itemView.findViewById(R.id.tv_queNo);
                llItem = itemView.findViewById(R.id.ll_itemview);
            }
        }
    }

    public void arenaReview(String s,JSONArray sections) {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("schemaName", getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE).getString("schema",""));
            jsonObject.put("arenaId",quizObj.getArenaId()+"");
            if (s.equalsIgnoreCase("1")){
                jsonObject.put("arenaStatus","1");
                jsonObject.put("arenaDraftStatus","1");
                jsonObject.put("sections",sections);
            }else {
                jsonObject.put("arenaStatus","2");
                jsonObject.put("teacherReview",etRemarks.getText().toString());
            }
            if (quizObj.getStudentId().equalsIgnoreCase("")){
                jsonObject.put("createdBy",tObj.getUserId()+"");
            }else {
                jsonObject.put("createdBy",quizObj.getStudentId()+"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().ReviewArenaStatus);
        utils.showLog(TAG,"url obj"+jsonObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        Request request = new Request.Builder()
                .url(new AppUrls().ReviewArenaStatus)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        utils.dismissDialog();
                        new AlertDialog.Builder(TeacherArenaQuizReview.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("Oops! Something went wrong.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(true)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                Log.v(TAG,"response "+resp);
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            new AlertDialog.Builder(TeacherArenaQuizReview.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("Oops! Something went wrong.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObj = new JSONObject(resp);
                        if (jsonObj.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (s.equalsIgnoreCase("1")){
                                        new AlertDialog.Builder(TeacherArenaQuizReview.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Approved successfully!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }else {
                                        new AlertDialog.Builder(TeacherArenaQuizReview.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Review successfully sent!")
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                })
                                                .setCancelable(true)
                                                .show();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void getSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"")
                .build();

        utils.showLog(TAG, AppUrls.GetAllBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"");

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(TeacherArenaQuizReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherArenaQuizReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);

                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){

                            JSONArray array = respObj.getJSONArray("collegesInfo");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollegeInfo>>() {
                            }.getType();

                            List<CollegeInfo> listBranches = new ArrayList<>();
                            listBranches.clear();
                            listBranches.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(TeacherArenaQuizReview.this,listBranches);
                                    dInstDetails.show();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherArenaQuizReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    void getNewSections(){
        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+quizObj.getArenaId())
                .build();

        utils.showLog(TAG, AppUrls.GetAllArenaBranchClassCourseSections +"schemaName=" +sh_Pref.getString("schema","")+ "&instId=" + tObj.getInstId()+"&arenaId="+quizObj.getArenaId());

        client.newCall(get).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        Toast.makeText(TeacherArenaQuizReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TeacherArenaQuizReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String resp = response.body().string();
                    try {
                        JSONObject respObj = new JSONObject(resp);

                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){

                            JSONArray array = respObj.getJSONArray("collegesInfo");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CollegeInfo>>() {
                            }.getType();

                            List<CollegeInfo> listBranches = new ArrayList<>();
                            listBranches.clear();
                            listBranches.addAll(gson.fromJson(array.toString(),type));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0;i<listBranches.size();i++){
                                        for (int j=0;j<listBranches.get(i).getCourses().size();j++){
                                            for (int k=0;k<listBranches.get(i).getCourses().get(j).getClasses().size();k++){
                                                for (int l=0;l<listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().size();l++){
                                                    if (listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).getAssignedId().equalsIgnoreCase("0")){
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(false);
                                                    }else {
                                                        listBranches.get(i).getCourses().get(j).getClasses().get(k).getSections().get(l).setSelected(true);
                                                        listBranches.get(i).setShow(true);
                                                        for (int a=0;a<listBranches.get(i).getCourses().size();a++) {
                                                            listBranches.get(i).getCourses().get(a).setShow(true);
                                                            if (listBranches.get(i).getCourses().get(a).getCourseId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getCourseId())){
                                                                for (int b=0;b<listBranches.get(i).getCourses().get(a).getClasses().size();b++){
                                                                    listBranches.get(i).getCourses().get(a).getClasses().get(b).setShow(true);
                                                                    if (listBranches.get(i).getCourses().get(a).getClasses().get(b).getClassId().equalsIgnoreCase(listBranches.get(i).getCourses().get(j).getClasses().get(k).getClassId())){
                                                                        for (int c=0;c<listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().size();c++){
                                                                            listBranches.get(i).getCourses().get(a).getClasses().get(b).getSections().get(c).setShow(true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    DialogInstituteDetails dInstDetails = new DialogInstituteDetails(TeacherArenaQuizReview.this,listBranches);
                                    dInstDetails.handleListNew();

                                }
                            });


                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TeacherArenaQuizReview.this,"Oops! Something went wrong.",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}