package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KHubBanners implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("bannerTitle")
    @Expose
    private String bannerTitle;
    @SerializedName("bannerImage")
    @Expose
    private String bannerImage;
    @SerializedName("isAction")
    @Expose
    private Boolean isAction;
    @SerializedName("actionType")
    @Expose
    private String actionType;
    @SerializedName("bannerOrder")
    @Expose
    private Integer bannerOrder;
    @SerializedName("bannerDesc")
    @Expose
    private String bannerDesc;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("creationDTS")
    @Expose
    private String creationDTS;
    @SerializedName("__v")
    @Expose
    private Integer v;
    private final static long serialVersionUID = 2671933672210980231L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Boolean getIsAction() {
        return isAction;
    }

    public void setIsAction(Boolean isAction) {
        this.isAction = isAction;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getBannerOrder() {
        return bannerOrder;
    }

    public void setBannerOrder(Integer bannerOrder) {
        this.bannerOrder = bannerOrder;
    }

    public String getBannerDesc() {
        return bannerDesc;
    }

    public void setBannerDesc(String bannerDesc) {
        this.bannerDesc = bannerDesc;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreationDTS() {
        return creationDTS;
    }

    public void setCreationDTS(String creationDTS) {
        this.creationDTS = creationDTS;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}
