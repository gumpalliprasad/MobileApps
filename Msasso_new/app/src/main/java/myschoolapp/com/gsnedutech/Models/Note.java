package myschoolapp.com.gsnedutech.Models;
public class Note {


    private int id;
    private String note;
    private String userid;
    private String usertype;
    private String timestamp;
    private int isCompleted;


    // Create table SQL query


    public Note() {
    }

    public Note(int id, String note, String timestamp, int isCompleted, String userid, String usertype) {
        this.id = id;
        this.note = note;
        this.timestamp = timestamp;
        this.isCompleted = isCompleted;
        this.userid = userid;
        this.usertype = usertype;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public int isCompleted() {
        return isCompleted;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserid() {
        return userid;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCompleted(int completed) {
        isCompleted = completed;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }
}