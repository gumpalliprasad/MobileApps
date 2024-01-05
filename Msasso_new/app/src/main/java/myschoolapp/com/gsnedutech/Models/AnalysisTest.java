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

public class AnalysisTest implements Serializable {

    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("testCategoryName")
    @Expose
    private String testCategoryName;
    @SerializedName("testCount")
    @Expose
    private String testCount;
    @SerializedName("totalTimeSpent")
    @Expose
    private String totalTimeSpent;
    @SerializedName("percentage")
    @Expose
    private String percentage;
    @SerializedName("testType")
    @Expose
    private String testType;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTestCategoryName() {
        return testCategoryName;
    }

    public void setTestCategoryName(String testCategoryName) {
        this.testCategoryName = testCategoryName;
    }

    public String getTestCount() {
        return testCount;
    }

    public void setTestCount(String testCount) {
        this.testCount = testCount;
    }

    public String getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(String totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }
}
