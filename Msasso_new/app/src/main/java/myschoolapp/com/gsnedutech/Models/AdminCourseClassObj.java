/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 2:23 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 7/9/19 3:20 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminCourseClassObj implements Serializable {

    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("classCourseId")
    @Expose
    private Integer classCourseId;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(Integer classCourseId) {
        this.classCourseId = classCourseId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
