
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


@SuppressWarnings("unused")
public class TopicQAObj implements Serializable {

    @SerializedName("answer")
    @Expose
    private String mAnswer;
    @SerializedName("difficultyLevel")
    @Expose
    private String mDifficultyLevel;
    @SerializedName("question")
    @Expose
    private String mQuestion;

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }

    public String getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion(String question) {
        mQuestion = question;
    }

}
