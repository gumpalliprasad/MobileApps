package myschoolapp.com.gsnedutech.QBox.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class QboxReply implements Serializable
{

    @SerializedName("qboxAnswer")
    @Expose
    private String qboxAnswer;
    @SerializedName("replies")
    @Expose
    private List<Reply> replies = null;
    @SerializedName("stuqboxId")
    @Expose
    private Integer stuqboxId;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("teacherqboxId")
    @Expose
    private Integer teacherqboxId;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public String getQboxAnswer() {
        return qboxAnswer;
    }

    public void setQboxAnswer(String qboxAnswer) {
        this.qboxAnswer = qboxAnswer;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public Integer getStuqboxId() {
        return stuqboxId;
    }

    public void setStuqboxId(Integer stuqboxId) {
        this.stuqboxId = stuqboxId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getTeacherqboxId() {
        return teacherqboxId;
    }

    public void setTeacherqboxId(Integer teacherqboxId) {
        this.teacherqboxId = teacherqboxId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
