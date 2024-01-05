package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.EventDetail;
import myschoolapp.com.gsnedutech.Models.EventsAndHoliday;
import myschoolapp.com.gsnedutech.Models.StudentObj;
import myschoolapp.com.gsnedutech.Util.AppUrls;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CalendarAndEvents extends AppCompatActivity {

    private static final String TAG = CalendarAndEvents.class.getName();

//    @BindView(R.id.prev_month)
//    ImageView prevMonth;
    @BindView(R.id.tv_month_year)
    TextView tvMonthYear;
//    @BindView(R.id.next_month)
//    ImageView nextMonth;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView cvEvents;
    @BindView(R.id.rv_events)
    RecyclerView rvEvents;
    @BindView(R.id.tv_event)
    TextView tvEvent;
    SharedPreferences sh_Pref;
    List<EventsAndHoliday> eventList = new ArrayList<>();
    String roleId, branchId, courseId = "", classId = "";
    Date scrolledDate = new Date();
    MyUtils utils = new MyUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        setContentView(R.layout.activity_calendar_and_events);
        ButterKnife.bind(this);

        init();
    }


    void init(){

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        sh_Pref = getSharedPreferences(AppUrls.shCredentials, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sh_Pref.getString("studentObj", "");
        StudentObj sObj = gson.fromJson(json, StudentObj.class);
        roleId = "0";
        branchId = "" + sObj.getBranchId();
        courseId = "" + sObj.getCourseId();
        classId = "" + sObj.getClassId();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        tvMonthYear.setText(sdf.format(d));


        tvMonthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
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

                        Calendar cal = Calendar.getInstance();
                        cal.set(year,(month-1),1);

                        scrolledDate = cal.getTime();

                        cvEvents.setCurrentDate(cal.getTime());
                        tvMonthYear.setText(mon+" "+year);

                        getEvents(month + "", year + "");

                    }
                });

                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });


