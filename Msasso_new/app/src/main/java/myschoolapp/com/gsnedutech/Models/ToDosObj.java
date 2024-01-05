package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ToDosObj implements Serializable {
    @SerializedName("todoEndTime")
    @Expose
    private String todoEndTime;
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("todoStartTime")
    @Expose
    private String todoStartTime="";
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("todoListId")
    @Expose
    private String todoListId;
    @SerializedName("todoDesc")
    @Expose
    private String todoDesc;
    @SerializedName("todoTitle")
    @Expose
    private String todoTitle;
    @SerializedName("isRemainder")
    @Expose
    private String isRemainder;
    @SerializedName("todoStatus")
    @Expose
    private String todoStatus;

    public String getTodoStatus() {
        return todoStatus;
    }

    public void setTodoStatus(String todoStatus) {
        this.todoStatus = todoStatus;
    }



    public ToDosObj(String title, String desc, String startTime, String endTime, String isRemainder) {
        this.todoTitle = title;
        this.todoDesc = desc;
        this.todoStartTime = startTime;
        this.todoEndTime = endTime;
        this.isRemainder = isRemainder;
    }

    public String getTodoEndTime() {
        return todoEndTime;
    }

    public void setTodoEndTime(String todoEndTime) {
        this.todoEndTime = todoEndTime;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTodoStartTime() {
        return todoStartTime;
    }

    public void setTodoStartTime(String todoStartTime) {
        this.todoStartTime = todoStartTime;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }

    public String getTodoDesc() {
        return todoDesc;
    }

    public void setTodoDesc(String todoDesc) {
        this.todoDesc = todoDesc;
    }

    public String getTodoTitle() {
        return todoTitle;
    }

    public void setTodoTitle(String todoTitle) {
        this.todoTitle = todoTitle;
    }

    public String getIsRemainder() {
        return isRemainder;
    }

    public void setIsRemainder(String isRemainder) {
        this.isRemainder = isRemainder;
    }

}
