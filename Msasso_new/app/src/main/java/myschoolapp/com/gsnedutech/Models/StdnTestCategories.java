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

public class StdnTestCategories implements Serializable {

    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("totalQuestions")
    @Expose
    private Integer totalQuestions;
    @SerializedName("wrongMarks")
    @Expose
    private Integer wrongMarks;
    @SerializedName("multipleSubjects")
    @Expose
    private Integer multipleSubjects;
    @SerializedName("isQTypeReq")
    @Expose
    private Integer isQTypeReq;
    @SerializedName("correctMarks")
    @Expose
    private Integer correctMarks;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getWrongMarks() {
        return wrongMarks;
    }

    public void setWrongMarks(Integer wrongMarks) {
        this.wrongMarks = wrongMarks;
    }

    public Integer getMultipleSubjects() {
        return multipleSubjects;
    }

    public void setMultipleSubjects(Integer multipleSubjects) {
        this.multipleSubjects = multipleSubjects;
    }

    public Integer getIsQTypeReq() {
        return isQTypeReq;
    }

    public void setIsQTypeReq(Integer isQTypeReq) {
        this.isQTypeReq = isQTypeReq;
    }

    public Integer getCorrectMarks() {
        return correctMarks;
    }

    public void setCorrectMarks(Integer correctMarks) {
        this.correctMarks = correctMarks;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
