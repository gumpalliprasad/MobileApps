package myschoolapp.com.gsnedutech;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import myschoolapp.com.gsnedutech.Models.OptionArray;

public class SurveyFormQuestions {
    @SerializedName("surveyQuesTypeId")
    @Expose
    private String surveyQuesTypeId;
    @SerializedName("surveyQuestionId")
    @Expose
    private String surveyQuestionId;
    @SerializedName("surveyQuestion")
    @Expose
    private String surveyQuestion;
    @SerializedName("optionArray")
    @Expose
    private List<OptionArray> optionArray = null;
    @SerializedName("surveyQuesType")
    @Expose
    private String surveyQuesType;
    @SerializedName("ratingMin")
    @Expose
    private String ratingMin;
    @SerializedName("ratingMax")
    @Expose
    private String ratingMax;
    String selectedOption = "NA";


    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }



    public String getSurveyQuesTypeId() {
        return surveyQuesTypeId;
    }

    public void setSurveyQuesTypeId(String surveyQuesTypeId) {
        this.surveyQuesTypeId = surveyQuesTypeId;
    }

    public String getSurveyQuestionId() {
        return surveyQuestionId;
    }

    public void setSurveyQuestionId(String surveyQuestionId) {
        this.surveyQuestionId = surveyQuestionId;
    }

    public String getSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(String surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }

    public List<OptionArray> getOptionArray() {
        return optionArray;
    }

    public void setOptionArray(List<OptionArray> optionArray) {
        this.optionArray = optionArray;
    }

    public String getSurveyQuesType() {
        return surveyQuesType;
    }

    public void setSurveyQuesType(String surveyQuesType) {
        this.surveyQuesType = surveyQuesType;
    }

    public String getRatingMin() {
        return ratingMin;
    }

    public void setRatingMin(String ratingMin) {
        this.ratingMin = ratingMin;
    }

    public String getRatingMax() {
        return ratingMax;
    }

    public void setRatingMax(String ratingMax) {
        this.ratingMax = ratingMax;
    }

}
