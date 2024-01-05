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

public class StdnTestClass implements Serializable {
    @SerializedName("classId")
    @Expose
    private String classId;
    @SerializedName("subjects")
    @Expose
    private List<StdnTestSubject> subjects = null;
    @SerializedName("className")
    @Expose
    private String className;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public List<StdnTestSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<StdnTestSubject> subjects) {
        this.subjects = subjects;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    @Override
    public String toString() {
        String clasName = "";

        if (className.contains("6")){
            clasName = "Class VI  ";
        } if (className.contains("7")){
            clasName = "Class VII  ";
        } if (className.contains("8")){
            clasName = "Class VIII  ";
        } if (className.contains("9")){
            clasName = "Class IX  ";
        } if (className.contains("10")){
            clasName = "Class X  ";
        } if (className.contains("11")){
            clasName = "Class XI  ";
        } if (className.contains("12")){
            clasName = "Class XII  ";
        }
        return clasName;
    }
}
