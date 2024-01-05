package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExamSectionInfo implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("sectionTitle")
    @Expose
    private String sectionTitle;
    @SerializedName("sectionDesc")
    @Expose
    private String sectionDesc;
    @SerializedName("sectionType")
    @Expose
    private String sectionType;
    @SerializedName("sectionMarks")
    @Expose
    private Integer sectionMarks;
    @SerializedName("qCount")
    @Expose
    private Integer qCount;
    @SerializedName("mCount")
    @Expose
    private Integer mCount;
    @SerializedName("qMarks")
    @Expose
    private Integer qMarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getSectionDesc() {
        return sectionDesc;
    }

    public void setSectionDesc(String sectionDesc) {
        this.sectionDesc = sectionDesc;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Integer getSectionMarks() {
        return sectionMarks;
    }

    public void setSectionMarks(Integer sectionMarks) {
        this.sectionMarks = sectionMarks;
    }

    public Integer getqCount() {
        return qCount;
    }

    public void setqCount(Integer qCount) {
        this.qCount = qCount;
    }

    public Integer getmCount() {
        return mCount;
    }

    public void setmCount(Integer mCount) {
        this.mCount = mCount;
    }

    public Integer getqMarks() {
        return qMarks;
    }

    public void setqMarks(Integer qMarks) {
        this.qMarks = qMarks;
    }

}