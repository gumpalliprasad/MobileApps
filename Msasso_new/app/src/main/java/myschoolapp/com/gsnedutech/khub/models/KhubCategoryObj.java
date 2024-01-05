package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhubCategoryObj implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("kCatName")
    @Expose
    private String kCatName;
    @SerializedName("kCatDesc")
    @Expose
    private String kCatDesc;
    @SerializedName("kCatImage")
    @Expose
    private String kCatImage;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKCatName() {
        return kCatName;
    }

    public void setKCatName(String kCatName) {
        this.kCatName = kCatName;
    }

    public String getKCatDesc() {
        return kCatDesc;
    }

    public void setKCatDesc(String kCatDesc) {
        this.kCatDesc = kCatDesc;
    }

    public String getKCatImage() {
        return kCatImage;
    }

    public void setKCatImage(String kCatImage) {
        this.kCatImage = kCatImage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }
}
