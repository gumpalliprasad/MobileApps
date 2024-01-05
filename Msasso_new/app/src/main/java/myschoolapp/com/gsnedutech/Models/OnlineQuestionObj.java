
/*
 * *
 *  * Created by SriRamaMurthy A on 3/10/19 7:01 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/10/19 7:01 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OnlineQuestionObj implements Serializable {

    @SerializedName("question_number")
    @Expose
    private int question_number;

    public int getQuestion_number() {
        return question_number;
    }

    public void setQuestion_number(int question_number) {
        this.question_number = question_number;
    }

    @SerializedName("questionId")
    @Expose
    private Integer questionId;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("selectedAnswer")
    @Expose
    private String selectedAnswer = "";
    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("testSectionName")
    @Expose
    private String testSectionName;
    @SerializedName("timeTaken")
    @Expose
    private String timeTaken;
    @SerializedName("option3")
    @Expose
    private String option3 = "NA";
    @SerializedName("option4")
    @Expose
    private String option4 = "NA";
    @SerializedName("style")
    @Expose
    private String style;
    @SerializedName("option1")
    @Expose
    private String option1 = "NA";
    @SerializedName("option2")
    @Expose
    private String option2 = "NA";
    @SerializedName("correctAnswer")
    @Expose
    private String correctAnswer;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("questions")
    @Expose
    private List<OnlineQues> questions = null;

    public boolean isReviewflag() {
        return reviewflag;
    }

    public void setReviewflag(boolean reviewflag) {
        this.reviewflag = reviewflag;
    }

    public boolean isVisitedflag() {
        return visitedflag;
    }

    public void setVisitedflag(boolean visitedflag) {
        this.visitedflag = visitedflag;
    }

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    @Expose
    @SerializedName("explanation")
    private String Explanation;

    public String getExplanation() {
        return Explanation;
    }

    public void setExplanation(String Explanation) {
        this.Explanation = Explanation;
    }

    @SerializedName("reviewflag")
    @Expose
    private boolean reviewflag = false;
    @SerializedName("visitedflag")
    @Expose
    private boolean visitedflag = false;
    @SerializedName("proceed")
    @Expose
    private boolean proceed = false;

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTestSectionName() {
        return testSectionName;
    }

    public void setTestSectionName(String testSectionName) {
        this.testSectionName = testSectionName;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<OnlineQues> getQuestions() {
        return questions;
    }

    public void setQuestions(List<OnlineQues> questions) {
        this.questions = questions;
    }

}