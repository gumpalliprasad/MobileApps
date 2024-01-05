package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DescCategory implements Serializable {

    @SerializedName("question_number")
    @Expose
    private Integer questionNumber;
    @SerializedName("questionId")
    @Expose
    private Integer questionId;
    @SerializedName("examSectionId")
    @Expose
    private int examSectionId;
    @SerializedName("studentAnswer")
    @Expose
    private List<StudentAnswer> studentAnswer = null;
    @SerializedName("marks")
    @Expose
    private Integer marks;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("resultMarks")
    @Expose
    private float resultMarks;

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public int getExamSectionId() {
        return examSectionId;
    }

    public void setExamSectionId(int examSectionId) {
        this.examSectionId = examSectionId;
    }

    public List<StudentAnswer> getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(List<StudentAnswer> studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setResultMarks(float resultMarks) {
        this.resultMarks = resultMarks;
    }

    public float getResultMarks() {
        return resultMarks;
    }
}

