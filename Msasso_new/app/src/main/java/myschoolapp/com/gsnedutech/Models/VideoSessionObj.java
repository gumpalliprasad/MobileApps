package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoSessionObj implements Serializable {
    @SerializedName("videoName")
    @Expose
    private String videoName;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("chapterName")
    @Expose
    private String chapterName;
    @SerializedName("videoLink")
    @Expose
    private String videoLink;
    @SerializedName("videoLinkId")
    @Expose
    private String videoLinkId;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("chapterCCMapId")
    @Expose
    private String chapterCCMapId;

    public String getChapterCCMapId() {
        return chapterCCMapId;
    }

    public void setChapterCCMapId(String chapterCCMapId) {
        this.chapterCCMapId = chapterCCMapId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoLinkId() {
        return videoLinkId;
    }

    public void setVideoLinkId(String videoLinkId) {
        this.videoLinkId = videoLinkId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

}
