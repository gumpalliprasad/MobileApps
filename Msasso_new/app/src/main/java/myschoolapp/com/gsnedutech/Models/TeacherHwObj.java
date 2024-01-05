package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TeacherHwObj implements Serializable {

    @SerializedName("homeWorkDesc")
    @Expose
    private String homeWorkDesc;
    @SerializedName("homeworkTypeId")
    @Expose
    private String homeworkTypeId;
    @SerializedName("HomeWorkDetails")
    @Expose
    private List<HomeWorkDetailTeacher> homeWorkDetails = null;

    public String getHomeWorkDesc() {
        return homeWorkDesc;
    }

    public void setHomeWorkDesc(String homeWorkDesc) {
        this.homeWorkDesc = homeWorkDesc;
    }

    public String getHomeworkTypeId() {
        return homeworkTypeId;
    }

    public void setHomeworkTypeId(String homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }

    public List<HomeWorkDetailTeacher> getHomeWorkDetails() {
        return homeWorkDetails;
    }

    public void setHomeWorkDetails(List<HomeWorkDetailTeacher> homeWorkDetails) {
        this.homeWorkDetails = homeWorkDetails;
    }
}
