package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveVideoInfo {
    @SerializedName("LiveVideos")
    @Expose
    private List<LiveVideo> liveVideos = null;
    @SerializedName("monthName")
    @Expose
    private String monthName;
    @SerializedName("monthId")
    @Expose
    private String monthId;

    public List<LiveVideo> getLiveVideos() {
        return liveVideos;
    }

    public void setLiveVideos(List<LiveVideo> liveVideos) {
        this.liveVideos = liveVideos;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthId() {
        return monthId;
    }

    public void setMonthId(String monthId) {
        this.monthId = monthId;
    }
}
