package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminTestQuestionAnalysis {

    @SerializedName("questionId")
    @Expose
    private String questionId;
    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("chapterName")
    @Expose
    private String chapterName;
    @SerializedName("correctAnswerpercentage")
    @Expose
    private String correctAnswerpercentage;
    @SerializedName("unattemptedAnswerPercentage")
    @Expose
    private String unattemptedAnswerPercentage;
    @SerializedName("wrongAnswerpercentage")
    @Expose
    private String wrongAnswerpercentage;
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("wrongAnswers")
    @Expose
    private String wrongAnswers;
    @SerializedName("unattemptedAnswers")
    @Expose
    private String unattemptedAnswers;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("topicName")
    @Expose
    private String topicName;
    @SerializedName("testId")
    @Expose
    private String testId;
    @SerializedName("correctAnswers")
    @Expose
    private String correctAnswers;
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @SerializedName("questionType")
    @Expose
    private String questionType;

    @SerializedName("chapterCCMapId")
    @Expose
    private String chapterCCMapId;

    public String getChapterCCMapId() {
        return chapterCCMapId;
    }

    public void setChapterCCMapId(String chapterCCMapId) {
        this.chapterCCMapId = chapterCCMapId;
    }



    int qNum=0;

    public void setqNum(int qNum) {
        this.qNum = qNum;
    }

    public int getqNum() {
        return qNum;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getCorrectAnswerpercentage() {
        return correctAnswerpercentage;
    }

    public void setCorrectAnswerpercentage(String correctAnswerpercentage) {
        this.correctAnswerpercentage = correctAnswerpercentage;
    }

    public String getUnattemptedAnswerPercentage() {
        return unattemptedAnswerPercentage;
    }

    public void setUnattemptedAnswerPercentage(String unattemptedAnswerPercentage) {
        this.unattemptedAnswerPercentage = unattemptedAnswerPercentage;
    }

    public String getWrongAnswerpercentage() {
        return wrongAnswerpercentage;
    }

    public void setWrongAnswerpercentage(String wrongAnswerpercentage) {
        this.wrongAnswerpercentage = wrongAnswerpercentage;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(String wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getUnattemptedAnswers() {
        return unattemptedAnswers;
    }

    public void setUnattemptedAnswers(String unattemptedAnswers) {
        this.unattemptedAnswers = unattemptedAnswers;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

}
