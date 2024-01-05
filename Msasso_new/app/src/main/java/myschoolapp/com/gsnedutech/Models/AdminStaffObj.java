/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 29/10/19 12:02 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminStaffObj implements Serializable {
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("roleId")
    @Expose
    private String roleId;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("loginName")
    @Expose
    private String loginName;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("deptId")
    @Expose
    private String deptId;
    @SerializedName("branchName")
    @Expose
    private String branchName;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("Subjects")
    @Expose
    private String subjects;
    @SerializedName("contactNo")
    @Expose
    private String contactNo;
    @SerializedName("staffLeaveRequId")
    @Expose
    private String staffLeaveRequId;
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



    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getStaffLeaveRequId() {
        return staffLeaveRequId;
    }

    public void setStaffLeaveRequId(String staffLeaveRequId) {
        this.staffLeaveRequId = staffLeaveRequId;
    }

}
