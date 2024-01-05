package myschoolapp.com.gsnedutech.JeeMains.models;

public class UserProfile {
    public UserLatlongs getUserGeoLocation() {
        return userGeoLocation;
    }

    public void setUserGeoLocation(UserLatlongs userGeoLocation) {
        this.userGeoLocation = userGeoLocation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    UserLatlongs userGeoLocation;
    String version;
    String mType;
    String firebaseId;
}
