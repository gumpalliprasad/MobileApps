
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

public class StudentOnlineTestObj implements Serializable {

    @SerializedName("correctAnswerMarks")
    @Expose
    private String mCorrectAnswerMarks;
    @SerializedName("student_id")
    @Expose
    private String mStudentId;
    @SerializedName("studentTestStatus")
    @Expose
    private String mStudentTestStatus;
    @SerializedName("testCategory")
    @Expose
    private String mTestCategory;
    @SerializedName("testCategoryName")
    @Expose
    private String mTestCategoryName;
    @SerializedName("testDuration")
    @Expose
    private String mTestDuration;
    @SerializedName("testEndDate")
    @Expose
    private String mTestEndDate;
    @SerializedName("testId")
    @Expose
    private String mTestId;
    @SerializedName("testName")
    @Expose
    private String mTestName;
    @SerializedName("testStartDate")
    @Expose
    private String mTestStartDate;
    @SerializedName("testStatus")
    @Expose
    private String mTestStatus;
    @SerializedName("totalQuestions")
    @Expose
    private String mTotalQuestions;
    @SerializedName("WrongAnswerMarks")
    @Expose
    private String mWrongAnswerMarks;
    @SerializedName("jeeSectionTemplateName")
    @Expose
    private String mjeeSectionTemplateName = "NA";
    @SerializedName("testFilePath")
    @Expose
    private String testFilePath = "NA";
    @SerializedName("studentTestFilePath")
    @Expose
    private String studentTestFilePath = "NA";

    public String getStudentTestFilePath() {
        return studentTestFilePath;
    }

    public void setStudentTestFilePath(String studentTestFilePath) {
        this.studentTestFilePath = studentTestFilePath;
    }

    @SerializedName("testResultDate")
    @Expose
    private String testResultDate;

    public String getTestResultDate() {
        return testResultDate;
    }

    public void setTestResultDate(String testResultDate) {
        this.testResultDate = testResultDate;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }


    public String getMjeeSectionTemplateName() {
        return mjeeSectionTemplateName;
    }

    public void setMjeeSectionTemplateName(String mjeeSectionTemplateName) {
        this.mjeeSectionTemplateName = mjeeSectionTemplateName;
    }

    public String getCorrectAnswerMarks() {
        return mCorrectAnswerMarks;
    }

    public void setCorrectAnswerMarks(String correctAnswerMarks) {
        mCorrectAnswerMarks = correctAnswerMarks;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public void setStudentId(String studentId) {
        mStudentId = studentId;
    }

    public String getStudentTestStatus() {
        return mStudentTestStatus;
    }

    public void setStudentTestStatus(String studentTestStatus) {
        mStudentTestStatus = studentTestStatus;
    }

    public String getTestCategory() {
        return mTestCategory;
    }

    public void setTestCategory(String testCategory) {
        mTestCategory = testCategory;
    }

    public String getTestCategoryName() {
        return mTestCategoryName;
    }

    public void setTestCategoryName(String testCategoryName) {
        mTestCategoryName = testCategoryName;
    }

    public String getTestDuration() {
        return mTestDuration;
    }

    public void setTestDuration(String testDuration) {
        mTestDuration = testDuration;
    }

    public String getTestEndDate() {
        return mTestEndDate;
    }

    public void setTestEndDate(String testEndDate) {
        mTestEndDate = testEndDate;
    }

    public String getTestId() {
        return mTestId;
    }

    public void setTestId(String testId) {
        mTestId = testId;
    }

    public String getTestName() {
        return mTestName;
    }

    public void setTestName(String testName) {
        mTestName = testName;
    }

    public String getTestStartDate() {
        return mTestStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        mTestStartDate = testStartDate;
    }

    public String getTestStatus() {
        return mTestStatus;
    }

    public void setTestStatus(String testStatus) {
        mTestStatus = testStatus;
    }

    public String getTotalQuestions() {
        return mTotalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        mTotalQuestions = totalQuestions;
    }

    public String getWrongAnswerMarks() {
        return mWrongAnswerMarks;
    }

    public void setWrongAnswerMarks(String wrongAnswerMarks) {
        mWrongAnswerMarks = wrongAnswerMarks;
    }

}
