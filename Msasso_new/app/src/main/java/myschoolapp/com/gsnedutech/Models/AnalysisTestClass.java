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

public class AnalysisTestClass implements Serializable {

    @SerializedName("testCount")
    @Expose
    private Integer testCount;
    @SerializedName("wrongAnswers")
    @Expose
    private Integer wrongAnswers;
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("totalTimeSpent")
    @Expose
    private Integer totalTimeSpent;
    @SerializedName("percentage")
    @Expose
    private Float percentage;
    @SerializedName("skippedAnswers")
    @Expose
    private Integer skippedAnswers;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("correctAnswers")
    @Expose
    private Integer correctAnswers;

    public Integer getTestCount() {
        return testCount;
    }

    public void setTestCount(Integer testCount) {
        this.testCount = testCount;
    }

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(Integer totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public Integer getSkippedAnswers() {
        return skippedAnswers;
    }

    public void setSkippedAnswers(Integer skippedAnswers) {
        this.skippedAnswers = skippedAnswers;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
