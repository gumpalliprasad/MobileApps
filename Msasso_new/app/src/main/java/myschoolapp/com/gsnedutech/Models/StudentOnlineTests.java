
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

public class StudentOnlineTests implements Serializable {

    @SerializedName("yearId")
    @Expose
    private String yearId;
    @SerializedName("monthId")
    @Expose
    private String mMonthId;
    @SerializedName("monthName")
    @Expose
    private String mMonthName;
    @SerializedName("Tests")
    @Expose
    private List<StudentOnlineTestObj> mTests;

    public String getYearId() {
        return yearId;
    }

    public String getMonthId() {
        return mMonthId;
    }

    public void setMonthId(String monthId) {
        mMonthId = monthId;
    }

    public String getMonthName() {
        return mMonthName;
    }

    public void setMonthName(String monthName) {
        mMonthName = monthName;
    }

    public List<StudentOnlineTestObj> getTests() {
        return mTests;
    }

    public void setTests(List<StudentOnlineTestObj> tests) {
        mTests = tests;
    }

}
