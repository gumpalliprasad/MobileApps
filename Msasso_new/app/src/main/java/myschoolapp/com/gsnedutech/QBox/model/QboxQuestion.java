package myschoolapp.com.gsnedutech.QBox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QboxQuestion implements Serializable
{

    @SerializedName("studentLike")
    @Expose
    private Integer studentLike;
    @SerializedName("stuqboxId")
    @Expose
    private Integer stuqboxId;
    @SerializedName("qboxFileCount")
    @Expose
    private Integer qboxFileCount;
    @SerializedName("qboxTitle")
    @Expose
    private String qboxTitle;
    @SerializedName("subjectId")
    @Expose
    private Integer subjectId;
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("topicId")
    @Expose
    private Integer topicId;
    @SerializedName("qboxStatus")
    @Expose
    private Integer qboxStatus;
    @SerializedName("replyCount")
    @Expose
    private Integer replyCount;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("chapterId")
    @Expose
    private Integer chapterId;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("admissionNumber")
    @Expose
    private String admissionNumber;
    @SerializedName("profilePath")
    @Expose
    private String profilePath;
    @SerializedName("qboxQuestion")
    @Expose
    private String qboxQuestion;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("topicCCMapId")
    @Expose
    private String topicCCMapId;
    public String getTopicCCMapId() {
        return topicCCMapId;
    }

    public void setTopicCCMapId(String topicCCMapId) {
        this.topicCCMapId = topicCCMapId;
    }

    @SerializedName("questionCCMapId")
    @Expose
    private String questionCCMapId;
    public String getQuestionCCMapId() {
        return questionCCMapId;
    }

    public void setQuestionCCMapId(String questionCCMapId) {
        this.questionCCMapId = questionCCMapId;
    }

    public Integer getStudentLike() {
        return studentLike;
    }

    public void setStudentLike(Integer studentLike) {
        this.studentLike = studentLike;
    }

    public Integer getStuqboxId() {
        return stuqboxId;
    }

    public void setStuqboxId(Integer stuqboxId) {
        this.stuqboxId = stuqboxId;
    }

    public Integer getQboxFileCount() {
        return qboxFileCount;
    }

    public void setQboxFileCount(Integer qboxFileCount) {
        this.qboxFileCount = qboxFileCount;
    }

    public String getQboxTitle() {
        return qboxTitle;
    }

    public void setQboxTitle(String qboxTitle) {
        this.qboxTitle = qboxTitle;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getQboxStatus() {
        return qboxStatus;
    }

    public void setQboxStatus(Integer qboxStatus) {
        this.qboxStatus = qboxStatus;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public String getQboxQuestion() {
        return qboxQuestion;
    }

    public void setQboxQuestion(String qboxQuestion) {
        this.qboxQuestion = qboxQuestion;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

}