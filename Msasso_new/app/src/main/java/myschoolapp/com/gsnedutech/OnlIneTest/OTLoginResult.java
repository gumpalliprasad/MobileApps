package myschoolapp.com.gsnedutech.OnlIneTest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OTLoginResult implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mStudentId")
    @Expose
    private Integer mStudentId;
    @SerializedName("sName")
    @Expose
    private String sName;
    @SerializedName("admissionNo")
    @Expose
    private String admissionNo;
    @SerializedName("mClassId")
    @Expose
    private Integer mClassId;
    @SerializedName("mClassName")
    @Expose
    private String mClassName;
    @SerializedName("mCourseId")
    @Expose
    private Integer mCourseId;
    @SerializedName("mCourseName")
    @Expose
    private String mCourseName;
    @SerializedName("mOptedCourseId")
    @Expose
    private String mOptedCourseId;
    @SerializedName("mBranchId")
    @Expose
    private String mBranchId;

    public String getmStudentPass() {
        return mStudentPass;
    }

    public void setmStudentPass(String mStudentPass) {
        this.mStudentPass = mStudentPass;
    }

    @SerializedName("mStudentPass")
    @Expose
    private String mStudentPass;
    @SerializedName("mBranchName")
    @Expose
    private String mBranchName;
    @SerializedName("liveExams")
    @Expose
    private ArrayList<LiveExams> liveExams;
    @SerializedName("schemaName")
    @Expose
    private String schemaName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMStudentId() {
        return mStudentId;
    }

    public void setMStudentId(Integer mStudentId) {
        this.mStudentId = mStudentId;
    }

    public String getSName() {
        return sName;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
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

    public String getMCourseName() {
        return mCourseName;
    }

    public void setMCourseName(String mCourseName) {
        this.mCourseName = mCourseName;
    }

    public String getMOptedCourseId() {
        return mOptedCourseId;
    }

    public void setMOptedCourseId(String mOptedCourseId) {
        this.mOptedCourseId = mOptedCourseId;
    }

    public String getMBranchId() {
        return mBranchId;
    }

    public void setMBranchId(String mBranchId) {
        this.mBranchId = mBranchId;
    }

    public String getMBranchName() {
        return mBranchName;
    }

    public void setMBranchName(String mBranchName) {
        this.mBranchName = mBranchName;
    }

    public ArrayList<LiveExams> getLiveExams() {
        return liveExams;
    }

    public void setLiveExams(ArrayList<LiveExams> liveExams) {
        this.liveExams = liveExams;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @SerializedName("mSectionId")
    @Expose
    private String mSectionId;

    public String getmSectionId() {
        return mSectionId;
    }

    public void setmSectionId(String mSectionId) {
        this.mSectionId = mSectionId;
    }


}
