package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvJeeParagraphQues implements Serializable {


    public int getSelectOption() {
        return selectOption;
    }

    public void setSelectOption(int selectOption) {
        this.selectOption = selectOption;
    }

    int selectOption = -1;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public ArrayList<AdvJeeQueOptions> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<AdvJeeQueOptions> options) {
        this.options = options;
    }

    public String getParagraphQuesId() {
        return paragraphQuesId;
    }

    public void setParagraphQuesId(String paragraphQuesId) {
        this.paragraphQuesId = paragraphQuesId;
    }

    String paragraphQuesId;

    String marks;

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getNegativeMarks() {
        return negativeMarks;
    }

    public void setNegativeMarks(String negativeMarks) {
        this.negativeMarks = negativeMarks;
    }

    String additionalInfo;
    String negativeMarks;
    String question;
    String value;
    ArrayList<AdvJeeQueOptions> options;


}
