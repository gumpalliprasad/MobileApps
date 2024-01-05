package myschoolapp.com.gsnedutech.Flip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FlipObj implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("modId")
    @Expose
    private String modId;
    @SerializedName("courseId")
    @Expose
    private String courseId;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("hyperText")
    @Expose
    private String hyperText;
    @SerializedName("path")
    @Expose
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public String getHyperText() {
        return hyperText;
    }

    public void setHyperText(String hyperText) {
        this.hyperText = hyperText;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
