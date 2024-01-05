package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContentDetails implements Serializable {
    @SerializedName("textbookCCMapId")
    int textbookCCMapId;
    @SerializedName("textbookContent")
    String textbookContent;
    @SerializedName("topicCCMapId")
    int topicCCMapId;
    @SerializedName("textbookContentId")
    int textbookContentId;
    @SerializedName("isActive")
    int isActive;
    @SerializedName("contentType")
    String contentType;
    @SerializedName("importantContent")
    String importantContent;
    @SerializedName("importantCCMapId")
    int importantCCMapId;

    public int getTextbookCCMapId() {
        return textbookCCMapId;
    }

    public void setTextbookCCMapId(int textbookCCMapId) {
        this.textbookCCMapId = textbookCCMapId;
    }

    public String getTextbookContent() {
        return textbookContent;
    }

    public void setTextbookContent(String textbookContent) {
        this.textbookContent = textbookContent;
    }

    public int getTopicCCMapId() {
        return topicCCMapId;
    }

    public void setTopicCCMapId(int topicCCMapId) {
        this.topicCCMapId = topicCCMapId;
    }

    public int getTextbookContentId() {
        return textbookContentId;
    }

    public void setTextbookContentId(int textbookContentId) {
        this.textbookContentId = textbookContentId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getImportantContent() {
        return importantContent;
    }

    public void setImportantContent(String importantContent) {
        this.importantContent = importantContent;
    }

    public int getImportantCCMapId() {
        return importantCCMapId;
    }

    public void setImportantCCMapId(int importantCCMapId) {
        this.importantCCMapId = importantCCMapId;
    }

    public int getImportantContentId() {
        return importantContentId;
    }

    public void setImportantContentId(int importantContentId) {
        this.importantContentId = importantContentId;
    }

    @SerializedName("importantContentId")
    int importantContentId;
}
