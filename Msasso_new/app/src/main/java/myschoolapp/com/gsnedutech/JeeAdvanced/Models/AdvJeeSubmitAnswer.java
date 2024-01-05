package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvJeeSubmitAnswer implements Serializable {
    String _id;
    String uid;
    String userId;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    String year;

    public String getqType() {
        return qType;
    }

    public void setqType(String qType) {
        this.qType = qType;
    }

    String qType;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getQuestion() {
        return questionRefId;
    }

    public void setQuestion(String question) {
        this.questionRefId = question;
    }

    public ArrayList<String> getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(ArrayList<String> selectedOption) {
        this.selectedOption = selectedOption;
    }

    String chapterId;
    String questionRefId;
    ArrayList<String> selectedOption;

    public String getContentMatrixId() {
        return contentMatrixId;
    }

    public void setContentMatrixId(String contentMatrixId) {
        this.contentMatrixId = contentMatrixId;
    }

    String contentMatrixId;

    public String getQuestionRefId() {
        return questionRefId;
    }

    public void setQuestionRefId(String questionRefId) {
        this.questionRefId = questionRefId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    boolean isCorrect;
    String paperType;

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getYearId() {
        return yearId;
    }

    public void setYearId(String yearId) {
        this.yearId = yearId;
    }

    public String getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(String questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public String getParagraphQuesId() {
        return paragraphQuesId;
    }

    public void setParagraphQuesId(String paragraphQuesId) {
        this.paragraphQuesId = paragraphQuesId;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    String yearId;
    String questionTypeId;
    String paragraphQuesId;
    int marks;
}
