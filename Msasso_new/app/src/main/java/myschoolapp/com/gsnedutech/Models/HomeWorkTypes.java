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

public class HomeWorkTypes implements Serializable {

    @SerializedName("homeTypeId")
    @Expose
    private Integer homeTypeId;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("homeWorkType")
    @Expose
    private String homeWorkType;

    public Integer getHomeTypeId() {
        return homeTypeId;
    }

    public void setHomeTypeId(Integer homeTypeId) {
        this.homeTypeId = homeTypeId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getHomeWorkType() {
        return homeWorkType;
    }

    public void setHomeWorkType(String homeWorkType) {
        this.homeWorkType = homeWorkType;
    }


    @Override
    public String toString() {
        return homeWorkType;
    }
}
