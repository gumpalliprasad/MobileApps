package myschoolapp.com.gsnedutech;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Arena.Fragments.FlashEditFrag;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaRecord;
import myschoolapp.com.gsnedutech.Arena.Models.ArenaTeacherList;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlashCardsDisplayRejected extends AppCompatActivity {

    private static final String TAG = FlashCardsDisplayRejected.class.getName();

    @BindView(R.id.rv_q_nums)
    RecyclerView rvQNums;

    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    StudentObj sObj;

    public List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();
    List<ArenaTeacherList> listTeachers = new ArrayList<>();

    public int qNum = 0;

    ArenaRecord flashObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards_display_rejected);
        ButterKnife.bind(this);

        init();
    }

    void init(){

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        sObj = gson.fromJson(json, StudentObj.class);

        flashObj = (ArenaRecord) getIntent().getSerializableExtra("flashObj");

        tvComment.setText(flashObj.getTeacherReview());
        if (flashObj.getArenaName().contains("~~")){
            tvTitle.setText(flashObj.getArenaName().split("~~")[0]);
        }else {
            tvTitle.setText(flashObj.getArenaName());
        }

        rvQNums.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        rvQNums.setAdapter(new QueNumAdapter());

        getQuizDetails();
    }

    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();



        Request get = new Request.Builder()
                .url(AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + flashObj.getArenaId()+"&arenaCategory=2")
                .build();

        utils.showLog(TAG, "url "+AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + flashObj.getArenaId()+"&arenaCategory=2");

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
                                    rvQNums.getAdapter().notifyDataSetChanged();
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

    class QueNumAdapter extends RecyclerView.Adapter<QueNumAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FlashCardsDisplayRejected.this).inflate(R.layout.item_khub_que_count,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvCount.setText((position+1)+"");

            if (position==qNum){
                loadFrag();
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_correct);
                holder.tvCount.setTextColor(Color.WHITE);
            }else {
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_not_selected);
                holder.tvCount.setTextColor(Color.parseColor("#646464"));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qNum = position;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listQuestions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCount = itemView.findViewById(R.id.tv_count);
            }
        }
    }
    
    void loadFrag(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main, new FlashEditFrag())
                .commit();
    }

    public void showContinueDialog(){
        new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Successfully Updated! Do you want to submit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getTeachers(flashObj.getArenaId()+"");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    //service to get teachers of your section to send for approval
    private void getTeachers(String arenaId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("schemaName", sh_Pref.getString("schema",""));
            postObject.put("sectionId",sObj.getClassCourseSectionId()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(new AppUrls().GetTeachersForArena)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                    }
                                })
                                .setCancelable(false)
                                .show();
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
                            new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }else{
                    try {
                        JSONObject respObj = new JSONObject(resp);
                        if (respObj.getString("StatusCode").equalsIgnoreCase("200")){
                            JSONArray array = respObj.getJSONArray("arenaRecords");

                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ArenaTeacherList>>(){}.getType();
                            listTeachers.clear();
                            listTeachers.addAll(gson.fromJson(array.toString(),type));

                            if (listTeachers.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog dialog = new Dialog(FlashCardsDisplayRejected.this);
                                        dialog.setContentView(R.layout.dialog_arena_teacher_list);
                                        dialog.setCancelable(true);
                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                                        .setTitle(getResources().getString(R.string.app_name))
                                                        .setMessage("Audio Article saved in drafts in my audio. Can submit for approval later.")

                                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                finish();
                                                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                                            }
                                                        })
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });

                                        RecyclerView rvTeachers = dialog.findViewById(R.id.rv_teachers);
                                        rvTeachers.setLayoutManager(new LinearLayoutManager(FlashCardsDisplayRejected.this));
                                        rvTeachers.setAdapter(new TeacherAdapter(listTeachers,arenaId,dialog));

                                        dialog.show();
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("No Teachers assigned for your section.\nAudio article cannot be sent for approval\nPlease contact your school for more information.")

                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();
                                                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("OOps! There was a problem.\nAudio Article saved in drafts in my audio. Can submit for approval later.")

                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
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

    //submit arena for approval
    private void submitArena(String arenaId,String teacherId) {

        utils.showLoader(this);

        JSONObject postObject = new JSONObject();

        try {
            postObject.put("arenaId",arenaId);
            postObject.put("arenaStatus", "0");
            postObject.put("createdBy", sObj.getStudentId()+"");
            postObject.put("userId", sObj.getStudentId()+"");
            postObject.put("teacherId", teacherId);
            postObject.put("arenaDraftStatus","1");
            postObject.put("schemaName", sh_Pref.getString("schema",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "";

        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = new AppUrls().ReviewArenaStatus;
        }else {
            url = new AppUrls().SubmitStudentArena;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(postObject));

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        utils.showLog(TAG,"url "+url);
        utils.showLog(TAG,"url obj "+postObject.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.dismissDialog();
                        new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage("oops! something went wrong please try again later")
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    }
                                })

                                .setCancelable(false)
                                .show();
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
                            new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("oops! something went wrong please try again later")
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        }
                                    })

                                    .setCancelable(false)
                                    .show();
                        }
                    });
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FlashCardsDisplayRejected.this,"Success!",Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    utils.dismissDialog();
                                    new AlertDialog.Builder(FlashCardsDisplayRejected.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("oops! something went wrong please try again later")
                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                }
                                            })

                                            .setCancelable(false)
                                            .show();
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

    class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder>{

        List<ArenaTeacherList> teacherList; String arenaId;
        Dialog dialog;

        public TeacherAdapter(List<ArenaTeacherList> teacherList, String arenaId,Dialog dialog) {
            this.teacherList = teacherList;
            this.arenaId = arenaId;
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(FlashCardsDisplayRejected.this).inflate(R.layout.tv_teacher_name,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(listTeachers.get(position).getUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    submitArena(arenaId,listTeachers.get(position).getTeacherId()+"");
                    dialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return teacherList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tv_name);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}