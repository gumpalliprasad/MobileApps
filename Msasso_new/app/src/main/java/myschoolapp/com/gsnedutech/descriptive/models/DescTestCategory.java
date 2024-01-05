package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DescTestCategory implements Serializable {

    @SerializedName("question_number")
    @Expose
    private Integer questionNumber;
    @SerializedName("questionId")
    @Expose
    private Integer questionId;
    @SerializedName("correctAnswer")
    @Expose
    private String correctAnswer;
    @SerializedName("selectedAnswer")
    @Expose
    private String selectedAnswer = "";
    @SerializedName("style")
    @Expose
    private String style = "not_visited_div";
    @SerializedName("timeTaken")
    @Expose
    private String timeTaken;
    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("correctMarks")
    @Expose
    private String correctMarks;
    @SerializedName("wrongMarks")
    @Expose
    private String wrongMarks;
    @SerializedName("testCategory")
    @Expose
    private String testCategory;
    @SerializedName("updated")
    @Expose
    private Boolean updated;
    @SerializedName("chapterName")
    @Expose
    private String chapterName;
    @SerializedName("topic_name")
    @Expose
    private String topicName;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("branchId")
    @Expose
    private String branchId;

    @SerializedName("testSectionName")
    @Expose
    private String testSectionName = "NA";
    @SerializedName("chapterCCMapId")
    @Expose
    private String chapterCCMapId;

    public String getChapterCCMapId() {
        return chapterCCMapId;
    }

    public void setChapterCCMapId(String chapterCCMapId) {
        this.chapterCCMapId = chapterCCMapId;
    }

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

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getCorrectMarks() {
        return correctMarks;
    }

    public void setCorrectMarks(String correctMarks) {
        this.correctMarks = correctMarks;
    }

    public String getWrongMarks() {
        return wrongMarks;
    }

    public void setWrongMarks(String wrongMarks) {
        this.wrongMarks = wrongMarks;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public boolean isReviewflag() {
        return reviewflag;
    }

    public void setReviewflag(boolean reviewflag) {
        this.reviewflag = reviewflag;
    }

    @SerializedName("reviewflag")
    @Expose
    private boolean reviewflag = false;
    @SerializedName("visitedflag")
    @Expose
    private boolean visitedflag = false;
    @SerializedName("proceed")
    @Expose
    private boolean proceed = false;

    public boolean isVisitedflag() {
        return visitedflag;
    }

    public void setVisitedflag(boolean visitedflag) {
        this.visitedflag = visitedflag;
    }

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    public String getTestSectionName() {
        return testSectionName;
    }

    public void setTestSectionName(String testSectionName) {
        this.testSectionName = testSectionName;
    }

    @SerializedName("question")
    @Expose
    private String question;

}

