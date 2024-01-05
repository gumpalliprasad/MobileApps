package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherDescExamsObj implements Serializable {

    @SerializedName("descExamTypeId")
    @Expose
    private Integer descExamTypeId;
    @SerializedName("examEndTime")
    @Expose
    private String examEndTime;
    @SerializedName("examStatus")
    @Expose
    private String examStatus;
    @SerializedName("examPath")
    @Expose
    private String examPath;
    @SerializedName("descExamId")
    @Expose
    private Integer descExamId;
    @SerializedName("totalMarks")
    @Expose
    private Integer totalMarks;
    @SerializedName("sectionsCount")
    @Expose
    private Integer sectionsCount;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("subjectId")
    @Expose
    private Integer subjectId;
    @SerializedName("descriptiveType")
    @Expose
    private String descriptiveType;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("examDescription")
    @Expose
    private String examDescription;
    @SerializedName("classId")
    @Expose
    private Integer classId;
    @SerializedName("examStartTime")
    @Expose
    private String examStartTime;
    @SerializedName("exam_name")
    @Expose
    private String examName;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    public Integer getDescExamTypeId() {
        return descExamTypeId;
    }

    public void setDescExamTypeId(Integer descExamTypeId) {
        this.descExamTypeId = descExamTypeId;
    }

    public String getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public String getExamPath() {
        return examPath;
    }

    public void setExamPath(String examPath) {
        this.examPath = examPath;
    }

    public Integer getDescExamId() {
        return descExamId;
    }

    public void setDescExamId(Integer descExamId) {
        this.descExamId = descExamId;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getSectionsCount() {
        return sectionsCount;
    }

    public void setSectionsCount(Integer sectionsCount) {
        this.sectionsCount = sectionsCount;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getDescriptiveType() {
        return descriptiveType;
    }

    public void setDescriptiveType(String descriptiveType) {
        this.descriptiveType = descriptiveType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getExamDescription() {
        return examDescription;
    }

    public void setExamDescription(String examDescription) {
        this.examDescription = examDescription;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

}
