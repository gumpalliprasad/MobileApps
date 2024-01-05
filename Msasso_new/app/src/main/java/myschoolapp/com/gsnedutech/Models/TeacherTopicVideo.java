/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherTopicVideo implements Serializable {
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("videoName")
    @Expose
    private String videoName;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("videoLink")
    @Expose
    private String videoLink;
    @SerializedName("videoLinkId")
    @Expose
    private String videoLinkId;
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
}
