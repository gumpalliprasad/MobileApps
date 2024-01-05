package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherLiveClassObj implements Serializable {

    @SerializedName("branchId")
    @Expose
    private Integer branchId;
    @SerializedName("liveStreamDesc")
    @Expose
    private String liveStreamDesc;
    @SerializedName("liveStreamLink")
    @Expose
    private String liveStreamLink;
    @SerializedName("meetingId")
    @Expose
    private String meetingId;
    @SerializedName("liveStreamStatus")
    @Expose
    private String liveStreamStatus;
    @SerializedName("startUrl")
    @Expose
    private String startUrl;
    @SerializedName("meetingPassword")
    @Expose
    private String meetingPassword;
    @SerializedName("liveStreamName")
    @Expose
    private String liveStreamName;
    @SerializedName("liveStreamId")
    @Expose
    private Integer liveStreamId;
    @SerializedName("liveStreamStartTime")
    @Expose
    private String liveStreamStartTime;
    @SerializedName("liveStreamDuration")
    @Expose
    private Integer liveStreamDuration;
    @SerializedName("joinUrl")
    @Expose
    private String joinUrl;
    @SerializedName("zoomUserId")
    @Expose
    private String zoomUserId;

    String endTime = "NA";

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getLiveStreamDesc() {
        return liveStreamDesc;
    }

    public void setLiveStreamDesc(String liveStreamDesc) {
        this.liveStreamDesc = liveStreamDesc;
    }

    public String getLiveStreamLink() {
        return liveStreamLink;
    }

    public void setLiveStreamLink(String liveStreamLink) {
        this.liveStreamLink = liveStreamLink;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getLiveStreamStatus() {
        return liveStreamStatus;
    }

    public void setLiveStreamStatus(String liveStreamStatus) {
        this.liveStreamStatus = liveStreamStatus;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getLiveStreamName() {
        return liveStreamName;
    }

    public void setLiveStreamName(String liveStreamName) {
        this.liveStreamName = liveStreamName;
    }

    public Integer getLiveStreamId() {
        return liveStreamId;
    }

    public void setLiveStreamId(Integer liveStreamId) {
        this.liveStreamId = liveStreamId;
    }

    public String getLiveStreamStartTime() {
        return liveStreamStartTime;
    }

    public void setLiveStreamStartTime(String liveStreamStartTime) {
        this.liveStreamStartTime = liveStreamStartTime;
    }

    public Integer getLiveStreamDuration() {
        return liveStreamDuration;
    }

    public void setLiveStreamDuration(Integer liveStreamDuration) {
        this.liveStreamDuration = liveStreamDuration;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public String getZoomUserId() {
        return zoomUserId;
    }

    public void setZoomUserId(String zoomUserId) {
        this.zoomUserId = zoomUserId;
    }
}
