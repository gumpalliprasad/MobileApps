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

import java.util.List;

public class AdminTestsYearObj {
    @SerializedName("Months")
    @Expose
    private List<AdminTestsMonthObj> months = null;
    @SerializedName("yearId")
    @Expose
    private String yearId;

    public List<AdminTestsMonthObj> getMonths() {
        return months;
    }

    public void setMonths(List<AdminTestsMonthObj> months) {
        this.months = months;
    }

    public String getYearId() {
        return yearId;
    }

    public void setYearId(String yearId) {
        this.yearId = yearId;
    }
}
