/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 24/10/19 5:59 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AttendaceAnalysis implements Serializable {
    @SerializedName("attendanceTakenDays")
    @Expose
    private Integer attendanceTakenDays;
    @SerializedName("totalDays")
    @Expose
    private Integer totalDays;
    @SerializedName("MonthandYear")
    @Expose
    private String monthandYear;
    @SerializedName("workingDays")
    @Expose
    private Integer workingDays;
    @SerializedName("lateCount")
    @Expose
    private String lateCount;
    @SerializedName("absentCount")
    @Expose
    private String absentCount;
    @SerializedName("MonthName")
    @Expose
    private String monthName;

    public Integer getAttendanceTakenDays() {
        return attendanceTakenDays;
    }

    public void setAttendanceTakenDays(Integer attendanceTakenDays) {
        this.attendanceTakenDays = attendanceTakenDays;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public String getMonthandYear() {
        return monthandYear;
    }

    public void setMonthandYear(String monthandYear) {
        this.monthandYear = monthandYear;
    }

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }

    public String getLateCount() {
        return lateCount;
    }

    public void setLateCount(String lateCount) {
        this.lateCount = lateCount;
    }

    public String getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(String absentCount) {
        this.absentCount = absentCount;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
}
