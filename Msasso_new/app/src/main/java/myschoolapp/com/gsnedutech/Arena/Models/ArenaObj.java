package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArenaObj {

    @SerializedName("arenaCategory")
    @Expose
    private String arenaCategory;
    @SerializedName("likesCount")
    @Expose
    private Integer likesCount;
    @SerializedName("arenaId")
    @Expose
    private Integer arenaId;
    @SerializedName("arenaStatus")
    @Expose
    private Integer arenaStatus;
    @SerializedName("arenaName")
    @Expose
    private String arenaName;
    @SerializedName("arenaType")
    @Expose
    private String arenaType;
    @SerializedName("arenaDesc")
    @Expose
    private String arenaDesc;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getArenaCategory() {
        return arenaCategory;
    }

    public void setArenaCategory(String arenaCategory) {
        this.arenaCategory = arenaCategory;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getArenaId() {
        return arenaId;
    }

    public void setArenaId(Integer arenaId) {
        this.arenaId = arenaId;
    }

    public Integer getArenaStatus() {
        return arenaStatus;
    }

    public void setArenaStatus(Integer arenaStatus) {
        this.arenaStatus = arenaStatus;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaType() {
        return arenaType;
    }

    public void setArenaType(String arenaType) {
        this.arenaType = arenaType;
    }

    public String getArenaDesc() {
        return arenaDesc;
    }

    public void setArenaDesc(String arenaDesc) {
        this.arenaDesc = arenaDesc;
    }
}
