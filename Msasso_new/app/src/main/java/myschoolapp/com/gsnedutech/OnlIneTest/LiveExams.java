package myschoolapp.com.gsnedutech.OnlIneTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LiveExams implements Serializable {
    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int getMyExamId() {
        return myExamId;
    }

    public void setMyExamId(int myExamId) {
        this.myExamId = myExamId;
    }

    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }



    public int geteCatId() {
        return eCatId;
    }

    public void seteCatId(int eCatId) {
        this.eCatId = eCatId;
    }

    public String geteCatName() {
        return eCatName;
    }

    public void seteCatName(String eCatName) {
        this.eCatName = eCatName;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

    public String getePath() {
        return ePath;
    }

    public void setePath(String ePath) {
        this.ePath = ePath;
    }

    String eName;
    int myExamId;
    String eStatus;

    public int geteDuration() {
        return eDuration;
    }

    public void seteDuration(int eDuration) {
        this.eDuration = eDuration;
    }

    int eDuration;
    int eCatId;
    String eCatName;
    String sTime="";
    String eTime="";
    String rDate="";
    String ePath;
    public String getJeeSectionTemplate() {
        return jeeSectionTemplate;
    }

    public void setJeeSectionTemplate(String jeeSectionTemplate) {
        this.jeeSectionTemplate = jeeSectionTemplate;
    }
    @SerializedName("jeeSectionTemplate")
    @Expose
    String jeeSectionTemplate = "NA";
    int cMarks;

    public int getcMarks() {
        return cMarks;
    }

    public void setcMarks(int cMarks) {
        this.cMarks = cMarks;
    }

    public int getwMarks() {
        return wMarks;
    }

    public void setwMarks(int wMarks) {
        this.wMarks = wMarks;
    }

    int wMarks;

    public String getStudentEStatus() {
        return studentEStatus;
    }

    public void setStudentEStatus(String studentEStatus) {
        this.studentEStatus = studentEStatus;
    }

    String studentEStatus = "";

    public String getStartTestName() {
        return startTestName;
    }

    public void setStartTestName(String startTestName) {
        this.startTestName = startTestName;
    }

    String startTestName = "";
}
