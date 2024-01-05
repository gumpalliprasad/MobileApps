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
import java.util.List;

public class TeacherCCSSObj implements Serializable {
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("courseId")
    @Expose
    private String courseId;
    @SerializedName("Classes")
    @Expose
    private List<TeacherCCSSClass> classes = null;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<TeacherCCSSClass> getClasses() {
        return classes;
    }

    public void setClasses(List<TeacherCCSSClass> classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return courseName+"   ";
    }
}
