package myschoolapp.com.gsnedutech.Arena.Models;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArQuizObject implements Serializable {

    String quizType;
    String questionType;
    String quizTitle;
    String quizDesc;
    String arenaId;
    int colorPos,numberOfQuestions,timeforQuestion;
    Uri coverImage = null;
    String coverImagePath = "NA";


    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }


    public String getQuizDesc() {
        return quizDesc;
    }

    public void setQuizDesc(String quizDesc) {
        this.quizDesc = quizDesc;
    }

    public Uri getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Uri coverImage) {
        this.coverImage = coverImage;
    }


    List<ArQuizMCQ> listMCQ = new ArrayList<>();

    public List<ArQuizMCQ> getListMCQ() {
        return listMCQ;
    }

    public void setListMCQ(List<ArQuizMCQ> listMCQ) {
        this.listMCQ = listMCQ;
    }

    public String getQuizType() {
        return quizType;
    }

    public String getArenaId() {
        return arenaId;
    }

    public void setArenaId(String arenaId) {
        this.arenaId = arenaId;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getColorPos() {
        return colorPos;
    }

    public void setColorPos(int colorPos) {
        this.colorPos = colorPos;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getTimeforQuestion() {
        return timeforQuestion;
    }

    public void setTimeforQuestion(int timeforQuestion) {
        this.timeforQuestion = timeforQuestion;
    }





}
