/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 31/8/19 2:17 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AdminClassCourseObj implements Serializable {
    @SerializedName("instType")
    @Expose
    private Integer instType;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("isDefault")
    @Expose
    private Integer isDefault;
    @SerializedName("courseActive")
    @Expose
    private Integer courseActive;
    @SerializedName("classes")
    @Expose
    private List<AdminClassObj> classes = null;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;

    public Integer getInstType() {
        return instType;
    }

    public void setInstType(Integer instType) {
        this.instType = instType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getCourseActive() {
        return courseActive;
    }

    public void setCourseActive(Integer courseActive) {
        this.courseActive = courseActive;
    }

    public List<AdminClassObj> getClasses() {
        return classes;
    }

    public void setClasses(List<AdminClassObj> classes) {
        this.classes = classes;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

}
