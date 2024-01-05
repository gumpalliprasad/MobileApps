package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KhubModelContentType {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("displayOrder")
    @Expose
    private Integer displayOrder;
    @SerializedName("moduleDataOrder")
    @Expose
    private Integer moduleDataOrder;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("desc")
    @Expose
    private String desc;
    private final static long serialVersionUID = 416398259802775426L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getModuleDataOrder() {
        return moduleDataOrder;
    }

    public void setModuleDataOrder(Integer moduleDataOrder) {
        this.moduleDataOrder = moduleDataOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
