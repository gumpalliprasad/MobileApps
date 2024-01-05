package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DescExam implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("examId")
    @Expose
    private String examId;
    @SerializedName("courseId")
    @Expose
    private String courseId;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("classId")
    @Expose
    private String classId;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("sectionArray")
    @Expose
    private List<SectionArray> sectionArray = null;
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("eName")
    @Expose
    private String eName;
    @SerializedName("eType")
    @Expose
    private String eType;
    @SerializedName("eStatus")
    @Expose
    private String eStatus;
    @SerializedName("sectionCount")
    @Expose
    private Integer sectionCount;
    @SerializedName("eDuration")
    @Expose
    private Integer eDuration;
    @SerializedName("sTime")
    @Expose
    private String sTime;
    @SerializedName("eTime")
    @Expose
    private String eTime;
    @SerializedName("rDate")
    @Expose
    private String rDate;
    @SerializedName("ePath")
    @Expose
    private String ePath;
    @SerializedName("examSectionInfo")
    @Expose
    private List<ExamSectionInfo> examSectionInfo = null;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("creationDts")
    @Expose
    private String creationDts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public List<SectionArray> getSectionArray() {
        return sectionArray;
    }

    public void setSectionArray(List<SectionArray> sectionArray) {
        this.sectionArray = sectionArray;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String geteType() {
        return eType;
    }

    public void seteType(String eType) {
        this.eType = eType;
    }

    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }

    public Integer getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    public Integer geteDuration() {
        return eDuration;
    }

    public void seteDuration(Integer eDuration) {
        this.eDuration = eDuration;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

    public String getePath() {
        return ePath;
    }

    public void setePath(String ePath) {
        this.ePath = ePath;
    }

    public List<ExamSectionInfo> getExamSectionInfo() {
        return examSectionInfo;
    }

    public void setExamSectionInfo(List<ExamSectionInfo> examSectionInfo) {
        this.examSectionInfo = examSectionInfo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreationDts() {
        return creationDts;
    }

    public void setCreationDts(String creationDts) {
        this.creationDts = creationDts;
    }

}
