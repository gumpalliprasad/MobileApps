package myschoolapp.com.gsnedutech.Models.fee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TermArray implements Serializable {

    @SerializedName("termFeeAmount")
    @Expose
    private Integer termFeeAmount;
    @SerializedName("termFeeRemainingAmount")
    @Expose
    private Integer termFeeRemainingAmount;
    @SerializedName("discountAmount")
    @Expose
    private Integer discountAmount;
    @SerializedName("ccsFeeId")
    @Expose
    private Integer ccsFeeId;
    @SerializedName("termFromDate")
    @Expose
    private String termFromDate;
    @SerializedName("discountPercentage")
    @Expose
    private Integer discountPercentage;
    @SerializedName("termId")
    @Expose
    private Integer termId;
    @SerializedName("termFeeAmountPaid")
    @Expose
    private Integer termFeeAmountPaid;
    @SerializedName("termToDate")
    @Expose
    private String termToDate;
    @SerializedName("studentFeeDetailId")
    @Expose
    private Integer studentFeeDetailId;
    @SerializedName("ccsFeeTermId")
    @Expose
    private Integer ccsFeeTermId;
    @SerializedName("termFeeFinalAmount")
    @Expose
    private Integer termFeeFinalAmount;
    @SerializedName("termName")
    @Expose
    private String termName;

    public Integer getTermFeeAmount() {
        return termFeeAmount;
    }

    public void setTermFeeAmount(Integer termFeeAmount) {
        this.termFeeAmount = termFeeAmount;
    }

    public Integer getTermFeeRemainingAmount() {
        return termFeeRemainingAmount;
    }

    public void setTermFeeRemainingAmount(Integer termFeeRemainingAmount) {
        this.termFeeRemainingAmount = termFeeRemainingAmount;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Integer discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getCcsFeeId() {
        return ccsFeeId;
    }

    public void setCcsFeeId(Integer ccsFeeId) {
        this.ccsFeeId = ccsFeeId;
    }

    public String getTermFromDate() {
        return termFromDate;
    }

    public void setTermFromDate(String termFromDate) {
        this.termFromDate = termFromDate;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public Integer getTermFeeAmountPaid() {
        return termFeeAmountPaid;
    }

    public void setTermFeeAmountPaid(Integer termFeeAmountPaid) {
        this.termFeeAmountPaid = termFeeAmountPaid;
    }

    public String getTermToDate() {
        return termToDate;
    }

    public void setTermToDate(String termToDate) {
        this.termToDate = termToDate;
    }

    public Integer getStudentFeeDetailId() {
        return studentFeeDetailId;
    }

    public void setStudentFeeDetailId(Integer studentFeeDetailId) {
        this.studentFeeDetailId = studentFeeDetailId;
    }

    public Integer getCcsFeeTermId() {
        return ccsFeeTermId;
    }

    public void setCcsFeeTermId(Integer ccsFeeTermId) {
        this.ccsFeeTermId = ccsFeeTermId;
    }

    public Integer getTermFeeFinalAmount() {
        return termFeeFinalAmount;
    }

    public void setTermFeeFinalAmount(Integer termFeeFinalAmount) {
        this.termFeeFinalAmount = termFeeFinalAmount;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked = false;

}
