package myschoolapp.com.gsnedutech.QBox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchQbox {

    @SerializedName("stuQboxId")
    @Expose
    private String stuQboxId;
    @SerializedName("qboxQuestion")
    @Expose
    private String qboxQuestion;

    public void setQboxQuestion(String qboxQuestion) {
        this.qboxQuestion = qboxQuestion;
    }

    public void setStuQboxId(String stuQboxId) {
        this.stuQboxId = stuQboxId;
    }

    public String getQboxQuestion() {
        return qboxQuestion;
    }

    public String getStuQboxId() {
        return stuQboxId;
    }
}
