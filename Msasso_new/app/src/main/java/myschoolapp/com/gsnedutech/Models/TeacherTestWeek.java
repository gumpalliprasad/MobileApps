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

public class TeacherTestWeek implements Serializable {

    @SerializedName("noOfTest")
    @Expose
    private String noOfTest;
    @SerializedName("weekOfMonth")
    @Expose
    private String weekOfMonth;

    public String getNoOfTest() {
        return noOfTest;
    }

    public void setNoOfTest(String noOfTest) {
        this.noOfTest = noOfTest;
    }

    public String getWeekOfMonth() {
        return weekOfMonth;
    }

    public void setWeekOfMonth(String weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

}