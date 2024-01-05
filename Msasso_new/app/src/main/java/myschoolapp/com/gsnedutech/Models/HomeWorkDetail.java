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

public class HomeWorkDetail implements Serializable {
    @SerializedName("HWdate")
    @Expose
    private String hWdate;
    @SerializedName("HomeWorkDetail")
    @Expose
    private List<HomeWorkDetails> homeWorkDetail = null;

    public String getHWdate() {
        return hWdate;
    }

    public void setHWdate(String hWdate) {
        this.hWdate = hWdate;
    }

    public List<HomeWorkDetails> getHomeWorkDetail() {
        return homeWorkDetail;
    }

    public void setHomeWorkDetail(List<HomeWorkDetails> homeWorkDetail) {
        this.homeWorkDetail = homeWorkDetail;
    }
}
