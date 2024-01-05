package myschoolapp.com.gsnedutech.Neet.models;

public class NeetSubmitExpectedQuestion {

    String _id;
    String uid;
    String userId;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getQuestion() {
        return questionRefId;
    }

    public void setQuestion(String question) {
        this.questionRefId = question;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    String chapterId;
    String questionRefId;
    String selectedOption;

    public String getContentMatrixId() {
        return contentMatrixId;
    }

    public void setContentMatrixId(String contentMatrixId) {
        this.contentMatrixId = contentMatrixId;
    }

    String contentMatrixId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    String questionId;

    public String getQuestionRefId() {
        return questionRefId;
    }

    public void setQuestionRefId(String questionRefId) {
        this.questionRefId = questionRefId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    boolean isCorrect;
}
