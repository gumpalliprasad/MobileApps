/*
 * *
 *  * Created by SriRamaMurthy A on 4/9/19 1:08 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 4/9/19 1:05 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminFeaturedUpcomingExamObj {


    @SerializedName("testStartDate")
    @Expose
    private String testStartDate;
    @SerializedName("testId")
    @Expose
    private String testId;
    @SerializedName("testName")
    @Expose
    private String testName;

    public String getTestStartDate() {
        return testStartDate;
    }

    public void setTestStartDate(String testStartDate) {
        this.testStartDate = testStartDate;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

}
