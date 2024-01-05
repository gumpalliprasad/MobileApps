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

public class Chaptertopic implements Serializable {


    @SerializedName("chapterOwner")
    @Expose
    private String chapterOwner;
    @SerializedName("Topics")
    @Expose
    private List<SubChapterTopic> topics = null;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
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

    boolean isAccessible=true;

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }


    public String getChapterOwner() {
        return chapterOwner;
    }

    public void setChapterOwner(String chapterOwner) {
        this.chapterOwner = chapterOwner;
    }

    public List<SubChapterTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<SubChapterTopic> topics) {
        this.topics = topics;
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

}
