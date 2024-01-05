package myschoolapp.com.gsnedutech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import myschoolapp.com.gsnedutech.Models.DateObj;
import myschoolapp.com.gsnedutech.Models.Events;
import myschoolapp.com.gsnedutech.Util.MonthYearPickerDialog;
import myschoolapp.com.gsnedutech.Util.MyUtils;

public class SchedulesCalendar extends AppCompatActivity {


    @BindView(R.id.tv_month_name)
    TextView tvMonthName;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.rv_cal)
    RecyclerView rvCal;
    @BindView(R.id.rv_events)
    RecyclerView rvEvents;
    @BindView(R.id.iv_no_schedules)
    ImageView ivNoSchedules;
    MyUtils utils = new MyUtils();

    List<DateObj> listDates = new ArrayList<>();

    CalendarAdapter calendarAdapter;
    int selectedDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedules_calendar);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        selectedDay = Calendar.getInstance().get(Calendar.DATE);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        tvDate.setText(new SimpleDateFormat("dd MMMM, yyyy").format(new Date()));
        tvMonthName.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));

        findViewById(R.id.tv_month_name).setOnClickListener(new View.OnClickListener() {
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

                        tvMonthName.setText(mon+" "+year);
                        tvDate.setText(1+" "+mon+", "+year);
                        selectedDay = 1;
                        calendarWork(--month,year);

                    }
                });

                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");

            }
        });

        calendarWork(Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR));

    }



    void calendarWork(int month,int year){

        listDates.clear();

        int iDay = 1;

        Calendar mycal = Calendar.getInstance();
        mycal.set(year,month,iDay);

        utils.showLog("tag", mycal.get(Calendar.DATE)+" "+mycal.get(Calendar.MONTH)+" "+mycal.get(Calendar.YEAR));

        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i=1;i<=daysInMonth;i++){
            Calendar cal = Calendar.getInstance();
            cal.set(year,month,i);
            String day = "";
            switch (cal.get(Calendar.DAY_OF_WEEK)){
                case 1:
                    day="S";
                    break;
                case 2:
                    day="M";
                    break;
                case 3:
                    day="T";
                    break;
                case 4:
                    day="W";
                    break;
                case 5:
                    day="Th";
                    break;
                case 6:
                    day="F";
                    break;
                case 7:
                    day="Sa";
                    break;
            }

            utils.showLog("tag","date "+i+" day "+day);

            List<Events> events = new ArrayList<>();
            String d="";

            if (i < 10) {
                d = "0" + i;
            }else {
                d=i+"";
            }

            if (d.equalsIgnoreCase(new SimpleDateFormat("dd").format(new Date()))){
                events.add(new Events("09:00 am","Mathematics Live Class","Complete chapter 4",15,"Live"));
                events.add(new Events("11:00 am","Science Practice Test","Complete chapter 4",60,"Test"));
                events.add(new Events("12:00 am","K-Hub Course Completion","Complete chapter 4",90,"K-Hub"));
                events.add(new Events("02:00 pm","Science Live Class","Complete chapter 4",30,"Live"));
            }

            DateObj dateObj = new DateObj(day,i+"",events);
            listDates.add(dateObj);
        }

        LinearLayoutManager manager = new LinearLayoutManager(SchedulesCalendar.this,RecyclerView.HORIZONTAL,false);
        rvCal.setLayoutManager(manager);
        calendarAdapter = new CalendarAdapter(listDates);
        rvCal.setAdapter(calendarAdapter);
        manager.scrollToPosition((selectedDay-1));

    }



    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder>{
        List<DateObj> listDate;
        public CalendarAdapter(List<DateObj> listDate) {
            this.listDate = listDate;
        }

        @NonNull
        @Override
        public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(SchedulesCalendar.this).inflate(R.layout.item_calendar,parent,false));
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

            if ((position+1)==selectedDay){
                holder.tvDate.setTextColor(Color.WHITE);
                holder.tvDate.setAlpha(1);
                holder.tvDate.setBackgroundResource(R.drawable.bg_date_selected);
                if (listDate.get(position).getListEvents().size()>0){
                    rvEvents.setLayoutManager(new LinearLayoutManager(SchedulesCalendar.this));
                    rvEvents.setAdapter(new EventAdapter(listDate.get(position).getListEvents()));
                    ivNoSchedules.setVisibility(View.GONE);
                    rvEvents.setVisibility(View.VISIBLE);
                }else {
                    rvEvents.setVisibility(View.GONE);
                    ivNoSchedules.setVisibility(View.VISIBLE);
                }
            }else{
                holder.tvDate.setTextColor(Color.rgb(73,73,73));
                holder.tvDate.setAlpha(0.5f);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rvCal.getLayoutManager().scrollToPosition(position);
                    selectedDay = Integer.parseInt(listDate.get(position).getDate());
                    notifyDataSetChanged();
                    tvDate.setText(selectedDay+" "+tvMonthName.getText().toString().split(" ")[0]+", "+tvMonthName.getText().toString().split(" ")[1]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listDate.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvDate,tvDay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvDate = itemView.findViewById(R.id.tv_date);
                tvDay = itemView.findViewById(R.id.tv_day);
            }
        }
    }


    class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

        List<Events> listEvent;

        EventAdapter(List<Events> listEvent){
            this.listEvent = listEvent;
        }

        @Override
        public int getItemViewType(int position) {
            if ( listEvent.get(position).getDuration()<=15){
                return 0;
            }else if (listEvent.get(position).getDuration()>15 && listEvent.get(position).getDuration()<=30){
                return 1;
            }else if (listEvent.get(position).getDuration()>30 && listEvent.get(position).getDuration()<=60){
                return 2;
            }else {
                return 3;
            }

        }

        @NonNull
        @Override
        public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case 1:
                    return new ViewHolder(LayoutInflater.from(SchedulesCalendar.this).inflate(R.layout.item_schedule_30,parent,false));
                case 2:
                    return new ViewHolder(LayoutInflater.from(SchedulesCalendar.this).inflate(R.layout.item_schedule_60,parent,false));
                case 3:
                    return new ViewHolder(LayoutInflater.from(SchedulesCalendar.this).inflate(R.layout.item_schedule_90,parent,false));
                default:
                    return new ViewHolder(LayoutInflater.from(SchedulesCalendar.this).inflate(R.layout.item_schedule_15,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {

            if (listEvent.get(position).getDuration()<=15){
                holder.tvTime.setText(listEvent.get(position).getDuration()+" mins");
                holder.tvTime.setTextSize(14);
            }else {
                holder.tvTime.setText(listEvent.get(position).getTime().split(" ")[0]+"\n"+listEvent.get(position).getTime().split(" ")[1]);
            }
            holder.tvDuration.setText(listEvent.get(position).getDuration()+" mins");
            holder.tvTitle.setText(listEvent.get(position).getEventTitle());
            holder.tvDesc.setText(listEvent.get(position).getEventDesc());

            switch(listEvent.get(position).getType()){
                case "Live":
                    holder.flBackground.setBackgroundResource(R.drawable.bg_red_gradient);
                    holder.llMain.setBackgroundResource(R.drawable.bg_red_mask);
                    break;
                case "Test":
                    holder.flBackground.setBackgroundResource(R.drawable.bg_blue_gradient);
                    holder.llMain.setBackgroundResource(R.drawable.bg_blue_mask);
                    break;
                case "K-Hub":
                    holder.flBackground.setBackgroundResource(R.drawable.bg_green_gradient);
                    holder.llMain.setBackgroundResource(R.drawable.bg_green_mask);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return listEvent.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTime,tvTitle,tvDesc,tvDuration;
            LinearLayout llMain;
            FrameLayout flBackground;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDesc = itemView.findViewById(R.id.tv_desc);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                llMain = itemView.findViewById(R.id.ll_main);
                flBackground = itemView.findViewById(R.id.fl_background);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}

