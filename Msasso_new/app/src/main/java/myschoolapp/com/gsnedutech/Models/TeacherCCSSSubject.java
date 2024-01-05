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

public class TeacherCCSSSubject implements Serializable {
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("electiveGroupId")
    @Expose
    private String electiveGroupId="0";
    @SerializedName("isElective")
    @Expose
    private String isElective="0";
    @SerializedName("electiveName")
    @Expose
    private String electiveName="NA";

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType = "NA";

    public String getElectiveGroupId() {
        return electiveGroupId;
    }

    public void setElectiveGroupId(String electiveGroupId) {
        this.electiveGroupId = electiveGroupId;
    }

    public String getIsElective() {
        return isElective;
    }

    public void setIsElective(String isElective) {
        this.isElective = isElective;
    }

    public String getElectiveName() {
        return electiveName;
    }

    public void setElectiveName(String electiveName) {
        this.electiveName = electiveName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return subjectName+"   " ;
    }
}
