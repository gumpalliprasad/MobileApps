package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudentHWFile implements Serializable {
    @SerializedName("studentHWFileId")
    @Expose
    private String studentHWFileId;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("teacherFeedback")
    @Expose
    private String teacherFeedback;
    @SerializedName("studentHWFilePath")
    @Expose
    private String studentHWFilePath;
    @SerializedName("hwId")
    @Expose
    private Integer hwId;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("evaluated")
    @Expose
    private Integer evaluated;
    @SerializedName("teacherEvaluatePath")
    @Expose
    private String teacherEvaluatePath="NA";


    public String getTeacherEvaluatePath() {
        return teacherEvaluatePath;
    }

    public void setTeacherEvaluatePath(String teacherEvaluatePath) {
        this.teacherEvaluatePath = teacherEvaluatePath;
    }


    public Integer getEvaluated() {
        return evaluated;
    }

    public void setEvaluated(Integer evaluated) {
        this.evaluated = evaluated;
    }

    public String getStudentHWFileId() {
        return studentHWFileId;
    }

    public void setStudentHWFileId(String studentHWFileId) {
        this.studentHWFileId = studentHWFileId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getStudentHWFilePath() {
        return studentHWFilePath;
    }

    public void setStudentHWFilePath(String studentHWFilePath) {
        this.studentHWFilePath = studentHWFilePath;
    }

    public Integer getHwId() {
        return hwId;
    }

    public void setHwId(Integer hwId) {
        this.hwId = hwId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
