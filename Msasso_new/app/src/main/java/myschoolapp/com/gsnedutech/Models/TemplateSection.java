package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TemplateSection implements Serializable {

    @SerializedName("sectionName")
    @Expose
    private String sectionName;
    @SerializedName("SectionsDesc")
    @Expose
    private String sectionsDesc;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionsDesc() {
        return sectionsDesc;
    }

    public void setSectionsDesc(String sectionsDesc) {
        this.sectionsDesc = sectionsDesc;
    }
}
