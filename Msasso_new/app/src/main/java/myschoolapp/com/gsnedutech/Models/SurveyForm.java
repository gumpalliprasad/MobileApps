package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyForm {
    @SerializedName("formEndDate")
    @Expose
    private String formEndDate;
    @SerializedName("formStartDate")
    @Expose
    private String formStartDate;
    @SerializedName("surveyFormDesc")
    @Expose
    private String surveyFormDesc;
    @SerializedName("surveyFormName")
    @Expose
    private String surveyFormName;
    @SerializedName("surveyFormId")
    @Expose
    private String surveyFormId;
    @SerializedName("isActive")
    @Expose
    private String isActive;

    public String getFormEndDate() {
        return formEndDate;
    }

    public void setFormEndDate(String formEndDate) {
        this.formEndDate = formEndDate;
    }

    public String getFormStartDate() {
        return formStartDate;
    }

    public void setFormStartDate(String formStartDate) {
        this.formStartDate = formStartDate;
    }

    public String getSurveyFormDesc() {
        return surveyFormDesc;
    }

    public void setSurveyFormDesc(String surveyFormDesc) {
        this.surveyFormDesc = surveyFormDesc;
    }

    public String getSurveyFormName() {
        return surveyFormName;
    }

    public void setSurveyFormName(String surveyFormName) {
        this.surveyFormName = surveyFormName;
    }

    public String getSurveyFormId() {
        return surveyFormId;
    }

    public void setSurveyFormId(String surveyFormId) {
        this.surveyFormId = surveyFormId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

}
