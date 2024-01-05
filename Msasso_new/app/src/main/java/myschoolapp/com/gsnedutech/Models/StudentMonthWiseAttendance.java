package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentMonthWiseAttendance {
    @SerializedName("studentId")
    @Expose
    private Integer studentId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("attendanceType")
    @Expose
    private String attendanceType;
    @SerializedName("dayName")
    @Expose
    private String dayName;
    @SerializedName("teacherComments")
    @Expose
    private Integer teacherComments;
    @SerializedName("sectionId")
    @Expose
    private Integer sectionId;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public Integer getTeacherComments() {
        return teacherComments;
    }

    public void setTeacherComments(Integer teacherComments) {
        this.teacherComments = teacherComments;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }
}
