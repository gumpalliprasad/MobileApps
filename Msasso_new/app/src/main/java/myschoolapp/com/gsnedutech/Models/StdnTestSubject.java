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

public class StdnTestSubject implements Serializable {
    @SerializedName("isElective")
    @Expose
    private String isElective;
    @SerializedName("classId")
    @Expose
    private String classId;
    @SerializedName("courseId")
    @Expose
    private String courseId;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("electiveGroupId")
    @Expose
    private String electiveGroupId = "0";

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;

    public String getIsElective() {
        return isElective;
    }

    public void setIsElective(String isElective) {
        this.isElective = isElective;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getElectiveGroupId() {
        return electiveGroupId;
    }

    public void setElectiveGroupId(String electiveGroupId) {
        this.electiveGroupId = electiveGroupId;
    }
}
