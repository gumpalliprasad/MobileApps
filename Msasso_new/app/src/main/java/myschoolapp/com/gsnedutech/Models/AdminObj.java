/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 29/8/19 11:22 AM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminObj implements Serializable {
    @SerializedName("instId")
    @Expose
    private String instId;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("branch_id")
    @Expose
    private String branchId;
    @SerializedName("roleId")
    @Expose
    private String roleId;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("instName")
    @Expose
    private String instName;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("StatusCode")
    @Expose
    private String statusCode;

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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
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
}
