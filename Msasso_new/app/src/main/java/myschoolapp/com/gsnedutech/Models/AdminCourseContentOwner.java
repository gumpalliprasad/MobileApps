/*
 * *
 *  * Created by SriRamaMurthy A on 9/9/19 2:23 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 7/9/19 3:37 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdminCourseContentOwner {
    @SerializedName("contentSchema")
    @Expose
    private String contentSchema;
    @SerializedName("displayName")
    @Expose
    private String displayName;
    @SerializedName("refId")
    @Expose
    private Integer refId;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("courseId")
    @Expose
    private String courseId;

    public String getContentSchema() {
        return contentSchema;
    }

    public void setContentSchema(String contentSchema) {
        this.contentSchema = contentSchema;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
