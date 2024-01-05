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

public class TeacherCCSSClass implements Serializable {
    @SerializedName("classId")
    @Expose
    private String classId;
    @SerializedName("Sections")
    @Expose
    private List<TeacherCCSSSection> sections = null;
    @SerializedName("className")
    @Expose
    private String className;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public List<TeacherCCSSSection> getSections() {
        return sections;
    }

    public void setSections(List<TeacherCCSSSection> sections) {
        this.sections = sections;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        String className = "";
        if (this.className.contains("1"))
            className = "I  ";
        if (this.className.contains("2"))
            className = "II  ";
        if (this.className.contains("3"))
            className = "III  ";
        if (this.className.contains("4"))
            className = "IV  ";
        if (this.className.contains("5"))
            className = "V  ";
        if (this.className.contains("6"))
            className = "VI  ";
        if (this.className.contains("7"))
            className = "VII  ";
        if (this.className.contains("8"))
            className = "VIII  ";
        if (this.className.contains("9"))
            className = "IX  ";
        if (this.className.contains("10"))
            className = "X  ";
        if (this.className.contains("11"))
            className = "XI  ";
        if (this.className.contains("12"))
            className = "XII  ";

        return className;
    }

}
