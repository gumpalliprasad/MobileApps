package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContentObject implements Serializable {
    @SerializedName("MESSAGE")
    String MESSAGE;
    @SerializedName("topicId")
    String topicId;
    @SerializedName("TextBookContent")
    String TextBookContent;
    @SerializedName("STATUS")
    String STATUS;
    @SerializedName("topicName")
    String topicName;

    public List<ContentDetails> getImportantContentList() {
        return ImportantContentList;
    }

    public void setImportantContentList(List<ContentDetails> importantContentList) {
        ImportantContentList = importantContentList;
    }

    public List<ContentDetails> getTextbookContentList() {
        return textbookContentList;
    }

    public void setTextbookContentList(List<ContentDetails> textbookContentList) {
        this.textbookContentList = textbookContentList;
    }

    @SerializedName("importantContent")
    List<ContentDetails> ImportantContentList;
    @SerializedName("StatusCode")
    String StatusCode;
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

    @SerializedName("textbookContent")
    List<ContentDetails> textbookContentList;

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTextBookContent() {
        return TextBookContent;
    }

    public void setTextBookContent(String textBookContent) {
        TextBookContent = textBookContent;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(String statusCode) {
        StatusCode = statusCode;
    }

    public String getQuestionAndAnswers() {
        return QuestionAndAnswers;
    }

    public void setQuestionAndAnswers(String questionAndAnswers) {
        QuestionAndAnswers = questionAndAnswers;
    }

    @SerializedName("QuestionAndAnswers")
    String QuestionAndAnswers;
}
