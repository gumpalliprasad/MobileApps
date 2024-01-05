package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import myschoolapp.com.gsnedutech.Models.FlashCardFilesArray;

public class ArFlashCardsObj implements Serializable {
    @SerializedName("schemaName")
    @Expose
    private String schemaName;
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("sectionId")
    @Expose
    private Integer sectionId;
    @SerializedName("arenaName")
    @Expose
    private String arenaName;
    @SerializedName("arenaDesc")
    @Expose
    private String arenaDesc;
    @SerializedName("arenaType")
    @Expose
    private String arenaType;
    @SerializedName("arenaCategory")
    @Expose
    private String arenaCategory;
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("filesArray")
    @Expose
    private List<FlashCardFilesArray> filesArray = null;
    @SerializedName("questionCount")
    @Expose
    private Integer questionCount;

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @SerializedName("color")
    @Expose
    private String color;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaDesc() {
        return arenaDesc;
    }

    public void setArenaDesc(String arenaDesc) {
        this.arenaDesc = arenaDesc;
    }

    public String getArenaType() {
        return arenaType;
    }

    public void setArenaType(String arenaType) {
        this.arenaType = arenaType;
    }

    public String getArenaCategory() {
        return arenaCategory;
    }

    public void setArenaCategory(String arenaCategory) {
        this.arenaCategory = arenaCategory;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<FlashCardFilesArray> getFilesArray() {
        return filesArray;
    }

    public void setFilesArray(List<FlashCardFilesArray> filesArray) {
        this.filesArray = filesArray;
    }
}
