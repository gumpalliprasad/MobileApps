/*
 * *
 *  * Created by SriRamaMurthy A on 4/9/19 1:08 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 4/9/19 12:13 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminStaffAttendObj {
    @SerializedName("presentCount")
    @Expose
    private String presentCount;
    @SerializedName("lateCount")
    @Expose
    private String lateCount;
    @SerializedName("presentPercentage")
    @Expose
    private String presentPercentage;
    @SerializedName("absentCount")
    @Expose
    private String absentCount;
    @SerializedName("absentPercentage")
    @Expose
    private String absentPercentage;
    @SerializedName("latePercentage")
    @Expose
    private String latePercentage;

    @SerializedName("staffAttendanceReportId")
    @Expose
    private String staffAttendanceReportId;

    public String getStaffAttendanceReportId() {
        return staffAttendanceReportId;
    }

    public void setStaffAttendanceReportId(String staffAttendanceReportId) {
        this.staffAttendanceReportId = staffAttendanceReportId;
    }


    public String getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(String presentCount) {
        this.presentCount = presentCount;
    }

    public String getLateCount() {
        return lateCount;
    }

    public void setLateCount(String lateCount) {
        this.lateCount = lateCount;
    }

    public String getPresentPercentage() {
        return presentPercentage;
    }

    public void setPresentPercentage(String presentPercentage) {
        this.presentPercentage = presentPercentage;
    }

    public String getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(String absentCount) {
        this.absentCount = absentCount;
    }

    public String getAbsentPercentage() {
        return absentPercentage;
    }

    public void setAbsentPercentage(String absentPercentage) {
        this.absentPercentage = absentPercentage;
    }

    public String getLatePercentage() {
        return latePercentage;
    }

    public void setLatePercentage(String latePercentage) {
        this.latePercentage = latePercentage;
    }
}
