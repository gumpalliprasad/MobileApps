/*
 * *
 *  * Created by SriRamaMurthy A on 26/9/19 12:13 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 25/9/19 4:45 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessagesChatObj {
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("messageFrom")
    @Expose
    private String messageFrom;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

}
