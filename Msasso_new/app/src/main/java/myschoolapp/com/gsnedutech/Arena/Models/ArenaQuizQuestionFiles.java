package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArenaQuizQuestionFiles implements Serializable {
    @SerializedName("arenaQuestionId")
    @Expose
    private String arenaQuestionId;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("questTime")
    @Expose
    private String questTime;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("arenaId")
    @Expose
    private Integer arenaId;
    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("option3")
    @Expose
    private String option3;
    @SerializedName("option4")
    @Expose
    private String option4;
    @SerializedName("option1")
    @Expose
    private String option1;
    @SerializedName("option2")
    @Expose
    private String option2;
    @SerializedName("explanation")
    @Expose
    private String explanation;
    @SerializedName("isActive")
    @Expose
    private String isActive;
    String selectedAnswer = "NA";


    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }


    public String getArenaQuestionId() {
        return arenaQuestionId;
    }

    public void setArenaQuestionId(String arenaQuestionId) {
        this.arenaQuestionId = arenaQuestionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestTime() {
        return questTime;
    }

    public void setQuestTime(String questTime) {
        this.questTime = questTime;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getArenaId() {
        return arenaId;
    }

    public void setArenaId(Integer arenaId) {
        this.arenaId = arenaId;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }



}
