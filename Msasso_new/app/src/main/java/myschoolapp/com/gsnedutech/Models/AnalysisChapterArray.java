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

public class AnalysisChapterArray implements Serializable {
    @SerializedName("testCount")
    @Expose
    private String testCount;
    @SerializedName("totalQuestions")
    @Expose
    private String totalQuestions;
    @SerializedName("totalTimeSpent")
    @Expose
    private String totalTimeSpent = "0";
    @SerializedName("testTypeName")
    @Expose
    private String testTypeName;
    @SerializedName("testType")
    @Expose
    private String testType;
    @SerializedName("correctAnswers")
    @Expose
    private String correctAnswers;

    public String getTestCount() {
        return testCount;
    }

    public void setTestCount(String testCount) {
        this.testCount = testCount;
    }

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(String totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getTestTypeName() {
        return testTypeName;
    }

    public void setTestTypeName(String testTypeName) {
        this.testTypeName = testTypeName;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
