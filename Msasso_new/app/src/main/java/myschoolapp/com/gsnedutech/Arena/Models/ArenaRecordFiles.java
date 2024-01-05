package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArenaRecordFiles {
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("arenaId")
    @Expose
    private String arenaId;
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    @SerializedName("fileType")
    @Expose
    private String fileType;
    @SerializedName("arenaFileDetailId")
    @Expose
    private String arenaFileDetailId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getArenaId() {
        return arenaId;
    }

    public void setArenaId(String arenaId) {
        this.arenaId = arenaId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getArenaFileDetailId() {
        return arenaFileDetailId;
    }

    public void setArenaFileDetailId(String arenaFileDetailId) {
        this.arenaFileDetailId = arenaFileDetailId;
    }
}
