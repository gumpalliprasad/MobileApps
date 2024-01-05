package myschoolapp.com.gsnedutech.JeeAdvanced.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class AdvJeeAllSubjects implements Serializable {

    String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectImage() {
        return subjectImage;
    }

    public void setSubjectImage(String subjectImage) {
        this.subjectImage = subjectImage;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<AdvJeeQuestion> getQueresult() {
        return queresult;
    }

    public void setQueresult(ArrayList<AdvJeeQuestion> queresult) {
        this.queresult = queresult;
    }

    String subjectName;
    String description;
    String subjectImage;
    String subjectId;
    boolean status;
    ArrayList<AdvJeeQuestion> queresult;

}
