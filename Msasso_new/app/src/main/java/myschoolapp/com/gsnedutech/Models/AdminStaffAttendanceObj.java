/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 31/10/19 3:26 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminStaffAttendanceObj {
    String attendance;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("staffLeaveRequId")
    @Expose
    private Integer staffLeaveRequId;
    @SerializedName("contactNo")
    @Expose
    private String contactNo;
    @SerializedName("isChecked")
    @Expose
    boolean isChecked;
    @SerializedName("previousState")
    @Expose
    private String previousState = "blank";
    @SerializedName("attendanceType")
    @Expose
    private String attendanceType = "blank";
    @SerializedName("attendanceId")
    @Expose
    private String attendanceId = "0";
    @SerializedName("reason")
    @Expose
    private String reason = "NA";

    public String getPreviousState() {
        return previousState;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStaffLeaveRequId() {
        return staffLeaveRequId;
    }

    public void setStaffLeaveRequId(Integer staffLeaveRequId) {
        this.staffLeaveRequId = staffLeaveRequId;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
