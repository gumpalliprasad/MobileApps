package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvJeeQuestion implements Serializable {

    String _id, question, boardId, courseId, classId, yearId, sectionId, boardName, className;
    String courseCode, subjectName, year, sectionName, paperType, subjectId, questionTypeId;
    String questionTypeCode, questionStatus, chapterCode, topicCode;
    int avgTime, qTime, questionNumber, totalQuestionMarks, negativemarks, qOrder, noOfAttempts, noOfRights, noOfWrongs, isAttempted;
    ArrayList<AdvJeeQueOptions> Options;
    AdvJeeITQCorrectAnswer ITQCorrectAnswer;
    boolean negativeMarkingEnabled;

    String possibleCasesContent;

    public String getPossibleCasesContent() {
        return possibleCasesContent;
    }

    public void setPossibleCasesContent(String possibleCasesContent) {
        this.possibleCasesContent = possibleCasesContent;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    String additionalInfo;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getYearId() {
        return yearId;
    }

    public void setYearId(String yearId) {
        this.yearId = yearId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(String questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public String getQuestionTypeCode() {
        return questionTypeCode;
    }

    public void setQuestionTypeCode(String questionTypeCode) {
        this.questionTypeCode = questionTypeCode;
    }

    public String getQuestionStatus() {
        return questionStatus;
    }

    public void setQuestionStatus(String questionStatus) {
        this.questionStatus = questionStatus;
    }

    public String getChapterCode() {
        return chapterCode;
    }

    public void setChapterCode(String chapterCode) {
        this.chapterCode = chapterCode;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    public int getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(int avgTime) {
        this.avgTime = avgTime;
    }

    public int getqTime() {
        return qTime;
    }

    public void setqTime(int qTime) {
        this.qTime = qTime;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getTotalQuestionMarks() {
        return totalQuestionMarks;
    }

    public void setTotalQuestionMarks(int totalQuestionMarks) {
        this.totalQuestionMarks = totalQuestionMarks;
    }

    public int getNegativemarks() {
        return negativemarks;
    }

    public void setNegativemarks(int negativemarks) {
        this.negativemarks = negativemarks;
    }

    public int getqOrder() {
        return qOrder;
    }

    public void setqOrder(int qOrder) {
        this.qOrder = qOrder;
    }

    public int getNoOfAttempts() {
        return noOfAttempts;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        this.noOfAttempts = noOfAttempts;
    }

    public int getNoOfRights() {
        return noOfRights;
    }

    public void setNoOfRights(int noOfRights) {
        this.noOfRights = noOfRights;
    }

    public int getNoOfWrongs() {
        return noOfWrongs;
    }

    public void setNoOfWrongs(int noOfWrongs) {
        this.noOfWrongs = noOfWrongs;
    }

    public int getIsAttempted() {
        return isAttempted;
    }

    public void setIsAttempted(int isAttempted) {
        this.isAttempted = isAttempted;
    }

    public ArrayList<AdvJeeQueOptions> getOptions() {
        return Options;
    }

    public void setOptions(ArrayList<AdvJeeQueOptions> options) {
        Options = options;
    }

    public AdvJeeITQCorrectAnswer getITQCorrectAnswer() {
        return ITQCorrectAnswer;
    }

    public void setITQCorrectAnswer(AdvJeeITQCorrectAnswer ITQCorrectAnswer) {
        this.ITQCorrectAnswer = ITQCorrectAnswer;
    }

    public boolean isNegativeMarkingEnabled() {
        return negativeMarkingEnabled;
    }

    public void setNegativeMarkingEnabled(boolean negativeMarkingEnabled) {
        this.negativeMarkingEnabled = negativeMarkingEnabled;
    }

    public boolean isPartialmarking() {
        return partialmarking;
    }

    public void setPartialmarking(boolean partialmarking) {
        this.partialmarking = partialmarking;
    }

    boolean partialmarking;

    public ArrayList<AdvJeeParagraphQues> getParagraphQuestions() {
        return paragraphQuestions;
    }

    public void setParagraphQuestions(ArrayList<AdvJeeParagraphQues> paragraphQuestions) {
        this.paragraphQuestions = paragraphQuestions;
    }

    ArrayList<AdvJeeParagraphQues> paragraphQuestions;

    public String getqDisplayOrder() {
        return qDisplayOrder;
    }

    public void setqDisplayOrder(String qDisplayOrder) {
        this.qDisplayOrder = qDisplayOrder;
    }

    String qDisplayOrder;

//            "correctAnswer": [],


}
