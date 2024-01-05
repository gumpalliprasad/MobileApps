package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KhubBlankTextObj {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("desc")
    @Expose
    private String desc;
    private final static long serialVersionUID = -87711197027022055L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
