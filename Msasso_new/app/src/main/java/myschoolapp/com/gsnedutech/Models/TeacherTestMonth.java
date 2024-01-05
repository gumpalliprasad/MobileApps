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

public class TeacherTestMonth implements Serializable {

    @SerializedName("Weeks")
    @Expose
    private List<TeacherTestWeek> teacherTestWeeks = null;
    @SerializedName("monthName")
    @Expose
    private String monthName;
    @SerializedName("monthId")
    @Expose
    private String monthId;

    public List<TeacherTestWeek> getTeacherTestWeeks() {
        return teacherTestWeeks;
    }

    public void setTeacherTestWeeks(List<TeacherTestWeek> teacherTestWeeks) {
        this.teacherTestWeeks = teacherTestWeeks;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthId() {
        return monthId;
    }

    public void setMonthId(String monthId) {
        this.monthId = monthId;
    }

}