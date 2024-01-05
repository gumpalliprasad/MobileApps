package myschoolapp.com.gsnedutech.Models.fee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FeeData implements Serializable {

    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("feeCategoryId")
    @Expose
    private Integer feeCategoryId;
    @SerializedName("feeCategoryName")
    @Expose
    private String feeCategoryName;
    @SerializedName("totalFeeAmout")
    @Expose
    private Integer totalFeeAmout;
    @SerializedName("termArray")
    @Expose
    private List<TermArray> termArray = null;
    @SerializedName("feeCatgoryDesc")
    @Expose
    private String feeCatgoryDesc;
    @SerializedName("feeAmountRemaining")
    @Expose
    private Integer feeAmountRemaining;
    @SerializedName("toDate")
    @Expose
    private String toDate;
    @SerializedName("ccsFeeId")
    @Expose
    private Integer ccsFeeId;
    @SerializedName("finalFeeAmount")
    @Expose
    private String finalFeeAmount;
    @SerializedName("lastDate")
    @Expose
    private String lastDate;
    @SerializedName("termFeeRemainingAmount")
    @Expose
    private String termFeeRemainingAmount;
    @SerializedName("termFeeAmountPaid")
    @Expose
    private String termFeeAmountPaid;
    @SerializedName("studentFeeDetailId")
    @Expose
    private String studentFeeDetailId;

    public String getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(String academicYearId) {
        this.academicYearId = academicYearId;
    }

    @SerializedName("academicYearId")
    @Expose
    private String academicYearId;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public Integer getFeeCategoryId() {
        return feeCategoryId;
    }

    public void setFeeCategoryId(Integer feeCategoryId) {
        this.feeCategoryId = feeCategoryId;
    }

    public String getFeeCategoryName() {
        return feeCategoryName;
    }

    public void setFeeCategoryName(String feeCategoryName) {
        this.feeCategoryName = feeCategoryName;
    }

    public Integer getTotalFeeAmout() {
        return totalFeeAmout;
    }

    public void setTotalFeeAmout(Integer totalFeeAmout) {
        this.totalFeeAmout = totalFeeAmout;
    }

    public List<TermArray> getTermArray() {
        return termArray;
    }

    public void setTermArray(List<TermArray> termArray) {
        this.termArray = termArray;
    }

    public String getFeeCatgoryDesc() {
        return feeCatgoryDesc;
    }

    public void setFeeCatgoryDesc(String feeCatgoryDesc) {
        this.feeCatgoryDesc = feeCatgoryDesc;
    }

    public Integer getFeeAmountRemaining() {
        return feeAmountRemaining;
    }

    public void setFeeAmountRemaining(Integer feeAmountRemaining) {
        this.feeAmountRemaining = feeAmountRemaining;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getCcsFeeId() {
        return ccsFeeId;
    }

    public void setCcsFeeId(Integer ccsFeeId) {
        this.ccsFeeId = ccsFeeId;
    }

    public String getFinalFeeAmount() {
        return finalFeeAmount;
    }

    public void setFinalFeeAmount(String finalFeeAmount) {
        this.finalFeeAmount = finalFeeAmount;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getTermFeeRemainingAmount() {
        return termFeeRemainingAmount;
    }

    public void setTermFeeRemainingAmount(String termFeeRemainingAmount) {
        this.termFeeRemainingAmount = termFeeRemainingAmount;
    }

    public String getTermFeeAmountPaid() {
        return termFeeAmountPaid;
    }

    public void setTermFeeAmountPaid(String termFeeAmountPaid) {
        this.termFeeAmountPaid = termFeeAmountPaid;
    }

    public String getStudentFeeDetailId() {
        return studentFeeDetailId;
    }

    public void setStudentFeeDetailId(String studentFeeDetailId) {
        this.studentFeeDetailId = studentFeeDetailId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked  = false;

}
