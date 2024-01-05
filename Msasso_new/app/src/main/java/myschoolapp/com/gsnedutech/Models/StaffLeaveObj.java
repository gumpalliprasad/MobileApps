/*
 * *
 *  * Created by SriRamaMurthy A on 11/9/19 3:50 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 11/9/19 2:41 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StaffLeaveObj implements Serializable {
    @SerializedName("LeaveRequestStatus")
    @Expose
    private String leaveRequestStatus;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("leaveTo")
    @Expose
    private String leaveTo;
    @SerializedName("staffId")
    @Expose
    private Integer staffId;
    @SerializedName("leaveFrom")
    @Expose
    private String leaveFrom;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLeaveTo() {
        return leaveTo;
    }

    public void setLeaveTo(String leaveTo) {
        this.leaveTo = leaveTo;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getLeaveFrom() {
        return leaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        this.leaveFrom = leaveFrom;
    }
}
