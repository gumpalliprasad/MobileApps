package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AdminCollegeBranches implements Serializable {

    @SerializedName("branchCode")
    @Expose
    private String branchCode;
    @SerializedName("branchId")
    @Expose
    private String branchId;
    @SerializedName("branchInstType")
    @Expose
    private List<AdminBranchInstType> branchInstType = null;
    @SerializedName("branchContact")
    @Expose
    private String branchContact;
    @SerializedName("createdBy")
    @Expose
    private Integer createdBy;
    @SerializedName("branchName")
    @Expose
    private String branchName;
    @SerializedName("branchAddress")
    @Expose
    private String branchAddress;
    @SerializedName("branchEmail")
    @Expose
    private String branchEmail;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public List<AdminBranchInstType> getBranchInstType() {
        return branchInstType;
    }

    public void setBranchInstType(List<AdminBranchInstType> branchInstType) {
        this.branchInstType = branchInstType;
    }

    public String getBranchContact() {
        return branchContact;
    }

    public void setBranchContact(String branchContact) {
        this.branchContact = branchContact;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getBranchEmail() {
        return branchEmail;
    }

    public void setBranchEmail(String branchEmail) {
        this.branchEmail = branchEmail;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

}
