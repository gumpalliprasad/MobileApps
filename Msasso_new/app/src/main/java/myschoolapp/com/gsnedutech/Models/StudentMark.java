package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentMark {
    @SerializedName("totalQuestions")
    @Expose
    private String totalQuestions;
    @SerializedName("subjectGroup")
    @Expose
    private String subjectGroup;
    @SerializedName("Marks")
    @Expose
    private String marks;

    public String getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(String totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

}
