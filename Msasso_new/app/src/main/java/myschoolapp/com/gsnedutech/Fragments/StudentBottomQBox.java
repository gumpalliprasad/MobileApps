package myschoolapp.com.gsnedutech.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
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
import butterknife.Unbinder;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.QBox.AskADoubt;
import myschoolapp.com.gsnedutech.QBox.QboxMyDoubts;
import myschoolapp.com.gsnedutech.QBox.QboxQuestionDetails;
import myschoolapp.com.gsnedutech.QBox.model.FileArray;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.StudentHome;
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

import static android.content.Context.MODE_PRIVATE;


public class StudentBottomQBox extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = StudentBottomQBox.class.getName();

    Activity mActivity;
    View viewStudentBottomQBox;
    Unbinder unbinder;
    MyUtils utils = new MyUtils();

    @BindView(R.id.rv_qbox_questions)
    RecyclerView rvQBoxQuestions;

    @BindView(R.id.tv_my_doubts)
    TextView tvMyDoubts;

    @BindView(R.id.sv_qbox_questions)
    SearchView svQboxQuestions;

    @BindView(R.id.iv_qbox_filter)
    ImageView ivQboxFilter;

    @BindView(R.id.tv_no_questions)
    TextView tvNoQuestions;

    List<QboxQuestion> questions = new ArrayList<>();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    String studentId = "";
    QuestionsAdapter adapter;

    int offset=0;
    boolean hasNextPage = false;
    int itemCount = 15;

    List<String> subjects  = new ArrayList<>();



    public StudentBottomQBox() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewStudentBottomQBox =  inflater.inflate(R.layout.fragment_student_bottom_q_box, container, false);
        unbinder = ButterKnife.bind(this, viewStudentBottomQBox);

        init();

        viewStudentBottomQBox.findViewById(R.id.ll_ask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AskADoubt.class));
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        return viewStudentBottomQBox;
    }


    private void init() {

        sh_Pref = mActivity.getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        studentId = sObj.getStudentId();
        svQboxQuestions.setOnQueryTextListener(this);

        ivQboxFilter.setOnClickListener(v -> {
            if (subjects.size()>0){
                final Dialog dialog = new Dialog(mActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_qbox_subjects);
                dialog.setCancelable(true);
                DisplayMetrics metrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int wwidth = metrics.widthPixels;
                dialog.getWindow().setLayout((int) (wwidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                ImageView ivClose = dialog.findViewById(R.id.iv_close);
                RecyclerView rvSubjects = dialog.findViewById(R.id.rv_subjects);
                rvSubjects.setLayoutManager(new LinearLayoutManager(mActivity));
                rvSubjects.setAdapter(new SubjectAdapter(subjects, dialog));
                ivClose.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
                dialog.show();
            }
        });

    }

    class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

        List<String> subs;
        Dialog dialog;

        public SubjectAdapter(List<String> subjects, Dialog dialog) {
           subs = subjects;
           this.dialog = dialog;
        }

        @NonNull
        @Override
        public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubjectAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_qbox_subject, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvSubjectName.setText(subjects.get(position));


            holder.itemView.setOnClickListener(view -> {
                dialog.dismiss();
                svQboxQuestions.setQuery(subjects.get(position),true);
            });


        }



        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return subs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSubjectName;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSubjectName = itemView.findViewById(R.id.tv_subjectName);

            }
        }
    }





    @Override
    public void onResume() {
        ((StudentHome) mActivity).fabMyDoubts.setVisibility(View.VISIBLE);
        ((StudentHome) mActivity).fabMyDoubts.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, QboxMyDoubts.class);
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });
        super.onResume();
