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

public class TeacherHwStudentObj {

    @SerializedName("isChecked")
    @Expose
    boolean isChecked;
    @SerializedName("studentId")
    @Expose
    private String studentId;
    @SerializedName("studentName")
    @Expose
    private String studentName;
    @SerializedName("rollnumber")
    @Expose
    private String rollnumber;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;
    private String sectionId;

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getRollnumber() {
        return rollnumber;
    }

    public void setRollnumber(String rollnumber) {
        this.rollnumber = rollnumber;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


}
