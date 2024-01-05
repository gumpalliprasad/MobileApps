package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;

public class AdvJeeITQCorrectAnswer implements Serializable {
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    String answer, explanation;
}
