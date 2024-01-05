package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SubChapterTopic implements Serializable {
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("topicOwner")
    @Expose
    private String topicOwner;
    @SerializedName("chaptertopicAccessId")
    @Expose
    private String chaptertopicAccessId;
    @SerializedName("topicName")
    @Expose
    private String topicName;
    @SerializedName("isActive")
    @Expose
    private String isActive;

    @SerializedName("topicCCMapId")
    @Expose
    private String topicCCMapId;

    @SerializedName("resourceCount")
    @Expose
    private String resourceCount;
    @SerializedName("videoCount")
    @Expose
    private String videoCount;
    @SerializedName("annexureCount")
    @Expose
    private String annexureCount;
    @SerializedName("classroomVideoCount")
    @Expose
    private String classroomVideoCount;
    @SerializedName("questionAnswerCount")
    @Expose
    private String questionAnswerCount;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("importantCount")
    @Expose
    private String importantCount;
    @SerializedName("questionsCount")
    @Expose
    private String questionsCount;
    @SerializedName("textbookCount")
    @Expose
    private String textbookCount;

    public String getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(String resourceCount) {
        this.resourceCount = resourceCount;
    }

    public String getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(String videoCount) {
        this.videoCount = videoCount;
    }

    public String getAnnexureCount() {
        return annexureCount;
    }

    public void setAnnexureCount(String annexureCount) {
        this.annexureCount = annexureCount;
    }

    public String getClassroomVideoCount() {
        return classroomVideoCount;
    }

    public void setClassroomVideoCount(String classroomVideoCount) {
        this.classroomVideoCount = classroomVideoCount;
    }

    public String getQuestionAnswerCount() {
        return questionAnswerCount;
    }

    public void setQuestionAnswerCount(String questionAnswerCount) {
        this.questionAnswerCount = questionAnswerCount;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getImportantCount() {
        return importantCount;
    }

    public void setImportantCount(String importantCount) {
        this.importantCount = importantCount;
    }

    public String getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(String questionsCount) {
        this.questionsCount = questionsCount;
    }

    public String getTextbookCount() {
        return textbookCount;
    }

    public void setTextbookCount(String textbookCount) {
        this.textbookCount = textbookCount;
    }

    public String getTopicContentOwner() {
        return topicContentOwner;
    }

    public void setTopicContentOwner(String topicContentOwner) {
        this.topicContentOwner = topicContentOwner;
    }

    @SerializedName("topicContentOwner")
    @Expose
    private String topicContentOwner;
    public String getTopicCCMapId() {
        return topicCCMapId;
    }

    public void setTopicCCMapId(String topicCCMapId) {
        this.topicCCMapId = topicCCMapId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicOwner() {
        return topicOwner;
    }

    public void setTopicOwner(String topicOwner) {
        this.topicOwner = topicOwner;
    }

    public String getChaptertopicAccessId() {
        return chaptertopicAccessId;
    }

    public void setChaptertopicAccessId(String chaptertopicAccessId) {
        this.chaptertopicAccessId = chaptertopicAccessId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
