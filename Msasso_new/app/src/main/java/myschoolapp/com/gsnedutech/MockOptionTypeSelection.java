package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StdnTestCategories;
import myschoolapp.com.gsnedutech.Models.StdnTestChapters;
import myschoolapp.com.gsnedutech.Models.StdnTestClass;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Models.StdnTestSubject;
import myschoolapp.com.gsnedutech.Models.StdnTestTopics;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MockOptionTypeSelection extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = MockOptionTypeSelection.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_mock_op_type)
    RecyclerView rvMockOpType;
    MyUtils utils = new MyUtils();

    String currentType="SUBJECT-WISE";
    String type="";

    @BindView(R.id.tv_type_header)
    TextView tvTypeHeader;
    @BindView(R.id.tv_type_sub_header)
    TextView tvTypeSubHeader;
    @BindView(R.id.tv_type_desc)
    TextView tvTypeDesc;

    StdnTestDefClsCourseSub courseObj = new StdnTestDefClsCourseSub();
    StdnTestCategories testCategoryObj ;

    List<StdnTestClass> list_classes = new ArrayList<>();
    List<StdnTestSubject> list_subjects = new ArrayList<>();
    List<StdnTestChapters> list_chapters = new ArrayList<>();
    List<StdnTestTopics> list_topics = new ArrayList<>();

    int class_selected = 0, subject_selected = 0, chapter_selected = 0, topic_selected = 0;

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    String navto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_option_type_selection);
        ButterKnife.bind(this);

        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, MockOptionTypeSelection.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
            }
            isNetworkAvail = true;
        }
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }

    private void init() {
        courseObj = (StdnTestDefClsCourseSub) getIntent().getSerializableExtra("courseObj");
        testCategoryObj = (StdnTestCategories) getIntent().getSerializableExtra("testCategoryObj");

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);
        list_classes.addAll(courseObj.getClasses());

        if (getIntent().hasExtra("class_selected")){
            class_selected = Integer.parseInt(getIntent().getStringExtra("class_selected"));
        }else {
            class_selected = 0;
        }

        list_subjects.addAll(list_classes.get(class_selected).getSubjects());


        type = getIntent().getStringExtra("type");
        navto = getIntent().getStringExtra("navto");
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvMockOpType.setLayoutManager(new GridLayoutManager(this,2));
        rvMockOpType.setAdapter(new MockSubjectAdapter());
        switch (testCategoryObj.getCategoryName())
        {
            case "SUBJECT-WISE":
                tvTypeHeader.setText("Subject Wise");
                tvTypeSubHeader.setText("Subject Wise MCQ Test");
                tvTypeDesc.setText("Select a Subject to begin.");
                break;
            case "TOPIC-WISE":
                tvTypeHeader.setText("Topic Wise");
                tvTypeSubHeader.setText("Topic Wise MCQ Test");
                break;
            case "CHAPTER-WISE":
                tvTypeHeader.setText("Chapter Wise");
                tvTypeSubHeader.setText("Chapter Wise MCQ Test");
                break;
        }
    }

    class MockSubjectAdapter extends RecyclerView.Adapter<MockSubjectAdapter.ViewHolder>{

        @NonNull
        @Override
        public MockSubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MockOptionTypeSelection.this).inflate(R.layout.item_mock_sub,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull MockSubjectAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.ivSubImage.setVisibility(View.VISIBLE);

            holder.tvSubjectName.setText(list_subjects.get(position).getSubjectName());

            if (list_subjects.get(position).getSubjectName().contains("Maths")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_maths_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_maths_icon_white);
            }
            else if (list_subjects.get(position).getSubjectName().contains("Social Science")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_social_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_social_white);
            }
            else if (list_subjects.get(position).getSubjectName().contains("Science")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_science_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_science_white);
            }
            else if (list_subjects.get(position).getSubjectName().contains("English")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_english_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_english_white);
            }
            else if (list_subjects.get(position).getSubjectName().contains("Telugu")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_telugu_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_telugu_white);
            }
            else if (list_subjects.get(position).getSubjectName().contains("Hindi")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_hindi_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_hindi_white);
            }else if (list_subjects.get(position).getSubjectName().contains("Physics")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_grad_maths);
                holder.ivSubImage.setImageResource(R.drawable.ic_physics_white);
            }else if (list_subjects.get(position).getSubjectName().contains("Chemistry")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_grad_maths);
                holder.ivSubImage.setImageResource(R.drawable.ic_chemistry_white);
            }else if (list_subjects.get(position).getSubjectName().contains("Biology")){
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_grad_maths);
                holder.ivSubImage.setImageResource(R.drawable.ic_biology_white);
            }else {
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_maths_square_grad);
                holder.ivSubImage.setImageResource(R.drawable.ic_english_white);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subject_selected = position;
                    switch (type){
                        case "SUBJECT-WISE":
                            if (navto.equalsIgnoreCase("mock")){
                                Intent intent = new Intent(MockOptionTypeSelection.this, MockTestInfo.class);
                                intent.putExtra("classId", list_classes.get(class_selected).getClassId());
                                intent.putExtra("className", list_classes.get(class_selected).getClassName());
                                intent.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                intent.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                intent.putExtra("chapterId", "0");
                                intent.putExtra("chapterName", "0");
                                intent.putExtra("topicId", "0");
                                intent.putExtra("topicName", "0");
                                intent.putExtra("testType",currentType);
                                intent.putExtra("courseObj", courseObj);
                                intent.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                intent.putExtra("testCategoryObj", testCategoryObj);
                                intent.putExtra(AppConst.TopicCCMapId, "0");
                                intent.putExtra(AppConst.ChapterCCMapId, "0");
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(MockOptionTypeSelection.this, PracticeTest.class);
                                intent.putExtra("classId", list_classes.get(class_selected).getClassId());
                                intent.putExtra("className", list_classes.get(class_selected).getClassName());
                                intent.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                intent.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                intent.putExtra("chapterId", "0");
                                intent.putExtra("chapterName", "0");
                                intent.putExtra("topicId", "0");
                                intent.putExtra("testType",currentType);
                                intent.putExtra("topicName", "0");
                                intent.putExtra("courseId", courseObj.getCourseId());
                                intent.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                intent.putExtra("testCategoryId", testCategoryObj.getCategoryId());
                                intent.putExtra(AppConst.TopicCCMapId, "0");
                                intent.putExtra(AppConst.ChapterCCMapId, "0");
                                startActivity(intent);
                            }

//                            startActivity(new Intent(MockOptionTypeSelection.this, MocKTest.class));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "CHAPTER-WISE":
                            if (currentType.equalsIgnoreCase("SUBJECT-WISE")){
                                tvTypeDesc.setText("Select a chapter to begin test");
                                currentType = "CHAPTER-WISE";
                                getChapterTopics(list_subjects.get(subject_selected).getSubjectId(), list_subjects.get(subject_selected).getContentType());
                            }else{
                                if(currentType != null && currentType != "CHAPTER-WISE") {
                                    startActivity(new Intent(MockOptionTypeSelection.this, PracticeTest.class));
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            }
                            break;
                        case "TOPIC-WISE":
                            if (currentType.equalsIgnoreCase("SUBJECT-WISE")){
                                tvTypeDesc.setText("Select a chapter");
                                currentType = "CHAPTER-WISE";
                                getChapterTopics(list_subjects.get(subject_selected).getSubjectId(), list_subjects.get(subject_selected).getContentType());
                            }else if(currentType.equalsIgnoreCase("CHAPTER-WISE")){
                                tvTypeDesc.setText("Select a topic to begin test");
                                currentType = "TOPIC-WISE";
                                notifyDataSetChanged();
                            }else{
                                if (list_chapters.size()>0 && list_topics.size()>0) {
                                    startActivity(new Intent(MockOptionTypeSelection.this, PracticeTest.class));
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            }
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list_subjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llSubBackGround;
            TextView tvSubjectName;
            ImageView ivSubImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                llSubBackGround = itemView.findViewById(R.id.ll_sub_background);
                tvSubjectName = itemView.findViewById(R.id.tv_subject);
                ivSubImage = itemView.findViewById(R.id.iv_subject);
            }
        }
    }

    private void getChapterTopics(String subjectId, String contentType) {
        utils.showLoader(this);
        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "URL " + new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "") + "&contentType=" + contentType + "&subjectId=" + subjectId + "&sectionId=" + sObj.getClassCourseSectionId());
        Request request = apiClient.getRequest(new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "")
                +"&contentType=" + contentType+ "&subjectId=" + subjectId
                +"&sectionId="+sObj.getClassCourseSectionId()
                +"&courseId="+courseObj.getCourseId() +"&classId="+sObj.getClassId(), sh_Pref);
        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> utils.dismissDialog());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> utils.dismissDialog());
                if (response.body()!=null){
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Chapters - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArr = parentjObject.getJSONArray("ContentOwner");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestChapters>>() {
                            }.getType();

                            list_chapters.clear();
                            list_chapters.addAll(gson.fromJson(jsonArr.toString(), type));
                            utils.showLog(TAG, "Size " + list_chapters.size());

                            runOnUiThread(() -> {
                                if (list_chapters.size()>0) {
                                    rvMockOpType.setAdapter(new MockChapterAdapter());
                                }
                                else {
                                    showNoChaptersFound();
                                }
                            });
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, MockOptionTypeSelection.this, message, sh_Pref);
                            });
                        }
                        else {
                            runOnUiThread(() -> showNoChaptersFound());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    void showNoChaptersFound(){
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("No chapters found on this subject")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    }
                })
                .setCancelable(false)
                .show();
    }

    class MockChapterAdapter extends RecyclerView.Adapter<MockChapterAdapter.ViewHolder>{

        @NonNull
        @Override
        public MockChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MockOptionTypeSelection.this).inflate(R.layout.item_mock_sub,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull MockChapterAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvSubjectName.setText(list_chapters.get(position).getChapterName());
            holder.llSubBackGround.setBackgroundResource(R.drawable.bg_chapter_topic_square);
            holder.ivSubImage.setVisibility(View.GONE);

            if (currentType.equalsIgnoreCase("Topic Wise")){
                holder.tvSubjectName.setText(list_chapters.get(position).getChapterName());
                holder.llSubBackGround.setBackgroundResource(R.drawable.bg_chapter_topic_square);
                holder.ivSubImage.setVisibility(View.GONE);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chapter_selected = position;
                    switch (type){
                        case "SUBJECT-WISE":
                            if (navto.equalsIgnoreCase("mock")){
                                Intent intent = new Intent(MockOptionTypeSelection.this, MockTestNew.class);
                                intent.putExtra("classId", list_classes.get(class_selected).getClassId());
                                intent.putExtra("className", list_classes.get(class_selected).getClassName());
                                intent.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                intent.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                intent.putExtra("chapterId", "0");
                                intent.putExtra("chapterName", "0");
                                intent.putExtra("topicId", "0");
                                intent.putExtra("topicName", "0");
                                intent.putExtra("testType",currentType);
                                intent.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                intent.putExtra("courseObj", courseObj);
                                intent.putExtra("testCategoryObj", testCategoryObj);
                                intent.putExtra(AppConst.ChapterCCMapId, "0");
                                intent.putExtra(AppConst.TopicCCMapId, "0");
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(MockOptionTypeSelection.this, PracticeTest.class);
                                intent.putExtra("classId", list_classes.get(class_selected).getClassId());
                                intent.putExtra("className", list_classes.get(class_selected).getClassName());
                                intent.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                intent.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                intent.putExtra("chapterId", "0");
                                intent.putExtra("chapterName", "0");
                                intent.putExtra("testType",currentType);
                                intent.putExtra("topicId", "0");
                                intent.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                intent.putExtra("topicName", "0");
                                intent.putExtra("courseId", courseObj.getCourseId());
                                intent.putExtra("testCategoryId", testCategoryObj.getCategoryId());
                                intent.putExtra(AppConst.ChapterCCMapId, "0");
                                intent.putExtra(AppConst.TopicCCMapId, "0");
                                startActivity(intent);
                            }

//                            startActivity(new Intent(MockOptionTypeSelection.this, MocKTest.class));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "CHAPTER-WISE":
                            if (currentType.equalsIgnoreCase("SUBJECT-WISE")){
                                tvTypeDesc.setText("Select a chapter to begin test");
                                currentType = "CHAPTER-WISE";
                                getChapterTopics(list_subjects.get(position).getSubjectId(), list_subjects.get(position).getContentType());


                            }else{
                                if (navto.equalsIgnoreCase("mock")){
                                    Intent intent1 = new Intent(MockOptionTypeSelection.this, MockTestInfo.class);
                                    intent1.putExtra("classId", list_classes.get(class_selected).getClassId());
                                    intent1.putExtra("className", list_classes.get(class_selected).getClassName());
                                    intent1.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                    intent1.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                    intent1.putExtra("chapterId", list_chapters.get(chapter_selected).getChapterId());
                                    intent1.putExtra("chapterName", list_chapters.get(chapter_selected).getChapterName());
                                    intent1.putExtra("topicId", "0");
                                    intent1.putExtra("topicName", "0");
                                    intent1.putExtra("testType",currentType);
                                    intent1.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                    intent1.putExtra("courseObj", courseObj);
                                    intent1.putExtra("testCategoryObj", testCategoryObj);
                                    intent1.putExtra(AppConst.ChapterCCMapId, list_chapters.get(chapter_selected).getChapterCCMapId());
                                    intent1.putExtra(AppConst.TopicCCMapId, "0");
                                    startActivity(intent1);
                                }
                                else {
                                    Intent intent1 = new Intent(MockOptionTypeSelection.this, PracticeTest.class);
                                    intent1.putExtra("classId", list_classes.get(class_selected).getClassId());
                                    intent1.putExtra("className", list_classes.get(class_selected).getClassName());
                                    intent1.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                    intent1.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                    intent1.putExtra("chapterId", list_chapters.get(chapter_selected).getChapterId());
                                    intent1.putExtra("chapterName", list_chapters.get(chapter_selected).getChapterName());
                                    intent1.putExtra("topicId", "0");
                                    intent1.putExtra("testType",currentType);
                                    intent1.putExtra("topicName", "0");
                                    intent1.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                    intent1.putExtra("courseId", courseObj.getCourseId());
                                    intent1.putExtra("testCategoryId", testCategoryObj.getCategoryId());
                                    intent1.putExtra(AppConst.ChapterCCMapId, list_chapters.get(chapter_selected).getChapterCCMapId());
                                    intent1.putExtra(AppConst.TopicCCMapId, "0");
                                    startActivity(intent1);
                                }

                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                        case "TOPIC-WISE":
                            if (currentType.equalsIgnoreCase("SUBJECT-WISE")){
                                tvTypeDesc.setText("Select a chapter");
                                currentType = "CHAPTER-WISE";

                            }else if(currentType.equalsIgnoreCase("CHAPTER-WISE")){
                                tvTypeDesc.setText("Select a topic to begin test");

                                currentType = "TOPIC-WISE";

                               list_topics.clear();
                               list_topics.addAll(list_chapters.get(position).getChapterTopic());
                               rvMockOpType.setAdapter(new MockOptionAdapter());
                            }else{
                                startActivity(new Intent(MockOptionTypeSelection.this, PracticeTest.class));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list_chapters.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llSubBackGround;
            TextView tvSubjectName;
            ImageView ivSubImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                llSubBackGround = itemView.findViewById(R.id.ll_sub_background);
                tvSubjectName = itemView.findViewById(R.id.tv_subject);
                ivSubImage = itemView.findViewById(R.id.iv_subject);
            }
        }
    }

    class MockOptionAdapter extends RecyclerView.Adapter<MockOptionAdapter.ViewHolder>{

        @NonNull
        @Override
        public MockOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(MockOptionTypeSelection.this).inflate(R.layout.item_mock_sub,parent,false));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(@NonNull MockOptionAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);
            holder.tvSubjectName.setText(list_topics.get(position).getTopicName());
            holder.llSubBackGround.setBackgroundResource(R.drawable.bg_chapter_topic_square);
            holder.ivSubImage.setVisibility(View.GONE);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    topic_selected = position;
                    switch (type){
                        case "SUBJECT-WISE":
                            startActivity(new Intent(MockOptionTypeSelection.this, PracticeTest.class));
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            break;
                        case "CHAPTER-WISE":
                            if (currentType.equalsIgnoreCase("SUBJECT-WISE")){
                                tvTypeDesc.setText("Select a chapter to begin test");
                                currentType = "CHAPTER-WISE";
                            }else{
                                startActivity(new Intent(MockOptionTypeSelection.this, PracticeTest.class));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                        case "TOPIC-WISE":
                            if (currentType.equalsIgnoreCase("SUBJECT-WISE")){
                                tvTypeDesc.setText("Select a chapter");
                                currentType = "CHAPTER-WISE";
                            }else if(currentType.equalsIgnoreCase("CHAPTER-WISE")){
                                tvTypeDesc.setText("Select a topic to begin test");
                                currentType = "TOPIC-WISE";
                            }else{
                                if (navto.equalsIgnoreCase("mock")){
                                    Intent intent = new Intent(MockOptionTypeSelection.this, MockTestInfo.class);
                                    intent.putExtra("classId", list_classes.get(class_selected).getClassId());
                                    intent.putExtra("className", list_classes.get(class_selected).getClassName());
                                    intent.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                    intent.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                    intent.putExtra("chapterId", list_chapters.get(chapter_selected).getChapterId());
                                    intent.putExtra("chapterName", list_chapters.get(chapter_selected).getChapterName());
                                    intent.putExtra("topicId", list_topics.get(topic_selected).getTopicId());
                                    intent.putExtra("topicName", list_topics.get(topic_selected).getTopicName());
                                    intent.putExtra("courseObj", courseObj);
                                    intent.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                    intent.putExtra("testCategoryObj", testCategoryObj);
                                    intent.putExtra(AppConst.ChapterCCMapId, list_chapters.get(chapter_selected).getChapterCCMapId());
                                    intent.putExtra(AppConst.TopicCCMapId, list_topics.get(topic_selected).getTopicCCMapId());
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(MockOptionTypeSelection.this, PracticeTest.class);
                                    intent.putExtra("classId", list_classes.get(class_selected).getClassId());
                                    intent.putExtra("className", list_classes.get(class_selected).getClassName());
                                    intent.putExtra("subjectId", list_subjects.get(subject_selected).getSubjectId());
                                    intent.putExtra("subjectName", list_subjects.get(subject_selected).getSubjectName());
                                    intent.putExtra("chapterId", list_chapters.get(chapter_selected).getChapterId());
                                    intent.putExtra("chapterName", list_chapters.get(chapter_selected).getChapterName());
                                    intent.putExtra("topicId", list_topics.get(topic_selected).getTopicId());
                                    intent.putExtra("topicName", list_topics.get(topic_selected).getTopicName());
                                    intent.putExtra("courseId", courseObj.getCourseId());
                                    intent.putExtra("testType",currentType);
                                    intent.putExtra("contentType",list_subjects.get(subject_selected).getContentType());
                                    intent.putExtra("testCategoryId", testCategoryObj.getCategoryId());
                                    intent.putExtra(AppConst.ChapterCCMapId, list_chapters.get(chapter_selected).getChapterCCMapId());
                                    intent.putExtra(AppConst.TopicCCMapId, list_topics.get(topic_selected).getTopicCCMapId());
                                    startActivity(intent);
                                }

                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            }
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list_topics.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llSubBackGround;
            TextView tvSubjectName;
            ImageView ivSubImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                llSubBackGround = itemView.findViewById(R.id.ll_sub_background);
                tvSubjectName = itemView.findViewById(R.id.tv_subject);
                ivSubImage = itemView.findViewById(R.id.iv_subject);
            }
        }
    }

    @Override
    public void onBackPressed() {

        switch (currentType){
            case "TOPIC-WISE":
                tvTypeDesc.setText("Select a chapter");
                currentType = "CHAPTER-WISE";
                rvMockOpType.setAdapter(new MockChapterAdapter());
                break;
            case "CHAPTER-WISE":
                currentType="SUBJECT-WISE";
                tvTypeDesc.setText("Select a subject");
                rvMockOpType.setAdapter(new MockSubjectAdapter());
                break;
            case "SUBJECT-WISE":
                tvTypeDesc.setText("Select a subject to begin test");
                super.onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }
}