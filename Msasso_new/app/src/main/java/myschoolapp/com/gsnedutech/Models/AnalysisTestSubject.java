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

import java.io.Serializable;

public class AnalysisTestSubject implements Serializable {
    @SerializedName("testCount")
    @Expose
    private String testCount;
    @SerializedName("wrongAnswers")
    @Expose
    private String wrongAnswers;
    @SerializedName("totalTimeSpent")
    @Expose
    private String totalTimeSpent;
    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("percentage")
    @Expose
    private Double percentage;
    @SerializedName("skippedAnswers")
    @Expose
    private String skippedAnswers;
    @SerializedName("correctAnswers")
    @Expose
    private String correctAnswers;
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    public String getTestCount() {
        return testCount;
    }

    public void setTestCount(String testCount) {
        this.testCount = testCount;
    }

    public String getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(String wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(String totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getSkippedAnswers() {
        return skippedAnswers;
    }

    public void setSkippedAnswers(String skippedAnswers) {
        this.skippedAnswers = skippedAnswers;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
