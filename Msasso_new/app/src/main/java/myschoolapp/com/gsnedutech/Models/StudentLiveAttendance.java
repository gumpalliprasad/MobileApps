package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentLiveAttendance {
    @SerializedName("liveStreamName")
    @Expose
    private String liveStreamName;
    @SerializedName("liveStreamId")
    @Expose
    private Integer liveStreamId;
    @SerializedName("liveStreamStartTime")
    @Expose
    private String liveStreamStartTime;
    @SerializedName("attendance")
    @Expose
    private Integer attendance;

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

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }
}
