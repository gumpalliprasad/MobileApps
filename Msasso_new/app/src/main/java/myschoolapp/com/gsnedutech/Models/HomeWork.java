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

public class HomeWork implements Serializable {
    @SerializedName("homeWorkDesc")
    @Expose
    private String homeWorkDesc;
    @SerializedName("homeworkTypeId")
    @Expose
    private String homeworkTypeId;
    @SerializedName("HomeWorkDetails")
    @Expose
    private List<HomeWorkDetail> homeWorkDetails = null;

    public String getHomeWorkDesc() {
        return homeWorkDesc;
    }

    public void setHomeWorkDesc(String homeWorkDesc) {
        this.homeWorkDesc = homeWorkDesc;
    }

    public String getHomeworkTypeId() {
        return homeworkTypeId;
    }

    public void setHomeworkTypeId(String homeworkTypeId) {
        this.homeworkTypeId = homeworkTypeId;
    }

    public List<HomeWorkDetail> getHomeWorkDetails() {
        return homeWorkDetails;
    }

    public void setHomeWorkDetails(List<HomeWorkDetail> homeWorkDetails) {
        this.homeWorkDetails = homeWorkDetails;
    }

}
