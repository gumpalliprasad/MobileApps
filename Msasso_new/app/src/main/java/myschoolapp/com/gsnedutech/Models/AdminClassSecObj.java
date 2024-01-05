/*
 * *
 *  * Created by SriRamaMurthy A on 6/9/19 2:14 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 6/9/19 2:14 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminClassSecObj implements Serializable {

    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("branchId")
    @Expose
    private Integer branchId;
    @SerializedName("classCourseId")
    @Expose
    private Integer classCourseId;
    @SerializedName("sectionId")
    @Expose
    private Integer sectionId;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public Integer getClassCourseId() {
        return classCourseId;
    }

    public void setClassCourseId(Integer classCourseId) {
        this.classCourseId = classCourseId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }


}
