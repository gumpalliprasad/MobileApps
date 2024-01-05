package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CollegeInfo implements Serializable {

    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("courses")
    @Expose
    private List<CourseInfo> courses = null;
    @SerializedName("branchName")
    @Expose
    private String branchName;

    boolean selected = false;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    boolean show = true;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public List<CourseInfo> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseInfo> courses) {
        this.courses = courses;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }


}
