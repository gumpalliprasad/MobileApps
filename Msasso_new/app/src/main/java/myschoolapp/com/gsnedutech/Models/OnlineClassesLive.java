package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnlineClassesLive implements Serializable {

    @SerializedName("liveClassDateTime")
    @Expose
    private String liveClassDateTime;
    @SerializedName("liveClassId")
    @Expose
    private String liveClassId;
    @SerializedName("teacher")
    @Expose
    private String teacher;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("admissionNumber")
    @Expose
    private String admissionNumber;
    @SerializedName("liveClassDetailId")
    @Expose
    private Integer liveClassDetailId;
    @SerializedName("liveClassUrl")
    @Expose
    private String liveClassUrl;
    @SerializedName("liveClassTitle")
    @Expose
    private String liveClassTitle;
    @SerializedName("liveClassDuration")
    @Expose
    private String liveClassDuration;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    String endTime ="NA";

    public String getLiveClassDateTime() {
        return liveClassDateTime;
    }

    public void setLiveClassDateTime(String liveClassDateTime) {
        this.liveClassDateTime = liveClassDateTime;
    }

    public String getLiveClassId() {
        return liveClassId;
    }

    public void setLiveClassId(String liveClassId) {
        this.liveClassId = liveClassId;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public Integer getLiveClassDetailId() {
        return liveClassDetailId;
    }

    public void setLiveClassDetailId(Integer liveClassDetailId) {
        this.liveClassDetailId = liveClassDetailId;
    }

    public String getLiveClassUrl() {
        return liveClassUrl;
    }

    public void setLiveClassUrl(String liveClassUrl) {
        this.liveClassUrl = liveClassUrl;
    }

    public String getLiveClassTitle() {
        return liveClassTitle;
    }

    public void setLiveClassTitle(String liveClassTitle) {
        this.liveClassTitle = liveClassTitle;
    }

    public String getLiveClassDuration() {
        return liveClassDuration;
    }

    public void setLiveClassDuration(String liveClassDuration) {
        this.liveClassDuration = liveClassDuration;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

}
