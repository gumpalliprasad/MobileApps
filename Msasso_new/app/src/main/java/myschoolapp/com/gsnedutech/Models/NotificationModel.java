package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    @SerializedName("notificationTitle")
    @Expose
    private String notificationTitle;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("notificationFiles")
    @Expose
    private String notificationFiles;
    @SerializedName("notificationId")
    @Expose
    private Integer notificationId;
    @SerializedName("notificationDesc")
    @Expose
    private String notificationDesc;
    @SerializedName("notificationDate")
    @Expose
    private String notificationDate;

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getNotificationFiles() {
        return notificationFiles;
    }

    public void setNotificationFiles(String notificationFiles) {
        this.notificationFiles = notificationFiles;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationDesc() {
        return notificationDesc;
    }

    public void setNotificationDesc(String notificationDesc) {
        this.notificationDesc = notificationDesc;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }
}
