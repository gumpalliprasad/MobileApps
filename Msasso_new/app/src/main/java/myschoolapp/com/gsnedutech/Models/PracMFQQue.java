/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PracMFQQue implements Serializable {
    @SerializedName("questionMapid")
    @Expose
    private String questionMapid;
    @SerializedName("ColumnB")
    @Expose
    private String columnB;
    @SerializedName("ColumnBIndex")
    @Expose
    private String columnBIndex;
    @SerializedName("ColumnAIndex")
    @Expose
    private Integer columnAIndex;
    @SerializedName("correctAnswer")
    @Expose
    private String correctAnswer;
    @SerializedName("ColumnA")
    @Expose
    private String columnA;

    public String getQuestionMapid() {
        return questionMapid;
    }

    public void setQuestionMapid(String questionMapid) {
        this.questionMapid = questionMapid;
    }

    public String getColumnB() {
        return columnB;
    }

    public void setColumnB(String columnB) {
        this.columnB = columnB;
    }

    public String getColumnBIndex() {
        return columnBIndex;
    }

    public void setColumnBIndex(String columnBIndex) {
        this.columnBIndex = columnBIndex;
    }

    public Integer getColumnAIndex() {
        return columnAIndex;
    }

    public void setColumnAIndex(Integer columnAIndex) {
        this.columnAIndex = columnAIndex;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getColumnA() {
        return columnA;
    }

    public void setColumnA(String columnA) {
        this.columnA = columnA;
    }

}
