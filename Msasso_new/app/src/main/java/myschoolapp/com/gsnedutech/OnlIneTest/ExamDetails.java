package myschoolapp.com.gsnedutech.OnlIneTest;

import java.io.Serializable;

public class ExamDetails implements Serializable {
        int examId;

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getqCount() {
        return qCount;
    }

    public void setqCount(int qCount) {
        this.qCount = qCount;
    }

    public int getwMarks() {
        return wMarks;
    }

    public void setwMarks(int wMarks) {
        this.wMarks = wMarks;
    }

    public int getcMarks() {
        return cMarks;
    }

    public void setcMarks(int cMarks) {
        this.cMarks = cMarks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getExmaName() {
        return exmaName;
    }

    public void setExmaName(String exmaName) {
        this.exmaName = exmaName;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public String getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(String examDuration) {
        this.examDuration = examDuration;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getExamResultDate() {
        return examResultDate;
    }

    public void setExamResultDate(String examResultDate) {
        this.examResultDate = examResultDate;
    }

    int qCount;
    int wMarks;
    int cMarks;
    int totalMarks;
        String exmaName;
    String examStatus;
    String examDuration;
    String startDate;
    String endDate;
    String examResultDate;

    public String getExamPath() {
        return examPath;
    }

    public void setExamPath(String examPath) {
        this.examPath = examPath;
    }

    String examPath;


}
