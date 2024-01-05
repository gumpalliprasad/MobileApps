package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KhubTextBookObj {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("pageTitle")
    @Expose
    private String pageTitle;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("pageNo")
    @Expose
    private String pageNo;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("creationDts")
    @Expose
    private String creationDts;
    @SerializedName("modifiedBy")
    @Expose
    private String modifiedBy;
    @SerializedName("modifiedDts")
    @Expose
    private String modifiedDts;
    private final static long serialVersionUID = 9119612214587835129L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedDts() {
        return modifiedDts;
    }

    public void setModifiedDts(String modifiedDts) {
        this.modifiedDts = modifiedDts;
    }
}
