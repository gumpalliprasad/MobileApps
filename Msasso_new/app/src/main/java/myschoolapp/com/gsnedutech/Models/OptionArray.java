package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OptionArray {

    @SerializedName("surveyOption")
    @Expose
    private String surveyOption;
    @SerializedName("surveyOptionId")
    @Expose
    private String surveyOptionId;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    boolean selected = false;

    public String getSurveyOption() {
        return surveyOption;
    }

    public void setSurveyOption(String surveyOption) {
        this.surveyOption = surveyOption;
    }

    public String getSurveyOptionId() {
        return surveyOptionId;
    }

    public void setSurveyOptionId(String surveyOptionId) {
        this.surveyOptionId = surveyOptionId;
    }
}
