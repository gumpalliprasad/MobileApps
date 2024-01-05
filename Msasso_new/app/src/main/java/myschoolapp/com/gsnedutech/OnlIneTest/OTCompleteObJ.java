package myschoolapp.com.gsnedutech.OnlIneTest;

import java.util.ArrayList;

import myschoolapp.com.gsnedutech.Models.OnlineQuestionObj;


public class OTCompleteObJ {

    String _id, schemaName;
    int examId, studentId;
    String eStatus, ePath, creationDts, sTime, modifiedDts, eTime;

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    String eName;
    String examStartTime;
    String examEndTime;

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

    String rDate;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }

    public String getePath() {
        return ePath;
    }

    public void setePath(String ePath) {
        this.ePath = ePath;
    }

    public String getCreationDts() {
        return creationDts;
    }

    public void setCreationDts(String creationDts) {
        this.creationDts = creationDts;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String getModifiedDts() {
        return modifiedDts;
    }

    public void setModifiedDts(String modifiedDts) {
        this.modifiedDts = modifiedDts;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public ArrayList<OnlineQuestionObj> getTestCategories() {
        return testCategories;
    }

    public void setTestCategories(ArrayList<OnlineQuestionObj> testCategories) {
        this.testCategories = testCategories;
    }

    ArrayList<OnlineQuestionObj> testCategories;
}
