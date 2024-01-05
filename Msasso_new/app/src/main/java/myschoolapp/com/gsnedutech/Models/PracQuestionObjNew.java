
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
import java.util.List;

public class PracQuestionObjNew implements Serializable {

    @SerializedName("correctAnswer")
    @Expose
    private String mCorrectAnswer;
    @SerializedName("explanation")
    @Expose
    private String mExplanation;
    @SerializedName("option1")
    @Expose
    private String mOption1;
    @SerializedName("option2")
    @Expose
    private String mOption2;
    @SerializedName("option3")
    @Expose
    private String mOption3;
    @SerializedName("option4")
    @Expose
    private String mOption4;
    @SerializedName("questType")
    @Expose
    private String mQuestType;
    @SerializedName("question")
    @Expose
    private String mQuestion;
    @SerializedName("questionId")
    @Expose
    private Long mQuestionId;
    public void setQueOptions(List<PracMFQQue> queOptions) {
        this.queOptions = queOptions;
    }
    @SerializedName("questionOptions")
    @Expose
    private List<PracMFQQue> queOptions;
    public List<PracMFQQue> getQueOptions() {
        return queOptions;
    }
    public long getTimetaken() {
        return timetaken;
    }
    public void setTimetaken(long timetaken) {
        this.timetaken = timetaken;
    }
    public String getSelectedAnswer() {
        return selectedAnswer;
    }
    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
    public boolean isProceed() {
        return proceed;
    }
    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }
    @SerializedName("timetaken")
    @Expose
    private long timetaken = 0;
    @SerializedName("selected_answer")
    @Expose
    private String selectedAnswer = "blank";
    @SerializedName("proceed")
    @Expose
    private boolean proceed = false;
    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }
    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }
    public String getExplanation() {
        return mExplanation;
    }
    public void setExplanation(String explanation) {
        mExplanation = explanation;
    }
    public String getOption1() {
        return mOption1;
    }
    public void setOption1(String option1) {
        mOption1 = option1;
    }
    public String getOption2() {
        return mOption2;
    }
    public void setOption2(String option2) {
        mOption2 = option2;
    }
    public String getOption3() {
        return mOption3;
    }
    public void setOption3(String option3) {
        mOption3 = option3;
    }
    public String getOption4() {
        return mOption4;
    }
    public void setOption4(String option4) {
        mOption4 = option4;
    }
    public String getQuestType() {
        return mQuestType;
    }
    public void setQuestType(String questType) {
        mQuestType = questType;
    }
    public String getQuestion() {
        return mQuestion;
    }
    public void setQuestion(String question) {
        mQuestion = question;
    }
    public Long getQuestionId() {
        return mQuestionId;
    }
    public void setQuestionId(Long questionId) {
        mQuestionId = questionId;
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
