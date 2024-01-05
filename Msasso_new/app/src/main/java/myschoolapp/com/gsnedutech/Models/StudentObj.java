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

public class StudentObj implements Serializable {

    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("loginId")
    @Expose
    private String loginId;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("isFirstLogin")
    @Expose
    private Integer isFirstLogin;
    @SerializedName("studentRollnumber")
    @Expose
    private String studentRollnumber;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("classCourseId")
    @Expose
    private Integer classCourseId;
    @SerializedName("branchName")
    @Expose
    private String branchName;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("classCourseSectionId")
    @Expose
    private String classCourseSectionId;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("classType")
    @Expose
    private Integer classType ;



    public Integer getClassType() {
        return classType;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Integer isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public String getStudentRollnumber() {
        return studentRollnumber;
    }

    public void setStudentRollnumber(String studentRollnumber) {
        this.studentRollnumber = studentRollnumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Integer getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(Integer classCourseId) {
        this.classCourseId = classCourseId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClassCourseSectionId() {
        return classCourseSectionId;
    }

    public void setClassCourseSectionId(String classCourseSectionId) {
        this.classCourseSectionId = classCourseSectionId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}