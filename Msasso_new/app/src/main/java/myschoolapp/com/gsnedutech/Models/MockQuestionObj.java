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

public class MockQuestionObj implements Serializable {

    @SerializedName("wrongMarks")
    @Expose
    private String wrongMarks;
    @SerializedName("questionId")
    @Expose
    private String questionId;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("questionOptions")
    @Expose
    private List<MockMFQQues> queOptions;

    public List<MockMFQQues> getQueOptions() {
        return queOptions;
    }

    public void setQueOptions(List<MockMFQQues> queOptions) {
        this.queOptions = queOptions;
    }



    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("chapterName")
    @Expose
    private String chapterName;
    @SerializedName("explanation")
    @Expose
    private String explanation;
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("option3")
    @Expose
    private String option3;
    @SerializedName("topicName")
    @Expose
    private String topicName;
    @SerializedName("option4")
    @Expose
    private String option4;
    @SerializedName("correctMarks")
    @Expose
    private String correctMarks;
    @SerializedName("option1")
    @Expose
    private String option1;
    @SerializedName("option2")
    @Expose
    private String option2;
    @SerializedName("correctAnswer")
    @Expose
    private String correctAnswer;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    @SerializedName("selected_answer")
    @Expose
    private String selectedAnswer="blank";
    @SerializedName("timetaken")
    @Expose
    private long timetaken=0;
    @SerializedName("reviewflag")
    @Expose
    private boolean reviewflag = false;
    @SerializedName("visitedflag")
    @Expose
    private boolean visitedflag  = false;
    @SerializedName("proceed")
    @Expose
    private boolean proceed;
    @SerializedName("style")
    @Expose
    private String style="not_visited_div";

    @SerializedName("question_number")
    @Expose
    private int question_number;
    @SerializedName("chapterCCMapId")
    @Expose
    private String chapterCCMapId;

    public String getChapterCCMapId() {
        return chapterCCMapId;
    }

    public void setChapterCCMapId(String chapterCCMapId) {
        this.chapterCCMapId = chapterCCMapId;
    }

    @SerializedName("topicCCMapId")
    @Expose
    private String topicCCMapId;
    public String getTopicCCMapId() {
        return topicCCMapId;
    }

    public void setTopicCCMapId(String topicCCMapId) {
        this.topicCCMapId = topicCCMapId;
    }

    @SerializedName("questionCCMapId")
    @Expose
    private String questionCCMapId;
    public String getQuestionCCMapId() {
        return questionCCMapId;
    }

    public void setQuestionCCMapId(String questionCCMapId) {
        this.questionCCMapId = questionCCMapId;
    }

    public int getQuestion_number() {
        return question_number;
    }

    public void setQuestion_number(int question_number) {
        this.question_number = question_number;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }



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


    public String getWrongMarks() {
        return wrongMarks;
    }

    public void setWrongMarks(String wrongMarks) {
        this.wrongMarks = wrongMarks;
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

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getCorrectMarks() {
        return correctMarks;
    }

    public void setCorrectMarks(String correctMarks) {
        this.correctMarks = correctMarks;
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

}
