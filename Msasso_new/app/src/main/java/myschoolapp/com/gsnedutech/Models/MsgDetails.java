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

public class MsgDetails {

    @SerializedName("threadId")
    @Expose
    private Integer threadId;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
