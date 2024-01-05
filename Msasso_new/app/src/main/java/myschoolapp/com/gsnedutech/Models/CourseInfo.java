package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CourseInfo implements Serializable {

    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("classes")
    @Expose
    private List<ClassInfo> classes = null;
    @SerializedName("courseId")
    @Expose
    private String courseId;

    boolean selected = false;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    boolean show = false;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<ClassInfo> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassInfo> classes) {
        this.classes = classes;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }


}
