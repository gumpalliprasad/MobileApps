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

public class StudentLeavesObj implements Serializable {
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("LeaveRequestStatus")
    @Expose
    private String leaveRequestStatus;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("teacherComments")
    @Expose
    private Integer teacherComments;
    @SerializedName("leaveTo")
    @Expose
    private String leaveTo;
    @SerializedName("leaveFrom")
    @Expose
    private String leaveFrom;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getLeaveRequestStatus() {
        return leaveRequestStatus;
    }

    public void setLeaveRequestStatus(String leaveRequestStatus) {
        this.leaveRequestStatus = leaveRequestStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getTeacherComments() {
        return teacherComments;
    }

    public void setTeacherComments(Integer teacherComments) {
        this.teacherComments = teacherComments;
    }

    public String getLeaveTo() {
        return leaveTo;
    }

    public void setLeaveTo(String leaveTo) {
        this.leaveTo = leaveTo;
    }

    public String getLeaveFrom() {
        return leaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        this.leaveFrom = leaveFrom;
    }
}
