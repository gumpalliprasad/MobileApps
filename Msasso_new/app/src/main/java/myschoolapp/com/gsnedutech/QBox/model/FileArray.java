package myschoolapp.com.gsnedutech.QBox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FileArray implements Serializable
{

    @SerializedName("qboxFileName")
    @Expose
    private String qboxFileName;
    @SerializedName("qboxFileId")
    @Expose
    private Integer qboxFileId;
    @SerializedName("qboxFilePath")
    @Expose
    private String qboxFilePath;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;

    public String getQboxFileName() {
        return qboxFileName;
    }

    public void setQboxFileName(String qboxFileName) {
        this.qboxFileName = qboxFileName;
    }

    public Integer getQboxFileId() {
        return qboxFileId;
    }

    public void setQboxFileId(Integer qboxFileId) {
        this.qboxFileId = qboxFileId;
    }

    public String getQboxFilePath() {
        return qboxFilePath;
    }

    public void setQboxFilePath(String qboxFilePath) {
        this.qboxFilePath = qboxFilePath;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}
