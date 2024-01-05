package myschoolapp.com.gsnedutech.QBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.PostFileObject;
import myschoolapp.com.gsnedutech.Models.StdnTestClass;
import myschoolapp.com.gsnedutech.Models.StdnTestDefClsCourseSub;
import myschoolapp.com.gsnedutech.Models.StdnTestSubject;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.SubChapter;
import myschoolapp.com.gsnedutech.Models.SubChapterTopic;
import myschoolapp.com.gsnedutech.QBox.model.QboxQuestion;
import myschoolapp.com.gsnedutech.QBox.model.SearchQbox;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.ApiClient;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.FileUtil;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AskADoubt extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SriRam -" + AskADoubt.class.getName();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;
    AmazonS3Client s3Client1;

    List<StdnTestDefClsCourseSub> stdnTestCoursesList = new ArrayList<>();
    List<SubChapter> chaptertopicList = new ArrayList<>();

    List<StdnTestClass> list_classes = new ArrayList<>();
    List<StdnTestSubject> list_sub = new ArrayList<>();
    List<SubChapter> list_chap = new ArrayList<>();
    List<SubChapterTopic> list_topic = new ArrayList<>();
    int offset=0;
    boolean hasNextPage = false;
    int itemCount = 15;

    int courseSelected = 0, classSelected = 0, subjectSelected = 0, chapterSelected = 0, topicSelected = 0;


    @BindView(R.id.sp_course)
    Spinner spCourse;
    @BindView(R.id.sp_class)
    Spinner spClasses;

    @BindView(R.id.sp_subjects)
    Spinner spSubjects;

    @BindView(R.id.sp_chapters)
    Spinner spChapters;

    @BindView(R.id.sp_topics)
    Spinner spTopics;

    @BindView(R.id.et_message)
    EditText etMessage;

    @BindView(R.id.tv_message_count)
    TextView tvMessageCount;

    @BindView(R.id.tv_proceed)
    TextView tvProceed;


    @BindView(R.id.rv_images)
    RecyclerView rvAssignFiles;

    @BindView(R.id.ll_suggestions)
    LinearLayout llSuggestions;
    @BindView(R.id.tv_view_all)
    TextView tvViewAll;
    @BindView(R.id.rv_suggestions)
    RecyclerView rvSuggestions;

    @BindView(R.id.tv_no_suggestions)
    TextView tvNoSuggestions;

    MyUtils utils = new MyUtils();


    List<Uri> attachedListFiles = new ArrayList<>();
    List<String> fileName = new ArrayList<>();
    List<PostFileObject> listFiles = new ArrayList<>();
    List<String> keyName = new ArrayList<>();


    private Uri picUri;
    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE_MULTIPLE = 2;

    UploadFilesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_a_doubt_new);

        ButterKnife.bind(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        etMessage.addTextChangedListener(mTextEditorWatcher);
        tvProceed.setOnClickListener(this);

        rvAssignFiles.setLayoutManager(new LinearLayoutManager(AskADoubt.this, RecyclerView.HORIZONTAL, false));
        rvAssignFiles.setAdapter(new AskADoubt.FileAdapter());


        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);


        if (NetworkConnectivity.isConnected(AskADoubt.this)) {
            getAllStudentClassCourseSubjects();
        } else {
            new MyUtils().alertDialog(1, AskADoubt.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        }

        tvViewAll.setOnClickListener(v -> {
            Intent suggestIntent = new Intent(AskADoubt.this, SuggestedQuestions.class);
            suggestIntent.putExtra("keyword", etMessage.getText().toString());
            startActivity(suggestIntent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        });


    }

    private void getAllStudentClassCourseSubjects() {
        ApiClient apiClient = new ApiClient();
        utils.showLog(TAG, "Log - " + new AppUrls().GetAllStudentClassCourseSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&branchId=" + sObj.getBranchId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId());

        Request request = apiClient.getRequest(new AppUrls().GetAllStudentClassCourseSubjects + "schemaName=" + sh_Pref.getString("schema", "") + "&branchId=" + sObj.getBranchId()
                + "&classId=" + sObj.getClassId() + "&courseId=" + sObj.getCourseId() + "&studentId=" + sObj.getStudentId(), sh_Pref);

        apiClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (AskADoubt.this != null) {
                    utils.dismissDialog();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    try {
                        String responce = response.body().string();
                        utils.showLog(TAG, "Students Subjects - " + responce);
                        JSONObject parentjObject = new JSONObject(responce);
                        if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = parentjObject.getJSONArray("defaultCourseClasses");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StdnTestDefClsCourseSub>>() {
                            }.getType();

                            stdnTestCoursesList.clear();
                            stdnTestCoursesList.addAll(gson.fromJson(jsonArr.toString(), type));

                            utils.showLog(TAG, "CoursesList - " + stdnTestCoursesList.size());

                            if (stdnTestCoursesList.size() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setSpinners();
                                    }
                                });

                            }

                        } else if (parentjObject.getString("StatusCode").equalsIgnoreCase("300")) {
                            if (AskADoubt.this != null) {
                                utils.dismissDialog();
                            }
                        }else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, AskADoubt.this, message, sh_Pref);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void setSpinners() {
        List<String> spCourses_list = new ArrayList<>();
        spCourses_list.add("Choose Course");
        for (int i = 0; i < stdnTestCoursesList.size(); i++) {
            spCourses_list.add(stdnTestCoursesList.get(i).getCourseName());
        }
        ArrayAdapter<String> dataAdapteCrourse = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spCourses_list);
        dataAdapteCrourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCourse.setAdapter(dataAdapteCrourse);
        spCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spCourse.getSelectedItem().toString().equalsIgnoreCase("Choose Course")) {
                    list_classes.clear();
                    courseSelected = position - 1;
                    list_classes.clear();
                    list_classes.addAll(stdnTestCoursesList.get(courseSelected).getClasses());
                    List<String> spClass_list = new ArrayList<>();
                    spClass_list.add("Choose Class");
                    for (int j = 0; j < list_classes.size(); j++) {
                        spClass_list.add(list_classes.get(j).getClassName());
                    }
                    ArrayAdapter<String> dataAdapterClass = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spClass_list);
                    dataAdapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spClasses.setAdapter(dataAdapterClass);
                    spClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (!spClasses.getSelectedItem().toString().equalsIgnoreCase("Choose Class")) {
                                list_sub.clear();
                                classSelected = position - 1;
                                list_sub.addAll(stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getSubjects());
                                List<String> spSub_list = new ArrayList<>();
                                spSub_list.add("Choose Subject");
                                for (int j = 0; j < list_sub.size(); j++) {
                                    spSub_list.add(list_sub.get(j).getSubjectName());
                                }
                                ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spSub_list);
                                dataAdapterSub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spSubjects.setAdapter(dataAdapterSub);
                                spSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        chaptertopicList.clear();
                                        if (!spSubjects.getSelectedItem().toString().equalsIgnoreCase("Choose Subject")) {
                                            list_chap.clear();
                                            subjectSelected = position - 1;
                                            getChapterTopics();
                                        }else {
                                            List<String> spChap_list = new ArrayList<>();
                                            spChap_list.add("Choose Chapter");
                                            ArrayAdapter<String> dataAdapterCh = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spChap_list);
                                            dataAdapterCh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spChapters.setAdapter(dataAdapterCh);
                                            List<String> spTopics_list = new ArrayList<>();
                                            spTopics_list.add("Choose Topic");
                                            ArrayAdapter<String> dataAdapterTopic = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spTopics_list);
                                            dataAdapterTopic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spTopics.setAdapter(dataAdapterTopic);
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }else {
                                List<String> spSub_list = new ArrayList<>();
                                spSub_list.add("Choose Subject");
                                ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spSub_list);
                                dataAdapterSub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spSubjects.setAdapter(dataAdapterSub);
                                List<String> spChap_list = new ArrayList<>();
                                spChap_list.add("Choose Chapter");
                                ArrayAdapter<String> dataAdapterCh = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spChap_list);
                                dataAdapterCh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spChapters.setAdapter(dataAdapterCh);
                                List<String> spTopics_list = new ArrayList<>();
                                spTopics_list.add("Choose Topic");
                                ArrayAdapter<String> dataAdapterTopic = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spTopics_list);
                                dataAdapterTopic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spTopics.setAdapter(dataAdapterTopic);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }else{
                    List<String> spClass_list = new ArrayList<>();
                    spClass_list.add("Choose Class");
                    ArrayAdapter<String> dataAdapterClass = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spClass_list);
                    dataAdapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spClasses.setAdapter(dataAdapterClass);
                    List<String> spSub_list = new ArrayList<>();
                    spSub_list.add("Choose Subject");
                    ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spSub_list);
                    dataAdapterSub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSubjects.setAdapter(dataAdapterSub);
                    List<String> spChap_list = new ArrayList<>();
                    spChap_list.add("Choose Chapter");
                    ArrayAdapter<String> dataAdapterCh = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spChap_list);
                    dataAdapterCh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spChapters.setAdapter(dataAdapterCh);
                    List<String> spTopics_list = new ArrayList<>();
                    spTopics_list.add("Choose Topic");
                    ArrayAdapter<String> dataAdapterTopic = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spTopics_list);
                    dataAdapterTopic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTopics.setAdapter(dataAdapterTopic);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setChapTopicSpinners() {
        List<String> spChap_list = new ArrayList<>();
        spChap_list.add("Choose Chapter");
        for (int i = 0; i < chaptertopicList.size(); i++) {
            spChap_list.add(chaptertopicList.get(i).getChapterName());
        }
        ArrayAdapter<String> dataAdapteChap = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spChap_list);
        dataAdapteChap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChapters.setAdapter(dataAdapteChap);
        spChapters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spChapters.getSelectedItem().toString().equalsIgnoreCase("Choose Chapter")) {
                    list_topic.clear();
                    chapterSelected = position-1;
                    list_topic.addAll(chaptertopicList.get(chapterSelected).getChapterTopic());
                    List<String> spTopic_list = new ArrayList<>();
                    spTopic_list.add("Choose Topic");
                    for (int j = 0; j < list_topic.size(); j++) {
                        spTopic_list.add(list_topic.get(j).getTopicName());
                    }
                    ArrayAdapter<String> dataAdapterSub = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spTopic_list);
                    dataAdapterSub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTopics.setAdapter(dataAdapterSub);
                    spTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (!spTopics.getSelectedItem().toString().equalsIgnoreCase("Choose Topic")) {
                                topicSelected = position-1;
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }else {
                    List<String> spTopics_list = new ArrayList<>();
                    spTopics_list.add("Choose Topic");
                    ArrayAdapter<String> dataAdapterTopic = new ArrayAdapter<String>(AskADoubt.this, android.R.layout.simple_spinner_item, spTopics_list);
                    dataAdapterTopic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spTopics.setAdapter(dataAdapterTopic);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    void getChapterTopics() {

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "") +
                        "&contentType=" + stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getSubjects().get(subjectSelected).getContentType() +
                        "&subjectId=" + stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getSubjects().get(subjectSelected).getSubjectId()
                        + "&sectionId=" + sObj.getClassCourseSectionId())
                .build();

        utils.showLog(TAG, "url -" + new AppUrls().GetContentAccessBySection + "schemaName=" + sh_Pref.getString("schema", "") + "&contentType=" + stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getSubjects().get(subjectSelected).getContentType() +
                "&subjectId=" + stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getSubjects().get(subjectSelected).getSubjectId()
                + "&sectionId=" + sObj.getClassCourseSectionId());

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

                try {

                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    } else {
                        String resp = responseBody.string();

                        utils.showLog(TAG, "response- " + resp);

                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {

                            JSONArray jsonArr = ParentjObject.getJSONArray("ContentOwner");


                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SubChapter>>() {
                            }.getType();

                            chaptertopicList.clear();
                            chaptertopicList.addAll(gson.fromJson(jsonArr.toString(), type));

                            if (chaptertopicList.size() > 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setChapTopicSpinners();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AskADoubt.this,"No Chapters available for this subject. Please select a different one.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AskADoubt.this,"No Chapters available for this subject. Please select a different one.",Toast.LENGTH_LONG).show();
                                }
                            });
                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
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

    class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @NonNull
        @Override
        public FileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new FileAdapter.ViewHolder(LayoutInflater.from(AskADoubt.this).inflate(R.layout.item_upload_laout, viewGroup, false));
            } else {
                return new FileAdapter.ViewHolder(LayoutInflater.from(AskADoubt.this).inflate(R.layout.item_files, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull FileAdapter.ViewHolder viewHolder, int i) {

            if (i == 0) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fileName.size() <= 2) {
                            final Dialog dialog = new Dialog(AskADoubt.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_imgselector);
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int wwidth = metrics.widthPixels;
                            dialog.getWindow().setLayout((int) (wwidth * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.setCancelable(true);

                            TextView tvCam = dialog.findViewById(R.id.tv_cam);
                            tvCam.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                            String imageFileName = "JPEG_" + timeStamp + "_";
                                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
                                            // Save a file: path for use with ACTION_VIEW intents
                                            picUri =  Uri.fromFile(image);
                                            if (image != null) {
                                                Uri photoURI = FileProvider.getUriForFile(AskADoubt.this,
                                                        getPackageName()+".provider",
                                                        image);
                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                                startActivityForResult(takePictureIntent, 1);
                                            }
                                        }
                                        else {
                                            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis() + "/picture.jpg";
                                            File imageFile = new File(imageFilePath);
                                            picUri = Uri.fromFile(imageFile); // convert path to Uri
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                                            startActivityForResult(takePictureIntent, 1);
                                        }
                                    } catch (ActivityNotFoundException | IOException anfe) {
                                        //display an error message
                                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                                        Toast.makeText(AskADoubt.this, errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });

                            TextView tvFiles = dialog.findViewById(R.id.tv_gallery);
                            tvFiles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("*/*");
                                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                        else {
                            Toast.makeText(AskADoubt.this, "Max 3 files allowed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                viewHolder.ivRemove.setVisibility(View.VISIBLE);


                viewHolder.tvFilename.setText(fileName.get(i - 1));
                if (fileName.get(i - 1).contains(".pdf")) {
                    viewHolder.ivFile.setImageResource(R.drawable.ic_pdf);
                } else {
                    viewHolder.ivFile.setImageResource(R.drawable.ic_img_attachment);
                }


                viewHolder.ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        attachedListFiles.remove(i - 1);
                        fileName.remove(i - 1);
                        notifyDataSetChanged();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return fileName.size() + 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFilename;
            ImageView ivRemove, ivFile;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFilename = itemView.findViewById(R.id.tv_file_name);
                ivRemove = itemView.findViewById(R.id.iv_remove);
                ivFile = itemView.findViewById(R.id.iv_file);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        picUri =  Uri.fromFile(image);
        return image;
    }

    class UploadFilesAdapter extends RecyclerView.Adapter<UploadFilesAdapter.ViewHolder> {

        List<Uri> listFiles;

        UploadFilesAdapter(List<Uri> listFiles) {
            this.listFiles = listFiles;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public UploadFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            return new UploadFilesAdapter.ViewHolder(LayoutInflater.from(AskADoubt.this).inflate(R.layout.item_image, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull UploadFilesAdapter.ViewHolder holder, int position) {
            holder.tvFileName.setText("File" + (position + 1) + ".JPG");
        }

        @Override
        public int getItemCount() {
            return listFiles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFileName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvFileName = itemView.findViewById(R.id.tv_file_name);
            }
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            tvMessageCount.setText(String.valueOf(s.length() + "/200"));
        }

        public void afterTextChanged(Editable s) {
            if (s.toString().length()>4){
                getSuggestions(s.toString());
            }
            else {
                tvViewAll.setVisibility(View.GONE);
                rvSuggestions.setVisibility(View.GONE);
                tvNoSuggestions.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_proceed:

                if (spCourse.getSelectedItemPosition() != 0){
                    if (spClasses.getSelectedItemPosition() != 0){
                        if (spSubjects.getSelectedItemPosition() != 0){
                            if (spChapters.getSelectedItemPosition() != 0){
                                if (spTopics.getSelectedItemPosition() != 0){
                                    if (!etMessage.getText().toString().isEmpty()){
                                        if (attachedListFiles.size() > 0) {
                                            uploadToS3();
                                        } else {
                                            postQuestion();
                                        }
                                    }else {
                                        Toast.makeText(this, "Enter your doubt", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(this, "Select Topic", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(this, "Select Chapter", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(this, "Select Subject", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "Select Class", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Select Course", Toast.LENGTH_SHORT).show();
                }

//                if (spSubjects.getSelectedItemPosition() != 0 && spTopics.getSelectedItemPosition() != 0
//                        && spChapters.getSelectedItemPosition() != 0 && !etMessage.getText().toString().isEmpty()) {
//                    if (attachedListFiles.size() > 0) {
//                        uploadToS3();
//                    } else {
//                        postQuestion();
//                    }
//                } else {
//                    Toast.makeText(this, "Select all fields", Toast.LENGTH_SHORT).show();
//                }
                break;
        }

    }

    void uploadToS3() {

        utils.showLoader(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Date expiration = new Date();
                    long msec = expiration.getTime();
                    msec += 1000 * 60 * 60; // 1 hour.
                    expiration.setTime(msec);

                    int c = 0;

                    for (int i = 0; i < attachedListFiles.size(); i++) {
                        c++;
                        File file = FileUtil.from(AskADoubt.this, attachedListFiles.get(i));
//                        String ext = file.getName().split("\\.")[(file.getName().split("\\.")).length-1];
                        File f = new File(Environment.getExternalStorageState(), file.getName());
                        boolean success = file.renameTo(file);


                        if (success) {
                            utils.showLog(TAG, "url - " + "qboz/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "/" + f.getName());
                            keyName.add("qboz/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "/" + f.getName());
                            s3Client1 = new AmazonS3Client(new BasicAWSCredentials(sh_Pref.getString("akey", ""), sh_Pref.getString("skey", "")),
                                    Region.getRegion(Regions.AP_SOUTH_1));


                            PutObjectRequest por = new PutObjectRequest(
                                    sh_Pref.getString("bucket", ""),
                                    "qboz/" + sObj.getStudentId() + "/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "/" + f.getName(), file);


                            //making the object Public
                            por.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3Client1.putObject(por);

                            utils.showLog(TAG, "urls - " + s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i)));

                            String url = s3Client1.getResourceUrl(sh_Pref.getString("bucket", ""), keyName.get(i));

                            PostFileObject postFileObject = new PostFileObject();
                            postFileObject.setFileName(f.getName());
                            postFileObject.setFilePath(url);
                            listFiles.add(postFileObject);
                        } else {
                            Toast.makeText(AskADoubt.this, "Rename failed!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            postQuestion();
                        }
                    });
                    for (int i = 0; i < listFiles.size(); i++) {
                        Log.v(TAG, "listfile " + i + " - " + listFiles.get(i).getFileName());
                        Log.v(TAG, "listfile " + i + " - " + listFiles.get(i).getFilePath());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    utils.showLog(TAG, "error " + e.getMessage());
                }
            }
        }).start();

    }

    void postQuestion() {
        JSONArray filesArray = new JSONArray();

        if (listFiles.size() > 0) {
            for (int i = 0; i < listFiles.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("qboxFilePath",  listFiles.get(i).getFilePath());
                    jsonObject.put("qboxFileName", listFiles.get(i).getFileName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                filesArray.put(jsonObject);

            }
        }

        JSONObject postObject = new JSONObject();
        try {
            postObject.put("schemaName", sh_Pref.getString("schema", ""));
            postObject.put("qboxTitle", "NA");
            postObject.put("qboxQuestion", etMessage.getText().toString());
            postObject.put("qboxFileCount", "" + listFiles.size());
            postObject.put("courseId", stdnTestCoursesList.get(courseSelected).getCourseId());
            postObject.put("classId", stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getClassId());
            postObject.put("subjectId", stdnTestCoursesList.get(courseSelected).getClasses().get(classSelected).getSubjects().get(subjectSelected).getSubjectId());
            postObject.put("chapterId", chaptertopicList.get(chapterSelected).getChapterId());
            postObject.put("topicId", chaptertopicList.get(chapterSelected).getChapterTopic().get(topicSelected).getTopicId());
            postObject.put("chapterCCMapId", chaptertopicList.get(chapterSelected).getChapterCCMapId());
            postObject.put("topicCCMapId", chaptertopicList.get(chapterSelected).getChapterTopic().get(topicSelected).getTopicCCMapId());
            postObject.put("studentId", sObj.getStudentId());
            postObject.put("createdBy", sObj.getStudentId());
            postObject.put("sectionId", sObj.getClassCourseSectionId());
            postObject.put("filesArray", filesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        utils.showLog(TAG, "post Question URL " + postObject.toString());
        utils.showLog(TAG, "post Question obj " + postObject.toString());

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Log.v(TAG, "url - " + new AppUrls().InsertStudentQBoxQuestion);

        Request request = new Request.Builder()
                .url(new AppUrls().InsertStudentQBoxQuestion)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                utils.showLog(TAG, "response " + resp);

                try {
                    JSONObject json = new JSONObject(resp);
                    if (json.getString("StatusCode").equalsIgnoreCase("200")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new AlertDialog.Builder(AskADoubt.this).
                                        setTitle("Ask A Doubt").
                                        setMessage("Doubt Posted Successfully!").
                                        setPositiveButton("Ok", (dialog, which) -> {
                                            finish();
                                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                        }).setCancelable(false).show();

//                                Toast.makeText(AskADoubt.this, "Success!", Toast.LENGTH_SHORT).show();

//                                onBackPressed();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
//                //get the Uri for the captured image

                attachedListFiles.add(picUri);

                File file = null;
                try {
                    file = FileUtil.from(AskADoubt.this, picUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileName.add(file.getName());
                rvAssignFiles.setAdapter(new FileAdapter());


            } else if (requestCode == PICK_IMAGE_MULTIPLE && null != data) {
                // Get the Image from data
                try {
                    if (data.getClipData() == null) {
                        attachedListFiles.add(data.getData());
                        Uri selectedImageURI = data.getData();
                        File file = FileUtil.from(AskADoubt.this, selectedImageURI);
                        fileName.add(file.getName());
                    } else {

                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            attachedListFiles.add(data.getClipData().getItemAt(index).getUri());
                            File file = FileUtil.from(AskADoubt.this, attachedListFiles.get(index));
                            fileName.add(file.getName());
                        }
                    }

                    Log.v(TAG, "mImageUri F Filemanager- " + attachedListFiles.size());
                    rvAssignFiles.setAdapter(new FileAdapter());

                } catch (Exception e) {
                    Toast.makeText(AskADoubt.this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                    Log.v(TAG, e + "");
                }

            } else {
                Toast.makeText(AskADoubt.this, "You haven't picked any File", Toast.LENGTH_LONG).show();
            }

        }


    }

    void getSuggestions(String searchKeyWord){
//        utils.showLoader(AskADoubt.this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("searchString", searchKeyWord);
            postObject.put("qboxStatus", 2);
            postObject.put("itemCount", itemCount);
            postObject.put("offset", offset+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().AutoCompleteQboxSuggestions);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().AutoCompleteQboxSuggestions)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        utils.dismissDialog();
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
//                            utils.dismissDialog();
                        }
                    });
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray jar = jsonObject.getJSONArray("qboxQuestions");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<SearchQbox>>() {
                            }.getType();
                            List<SearchQbox> list = new ArrayList<>();
                            list.addAll(gson.fromJson(jar.toString(), type));
                            if (list.size()>0){
                                runOnUiThread(() -> {
                                    tvViewAll.setVisibility(View.VISIBLE);
                                    tvNoSuggestions.setVisibility(View.GONE);
                                    rvSuggestions.setVisibility(View.VISIBLE);
                                    rvSuggestions.setLayoutManager(new LinearLayoutManager(AskADoubt.this));
                                    rvSuggestions.setAdapter(new SuggestAdapter(list));
                                });
                            }
                            else {
                                runOnUiThread(() -> {
                                    tvViewAll.setVisibility(View.GONE);
                                    rvSuggestions.setVisibility(View.GONE);
                                    tvNoSuggestions.setVisibility(View.VISIBLE);
                                });

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(() -> {
                                    tvViewAll.setVisibility(View.GONE);
                                    rvSuggestions.setVisibility(View.GONE);
                                    tvNoSuggestions.setVisibility(View.VISIBLE);
                                });

                            }
                        });
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        utils.dismissDialog();
                    }
                });

            }
        });

    }

    class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {

        List<SearchQbox> questions;

        public SuggestAdapter(List<SearchQbox> questions) {
            this.questions = questions;
        }

        @NonNull
        @Override
        public SuggestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SuggestAdapter.ViewHolder(LayoutInflater.from(AskADoubt.this).inflate(R.layout.item_qbox_suggestion, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestAdapter.ViewHolder holder, int position) {

            holder.setIsRecyclable(false);

            holder.tvSuggestQuestion.setText(questions.get(position).getQboxQuestion());

            holder.itemView.setOnClickListener(view -> {
                getQuestionDetailsById(questions.get(position).getStuQboxId());
            });

//            if (position == (questions.size()-1)){
//                utils.showLoader(TeacherQBoxQuestions.this);
//                if (hasNextPage){
//                    offset = offset+itemCount;
//                    getTeacherQboxQuestions();
//                }else {
//                    utils.dismissDialog();
//                }
//            }


        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvSuggestQuestion;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvSuggestQuestion = itemView.findViewById(R.id.tv_sug_question);

            }
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Are you sure you want to go back?\nAll progress will be lost.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AskADoubt.super.onBackPressed();
                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setCancelable(true)
                .show();
    }

    void getQuestionDetailsById(String stuQboxId){
        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("myUserId", sObj.getStudentId());
            postObject.put("stuqboxId", stuQboxId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        utils.showLog(TAG,"url "+new AppUrls().AutoCompleteQboxSuggestions);
        utils.showLog(TAG,"url obj "+postObject.toString());


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetQboxDetailQuestionById)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                            JSONArray jar = jsonObject.getJSONArray("qboxReplies");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<QboxQuestion>>() {
                            }.getType();
                            if (jar.length()>0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<QboxQuestion> list = gson.fromJson(jar.toString(), type);
                                        Intent queIntent = new Intent(AskADoubt.this, QboxQuestionDetails.class);
                                        queIntent.putExtra("queObj", list.get(0));
                                        startActivity(queIntent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                utils.dismissDialog();
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
}