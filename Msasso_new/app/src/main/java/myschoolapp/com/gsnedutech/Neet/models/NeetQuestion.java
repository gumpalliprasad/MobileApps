package myschoolapp.com.gsnedutech.Neet.models;

import java.io.Serializable;
import java.util.ArrayList;

public class NeetQuestion implements Serializable {

    ArrayList<String> qAppearace;
    ArrayList<NeetQueOptions> qOptions;
//            "possibleCases": [],
    String _id;

    public ArrayList<String> getqAppearace() {
        return qAppearace;
    }

    public void setqAppearace(ArrayList<String> qAppearace) {
        this.qAppearace = qAppearace;
    }

    public ArrayList<NeetQueOptions> getqOptions() {
        return qOptions;
    }

    public void setqOptions(ArrayList<NeetQueOptions> qOptions) {
        this.qOptions = qOptions;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }

    public String getContentMatrixId() {
        return contentMatrixId;
    }

    public void setContentMatrixId(String contentMatrixId) {
        this.contentMatrixId = contentMatrixId;
    }

    public String getqType() {
        return qType;
    }

    public void setqType(String qType) {
        this.qType = qType;
    }

    public int getqMarks() {
        return qMarks;
    }

    public void setqMarks(int qMarks) {
        this.qMarks = qMarks;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPossibleCasesContent() {
        return possibleCasesContent;
    }

    public void setPossibleCasesContent(String possibleCasesContent) {
        this.possibleCasesContent = possibleCasesContent;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreationDts() {
        return creationDts;
    }

    public void setCreationDts(String creationDts) {
        this.creationDts = creationDts;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedDts() {
        return modifiedDts;
    }

    public void setModifiedDts(String modifiedDts) {
        this.modifiedDts = modifiedDts;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getqHint() {
        return qHint;
    }
    public void setqHint(String qHint) {
        this.qHint = qHint;
    }

    String qHint;
    String chapterId;
    String qName;
    String contentMatrixId;
    String qType;
    int qMarks;
    boolean status;
    String possibleCasesContent, additionalInfo, createdBy, creationDts, modifiedBy, modifiedDts;
    int __v;

    public int getNoOfAttempts() {
        return noOfAttempts;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        this.noOfAttempts = noOfAttempts;
    }

    public int getNoOfRights() {
        return noOfRights;
    }

    public void setNoOfRights(int noOfRights) {
        this.noOfRights = noOfRights;
    }

    public int getNoOfWrongs() {
        return noOfWrongs;
    }

    public void setNoOfWrongs(int noOfWrongs) {
        this.noOfWrongs = noOfWrongs;
    }

    public int getIsAttempted() {
        return isAttempted;
    }

    public void setIsAttempted(int isAttempted) {
        this.isAttempted = isAttempted;
    }

    int noOfAttempts, noOfRights, noOfWrongs, isAttempted;


}
