package myschoolapp.com.gsnedutech.Arena.Models;

import java.io.Serializable;

public class ArFlashObject implements Serializable {

    public String getFlashTitle() {
        return flashTitle;
    }

    public void setFlashTitle(String flashTitle) {
        this.flashTitle = flashTitle;
    }

    String flashTitle;
    String colorString;

    public int getNumberOfFlashCards() {
        return numberOfFlashCards;
    }

    public void setNumberOfFlashCards(int numberOfFlashCards) {
        this.numberOfFlashCards = numberOfFlashCards;
    }

    int numberOfFlashCards;

    public String getColorString() {
        return colorString;
    }

    public void setColorString(String colorString) {
        this.colorString = colorString;
    }

}
