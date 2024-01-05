package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhubOption implements Serializable {
    @SerializedName("option")
    @Expose
    private String option;
    @SerializedName("isCorrect")
    @Expose
    private Boolean isCorrect;
    @SerializedName("marks")
    @Expose
    private Integer marks;
    @SerializedName("explanation")
    @Expose
    private String explanation;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
