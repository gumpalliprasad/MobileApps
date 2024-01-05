/*
 * *
 *  * Created by SriRamaMurthy A on 25/9/19 12:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 23/9/19 5:38 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudentProfileObj implements Serializable {
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("studentAddressId")
    @Expose
    private Integer studentAddressId;
    @SerializedName("classCourseId")
    @Expose
    private String classCourseId;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("studentEmail")
    @Expose
    private String studentEmail;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("STATUS")
    @Expose
    private String sTATUS;
    @SerializedName("DOB")
    @Expose
    private String dOB;
    @SerializedName("loginName")
    @Expose
    private String loginName;
    @SerializedName("rollNumber")
    @Expose
    private String rollNumber;
    @SerializedName("contactNumber")
    @Expose
    private String contactNumber;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("pincode")
    @Expose
    private Integer pincode;
    @SerializedName("studentPhoneNumber")
    @Expose
    private String studentPhoneNumber;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("aadharNo")
    @Expose
    private String aadharNo;
    @SerializedName("parentId")
    @Expose
    private Integer parentId;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("parentName")
    @Expose
    private String parentName;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("admissionNumber")
    @Expose
    private String admissionNumber;
    @SerializedName("parentEmailId")
    @Expose
    private String parentEmailId;
    @SerializedName("primaryAddress")
    @Expose
    private String primaryAddress;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getStudentAddressId() {
        return studentAddressId;
    }

    public void setStudentAddressId(Integer studentAddressId) {
        this.studentAddressId = studentAddressId;
    }

    public String getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(String classCourseId) {
        this.classCourseId = classCourseId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getSTATUS() {
        return sTATUS;
    }

    public void setSTATUS(String sTATUS) {
        this.sTATUS = sTATUS;
    }

    public String getDOB() {
        return dOB;
    }

    public void setDOB(String dOB) {
        this.dOB = dOB;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public String getStudentPhoneNumber() {
        return studentPhoneNumber;
    }

    public void setStudentPhoneNumber(String studentPhoneNumber) {
        this.studentPhoneNumber = studentPhoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getParentEmailId() {
        return parentEmailId;
    }

    public void setParentEmailId(String parentEmailId) {
        this.parentEmailId = parentEmailId;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(String primaryAddress) {
        this.primaryAddress = primaryAddress;
    }
}