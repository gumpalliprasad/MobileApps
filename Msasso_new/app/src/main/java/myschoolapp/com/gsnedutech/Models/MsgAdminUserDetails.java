/*
 * *
 *  * Created by SriRamaMurthy A on 26/9/19 1:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 26/9/19 1:09 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MsgAdminUserDetails {
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("roleId")
    @Expose
    private String roleId;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("roleName")
    @Expose
    private String roleName;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userId")
    @Expose
    private String userId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
