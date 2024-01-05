package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CircularTransactions implements Serializable {

    @SerializedName("transactionType")
    @Expose
    private String transactionType;
    @SerializedName("academicTransactionId")
    @Expose
    private String academicTransactionId;
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("transactionName")
    @Expose
    private String transactionName;
    @SerializedName("transactionDate")
    @Expose
    private String transactionDate;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAcademicTransactionId() {
        return academicTransactionId;
    }

    public void setAcademicTransactionId(String academicTransactionId) {
        this.academicTransactionId = academicTransactionId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
