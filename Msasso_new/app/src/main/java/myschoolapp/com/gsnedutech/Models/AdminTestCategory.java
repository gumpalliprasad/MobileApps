/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 2:23 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 7/9/19 12:57 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdminTestCategory {
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("branchName")
    @Expose
    private String branchName;
    @SerializedName("years")
    @Expose
    private List<AdminTestsYearObj> years = null;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<AdminTestsYearObj> getYears() {
        return years;
    }

    public void setYears(List<AdminTestsYearObj> years) {
        this.years = years;
    }
}
