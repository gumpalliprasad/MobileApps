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

public class ParentStudentDetail implements Serializable {
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("studentRollnumber")
    @Expose
    private String studentRollnumber;
    @SerializedName("classCourseSectionId")
    @Expose
    private Integer classCourseSectionId;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("classCourseId")
    @Expose
    private String classCourseId;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;

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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
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

    public String getStudentRollnumber() {
        return studentRollnumber;
    }

    public void setStudentRollnumber(String studentRollnumber) {
        this.studentRollnumber = studentRollnumber;
    }

    public Integer getClassCourseSectionId() {
        return classCourseSectionId;
    }

    public void setClassCourseSectionId(Integer classCourseSectionId) {
        this.classCourseSectionId = classCourseSectionId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

}
