
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

public class PracQuestionObj {

    @SerializedName("subject_id")
    @Expose
    private String subjectId;

    @SerializedName("questionId")
    @Expose
    private String questionId;
    @SerializedName("quesNo")
    @Expose
    private int quesNO;
    @SerializedName("questType")
    @Expose
    private String questionType;
    @SerializedName("question")
    @Expose
    private String question;
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
    @SerializedName("correctAnswer")
    @Expose
    private String correctAnswer;
    @SerializedName("subject_name")
    @Expose
    private String subjectName;
    @SerializedName("selected_answer")
    @Expose
    private String selectedAnswer = "blank";
    @SerializedName("proceed")
    @Expose
    private boolean proceed = false;
    @SerializedName("explanation")
    @Expose
    private String explanation="";
    @SerializedName("subject_group")
    @Expose
    private String subjectGroup;
    @SerializedName("timetaken")
    @Expose
    private long timetaken=0;
    @SerializedName("reviewflag")
    @Expose
    private boolean reviewflag;
    @SerializedName("visitedflag")
    @Expose
    private boolean visitedflag;


    public List<PracMFQQue> getQueOptions() {
        return queOptions;
    }

    public void setQueOptions(List<PracMFQQue> queOptions) {
        this.queOptions = queOptions;
    }

    @SerializedName("questionOptions")
    @Expose
    private List<PracMFQQue> queOptions;


    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @SerializedName("style")
    @Expose
    private String style="not_visited_div";

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }


    public long getTimetaken() {
        return timetaken;
    }

    public void setTimetaken(long timetaken) {
        this.timetaken = timetaken;
    }

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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
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

    public int getQuesNO() {
        return quesNO;
    }

    public void setQuesNO(int quesNO) {
        this.quesNO = quesNO;
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
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}