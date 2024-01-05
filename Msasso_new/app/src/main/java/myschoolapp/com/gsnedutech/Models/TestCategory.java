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

public class TestCategory implements Serializable {

    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("branchName")
    @Expose
    private String branchName;
    @SerializedName("years")
    @Expose
    private List<TeacherTestYear> teacherTestYears = null;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<TeacherTestYear> getTeacherTestYears() {
        return teacherTestYears;
    }

    public void setTeacherTestYears(List<TeacherTestYear> teacherTestYears) {
        this.teacherTestYears = teacherTestYears;
    }

}