/*
 * *
 *  * Created by SriRamaMurthy A on 10/10/19 5:39 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 10/10/19 5:39 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeWorkDetails implements Serializable {

    @SerializedName("homeworkId")
    @Expose
    private Integer homeworkId;
    @SerializedName("filesDetails")
    @Expose
    private List<HomeWorkFilesDetail> filesDetails = new ArrayList<>();
    @SerializedName("hwStatus")
    @Expose
    private String hwStatus;
    @SerializedName("teacherComments")
    @Expose
    private String teacherComments;
    @SerializedName("hwRating")
    @Expose
    private String hwRating;
    @SerializedName("fileAvailable")
    @Expose
    private String fileAvailable;
    @SerializedName("marks")
    @Expose
    private String marks;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("submissionDate")
    @Expose
    private String submissionDate;
    @SerializedName("subjectId")
    @Expose
    private Integer subjectId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("homeDetails")
    @Expose
    private String homeDetails;

    @SerializedName("notSubmitted")
    @Expose
    private String notSubmitted = "0";
    @SerializedName("submitted")
    @Expose
    private String submitted = "0";
    @SerializedName("homeworkDetail")
    @Expose
    private String homeworkDetail;
    @SerializedName("hwTitle")
    @Expose
    private String hwTitle;

    public String getHwTitle() {
        return hwTitle;
    }

    public void setHwTitle(String hwTitle) {
        this.hwTitle = hwTitle;
    }

    public String getHwSubmitDate() {
        return hwSubmitDate;
    }

    public void setHwSubmitDate(String hwSubmitDate) {
        this.hwSubmitDate = hwSubmitDate;
    }

    @SerializedName("hwSubmitDate")
    @Expose
    private String hwSubmitDate;

    public String getHwDate() {
        return hwDate;
    }

    public void setHwDate(String hwDate) {
        this.hwDate = hwDate;
    }

    String hwDate="";


    public String getHwStatus() {
        return hwStatus;
    }

    public void setHwStatus(String hwStatus) {
        this.hwStatus = hwStatus;
    }

    public String getTeacherComments() {
        return teacherComments;
    }

    public void setTeacherComments(String teacherComments) {
        this.teacherComments = teacherComments;
    }

    public String getHwRating() {
        return hwRating;
    }

    public void setHwRating(String hwRating) {
        this.hwRating = hwRating;
    }

    public String getFileAvailable() {
        return fileAvailable;
    }

    public void setFileAvailable(String fileAvailable) {
        this.fileAvailable = fileAvailable;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getHomeDetails() {
        return homeDetails;
    }

    public void setHomeDetails(String homeDetails) {
        this.homeDetails = homeDetails;
    }

    public List<HomeWorkFilesDetail> getFilesDetails() {
        return filesDetails;
    }

    @SerializedName("isCompleted")
    @Expose
    private String isCompleted = "0";

    public String getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(String isCompleted) {
        this.isCompleted = isCompleted;
    }

    @SerializedName("sectionId")
    @Expose
    private Integer sectionId;


    public void setFilesDetails(List<HomeWorkFilesDetail> filesDetails) {
        this.filesDetails = filesDetails;
    }

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getHomeworkDetail() {
        return homeworkDetail;
    }

    public void setHomeworkDetail(String homeworkDetail) {
        this.homeworkDetail = homeworkDetail;
    }

    public String getNotSubmitted() {
        return notSubmitted;
    }

    public void setNotSubmitted(String notSubmitted) {
        this.notSubmitted = notSubmitted;
    }

    public String getSubmitted() {
        return submitted;
    }

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

}
