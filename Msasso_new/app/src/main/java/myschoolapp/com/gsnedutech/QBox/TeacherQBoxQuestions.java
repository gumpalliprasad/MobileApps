package myschoolapp.com.gsnedutech.QBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherQBoxQuestions extends AppCompatActivity {

    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    @BindView(R.id.rv_qbox_questions)
    RecyclerView rvQboxQuestions;

    MyUtils utils = new MyUtils();
    private static final String TAG = TeacherQBoxQuestions.class.getName();
    List<QboxQuestion> listQuestions = new ArrayList<>();

    String questionStatus = "0";
    int offset=0;
    int itemCount = 15;
    boolean hasNextPage = false;
    String subjectId = "" ;

    SharedPreferences sh_Pref;
    TeacherObj tObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_q_box_questions);
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

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);
        subjectId = getIntent().getStringExtra("subjectId");


        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionStatus = "0";
                unSelectAll();
                offset=0;
                hasNextPage = false;
                tvPending.setTextColor(Color.BLACK);
                tvPending.setBackgroundResource(R.drawable.underline);
                getTeacherQboxQuestions();
            }
        });
        tvApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionStatus="1,2";
                offset=0;
                hasNextPage = false;
                unSelectAll();
                tvApproved.setTextColor(Color.BLACK);
                tvApproved.setBackgroundResource(R.drawable.underline);
                getTeacherQboxQuestions();
            }
        });
        tvRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionStatus="3";
                offset=0;
                hasNextPage = false;
                unSelectAll();
                tvRejected.setTextColor(Color.BLACK);
                tvRejected.setBackgroundResource(R.drawable.underline);
                getTeacherQboxQuestions();
            }
        });


    }

    void unSelectAll(){
        tvPending.setBackgroundResource(0);
        tvApproved.setBackgroundResource(0);
        tvRejected.setBackgroundResource(0);
        tvPending.setTextColor(Color.parseColor("#64494949"));
        tvApproved.setTextColor(Color.parseColor("#64494949"));
        tvRejected.setTextColor(Color.parseColor("#64494949"));
    }

    @Override
    protected void onResume() {
        offset=0;
        hasNextPage = false;
        getTeacherQboxQuestions();
        super.onResume();
    }


    void getTeacherQboxQuestions(){
        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("teacherId",tObj.getUserId());
            postObject.put("qboxStatus",questionStatus);
            postObject.put("itemCount",itemCount);
            postObject.put("offset", offset+"");
            postObject.put("subjectId", subjectId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().GetTeacherQBoxQuestions);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeacherQBoxQuestions)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        rvQboxQuestions.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String resp = response.body().string();

                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            rvQboxQuestions.setVisibility(View.GONE);
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("qboxQuestions");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<QboxQuestion>>() {
                            }.getType();

                            if (offset==0){
                                listQuestions.clear();
                            }
                            listQuestions.addAll(gson.fromJson(array.toString(),type));

                            if (listQuestions.size()%itemCount==0){
                                hasNextPage = true;
                            }else {
                                hasNextPage = false;
                            }
                            if (listQuestions.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        rvQboxQuestions.setVisibility(View.VISIBLE);
                                        rvQboxQuestions.setLayoutManager(new LinearLayoutManager(TeacherQBoxQuestions.this));
                                        rvQboxQuestions.setAdapter(new QuestionsAdapter(listQuestions));
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvQboxQuestions.setVisibility(View.GONE);
                                    }
                                });
                            }

                        }
                        else {
                            if (offset==0){
                                listQuestions.clear();
                                runOnUiThread(() -> rvQboxQuestions.setVisibility(View.GONE));

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvQboxQuestions.setVisibility(View.GONE);
                            }
                        });
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


    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        List<QboxQuestion> questions;

        public QuestionsAdapter(List<QboxQuestion> questions) {
            this.questions = questions;
        }

        @NonNull
        @Override
        public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionsAdapter.ViewHolder(LayoutInflater.from(TeacherQBoxQuestions.this).inflate(R.layout.item_qbox_student_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvStudName.setText(questions.get(position).getStudentName());
            holder.tvStudAdmNo.setText(""+questions.get(position).getAdmissionNumber());
            holder.tvQuestion.setText(questions.get(position).getQboxQuestion());
            holder.tvQueReplies.setText(questions.get(position).getReplyCount()+" Replies");
            holder.tvLikes.setText(questions.get(position).getLikes()+ " Likes");
            if (!questions.get(position).getProfilePath().equalsIgnoreCase("NA")) {
                Picasso.with(TeacherQBoxQuestions.this).load(questions.get(position).getProfilePath()).placeholder(R.drawable.user_default)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivStudentImage);
            }
            if (questions.get(position).getLikes()>0) {
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_filled,0,0,0);
            }
            else {
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_border,0,0,0);
            }
            try {
                Date dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(questions.get(position).getCreatedDate());
                holder.tvQueDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(dt));
                holder.tvQueTime.setText(new SimpleDateFormat("HH:mm a").format(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }




            holder.itemView.setOnClickListener(view -> {
                Intent queIntent = new Intent(TeacherQBoxQuestions.this, QboxQuestionDetails.class);
                queIntent.putExtra("queObj", questions.get(position));
                if (questionStatus.equalsIgnoreCase("0"))
                    queIntent.putExtra("from", "Pending");
                else if (questionStatus.equalsIgnoreCase("3"))
                    queIntent.putExtra("from", "Rejected");
                else if (questionStatus.equalsIgnoreCase("1,2"))
                    queIntent.putExtra("from", "Approved/Published");
                startActivity(queIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            });

            if (position == (questions.size()-1)){
                utils.showLoader(TeacherQBoxQuestions.this);
                if (hasNextPage){
                    offset = offset+itemCount;
                    getTeacherQboxQuestions();
                }else {
                    utils.dismissDialog();
                }
            }


        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvLikes, tvQueDate, tvQueTime, tvQuestion, tvQueReplies, tvStudName, tvStudAdmNo;
            ImageView ivShare, ivStudentImage;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvLikes = itemView.findViewById(R.id.tv_likes);
                tvQueReplies = itemView.findViewById(R.id.tv_replies);
                tvQueDate = itemView.findViewById(R.id.tv_que_date);
                tvQueTime = itemView.findViewById(R.id.tv_que_time);
                tvQuestion = itemView.findViewById(R.id.tv_question);
                tvStudAdmNo = itemView.findViewById(R.id.tv_rollnum);
                tvStudName = itemView.findViewById(R.id.tv_student_name);
                ivShare = itemView.findViewById(R.id.iv_share);
                ivStudentImage = itemView.findViewById(R.id.iv_student_image);

            }
        }
    }
}