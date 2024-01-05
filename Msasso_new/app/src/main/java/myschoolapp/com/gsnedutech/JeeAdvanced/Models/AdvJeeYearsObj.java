package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvJeeYearsObj implements Serializable {

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    String _id;
    String year;
    boolean status;

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getPercentagep1() {
        return percentagep1;
    }

    public void setPercentagep1(float percentagep1) {
        this.percentagep1 = percentagep1;
    }

    public float getPercentagep2() {
        return percentagep2;
    }

    public void setPercentagep2(float percentagep2) {
        this.percentagep2 = percentagep2;
    }

    float percentage, percentagep1, percentagep2;

    public int getPracticeCount() {
        return practiceCount;
    }

    public void setPracticeCount(int practiceCount) {
        this.practiceCount = practiceCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getP1Attemted() {
        return p1Attemted;
    }

    public void setP1Attemted(int p1Attemted) {
        this.p1Attemted = p1Attemted;
    }

    public int getP1Total() {
        return p1Total;
    }

    public void setP1Total(int p1Total) {
        this.p1Total = p1Total;
    }

    public int getP2Total() {
        return p2Total;
    }

    public void setP2Total(int p2Total) {
        this.p2Total = p2Total;
    }

    public int getP2Attemted() {
        return p2Attemted;
    }

    public void setP2Attemted(int p2Attemted) {
        this.p2Attemted = p2Attemted;
    }

    int practiceCount, questionCount, p1Attemted, p1Total, p2Total, p2Attemted;

    public ArrayList<AdvJeeDisplayOrder> getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(ArrayList<AdvJeeDisplayOrder> displayOrder) {
        this.displayOrder = displayOrder;
    }

    ArrayList<AdvJeeDisplayOrder> displayOrder;

}
