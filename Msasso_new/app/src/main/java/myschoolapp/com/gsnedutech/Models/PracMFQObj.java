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

import java.util.List;

public class PracMFQObj {
    @SerializedName("questionId")
    @Expose
    private String questionId;
    @SerializedName("question")
    @Expose
    private List<PracMFQQue> question = null;
    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("explanation")
    @Expose
    private String explanation;
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    @SerializedName("correctAnswer")
    @Expose
    private String correctAnswer;
    public boolean isProceed() {
        return proceed;
    }
    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }
    public String getSelectedAnswer() {
        return selectedAnswer;
    }
    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
    public long getTimetaken() {
        return timetaken;
    }
    public void setTimetaken(long timetaken) {
        this.timetaken = timetaken;
    }
    public int getQuesNO() {
        return quesNO;
    }
    public void setQuesNO(int quesNO) {
        this.quesNO = quesNO;
    }
    @SerializedName("proceed")
    @Expose
    private boolean proceed;
    @SerializedName("selected_answer")
    @Expose
    private String selectedAnswer;
    @SerializedName("timetaken")
    @Expose
    private long timetaken;
    @SerializedName("quesNo")
    @Expose
    private int quesNO;
    public String getQuestionId() {
        return questionId;
    }
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public List<PracMFQQue> getQuestion() {
        return question;
    }
    public void setQuestion(List<PracMFQQue> question) {
        this.question = question;
    }
    public String getQuestType() {
        return questType;
    }
    public void setQuestType(String questType) {
        this.questType = questType;
    }
    public String getExplanation() {
        return explanation;
    }
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    public int getQuestionCCMapId() {
        return questionCCMapId;
    }

    public void setQuestionCCMapId(int questionCCMapId) {
        this.questionCCMapId = questionCCMapId;
    }

    @SerializedName("questionCCMapId")
    @Expose
    private int questionCCMapId;
}