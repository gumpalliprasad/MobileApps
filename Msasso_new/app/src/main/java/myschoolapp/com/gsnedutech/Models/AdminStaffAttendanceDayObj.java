/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 2:23 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 9/9/19 1:48 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdminStaffAttendanceDayObj {
    @SerializedName("staffList")
    @Expose
    private List<AdminStaffAbsentObj> staffList = null;
    @SerializedName("day")
    @Expose
    private Integer day;

    public List<AdminStaffAbsentObj> getStaffList() {
        return staffList;
    }

    public void setStaffList(List<AdminStaffAbsentObj> staffList) {
        this.staffList = staffList;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

}
