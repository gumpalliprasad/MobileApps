/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 2:23 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 7/9/19 12:38 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminTestWeekObj {
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
