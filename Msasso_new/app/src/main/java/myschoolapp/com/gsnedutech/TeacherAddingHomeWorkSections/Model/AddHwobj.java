package myschoolapp.com.gsnedutech.TeacherAddingHomeWorkSections.Model;


//  jsonObject.put("schemaName", sh_Pref.getString("schema", ""));
//          jsonObject.put("sectionId",sectionId);
//          jsonObject.put("subjectId",subjectId);
//          jsonObject.put("branchId",tObj.getBranchId());
//          jsonObject.put("homeworkDetail",description);
//          jsonObject.put("homeworkTitle",title);
//          jsonObject.put("homeworkDate",hwdate);
//          jsonObject.put("createdBy",tObj.getUserId());
//          jsonObject.put("submissionDate",submissionDate);
//          jsonObject.put("homeWorktypeId",homeWorktypeId);
//          jsonObject.put("sectionArray",sectionArray);
//          jsonObject.put("studentArray",studentArray);


import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class AddHwobj {

    List<Uri> mImageUri = new ArrayList<>();
    List<String> fileName = new ArrayList<>();
    String sectionId="";
    String subjectId="";
    String branchId="";
    String homeworkDetail="";
    String homeworkTitle="";
    String homeworkDate="";
    String createdBy="";
    String submissionDate="";
    String homeWorktypeId="";
    List<HwAssign> listAssigned = new ArrayList<>();
    List<HwAssign> listCanAssign = new ArrayList<>();

    public List<HwAssign> getListCanAssign() {
        return listCanAssign;
    }

    public void setListCanAssign(List<HwAssign> listCanAssign) {
        this.listCanAssign = listCanAssign;
    }


    public List<HwAssign> getListAssigned() {
        return listAssigned;
    }

    public void setListAssigned(List<HwAssign> listAssigned) {
        this.listAssigned = listAssigned;
    }

    public List<Uri> getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(List<Uri> mImageUri) {
        this.mImageUri = mImageUri;
    }

    public List<String> getFileName() {
        return fileName;
    }

    public void setFileName(List<String> fileName) {
        this.fileName = fileName;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getHomeworkDetail() {
        return homeworkDetail;
    }

    public void setHomeworkDetail(String homeworkDetail) {
        this.homeworkDetail = homeworkDetail;
    }

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public String getHomeworkDate() {
        return homeworkDate;
    }

    public void setHomeworkDate(String homeworkDate) {
        this.homeworkDate = homeworkDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getHomeWorktypeId() {
        return homeWorktypeId;
    }

    public void setHomeWorktypeId(String homeWorktypeId) {
        this.homeWorktypeId = homeWorktypeId;
    }




}
