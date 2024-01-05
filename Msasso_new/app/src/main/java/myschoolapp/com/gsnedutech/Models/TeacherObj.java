/*
 * *
 *  * Created by SriRamaMurthy A on 11/9/19 3:50 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 11/9/19 11:53 AM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherObj implements Serializable {
    @SerializedName("roleId")
    @Expose
    private String roleId;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("deptId")
    @Expose
    private String deptId;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;
    @SerializedName("instId")
    @Expose
    private String instId;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("branch_id")
    @Expose
    private String branchId;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("userJoiningDate")
    @Expose
    private String userJoiningDate;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("instName")
    @Expose
    private String instName;
    @SerializedName("Subjects")
    @Expose
    private String subjects;

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

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getUserJoiningDate() {
        return userJoiningDate;
    }

    public void setUserJoiningDate(String userJoiningDate) {
        this.userJoiningDate = userJoiningDate;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

}
