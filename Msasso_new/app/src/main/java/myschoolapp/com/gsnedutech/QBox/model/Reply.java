package myschoolapp.com.gsnedutech.QBox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Reply implements Serializable
{

    @SerializedName("rqboxFilePath")
    @Expose
    private String rqboxFilePath;
    @SerializedName("tqboxFileName")
    @Expose
    private String tqboxFileName;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("teacherqboxFileId")
    @Expose
    private String teacherqboxFileId;
    @SerializedName("teacherqboxId")
    @Expose
    private String teacherqboxId;

    public String getRqboxFilePath() {
        return rqboxFilePath;
    }

    public void setRqboxFilePath(String rqboxFilePath) {
        this.rqboxFilePath = rqboxFilePath;
    }

    public String getTqboxFileName() {
        return tqboxFileName;
    }

    public void setTqboxFileName(String tqboxFileName) {
        this.tqboxFileName = tqboxFileName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getTeacherqboxFileId() {
        return teacherqboxFileId;
    }

    public void setTeacherqboxFileId(String teacherqboxFileId) {
        this.teacherqboxFileId = teacherqboxFileId;
    }

    public String getTeacherqboxId() {
        return teacherqboxId;
    }

    public void setTeacherqboxId(String teacherqboxId) {
        this.teacherqboxId = teacherqboxId;
    }

}
