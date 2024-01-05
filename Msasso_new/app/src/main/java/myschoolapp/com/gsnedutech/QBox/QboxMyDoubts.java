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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.QBox.model.FileArray;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.ImageDisp;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NewPdfViewer;
import myschoolapp.com.gsnedutech.Util.PdfWebViewer;
import myschoolapp.com.gsnedutech.Util.PlayerActivity;
import myschoolapp.com.gsnedutech.Util.YoutubeActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QboxMyDoubts extends AppCompatActivity {

    private static final String TAG = QboxMyDoubts.class.getName();


    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    @BindView(R.id.rv_qbox_questions)
    RecyclerView rvQboxQuestions;

    MyUtils utils = new MyUtils();
    List<QboxQuestion> questionList = new ArrayList<>();

    String questionStatus = "0";
    int offset=0;
    int itemCount = 15;
    boolean hasNextPage = false;
    String subjectId = "" ;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "";
    QuestionsAdapter adapter;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qbox_my_doubts);
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
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();
        subjectId = getIntent().getStringExtra("subjectId");
        rvQboxQuestions.setLayoutManager(new LinearLayoutManager(QboxMyDoubts.this));
        adapter = new QuestionsAdapter(questionList);
        rvQboxQuestions.setAdapter(adapter);


        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionStatus = "0";
                unSelectAll();
                offset=0;
                hasNextPage = false;
                tvPending.setTextColor(Color.BLACK);
                tvPending.setBackgroundResource(R.drawable.underline);
                getQBoxQuestions(true);
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
                getQBoxQuestions(true);
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
                getQBoxQuestions(true);
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
        getQBoxQuestions(true);
        super.onResume();
    }


    private void getQBoxQuestions(boolean loader) {
        if (loader) utils.showLoader(QboxMyDoubts.this);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("qboxStatus", questionStatus);
            jsonObject.put("myUserId", studentId);
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("itemCount",itemCount);
            jsonObject.put("offset", offset+"");

        } catch (Exception e) {

        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request postRequest = apiClient.postRequest(AppUrls.GetStudentQBoxQuestions,body, sh_Pref);

        utils.showLog(TAG, "url "+ AppUrls.GetStudentQBoxQuestions);
        utils.showLog(TAG, "body -"+ jsonObject.toString());

        apiClient.getClient().newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                isLoading = false;
                runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                isLoading = false;
                if (response.body()!=null){
                    String res = response.body().string();
                    utils.showLog(TAG, "Resp - "+ res);
                    try {
                        JSONObject parentjObject = new JSONObject(res);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jar = parentjObject.getJSONArray("qboxQuestions");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<QboxQuestion>>() {
                            }.getType();
                            if (offset==0){
                                questionList.clear();
                            }
                            questionList.addAll(gson.fromJson(jar.toString(), type));

                            if (questionList.size()%itemCount==0 && jar.length()>0){
                                hasNextPage = true;
                            }else {
                                hasNextPage = false;
                            }
                            if (questionList.size()>0){
                                runOnUiThread(() -> {
                                    rvQboxQuestions.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                });
                            }
                            else {
                                runOnUiThread(() -> {
                                    if (offset==0) {
                                        questionList.clear();
                                        rvQboxQuestions.setVisibility(View.GONE);
                                    }
                                });

                            }

                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissAlertDialog();
                                MyUtils.forceLogoutUser(toEdit, QboxMyDoubts.this, message, sh_Pref);
                            });
                        }
                        else {
                            runOnUiThread(() -> {
                                if (offset==0) {
                                    questionList.clear();
                                    rvQboxQuestions.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                    catch (JSONException e) {
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


    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        List<QboxQuestion> questions;

        public QuestionsAdapter(List<QboxQuestion> questions) {
            this.questions = questions;
        }

        @NonNull
        @Override
        public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionsAdapter.ViewHolder(LayoutInflater.from(QboxMyDoubts.this).inflate(R.layout.item_qbox_student_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            if (questions.get(position)!=null) {
                holder.tvStudName.setText(questions.get(position).getStudentName());
                holder.tvStudAdmNo.setText("" + questions.get(position).getAdmissionNumber());
                holder.tvQuestion.setText(questions.get(position).getQboxQuestion());
                holder.tvQueReplies.setText(questions.get(position).getReplyCount() + " Replies");
                holder.tvSubjectName.setText(questions.get(position).getSubjectName());
                if (questions.get(position).getQboxFileCount() > 0) {
                    holder.tvAttachments.setVisibility(View.VISIBLE);
                } else holder.tvAttachments.setVisibility(View.GONE);
                if (!questions.get(position).getProfilePath().equalsIgnoreCase("NA")) {
                    Picasso.with(QboxMyDoubts.this).load(questions.get(position).getProfilePath()).placeholder(R.drawable.user_default)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivStudentImage);
                }
                holder.tvLikes.setText(questions.get(position).getLikes() + " Likes");
                if (questions.get(position).getStudentLike() > 0) {
                    holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_filled, 0, 0, 0);
                } else {
                    holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_border, 0, 0, 0);
                }

                holder.tvLikes.setOnClickListener(v -> {
//                postALike(questions.get(position).getStuqboxId(), position);
                });

                try {
                    Date dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(questions.get(position).getCreatedDate());
                    holder.tvQueDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(dt));
                    holder.tvQueTime.setText(new SimpleDateFormat("HH:mm a").format(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (questionStatus.equalsIgnoreCase("0")) {
                    holder.llCounts.setVisibility(View.GONE);
                }
                if (questionStatus.equalsIgnoreCase("3")) {
                    holder.tvLikes.setVisibility(View.INVISIBLE);
                }

                holder.tvAttachments.setOnClickListener(view -> {
                    getFilesArray(questions.get(position).getStuqboxId());
                });

                holder.itemView.setOnClickListener(view -> {
                    Intent queIntent = new Intent(QboxMyDoubts.this, QboxQuestionDetails.class);
                    queIntent.putExtra("queObj", questions.get(position));
                    startActivity(queIntent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                });
                if (!isLoading && position == (questions.size() - 1)) {
                    isLoading = true;
//                utils.showLoader(QboxMyDoubts.this);
                    if (hasNextPage) {
                        offset = offset + itemCount;
                        getQBoxQuestions(false);
                    } else {
                        utils.dismissDialog();
                    }
                }
            }


        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvLikes, tvQueDate, tvQueTime, tvQuestion, tvQueReplies, tvStudName,
                    tvStudAdmNo, tvSubjectName, tvAttachments;
            ImageView ivShare, ivStudentImage;
            LinearLayout llCounts;


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
                tvSubjectName = itemView.findViewById(R.id.tv_subjectName);
                tvAttachments = itemView.findViewById(R.id.tv_attachments);
                llCounts = itemView.findViewById(R.id.ll_counts);

            }
        }
    }

    private void getFilesArray(int stuqboxId) {
        utils.showLoader(QboxMyDoubts.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().GetQboxFiles+"stuqboxId="+stuqboxId+"&schemaName="+sh_Pref.getString("schema",""))
                .build();

        utils.showLog(TAG, "Attendance Student request - " + new AppUrls().GetQboxFiles+"stuqboxId="+stuqboxId+"&schemaName="+sh_Pref.getString("schema",""));

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

                utils.showLog(TAG,"response "+resp);

                if (!response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = jsonObject.getJSONArray("fileArray");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FileArray>>() {
                            }.getType();

                            List<FileArray> files = new ArrayList<>();
                            files.addAll(gson.fromJson(array.toString(),type));


                            if (files.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showBottomDialog(files);
                                    }
                                });
                            }

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

    void showBottomDialog(List<FileArray> files){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(QboxMyDoubts.this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(R.layout.qbox_bottom_sheet_attach);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.show();

        RecyclerView rvFiles = bottomSheetDialog.findViewById(R.id.rv_files);
        rvFiles.setLayoutManager(new LinearLayoutManager(QboxMyDoubts.this,RecyclerView.HORIZONTAL, false));
        rvFiles.setAdapter(new AttachmentAdapter(files,bottomSheetDialog));
    }


    class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder>{

        List<FileArray> replies;
        BottomSheetDialog dialog;

        public AttachmentAdapter(List<FileArray> replies, BottomSheetDialog bottomSheetDialog) {
            this.replies = replies;
            dialog = bottomSheetDialog;
        }

        @NonNull
        @Override
        public AttachmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AttachmentAdapter.ViewHolder(LayoutInflater.from(QboxMyDoubts.this).inflate(R.layout.item_hw_attachments,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttachmentAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText(replies.get(position).getQboxFileName());
            if (replies.get(position).getQboxFilePath().contains(".pdf")){
                holder.ivFile.setImageResource(R.drawable.ic_pdf);
            }
            else if(replies.get(position).getQboxFilePath().contains(".jpg") || replies.get(position).getQboxFilePath().contains(".png") ||
                    replies.get(position).getQboxFilePath().contains(".jpeg")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_jpg);
            }else if(replies.get(position).getQboxFilePath().contains(".mp4")){
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_mp4);
            }else{
                holder.ivFile.setImageResource(R.drawable.ic_student_sub_doc);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog!=null && dialog.isShowing()) dialog.dismiss();
                    if (replies.get(position).getQboxFilePath().contains("pdf")) {
                        //                        Intent intent = new Intent(StudentHWDetails.this, SomeAct.class);
                        Intent intent = new Intent(QboxMyDoubts.this, NewPdfViewer.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("mp4")) {
                        Intent intent = new Intent(QboxMyDoubts.this, PlayerActivity.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("youtube")) {
                        Intent intent = new Intent(QboxMyDoubts.this, YoutubeActivity.class);

                        String s[] = replies.get(position).getQboxFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("jpg") ||
                            replies.get(position).getQboxFilePath().contains("jpeg") ||
                            replies.get(position).getQboxFilePath().contains("png")) {
                        Intent intent = new Intent(QboxMyDoubts.this, ImageDisp.class);
                        intent.putExtra("path", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("doc") || replies.get(position).getQboxFilePath().equalsIgnoreCase("docx") || replies.get(position).getQboxFilePath().equalsIgnoreCase("ppt")
                            || replies.get(position).getQboxFilePath().equalsIgnoreCase("pptx") | replies.get(position).getQboxFilePath().equalsIgnoreCase("xls") ||
                            replies.get(position).getQboxFilePath().equalsIgnoreCase("xlsx")) {
                        Intent intent = new Intent(QboxMyDoubts.this, PdfWebViewer.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else{
                        Toast.makeText(QboxMyDoubts.this,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return replies.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivFile;
            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }
}