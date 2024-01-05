package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnlineQuestionParagraphDetails implements Serializable {
    @SerializedName("paragraph")
    @Expose
    private String paragraph;
    @SerializedName("paragraphId")
    @Expose
    private Integer paragraphId;

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public Integer getParagraphId() {
        return paragraphId;
    }

    public void setParagraphId(Integer paragraphId) {
        this.paragraphId = paragraphId;
    }
}
