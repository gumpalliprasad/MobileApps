package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JEETestSectionDetail implements Serializable {
    @SerializedName("numQuestions")
    @Expose
    private String numQuestions;
    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("jeeSectionId")
    @Expose
    private String jeeSectionId;
    @SerializedName("correctAnswerMarks")
    @Expose
    private String correctAnswerMarks;
    @SerializedName("partialCorrectMarks")
    @Expose
    private String partialCorrectMarks;
    @SerializedName("marksUnattempted")
    @Expose
    private String marksUnattempted;
    @SerializedName("isOptionMarks")
    @Expose
    private String isOptionMarks;
    @SerializedName("partialWrongMarks")
    @Expose
    private String partialWrongMarks;
    @SerializedName("partialMarksAvailable")
    @Expose
    private String partialMarksAvailable;
    @SerializedName("wrongAnswerMarks")
    @Expose
    private String wrongAnswerMarks;
    @SerializedName("questionLimitCount")
    @Expose
    private String questionLimitCount="0";


    List<String> subject=new ArrayList<>();

    List<TemplateSection> section=null;

    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    public List<TemplateSection> getTemplateSection() {
        return section;
    }

    public void setSection(List<TemplateSection> section) {
        this.section = section;
    }

    public String getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(String numQuestions) {
        this.numQuestions = numQuestions;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getPartialCorrectMarks() {
        return partialCorrectMarks;
    }

    public void setPartialCorrectMarks(String partialCorrectMarks) {
        this.partialCorrectMarks = partialCorrectMarks;
    }

    public String getMarksUnattempted() {
        return marksUnattempted;
    }

    public void setMarksUnattempted(String marksUnattempted) {
        this.marksUnattempted = marksUnattempted;
    }

    public String getJeeSectionId() {
        return jeeSectionId;
    }

    public void setJeeSectionId(String jeeSectionId) {
        this.jeeSectionId = jeeSectionId;
    }

    public String getCorrectAnswerMarks() {
        return correctAnswerMarks;
    }

    public void setCorrectAnswerMarks(String correctAnswerMarks) {
        this.correctAnswerMarks = correctAnswerMarks;
    }

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public String getIsOptionMarks() {
        return isOptionMarks;
    }

    public void setIsOptionMarks(String isOptionMarks) {
        this.isOptionMarks = isOptionMarks;
    }

    public String getPartialWrongMarks() {
        return partialWrongMarks;
    }

    public void setPartialWrongMarks(String partialWrongMarks) {
        this.partialWrongMarks = partialWrongMarks;
    }

    public String getPartialMarksAvailable() {
        return partialMarksAvailable;
    }

    public void setPartialMarksAvailable(String partialMarksAvailable) {
        this.partialMarksAvailable = partialMarksAvailable;
    }

    public String getWrongAnswerMarks() {
        return wrongAnswerMarks;
    }

    public void setWrongAnswerMarks(String wrongAnswerMarks) {
        this.wrongAnswerMarks = wrongAnswerMarks;
    }

    public String getQuestionLimitCount() {
        return questionLimitCount;
    }

    public void setQuestionLimitCount(String questionLimitCount) {
        this.questionLimitCount = questionLimitCount;
    }

}
