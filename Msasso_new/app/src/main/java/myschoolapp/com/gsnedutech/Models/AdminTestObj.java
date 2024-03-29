/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 3:06 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 9/9/19 2:30 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminTestObj implements Serializable {
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("wrongMarks")
    @Expose
    private String wrongMarks;
    @SerializedName("testCategory")
    @Expose
    private String testCategory;
    @SerializedName("testStartDate")
    @Expose
    private String testStartDate = "blank";
    @SerializedName("testType")
    @Expose
    private String testType;
    @SerializedName("numQuestions")
    @Expose
    private String numQuestions;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("assignedBranchId")
    @Expose
    private String assignedBranchId;
    @SerializedName("testCreationType")
    @Expose
    private String testCreationType;
    @SerializedName("testStatus")
    @Expose
    private String testStatus;
    @SerializedName("testId")
    @Expose
    private String testId;
    @SerializedName("correctMarks")
    @Expose
    private String correctMarks;
    @SerializedName("testName")
    @Expose
    private String testName;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
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

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(String numQuestions) {
        this.numQuestions = numQuestions;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getAssignedBranchId() {
        return assignedBranchId;
    }

    public void setAssignedBranchId(String assignedBranchId) {
        this.assignedBranchId = assignedBranchId;
    }

    public String getTestCreationType() {
        return testCreationType;
    }

    public void setTestCreationType(String testCreationType) {
        this.testCreationType = testCreationType;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getCorrectMarks() {
        return correctMarks;
    }

    public void setCorrectMarks(String correctMarks) {
        this.correctMarks = correctMarks;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }


}
