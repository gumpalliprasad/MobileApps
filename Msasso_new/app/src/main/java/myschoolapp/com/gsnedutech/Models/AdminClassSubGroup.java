/*
 * *
 *  * Created by SriRamaMurthy A on 6/9/19 2:14 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 5/9/19 3:20 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AdminClassSubGroup implements Serializable {
    @SerializedName("isElective")
    @Expose
    private String isElective;
    @SerializedName("subjects")
    @Expose
    private List<AdminClassSub> subjects = null;
    @SerializedName("electiveGroupId")
    @Expose
    private String electiveGroupId;
    @SerializedName("electiveGroupName")
    @Expose
    private String electiveGroupName;

    public String getIsElective() {
        return isElective;
    }

    public void setIsElective(String isElective) {
        this.isElective = isElective;
    }

    public List<AdminClassSub> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<AdminClassSub> subjects) {
        this.subjects = subjects;
    }

    public String getElectiveGroupId() {
        return electiveGroupId;
    }

    public void setElectiveGroupId(String electiveGroupId) {
        this.electiveGroupId = electiveGroupId;
    }

    public String getElectiveGroupName() {
        return electiveGroupName;
    }

    public void setElectiveGroupName(String electiveGroupName) {
        this.electiveGroupName = electiveGroupName;
    }

}
