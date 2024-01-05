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

public class TeacherTestYear implements Serializable {

    @SerializedName("Months")
    @Expose
    private List<TeacherTestMonth> teacherTestMonths = null;
    @SerializedName("yearId")
    @Expose
    private String yearId;

    public List<TeacherTestMonth> getTeacherTestMonths() {
        return teacherTestMonths;
    }

    public void setTeacherTestMonths(List<TeacherTestMonth> teacherTestMonths) {
        this.teacherTestMonths = teacherTestMonths;
    }

    public String getYearId() {
        return yearId;
    }

    public void setYearId(String yearId) {
        this.yearId = yearId;
    }

}