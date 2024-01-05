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

public class TeacherCCSSSection implements Serializable {

    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("sectionId")
    @Expose
    private String sectionId;
    @SerializedName("Subjects")
    @Expose
    private List<TeacherCCSSSubject> subjects = null;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public List<TeacherCCSSSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<TeacherCCSSSubject> subjects) {
        this.subjects = subjects;
    }

}
