package myschoolapp.com.gsnedutech.Models;

public class ScheduleObj {
    String time,eventTitle,eventDesc,type,duration;

    public ScheduleObj(String time, String eventTitle, String eventDesc, String duration, String type){
        this.time = time;
        this.eventTitle = eventTitle;
        this.eventDesc = eventDesc;
        this.duration = duration;
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
