package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class KhubModuleNew implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("modName")
    @Expose
    private String modName;
    @SerializedName("modLevel")
    @Expose
    private Integer modLevel;
    @SerializedName("hasChild")
    @Expose
    private Boolean hasChild;
    @SerializedName("parentId")
    @Expose
    private String parentId;
    @SerializedName("modOrder")
    @Expose
    private Integer modOrder;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("modules")
    @Expose
    private List<KhubModuleNew> modules = null;
    @SerializedName("mProgress")
    @Expose
    private Integer mProgress = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModName() {
        return modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public Integer getModLevel() {
        return modLevel;
    }

    public void setModLevel(Integer modLevel) {
        this.modLevel = modLevel;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getModOrder() {
        return modOrder;
    }

    public void setModOrder(Integer modOrder) {
        this.modOrder = modOrder;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<KhubModuleNew> getModules() {
        return modules;
    }

    public void setModules(List<KhubModuleNew> modules) {
        this.modules = modules;
    }

    public void setmProgress(Integer mProgress) {
        this.mProgress = mProgress;
    }

    public Integer getmProgress() {
        return mProgress;
    }
}
