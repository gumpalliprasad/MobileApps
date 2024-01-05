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

public class CourseMockTestReports implements Serializable {
    @SerializedName("wrongAnswers")
    @Expose
    private Integer wrongAnswers;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("totalTimeSpent")
    @Expose
    private Integer totalTimeSpent;
    @SerializedName("percentage")
    @Expose
    private String percentage;
    @SerializedName("skippedAnswers")
    @Expose
    private Integer skippedAnswers;
    @SerializedName("mockTestId")
    @Expose
    private Integer mockTestId;
    @SerializedName("correctAnswers")
    @Expose
    private Integer correctAnswers;

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(Integer totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public Integer getSkippedAnswers() {
        return skippedAnswers;
    }

    public void setSkippedAnswers(Integer skippedAnswers) {
        this.skippedAnswers = skippedAnswers;
    }

    public Integer getMockTestId() {
        return mockTestId;
    }

    public void setMockTestId(Integer mockTestId) {
        this.mockTestId = mockTestId;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
