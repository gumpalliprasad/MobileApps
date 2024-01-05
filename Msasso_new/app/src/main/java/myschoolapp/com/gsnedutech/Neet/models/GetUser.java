package myschoolapp.com.gsnedutech.Neet.models;

import java.util.ArrayList;

public class GetUser {

    String uid, userName, userEmail;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ArrayList<Double> getUserGeoLocation() {
        return userGeoLocation;
    }

    public void setUserGeoLocation(ArrayList<Double> userGeoLocation) {
        this.userGeoLocation = userGeoLocation;
    }

    ArrayList<Double> userGeoLocation;
}
