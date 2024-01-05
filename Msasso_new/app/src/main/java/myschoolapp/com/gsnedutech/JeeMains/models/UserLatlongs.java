package myschoolapp.com.gsnedutech.JeeMains.models;

import java.util.ArrayList;

public class UserLatlongs {

    String type = "Point";

    public ArrayList<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Double> coordinates) {
        this.coordinates = coordinates;
    }

    ArrayList<Double> coordinates;

}
