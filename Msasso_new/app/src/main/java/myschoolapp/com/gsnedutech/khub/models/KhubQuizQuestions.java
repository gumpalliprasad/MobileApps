package myschoolapp.com.gsnedutech.khub.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KhubQuizQuestions {
    @SerializedName("possibleAnswers")
    @Expose
    private List<String> possibleAnswers = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("pId")
    @Expose
    private String pId;
    @SerializedName("qType")
    @Expose
    private String qType;
    @SerializedName("qStatus")
    @Expose
    private String qStatus;
    @SerializedName("qLevel")
    @Expose
    private String qLevel;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("options")
    @Expose
    private List<KhubOption> KhubOptions = null;
    @SerializedName("trueOrFalse")
    @Expose
    private String trueOrFalse;
    @SerializedName("qMarks")
    @Expose
    private Integer qMarks;
    @SerializedName("explanation")
    @Expose
    private String explanation;
    @SerializedName("modId")
    @Expose
    private String modId;
    @SerializedName("moduleContentId")
    @Expose
    private String moduleContentId;
    @SerializedName("hint")
    @Expose
    private String hint;
    @SerializedName("QuestionAnswer")
    @Expose
    private String questionAnswer;

    int selectedOption = 0;

    String selectedAnswer="";

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }


    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(List<String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getQType() {
        return qType;
    }

    public void setQType(String qType) {
        this.qType = qType;
    }

    public String getQStatus() {
        return qStatus;
    }

    public void setQStatus(String qStatus) {
        this.qStatus = qStatus;
    }

    public String getQLevel() {
        return qLevel;
    }

    public void setQLevel(String qLevel) {
        this.qLevel = qLevel;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<KhubOption> getKhubOptions() {
        return KhubOptions;
    }

    public void setKhubOptions(List<KhubOption> KhubOptions) {
        this.KhubOptions = KhubOptions;
    }

    public String getTrueOrFalse() {
        return trueOrFalse;
    }

    public void setTrueOrFalse(String trueOrFalse) {
        this.trueOrFalse = trueOrFalse;
    }

    public Integer getQMarks() {
        return qMarks;
    }

    public void setQMarks(Integer qMarks) {
        this.qMarks = qMarks;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getModuleContentId() {
        return moduleContentId;
    }

    public void setModuleContentId(String moduleContentId) {
        this.moduleContentId = moduleContentId;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    boolean isCorrect = false;

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }
}
