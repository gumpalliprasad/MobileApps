package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminStaffAllLeaveObj {

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
    @SerializedName("staffLeaveReqId")
    @Expose
    private Integer staffLeaveReqId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("leaveTo")
    @Expose
    private String leaveTo;
    @SerializedName("userName")
    @Expose
    private String userName;
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

    public Integer getStaffLeaveReqId() {
        return staffLeaveReqId;
    }

    public void setStaffLeaveReqId(Integer staffLeaveReqId) {
        this.staffLeaveReqId = staffLeaveReqId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
