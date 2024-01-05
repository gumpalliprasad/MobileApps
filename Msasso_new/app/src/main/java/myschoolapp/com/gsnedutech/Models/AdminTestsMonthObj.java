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

public class AdminTestsMonthObj {
    @SerializedName("Weeks")
    @Expose
    private List<AdminTestWeekObj> weeks = null;
    @SerializedName("monthName")
    @Expose
    private String monthName;
    @SerializedName("monthId")
    @Expose
    private String monthId;

    public List<AdminTestWeekObj> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<AdminTestWeekObj> weeks) {
        this.weeks = weeks;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getMonthId() {
        return monthId;
    }

    public void setMonthId(String monthId) {
        this.monthId = monthId;
    }

}
