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
import java.util.List;

public class SubChapter implements Serializable {
    @SerializedName("chapterOwner")
    @Expose
    private String chapterOwner;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("ChapterTopic")
    @Expose
    private List<SubChapterTopic> chapterTopic = null;
    @SerializedName("chapterName")
    @Expose
    private String chapterName;
    @SerializedName("chapterCCMapId")
    @Expose
    private String chapterCCMapId;

    public String getChapterCCMapId() {
        return chapterCCMapId;
    }

    public void setChapterCCMapId(String chapterCCMapId) {
        this.chapterCCMapId = chapterCCMapId;
    }

    public Boolean getIsaccessible() {
        return isaccessible;
    }

    public void setIsaccessible(Boolean isaccessible) {
        this.isaccessible = isaccessible;
    }

    private Boolean isaccessible = false;

    public String getChapterOwner() {
        return chapterOwner;
    }

    public void setChapterOwner(String chapterOwner) {
        this.chapterOwner = chapterOwner;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public List<SubChapterTopic> getChapterTopic() {
        return chapterTopic;
    }

    public void setChapterTopic(List<SubChapterTopic> chapterTopic) {
        this.chapterTopic = chapterTopic;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}
