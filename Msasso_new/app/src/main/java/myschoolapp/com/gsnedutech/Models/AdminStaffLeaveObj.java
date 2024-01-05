package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminStaffLeaveObj {

    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("LeaveReqestStatus")
    @Expose
    private String leaveReqestStatus;
    @SerializedName("LeaveTo")
    @Expose
    private String leaveTo;
    @SerializedName("staffLeaveReqId")
    @Expose
    private String staffLeaveReqId;
    @SerializedName("staffName")
    @Expose
    private String staffName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("isApproved")
    @Expose
    private Integer isApproved;
    @SerializedName("staffId")
    @Expose
    private String staffId;
    @SerializedName("leaveFrom")
    @Expose
    private String leaveFrom;

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

    public String getStaffLeaveReqId() {
        return staffLeaveReqId;
    }

    public void setStaffLeaveReqId(String staffLeaveReqId) {
        this.staffLeaveReqId = staffLeaveReqId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
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

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getLeaveFrom() {
        return leaveFrom;
    }

    public void setLeaveFrom(String leaveFrom) {
        this.leaveFrom = leaveFrom;
    }
}
