package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ClassInfo implements Serializable {

    @SerializedName("classId")
    @Expose
    private String classId;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("sections")
    @Expose
    private List<SectionInfo> sections = null;

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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<SectionInfo> getSections() {
        return sections;
    }

    public void setSections(List<SectionInfo> sections) {
        this.sections = sections;
    }


}
