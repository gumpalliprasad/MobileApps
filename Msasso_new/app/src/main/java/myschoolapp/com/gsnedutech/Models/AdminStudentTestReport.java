package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdminStudentTestReport {
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("studentMarks")
    @Expose
    private List<StudentMark> studentMarks = null;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("percentage")
    @Expose
    private Integer percentage;
    @SerializedName("studenRollnumber")
    @Expose
    private String studenRollnumber;
    @SerializedName("totalMarks")
    @Expose
    private Integer totalMarks;
    int sm=0;
    String grade="";


    public AdminStudentTestReport(String id, String sectionName, String studentName, String studentRollNum, int studentMarks, int totalMarks, String grade){
        this.studentId = id;
        this.sectionName = sectionName;
        this.studentName = studentName;
        this.studenRollnumber = studentRollNum;
        this.sm = studentMarks;
        this.totalMarks = totalMarks;
        this.grade = grade;

    }

    public int getSm() {
        return sm;
    }

    public String getGrade() {
        return grade;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<StudentMark> getStudentMarks() {
        return studentMarks;
    }

    public void setStudentMarks(List<StudentMark> studentMarks) {
        this.studentMarks = studentMarks;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public String getStudenRollnumber() {
        return studenRollnumber;
    }

    public void setStudenRollnumber(String studenRollnumber) {
        this.studenRollnumber = studenRollnumber;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }
}
