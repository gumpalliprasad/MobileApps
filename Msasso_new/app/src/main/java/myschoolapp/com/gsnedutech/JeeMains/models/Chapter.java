package myschoolapp.com.gsnedutech.JeeMains.models;

import java.io.Serializable;

public class Chapter implements Serializable {

    String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContentMatrixId() {
        return contentMatrixId;
    }

    public void setContentMatrixId(String contentMatrixId) {
        this.contentMatrixId = contentMatrixId;
    }

    public String getChapName() {
        return chapName;
    }

    public void setChapName(String chapName) {
        this.chapName = chapName;
    }

    public String getChapDisplayName() {
        return chapDisplayName;
    }

    public void setChapDisplayName(String chapDisplayName) {
        this.chapDisplayName = chapDisplayName;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    String contentMatrixId;
    String chapName;
    String chapDisplayName;
    String createdBy;
    String creationDts;
    String modifiedBy;
    String modifiedDts;
    int __v;
    boolean status;

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    float percentage;

    int isAttempted;
    int totalNoOfQuestions;

    public int getIsAttempted() {
        return isAttempted;
    }

    public void setIsAttempted(int isAttempted) {
        this.isAttempted = isAttempted;
    }

    public int getTotalNoOfQuestions() {
        return totalNoOfQuestions;
    }

    public void setTotalNoOfQuestions(int totalNoOfQuestions) {
        this.totalNoOfQuestions = totalNoOfQuestions;
    }

    public int getNoOfQuestionsAttempted() {
        return noOfQuestionsAttempted;
    }

    public void setNoOfQuestionsAttempted(int noOfQuestionsAttempted) {
        this.noOfQuestionsAttempted = noOfQuestionsAttempted;
    }

    int noOfQuestionsAttempted;
}
