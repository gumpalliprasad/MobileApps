/*
 * *
 *  * Created by SriRamaMurthy A on 3/10/19 7:01 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 3/10/19 12:19 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherAttendStudentObj implements Serializable {
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("rollNumber")
    @Expose
    private String rollNumber;
    @SerializedName("previousState")
    @Expose
    private String previousState = "blank";
    @SerializedName("attendanceType")
    @Expose
    private String attendanceType = "blank";
    @SerializedName("attendanceId")
    @Expose
    private String attendanceId = "0";
    @SerializedName("reason")
    @Expose
    private String reason = "NA";
    @SerializedName("isChecked")
    @Expose
    boolean isChecked;
    @SerializedName("admissionNumber")
    @Expose
    String admissionNumber;

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getPreviousState() {
        return previousState;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

}
