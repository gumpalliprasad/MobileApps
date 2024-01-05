package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PollOption implements Serializable {
    @SerializedName("pollOption")
    @Expose
    private String pollOption;
    @SerializedName("pollOptionCount")
    @Expose
    private Integer pollOptionCount;

    public String getPollOption() {
        return pollOption;
    }

    public void setPollOption(String pollOption) {
        this.pollOption = pollOption;
    }

    public Integer getPollOptionCount() {
        return pollOptionCount;
    }

    public void setPollOptionCount(Integer pollOptionCount) {
        this.pollOptionCount = pollOptionCount;
    }
}
