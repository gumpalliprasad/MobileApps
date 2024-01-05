package myschoolapp.com.gsnedutech.Neet.models;

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
