package myschoolapp.com.gsnedutech;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Models.ToDosObj;
import myschoolapp.com.gsnedutech.Util.AppConst;
import myschoolapp.com.gsnedutech.Util.AppUrls;
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

public class TeacherToDoActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = TeacherToDoActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_todo_incomplete)
    RecyclerView rvTodoIncomplete;
    @BindView(R.id.rv_todo_complete)
    RecyclerView rvTodoComplete;
    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    List<ToDosObj> listTodos = new ArrayList<>();
    List<ToDosObj> listTodosIncomplete = new ArrayList<>();
    List<ToDosObj> listTodosComplete = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
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
            utils.alertDialog(1, TeacherToDoActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
                getTodoList();
            }
            isNetworkAvail = true;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectivity);
    }

    private void init() {

        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);
        toEdit = sh_Pref.edit();
        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);

        rvTodoComplete.setLayoutManager(new LinearLayoutManager(this));


        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TeacherToDoActivity.this,TeacherCreateToDoActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });
    }

    void getTodoList(){
        utils.showLoader(TeacherToDoActivity.this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        Request get = new Request.Builder()
                .url(new AppUrls().GetTeacherToDo+"schemaName="+sh_Pref.getString("schema", "")+"&userId="+tObj.getUserId())
                .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url " + new AppUrls().GetTeacherToDo+"schemaName="+sh_Pref.getString("schema", "")+"&userId="+tObj.getUserId());

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
                ResponseBody body = response.body();
                String resp = body.string();
                runOnUiThread(() -> utils.dismissDialog());
                try {
                    JSONObject parentjObject = new JSONObject(resp);
                    if (parentjObject.getString("StatusCode").equalsIgnoreCase("200")){
                        JSONArray jsonArray = parentjObject.getJSONArray("todoList");

                        listTodos.clear();
                        listTodos.addAll(new Gson().fromJson(jsonArray.toString(), new TypeToken<List<ToDosObj>>() {
                        }.getType()));

                        listTodosComplete.clear();
                        listTodosIncomplete.clear();

                        for (int i=0;i<listTodos.size();i++){
                            if (listTodos.get(i).getTodoStatus().equalsIgnoreCase("0")){
                                listTodosIncomplete.add(listTodos.get(i));
                            }else{
                                listTodosComplete.add(listTodos.get(i));
                            }
                        }

                        if (listTodos.size()>0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (listTodosIncomplete.size()>0){
                                        rvTodoIncomplete.setVisibility(View.VISIBLE);
                                        rvTodoIncomplete.setLayoutManager(new LinearLayoutManager(TeacherToDoActivity.this));
                                        rvTodoIncomplete.setAdapter(new ToDoAdapter(listTodosIncomplete));
                                    }else{
                                        rvTodoIncomplete.setVisibility(View.GONE);
                                    }
                                    if (listTodosComplete.size()>0){
                                        rvTodoComplete.setVisibility(View.VISIBLE);
                                        findViewById(R.id.tv_completed_text).setVisibility(View.VISIBLE);
                                        rvTodoComplete.setLayoutManager(new LinearLayoutManager(TeacherToDoActivity.this));
                                        rvTodoComplete.setAdapter(new CompleteAdapter(listTodosComplete));
                                    }else {
                                        rvTodoComplete.setVisibility(View.GONE);
                                        findViewById(R.id.tv_completed_text).setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else if (parentjObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(parentjObject)){ //TODO New Changes
                            String message = parentjObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                MyUtils.forceLogoutUser(toEdit, TeacherToDoActivity.this, message, sh_Pref);
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

        List<ToDosObj> listTodosInc;

        public ToDoAdapter(List<ToDosObj> listTodosInc) {
            this.listTodosInc = listTodosInc;
        }

        @NonNull
        @Override
        public ToDoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ToDoAdapter.ViewHolder(LayoutInflater.from(TeacherToDoActivity.this).inflate(R.layout.item_todo_incomplete, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ToDoAdapter.ViewHolder holder, int position) {
            holder.tvTitle.setText(listTodosInc.get(position).getTodoTitle());
            holder.tvDescription.setText(listTodosInc.get(position).getTodoDesc());
            holder.viewComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ToDo todo = toDosActive.get(position);
//                    if (todo.getReminderCode()!=0){
//                        Intent intent = new Intent(MainActivity.this, FinalAlarm.class);//the same as up
//                        boolean isWorking = (PendingIntent.getBroadcast(MainActivity.this, todo.getReminderCode(), intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
//                        if (isWorking) {
//                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                            Intent intent1 = new Intent(MainActivity.this, FinalAlarm.class);//the same as up
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, todo.getReminderCode(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);//the same as up
//                            am.cancel(pendingIntent);//important
//                            pendingIntent.cancel();//important
//                            todo.setReminderCode(0);
//                            todo.setIsRemainder(0);
//                        }
//                        else {
//                            todo.setReminderCode(0);
//                            todo.setIsRemainder(0);
//                        }
//                    }
//                    todo.setIsCompleted(1);
//                    ToDoDatabase.databaseWriteExecutor.execute(() -> {
//                        qdb.toDoDao().updateToDO(todo);
//                    });
//                    getTodos();



                }
            });
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherToDoActivity.this, TeacherCreateToDoActivity.class);
                    intent.putExtra("todo", listTodosInc.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });

            holder.viewComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    markComplete(listTodosInc.get(position).getTodoListId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return listTodosInc.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription;
            View viewComplete;
            LinearLayout llItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_todotitle);
                tvDescription = itemView.findViewById(R.id.tv_tododesc);
                viewComplete = itemView.findViewById(R.id.vw_complete);
                llItem = itemView.findViewById(R.id.ll_item);
            }
        }
    }

    private void markComplete(String todoListId) {


//        utils.showLoader(this);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
            jsonObject.put("userId", tObj.getUserId());
            jsonObject.put("todoListId", todoListId);
            jsonObject.put("todoStatus", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        utils.showLog(TAG, "url " + new AppUrls().CompletedToDoList);
        utils.showLog(TAG, "body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(new AppUrls().CompletedToDoList)
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

                if (!response.isSuccessful()){

                }else{
                    try {
                        JSONObject ParentObject = new JSONObject(resp);
                        if (ParentObject.getString("StatusCode").equalsIgnoreCase("200")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getTodoList();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    class CompleteAdapter extends RecyclerView.Adapter<CompleteAdapter.ViewHolder> {

        List<ToDosObj> listTodosComp;

        public CompleteAdapter(List<ToDosObj> listTodosComp) {
            this.listTodosComp = listTodosComp;
        }

        @NonNull
        @Override
        public CompleteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CompleteAdapter.ViewHolder(LayoutInflater.from(TeacherToDoActivity.this).inflate(R.layout.item_todo_complete, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull CompleteAdapter.ViewHolder holder, int position) {
            holder.tvTitle.setText(listTodosComp.get(position).getTodoTitle());
            holder.tvDescription.setText(listTodosComp.get(position).getTodoDesc());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TeacherToDoActivity.this, CompletedToDoActivity.class);
                    intent.putExtra("todo", listTodosComp.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listTodosComp.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDescription;
            View viewComplete;
            LinearLayout llItem;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_todotitle);
                tvDescription = itemView.findViewById(R.id.tv_tododesc);
                viewComplete = itemView.findViewById(R.id.vw_complete);
                llItem = itemView.findViewById(R.id.ll_item);
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}