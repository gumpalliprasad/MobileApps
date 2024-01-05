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

public class StdnTestChapters implements Serializable {
    @SerializedName("chapterOwner")
    @Expose
    private String chapterOwner;
    @SerializedName("chapterschemaName")
    @Expose
    private String chapterschemaName;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("ChapterTopic")
    @Expose
    private List<StdnTestTopics> chapterTopic = null;
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

    public String getChapterOwner() {
        return chapterOwner;
    }

    public void setChapterOwner(String chapterOwner) {
        this.chapterOwner = chapterOwner;
    }

    public String getChapterschemaName() {
        return chapterschemaName;
    }

    public void setChapterschemaName(String chapterschemaName) {
        this.chapterschemaName = chapterschemaName;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public List<StdnTestTopics> getChapterTopic() {
        return chapterTopic;
    }

    public void setChapterTopic(List<StdnTestTopics> chapterTopic) {
        this.chapterTopic = chapterTopic;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}