//        prevMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String current = tvMonthYear.getText().toString();
//                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
//                Date datec = null;
//                try {
//                    datec = sdf.parse(current);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                cvEvents.scrollLeft();
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(datec);
//                cal.add(Calendar.MONTH, -1);
//                Date d = cal.getTime();
//                tvMonthYear.setText(sdf.format(d));
//            }
//        });
//        nextMonth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String current = tvMonthYear.getText().toString();
//                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
//                Date datec = null;
//                try {
//                    datec = sdf.parse(current);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                cvEvents.scrollRight();
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(datec);
//                cal.add(Calendar.MONTH, 1);
//                Date d = cal.getTime();
//                tvMonthYear.setText(sdf.format(d));
//            }
//        });

        cvEvents.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = cvEvents.getEvents(dateClicked);
                utils.showLog("date", "Day was clicked: " + dateClicked + " with events " + events);
                if (events.size() > 0) {
                    if (new SimpleDateFormat("dd-MM-yyyy").format(dateClicked).equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))){
                        tvEvent.setText("Today's Events");
                    }else{
                        tvEvent.setText("Events on "+new SimpleDateFormat("dd-MM-yyyy").format(dateClicked));
                    }
                    rvEvents.setVisibility(View.VISIBLE);
                    utils.showLog(TAG, "text size " + events.get(0).getData().toString().length() + "");
                    if (!(events.get(0).getData().toString().equals("  "))) {
                        final LayoutAnimationController controller =
                                AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
                        rvEvents.setAdapter(new AdapterEvents(events));
                        rvEvents.scheduleLayoutAnimation();
                    } else {
                        rvEvents.setVisibility(View.GONE);
                        tvEvent.setText("No events on "+new SimpleDateFormat("dd-MM-yyyy").format(dateClicked));
                    }
                } else {
                    rvEvents.setVisibility(View.GONE);
                    if (new SimpleDateFormat("dd-MM-yyyy").format(dateClicked).equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))){
                        tvEvent.setText("No Events Today");
                    }else{
                        tvEvent.setText("No Events on "+new SimpleDateFormat("dd-MM-yyyy").format(dateClicked));
                    }
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
                tvMonthYear.setText(sdf.format(firstDayOfNewMonth));
                utils.showLog(TAG, "date after scroll " + firstDayOfNewMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM yyyy");
                String date = simpleDateFormat.format(firstDayOfNewMonth);
                String[] monthYear = date.split(" ");
               getEvents(monthYear[0], monthYear[1]);
                scrolledDate = firstDayOfNewMonth;

            }
        });
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int year = cal.get(Calendar.YEAR);
       getEvents(month + "", year + "");

    }


    private void setCalendar() {
        cvEvents.removeAllEvents();
        Calendar currentCalender = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        events.clear();
        for (int i = 0; i < eventList.size(); i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<EventDetail> details = new ArrayList<>();
            details.addAll(eventList.get(i).getEventDetails());
            for (int j = 0; j < details.size(); j++) {
                try {
                    if (sdf.parse(details.get(j).getFrom()).equals(sdf.parse(details.get(j).getTo()))) {
                        currentCalender.setTime(sdf.parse(details.get(j).getFrom()));
                        long timeInMillis = currentCalender.getTimeInMillis();
                        Event ev1 = new Event(Color.rgb(231, 105, 89), timeInMillis, details.get(j).getEventName() + "  " + details.get(j).getEventDesc());
                        events.add(ev1);
                    } else {
                        Date date = sdf.parse(details.get(j).getFrom());
                        while (date.getTime() != (sdf.parse(details.get(j).getTo())).getTime()) {
                            currentCalender.setTime(date);
                            long timeInMillis = currentCalender.getTimeInMillis();
                            Event ev1 = new Event(Color.rgb(231, 105, 89), timeInMillis, details.get(j).getEventName() + "  " + details.get(j).getEventDesc());
                            events.add(ev1);
                            currentCalender.add(Calendar.DATE, 1);
                            date = currentCalender.getTime();
                            if (sdf.format(date).equalsIgnoreCase(details.get(j).getTo())) {
                                currentCalender.setTime(sdf.parse(details.get(j).getTo()));
                                long timeInMillis1 = currentCalender.getTimeInMillis();
                                Event ev2 = new Event(Color.rgb(231, 105, 89), timeInMillis1, details.get(j).getEventName() + "  " + details.get(j).getEventDesc());
                                events.add(ev2);
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        cvEvents.addEvents(events);
        List<Event> eventsCount = cvEvents.getEvents(scrolledDate);

        if (eventsCount.size() > 0) {
            if (!(eventsCount.get(0).getData().toString().equals("  "))) {
                final LayoutAnimationController controller =
                        AnimationUtils.loadLayoutAnimation(rvEvents.getContext(), R.anim.layout_animation_fall_down);
                rvEvents.setAdapter(new AdapterEvents(eventsCount));
                rvEvents.getLayoutManager().scrollToPosition(0);
                rvEvents.scheduleLayoutAnimation();
                if (new SimpleDateFormat("dd-MM-yyyy").format(scrolledDate).equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))){
                    tvEvent.setText("Today's Events");
                }else{
                    tvEvent.setText("Events on "+new SimpleDateFormat("dd-MM-yyyy").format(scrolledDate));
                }
            }
            else {

                rvEvents.setVisibility(View.GONE);

                if (new SimpleDateFormat("dd-MM-yyyy").format(scrolledDate).equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))){
                    tvEvent.setText("No Events Today");
                }else{
                    tvEvent.setText("No Events on "+new SimpleDateFormat("dd-MM-yyyy").format(scrolledDate));
                }

            }
        } else {
            rvEvents.setVisibility(View.GONE);

            if (new SimpleDateFormat("dd-MM-yyyy").format(scrolledDate).equals(new SimpleDateFormat("dd-MM-yyyy").format(new Date()))){
                tvEvent.setText("No Events Today");
            }else{
                tvEvent.setText("No Events on "+new SimpleDateFormat("dd-MM-yyyy").format(scrolledDate));
            }

        }

    }


    class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.ViewHolder> {
        List<Event> events;

        AdapterEvents(List<Event> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(CalendarAndEvents.this).inflate(R.layout.item_cal_and_events, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String[] text = events.get(position).getData().toString().split("  ");
            holder.tvTitle.setText(text[0]);
            holder.tvDesc.setText(text[1]);
            holder.ivOptions.setVisibility(View.GONE);
            holder.ivOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(CalendarAndEvents.this, view);
                    popup.getMenuInflater().inflate(R.menu.menu_calendar, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Toast.makeText(CalendarAndEvents.this,"You Clicked : " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                    popup.show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(CalendarAndEvents.this, CalendarEventDisplay.class);
                    i.putExtra("event title",text[0]);
                    i.putExtra("event desc",text[0]);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc;
            ImageView ivOptions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDesc = itemView.findViewById(R.id.tv_desc);
                tvTitle = itemView.findViewById(R.id.tv_title);
                ivOptions = itemView.findViewById(R.id.iv_options);
            }
        }
    }


    void getEvents(String month,String year){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("monthId", month);
            jsonObject.put("yearId", year);
            jsonObject.put("schemaName", sh_Pref.getString("schema",""));
            jsonObject.put("branchId", branchId);
            jsonObject.put("roleId", roleId);
            jsonObject.put("courseId", courseId);
            jsonObject.put("classId", classId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, String.valueOf(jsonObject));

        utils.showLog(TAG, "url " + new AppUrls().GetEventsAndHolidaysMonth);
        utils.showLog(TAG, "body " + jsonObject.toString());

        Request request = new Request.Builder()
                .url(new AppUrls().GetEventsAndHolidaysMonth)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {

                } else {
                    String resp = responseBody.string();
                    try {
                        JSONObject ParentjObject = new JSONObject(resp);
                        if (ParentjObject.getString("StatusCode").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = ParentjObject.getJSONArray("EventsAndHolidays");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<EventsAndHoliday>>() {
                            }.getType();
                            eventList.clear();
                            eventList.addAll(gson.fromJson(jsonArray.toString(), type));
                            utils.showLog(TAG, "size " + eventList.size());
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   setCalendar();
                               }
                           });
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}