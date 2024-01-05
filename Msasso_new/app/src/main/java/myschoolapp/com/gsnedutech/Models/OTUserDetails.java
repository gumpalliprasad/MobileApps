package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTUserDetails {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mOptedCourseId")
    @Expose
    private Integer mOptedCourseId;
    @SerializedName("mStudentId")
    @Expose
    private Integer mStudentId;
    @SerializedName("DOB")
    @Expose
    private String dOB;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("sName")
    @Expose
    private String sName;
    @SerializedName("mClassId")
    @Expose
    private Integer mClassId;
    @SerializedName("mClassName")
    @Expose
    private String mClassName;
    @SerializedName("mCourseId")
    @Expose
    private Integer mCourseId;
    @SerializedName("mBranchId")
    @Expose
    private Integer mBranchId;
    @SerializedName("admissionNo")
    @Expose
    private String admissionNo;
    @SerializedName("mCourseName")
    @Expose
    private String mCourseName;
    @SerializedName("mClassCourseId")
    @Expose
    private String mClassCourseId;
    @SerializedName("mSectionName")
    @Expose
    private String mSectionName;
    @SerializedName("mStudentPass")
    @Expose
    private String mStudentPass;
    @SerializedName("mSectionId")
    @Expose
    private Integer mSectionId;
    @SerializedName("mBranchName")
    @Expose
    private String mBranchName;
    @SerializedName("schemaName")
    @Expose
    private String schemaName;
    @SerializedName("isFirstLogin")
    @Expose
    private Integer isFirstLogin;

    @SerializedName("updatedBy")
    @Expose
    private Integer updatedBy;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMOptedCourseId() {
        return mOptedCourseId;
    }

    public void setMOptedCourseId(Integer mOptedCourseId) {
        this.mOptedCourseId = mOptedCourseId;
    }

    public Integer getMStudentId() {
        return mStudentId;
    }

    public void setMStudentId(Integer mStudentId) {
        this.mStudentId = mStudentId;
    }

    public String getDOB() {
        return dOB;
    }

    public void setDOB(String dOB) {
        this.dOB = dOB;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSName() {
        return sName;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public Integer getMClassId() {
        return mClassId;
    }

    public void setMClassId(Integer mClassId) {
        this.mClassId = mClassId;
    }

    public String getMClassName() {
        return mClassName;
    }

    public void setMClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    public Integer getMCourseId() {
        return mCourseId;
    }

    public void setMCourseId(Integer mCourseId) {
        this.mCourseId = mCourseId;
    }

    public Integer getMBranchId() {
        return mBranchId;
    }

    public void setMBranchId(Integer mBranchId) {
        this.mBranchId = mBranchId;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getMCourseName() {
        return mCourseName;
    }

    public void setMCourseName(String mCourseName) {
        this.mCourseName = mCourseName;
    }

    public String getMClassCourseId() {
        return mClassCourseId;
    }

    public void setMClassCourseId(String mClassCourseId) {
        this.mClassCourseId = mClassCourseId;
    }

    public String getMSectionName() {
        return mSectionName;
    }

    public void setMSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }

    public String getMStudentPass() {
        return mStudentPass;
    }

    public void setMStudentPass(String mStudentPass) {
        this.mStudentPass = mStudentPass;
    }

    public Integer getMSectionId() {
        return mSectionId;
    }

    public void setMSectionId(Integer mSectionId) {
        this.mSectionId = mSectionId;
    }

    public String getMBranchName() {
        return mBranchName;
    }

    public void setMBranchName(String mBranchName) {
        this.mBranchName = mBranchName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Integer getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Integer isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }


    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
