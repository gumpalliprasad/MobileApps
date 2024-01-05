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

public class SectionTimeTableObj implements Serializable {
    @SerializedName("workingDayId")
    @Expose
    private String workingDayId;
    @SerializedName("dayName")
    @Expose
    private String dayName;
    @SerializedName("periods")
    @Expose
    private List<SectionTTPeriods> periods = null;

    public String getWorkingDayId() {
        return workingDayId;
    }

    public void setWorkingDayId(String workingDayId) {
        this.workingDayId = workingDayId;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public List<SectionTTPeriods> getPeriods() {
        return periods;
    }

    public void setPeriods(List<SectionTTPeriods> periods) {
        this.periods = periods;
    }

}
