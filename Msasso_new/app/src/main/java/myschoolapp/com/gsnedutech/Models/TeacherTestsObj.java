package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherTestsObj implements Serializable {
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("testCreatedDate")
    @Expose
    private String testCreatedDate;
    @SerializedName("assignedBranchId")
    @Expose
    private String assignedBranchId;
    @SerializedName("testCategory")
    @Expose
    private String testCategory;
    @SerializedName("testStartDate")
    @Expose
    private String testStartDate;
    @SerializedName("testType")
    @Expose
    private String testType;
    @SerializedName("testId")
    @Expose
    private String testId;
    @SerializedName("testName")
    @Expose
    private String testName;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTestCreatedDate() {
        return testCreatedDate;
    }

    public void setTestCreatedDate(String testCreatedDate) {
        this.testCreatedDate = testCreatedDate;
    }

    public String getAssignedBranchId() {
        return assignedBranchId;
    }

    public void setAssignedBranchId(String assignedBranchId) {
        this.assignedBranchId = assignedBranchId;
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

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

}
