package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.StudentObj;
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

public class ToDoActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener {

    private static final String TAG = ToDoActivity.class.getName();

    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;

    @BindView(R.id.rv_todo_incomplete)
    RecyclerView rvTodoIncomplete;
    @BindView(R.id.rv_todo_complete)
    RecyclerView rvTodoComplete;
    @BindView(R.id.tv_date)
    TextView tvDate;

    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    StudentObj sObj;

    List<ToDosObj> listTodos = new ArrayList<>();
    List<ToDosObj> listTodosIncomplete = new ArrayList<>();
    List<ToDosObj> listTodosComplete = new ArrayList<>();

    ToDoAdapter todoAdapter;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        ButterKnife.bind(this);

        init();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToDoActivity.this, CreateToDoActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        findViewById(R.id.tv_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDatePicker(v);
            }
        });
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
            utils.alertDialog(1, ToDoActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
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

        Calendar calendar = Calendar.getInstance();
        month= calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        dayOfMonth = calendar.get(Calendar.DATE);

        tvDate.setText(getMonth(month + 1) + " " + dayOfMonth + ", " + year);

    }

    public void callDatePicker(final View view) {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        TextView tv = (TextView) view;
                        tv.setText(getMonth(month + 1) + " " + day + ", " + year);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    String getMonth(int month) {
        String mon = "";
        switch (month) {
            case 1:
                mon = "January";
                break;
            case 2:
                mon = "February";
                break;
            case 3:
                mon = "March";
                break;
            case 4:
                mon = "April";
                break;
            case 5:
                mon = "May";
                break;
            case 6:
                mon = "June";
                break;
            case 7:
                mon = "July";
                break;
            case 8:
                mon = "August";
                break;
            case 9:
                mon = "September";
                break;
            case 10:
                mon = "October";
                break;
            case 11:
                mon = "November";
                break;
            case 12:
                mon = "December";
                break;
        }
        return mon;
    }


    void getTodoList() {
        utils.showLoader(this);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request get = new Request.Builder()
                .url(new AppUrls().GetStudentToDo + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId())
               .headers(MyUtils.addHeaders(sh_Pref))
                .build();

        utils.showLog(TAG, "url " + new AppUrls().GetStudentToDo + "schemaName=" + sh_Pref.getString("schema", "") + "&studentId=" + sObj.getStudentId());

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

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("todoList");

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

                        if (listTodos.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                   if (listTodosIncomplete.size()>0){
                                       rvTodoIncomplete.setVisibility(View.VISIBLE);
                                       rvTodoIncomplete.setLayoutManager(new LinearLayoutManager(ToDoActivity.this));
                                       rvTodoIncomplete.setAdapter(new ToDoAdapter(listTodosIncomplete));
                                   }else{
                                       rvTodoIncomplete.setVisibility(View.GONE);
                                   }
                                   if (listTodosComplete.size()>0){
                                       rvTodoComplete.setVisibility(View.VISIBLE);
                                       findViewById(R.id.tv_completed_text).setVisibility(View.VISIBLE);
                                       rvTodoComplete.setLayoutManager(new LinearLayoutManager(ToDoActivity.this));
                                       rvTodoComplete.setAdapter(new CompleteAdapter(listTodosComplete));
                                   }else {
                                       rvTodoComplete.setVisibility(View.GONE);
                                       findViewById(R.id.tv_completed_text).setVisibility(View.GONE);
                                   }
                                }
                            });
                        } else if (jsonObject.has(AppConst.STATUS_CODE) && MyUtils.showLogOutAlert(jsonObject)){ //TODO New Changes
                            String message = jsonObject.getString(AppConst.MESSAGE);
                            runOnUiThread(() -> {
                                utils.dismissDialog();
                                MyUtils.forceLogoutUser(toEdit, ToDoActivity.this, message, sh_Pref);
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }


                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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


    class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

        List<ToDosObj> listTodosInc;

        public ToDoAdapter(List<ToDosObj> listTodosInc) {
            this.listTodosInc = listTodosInc;
        }

        @NonNull
        @Override
        public ToDoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ToDoAdapter.ViewHolder(LayoutInflater.from(ToDoActivity.this).inflate(R.layout.item_todo_incomplete, parent, false));
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
                    Intent intent = new Intent(ToDoActivity.this, CreateToDoActivity.class);
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
            jsonObject.put("studentId", sObj.getStudentId());
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
            return new ViewHolder(LayoutInflater.from(ToDoActivity.this).inflate(R.layout.item_todo_complete, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull CompleteAdapter.ViewHolder holder, int position) {
            holder.tvTitle.setText(listTodosComp.get(position).getTodoTitle());
            holder.tvDescription.setText(listTodosComp.get(position).getTodoDesc());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ToDoActivity.this, CompletedToDoActivity.class);
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