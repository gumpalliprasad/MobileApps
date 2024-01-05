package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminBranchInstType implements Serializable {
    @SerializedName("instType")
    @Expose
    private String instType;
    @SerializedName("instTypeId")
    @Expose
    private Integer instTypeId;

    public String getInstType() {
        return instType;
    }

    public void setInstType(String instType) {
        this.instType = instType;
    }

    public Integer getInstTypeId() {
        return instTypeId;
    }

    public void setInstTypeId(Integer instTypeId) {
        this.instTypeId = instTypeId;
    }

}
