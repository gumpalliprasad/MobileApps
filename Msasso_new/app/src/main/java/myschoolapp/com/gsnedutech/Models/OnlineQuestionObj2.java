
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

public class OnlineQuestionObj2 implements Serializable {

    @SerializedName("question_number")
    @Expose
    private int question_number;

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    @SerializedName("marks")
    @Expose
    private Integer marks;

    public Integer getQuestionDisplayOrder() {
        return questionDisplayOrder;
    }

    public void setQuestionDisplayOrder(Integer questionDisplayOrder) {
        this.questionDisplayOrder = questionDisplayOrder;
    }

    @SerializedName("questionDisplayOrder")
    @Expose
    private Integer questionDisplayOrder = 0;

    @SerializedName("questionId")
    @Expose
    private Integer questionId;

    public String getQuestionMapId() {
        return questionMapId;
    }

    public void setQuestionMapId(String questionMapId) {
        this.questionMapId = questionMapId;
    }

    @SerializedName("questionMapId")
    @Expose
    private String questionMapId = "";
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
    private String questType = "MCQ";
    @SerializedName("subjectId")
    @Expose
    private String subjectId;
    @SerializedName("chapterId")
    @Expose
    private String chapterId;
    @SerializedName("topicId")
    @Expose
    private String topicId;
    @SerializedName("testSectionName")
    @Expose
    private String testSectionName = "NA";

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("timeTaken")
    @Expose
    private String timeTaken = "0";
    @SerializedName("option3")
    @Expose
    private String option3 = "NA";
    @SerializedName("option4")
    @Expose
    private String option4 = "NA";
    @SerializedName("style")
    @Expose
    private String style = "not_visited_div";
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
    @SerializedName("chapterName")
    @Expose
    private String chapterName;
    @SerializedName("topic_name")
    @Expose
    private String topic_name;
    @SerializedName("questions")
    @Expose
    private List<OnlineQues> questions = null;
    @SerializedName("updated")
    @Expose
    private Boolean updated;
    @SerializedName("correctMarks")
    @Expose
    private int correctMarks = 0;
    @SerializedName("wrongMarks")
    @Expose
    private String wrongMarks = "";
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

    public String getParagraphId() {
        return paragraphId;
    }

    public void setParagraphId(String paragraphId) {
        this.paragraphId = paragraphId;
    }


    @SerializedName("paragraphId")
    @Expose
    private String paragraphId = "";
    @SerializedName("partialMarksAvailable")
    @Expose
    private String partialMarksAvailable = "";
    @SerializedName("partialCorrectMarks")
    @Expose
    private String partialCorrectMarks = "";

    public String getPartialMarksAvailable() {
        return partialMarksAvailable;
    }

    public void setPartialMarksAvailable(String partialMarksAvailable) {
        this.partialMarksAvailable = partialMarksAvailable;
    }

    public String getPartialCorrectMarks() {
        return partialCorrectMarks;
    }

    public void setPartialCorrectMarks(String partialCorrectMarks) {
        this.partialCorrectMarks = partialCorrectMarks;
    }

    public String getPartialWrongMarks() {
        return partialWrongMarks;
    }

    public void setPartialWrongMarks(String partialWrongMarks) {
        this.partialWrongMarks = partialWrongMarks;
    }

    public String getMarksUnattempted() {
        return marksUnattempted;
    }

    public void setMarksUnattempted(String marksUnattempted) {
        this.marksUnattempted = marksUnattempted;
    }

    public String getIsOptionMarks() {
        return isOptionMarks;
    }

    public void setIsOptionMarks(String isOptionMarks) {
        this.isOptionMarks = isOptionMarks;
    }

    @SerializedName("partialWrongMarks")
    @Expose
    private String partialWrongMarks = "";
    @SerializedName("marksUnattempted")
    @Expose
    private String marksUnattempted = "";
    @SerializedName("isOptionMarks")
    @Expose
    private String isOptionMarks = "";

    @SerializedName("isGrace")
    @Expose
    private int isGrace = 0;

    public int getIsGrace() {
        return isGrace;
    }

    public void setIsGrace(int isGrace) {
        this.isGrace = isGrace;
    }


    public int getCorrectMarks() {
        return correctMarks;
    }

    public void setCorrectMarks(int correctMarks) {
        this.correctMarks = correctMarks;
    }

    public String getWrongMarks() {
        return wrongMarks;
    }

    public void setWrongMarks(String wrongMarks) {
        this.wrongMarks = wrongMarks;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }


    public int getQuestion_number() {
        return question_number;
    }

    public void setQuestion_number(int question_number) {
        this.question_number = question_number;
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

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    @Expose
    @SerializedName("explanation")
    private String Explanation = "";

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

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
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

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public List<OnlineQues> getQuestions() {
        return questions;
    }

    public void setQuestions(List<OnlineQues> questions) {
        this.questions = questions;
    }

    public int getExamSectionId() {
        return examSectionId;
    }

    public void setExamSectionId(int examSectionId) {
        this.examSectionId = examSectionId;
    }

    @SerializedName("examSectionId")
    @Expose
    private int examSectionId;
}