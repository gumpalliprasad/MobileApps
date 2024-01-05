package myschoolapp.com.gsnedutech.Models;

import java.util.List;

public class DateObj{
    String day;
    String date;
    List<Events> listEvents;

    public DateObj(String day, String date,List<Events> listEvents){
        this.day = day;
        this.date = date;
        this.listEvents = listEvents;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Events> getListEvents() {
        return listEvents;
    }

    public void setListEvents(List<Events> listEvents) {
        this.listEvents = listEvents;
    }


}