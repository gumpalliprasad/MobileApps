package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DescriptiveStudents implements Serializable {

    @SerializedName("admissionNumber")
    @Expose
    private String admissionNumber;
    @SerializedName("branchId")
    @Expose
    private Integer branchId;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("sectionId")
    @Expose
    private Integer sectionId;
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("testFinishDate")
    @Expose
    private String testFinishDate;
    @SerializedName("testStartDate")
    @Expose
    private String testStartDate;
    @SerializedName("testStatus")
    @Expose
    private String testStatus;
    @SerializedName("reviewStatus")
    @Expose
    private String reviewStatus;
    @SerializedName("reviewComments")
    @Expose
    private String reviewComments;

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTestFinishDate() {
        return testFinishDate;
    }

    public void setTestFinishDate(String testFinishDate) {
        this.testFinishDate = testFinishDate;
    }

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }

}

