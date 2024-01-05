package myschoolapp.com.gsnedutech;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.DateObj;
import myschoolapp.com.gsnedutech.Models.Events;
import myschoolapp.com.gsnedutech.Models.TeacherObj;
import myschoolapp.com.gsnedutech.Models.ToDosObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import myschoolapp.com.gsnedutech.Util.NetworkConnectivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TeacherCreateToDoActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener{

    private static final String TAG = TeacherCreateToDoActivity.class.getName();
    private final NetworkConnectivity networkConnectivity = new NetworkConnectivity();
    boolean isNetworkAvail = false;


    @BindView(R.id.rv_cal)
    RecyclerView rvCal;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_desscription)
    EditText etDescription;

    @BindView(R.id.et_start_time)
    EditText etStartTime;

    @BindView(R.id.et_end_time)
    EditText etEndTime;

    @BindView(R.id.switch_reminder)
    SwitchCompat switchReminder;

    @BindView(R.id.btn_save)
    LinearLayout btnSave;

    @BindView(R.id.tv_month_name)
    TextView tvMonthName;

    @BindView(R.id.tv_message_count)
    TextView tvMessageCount;

    List<DateObj> listDates = new ArrayList<>();

    CalendarAdapter calendarAdapter;
    int selectedDay = 0;
    long startTime = 0, endTime = 0;
    int month = 9, year = 2020, date = 16;

    boolean isChecked = false;
    ToDosObj toDo;
    boolean isEnabled = true;
    MyUtils utils = new MyUtils();

    SharedPreferences sh_Pref;
    SharedPreferences.Editor toEdit;
    TeacherObj tObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_to_do);
        ButterKnife.bind(this);
        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        date = calendar.get(Calendar.DATE);
        init();
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
            utils.alertDialog(1, TeacherCreateToDoActivity.this, getString(R.string.error_connect), getString(R.string.error_internet),
                    getString(R.string.action_settings), getString(R.string.action_close), false);
        } else {
            if (!isNetworkAvail){
                utils.dismissAlertDialog();
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

        Gson gson = new Gson();
        String json = sh_Pref.getString("teacherObj", "");
        tObj = gson.fromJson(json, TeacherObj.class);
        tvMonthName.setText(getMonth(month) + " " + year);
        selectedDay = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));


        findViewById(R.id.tv_month_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        String mon = getMonth(month);
                        Calendar cal = Calendar.getInstance();

                        TeacherCreateToDoActivity.this.month = month;
                        TeacherCreateToDoActivity.this.year = year;
                        tvMonthName.setText(mon + " " + year);
//                        tvDate.setText(1+" "+mon+", "+year);
                        calendarWork(--month, year);

                    }
                });

                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        calendarWork(Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR));

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0 && etDescription.getText().toString().length() > 0) {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grad_intro_next);

                } else {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grey_save);
                }

            }
        });
        InputFilter[] FilterArray = new InputFilter[1];
        String ss = getResources().getString(R.string.text_count);
        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(ss));
        etDescription.setFilters(FilterArray);
        tvMessageCount.setText(MessageFormat.format("0/{0}", ss));

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etDescription.getText().toString().length() > 0 && etStartTime.getText().toString().length()>0 && etEndTime.getText().toString().length()>0) {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grad_intro_next);
                } else {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grey_save);
                }
            }
        });

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvMessageCount.setText(MessageFormat.format("{0}/{1}", s.length(), ss));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0 && etTitle.getText().toString().length() > 0 && etStartTime.getText().toString().length()>0 && etEndTime.getText().toString().length()>0) {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grad_intro_next);
                } else {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grey_save);
                }
            }
        });

        etStartTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etTitle.getText().toString().length() > 0 && etDescription.getText().toString().length()>0 && etEndTime.getText().toString().length()>0) {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grad_intro_next);
                } else {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grey_save);
                }
            }
        });

        etEndTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etTitle.getText().toString().length() > 0 && etDescription.getText().toString().length()>0 && etStartTime.getText().toString().length()>0) {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grad_intro_next);
                } else {
                    findViewById(R.id.btn_save).setBackgroundResource(R.drawable.bg_grey_save);
                }
            }
        });

        etStartTime.setInputType(InputType.TYPE_NULL);
        etEndTime.setInputType(InputType.TYPE_NULL);
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                cldr.add(Calendar.MINUTE, 2);
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                TimePickerDialog picker = new TimePickerDialog(TeacherCreateToDoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                etStartTime.setText((sHour > 9 ? sHour : "0" + sHour) + ":" + (sMinute > 9 ? sMinute : "0" + sMinute));
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                cldr.add(Calendar.MINUTE, 2);
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                TimePickerDialog picker = new TimePickerDialog(TeacherCreateToDoActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                etEndTime.setText((sHour > 9 ? sHour : "0" + sHour) + ":" + (sMinute > 9 ? sMinute : "0" + sMinute));
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (b) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month - 1, date, Integer.parseInt(etStartTime.getText().toString().split(":")[0]), Integer.parseInt(etStartTime.getText().toString().split(":")[1]), 0);
                        Calendar cal1 = Calendar.getInstance();
                        startTime = cal.getTimeInMillis();
                        if (!etStartTime.getText().toString().isEmpty() && startTime < cal1.getTimeInMillis()) {
                            switchReminder.setChecked(false);
                            Toast.makeText(TeacherCreateToDoActivity.this, "Start time is should be greater than curren time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toDo != null) {
                    if (etDescription.getText().toString().trim().isEmpty() || etTitle.getText().toString().trim().isEmpty()) {
                        Toast.makeText(TeacherCreateToDoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        updateTODO();
                    }

                } else {
                    if (etDescription.getText().toString().trim().isEmpty() || etTitle.getText().toString().trim().isEmpty()) {
                        Toast.makeText(TeacherCreateToDoActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        createTODO();
                    }
                }

            }
        });
        if (getIntent().hasExtra("todo")) {
            toDo = (ToDosObj) getIntent().getSerializableExtra("todo");
            if (toDo != null) {
                etTitle.setText(toDo.getTodoTitle());
                etDescription.setText(toDo.getTodoDesc());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {

                    Date st = sdf.parse(toDo.getTodoStartTime());
                    Date et = sdf.parse(toDo.getTodoEndTime());
                    etStartTime.setText(simpleDateFormat.format(st));
                    etEndTime.setText(simpleDateFormat.format(et));
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                    if (toDo.getIsRemainder().equalsIgnoreCase("1")) {
                        switchReminder.setChecked(true);
                    } else switchReminder.setChecked(false);
                    Date dd = sdf.parse(sdf.format(st));
                    String date = sdf1.format(dd);
                    String[] da = date.split("-");
                    selectedDay = Integer.parseInt(da[0]);
                    int mo = Integer.parseInt(da[1]);
                    tvMonthName.setText(getMonth(mo) + " " + da[2]);
                    calendarWork(--mo, Integer.parseInt(da[2]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

        }
    }

    void createTODO() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jObj = new JSONObject();
        try {
            jObj.put("schemaName", sh_Pref.getString("schema", ""));
            jObj.put("userId", tObj.getUserId());
            String startDate = year + "-" + month + "-" + date + " " + etStartTime.getText().toString().trim() + ":00";
            String endDate = year + "-" + month + "-" + date + " " + etEndTime.getText().toString().trim() + ":00";
            JSONObject toDosObj = new JSONObject();
            toDosObj.put("todoTitle", etTitle.getText().toString().trim());
            toDosObj.put("todoDesc", etDescription.getText().toString().trim());
            toDosObj.put("todoStartTime", startDate);
            toDosObj.put("todoEndTime", endDate);
            toDosObj.put("isRemainder", switchReminder.isChecked() ? "1" : "0");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(toDosObj);
            jObj.put("insertRecords", jsonArray);

        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(JSON, jObj.toString());


        Request post = new Request.Builder()
                .url(new AppUrls().AddTeacherToDo)
                .post(body)
                .build();

        utils.showLog(TAG, "url " + new AppUrls().AddTeacherToDo);

        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.alertDialog(3, TeacherCreateToDoActivity.this, "Alert", "Something Went Wrong!", "okay", "okay", false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(resp);
                            if (json.getString("StatusCode").equalsIgnoreCase("200")) {
                                Toast.makeText(TeacherCreateToDoActivity.this, "todo added successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(TeacherCreateToDoActivity.this, "something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

    void updateTODO() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jObj = new JSONObject();
        try {
            jObj.put("schemaName", sh_Pref.getString("schema", ""));
            String startDate = year + "-" + month + "-" + date + " " + etStartTime.getText().toString().trim() + ":00";
            String endDate = year + "-" + month + "-" + date + " " + etEndTime.getText().toString().trim() + ":00";
            jObj.put("todoTitle", etTitle.getText().toString().trim());
            jObj.put("todoDesc", etDescription.getText().toString().trim());
            jObj.put("todoStartTime", startDate);
            jObj.put("todoEndTime", endDate);
            jObj.put("isRemainder", switchReminder.isChecked() ? "1" : "0");
            jObj.put("todoListId", toDo.getTodoListId());
            jObj.put("userId", tObj.getUserId());

        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(JSON, jObj.toString());


        Request post = new Request.Builder()
                .url(new AppUrls().UpdateTeacherTODO)
                .post(body)
                .build();

        utils.showLog(TAG, "url " + new AppUrls().UpdateTeacherTODO);

        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        utils.alertDialog(3, TeacherCreateToDoActivity.this, "Alert", "Something Went Wrong!", "okay", "okay", false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resp = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject(resp);
                            if (json.getString("StatusCode").equalsIgnoreCase("200")) {
                                Toast.makeText(TeacherCreateToDoActivity.this, "todo updated successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

    void calendarWork(int month, int year) {

        listDates.clear();

        int iDay = 1;

        Calendar mycal = Calendar.getInstance();
        mycal.set(year, month, iDay);

        utils.showLog("tag", mycal.get(Calendar.DATE) + " " + mycal.get(Calendar.MONTH) + " " + mycal.get(Calendar.YEAR));

        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, i);
            String day = "";
            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    day = "S";
                    break;
                case 2:
                    day = "M";
                    break;
                case 3:
                    day = "T";
                    break;
                case 4:
                    day = "W";
                    break;
                case 5:
                    day = "Th";
                    break;
                case 6:
                    day = "F";
                    break;
                case 7:
                    day = "Sa";
                    break;
            }

            utils.showLog("tag", "date " + i + " day " + day);

            List<Events> events = new ArrayList<>();

            if ((i + "").equalsIgnoreCase(new SimpleDateFormat("dd").format(new Date()))) {
                events.add(new Events("09:00 am", "Mathematics Live Class", "Complete chapter 4", 15, "Live"));
                events.add(new Events("11:00 am", "Science Practice Test", "Complete chapter 4", 60, "Test"));
                events.add(new Events("12:00 am", "K-Hub Course Completion", "Complete chapter 4", 90, "K-Hub"));
                events.add(new Events("02:00 pm", "Science Live Class", "Complete chapter 4", 30, "Live"));
            }

            DateObj dateObj = new DateObj(day, i + "", events);
            listDates.add(dateObj);
        }

        LinearLayoutManager manager = new LinearLayoutManager(TeacherCreateToDoActivity.this, RecyclerView.HORIZONTAL, false);
        rvCal.setLayoutManager(manager);
        calendarAdapter = new CalendarAdapter(listDates);
        rvCal.setAdapter(calendarAdapter);
        manager.scrollToPosition((selectedDay - 1));

    }


    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
        List<DateObj> listDate;

        public CalendarAdapter(List<DateObj> listDate) {
            this.listDate = listDate;
        }

        @NonNull
        @Override
        public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CalendarAdapter.ViewHolder(LayoutInflater.from(TeacherCreateToDoActivity.this).inflate(R.layout.item_calendar, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            holder.tvDate.setText(listDate.get(position).getDate());
            holder.tvDay.setText(listDate.get(position).getDay());

            if ((position + 1) == selectedDay) {
                holder.tvDate.setTextColor(Color.WHITE);
                holder.tvDate.setAlpha(1);
                holder.tvDate.setBackgroundResource(R.drawable.bg_date_selected);
                TeacherCreateToDoActivity.this.date = selectedDay;
            } else {
                holder.tvDate.setTextColor(Color.rgb(73, 73, 73));
                holder.tvDate.setAlpha(0.5f);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEnabled) return;
                    rvCal.getLayoutManager().scrollToPosition(position);
                    selectedDay = Integer.parseInt(listDate.get(position).getDate());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listDate.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate, tvDay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvDate = itemView.findViewById(R.id.tv_date);
                tvDay = itemView.findViewById(R.id.tv_day);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}