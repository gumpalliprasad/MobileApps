package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhubCategoryCourse implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mediumId")
    @Expose
    private String mediumId;
    @SerializedName("mediumCode")
    @Expose
    private String mediumCode;
    @SerializedName("kCourseName")
    @Expose
    private String kCourseName;
    @SerializedName("kCourseImage")
    @Expose
    private String kCourseImage;
    @SerializedName("kCourseDesc")
    @Expose
    private String kCourseDesc;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("creationDts")
    @Expose
    private String creationDts;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("modifiedDts")
    @Expose
    private String modifiedDts;
    @SerializedName("modifiedBy")
    @Expose
    private String modifiedBy;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("isEnrolled")
    @Expose
    private Boolean isEnrolled;
    @SerializedName("viewsCount")
    @Expose
    private Integer viewsCount = 0;
    @SerializedName("courseGroup")
    @Expose
    private String courseGroup;
    @SerializedName("kCourseExp")
    @Expose
    private String kCourseExp;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("courseId")
    @Expose
    private String courseId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediumId() {
        return mediumId;
    }

    public void setMediumId(String mediumId) {
        this.mediumId = mediumId;
    }

    public String getMediumCode() {
        return mediumCode;
    }

    public void setMediumCode(String mediumCode) {
        this.mediumCode = mediumCode;
    }

    public String getKCourseName() {
        return kCourseName;
    }

    public void setKCourseName(String kCourseName) {
        this.kCourseName = kCourseName;
    }

    public String getKCourseImage() {
        return kCourseImage;
    }

    public void setKCourseImage(String kCourseImage) {
        this.kCourseImage = kCourseImage;
    }

    public String getKCourseDesc() {
        return kCourseDesc;
    }

    public void setKCourseDesc(String kCourseDesc) {
        this.kCourseDesc = kCourseDesc;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCreationDts() {
        return creationDts;
    }

    public void setCreationDts(String creationDts) {
        this.creationDts = creationDts;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedDts() {
        return modifiedDts;
    }

    public void setModifiedDts(String modifiedDts) {
        this.modifiedDts = modifiedDts;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsEnrolled() {
        return isEnrolled;
    }

    public void setIsEnrolled(Boolean enrolled) {
        isEnrolled = enrolled;
    }

    public Integer getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(Integer viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(String courseGroup) {
        this.courseGroup = courseGroup;
    }

    public String getkCourseExp() {
        return kCourseExp;
    }

    public void setkCourseExp(String kCourseExp) {
        this.kCourseExp = kCourseExp;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}


