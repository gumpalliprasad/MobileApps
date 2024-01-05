package myschoolapp.com.gsnedutech.descriptive.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudentAnswer implements Serializable {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("fileOrder")
    @Expose
    private Integer fileOrder;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileOrder(Integer fileOrder) {
        this.fileOrder = fileOrder;
    }

    public Integer getFileOrder() {
        return fileOrder;
    }
}