//        ((StudentHome)getActivity()).getSupportActionBar().hide();
        InputMethodManager inputManager = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(svQboxQuestions.getWindowToken(), 0);
        getQBoxQuestions();


    }

    @Override
    public void onStop() {
        ((StudentHome) mActivity).fabMyDoubts.setVisibility(View.GONE);
        super.onStop();
//        ((StudentHome)getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onPause() {
        ((StudentHome) mActivity).fabMyDoubts.setVisibility(View.GONE);
        super.onPause();

    }

    private void getQBoxQuestions() {
        utils.showLoader(mActivity);
        ApiClient apiClient = new ApiClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("qboxStatus", 2);
            jsonObject.put("myUserId", studentId);
            jsonObject.put("courseId", sObj.getCourseId());
            jsonObject.put("classId", sObj.getClassId());
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));

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
                mActivity.runOnUiThread(() -> {
                    utils.dismissDialog();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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
                            questions.clear();
                            questions.addAll(gson.fromJson(jar.toString(), type));
                            if (questions.size()>0){
                                mActivity.runOnUiThread(() -> {
                                    rvQBoxQuestions.setVisibility(View.VISIBLE);
                                    tvNoQuestions.setVisibility(View.GONE);
                                    for (QboxQuestion question : questions){
                                        if (!subjects.contains(question.getSubjectName()))
                                            subjects.add(question.getSubjectName());
                                    }
                                    rvQBoxQuestions.setLayoutManager(new LinearLayoutManager(mActivity));
                                    adapter = new QuestionsAdapter(questions);
                                    rvQBoxQuestions.setAdapter(adapter);
                                    if (svQboxQuestions.getQuery().toString().length()>0){
                                        svQboxQuestions.setQuery(svQboxQuestions.getQuery().toString(),true);
                                    }
                                });
                            }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                                String message = parentjObject.getString(AppConst.MESSAGE);
                                mActivity.runOnUiThread(() -> {
                                    utils.dismissDialog();
                                    MyUtils.forceLogoutUser(toEdit, mActivity, message, sh_Pref);
                                });
                            }
                            else {
                                mActivity.runOnUiThread(() -> {
                                    rvQBoxQuestions.setVisibility(View.GONE);
                                    tvNoQuestions.setVisibility(View.VISIBLE);
                                });
                            }


                        }
                    }
                    catch (JSONException e) {
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (adapter!=null)
            adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter!=null)
        adapter.getFilter().filter(newText);
        return false;
    }

    class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> implements Filterable {

        List<QboxQuestion> list, list_filtered;

        public QuestionsAdapter(List<QboxQuestion> questions) {
            this.list = questions;
            this.list_filtered = questions;
        }

        @NonNull
        @Override
        public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new QuestionsAdapter.ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_qbox_student_home, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvStudName.setText(list_filtered.get(position).getStudentName());
            holder.tvStudAdmNo.setText(""+list_filtered.get(position).getAdmissionNumber());
            holder.tvQuestion.setText(list_filtered.get(position).getQboxQuestion());
            holder.tvSubjectName.setText(list_filtered.get(position).getSubjectName());
            if (list_filtered.get(position).getQboxFileCount()>0){
                holder.tvAttachments.setVisibility(View.VISIBLE);
            }
            else holder.tvAttachments.setVisibility(View.GONE);
            holder.tvQueReplies.setText(list_filtered.get(position).getReplyCount()+" Replies");
            if (list_filtered.get(position).getProfilePath()!=null
             && !list_filtered.get(position).getProfilePath().equalsIgnoreCase("NA")) {
                Picasso.with(mActivity).load(list_filtered.get(position).getProfilePath()).placeholder(R.drawable.user_default)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivStudentImage);
            }
            holder.tvLikes.setText(list_filtered.get(position).getLikes() + " Likes");
            if (list_filtered.get(position).getStudentLike()>0) {
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_filled,0,0,0);
            }
            else {
                holder.tvLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_border,0,0,0);
            }

            holder.tvLikes.setOnClickListener(v -> {
                if (!(list_filtered.get(position).getStudentLike()>0))
                    postALike(list_filtered.get(position).getStuqboxId(), position);
            });

            try {
                Date dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list_filtered.get(position).getCreatedDate());
                holder.tvQueDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(dt));
                holder.tvQueTime.setText(new SimpleDateFormat("HH:mm a").format(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvAttachments.setOnClickListener(view -> {

                getFilesArray(list_filtered.get(position).getStuqboxId());

            });

            holder.itemView.setOnClickListener(view -> {
                Intent queIntent = new Intent(mActivity, QboxQuestionDetails.class);
                queIntent.putExtra("queObj", list_filtered.get(position));
                mActivity.startActivity(queIntent);
                mActivity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            });


        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String string = constraint.toString();
                    if (string.isEmpty()) {
                        list_filtered = list;
                    } else {

                        List<QboxQuestion> filteredList = new ArrayList<>();
                        for (QboxQuestion s : list) {

                            if (s.getQboxQuestion().toLowerCase().contains(string.toLowerCase()) || s.getSubjectName().toLowerCase().contains(string.toLowerCase())) {
                                filteredList.add(s);
                            }
                        }

                        list_filtered = filteredList;

                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_filtered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list_filtered = (List<QboxQuestion>) results.values;
                    if (list_filtered.size()>0){
                        rvQBoxQuestions.setVisibility(View.VISIBLE);
                        tvNoQuestions.setVisibility(View.GONE);
                    }
                    else {
                        rvQBoxQuestions.setVisibility(View.GONE);
                        tvNoQuestions.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        public void update(List<QboxQuestion> listUpdated) {
            list_filtered = listUpdated;
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return list_filtered.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvLikes, tvQueDate, tvQueTime, tvQuestion, tvQueReplies, tvStudName,
                    tvStudAdmNo, tvSubjectName, tvAttachments;
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
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    void postALike(Integer qboxQuestion, int position){
        utils.showLoader(mActivity);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("studentId",studentId);
            postObject.put("stuqboxId", qboxQuestion);
            postObject.put("like", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().LikeRateqboxQuestion);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().LikeRateqboxQuestion)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                            questions.get(position).setStudentLike(Integer.valueOf(studentId));
                            questions.get(position).setLikes(questions.get(position).getLikes()+1);
                            mActivity.runOnUiThread(() -> {
                                adapter.notifyItemChanged(position);
                            });

                        }
                        else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
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





}