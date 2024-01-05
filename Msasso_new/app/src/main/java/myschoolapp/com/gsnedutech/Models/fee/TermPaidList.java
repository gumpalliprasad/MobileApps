package myschoolapp.com.gsnedutech.Models.fee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TermPaidList implements Serializable {

    @SerializedName("feeCategoryId")
    @Expose
    private Integer feeCategoryId;
    @SerializedName("ccsFeeId")
    @Expose
    private Integer ccsFeeId;
    @SerializedName("ccsFeeTermId")
    @Expose
    private Integer ccsFeeTermId;
    @SerializedName("termFeeAmountPaying")
    @Expose
    private Integer termFeeAmountPaying;
    @SerializedName("termFeeRemainingAmount")
    @Expose
    private Integer termFeeRemainingAmount;

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    private String termName;

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    private String academicYear;

    public Integer getFeeCategoryId() {
        return feeCategoryId;
    }

    public void setFeeCategoryId(Integer feeCategoryId) {
        this.feeCategoryId = feeCategoryId;
    }

    public Integer getCcsFeeId() {
        return ccsFeeId;
    }

    public void setCcsFeeId(Integer ccsFeeId) {
        this.ccsFeeId = ccsFeeId;
    }

    public Integer getCcsFeeTermId() {
        return ccsFeeTermId;
    }

    public void setCcsFeeTermId(Integer ccsFeeTermId) {
        this.ccsFeeTermId = ccsFeeTermId;
    }

    public Integer getTermFeeAmountPaying() {
        return termFeeAmountPaying;
    }

    public void setTermFeeAmountPaying(Integer termFeeAmountPaying) {
        this.termFeeAmountPaying = termFeeAmountPaying;
    }

    public Integer getTermFeeRemainingAmount() {
        return termFeeRemainingAmount;
    }

    public void setTermFeeRemainingAmount(Integer termFeeRemainingAmount) {
        this.termFeeRemainingAmount = termFeeRemainingAmount;
    }

}
