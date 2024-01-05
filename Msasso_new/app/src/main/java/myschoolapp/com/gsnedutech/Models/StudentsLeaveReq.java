/*
 * *
 *  * Created by SriRamaMurthy A on 24/10/19 6:07 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 17/10/19 4:43 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudentsLeaveReq implements Serializable {

    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("LeaveReqestStatus")
    @Expose
    private String leaveReqestStatus;
    @SerializedName("LeaveTo")
    @Expose
    private String leaveTo;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("studentLeaveReqId")
    @Expose
    private String studentLeaveReqId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("isApproved")
    @Expose
    private Integer isApproved;
    @SerializedName("leaveFrom")
    @Expose
    private String leaveFrom;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLeaveReqestStatus() {
        return leaveReqestStatus;
    }

    public void setLeaveReqestStatus(String leaveReqestStatus) {
        this.leaveReqestStatus = leaveReqestStatus;
    }

    public String getLeaveTo() {
        return leaveTo;
    }

    public void setLeaveTo(String leaveTo) {
        this.leaveTo = leaveTo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentLeaveReqId() {
        return studentLeaveReqId;
    }

    public void setStudentLeaveReqId(String studentLeaveReqId) {
        this.studentLeaveReqId = studentLeaveReqId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public String getLeaveFrom() {
        return leaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        this.leaveFrom = leaveFrom;
    }

}
