package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeWorkDetailsTeacher implements Serializable {
    @SerializedName("homeworkId")
    @Expose
    private Integer homeworkId;
    @SerializedName("homeworkTitle")
    @Expose
    private String homeworkTitle;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("homeworkDetail")
    @Expose
    private String homeworkDetail;
    @SerializedName("fileAvailable")
    @Expose
    private Integer fileAvailable;
    @SerializedName("submissionDate")
    @Expose
    private String submissionDate;
    @SerializedName("sectionId")
    @Expose
    private Integer sectionId;
    @SerializedName("homeworkDate")
    @Expose
    private String homeworkDate;
    @SerializedName("subjectId")
    @Expose
    private Integer subjectId;
    @SerializedName("hwKeyCount")
    @Expose
    private String hwKeyCount;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("filesDetails")
    @Expose
    private List<TeacherFilesDetail> TeacherFilesDetails = null;
    @SerializedName("notSubmitted")
    @Expose
    private Integer notSubmitted;
    @SerializedName("completed")
    @Expose
    private Integer completed = 0;
    @SerializedName("totalCount")
    @Expose
    private Integer totalCount = 0;
    @SerializedName("reAssign")
    @Expose
    private Integer reAssign = 0;
    @SerializedName("submitted")
    @Expose
    private Integer submitted = 0;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type="";

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getHomeworkDetail() {
        return homeworkDetail;
    }

    public void setHomeworkDetail(String homeworkDetail) {
        this.homeworkDetail = homeworkDetail;
    }

    public Integer getFileAvailable() {
        return fileAvailable;
    }

    public void setFileAvailable(Integer fileAvailable) {
        this.fileAvailable = fileAvailable;
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

    public String getHomeworkDate() {
        return homeworkDate;
    }

    public void setHomeworkDate(String homeworkDate) {
        this.homeworkDate = homeworkDate;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getHwKeyCount() {
        return hwKeyCount;
    }

    public void setHwKeyCount(String hwKeyCount) {
        this.hwKeyCount = hwKeyCount;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<TeacherFilesDetail> getTeacherFilesDetails() {
        return TeacherFilesDetails;
    }

    public void setTeacherFilesDetails(List<TeacherFilesDetail> TeacherFilesDetails) {
        this.TeacherFilesDetails = TeacherFilesDetails;
    }

    public Integer getNotSubmitted() {
        return notSubmitted;
    }

    public void setNotSubmitted(Integer notSubmitted) {
        this.notSubmitted = notSubmitted;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getReAssign() {
        return reAssign;
    }

    public void setReAssign(Integer reAssign) {
        this.reAssign = reAssign;
    }

    public Integer getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Integer submitted) {
        this.submitted = submitted;
    }
}
