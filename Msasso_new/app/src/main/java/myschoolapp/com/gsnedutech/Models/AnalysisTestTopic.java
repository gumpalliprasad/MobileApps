/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnalysisTestTopic {
    @SerializedName("testCount")
    @Expose
    private String testCount;
    @SerializedName("totalQuestions")
    @Expose
    private Integer totalQuestions;
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("wrongAnswers")
    @Expose
    private Integer wrongAnswers;
    @SerializedName("totalTimeSpent")
    @Expose
    private String totalTimeSpent;
    @SerializedName("topicName")
    @Expose
    private String topicName;
    @SerializedName("testType")
    @Expose
    private String testType;
    @SerializedName("correctAnswers")
    @Expose
    private Integer correctAnswers;
    @SerializedName("correctAnswerPercentage")
    @Expose
    private String correctAnswerPercentage;
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

    public String getTestCount() {
        return testCount;
    }

    public void setTestCount(String testCount) {
        this.testCount = testCount;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(String totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getCorrectAnswerPercentage() {
        return correctAnswerPercentage;
    }

    public void setCorrectAnswerPercentage(String correctAnswerPercentage) {
        this.correctAnswerPercentage = correctAnswerPercentage;
    }

}
