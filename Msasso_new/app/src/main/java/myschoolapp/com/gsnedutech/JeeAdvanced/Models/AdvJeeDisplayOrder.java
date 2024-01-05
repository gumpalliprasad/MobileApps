package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvJeeDisplayOrder implements Serializable {

    int paper;

    public int getPaper() {
        return paper;
    }

    public void setPaper(int paper) {
        this.paper = paper;
    }

    public ArrayList<AdvJeeSubOrder> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<AdvJeeSubOrder> subjects) {
        this.subjects = subjects;
    }

    ArrayList<AdvJeeSubOrder> subjects;
}
