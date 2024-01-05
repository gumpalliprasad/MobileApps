package myschoolapp.com.gsnedutech.Arena.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArenaCategories implements Serializable {

    @SerializedName("arenaCategoryName")
    @Expose
    private String arenaCategoryName;
    @SerializedName("arenaCategoryDesc")
    @Expose
    private String arenaCategoryDesc;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("arenaCategoryId")
    @Expose
    private Integer arenaCategoryId;

    public String getArenaCategoryName() {
        return arenaCategoryName;
    }

    public void setArenaCategoryName(String arenaCategoryName) {
        this.arenaCategoryName = arenaCategoryName;
    }

    public String getArenaCategoryDesc() {
        return arenaCategoryDesc;
    }

    public void setArenaCategoryDesc(String arenaCategoryDesc) {
        this.arenaCategoryDesc = arenaCategoryDesc;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getArenaCategoryId() {
        return arenaCategoryId;
    }

    public void setArenaCategoryId(Integer arenaCategoryId) {
        this.arenaCategoryId = arenaCategoryId;
    }


}
