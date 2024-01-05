package myschoolapp.com.gsnedutech.Arena.Models;

import android.net.Uri;

public class ArQuizMCQ {

    String question = "";
    String questionType = "";
    Uri quesImage = null;
    String quesImageFilepath = "NA";


    String questionTime = "";

    String optionA = "";
    Uri optionAImage = null;
    String optionAImagePath = "NA";

    String optionB = "";
    Uri OptionBImage = null;
    String optionBImagePath = "NA";

    String optionC = "";
    Uri OptionCImage = null;
    String optionCImagePath = "NA";

    String optionD = "";
    Uri OptionDImage = null;
    String optionDImagePath = "NA";

    String optionTrue = "True";
    String optionFalse  = "False";

    String isCorrect = "";


    public String getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(String questionTime) {
        this.questionTime = questionTime;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getOptionTrue() {
        return optionTrue;
    }

    public void setOptionTrue(String optionTrue) {
        this.optionTrue = optionTrue;
    }

    public String getOptionFalse() {
        return optionFalse;
    }

    public void setOptionFalse(String optionFalse) {
        this.optionFalse = optionFalse;
    }

    public String getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(String isCorrect) {
        this.isCorrect = isCorrect;
    }



    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Uri getQuesImage() {
        return quesImage;
    }

    public void setQuesImage(Uri quesImage) {
        this.quesImage = quesImage;
    }

    public String getQuesImageFilepath() {
        return quesImageFilepath;
    }

    public void setQuesImageFilepath(String quesImageFilepath) {
        this.quesImageFilepath = quesImageFilepath;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public Uri getOptionAImage() {
        return optionAImage;
    }

    public void setOptionAImage(Uri optionAImage) {
        this.optionAImage = optionAImage;
    }

    public String getOptionAImagePath() {
        return optionAImagePath;
    }

    public void setOptionAImagePath(String optionAImagePath) {
        this.optionAImagePath = optionAImagePath;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public Uri getOptionBImage() {
        return OptionBImage;
    }

    public void setOptionBImage(Uri optionBImage) {
        OptionBImage = optionBImage;
    }

    public String getOptionBImagePath() {
        return optionBImagePath;
    }

    public void setOptionBImagePath(String optionBImagePath) {
        this.optionBImagePath = optionBImagePath;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public Uri getOptionCImage() {
        return OptionCImage;
    }

    public void setOptionCImage(Uri optionCImage) {
        OptionCImage = optionCImage;
    }

    public String getOptionCImagePath() {
        return optionCImagePath;
    }

    public void setOptionCImagePath(String optionCImagePath) {
        this.optionCImagePath = optionCImagePath;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public Uri getOptionDImage() {
        return OptionDImage;
    }

    public void setOptionDImage(Uri optionDImage) {
        OptionDImage = optionDImage;
    }

    public String getOptionDImagePath() {
        return optionDImagePath;
    }

    public void setOptionDImagePath(String optionDImagePath) {
        this.optionDImagePath = optionDImagePath;
    }
}
