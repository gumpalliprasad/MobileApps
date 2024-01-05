/*
 * *
 *  * Created by SriRamaMurthy A on 10/10/19 2:22 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 9/10/19 3:49 PM
 *
 */

package myschoolapp.com.gsnedutech.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HomeWorkFilesDetail implements Serializable {
    @SerializedName("fileName")
    @Expose
    private String fileName;
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("attachmentId")
    @Expose
    private String attachmentId;
    @SerializedName("fileType")
    @Expose
    private String fileType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
