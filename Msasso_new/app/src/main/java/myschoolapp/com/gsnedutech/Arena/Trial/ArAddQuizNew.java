package myschoolapp.com.gsnedutech.Arena.Trial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import myschoolapp.com.gsnedutech.Arena.Models.ArenaQuizQuestionFiles;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.R;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArAddQuizNew extends AppCompatActivity {

    private static final String TAG = ArAddQuizNew.class.getName();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rv_q_num)
    RecyclerView rvQNum;

    public String arenaId;
    public String typeOne;

    public int count,currentPosition = 0;

    int stepNo = 1;

    int qNum;
    List<ArenaQuizQuestionFiles> listQuestions = new ArrayList<>();

    MyUtils utils = new MyUtils();
    SharedPreferences sh_Pref;
    StudentObj sObj;
    TeacherObj tObj;

    public Fragment myFrag = null;

    public QuizPreview qpFrag = null;

    boolean isPreview = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_add_quiz_new);
        ButterKnife.bind(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init();

    }

    void init(){

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            Gson gson = new Gson();
            String json = sh_Pref.getString("teacherObj", "");
            tObj = gson.fromJson(json, TeacherObj.class);
        }
        else {
            Gson gson = new Gson();
            String json = sh_Pref.getString("studentObj", "");
            sObj = gson.fromJson(json, StudentObj.class);
        }

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent().hasExtra("arenaId")){
            arenaId = getIntent().getStringExtra("arenaId");
            count = Integer.parseInt(getIntent().getStringExtra("count"));
            rvQNum.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
            rvQNum.setAdapter(new QueCountAdapter());
            String s = getIntent().getStringExtra("title");
            if (s.contains("~~")){
                String[] str = s.split("~~");
                tvTitle.setText(str[0]);
            }else {
                tvTitle.setText(s);
            }
            getQuizDetails();
            //get quiz details and set Fragment
        }else {
            loadFragment();
        }
    }


    void getQuizDetails(){

        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = "";
        if (sh_Pref.getBoolean("teacher_loggedin", false)) {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + arenaId + "&userId="+tObj.getUserId()+"&arenaCategory=1";
        }else {
            url = AppUrls.GetQuizQuestionsById +"schemaName=" +sh_Pref.getString("schema","")+ "&arenaId=" + arenaId+"&arenaCategory=1";
        }

        Request get = new Request.Builder()
                .url(url)
                .build();

        utils.showLog(TAG, "url "+url);

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
                                    currentPosition = listQuestions.size();
                                    if (currentPosition==count){
                                        stepNo = 3;
                                        loadFragment();
                                    }else if(currentPosition==0){
                                        new AlertDialog.Builder(ArAddQuizNew.this)
                                                .setTitle(getResources().getString(R.string.app_name))
                                                .setMessage("Please select a type and start entering your questions.")
                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog1, int which) {
                                                        dialog1.dismiss();
                                                        Dialog dialog = new Dialog(ArAddQuizNew.this);
                                                        dialog.setContentView(R.layout.layout_arena_quiz_option);
                                                        dialog.setCancelable(false);
                                                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
                                                        dialog.show();

                                                        dialog.findViewById(R.id.tv_close_dialog).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Toast.makeText(ArAddQuizNew.this,"Please select an option to continue.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        dialog.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                typeOne = "MCQ";
                                                                stepNo = 2;
                                                                loadFragment();
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        dialog.findViewById(R.id.cv_maq).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                typeOne = "MAQ";
                                                                stepNo = 2;
                                                                loadFragment();
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        dialog.findViewById(R.id.cv_tf).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                typeOne = "TOF";
                                                                stepNo = 2;
                                                                loadFragment();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                    }
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                    else {

                                        qNum = 0;
                                        stepNo = 4;
                                        loadFragment();


                                    }
                                }
                            });



                        }
                        else if(jsonObject.getString("StatusCode").equalsIgnoreCase("300")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(ArAddQuizNew.this)
                                            .setTitle(getResources().getString(R.string.app_name))
                                            .setMessage("Please select a type and start entering your questions.")
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog1, int which) {
                                                    dialog1.dismiss();
                                                    Dialog dialog = new Dialog(ArAddQuizNew.this);
                                                    dialog.setContentView(R.layout.layout_arena_quiz_option);
                                                    dialog.setCancelable(false);
                                                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                    dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
                                                    dialog.show();

                                                    dialog.findViewById(R.id.tv_close_dialog).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Toast.makeText(ArAddQuizNew.this,"Please select an option to continue.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    dialog.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            typeOne = "MCQ";
                                                            stepNo = 2;
                                                            loadFragment();
                                                            dialog.dismiss();
                                                        }
                                                    });

                                                    dialog.findViewById(R.id.cv_maq).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            typeOne = "MAQ";
                                                            stepNo = 2;
                                                            loadFragment();
                                                            dialog.dismiss();
                                                        }
                                                    });

                                                    dialog.findViewById(R.id.cv_tf).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            typeOne = "TOF";
                                                            stepNo = 2;
                                                            loadFragment();
                                                            dialog.dismiss();
                                                        }
                                                    });
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

    void loadFragment(){
        Fragment fragment = null;
        switch (stepNo){
            case 1:
                rvQNum.setVisibility(View.GONE);
                fragment = new QuizDetails();
                break;
            case 2:
                rvQNum.setVisibility(View.VISIBLE);
                rvQNum.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
                rvQNum.setAdapter(new QueCountAdapter());
                fragment = new AddQuestion();
                break;
            case 3:
                rvQNum.setVisibility(View.GONE);
                fragment = new QuizPreview();
                qpFrag = (QuizPreview) fragment;
                break;
            case 4:
                if (myFrag==null){
                    if (isPreview){
                        rvQNum.setVisibility(View.GONE);
                    }else {
                        rvQNum.setVisibility(View.VISIBLE);
                    }
                }else {
                    rvQNum.setVisibility(View.VISIBLE);
                }
                fragment = new QuizEditingFrag();
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    public void loadNewFrag(){
        stepNo = 2;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, myFrag)
                .commit();
        myFrag = null;
    }

    class QueCountAdapter extends RecyclerView.Adapter<QueCountAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ArAddQuizNew.this).inflate(R.layout.item_khub_que_count,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvCount.setText((position+1)+"");

            if (position==currentPosition){
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_selected);
                holder.tvCount.setTextColor(Color.WHITE);
            }else if (position<currentPosition){
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_correct);
                holder.tvCount.setTextColor(Color.WHITE);
            }else {
                holder.tvCount.setBackgroundResource(R.drawable.bg_khub_ques_not_selected);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position > currentPosition){
                        Toast.makeText(ArAddQuizNew.this,"Please complete this question first!",Toast.LENGTH_SHORT).show();
                    }else if(position < currentPosition){
                        int x = getSupportFragmentManager().getFragments().size();
                        if (getSupportFragmentManager().getFragments().get((x-1)).getClass().getSimpleName().trim().equalsIgnoreCase("AddQuestion")) {
                            myFrag = getSupportFragmentManager().getFragments().get((x - 1));
                        }

                        stepNo = 4;
                        qNum = position;
                        loadFragment();
                    }else {
                        if (myFrag!=null){
                            loadNewFrag();
                        }else {
                            Dialog dialog = new Dialog(ArAddQuizNew.this);
                            dialog.setContentView(R.layout.layout_arena_quiz_option);
                            dialog.setCancelable(false);
                            dialog.getWindow().setGravity(Gravity.BOTTOM);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.BottomDialogAnimation;
                            dialog.show();

                            dialog.findViewById(R.id.tv_close_dialog).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(ArAddQuizNew.this,"Please select an option to continue.",Toast.LENGTH_SHORT).show();
                                }
                            });

                            dialog.findViewById(R.id.cv_quiz).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    typeOne = "MCQ";
                                    stepNo = 2;
                                    loadFragment();
                                    dialog.dismiss();
                                }
                            });

                            dialog.findViewById(R.id.cv_maq).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    typeOne = "MAQ";
                                    stepNo = 2;
                                    loadFragment();
                                    dialog.dismiss();
                                }
                            });

                            dialog.findViewById(R.id.cv_tf).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    typeOne = "TOF";
                                    stepNo = 2;
                                    loadFragment();
                                    dialog.dismiss();
                                }
                            });

                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return count;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvCount;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCount = itemView.findViewById(R.id.tv_count);

            }
        }
    }


    @Override
    public void onBackPressed() {
        switch (stepNo){
            case 1:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Are you sure you want to go back?\nAll progress will be lost.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;

            case 2:
            case 3:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Are you sure you want to go back?\nYou can find this arena in drafts.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
            case 4:
                if (isPreview){
                    stepNo = 3;
                    loadFragment();
                }else {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("Are you sure you want to go back?\nYou can find this arena in drafts.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                }
                            })
                            .setCancelable(false)
                            .show();
                    break;
                }
        }
    }
}