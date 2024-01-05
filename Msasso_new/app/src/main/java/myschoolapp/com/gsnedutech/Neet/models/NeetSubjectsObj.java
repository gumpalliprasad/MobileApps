package myschoolapp.com.gsnedutech.Neet.models;

import java.util.ArrayList;

public class NeetSubjectsObj {
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

    public String getSubjectImage() {
        return subjectImage;
    }

    public void setSubjectImage(String subjectImage) {
        this.subjectImage = subjectImage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public ArrayList<ContentMatrices> getContentmatricese() {
        return contentmatriceses;
    }

    public void setContentmatricese(ArrayList<ContentMatrices> contentmatriceses) {
        this.contentmatriceses = contentmatriceses;
    }

    String _id, subjectName, subjectImage;
    boolean status;
    ArrayList<ContentMatrices> contentmatriceses;
}

