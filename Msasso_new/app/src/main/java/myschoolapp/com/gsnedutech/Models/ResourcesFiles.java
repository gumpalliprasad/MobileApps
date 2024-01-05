package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResourcesFiles implements Serializable {

    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("textbookFileId")
    @Expose
    private Integer textbookFileId;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("fileType")
    @Expose
    private String fileType;
    @SerializedName("contentOwner")
    @Expose
    private String contentOwner;
    @SerializedName("chapterCCMapId")
    @Expose
    private String chapterCCMapId;

    public String getChapterCCMapId() {
        return chapterCCMapId;
    }

    public void setChapterCCMapId(String chapterCCMapId) {
        this.chapterCCMapId = chapterCCMapId;
    }

    @SerializedName("topicCCMapId")
    @Expose
    private String topicCCMapId;
    public String getTopicCCMapId() {
        return topicCCMapId;
    }

    public void setTopicCCMapId(String topicCCMapId) {
        this.topicCCMapId = topicCCMapId;
    }

    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public Integer getTextbookFileId() {
        return textbookFileId;
    }
    public void setTextbookFileId(Integer textbookFileId) {
        this.textbookFileId = textbookFileId;
    }
    public String getChapterId() {
        return chapterId;
    }
    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getContentOwner() {
        return contentOwner;
    }
    public void setContentOwner(String contentOwner) {
        this.contentOwner = contentOwner;
    }

}
