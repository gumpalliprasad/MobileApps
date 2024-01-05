package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LiveVideo implements Serializable {
    @SerializedName("playbackTime")
    @Expose
    private Integer playbackTime;
    @SerializedName("liveStreamLink")
    @Expose
    private String liveStreamLink;
    @SerializedName("meetingId")
    @Expose
    private String meetingId;
    @SerializedName("studentLiveStreamStatus")
    @Expose
    private String studentLiveStreamStatus;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("liveStreamStatus")
    @Expose
    private String liveStreamStatus;
    @SerializedName("facultyId")
    @Expose
    private String facultyId;
    @SerializedName("startUrl")
    @Expose
    private String startUrl;
    @SerializedName("liveStreamName")
    @Expose
    private String liveStreamName;
    @SerializedName("liveStreamId")
    @Expose
    private String liveStreamId;
    @SerializedName("liveStreamStartTime")
    @Expose
    private String liveStreamStartTime;
    @SerializedName("meetingPwd")
    @Expose
    private String meetingPwd;
    @SerializedName("facultyName")
    @Expose
    private String facultyName;
    @SerializedName("joinUrl")
    @Expose
    private String joinUrl;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("studentLiveStreamId")
    @Expose
    private String studentLiveStreamId;

    String endTime = "";


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public Integer getPlaybackTime() {
        return playbackTime;
    }

    public void setPlaybackTime(Integer playbackTime) {
        this.playbackTime = playbackTime;
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

    public String getStudentLiveStreamStatus() {
        return studentLiveStreamStatus;
    }

    public void setStudentLiveStreamStatus(String studentLiveStreamStatus) {
        this.studentLiveStreamStatus = studentLiveStreamStatus;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLiveStreamStatus() {
        return liveStreamStatus;
    }

    public void setLiveStreamStatus(String liveStreamStatus) {
        this.liveStreamStatus = liveStreamStatus;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getLiveStreamName() {
        return liveStreamName;
    }

    public void setLiveStreamName(String liveStreamName) {
        this.liveStreamName = liveStreamName;
    }

    public String getLiveStreamId() {
        return liveStreamId;
    }

    public void setLiveStreamId(String liveStreamId) {
        this.liveStreamId = liveStreamId;
    }

    public String getLiveStreamStartTime() {
        return liveStreamStartTime;
    }

    public void setLiveStreamStartTime(String liveStreamStartTime) {
        this.liveStreamStartTime = liveStreamStartTime;
    }

    public String getMeetingPwd() {
        return meetingPwd;
    }

    public void setMeetingPwd(String meetingPwd) {
        this.meetingPwd = meetingPwd;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public String getStudentLiveStreamId() {
        return studentLiveStreamId;
    }

    public void setStudentLiveStreamId(String studentLiveStreamId) {
        this.studentLiveStreamId = studentLiveStreamId;
    }
}
