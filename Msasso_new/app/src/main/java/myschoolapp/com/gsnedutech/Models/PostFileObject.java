package myschoolapp.com.gsnedutech.Models;

import java.io.File;

public class PostFileObject {
    String fileType;
    String filePath;
    String fileName;
    String link="";
    File f;

    public File getF() {
        return f;
    }

    public void setF(File f) {
        this.f = f;
    }


    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}

