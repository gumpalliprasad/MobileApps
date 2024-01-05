package myschoolapp.com.gsnedutech.Models.fee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeeDetail implements Serializable {

    @SerializedName("termFeeRemainingAmount")
    @Expose
    private String termFeeRemainingAmount;
    @SerializedName("feeCategoryName")
    @Expose
    private String feeCategoryName;
    @SerializedName("termFeeAmount")
    @Expose
    private String termFeeAmount;
    @SerializedName("toDate")
    @Expose
    private String toDate;
    @SerializedName("academicYearId")
    @Expose
    private String academicYearId;
    @SerializedName("totalFeeAmount")
    @Expose
    private String totalFeeAmount;
    @SerializedName("discountAmount")
    @Expose
    private String discountAmount;
    @SerializedName("ccsFeeId")
    @Expose
    private String ccsFeeId;
    @SerializedName("discoutCategoryId")
    @Expose
    private String discoutCategoryId;
    @SerializedName("isHavingTerms")
    @Expose
    private String isHavingTerms;
    @SerializedName("discountPercentage")
    @Expose
    private String discountPercentage;
    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("termFromDate")
    @Expose
    private String termFromDate;
    @SerializedName("termFeeAmountPaid")
    @Expose
    private String termFeeAmountPaid;
    @SerializedName("termToDate")
    @Expose
    private String termToDate;
    @SerializedName("feeCategoryTermName")
    @Expose
    private String feeCategoryTermName;
    @SerializedName("studentFeeDetailId")
    @Expose
    private String studentFeeDetailId;
    @SerializedName("ccsFeeTermId")
    @Expose
    private String ccsFeeTermId;
    @SerializedName("termFeeFinalAmount")
    @Expose
    private String termFeeFinalAmount;
    @SerializedName("academicYearName")
    @Expose
    private String academicYearName;
    @SerializedName("finalFeeAmount")
    @Expose
    private String finalFeeAmount;
    @SerializedName("lastDate")
    @Expose
    private String lastDate;
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked  = false;


    public String getTermFeeRemainingAmount() {
        return termFeeRemainingAmount;
    }

    public void setTermFeeRemainingAmount(String termFeeRemainingAmount) {
        this.termFeeRemainingAmount = termFeeRemainingAmount;
    }

    public String getFeeCategoryName() {
        return feeCategoryName;
    }

    public void setFeeCategoryName(String feeCategoryName) {
        this.feeCategoryName = feeCategoryName;
    }

    public String getTermFeeAmount() {
        return termFeeAmount;
    }

    public void setTermFeeAmount(String termFeeAmount) {
        this.termFeeAmount = termFeeAmount;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(String academicYearId) {
        this.academicYearId = academicYearId;
    }

    public String getTotalFeeAmount() {
        return totalFeeAmount;
    }

    public void setTotalFeeAmount(String totalFeeAmount) {
        this.totalFeeAmount = totalFeeAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getCcsFeeId() {
        return ccsFeeId;
    }

    public void setCcsFeeId(String ccsFeeId) {
        this.ccsFeeId = ccsFeeId;
    }

    public String getDiscoutCategoryId() {
        return discoutCategoryId;
    }

    public void setDiscoutCategoryId(String discoutCategoryId) {
        this.discoutCategoryId = discoutCategoryId;
    }

    public String getIsHavingTerms() {
        return isHavingTerms;
    }

    public void setIsHavingTerms(String isHavingTerms) {
        this.isHavingTerms = isHavingTerms;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getTermFromDate() {
        return termFromDate;
    }

    public void setTermFromDate(String termFromDate) {
        this.termFromDate = termFromDate;
    }

    public String getTermFeeAmountPaid() {
        return termFeeAmountPaid;
    }

    public void setTermFeeAmountPaid(String termFeeAmountPaid) {
        this.termFeeAmountPaid = termFeeAmountPaid;
    }

    public String getTermToDate() {
        return termToDate;
    }

    public void setTermToDate(String termToDate) {
        this.termToDate = termToDate;
    }

    public String getFeeCategoryTermName() {
        return feeCategoryTermName;
    }

    public void setFeeCategoryTermName(String feeCategoryTermName) {
        this.feeCategoryTermName = feeCategoryTermName;
    }

    public String getStudentFeeDetailId() {
        return studentFeeDetailId;
    }

    public void setStudentFeeDetailId(String studentFeeDetailId) {
        this.studentFeeDetailId = studentFeeDetailId;
    }

    public String getCcsFeeTermId() {
        return ccsFeeTermId;
    }

    public void setCcsFeeTermId(String ccsFeeTermId) {
        this.ccsFeeTermId = ccsFeeTermId;
    }

    public String getTermFeeFinalAmount() {
        return termFeeFinalAmount;
    }

    public void setTermFeeFinalAmount(String termFeeFinalAmount) {
        this.termFeeFinalAmount = termFeeFinalAmount;
    }

    public String getAcademicYearName() {
        return academicYearName;
    }

    public void setAcademicYearName(String academicYearName) {
        this.academicYearName = academicYearName;
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


}
