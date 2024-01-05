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

public class AdminClassObj implements Serializable {
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("isAssigned")
    @Expose
    private Integer isAssigned;
    @SerializedName("classCourseId")
    @Expose
    private String classCourseId;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("classActive")
    @Expose
    private Integer classActive;

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(Integer isAssigned) {
        this.isAssigned = isAssigned;
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

    public Integer getClassActive() {
        return classActive;
    }

    public void setClassActive(Integer classActive) {
        this.classActive = classActive;
    }

}
