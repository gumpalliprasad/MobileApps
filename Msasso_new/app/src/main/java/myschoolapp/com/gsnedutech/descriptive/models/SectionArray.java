package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SectionArray implements Serializable {

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
    private int sectionMarks;
    @SerializedName("qCount")
    @Expose
    private int qCount;
    @SerializedName("mCount")
    @Expose
    private int mCount;
    @SerializedName("qMarks")
    @Expose
    private int qMarks;
    @SerializedName("isObjective")
    @Expose
    private int isObjective;
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("sectionId")
    @Expose
    private String sectionId;

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

    public int getSectionMarks() {
        return sectionMarks;
    }

    public void setSectionMarks(int sectionMarks) {
        this.sectionMarks = sectionMarks;
    }

    public int getqCount() {
        return qCount;
    }

    public void setqCount(int qCount) {
        this.qCount = qCount;
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getqMarks() {
        return qMarks;
    }

    public void setqMarks(int qMarks) {
        this.qMarks = qMarks;
    }

    public int getIsObjective() {
        return isObjective;
    }

    public void setIsObjective(int isObjective) {
        this.isObjective = isObjective;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    @SerializedName("sectionName")
    @Expose
    private String sectionName;
}