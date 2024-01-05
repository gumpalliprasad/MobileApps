package myschoolapp.com.gsnedutech.JeeMains.models;

import java.io.Serializable;

public class CorrectAnswer implements Serializable {
    float answer;

    public float getAnswer() {
        return answer;
    }

    public void setAnswer(float answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    String explanation;
}
