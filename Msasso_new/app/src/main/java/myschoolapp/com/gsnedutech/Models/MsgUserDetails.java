/*
 * *
 *  * Created by SriRamaMurthy A on 26/9/19 3:50 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 26/9/19 1:37 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MsgUserDetails {
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("roleId")
    @Expose
    private String roleId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("userId")
    @Expose
    private String userId;

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
