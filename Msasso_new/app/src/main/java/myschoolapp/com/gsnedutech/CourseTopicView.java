package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.CustomTopic;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.SubChapterTopic;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.DrawableUtils;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import myschoolapp.com.gsnedutech.activity.ResourcesListActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CourseTopicView extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = CourseTopicView.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_topics)
    RecyclerView rvTopics;
    @BindView(R.id.rv_topic_items)
    RecyclerView rvTopicItems;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    int[] backgrounds = new DrawableUtils().getBgCourse();
    List<CustomTopic> listTypes = new ArrayList<>();
    List<SubChapterTopic> listTopics = new ArrayList<>();

    String topicId = "";
    String chapterId = "";
    String chapterCCMapId = "";

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    int selectedItem;
    MyUtils utils = new MyUtils();
    String contentType = "";
    String  testCount= "0";
    SubChapterTopic mTopic = new SubChapterTopic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_topic_view); //activity_teacher_course_topic_view
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvail = false;
        NetworkConnectivity.connectivityReceiverListener = this;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectivity, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            isNetworkAvail = false;
            utils.alertDialog(1, CourseTopicView.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail) {
                utils.dismissAlertDialog();
                getReports();
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

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listTopics.addAll((Collection<? extends SubChapterTopic>) getIntent().getSerializableExtra("topics"));

        chapterId = getIntent().getStringExtra("chapterId");
        contentType = getIntent().getStringExtra("contentType");
        chapterCCMapId = getIntent().getStringExtra(AppConst.ChapterCCMapId);
        if (listTopics.size() > 1) {
            selectedItem = Integer.parseInt(getIntent().getStringExtra("position"));
            tvTitle.setText(listTopics.get(selectedItem).getTopicName());
            topicId = listTopics.get(selectedItem).getTopicId();
            mTopic = listTopics.get(selectedItem );
        } else {
            tvTitle.setText(listTopics.get(0).getTopicName());
            topicId = listTopics.get(0).getTopicId();
            mTopic = listTopics.get(0);
        }

        if (mTopic.getVideoCount() != null && Integer.parseInt(mTopic.getVideoCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("Concept Based Learning");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getVideoCount()));
            listTypes.add(customTopic);
        }
        if (mTopic.getClassroomVideoCount() != null && Integer.parseInt(mTopic.getClassroomVideoCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("ClassRoom Videos");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getClassroomVideoCount()));
            listTypes.add(customTopic);
        }
        if (mTopic.getQuestionAnswerCount() != null && Integer.parseInt(mTopic.getQuestionAnswerCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("Question and Answers");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getQuestionAnswerCount()));
            listTypes.add(customTopic);
        }
        if (mTopic.getResourceCount() != null && Integer.parseInt(mTopic.getResourceCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("Topic Resources");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getResourceCount()));
            listTypes.add(customTopic);
        }
        if (mTopic.getAnnexureCount() != null && Integer.parseInt(mTopic.getAnnexureCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("Summary");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getAnnexureCount()));
            listTypes.add(customTopic);
        }
        if (mTopic.getImportantCount() != null && Integer.parseInt(mTopic.getImportantCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("Important Summary");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getImportantCount()));
            listTypes.add(customTopic);
        }
        if (mTopic.getQuestionsCount() != null && Integer.parseInt(mTopic.getQuestionsCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("Worksheet Practice");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getQuestionsCount()));
            listTypes.add(customTopic);
            CustomTopic customTopic1 = new CustomTopic();
            customTopic1.setName("Mock Test");
            customTopic1.setTopicCount(Integer.parseInt(mTopic.getQuestionsCount()));
            listTypes.add(customTopic1);
        }
        if (mTopic.getTextbookCount() != null && Integer.parseInt(mTopic.getTextbookCount()) > 0) {
            CustomTopic customTopic = new CustomTopic();
            customTopic.setName("TextBook Summary");
            customTopic.setTopicCount(Integer.parseInt(mTopic.getTextbookCount()));
            listTypes.add(customTopic);
        }
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(rvTopicItems.getContext(), R.anim.layout_animation_fall_down);
        rvTopicItems.setLayoutManager(new LinearLayoutManager(CourseTopicView.this));
        rvTopicItems.setAdapter(new TopicItemsAdapter(mTopic));
        rvTopicItems.scheduleLayoutAnimation();
    }

    private void getReports() {

        utils.showLoader(CourseTopicView.this);

        ApiClient apiClient = new ApiClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("schemaName",sh_Pref.getString("schema",""));
            json.put("studentId", sObj.getStudentId());
            json.put("topicId", topicId);
            json.put("topicCCMapId",  listTopics.get(0).getTopicCCMapId());
            json.put("chapterId", 0);
            json.put("subjectId", 0);
            json.put("classId", 0);
            json.put("courseId", 0);
            json.put("testType", "1");
            json.put("testTypename", "TOPIC-WISE");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(),JSON);

        utils.showLog(TAG, "TestReport Url - " + AppUrls.getStudentMockTestReportCount );
        utils.showLog(TAG, "TestReport Obj - " + json);
        Request request = apiClient.postRequest(AppUrls.getStudentMockTestReportCount ,body, sh_Pref);
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
                        String jsonResp = response.body().string();

                        utils.showLog(TAG, "Reports analysis- " + jsonResp);
                        JSONObject parentjObject = new JSONObject(jsonResp);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            testCount = parentjObject.getString("testsCount");
                            runOnUiThread(() -> rvTopicItems.getAdapter().notifyDataSetChanged());
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, CourseTopicView.this, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    class TopicItemsAdapter extends RecyclerView.Adapter<TopicItemsAdapter.ViewHolder> {
        SubChapterTopic topic;

        public TopicItemsAdapter(SubChapterTopic mTopic) {
            topic = mTopic;
        }

        @NonNull
        @Override
        public TopicItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(CourseTopicView.this).inflate(R.layout.item_topic_options, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TopicItemsAdapter.ViewHolder holder, int position) {
            CustomTopic customTopic = listTypes.get(position);
            int val = position % backgrounds.length;
            holder.flBackground.setBackgroundResource(backgrounds[val]);

            holder.tvType.setText(customTopic.getName());
            switch (customTopic.getName()) {
                case "Concept Based Learning":
                    holder.tvCountType.setText("Videos");
                    holder.tvDesc.setText("Learn better through our Simple, Bite-Sized and Engaging Animated Videos");
                    holder.tvCount.setText(customTopic.getTopicCount()+"");
                    holder.imgType.setImageResource(R.drawable.ic_whiteol_camera);

                    holder.itemView.setOnClickListener((View.OnClickListener) view -> {
                        Intent i = new Intent(CourseTopicView.this, TopicVideoActivity.class);
                        i.putExtra(AppConst.CARD_TYPE, AppConst.VIDEOS);
                        i.putExtra(AppConst.CONTENT_TYPE, contentType);
                        i.putExtra(AppConst.TOPIC, (Serializable) topic);
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "ClassRoom Videos":
                    holder.tvCountType.setText("ClassRoom Videos");
                    holder.tvDesc.setText("Learn every Concept from Teachers for a Strong Understanding");
                    holder.tvCount.setText(customTopic.getTopicCount()+"");
                    holder.imgType.setImageResource(R.drawable.ic_whiteol_camera);

                    holder.itemView.setOnClickListener(view -> {
                        Intent i = new Intent(CourseTopicView.this, TopicVideoActivity.class);
                        i.putExtra(AppConst.CARD_TYPE, AppConst.CLASS_VIDEOS);
                        i.putExtra(AppConst.CONTENT_TYPE, contentType);
                        i.putExtra(AppConst.TOPIC, (Serializable) topic);
                        startActivity(i);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Question and Answers":
                    holder.tvCountType.setVisibility(View.GONE);
                    holder.tvDesc.setText("Detailed Descriptive Solutions for all Questions");
                    holder.tvCount.setText(customTopic.getTopicCount()+"");
                    holder.imgType.setImageDrawable(getDrawable(R.drawable.ic_whiteol_qa));
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(CourseTopicView.this, QAActivity.class);
                        intent.putExtra(AppConst.TOPIC, (Serializable) topic);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Summary":
                    holder.tvCountType.setVisibility(View.GONE);
                    holder.tvCount.setVisibility(View.GONE);
                    holder.tvDesc.setText("A Brief Summary of the Chapter in a Nut Shell");
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(CourseTopicView.this, SummaryActivity.class);
                        intent.putExtra(AppConst.API_TYPE, AppConst.SUMMARY);
                        intent.putExtra(AppConst.CONTENT_TYPE, contentType);
                        intent.putExtra(AppConst.CHAPTER_ID, chapterId);
                        intent.putExtra(AppConst.CHAPTER_CC_MAP_ID, chapterCCMapId);
                        intent.putExtra(AppConst.TOPIC_CC_MAP_ID, topic.getTopicCCMapId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Important Summary":
                    holder.tvCountType.setVisibility(View.GONE);
                    holder.tvCount.setVisibility(View.GONE);
                    holder.tvDesc.setText("A Brief Summary of the Chapter in a Nut Shell");
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(CourseTopicView.this, SummaryActivity.class);
                        intent.putExtra(AppConst.API_TYPE, AppConst.IMPORTANT);
                        intent.putExtra(AppConst.CONTENT_TYPE, contentType);
                        intent.putExtra(AppConst.CHAPTER_ID, chapterId);
                        intent.putExtra(AppConst.CHAPTER_CC_MAP_ID, chapterCCMapId);
                        intent.putExtra(AppConst.TOPIC_CC_MAP_ID, topic.getTopicCCMapId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "TextBook Summary":
                    holder.tvCountType.setVisibility(View.GONE);
                    holder.tvCount.setVisibility(View.GONE);
                    holder.tvDesc.setText("A Brief Summary of the Chapter in a Nut Shell");
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(CourseTopicView.this, SummaryActivity.class);
                        intent.putExtra(AppConst.API_TYPE, AppConst.TEXT_BOOK);
                        intent.putExtra(AppConst.CONTENT_TYPE, contentType);
                        intent.putExtra(AppConst.CHAPTER_ID, chapterId);
                        intent.putExtra(AppConst.CHAPTER_CC_MAP_ID, chapterCCMapId);
                        intent.putExtra(AppConst.TOPIC_CC_MAP_ID, topic.getTopicCCMapId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Worksheet Practice":
                    holder.tvCountType.setText("Sheets");
                    holder.tvDesc.setText("Learn by Solving Questions which Adapts to your Skill and Pace");
                    holder.tvCount.setText(customTopic.getTopicCount()+"");
                    holder.itemView.setOnClickListener(view -> {
                        Intent testdefault_intent = new Intent(CourseTopicView.this, CoursePracticeTest.class);
                        testdefault_intent.putExtra("contentType", getIntent().getStringExtra("contentType"));
                        testdefault_intent.putExtra("topicId", topicId);
                        testdefault_intent.putExtra("que_No", 0);
                        testdefault_intent.putExtra(AppConst.TopicCCMapId, listTopics.get(selectedItem).getTopicCCMapId());
                        testdefault_intent.putExtra("topicName", getIntent().getStringExtra("topicName"));
                        testdefault_intent.putExtra("subId", getIntent().getStringExtra("subId"));
                        startActivity(testdefault_intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Topic related Activities":
                    holder.tvCountType.setVisibility(View.GONE);
                    holder.tvCount.setVisibility(View.GONE);

                    break;
                case "Synopsis":
                    holder.tvCountType.setVisibility(View.GONE);
                    holder.tvDesc.setText("Learn from the original source - Textbook");
                    holder.tvCount.setVisibility(View.GONE);
                    holder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(CourseTopicView.this, TextBookActivity.class);
                        intent.putExtra("topicId", topicId);
                        intent.putExtra(AppConst.TopicCCMapId, listTopics.get(selectedItem).getTopicCCMapId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Mock Test":
                    holder.tvCountType.setText("Tests Taken");
                    holder.tvDesc.setText("Create Real Time Exam -like Situation and Compete with Fellow Students");
                    holder.tvCount.setText(testCount);
                    holder.itemView.setOnClickListener(view -> {
                        Intent testmock_intent = new Intent(CourseTopicView.this, TopicMockTestInfo.class);
                        testmock_intent.putExtra("contentType", getIntent().getStringExtra("contentType"));
                        testmock_intent.putExtra("courseId", sObj.getCourseId()+"");
                        testmock_intent.putExtra("classId", sObj.getClassId()+"");
                        testmock_intent.putExtra("subId", getIntent().getStringExtra("subId"));
                        testmock_intent.putExtra("chapterId", chapterId);
                        testmock_intent.putExtra(AppConst.ChapterCCMapId, chapterCCMapId);
                        testmock_intent.putExtra("topicId", topic.getTopicId());
                        testmock_intent.putExtra(AppConst.TopicCCMapId, topic.getTopicCCMapId());
                        testmock_intent.putExtra("topicName", topic.getTopicName());
                        startActivity(testmock_intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    });
                    break;
                case "Topic Resources":
                    holder.tvCountType.setText("Topics");
                    holder.tvDesc.setText("");
                    holder.tvCount.setText(customTopic.getTopicCount()+"");
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(CourseTopicView.this, ResourcesListActivity.class);
                        intent.putExtra(AppConst.TOPIC, topic);
                        startActivity(intent);
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return listTypes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvType, tvDesc, tvPublishedBy, tvCountType, tvCount, tvPublishedByHead;
            ImageView imgType;
            FrameLayout flBackground;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                flBackground = itemView.findViewById(R.id.fl_background);
                imgType = itemView.findViewById(R.id.img_type);
                tvType = itemView.findViewById(R.id.tv_type);
                tvDesc = itemView.findViewById(R.id.tv_desc);
                tvPublishedBy = itemView.findViewById(R.id.tv_published_by);
                tvCountType = itemView.findViewById(R.id.tv_count_type);
                tvCount = itemView.findViewById(R.id.tv_count);
                tvPublishedByHead = itemView.findViewById(R.id.tv_published_by_head);
            }
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==2){
            getReports();
        }
        else if (requestCode == 1234 && resultCode == 4321){
            getQuestionCount();
        }
    }

   */


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}