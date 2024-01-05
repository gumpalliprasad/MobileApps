package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeWorkDetailTeacher implements Serializable {
    @SerializedName("HWdate")
    @Expose
    private String hWdate;
    @SerializedName("HomeWorkDetail")
    @Expose
    private List<HomeWorkDetailsTeacher> homeWorkDetail = null;

    public String getHWdate() {
        return hWdate;
    }

    public void setHWdate(String hWdate) {
        this.hWdate = hWdate;
    }

    public List<HomeWorkDetailsTeacher> getHomeWorkDetail() {
        return homeWorkDetail;
    }

    public void setHomeWorkDetail(List<HomeWorkDetailsTeacher> homeWorkDetail) {
        this.homeWorkDetail = homeWorkDetail;
    }
}
