package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KhubImagePptPdfModels implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("heading")
    @Expose
    private String heading;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("hyperText")
    @Expose
    private String hyperText;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("path")
    @Expose
    private String path;

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

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHyperText() {
        return hyperText;
    }

    public void setHyperText(String hyperText) {
        this.hyperText = hyperText;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
