package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArenaRecord implements Serializable {
    @SerializedName("questionCount")
    @Expose
    private String questionCount;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("arenaId")
    @Expose
    private Integer arenaId;
    @SerializedName("arenaName")
    @Expose
    private String arenaName;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("arenaDesc")
    @Expose
    private String arenaDesc;
    @SerializedName("arenaCategory")
    @Expose
    private String arenaCategory;
    @SerializedName("userId")
    @Expose
    private String userId="";
    @SerializedName("likesCount")
    @Expose
    private String likesCount;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("arenaStatus")
    @Expose
    private String arenaStatus;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("arenaType")
    @Expose
    private String arenaType;
    @SerializedName("studentLike")
    @Expose
    private String studentLike;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userRole")
    @Expose
    private String userRole;
    @SerializedName("studentId")
    @Expose
    private String studentId="";
    @SerializedName("arenaUserStatus")
    @Expose
    private String arenaUserStatus="";
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("totalTimeTaken")
    @Expose
    private String totalTimeTaken;
    @SerializedName("teacherReview")
    @Expose
    private String teacherReview;

    public String getTeacherReview() {
        return teacherReview;
    }

    public void setTeacherReview(String teacherReview) {
        this.teacherReview = teacherReview;
    }

    public String getArenaUserStatus() {
        return arenaUserStatus;
    }

    public void setArenaUserStatus(String arenaUserStatus) {
        this.arenaUserStatus = arenaUserStatus;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTotalTimeTaken() {
        return totalTimeTaken;
    }

    public void setTotalTimeTaken(String totalTimeTaken) {
        this.totalTimeTaken = totalTimeTaken;
    }

    public String getStudentLike() {
        return studentLike;
    }

    public void setStudentLike(String studentLike) {
        this.studentLike = studentLike;
    }

    public String getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(String questionCount) {
        this.questionCount = questionCount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getArenaId() {
        return arenaId;
    }

    public void setArenaId(Integer arenaId) {
        this.arenaId = arenaId;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getArenaDesc() {
        return arenaDesc;
    }

    public void setArenaDesc(String arenaDesc) {
        this.arenaDesc = arenaDesc;
    }

    public String getArenaCategory() {
        return arenaCategory;
    }

    public void setArenaCategory(String arenaCategory) {
        this.arenaCategory = arenaCategory;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getArenaStatus() {
        return arenaStatus;
    }

    public void setArenaStatus(String arenaStatus) {
        this.arenaStatus = arenaStatus;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getArenaType() {
        return arenaType;
    }

    public void setArenaType(String arenaType) {
        this.arenaType = arenaType;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
