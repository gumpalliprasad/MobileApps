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

public class TeacherHWStudentofHWObj implements Serializable {
    @SerializedName("HWStatus")
    @Expose
    private String hWStatus;
    @SerializedName("teacherComments")
    @Expose
    private String teacherComments;
    @SerializedName("homeworkSubmitDate")
    @Expose
    private String homeworkSubmitDate;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("HWAssigned")
    @Expose
    private Integer hWAssigned;
    @SerializedName("studentHWId")
    @Expose
    private Integer studentHWId;
    @SerializedName("marks")
    @Expose
    private Integer marks;
    @SerializedName("version")
    @Expose
    private Integer version;
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("hwRating")
    @Expose
    private Integer hwRating;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("rollNumber")
    @Expose
    private String rollNumber;
    @SerializedName("file_Available")
    @Expose
    private Integer fileAvailable;
    @SerializedName("admissionNo")
    private String admissionNo;

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public String getHWStatus() {
        return hWStatus;
    }

    public void setHWStatus(String hWStatus) {
        this.hWStatus = hWStatus;
    }

    public String getTeacherComments() {
        return teacherComments;
    }

    public void setTeacherComments(String teacherComments) {
        this.teacherComments = teacherComments;
    }

    public String getHomeworkSubmitDate() {
        return homeworkSubmitDate;
    }

    public void setHomeworkSubmitDate(String homeworkSubmitDate) {
        this.homeworkSubmitDate = homeworkSubmitDate;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Integer getHWAssigned() {
        return hWAssigned;
    }

    public void setHWAssigned(Integer hWAssigned) {
        this.hWAssigned = hWAssigned;
    }

    public Integer getStudentHWId() {
        return studentHWId;
    }

    public void setStudentHWId(Integer studentHWId) {
        this.studentHWId = studentHWId;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getHwRating() {
        return hwRating;
    }

    public void setHwRating(Integer hwRating) {
        this.hwRating = hwRating;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Integer getFileAvailable() {
        return fileAvailable;
    }

    public void setFileAvailable(Integer fileAvailable) {
        this.fileAvailable = fileAvailable;
    }
}
