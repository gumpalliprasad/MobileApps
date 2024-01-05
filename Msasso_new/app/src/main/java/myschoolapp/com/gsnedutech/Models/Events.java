package myschoolapp.com.gsnedutech.Models;

public class Events {
    String time,eventTitle,eventDesc,type;
    int duration;

    public Events(String time, String eventTitle, String eventDesc, int duration,String type){
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
