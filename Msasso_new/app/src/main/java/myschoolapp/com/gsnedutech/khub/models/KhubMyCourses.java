package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhubMyCourses implements Serializable
{

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
    @SerializedName("kCatName")
    @Expose
    private String kCatName;
    @SerializedName("kCatId")
    @Expose
    private String kCatId;
    @SerializedName("cProgress")
    @Expose
    private Integer cProgress;
    private final static long serialVersionUID = -1447397243210398524L;

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

    public String getKCatName() {
        return kCatName;
    }

    public void setKCatName(String kCatName) {
        this.kCatName = kCatName;
    }

    public String getKCatId() {
        return kCatId;
    }

    public void setKCatId(String kCatId) {
        this.kCatId = kCatId;
    }

    public Integer getCProgress() {
        return cProgress;
    }

    public void setCProgress(Integer cProgress) {
        this.cProgress = cProgress;
    }

}
