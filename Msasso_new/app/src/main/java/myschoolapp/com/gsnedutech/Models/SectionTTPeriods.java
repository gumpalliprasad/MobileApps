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
import java.util.ArrayList;
import java.util.List;

public class SectionTTPeriods implements Serializable {
    @SerializedName("periodId")
    @Expose
    private String periodId;
    @SerializedName("isElective")
    @Expose
    private String isElective;
    @SerializedName("isAssigned")
    @Expose
    private String isAssigned;
    @SerializedName("isBreak")
    @Expose
    private String isBreak;
    @SerializedName("breakDesc")
    @Expose
    private String breakDesc;
    @SerializedName("periodStartTime")
    @Expose
    private String periodStartTime;
    @SerializedName("periodName")
    @Expose
    private String periodName;
    @SerializedName("periodEndTime")
    @Expose
    private String periodEndTime;
    @SerializedName("timeTableId")
    @Expose
    private String timeTableId;
    @SerializedName("subjectTeacher")
    @Expose
    private List<SectionTTSubjectTeacher> subjectTeacher = new ArrayList<>();
    @SerializedName("electiveGroupName")
    @Expose
    private String electiveGroupName;

    public String getPeriodId() {
        return periodId;
    }

    public void setPeriodId(String periodId) {
        this.periodId = periodId;
    }

    public String getIsElective() {
        return isElective;
    }

    public void setIsElective(String isElective) {
        this.isElective = isElective;
    }

    public String getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(String isAssigned) {
        this.isAssigned = isAssigned;
    }

    public String getIsBreak() {
        return isBreak;
    }

    public void setIsBreak(String isBreak) {
        this.isBreak = isBreak;
    }

    public String getBreakDesc() {
        return breakDesc;
    }

    public void setBreakDesc(String breakDesc) {
        this.breakDesc = breakDesc;
    }

    public String getPeriodStartTime() {
        return periodStartTime;
    }

    public void setPeriodStartTime(String periodStartTime) {
        this.periodStartTime = periodStartTime;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getPeriodEndTime() {
        return periodEndTime;
    }

    public void setPeriodEndTime(String periodEndTime) {
        this.periodEndTime = periodEndTime;
    }

    public String getTimeTableId() {
        return timeTableId;
    }

    public void setTimeTableId(String timeTableId) {
        this.timeTableId = timeTableId;
    }

    public List<SectionTTSubjectTeacher> getSubjectTeacher() {
        return subjectTeacher;
    }

    public void setSubjectTeacher(List<SectionTTSubjectTeacher> subjectTeacher) {
        this.subjectTeacher = subjectTeacher;
    }

    public String getElectiveGroupName() {
        return electiveGroupName;
    }

    public void setElectiveGroupName(String electiveGroupName) {
        this.electiveGroupName = electiveGroupName;
    }

}
