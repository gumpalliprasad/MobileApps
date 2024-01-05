package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TemplateInstructions implements Serializable {
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("Sections")
    @Expose
    private List<TemplateSection> sections = null;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<TemplateSection> getSections() {
        return sections;
    }

    public void setSections(List<TemplateSection> sections) {
        this.sections = sections;
    }

}
