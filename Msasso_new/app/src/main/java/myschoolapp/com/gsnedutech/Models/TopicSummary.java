
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


public class TopicSummary implements Serializable {

    @SerializedName("annexureContent")
    @Expose
    private String mAnnexureContent;
    @SerializedName("annexureId")
    @Expose
    private String mAnnexureId;

    public String getAnnexureContent() {
        return mAnnexureContent;
    }

    public void setAnnexureContent(String annexureContent) {
        mAnnexureContent = annexureContent;
    }

    public String getAnnexureId() {
        return mAnnexureId;
    }

    public void setAnnexureId(String annexureId) {
        mAnnexureId = annexureId;
    }

}
