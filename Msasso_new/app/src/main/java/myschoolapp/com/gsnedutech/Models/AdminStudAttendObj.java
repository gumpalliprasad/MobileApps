/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/10/19 2:51 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminStudAttendObj {
    @SerializedName("latePercentageCount")
    @Expose
    private Integer latePercentageCount;
    @SerializedName("presentPercentage")
    @Expose
    private Double presentPercentage;
    @SerializedName("presentPercentageCount")
    @Expose
    private Integer presentPercentageCount;
    @SerializedName("absentPercentageCount")
    @Expose
    private Integer absentPercentageCount;
    @SerializedName("absentPercentage")
    @Expose
    private Double absentPercentage;
    @SerializedName("latePercentage")
    @Expose
    private Double latePercentage;

    public Integer getLatePercentageCount() {
        return latePercentageCount;
    }

    public void setLatePercentageCount(Integer latePercentageCount) {
        this.latePercentageCount = latePercentageCount;
    }

    public Double getPresentPercentage() {
        return presentPercentage;
    }

    public void setPresentPercentage(Double presentPercentage) {
        this.presentPercentage = presentPercentage;
    }

    public Integer getPresentPercentageCount() {
        return presentPercentageCount;
    }

    public void setPresentPercentageCount(Integer presentPercentageCount) {
        this.presentPercentageCount = presentPercentageCount;
    }

    public Integer getAbsentPercentageCount() {
        return absentPercentageCount;
    }

    public void setAbsentPercentageCount(Integer absentPercentageCount) {
        this.absentPercentageCount = absentPercentageCount;
    }

    public Double getAbsentPercentage() {
        return absentPercentage;
    }

    public void setAbsentPercentage(Double absentPercentage) {
        this.absentPercentage = absentPercentage;
    }

    public Double getLatePercentage() {
        return latePercentage;
    }

    public void setLatePercentage(Double latePercentage) {
        this.latePercentage = latePercentage;
    }

}
