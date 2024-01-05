package myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections.Model;

import java.util.ArrayList;
import java.util.List;

import myschoolapp.com.gsnedutech.Models.TeacherHwStudentObj;

public class HwAssign {

    String className="",classId="",SectionName="",sectionId="",subjectName="",subjectId="";
    List<TeacherHwStudentObj> studList = new ArrayList<>();

    public List<TeacherHwStudentObj> getStudList() {
        return studList;
    }

    public void setStudList(List<TeacherHwStudentObj> studList) {
        this.studList = studList;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSectionName() {
        return SectionName;
    }

    public void setSectionName(String sectionName) {
        SectionName = sectionName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }


}
