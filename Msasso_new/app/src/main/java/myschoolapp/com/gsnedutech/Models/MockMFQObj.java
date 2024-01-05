
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

public class MockMFQObj implements Serializable {

    @SerializedName("chapterId")
    @Expose
    private String mChapterId;
    @SerializedName("chapterName")
    @Expose
    private String mChapterName;
    @SerializedName("correctAnswer")
    @Expose
    private String mCorrectAnswer;
    @SerializedName("correctMarks")
    @Expose
    private String mCorrectMarks;
    @SerializedName("duration")
    @Expose
    private String mDuration;
    @SerializedName("explanation")
    @Expose
    private String mExplanation;
    @SerializedName("questType")
    @Expose
    private String mQuestType;
    @SerializedName("question")
    @Expose
    private List<MockMFQQues> mMockMFQQues;
    @SerializedName("questionId")
    @Expose
    private String mQuestionId;
    @SerializedName("subjectGroup")
    @Expose
    private String mSubjectGroup;
    @SerializedName("subjectId")
    @Expose
    private String mSubjectId;
    @SerializedName("subjectName")
    @Expose
    private String mSubjectName;
    @SerializedName("topicId")
    @Expose
    private String mTopicId;
    @SerializedName("topicName")
    @Expose
    private String mTopicName;
    @SerializedName("wrongMarks")
    @Expose
    private String mWrongMarks;

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

    public String getChapterId() {
        return mChapterId;
    }

    public void setChapterId(String chapterId) {
        mChapterId = chapterId;
    }

    public String getChapterName() {
        return mChapterName;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }

    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }

    public String getCorrectMarks() {
        return mCorrectMarks;
    }

    public void setCorrectMarks(String correctMarks) {
        mCorrectMarks = correctMarks;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public String getExplanation() {
        return mExplanation;
    }

    public void setExplanation(String explanation) {
        mExplanation = explanation;
    }

    public String getQuestType() {
        return mQuestType;
    }

    public void setQuestType(String questType) {
        mQuestType = questType;
    }

    public List<MockMFQQues> getQuestion() {
        return mMockMFQQues;
    }

    public void setQuestion(List<MockMFQQues> mockMFQQues) {
        mMockMFQQues = mockMFQQues;
    }

    public String getQuestionId() {
        return mQuestionId;
    }

    public void setQuestionId(String questionId) {
        mQuestionId = questionId;
    }

    public String getSubjectGroup() {
        return mSubjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        mSubjectGroup = subjectGroup;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public void setSubjectName(String subjectName) {
        mSubjectName = subjectName;
    }

    public String getTopicId() {
        return mTopicId;
    }

    public void setTopicId(String topicId) {
        mTopicId = topicId;
    }

    public String getTopicName() {
        return mTopicName;
    }

    public void setTopicName(String topicName) {
        mTopicName = topicName;
    }

    public String getWrongMarks() {
        return mWrongMarks;
    }

    public void setWrongMarks(String wrongMarks) {
        mWrongMarks = wrongMarks;
    }

}
