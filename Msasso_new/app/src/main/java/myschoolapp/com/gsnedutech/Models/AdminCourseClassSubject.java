/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 2:23 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 7/9/19 5:13 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminCourseClassSubject implements Serializable {
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("subjectId")
    @Expose
    private Integer subjectId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
