package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TeacherStudentsMonthWiseAttendance implements Serializable {

    @SerializedName("AbsentPercentage")
    @Expose
    private Double absentPercentage;
    @SerializedName("attendanceTakenDays")
    @Expose
    private Integer attendanceTakenDays;
    @SerializedName("MonthandYear")
    @Expose
    private String monthandYear;
    @SerializedName("workingDays")
    @Expose
    private Integer workingDays;
    @SerializedName("LatePercentage")
    @Expose
    private Double latePercentage;
    @SerializedName("presentPercentage")
    @Expose
    private Double presentPercentage;
    @SerializedName("MonthName")
    @Expose
    private String monthName;

    public Double getAbsentPercentage() {
        return absentPercentage;
    }

    public void setAbsentPercentage(Double absentPercentage) {
        this.absentPercentage = absentPercentage;
    }

    public Integer getAttendanceTakenDays() {
        return attendanceTakenDays;
    }

    public void setAttendanceTakenDays(Integer attendanceTakenDays) {
        this.attendanceTakenDays = attendanceTakenDays;
    }

    public String getMonthandYear() {
        return monthandYear;
    }

    public void setMonthandYear(String monthandYear) {
        this.monthandYear = monthandYear;
    }

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }

    public Double getLatePercentage() {
        return latePercentage;
    }

    public void setLatePercentage(Double latePercentage) {
        this.latePercentage = latePercentage;
    }

    public Double getPresentPercentage() {
        return presentPercentage;
    }

    public void setPresentPercentage(Double presentPercentage) {
        this.presentPercentage = presentPercentage;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
}
