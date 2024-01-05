package myschoolapp.com.gsnedutech.Models;

public class AssignmentsObj {

    String createdDate,submissionDate,subject,chapter;
    boolean submitted;
    boolean video;
    boolean key;
    boolean review;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }




    public AssignmentsObj(String createdDate, String submissionDate, String subject, String chapter, boolean submitted, boolean video, boolean key, boolean review) {
        this.createdDate = createdDate;
        this.submissionDate = submissionDate;
        this.subject = subject;
        this.chapter = chapter;
        this.submitted = submitted;
        this.video = video;
        this.key = key;
        this.review = review;
    }




}
