package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.QBox.QboxQuestionDetails;
import myschoolapp.com.gsnedutech.QBox.model.FileArray;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.R;
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

import static android.content.Context.MODE_PRIVATE;

public class TeacherBottomQBoxNew extends Fragment {

    private static final String TAG = "SriRam -" + TeacherBottomQBoxNew.class.getName();

    MyUtils utils = new MyUtils();
    Activity mActivity;
    Unbinder unbinder;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    View teacherBottomQboxView;


    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_approved)
    TextView tvApproved;
    @BindView(R.id.tv_rejected)
    TextView tvRejected;

    @BindView(R.id.rv_qbox_questions)
    RecyclerView rvQboxQuestions;


    List<QboxQuestion> listQuestions = new ArrayList<>();

    String questionStatus = "0";
    int offset=0;
    int itemCount = 15;
    boolean hasNextPage = false;
    String subjectId = "" ;
    QuestionsAdapter adapter;
    boolean isLoading = false;



    public TeacherBottomQBoxNew() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        teacherBottomQboxView = inflater.inflate(R.layout.fragment_teacher_bottom_qbox_new, container, false);
        unbinder = ButterKnife.bind(this, teacherBottomQboxView);

        init();



        return teacherBottomQboxView;
    }

    @Override
    public void onResume() {
        offset=0;
        hasNextPage = false;
        getTeacherQboxQuestions(true);
        super.onResume();
    }

    void init() {

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);
        rvQboxQuestions.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new QuestionsAdapter(listQuestions);
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
                getTeacherQboxQuestions(true);
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
                getTeacherQboxQuestions(true);
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
                getTeacherQboxQuestions(true);
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


    void getTeacherQboxQuestions(boolean loader){
        if (loader) utils.showLoader(mActivity);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("teacherId",tObj.getUserId());
            postObject.put("qboxStatus",questionStatus);
            postObject.put("itemCount",itemCount);
            postObject.put("offset", offset+"");
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
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                isLoading = false;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        rvQboxQuestions.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                isLoading = false;
                String resp = response.body().string();

                utils.showLog(TAG,"response "+resp);
                mActivity.runOnUiThread(() -> {
                    utils.dismissDialog();
                    rvQboxQuestions.setVisibility(View.GONE);
                });
                if (response.body() != null){
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

                            if (listQuestions.size()%itemCount==0 && array.length()>0){
                                hasNextPage = true;
                            }else {
                                hasNextPage = false;
                            }
                            if (listQuestions.size()>0){
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        rvQboxQuestions.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }else {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rvQboxQuestions.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)){ //TODO New Changes
                            String message = jsonObject.getString(AppConst.MESSAGE);
                            mActivity.runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                            });
                        }
                        else {
                            if (offset==0){
                                listQuestions.clear();
                                mActivity.runOnUiThread(() -> rvQboxQuestions.setVisibility(View.GONE));

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rvQboxQuestions.setVisibility(View.GONE);
                            }
                        });
                    }
                }else{
                     mActivity.runOnUiThread(() -> {
                    rvQboxQuestions.setVisibility(View.GONE);
                });
                }
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
            return new QuestionsAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_qbox_student_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            if (questions.get(position)!=null) {
                holder.tvStudName.setText(questions.get(position).getStudentName());
                holder.tvStudAdmNo.setText("" + questions.get(position).getAdmissionNumber());
                holder.tvQuestion.setText(questions.get(position).getQboxQuestion());
                holder.tvQueReplies.setText(questions.get(position).getReplyCount() + " Replies");
                holder.tvLikes.setText(questions.get(position).getLikes() + " Likes");
                holder.tvSubjectName.setText(questions.get(position).getSubjectName());
                if (questions.get(position).getQboxFileCount() > 0) {
                    holder.tvAttachments.setVisibility(View.VISIBLE);
                } else holder.tvAttachments.setVisibility(View.GONE);
                if (!questions.get(position).getProfilePath().equalsIgnoreCase("NA")) {
                    Picasso.with(mActivity).load(questions.get(position).getProfilePath()).placeholder(R.drawable.user_default)
                            .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivStudentImage);
                }
                if (questions.get(position).getLikes() > 0) {
                    holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_filled, 0, 0, 0);
                } else {
                    holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_border, 0, 0, 0);
                }
                try {
                    Date dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(questions.get(position).getCreatedDate());
                    holder.tvQueDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(dt));
                    holder.tvQueTime.setText(new SimpleDateFormat("HH:mm a").format(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.tvAttachments.setOnClickListener(view -> {

                    getFilesArray(questions.get(position).getStuqboxId());

                });


                holder.itemView.setOnClickListener(view -> {
                    Intent queIntent = new Intent(mActivity, QboxQuestionDetails.class);
                    queIntent.putExtra("queObj", questions.get(position));
                    if (questionStatus.equalsIgnoreCase("0"))
                        queIntent.putExtra("from", "Pending");
                    else if (questionStatus.equalsIgnoreCase("3"))
                        queIntent.putExtra("from", "Rejected");
                    else if (questionStatus.equalsIgnoreCase("1,2"))
                        queIntent.putExtra("from", "Approved/Published");
                    startActivity(queIntent);
                    mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                });

                if (!isLoading && position == (questions.size() - 1)) {
                    isLoading = true;
//                    utils.showLoader(mActivity);
                    if (hasNextPage) {
                        offset = offset + itemCount;
                        getTeacherQboxQuestions(false);
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
                    tvStudAdmNo,tvSubjectName, tvAttachments;
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
                tvSubjectName = itemView.findViewById(R.id.tv_subjectName);
                tvAttachments = itemView.findViewById(R.id.tv_attachments);
                ivShare = itemView.findViewById(R.id.iv_share);
                ivStudentImage = itemView.findViewById(R.id.iv_student_image);

            }
        }
    }

    private void getFilesArray(int stuqboxId) {
        utils.showLoader(mActivity);
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
                mActivity.runOnUiThread(new Runnable() {
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
                    mActivity.runOnUiThread(new Runnable() {
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
                                mActivity.runOnUiThread(new Runnable() {
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

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                    }
                });
            }
        });
    }

    void showBottomDialog(List<FileArray> files){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(R.layout.qbox_bottom_sheet_attach);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.show();

        RecyclerView rvFiles = bottomSheetDialog.findViewById(R.id.rv_files);
        rvFiles.setLayoutManager(new LinearLayoutManager(mActivity,RecyclerView.HORIZONTAL, false));
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
            return new AttachmentAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_hw_attachments,parent,false));
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
                        Intent intent = new Intent(mActivity, NewPdfViewer.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("mp4")) {
                        Intent intent = new Intent(mActivity, PlayerActivity.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("youtube")) {
                        Intent intent = new Intent(mActivity, YoutubeActivity.class);

                        String s[] = replies.get(position).getQboxFilePath().split("/");

                        intent.putExtra("videoItem", s[s.length - 1]);
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("jpg") ||
                            replies.get(position).getQboxFilePath().contains("jpeg") ||
                            replies.get(position).getQboxFilePath().contains("png")) {
                        Intent intent = new Intent(mActivity, ImageDisp.class);
                        intent.putExtra("path", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else
                    if (replies.get(position).getQboxFilePath().contains("doc") || replies.get(position).getQboxFilePath().equalsIgnoreCase("docx") || replies.get(position).getQboxFilePath().equalsIgnoreCase("ppt")
                            || replies.get(position).getQboxFilePath().equalsIgnoreCase("pptx") | replies.get(position).getQboxFilePath().equalsIgnoreCase("xls") ||
                            replies.get(position).getQboxFilePath().equalsIgnoreCase("xlsx")) {
                        Intent intent = new Intent(mActivity, PdfWebViewer.class);
                        intent.putExtra("url", replies.get(position).getQboxFilePath());
                        startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }else{
                        Toast.makeText(mActivity,"Cannot open this type of file!",Toast.LENGTH_SHORT).show();
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }
}