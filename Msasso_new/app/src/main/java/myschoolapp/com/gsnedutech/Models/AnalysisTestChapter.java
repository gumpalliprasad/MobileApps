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

public class AnalysisTestChapter implements Serializable {
    @SerializedName("chapterAnalysisArray")
    @Expose
    private List<AnalysisChapterArray> chapterAnalysisArray = null;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("percentage")
    @Expose
    private Double percentage;
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

    public List<AnalysisChapterArray> getChapterAnalysisArray() {
        return chapterAnalysisArray;
    }

    public void setChapterAnalysisArray(List<AnalysisChapterArray> chapterAnalysisArray) {
        this.chapterAnalysisArray = chapterAnalysisArray;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

}
