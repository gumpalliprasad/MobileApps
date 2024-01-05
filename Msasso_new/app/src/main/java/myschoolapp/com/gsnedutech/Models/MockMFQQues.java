
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


public class MockMFQQues implements Serializable {

    @SerializedName("ColumnA")
    @Expose
    private String mColumnA;
    @SerializedName("ColumnAIndex")
    @Expose
    private Long mColumnAIndex;
    @SerializedName("ColumnB")
    @Expose
    private String mColumnB;
    @SerializedName("ColumnBIndex")
    @Expose
    private String mColumnBIndex;
    @SerializedName("correctAnswer")
    @Expose
    private String mCorrectAnswer;
    @SerializedName("questionMapid")
    @Expose
    private String mQuestionMapid;

    public String getColumnA() {
        return mColumnA;
    }

    public void setColumnA(String columnA) {
        mColumnA = columnA;
    }

    public Long getColumnAIndex() {
        return mColumnAIndex;
    }

    public void setColumnAIndex(Long columnAIndex) {
        mColumnAIndex = columnAIndex;
    }

    public String getColumnB() {
        return mColumnB;
    }

    public void setColumnB(String columnB) {
        mColumnB = columnB;
    }

    public String getColumnBIndex() {
        return mColumnBIndex;
    }

    public void setColumnBIndex(String columnBIndex) {
        mColumnBIndex = columnBIndex;
    }

    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        mCorrectAnswer = correctAnswer;
    }

    public String getQuestionMapid() {
        return mQuestionMapid;
    }

    public void setQuestionMapid(String questionMapid) {
        mQuestionMapid = questionMapid;
    }

}
