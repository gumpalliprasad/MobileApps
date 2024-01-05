package myschoolapp.com.gsnedutech.Models;

import java.io.File;

public class AudioFileObj {

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    File file;
    int currentPosition = 0;

    public AudioFileObj(File file, int currentPosition){
        this.file = file;
        this.currentPosition = currentPosition;
    }
}
