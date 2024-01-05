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

public class StdnTestTopics implements Serializable {
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("topicOwner")
    @Expose
    private String topicOwner;
    @SerializedName("chaptertopicAccessId")
    @Expose
    private String chaptertopicAccessId;
    @SerializedName("topicName")
    @Expose
    private String topicName;
    @SerializedName("topicschemaName")
    @Expose
    private String topicschemaName;
    @SerializedName("isActive")
    @Expose
    private String isActive;

    @SerializedName("topicCCMapId")
    @Expose
    private String topicCCMapId;
    public String getTopicCCMapId() {
        return topicCCMapId;
    }

    public void setTopicCCMapId(String topicCCMapId) {
        this.topicCCMapId = topicCCMapId;
    }

    @SerializedName("questionCCMapId")
    @Expose
    private String questionCCMapId;
    public String getQuestionCCMapId() {
        return questionCCMapId;
    }

    public void setQuestionCCMapId(String questionCCMapId) {
        this.questionCCMapId = questionCCMapId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicOwner() {
        return topicOwner;
    }

    public void setTopicOwner(String topicOwner) {
        this.topicOwner = topicOwner;
    }

    public String getChaptertopicAccessId() {
        return chaptertopicAccessId;
    }

    public void setChaptertopicAccessId(String chaptertopicAccessId) {
        this.chaptertopicAccessId = chaptertopicAccessId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicschemaName() {
        return topicschemaName;
    }

    public void setTopicschemaName(String topicschemaName) {
        this.topicschemaName = topicschemaName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
