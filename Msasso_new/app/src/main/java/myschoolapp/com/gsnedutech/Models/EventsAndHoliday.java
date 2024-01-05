/*
 * *
 *  * Created by SriRamaMurthy A on 31/10/19 3:27 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 31/10/19 1:34 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventsAndHoliday {
    @SerializedName("eventDetails")
    @Expose
    private List<EventDetail> eventDetails = null;
    @SerializedName("eventCategoryName")
    @Expose
    private String eventCategoryName;
    @SerializedName("eventCategoryId")
    @Expose
    private Integer eventCategoryId;

    public List<EventDetail> getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(List<EventDetail> eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getEventCategoryName() {
        return eventCategoryName;
    }

    public void setEventCategoryName(String eventCategoryName) {
        this.eventCategoryName = eventCategoryName;
    }

    public Integer getEventCategoryId() {
        return eventCategoryId;
    }

    public void setEventCategoryId(Integer eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }
}
